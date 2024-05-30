import {mockNativeModule} from "../../../jest/mockNativeModules";

export function mockNativeSmartGeofencesModule(platform, module) {
  return mockNativeModule(
    platform,
    {
      androidName: 'SentianceSmartGeofences',
      iosName: 'SentianceCore'
    },
    module);
}
