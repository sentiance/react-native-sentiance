const core = require('@react-native-sentiance/core');
const crashDetection = require('@react-native-sentiance/crash-detection');
const RNSentiance = require('./deprecated');

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

export default RNSentiance;
