import type { Event, TransportTags } from "./types";
import { type TimelineUpdateListener } from "./timeline-event-emitter";
import { type EmitterSubscription } from "react-native";
export declare const getTimelineUpdates: (afterEpochTimeMs: number, includeProvisionalEvents?: boolean) => Promise<Event[]>;
export declare const getTimelineEvents: (fromEpochTimeMs: number, toEpochTimeMs: number, includeProvisionalEvents?: boolean) => Promise<Event[]>;
export declare const getTimelineEvent: (eventId: string) => Promise<Event | null>;
export declare const setTransportTags: (tags: TransportTags) => Promise<void>;
export declare const addTimelineUpdateListener: (listener: TimelineUpdateListener, includeProvisionalEvents?: boolean) => Promise<EmitterSubscription>;
