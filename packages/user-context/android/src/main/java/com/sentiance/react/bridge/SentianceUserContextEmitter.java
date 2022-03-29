package com.sentiance.react.bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;

import java.util.List;

import static com.sentiance.react.bridge.SentianceUserContextConverter.convertUserContext;

class SentianceUserContextEmitter {
		private static final String USER_CONTEXT_EVENT = "UserContextUpdateEvent";
		private final Handler mHandler = new Handler(Looper.getMainLooper());

		private ReactContext reactContext;
		private ReactNativeHost reactNativeHost;

		SentianceUserContextEmitter(Context context) {
				ReactApplication reactApplication = ((ReactApplication) context.getApplicationContext());
				reactNativeHost = reactApplication.getReactNativeHost();
				reactContext = createReactContext();
		}

		private ReactContext createReactContext() {
				if (!reactNativeHost.getReactInstanceManager().hasStartedCreatingInitialContext())
						reactNativeHost.getReactInstanceManager().createReactContextInBackground();
				return reactNativeHost.getReactInstanceManager().getCurrentReactContext();
		}

		void sendUserContext(List<UserContextUpdateCriteria> criteria, UserContext userContext) {
				WritableMap map = Arguments.createMap();
				map.putMap("userContext", convertUserContext(userContext));
				map.putArray("criteria", convertCriteriaList(criteria));

				sendEvent(USER_CONTEXT_EVENT, map);
		}

		private WritableArray convertCriteriaList(List<UserContextUpdateCriteria> criteria) {
				WritableArray array = Arguments.createArray();
				for (UserContextUpdateCriteria criterion : criteria) {
						array.pushString(criterion.toString());
				}
				return array;
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
										if (SentianceUserContextEmitter.this.reactContext == null)
												SentianceUserContextEmitter.this.reactContext = createReactContext();
										if (SentianceUserContextEmitter.this.reactContext != null && SentianceUserContextEmitter.this.reactContext.hasActiveCatalystInstance()) {
												SentianceUserContextEmitter.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(key, map);
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


