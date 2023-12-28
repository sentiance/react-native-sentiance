package com.sentiance.react.bridge.eventtimeline;

import android.content.Context;

import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.ondevice.api.event.Event;

public class EventTimelineEmitter extends AbstractSentianceEmitter {

  public static final String TIMELINE_UPDATE_EVENT = "SENTIANCE_TIMELINE_UPDATE_EVENT";
  private final OnDeviceTypesConverter onDeviceTypesConverter;

  public EventTimelineEmitter(Context context) {
    super(context);
    onDeviceTypesConverter = new OnDeviceTypesConverter();
  }

  public void sendTimelineUpdateEvent(Event event) {
    sendEvent(TIMELINE_UPDATE_EVENT, onDeviceTypesConverter.convertEvent(event));
  }
}
