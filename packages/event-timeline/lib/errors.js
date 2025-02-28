"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.isErrorWithCodeAndMsg = exports.TransportTaggingError = exports.E_TRANSPORT_TAG_ERROR = void 0;
exports.E_TRANSPORT_TAG_ERROR = "E_TRANSPORT_TAG_ERROR";
class TransportTaggingError extends Error {
    constructor(message) {
        super(message);
        this.name = this.constructor.name;
        Object.setPrototypeOf(this, TransportTaggingError.prototype);
    }
}
exports.TransportTaggingError = TransportTaggingError;
function isErrorWithCodeAndMsg(e) {
    return (typeof e === "object" &&
        e !== null &&
        typeof e.code === "string" &&
        typeof e.message === "string");
}
exports.isErrorWithCodeAndMsg = isErrorWithCodeAndMsg;
