package com.sentiance.react.bridge.usercontext;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.ondevice.api.Attribute;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.segment.Segment;
import com.sentiance.sdk.ondevice.api.venue.Venue;
import com.sentiance.sdk.ondevice.api.venue.VenueCandidate;
import com.sentiance.sdk.ondevice.api.venue.Visit;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.RequestUserContextFailureReason;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.util.DateTime;

import java.util.List;
import java.util.Map;

public class SentianceUserContextConverter {

  public static WritableMap convertGeoLocation(GeoLocation location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putString("latitude", String.valueOf(location.getLatitude()));
    locationMap.putString("longitude", String.valueOf(location.getLongitude()));
    locationMap.putString("accuracy", String.valueOf(location.getAccuracyInMeters()));

    return locationMap;
  }

  private static WritableMap convertSegment(Segment segment) {
    WritableMap map = Arguments.createMap();

    map.putString("category", segment.getCategory().name());
    map.putString("subcategory", segment.getSubcategory().name());
    map.putString("type", segment.getType().name());
    map.putInt("id", segment.getType().getUniqueId());
    map.putString("startTime", segment.getStartTime().toString());
    map.putString("startTimeEpoch", "" + segment.getStartTime().getEpochTime() / 1000);

    DateTime endTime = segment.getEndTime();
    if (endTime != null) {
      map.putString("endTime", endTime.toString());
      map.putString("endTimeEpoch", "" + segment.getEndTime().getEpochTime() / 1000);
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
    if (event.getEndTime() != null) {
      map.putString("endTime", event.getEndTime().toString());

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

    map.putString("venueSignificance", event.getVenueSignificance().toString());

    WritableArray venueCandidatesArray = Arguments.createArray();
    for (VenueCandidate candidate : event.getVenueCandidates()) {
      venueCandidatesArray.pushMap(convertVenueCandidate(candidate));
    }

    map.putArray("venueCandidates", venueCandidatesArray);
  }

  private static WritableMap convertVenueCandidate(VenueCandidate candidate) {
    WritableMap venueCandidateMap = Arguments.createMap();
    venueCandidateMap.putMap("venue", convertVenue(candidate.getVenue()));
    venueCandidateMap.putDouble("likelihood", candidate.getLikelihood());

    WritableArray visitsArray = Arguments.createArray();
    for (Visit visit : candidate.getVisits()) {
      visitsArray.pushMap(convertVisit(visit));
    }
    venueCandidateMap.putArray("visits", visitsArray);
    return venueCandidateMap;
  }

  private static WritableMap convertVenue(Venue venue) {
    WritableMap venueMap = Arguments.createMap();

    if (venue.getName() != null) {
      venueMap.putString("name", venue.getName());
    }

    venueMap.putMap("location", convertGeoLocation(venue.getLocation()));

    WritableMap venueLabelsMap = Arguments.createMap();
    for (Map.Entry<String, String> entry : venue.getLabels().entrySet()) {
      venueLabelsMap.putString(entry.getKey(), entry.getValue());
    }
    venueMap.putMap("venueLabels", venueLabelsMap);

    return venueMap;
  }

  private static WritableMap convertVisit(Visit visit) {
    WritableMap visitMap = Arguments.createMap();
    visitMap.putString("startTime", visit.getStartTime().toString());
    visitMap.putString("startTimeEpoch", "" + visit.getStartTime().getEpochTime());

    visitMap.putString("endTime", visit.getEndTime().toString());
    visitMap.putString("endTimeEpoch", "" + visit.getEndTime().getEpochTime());

    visitMap.putInt("durationInSeconds", (int) visit.getDurationInSeconds());

    return visitMap;
  }

  private static void addTransportEventInfo(WritableMap map, TransportEvent event) {
    map.putString("transportMode", event.getTransportMode().toString());
  }
}
