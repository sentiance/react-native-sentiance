package com.sentiance.react.bridge;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.ResetCallback;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.SubmitDetectionsCallback;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.crashdetection.api.CrashDetectionApi;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.crashdetection.api.VehicleCrashListener;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextHandler;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;
import com.sentiance.sdk.usercreation.UserCreationFailureReason;
import com.sentiance.sdk.usercreation.UserCreationResultHandler;
import com.sentiance.sdk.usercreation.UserInfo;

import java.util.List;
import java.util.Map;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

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
  private final RNSentianceHelper rnSentianceHelper;
  private final RNSentianceEmitter emitter;
  private final StartFinishedHandlerCreator startFinishedHandlerCreator;

  private @Nullable UserContextUpdateListener mUserContextUpdateListener;


  public RNSentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

    sdk = Sentiance.getInstance(reactContext);
    rnSentianceHelper = RNSentianceHelper.getInstance(reactContext);
    emitter = new RNSentianceEmitter(reactContext);
    startFinishedHandlerCreator = new StartFinishedHandlerCreator(emitter);
  }

  @NonNull
  @Override
  public String getName() {
    return "RNSentiance";
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
    public void initialize(final String platformUrl, final Promise promise) {
        InitializationResult result = rnSentianceHelper.initializeSDK(platformUrl);
        if (result.isSuccessful()) {
            promise.resolve(RNSentianceConverter.convertInitializationResult(result));
        } else {
            InitializationFailureReason failureReason = result.getFailureReason();
            String error = failureReason == null ? "failure reason unknown" : failureReason.name();
            promise.reject("Initialization failed", error);
        }
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void createUnlinkedUser(String appId, String secret, final Promise promise) {
        sdk.createUnlinkedUser(appId, secret, new UserCreationResultHandler() {
            @Override
            public void onUserCreationSuccess(UserInfo userInfo) {
                promise.resolve(RNSentianceConverter.convertUserInfo(userInfo));
            }

            @Override
            public void onUserCreationFailure(UserCreationFailureReason reason, String details) {
                promise.reject(reason.toString(), details);
            }
        });
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void createLinkedUser(String appId, String secret, final Promise promise) {
        rnSentianceHelper.createLinkedUser(appId, secret, new UserCreationResultHandler() {
            @Override
            public void onUserCreationSuccess(UserInfo userInfo) {
                promise.resolve(RNSentianceConverter.convertUserInfo(userInfo));
            }

            @Override
            public void onUserCreationFailure(UserCreationFailureReason reason, String details) {
                promise.reject(reason.toString(), details);
            }
        });
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void linkUser(final Promise promise) {
        rnSentianceHelper.linkUser(promise);
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void userExists(final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        promise.resolve(sdk.userExists());
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void isUserLinked(final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        promise.resolve(sdk.isUserLinked());
    }

  @ReactMethod
  @SuppressWarnings("unused")
  public void reset(final Promise promise) {
    sdk.reset(new ResetCallback() {
      @Override
      public void onResetSuccess() {
        promise.resolve(true);
      }

      @Override
      public void onResetFailure(@NonNull ResetFailureReason reason) {
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
  public void startWithStopDate(@Nullable final Double stopEpochTimeMs, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    mHandler.post(new Runnable() {
      @Override
      public void run() {
        Long stopTime = stopEpochTimeMs == null ? null : stopEpochTimeMs.longValue();
        rnSentianceHelper.startSentianceSDK(stopTime,
          startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
        );
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void stop(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
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
  public void startTrip(@Nullable ReadableMap metadata, int hint, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Map metadataMap = null;
    if (metadata != null) {
      metadataMap = metadata.toHashMap();
    }
    final TransportMode transportModeHint = RNSentianceConverter.toTransportMode(hint);
    sdk.startTrip(metadataMap, transportModeHint, new StartTripCallback() {
      @Override
      public void onSuccess() {
        promise.resolve(true);
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
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.stopTrip(new StopTripCallback() {
      @Override
      public void onSuccess() {
        promise.resolve(true);
      }

      @Override
      public void onFailure(SdkStatus sdkStatus) {
        promise.reject(E_SDK_STOP_TRIP_ERROR, "");
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getSdkStatus(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
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
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    if (typeParam == null) {
      promise.reject(E_SDK_MISSING_PARAMS, "TripType is required");
      return;
    }
    final TripType type = RNSentianceConverter.toTripType(typeParam);
    Boolean isTripOngoing = sdk.isTripOngoing(type);
    promise.resolve(isTripOngoing);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserAccessToken(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.getUserAccessToken(new TokenResultCallback() {
      @Override
      public void onSuccess(@NonNull Token token) {
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
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    String userId = sdk.getUserId();
    promise.resolve(userId);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataField(final String label, final String value, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    if (label == null || value == null) {
      promise.reject(E_SDK_MISSING_PARAMS, "label and value are required");
      return;
    }

    sdk.addUserMetadataField(label, value);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    boolean result = sdk.addTripMetadata(metadata);
    promise.resolve(result);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addUserMetadataFields(ReadableMap inputMetadata, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    if (inputMetadata == null) {
      promise.reject(E_SDK_MISSING_PARAMS, "metadata object is required");
      return;
    }

    final Map<String, String> metadata = RNSentianceConverter.convertReadableMapToMap(inputMetadata);
    sdk.addUserMetadataFields(metadata);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void removeUserMetadataField(final String label, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    if (label == null) {
      promise.reject(E_SDK_MISSING_PARAMS, "label is required");
      return;
    }

    sdk.removeUserMetadataField(label);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void submitDetections(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.submitDetections(new SubmitDetectionsCallback() {
      @Override
      public void onSuccess() {
        promise.resolve(true);
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
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long wifiQuotaLimit = sdk.getWiFiQuotaLimit();
    promise.resolve(wifiQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long wifiQuotaUsage = sdk.getWiFiQuotaUsage();
    promise.resolve(wifiQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaLimit(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long mobileQuotaLimit = sdk.getMobileQuotaLimit();
    promise.resolve(mobileQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long mobileQuotaUsage = sdk.getMobileQuotaUsage();
    promise.resolve(mobileQuotaUsage.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaLimit(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long diskQuotaLimit = sdk.getDiskQuotaLimit();
    promise.resolve(diskQuotaLimit.toString());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Long diskQuotaUsage = sdk.getDiskQuotaUsage();
    promise.resolve(diskQuotaUsage.toString());
  }

  @SuppressLint("MissingPermission")
  @ReactMethod
  @SuppressWarnings("unused")
  public void disableBatteryOptimization(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.disableBatteryOptimization();
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenUserActivityUpdates(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).setUserActivityListener(new UserActivityListener() {
      @Override
      public void onUserActivityChange(@NonNull UserActivity activity) {
        Log.d(LOG_TAG, activity.toString());
        emitter.sendUserActivityUpdate(activity);
      }
    });
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenVehicleCrashEvents(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    CrashDetectionApi.getInstance(reactContext).setVehicleCrashListener(new VehicleCrashListener() {
      @Override
      public void onVehicleCrash(@NonNull VehicleCrashEvent crashEvent) {
        emitter.sendVehicleCrashEvent(crashEvent);
      }
    });
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserActivity(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserActivity activity = Sentiance.getInstance(reactContext).getUserActivity();
    promise.resolve(RNSentianceConverter.convertUserActivity(activity));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void updateSdkNotification(final String title, final String message, Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).updateSdkNotification(rnSentianceHelper.createNotificationFromManifestData(title, message));
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void invokeDummyVehicleCrash(Promise promise) {
    CrashDetectionApi.getInstance(reactContext).invokeDummyVehicleCrash();
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isVehicleCrashDetectionSupported(Promise promise) {
    Boolean isCrashDetectionSupported = CrashDetectionApi.getInstance(reactContext).isVehicleCrashDetectionSupported();
    promise.resolve(isCrashDetectionSupported);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserContext(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserContextApi.getInstance(reactContext).getUserContext(new UserContextHandler() {
      @Override
      public void onUserContextReady(@NonNull UserContext userContext) {
        promise.resolve(RNSentianceConverter.convertUserContext(userContext));
      }
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenUserContextUpdates(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserContextApi userContextApi = UserContextApi.getInstance(reactContext);

    if (mUserContextUpdateListener != null) {
      userContextApi.removeUserContextUpdateListener(mUserContextUpdateListener);
    }

    mUserContextUpdateListener = new UserContextUpdateListener() {
      @Override
      public void onUserContextUpdated(@NonNull List<UserContextUpdateCriteria> criteria, @NonNull UserContext userContext) {
        emitter.sendUserContext(criteria, userContext);
      }
    };

    userContextApi.addUserContextUpdateListener(mUserContextUpdateListener);

    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void setAppSessionDataCollectionEnabled(boolean enabled, Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).setAppSessionDataCollectionEnabled(enabled);

    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isAppSessionDataCollectionEnabled(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    promise.resolve(Sentiance.getInstance(reactContext).isAppSessionDataCollectionEnabled());
  }

  private boolean rejectIfNotInitialized(Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return true;
    }
    return false;
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

