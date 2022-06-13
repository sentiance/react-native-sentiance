const {NativeModules, NativeEventEmitter, Platform} = require("react-native");
const {varToString} = require("@sentiance-react-native/core/lib/utils")
const {SentianceUserContext, SentianceCore} = NativeModules;

const SDK_USER_CONTEXT_UPDATE_EVENT = "SENTIANCE_USER_CONTEXT_UPDATE_EVENT";

let didLocateNativeModule = true;
var userContextModule
if (Platform.OS === 'android') {
  if (!SentianceUserContext) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceUserContext});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  }
  userContextModule = SentianceUserContext
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceCore});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  }
  userContextModule = SentianceCore
}

if (didLocateNativeModule) {
  const SENTIANCE_EMITTER = new NativeEventEmitter(userContextModule);

  userContextModule._addUserContextUpdateListener = async (onUserContextUpdated) => {
    await userContextModule.listenUserContextUpdates();
    return SENTIANCE_EMITTER.addListener(SDK_USER_CONTEXT_UPDATE_EVENT, (data) => {
      onUserContextUpdated(data);
    });
  };
}

export default userContextModule;
