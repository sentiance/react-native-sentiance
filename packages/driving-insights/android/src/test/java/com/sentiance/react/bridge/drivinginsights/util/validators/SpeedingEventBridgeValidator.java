package com.sentiance.react.bridge.drivinginsights.util.validators;

import com.facebook.react.bridge.JavaOnlyMap;
import com.sentiance.react.bridge.test.validators.WaypointsBridgeValidator;
import com.sentiance.sdk.drivinginsights.api.SpeedingEvent;

public class SpeedingEventBridgeValidator extends DrivingEventBridgeValidator<SpeedingEvent> {

  private WaypointsBridgeValidator waypointsValidator;

  public SpeedingEventBridgeValidator() {
    waypointsValidator = new WaypointsBridgeValidator();
  }

  @Override
  public void validate(SpeedingEvent expected, JavaOnlyMap actual) {
    super.validate(expected, actual);
    waypointsValidator.validate(expected.getWaypoints(), actual);
  }
}
