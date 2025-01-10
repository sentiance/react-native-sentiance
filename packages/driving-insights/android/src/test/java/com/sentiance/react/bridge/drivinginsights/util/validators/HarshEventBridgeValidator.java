package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_CONFIDENCE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_MAGNITUDE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TYPE;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;

public class HarshEventBridgeValidator extends DrivingEventBridgeValidator<HarshDrivingEvent> {

  @Override
  public void validate(@NonNull HarshDrivingEvent expected, @NonNull JavaOnlyMap actual) {
    super.validate(expected, actual);
    assertEquals(
      expected.getMagnitude(),
      actual.getDouble(JS_KEY_MAGNITUDE), 0.001);
    assertEquals(expected.getConfidence(), actual.getInt(JS_KEY_CONFIDENCE));
    assertEquals(expected.getType().name(), actual.getString(JS_KEY_TYPE));
  }

  @Override
  public void validate(@NonNull JavaOnlyMap expected, @NonNull HarshDrivingEvent actual) {
    validate(actual, expected);
  }
}
