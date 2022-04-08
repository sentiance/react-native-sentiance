package com.sentiance.react.bridge.legacy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sentiance.react.bridge.core.SentianceEmitter;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.OnInitCallback;
import com.sentiance.sdk.ResetCallback;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.reset.ResetFailureReason;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.sentiance.react.bridge.core.SentianceErrorCodes.E_SDK_NOT_INITIALIZED;

public class LegacySentianceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

		private static final String NATIVE_MODULE_NAME = "RNSentiance";
		private final Sentiance sdk;
		private final Handler mHandler = new Handler(Looper.getMainLooper());
		private final RNSentianceHelper legacySentianceHelper;
		private final StartFinishedHandlerCreator startFinishedHandlerCreator;
		private final SentianceEmitter emitter;

		public LegacySentianceModule(ReactApplicationContext reactContext) {
				super(reactContext);

				sdk = Sentiance.getInstance(reactContext);
				legacySentianceHelper = RNSentianceHelper.getInstance(reactContext);
				emitter = new SentianceEmitter(reactContext);
				startFinishedHandlerCreator = new StartFinishedHandlerCreator(emitter);
		}

		@NonNull
		@Override
		public String getName() {
				return NATIVE_MODULE_NAME;
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void init(final String appId, final String appSecret, @Nullable final String baseURL,
										 final boolean shouldStart, final Promise promise) {
				Log.v(NATIVE_MODULE_NAME, "Initializing SDK with APP_ID: " + appId);

				final OnInitCallback initCallback = new OnInitCallback() {
						@Override
						public void onInitSuccess() {
								if (!shouldStart)
										promise.resolve(true);
						}

						@Override
						public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
								if (throwable != null) {
										promise.reject(issue.name(), throwable);
								} else {
										promise.reject(issue.name(), "");
								}
						}
				};

				new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
								legacySentianceHelper.initializeSentianceSDK(
												appId, appSecret,
												shouldStart,
												baseURL,
												initCallback,
												startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
								);
						}
				});

		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void initWithUserLinkingEnabled(final String appId, final String appSecret, @Nullable final String baseURL,
																					 final boolean shouldStart, final Promise promise) {
				Log.v(NATIVE_MODULE_NAME, "Initializing SDK with APP_ID: " + appId);

				final OnInitCallback initCallback = new OnInitCallback() {
						@Override
						public void onInitSuccess() {
								Log.v(NATIVE_MODULE_NAME, "onInitSuccess - shouldStart: " + shouldStart);
								if (!shouldStart)
										promise.resolve(true);
						}

						@Override
						public void onInitFailure(InitIssue issue, @Nullable Throwable throwable) {
								Log.v(NATIVE_MODULE_NAME, "onInitFailure");
								if (throwable != null) {
										promise.reject(issue.name(), throwable);
								} else {
										promise.reject(issue.name(), "");
								}
						}
				};

				new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
								Log.v(NATIVE_MODULE_NAME, "legacySentianceHelper.initializeSentianceSDKWithUserLinking()");
								Log.v(NATIVE_MODULE_NAME, "baseURL: " + baseURL);
								legacySentianceHelper.initializeSentianceSDKWithUserLinking(
												appId, appSecret,
												shouldStart,
												baseURL,
												initCallback,
												startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
								);
						}
				});
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void reset(final Promise promise) {
				sdk.reset(new ResetCallback() {
						@Override
						public void onResetSuccess() {
								legacySentianceHelper.disableNativeInitialization();
								promise.resolve(true);
						}

						@Override
						public void onResetFailure(ResetFailureReason reason) {
								promise.reject(reason.name(), "Resetting the SDK failed");
						}
				});
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void start(final Promise promise) {
				startWithStopDate(null, promise);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void startWithStopDate(@Nullable final Double stopEpochTimeMs, final Promise promise) {
				if (rejectIfNotInitialized(promise)) {
						return;
				}

				mHandler.post(new Runnable() {
						@Override
						public void run() {
								Long stopTime = stopEpochTimeMs == null ? null : stopEpochTimeMs.longValue();
								legacySentianceHelper.startSentianceSDK(stopTime,
												startFinishedHandlerCreator.createNewStartFinishedHandler(promise)
								);
						}
				});
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void stop(final Promise promise) {
				if (rejectIfNotInitialized(promise)) {
						return;
				}

				sdk.stop();
				promise.resolve(true);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void setValueForKey(String key, String value) {
				legacySentianceHelper.setValueForKey(key, value);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void getValueForKey(String key, String defaultValue, Promise promise) {
				String value = legacySentianceHelper.getValueForKey(key, defaultValue);
				promise.resolve(value);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void isThirdPartyLinked(Promise promise) {
				promise.resolve(legacySentianceHelper.isThirdPartyLinked());
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void isNativeInitializationEnabled(Promise promise) {
				promise.resolve(legacySentianceHelper.isNativeInitializationEnabled());
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void enableNativeInitialization(Promise promise) {
				legacySentianceHelper.enableNativeInitialization();
				promise.resolve(true);
		}

		@ReactMethod
		@SuppressWarnings("unused")
		public void disableNativeInitialization(Promise promise) {
				legacySentianceHelper.disableNativeInitialization();
				promise.resolve(true);
		}

		private boolean rejectIfNotInitialized(Promise promise) {
				if (isSdkNotInitialized()) {
						promise.reject(E_SDK_NOT_INITIALIZED, "Sdk not initialized");
						return true;
				}
				return false;
		}

		private boolean isSdkNotInitialized() {
				return sdk.getInitState() != InitState.INITIALIZED;
		}

		@Override
		public void onHostResume() {
				// Activity `onResume`
		}

		@Override
		public void onHostPause() {
				// Activity `onPause`
		}

		@Override
		public void onHostDestroy() {
				// Activity `onDestroy`
		}

}

