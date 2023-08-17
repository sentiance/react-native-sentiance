package com.sentiance.react.bridge.drivinginsights.util;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_DISTANCE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_DURATION_SECONDS;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_ID;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_LONGITUDE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SAFETY_SCORES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SMOOTH_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_START_TIME_EPOCH;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TIMESTAMP;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TRANSPORT_EVENT;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_WAYPOINTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;

import java.util.List;

public class DrivingInsightsJsPayloadValidator implements JsPayloadValidator<DrivingInsights> {

  @Override
  public void validate(DrivingInsights expected, JavaOnlyMap actual) {
    TransportEvent transportEvent = expected.getTransportEvent();
    SafetyScores safetyScores = expected.getSafetyScores();

    validateTransportEvent(transportEvent, (JavaOnlyMap) actual.getMap(JS_KEY_TRANSPORT_EVENT));
    validateSafetyScores(safetyScores, (JavaOnlyMap) actual.getMap(JS_KEY_SAFETY_SCORES));
  }

  private void validateTransportEvent(TransportEvent transportEvent,
                                      JavaOnlyMap transformedTransportEvent) {
    assertEquals(
      transportEvent.getId(),
      transformedTransportEvent.getString(JS_KEY_ID));
    assertEquals(
      transportEvent.getStartTime().toString(),
      transformedTransportEvent.getString(JS_KEY_START_TIME));
    assertEquals(
      transportEvent.getStartTime().getEpochTime(),
      transformedTransportEvent.getDouble(JS_KEY_START_TIME_EPOCH), 0.001);

    assertEquals(
      transportEvent.getEndTime().toString(),
      transformedTransportEvent.getString(JS_KEY_END_TIME));
    assertEquals(
      transportEvent.getEndTime().getEpochTime(),
      transformedTransportEvent.getDouble(JS_KEY_END_TIME_EPOCH), 0.001);

    Long durationInSeconds = transportEvent.getDurationInSeconds();
    if (durationInSeconds == null) {
      assertFalse(transformedTransportEvent.hasKey(JS_KEY_DURATION_SECONDS));
    } else {
      assertEquals(
        durationInSeconds,
        transformedTransportEvent.getDouble(JS_KEY_DURATION_SECONDS), 0.001);
    }

    assertEquals(
      transportEvent.getEventType().toString(),
      transformedTransportEvent.getString(DrivingInsightsConverter.JS_KEY_TYPE));
    assertEquals(
      transportEvent.getTransportMode().toString(),
      transformedTransportEvent.getString(DrivingInsightsConverter.JS_KEY_TRANSPORT_MODE));

    Integer distanceInMeters = transportEvent.getDistanceInMeters();
    if (distanceInMeters == null) {
      assertFalse(transformedTransportEvent.hasKey(JS_KEY_DISTANCE));
    } else {
      assertEquals(
        distanceInMeters,
        transformedTransportEvent.getDouble(JS_KEY_DISTANCE), 0.001);
    }

    validateWaypoints(transportEvent.getWaypoints(), transformedTransportEvent);
  }

  private void validateWaypoints(List<Waypoint> waypoints, JavaOnlyMap transformedTransportEvent) {
    ReadableArray transformedWaypoints = transformedTransportEvent.getArray(JS_KEY_WAYPOINTS);
    for (int i = 0; i < waypoints.size(); i++) {
      Waypoint waypoint = waypoints.get(i);
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
      }
    }
  }

  private void validateSafetyScores(@NonNull SafetyScores safetyScores, JavaOnlyMap transformedSafetyScores) {
    assertEquals(safetyScores.getSmoothScore(), transformedSafetyScores.getDouble(JS_KEY_SMOOTH_SCORE), 0.0);
  }
}
