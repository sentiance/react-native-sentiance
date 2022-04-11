const {NativeModules} = require("react-native");
const {varToString} = require("./utils");
const {SentianceCore} = NativeModules;

if (!SentianceCore) {
  const nativeModuleName = varToString({SentianceCore});
  throw `Could not locate the native ${nativeModuleName} module.
  Make sure that your native code is properly linked, and that the module name you specified is correct.`;
}

export default SentianceCore;
