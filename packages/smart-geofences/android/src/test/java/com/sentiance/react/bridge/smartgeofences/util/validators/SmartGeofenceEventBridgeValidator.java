package com.sentiance.react.bridge.smartgeofences.util.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.react.bridge.test.validators.LocationBridgeValidator;
import com.sentiance.sdk.smartgeofences.api.SmartGeofence;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceEvent;

import java.util.List;

public class SmartGeofenceEventBridgeValidator implements BridgeValidator<SmartGeofenceEvent> {

    private final SmartGeofenceBridgeValidator smartGeofenceBridgeValidator;
    private final LocationBridgeValidator locationBridgeValidator;

    public SmartGeofenceEventBridgeValidator() {
        smartGeofenceBridgeValidator = new SmartGeofenceBridgeValidator();
        locationBridgeValidator = new LocationBridgeValidator();
    }

    @Override
    public void validate(SmartGeofenceEvent expected, JavaOnlyMap actual) {
        assertTrue(actual.hasKey(SmartGeofencesConverter.JS_KEY_GEOFENCES));
        validateSmartGeofences(expected.getGeofences(), actual.getArray(SmartGeofencesConverter.JS_KEY_GEOFENCES));

        assertEquals(expected.getEventType().name(), actual.getString(SmartGeofencesConverter.JS_KEY_EVENT_TYPE));

        locationBridgeValidator.validate(expected.getTriggeringLocation(),
            (JavaOnlyMap) actual.getMap(SmartGeofencesConverter.JS_KEY_TRIGGERING_LOCATION));
    }

    private void validateSmartGeofences(List<SmartGeofence> expectedGeofences, ReadableArray actualGeofences) {
        for (int i = 0; i < expectedGeofences.size(); i++) {
            SmartGeofence expectedGeofence = expectedGeofences.get(i);
            ReadableMap actualGeofence = actualGeofences.getMap(i);

            smartGeofenceBridgeValidator.validate(expectedGeofence, (JavaOnlyMap) actualGeofence);
        }
    }
}
