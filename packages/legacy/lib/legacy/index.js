import { Platform } from 'react-native';
import {NativeModules} from 'react-native'
import {varToString} from '@react-native-sentiance/core/lib/utils'

const {RNSentiance,SentianceCore} = NativeModules;

var legacyModule
if (Platform.OS === 'ios') {
  if (!SentianceCore) {
    const nativeModuleName = varToString({SentianceCore});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  legacyModule = SentianceCore
}
else {
  if (!RNSentiance) {
    const nativeModuleName = varToString({RNSentiance});
    throw `Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`;
  }
  legacyModule = RNSentiance
}

export default legacyModule