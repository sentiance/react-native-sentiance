package com.sentiance.react.bridge.smartgeofences;

import android.content.Context;

import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.react.bridge.smartgeofences.converters.SmartGeofencesConverter;
import com.sentiance.smartgeofences.api.SmartGeofenceEvent;

public class SmartGeofenceEmitter extends AbstractSentianceEmitter {
    public static final String SMART_GEOFENCE_EVENT = "SENTIANCE_SMART_GEOFENCE_EVENT";
    private final SmartGeofencesConverter smartGeofencesConverter;

    protected SmartGeofenceEmitter(Context context, SmartGeofencesConverter smartGeofencesConverter) {
        super(context);
        this.smartGeofencesConverter = smartGeofencesConverter;
    }

    public void sendSmartGeofencesEvent(SmartGeofenceEvent event) {
        sendEvent(SMART_GEOFENCE_EVENT, smartGeofencesConverter.convertSmartGeofenceEvent(event));
    }
}
