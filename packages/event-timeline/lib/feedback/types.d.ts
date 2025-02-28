export type OccupantRoleFeedbackResult = "ACCEPTED" | "TRANSPORT_IS_PROVISIONAL" | "TRANSPORT_TYPE_NOT_SUPPORTED" | "TRANSPORT_NOT_FOUND" | "TRANSPORT_NOT_YET_COMPLETE" | "FEEDBACK_ALREADY_PROVIDED" | "UNEXPECTED_ERROR";
/**
 * @internal
 */
declare const OCCUPANT_ROLE_FEEDBACK_VALUES: ("DRIVER" | "PASSENGER")[];
export type OccupantRoleFeedback = (typeof OCCUPANT_ROLE_FEEDBACK_VALUES)[number];
export declare function isValidOccupantRoleFeedback(value: unknown): value is OccupantRoleFeedback;
export interface SentianceFeedback {
    submitOccupantRoleFeedback(transportId: string, occupantRoleFeedback: OccupantRoleFeedback): Promise<OccupantRoleFeedbackResult>;
}
export {};
