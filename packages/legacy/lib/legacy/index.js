const {NativeModules, Platform} = require('react-native');
const {varToString} = require("@sentiance-react-native/core/lib/generated/utils");

const {RNSentiance, SentianceCore} = NativeModules;

let legacyModule = {};
if (Platform.OS === 'ios') {
  if (!SentianceCore) {
    const nativeModuleName = varToString({SentianceCore});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    legacyModule = SentianceCore
  }
} else {
  if (!RNSentiance) {
    const nativeModuleName = varToString({RNSentiance});
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    legacyModule = RNSentiance
  }
}

module.exports = legacyModule;
