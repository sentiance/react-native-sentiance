package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LONGITUDE;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.ondevice.api.GeoLocation;

public class GeoLocationBridgeValidator implements BridgeValidator<GeoLocation> {
  @Override
  public void validate(@NonNull GeoLocation expected, @NonNull JavaOnlyMap actual) {
    assertEquals(expected.getLatitude(), actual.getDouble(JS_KEY_LATITUDE), 0.000001);
    assertEquals(expected.getLongitude(), actual.getDouble(JS_KEY_LONGITUDE), 0.000001);
    assertEquals(expected.getAccuracyInMeters(), actual.getInt(JS_KEY_ACCURACY));
  }
}
