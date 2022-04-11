const {NativeModules} = require("react-native");
const {varToString} = require("@react-native-sentiance/core/lib/utils")
const {SentianceUserContext} = NativeModules;

if (!SentianceUserContext) {
  const nativeModuleName = varToString({SentianceUserContext});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

export default SentianceUserContext;
