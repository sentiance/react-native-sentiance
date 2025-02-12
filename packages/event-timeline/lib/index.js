const { NativeModules, Platform } = require("react-native");
const { varToString } = require("@sentiance-react-native/core/lib/utils");
const SentianceEventEmitter = require("@sentiance-react-native/core/lib/SentianceEventEmitter");
const { createEventListener } = require("@sentiance-react-native/core/lib/SentianceEventListenerUtils");
const { TransportTaggingError, E_TRANSPORT_TAG_ERROR } = require("./errors/errors");
const { SentianceEventTimeline, SentianceCore} = NativeModules;
const sentianceFeedback = require("./feedback");


const TIMELINE_UPDATE_EVENT = "SENTIANCE_TIMELINE_UPDATE_EVENT";

let didLocateNativeModule = true;
let eventTimelineModule = {};
if (Platform.OS === "android") {
  if (!SentianceEventTimeline) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceEventTimeline });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    eventTimelineModule = SentianceEventTimeline;
  }
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceCore });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    eventTimelineModule = SentianceCore;
  }
}

if (didLocateNativeModule) {
  const emitter = new SentianceEventEmitter(eventTimelineModule);

  eventTimelineModule._addTimelineUpdateListener = async (onTimelineUpdated) => {
    return createEventListener(TIMELINE_UPDATE_EVENT, emitter, onTimelineUpdated);
  };
}

const getTimelineUpdates = (afterEpochTimeMs) => eventTimelineModule.getTimelineUpdates(afterEpochTimeMs);

const getTimelineEvents = (fromEpochTimeMs, toEpochTimeMs) => eventTimelineModule.getTimelineEvents(fromEpochTimeMs, toEpochTimeMs);

const getTimelineEvent = (eventId) => eventTimelineModule.getTimelineEvent(eventId);

const addTimelineUpdateListener = eventTimelineModule._addTimelineUpdateListener;

const setTransportTags = async (tags) => {
  try {
    return await eventTimelineModule.setTransportTags(tags);
  } catch (e) {
    if (e.code === E_TRANSPORT_TAG_ERROR) {
      throw new TransportTaggingError(e.message);
    }
    throw e;
  }
};

module.exports = {
  getTimelineUpdates,
  getTimelineEvents,
  getTimelineEvent,
  addTimelineUpdateListener,
  setTransportTags,
  sentianceFeedback
};

module.exports.events = {
  TIMELINE_UPDATE_EVENT
};
