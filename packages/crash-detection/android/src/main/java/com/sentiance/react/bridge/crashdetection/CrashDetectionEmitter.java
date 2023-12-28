package com.sentiance.react.bridge.crashdetection;

import android.content.Context;

import com.sentiance.react.bridge.core.common.base.AbstractSentianceEmitter;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.crashdetection.api.VehicleCrashDiagnostic;

class CrashDetectionEmitter extends AbstractSentianceEmitter {

  private static final String VEHICLE_CRASH_EVENT = "SENTIANCE_VEHICLE_CRASH_EVENT";
  private static final String VEHICLE_CRASH_DIAGNOSTIC_EVENT = "SENTIANCE_VEHICLE_CRASH_DIAGNOSTIC_EVENT";

  private final SentianceCrashDetectionConverter converter;

  public CrashDetectionEmitter(Context context) {
    super(context);
    converter = new SentianceCrashDetectionConverter();
  }

  void sendVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    sendEvent(VEHICLE_CRASH_EVENT, converter.convertVehicleCrashEvent(crashEvent));
  }

  void sendVehicleCrashDiagnosticEvent(VehicleCrashDiagnostic vehicleCrashDiagnostic){
    sendEvent(VEHICLE_CRASH_DIAGNOSTIC_EVENT, converter.convertVehicleCrashDiagnostic(vehicleCrashDiagnostic));
  }
}


