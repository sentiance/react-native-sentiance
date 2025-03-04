package com.sentiance.react.bridge.drivinginsights.util.validators;

import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_CALL_WHILE_MOVING_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_FOCUS_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_HARSH_ACCELERATION_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_HARSH_BRAKING_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_HARSH_TURNING_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_LEGAL_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_OVERALL_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SAFETY_SCORES;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_SMOOTH_SCORE;
import static com.sentiance.react.bridge.drivinginsights.DrivingInsightsConverter.JS_KEY_TRANSPORT_EVENT;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_DISTANCE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_DURATION_SECONDS;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_END_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_ID;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_START_TIME_EPOCH;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_TRANSPORT_MODE;
import static com.sentiance.react.bridge.eventtimeline.converters.OnDeviceTypesConverter.JS_KEY_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.validators.BridgeValidator;
import com.sentiance.react.bridge.test.validators.WaypointsBridgeValidator;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;
import com.sentiance.sdk.drivinginsights.api.SafetyScores;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;

public class DrivingInsightsBridgeValidator implements BridgeValidator<DrivingInsights> {

  private WaypointsBridgeValidator waypointsValidator;

  public DrivingInsightsBridgeValidator() {
    waypointsValidator = new WaypointsBridgeValidator();
  }

  @Override
  public void validate(@NonNull DrivingInsights expected, @NonNull JavaOnlyMap actual) {
    TransportEvent transportEvent = expected.getTransportEvent();
    SafetyScores safetyScores = expected.getSafetyScores();

    validateTransportEvent(transportEvent, (JavaOnlyMap) actual.getMap(JS_KEY_TRANSPORT_EVENT));
    validateSafetyScores(safetyScores, (JavaOnlyMap) actual.getMap(JS_KEY_SAFETY_SCORES));
  }

  private void validateTransportEvent(TransportEvent transportEvent,
                                      JavaOnlyMap transformedTransportEvent) {
    assertEquals(
      transportEvent.getId(),
      transformedTransportEvent.getString(JS_KEY_ID));
    assertEquals(
      transportEvent.getStartTime().toString(),
      transformedTransportEvent.getString(JS_KEY_START_TIME));
    assertEquals(
      transportEvent.getStartTime().getEpochTime(),
      transformedTransportEvent.getDouble(JS_KEY_START_TIME_EPOCH), 0.001);

    assertEquals(
      transportEvent.getEndTime().toString(),
      transformedTransportEvent.getString(JS_KEY_END_TIME));
    assertEquals(
      transportEvent.getEndTime().getEpochTime(),
      transformedTransportEvent.getDouble(JS_KEY_END_TIME_EPOCH), 0.001);

    Long durationInSeconds = transportEvent.getDurationInSeconds();
    if (durationInSeconds == null) {
      assertFalse(transformedTransportEvent.hasKey(JS_KEY_DURATION_SECONDS));
    } else {
      assertEquals(
        durationInSeconds,
        transformedTransportEvent.getDouble(JS_KEY_DURATION_SECONDS), 0.001);
    }

    assertEquals(
      transportEvent.getEventType().toString(),
      transformedTransportEvent.getString(JS_KEY_TYPE));
    assertEquals(
      transportEvent.getTransportMode().toString(),
      transformedTransportEvent.getString(JS_KEY_TRANSPORT_MODE));

    Integer distanceInMeters = transportEvent.getDistanceInMeters();
    if (distanceInMeters == null) {
      assertFalse(transformedTransportEvent.hasKey(JS_KEY_DISTANCE));
    } else {
      assertEquals(
        distanceInMeters,
        transformedTransportEvent.getDouble(JS_KEY_DISTANCE), 0.001);
    }

    waypointsValidator.validate(transportEvent.getWaypoints(), transformedTransportEvent);
  }

  private void validateSafetyScores(@NonNull SafetyScores safetyScores, JavaOnlyMap transformedSafetyScores) {
    if (safetyScores.getSmoothScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_SMOOTH_SCORE));
    } else {
      assertEquals(safetyScores.getSmoothScore(), transformedSafetyScores.getDouble(JS_KEY_SMOOTH_SCORE), 0.0);
    }

    if (safetyScores.getFocusScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_FOCUS_SCORE));
    } else {
      assertEquals(safetyScores.getFocusScore(), transformedSafetyScores.getDouble(JS_KEY_FOCUS_SCORE), 0.0);
    }

    if (safetyScores.getCallWhileMovingScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_CALL_WHILE_MOVING_SCORE));
    } else {
      assertEquals(safetyScores.getCallWhileMovingScore(), transformedSafetyScores.getDouble(JS_KEY_CALL_WHILE_MOVING_SCORE), 0.0);
    }

    if (safetyScores.getLegalScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_LEGAL_SCORE));
    } else {
      assertEquals(safetyScores.getLegalScore(), transformedSafetyScores.getDouble(JS_KEY_LEGAL_SCORE), 0.0);
    }

    if (safetyScores.getOverallScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_OVERALL_SCORE));
    } else {
      assertEquals(safetyScores.getOverallScore(), transformedSafetyScores.getDouble(JS_KEY_OVERALL_SCORE), 0.0);
    }

    if (safetyScores.getHarshBrakingScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_HARSH_BRAKING_SCORE));
    } else {
      assertEquals(safetyScores.getHarshBrakingScore(), transformedSafetyScores.getDouble(JS_KEY_HARSH_BRAKING_SCORE), 0.0);
    }

    if (safetyScores.getHarshTurningScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_HARSH_TURNING_SCORE));
    } else {
      assertEquals(safetyScores.getHarshTurningScore(), transformedSafetyScores.getDouble(JS_KEY_HARSH_TURNING_SCORE), 0.0);
    }

    if (safetyScores.getHarshAccelerationScore() == null) {
      assertFalse(transformedSafetyScores.hasKey(JS_KEY_HARSH_ACCELERATION_SCORE));
    } else {
      assertEquals(safetyScores.getHarshAccelerationScore(), transformedSafetyScores.getDouble(JS_KEY_HARSH_ACCELERATION_SCORE), 0.0);
    }
  }
}
