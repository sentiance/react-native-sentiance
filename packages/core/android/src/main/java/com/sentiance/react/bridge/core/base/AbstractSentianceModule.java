package com.sentiance.react.bridge.core.base;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_NOT_INITIALIZED;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

public abstract class AbstractSentianceModule extends ReactContextBaseJavaModule {

  protected final ReactApplicationContext reactContext;
  protected final Sentiance sdk;

  public AbstractSentianceModule(ReactApplicationContext reactApplicationContext) {
    super(reactApplicationContext);
    reactContext = reactApplicationContext;
    sdk = Sentiance.getInstance(reactContext);
  }

  @ReactMethod
  public void addListener(String eventName) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    // Remove upstream listeners, stop unnecessary background tasks
  }

  protected boolean rejectIfNotInitialized(Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return true;
    }
    return false;
  }

  protected boolean isSdkInitialized() {
    return sdk.getInitState() == InitState.INITIALIZED;
  }
}
