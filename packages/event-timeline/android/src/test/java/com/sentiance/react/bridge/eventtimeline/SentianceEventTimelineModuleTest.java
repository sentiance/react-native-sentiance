package com.sentiance.react.bridge.eventtimeline;

import static com.sentiance.react.bridge.eventtimeline.ErrorCodes.E_TRANSPORT_TAG_ERROR;
import static com.sentiance.react.bridge.eventtimeline.EventTimelineEmitter.TIMELINE_UPDATE_EVENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.util.SingleParamRunnable;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.react.bridge.test.ReactNativeModuleTest;
import com.sentiance.sdk.NoSentianceUserException;
import com.sentiance.sdk.SdkException;
import com.sentiance.sdk.eventtimeline.api.EventTimelineApi;
import com.sentiance.sdk.eventtimeline.api.EventTimelineUpdateListener;
import com.sentiance.sdk.feedback.api.FeedbackApi;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedback;
import com.sentiance.sdk.feedback.api.OccupantRoleFeedbackResult;
import com.sentiance.sdk.ondevice.api.event.Event;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SentianceEventTimelineModuleTest extends ReactNativeModuleTest<SentianceEventTimelineModule> {

    @Mock
    private EventTimelineApi eventTimelineApi;
    @Mock
    private EventTimelineEmitter eventTimelineEmitter;
    @Mock
    private OnDeviceTypesConverter commonConverter;
    @Captor
    private ArgumentCaptor<SingleParamRunnable> singleParamRunnableCaptor;
    @Captor
    private ArgumentCaptor<EventTimelineUpdateListener> eventTimelineUpdateListenerCaptor;

    @Override
    protected SentianceEventTimelineModule initModule() {
        return new SentianceEventTimelineModule(
            mReactApplicationContext, mSentiance, mSentianceSubscriptionsManager,
            eventTimelineApi, eventTimelineEmitter, commonConverter
        );
    }

    @Test
    public void testGetTimelineUpdates_includeProvisionalEvents() {
        Double afterTimestamp = 1000.0;
        Date afterDate = new Date(afterTimestamp.longValue());

        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));
        when(eventTimelineApi.getTimelineUpdatesIncludingProvisionalEvents(eq(afterDate)))
            .thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        mModule.getTimelineUpdates(afterTimestamp, true, mPromise);

        verify(mPromise).resolve(eq(mockWritableArray));
    }

    @Test
    public void testGetTimelineUpdates_notIncludingProvisionalEvents() {
        Double afterTimestamp = 1000.0;
        Date afterDate = new Date(afterTimestamp.longValue());

        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));
        when(eventTimelineApi.getTimelineUpdates(eq(afterDate)))
            .thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        mModule.getTimelineUpdates(afterTimestamp, false, mPromise);

        verify(mPromise).resolve(eq(mockWritableArray));
    }

    @Test
    public void testGetTimelineEvents_includeProvisionalEvents() {
        Double fromTimestamp = 1000.0;
        Date fromDate = new Date(fromTimestamp.longValue());
        Double toTimestamp = 2000.0;
        Date toDate = new Date(toTimestamp.longValue());

        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));

        when(eventTimelineApi.getTimelineEventsIncludingProvisionalOnes(eq(fromDate), eq(toDate)))
            .thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        mModule.getTimelineEvents(fromTimestamp, toTimestamp, true, mPromise);

        verify(mPromise).resolve(eq(mockWritableArray));
    }

    @Test
    public void testGetTimelineEvents_notIncludingProvisionalEvents() {
        Double fromTimestamp = 1000.0;
        Date fromDate = new Date(fromTimestamp.longValue());
        Double toTimestamp = 2000.0;
        Date toDate = new Date(toTimestamp.longValue());

        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));

        when(eventTimelineApi.getTimelineEvents(eq(fromDate), eq(toDate)))
            .thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        mModule.getTimelineEvents(fromTimestamp, toTimestamp, false, mPromise);

        verify(mPromise).resolve(eq(mockWritableArray));
    }

    @Test
    public void testGetTimelineEvent() {
        String expectedEventId = "event_id";
        Event mockEvent = mock(Event.class);

        when(eventTimelineApi.getTimelineEvent(expectedEventId)).thenReturn(mockEvent);
        when(commonConverter.convertEvent(mockEvent)).thenReturn(mockWritableMap);

        mModule.getTimelineEvent(expectedEventId, mPromise);

        verify(eventTimelineApi).getTimelineEvent(expectedEventId);
        verify(commonConverter).convertEvent(mockEvent);
        verify(mPromise).resolve(mockWritableMap);
    }

    @Test
    public void addTimelineUpdateListener_includeProvisionalEvents() {
        int subscriptionId = 1;
        WritableMap payload = Arguments.createMap();
        payload.putBoolean(SentianceEventTimelineModule.JS_PAYLOAD_KEY_INCLUDE_PROVISIONAL_EVENTS, true);
        mModule.addNativeListener(TIMELINE_UPDATE_EVENT, subscriptionId, payload, mPromise);

        verify(mPromise).resolve(null);

        verify(mSentianceSubscriptionsManager)
            .addSubscription((eq(TIMELINE_UPDATE_EVENT)), eq(subscriptionId), eventTimelineUpdateListenerCaptor.capture());
    }

    @Test
    public void addTimelineUpdateListener_notIncludingProvisionalEvents() {
        int subscriptionId = 1;
        WritableMap payload = Arguments.createMap();
        payload.putBoolean(SentianceEventTimelineModule.JS_PAYLOAD_KEY_INCLUDE_PROVISIONAL_EVENTS, false);
        mModule.addNativeListener(TIMELINE_UPDATE_EVENT, subscriptionId, payload, mPromise);

        verify(mPromise).resolve(null);

        verify(mSentianceSubscriptionsManager)
            .addSubscription((eq(TIMELINE_UPDATE_EVENT)), eq(subscriptionId), eventTimelineUpdateListenerCaptor.capture());
    }

    @Test
    public void testRemoveTimelineUpdateListener() {
        int subscriptionId = 1;
        mModule.removeNativeListener(TIMELINE_UPDATE_EVENT, subscriptionId, mPromise);

        verify(mSentianceSubscriptionsManager)
            .removeSubscription(eq(subscriptionId), eq(TIMELINE_UPDATE_EVENT));
    }

    @Test
    public void testSetTransportTags() {
        ReadableMap tags = mock(ReadableMap.class);
        Map<String, String> convertedTags = mock(Map.class);

        when(commonConverter.convertTransportTags(tags)).thenReturn(convertedTags);

        mModule.setTransportTags(tags, mPromise);

        verify(eventTimelineApi).setTransportTags(convertedTags);
        verify(mPromise).resolve(null);
    }

    @Test
    public void testSetTransportTagsThrowsCustomErrorIfAnIllegalArgumentExceptionIsRaised() {
        String exceptionMessage = "failed to set transport tags";
        doThrow(new IllegalArgumentException(exceptionMessage))
            .when(eventTimelineApi)
            .setTransportTags(any());

        mModule.setTransportTags(mock(ReadableMap.class), mPromise);

        verify(mPromise).reject(eq(E_TRANSPORT_TAG_ERROR), eq(exceptionMessage));
    }

    @Test
    public void testSetTransportTagsThrowsCustomErrorIfNoSentianceUserExceptionIsRaised() {
        NoSentianceUserException exception = new NoSentianceUserException();
        doThrow(exception)
            .when(eventTimelineApi)
            .setTransportTags(any());

        mModule.setTransportTags(mock(ReadableMap.class), mPromise);

        verify(mPromise).reject(eq(E_TRANSPORT_TAG_ERROR), eq(exception.getMessage()));
    }

    @Test
    public void testSetTransportTagsThrowsGeneralError() {
        SdkException ex = new SdkException("failed to set transport tags");
        doThrow(ex)
            .when(eventTimelineApi)
            .setTransportTags(any());

        mModule.setTransportTags(mock(ReadableMap.class), mPromise);

        verify(mPromise).reject(eq(ex));
    }

    @Test
    public void testSubmitOccupantRoleFeedback() {
        for (OccupantRoleFeedback feedback : OccupantRoleFeedback.values()) {
            testFeedbackForAllResults(feedback);
        }
    }

    private void testFeedbackForAllResults(OccupantRoleFeedback feedback) {
        for (OccupantRoleFeedbackResult feedbackResultExpected : OccupantRoleFeedbackResult.values()) {
            FeedbackApi feedbackApi = mock(FeedbackApi.class);
            SentianceFeedbackModule feedbackModule = createFeedBackModule(feedbackApi);

            when(feedbackApi.submitOccupantRoleFeedback(any(), any())).thenReturn(feedbackResultExpected);
            when(commonConverter.getOccupantRoleFeedbackFrom(anyString())).thenReturn(feedback);

            feedbackModule.submitOccupantRoleFeedback("1000", feedback.name(), mPromise);

            ArgumentCaptor<String> feedbackResultStringCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> feedbackRoleStringCaptor = ArgumentCaptor.forClass(String.class);

            verify(commonConverter).getOccupantRoleFeedbackFrom(feedbackRoleStringCaptor.capture());
            assertEquals(feedback.name(), feedbackRoleStringCaptor.getValue());

            verify(mPromise).resolve(feedbackResultStringCaptor.capture());
            assertEquals(feedbackResultExpected.name(), feedbackResultStringCaptor.getValue());

            reset(commonConverter, mPromise);
        }
    }

    private SentianceFeedbackModule createFeedBackModule(FeedbackApi feedbackApi) {
        return new SentianceFeedbackModule(
            mock(ReactApplicationContext.class),
            mSentiance,
            feedbackApi,
            mSentianceSubscriptionsManager,
            commonConverter
        );
    }
}
