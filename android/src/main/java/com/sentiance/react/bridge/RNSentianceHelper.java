package com.sentiance.react.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.MetaUserLinker;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.detectionupdates.UserActivityType;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TransportMode;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static android.content.Context.MODE_PRIVATE;
import static com.sentiance.react.bridge.RNSentianceConverter.*;

public class RNSentianceHelper {
    private static final boolean DEBUG = true;
    private static final String TAG = "RNSentianceHelper";
    private final String E_SDK_INIT_ERROR = "E_SDK_INIT_ERROR";
    private final String E_SDK_GET_TOKEN_ERROR = "E_SDK_GET_TOKEN_ERROR";
    private final String E_SDK_START_TRIP_ERROR = "E_SDK_START_TRIP_ERROR";
    private final String E_SDK_STOP_TRIP_ERROR = "E_SDK_STOP_TRIP_ERROR";
    private final String E_SDK_NOT_INITIALIZED = "E_SDK_NOT_INITIALIZED";
    private final String E_SDK_DISABLE_BATTERY_OPTIMIZATION = "E_SDK_DISABLE_BATTERY_OPTIMIZATION";
    private final Context mContext;
    private final CountDownLatch userLinkLatch = new CountDownLatch(1);
    private Boolean userLinkResult = false;
    private final RNSentianceEmitter emitter;

    private OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = new OnSdkStatusUpdateHandler() {
        @Override
        public void onSdkStatusUpdate(SdkStatus status) {
            Log.d("EVENT", "status update");
            emitter.sendStatusUpdateEvent(status);
        }
    };

    private MetaUserLinker userLinker = new MetaUserLinker() {
        @Override
        public boolean link(String installId) {
            Log.d(TAG, "User Link");
            emitter.sendUserLinkEvent(installId);
            try {
                userLinkLatch.await();

                return userLinkResult;
            } catch (InterruptedException e) {
                return false;
            }
        }
    };

    private void log(String msg, Object... params) {
        if (DEBUG) {
            Log.e("SentianceSDK", String.format(msg, params));
        }
    }

    public RNSentianceHelper(Context context) {
        mContext = context;
        this.emitter = new RNSentianceEmitter(context);
    }

