package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_WAYPOINTS;
import static org.junit.Assert.assertTrue;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.sdk.ondevice.api.Waypoint;

import java.util.List;

public class WaypointsBridgeValidator implements BridgeValidator<List<Waypoint>> {

  private final WaypointBridgeValidator waypointBridgeValidator;

  public WaypointsBridgeValidator() {
    waypointBridgeValidator = new WaypointBridgeValidator();
  }

  @Override
  public void validate(List<Waypoint> expected, JavaOnlyMap actual) {
    assertTrue(actual.hasKey(JS_KEY_WAYPOINTS));
    ReadableArray transformedWaypoints = actual.getArray(JS_KEY_WAYPOINTS);
    for (int i = 0; i < expected.size(); i++) {
      Waypoint waypoint = expected.get(i);
      ReadableMap transformedWaypoint = transformedWaypoints.getMap(i);
      if (transformedWaypoint != null) {
        waypointBridgeValidator.validate(waypoint, (JavaOnlyMap) transformedWaypoint);
      }
    }
  }
}
