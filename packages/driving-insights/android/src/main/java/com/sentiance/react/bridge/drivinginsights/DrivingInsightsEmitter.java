package com.sentiance.react.bridge.drivinginsights;

import android.content.Context;

import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.sdk.drivinginsights.api.DrivingInsights;

public class DrivingInsightsEmitter extends AbstractSentianceEmitter {

  public static final String DRIVING_INSIGHTS_READY_EVENT = "SENTIANCE_DRIVING_INSIGHTS_READY_EVENT";
  private final DrivingInsightsConverter converter;

  public DrivingInsightsEmitter(Context context) {
    super(context);
    converter = new DrivingInsightsConverter();
  }

  public void sendDrivingInsightsReadyEvent(DrivingInsights drivingInsights) {
    sendEvent(DRIVING_INSIGHTS_READY_EVENT, converter.convertDrivingInsights(drivingInsights));
  }
}
