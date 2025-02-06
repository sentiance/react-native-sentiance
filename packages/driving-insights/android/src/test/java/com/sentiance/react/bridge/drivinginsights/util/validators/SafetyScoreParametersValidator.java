package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_OCCUPANT_ROLES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_PERIOD;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TRANSPORT_MODES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.sdk.drivinginsights.api.SafetyScoreRequestParameters;
import com.sentiance.sdk.ondevice.api.event.OccupantRole;
import com.sentiance.sdk.ondevice.api.event.TransportMode;

import java.util.List;

public class SafetyScoreParametersValidator implements BridgeValidator<SafetyScoreRequestParameters> {
    @Override
    public void validate(@NonNull JavaOnlyMap expected, @NonNull SafetyScoreRequestParameters actual) {
        int period = expected.getInt(JS_KEY_PERIOD);
        ReadableArray transportModesArray = expected.getArray(JS_KEY_TRANSPORT_MODES);
        ReadableArray occupantRolesArray = expected.getArray(JS_KEY_OCCUPANT_ROLES);

        assertNotNull(transportModesArray);
        assertNotNull(occupantRolesArray);

        validatePeriod(actual.getPeriod(), period);
        validateTransportModes(actual.getTransportModes(), transportModesArray);
        validateOccupantRoles(actual.getOccupantRoles(), occupantRolesArray);
    }

    private void validatePeriod(SafetyScoreRequestParameters.Period actualPeriod, int expectedIntPeriod) {
        SafetyScoreRequestParameters.Period expectedPeriod;
        switch (expectedIntPeriod) {
            case 7:
                expectedPeriod = SafetyScoreRequestParameters.Period.LAST_7_DAYS;
                break;
            case 14:
                expectedPeriod = SafetyScoreRequestParameters.Period.LAST_14_DAYS;
                break;
            case 30:
                expectedPeriod = SafetyScoreRequestParameters.Period.LAST_30_DAYS;
                break;
            default:
                throw new IllegalArgumentException("Unexpected period value: " + expectedIntPeriod);
        }

        assertEquals(expectedPeriod, actualPeriod);
    }

    private void validateTransportModes(SafetyScoreRequestParameters.TransportModes actualTransportModes, ReadableArray expectedTransportModes) {
        List<TransportMode> actualModes = actualTransportModes.getModes();
        assertEquals(expectedTransportModes.size(), actualModes.size());

        for (int i = 0; i < actualModes.size(); i++) {
            TransportMode actualTransportMode = actualModes.get(i);
            String expectedTransportModeAsString = expectedTransportModes.getString(i);
            assertEquals(TransportMode.valueOf(expectedTransportModeAsString), actualTransportMode);
        }
    }

    private void validateOccupantRoles(SafetyScoreRequestParameters.OccupantRoles actualOccupantRoles, ReadableArray expectedOccupantRoles) {
        List<OccupantRole> actualRoles = actualOccupantRoles.getRoles();
        assertEquals(expectedOccupantRoles.size(), actualRoles.size());

        for (int i = 0; i < actualRoles.size(); i++) {
            OccupantRole actualOccupantRole = actualRoles.get(i);
            String expectedOccupantRolesAsString = expectedOccupantRoles.getString(i);
            assertEquals(OccupantRole.valueOf(expectedOccupantRolesAsString), actualOccupantRole);
        }
    }
}
