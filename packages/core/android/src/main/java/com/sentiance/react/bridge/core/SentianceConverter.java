package com.sentiance.react.bridge.core;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.DetectionStatus;
import com.sentiance.sdk.DisableDetectionsResult;
import com.sentiance.sdk.EnableDetectionsError;
import com.sentiance.sdk.EnableDetectionsFailureReason;
import com.sentiance.sdk.EnableDetectionsResult;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.UserAccessTokenError;
import com.sentiance.sdk.UserAccessTokenFailureReason;
import com.sentiance.sdk.authentication.UserLinkingError;
import com.sentiance.sdk.authentication.UserLinkingResult;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityType;
import com.sentiance.sdk.reset.ResetError;
import com.sentiance.sdk.reset.ResetResult;
import com.sentiance.sdk.trip.StartTripError;
import com.sentiance.sdk.trip.StartTripFailureReason;
import com.sentiance.sdk.trip.StopTripError;
import com.sentiance.sdk.trip.StopTripFailureReason;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserCreationResult;
import com.sentiance.sdk.usercreation.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class SentianceConverter {

  public static WritableMap createEmptyResult() {
    return Arguments.createMap();
  }

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
    if (type.equals("sdk") || type.equals("TRIP_TYPE_SDK")) {
      return TripType.SDK_TRIP;
    } else if (type.equals("external") || type.equals("TRIP_TYPE_EXTERNAL")) {
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

  public static WritableMap convertUserCreationResult(UserCreationResult result) {
    WritableMap map = Arguments.createMap();
    UserInfo userInfo = result.getUserInfo();
    try {
      Token token = userInfo.getToken();
      map.putString("userId", userInfo.getUserId());
      map.putString("tokenId", token.getTokenId());
      map.putString("tokenExpiryDate", token.getExpiryDate().toString());
      map.putBoolean("isTokenExpired", token.isExpired());
    } catch (Exception ignored) {
    }

    return map;
  }

  public static WritableMap convertUserLinkingResult(UserLinkingResult result) {
    WritableMap map = Arguments.createMap();
    UserInfo userInfo = result.getUserInfo();
    try {
      Token token = userInfo.getToken();
      map.putString("userId", userInfo.getUserId());
      map.putString("tokenId", token.getTokenId());
      map.putString("tokenExpiryDate", token.getExpiryDate().toString());
      map.putBoolean("isTokenExpired", token.isExpired());
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
      map.putString("detectionStatus", status.detectionStatus.name());
      map.putBoolean("canDetect", status.canDetect);
      map.putBoolean("isRemoteEnabled", status.isRemoteEnabled);
      map.putString("locationPermission", status.locationPermission.toString());
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
      map.putBoolean("isPreciseLocationAuthorizationGranted", status.isPreciseLocationPermGranted);
      map.putBoolean("isSchedulingExactAlarmsPermitted", status.isSchedulingExactAlarmsPermitted);
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

  public static WritableMap convertEnableDetectionsResult(EnableDetectionsResult enableDetectionsResult) {
    return convertDetectionsResult(enableDetectionsResult.getSdkStatus(),
      enableDetectionsResult.getDetectionStatus());
  }

  public static WritableMap convertDisableDetectionsResult(DisableDetectionsResult disableDetectionsResult) {
    return convertDetectionsResult(disableDetectionsResult.getSdkStatus(),
      disableDetectionsResult.getDetectionStatus());
  }

  public static WritableMap convertResetResult(ResetResult resetResult) {
    WritableMap result = Arguments.createMap();
    result.putString("initState", resetResult.getInitState().name());
    return result;
  }

  private static WritableMap convertDetectionsResult(SdkStatus sdkStatus, DetectionStatus detectionStatus) {
    WritableMap result = Arguments.createMap();
    WritableMap sdkStatusMap = convertSdkStatus(sdkStatus);

    result.putMap("sdkStatus", sdkStatusMap);
    result.putString("detectionStatus", detectionStatus.toString());

    return result;
  }

  public static String stringifyEnableDetectionsError(EnableDetectionsError error) {
    EnableDetectionsFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER:
        details = "No Sentiance user present on the device.";
        break;
      case PAST_EXPIRY_DATE:
        details = "Expiry date is in past.";
        break;
      case USER_DISABLED_REMOTELY:
        details = "The user is disabled remotely.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyUserLinkingError(UserLinkingError error) {
    return String.format("Reason: %s - %s", error.getReason().name(), error.getDetails());
  }

  public static String stringifyUserCreationError(UserCreationError error) {
    return String.format("Reason: %s - %s", error.getReason().name(), error.getDetails());
  }

  public static String stringifyResetError(ResetError error) {
    return String.format("%s - caused by: %s", error.getReason().name(), error.getException());
  }

  public static String stringifyStartTripError(StartTripError error) {
    StartTripFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER:
        details = "No Sentiance user present on the device.";
        break;
      case DETECTIONS_DISABLED:
        details = "Detections are disabled. Enable them first before starting a trip.";
        break;
      case DETECTIONS_BLOCKED:
        details = "Detections are enabled but not running. Check the SDK's status to find out why.";
        break;
      case TRIP_ALREADY_STARTED:
        details = "An external trip is already started. To start a new trip, call `stopTrip()` first.";
        break;
      case USER_DISABLED_REMOTELY:
        details = "The user is disabled remotely.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyStopTripError(StopTripError error) {
    StopTripFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_ONGOING_TRIP:
        details = "There is no ongoing external trip.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyUserAccessTokenError(UserAccessTokenError error) {
    UserAccessTokenFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER:
        details = "No Sentiance user is present on device.";
        break;
      case NETWORK_ERROR:
        details = "A network error occurred. This can happen when the existing token is expired, " +
          "and it was not possible to contact the Sentiance Platform to refresh it.";
        break;
      case USER_DISABLED_REMOTELY:
        details = "The user is disabled remotely.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }
}
