const { NativeModules, Platform } = require("react-native");
const { varToString } = require("@sentiance-react-native/core/lib/generated/utils");
const SentianceEventEmitter = require("@sentiance-react-native/core/lib/generated/sentiance-event-emitter").default;
const { SentianceDrivingInsights, SentianceCore } = NativeModules;
const DRIVING_INSIGHTS_READY_EVENT = "SENTIANCE_DRIVING_INSIGHTS_READY_EVENT";

let didLocateNativeModule = true;
let drivingInsightsModule = {};
if (Platform.OS === "android") {
  if (!SentianceDrivingInsights) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceDrivingInsights });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    drivingInsightsModule = SentianceDrivingInsights;
  }
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceCore });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    drivingInsightsModule = SentianceCore;
  }
}

if (didLocateNativeModule) {
  const emitter = new SentianceEventEmitter(drivingInsightsModule);

  drivingInsightsModule._addDrivingInsightsReadyListener = (onDrivingInsightsReady) =>
    emitter.addListener(DRIVING_INSIGHTS_READY_EVENT, onDrivingInsightsReady);
}

module.exports = drivingInsightsModule;
module.exports.events = {
  DRIVING_INSIGHTS_READY_EVENT
};
