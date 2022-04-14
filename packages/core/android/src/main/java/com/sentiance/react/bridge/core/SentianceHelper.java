package com.sentiance.react.bridge.core;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.utils.SentianceUtils;
import com.sentiance.sdk.DisableDetectionsError;
import com.sentiance.sdk.DisableDetectionsResult;
import com.sentiance.sdk.EnableDetectionsError;
import com.sentiance.sdk.EnableDetectionsResult;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.UserLinker;
import com.sentiance.sdk.authentication.UserLinkingError;
import com.sentiance.sdk.authentication.UserLinkingResult;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.init.SentianceOptions;
import com.sentiance.sdk.pendingoperation.OnCompleteListener;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationOptions;
import com.sentiance.sdk.usercreation.UserCreationResult;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_DISABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_ENABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_USER_LINK_ERROR;

public class SentianceHelper {
		private static final String TAG = "SentianceHelper";
		private static final int NOTIFICATION_ID = 1001;
		private static SentianceHelper sentianceHelper;

		private final SentianceEmitter emitter;
		private final WeakReference<Context> weakContext;

		private Boolean userLinkResult = false;
		private volatile CountDownLatch userLinkLatch;

		private final OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = new OnSdkStatusUpdateHandler() {
				@Override
				public void onSdkStatusUpdate(SdkStatus status) {
						Log.d(TAG, "status update");
						emitter.sendStatusUpdateEvent(status);
				}
		};

		private final UserLinker userLinker = new UserLinker() {
				@Override
				public boolean link(String installId) {
						Log.d(TAG, "User Link");
						userLinkLatch = new CountDownLatch(1);
						emitter.sendUserLinkEvent(installId);
						try {
								userLinkLatch.await();

								return userLinkResult;
						} catch (InterruptedException e) {
								return false;
						}
				}
		};

		private SentianceHelper(Context context) {
				emitter = new SentianceEmitter(context);
				weakContext = new WeakReference<>(context);
		}

		public static SentianceHelper getInstance(Context context) {
				if (sentianceHelper == null) {
						synchronized (SentianceHelper.class) {
								sentianceHelper = new SentianceHelper(context);
						}
				}
				return sentianceHelper;
		}

		void userLinkCallback(final Boolean linkResult) {
				userLinkResult = linkResult;

				CountDownLatch latch = userLinkLatch;
				if (latch != null) {
						latch.countDown();
				}
		}

		public InitializationResult initializeSDK() {
				Context context = weakContext.get();
				if (context == null) {
						return new InitializationResult(
										false, InitializationFailureReason.EXCEPTION_OR_ERROR, new Throwable("Context is null"));
				}
				Notification notification = SentianceUtils.createNotificationFromManifestData(weakContext);
				Sentiance sentiance = Sentiance.getInstance(context);

				SentianceOptions options = new SentianceOptions.Builder(context)
								.enableAllFeatures()
								.setNotification(notification, NOTIFICATION_ID)
								.build();
				InitializationResult result = sentiance.initialize(options);
				sentiance.setSdkStatusUpdateHandler(onSdkStatusUpdateHandler);
				return result;
		}

		PendingOperation<UserCreationResult, UserCreationError> createLinkedUser(String authCode, String platformUrl) {
				UserCreationOptions.Builder builder = new UserCreationOptions.Builder(authCode);
				if (platformUrl != null) {
						builder.setPlatformUrl(platformUrl);
				}
				return getSentiance().createUser(builder.build());
		}

		PendingOperation<UserCreationResult, UserCreationError> createUnlinkedUser(String appId, String secret,
																																							 String platformUrl) {
				UserCreationOptions.Builder builder = new UserCreationOptions.Builder(appId, secret, UserLinker.NO_OP);
				if (platformUrl != null) {
						builder.setPlatformUrl(platformUrl);
				}

				return getSentiance().createUser(builder.build());
		}

