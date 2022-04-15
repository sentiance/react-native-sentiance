import { Platform } from 'react-native';
import Legacy  from './legacy';
import core from '@react-native-sentiance/core';
import crashDetection from '@react-native-sentiance/crash-detection';

var RNSentiance = Legacy;

if (Platform.OS === 'android') {
  console.log('iandroid specific methods')

  RNSentiance.TransportMode = core.transportModes;

  const {
    userLinkCallback,
    getVersion,
    getUserId,
    getUserAccessToken,
    addUserMetadataField,
    addUserMetadataFields,
    removeUserMetadataField,
    getUserActivity,
    listenUserActivityUpdates,
    isTripOngoing,
    submitDetections,
    updateSdkNotification,
    addTripMetadata,
    getInitState,
    getSdkStatus,
    getDiskQuotaLimit,
    getDiskQuotaUsage,
    disableBatteryOptimization,
    getMobileQuotaLimit,
    getMobileQuotaUsage,
    getWiFiQuotaLimit,
    getWiFiQuotaUsage
  } = core;

  const {
    listenVehicleCrashEvents,
    invokeDummyVehicleCrash,
    isVehicleCrashDetectionSupported
  } = crashDetection;

  const {
    init,
    initWithUserLinkingEnabled,
    reset,
    start,
    startWithStopDate,
    stop,
    startTrip,
    stopTrip,
    setValueForKey,
    getValueForKey,
    isThirdPartyLinked,
    isNativeInitializationEnabled,
    enableNativeInitialization,
    disableNativeInitialization
  } = Legacy;

  // Core bindings
  RNSentiance.userLinkCallback = userLinkCallback;
  RNSentiance.getUserId = getUserId;
  RNSentiance.getUserAccessToken = getUserAccessToken;
  RNSentiance.addUserMetadataField = addUserMetadataField;
  RNSentiance.addUserMetadataFields = addUserMetadataFields;
  RNSentiance.removeUserMetadataField = removeUserMetadataField;
  RNSentiance.getVersion = getVersion;
  RNSentiance.getUserActivity = getUserActivity;
  RNSentiance.listenUserActivityUpdates = listenUserActivityUpdates;
  RNSentiance.isTripOngoing = isTripOngoing;
  RNSentiance.submitDetections = submitDetections;
  RNSentiance.updateSdkNotification = updateSdkNotification;
  RNSentiance.addTripMetadata = addTripMetadata;
  RNSentiance.getInitState = getInitState;
  RNSentiance.getSdkStatus = getSdkStatus;
  RNSentiance.getDiskQuotaLimit = getDiskQuotaLimit;
  RNSentiance.getDiskQuotaUsage = getDiskQuotaUsage;
  RNSentiance.disableBatteryOptimization = disableBatteryOptimization;
  RNSentiance.getMobileQuotaLimit = getMobileQuotaLimit;
  RNSentiance.getMobileQuotaUsage = getMobileQuotaUsage;
  RNSentiance.getWiFiQuotaLimit = getWiFiQuotaLimit;
  RNSentiance.getWiFiQuotaUsage = getWiFiQuotaUsage;

  // Crash detection bindings
  RNSentiance.listenVehicleCrashEvents = listenVehicleCrashEvents;
  RNSentiance.invokeDummyVehicleCrash = invokeDummyVehicleCrash;
  RNSentiance.isVehicleCrashDetectionSupported = isVehicleCrashDetectionSupported;
}

export default RNSentiance;