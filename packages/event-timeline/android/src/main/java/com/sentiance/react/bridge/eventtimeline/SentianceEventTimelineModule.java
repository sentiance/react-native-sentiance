package com.sentiance.react.bridge.eventtimeline;

import static com.sentiance.react.bridge.eventtimeline.ErrorCodes.E_TRANSPORT_TAG_ERROR;
import static com.sentiance.react.bridge.eventtimeline.EventTimelineEmitter.TIMELINE_UPDATE_EVENT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.sentiance.react.bridge.core.common.SentianceSubscriptionsManager;
import com.sentiance.react.bridge.core.common.base.AbstractSentianceModule;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.NoSentianceUserException;
import com.sentiance.sdk.Sentiance;
import com.sentiance.sdk.eventtimeline.api.EventTimelineApi;
import com.sentiance.sdk.eventtimeline.api.EventTimelineUpdateListener;
import com.sentiance.sdk.ondevice.api.event.Event;

import java.util.Date;
import java.util.List;

public class SentianceEventTimelineModule extends AbstractSentianceModule {
    static final String NATIVE_MODULE_NAME = "SentianceEventTimeline";
    static final String JS_PAYLOAD_KEY_INCLUDE_PROVISIONAL_EVENTS = "includeProvisionalEvents";

    private final EventTimelineApi mEventTimelineApi;
    private final EventTimelineEmitter mEmitter;
    private final OnDeviceTypesConverter onDeviceTypesConverter;

    public SentianceEventTimelineModule(ReactApplicationContext reactApplicationContext,
                                        Sentiance sentiance,
                                        SentianceSubscriptionsManager subscriptionsManager,
                                        EventTimelineApi eventTimelineApi,
                                        EventTimelineEmitter emitter,
                                        OnDeviceTypesConverter converter) {
        super(reactApplicationContext, sentiance, subscriptionsManager);
        mEventTimelineApi = eventTimelineApi;
        mEmitter = emitter;
        onDeviceTypesConverter = converter;
    }

    @Override
    protected void addSupportedEventSubscriptions(SentianceSubscriptionsManager subscriptionsManager) {
        mSubscriptionsManager.addSupportedSubscription(
            TIMELINE_UPDATE_EVENT,
            mEventTimelineApi::setProvisionalAwareTimelineUpdateListener,
            listener -> mEventTimelineApi.setTimelineUpdateListener(null),
            SentianceSubscriptionsManager.SubscriptionType.SINGLE
        );
    }

    @Override
    @ReactMethod
    protected void addNativeListener(String eventName, int subscriptionId, @Nullable ReadableMap payload, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        switch (eventName) {
            case TIMELINE_UPDATE_EVENT:
                mSubscriptionsManager.addSubscription(eventName, subscriptionId, (EventTimelineUpdateListener) mEmitter::sendTimelineUpdateEvent);
                break;
        }
        promise.resolve(null);
    }

    @Override
    @ReactMethod
    protected void removeNativeListener(String eventName, int subscriptionId, Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        mSubscriptionsManager.removeSubscription(subscriptionId, eventName);
        promise.resolve(null);
    }

    @Override
    @ReactMethod
    protected void addListener(String eventName) {

    }

    @Override
    @ReactMethod
    protected void removeListeners(Integer count) {

    }

    @NonNull
    @Override
    public String getName() {
        return NATIVE_MODULE_NAME;
    }

    @ReactMethod
    public void getTimelineUpdates(final Double afterEpochTimeMs,
                                   final boolean includeProvisionalEvents,
                                   final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        Date afterDate = new Date(afterEpochTimeMs.longValue());
        List<Event> events;
        if (includeProvisionalEvents) {
            events = mEventTimelineApi.getTimelineUpdatesIncludingProvisionalEvents(afterDate);
        } else {
            events = mEventTimelineApi.getTimelineUpdates(afterDate);
        }
        promise.resolve(onDeviceTypesConverter.convertEvents(events));
    }

    @ReactMethod
    public void getTimelineEvents(final Double fromEpochTimeMs,
                                  final Double toEpochTimeMs,
                                  final boolean includeProvisionalEvents,
                                  final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        Date fromDate = new Date(fromEpochTimeMs.longValue());
        Date toDate = new Date(toEpochTimeMs.longValue());
        List<Event> events;
        if (includeProvisionalEvents) {
            events = mEventTimelineApi.getTimelineEventsIncludingProvisionalOnes(fromDate, toDate);
        } else {
            events = mEventTimelineApi.getTimelineEvents(fromDate, toDate);
        }
        promise.resolve(onDeviceTypesConverter.convertEvents(events));
    }

    @ReactMethod
    public void getTimelineEvent(final String eventId, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        Event event = mEventTimelineApi.getTimelineEvent(eventId);
        promise.resolve(event == null ? null : onDeviceTypesConverter.convertEvent(event));
    }

    @ReactMethod
    public void setTransportTags(ReadableMap tags, final Promise promise) {
        if (rejectIfNotInitialized(promise)) {
            return;
        }

        try {
            mEventTimelineApi.setTransportTags(onDeviceTypesConverter.convertTransportTags(tags));
            promise.resolve(null);
        } catch (IllegalArgumentException | NoSentianceUserException e) {
            promise.reject(E_TRANSPORT_TAG_ERROR, e.getMessage());
        } catch (Exception e) {
            promise.reject(e);
        }
    }
}
