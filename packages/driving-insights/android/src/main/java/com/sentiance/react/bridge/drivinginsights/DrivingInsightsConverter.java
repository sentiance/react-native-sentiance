package com.sentiance.react.bridge.drivinginsights;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.drivinginsights.api.DrivingEvent;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.PhoneUsageEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;

import java.util.List;

public class DrivingInsightsConverter {

  public static final String JS_KEY_ID = "id";
  public static final String JS_KEY_TYPE = "type";
  public static final String JS_KEY_END_TIME = "endTime";
  public static final String JS_KEY_DISTANCE = "distance";
  public static final String JS_KEY_LATITUDE = "latitude";
  public static final String JS_KEY_ACCURACY = "accuracy";
  public static final String JS_KEY_LONGITUDE = "longitude";
  public static final String JS_KEY_WAYPOINTS = "waypoints";
  public static final String JS_KEY_MAGNITUDE = "magnitude";
  public static final String JS_KEY_TIMESTAMP = "timestamp";
  public static final String JS_KEY_START_TIME = "startTime";
  public static final String JS_KEY_FOCUS_SCORE = "focusScore";
  public static final String JS_KEY_SMOOTH_SCORE = "smoothScore";
  public static final String JS_KEY_SAFETY_SCORES = "safetyScores";
  public static final String JS_KEY_END_TIME_EPOCH = "endTimeEpoch";
  public static final String JS_KEY_TRANSPORT_MODE = "transportMode";
  public static final String JS_KEY_TRANSPORT_EVENT = "transportEvent";
  public static final String JS_KEY_START_TIME_EPOCH = "startTimeEpoch";
  public static final String JS_KEY_DURATION_SECONDS = "durationInSeconds";

  public static WritableMap convertHarshDrivingEvent(HarshDrivingEvent event) {
    WritableMap map = convertDrivingEvent(event);
    map.putDouble(JS_KEY_MAGNITUDE, event.getMagnitude());

    return map;
  }

  public static WritableMap convertPhoneUsageEvent(PhoneUsageEvent event) {
    return convertDrivingEvent(event);
  }

  private static WritableMap convertDrivingEvent(DrivingEvent event) {
    WritableMap map = Arguments.createMap();

    map.putString(JS_KEY_START_TIME, event.getStartTime().toString());
    map.putDouble(JS_KEY_START_TIME_EPOCH, event.getStartTime().getEpochTime());
    map.putString(JS_KEY_END_TIME, event.getEndTime().toString());
    map.putDouble(JS_KEY_END_TIME_EPOCH, event.getEndTime().getEpochTime());

    return map;
  }

  public static WritableMap convertDrivingInsights(DrivingInsights drivingInsights) {
    WritableMap map = Arguments.createMap();

    map.putMap(JS_KEY_TRANSPORT_EVENT, convertTransportEvent(drivingInsights.getTransportEvent()));
    map.putMap(JS_KEY_SAFETY_SCORES, convertSafetyScores(drivingInsights.getSafetyScores()));

    return map;
  }

  private static WritableMap convertSafetyScores(SafetyScores safetyScores) {
    WritableMap map = Arguments.createMap();

    Float smoothScore = safetyScores.getSmoothScore();
    if (smoothScore != null) {
      map.putDouble(JS_KEY_SMOOTH_SCORE, smoothScore);
    }

    Float focusScore = safetyScores.getFocusScore();
    if (focusScore != null) {
      map.putDouble(JS_KEY_FOCUS_SCORE, focusScore);
    }

    return map;
  }

  private static WritableMap convertTransportEvent(TransportEvent event) {
    WritableMap map = Arguments.createMap();

    map.putString(JS_KEY_ID, event.getId());
    map.putString(JS_KEY_START_TIME, event.getStartTime().toString());
    map.putDouble(JS_KEY_START_TIME_EPOCH, event.getStartTime().getEpochTime());
    if (event.getEndTime() != null) {
      map.putString(JS_KEY_END_TIME, event.getEndTime().toString());
      map.putDouble(JS_KEY_END_TIME_EPOCH, event.getEndTime().getEpochTime());

      Long durationInSeconds = event.getDurationInSeconds();
      if (durationInSeconds != null) {
        map.putDouble(JS_KEY_DURATION_SECONDS, durationInSeconds);
      }
    }

    map.putString(JS_KEY_TYPE, event.getEventType().toString());
    map.putString(JS_KEY_TRANSPORT_MODE, event.getTransportMode().toString());
    map.putArray(JS_KEY_WAYPOINTS, convertWaypointList(event.getWaypoints()));

    if (event.getDistanceInMeters() != null) {
      map.putInt(JS_KEY_DISTANCE, event.getDistanceInMeters());
    }

    return map;
  }

  private static WritableArray convertWaypointList(List<Waypoint> waypointList) {
    WritableArray array = Arguments.createArray();
    for (Waypoint waypoint : waypointList) {
      array.pushMap(convertWaypoint(waypoint));
    }
    return array;
  }

  public static WritableMap convertWaypoint(Waypoint waypoint) {
    WritableMap waypointMap = Arguments.createMap();

    waypointMap.putDouble(JS_KEY_LATITUDE, waypoint.getLatitude());
    waypointMap.putDouble(JS_KEY_LONGITUDE, waypoint.getLongitude());
    waypointMap.putInt(JS_KEY_ACCURACY, waypoint.getAccuracyInMeters());
    waypointMap.putDouble(JS_KEY_TIMESTAMP, waypoint.getTimestamp());

    return waypointMap;
  }
}
