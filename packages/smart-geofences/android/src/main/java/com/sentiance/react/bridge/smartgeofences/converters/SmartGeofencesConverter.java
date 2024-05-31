package com.sentiance.react.bridge.smartgeofences.converters;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.sdk.smartgeofences.api.DetectionMode;
import com.sentiance.sdk.smartgeofences.api.SmartGeofence;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceEvent;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshError;

import java.util.List;

public class SmartGeofencesConverter {

    public static final String JS_KEY_REASON = "reason";
    public static final String JS_KEY_DETAILS = "details";
    public static final String JS_KEY_TRIGGERING_LOCATION = "triggeringLocation";
    public static final String JS_KEY_EVENT_TYPE = "eventType";
    public static final String JS_KEY_GEOFENCES = "geofences";
    public static final String JS_KEY_SENTIANCE_ID = "sentianceId";
    public static final String JS_KEY_RADIUS = "radius";
    public static final String JS_KEY_EXTERNAL_ID = "externalId";

    private final SentianceConverter coreConverter;

    public SmartGeofencesConverter(SentianceConverter converter) {
        coreConverter = converter;
    }

    public WritableMap convertRefreshError(SmartGeofencesRefreshError error) {
        WritableMap map = Arguments.createMap();

        map.putString(JS_KEY_REASON, error.getReason().name());

        String details = error.getDetails();
        if (details != null) {
            map.putString(JS_KEY_DETAILS, details);
        }

        return map;
    }

    public String convertDetectionMode(DetectionMode detectionMode) {
        return detectionMode.name();
    }

    public WritableMap convertSmartGeofenceEvent(SmartGeofenceEvent event) {
        WritableMap map = Arguments.createMap();

        map.putDouble(SentianceConverter.JS_KEY_TIMESTAMP, event.getEventTime());
        map.putMap(JS_KEY_TRIGGERING_LOCATION, coreConverter.convertLocation(event.getTriggeringLocation()));
        map.putString(JS_KEY_EVENT_TYPE, convertSmartGeofenceEventType(event.getEventType()));
        map.putArray(JS_KEY_GEOFENCES, convertSmartGeofences(event.getGeofences()));

        return map;
    }

    private String convertSmartGeofenceEventType(SmartGeofenceEvent.Type eventType) {
        return eventType.name();
    }

    private WritableArray convertSmartGeofences(List<SmartGeofence> geofences) {
        WritableArray array = Arguments.createArray();

        for (SmartGeofence geofence : geofences) {
            array.pushMap(convertSmartGeofence(geofence));
        }

        return array;
    }

    private WritableMap convertSmartGeofence(SmartGeofence geofence) {
        WritableMap map = Arguments.createMap();

        map.putString(JS_KEY_SENTIANCE_ID, geofence.getSentianceId());
        map.putDouble(SentianceConverter.JS_KEY_LATITUDE, geofence.getLatitude());
        map.putDouble(SentianceConverter.JS_KEY_LONGITUDE, geofence.getLongitude());
        map.putInt(JS_KEY_RADIUS, geofence.getRadius());
        map.putString(JS_KEY_EXTERNAL_ID, geofence.getExternalId());

        return map;
    }
}
