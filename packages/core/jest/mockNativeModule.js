import {mockNativeModule} from "../../../jest/mockNativeModules";

export function mockNativeCoreModule(platform, module) {
  return mockNativeModule(
    platform,
    {
      androidName: 'SentianceCore',
      iosName: 'SentianceCore'
    },
    module);
}
