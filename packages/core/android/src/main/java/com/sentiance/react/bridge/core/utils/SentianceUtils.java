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

import java.lang.ref.WeakReference;

import androidx.core.app.NotificationCompat;

public class SentianceUtils {
		public static Notification createNotificationFromManifestData(WeakReference<Context> weakContext,
																																	String title, String message) {
				Context context = weakContext.get();
				if (context == null) return null;

				String channelName = "Sentiance";
				int icon = context.getApplicationInfo().icon;
				String channelId = "Sentiance";


				ApplicationInfo info;
				try {
						info = context.getPackageManager().getApplicationInfo(
										context.getPackageName(), PackageManager.GET_META_DATA);
						channelName = getStringMetadataFromManifest(weakContext, info,
										"com.sentiance.react.bridge.notification_channel_name",
										channelName);
						icon = getIntMetadataFromManifest(info, "com.sentiance.react.bridge.notification_icon", icon);
						channelId = getStringMetadataFromManifest(weakContext, info,
										"com.sentiance.react.bridge.channel_id", channelId);
				} catch (PackageManager.NameNotFoundException e) {
						e.printStackTrace();
				}

				return createNotification(weakContext, getLaunchActivityPendingIntent(context), title, message, channelName,
								channelId, icon);
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
						title = getStringMetadataFromManifest(weakContext, info, "com.sentiance.react.bridge.notification_title",
										title);
						message = getStringMetadataFromManifest(weakContext, info, "com.sentiance.react.bridge.notification_text",
										message);
						channelName = getStringMetadataFromManifest(weakContext, info, "com.sentiance.react.bridge" +
										".notification_channel_name", channelName);
						icon = getIntMetadataFromManifest(info, "com.sentiance.react.bridge.notification_icon", icon);
						channelId = getStringMetadataFromManifest(weakContext, info, "com.sentiance.react.bridge.channel_id",
										channelId);
				} catch (PackageManager.NameNotFoundException e) {
						e.printStackTrace();
				}

				return createNotification(weakContext, getLaunchActivityPendingIntent(context), title, message, channelName,
								channelId, icon);
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
