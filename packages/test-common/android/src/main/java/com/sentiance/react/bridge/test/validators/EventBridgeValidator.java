package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_DISTANCE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_DURATION_SECONDS;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_ID;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_LAST_UPDATE_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_LAST_UPDATE_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_LOCATION;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_TRANSPORT_MODE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_TYPE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_VENUE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_WAYPOINTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.util.DateTime;

public class EventBridgeValidator implements BridgeValidator<Event> {

  private final GeoLocationBridgeValidator geoLocationBridgeValidator;
  private final VenueBridgeValidator venueBridgeValidator;
  private final WaypointsBridgeValidator waypointsBridgeValidator;

  public EventBridgeValidator() {
    this.geoLocationBridgeValidator = new GeoLocationBridgeValidator();
    this.venueBridgeValidator = new VenueBridgeValidator();
    this.waypointsBridgeValidator = new WaypointsBridgeValidator();
  }

  @Override
  public void validate(Event expected, JavaOnlyMap actual) {
    assertEquals(expected.getId(), actual.getString(JS_KEY_ID));
    assertEquals(expected.getStartTime().getEpochTime(), actual.getDouble(JS_KEY_START_TIME_EPOCH), 0.000001);
    assertEquals(expected.getStartTime().toString(), actual.getString(JS_KEY_START_TIME));

    DateTime endTime = expected.getEndTime();
    if (endTime == null) {
      assertFalse(actual.hasKey(JS_KEY_END_TIME));
    } else {
      assertEquals(endTime.toString(), actual.getString(JS_KEY_END_TIME));
      assertEquals(endTime.getEpochTime(), actual.getDouble(JS_KEY_END_TIME_EPOCH), 0.000001);

      Long durationInSeconds = expected.getDurationInSeconds();
      if (durationInSeconds == null) {
        assertFalse(actual.hasKey(JS_KEY_DURATION_SECONDS));
      } else {
        assertEquals(durationInSeconds, actual.getDouble(JS_KEY_DURATION_SECONDS), 0.000001);
      }
    }

    assertEquals(expected.getLastUpdateTime().toString(), actual.getString(JS_KEY_LAST_UPDATE_TIME));
    assertEquals(expected.getLastUpdateTime().getEpochTime(), actual.getDouble(JS_KEY_LAST_UPDATE_TIME_EPOCH), 0.000001);
    assertEquals(expected.getEventType().toString(), actual.getString(JS_KEY_TYPE));

    if (!(expected instanceof StationaryEvent)) {
      assertFalse(actual.hasKey(JS_KEY_LOCATION));
      assertFalse(actual.hasKey(JS_KEY_VENUE));

      if (!(expected instanceof TransportEvent)) {
        assertFalse(actual.hasKey(JS_KEY_TRANSPORT_MODE));
        assertFalse(actual.hasKey(JS_KEY_WAYPOINTS));
        assertFalse(actual.hasKey(JS_KEY_DISTANCE));
      } else {
        TransportEvent transport = (TransportEvent) expected;
        assertEquals(transport.getTransportMode().toString(), actual.getString(JS_KEY_TRANSPORT_MODE));
        waypointsBridgeValidator.validate(transport.getWaypoints(), actual);

        Integer distance = transport.getDistanceInMeters();
        if (distance == null) {
          assertFalse(actual.hasKey(JS_KEY_DISTANCE));
        } else {
          assertEquals(distance.intValue(), actual.getInt(JS_KEY_DISTANCE));
        }
      }
    } else {
      StationaryEvent stationary = (StationaryEvent) expected;
      GeoLocation geoLocation = stationary.getLocation();
      if (geoLocation == null) {
        assertFalse(actual.hasKey(JS_KEY_LOCATION));
      } else {
        geoLocationBridgeValidator.validate(geoLocation, (JavaOnlyMap) actual.getMap(JS_KEY_LOCATION));
      }

      venueBridgeValidator.validate(stationary.getVenue(), (JavaOnlyMap) actual.getMap(JS_KEY_VENUE));
    }
  }
}
