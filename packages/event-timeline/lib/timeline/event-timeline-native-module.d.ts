import { type NativeModule } from "@sentiance-react-native/core/lib/generated/native-module";
import type { Event, TransportTags } from "./types";
export declare const NATIVE_MODULE_NAME = "SentianceEventTimeline";
export interface EventTimelineModule extends NativeModule {
    getTimelineUpdates(afterEpochTimeMs: number, includeProvisionalEvents: boolean): Promise<Event[]>;
    getTimelineEvents(fromEpochTimeMs: number, toEpochTimeMs: number, includeProvisionalEvents: boolean): Promise<Event[]>;
    getTimelineEvent(eventId: string): Promise<Event | null>;
    setTransportTags(tags: TransportTags): Promise<void>;
}
export default function (): EventTimelineModule;
