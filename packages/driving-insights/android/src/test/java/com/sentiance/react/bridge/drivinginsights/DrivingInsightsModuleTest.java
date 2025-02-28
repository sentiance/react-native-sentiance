package com.sentiance.react.bridge.drivinginsights;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_OCCUPANT_ROLES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_PERIOD;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SAFETY_SCORES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TRANSPORT_MODES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsEmitter.DRIVING_INSIGHTS_READY_EVENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.drivinginsights.util.validators.CallWhileMovingEventBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.DrivingInsightsBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.HarshEventBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.PhoneUsageEventBridgeValidator;
import com.sentiance.react.bridge.drivinginsights.util.validators.SpeedingEventBridgeValidator;
import com.sentiance.react.bridge.test.ReactNativeModuleTest;
import com.sentiance.sdk.drivinginsights.api.CallWhileMovingEvent;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsApi;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsReadyListener;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.PhoneUsageEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScoreRequestParameters;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.drivinginsights.api.SpeedingEvent;
import com.sentiance.sdk.eventtimeline.timelines.creators.SafetyScoreType;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.OccupantRole;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.event.TransportMode;
import com.sentiance.sdk.util.DateTime;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DrivingInsightsModuleTest extends ReactNativeModuleTest<DrivingInsightsModule> {

    @Mock
    private DrivingInsightsApi mDrivingInsightsApi;
    @Mock
    private DrivingInsightsEmitter mDrivingInsightsEmitter;
    @Captor
    private ArgumentCaptor<DrivingInsightsReadyListener> drivingInsightsReadyListenerCaptor;

    private HarshEventBridgeValidator harshEventJsPayloadValidator;
    private PhoneUsageEventBridgeValidator phoneUsageEventJsPayloadValidator;
    private SpeedingEventBridgeValidator speedingEventJsPayloadValidator;
    private CallWhileMovingEventBridgeValidator callWhileMovingEventBridgeValidator;
    private DrivingInsightsBridgeValidator drivingInsightsJsPayloadValidator;

    @Override
    protected DrivingInsightsModule initModule() {
        return new DrivingInsightsModule(
            mReactApplicationContext, mSentiance, mDrivingInsightsApi, mDrivingInsightsEmitter,
            mSentianceSubscriptionsManager, new DrivingInsightsConverter());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        harshEventJsPayloadValidator = new HarshEventBridgeValidator();
        phoneUsageEventJsPayloadValidator = new PhoneUsageEventBridgeValidator();
        speedingEventJsPayloadValidator = new SpeedingEventBridgeValidator();
        callWhileMovingEventBridgeValidator = new CallWhileMovingEventBridgeValidator();
        drivingInsightsJsPayloadValidator = new DrivingInsightsBridgeValidator();
    }

    @Test
    public void testGetHarshDrivingEvents() {
        String transportId = "transport_id";
        List<HarshDrivingEvent> expectedHarshEvents = Arrays.asList(
            new HarshDrivingEvent(
                DateTime.fromMillis(System.currentTimeMillis()),
                DateTime.fromMillis(System.currentTimeMillis() + 2000),
                HarshDrivingEvent.Type.ACCELERATION,
                78,
                0.55,
                createDummyWaypoints()
            ),
            new HarshDrivingEvent(
                DateTime.fromMillis(System.currentTimeMillis()),
                DateTime.fromMillis(System.currentTimeMillis() + 2000),
                HarshDrivingEvent.Type.BRAKING,
                90,
                0.43,
                createDummyWaypoints()
            )
        );

        when(mDrivingInsightsApi.getHarshDrivingEvents(transportId)).thenReturn(expectedHarshEvents);
        mModule.getHarshDrivingEvents(transportId, mPromise);

        verify(mPromise).resolve(writableArrayCaptor.capture());

        WritableArray transformedHarshEventsArray = writableArrayCaptor.getValue();
        assertEquals(expectedHarshEvents.size(), transformedHarshEventsArray.size());

        List<JavaOnlyMap> transformedHarshEvents = transformedHarshEventsArray.toArrayList()
            .stream()
            .map(o -> (JavaOnlyMap) o)
            .collect(Collectors.toList());
        for (int i = 0; i < expectedHarshEvents.size(); i++) {
            harshEventJsPayloadValidator.validate(expectedHarshEvents.get(i), transformedHarshEvents.get(i));
        }
    }

    @Test
    public void testGetPhoneUsageEvents() {
        long now = System.currentTimeMillis();
        String transportId = "transport_id";
        List<PhoneUsageEvent> expectedPhoneUsageEvents = Arrays.asList(
            new PhoneUsageEvent(
                DateTime.fromMillis(now),
                DateTime.fromMillis(now + 2000),
                createDummyWaypoints()
            ),
            new PhoneUsageEvent(
                DateTime.fromMillis(now + 2000),
                DateTime.fromMillis(now + 4000),
                createDummyWaypoints()
            )
        );

        when(mDrivingInsightsApi.getPhoneUsageEvents(transportId)).thenReturn(expectedPhoneUsageEvents);
        mModule.getPhoneUsageEvents(transportId, mPromise);

        verify(mPromise).resolve(writableArrayCaptor.capture());

        WritableArray transformedPhoneUsageEventsArray = writableArrayCaptor.getValue();
        assertEquals(expectedPhoneUsageEvents.size(), transformedPhoneUsageEventsArray.size());

        List<JavaOnlyMap> transformedPhoneUsageEvents = transformedPhoneUsageEventsArray.toArrayList()
            .stream()
            .map(o -> (JavaOnlyMap) o)
            .collect(Collectors.toList());
        for (int i = 0; i < expectedPhoneUsageEvents.size(); i++) {
            phoneUsageEventJsPayloadValidator.validate(expectedPhoneUsageEvents.get(i), transformedPhoneUsageEvents.get(i));
        }
    }

    @Test
    public void testGetCallWhileMovingEvents() {
        long now = System.currentTimeMillis();
        String transportId = "transport_id";
        List<CallWhileMovingEvent> expectedCallWhileMovingEvents = Arrays.asList(
            new CallWhileMovingEvent(
                DateTime.fromMillis(now),
                DateTime.fromMillis(now + 2_000),
                10f, 5f,
                createDummyWaypoints()
            ),
            new CallWhileMovingEvent(
                DateTime.fromMillis(now + 4_000),
                DateTime.fromMillis(now + 6_000),
                null, 5f,
                createDummyWaypoints()
            ),
            new CallWhileMovingEvent(
                DateTime.fromMillis(now + 8_000),
                DateTime.fromMillis(System.currentTimeMillis() + 10_000),
                null, null
            ),
            new CallWhileMovingEvent(
                DateTime.fromMillis(now + 12_000),
                DateTime.fromMillis(now + 14_000),
                11f, null
            )
        );

        when(mDrivingInsightsApi.getCallWhileMovingEvents(transportId)).thenReturn(expectedCallWhileMovingEvents);
        mModule.getCallWhileMovingEvents(transportId, mPromise);

        verify(mPromise).resolve(writableArrayCaptor.capture());

        WritableArray transformedCallWhileMovingEventsArray = writableArrayCaptor.getValue();
        assertEquals(expectedCallWhileMovingEvents.size(), transformedCallWhileMovingEventsArray.size());

        List<JavaOnlyMap> transformedCallWhileMovingEvents = transformedCallWhileMovingEventsArray.toArrayList()
            .stream()
            .map(o -> (JavaOnlyMap) o)
            .collect(Collectors.toList());
        for (int i = 0; i < expectedCallWhileMovingEvents.size(); i++) {
            callWhileMovingEventBridgeValidator.validate(expectedCallWhileMovingEvents.get(i), transformedCallWhileMovingEvents.get(i));
        }
    }

    @Test
    public void testGetSpeedingEvents() {
        long now = System.currentTimeMillis();
        String transportId = "transport_id";

        List<SpeedingEvent> expectedSpeedingEvents = Arrays.asList(
            new SpeedingEvent(
                DateTime.fromMillis(now),
                DateTime.fromMillis(now + 2000),
                createDummyWaypoints()
            ),
            new SpeedingEvent(
                DateTime.fromMillis(now + 10_000),
                DateTime.fromMillis(now + 20_000),
                createDummyWaypoints()
            )
        );

        when(mDrivingInsightsApi.getSpeedingEvents(transportId)).thenReturn(expectedSpeedingEvents);
        mModule.getSpeedingEvents(transportId, mPromise);

        verify(mPromise).resolve(writableArrayCaptor.capture());

        WritableArray transformedSpeedingEventsArray = writableArrayCaptor.getValue();
        assertEquals(expectedSpeedingEvents.size(), transformedSpeedingEventsArray.size());

        List<JavaOnlyMap> transformedSpeedingEvents = transformedSpeedingEventsArray.toArrayList()
            .stream()
            .map(o -> (JavaOnlyMap) o)
            .collect(Collectors.toList());
        for (int i = 0; i < expectedSpeedingEvents.size(); i++) {
            speedingEventJsPayloadValidator.validate(expectedSpeedingEvents.get(i), transformedSpeedingEvents.get(i));
        }
    }

    @Test
    public void testGetDrivingInsights() {
        String transportId = "transport_id";
        DrivingInsights expectedDrivingInsights = createDummyDrivingInsights(transportId);

        when(mDrivingInsightsApi.getDrivingInsights(transportId)).thenReturn(expectedDrivingInsights);
        mModule.getDrivingInsights(transportId, mPromise);

        verify(mPromise).resolve(writableMapCaptor.capture());

        WritableMap transformedDrivingInsights = writableMapCaptor.getValue();
        drivingInsightsJsPayloadValidator.validate(expectedDrivingInsights, (JavaOnlyMap) transformedDrivingInsights);
    }

    @Test
    public void testAllSafetyScoresAreExposedToJs() {
        String transportId = "transport_id";
        DrivingInsights expectedDrivingInsights = createDummyDrivingInsights(transportId);

        when(mDrivingInsightsApi.getDrivingInsights(transportId)).thenReturn(expectedDrivingInsights);
        mModule.getDrivingInsights(transportId, mPromise);

        verify(mPromise).resolve(writableMapCaptor.capture());

        WritableMap transformedDrivingInsights = writableMapCaptor.getValue();
        JavaOnlyMap transformedSafetyScores = (JavaOnlyMap) transformedDrivingInsights.getMap(JS_KEY_SAFETY_SCORES);

        int totalAvailableSafetyScoreTypes = SafetyScoreType.values().length;
        int actualNbrOfExposedSafetyScoreTypes = transformedSafetyScores.toHashMap().size();
        assertEquals(totalAvailableSafetyScoreTypes, actualNbrOfExposedSafetyScoreTypes);
    }

    @Test
    public void testAddDrivingInsightsListener() {
        int subscriptionId = 1;
        mModule.addNativeListener(DRIVING_INSIGHTS_READY_EVENT, subscriptionId, null, mPromise);

        verify(mPromise).resolve(null);
        verify(mSentianceSubscriptionsManager)
            .addSubscription(stringCaptor.capture(), intCaptor.capture(), drivingInsightsReadyListenerCaptor.capture());

        assertEquals(DRIVING_INSIGHTS_READY_EVENT, stringCaptor.getValue());
        assertEquals(subscriptionId, intCaptor.getValue().intValue());

        DrivingInsightsReadyListener drivingInsightsReadyListener = drivingInsightsReadyListenerCaptor.getValue();
        DrivingInsights drivingInsights = createDummyDrivingInsights();
        drivingInsightsReadyListener.onDrivingInsightsReady(drivingInsights);

        verify(mDrivingInsightsEmitter).sendDrivingInsightsReadyEvent(drivingInsights);
    }

    @Test
    public void testRemoveDrivingInsightsListener() {
        int subscriptionId = 1;
        mModule.removeNativeListener(DRIVING_INSIGHTS_READY_EVENT, subscriptionId, mPromise);

        verify(mSentianceSubscriptionsManager)
            .removeSubscription(intCaptor.capture(), stringCaptor.capture());

        assertEquals(subscriptionId, intCaptor.getValue().intValue());
        assertEquals(DRIVING_INSIGHTS_READY_EVENT, stringCaptor.getValue());
    }

    @Test
    public void testGetAverageOverallSafetyScore() {
        float expectedScore = 0.76f;
        when(mDrivingInsightsApi.getAverageOverallSafetyScore(any()))
            .thenReturn(expectedScore);

        ReadableMap jsInput = JavaOnlyMap.of(
            JS_KEY_PERIOD, 30,
            JS_KEY_TRANSPORT_MODES, JavaOnlyArray.of(TransportMode.CAR.name(), TransportMode.WALKING.name()),
            JS_KEY_OCCUPANT_ROLES, JavaOnlyArray.of(OccupantRole.DRIVER.name())
        );

        mModule.getAverageOverallSafetyScore(jsInput, mPromise);

        ArgumentCaptor<SafetyScoreRequestParameters> captor = ArgumentCaptor.forClass(SafetyScoreRequestParameters.class);
        verify(mPromise).resolve(expectedScore);
        verify(mDrivingInsightsApi).getAverageOverallSafetyScore(captor.capture());

        SafetyScoreRequestParameters capturedParams = captor.getValue();
        assertEquals(SafetyScoreRequestParameters.Period.LAST_30_DAYS, capturedParams.getPeriod());
        assertEquals(Arrays.asList(TransportMode.CAR, TransportMode.WALKING), capturedParams.getTransportModes().getModes());
        assertEquals(Collections.singletonList(OccupantRole.DRIVER), capturedParams.getOccupantRoles().getRoles());
    }

    private Map<String, String> dummyTransportTags() {
        return new HashMap<String, String>() {{
            put("key1", "value1");
            put("key2", "value2");
        }};
    }

    private DrivingInsights createDummyDrivingInsights() {
        return createDummyDrivingInsights(null);
    }

    private DrivingInsights createDummyDrivingInsights(@Nullable String transportId) {
        long now = System.currentTimeMillis();
        return new DrivingInsights(
            new TransportEvent(
                transportId == null ? "transport_id" : transportId,
                DateTime.fromMillis(now),
                DateTime.fromMillis(now + 10000),
                DateTime.fromMillis(now + 10000),
                TransportMode.CAR,
                Collections.singletonList(
                    new Waypoint(13.14, 34.67, now, 20, 5.5f, 6.5f)
                ),
                500,
                dummyTransportTags()
            ),
            new SafetyScores.Builder()
                .setSmoothScore(.78f)
                .setFocusScore(.99f)
                .setCallWhileMovingScore(.55f)
                .setLegalScore(.88f)
                .setOverallScore(.44f)
                .createSafetyScores()
        );
    }

    private List<Waypoint> createDummyWaypoints() {
        long now = System.currentTimeMillis();

        Waypoint mockWaypoint = mock(Waypoint.class);
        when(mockWaypoint.isSpeedLimitInfoSet()).thenReturn(false);
        when(mockWaypoint.hasUnlimitedSpeedLimit()).thenReturn(true);

        return Arrays.asList(
            mockWaypoint,
            new Waypoint(14.14, 34.67, now, 22, 7.5f, 6.2f, true),
            new Waypoint(15.14, 34.67, now, -1, -1f, -1f, false)
        );
    }
}
