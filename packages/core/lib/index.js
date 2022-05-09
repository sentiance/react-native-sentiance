import core from './core';
import Platform from 'react-native';

const enableDetections = () => core.enableDetections();
const enableDetectionsWithExpiryDate = (expiryTime) => core.enableDetectionsWithExpiryDate(expiryTime);
const disableDetections = () => core.disableDetections();
const getInitState = () => core.getInitState();
const userLinkCallback = (userLinkResult) => core.userLinkCallback(userLinkResult);
const userExists = () => core.userExists();
const isUserLinked = () => core.isUserLinked();
const getVersion = () => core.getVersion();
const getUserId = () => core.getUserId();
const requestUserAccessToken = () => core.requestUserAccessToken();
const addUserMetadataField = (label, value) => core.addUserMetadataField(label, value);
const addUserMetadataFields = (metadata) => core.addUserMetadataFields(metadata);
const removeUserMetadataField = (label) => core.removeUserMetadataField(label);
const getUserActivity = () => core.getUserActivity();
const listenUserActivityUpdates = () => core.listenUserActivityUpdates();
const isTripOngoing = (tripType) => core.isTripOngoing(tripType);
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


var startTrip
var stopTrip
var submitDetections
var reset

if (Platform.OS === 'ios') {
  startTrip = (metadata, hint) => core.startTripNewApi(metadata, hint);
  stopTrip = () => core.stopTripNewApi();
  submitDetections = () => core.submitDetectionsNewApi();
  reset = () => core.resetNewApi();
}
else {
  startTripN = (metadata, hint) => core.startTrip(metadata, hint);
  stopTrip = () => core.stopTrip();
  submitDetections = () => core.submitDetections();
  reset = () => core.reset();
}

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
  } else if (linker) {
    core._addUserLinkListener(linker);
    return core.createLinkedUser(appId, appSecret, platformUrl);
  } else {
    return core.createUnlinkedUser(appId, appSecret, platformUrl);
  }
}

const addSdkStatusUpdateListener = core._addSdkStatusUpdateListener;

/**
 This helper function allows the consumers of this module to add user linking listeners in a simpler fashion
 without having to :
 1. Specify the exact corresponding native event name that gets published by native code to let the JS side know
 that it's time to perform user linking
 2. Call userLinkCallback at the end of the process to publish user linking results back to the SDK.
 @see {@link userLinkCallback}
 */
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
  requestUserAccessToken,
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
