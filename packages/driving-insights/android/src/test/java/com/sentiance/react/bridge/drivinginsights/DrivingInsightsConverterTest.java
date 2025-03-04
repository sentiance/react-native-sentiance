package com.sentiance.react.bridge.drivinginsights;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_OCCUPANT_ROLES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_PERIOD;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TRANSPORT_MODES;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.drivinginsights.util.validators.DrivingInsightsBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.HarshEventBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.SafetyScoreParametersValidator;
import com.sentiance.react.bridge.test.ReactNativeTest;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScoreRequestParameters;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.OccupantRole;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.event.TransportMode;
import com.sentiance.sdk.util.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// TODO: add unit tests
@RunWith(RobolectricTestRunner.class)
public class DrivingInsightsConverterTest extends ReactNativeTest {
    private DrivingInsightsConverter converter;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        converter = new DrivingInsightsConverter();
    }

    @Test
    public void testConvertHarshDrivingEvent() {
        HarshDrivingEvent harshDrivingEvent = new HarshDrivingEvent(DateTime.now(), DateTime.now(), HarshDrivingEvent.Type.ACCELERATION, 100, 20.0);
        JavaOnlyMap transformedEvent = (JavaOnlyMap) converter.convertHarshDrivingEvent(harshDrivingEvent);
        new HarshEventBridgeValidator().validate(transformedEvent, harshDrivingEvent);
    }

    @Test
    public void testConvertToSafetyScoreRequestParameters() {
        List<JavaOnlyMap> jsInputs = Arrays.asList(
            JavaOnlyMap.of(
                JS_KEY_PERIOD, 7,
                JS_KEY_TRANSPORT_MODES, JavaOnlyArray.of(Arrays.stream(TransportMode.values()).map(Objects::toString).toArray()),
                JS_KEY_OCCUPANT_ROLES, JavaOnlyArray.of(Arrays.stream(OccupantRole.values()).map(Objects::toString).toArray())
            ),
            JavaOnlyMap.of(
                JS_KEY_PERIOD, 14,
                JS_KEY_TRANSPORT_MODES, JavaOnlyArray.of(TransportMode.BUS.name(), TransportMode.CAR.name()),
                JS_KEY_OCCUPANT_ROLES, JavaOnlyArray.of()
            ),
            JavaOnlyMap.of(
                JS_KEY_PERIOD, 30,
                JS_KEY_TRANSPORT_MODES, JavaOnlyArray.of(),
                JS_KEY_OCCUPANT_ROLES, JavaOnlyArray.of(OccupantRole.DRIVER.name(), OccupantRole.PASSENGER.name())
            )
        );

        for (JavaOnlyMap javascriptParams : jsInputs) {
            SafetyScoreRequestParameters params = converter.convertToSafetyScoreRequestParameters(javascriptParams);
            new SafetyScoreParametersValidator().validate(javascriptParams, params);
        }
    }

    @Test
    public void testConvertDrivingInsightsToReactNativeMap() {
        List<DrivingInsights> inputList = Arrays.asList(
            new DrivingInsights(
                new TransportEvent(
                    "transport_id",
                    DateTime.now(),
                    DateTime.now(),
                    DateTime.now(),
                    TransportMode.CAR,
                    Collections.singletonList(
                        new Waypoint(13.14, 34.67, DateTime.now().getEpochTime(), 20, 5.5f, 6.5f)
                    ),
                    500,
                    new HashMap<>(),
                    OccupantRole.DRIVER,
                    true
                ),
                new SafetyScores.Builder()
                    .setSmoothScore(.78f)
                    .setFocusScore(.99f)
                    .setCallWhileMovingScore(.55f)
                    .setLegalScore(.88f)
                    .setOverallScore(.44f)
                    .setHarshBrakingScore(.76f)
                    .setHarshTurningScore(.25f)
                    .setHarshAccelerationScore(.85f)
                    .createSafetyScores()
            ),
            new DrivingInsights(
                new TransportEvent(
                    "transport_id",
                    DateTime.now(),
                    DateTime.now(),
                    DateTime.now(),
                    TransportMode.CAR,
                    Collections.singletonList(
                        new Waypoint(13.14, 34.67, DateTime.now().getEpochTime(), 20, 5.5f, 6.5f)
                    ),
                    500,
                    new HashMap<>(),
                    OccupantRole.DRIVER,
                    true
                ),
                new SafetyScores.Builder()
                    .createSafetyScores()
            )
        );

        for (DrivingInsights drivingInsights : inputList) {
            WritableMap map = converter.convertDrivingInsights(drivingInsights);
            new DrivingInsightsBridgeValidator().validate(drivingInsights, (JavaOnlyMap) map);
        }
    }
}
