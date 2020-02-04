package com.sentiance.react.bridge;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityType;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;

import java.util.HashMap;
import java.util.Map;

public class RNSentianceConverter {

  public static Map<String, String> convertReadableMapToMap(ReadableMap inputMap) {
    Map<String, String> map = new HashMap<String, String>();
    ReadableMapKeySetIterator iterator = inputMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      try {
        map.put(key, String.valueOf(inputMap.getString(key)));
      } catch (Exception ignored) {

      }
    }
    return map;
  }

  public static TripType toTripType(final String type) {
    if (type.equals("sdk")) {
      return TripType.SDK_TRIP;
    } else if (type.equals("external")) {
      return TripType.EXTERNAL_TRIP;
    } else {
      return TripType.ANY;
    }
  }

  public static TransportMode toTransportMode(int t) {
    switch (t) {
      case 2:
        return TransportMode.CAR;
      case 3:
        return TransportMode.BICYCLE;
      case 4:
        return TransportMode.ON_FOOT;
      case 5:
        return TransportMode.TRAIN;
      case 6:
        return TransportMode.TRAM;
      case 7:
        return TransportMode.BUS;
      case 8:
        return TransportMode.PLANE;
      case 9:
        return TransportMode.BOAT;
      case 10:
        return TransportMode.METRO;
      case 11:
        return TransportMode.RUNNING;
      default:
        return null;
    }
  }

  public static String convertInitState(InitState initState) {
    switch (initState) {
      case NOT_INITIALIZED:
        return "NOT_INITIALIZED";
      case INIT_IN_PROGRESS:
        return "INIT_IN_PROGRESS";
      case INITIALIZED:
        return "INITIALIZED";
      case RESETTING:
        return "RESETTING";
      default:
        return "NOT_INITIALIZED";
    }
  }

  public static WritableMap convertToken(Token token) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("tokenId", token.getTokenId());
      map.putString("expiryDate", String.valueOf(token.getExpiryDate()));
    } catch (Exception ignored) {
    }

    return map;
  }

  public static WritableMap convertInstallId(String installId) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("installId", installId);
    } catch (Exception ignored) {
    }
    return map;
  }

  public static WritableMap convertSdkStatus(SdkStatus status) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("startStatus", status.startStatus.name());
      map.putBoolean("canDetect", status.canDetect);
      map.putBoolean("isRemoteEnabled", status.isRemoteEnabled);
      map.putBoolean("isLocationPermGranted", status.isLocationPermGranted);
      map.putString("locationSetting", status.locationSetting.name());
      map.putBoolean("isAccelPresent", status.isAccelPresent);
      map.putBoolean("isGyroPresent", status.isGyroPresent);
      map.putBoolean("isGpsPresent", status.isGpsPresent);
      map.putBoolean("isGooglePlayServicesMissing", status.isGooglePlayServicesMissing);
      map.putString("wifiQuotaStatus", status.wifiQuotaStatus.toString());
      map.putString("mobileQuotaStatus", status.mobileQuotaStatus.toString());
      map.putString("diskQuotaStatus", status.diskQuotaStatus.toString());
    } catch (Exception ignored) {
    }

    return map;
  }

  public static WritableMap convertUserActivity(UserActivity activity) {
    WritableMap map = Arguments.createMap();
    try {
      map.putString("type", convertUserActivityType(activity.getActivityType()));

      //Trip Info
      if (activity.getTripInfo() != null) {
        WritableMap tripInfoMap = Arguments.createMap();
        String tripType = convertTripType(activity.getTripInfo().getTripType());
        tripInfoMap.putString("type", tripType);
        map.putMap("tripInfo", tripInfoMap);
      }

      //Stationary Info
      if (activity.getStationaryInfo() != null) {
        WritableMap stationaryInfoMap = Arguments.createMap();
        if (activity.getStationaryInfo().getLocation() != null) {
          WritableMap locationMap = convertLocation(activity.getStationaryInfo().getLocation());
          stationaryInfoMap.putMap("location", locationMap);
        }
        map.putMap("stationaryInfo", stationaryInfoMap);
      }

    } catch (Exception ignored) {
    }

    return map;
  }


  public static WritableMap convertLocation(Location location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putString("latitude", String.valueOf(location.getLatitude()));
    locationMap.putString("longitude", String.valueOf(location.getLongitude()));
    locationMap.putString("accuracy", String.valueOf(location.getAccuracy()));
    locationMap.putString("altitude", String.valueOf(location.getAltitude()));
    locationMap.putString("provider", location.getProvider());

    return locationMap;

  }

  public static String convertTripType(TripType tripType) {
    switch (tripType) {
      case ANY:
        return "ANY";
      case EXTERNAL_TRIP:
        return "TRIP_TYPE_EXTERNAL";
      case SDK_TRIP:
        return "TRIP_TYPE_SDK";
      default:
        return "TRIP_TYPE_SDK";
    }
  }

  public static String convertUserActivityType(UserActivityType activityType) {
    switch (activityType) {
      case TRIP:
        return "USER_ACTIVITY_TYPE_TRIP";
      case STATIONARY:
        return "USER_ACTIVITY_TYPE_STATIONARY";
      case UNKNOWN:
        return "USER_ACTIVITY_TYPE_UNKNOWN";
      default:
        return "USER_ACTIVITY_TYPE_UNKNOWN";
    }
  }

}
