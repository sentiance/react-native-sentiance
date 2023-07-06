package com.sentiance.react.bridge.legacy;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_GET_TOKEN_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_START_TRIP_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_STOP_TRIP_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_SUBMIT_DETECTIONS_ERROR;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.ResetCallback;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.SubmitDetectionsCallback;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.TokenResultCallback;
import com.sentiance.sdk.trip.StartTripCallback;
import com.sentiance.sdk.trip.StopTripCallback;
import com.sentiance.sdk.trip.TransportMode;

import java.util.HashMap;
import java.util.Map;

public class LegacySentianceModule extends AbstractSentianceModule implements LifecycleEventListener {

  private static final String NATIVE_MODULE_NAME = "RNSentiance";
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private final RNSentianceHelper legacySentianceHelper;
  private final StartFinishedHandlerCreator startFinishedHandlerCreator;

  public LegacySentianceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    legacySentianceHelper = RNSentianceHelper.getInstance(reactContext);
    startFinishedHandlerCreator = new StartFinishedHandlerCreator();
  }

  @NonNull
  @Override
  public String getName() {
    return NATIVE_MODULE_NAME;
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void init(final String appId, final String appSecret, @Nullable final String baseURL,
                   final boolean shouldStart, final Promise promise) {
    Log.v(NATIVE_MODULE_NAME, "Initializing SDK with APP_ID: " + appId);

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        if (!shouldStart)
          promise.resolve(true);
      }

      @Override
      public void onInitFailure(@NonNull InitIssue issue, @Nullable Throwable throwable) {
        if (throwable != null) {
          promise.reject(issue.name(), throwable);
        } else {
          promise.reject(issue.name(), "");
        }
      }
    };

    new Handler(Looper.getMainLooper()).post(() -> legacySentianceHelper.initializeSentianceSDK(
      appId, appSecret,
      shouldStart,
      baseURL,
      initCallback,
      startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
    ));

  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void initWithUserLinkingEnabled(final String appId, final String appSecret, @Nullable final String baseURL,
                                         final boolean shouldStart, final Promise promise) {
    Log.v(NATIVE_MODULE_NAME, "Initializing SDK with APP_ID: " + appId);

    final OnInitCallback initCallback = new OnInitCallback() {
      @Override
      public void onInitSuccess() {
        Log.v(NATIVE_MODULE_NAME, "onInitSuccess - shouldStart: " + shouldStart);
        if (!shouldStart)
          promise.resolve(true);
      }

      @Override
      public void onInitFailure(@NonNull InitIssue issue, @Nullable Throwable throwable) {
        Log.v(NATIVE_MODULE_NAME, "onInitFailure");
        if (throwable != null) {
          promise.reject(issue.name(), throwable);
        } else {
          promise.reject(issue.name(), "");
        }
      }
    };

    new Handler(Looper.getMainLooper()).post(() -> {
      Log.v(NATIVE_MODULE_NAME, "legacySentianceHelper.initializeSentianceSDKWithUserLinking()");
      Log.v(NATIVE_MODULE_NAME, "baseURL: " + baseURL);
      legacySentianceHelper.initializeSentianceSDKWithUserLinking(
        appId, appSecret,
        shouldStart,
        baseURL,
        initCallback,
        startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
      );
    });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void reset(final Promise promise) {
    sdk.reset(new ResetCallback() {
      @Override
      public void onResetSuccess() {
        legacySentianceHelper.disableNativeInitialization();
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

    mHandler.post(() -> {
      Long stopTime = stopEpochTimeMs == null ? null : stopEpochTimeMs.longValue();
      legacySentianceHelper.startSentianceSDK(stopTime,
        startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
      );
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
  public void setValueForKey(String key, String value) {
    legacySentianceHelper.setValueForKey(key, value);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void getValueForKey(String key, String defaultValue, Promise promise) {
    String value = legacySentianceHelper.getValueForKey(key, defaultValue);
    promise.resolve(value);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isThirdPartyLinked(Promise promise) {
    promise.resolve(legacySentianceHelper.isThirdPartyLinked());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isNativeInitializationEnabled(Promise promise) {
    promise.resolve(legacySentianceHelper.isNativeInitializationEnabled());
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void enableNativeInitialization(Promise promise) {
    legacySentianceHelper.enableNativeInitialization();
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void disableNativeInitialization(Promise promise) {
    legacySentianceHelper.disableNativeInitialization();
    promise.resolve(true);
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
        promise.resolve(SentianceConverter.convertToken(token));
      }

      @Override
      public void onFailure() {
        promise.reject(E_SDK_GET_TOKEN_ERROR, "Something went wrong while obtaining a user token.");
      }
    });
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

  @Override
  @ReactMethod
  protected void addNativeListener(String eventName, int subscriptionId, Promise promise) {

  }

  @Override
  @ReactMethod
  protected void removeNativeListener(String eventName, int subscriptionId, Promise promise) {

  }

  @Override
  @ReactMethod
  protected void addListener(String eventName) {}

  @Override
  @ReactMethod
  public void removeListeners(Integer count) {}
}

