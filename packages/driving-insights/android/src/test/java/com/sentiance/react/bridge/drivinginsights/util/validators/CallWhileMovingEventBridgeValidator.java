package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_MAX_TRAVELLED_SPEED_MPS;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_MIN_TRAVELLED_SPEED_MPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.drivinginsights.api.CallWhileMovingEvent;

public class CallWhileMovingEventBridgeValidator extends DrivingEventBridgeValidator<CallWhileMovingEvent> {

  @Override
  public void validate(@NonNull CallWhileMovingEvent expected, @NonNull JavaOnlyMap actual) {
    super.validate(expected, actual);

    if (expected.getMaxTraveledSpeedInMps() == null) {
      assertFalse(actual.hasKey(JS_KEY_MAX_TRAVELLED_SPEED_MPS));
    } else {
      assertEquals(expected.getMaxTraveledSpeedInMps(), actual.getDouble(JS_KEY_MAX_TRAVELLED_SPEED_MPS), 0.0);
    }

    if (expected.getMinTraveledSpeedInMps() == null) {
      assertFalse(actual.hasKey(JS_KEY_MIN_TRAVELLED_SPEED_MPS));
    } else {
      assertEquals(expected.getMinTraveledSpeedInMps(), actual.getDouble(JS_KEY_MIN_TRAVELLED_SPEED_MPS), 0.0);
    }
  }
}
