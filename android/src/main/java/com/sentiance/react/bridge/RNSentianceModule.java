package com.sentiance.react.bridge;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.SubmitDetectionsError;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.UserAccessTokenError;
import com.sentiance.sdk.crashdetection.api.CrashDetectionApi;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.crashdetection.api.VehicleCrashListener;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityListener;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.r0;
import com.sentiance.sdk.reset.ResetError;
import com.sentiance.sdk.reset.ResetResult;
import com.sentiance.sdk.trip.StartTripError;
import com.sentiance.sdk.trip.StartTripResult;
import com.sentiance.sdk.trip.StopTripError;
import com.sentiance.sdk.trip.StopTripResult;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.usercontext.api.GetUserContextError;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationResult;
import com.sentiance.sdk.usercreation.UserInfo;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.sentiance.react.bridge.ErrorCodes.E_SDK_CREATE_LINKED_USER_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_CREATE_UNLINKED_USER_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_GET_TOKEN_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_MISSING_PARAMS;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_NOT_INITIALIZED;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_RESET_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_START_TRIP_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_STOP_TRIP_ERROR;
import static com.sentiance.react.bridge.ErrorCodes.E_SDK_SUBMIT_DETECTIONS_ERROR;

public class RNSentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private static final String LOG_TAG = "RNSentiance";
  private final ReactApplicationContext reactContext;
  private final Sentiance sdk;
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
    public void enableDetections(final Promise promise) {
      if (rejectIfNotInitialized(promise)) {
        return;
      }
      rnSentianceHelper.enableDetections(promise);
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void enableDetectionsWithExpiryDate(@Nullable final Double expiryEpochTimeMs, final Promise promise) {
      if (rejectIfNotInitialized(promise)) {
        return;
      }
      Long expiryTime = expiryEpochTimeMs == null ? null : expiryEpochTimeMs.longValue();
      rnSentianceHelper.enableDetections(expiryTime, promise);
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void disableDetections(final Promise promise) {
      if (rejectIfNotInitialized(promise)) {
        return;
      }
      rnSentianceHelper.disableDetections(promise);
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void createUnlinkedUser(String appId, String secret, final Promise promise) {
      sdk.createUnlinkedUser(appId, secret)
              .addOnCompleteListener(new OnCompleteListener<UserCreationResult, UserCreationError>() {
                  @Override
                  public void onComplete(@NonNull PendingOperation<UserCreationResult, UserCreationError> pendingOperation) {
                    if (pendingOperation.isSuccessful()) {
                      UserInfo userInfo = pendingOperation.getResult().getUserInfo();
                      promise.resolve(RNSentianceConverter.convertUserInfo(userInfo));
                    } else {
                      UserCreationError error = pendingOperation.getError();
                      promise.reject(E_SDK_CREATE_UNLINKED_USER_ERROR,
                              RNSentianceConverter.stringifyUserCreationError(error));
                    }
                  }
              });
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void createLinkedUser(String appId, String secret, final Promise promise) {
      rnSentianceHelper.createLinkedUser(appId, secret)
              .addOnCompleteListener(new OnCompleteListener<UserCreationResult, UserCreationError>() {
                @Override
                public void onComplete(@NonNull PendingOperation<UserCreationResult, UserCreationError> pendingOperation) {
                  if (pendingOperation.isSuccessful()) {
                    UserInfo userInfo = pendingOperation.getResult().getUserInfo();
                    promise.resolve(RNSentianceConverter.convertUserInfo(userInfo));
                  } else {
                    UserCreationError error = pendingOperation.getError();
                    promise.reject(E_SDK_CREATE_LINKED_USER_ERROR,
                            RNSentianceConverter.stringifyUserCreationError(error));
                  }
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
    sdk.reset()
            .addOnCompleteListener(new OnCompleteListener<ResetResult, ResetError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<ResetResult, ResetError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  promise.resolve(true);
                } else {
                  ResetError error = pendingOperation.getError();
                  promise.reject(E_SDK_RESET_ERROR, RNSentianceConverter.stringifyResetError(error));
                }
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
    sdk.startTrip(metadataMap, transportModeHint)
            .addOnCompleteListener(new OnCompleteListener<StartTripResult, StartTripError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<StartTripResult, StartTripError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  promise.resolve(true);
                } else {
                  StartTripError error = pendingOperation.getError();
                  promise.reject(E_SDK_START_TRIP_ERROR,
                          RNSentianceConverter.stringifyStartTripError(error));
                }
              }
            });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void stopTrip(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sdk.stopTrip()
            .addOnCompleteListener(new OnCompleteListener<StopTripResult, StopTripError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<StopTripResult, StopTripError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  promise.resolve(true);
                } else {
                  StopTripError error = pendingOperation.getError();
                  promise.reject(E_SDK_STOP_TRIP_ERROR,
                          RNSentianceConverter.stringifyStopTripError(error));
                }
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

    sdk.getUserAccessToken()
            .addOnCompleteListener(new OnCompleteListener<Token, UserAccessTokenError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<Token, UserAccessTokenError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  Token token = pendingOperation.getResult();
                  promise.resolve(RNSentianceConverter.convertToken(token));
                } else {
                  UserAccessTokenError error = pendingOperation.getError();
                  promise.reject(E_SDK_GET_TOKEN_ERROR,
                          RNSentianceConverter.stringifyUserAccessTokenError(error));
                }
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

    sdk.submitDetections()
            .addOnCompleteListener(new OnCompleteListener<r0, SubmitDetectionsError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<r0, SubmitDetectionsError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  promise.resolve(true);
                } else {
                  SubmitDetectionsError error = pendingOperation.getError();
                  promise.reject(E_SDK_SUBMIT_DETECTIONS_ERROR, error.getReason().name());
                }
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

    UserContextApi.getInstance(reactContext)
            .getUserContext()
            .addOnCompleteListener(new OnCompleteListener<UserContext, GetUserContextError>() {
              @Override
              public void onComplete(@NonNull PendingOperation<UserContext, GetUserContextError> pendingOperation) {
                if (pendingOperation.isSuccessful()) {
                  UserContext userContext = pendingOperation.getResult();
                  promise.resolve(RNSentianceConverter.convertUserContext(userContext));
                } else {
                  GetUserContextError error = pendingOperation.getError();
                  promise.reject(error.getReason().name(),
                          RNSentianceConverter.stringifyGetUserContextError(error));
                }
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

