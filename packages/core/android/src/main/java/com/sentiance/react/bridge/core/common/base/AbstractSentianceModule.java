package com.sentiance.react.bridge.core.common.base;

import static com.sentiance.react.bridge.core.common.util.ErrorCodes.E_SDK_NOT_INITIALIZED;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
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

  /**
   * This binding is invoked when the Sentiance SDK's Javascript APIs attempt to unregister listeners for a specific event.
   *
   * @param eventName the name of the event from which the app wants to unsubscribe.
   * @param subscriptionId The identifier of the Javascript subscription that was created following
   *                       an attempt to add a listener for the provided event name.
   * @param promise that resolves once the native SDK listener(s) has/have been unset.
   */
  protected abstract void removeNativeListener(String eventName, int subscriptionId, Promise promise);

  /**
   * This binding is invoked when the Sentiance SDK's Javascript APIs attempt to register listeners for a specific event.
   *
   * @param eventName the name of the event of which the app wants to be notified.
   * @param subscriptionId The identifier of the Javascript subscription that was created following
   *                       an attempt to add a listener for the provided event name. By default,
   *                       React Native supports adding multiple listeners for the same event,
   *                       but most of the Sentiance native SDKs don't.
   * @param payload extra information that was passed-in during the corresponding JS listener registration.
   * @param promise that resolves once the native SDK listener(s) has/have been set.
   */
  protected abstract void addNativeListener(String eventName, int subscriptionId, @Nullable ReadableMap payload, Promise promise);

  protected abstract void addListener(String eventName);

  protected abstract void removeListeners(Integer count);
}
