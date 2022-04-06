const { NativeModules, NativeEventEmitter } = require("react-native");

const { RNSentiance } = NativeModules;

const SENTIANCE_EMITTER = new NativeEventEmitter(RNSentiance);

const KEY_SENTIANCE_SDK_IS_READY_FOR_BACKGROUND =
  "SENTIANCE_SDK_IS_READY_FOR_BACKGROUND";
const KEY_SENTIANCE_SDK_IS_DISABLED = "SENTIANCE_SDK_IS_DISABLED";

const SENTIANCE_STORE_KEYS = [
  "SENTIANCE_SDK_APP_ID",
  "SENTIANCE_SDK_APP_SECRET",
  "SENTIANCE_SDK_APP_BASE_URL",
  KEY_SENTIANCE_SDK_IS_READY_FOR_BACKGROUND,
  KEY_SENTIANCE_SDK_IS_DISABLED,
];

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

const createUserExperimental = async (configuration) => {
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

  return new Promise(async (resolve, reject) => {
    SENTIANCE_EMITTER.addListener("SDKUserLink", (data) => {
      linker(data, async (isDone) => {
        if (isDone === false) {
          RNSentiance.userLinkCallback(false);
          reject();
          return;
        }

        RNSentiance.userLinkCallback(true);
        RNSentiance.setValueForKey(
          KEY_SENTIANCE_SDK_IS_READY_FOR_BACKGROUND,
          "YES"
        );
      });
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
const resetExperimental = () => {
  SENTIANCE_STORE_KEYS.forEach(async (key) => {
    RNSentiance.setValueForKey(key, "");
  });
  RNSentiance.reset();
};

/**
 * Stop the SDK and set empties the "IS READY" flag
 *
 * This ensure that the SDK will not collect data while in the backrgound. The
 * application would need to call "enableExperimental" when it want to resume
 * data collection
 */
const disableExperimental = async () => {
  RNSentiance.setValueForKey(KEY_SENTIANCE_SDK_IS_DISABLED, "YES");
  await RNSentiance.stop();
};

/**
 * Restarts the SDK.
 *
 * This assumes that the SDK is already INITIALIZED. It sets the "IS READY" flag
 * allowing the SDK to resume data collection while in the background.
 */
const enableExperimental = async () => {
  await RNSentiance.start();
  RNSentiance.setValueForKey(KEY_SENTIANCE_SDK_IS_DISABLED, "");
};

module.exports = {
  createUserExperimental,
  resetExperimental,
  disableExperimental,
  enableExperimental,
};
