package com.sentiance.react.bridge.crashdetection;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.crashdetection.api.CrashDetectionApi;

public class SentianceCrashDetectionModule extends AbstractSentianceModule {

  private static final String NATIVE_MODULE_NAME = "SentianceCrashDetection";
  private final CrashDetectionEmitter emitter;

  public SentianceCrashDetectionModule(ReactApplicationContext reactContext) {
    super(reactContext, Sentiance.getInstance(reactContext), new SentianceSubscriptionsManager());
    emitter = new CrashDetectionEmitter(reactContext);
  }

  @NonNull
  @Override
  public String getName() {
    return NATIVE_MODULE_NAME;
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenVehicleCrashEvents(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    CrashDetectionApi.getInstance(mReactContext).setVehicleCrashListener(emitter::sendVehicleCrashEvent);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenVehicleCrashDiagnostic(final Promise promise) {
    if (rejectIfNotInitialized(promise)){
      return;
    }

    CrashDetectionApi.getInstance(mReactContext).setVehicleCrashDiagnosticListener(emitter::sendVehicleCrashDiagnosticEvent);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void invokeDummyVehicleCrash(Promise promise) {
    CrashDetectionApi.getInstance(mReactContext).invokeDummyVehicleCrash();
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void isVehicleCrashDetectionSupported(Promise promise) {
    Boolean isCrashDetectionSupported =
      CrashDetectionApi.getInstance(mReactContext).isVehicleCrashDetectionSupported();
    promise.resolve(isCrashDetectionSupported);
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

