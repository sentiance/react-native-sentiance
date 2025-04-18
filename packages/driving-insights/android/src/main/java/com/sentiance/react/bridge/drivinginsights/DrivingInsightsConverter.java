package com.sentiance.react.bridge.drivinginsights;

import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_WAYPOINTS;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter;
import com.sentiance.sdk.drivinginsights.api.CallWhileMovingEvent;
import com.sentiance.sdk.drivinginsights.api.DrivingEvent;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.HarshDrivingEvent;
import com.sentiance.sdk.drivinginsights.api.PhoneUsageEvent;
import com.sentiance.sdk.drivinginsights.api.SafetyScoreRequestParameters;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.drivinginsights.api.SpeedingEvent;
import com.sentiance.sdk.ondevice.api.event.OccupantRole;
import com.sentiance.sdk.ondevice.api.event.TransportMode;

import java.util.Objects;

import javax.annotation.Nonnull;

public class DrivingInsightsConverter {

    public static final String JS_KEY_MAGNITUDE = "magnitude";
    public static final String JS_KEY_CONFIDENCE = "confidence";
    public static final String JS_KEY_TYPE = "type";
    public static final String JS_KEY_FOCUS_SCORE = "focusScore";
    public static final String JS_KEY_SMOOTH_SCORE = "smoothScore";
    public static final String JS_KEY_LEGAL_SCORE = "legalScore";
    public static final String JS_KEY_CALL_WHILE_MOVING_SCORE = "callWhileMovingScore";
    public static final String JS_KEY_OVERALL_SCORE = "overallScore";
    public static final String JS_KEY_HARSH_BRAKING_SCORE = "harshBrakingScore";
    public static final String JS_KEY_HARSH_TURNING_SCORE = "harshTurningScore";
    public static final String JS_KEY_HARSH_ACCELERATION_SCORE = "harshAccelerationScore";
    public static final String JS_KEY_SAFETY_SCORES = "safetyScores";
    public static final String JS_KEY_TRANSPORT_EVENT = "transportEvent";
    public static final String JS_KEY_MAX_TRAVELLED_SPEED_MPS = "maxTravelledSpeedInMps";
    public static final String JS_KEY_MIN_TRAVELLED_SPEED_MPS = "minTravelledSpeedInMps";
    public static final String JS_KEY_PERIOD = "period";
    public static final String JS_KEY_TRANSPORT_MODES = "transportModes";
    public static final String JS_KEY_OCCUPANT_ROLES = "occupantRoles";

    private final OnDeviceTypesConverter onDeviceTypesConverter;

    public DrivingInsightsConverter() {
        onDeviceTypesConverter = new OnDeviceTypesConverter();
    }

    public SafetyScoreRequestParameters convertToSafetyScoreRequestParameters(ReadableMap params) {
        int periodInDays = params.getInt(JS_KEY_PERIOD);
        ReadableArray transportModesArray = params.getArray(JS_KEY_TRANSPORT_MODES);
        ReadableArray occupantRolesArray = params.getArray(JS_KEY_OCCUPANT_ROLES);

        Objects.requireNonNull(transportModesArray);
        Objects.requireNonNull(occupantRolesArray);

        return new SafetyScoreRequestParameters
            .Builder(intToPeriod(periodInDays))
            .setTransportModes(arrayToTransportModes(transportModesArray))
            .setOccupantRoles(arrayToOccupantRoles(occupantRolesArray))
            .build();
    }

    public WritableMap convertHarshDrivingEvent(HarshDrivingEvent event) {
        WritableMap map = convertDrivingEvent(event);
        map.putDouble(JS_KEY_MAGNITUDE, event.getMagnitude());
        map.putInt(JS_KEY_CONFIDENCE, event.getConfidence());
        map.putString(JS_KEY_TYPE, event.getType().name());
        return map;
    }

    public WritableMap convertPhoneUsageEvent(PhoneUsageEvent event) {
        return convertDrivingEvent(event);
    }

    public WritableMap convertCallWhileMovingEvent(CallWhileMovingEvent event) {
        WritableMap map = convertDrivingEvent(event);

        if (event.getMinTraveledSpeedInMps() != null) {
            map.putDouble(JS_KEY_MIN_TRAVELLED_SPEED_MPS, event.getMinTraveledSpeedInMps());
        }

        if (event.getMaxTraveledSpeedInMps() != null) {
            map.putDouble(JS_KEY_MAX_TRAVELLED_SPEED_MPS, event.getMaxTraveledSpeedInMps());
        }

        return map;
    }

