package com.sentiance.react.bridge;

import com.sentiance.react.bridge.RNSentianceConfig;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;

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
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.trip.TransportMode;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import android.util.Log;
import java.lang.Throwable;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private static final boolean DEBUG = true;
  private static final String LOG_TAG = "RNSentiance";
  private final ReactApplicationContext reactContext;
  private static RNSentianceConfig sentianceConfig = new RNSentianceConfig(null, null);
  private final Sentiance sdk;
  private final String STATUS_UPDATE = "SDKStatusUpdate";
  private final String META_USER_LINK = "SDKMetaUserLink";
  private static final String USER_ACTIVITY_UPDATE = "SDKUserActivityUpdate";
  private final String E_SDK_INIT_ERROR = "E_SDK_INIT_ERROR";
  private final String E_SDK_GET_TOKEN_ERROR = "E_SDK_GET_TOKEN_ERROR";
  private final String E_SDK_START_TRIP_ERROR = "E_SDK_START_TRIP_ERROR";
  private final String E_SDK_STOP_TRIP_ERROR = "E_SDK_STOP_TRIP_ERROR";
  private final String E_SDK_NOT_INITIALIZED = "E_SDK_NOT_INITIALIZED";
  private final String E_SDK_DISABLE_BATTERY_OPTIMIZATION = "E_SDK_DISABLE_BATTERY_OPTIMIZATION";
  private final CountDownLatch metaUserLinkLatch = new CountDownLatch(1);
  private Boolean metaUserLinkResult = false;
  private OnStartFinishedHandler mHandler;

  public RNSentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.sdk = Sentiance.getInstance(this.reactContext);
  }

  public static void setConfig(RNSentianceConfig config) {
    sentianceConfig = config;
  }

  private static OnStartFinishedHandler startFinishedHandler(final Promise promise) {
    OnStartFinishedHandler handler = new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(SdkStatus sdkStatus) {
        promise.resolve(convertSdkStatus(sdkStatus));
      }
    };

    return handler;
  }

  private void initializeSentianceSdk(final Promise promise) {
    // Create the config.
    OnSdkStatusUpdateHandler statusHandler = new OnSdkStatusUpdateHandler() {
      @Override
      public void onSdkStatusUpdate(SdkStatus status) {
        sendStatusUpdate(status);
      }
    };
    // Create metaUserLinker wich sends event to JS and waits for result via @ReactMethod metaUserLinkCallback
    MetaUserLinker metaUserLinker = new MetaUserLinker() {
      @Override
      public boolean link(String installId) {
        sendMetaUserLink(installId);
        try {
          metaUserLinkLatch.await();
          return metaUserLinkResult;
        } catch(InterruptedException e) {
          return false;
        }
      }
    };

    Notification sdkNotification = sentianceConfig.notification != null ? sentianceConfig.notification
            : createNotification();
    SdkConfig.Builder configBuilder = new SdkConfig.Builder(sentianceConfig.appId, sentianceConfig.appSecret, sdkNotification)
            .setOnSdkStatusUpdateHandler(statusHandler)
            .setMetaUserLinker(metaUserLinker);

    if (sentianceConfig.baseURL != null) {
      configBuilder.baseURL(sentianceConfig.baseURL);
    }
    SdkConfig config = configBuilder.build();

    OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        sentianceConfig.initCallback.onInitSuccess();
        if (sentianceConfig.autoStart) {
          sdk.start(new OnStartFinishedHandler() {
            @Override
            public void onStartFinished(SdkStatus sdkStatus) {
              Log.v(LOG_TAG, "SDK started successfully");
              if (promise != null) {
                promise.resolve(convertSdkStatus(sdkStatus));
              }
            }
          });
        } else {
          Log.v(LOG_TAG, "No autostart configured");
          promise.resolve(null);
        }
      }

      @Override
      public void onInitFailure(InitIssue issue, Throwable throwable) {
        sentianceConfig.initCallback.onInitFailure(issue, throwable);
        if (promise != null) {
          promise.reject(E_SDK_INIT_ERROR, issue.toString());
        }
      }
    };
    // Initialize the Sentiance SDK.
    Log.v(LOG_TAG, "Initializing through react-native-sentiance");
    this.sdk.init(config, initCallback);
  }

  private Notification createNotification() {
      Log.v(LOG_TAG, "Creating Notification through RNSentiance");
      // PendingIntent that will start your application's MainActivity
      String packageName = this.reactContext.getPackageName();
      Intent launchIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
      String className = launchIntent.getComponent().getClassName();
      Intent intent = new Intent(className);
      PendingIntent pendingIntent = PendingIntent.getActivity(this.reactContext, 0, intent, 0);

      // On Oreo and above, you must create a notification channel
      String channelId = "journeys";
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
          NotificationChannel channel = new NotificationChannel(channelId,
                  "Journeys", NotificationManager.IMPORTANCE_LOW);
          channel.setShowBadge(false);
          NotificationManager notificationManager = (NotificationManager) this.reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.createNotificationChannel(channel);
      }

      Resources res = null;
      try {
          res = this.reactContext.getPackageManager().getResourcesForApplication(packageName);
      } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
      }
      int appNameId = res.getIdentifier("app_name", "string", packageName);

      return new NotificationCompat.Builder(this.reactContext, channelId)
              .setContentTitle(res.getString(appNameId) + " is running")
              .setContentText("Touch to open.")
              .setContentIntent(pendingIntent)
              .setShowWhen(false)
              .setSmallIcon(res.getIdentifier("notification_icon", "mipmap", packageName))
              .setPriority(NotificationCompat.PRIORITY_MIN)
              .build();
  }

  @Override
  public String getName() {
    return "RNSentiance";
  }

  private void log(String msg, Object... params) {
    if (DEBUG) {
      Log.e("SentianceSDK", String.format(msg, params));
    }
  }

  private WritableMap convertUserActivity(UserActivity activity) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("type", convertUserActivityType(activity.getActivityType()));

      //Trip Info
      if (activity.getTripInfo() != null) {
        WritableMap tripInfoMap = Arguments.createMap();
        String tripType = convertTripType(activity.getTripInfo().getTripType());
        tripInfoMap.putString("type", tripType);
        map.putMap("tripInfo", tripInfoMap);
      }

      //Stationary Info
      if (activity.getStationaryInfo() != null) {
        WritableMap stationaryInfoMap = Arguments.createMap();
        if (activity.getStationaryInfo().getLocation() != null) {
          WritableMap locationMap = convertLocation(activity.getStationaryInfo().getLocation());
          stationaryInfoMap.putMap("location", locationMap);
        }
        map.putMap("stationaryInfo", stationaryInfoMap);
      }

    } catch (Exception ignored) {
    }

    return map;
  }


  private WritableMap convertLocation(Location location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putString("latitude", String.valueOf(location.getLatitude()));
    locationMap.putString("longitude", String.valueOf(location.getLongitude()));
    locationMap.putString("accuracy", String.valueOf(location.getAccuracy()));
    locationMap.putString("altitude", String.valueOf(location.getAltitude()));
    locationMap.putString("provider", location.getProvider());

    return locationMap;

  }

  private String convertTripType(TripType tripType) {
    switch (tripType) {
      case ANY:
        return "ANY";
      case EXTERNAL_TRIP:
        return "TRIP_TYPE_EXTERNAL";
      case SDK_TRIP:
        return "TRIP_TYPE_SDK";
      default:
        return "TRIP_TYPE_SDK";
    }
  }

  private String convertUserActivityType(UserActivityType activityType) {
    switch (activityType) {
      case TRIP:
        return "USER_ACTIVITY_TYPE_TRIP";
      case STATIONARY:
        return "USER_ACTIVITY_TYPE_STATIONARY";
      case UNKNOWN:
        return "USER_ACTIVITY_TYPE_UNKNOWN";
      default:
        return "USER_ACTIVITY_TYPE_UNKNOWN";
    }
  }


  private static WritableMap convertSdkStatus(SdkStatus status) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("startStatus", status.startStatus.name());
      map.putBoolean("canDetect", status.canDetect);
      map.putBoolean("isRemoteEnabled", status.isRemoteEnabled);
      map.putBoolean("isLocationPermGranted", status.isLocationPermGranted);
      map.putString("locationSetting", status.locationSetting.name());
      map.putBoolean("isAccelPresent", status.isAccelPresent);
      map.putBoolean("isGyroPresent", status.isGyroPresent);
      map.putBoolean("isGpsPresent", status.isGpsPresent);
      map.putBoolean("isGooglePlayServicesMissing", status.isGooglePlayServicesMissing);
      map.putString("wifiQuotaStatus", status.wifiQuotaStatus.toString());
      map.putString("mobileQuotaStatus", status.mobileQuotaStatus.toString());
      map.putString("diskQuotaStatus", status.diskQuotaStatus.toString());
    } catch (Exception ignored) {
    }

    return map;
  }

  private WritableMap convertToken(Token token) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("tokenId", token.getTokenId());
      map.putString("expiryDate", String.valueOf(token.getExpiryDate()));
    } catch (Exception ignored) {
    }

    return map;
  }

  private WritableMap convertInstallId(String installId) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("installId", installId);
    } catch (Exception ignored) {
    }
    return map;
  }

  private Map<String, String> convertReadableMapToMap(ReadableMap inputMap) {
    Map<String, String> map = new HashMap<String, String>();
    ReadableMapKeySetIterator iterator = inputMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      try {
        map.put(key, String.valueOf(inputMap.getString(key)));
      } catch (Exception ignored) {

      }
    }
    return map;
  }

  private TripType toTripType(final String type) {
    if (type.equals("sdk")) {
      return TripType.SDK_TRIP;
    } else if (type.equals("external")) {
      return TripType.EXTERNAL_TRIP;
    } else {
      return TripType.ANY;
    }
  }

  @ReactMethod
  public void metaUserLinkCallback(final Boolean linkResult) {
    metaUserLinkResult = linkResult;
    metaUserLinkLatch.countDown();
  }

  @ReactMethod
  public void init(final String appId, final String appSecret, final Promise promise) {
    Log.v(LOG_TAG, "Initializing SDK with APP_ID: " + appId + " and SECRET: " + appSecret);
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (Sentiance.getInstance(getReactApplicationContext()).isInitialized()) {
          promise.resolve(null);
        } else {
          RNSentianceModule.setConfig(new RNSentianceConfig(appId, appSecret));
          initializeSentianceSdk(promise);
        }
      }
    });
  }

  private void sendStatusUpdate(SdkStatus sdkStatus) {
    this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(STATUS_UPDATE,
            convertSdkStatus(sdkStatus));
  }

  private void sendMetaUserLink(String installId) {
    this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(META_USER_LINK,
            convertInstallId(installId));
  }

  private void sendUserActivityUpdates(UserActivity activity) {
    this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(USER_ACTIVITY_UPDATE,
            convertUserActivity(activity));
  }

  @ReactMethod
  public void start(final Promise promise) {
    mHandler = startFinishedHandler(promise);

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Sentiance.getInstance(getReactApplicationContext()).start(mHandler);
      }
    });
  }

  @ReactMethod
  public void stop(final Promise promise) {
    Sentiance.getInstance(this.reactContext).stop();
    promise.resolve("OK");
  }

  @ReactMethod
  public void startTrip(ReadableMap metadata, int hint, final Promise promise) {
    final Map metadataMap = metadata.toHashMap();
    final TransportMode transportModeHint = toTransportMode(hint);
    Sentiance.getInstance(this.reactContext).startTrip(metadataMap, transportModeHint, new StartTripCallback() {
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

  private TransportMode toTransportMode(int t) {
    switch (t) {
      case 2: return TransportMode.CAR;
      case 3: return TransportMode.BICYCLE;
      case 4: return TransportMode.ON_FOOT;
      case 5: return TransportMode.TRAIN;
      case 6: return TransportMode.TRAM;
      case 7: return TransportMode.BUS;
      case 8: return TransportMode.PLANE;
      case 9: return TransportMode.BOAT;
      case 10: return TransportMode.METRO;
      case 11: return TransportMode.RUNNING;
      default: return null;
    }
  }


  @ReactMethod
  public void stopTrip(final Promise promise) {
    Sentiance.getInstance(this.reactContext).stopTrip(new StopTripCallback() {
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

  @ReactMethod
  public void getSdkStatus(final Promise promise) {
    SdkStatus sdkStatus = Sentiance.getInstance(this.reactContext).getSdkStatus();
    promise.resolve(convertSdkStatus(sdkStatus));
  }

  @ReactMethod
  public void getVersion(final Promise promise) {
    String version = Sentiance.getInstance(this.reactContext).getVersion();
    promise.resolve(version);
  }

  @ReactMethod
  public void isInitialized(final Promise promise) {
    Boolean isInitialized = Sentiance.getInstance(this.reactContext).isInitialized();
    promise.resolve(isInitialized);
  }

  @ReactMethod
  public void isTripOngoing(String typeParam, final Promise promise) {
    if (typeParam == null) {
      typeParam = "sdk";
    }
    final TripType type = toTripType(typeParam);
    Boolean isTripOngoing = Sentiance.getInstance(this.reactContext).isTripOngoing(type);
    promise.resolve(isTripOngoing);
  }

  @ReactMethod
  public void getUserAccessToken(final Promise promise) {
    Sentiance.getInstance(this.reactContext).getUserAccessToken(new TokenResultCallback() {
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

  @ReactMethod
  public void getUserId(final Promise promise) {
    String userId = Sentiance.getInstance(this.reactContext).getUserId();
    promise.resolve(userId);
  }

  @ReactMethod
  public void addUserMetadataField(final String label, final String value, final Promise promise) {
    Log.v(LOG_TAG, label);
    Sentiance.getInstance(this.reactContext).addUserMetadataField(label, value);
    promise.resolve(null);
  }

  @ReactMethod
  public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
    final Map<String, String> metadata = convertReadableMapToMap(inputMetadata);
    Sentiance.getInstance(this.reactContext).addUserMetadataFields(metadata);
    promise.resolve(null);
  }

  @ReactMethod
  public void removeUserMetadataField(final String label, final Promise promise) {
    Sentiance.getInstance(this.reactContext).removeUserMetadataField(label);
    promise.resolve(null);
  }

  @ReactMethod
  public void submitDetections(final Promise promise) {
    promise.resolve(null);
  }

  @ReactMethod
  public void getWiFiQuotaLimit(final Promise promise) {
    Long wifiQuotaLimit = Sentiance.getInstance(this.reactContext).getWiFiQuotaLimit();
    promise.resolve(wifiQuotaLimit.toString());
  }

  @ReactMethod
  public void getWiFiQuotaUsage(final Promise promise) {
    Long wifiQuotaUsage = Sentiance.getInstance(this.reactContext).getWiFiQuotaUsage();
    promise.resolve(wifiQuotaUsage.toString());
  }

  @ReactMethod
  public void getMobileQuotaLimit(final Promise promise) {
    Long mobileQuotaLimit = Sentiance.getInstance(this.reactContext).getMobileQuotaLimit();
    promise.resolve(mobileQuotaLimit.toString());
  }

  @ReactMethod
  public void getMobileQuotaUsage(final Promise promise) {
    Long mobileQuotaUsage = Sentiance.getInstance(this.reactContext).getMobileQuotaUsage();
    promise.resolve(mobileQuotaUsage.toString());
  }

  @ReactMethod
  public void getDiskQuotaLimit(final Promise promise) {
    Long diskQuotaLimit = Sentiance.getInstance(this.reactContext).getDiskQuotaLimit();
    promise.resolve(diskQuotaLimit.toString());
  }

  @ReactMethod
  public void getDiskQuotaUsage(final Promise promise) {
    Long diskQuotaUsage = Sentiance.getInstance(this.reactContext).getDiskQuotaUsage();
    promise.resolve(diskQuotaUsage.toString());
  }

  @ReactMethod
  public void disableBatteryOptimization(final Promise promise) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      Sentiance.getInstance(this.reactContext).disableBatteryOptimization();
    }
    promise.resolve(null);
  }

  @ReactMethod
  public void listenUserActivityUpdates() {
    Sentiance.getInstance(reactContext).setUserActivityListener(new UserActivityListener() {
      @Override
      public void onUserActivityChange(UserActivity activity) {
        sendUserActivityUpdates(activity);
      }
    });
  }

  @ReactMethod
  public void getUserActivity(final Promise promise) {
    if(Sentiance.getInstance(reactContext).getInitState() == InitState.INITIALIZED) {
      UserActivity activity = Sentiance.getInstance(reactContext).getUserActivity();
      promise.resolve(convertUserActivity(activity));
    }else {
      promise.reject(E_SDK_NOT_INITIALIZED, "SDK not initialized");
    }
  }

  @Override
  public void onHostResume() {
    // Activity `onResume`
  }

  @Override
  public void onHostPause() {
    // Activity `onPause`
  }

  @Override
  public void onHostDestroy() {
    // Activity `onDestroy`
  }

}
