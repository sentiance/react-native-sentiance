package com.sentiance.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.LifecycleEventListener;

import com.sentiance.sdk.InitState;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.OnStartFinishedHandler;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.trip.TransportMode;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import java.util.Map;

import android.support.annotation.Nullable;
import android.util.Log;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private static final boolean DEBUG = true;
  private static final String LOG_TAG = "RNSentiance";
  private final ReactApplicationContext reactContext;
  private static RNSentianceConfig sentianceConfig = null;
  private final Sentiance sdk;
  private final String E_SDK_GET_TOKEN_ERROR = "E_SDK_GET_TOKEN_ERROR";
  private final String E_SDK_START_TRIP_ERROR = "E_SDK_START_TRIP_ERROR";
  private final String E_SDK_STOP_TRIP_ERROR = "E_SDK_STOP_TRIP_ERROR";
  private final String E_SDK_NOT_INITIALIZED = "E_SDK_NOT_INITIALIZED";
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private RNSentianceHelper rnSentianceHelper;
  private final RNSentianceEmitter emitter;


  public RNSentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

    sdk = Sentiance.getInstance(reactContext);
    rnSentianceHelper = RNSentianceHelper.getInstance(reactContext);
    emitter = new RNSentianceEmitter(reactContext);
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

  @ReactMethod
  @SuppressWarnings("unused")
  public void userLinkCallback(final Boolean linkResult) {
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.userLinkCallback(linkResult);
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void init(final String appId, final String appSecret,final String baseURL,final boolean autoStart, final Promise promise) {
    Log.v(LOG_TAG, "Initializing SDK with APP_ID: " + appId);

    final OnStartFinishedHandler startFinishedHandler = new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(SdkStatus sdkStatus) {
        promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
        emitter.sendStatusUpdateEvent(sdkStatus);
      }
    };

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if(!autoStart)
          promise.resolve(null);
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        promise.reject(issue.name(),throwable);
      }
    };

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.initializeSentianceSDK(
                appId, appSecret,
                rnSentianceHelper.createNotificationFromManifestData(),
                autoStart,
                baseURL,
                initCallback,
                startFinishedHandler
        );
      }
    });

  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void initWithUserLinkingEnabled(final String appId, final String appSecret, final String baseURL,final boolean autoStart, final Promise promise) {
    Log.v(LOG_TAG, "Initializing SDK with APP_ID: " + appId);

    final OnStartFinishedHandler startFinishedHandler = new OnStartFinishedHandler() {
      @Override
      public void onStartFinished(SdkStatus sdkStatus) {
        promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
        emitter.sendStatusUpdateEvent(sdkStatus);
      }
    };

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if(!autoStart)
          promise.resolve(null);
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        promise.reject(issue.name(),throwable);
      }
    };

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.initializeSentianceSDKWithUserLinking(
                appId, appSecret,
                rnSentianceHelper.createNotificationFromManifestData(),
                autoStart,
                baseURL,
                initCallback,
                startFinishedHandler
        );
      }
    });

  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void start(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    mHandler.post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.startSentianceSDK(new OnStartFinishedHandler() {
          @Override
          public void onStartFinished(SdkStatus sdkStatus) {
            promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
            emitter.sendStatusUpdateEvent(sdkStatus);
          }
        });
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void startWithStopDate(final long stopDateEpoch, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    mHandler.post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.startSentianceSDK(stopDateEpoch,new OnStartFinishedHandler() {
          @Override
          public void onStartFinished(SdkStatus sdkStatus) {
            promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
            emitter.sendStatusUpdateEvent(sdkStatus);
          }
        });
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void stop(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    sdk.stop();
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getInitState(final Promise promise) {
    InitState initState = sdk.getInitState();
    promise.resolve(RNSentianceConverter.convertInitState(initState));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void startTrip(ReadableMap metadata, int hint, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    final Map metadataMap = metadata.toHashMap();
    final TransportMode transportModeHint = RNSentianceConverter.toTransportMode(hint);
    sdk.startTrip(metadataMap, transportModeHint, new StartTripCallback() {
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

  @ReactMethod
  @SuppressWarnings("unused")
  public void stopTrip(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    sdk.stopTrip(new StopTripCallback() {
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
  @SuppressWarnings("unused")
  public void getSdkStatus(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    SdkStatus sdkStatus = sdk.getSdkStatus();
    promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getVersion(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    String version = sdk.getVersion();
    promise.resolve(version);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isTripOngoing(String typeParam, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    if (typeParam == null) {
      typeParam = "sdk";
    }
    final TripType type = RNSentianceConverter.toTripType(typeParam);
    Boolean isTripOngoing = sdk.isTripOngoing(type);
    promise.resolve(isTripOngoing);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserAccessToken(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    sdk.getUserAccessToken(new TokenResultCallback() {
      @Override
      public void onSuccess(Token token) {
        promise.resolve(RNSentianceConverter.convertToken(token));
      }

      @Override
      public void onFailure() {
        promise.reject(E_SDK_GET_TOKEN_ERROR, "Something went wrong while obtaining a user token.");
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserId(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    String userId = sdk.getUserId();
    promise.resolve(userId);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataField(final String label, final String value, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    sdk.addUserMetadataField(label, value);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    boolean result = sdk.addTripMetadata(metadata);
    promise.resolve(result);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    sdk.addUserMetadataFields(metadata);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void removeUserMetadataField(final String label, final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    sdk.removeUserMetadataField(label);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void submitDetections(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaLimit(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long wifiQuotaLimit = sdk.getWiFiQuotaLimit();
    promise.resolve(wifiQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaUsage(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long wifiQuotaUsage = sdk.getWiFiQuotaUsage();
    promise.resolve(wifiQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaLimit(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long mobileQuotaLimit = sdk.getMobileQuotaLimit();
    promise.resolve(mobileQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaUsage(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long mobileQuotaUsage = sdk.getMobileQuotaUsage();
    promise.resolve(mobileQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaLimit(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long diskQuotaLimit = sdk.getDiskQuotaLimit();
    promise.resolve(diskQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaUsage(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Long diskQuotaUsage = sdk.getDiskQuotaUsage();
    promise.resolve(diskQuotaUsage.toString());
  }

  @SuppressLint("MissingPermission")
  @ReactMethod
  @SuppressWarnings("unused")
  public void disableBatteryOptimization(final Promise promise) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      sdk.disableBatteryOptimization();
    }
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenUserActivityUpdates(Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

      Sentiance.getInstance(reactContext).setUserActivityListener(new UserActivityListener() {
        @Override
        public void onUserActivityChange(UserActivity activity) {
          Log.d(LOG_TAG, activity.toString());
          emitter.sendUserActivityUpdate(activity);
        }
      });
      promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserActivity(final Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    UserActivity activity = Sentiance.getInstance(reactContext).getUserActivity();
    promise.resolve(RNSentianceConverter.convertUserActivity(activity));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void updateSdkNotification(final String title , final String message, Promise promise) {
    if(!isSdkInitialized()) promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");

    Sentiance.getInstance(reactContext).updateSdkNotification(rnSentianceHelper.createNotificationFromManifestData(title,message));
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void setValueForKey(String key , String value, Promise promise){
    rnSentianceHelper.setValueForKey(key,value);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getValueForKey(String key , String defaultValue, Promise promise){
    String value = rnSentianceHelper.getValueForKey(key,defaultValue);
    promise.resolve(value);
  }


  private boolean isSdkInitialized(){
    return sdk.getInitState() == InitState.INITIALIZED;
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
