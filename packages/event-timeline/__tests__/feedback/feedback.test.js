/**
 * @description This file contains tests for the APIs related to feedback submission.
 */

const VALID_OCCUPANT_ROLE_FEEDBACK_TYPES = ["DRIVER", "PASSENGER"];
const VALID_OCCUPANT_ROLE_FEEDDBACK_RESULT_TYPES = [
  "ACCEPTED",
  "TRANSPORT_IS_PROVISIONAL",
  "TRANSPORT_TYPE_NOT_SUPPORTED",
  "TRANSPORT_NOT_FOUND",
  "TRANSPORT_NOT_YET_COMPLETE",
  "FEEDBACK_ALREADY_PROVIDED"
];

const PLATFORMS = ["android", "ios"];

describe("Feedback API tests", () => {
  beforeEach(() => {
    jest.resetModules();

    // Mock the event timeline APIs, as they are exposed as part of the public API
    // of the event timeline module and hence are a dependency.
    // The event timeline API tests are in a separate file.
    jest.doMock("../../lib/timeline", () => {
      return {};
    });
  });

  const testCases = [];
  for (const platform of PLATFORMS) {
    for (const feedbackRole of VALID_OCCUPANT_ROLE_FEEDBACK_TYPES) {
      for (const feedbackResult of VALID_OCCUPANT_ROLE_FEEDDBACK_RESULT_TYPES) {
        testCases.push([feedbackRole, feedbackResult, platform]);
      }
    }
  }

  describe("submitOccupantRoleFeedback() ", () => {
    test.each(testCases)(
      "should submit feedback for role: %s and get result: %s on %s",
      async (feedbackRole, feedbackResult, platform) => {
        const mockSubmitOccupantRoleFeedback = jest.fn()
          .mockResolvedValue(feedbackResult);

        // Mock native feedback module
        jest.doMock("../../lib/feedback/feedback-native-module", () => {
          return () => ({
            submitOccupantRoleFeedback: mockSubmitOccupantRoleFeedback
          });
        });

        const { submitOccupantRoleFeedback } = require("../../lib");

        // Call function and assert
        await expect(submitOccupantRoleFeedback("", feedbackRole)).resolves.toBe(feedbackResult);
        expect(mockSubmitOccupantRoleFeedback).toHaveBeenCalledWith("", feedbackRole);
      }
    );
  });

  describe("Named/Default import tests", () => {
    beforeEach(() => {
      // We don't need to mock the individual APIs, just the module as a whole
      // so that the dependency is resolvable. (since we don't invoke the APIs during the test)
      jest.doMock("../../lib/feedback/feedback-native-module", () => {
        return () => ({});
      });
    });

    describe("Import submitOccupantRoleFeedback() ", () => {
      test("using a named import", async () => {
        const { submitOccupantRoleFeedback } = require("../../lib");
        expect(submitOccupantRoleFeedback).toBeTruthy();
      });

      test("using a default import", async () => {
        const { sentianceFeedback: { submitOccupantRoleFeedback } } = require("../../lib").default;
        expect(submitOccupantRoleFeedback).toBeTruthy();
      });
    });
  });
});
