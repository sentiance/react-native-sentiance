package com.sentiance.react.bridge.usercontext;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.ondevice.api.Attribute;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.segment.Segment;
import com.sentiance.sdk.usercontext.api.RequestUserContextError;
import com.sentiance.sdk.usercontext.api.RequestUserContextFailureReason;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercontext.api.UserContextUpdateCriteria;
import com.sentiance.sdk.util.DateTime;

import java.util.List;

public class SentianceUserContextConverter {
  private final OnDeviceTypesConverter onDeviceTypesConverter;

  public SentianceUserContextConverter() {
    onDeviceTypesConverter = new OnDeviceTypesConverter();
  }

  private WritableMap convertSegment(Segment segment) {
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

  public WritableArray convertCriteriaList(List<UserContextUpdateCriteria> criteria) {
    WritableArray array = Arguments.createArray();
    for (UserContextUpdateCriteria criterion : criteria) {
      array.pushString(criterion.toString());
    }
    return array;
  }

  public WritableMap convertUserContext(UserContext userContext) {
    WritableMap userContextMap = Arguments.createMap();

    // Events
    WritableArray eventArray = Arguments.createArray();
    for (Event event : userContext.getEvents()) {
      eventArray.pushMap(onDeviceTypesConverter.convertEvent(event));
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
      userContextMap.putMap("lastKnownLocation", onDeviceTypesConverter.convertGeoLocation(userContext.getLastKnownLocation()));
    }

    // Home
    if (userContext.getHome() != null) {
      userContextMap.putMap("home", onDeviceTypesConverter.convertVenue(userContext.getHome()));
    }

    // Work
    if (userContext.getWork() != null) {
      userContextMap.putMap("work", onDeviceTypesConverter.convertVenue(userContext.getWork()));
    }

    // Semantic time
    userContextMap.putString("semanticTime", userContext.getSemanticTime().name());

    return userContextMap;
  }

  public String stringifyGetUserContextError(RequestUserContextError error) {
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
}
