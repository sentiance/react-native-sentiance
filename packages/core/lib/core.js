const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("./generated/utils");
const {SentianceCore} = NativeModules;

const SDK_STATUS_UPDATE_EVENT = "SENTIANCE_STATUS_UPDATE_EVENT";
const SDK_USER_LINK_EVENT = "SENTIANCE_USER_LINK_EVENT";
const SDK_USER_ACTIVITY_UPDATE_EVENT = "SENTIANCE_USER_ACTIVITY_UPDATE_EVENT";
const SDK_ON_TRIP_TIMED_OUT_EVENT = "SENTIANCE_ON_TRIP_TIMED_OUT_EVENT";

let coreModule = {};
if (!SentianceCore) {
  const nativeModuleName = varToString({SentianceCore});
  console.error(`Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`);
} else {
  const SENTIANCE_EMITTER = new NativeEventEmitter(SentianceCore);

  const _addSdkStatusUpdateListener = async (onSdkStatusUpdated) => {
    await SentianceCore.listenSdkStatusUpdates();
    return SENTIANCE_EMITTER.addListener(SDK_STATUS_UPDATE_EVENT, (sdkStatus) => {
      onSdkStatusUpdated(sdkStatus);
    });
  };

  const _addUserLinkListener = async (linker) => {
    const subscription = SENTIANCE_EMITTER.addListener(SDK_USER_LINK_EVENT, async (data) => {
      const {installId} = data;
      const linkingResult = await linker(installId);
      if (typeof linkingResult != "boolean") {
        console.error('Expected linker result of type boolean, got: ' + typeof linkingResult);
        SentianceCore.userLinkCallback(false);
      } else {
        SentianceCore.userLinkCallback(linkingResult);
      }
      subscription.remove();
    });
  };

  const _addSdkUserActivityUpdateListener = async (onUserActivityUpdated) => {
    await SentianceCore.listenUserActivityUpdates();
    return SENTIANCE_EMITTER.addListener(SDK_USER_ACTIVITY_UPDATE_EVENT, (data) => {
      onUserActivityUpdated(data);
    });
  };

  const _addTripTimeoutListener = async (onTripTimedOut) => {
    await SentianceCore.listenTripTimeout();
    return SENTIANCE_EMITTER.addListener(SDK_ON_TRIP_TIMED_OUT_EVENT, onTripTimedOut);
  };

  SentianceCore._addSdkStatusUpdateListener = _addSdkStatusUpdateListener;
  SentianceCore._addUserLinkListener = _addUserLinkListener;
  SentianceCore._addSdkUserActivityUpdateListener = _addSdkUserActivityUpdateListener;
  SentianceCore._addTripTimeoutListener = _addTripTimeoutListener;
  coreModule = SentianceCore;
}

module.exports = coreModule;
