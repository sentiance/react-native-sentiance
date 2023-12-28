package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_ALTITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LONGITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_PROVIDER;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_TIMESTAMP;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.location.Location;

import com.facebook.react.bridge.JavaOnlyMap;

public class LocationBridgeValidator implements BridgeValidator<Location> {
  @Override
  public void validate(Location expected, JavaOnlyMap actual) {
    assertEquals(expected.getTime(), actual.getDouble(JS_KEY_TIMESTAMP), 0.001);
    assertEquals(expected.getLatitude(), actual.getDouble(JS_KEY_LATITUDE), 0.001);
    assertEquals(expected.getLongitude(), actual.getDouble(JS_KEY_LONGITUDE), 0.001);
    assertEquals(expected.getProvider(), actual.getString(JS_KEY_PROVIDER));
    if (expected.hasAccuracy()) {
      assertEquals(expected.getAccuracy(), actual.getDouble(JS_KEY_ACCURACY), 0.0);
    } else {
      assertFalse(actual.hasKey(JS_KEY_ACCURACY));
    }
    if (expected.hasAltitude()) {
      assertEquals(expected.getAltitude(), actual.getDouble(JS_KEY_ALTITUDE), 0.0);
    } else {
      assertFalse(actual.hasKey(JS_KEY_ALTITUDE));
    }
  }
}
