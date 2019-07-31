package com.sentiance.react.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.react.ReactNativeHost;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;

public class RNSentianceHelper {

  public interface SentinaceInitAndStartCallback {
    void onInitSuccess();
    void onInitIssue(OnInitCallback.InitIssue initIssue, @Nullable Throwable throwable);
    void onStartFinish(SdkStatus startStatus);
  }

  private static final String TAG = "RNSentianceHelper";
  private final SentinaceInitAndStartCallback mSentinaceInitAndStartCallback;
  private final RNSentiancePackage mRNSentiancePackage;
  private final Context mContext;

  public RNSentianceHelper(Context context, RNSentiancePackage rnSentiancePackage, SentinaceInitAndStartCallback sentinaceInitAndStartCallback) {
    mSentinaceInitAndStartCallback = sentinaceInitAndStartCallback;
    mRNSentiancePackage = rnSentiancePackage;
    mContext = context;
  }

  public void initializeSentianceSDK(ReactNativeHost reactNativeHost, String appId, String appSecret, Notification notification, boolean autoStart){
    initializeAndStartSentianceSDK(reactNativeHost,appId,appSecret,notification,autoStart,null,false);
  }

  public void initializeSentianceSDK(ReactNativeHost reactNativeHost, String appId, String appSecret, Notification notification, boolean autoStart, String baseUrl){
    initializeAndStartSentianceSDK(reactNativeHost,appId,appSecret,notification,autoStart,baseUrl,false);
  }

  public void initializeSentianceSDKWithUserLinking(ReactNativeHost reactNativeHost, String appId, String appSecret, Notification notification, boolean autoStart){
    initializeAndStartSentianceSDK(reactNativeHost,appId,appSecret,notification,autoStart,null,true);
  }

  public void initializeSentianceSDKWithUserLinking(ReactNativeHost reactNativeHost, String appId, String appSecret, Notification notification,String baseUrl, boolean autoStart){
    initializeAndStartSentianceSDK(reactNativeHost,appId,appSecret,notification,autoStart,baseUrl,true);
  }


  private void initializeAndStartSentianceSDK(ReactNativeHost reactNativeHost, String appId, String appSecret, Notification notification , final boolean autoStart, @Nullable String baseUrl, boolean userLinkingEnabled){
    //create react context in background so that SDK could be delivered to JS even if app is not running
    if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
      reactNativeHost.getReactInstanceManager().createReactContextInBackground();

    // Create the config.
    SdkConfig.Builder builder = new SdkConfig.Builder(appId, appSecret, notification)
      .setOnSdkStatusUpdateHandler(mRNSentiancePackage.getOnSdkStatusUpdateHandler());
    if(userLinkingEnabled)
      builder.setMetaUserLinker(mRNSentiancePackage.getMetaUserLinker());
    if(baseUrl!=null)
      builder.baseURL(baseUrl);

    SdkConfig config = builder.build();

    // Initialize  and start  Sentiance SDK.
    Sentiance.getInstance(mContext).init(config, new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        Log.i(TAG, "onInitSuccess");
        if(mSentinaceInitAndStartCallback!=null)
          mSentinaceInitAndStartCallback.onInitSuccess();
        if(autoStart)
          startSentianceSDK();
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        if(mSentinaceInitAndStartCallback!=null)
          mSentinaceInitAndStartCallback.onInitIssue(issue,throwable);
        Log.e(TAG, issue.toString());
      }
    });
  }


  public void startSentianceSDK() {
    Sentiance.getInstance(mContext).start(new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(SdkStatus sdkStatus) {
        if(mSentinaceInitAndStartCallback!=null)
          mSentinaceInitAndStartCallback.onStartFinish(sdkStatus);
        Log.i(TAG, sdkStatus.toString());
      }
    });
  }

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


  public Notification createNotificationFromManifestData(PendingIntent pendingIntent) {

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

    return createNotification(pendingIntent,title,message,channelName,channelId,icon);
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
