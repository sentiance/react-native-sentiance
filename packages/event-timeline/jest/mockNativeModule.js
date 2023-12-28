import {mockNativeModule} from "../../../jest/mockNativeModules";

export function mockNativeEventTimelineModule(platform, module) {
  return mockNativeModule(
    platform,
    {
      androidName: 'SentianceEventTimeline',
      iosName: 'SentianceCore'
    },
    module);
}
