package com.sentiance.react.bridge.usercontext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.base.AbstractSentianceModule;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextApi;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.usercontext.api.UserContextUpdateListener;

import java.util.List;

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
      .addOnCompleteListener(new OnCompleteListener<UserContext, RequestUserContextError>() {
        @Override
        public void onComplete(@NonNull PendingOperation<UserContext, RequestUserContextError> pendingOperation) {
          if (pendingOperation.isSuccessful()) {
            UserContext userContext = pendingOperation.getResult();
            promise.resolve(SentianceUserContextConverter.convertUserContext(userContext));
          } else {
            RequestUserContextError error = pendingOperation.getError();
            promise.reject(error.getReason().name(),
              SentianceUserContextConverter.stringifyGetUserContextError(error));
          }
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

    mUserContextUpdateListener = new UserContextUpdateListener() {
      @Override
      public void onUserContextUpdated(@NonNull List<UserContextUpdateCriteria> criteria,
                                       @NonNull UserContext userContext) {
        emitter.sendUserContext(criteria, userContext);
      }
    };

    userContextApi.addUserContextUpdateListener(mUserContextUpdateListener);
    promise.resolve(true);
  }
}
