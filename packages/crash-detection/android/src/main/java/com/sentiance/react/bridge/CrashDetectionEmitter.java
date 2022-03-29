package com.sentiance.react.bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

import static com.sentiance.react.bridge.SentianceCrashDetectionConverter.convertVehicleCrashEvent;

class CrashDetectionEmitter {
		private static final String VEHICLE_CRASH_EVENT = "SDKVehicleCrashEvent";
		private final Handler mHandler = new Handler(Looper.getMainLooper());

		private ReactContext reactContext;
		private ReactNativeHost reactNativeHost;

		CrashDetectionEmitter(Context context) {
				ReactApplication reactApplication = ((ReactApplication) context.getApplicationContext());
				reactNativeHost = reactApplication.getReactNativeHost();
				reactContext = createReactContext();
		}

		private ReactContext createReactContext() {
				if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
						reactNativeHost.getReactInstanceManager().createReactContextInBackground();
				return reactNativeHost.getReactInstanceManager().getCurrentReactContext();
		}

		void sendVehicleCrashEvent(VehicleCrashEvent crashEvent) {
				sendEvent(VEHICLE_CRASH_EVENT, convertVehicleCrashEvent(crashEvent));
		}

		private void sendEvent(final String key, final WritableMap map) {
				if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
						this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
				} else {
						// add delay

						final Counter retry = new Counter(20);
						mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
										if (CrashDetectionEmitter.this.reactContext == null)
												CrashDetectionEmitter.this.reactContext = createReactContext();
										if (CrashDetectionEmitter.this.reactContext != null && CrashDetectionEmitter.this.reactContext.hasActiveCatalystInstance()) {
												CrashDetectionEmitter.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
										} else if (retry.count-- > 0) {
												mHandler.postDelayed(this, 500);
										}
								}
						}, 500);
				}
		}

		private class Counter {
				Counter(int count) {
						this.count = count;
				}

				int count;
		}

}


