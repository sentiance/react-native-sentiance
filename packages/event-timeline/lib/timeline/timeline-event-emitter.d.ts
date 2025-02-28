import { type EventTimelineModule } from "./event-timeline-native-module";
import SentianceEventEmitter from "@sentiance-react-native/core/lib/generated/sentiance-event-emitter";
import { type EmitterSubscription } from "react-native";
import { type Event } from "./types";
export type TimelineUpdateListener = (event: Event) => void;
export declare const TIMELINE_UPDATE_EVENT = "SENTIANCE_TIMELINE_UPDATE_EVENT";
export default class EventTimelineEventEmitter extends SentianceEventEmitter {
    constructor(nativeModule: EventTimelineModule);
    addEventTimelineListener(listener: TimelineUpdateListener, includeProvisionalEvents: boolean): Promise<EmitterSubscription>;
}
