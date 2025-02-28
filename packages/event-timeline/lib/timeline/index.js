"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.addTimelineUpdateListener = exports.setTransportTags = exports.getTimelineEvent = exports.getTimelineEvents = exports.getTimelineUpdates = void 0;
const event_timeline_native_module_1 = __importDefault(require("./event-timeline-native-module"));
const timeline_event_emitter_1 = __importDefault(require("./timeline-event-emitter"));
const errors_1 = require("../errors");
const nativeModule = (0, event_timeline_native_module_1.default)();
const emitter = new timeline_event_emitter_1.default(nativeModule);
const getTimelineUpdates = (afterEpochTimeMs, includeProvisionalEvents = false) => nativeModule.getTimelineUpdates(afterEpochTimeMs, includeProvisionalEvents);
exports.getTimelineUpdates = getTimelineUpdates;
const getTimelineEvents = (fromEpochTimeMs, toEpochTimeMs, includeProvisionalEvents = false) => nativeModule.getTimelineEvents(fromEpochTimeMs, toEpochTimeMs, includeProvisionalEvents);
exports.getTimelineEvents = getTimelineEvents;
const getTimelineEvent = (eventId) => nativeModule.getTimelineEvent(eventId);
exports.getTimelineEvent = getTimelineEvent;
const setTransportTags = (tags) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        return yield nativeModule.setTransportTags(tags);
    }
    catch (e) {
        if ((0, errors_1.isErrorWithCodeAndMsg)(e) && e.code == errors_1.E_TRANSPORT_TAG_ERROR) {
            throw new errors_1.TransportTaggingError(e.message);
        }
        throw e;
    }
});
exports.setTransportTags = setTransportTags;
const addTimelineUpdateListener = (listener, includeProvisionalEvents = false) => emitter.addEventTimelineListener(listener, includeProvisionalEvents);
exports.addTimelineUpdateListener = addTimelineUpdateListener;
