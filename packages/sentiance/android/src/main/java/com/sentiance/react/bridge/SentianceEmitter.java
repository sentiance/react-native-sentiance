package com.sentiance.react.bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.detectionupdates.UserActivity;

import static com.sentiance.react.bridge.SentianceConverter.convertInstallId;
import static com.sentiance.react.bridge.SentianceConverter.convertSdkStatus;
import static com.sentiance.react.bridge.SentianceConverter.convertUserActivity;

class SentianceEmitter {
		private static final String USER_LINK = "SDKUserLink";
		private static final String STATUS_UPDATE = "SDKStatusUpdate";
		private static final String USER_ACTIVITY_UPDATE = "SDKUserActivityUpdate";
		private static final String ON_DETECTIONS_ENABLED = "OnDetectionsEnabled";
		private final Handler mHandler = new Handler(Looper.getMainLooper());

		private ReactContext reactContext;
		private ReactNativeHost reactNativeHost;

		SentianceEmitter(Context context) {
				ReactApplication reactApplication = ((ReactApplication) context.getApplicationContext());
				reactNativeHost = reactApplication.getReactNativeHost();
				reactContext = createReactContext();
		}

		private ReactContext createReactContext() {
				if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
						reactNativeHost.getReactInstanceManager().createReactContextInBackground();
				return reactNativeHost.getReactInstanceManager().getCurrentReactContext();
		}

		void sendUserLinkEvent(String installId) {
				sendEvent(USER_LINK, convertInstallId(installId));
		}

		void sendStatusUpdateEvent(SdkStatus status) {
				sendEvent(STATUS_UPDATE, convertSdkStatus(status));
		}

		void sendUserActivityUpdate(UserActivity userActivity) {
				sendEvent(USER_ACTIVITY_UPDATE, convertUserActivity(userActivity));
		}

		void sendOnDetectionsEnabledEvent(SdkStatus status) {
				sendEvent(ON_DETECTIONS_ENABLED, convertSdkStatus(status));
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
										if (SentianceEmitter.this.reactContext == null)
												SentianceEmitter.this.reactContext = createReactContext();
										if (SentianceEmitter.this.reactContext != null && SentianceEmitter.this.reactContext.hasActiveCatalystInstance()) {
												SentianceEmitter.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
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


