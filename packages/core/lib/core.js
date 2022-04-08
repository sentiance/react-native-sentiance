const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("./utils");
const {SentianceCore} = NativeModules;

if (!SentianceCore) {
  const nativeModuleName = varToString({SentianceCore});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

const SENTIANCE_EMITTER = new NativeEventEmitter(SentianceCore);

const userLinkCallback = (userLinkResult) => SentianceCore.userLinkCallback(userLinkResult);
const userExists = () => SentianceCore.userExists();
const enableDetections = () => SentianceCore.enableDetections();
const enableDetectionsWithExpiryDate = (expiryTime) => SentianceCore.enableDetectionsWithExpiryDate(expiryTime);
const reset = () => SentianceCore.reset();
const isUserLinked = () => SentianceCore.isUserLinked();
const getVersion = () => SentianceCore.getVersion();
const getUserId = () => SentianceCore.getUserId();
const getUserAccessToken = () => SentianceCore.getUserAccessToken();
const addUserMetadataField = (label, value) => SentianceCore.addUserMetadataField(label, value);
const addUserMetadataFields = (metadata) => SentianceCore.addUserMetadataFields(metadata);
const removeUserMetadataField = (label) => SentianceCore.removeUserMetadataField(label);
const getUserActivity = () => SentianceCore.getUserActivity();
const listenUserActivityUpdates = () => SentianceCore.listenUserActivityUpdates();
const startTrip = (metadata, hint) => SentianceCore.startTrip(metadata, hint);
const stopTrip = () => SentianceCore.stopTrip();
const isTripOngoing = (tripType) => SentianceCore.isTripOngoing(tripType);
const submitDetections = () => SentianceCore.submitDetections();
const updateSdkNotification = (title, message) => SentianceCore.updateSdkNotification(title, message);
const addTripMetadata = (metadata) => SentianceCore.addTripMetadata(metadata);
const setAppSessionDataCollectionEnabled = (enabled) => SentianceCore.setAppSessionDataCollectionEnabled(enabled);
const isAppSessionDataCollectionEnabled = () => SentianceCore.isAppSessionDataCollectionEnabled();
const disableDetections = () => SentianceCore.disableDetections();
const getInitState = () => SentianceCore.getInitState();
const getSdkStatus = () => SentianceCore.getSdkStatus();
const getDiskQuotaLimit = () => SentianceCore.getDiskQuotaLimit();
const getDiskQuotaUsage = () => SentianceCore.getDiskQuotaUsage();
const disableBatteryOptimization = () => SentianceCore.disableBatteryOptimization();
const getMobileQuotaLimit = () => SentianceCore.getMobileQuotaLimit();
const getMobileQuotaUsage = () => SentianceCore.getMobileQuotaUsage();
const getWiFiQuotaLimit = () => SentianceCore.getWiFiQuotaLimit();
const getWiFiQuotaUsage = () => SentianceCore.getWiFiQuotaUsage();
const createUnlinkedUser = async (appId, secret) => {
  return SentianceCore.createUnlinkedUser(appId, secret);
};

const createLinkedUser = async (appId, secret, linker) => {
  _addUserLinkListener(linker);
  return SentianceCore.createLinkedUser(appId, secret);
};

const linkUser = async (linker) => {
  _addUserLinkListener(linker);
  return SentianceCore.linkUser();
};

const _addUserLinkListener = (linker) => {
  let subscription = SENTIANCE_EMITTER.addListener("SDKUserLink", async (event) => {
    const {installId} = event;
    const linkingResult = await linker(installId);
    SentianceCore.userLinkCallback(linkingResult);
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
