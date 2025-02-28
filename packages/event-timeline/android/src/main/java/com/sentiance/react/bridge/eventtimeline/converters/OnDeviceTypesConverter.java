package com.sentiance.react.bridge.eventtimeline.converters;

import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LONGITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_TIMESTAMP;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedback;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedbackResult;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.venue.Venue;

import java.util.List;
import java.util.Map;

public class OnDeviceTypesConverter {
    public static final String JS_KEY_ID = "id";
    public static final String JS_KEY_START_TIME = "startTime";
    public static final String JS_KEY_START_TIME_EPOCH = "startTimeEpoch";
    public static final String JS_KEY_END_TIME = "endTime";
    public static final String JS_KEY_END_TIME_EPOCH = "endTimeEpoch";
    public static final String JS_KEY_LAST_UPDATE_TIME = "lastUpdateTime";
    public static final String JS_KEY_LAST_UPDATE_TIME_EPOCH = "lastUpdateTimeEpoch";
    public static final String JS_KEY_DURATION_SECONDS = "durationInSeconds";
    public static final String JS_KEY_TYPE = "type";
    public static final String JS_KEY_LOCATION = "location";
    public static final String JS_KEY_VENUE = "venue";
    public static final String JS_KEY_SIGNIFICANCE = "significance";
    public static final String JS_KEY_TRANSPORT_MODE = "transportMode";
    public static final String JS_KEY_WAYPOINTS = "waypoints";
    public static final String JS_KEY_DISTANCE = "distance";
    public static final String JS_KEY_SPEED_IN_MPS = "speedInMps";
    public static final String JS_KEY_SPEED_LIMIT_IN_MPS = "speedLimitInMps";
    public static final String JS_KEY_HAS_UNLIMITED_SPEED_LIMIT = "hasUnlimitedSpeedLimit";
    public static final String JS_KEY_IS_SPEED_LIMIT_INFO_SET = "isSpeedLimitInfoSet";
    public static final String JS_KEY_TRANSPORT_TAGS = "transportTags";
    public static final String JS_KEY_IS_SYNTHETIC = "isSynthetic";
    public static final String JS_KEY_OCCUPANT_ROLE = "occupantRole";
    public static final String JS_KEY_IS_PROVISIONAL = "isProvisional";

    private final TransportTagsConverter transportTagsConverter;

    public OnDeviceTypesConverter() {
        transportTagsConverter = new TransportTagsConverter();
    }

    public WritableMap convertGeoLocation(GeoLocation location) {
        WritableMap locationMap = Arguments.createMap();

        locationMap.putDouble(JS_KEY_LATITUDE, location.getLatitude());
        locationMap.putDouble(JS_KEY_LONGITUDE, location.getLongitude());
        locationMap.putInt(JS_KEY_ACCURACY, location.getAccuracyInMeters());

        return locationMap;
    }

    public WritableArray convertEvents(List<Event> events) {
        WritableArray array = Arguments.createArray();
        for (Event event : events) {
            array.pushMap(convertEvent(event));
        }
        return array;
    }

    public WritableMap convertEvent(Event event) {
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

        map.putString(JS_KEY_LAST_UPDATE_TIME, event.getLastUpdateTime().toString());
        map.putDouble(JS_KEY_LAST_UPDATE_TIME_EPOCH, event.getLastUpdateTime().getEpochTime());

        map.putString(JS_KEY_TYPE, event.getEventType().toString());
        map.putBoolean(JS_KEY_IS_PROVISIONAL, event.isProvisional());

        if (event instanceof StationaryEvent) {
            addStationaryEventInfo(map, (StationaryEvent) event);
        } else if (event instanceof TransportEvent) {
            addTransportEventInfo(map, (TransportEvent) event);
        }

        return map;
    }

    private void addStationaryEventInfo(WritableMap map, StationaryEvent event) {
        if (event.getLocation() != null) {
            map.putMap(JS_KEY_LOCATION, convertGeoLocation(event.getLocation()));
        }
        map.putMap(JS_KEY_VENUE, convertVenue(event.getVenue()));
    }

    public WritableMap convertVenue(Venue venue) {
        WritableMap venueMap = Arguments.createMap();

        if (venue.getLocation() != null) {
            venueMap.putMap(JS_KEY_LOCATION, convertGeoLocation(venue.getLocation()));
        }
        venueMap.putString(JS_KEY_SIGNIFICANCE, venue.getSignificance().name());
        venueMap.putString(JS_KEY_TYPE, venue.getType().name());

        return venueMap;
    }

    private void addTransportEventInfo(WritableMap map, TransportEvent event) {
        map.putString(JS_KEY_TRANSPORT_MODE, event.getTransportMode().toString());
        map.putArray(JS_KEY_WAYPOINTS, convertWaypointList(event.getWaypoints()));
        map.putString(JS_KEY_OCCUPANT_ROLE, event.getOccupantRole().name());

        if (event.getDistanceInMeters() != null) {
            map.putInt(JS_KEY_DISTANCE, event.getDistanceInMeters());
        }

        Map<String, String> transportTags = event.getTags();
        map.putMap(JS_KEY_TRANSPORT_TAGS, transportTagsConverter.convertFrom(transportTags));
    }

    public WritableArray convertWaypoints(List<Waypoint> waypointList) {
        WritableArray array = Arguments.createArray();
        for (Waypoint waypoint : waypointList) {
            array.pushMap(convertWaypoint(waypoint));
        }
        return array;
    }

    public WritableMap convertWaypoint(Waypoint waypoint) {
        WritableMap waypointMap = Arguments.createMap();

        waypointMap.putDouble(JS_KEY_LATITUDE, waypoint.getLatitude());
        waypointMap.putDouble(JS_KEY_LONGITUDE, waypoint.getLongitude());
        waypointMap.putInt(JS_KEY_ACCURACY, waypoint.getAccuracyInMeters());
        waypointMap.putDouble(JS_KEY_TIMESTAMP, waypoint.getTimestamp());
        if (waypoint.hasSpeed()) {
            waypointMap.putDouble(JS_KEY_SPEED_IN_MPS, waypoint.getSpeedInMps());
        }
        if (waypoint.isSpeedLimitInfoSet() && !waypoint.hasUnlimitedSpeedLimit()) {
            waypointMap.putDouble(JS_KEY_SPEED_LIMIT_IN_MPS, waypoint.getSpeedLimitInMps());
        }
        waypointMap.putBoolean(JS_KEY_IS_SPEED_LIMIT_INFO_SET, waypoint.isSpeedLimitInfoSet());
        waypointMap.putBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT, waypoint.hasUnlimitedSpeedLimit());
        waypointMap.putBoolean(JS_KEY_IS_SYNTHETIC, waypoint.isSynthetic());

        return waypointMap;
    }

    private WritableArray convertWaypointList(List<Waypoint> waypointList) {
        WritableArray array = Arguments.createArray();
        for (Waypoint waypoint : waypointList) {
            array.pushMap(convertWaypoint(waypoint));
        }
        return array;
    }

    public Map<String, String> convertTransportTags(ReadableMap tags) {
        return transportTagsConverter.convertFrom(tags);
    }

    @Nullable
    public OccupantRoleFeedback getOccupantRoleFeedbackFrom(String value) {
      try {
        return OccupantRoleFeedback.valueOf(value.toUpperCase());  // Ensures case insensitivity
      } catch (IllegalArgumentException | NullPointerException e) {
        return null;
      }
    }
}
