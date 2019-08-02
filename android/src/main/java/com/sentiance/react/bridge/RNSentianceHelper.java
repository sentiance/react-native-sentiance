package com.sentiance.react.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sentiance.sdk.MetaUserLinker;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkConfig;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;

import java.util.concurrent.CountDownLatch;

public class RNSentianceHelper {
  private static RNSentianceHelper rnSentianceHelper;

  public static RNSentianceHelper getInstance(Context context){
    if(rnSentianceHelper == null)
      rnSentianceHelper = new RNSentianceHelper(context,null);

    return rnSentianceHelper;
  }

  private final CountDownLatch userLinkLatch = new CountDownLatch(1);
  private Boolean userLinkResult = false;
  private final RNSentianceEmitter emitter;
  private static final String TAG = "RNSentianceHelper";
  private final SentinaceInitAndStartCallback mSentinaceInitAndStartCallback;
  private final Context mContext;



  public interface SentinaceInitAndStartCallback {
    void onInitSuccess();
    void onInitIssue(OnInitCallback.InitIssue initIssue, @Nullable Throwable throwable);
    void onStartFinish(SdkStatus startStatus);
  }

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

  void userLinkCallback(final Boolean linkResult) {
    userLinkResult = linkResult;
    userLinkLatch.countDown();
  }


  public RNSentianceHelper(Context context, SentinaceInitAndStartCallback sentinaceInitAndStartCallback) {
    mSentinaceInitAndStartCallback = sentinaceInitAndStartCallback;
    this.emitter = new RNSentianceEmitter(context);
    mContext = context;
  }

  public void initializeSentianceSDK( String appId, String appSecret, Notification notification, boolean autoStart){
    initializeAndStartSentianceSDK(appId,appSecret,notification,autoStart,null,false);
  }

  public void initializeSentianceSDK(String appId, String appSecret, Notification notification, boolean autoStart, String baseUrl){
    initializeAndStartSentianceSDK(appId,appSecret,notification,autoStart,baseUrl,false);
  }

  public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, Notification notification, boolean autoStart){
    initializeAndStartSentianceSDK(appId,appSecret,notification,autoStart,null,true);
  }

  public void initializeSentianceSDKWithUserLinking(String appId, String appSecret, Notification notification, boolean autoStart, String baseUrl){
    initializeAndStartSentianceSDK(appId,appSecret,notification,autoStart,baseUrl,true);
  }


  private void initializeAndStartSentianceSDK(String appId, String appSecret, Notification notification , final boolean autoStart, @Nullable String baseUrl, boolean userLinkingEnabled){


    // Create the config.
    SdkConfig.Builder builder = new SdkConfig.Builder(appId, appSecret, notification)
      .setOnSdkStatusUpdateHandler(onSdkStatusUpdateHandler);
    if(userLinkingEnabled)
      builder.setMetaUserLinker(userLinker);
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
        emitter.sendStatusUpdateEvent(sdkStatus);
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

  Notification createNotificationFromManifestData(String title , String message) {

    String packageName = mContext.getPackageName();
    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
    String className = launchIntent.getComponent().getClassName();
    Intent intent = new Intent(className);
    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

    String channelName = "Sentiance";
    Integer icon = mContext.getApplicationInfo().icon;
    String channelId = "Sentiance";


    ApplicationInfo info;
    try {
      info = mContext.getPackageManager().getApplicationInfo(
        mContext.getPackageName(), PackageManager.GET_META_DATA);
      channelName = getStringMetadataFromManifest(info, "com.sentiance.sdk.notification_channel_name", channelName);
      icon = getIntMetadataFromManifest(info, "com.sentiance.sdk.notification_icon", icon);
      channelId = getStringMetadataFromManifest(info, "com.sentiance.sdk.channel_id", channelId);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return createNotification(pendingIntent,title,message,channelName,channelId,icon);
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
