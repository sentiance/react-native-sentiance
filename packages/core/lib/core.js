const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("./utils");
const {SentianceCore} = NativeModules;

const SDK_STATUS_UPDATE_EVENT = "SENTIANCE_STATUS_UPDATE_EVENT";
const SDK_USER_LINK_EVENT = "SENTIANCE_USER_LINK_EVENT";
const SDK_ON_DETECTIONS_ENABLED_EVENT = "SENTIANCE_ON_DETECTIONS_ENABLED_EVENT";
const SDK_USER_ACTIVITY_UPDATE_EVENT = "SENTIANCE_USER_ACTIVITY_UPDATE_EVENT";

if (!SentianceCore) {
  const nativeModuleName = varToString({SentianceCore});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

const SENTIANCE_EMITTER = new NativeEventEmitter(SentianceCore);

const _addSdkStatusUpdateListener = (onSdkStatusUpdated) => {
  return SENTIANCE_EMITTER.addListener(SDK_STATUS_UPDATE_EVENT, async (sdkStatus) => {
    onSdkStatusUpdated(sdkStatus);
  });
};

const _addUserLinkListener = (linker) => {
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

const _addOnDetectionsEnabledListener = (onDetectionsEnabled) => {
  return SENTIANCE_EMITTER.addListener(SDK_ON_DETECTIONS_ENABLED_EVENT, async (data) => {
    onDetectionsEnabled(data);
  });
};

const _addSdkUserActivityUpdateListener = (onUserActivityUpdated) => {
  return SENTIANCE_EMITTER.addListener(SDK_USER_ACTIVITY_UPDATE_EVENT, async (data) => {
    onUserActivityUpdated(data);
  });
};

SentianceCore._addSdkStatusUpdateListener = _addSdkStatusUpdateListener;
SentianceCore._addUserLinkListener = _addUserLinkListener;
SentianceCore._addOnDetectionsEnabledListener = _addOnDetectionsEnabledListener;
SentianceCore._addSdkUserActivityUpdateListener = _addSdkUserActivityUpdateListener;

export default SentianceCore;
