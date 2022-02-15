const { NativeModules, NativeEventEmitter } = require("react-native");

const { RNSentiance } = NativeModules;

const SENTIANCE_EMITTER = new NativeEventEmitter(RNSentiance);
const SENTIANCE_STORE_KEYS = [
  "SENTIANCE_SDK_APP_ID",
  "SENTIANCE_SDK_APP_SECRET",
  "SENTIANCE_SDK_APP_BASE_URL",
  "SENTIANCE_SDK_IS_READY_FOR_BACKGROUND",
];

RNSentiance.TransportMode = {};
(function (TransportMode) {
  TransportMode[(TransportMode["UNKNOWN"] = 1)] = "UNKNOWN";
  TransportMode[(TransportMode["CAR"] = 2)] = "CAR";
  TransportMode[(TransportMode["BICYCLE"] = 3)] = "BICYCLE";
  TransportMode[(TransportMode["ON_FOOT"] = 4)] = "ON_FOOT";
  TransportMode[(TransportMode["TRAIN"] = 5)] = "TRAIN";
  TransportMode[(TransportMode["TRAM"] = 6)] = "TRAM";
  TransportMode[(TransportMode["BUS"] = 7)] = "BUS";
  TransportMode[(TransportMode["PLANE"] = 8)] = "PLANE";
  TransportMode[(TransportMode["BOAT"] = 9)] = "BOAT";
  TransportMode[(TransportMode["METRO"] = 10)] = "METRO";
  TransportMode[(TransportMode["RUNNING"] = 11)] = "RUNNING";
})(RNSentiance.TransportMode);

/**
 * @typedef {Object} CreateUserConfiguration
 * @property {Object} credentials - Sentiance APP Credentials
 * @property {String} credentials.appId - APP ID
 * @property {String} credentials.appSecret - APP Secret
 * @property {String} credentials.baseUrl - Sentiance Base URL
 * @property {Function} linker - Function to handle the user linking
 */

/**
 * CreateUser setups the credentials for the SDK and initializes the SDK.
 *
 * The application is expected to call "createUser" at the time when it requires
 * the detections to start. However, it is expected to explicity call "start"
 * after the "createUser" method
 *
 * @example
 *   await RNSentiance.createUser({ credentials: ..., linker: ...});
 *   await RNSentiance.start()
 *
 * @param {CreateUserConfiguration} configuration
 */
const createUser = (configuration) => {};

RNSentiance.createUser = async (configuration) => {
  RNSentiance.setValueForKey("SENTIANCE_SDK_IS_READY_FOR_BACKGROUND", "");

  const { credentials, linker } = configuration;
  const baseUrl = credentials.baseUrl ?? null;
  const { appId, appSecret } = credentials;

  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_ID", appId);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_SECRET", appSecret);
  RNSentiance.setValueForKey("SENTIANCE_SDK_APP_BASE_URL", baseUrl);

  if (!linker) {
    await RNSentiance.init(
      credentials.appId,
      credentials.appSecret,
      baseUrl,
      false
    );
    await RNSentiance.setValueForKey(
      "SENTIANCE_SDK_IS_READY_FOR_BACKGROUND",
      "YES"
    );
    return Promise.resolve();
  }

  return new Promise(async (resolve, _reject) => {
    SENTIANCE_EMITTER.addListener("SDKUserLink", (data) => {
      linker(
        data,
        () => {
          RNSentiance.userLinkCallback(true);
          RNSentiance.setValueForKey(
            "SENTIANCE_SDK_IS_READY_FOR_BACKGROUND",
            "YES"
          );
        },
        () => {
          RNSentiance.userLinkCallback(false);
        }
      );
    });

    await RNSentiance.initWithUserLinkingEnabled(
      appId,
      appSecret,
      baseUrl,
      false
    );
    resolve();
  });
};

/**
 * Clears the state variables and resets the SDK managed by the "createUser"
 *
 * This method is intended to be used when a user is logged out or whenever the
 * SDK is meant to be reset.
 */
const clear = () => {
  SENTIANCE_STORE_KEYS.forEach(async (key) => {
    RNSentiance.setValueForKey(key, "");
  });
  RNSentiance.reset();
};

RNSentiance.createUser = createUser;
RNSentiance.clear = clear;

module.exports = RNSentiance;
