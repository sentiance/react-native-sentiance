import { isValidNativeModule, type NativeModule } from "@sentiance-react-native/core/lib/generated/native-module";
import requireNativeModule from "@sentiance-react-native/core/lib/generated/require-native-module";
import type { Event, TransportTags } from "./types";

export const NATIVE_MODULE_NAME = "SentianceEventTimeline";

export interface EventTimelineModule extends NativeModule {
  getTimelineUpdates(afterEpochTimeMs: number, includeProvisionalEvents: boolean): Promise<Event[]>;

  getTimelineEvents(fromEpochTimeMs: number, toEpochTimeMs: number, includeProvisionalEvents: boolean): Promise<Event[]>;

  getTimelineEvent(eventId: string): Promise<Event | null>;

  setTransportTags(tags: TransportTags): Promise<void>;
}

export default function(): EventTimelineModule {
  const module = requireNativeModule<EventTimelineModule>({
    androidName: NATIVE_MODULE_NAME,
    isModuleVerified: function(unverifiedModule): unverifiedModule is EventTimelineModule {
      return (
        typeof unverifiedModule.getTimelineUpdates === "function" &&
        typeof unverifiedModule.getTimelineEvents === "function" &&
        typeof unverifiedModule.getTimelineEvent === "function" &&
        typeof unverifiedModule.setTransportTags === "function"
      ) && isValidNativeModule(unverifiedModule);
    }
  });

  if (!module) {
    throw new Error("Could not locate the event timeline native module.");
  }

  return module;
}
