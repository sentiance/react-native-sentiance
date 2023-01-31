package com.sentiance.react.bridge.crashdetection;

import static com.sentiance.react.bridge.core.common.SentianceCommonConverter.convertLocation;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.crashdetection.api.VehicleCrashDiagnostic;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

public class SentianceCrashDetectionConverter {

  public static WritableMap convertVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    WritableMap map = Arguments.createMap();

    map.putDouble("time", (double) crashEvent.getTime());

    WritableMap locationMap = convertLocation(crashEvent.getLocation());
    map.putMap("location", locationMap);

    map.putDouble("magnitude", crashEvent.getMagnitude());
    map.putDouble("speedAtImpact", crashEvent.getSpeedAtImpact());
    map.putDouble("deltaV", crashEvent.getDeltaV());
    map.putInt("confidence", crashEvent.getConfidence());

    WritableArray precedingLocationsArray = Arguments.createArray();
    for (Location location : crashEvent.getPrecedingLocations()) {
      precedingLocationsArray.pushMap(convertLocation(location));
    }
    map.putArray("precedingLocations", precedingLocationsArray);

    return map;
  }

  public static WritableMap convertVehicleCrashDiagnostic(VehicleCrashDiagnostic vehicleCrashDiagnostic) {
    WritableMap map = Arguments.createMap();

    map.putString("crashDetectionState", vehicleCrashDiagnostic.getCrashDetectionState().name());
    map.putString("crashDetectionStateDescription", vehicleCrashDiagnostic.getCrashDetectionStateDescription());

    return map;
  }
}
