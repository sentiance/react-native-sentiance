package com.sentiance.react.bridge;

import android.location.Location;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityType;
import com.sentiance.sdk.ondevice.TripProfile;
import com.sentiance.sdk.ondevice.transportclassifier.HardEvent;
import com.sentiance.sdk.ondevice.transportclassifier.TransportSegment;
import com.sentiance.sdk.ondevice.transportclassifier.VehicleMode;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;

import java.util.HashMap;
import java.util.List;
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

  public static TripType toTripType(final int type) {
    if (type == 1) {
      return TripType.SDK_TRIP;
    } else if (type == 2) {
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
        return "UNRECOGNIZED_STATE";
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
      map.putBoolean("isActivityRecognitionPermGranted", status.isActivityRecognitionPermGranted);
      map.putString("locationSetting", status.locationSetting.name());
      map.putBoolean("isAirplaneModeEnabled", status.isAirplaneModeEnabled);
      map.putBoolean("isLocationAvailable", status.isLocationAvailable);
      map.putBoolean("isAccelPresent", status.isAccelPresent);
      map.putBoolean("isGyroPresent", status.isGyroPresent);
      map.putBoolean("isGpsPresent", status.isGpsPresent);
      map.putBoolean("isGooglePlayServicesMissing", status.isGooglePlayServicesMissing);
      map.putBoolean("isBatteryOptimizationEnabled", status.isBatteryOptimizationEnabled);
      map.putBoolean("isBatterySavingEnabled", status.isBatterySavingEnabled);
      map.putBoolean("isBackgroundProcessingRestricted", status.isBackgroundProcessingRestricted);
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

  public static WritableMap convertCrashEvent(long time, @Nullable Location lastKnownLocation) {
    WritableMap map = Arguments.createMap();

    map.putDouble("time", (double) time);

    if (lastKnownLocation != null) {
      WritableMap locationMap = convertLocation(lastKnownLocation);
      map.putMap("lastKnownLocation", locationMap);
    }

    return map;
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
        return "TRIP_TYPE_UNRECOGNIZED";
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
        return "USER_ACTIVITY_TYPE_UNRECOGNIZED";
    }
  }

  private static String convertVehicleMode(VehicleMode mode) {
    switch(mode) {
      case IDLE:
        return "IDLE";
      case VEHICLE:
        return "VEHICLE";
      case NOT_VEHICLE:
        return "NOT_VEHICLE";
      default:
        return "UNKNOWN";
    }
  }

    public static WritableMap convertTripProfile(TripProfile tripProfile) {
    WritableMap map = Arguments.createMap();

    try {
      map.putString("tripId", tripProfile.getTripId());
      List<TransportSegment> transportSegments = tripProfile.getTransportSegments();
      WritableArray transportSegmentsArray = Arguments.createArray();
      for (TransportSegment transportSegment : transportSegments) {
        WritableMap transportSegmentMap = Arguments.createMap();
        transportSegmentMap.putDouble("startTime", (double) transportSegment.getStartTime());
        transportSegmentMap.putDouble("endTime", (double) transportSegment.getEndTime());
        Double distance = transportSegment.getDistance();
        if (distance != null) {
          transportSegmentMap.putDouble("distance", distance);
        }
        Double avgSpeed = transportSegment.getAverageSpeed();
        if (avgSpeed != null) {
          transportSegmentMap.putDouble("averageSpeed", avgSpeed);
        }
        Double topSpeed = transportSegment.getTopSpeed();
        if (topSpeed != null) {
          transportSegmentMap.putDouble("topSpeed", topSpeed);
        }
        Integer percentOfTimeSpeeding = transportSegment.getPercentOfTimeSpeeding();
        if (percentOfTimeSpeeding != null) {
          transportSegmentMap.putInt("percentOfTimeSpeeding", percentOfTimeSpeeding);
        }
        transportSegmentMap.putString("vehicleMode", convertVehicleMode(transportSegment.getVehicleMode()));

        List<HardEvent> hardEvents = transportSegment.getHardEvents();
        WritableArray hardEventsArray = Arguments.createArray();
        if (hardEvents != null) {
          for (HardEvent hardEvent : hardEvents) {
            WritableMap hardEventMap = Arguments.createMap();
            hardEventMap.putDouble("magnitude", hardEvent.getMagnitude());
            hardEventMap.putDouble("timestamp", (double) hardEvent.getTimestamp());
            hardEventsArray.pushMap(hardEventMap);
          }
        }
        transportSegmentMap.putArray("hardEvents", hardEventsArray);

        transportSegmentsArray.pushMap(transportSegmentMap);
      }
      map.putArray("transportSegments", transportSegmentsArray);
    } catch (Exception ignored) {

    }

    return map;
  }

}
