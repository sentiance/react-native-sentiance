import { runOnEachPlatform } from "../../../jest/test_util";
import { mockNativeFeedbackModule } from "../jest/mockNativeModule";

const VALID_OCCUPANT_ROLE_FEEDBACK_TYPES = ["DRIVER", "PASSENGER"];
const VALID_OCCUPANT_ROLE_FEEDDBACK_RESULT_TYPES = [
  "ACCEPTED",
  "TRANSPORT_TYPE_NOT_SUPPORTED",
  "TRANSPORT_NOT_FOUND",
  "TRANSPORT_NOT_YET_COMPLETE",
  "FEEDBACK_ALREADY_PROVIDED"
];

const PLATFORMS = ["android", "ios"];

describe("Feedback API tests", () => {
  beforeEach(() => jest.resetModules());

  const testCases = [];
  for (const platform of PLATFORMS) {
    for (const feedbackRole of VALID_OCCUPANT_ROLE_FEEDBACK_TYPES) {
      for (const feedbackResult of VALID_OCCUPANT_ROLE_FEEDDBACK_RESULT_TYPES) {
        testCases.push([feedbackRole, feedbackResult, platform]);
      }
    }
  }

  test.each(testCases)(
    "should submit feedback for role: %s and get result: %s on %s",
    async (feedbackRole, feedbackResult, platform) => {
      const mockSubmitOccupantRoleFeedback = jest.fn().mockResolvedValueOnce(feedbackResult);

      // Mock native feedback module
      mockNativeFeedbackModule(platform, {
        submitOccupantRoleFeedback: mockSubmitOccupantRoleFeedback
      });

      // Require API after mocking
      const { sentianceFeedback: feedbackApi } = require("../lib");

      // Call function and assert
      await expect(feedbackApi.submitOccupantRoleFeedback("", feedbackRole)).resolves.toBe(feedbackResult);
      expect(mockSubmitOccupantRoleFeedback).toHaveBeenCalledWith("", feedbackRole);
    }
  );
});