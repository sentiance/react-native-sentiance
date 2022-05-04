import {Platform} from 'react-native';

const {NativeModules, NativeEventEmitter} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")
const {SentianceCrashDetection, SentianceCore} = NativeModules;
const SDK_VEHICLE_CRASH_EVENT = "SENTIANCE_VEHICLE_CRASH_EVENT";

var crashDetectionModule
if (Platform.OS === 'android') {
  if (!SentianceCrashDetection) {
    const nativeModuleName = varToString({SentianceCrashDetection});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  crashDetectionModule = SentianceCrashDetection
} else {
  if (!SentianceCore) {
    const nativeModuleName = varToString({SentianceCore});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  crashDetectionModule = SentianceCore
}


const SENTIANCE_EMITTER = new NativeEventEmitter(crashDetectionModule);

const _addVehicleCrashEventListener = (onVehicleCrashEvent) => {
  return SENTIANCE_EMITTER.addListener(SDK_VEHICLE_CRASH_EVENT, async (data) => {
    onVehicleCrashEvent(data);
  });
};

crashDetectionModule._addVehicleCrashEventListener = _addVehicleCrashEventListener;


export default crashDetectionModule;
