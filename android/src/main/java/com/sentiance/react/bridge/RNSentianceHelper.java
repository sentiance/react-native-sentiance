package com.sentiance.react.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.sentiance.sdk.MetaUserLinker;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class RNSentianceHelper {

    private static final String MY_PREFS_NAME = "RNSentianceHelper";
    private static final String TAG = "RNSentianceHelper";
    private static RNSentianceHelper rnSentianceHelper;

    private final CountDownLatch userLinkLatch = new CountDownLatch(1);
    private final RNSentianceEmitter emitter;
    private final WeakReference<Context> weakContext;
    private Boolean userLinkResult = false;

    private OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = new OnSdkStatusUpdateHandler() {
        @Override
        public void onSdkStatusUpdate(SdkStatus status) {
            Log.d(TAG, "status update");
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
        userLinkLatch.countDown();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void initializeSentianceSDK(String appId, String appSecret, boolean autoStart,
                                       @Nullable OnInitCallback initCallback, @Nullable OnStartFinishedHandler startFinishedHandler) {
        initializeAndStartSentianceSDK(appId, appSecret, autoStart, null, false, initCallback, startFinishedHandler);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void initializeSentianceSDK(String appId, String appSecret, boolean autoStart,
                                       String baseUrl, @Nullable OnInitCallback initCallback, @Nullable OnStartFinishedHandler startFinishedHandler) {
        initializeAndStartSentianceSDK(appId, appSecret, autoStart, baseUrl, false, initCallback, startFinishedHandler);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, boolean autoStart,
                                                      @Nullable OnInitCallback initCallback, @Nullable OnStartFinishedHandler startFinishedHandler) {
        initializeAndStartSentianceSDK(appId, appSecret, autoStart, null, true, initCallback, startFinishedHandler);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, boolean autoStart,
                                                      String baseUrl, @Nullable OnInitCallback initCallback, @Nullable OnStartFinishedHandler startFinishedHandler) {
        initializeAndStartSentianceSDK(appId, appSecret, autoStart, baseUrl, true, initCallback, startFinishedHandler);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setValueForKey(String key, String value) {
        Context context = weakContext.get();
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public String getValueForKey(String key, String defaultValue) {
        Context context = weakContext.get();
        if (context == null) return defaultValue;
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    private void initializeAndStartSentianceSDK(String appId, String appSecret,
                                                final boolean autoStart, @Nullable String baseUrl, boolean userLinkingEnabled,
                                                final @Nullable OnInitCallback initCallback, final @Nullable OnStartFinishedHandler startFinishedHandler) {
        Context context = weakContext.get();
        if (context == null) return;

        Notification notification = createNotificationFromManifestData();

        // Create the config.
        SdkConfig.Builder builder = new SdkConfig.Builder(appId, appSecret, notification)
                .setOnSdkStatusUpdateHandler(onSdkStatusUpdateHandler);
        if (userLinkingEnabled)
            builder.setMetaUserLinker(userLinker);
        if (baseUrl != null)
            builder.baseURL(baseUrl);

        SdkConfig config = builder.build();

        // Initialize  and start  Sentiance SDK.
        Sentiance.getInstance(context).init(config, new OnInitCallback() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
                if (initCallback != null)
                    initCallback.onInitSuccess();
                if (autoStart)
                    startSentianceSDK(startFinishedHandler);
            }

            @Override
            public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
                if (initCallback != null)
                    initCallback.onInitFailure(issue, throwable);
                Log.e(TAG, issue.toString());
            }
        });
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void startSentianceSDK(@Nullable final OnStartFinishedHandler callback) {
        Context context = weakContext.get();
        if (context == null) return;
        Sentiance.getInstance(context).start(new OnStartFinishedHandler() {
            @Override
            public void onStartFinished(SdkStatus sdkStatus) {
                if (callback != null)
                    callback.onStartFinished(sdkStatus);
                emitter.sendStatusUpdateEvent(sdkStatus);
                Log.i(TAG, sdkStatus.toString());
            }
        });
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void startSentianceSDK(final long stopDateEpoch, @Nullable final OnStartFinishedHandler callback) {
        Context context = weakContext.get();
        if (context == null) return;
        Sentiance.getInstance(context).start(new Date(stopDateEpoch), new OnStartFinishedHandler() {
            @Override
            public void onStartFinished(SdkStatus sdkStatus) {
                if (callback != null)
                    callback.onStartFinished(sdkStatus);
                emitter.sendStatusUpdateEvent(sdkStatus);
                Log.i(TAG, sdkStatus.toString());
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
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        Intent intent = new Intent(className);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        String channelName = "Sentiance";
        int icon = context.getApplicationInfo().icon;
        String channelId = "Sentiance";


        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            channelName = getStringMetadataFromManifest(info, "com.sentiance.sdk.notification_channel_name", channelName);
            icon = getIntMetadataFromManifest(info, "com.sentiance.sdk.notification_icon", icon);
            channelId = getStringMetadataFromManifest(info, "com.sentiance.sdk.channel_id", channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return createNotification(pendingIntent, title, message, channelName, channelId, icon);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    Notification createNotificationFromManifestData() {
        Context context = weakContext.get();
        if (context == null) return null;
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        Intent intent = new Intent(className);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

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
