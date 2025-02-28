import eventTimelineNativeModule from "./event-timeline-native-module";
import type { Event, TransportTags } from "./types";
import EventTimelineEventEmitter, { type TimelineUpdateListener } from "./timeline-event-emitter";
import { type EmitterSubscription } from "react-native";
import { E_TRANSPORT_TAG_ERROR, isErrorWithCodeAndMsg, TransportTaggingError } from "../errors";

const nativeModule = eventTimelineNativeModule();
const emitter = new EventTimelineEventEmitter(nativeModule);

export const getTimelineUpdates = (
  afterEpochTimeMs: number,
  includeProvisionalEvents = false
): Promise<Event[]> => nativeModule.getTimelineUpdates(afterEpochTimeMs, includeProvisionalEvents);

export const getTimelineEvents = (
  fromEpochTimeMs: number,
  toEpochTimeMs: number,
  includeProvisionalEvents = false
): Promise<Event[]> => nativeModule.getTimelineEvents(fromEpochTimeMs, toEpochTimeMs, includeProvisionalEvents);

export const getTimelineEvent = (eventId: string): Promise<Event | null> => nativeModule.getTimelineEvent(eventId);

export const setTransportTags = async (tags: TransportTags): Promise<void> => {
  try {
    return await nativeModule.setTransportTags(tags);
  } catch (e: unknown) {
    if (isErrorWithCodeAndMsg(e) && e.code == E_TRANSPORT_TAG_ERROR) {
      throw new TransportTaggingError(e.message);
    }
    throw e;
  }
};

export const addTimelineUpdateListener = (
  listener: TimelineUpdateListener,
  includeProvisionalEvents: boolean = false
): Promise<EmitterSubscription> => emitter.addEventTimelineListener(listener, includeProvisionalEvents);
