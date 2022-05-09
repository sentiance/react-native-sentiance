import Platform from 'react-native';
const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")
const {SentianceUserContext} = NativeModules;


const SDK_USER_CONTEXT_UPDATE_EVENT = "SENTIANCE_USER_CONTEXT_UPDATE_EVENT";

var userContextModule
if (Platform.OS === 'android') {
  if (!SentianceUserContext) {
    const nativeModuleName = varToString({SentianceUserContext});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  userContextModule = SentianceUserContext
} else {
  if (!SentianceCore) {
    const nativeModuleName = varToString({SentianceCore});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  userContextModule = SentianceCore
}

const SENTIANCE_EMITTER = new NativeEventEmitter(userContextModule);

const _addUserContextUpdateListener = (onUserContextUpdated) => {
  return SENTIANCE_EMITTER.addListener(SDK_USER_CONTEXT_UPDATE_EVENT, async (data) => {
    onUserContextUpdated(data);
  });
};

userContextModule._addUserContextUpdateListener = _addUserContextUpdateListener;

export default userContextModule;