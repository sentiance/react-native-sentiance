package com.sentiance.react.bridge.test.validators;

import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_ACCURACY;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LATITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_LONGITUDE;
import static com.sentiance.react.bridge.core.SentianceConverter.JS_KEY_TIMESTAMP;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_HAS_UNLIMITED_SPEED_LIMIT;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_IS_SPEED_LIMIT_INFO_SET;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_IS_SYNTHETIC;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_SPEED_IN_MPS;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_SPEED_LIMIT_IN_MPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.sdk.ondevice.api.Waypoint;

public class WaypointBridgeValidator implements BridgeValidator<Waypoint> {
    @Override
    public void validate(@NonNull Waypoint expected, @NonNull JavaOnlyMap actual) {
        assertEquals(expected.getLatitude(), actual.getDouble(JS_KEY_LATITUDE), 0.000001);
        assertEquals(expected.getLongitude(), actual.getDouble(JS_KEY_LONGITUDE), 0.000001);
        assertEquals(expected.getAccuracyInMeters(), actual.getInt(JS_KEY_ACCURACY), 0.00001);
        assertEquals(expected.getTimestamp(), actual.getDouble(JS_KEY_TIMESTAMP), 0.00001);

        if (expected.hasSpeed()) {
            assertEquals(expected.getSpeedInMps(), actual.getDouble(JS_KEY_SPEED_IN_MPS), 0.00001);
        } else {
            assertFalse(actual.hasKey(JS_KEY_SPEED_IN_MPS));
        }

        if (expected.hasUnlimitedSpeedLimit()) {
            assertTrue(actual.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
            assertEquals(Double.MAX_VALUE, actual.getDouble(JS_KEY_SPEED_LIMIT_IN_MPS), 0.00001);
        } else if (expected.isSpeedLimitInfoSet() && !expected.hasUnlimitedSpeedLimit()) {
            assertEquals(expected.getSpeedLimitInMps(), actual.getDouble(JS_KEY_SPEED_LIMIT_IN_MPS), 0.00001);
            assertFalse(actual.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
            assertTrue(actual.getBoolean(JS_KEY_IS_SPEED_LIMIT_INFO_SET));
        } else {
            assertFalse(actual.getBoolean(JS_KEY_HAS_UNLIMITED_SPEED_LIMIT));
            assertFalse(actual.hasKey(JS_KEY_SPEED_LIMIT_IN_MPS));
        }

        assertEquals(expected.isSpeedLimitInfoSet(), actual.getBoolean(JS_KEY_IS_SPEED_LIMIT_INFO_SET));
        assertEquals(expected.isSynthetic(), actual.getBoolean(JS_KEY_IS_SYNTHETIC));
    }
}
