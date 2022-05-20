package com.sentiance.react.bridge.usercontext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.react.bridge.usercontext.utils.ErrorCodes;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;

public class SentianceUserContextModule extends AbstractSentianceModule {

  private static final String NATIVE_MODULE_NAME = "SentianceUserContext";

  private final SentianceUserContextEmitter emitter;
  private @Nullable
  UserContextUpdateListener mUserContextUpdateListener;

  public SentianceUserContextModule(ReactApplicationContext reactContext) {
    super(reactContext);
    emitter = new SentianceUserContextEmitter(reactContext);
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

    UserContextApi.getInstance(reactContext)
      .requestUserContext()
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          UserContext userContext = pendingOperation.getResult();
          promise.resolve(SentianceUserContextConverter.convertUserContext(userContext));
        } else {
          RequestUserContextError error = pendingOperation.getError();
          promise.reject(ErrorCodes.E_SDK_REQUEST_USER_CONTEXT_ERROR,
            SentianceUserContextConverter.stringifyGetUserContextError(error));
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

    mUserContextUpdateListener = emitter::sendUserContext;

    userContextApi.addUserContextUpdateListener(mUserContextUpdateListener);
    promise.resolve(true);
  }
}
