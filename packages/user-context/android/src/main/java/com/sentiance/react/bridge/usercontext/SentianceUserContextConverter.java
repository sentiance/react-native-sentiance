package com.sentiance.react.bridge.usercontext;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.ondevice.api.Attribute;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.segment.Segment;
import com.sentiance.sdk.ondevice.api.venue.Venue;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.RequestUserContextFailureReason;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.util.DateTime;

import java.util.List;

public class SentianceUserContextConverter {

  public static WritableMap convertGeoLocation(GeoLocation location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putDouble("latitude", location.getLatitude());
    locationMap.putDouble("longitude", location.getLongitude());
    locationMap.putInt("accuracy", location.getAccuracyInMeters());

    return locationMap;
  }

  private static WritableMap convertSegment(Segment segment) {
    WritableMap map = Arguments.createMap();

    map.putString("category", segment.getCategory().name());
    map.putString("subcategory", segment.getSubcategory().name());
    map.putString("type", segment.getType().name());
    map.putInt("id", segment.getType().getUniqueId());
    map.putString("startTime", segment.getStartTime().toString());
    map.putDouble("startTimeEpoch", segment.getStartTime().getEpochTime());

    DateTime endTime = segment.getEndTime();
    if (endTime != null) {
      map.putString("endTime", endTime.toString());
      map.putDouble("endTimeEpoch", segment.getEndTime().getEpochTime());
    }

    WritableArray attributes = Arguments.createArray();
    for (Attribute attribute : segment.getAttributes()) {
      WritableMap a = Arguments.createMap();
      a.putString("name", attribute.getName());
      a.putDouble("value", attribute.getValue());
      attributes.pushMap(a);
    }
    map.putArray("attributes", attributes);

    return map;
  }

  private static WritableMap convertEvent(Event event) {
    WritableMap map = Arguments.createMap();

    map.putString("startTime", event.getStartTime().toString());
    map.putDouble("startTimeEpoch", event.getStartTime().getEpochTime());
    if (event.getEndTime() != null) {
      map.putString("endTime", event.getEndTime().toString());
      map.putDouble("endTimeEpoch", event.getEndTime().getEpochTime());

      Long durationInSeconds = event.getDurationInSeconds();
      if (durationInSeconds != null) {
        map.putInt("durationInSeconds", (int) (long) durationInSeconds);
      }
    }

    map.putString("type", event.getEventType().toString());

    if (event instanceof StationaryEvent) {
      addStationaryEventInfo(map, (StationaryEvent) event);
    } else if (event instanceof TransportEvent) {
      addTransportEventInfo(map, (TransportEvent) event);
    }

    return map;
  }

  public static WritableArray convertCriteriaList(List<UserContextUpdateCriteria> criteria) {
    WritableArray array = Arguments.createArray();
    for (UserContextUpdateCriteria criterion : criteria) {
      array.pushString(criterion.toString());
    }
    return array;
  }

  public static WritableMap convertUserContext(UserContext userContext) {
    WritableMap userContextMap = Arguments.createMap();

    // Events
    WritableArray eventArray = Arguments.createArray();
    for (Event event : userContext.getEvents()) {
      eventArray.pushMap(convertEvent(event));
    }
    userContextMap.putArray("events", eventArray);

    // Segments
    WritableArray segmentsArray = Arguments.createArray();
    for (Segment segment : userContext.getActiveSegments()) {
      segmentsArray.pushMap(convertSegment(segment));
    }
    userContextMap.putArray("activeSegments", segmentsArray);

    // Last know location
    if (userContext.getLastKnownLocation() != null) {
      userContextMap.putMap("lastKnownLocation", convertGeoLocation(userContext.getLastKnownLocation()));
    }

    // Home
    if (userContext.getHome() != null) {
      userContextMap.putMap("home", convertVenue(userContext.getHome()));
    }

    // Work
    if (userContext.getWork() != null) {
      userContextMap.putMap("work", convertVenue(userContext.getWork()));
    }

    // Semantic time
    userContextMap.putString("semanticTime", userContext.getSemanticTime().name());

    return userContextMap;
  }

  public static String stringifyGetUserContextError(RequestUserContextError error) {
    RequestUserContextFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER:
        details = "No Sentiance user present on the device.";
        break;
      case FEATURE_NOT_ENABLED:
        details = "Feature not enabled. Contact Sentiance support to enable it.";
        break;
      case USER_DISABLED_REMOTELY:
        details = "The user is disabled remotely.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  private static void addStationaryEventInfo(WritableMap map, StationaryEvent event) {
    if (event.getLocation() != null) {
      map.putMap("location", convertGeoLocation(event.getLocation()));
    }
    map.putMap("venue", convertVenue(event.getVenue()));
  }

  private static WritableMap convertVenue(Venue venue) {
    WritableMap venueMap = Arguments.createMap();

    if (venue.getLocation() != null) {
      venueMap.putMap("location", convertGeoLocation(venue.getLocation()));
    }
    venueMap.putString("significance", venue.getSignificance().name());
    venueMap.putString("type", venue.getType().name());

    return venueMap;
  }

  private static void addTransportEventInfo(WritableMap map, TransportEvent event) {
    map.putString("transportMode", event.getTransportMode().toString());
    map.putArray("waypoints", convertWaypointList(event.getWaypoints()));

    if (event.getDistanceInMeters() != null) {
      map.putInt("distance", event.getDistanceInMeters());
    }
  }

  public static WritableMap convertWaypoint(Waypoint waypoint) {
    WritableMap waypointMap = Arguments.createMap();

    waypointMap.putDouble("latitude", waypoint.getLatitude());
    waypointMap.putDouble("longitude", waypoint.getLongitude());
    waypointMap.putInt("accuracy", waypoint.getAccuracyInMeters());
    waypointMap.putDouble("timestamp", waypoint.getTimestamp());

    return waypointMap;
  }

  private static WritableArray convertWaypointList(List<Waypoint> waypointList) {
    WritableArray array = Arguments.createArray();
    for (Waypoint waypoint : waypointList) {
      array.pushMap(convertWaypoint(waypoint));
    }
    return array;
  }

}
