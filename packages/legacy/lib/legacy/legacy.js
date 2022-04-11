const {NativeModules} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")
const {RNSentiance} = NativeModules;

if (!RNSentiance) {
  const nativeModuleName = varToString({RNSentiance});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

export default RNSentiance;
