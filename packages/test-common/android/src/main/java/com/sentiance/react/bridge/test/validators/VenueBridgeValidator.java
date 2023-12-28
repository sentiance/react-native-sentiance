package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_LOCATION;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_SIGNIFICANCE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.venue.Venue;

public class VenueBridgeValidator implements BridgeValidator<Venue> {

  private final GeoLocationBridgeValidator geoLocationBridgeValidator;

  public VenueBridgeValidator() {
    this.geoLocationBridgeValidator = new GeoLocationBridgeValidator();
  }

  @Override
  public void validate(Venue expected, JavaOnlyMap actual) {
    GeoLocation location = expected.getLocation();
    if (location == null) {
      assertFalse(actual.hasKey(JS_KEY_LOCATION));
    } else {
      geoLocationBridgeValidator.validate(location, (JavaOnlyMap) actual.getMap(JS_KEY_LOCATION));
    }

    assertEquals(expected.getSignificance().name(), actual.getString(JS_KEY_SIGNIFICANCE));
    assertEquals(expected.getType().name(), actual.getString(JS_KEY_TYPE));
  }
}
