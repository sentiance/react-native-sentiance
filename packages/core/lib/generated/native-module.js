"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.isValidNativeModule = void 0;
function isValidNativeModule(obj) {
    return (typeof obj.addListener === "function" &&
        typeof obj.removeListeners === "function" &&
        typeof obj.addNativeListener === "function" &&
        typeof obj.removeNativeListener === "function");
}
exports.isValidNativeModule = isValidNativeModule;
