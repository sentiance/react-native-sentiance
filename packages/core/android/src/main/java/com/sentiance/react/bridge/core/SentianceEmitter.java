package com.sentiance.react.bridge.core;

import static com.sentiance.react.bridge.core.SentianceConverter.convertInstallId;
import static com.sentiance.react.bridge.core.SentianceConverter.convertSdkStatus;
import static com.sentiance.react.bridge.core.SentianceConverter.convertUserActivity;

import android.content.Context;

import com.sentiance.react.bridge.core.base.AbstractSentianceEmitter;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.detectionupdates.UserActivity;

public class SentianceEmitter extends AbstractSentianceEmitter {
  private static final String USER_LINK = "SDKUserLink";
  private static final String STATUS_UPDATE = "SDKStatusUpdate";
  private static final String USER_ACTIVITY_UPDATE = "SDKUserActivityUpdate";
  private static final String ON_DETECTIONS_ENABLED = "OnDetectionsEnabled";

  public SentianceEmitter(Context context) {
    super(context);
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
}


