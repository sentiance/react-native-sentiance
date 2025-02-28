import { SentianceEventTimeline } from "./timeline/types";
import feedbackApi from "./feedback";
import {
  addTimelineUpdateListener,
  getTimelineEvent,
  getTimelineEvents,
  getTimelineUpdates,
  setTransportTags
} from "./timeline";

export * from "./types";
export * from "./timeline";
export * from "./feedback";

const module: SentianceEventTimeline = {
  getTimelineUpdates,
  getTimelineEvents,
  getTimelineEvent,
  setTransportTags,
  addTimelineUpdateListener,
  sentianceFeedback: feedbackApi
};

export default module;
