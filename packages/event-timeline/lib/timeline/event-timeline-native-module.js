"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.NATIVE_MODULE_NAME = void 0;
const native_module_1 = require("@sentiance-react-native/core/lib/generated/native-module");
const require_native_module_1 = __importDefault(require("@sentiance-react-native/core/lib/generated/require-native-module"));
exports.NATIVE_MODULE_NAME = "SentianceEventTimeline";
function default_1() {
    const module = (0, require_native_module_1.default)({
        androidName: exports.NATIVE_MODULE_NAME,
        isModuleVerified: function (unverifiedModule) {
            return (typeof unverifiedModule.getTimelineUpdates === "function" &&
                typeof unverifiedModule.getTimelineEvents === "function" &&
                typeof unverifiedModule.getTimelineEvent === "function" &&
                typeof unverifiedModule.setTransportTags === "function") && (0, native_module_1.isValidNativeModule)(unverifiedModule);
        }
    });
    if (!module) {
        throw new Error("Could not locate the event timeline native module.");
    }
    return module;
}
exports.default = default_1;
