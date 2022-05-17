package com.sentiance.react.bridge.core;

import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_DISABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_ENABLE_DETECTIONS_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_USER_LINK_AUTH_CODE_ERROR;
import static com.sentiance.react.bridge.core.utils.ErrorCodes.E_SDK_USER_LINK_ERROR;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.sentiance.react.bridge.core.utils.SentianceUtils;
import com.sentiance.sdk.DisableDetectionsResult;
import com.sentiance.sdk.EnableDetectionsError;
import com.sentiance.sdk.EnableDetectionsResult;
import com.sentiance.sdk.OnSdkStatusUpdateHandler;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.SdkStatusUpdateListener;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.authentication.UserLinkingError;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.init.SentianceOptions;
import com.sentiance.sdk.pendingoperation.PendingOperation;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationOptions;
import com.sentiance.sdk.usercreation.UserCreationResult;

import java.lang.ref.WeakReference;
import java.util.Date;

public class SentianceHelper {
  private static final int NOTIFICATION_ID = 1001;
  private static SentianceHelper sentianceHelper;

  private final SentianceEmitter emitter;
  private final WeakReference<Context> weakContext;
  private final UserLinker userLinker;

  private final OnSdkStatusUpdateHandler onSdkStatusUpdateHandler = this::onSdkStatusUpdated;

  private final SdkStatusUpdateListener onSdkStatusUpdateListener = this::onSdkStatusUpdated;

  SdkStatusUpdateListener getOnSdkStatusUpdateListener() {
    return onSdkStatusUpdateListener;
  }


  protected SentianceHelper(Context context) {
    emitter = new SentianceEmitter(context);
    weakContext = new WeakReference<>(context);
    userLinker = new UserLinker(emitter);
  }

  public static SentianceHelper getInstance(Context context) {
    if (sentianceHelper == null) {
      synchronized (SentianceHelper.class) {
        sentianceHelper = new SentianceHelper(context);
      }
    }
    return sentianceHelper;
  }

  private void onSdkStatusUpdated(@NonNull SdkStatus status) {
    emitter.sendStatusUpdateEvent(status);
  }

  void userLinkCallback(final Boolean linkResult) {
    userLinker.setUserLinkResult(linkResult);
  }

  public InitializationResult initializeSDK(boolean mIsAppSessionDataCollectionEnabled) {
    Context context = weakContext.get();
    if (context == null) {
      return new InitializationResult(
        false, InitializationFailureReason.EXCEPTION_OR_ERROR, new Exception("Context is null"));
    }
    Notification notification = SentianceUtils.createNotificationFromManifestData(weakContext);
    if (notification == null) {
      return new InitializationResult(false, InitializationFailureReason.EXCEPTION_OR_ERROR,
              new Throwable("null context while creating notification"));
    }
    Sentiance sentiance = Sentiance.getInstance(context);

    SentianceOptions options = new SentianceOptions.Builder(context)
      .setNotification(notification, NOTIFICATION_ID)
      .collectAppSessionData(mIsAppSessionDataCollectionEnabled)
      .build();
    InitializationResult result = sentiance.initialize(options);
    sentiance.setSdkStatusUpdateListener(onSdkStatusUpdateListener);
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
    UserCreationOptions.Builder builder = new UserCreationOptions.Builder(appId, secret, userLinker   );
    if (platformUrl != null) {
      builder.setPlatformUrl(platformUrl);
    }
    return getSentiance().createUser(builder.build());
  }

  void enableDetections(@Nullable final Promise promise) {
    enableDetections(null, promise);
  }

  void enableDetections(@Nullable Long stopTime, @Nullable final Promise promise) {
    Sentiance sentiance = getSentiance();
    Date stopDate = null;

    if (stopTime != null) {
      stopDate = new Date(stopTime);
    }

    sentiance.enableDetections(stopDate)
      .addOnCompleteListener(pendingOperation -> {
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
      });
  }

  void disableDetections(final Promise promise) {
    Sentiance sentiance = getSentiance();
    sentiance.disableDetections()
            .addOnCompleteListener(pendingOperation -> {
              if (pendingOperation.isSuccessful()) {
                DisableDetectionsResult result = pendingOperation.getResult();
                promise.resolve(SentianceConverter.convertDisableDetectionsResult(result));
              } else {
                promise.reject(E_SDK_DISABLE_DETECTIONS_ERROR, "");
              }
            });
  }

  void linkUser(final Promise promise) {
    Sentiance sentiance = getSentiance();
    sentiance.linkUser(userLinker)
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.convertUserLinkingResult(pendingOperation.getResult()));
        } else {
          UserLinkingError error = pendingOperation.getError();
          promise.reject(E_SDK_USER_LINK_ERROR,
            SentianceConverter.stringifyUserLinkingError(error));
        }
      });
  }

  void linkUser(String authCode, final Promise promise) {
    Sentiance sentiance = getSentiance();
    sentiance.linkUser(authCode)
      .addOnCompleteListener(pendingOperation -> {
        if (pendingOperation.isSuccessful()) {
          promise.resolve(SentianceConverter.convertUserLinkingResult(pendingOperation.getResult()));
        } else {
          UserLinkingError error = pendingOperation.getError();
          promise.reject(E_SDK_USER_LINK_AUTH_CODE_ERROR,
            SentianceConverter.stringifyUserLinkingError(error));
        }
      });
  }

  private Sentiance getSentiance() {
    Context context = weakContext.get();
    if (context == null) {
      throw new IllegalStateException("Context is null.");
    }
    return Sentiance.getInstance(context);
  }
}
