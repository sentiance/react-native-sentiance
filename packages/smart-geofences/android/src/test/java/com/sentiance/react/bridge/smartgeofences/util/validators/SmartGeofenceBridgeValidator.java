package com.sentiance.react.bridge.smartgeofences.util.validators;

import static org.junit.Assert.assertEquals;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.sdk.smartgeofences.api.SmartGeofence;

public class SmartGeofenceBridgeValidator implements BridgeValidator<SmartGeofence> {
    @Override
    public void validate(SmartGeofence expected, JavaOnlyMap actual) {
        assertEquals(expected.getSentianceId(), actual.getString(SmartGeofencesConverter.JS_KEY_SENTIANCE_ID));
        assertEquals(expected.getLatitude(), actual.getDouble(SentianceConverter.JS_KEY_LATITUDE), 0.000001);
        assertEquals(expected.getLongitude(), actual.getDouble(SentianceConverter.JS_KEY_LONGITUDE), 0.000001);
        assertEquals(expected.getRadius(), actual.getInt(SmartGeofencesConverter.JS_KEY_RADIUS));
        assertEquals(expected.getExternalId(), actual.getString(SmartGeofencesConverter.JS_KEY_EXTERNAL_ID));
    }
}
