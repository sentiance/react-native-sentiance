import {Platform} from 'react-native';
import legacy from './legacy';
import core from '@react-native-sentiance/core';
import crashDetection from '@react-native-sentiance/crash-detection';

var RNSentiance = {};

var coreModule
if (Platform.OS === 'android') {
  coreModule = core;
} else {
  coreModule = legacy;
}

var crashDetectionModule
if (Platform.OS === 'android') {
  crashDetectionModule = crashDetection;
} else {
  crashDetectionModule = legacy;
}

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
  disableNativeInitialization,
  addListener,
  removeListeners
} = legacy;

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

// Legacy bindings
RNSentiance.init = init;
RNSentiance.initWithUserLinkingEnabled = initWithUserLinkingEnabled;
RNSentiance.reset = reset;
RNSentiance.start = start;
RNSentiance.startWithStopDate = startWithStopDate;
RNSentiance.stop = stop;
RNSentiance.startTrip = startTrip;
RNSentiance.stopTrip = stopTrip;
RNSentiance.setValueForKey = setValueForKey;
RNSentiance.getValueForKey = getValueForKey;
RNSentiance.isThirdPartyLinked = isThirdPartyLinked;
RNSentiance.isNativeInitializationEnabled = isNativeInitializationEnabled;
RNSentiance.enableNativeInitialization = enableNativeInitialization;
RNSentiance.disableNativeInitialization = disableNativeInitialization;
RNSentiance.addListener = addListener
RNSentiance.removeListeners = removeListeners

export default RNSentiance;
