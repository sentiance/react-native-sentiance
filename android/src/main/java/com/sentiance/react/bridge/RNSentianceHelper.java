package com.sentiance.react.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.UserLinker;
import com.sentiance.sdk.authentication.UserLinkingFailureReason;
import com.sentiance.sdk.authentication.UserLinkingResultHandler;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.init.SentianceOptions;
import com.sentiance.sdk.usercreation.UserCreationResultHandler;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RNSentianceHelper {

    private static final String SDK_NATIVE_INIT_FLAG = "SDK_NATIVE_INIT_FLAG";
    private static final String MY_PREFS_NAME = "RNSentianceHelper";
    private static final String TAG = "RNSentianceHelper";
    private static final int NOTIFICATION_ID = 1001;
    private static final int USER_LINKING_WINDOW_MILLIS = 5000;
    private static RNSentianceHelper rnSentianceHelper;

    private final RNSentianceEmitter emitter;
    private final WeakReference<Context> weakContext;

    private Boolean userLinkResult = false;
    private volatile CountDownLatch userLinkLatch;

    private final OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = new OnSdkStatusUpdateHandler() {
        @Override
        public void onSdkStatusUpdate(SdkStatus status) {
            Log.d(TAG, "status update");
            emitter.sendStatusUpdateEvent(status);
        }
    };

    private final UserLinker userLinker = new UserLinker() {
        @Override
        public boolean link(String installId) {
            Log.d(TAG, "User Link");
            userLinkLatch = new CountDownLatch(1);
            emitter.sendUserLinkEvent(installId);
            try {
                // We allow a 5 seconds timeframe for the user linking to happen otherwise the user linking
                // process may get stuck here in case the enclosing app did not call `userLinkCallback()`
                // to count this latch down.
                userLinkLatch.await(USER_LINKING_WINDOW_MILLIS, TimeUnit.MILLISECONDS);

                return userLinkResult;
            } catch (InterruptedException e) {
                return false;
            }
        }
    };

    public static RNSentianceHelper getInstance(Context context) {
        if (rnSentianceHelper == null) {
            synchronized (RNSentianceHelper.class) {
                rnSentianceHelper = new RNSentianceHelper(context);
            }
        }
        return rnSentianceHelper;
    }


    private RNSentianceHelper(Context context) {
        emitter = new RNSentianceEmitter(context);
        weakContext = new WeakReference<>(context);
    }

    void userLinkCallback(final Boolean linkResult) {
        userLinkResult = linkResult;

        CountDownLatch latch = userLinkLatch;
        if (latch != null) {
            latch.countDown();
        }
    }

    public InitializationResult initializeSDK(final String platformUrl) {
        Context context = weakContext.get();
        if (context == null) {
            return new InitializationResult(
                    false, InitializationFailureReason.EXCEPTION_OR_ERROR, new Throwable("Context is null"));
        }
        OnStartFinishedHandler handler = new OnStartFinishedHandler() {
            @Override
            public void onStartFinished(@NonNull SdkStatus sdkStatus) {
                emitter.sendOnStartFinishedEvent(sdkStatus);
            }
        };
        Notification notification = createNotificationFromManifestData();
        Sentiance sentiance = Sentiance.getInstance(context);

        SentianceOptions options = new SentianceOptions.Builder(context)
                .enableAllFeatures()
                .setNotification(notification, NOTIFICATION_ID)
                .setPlatformUrl(platformUrl)
                .build();
        InitializationResult result = sentiance.initialize(options);
        sentiance.setSdkStatusUpdateHandler(onSdkStatusUpdateHandler);
        if (sentiance.userExists()) {
            sentiance.start(handler);
        }
        return result;
    }

    public void createLinkedUser(String appId, String secret, UserCreationResultHandler handler) {
        Context context = weakContext.get();
        if (context == null) return;
        Sentiance sentiance = Sentiance.getInstance(context);

        sentiance.createLinkedUser(appId, secret, userLinker, handler);
    }

    public void linkUser(final Promise promise) {
        Context context = weakContext.get();
        if (context == null) return;
        Sentiance sentiance = Sentiance.getInstance(context);

        sentiance.linkUser(userLinker, new UserLinkingResultHandler() {
            @Override
            public void onUserLinkingSuccess() {
                promise.resolve(true);
            }

            @Override
            public void onUserLinkingFailure(UserLinkingFailureReason reason, String details) {
                promise.reject(reason.toString(), details);
            }
        });
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void startSentianceSDK(@Nullable final OnStartFinishedHandler callback) {
        startSentianceSDK(null, callback);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void startSentianceSDK(@Nullable final Long stopEpochTimeMs, @Nullable final OnStartFinishedHandler callback) {
        Context context = weakContext.get();
        if (context == null) return;

        if (stopEpochTimeMs != null) {
            Sentiance.getInstance(context).start(new Date(stopEpochTimeMs), callback);
        } else {
            Sentiance.getInstance(context).start(callback);
        }
    }

    private Notification createNotification(PendingIntent pendingIntent, String title, String message, String channelName, String channelId, Integer icon) {
        Context context = weakContext.get();
        if (context == null) return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }

    Notification createNotificationFromManifestData(String title, String message) {
        Context context = weakContext.get();
        if (context == null) return null;

        String channelName = "Sentiance";
        int icon = context.getApplicationInfo().icon;
        String channelId = "Sentiance";


        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            channelName = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.notification_channel_name", channelName);
            icon = getIntMetadataFromManifest(info, "com.sentiance.react.bridge.notification_icon", icon);
            channelId = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.channel_id", channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return createNotification(getLaunchActivityPendingIntent(context), title, message, channelName, channelId, icon);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    Notification createNotificationFromManifestData() {
        Context context = weakContext.get();
        if (context == null) return null;

        String appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        String channelName = "Sentiance";
        int icon = context.getApplicationInfo().icon;
        String channelId = "Sentiance";
        String title = appName + " is running";
        String message = "Touch to open";

        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            title = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.notification_title", title);
            message = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.notification_text", message);
            channelName = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.notification_channel_name", channelName);
            icon = getIntMetadataFromManifest(info, "com.sentiance.react.bridge.notification_icon", icon);
            channelId = getStringMetadataFromManifest(info, "com.sentiance.react.bridge.channel_id", channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return createNotification(getLaunchActivityPendingIntent(context), title, message, channelName, channelId, icon);
    }

    private PendingIntent getLaunchActivityPendingIntent(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            launchIntent = new Intent();
        }
        launchIntent.setPackage(null);
        return PendingIntent.getActivity(context, 0, launchIntent, 0);
    }

    private String getStringMetadataFromManifest(ApplicationInfo info, String name, String defaultValue) {
        Context context = weakContext.get();
        if (context == null) return null;
        Object obj = info.metaData.get(name);
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Integer) {
            return context.getString((Integer) obj);
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
