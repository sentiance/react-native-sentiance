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
import com.sentiance.sdk.DisableDetectionsError;
import com.sentiance.sdk.DisableDetectionsResult;
import com.sentiance.sdk.EnableDetectionsError;
import com.sentiance.sdk.EnableDetectionsResult;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.UserLinker;
import com.sentiance.sdk.authentication.UserLinkingError;
import com.sentiance.sdk.authentication.UserLinkingResult;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.init.SentianceOptions;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationResult;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.sentiance.react.bridge.ErrorCodes.E_SDK_DISABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_ENABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_USER_LINK_ERROR;

public class RNSentianceHelper {
    private static final String TAG = "RNSentianceHelper";
    private static final int NOTIFICATION_ID = 1001;
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
                // This will hang indefinitely if a user linking listener is not registered on the RN side
                userLinkLatch.await();

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

    InitializationResult initializeSDK(final String platformUrl) {
        Context context = weakContext.get();
        if (context == null) {
            return new InitializationResult(
                    false, InitializationFailureReason.EXCEPTION_OR_ERROR, new Throwable("Context is null"));
        }
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
            enableDetections();
        }
        return result;
    }

    private void enableDetections() {
        enableDetections(null);
    }

    void enableDetections(@Nullable final Promise promise) {
        enableDetections(null, promise);
    }

    public void enableDetections(@Nullable Long stopTime, @Nullable final Promise promise) {
        Context context = weakContext.get();
        if (context == null) {
            throw new IllegalStateException("Context is null.");
        }
        Sentiance sentiance = Sentiance.getInstance(context);
        PendingOperation<EnableDetectionsResult, EnableDetectionsError> enableDetectionsOp;

        if (stopTime == null) {
            enableDetectionsOp = sentiance.enableDetections();
        } else {
            enableDetectionsOp = sentiance.enableDetections(new Date(stopTime));
        }

        enableDetectionsOp.addOnCompleteListener(new OnCompleteListener<EnableDetectionsResult, EnableDetectionsError>() {
            @Override
            public void onComplete(@NonNull PendingOperation<EnableDetectionsResult, EnableDetectionsError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                    EnableDetectionsResult result = pendingOperation.getResult();
                    emitter.sendOnStartFinishedEvent(result.getSdkStatus());
                    if (promise != null) {
                        promise.resolve(RNSentianceConverter.convertEnableDetectionsResult(result));
                    }
                } else {
                    EnableDetectionsError error = pendingOperation.getError();
                    if (promise != null) {
                        promise.reject(E_SDK_ENABLE_DETECTIONS_ERROR, error.getReason().toString());
                    }
                }
            }
        });
    }

    void disableDetections(final Promise promise) {
        Context context = weakContext.get();
        if (context == null) {
            throw new IllegalStateException("Context is null.");
        }
        Sentiance.getInstance(context)
                .disableDetections()
                .addOnCompleteListener(new OnCompleteListener<DisableDetectionsResult, DisableDetectionsError>() {
                    @Override
                    public void onComplete(@NonNull PendingOperation<DisableDetectionsResult, DisableDetectionsError> pendingOperation) {
                        if (pendingOperation.isSuccessful()) {
                            DisableDetectionsResult result = pendingOperation.getResult();
                            promise.resolve(RNSentianceConverter.convertDisableDetectionsResult(result));
                        } else {
                            DisableDetectionsError error = pendingOperation.getError();
                            promise.reject(E_SDK_DISABLE_DETECTIONS_ERROR, error.getReason().toString());
                        }
                    }
                });
    }

    PendingOperation<UserCreationResult, UserCreationError> createLinkedUser(String appId, String secret) {
        Context context = weakContext.get();
        if (context == null) {
            throw new IllegalStateException("Context is null.");
        }
        Sentiance sentiance = Sentiance.getInstance(context);
        return sentiance.createLinkedUser(appId, secret, userLinker);
    }

    void linkUser(final Promise promise) {
        Context context = weakContext.get();
        if (context == null) return;
        Sentiance sentiance = Sentiance.getInstance(context);

        sentiance.linkUser(userLinker)
                .addOnCompleteListener(new OnCompleteListener<UserLinkingResult, UserLinkingError>() {
                    @Override
                    public void onComplete(@NonNull PendingOperation<UserLinkingResult, UserLinkingError> pendingOperation) {
                        if (pendingOperation.isSuccessful()) {
                            promise.resolve(true);
                        } else {
                            UserLinkingError error = pendingOperation.getError();
                            promise.reject(
                                    E_SDK_USER_LINK_ERROR,
                                    String.format("%s: %s", error.getReason().toString(), error.getDetails())
                            );
                        }
                    }
                });
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
