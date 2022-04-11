const {NativeEventEmitter} = require("react-native");
import core from './core';

const SENTIANCE_EMITTER = new NativeEventEmitter(core);

const userLinkCallback = (userLinkResult) => core.userLinkCallback(userLinkResult);
const userExists = () => core.userExists();
const enableDetections = () => core.enableDetections();
const enableDetectionsWithExpiryDate = (expiryTime) => core.enableDetectionsWithExpiryDate(expiryTime);
const reset = () => core.reset();
const isUserLinked = () => core.isUserLinked();
const getVersion = () => core.getVersion();
const getUserId = () => core.getUserId();
const getUserAccessToken = () => core.getUserAccessToken();
const addUserMetadataField = (label, value) => core.addUserMetadataField(label, value);
const addUserMetadataFields = (metadata) => core.addUserMetadataFields(metadata);
const removeUserMetadataField = (label) => core.removeUserMetadataField(label);
const getUserActivity = () => core.getUserActivity();
const listenUserActivityUpdates = () => core.listenUserActivityUpdates();
const startTrip = (metadata, hint) => core.startTrip(metadata, hint);
const stopTrip = () => core.stopTrip();
const isTripOngoing = (tripType) => core.isTripOngoing(tripType);
const submitDetections = () => core.submitDetections();
const updateSdkNotification = (title, message) => core.updateSdkNotification(title, message);
const addTripMetadata = (metadata) => core.addTripMetadata(metadata);
const setAppSessionDataCollectionEnabled = (enabled) => core.setAppSessionDataCollectionEnabled(enabled);
const isAppSessionDataCollectionEnabled = () => core.isAppSessionDataCollectionEnabled();
const disableDetections = () => core.disableDetections();
const getInitState = () => core.getInitState();
const getSdkStatus = () => core.getSdkStatus();
const getDiskQuotaLimit = () => core.getDiskQuotaLimit();
const getDiskQuotaUsage = () => core.getDiskQuotaUsage();
const disableBatteryOptimization = () => core.disableBatteryOptimization();
const getMobileQuotaLimit = () => core.getMobileQuotaLimit();
const getMobileQuotaUsage = () => core.getMobileQuotaUsage();
const getWiFiQuotaLimit = () => core.getWiFiQuotaLimit();
const getWiFiQuotaUsage = () => core.getWiFiQuotaUsage();
const createUnlinkedUser = async (appId, secret) => {
  return core.createUnlinkedUser(appId, secret);
};

const createLinkedUser = async (appId, secret, linker) => {
  _addUserLinkListener(linker);
  return core.createLinkedUser(appId, secret);
};

const linkUser = async (linker) => {
  _addUserLinkListener(linker);
  return core.linkUser();
};

const _addUserLinkListener = (linker) => {
  let subscription = SENTIANCE_EMITTER.addListener("SDKUserLink", async (event) => {
    const {installId} = event;
    const linkingResult = await linker(installId);
    core.userLinkCallback(linkingResult);
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
  userLinkCallback,
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
