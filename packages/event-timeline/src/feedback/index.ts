import feedbackNativeModule from "./feedback-native-module";
import { isValidOccupantRoleFeedback, type OccupantRoleFeedback, type OccupantRoleFeedbackResult } from "./types";

const nativeModule = feedbackNativeModule();

export const submitOccupantRoleFeedback = (
  transportId: string,
  occupantRoleFeedback: OccupantRoleFeedback
): Promise<OccupantRoleFeedbackResult> => {
  if (!isValidOccupantRoleFeedback(occupantRoleFeedback)) {
    throw new Error("Invalid feedback type: " + occupantRoleFeedback);
  }
  return nativeModule.submitOccupantRoleFeedback(transportId, occupantRoleFeedback)
};

export default {
  submitOccupantRoleFeedback
};
