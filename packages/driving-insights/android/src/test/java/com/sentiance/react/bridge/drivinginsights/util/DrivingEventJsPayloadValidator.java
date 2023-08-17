package com.sentiance.react.bridge.drivinginsights.util;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_START_TIME_EPOCH;
import static org.junit.Assert.assertEquals;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.drivinginsights.api.DrivingEvent;

class DrivingEventJsPayloadValidator<T extends DrivingEvent> implements JsPayloadValidator<T> {
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
