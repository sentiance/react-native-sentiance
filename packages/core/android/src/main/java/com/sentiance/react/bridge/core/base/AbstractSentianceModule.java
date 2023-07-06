package com.sentiance.react.bridge.core.base;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_NOT_INITIALIZED;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

public abstract class AbstractSentianceModule extends ReactContextBaseJavaModule {

  protected final ReactApplicationContext reactContext;
  protected final Sentiance sdk;
  protected final SentianceSubscriptionsManager subscriptionsManager;

  public AbstractSentianceModule(ReactApplicationContext reactApplicationContext) {
    super(reactApplicationContext);
    reactContext = reactApplicationContext;
    sdk = Sentiance.getInstance(reactContext);
    subscriptionsManager = new SentianceSubscriptionsManager();
  }

  @Override
  public void initialize() {
    addSupportedEventSubscriptions(subscriptionsManager);
  }

  protected boolean rejectIfNotInitialized(Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return true;
    }
    return false;
  }

  private boolean isSdkInitialized() {
    return sdk.getInitState() == InitState.INITIALIZED;
  }

  protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {

  }

  protected <T> void addSubscription(@NonNull String eventType, int subscriptionId, @NonNull T eventEmitterLogic) {
    subscriptionsManager.addSubscription(eventType, subscriptionId, eventEmitterLogic);
  }

  protected <T> void removeSubscription(int subscriptionId, @NonNull String eventType) {
    subscriptionsManager.removeSubscription(subscriptionId, eventType);
  }

  protected abstract void removeNativeListener(String eventName, int subscriptionId, Promise promise);

  protected abstract void addNativeListener(String eventName, int subscriptionId, Promise promise);

  protected abstract void addListener(String eventName);

  protected abstract void removeListeners(Integer count);
}
