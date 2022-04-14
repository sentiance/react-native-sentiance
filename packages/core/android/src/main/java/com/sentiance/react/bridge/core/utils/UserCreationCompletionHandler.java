package com.sentiance.react.bridge.core.utils;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationResult;

import androidx.annotation.NonNull;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_CREATE_USER_ERROR;

public class UserCreationCompletionHandler implements OnCompleteListener<UserCreationResult, UserCreationError> {

		private final Promise promise;

		public UserCreationCompletionHandler(Promise promise) {
				this.promise = promise;
		}

		@Override
		public void onComplete(@NonNull PendingOperation<UserCreationResult, UserCreationError> pendingOperation) {
				if (pendingOperation.isSuccessful()) {
						promise.resolve(SentianceConverter.convertUserCreationResult(pendingOperation.getResult()));
				} else {
						promise.reject(E_SDK_CREATE_USER_ERROR,
										SentianceConverter.stringifyUserCreationError(pendingOperation.getError()));
				}
		}
}