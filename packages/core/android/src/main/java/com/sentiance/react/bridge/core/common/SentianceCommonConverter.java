package com.sentiance.react.bridge.core.common;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class SentianceCommonConverter {

  public static WritableMap convertLocation(Location location) {
    WritableMap locationMap = Arguments.createMap();
    locationMap.putDouble("timestamp", location.getTime());
    locationMap.putDouble("latitude", location.getLatitude());
    locationMap.putDouble("longitude", location.getLongitude());
    if (location.hasAccuracy()) {
      locationMap.putDouble("accuracy", location.getAccuracy());
    }
    if (location.hasAltitude()) {
      locationMap.putDouble("altitude", location.getAltitude());
    }
    locationMap.putString("provider", location.getProvider());
    return locationMap;
  }
}
