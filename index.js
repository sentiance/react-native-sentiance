const RNSentiance = {};

RNSentiance.TransportMode = {};
(function (TransportMode) {
  TransportMode[TransportMode["UNKNOWN"] = 1] = "UNKNOWN";
  TransportMode[TransportMode["CAR"] = 2] = "CAR";
  TransportMode[TransportMode["BICYCLE"] = 3] = "BICYCLE";
  TransportMode[TransportMode["ON_FOOT"] = 4] = "ON_FOOT";
  TransportMode[TransportMode["TRAIN"] = 5] = "TRAIN";
  TransportMode[TransportMode["TRAM"] = 6] = "TRAM";
  TransportMode[TransportMode["BUS"] = 7] = "BUS";
  TransportMode[TransportMode["PLANE"] = 8] = "PLANE";
  TransportMode[TransportMode["BOAT"] = 9] = "BOAT";
  TransportMode[TransportMode["METRO"] = 10] = "METRO";
  TransportMode[TransportMode["RUNNING"] = 11] = "RUNNING";
})(RNSentiance.TransportMode);

const {
  userExists,
  enableDetections,
  enableDetectionsWithExpiryDate,
  reset,
  getUserContext,
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
  listenVehicleCrashEvents,
  invokeDummyVehicleCrash,
  isVehicleCrashDetectionSupported,
  listenUserContextUpdates,
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
} = require('./sentiance');

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
