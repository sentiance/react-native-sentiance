const {NativeModules} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")

const {SentianceCrashDetection} = NativeModules;

if (!SentianceCrashDetection) {
  const nativeModuleName = varToString({SentianceCrashDetection});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

export default SentianceCrashDetection;