    public Boolean isSdkReady() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sentiance", MODE_PRIVATE);
        return sharedPreferences.getBoolean("sentiance_sdk_ready", false);
    }

    public void setSdkReady(Boolean isReady) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sentiance", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("sentiance_sdk_ready", isReady).apply();
    }

    public void initSDK(String appId, String appSecret, @Nullable String baseUrl, final boolean autoStart, boolean userLinkingEnabled, Notification notification, final @Nullable Promise promise) {

        // Create notification if not provided
        if (notification == null) {
            notification = createNotificationFromManifestData();
        }

        // Handle SDK Status Updates
        SdkConfig.Builder builder = new SdkConfig.Builder(appId, appSecret, notification)
                .setOnSdkStatusUpdateHandler(onSdkStatusUpdateHandler);

        // Handle User Linking
        if (userLinkingEnabled)
            builder.setMetaUserLinker(userLinker);

        // Change SDK API URL if needed
        if (baseUrl != null)
            builder.baseURL(baseUrl);


        // Build config
        SdkConfig config = builder.build();


        // initialize SDK
        Sentiance.getInstance(mContext).init(config, new OnInitCallback() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
                setSdkReady(true);
                if (autoStart) {
                    startSDK();
                }
                if (promise != null) {
                    promise.resolve(null);
                }
            }

            @Override
            public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
                Log.e(TAG, issue.toString());
                if (promise != null) {
                    promise.reject(E_SDK_INIT_ERROR, issue.toString());
                }
            }

        });
    }

    public void startSDK() {
        Sentiance.getInstance(mContext).start(new OnStartFinishedHandler() {
            @Override
            public void onStartFinished(SdkStatus sdkStatus) {
                Log.i(TAG, sdkStatus.toString());
                emitter.sendStatusUpdateEvent(sdkStatus);
            }
        });
    }

    public void stopSDK() {
        Sentiance.getInstance(mContext).stop();
    }

    public void userLinkCallback(Boolean linkResult) {
        userLinkResult = linkResult;
        userLinkLatch.countDown();
    }

    public void getInitState(final Promise promise) {
        InitState initState = Sentiance.getInstance(mContext).getInitState();
        promise.resolve(convertInitState(initState));
    }

    public void getAccessToken(final Promise promise) {
        Sentiance.getInstance(mContext).getUserAccessToken(new TokenResultCallback() {
            @Override
            public void onSuccess(Token token) {
                promise.resolve(convertToken(token));
            }

            @Override
            public void onFailure() {
                promise.reject(E_SDK_GET_TOKEN_ERROR, "Something went wrong while obtaining a user token.");
            }
        });
    }

    public void getUserId(final Promise promise) {
        String userId = Sentiance.getInstance(mContext).getUserId();
        promise.resolve(userId);
    }

    public void startTrip(ReadableMap metadata, int hint, final Promise promise) {
        final Map metadataMap = metadata.toHashMap();
        final TransportMode transportModeHint = toTransportMode(hint);
        Sentiance.getInstance(mContext).startTrip(metadataMap, transportModeHint, new StartTripCallback() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(SdkStatus sdkStatus) {
                promise.reject(E_SDK_START_TRIP_ERROR, sdkStatus.toString());
            }
        });
    }

    public void stopTrip(final Promise promise) {
        Sentiance.getInstance(mContext).stopTrip(new StopTripCallback() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(SdkStatus sdkStatus) {
                promise.reject(E_SDK_STOP_TRIP_ERROR, sdkStatus.toString());
            }
        });
    }

    public void getSdkStatus(final Promise promise) {
        SdkStatus sdkStatus = Sentiance.getInstance(mContext).getSdkStatus();
        promise.resolve(convertSdkStatus(sdkStatus));
    }


    public void getVersion(final Promise promise) {
        String version = Sentiance.getInstance(mContext).getVersion();
        promise.resolve(version);
    }


    public void addUserMetadataField(final String label, final String value, final Promise promise) {
        Sentiance.getInstance(mContext).addUserMetadataField(label, value);
        promise.resolve(null);
    }

    public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
        final Map<String, String> metadata = convertReadableMapToMap(inputMetadata);
        boolean result = Sentiance.getInstance(mContext).addTripMetadata(metadata);
        promise.resolve(result);
    }

    public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
        final Map<String, String> metadata = convertReadableMapToMap(inputMetadata);
        Sentiance.getInstance(mContext).addUserMetadataFields(metadata);
        promise.resolve(null);
    }

    public void removeUserMetadataField(final String label, final Promise promise) {
        Sentiance.getInstance(mContext).removeUserMetadataField(label);
        promise.resolve(null);
    }

    public void submitDetections(final Promise promise) {
        promise.resolve(null);
    }

    public void getWiFiQuotaLimit(final Promise promise) {
        Long wifiQuotaLimit = Sentiance.getInstance(mContext).getWiFiQuotaLimit();
        promise.resolve(wifiQuotaLimit.toString());
    }

    public void getWiFiQuotaUsage(final Promise promise) {
        Long wifiQuotaUsage = Sentiance.getInstance(mContext).getWiFiQuotaUsage();
        promise.resolve(wifiQuotaUsage.toString());
    }

    public void getMobileQuotaLimit(final Promise promise) {
        Long mobileQuotaLimit = Sentiance.getInstance(mContext).getMobileQuotaLimit();
        promise.resolve(mobileQuotaLimit.toString());
    }

    public void getMobileQuotaUsage(final Promise promise) {
        Long mobileQuotaUsage = Sentiance.getInstance(mContext).getMobileQuotaUsage();
        promise.resolve(mobileQuotaUsage.toString());
    }

    public void getDiskQuotaLimit(final Promise promise) {
        Long diskQuotaLimit = Sentiance.getInstance(mContext).getDiskQuotaLimit();
        promise.resolve(diskQuotaLimit.toString());
    }

    public void getDiskQuotaUsage(final Promise promise) {
        Long diskQuotaUsage = Sentiance.getInstance(mContext).getDiskQuotaUsage();
        promise.resolve(diskQuotaUsage.toString());
    }

    public void disableBatteryOptimization(final Promise promise) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Sentiance.getInstance(mContext).disableBatteryOptimization();
        }
        promise.resolve(null);
    }

    public void listenUserActivityUpdates(Promise promise) {
        if (Sentiance.getInstance(mContext).getInitState() == InitState.INITIALIZED) {
            Sentiance.getInstance(mContext).setUserActivityListener(new UserActivityListener() {
                @Override
                public void onUserActivityChange(UserActivity activity) {
                    emitter.sendUserActivityUpdate(activity);
                }
            });
            promise.resolve(null);
        } else {
            promise.reject(E_SDK_NOT_INITIALIZED, "SDK not initialized");
        }
    }

    public void getUserActivity(final Promise promise) {
        if (Sentiance.getInstance(mContext).getInitState() == InitState.INITIALIZED) {
            UserActivity activity = Sentiance.getInstance(mContext).getUserActivity();
            promise.resolve(convertUserActivity(activity));
        } else {
            promise.reject(E_SDK_NOT_INITIALIZED, "SDK not initialized");
        }
    }

//    public void updateSdkNotification(final String title , final String message, Promise promise) {
//        if (Sentiance.getInstance(mContext).getInitState() == InitState.INITIALIZED) {
//            Sentiance.getInstance(mContext).updateSdkNotification(createNotification(title,message));
//            promise.resolve(null);
//        } else {
//            promise.reject(E_SDK_NOT_INITIALIZED, "SDK not initialized");
//        }
//    }

    public Notification createNotification(PendingIntent pendingIntent, String title, String message, String channelName, String channelId, Integer icon) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }


    public Notification createNotificationFromManifestData() {

        String packageName = mContext.getPackageName();
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        Intent intent = new Intent(className);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        String appName = mContext.getApplicationInfo().loadLabel(mContext.getPackageManager()).toString();
        String channelName = "Sentiance";
        Integer icon = mContext.getApplicationInfo().icon;
        String channelId = "Sentiance";
        String title = appName + " is running";
        String message = "Touch to open";

        ApplicationInfo info;
        try {
            info = mContext.getPackageManager().getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_META_DATA);
            title = getStringMetadataFromManifest(info, "com.sentiance.sdk.notification_title", title);
            message = getStringMetadataFromManifest(info, "com.sentiance.sdk.notification_text", message);
            channelName = getStringMetadataFromManifest(info, "com.sentiance.sdk.notification_channel_name", channelName);
            icon = getIntMetadataFromManifest(info, "com.sentiance.sdk.notification_icon", icon);
            channelId = getStringMetadataFromManifest(info, "com.sentiance.sdk.channel_id", channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return createNotification(pendingIntent, title, message, channelName, channelId, icon);
    }


    private String getStringMetadataFromManifest(ApplicationInfo info, String name, String defaultValue) {
        Object obj = info.metaData.get(name);
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Integer) {
            return mContext.getString((Integer) obj);
        } else {
            return defaultValue;
        }
    }

    private int getIntMetadataFromManifest(ApplicationInfo info, String name, int defaultValue) {
        Object obj = info.metaData.get(name);
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else {
            return defaultValue;
        }
    }
}