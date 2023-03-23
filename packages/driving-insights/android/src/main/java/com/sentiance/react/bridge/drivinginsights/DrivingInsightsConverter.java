package com.sentiance.react.bridge.drivinginsights;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;

import java.util.List;

public class DrivingInsightsConverter {
  public static WritableMap convertHarshDrivingEvent(HarshDrivingEvent event) {
    WritableMap map = Arguments.createMap();

    map.putString("time", event.getTime().toString());
    map.putDouble("timeEpoch", event.getTime().getEpochTime());
    map.putDouble("magnitude", event.getMagnitude());

    return map;
  }

  public static WritableMap convertDrivingInsights(DrivingInsights drivingInsights) {
    WritableMap map = Arguments.createMap();

    map.putMap("transportEvent", convertTransportEvent(drivingInsights.getTransportEvent()));
    map.putMap("safetyScores", convertSafetyScores(drivingInsights.getSafetyScores()));

    return map;
  }

  private static WritableMap convertSafetyScores(SafetyScores safetyScores) {
    WritableMap map = Arguments.createMap();

    Float smoothScore = safetyScores.getSmoothScore();
    if (smoothScore != null) {
      map.putDouble("smoothScore", smoothScore);
    }

    return map;
  }

  private static WritableMap convertTransportEvent(TransportEvent event) {
    WritableMap map = Arguments.createMap();

    map.putString("id", event.getId());
    map.putString("startTime", event.getStartTime().toString());
    map.putDouble("startTimeEpoch", event.getStartTime().getEpochTime());
    if (event.getEndTime() != null) {
      map.putString("endTime", event.getEndTime().toString());
      map.putDouble("endTimeEpoch", event.getEndTime().getEpochTime());

      Long durationInSeconds = event.getDurationInSeconds();
      if (durationInSeconds != null) {
        map.putDouble("durationInSeconds", durationInSeconds);
      }
    }

    map.putString("type", event.getEventType().toString());
    map.putString("transportMode", event.getTransportMode().toString());
    map.putArray("waypoints", convertWaypointList(event.getWaypoints()));

    if (event.getDistanceInMeters() != null) {
      map.putInt("distance", event.getDistanceInMeters());
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

    waypointMap.putDouble("latitude", waypoint.getLatitude());
    waypointMap.putDouble("longitude", waypoint.getLongitude());
    waypointMap.putInt("accuracy", waypoint.getAccuracyInMeters());
    waypointMap.putDouble("timestamp", waypoint.getTimestamp());

    return waypointMap;
  }
}
