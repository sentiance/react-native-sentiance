import core from './core';

const enableDetections = () => core.enableDetections();
const enableDetectionsWithExpiryDate = (expiryTime) => core.enableDetectionsWithExpiryDate(expiryTime);
const disableDetections = () => core.disableDetections();
const getInitState = () => core.getInitState();
const userLinkCallback = (userLinkResult) => core.userLinkCallback(userLinkResult);
const userExists = () => core.userExists();
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
const getSdkStatus = () => core.getSdkStatus();
const getDiskQuotaLimit = () => core.getDiskQuotaLimit();
const getDiskQuotaUsage = () => core.getDiskQuotaUsage();
const disableBatteryOptimization = () => core.disableBatteryOptimization();
const getMobileQuotaLimit = () => core.getMobileQuotaLimit();
const getMobileQuotaUsage = () => core.getMobileQuotaUsage();
const getWiFiQuotaLimit = () => core.getWiFiQuotaLimit();
const getWiFiQuotaUsage = () => core.getWiFiQuotaUsage();
const linkUserWithAuthCode = (authCode) => core.linkUserWithAuthCode(authCode);

const linkUser = async (linker) => {
  core._addUserLinkListener(linker);
  return core.linkUser();
};

/**
 * @typedef {Object} UserCreationOptions
 * @property {String} authCode - Auth Code
 * @property {String} platformUrl - Sentiance Platform URL
 * @property {String} appId - APP ID
 * @property {String} appSecret - APP Secret
 * @property {Function} linker (data) => result - Function to handle the user linking
 *
 * const userCreationOptions = {
 *  authCode: "<AUTH_CODE>"
 * }
 *
 * createUser(userCreationOptions);
 *
 * Or with appId / appSecret (Not recommended)
 *
 * const userCreationOptions = {
 *    linker : (installId) => {
 *      return linkUser(installId);
 *    },
 *    appId : "<APP_ID>",
 *    appSecret: "<APP_SECRET>"
 *  }
 *
 *  createUser(userCreationOptions);
 */


/**
 * Creates a Sentiance user if one does not yet exist, and links it to your app's user on the Sentiance platform.
 * This method requires an initialized SDK, otherwise it throws a runtime exception.
 *
 * @param {UserCreationOptions} userCreationOptions
 *
 * @example
 * const userCreationOptions = {
 *  authCode: "<AUTH_CODE>"
 * }
 *
 * createUser(userCreationOptions);
 *
 * Or with appId / appSecret (Not recommended)
 *
 * const userCreationOptions = {
 *    linker : (installId) => {
 *      return linkUser(installId);
 *    },
 *    appId : "<APP_ID>",
 *    appSecret: "<APP_SECRET>"
 *  }
 *
 *  createUser(userCreationOptions);
 */
const createUser = async (userCreationOptions) => {
  const appId = userCreationOptions.appId;
  const appSecret = userCreationOptions.appSecret;
  const authCode = userCreationOptions.authCode;
  const platformUrl = userCreationOptions.platformUrl;
  const linker = userCreationOptions.linker;

  if (!appId && !appSecret && !authCode) {
    return Promise.reject('Invalid userCreationOptions passed, please set authCode or  appId/appSecret with a linker to create user');
  }

  if (authCode) {
    return core.createLinkedUserWithAuthCode(authCode, platformUrl);
  }
  else if (linker) {
    core._addUserLinkListener(linker);
    return core.createLinkedUser(appId, appSecret, platformUrl);
  }
  else {
    return core.createUnlinkedUser(appId, appSecret, platformUrl);
  }
}

const addSdkStatusUpdateListener = core._addSdkStatusUpdateListener;
const addUserLinkListener = core._addUserLinkListener;
const addOnDetectionsEnabledListener = core._addOnDetectionsEnabledListener;
const addSdkUserActivityUpdateListener = core._addSdkUserActivityUpdateListener;

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
  createUser,
  linkUser,
  linkUserWithAuthCode,
  addSdkStatusUpdateListener,
  addUserLinkListener,
  addOnDetectionsEnabledListener,
  addSdkUserActivityUpdateListener,
  transportModes
};
