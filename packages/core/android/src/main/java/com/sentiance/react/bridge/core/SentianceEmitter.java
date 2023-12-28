package com.sentiance.react.bridge.core;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.detectionupdates.UserActivity;

public class SentianceEmitter extends AbstractSentianceEmitter {
  private static final String USER_LINK = "SENTIANCE_USER_LINK_EVENT";
  private static final String STATUS_UPDATE = "SENTIANCE_STATUS_UPDATE_EVENT";
  private static final String USER_ACTIVITY_UPDATE = "SENTIANCE_USER_ACTIVITY_UPDATE_EVENT";
  private static final String ON_TRIP_TIMED_OUT = "SENTIANCE_ON_TRIP_TIMED_OUT_EVENT";
  private final SentianceConverter converter;

  public SentianceEmitter(Context context) {
    super(context);
    converter = new SentianceConverter();
  }

  void sendUserLinkEvent(String installId) {
    sendEvent(USER_LINK, converter.convertInstallId(installId));
  }

  public void sendStatusUpdateEvent(SdkStatus status) {
    sendEvent(STATUS_UPDATE, converter.convertSdkStatus(status));
  }

  void sendUserActivityUpdate(UserActivity userActivity) {
    sendEvent(USER_ACTIVITY_UPDATE, converter.convertUserActivity(userActivity));
  }

  void sendOnTripTimedOutEvent() {
    sendEvent(ON_TRIP_TIMED_OUT, Arguments.createMap());
  }
}


