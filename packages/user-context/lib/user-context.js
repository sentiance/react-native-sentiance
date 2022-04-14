const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")
const {SentianceUserContext} = NativeModules;

if (!SentianceUserContext) {
  const nativeModuleName = varToString({SentianceUserContext});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

const SENTIANCE_EMITTER = new NativeEventEmitter(SentianceUserContext);

const _addUserContextUpdateListener = (onUserContextUpdated) => {
  return SENTIANCE_EMITTER.addListener("UserContextUpdateEvent", async (data) => {
    onUserContextUpdated(data);
  });
};

SentianceUserContext._addUserContextUpdateListener = _addUserContextUpdateListener;

export default SentianceUserContext;