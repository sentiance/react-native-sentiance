package com.sentiance.react.bridge.core.base;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_NOT_INITIALIZED;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

public abstract class AbstractSentianceModule extends ReactContextBaseJavaModule {

  protected final ReactApplicationContext mReactContext;
  protected final Sentiance mSdk;
  protected final SentianceSubscriptionsManager mSubscriptionsManager;

  public AbstractSentianceModule(ReactApplicationContext reactApplicationContext,
                                 Sentiance sentiance, SentianceSubscriptionsManager subscriptionsManager) {
    super(reactApplicationContext);
    mReactContext = reactApplicationContext;
    mSdk = sentiance;
    mSubscriptionsManager = subscriptionsManager;
  }

  @Override
  public void initialize() {
    addSupportedEventSubscriptions(mSubscriptionsManager);
  }

  protected boolean rejectIfNotInitialized(Promise promise) {
    if (!isSdkInitialized()) {
      promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
      return true;
    }
    return false;
  }

  private boolean isSdkInitialized() {
    return mSdk.getInitState() == InitState.INITIALIZED;
  }

  protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {

  }

  protected <T> void addSubscription(@NonNull String eventType, int subscriptionId, @NonNull T eventEmitterLogic) {
    mSubscriptionsManager.addSubscription(eventType, subscriptionId, eventEmitterLogic);
  }

  protected <T> void removeSubscription(int subscriptionId, @NonNull String eventType) {
    mSubscriptionsManager.removeSubscription(subscriptionId, eventType);
  }

  protected abstract void removeNativeListener(String eventName, int subscriptionId, Promise promise);

  protected abstract void addNativeListener(String eventName, int subscriptionId, Promise promise);

  protected abstract void addListener(String eventName);

  protected abstract void removeListeners(Integer count);
}
