const {NativeModules, NativeEventEmitter} = require("react-native");

const {RNSentiance} = NativeModules;

const SENTIANCE_EMITTER = new NativeEventEmitter(RNSentiance);

const userExists = () => RNSentiance.userExists();
const enableDetections = () => RNSentiance.enableDetections();
const enableDetectionsWithExpiryDate = (expiryTime) => RNSentiance.enableDetectionsWithExpiryDate(expiryTime);
const reset = () => RNSentiance.reset();
const isUserLinked = () => RNSentiance.isUserLinked();
const getVersion = () => RNSentiance.getVersion();
const getUserId = () => RNSentiance.getUserId();
const getUserAccessToken = () => RNSentiance.getUserAccessToken();
const addUserMetadataField = (label, value) => RNSentiance.addUserMetadataField(label, value);
const addUserMetadataFields = (metadata) => RNSentiance.addUserMetadataFields(metadata);
const removeUserMetadataField = (label) => RNSentiance.removeUserMetadataField(label);
const getUserActivity = () => RNSentiance.getUserActivity();
const listenUserActivityUpdates = () => RNSentiance.listenUserActivityUpdates();
const startTrip = (metadata, hint) => RNSentiance.startTrip(metadata, hint);
const stopTrip = () => RNSentiance.stopTrip();
const isTripOngoing = (tripType) => RNSentiance.isTripOngoing(tripType);
const submitDetections = () => RNSentiance.submitDetections();
const updateSdkNotification = (title, message) => RNSentiance.updateSdkNotification(title, message);
const addTripMetadata = (metadata) => RNSentiance.addTripMetadata(metadata);
const setAppSessionDataCollectionEnabled = (enabled) => RNSentiance.setAppSessionDataCollectionEnabled(enabled);
const isAppSessionDataCollectionEnabled = () => RNSentiance.isAppSessionDataCollectionEnabled();
const disableDetections = () => RNSentiance.disableDetections();
const getInitState = () => RNSentiance.getInitState();
const getSdkStatus = () => RNSentiance.getSdkStatus();
const getDiskQuotaLimit = () => RNSentiance.getDiskQuotaLimit();
const getDiskQuotaUsage = () => RNSentiance.getDiskQuotaUsage();
const disableBatteryOptimization = () => RNSentiance.disableBatteryOptimization();
const getMobileQuotaLimit = () => RNSentiance.getMobileQuotaLimit();
const getMobileQuotaUsage = () => RNSentiance.getMobileQuotaUsage();
const getWiFiQuotaLimit = () => RNSentiance.getWiFiQuotaLimit();
const getWiFiQuotaUsage = () => RNSentiance.getWiFiQuotaUsage();
const createUnlinkedUser = async (appId, secret) => {
  return RNSentiance.createUnlinkedUser(appId, secret);
};

const createLinkedUser = async (appId, secret, linker) => {
  _addUserLinkListener(linker);
  return RNSentiance.createLinkedUser(appId, secret);
};

const linkUser = async (linker) => {
  _addUserLinkListener(linker);
  return RNSentiance.linkUser();
};

const _addUserLinkListener = (linker) => {
  let subscription = SENTIANCE_EMITTER.addListener("SDKUserLink", async (event) => {
    const {installId} = event;
    const linkingResult = await linker(installId);
    RNSentiance.userLinkCallback(linkingResult);
    subscription.remove();
  });
};

const transportModes = {};
(function (transportModes) {
  transportModes[transportModes["UNKNOWN"] = 1] = "UNKNOWN";
  transportModes[transportModes["CAR"] = 2] = "CAR";
  transportModes[transportModes["BICYCLE"] = 3] = "BICYCLE";
  transportModes[transportModes["ON_FOOT"] = 4] = "ON_FOOT";
  transportModes[transportModes["TRAIN"] = 5] = "TRAIN";
  transportModes[transportModes["TRAM"] = 6] = "TRAM";
  transportModes[transportModes["BUS"] = 7] = "BUS";
  transportModes[transportModes["PLANE"] = 8] = "PLANE";
  transportModes[transportModes["BOAT"] = 9] = "BOAT";
  transportModes[transportModes["METRO"] = 10] = "METRO";
  transportModes[transportModes["RUNNING"] = 11] = "RUNNING";
})(transportModes);

module.exports = {
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
  linkUser,
  transportModes
};
