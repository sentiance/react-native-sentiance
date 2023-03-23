package com.sentiance.react.bridge.core;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_GET_TOKEN_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_MISSING_PARAMS;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_RESET_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_START_TRIP_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_STOP_TRIP_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_SUBMIT_DETECTIONS_ERROR;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.react.bridge.core.utils.SentianceUtils;
import com.sentiance.react.bridge.core.utils.UserCreationCompletionHandler;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.SubmitDetectionsError;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TransmittableDataType;
import com.sentiance.sdk.UserAccessTokenError;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.reset.ResetError;
import com.sentiance.sdk.trip.StartTripError;
import com.sentiance.sdk.trip.StopTripError;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripTimeoutListener;
import com.sentiance.sdk.trip.TripType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SentianceModule extends AbstractSentianceModule {

  private static final String NATIVE_MODULE_NAME = "SentianceCore";
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private final SentianceHelper sentianceHelper;
  private final SentianceEmitter emitter;

  public SentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    sentianceHelper = SentianceHelper.getInstance(reactContext);
    emitter = new SentianceEmitter(reactContext);
  }

  @NonNull
  @Override
  public String getName() {
    return NATIVE_MODULE_NAME;
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void userLinkCallback(final Boolean linkResult) {
    mHandler.post(() -> sentianceHelper.userLinkCallback(linkResult));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void enableDetections(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.enableDetections(promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void enableDetectionsWithExpiryDate(@Nullable final Double expiryEpochTimeMs, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    Long expiryTime = expiryEpochTimeMs == null ? null : expiryEpochTimeMs.longValue();
    sentianceHelper.enableDetections(expiryTime, promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void disableDetections(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.disableDetections(promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void createUnlinkedUser(String appId, String secret, @Nullable String platformUrl, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.createUnlinkedUser(appId, secret, platformUrl)
      .addOnCompleteListener(new UserCreationCompletionHandler(promise));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void createLinkedUser(String appId, String secret, @Nullable String platformUrl, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.createLinkedUser(appId, secret, platformUrl)
      .addOnCompleteListener(new UserCreationCompletionHandler(promise));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void createLinkedUserWithAuthCode(String authCode, @Nullable String platformUrl, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.createLinkedUser(authCode, platformUrl)
      .addOnCompleteListener(new UserCreationCompletionHandler(promise));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void linkUser(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.linkUser(promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void linkUserWithAuthCode(String authCode, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }
    sentianceHelper.linkUser(authCode, promise);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void userExists(final Promise promise) {
    promise.resolve(sdk.userExists());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isUserLinked(final Promise promise) {
    promise.resolve(sdk.isUserLinked());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void reset(final Promise promise) {
    sdk.reset()
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.convertResetResult(pendingOperation.getResult()));
        } else {
          ResetError error = pendingOperation.getError();
          promise.reject(E_SDK_RESET_ERROR, SentianceConverter.stringifyResetError(error));
        }
      });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getInitState(final Promise promise) {
    InitState initState = sdk.getInitState();
    promise.resolve(SentianceConverter.convertInitState(initState));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void startTrip(@Nullable ReadableMap metadata, int hint, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Map<String, String> metadataMap = new HashMap<>();
    if (metadata != null) {
      for (Map.Entry<String, Object> entry : metadata.toHashMap().entrySet()) {
        metadataMap.put(entry.getKey(), entry.getValue().toString());
      }
    }
    final TransportMode transportModeHint = SentianceConverter.toTransportMode(hint);
    sdk.startTrip(metadataMap, transportModeHint)
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.createEmptyResult());
        } else {
          StartTripError error = pendingOperation.getError();
          promise.reject(E_SDK_START_TRIP_ERROR,
            SentianceConverter.stringifyStartTripError(error));
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
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.createEmptyResult());
        } else {
          StopTripError error = pendingOperation.getError();
          promise.reject(E_SDK_STOP_TRIP_ERROR,
            SentianceConverter.stringifyStopTripError(error));
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
    promise.resolve(SentianceConverter.convertSdkStatus(sdkStatus));
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
    final TripType type = SentianceConverter.toTripType(typeParam);
    Boolean isTripOngoing = sdk.isTripOngoing(type);
    promise.resolve(isTripOngoing);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void requestUserAccessToken(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.requestUserAccessToken()
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          Token token = pendingOperation.getResult();
          promise.resolve(SentianceConverter.convertToken(token));
        } else {
          UserAccessTokenError error = pendingOperation.getError();
          promise.reject(E_SDK_GET_TOKEN_ERROR,
            SentianceConverter.stringifyUserAccessTokenError(error));
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
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void addTripMetadata(ReadableMap inputMetadata, final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    final Map<String, String> metadata = SentianceConverter.convertReadableMapToMap(inputMetadata);
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

    final Map<String, String> metadata = SentianceConverter.convertReadableMapToMap(inputMetadata);
    sdk.addUserMetadataFields(metadata);
    promise.resolve(null);
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
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void submitDetections(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.submitDetections()
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.createEmptyResult());
        } else {
          SubmitDetectionsError error = pendingOperation.getError();
          promise.reject(E_SDK_SUBMIT_DETECTIONS_ERROR, error.getReason().name());
        }
      });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaLimit(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long wifiQuotaLimit = sdk.getWiFiQuotaLimit();
    promise.resolve(Long.toString(wifiQuotaLimit));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getWiFiQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long wifiQuotaUsage = sdk.getWiFiQuotaUsage();
    promise.resolve(Long.toString(wifiQuotaUsage));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaLimit(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long mobileQuotaLimit = sdk.getMobileQuotaLimit();
    promise.resolve(Long.toString(mobileQuotaLimit));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getMobileQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long mobileQuotaUsage = sdk.getMobileQuotaUsage();
    promise.resolve(Long.toString(mobileQuotaUsage));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaLimit(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long diskQuotaLimit = sdk.getDiskQuotaLimit();
    promise.resolve(Long.toString(diskQuotaLimit));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getDiskQuotaUsage(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    long diskQuotaUsage = sdk.getDiskQuotaUsage();
    promise.resolve(Long.toString(diskQuotaUsage));
  }

  @SuppressLint("MissingPermission")
  @ReactMethod
  @SuppressWarnings("unused")
  public void disableBatteryOptimization(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    sdk.disableBatteryOptimization();
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenUserActivityUpdates(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).setUserActivityListener(emitter::sendUserActivityUpdate);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getUserActivity(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserActivity activity = Sentiance.getInstance(reactContext).getUserActivity();
    promise.resolve(SentianceConverter.convertUserActivity(activity));
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void updateSdkNotification(final String title, final String message, Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Notification notification = SentianceUtils.createNotificationFromManifestData(
            new WeakReference<>(reactContext.getApplicationContext()),
            title,
            message);
    if (notification != null) {
      Sentiance.getInstance(reactContext)
               .updateSdkNotification(notification);
    }

    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void setAppSessionDataCollectionEnabled(boolean enabled, Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).setAppSessionDataCollectionEnabled(enabled);
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isAppSessionDataCollectionEnabled(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    promise.resolve(Sentiance.getInstance(reactContext).isAppSessionDataCollectionEnabled());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenSdkStatusUpdates(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext).setSdkStatusUpdateListener(sentianceHelper.getOnSdkStatusUpdateListener());
    promise.resolve(null);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenTripTimeout(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    Sentiance.getInstance(reactContext)
      .setTripTimeoutListener(emitter::sendOnTripTimedOutEvent);
    promise.resolve(null);
  }

  @ReactMethod
  public void setTransmittableDataTypes(ReadableArray types, Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    ArrayList<Object> rawTypes = types.toArrayList();
    Set<TransmittableDataType> dataTypes = new HashSet<>();
    for (Object rawType : rawTypes) {
      dataTypes.add(TransmittableDataType.valueOf((String) rawType));
    }
    sdk.setTransmittableDataTypes(dataTypes);
    promise.resolve(null);
  }

  @ReactMethod
  public void getTransmittableDataTypes(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    WritableArray args = Arguments.createArray();
    Set<TransmittableDataType> transmittableDataTypes = sdk.getTransmittableDataTypes();
    for (TransmittableDataType type : transmittableDataTypes) {
      args.pushString(type.name());
    }
    promise.resolve(args);
  }

  @Override
  @ReactMethod
  protected void addListener(String eventName, int subscriptionId, Promise promise) {

  }

  @Override
  @ReactMethod
  protected void removeListener(String eventName, int subscriptionId, Promise promise) {

  }

  @Override
  @ReactMethod
  public void removeListeners(Integer count) {}
}

