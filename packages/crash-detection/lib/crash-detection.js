const {NativeModules, NativeEventEmitter, Platform} = require("react-native");
const {varToString} = require("@sentiance-react-native/core/lib/generated/utils");
const {SentianceCrashDetection, SentianceCore} = NativeModules;
const SDK_VEHICLE_CRASH_EVENT = "SENTIANCE_VEHICLE_CRASH_EVENT";
const SDK_VEHICLE_CRASH_DIAGNOSTIC_EVENT = "SENTIANCE_VEHICLE_CRASH_DIAGNOSTIC_EVENT";

let didLocateNativeModule = true;
let crashDetectionModule = {};
if (Platform.OS === 'android') {
  if (!SentianceCrashDetection) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceCrashDetection});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    crashDetectionModule = SentianceCrashDetection
  }
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceCore});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else if (!SentianceCrashDetection) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({SentianceCrashDetection});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    crashDetectionModule = {
      ...SentianceCore,
      isVehicleCrashDetectionSupported: SentianceCrashDetection.isVehicleCrashDetectionSupported,
      invokeDummyVehicleCrash: SentianceCrashDetection.invokeDummyVehicleCrash,
    }
  }
}

if (didLocateNativeModule) {
  const SENTIANCE_EMITTER = new NativeEventEmitter(crashDetectionModule);

  crashDetectionModule._addVehicleCrashEventListener = async (onVehicleCrash) => {
    await crashDetectionModule.listenVehicleCrashEvents();
    return SENTIANCE_EMITTER.addListener(SDK_VEHICLE_CRASH_EVENT, (data) => {
      onVehicleCrash(data);
    });
  };

  crashDetectionModule._addVehicleCrashDiagnosticListener = async (onVehicleCrashDiagnostic) => {
    await crashDetectionModule.listenVehicleCrashDiagnostic();
    return SENTIANCE_EMITTER.addListener(SDK_VEHICLE_CRASH_DIAGNOSTIC_EVENT, (data) => {
      onVehicleCrashDiagnostic(data);
    });
  };
}

module.exports = crashDetectionModule;
