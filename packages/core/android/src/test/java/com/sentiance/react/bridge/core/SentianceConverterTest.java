package com.sentiance.react.bridge.core;

import android.location.Location;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.ReactNativeTest;
import com.sentiance.react.bridge.test.validators.LocationBridgeValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

// TODO: add unit tests
@RunWith(RobolectricTestRunner.class)
public class SentianceConverterTest extends ReactNativeTest {
  private SentianceConverter converter;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    converter = new SentianceConverter();
  }

  @Test
  public void testConvertLocation() {
    Location location = newLocation(System.currentTimeMillis(), 0.65, 7.89, 10f, 8.5);
    Location locationWithoutAccuracy = newLocation(System.currentTimeMillis(), 0.65, 7.89, null, 8.5);
    Location locationWithoutAltitude = newLocation(System.currentTimeMillis(), 0.65, 7.89, 10f, null);

    for (Location loc : Arrays.asList(location, locationWithoutAccuracy, locationWithoutAltitude)) {
      JavaOnlyMap transformedLocation = (JavaOnlyMap) converter.convertLocation(loc);
      new LocationBridgeValidator().validate(loc, transformedLocation);
    }
  }

  private Location newLocation(long time, double latitude, double longitude, @Nullable Float accuracy, @Nullable Double altitude) {
    Location location = new Location("provider");
    location.setTime(time);
    location.setLatitude(latitude);
    location.setLongitude(longitude);
    if (accuracy != null) {
      location.setAccuracy(accuracy);
    }
    if (altitude != null) {
      location.setAltitude(altitude);
    }
    return location;
  }
}
