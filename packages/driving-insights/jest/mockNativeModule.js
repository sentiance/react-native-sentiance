import {mockNativeModule} from "../../../jest/mockNativeModules";

export function mockNativeDrivingInsightsModule(platform, module) {
  return mockNativeModule(
    platform,
    {
      androidName: 'SentianceDrivingInsights',
      iosName: 'SentianceCore'
    },
    module);
}
