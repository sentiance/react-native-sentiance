import {mockNativeModule} from "../../../jest/mockNativeModules";

export function mockNativeUserContextModule(platform, module) {
  return mockNativeModule(
    platform,
    {
      androidName: 'SentianceUserContext',
      iosName: 'SentianceCore'
    },
    module);
}