		PendingOperation<UserCreationResult, UserCreationError> createLinkedUser(String appId, String secret,
																																						 String platformUrl) {
				UserCreationOptions.Builder builder = new UserCreationOptions.Builder(appId, secret, userLinker);
				if (platformUrl != null) {
						builder.setPlatformUrl(platformUrl);
				}
				return getSentiance().createUser(builder.build());
		}

		void enableDetections(@Nullable final Promise promise) {
				enableDetections(null, promise);
		}

		void enableDetections(@Nullable Long stopTime, @Nullable final Promise promise) {
				Context context = weakContext.get();
				if (context == null) {
						throw new IllegalStateException("Context is null.");
				}
				Sentiance sentiance = Sentiance.getInstance(context);
				Date stopDate = null;

				if (stopTime != null) {
						stopDate = new Date(stopTime);
				}

				sentiance.enableDetections(stopDate)
								.addOnCompleteListener(new OnCompleteListener<EnableDetectionsResult, EnableDetectionsError>() {
										@Override
										public void onComplete(@NonNull PendingOperation<EnableDetectionsResult, EnableDetectionsError> pendingOperation) {
												if (pendingOperation.isSuccessful()) {
														EnableDetectionsResult result = pendingOperation.getResult();
														emitter.sendOnDetectionsEnabledEvent(result.getSdkStatus());
														if (promise != null) {
																promise.resolve(SentianceConverter.convertEnableDetectionsResult(result));
														}
												} else {
														EnableDetectionsError error = pendingOperation.getError();
														if (promise != null) {
																promise.reject(E_SDK_ENABLE_DETECTIONS_ERROR,
																				SentianceConverter.stringifyEnableDetectionsError(error));
														}
												}
										}
								});
		}

		void disableDetections(final Promise promise) {
				Context context = weakContext.get();
				if (context == null) {
						throw new IllegalStateException("Context is null.");
				}
				Sentiance.getInstance(context)
								.disableDetections()
								.addOnCompleteListener(new OnCompleteListener<DisableDetectionsResult, DisableDetectionsError>() {
										@Override
										public void onComplete(@NonNull PendingOperation<DisableDetectionsResult, DisableDetectionsError> pendingOperation) {
												if (pendingOperation.isSuccessful()) {
														DisableDetectionsResult result = pendingOperation.getResult();
														promise.resolve(SentianceConverter.convertDisableDetectionsResult(result));
												} else {
														DisableDetectionsError error = pendingOperation.getError();
														promise.reject(E_SDK_DISABLE_DETECTIONS_ERROR,
																		SentianceConverter.stringifyDisableDetectionsError(error));
												}
										}
								});
		}

		PendingOperation<UserCreationResult, UserCreationError> createUser(String appId, String secret) {
				Context context = weakContext.get();
				if (context == null) {
						throw new IllegalStateException("Context is null.");
				}

				return Sentiance.getInstance(context)
								.createUser(new UserCreationOptions.Builder(appId, secret, userLinker).build());
		}

		void linkUser(final Promise promise) {
				Context context = weakContext.get();
				if (context == null) return;
				Sentiance sentiance = Sentiance.getInstance(context);

				sentiance.linkUser(userLinker)
								.addOnCompleteListener(new OnCompleteListener<UserLinkingResult, UserLinkingError>() {
										@Override
										public void onComplete(@NonNull PendingOperation<UserLinkingResult, UserLinkingError> pendingOperation) {
												if (pendingOperation.isSuccessful()) {
														promise.resolve(SentianceConverter.convertUserLinkingResult(pendingOperation.getResult()));
												} else {
														UserLinkingError error = pendingOperation.getError();
														promise.reject(E_SDK_USER_LINK_ERROR,
																		SentianceConverter.stringifyUserLinkingError(error));
												}
										}
								});
		}

		public UserLinker getUserLinker() {
				return userLinker;
		}

		public OnSdkStatusUpdateHandler getOnSdkStatusUpdateHandler() {
				return onSdkStatusUpdateHandler;
		}

		private Sentiance getSentiance() {
				Context context = weakContext.get();
				if (context == null) {
						throw new IllegalStateException("Context is null.");
				}
				return Sentiance.getInstance(context);
		}
}