    public WritableMap convertSpeedingEvent(SpeedingEvent event) {
        return convertDrivingEvent(event);
    }

    private @Nonnull SafetyScoreRequestParameters.Period intToPeriod(int value) {
        switch (value) {
            case 7:
                return SafetyScoreRequestParameters.Period.LAST_7_DAYS;
            case 14:
                return SafetyScoreRequestParameters.Period.LAST_14_DAYS;
            case 30:
                return SafetyScoreRequestParameters.Period.LAST_30_DAYS;
            default:
                // this should not happen, as input validation already happens on the JS side
                throw new IllegalArgumentException("Unexpected period value: " + value);
        }
    }

    private @Nonnull SafetyScoreRequestParameters.TransportModes arrayToTransportModes(
        @Nonnull ReadableArray transportModeValues) {
        int count = transportModeValues.size();
        TransportMode[] convertedTransportModes = new TransportMode[count];
        for (int i = 0; i < count; i++) {
            String transportMode = transportModeValues.getString(i);
            convertedTransportModes[i] = TransportMode.valueOf(transportMode);
        }
        return SafetyScoreRequestParameters.TransportModes.some(convertedTransportModes);
    }

    private @Nonnull SafetyScoreRequestParameters.OccupantRoles arrayToOccupantRoles(
        @Nonnull ReadableArray occupantRoleValues) {
        int count = occupantRoleValues.size();
        OccupantRole[] convertedOccupantRoles = new OccupantRole[count];
        for (int i = 0; i < count; i++) {
            String occupantRole = occupantRoleValues.getString(i);
            convertedOccupantRoles[i] = OccupantRole.valueOf(occupantRole);
        }
        return SafetyScoreRequestParameters.OccupantRoles.some(convertedOccupantRoles);
    }

    private WritableMap convertDrivingEvent(DrivingEvent event) {
        WritableMap map = Arguments.createMap();

        map.putString(JS_KEY_START_TIME, event.getStartTime().toString());
        map.putDouble(JS_KEY_START_TIME_EPOCH, event.getStartTime().getEpochTime());
        map.putString(JS_KEY_END_TIME, event.getEndTime().toString());
        map.putDouble(JS_KEY_END_TIME_EPOCH, event.getEndTime().getEpochTime());
        map.putArray(JS_KEY_WAYPOINTS, onDeviceTypesConverter.convertWaypoints(event.getWaypoints()));

        return map;
    }

    public WritableMap convertDrivingInsights(DrivingInsights drivingInsights) {
        WritableMap map = Arguments.createMap();

        map.putMap(JS_KEY_TRANSPORT_EVENT, onDeviceTypesConverter.convertEvent(drivingInsights.getTransportEvent()));
        map.putMap(JS_KEY_SAFETY_SCORES, convertSafetyScores(drivingInsights.getSafetyScores()));

        return map;
    }

    private WritableMap convertSafetyScores(SafetyScores safetyScores) {
        WritableMap map = Arguments.createMap();

        Float smoothScore = safetyScores.getSmoothScore();
        if (smoothScore != null) {
            map.putDouble(JS_KEY_SMOOTH_SCORE, smoothScore);
        }

        Float focusScore = safetyScores.getFocusScore();
        if (focusScore != null) {
            map.putDouble(JS_KEY_FOCUS_SCORE, focusScore);
        }

        Float callWhileMovingScore = safetyScores.getCallWhileMovingScore();
        if (callWhileMovingScore != null) {
            map.putDouble(JS_KEY_CALL_WHILE_MOVING_SCORE, callWhileMovingScore);
        }

        Float legalScore = safetyScores.getLegalScore();
        if (legalScore != null) {
            map.putDouble(JS_KEY_LEGAL_SCORE, legalScore);
        }

        Float overallScore = safetyScores.getOverallScore();
        if (overallScore != null) {
            map.putDouble(JS_KEY_OVERALL_SCORE, overallScore);
        }

        Float harshBrakingScore = safetyScores.getHarshBrakingScore();
        if (harshBrakingScore != null) {
            map.putDouble(JS_KEY_HARSH_BRAKING_SCORE, harshBrakingScore);
        }

        Float harshTurningScore = safetyScores.getHarshTurningScore();
        if (harshTurningScore != null) {
            map.putDouble(JS_KEY_HARSH_TURNING_SCORE, harshTurningScore);
        }

        Float harshAccelerationScore = safetyScores.getHarshAccelerationScore();
        if (harshAccelerationScore != null) {
            map.putDouble(JS_KEY_HARSH_ACCELERATION_SCORE, harshAccelerationScore);
        }

        return map;
    }
}
