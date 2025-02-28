import { type OccupantRoleFeedback, type OccupantRoleFeedbackResult } from "./types";
export declare const submitOccupantRoleFeedback: (transportId: string, occupantRoleFeedback: OccupantRoleFeedback) => Promise<OccupantRoleFeedbackResult>;
declare const _default: {
    submitOccupantRoleFeedback: (transportId: string, occupantRoleFeedback: "DRIVER" | "PASSENGER") => Promise<OccupantRoleFeedbackResult>;
};
export default _default;
