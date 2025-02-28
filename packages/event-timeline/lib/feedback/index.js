"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.submitOccupantRoleFeedback = void 0;
const feedback_native_module_1 = __importDefault(require("./feedback-native-module"));
const types_1 = require("./types");
const nativeModule = (0, feedback_native_module_1.default)();
const submitOccupantRoleFeedback = (transportId, occupantRoleFeedback) => {
    if (!(0, types_1.isValidOccupantRoleFeedback)(occupantRoleFeedback)) {
        throw new Error("Invalid feedback type: " + occupantRoleFeedback);
    }
    return nativeModule.submitOccupantRoleFeedback(transportId, occupantRoleFeedback);
};
exports.submitOccupantRoleFeedback = submitOccupantRoleFeedback;
exports.default = {
    submitOccupantRoleFeedback: exports.submitOccupantRoleFeedback
};
