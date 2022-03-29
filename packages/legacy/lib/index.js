const sentiance = require('@react-native-sentiance/sentiance');
const crashDetection = require('@react-native-sentiance/crash-detection');
const userContext = require('@react-native-sentiance/user-context');

const RNSentiance = {};

RNSentiance.TransportMode = sentiance.transportModes;

const {
  userExists,
  enableDetections,
  enableDetectionsWithExpiryDate,
  reset,
  isUserLinked,
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
  disableDetections,
  getInitState,
  getSdkStatus,
  getDiskQuotaLimit,
  getDiskQuotaUsage,
  disableBatteryOptimization,
  getMobileQuotaLimit,
  getMobileQuotaUsage,
  getWiFiQuotaLimit,
  getWiFiQuotaUsage,
  createLinkedUser,
  createUnlinkedUser,
  linkUser
} = sentiance;

const {
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported
} = crashDetection;

const {
  getUserContext,
  listenUserContextUpdates
} = userContext;

RNSentiance.userExists = userExists;
RNSentiance.enableDetections = enableDetections;
RNSentiance.enableDetectionsWithExpiryDate = enableDetectionsWithExpiryDate;
RNSentiance.reset = reset;
RNSentiance.getUserContext = getUserContext;
RNSentiance.isUserLinked = isUserLinked;
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
RNSentiance.listenVehicleCrashEvents = listenVehicleCrashEvents;
RNSentiance.invokeDummyVehicleCrash = invokeDummyVehicleCrash;
RNSentiance.isVehicleCrashDetectionSupported = isVehicleCrashDetectionSupported;
RNSentiance.listenUserContextUpdates = listenUserContextUpdates;
RNSentiance.setAppSessionDataCollectionEnabled = setAppSessionDataCollectionEnabled;
RNSentiance.isAppSessionDataCollectionEnabled = isAppSessionDataCollectionEnabled;
RNSentiance.disableDetections = disableDetections;
RNSentiance.getInitState = getInitState;
RNSentiance.getSdkStatus = getSdkStatus;
RNSentiance.getDiskQuotaLimit = getDiskQuotaLimit;
RNSentiance.getDiskQuotaUsage = getDiskQuotaUsage;
RNSentiance.disableBatteryOptimization = disableBatteryOptimization;
RNSentiance.getMobileQuotaLimit = getMobileQuotaLimit;
RNSentiance.getMobileQuotaUsage = getMobileQuotaUsage;
RNSentiance.getWiFiQuotaLimit = getWiFiQuotaLimit;
RNSentiance.getWiFiQuotaUsage = getWiFiQuotaUsage;
RNSentiance.createLinkedUser = createLinkedUser;
RNSentiance.createUnlinkedUser = createUnlinkedUser;
RNSentiance.linkUser = linkUser;

module.exports = RNSentiance;
