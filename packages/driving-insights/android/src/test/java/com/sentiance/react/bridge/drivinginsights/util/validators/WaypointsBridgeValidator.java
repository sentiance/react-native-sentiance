package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_HAS_UNLIMITED_SPEED_LIMIT;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_LONGITUDE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SPEED_IN_MPS;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SPEED_LIMIT_IN_MPS;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TIMESTAMP;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_WAYPOINTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.sdk.ondevice.api.Waypoint;

import java.util.List;

public class WaypointsBridgeValidator implements BridgeValidator<List<Waypoint>> {

  @Override
  public void validate(List<Waypoint> expectedWaypoints, JavaOnlyMap actual) {
    assertTrue(actual.hasKey(JS_KEY_WAYPOINTS));

    ReadableArray transformedWaypoints = actual.getArray(JS_KEY_WAYPOINTS);
    for (int i = 0; i < expectedWaypoints.size(); i++) {
      Waypoint waypoint = expectedWaypoints.get(i);
      ReadableMap transformedWaypoint = transformedWaypoints.getMap(i);

      if (transformedWaypoint != null) {
        assertEquals(
          waypoint.getLatitude(),
          transformedWaypoint.getDouble(JS_KEY_LATITUDE), 0.001);
        assertEquals(
          waypoint.getLongitude(),
          transformedWaypoint.getDouble(JS_KEY_LONGITUDE), 0.001);
        assertEquals(
          waypoint.getAccuracyInMeters(),
          transformedWaypoint.getInt(JS_KEY_ACCURACY), 0.001);
        assertEquals(
          waypoint.getTimestamp(),
          transformedWaypoint.getDouble(JS_KEY_TIMESTAMP), 0.001);

        if (waypoint.hasSpeed()) {
          assertEquals(
            waypoint.getSpeedInMps(),
            transformedWaypoint.getDouble(JS_KEY_SPEED_IN_MPS), 0.001);
        } else {
          assertFalse(transformedWaypoint.hasKey(JS_KEY_SPEED_IN_MPS));
        }

        if (waypoint.isSpeedLimitInfoSet()) {
          assertEquals(
            waypoint.getSpeedLimitInMps(),
            transformedWaypoint.getDouble(JS_KEY_SPEED_LIMIT_IN_MPS), 0.001);
          assertFalse(transformedWaypoint.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
        } else if (waypoint.hasUnlimitedSpeedLimit()) {
          assertTrue(transformedWaypoint.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
          assertFalse(transformedWaypoint.hasKey(JS_KEY_SPEED_LIMIT_IN_MPS));
        } else {
          assertFalse(transformedWaypoint.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
          assertFalse(transformedWaypoint.hasKey(JS_KEY_SPEED_LIMIT_IN_MPS));
        }
      }
    }
  }
}
