package com.sentiance.react.bridge;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.sentiance.sdk.DetectionStatus;
import com.sentiance.sdk.DisableDetectionsError;
import com.sentiance.sdk.DisableDetectionsFailureReason;
import com.sentiance.sdk.DisableDetectionsResult;
import com.sentiance.sdk.EnableDetectionsError;
import com.sentiance.sdk.EnableDetectionsFailureReason;
import com.sentiance.sdk.InitState;
import com.sentiance.sdk.SdkStatus;
import com.sentiance.sdk.Token;
import com.sentiance.sdk.UserAccessTokenError;
import com.sentiance.sdk.UserAccessTokenFailureReason;
import com.sentiance.sdk.authentication.UserLinkingError;
import com.sentiance.sdk.authentication.UserLinkingFailureReason;
import com.sentiance.sdk.crashdetection.api.VehicleCrashEvent;
import com.sentiance.sdk.detectionupdates.UserActivity;
import com.sentiance.sdk.detectionupdates.UserActivityType;
import com.sentiance.sdk.init.InitializationFailureReason;
import com.sentiance.sdk.init.InitializationResult;
import com.sentiance.sdk.ondevice.api.Attribute;
import com.sentiance.sdk.ondevice.api.GeoLocation;
import com.sentiance.sdk.ondevice.api.event.Event;
import com.sentiance.sdk.ondevice.api.event.StationaryEvent;
import com.sentiance.sdk.ondevice.api.event.TransportEvent;
import com.sentiance.sdk.ondevice.api.segment.Segment;
import com.sentiance.sdk.ondevice.api.venue.Venue;
import com.sentiance.sdk.ondevice.api.venue.VenueCandidate;
import com.sentiance.sdk.ondevice.api.venue.Visit;
import com.sentiance.sdk.reset.ResetError;
import com.sentiance.sdk.trip.StartTripError;
import com.sentiance.sdk.trip.StartTripFailureReason;
import com.sentiance.sdk.trip.StopTripError;
import com.sentiance.sdk.trip.StopTripFailureReason;
import com.sentiance.sdk.trip.TransportMode;
import com.sentiance.sdk.trip.TripType;
import com.sentiance.sdk.usercontext.api.GetUserContextError;
import com.sentiance.sdk.usercontext.api.GetUserContextFailureReason;
import com.sentiance.sdk.usercontext.api.UserContext;
import com.sentiance.sdk.usercreation.UserCreationError;
import com.sentiance.sdk.usercreation.UserInfo;
import com.sentiance.sdk.util.DateTime;
import com.sentiance.sdk.EnableDetectionsResult;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

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
    if (type.equals("sdk") || type.equals("TRIP_TYPE_SDK")) {
      return TripType.SDK_TRIP;
    } else if (type.equals("external") || type.equals("TRIP_TYPE_EXTERNAL")){
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

  public static WritableMap convertUserInfo(UserInfo userInfo) {
    WritableMap map = Arguments.createMap();
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

  public static WritableMap convertInitializationResult(InitializationResult initResult) {
    WritableMap map = Arguments.createMap();
    try {
      map.putBoolean("isSuccessful", initResult.isSuccessful());

      InitializationFailureReason failureReason = initResult.getFailureReason();
      if (failureReason != null) {
        map.putString("failureReason", failureReason.name());
      }
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

  public static WritableMap convertGeoLocation(GeoLocation location) {
    WritableMap locationMap = Arguments.createMap();

    locationMap.putString("latitude", String.valueOf(location.getLatitude()));
    locationMap.putString("longitude", String.valueOf(location.getLongitude()));
    locationMap.putString("accuracy", String.valueOf(location.getAccuracyInMeters()));

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

  public static WritableMap convertVehicleCrashEvent(VehicleCrashEvent crashEvent) {
    WritableMap map = Arguments.createMap();

    map.putDouble("time", (double) crashEvent.getTime());

    if (crashEvent.getLocation() != null) {
      WritableMap locationMap = convertLocation(crashEvent.getLocation());
      map.putMap("location", locationMap);
    }

    if (crashEvent.getMagnitude() != null) {
      map.putDouble("magnitude", crashEvent.getMagnitude());
    }

    if (crashEvent.getSpeedAtImpact() != null) {
      map.putDouble("speedAtImpact", crashEvent.getSpeedAtImpact());
    }

    if (crashEvent.getDeltaV() != null) {
      map.putDouble("deltaV", crashEvent.getDeltaV());
    }

    if (crashEvent.getConfidence() != null) {
      map.putInt("confidence", crashEvent.getConfidence());
    }

    return map;
  }

  private static WritableMap convertSegment(Segment segment) {
    WritableMap map = Arguments.createMap();

    map.putString("category", segment.getCategory().name());
    map.putString("subcategory", segment.getSubcategory().name());
    map.putString("type", segment.getType().name());
    map.putInt("id", segment.getType().getUniqueId());
    map.putString("startTime", segment.getStartTime().toString());

    DateTime endTime = segment.getEndTime();
    if (endTime != null) {
      map.putString("endTime", endTime.toString());
    }

    WritableArray attributes = Arguments.createArray();
    for (Attribute attribute: segment.getAttributes()) {
      WritableMap a = Arguments.createMap();
      a.putString("name", attribute.getName());
      a.putDouble("value", attribute.getValue());
      attributes.pushMap(a);
    }
    map.putArray("attributes", attributes);

    return map;
  }

  private static WritableMap convertEvent(Event event) {
    WritableMap map = Arguments.createMap();

    map.putString("startTime", event.getStartTime().toString());
    if (event.getEndTime() != null) {
      map.putString("endTime", event.getEndTime().toString());

      Long durationInSeconds = event.getDurationInSeconds();
      if (durationInSeconds != null) {
        map.putInt("durationInSeconds", (int) (long) durationInSeconds);
      }
    }

    map.putString("type", event.getEventType().toString());

    if (event instanceof StationaryEvent) {
      addStationaryEventInfo(map, (StationaryEvent) event);
    }
    else if (event instanceof TransportEvent) {
      addTransportEventInfo(map, (TransportEvent) event);
    }

    return map;
  }

  public static WritableMap convertUserContext(UserContext userContext) {
    WritableMap userContextMap = Arguments.createMap();

    // Events
    WritableArray eventArray = Arguments.createArray();
    for (Event event: userContext.getEvents()) {
      eventArray.pushMap(convertEvent(event));
    }
    userContextMap.putArray("events", eventArray);

    // Segments
    WritableArray segmentsArray = Arguments.createArray();
    for (Segment segment: userContext.getActiveSegments()) {
      segmentsArray.pushMap(convertSegment(segment));
    }
    userContextMap.putArray("activeSegments", segmentsArray);

    // Last know location
    if (userContext.getLastKnownLocation() != null) {
      userContextMap.putMap("lastKnownLocation", convertGeoLocation(userContext.getLastKnownLocation()));
    }

    // Home
    if (userContext.getHome() != null) {
      userContextMap.putMap("home", convertVenue(userContext.getHome()));
    }

    // Work
    if (userContext.getWork() != null) {
      userContextMap.putMap("work", convertVenue(userContext.getWork()));
    }

    return userContextMap;
  }

  public static WritableMap convertEnableDetectionsResult(EnableDetectionsResult enableDetectionsResult) {
    return convertDetectionsResult(enableDetectionsResult.getSdkStatus(), enableDetectionsResult.getDetectionStatus());
  }

  public static WritableMap convertDisableDetectionsResult(DisableDetectionsResult disableDetectionsResult) {
    return convertDetectionsResult(disableDetectionsResult.getSdkStatus(), disableDetectionsResult.getDetectionStatus());
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
      case NO_USER: details = "No user present on device"; break;
      case PAST_EXPIRY_DATE: details = "Expiry date is in past."; break;
      case SDK_KILL_SWITCHED: details = "SDK has been killed switched from backend."; break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyDisableDetectionsError(DisableDetectionsError error) {
    DisableDetectionsFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER: details = "No user present on device"; break;
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
      case NO_USER: details = "No user present on device"; break;
      case DETECTIONS_DISABLED: details = "Enable detections first before starting a trip."; break;
      case DETECTIONS_EXPIRED: details = "Detections are stopped as per the provided stop date to " +
              "Sentiance.enableDetections(Date), please enable detections before starting a trip."; break;
      case DETECTIONS_NOT_RUNNING: details = "Detections are not running, check SDK Status."; break;
      case TRIP_ALREADY_STARTED: details = "Trip is already started, to start a new trip call Sentiance.stopTrip() first.";
        break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyStopTripError(StopTripError error) {
    StopTripFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER: details = "No user present on device"; break;
      case DETECTIONS_DISABLED: details = "Enable detections first before starting a trip."; break;
      case DETECTIONS_EXPIRED: details = "Detections are stopped as per the provided stop date to " +
              "Sentiance.enableDetections(Date), please enable detections before starting a trip."; break;
      case DETECTIONS_NOT_RUNNING: details = "Detections are not running, check SDK Status."; break;
      case NO_ONGOING_TRIP: details = "There is no trip ongoing."; break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyUserAccessTokenError(UserAccessTokenError error) {
    UserAccessTokenFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER: details = "No user present on device"; break;
      case NETWORK_ERROR: details = "This can happen if both of the following conditions are met: " +
              "1. The token has expired; 2. There is no network connection that can be used to get a new token from " +
              "the Sentiance API.";
      break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  public static String stringifyGetUserContextError(GetUserContextError error) {
    GetUserContextFailureReason reason = error.getReason();
    String details = "";
    switch (reason) {
      case NO_USER: details = "No user present on device"; break;
      case FEATURE_NOT_ENABLED: details = "Feature not enabled, make sure you have enabled the feature at " +
              "initialization and it is also enabled for your app from Sentiance backend.";
      break;
    }
    return String.format("Reason: %s - %s", reason.name(), details);
  }

  private static void addStationaryEventInfo(WritableMap map, StationaryEvent event) {
    if (event.getLocation() != null) {
      map.putMap("location", convertGeoLocation(event.getLocation()));
    }

    map.putString("venueType", event.getVenueType().toString());

    WritableArray venueCandidatesArray = Arguments.createArray();
    for (VenueCandidate candidate: event.getVenueCandidates()) {
      venueCandidatesArray.pushMap(convertVenueCandidate(candidate));
    }

    map.putArray("venueCandidates", venueCandidatesArray);
  }

  private static WritableMap convertVenueCandidate(VenueCandidate candidate) {
    WritableMap venueCandidateMap = Arguments.createMap();
    venueCandidateMap.putMap("venue", convertVenue(candidate.getVenue()));
    venueCandidateMap.putDouble("likelihood", candidate.getLikelihood());

    WritableArray visitsArray = Arguments.createArray();
    for (Visit visit: candidate.getVisits()) {
      visitsArray.pushMap(convertVisit(visit));
    }
    venueCandidateMap.putArray("visits", visitsArray);
    return venueCandidateMap;
  }

  private static WritableMap convertVenue(Venue venue) {
    WritableMap venueMap = Arguments.createMap();

    if (venue.getName() != null) {
      venueMap.putString("name", venue.getName());
    }

    venueMap.putMap("location", convertGeoLocation(venue.getLocation()));

    WritableMap venueLabelsMap = Arguments.createMap();
    for (Map.Entry<String, String> entry: venue.getLabels().entrySet()) {
      venueLabelsMap.putString(entry.getKey(), entry.getValue());
    }
    venueMap.putMap("venueLabels", venueLabelsMap);

    return venueMap;
  }

  private static WritableMap convertVisit(Visit visit) {
    WritableMap visitMap = Arguments.createMap();
    visitMap.putString("startTime", visit.getStartTime().toString());
    visitMap.putString("endTime", visit.getEndTime().toString());
    visitMap.putInt("durationInSeconds", (int) visit.getDurationInSeconds());

    return visitMap;
  }

  private static void addTransportEventInfo(WritableMap map, TransportEvent event) {
    map.putString("transportMode", event.getTransportMode().toString());
  }
}
