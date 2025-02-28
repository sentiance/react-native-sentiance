"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TIMELINE_UPDATE_EVENT = void 0;
const sentiance_event_emitter_1 = __importDefault(require("@sentiance-react-native/core/lib/generated/sentiance-event-emitter"));
exports.TIMELINE_UPDATE_EVENT = "SENTIANCE_TIMELINE_UPDATE_EVENT";
class EventTimelineEventEmitter extends sentiance_event_emitter_1.default {
    constructor(nativeModule) {
        super(nativeModule);
    }
    addEventTimelineListener(listener, includeProvisionalEvents) {
        // This instructs our native code to always register a listener that gets notified with all
        // sorts of events (provisional or not), so that we could then filter out the received events
        // and re-dispatch them to the appropriate JS callbacks.
        const context = {
            includeProvisionalEvents: true
        };
        return this.addListener(exports.TIMELINE_UPDATE_EVENT, (event) => {
            if (includeProvisionalEvents || !event.isProvisional) {
                listener(event);
            }
        }, context);
    }
}
exports.default = EventTimelineEventEmitter;
