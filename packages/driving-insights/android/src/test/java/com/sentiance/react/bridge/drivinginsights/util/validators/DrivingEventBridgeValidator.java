package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME_EPOCH;
import static org.junit.Assert.assertEquals;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.sdk.drivinginsights.api.DrivingEvent;

public class DrivingEventBridgeValidator<T extends DrivingEvent> implements BridgeValidator<T> {
  @Override
  public void validate(T expected, JavaOnlyMap actual) {
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
  }
}
