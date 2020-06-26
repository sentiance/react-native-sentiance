package com.sentiance.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.LifecycleEventListener;

import com.sentiance.sdk.InitState;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.ResetCallback;
import com.sentiance.sdk.TripProfileConfig;
import com.sentiance.sdk.crashdetection.CrashCallback;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.SubmitDetectionsCallback;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.TripProfileListener;
import com.sentiance.sdk.ondevice.TripProfile;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.trip.TransportMode;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.location.Location;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Map;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private static final boolean DEBUG = true;
  private static final String LOG_TAG = "RNSentiance";
  private final ReactApplicationContext reactContext;
  private final Sentiance sdk;
  private final String E_SDK_MISSING_PARAMS = "E_SDK_MISSING_PARAMS";
  private final String E_SDK_GET_TOKEN_ERROR = "E_SDK_GET_TOKEN_ERROR";
  private final String E_SDK_START_TRIP_ERROR = "E_SDK_START_TRIP_ERROR";
  private final String E_SDK_STOP_TRIP_ERROR = "E_SDK_STOP_TRIP_ERROR";
  private final String E_SDK_NOT_INITIALIZED = "E_SDK_NOT_INITIALIZED";
  private final String E_SDK_SUBMIT_DETECTIONS_ERROR = "E_SDK_SUBMIT_DETECTIONS_ERROR";
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private RNSentianceHelper rnSentianceHelper;
  private final RNSentianceEmitter emitter;
  private final StartFinishedHandlerCreator startFinishedHandlerCreator;


  public RNSentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

    sdk = Sentiance.getInstance(reactContext);
    rnSentianceHelper = RNSentianceHelper.getInstance(reactContext);
    emitter = new RNSentianceEmitter(reactContext);
    startFinishedHandlerCreator = new StartFinishedHandlerCreator(emitter);
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
  public void init(final String appId, final String appSecret, @Nullable final String baseURL, final boolean shouldStart, final Promise promise) {
    Log.v(LOG_TAG, "Initializing SDK with APP_ID: " + appId);

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if (!shouldStart)
          promise.resolve(true);
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        if (throwable != null) {
          promise.reject(issue.name(), throwable);
        } else {
          promise.reject(issue.name(), "");
        }
      }
    };

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.initializeSentianceSDK(
          appId, appSecret,
          shouldStart,
          baseURL,
          initCallback,
          startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
        );
      }
    });

  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void initWithUserLinkingEnabled(final String appId, final String appSecret, @Nullable final String baseURL, final boolean shouldStart, final Promise promise) {
    Log.v(LOG_TAG, "Initializing SDK with APP_ID: " + appId);

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if (!shouldStart)
          promise.resolve(true);
      }

      @Override
      public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
        if (throwable != null) {
          promise.reject(issue.name(), throwable);
        } else {
          promise.reject(issue.name(), "");
        }
      }
    };

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.initializeSentianceSDKWithUserLinking(
          appId, appSecret,
          shouldStart,
          baseURL,
          initCallback,
          startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
        );
      }
    });

  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void reset(final Promise promise) {
    sdk.reset(new ResetCallback() {
      @Override
      public void onResetSuccess() {
        promise.resolve(null);
      }

      @Override
      public void onResetFailure(ResetFailureReason reason) {
        promise.reject(reason.name(), "Resetting the SDK failed");
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void start(final Promise promise) {
    startWithStopDate(null, promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void startWithStopDate(@Nullable final Date stopDate, final Promise promise) {

    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    mHandler.post(new Runnable() {
      @Override
      public void run() {
        rnSentianceHelper.startSentianceSDK(stopDate,
          startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
        );
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void stop(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    sdk.stop();
    promise.resolve(true);
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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    SdkStatus sdkStatus = sdk.getSdkStatus();
    promise.resolve(RNSentianceConverter.convertSdkStatus(sdkStatus));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getVersion(final Promise promise) {
    String version = sdk.getVersion();
    promise.resolve(version);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isTripOngoing(String typeParam, final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    String userId = sdk.getUserId();
    promise.resolve(userId);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataField(final String label, final String value, final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    sdk.addUserMetadataField(label, value);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    boolean result = sdk.addTripMetadata(metadata);
    promise.resolve(result);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    sdk.addUserMetadataFields(metadata);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void removeUserMetadataField(final String label, final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    sdk.removeUserMetadataField(label);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void submitDetections(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    sdk.submitDetections(new SubmitDetectionsCallback() {
      @Override
      public void onSuccess() {
        promise.resolve(null);
      }

      @Override
      public void onFailure() {
        promise.reject(E_SDK_SUBMIT_DETECTIONS_ERROR, "Submission failed");
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaLimit(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Long wifiQuotaLimit = sdk.getWiFiQuotaLimit();
    promise.resolve(wifiQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaUsage(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Long wifiQuotaUsage = sdk.getWiFiQuotaUsage();
    promise.resolve(wifiQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaLimit(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Long mobileQuotaLimit = sdk.getMobileQuotaLimit();
    promise.resolve(mobileQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaUsage(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Long mobileQuotaUsage = sdk.getMobileQuotaUsage();
    promise.resolve(mobileQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaLimit(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Long diskQuotaLimit = sdk.getDiskQuotaLimit();
    promise.resolve(diskQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaUsage(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

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
  public void listenCrashEvents(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    sdk.setCrashCallback(new CrashCallback() {
      @Override
      public void onCrash(long time, @Nullable Location lastKnownLocation) {
        emitter.sendCrashEvent(time, lastKnownLocation);
      }
    });
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenTripProfiles(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Sentiance.getInstance(reactContext).setTripProfileListener(new TripProfileListener() {
      @Override
      public void onTripProfiled(TripProfile tripProfile) {
        Log.d(LOG_TAG, tripProfile.toString());
        emitter.sendTripProfile(tripProfile);
      }
    });
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void updateTripProfileConfig(ReadableMap config, final Promise promise) {
    if (!config.hasKey("enableFullProfiling")) {
      promise.reject(E_SDK_MISSING_PARAMS, "enableFullProfiling is not provided");
      return;
    }
    boolean enableFullProfiling = config.getBoolean("enableFullProfiling");
    Double speedLimit;
    if (config.hasKey("speedLimit")) {
      speedLimit = config.getDouble("speedLimit");
    } else {
      speedLimit = null;
    }
    Sentiance.getInstance(reactContext)
      .updateTripProfileConfig(
        new TripProfileConfig.Builder()
          .setSpeedLimit(speedLimit)
          .enableFullProfiling(enableFullProfiling)
          .build()
      );
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserActivity(final Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    UserActivity activity = Sentiance.getInstance(reactContext).getUserActivity();
    promise.resolve(RNSentianceConverter.convertUserActivity(activity));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void updateSdkNotification(final String title, final String message, Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return;
    }

    Sentiance.getInstance(reactContext).updateSdkNotification(rnSentianceHelper.createNotificationFromManifestData(title, message));
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void setValueForKey(String key, String value, Promise promise) {
    rnSentianceHelper.setValueForKey(key, value);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getValueForKey(String key, String defaultValue, Promise promise) {
    String value = rnSentianceHelper.getValueForKey(key, defaultValue);
    promise.resolve(value);
  }

  private boolean isSdkInitialized() {
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

