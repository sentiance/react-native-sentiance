const legacy = require('./legacy');
const core = require('@sentiance-react-native/core');
const crashDetection = require('@sentiance-react-native/crash-detection');

var RNSentiance = {};
if (core) {
  RNSentiance.TransportMode = core.transportModes;
}

const {
  userLinkCallback,
  getVersion,
  getUserId,
  addUserMetadataField,
  addUserMetadataFields,
  removeUserMetadataField,
  getUserActivity,
  listenUserActivityUpdates,
  isTripOngoing,
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
  getWiFiQuotaUsage,
  listenTripTimeout
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
  submitDetections,
  getUserAccessToken,
  addListener,
  removeListeners
} = legacy;

// Core bindings
RNSentiance.userLinkCallback = userLinkCallback;
RNSentiance.getUserId = getUserId;
RNSentiance.getVersion = getVersion;
RNSentiance.getUserActivity = getUserActivity;
RNSentiance.isTripOngoing = isTripOngoing;
RNSentiance.addTripMetadata = addTripMetadata;
RNSentiance.getInitState = getInitState;
RNSentiance.getSdkStatus = getSdkStatus;
RNSentiance.getDiskQuotaLimit = getDiskQuotaLimit;
RNSentiance.getDiskQuotaUsage = getDiskQuotaUsage;
RNSentiance.getMobileQuotaLimit = getMobileQuotaLimit;
RNSentiance.getMobileQuotaUsage = getMobileQuotaUsage;
RNSentiance.getWiFiQuotaLimit = getWiFiQuotaLimit;
RNSentiance.getWiFiQuotaUsage = getWiFiQuotaUsage;
RNSentiance.listenTripTimeout = listenTripTimeout;
RNSentiance.addUserMetadataField = async (label, value) => {
  await addUserMetadataField(label, value);
  return Promise.resolve(true);
};
RNSentiance.addUserMetadataFields = async (metadata) => {
  await addUserMetadataFields(metadata);
  return Promise.resolve(true);
};
RNSentiance.removeUserMetadataField = async (label) => {
  await removeUserMetadataField(label);
  return Promise.resolve(true);
};
RNSentiance.listenUserActivityUpdates = async () => {
  await listenUserActivityUpdates();
  return Promise.resolve(true);
};
RNSentiance.updateSdkNotification = async (title, message) => {
  await updateSdkNotification(title, message);
  return Promise.resolve(true);
};
RNSentiance.disableBatteryOptimization = async () => {
  await disableBatteryOptimization();
  return Promise.resolve(true);
};

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
RNSentiance.submitDetections = submitDetections;
RNSentiance.getUserAccessToken = getUserAccessToken;
RNSentiance.isNativeInitializationEnabled = isNativeInitializationEnabled;
RNSentiance.enableNativeInitialization = enableNativeInitialization;
RNSentiance.disableNativeInitialization = disableNativeInitialization;
RNSentiance.addListener = addListener
RNSentiance.removeListeners = removeListeners

module.exports = RNSentiance;
