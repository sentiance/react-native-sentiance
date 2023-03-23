package com.sentiance.react.bridge.crashdetection;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.sdk.crashdetection.api.CrashDetectionApi;

public class SentianceCrashDetectionModule extends AbstractSentianceModule {

  private static final String NATIVE_MODULE_NAME = "SentianceCrashDetection";
  private final CrashDetectionEmitter emitter;

  public SentianceCrashDetectionModule(ReactApplicationContext reactContext) {
    super(reactContext);
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

    CrashDetectionApi.getInstance(reactContext).setVehicleCrashListener(emitter::sendVehicleCrashEvent);
    promise.resolve(true);
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenVehicleCrashDiagnostic(final Promise promise) {
    if (rejectIfNotInitialized(promise)){
      return;
    }

    CrashDetectionApi.getInstance(reactContext).setVehicleCrashDiagnosticListener(emitter::sendVehicleCrashDiagnosticEvent);
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
    Boolean isCrashDetectionSupported =
      CrashDetectionApi.getInstance(reactContext).isVehicleCrashDetectionSupported();
    promise.resolve(isCrashDetectionSupported);
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

