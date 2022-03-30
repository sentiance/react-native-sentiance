package com.sentiance.react.bridge.crashdetection;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.crashdetection.api.CrashDetectionApi;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.crashdetection.api.VehicleCrashListener;

import androidx.annotation.NonNull;

public class SentianceCrashDetectionModule extends ReactContextBaseJavaModule {

		static final String E_SDK_NOT_INITIALIZED = "E_SDK_NOT_INITIALIZED";

		private final ReactApplicationContext reactContext;
		private final Sentiance sdk;
		private final CrashDetectionEmitter emitter;

		public SentianceCrashDetectionModule(ReactApplicationContext reactContext) {
				super(reactContext);

				this.reactContext = reactContext;
				sdk = Sentiance.getInstance(reactContext);
				emitter = new CrashDetectionEmitter(reactContext);
		}

		@NonNull
		@Override
		public String getName() {
				return "SentianceCrashDetection";
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void listenVehicleCrashEvents(final Promise promise) {
				if (rejectIfNotInitialized(promise)) {
						return;
				}

				CrashDetectionApi.getInstance(reactContext).setVehicleCrashListener(new VehicleCrashListener() {
						@Override
						public void onVehicleCrash(@NonNull VehicleCrashEvent crashEvent) {
								emitter.sendVehicleCrashEvent(crashEvent);
						}
				});
				promise.resolve(true);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void invokeDummyVehicleCrash(Promise promise) {
				CrashDetectionApi.getInstance(reactContext).invokeDummyVehicleCrash();
				promise.resolve(true);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void isVehicleCrashDetectionSupported(Promise promise) {
				Boolean isCrashDetectionSupported =
								CrashDetectionApi.getInstance(reactContext).isVehicleCrashDetectionSupported();
				promise.resolve(isCrashDetectionSupported);
		}

		private boolean rejectIfNotInitialized(Promise promise) {
				if (!isSdkInitialized()) {
						promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
						return true;
				}
				return false;
		}

		private boolean isSdkInitialized() {
				return sdk.getInitState() == InitState.INITIALIZED;
		}
}

