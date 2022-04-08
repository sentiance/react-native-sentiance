const {NativeModules} = require("react-native");
const {RNSentiance} = NativeModules;

const init = (appId, appSecret, baseURL, shouldStart) => RNSentiance.init(appId, appSecret, baseURL, shouldStart);
const initWithUserLinkingEnabled = (appId, appSecret, baseURL, shouldStart) =>
  RNSentiance.initWithUserLinkingEnabled(appId, appSecret, baseURL, shouldStart);
const reset = () => RNSentiance.reset();
const start = () => RNSentiance.start();
const startWithStopDate = (stopEpochTimeMs) => RNSentiance.startWithStopDate(stopEpochTimeMs);
const stop = () => RNSentiance.stop();
const setValueForKey = (key, value) => RNSentiance.setValueForKey(key, value);
const getValueForKey = (key, defaultValue) => RNSentiance.getValueForKey(key, defaultValue);
const isThirdPartyLinked = () => RNSentiance.isThirdPartyLinked();
const isNativeInitializationEnabled = () => RNSentiance.isNativeInitializationEnabled();
const enableNativeInitialization = () => RNSentiance.enableNativeInitialization();
const disableNativeInitialization = () => RNSentiance.disableNativeInitialization();

module.exports = {
  init,
  initWithUserLinkingEnabled,
  reset,
  start,
  startWithStopDate,
  stop,
  setValueForKey,
  getValueForKey,
  isThirdPartyLinked,
  isNativeInitializationEnabled,
  enableNativeInitialization,
  disableNativeInitialization
}
