const core = require('@react-native-sentiance/core');
const crashDetection = require('@react-native-sentiance/crash-detection');
const legacy = require('./legacy');

const RNSentiance = {};

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
  startTrip,
  stopTrip,
  isTripOngoing,
  submitDetections,
  updateSdkNotification,
  addTripMetadata,
  setAppSessionDataCollectionEnabled,
  isAppSessionDataCollectionEnabled,
  getInitState,
  getSdkStatus,
  getDiskQuotaLimit,
  getDiskQuotaUsage,
  disableBatteryOptimization,
  getMobileQuotaLimit,
  getMobileQuotaUsage,
  getWiFiQuotaLimit,
  getWiFiQuotaUsage,
  linkUser
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
  setValueForKey,
  getValueForKey,
  isThirdPartyLinked,
  isNativeInitializationEnabled,
  enableNativeInitialization,
  disableNativeInitialization
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
RNSentiance.startTrip = startTrip;
RNSentiance.stopTrip = stopTrip;
RNSentiance.isTripOngoing = isTripOngoing;
RNSentiance.submitDetections = submitDetections;
RNSentiance.updateSdkNotification = updateSdkNotification;
RNSentiance.addTripMetadata = addTripMetadata;
RNSentiance.setAppSessionDataCollectionEnabled = setAppSessionDataCollectionEnabled;
RNSentiance.isAppSessionDataCollectionEnabled = isAppSessionDataCollectionEnabled;
RNSentiance.getInitState = getInitState;
RNSentiance.getSdkStatus = getSdkStatus;
RNSentiance.getDiskQuotaLimit = getDiskQuotaLimit;
RNSentiance.getDiskQuotaUsage = getDiskQuotaUsage;
RNSentiance.disableBatteryOptimization = disableBatteryOptimization;
RNSentiance.getMobileQuotaLimit = getMobileQuotaLimit;
RNSentiance.getMobileQuotaUsage = getMobileQuotaUsage;
RNSentiance.getWiFiQuotaLimit = getWiFiQuotaLimit;
RNSentiance.getWiFiQuotaUsage = getWiFiQuotaUsage;
RNSentiance.linkUser = linkUser;

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
RNSentiance.setValueForKey = setValueForKey;
RNSentiance.getValueForKey = getValueForKey;
RNSentiance.isThirdPartyLinked = isThirdPartyLinked;
RNSentiance.isNativeInitializationEnabled = isNativeInitializationEnabled;
RNSentiance.enableNativeInitialization = enableNativeInitialization;
RNSentiance.disableNativeInitialization = disableNativeInitialization;

module.exports = RNSentiance;
