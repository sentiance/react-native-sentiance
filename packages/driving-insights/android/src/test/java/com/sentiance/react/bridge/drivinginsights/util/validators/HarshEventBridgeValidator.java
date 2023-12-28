package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_MAGNITUDE;

import static org.junit.Assert.assertEquals;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;

public class HarshEventBridgeValidator extends DrivingEventBridgeValidator<HarshDrivingEvent> {

  @Override
  public void validate(HarshDrivingEvent expected, JavaOnlyMap actual) {
    super.validate(expected, actual);
    assertEquals(
      expected.getMagnitude(),
      actual.getDouble(JS_KEY_MAGNITUDE), 0.001);
  }
}
