package com.sentiance.react.bridge.usercontext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.react.bridge.usercontext.utils.ErrorCodes;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;

public class SentianceUserContextModule extends AbstractSentianceModule {

  private static final String NATIVE_MODULE_NAME = "SentianceUserContext";

  private final SentianceUserContextEmitter emitter;
  private final SentianceUserContextConverter converter;
  private @Nullable
  UserContextUpdateListener mUserContextUpdateListener;

  public SentianceUserContextModule(ReactApplicationContext reactContext) {
    super(reactContext, Sentiance.getInstance(reactContext), new SentianceSubscriptionsManager());
    emitter = new SentianceUserContextEmitter(reactContext);
    converter = new SentianceUserContextConverter();
  }

  @NonNull
  @Override
  public String getName() {
    return NATIVE_MODULE_NAME;
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void requestUserContext(final Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserContextApi.getInstance(mReactContext)
      .requestUserContext()
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          UserContext userContext = pendingOperation.getResult();
          promise.resolve(converter.convertUserContext(userContext));
        } else {
          RequestUserContextError error = pendingOperation.getError();
          promise.reject(ErrorCodes.E_SDK_REQUEST_USER_CONTEXT_ERROR,
            converter.stringifyGetUserContextError(error));
        }
      });
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void listenUserContextUpdates(Promise promise) {
    if (rejectIfNotInitialized(promise)) {
      return;
    }

    UserContextApi userContextApi = UserContextApi.getInstance(mReactContext);

    if (mUserContextUpdateListener != null) {
      userContextApi.removeUserContextUpdateListener(mUserContextUpdateListener);
    }

    mUserContextUpdateListener = emitter::sendUserContext;

    userContextApi.addUserContextUpdateListener(mUserContextUpdateListener);
    promise.resolve(true);
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
