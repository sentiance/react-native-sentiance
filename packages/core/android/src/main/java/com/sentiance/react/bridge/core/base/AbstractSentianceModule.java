package com.sentiance.react.bridge.core.base;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_NOT_INITIALIZED;

public abstract class AbstractSentianceModule extends ReactContextBaseJavaModule {

		protected final ReactApplicationContext reactContext;
		protected final Sentiance sdk;

		public AbstractSentianceModule(ReactApplicationContext reactApplicationContext) {
				super(reactApplicationContext);
				reactContext = reactApplicationContext;
				sdk = Sentiance.getInstance(reactContext);
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
