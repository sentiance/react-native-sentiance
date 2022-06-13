package com.sentiance.react.bridge.crashdetection;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;

public class SentianceCrashDetectionConverter {

  public static WritableMap convertLocation(Location location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putString("latitude", String.valueOf(location.getLatitude()));
    locationMap.putString("longitude", String.valueOf(location.getLongitude()));
    locationMap.putString("accuracy", String.valueOf(location.getAccuracy()));
    locationMap.putString("altitude", String.valueOf(location.getAltitude()));
    locationMap.putString("provider", location.getProvider());

    return locationMap;
  }

  public static WritableMap convertVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    WritableMap map = Arguments.createMap();

    map.putDouble("time", (double) crashEvent.getTime());
      
    WritableMap locationMap = convertLocation(crashEvent.getLocation());
    map.putMap("location", locationMap);

    map.putDouble("magnitude", crashEvent.getMagnitude());
    map.putDouble("speedAtImpact", crashEvent.getSpeedAtImpact());
    map.putDouble("deltaV", crashEvent.getDeltaV());
    map.putInt("confidence", crashEvent.getConfidence());

    return map;
  }
}
