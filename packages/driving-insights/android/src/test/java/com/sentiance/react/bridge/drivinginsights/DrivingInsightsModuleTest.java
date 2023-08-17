package com.sentiance.react.bridge.drivinginsights;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsEmitter.DRIVING_INSIGHTS_READY_EVENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.drivinginsights.util.DrivingInsightsJsPayloadValidator;
import com.sentiance.react.bridge.drivinginsights.util.HarshEventJsPayloadValidator;
import com.sentiance.react.bridge.drivinginsights.util.PhoneUsageEventJsPayloadValidator;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsApi;
import com.sentiance.sdk.drivinginsights.api.DrivingInsightsReadyListener;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.PhoneUsageEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.Waypoint;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.event.TransportMode;
import com.sentiance.sdk.util.DateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(RobolectricTestRunner.class)
public class DrivingInsightsModuleTest extends ReactNativeModuleTest<DrivingInsightsModule> {

  @Mock
  private DrivingInsightsApi mDrivingInsightsApi;
  @Mock
  private DrivingInsightsEmitter mDrivingInsightsEmitter;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Integer> intCaptor;
  @Captor
  private ArgumentCaptor<DrivingInsightsReadyListener> drivingInsightsReadyListenerCaptor;

  private HarshEventJsPayloadValidator harshEventJsPayloadValidator;
  private PhoneUsageEventJsPayloadValidator phoneUsageEventJsPayloadValidator;
  private DrivingInsightsJsPayloadValidator drivingInsightsJsPayloadValidator;

  @Override
  protected DrivingInsightsModule initModule() {
    return new DrivingInsightsModule(
      mReactApplicationContext, mSentiance, mDrivingInsightsApi, mDrivingInsightsEmitter,
      mSentianceSubscriptionsManager);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    harshEventJsPayloadValidator = new HarshEventJsPayloadValidator();
    phoneUsageEventJsPayloadValidator = new PhoneUsageEventJsPayloadValidator();
    drivingInsightsJsPayloadValidator = new DrivingInsightsJsPayloadValidator();
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
        0.55
      ),
      new HarshDrivingEvent(
        DateTime.fromMillis(System.currentTimeMillis()),
        DateTime.fromMillis(System.currentTimeMillis() + 2000),
        HarshDrivingEvent.Type.BRAKING,
        90,
        0.43
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
    String transportId = "transport_id";
    List<PhoneUsageEvent> expectedPhoneUsageEvents = Arrays.asList(
      new PhoneUsageEvent(
        DateTime.fromMillis(System.currentTimeMillis()),
        DateTime.fromMillis(System.currentTimeMillis() + 2000)
      ),
      new PhoneUsageEvent(
        DateTime.fromMillis(System.currentTimeMillis() + 2000),
        DateTime.fromMillis(System.currentTimeMillis() + 4000)
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
  public void testAddDrivingInsightsListener() {
    int subscriptionId = 1;
    mModule.addNativeListener(DRIVING_INSIGHTS_READY_EVENT, subscriptionId, mPromise);

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
        TransportMode.CAR,
        Collections.singletonList(
          new Waypoint(13.14, 34.67, now, 20)
        ),
        500
      ),
      new SafetyScores(.78f, .99f)
    );
  }
}
