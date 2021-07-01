package com.sentiance.react.bridge.crashdetection;

import static com.sentiance.react.bridge.crashdetection.SentianceCrashDetectionConverter.convertVehicleCrashEvent;

import android.content.Context;

import com.sentiance.react.bridge.core.base.AbstractSentianceEmitter;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

class CrashDetectionEmitter extends AbstractSentianceEmitter {

  private static final String VEHICLE_CRASH_EVENT = "SENTIANCE_VEHICLE_CRASH_EVENT";

  public CrashDetectionEmitter(Context context) {
    super(context);
  }

  void sendVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    sendEvent(VEHICLE_CRASH_EVENT, convertVehicleCrashEvent(crashEvent));
  }
}


