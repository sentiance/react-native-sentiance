package com.sentiance.react.bridge.core.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.lang.ref.WeakReference;

public class SentianceUtils {

  public static final int SENTIANCE_FALLBACK_NOTIFICATION_ID = 2123874432;
  public static final String SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_NAME = "Sentiance";
  public static final String SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_ID = "Sentiance";

  public static final String SENTIANCE_NOTIFICATION_ID = "com.sentiance.react.bridge.notification_id";
  public static final String SENTIANCE_NOTIFICATION_TITLE = "com.sentiance.react.bridge.notification_title";
  public static final String SENTIANCE_NOTIFICATION_ICON = "com.sentiance.react.bridge.notification_icon";
  public static final String SENTIANCE_NOTIFICATION_CHANNEL_ID = "com.sentiance.react.bridge.channel_id";
  public static final String SENTIANCE_NOTIFICATION_CHANNEL_NAME = "com.sentiance.react.bridge.notification_channel_name";
  public static final String SENTIANCE_NOTIFICATION_NOTIFICATION_TEXT = "com.sentiance.react.bridge.notification_text";

  public static Notification createNotificationFromManifestData(WeakReference<Context> weakContext,
                                                                String title, String message) {
    Context context = weakContext.get();
    if (context == null) return null;

    String channelName = SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_NAME;
    String channelId = SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_ID;
    int icon = context.getApplicationInfo().icon;

    ApplicationInfo info;
    try {
      info = context.getPackageManager().getApplicationInfo(
        context.getPackageName(), PackageManager.GET_META_DATA);
      channelName = getStringMetadataFromManifest(weakContext, info,
        SENTIANCE_NOTIFICATION_CHANNEL_NAME, SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_NAME);
      icon = getIntMetadataFromManifest(info, SENTIANCE_NOTIFICATION_ICON, icon);
      channelId = getStringMetadataFromManifest(weakContext, info,
        SENTIANCE_NOTIFICATION_CHANNEL_ID, SENTIANCE_FALLBACK_NOTIFICATION_CHANNEL_ID);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return createNotification(weakContext, getLaunchActivityPendingIntent(context),
      title, message, channelName, channelId, icon);
  }

  @SuppressWarnings({"unused", "WeakerAccess"})
  public static Notification createNotificationFromManifestData(WeakReference<Context> weakContext) {
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
      title = getStringMetadataFromManifest(weakContext, info,
        SENTIANCE_NOTIFICATION_TITLE, title);
      message = getStringMetadataFromManifest(weakContext, info, SENTIANCE_NOTIFICATION_NOTIFICATION_TEXT, message);
      channelName = getStringMetadataFromManifest(weakContext, info, SENTIANCE_NOTIFICATION_CHANNEL_NAME, channelName);
      icon = getIntMetadataFromManifest(info, SENTIANCE_NOTIFICATION_ICON, icon);
      channelId = getStringMetadataFromManifest(weakContext, info, SENTIANCE_NOTIFICATION_CHANNEL_ID, channelId);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return createNotification(weakContext, getLaunchActivityPendingIntent(context), title, message, channelName,
      channelId, icon);
  }

  public static int getSentianceNotificationId(WeakReference<Context> weakContext) {
    Context context = weakContext.get();
    if (context == null) {
      return SENTIANCE_FALLBACK_NOTIFICATION_ID;
    }

    try {
      ApplicationInfo info = context.getPackageManager().getApplicationInfo(
        context.getPackageName(), PackageManager.GET_META_DATA);
      return getIntMetadataFromManifest(info, SENTIANCE_NOTIFICATION_ID, SENTIANCE_FALLBACK_NOTIFICATION_ID);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return SENTIANCE_FALLBACK_NOTIFICATION_ID;
  }

  private static Notification createNotification(WeakReference<Context> weakContext, PendingIntent pendingIntent,
                                                 String title, String message, String channelName, String channelId,
                                                 Integer icon) {
    Context context = weakContext.get();
    if (context == null) return null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channelId,
        channelName, NotificationManager.IMPORTANCE_LOW);
      channel.setShowBadge(false);
      NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

  private static PendingIntent getLaunchActivityPendingIntent(Context context) {
    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    if (launchIntent == null) {
      launchIntent = new Intent();
    }
    launchIntent.setPackage(null);
    int pendingFlags = (Build.VERSION.SDK_INT >= 31) ? PendingIntent.FLAG_IMMUTABLE : 0;
    return PendingIntent.getActivity(context, 0, launchIntent, pendingFlags);
  }

  private static String getStringMetadataFromManifest(WeakReference<Context> weakContext,
                                                      ApplicationInfo info, String name, String defaultValue) {
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

  private static int getIntMetadataFromManifest(ApplicationInfo info, String name, int defaultValue) {
    Object obj = info.metaData.get(name);
    if (obj instanceof Integer) {
      return (Integer) obj;
    } else {
      return defaultValue;
    }
  }
}
