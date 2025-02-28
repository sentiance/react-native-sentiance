import { OCCUPANT_ROLE_VALUES, type OccupantRole } from "../timeline/types";

export type OccupantRoleFeedbackResult =
  | "ACCEPTED"
  | "TRANSPORT_IS_PROVISIONAL"
  | "TRANSPORT_TYPE_NOT_SUPPORTED"
  | "TRANSPORT_NOT_FOUND"
  | "TRANSPORT_NOT_YET_COMPLETE"
  | "FEEDBACK_ALREADY_PROVIDED"
  | "UNEXPECTED_ERROR";

/**
 * @internal
 */
const OCCUPANT_ROLE_FEEDBACK_VALUES = OCCUPANT_ROLE_VALUES.filter(
  (role): role is Exclude<OccupantRole, "UNAVAILABLE"> => role !== "UNAVAILABLE"
);
/**
 * @internal
 */
const validOccupantRoleFeedbackValues = new Set(OCCUPANT_ROLE_FEEDBACK_VALUES);
export type OccupantRoleFeedback = (typeof OCCUPANT_ROLE_FEEDBACK_VALUES)[number];

export function isValidOccupantRoleFeedback(
  value: unknown
): value is OccupantRoleFeedback {
  return validOccupantRoleFeedbackValues.has(value as OccupantRoleFeedback);
}

export interface SentianceFeedback {
  submitOccupantRoleFeedback(transportId: string, occupantRoleFeedback: OccupantRoleFeedback): Promise<OccupantRoleFeedbackResult>;
}
