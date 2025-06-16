package com.sentiance.react.bridge.crashdetection;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.core.SentianceConverter;
import com.sentiance.sdk.crashdetection.api.VehicleCrashDiagnostic;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

public class SentianceCrashDetectionConverter {

    public static final String JS_KEY_TIME = "time";
    public static final String JS_KEY_DELTA_V = "deltaV";
    public static final String JS_KEY_LOCATION = "location";
    public static final String JS_KEY_MAGNITUDE = "magnitude";
    public static final String JS_KEY_CONFIDENCE = "confidence";
    public static final String JS_KEY_CRASH_SEVERITY = "severity";
    public static final String JS_KEY_DETECTOR_MODE = "detectorMode";
    public static final String JS_KEY_SPEED_AT_IMPACT = "speedAtImpact";
    public static final String JS_KEY_PRECEDING_LOCATIONS = "precedingLocations";
    public static final String JS_KEY_CRASH_DETECTION_STATE = "crashDetectionState";
    public static final String JS_KEY_CRASH_DETECTION_STATE_DESC = "crashDetectionStateDescription";

    private final SentianceConverter coreConverter;

    public SentianceCrashDetectionConverter() {
        coreConverter = new SentianceConverter();
    }

    public WritableMap convertVehicleCrashEvent(VehicleCrashEvent crashEvent) {
        WritableMap map = Arguments.createMap();

        map.putDouble(JS_KEY_TIME, (double) crashEvent.getTime());

        WritableMap locationMap = coreConverter.convertLocation(crashEvent.getLocation());
        map.putMap(JS_KEY_LOCATION, locationMap);

        map.putDouble(JS_KEY_MAGNITUDE, crashEvent.getMagnitude());
        map.putDouble(JS_KEY_SPEED_AT_IMPACT, crashEvent.getSpeedAtImpact());
        map.putDouble(JS_KEY_DELTA_V, crashEvent.getDeltaV());
        map.putInt(JS_KEY_CONFIDENCE, crashEvent.getConfidence());

        WritableArray precedingLocationsArray = Arguments.createArray();
        for (Location location : crashEvent.getPrecedingLocations()) {
            precedingLocationsArray.pushMap(coreConverter.convertLocation(location));
        }
        map.putArray(JS_KEY_PRECEDING_LOCATIONS, precedingLocationsArray);
        map.putString(JS_KEY_CRASH_SEVERITY, crashEvent.getSeverity().name());
        map.putString(JS_KEY_DETECTOR_MODE, crashEvent.getDetectorMode().name());

        return map;
    }

    public WritableMap convertVehicleCrashDiagnostic(VehicleCrashDiagnostic vehicleCrashDiagnostic) {
        WritableMap map = Arguments.createMap();

        map.putString(JS_KEY_CRASH_DETECTION_STATE, vehicleCrashDiagnostic.getCrashDetectionState().name());
        map.putString(JS_KEY_CRASH_DETECTION_STATE_DESC, vehicleCrashDiagnostic.getCrashDetectionStateDescription());

        return map;
    }
}
