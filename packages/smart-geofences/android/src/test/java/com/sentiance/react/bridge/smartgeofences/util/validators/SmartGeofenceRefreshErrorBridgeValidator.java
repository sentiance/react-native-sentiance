package com.sentiance.react.bridge.smartgeofences.util.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshError;

public class SmartGeofenceRefreshErrorBridgeValidator implements BridgeValidator<SmartGeofencesRefreshError> {

    @Override
    public void validate(SmartGeofencesRefreshError expected, JavaOnlyMap actual) {
        assertEquals(expected.getReason().name(), actual.getString(SmartGeofencesConverter.JS_KEY_REASON));

        String details = expected.getDetails();
        if (details == null) {
            assertFalse(actual.hasKey(SmartGeofencesConverter.JS_KEY_DETAILS));
        } else {
            assertEquals(details, actual.getString(SmartGeofencesConverter.JS_KEY_DETAILS));
        }
    }
}
