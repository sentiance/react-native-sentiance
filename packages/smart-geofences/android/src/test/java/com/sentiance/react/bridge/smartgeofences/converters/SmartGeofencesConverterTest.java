package com.sentiance.react.bridge.smartgeofences.converters;

import android.location.Location;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.react.bridge.smartgeofences.util.validators.SmartGeofenceEventBridgeValidator;
import com.sentiance.react.bridge.smartgeofences.util.validators.SmartGeofenceRefreshErrorBridgeValidator;
import com.sentiance.react.bridge.test.ReactNativeTest;
import com.sentiance.sdk.smartgeofences.api.SmartGeofence;
import com.sentiance.sdk.smartgeofences.api.SmartGeofenceEvent;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshError;
import com.sentiance.sdk.smartgeofences.api.SmartGeofencesRefreshFailureReason;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class SmartGeofencesConverterTest extends ReactNativeTest {
    private SmartGeofencesConverter converter;
    private SmartGeofenceRefreshErrorBridgeValidator refreshErrorBridgeValidator;
    private SmartGeofenceEventBridgeValidator smartGeofenceEventBridgeValidator;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        converter = new SmartGeofencesConverter(new SentianceConverter());
        refreshErrorBridgeValidator = new SmartGeofenceRefreshErrorBridgeValidator();
        smartGeofenceEventBridgeValidator = new SmartGeofenceEventBridgeValidator();
    }

    @Test
    public void testConvertRefreshError() {
        List<SmartGeofencesRefreshError> errors = Arrays.asList(
            new SmartGeofencesRefreshError(SmartGeofencesRefreshFailureReason.NETWORK_ERROR, "network error"),
            new SmartGeofencesRefreshError(SmartGeofencesRefreshFailureReason.NETWORK_USAGE_RESTRICTED, null)
        );

        for (SmartGeofencesRefreshError error : errors) {
            WritableMap writableMap = converter.convertRefreshError(error);
            refreshErrorBridgeValidator.validate(error, (JavaOnlyMap) writableMap);
        }
    }

    @Test
    public void testConvertSmartGeofenceEvent() {
        List<SmartGeofenceEvent> events = Arrays.asList(
            new SmartGeofenceEvent(
              System.currentTimeMillis(),
                Arrays.asList(
                    new SmartGeofence("sent_id1", 1.23, 4.56, 100, "external_id1"),
                    new SmartGeofence("sent_id2", 1.23, 4.56, 10, "external_id2")
                ),
                SmartGeofenceEvent.Type.ENTRY,
                new Location("gps")
            ),
            new SmartGeofenceEvent(
              System.currentTimeMillis(),
              Arrays.asList(
                    new SmartGeofence("sent_id1", 1.23, 4.56, 100, null),
                    new SmartGeofence("sent_id2", 1.23, 4.56, 10, "external_id2")
                ),
                SmartGeofenceEvent.Type.EXIT,
                new Location("gps")
            )
        );

        for (SmartGeofenceEvent event : events) {
            WritableMap writableMap = converter.convertSmartGeofenceEvent(event);
            smartGeofenceEventBridgeValidator.validate(event, (JavaOnlyMap) writableMap);
        }
    }
}
