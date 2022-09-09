package com.sentiance.react.bridge.crashdetection;

import static com.sentiance.react.bridge.crashdetection.SentianceCrashDetectionConverter.convertVehicleCrashEvent;
import static com.sentiance.react.bridge.crashdetection.SentianceCrashDetectionConverter.convertVehicleCrashDiagnostic;

import android.content.Context;

import com.sentiance.react.bridge.core.base.AbstractSentianceEmitter;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.crashdetection.api.VehicleCrashDiagnostic;

class CrashDetectionEmitter extends AbstractSentianceEmitter {

  private static final String VEHICLE_CRASH_EVENT = "SENTIANCE_VEHICLE_CRASH_EVENT";
  private static final String VEHICLE_CRASH_DIAGNOSTIC_EVENT = "SENTIANCE_VEHICLE_CRASH_DIAGNOSTIC_EVENT";

  public CrashDetectionEmitter(Context context) {
    super(context);
  }

  void sendVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    sendEvent(VEHICLE_CRASH_EVENT, convertVehicleCrashEvent(crashEvent));
  }

  void sendVehicleCrashDiagnosticEvent(VehicleCrashDiagnostic vehicleCrashDiagnostic){
    sendEvent(VEHICLE_CRASH_DIAGNOSTIC_EVENT, convertVehicleCrashDiagnostic(vehicleCrashDiagnostic));
  }
}


