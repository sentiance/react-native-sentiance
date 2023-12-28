package com.sentiance.react.bridge.core.utils;

import static com.sentiance.react.bridge.core.common.util.ErrorCodes.E_SDK_CREATE_USER_ERROR;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationResult;

public class UserCreationCompletionHandler implements OnCompleteListener<UserCreationResult, UserCreationError> {

  private final Promise promise;
  private final SentianceConverter converter;

  public UserCreationCompletionHandler(Promise promise) {
    this.promise = promise;
    converter = new SentianceConverter();
  }

  @Override
  public void onComplete(@NonNull PendingOperation<UserCreationResult, UserCreationError> pendingOperation) {
    if (pendingOperation.isSuccessful()) {
      promise.resolve(converter.convertUserCreationResult(pendingOperation.getResult()));
    } else {
      promise.reject(E_SDK_CREATE_USER_ERROR,
        converter.stringifyUserCreationError(pendingOperation.getError()));
    }
  }
}
