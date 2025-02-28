"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.isValidOccupantRoleFeedback = void 0;
const types_1 = require("../timeline/types");
/**
 * @internal
 */
const OCCUPANT_ROLE_FEEDBACK_VALUES = types_1.OCCUPANT_ROLE_VALUES.filter((role) => role !== "UNAVAILABLE");
/**
 * @internal
 */
const validOccupantRoleFeedbackValues = new Set(OCCUPANT_ROLE_FEEDBACK_VALUES);
function isValidOccupantRoleFeedback(value) {
    return validOccupantRoleFeedbackValues.has(value);
}
exports.isValidOccupantRoleFeedback = isValidOccupantRoleFeedback;
