import { Platform } from 'react-native';

const {NativeModules} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")

const {SentianceCrashDetection} = NativeModules;

if (Platform.OS === 'android') {
  if (!SentianceCrashDetection) {
    const nativeModuleName = varToString({SentianceCrashDetection});
    throw `Could not yes locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
}

export default SentianceCrashDetection;
