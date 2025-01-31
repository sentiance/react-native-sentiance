package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME_EPOCH;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.react.bridge.test.validators.WaypointsBridgeValidator;
import com.sentiance.sdk.drivinginsights.api.DrivingEvent;

public class DrivingEventBridgeValidator<T extends DrivingEvent> implements BridgeValidator<T> {

  private final WaypointsBridgeValidator waypointsValidator;

  public DrivingEventBridgeValidator() {
    waypointsValidator = new WaypointsBridgeValidator();
  }

  @Override
  public void validate(@NonNull T expected, @NonNull JavaOnlyMap actual) {
    assertEquals(
      expected.getStartTime().toString(),
      actual.getString(JS_KEY_START_TIME));
    assertEquals(
      expected.getStartTime().getEpochTime(),
      actual.getDouble(JS_KEY_START_TIME_EPOCH), 0.00000001);
    assertEquals(
      expected.getEndTime().toString(),
      actual.getString(JS_KEY_END_TIME));
    assertEquals(
      expected.getEndTime().getEpochTime(),
      actual.getDouble(JS_KEY_END_TIME_EPOCH), 0.00000001);

    waypointsValidator.validate(expected.getWaypoints(), actual);
  }
}
