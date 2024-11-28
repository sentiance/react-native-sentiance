package com.sentiance.react.bridge.eventtimeline;

import static com.sentiance.react.bridge.eventtimeline.ErrorCodes.E_TRANSPORT_TAG_ERROR;
import static com.sentiance.react.bridge.eventtimeline.EventTimelineEmitter.TIMELINE_UPDATE_EVENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.react.bridge.test.ReactNativeModuleTest;
import com.sentiance.sdk.NoSentianceUserException;
import com.sentiance.sdk.SdkException;
import com.sentiance.sdk.eventtimeline.api.EventTimelineApi;
import com.sentiance.sdk.eventtimeline.api.EventTimelineUpdateListener;
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
    private ArgumentCaptor<List<Event>> eventsCaptor;
    @Captor
    private ArgumentCaptor<Date> dateCaptor;
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
    public void testGetTimelineUpdates() {
        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));
        when(eventTimelineApi.getTimelineUpdates(any())).thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        mModule.getTimelineUpdates(1000.0, mPromise);

        verify(commonConverter).convertEvents(eventsCaptor.capture());
        verify(mPromise).resolve(writableArrayCaptor.capture());

        WritableArray transformedEvents = writableArrayCaptor.getValue();
        assertEquals(mockWritableArray, transformedEvents);

        List<Event> capturedEvents = eventsCaptor.getValue();
        assertEquals(mockEvents, capturedEvents);
    }

    @Test
    public void testGetTimelineEvents() {
        List<Event> mockEvents = Arrays.asList(mock(Event.class), mock(Event.class));

        when(eventTimelineApi.getTimelineEvents(any(), any())).thenReturn(mockEvents);
        when(commonConverter.convertEvents(mockEvents)).thenReturn(mockWritableArray);

        double fromTimestamp = 1000.0;
        double toTimestamp = 10_000.0;
        mModule.getTimelineEvents(fromTimestamp, toTimestamp, mPromise);

        verify(eventTimelineApi).getTimelineEvents(dateCaptor.capture(), dateCaptor.capture());
        verify(commonConverter).convertEvents(eventsCaptor.capture());
        verify(mPromise).resolve(writableArrayCaptor.capture());

        List<Date> dates = dateCaptor.getAllValues();
        assertEquals(fromTimestamp, dates.get(0).getTime(), 0.000001);
        assertEquals(toTimestamp, dates.get(1).getTime(), 0.000001);

        WritableArray transformedEvents = writableArrayCaptor.getValue();
        assertEquals(mockWritableArray, transformedEvents);

        List<Event> capturedEvents = eventsCaptor.getValue();
        assertEquals(mockEvents, capturedEvents);
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
    public void testAddTimelineUpdateListener() {
        int subscriptionId = 1;
        mModule.addNativeListener(TIMELINE_UPDATE_EVENT, subscriptionId, mPromise);

        verify(mPromise).resolve(null);
        verify(mSentianceSubscriptionsManager)
            .addSubscription(stringCaptor.capture(), intCaptor.capture(), eventTimelineUpdateListenerCaptor.capture());

        assertEquals(TIMELINE_UPDATE_EVENT, stringCaptor.getValue());
        assertEquals(subscriptionId, intCaptor.getValue().intValue());

        EventTimelineUpdateListener listener = eventTimelineUpdateListenerCaptor.getValue();
        Event mockEvent = mock(Event.class);
        listener.onEventTimelineUpdated(mockEvent);

        verify(eventTimelineEmitter).sendTimelineUpdateEvent(mockEvent);
    }

    @Test
    public void testRemoveTimelineUpdateListener() {
        int subscriptionId = 1;
        mModule.removeNativeListener(TIMELINE_UPDATE_EVENT, subscriptionId, mPromise);

        verify(mSentianceSubscriptionsManager)
            .removeSubscription(intCaptor.capture(), stringCaptor.capture());

        assertEquals(TIMELINE_UPDATE_EVENT, stringCaptor.getValue());
        assertEquals(subscriptionId, intCaptor.getValue().intValue());
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
}
