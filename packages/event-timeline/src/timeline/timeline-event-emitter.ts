import { type EventTimelineModule } from "./event-timeline-native-module";
import SentianceEventEmitter from "@sentiance-react-native/core/lib/generated/sentiance-event-emitter";
import { type EmitterSubscription } from "react-native";
import { type Event } from "./types";

export type TimelineUpdateListener = (event: Event) => void;
export const TIMELINE_UPDATE_EVENT = "SENTIANCE_TIMELINE_UPDATE_EVENT";

export default class EventTimelineEventEmitter extends SentianceEventEmitter<EventTimelineModule> {

  constructor(nativeModule: EventTimelineModule) {
    super(nativeModule);
  }

  addEventTimelineListener(listener: TimelineUpdateListener, includeProvisionalEvents: boolean): Promise<EmitterSubscription> {
    // This instructs our native code to always register a listener that gets notified with all
    // sorts of events (provisional or not), so that we could then filter out the received events
    // and re-dispatch them to the appropriate JS callbacks.
    const context = {
      includeProvisionalEvents: true
    };

    return this.addListener(
      TIMELINE_UPDATE_EVENT,
      (event: Event) => {
        if (includeProvisionalEvents || !event.isProvisional) {
          listener(event);
        }
      },
      context
    );
  }
}
