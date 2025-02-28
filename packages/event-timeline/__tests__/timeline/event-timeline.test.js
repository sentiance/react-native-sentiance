/**
 * @description This file contains tests for the APIs related to the event timeline.
 */

import { runOnEachPlatform } from "../../../../jest/test_util";
import { E_TRANSPORT_TAG_ERROR, TransportTaggingError } from "../../lib/errors";

describe("Event Timeline API tests", () => {
  beforeEach(() => {
    jest.resetModules();

    // Mock the feedback API, as it's exposed as part of the public API
    // of the event timeline module and hence is a dependency.
    // The feedback API tests are in a separate file.
    jest.doMock("../../lib/feedback", () => {
      return {};
    });
  });

  describe("Get timeline updates", () => {
    runOnEachPlatform(async platform => {
      const mockGetTimelineUpdates = jest.fn()
        .mockResolvedValueOnce([{}])
        .mockResolvedValueOnce([{}, {}])
        .mockResolvedValueOnce([{}, {}, {}]);
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          getTimelineUpdates: mockGetTimelineUpdates
        });
      });

      const eventTimelineApi = require("../../lib");

      let events = await eventTimelineApi.getTimelineUpdates(1000);
      expect(mockGetTimelineUpdates).toHaveBeenLastCalledWith(1000, false);
      expect(events.length).toEqual(1);

      events = await eventTimelineApi.getTimelineUpdates(2000, false);
      expect(mockGetTimelineUpdates).toHaveBeenLastCalledWith(2000, false);
      expect(events.length).toEqual(2);

      events = await eventTimelineApi.getTimelineUpdates(3000, true);
      expect(mockGetTimelineUpdates).toHaveBeenLastCalledWith(3000, true);
      expect(events.length).toEqual(3);
    });
  });

  describe("Get timeline events", () => {
    runOnEachPlatform(async platform => {
      const mockGetTimelineEvents = jest.fn()
        .mockResolvedValueOnce([{}])
        .mockResolvedValueOnce([{}, {}])
        .mockResolvedValueOnce([{}, {}, {}]);
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          getTimelineEvents: mockGetTimelineEvents
        });
      });

      const eventTimelineApi = require("../../lib");

      let events = await eventTimelineApi.getTimelineEvents(10_000, 20_000);
      expect(mockGetTimelineEvents).toHaveBeenLastCalledWith(10_000, 20_000, false);
      expect(events.length).toEqual(1);

      events = await eventTimelineApi.getTimelineEvents(30_000, 40_000, false);
      expect(mockGetTimelineEvents).toHaveBeenLastCalledWith(30_000, 40_000, false);
      expect(events.length).toEqual(2);

      events = await eventTimelineApi.getTimelineEvents(45_000, 50_000, true);
      expect(mockGetTimelineEvents).toHaveBeenLastCalledWith(45_000, 50_000, true);
      expect(events.length).toEqual(3);
    });
  });

  describe("Get single timeline event", () => {
    runOnEachPlatform(async platform => {
      const mockGetTimelineEvent = jest.fn();
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          getTimelineEvent: mockGetTimelineEvent
        });
      });

      const eventTimelineApi = require("../../lib");
      const eventId = "event_id";

      mockGetTimelineEvent.mockResolvedValueOnce(null);
      let event = await eventTimelineApi.getTimelineEvent(eventId);
      expect(event).toBeNull();
      expect(mockGetTimelineEvent).toHaveBeenCalledWith(eventId);

      mockGetTimelineEvent.mockResolvedValueOnce({ id: eventId });
      event = await eventTimelineApi.getTimelineEvent(eventId);
      expect(event).not.toBeNull();
      expect(event).toEqual({ id: eventId });
      expect(mockGetTimelineEvent).toHaveBeenCalledWith(eventId);
    });
  });

  describe("Test add/remove timeline update listener", () => {
    runOnEachPlatform(async platform => {
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({});
      });

      let count = 0;
      const mockRemove1 = jest.fn();
      const mockRemove2 = jest.fn();
      const mockRemove3 = jest.fn();
      const mockAddEventTimelineListener = jest.fn(() => {
        switch (count++) {
          case 0:
            return { remove: mockRemove1 };
          case 1:
            return { remove: mockRemove2 };
          case 2:
            return { remove: mockRemove3 };
          default:
            throw new Error("Not implemented");
        }
      });

      jest.doMock("../../lib/timeline/timeline-event-emitter", () => {
        return function() {
          return {
            addEventTimelineListener: mockAddEventTimelineListener
          };
        };
      });

      const eventTimelineApi = require("../../lib");

      //////////////////////////////////
      // addTimelineUpdateListener tests
      //////////////////////////////////
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      const listener1 = (event) => {
      };
      const sub1 = await eventTimelineApi.addTimelineUpdateListener(listener1);
      expect(mockAddEventTimelineListener).toHaveBeenLastCalledWith(listener1, false);

      // eslint-disable-next-line @typescript-eslint/no-empty-function
      const listener2 = (event) => {
      };
      const sub2 = await eventTimelineApi.addTimelineUpdateListener(listener2, false);
      expect(mockAddEventTimelineListener).toHaveBeenLastCalledWith(listener2, false);

      // eslint-disable-next-line @typescript-eslint/no-empty-function
      const listener3 = (event) => {
      };
      const sub3 = await eventTimelineApi.addTimelineUpdateListener(listener3, true);
      expect(mockAddEventTimelineListener).toHaveBeenLastCalledWith(listener3, true);

      //////////////////////////////////
      // Remove listener tests
      //////////////////////////////////
      expect(mockRemove1).toHaveBeenCalledTimes(0);

      sub1.remove();
      expect(mockRemove1).toHaveBeenCalledTimes(1);
      expect(mockRemove2).toHaveBeenCalledTimes(0);
      expect(mockRemove3).toHaveBeenCalledTimes(0);

      sub2.remove();
      expect(mockRemove1).toHaveBeenCalledTimes(1);
      expect(mockRemove2).toHaveBeenCalledTimes(1);
      expect(mockRemove3).toHaveBeenCalledTimes(0);

      sub3.remove();
      expect(mockRemove1).toHaveBeenCalledTimes(1);
      expect(mockRemove2).toHaveBeenCalledTimes(1);
      expect(mockRemove3).toHaveBeenCalledTimes(1);
    });
  });

  describe("Test set custom transport tags succeeds", () => {
    runOnEachPlatform(async platform => {
      const mockSetTransportTags = jest.fn();
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          setTransportTags: mockSetTransportTags
        });
      });

      mockSetTransportTags.mockResolvedValueOnce(null);

      const eventTimelineApi = require("../../lib");
      const tags = {
        "key1": "value1",
        "key2": "value2",
        "key3": "value3"
      };

      const result = await eventTimelineApi.setTransportTags(tags);
      expect(mockSetTransportTags).toHaveBeenCalledWith(tags);
      expect(result).toBeNull();
    });
  });

  describe("Test set custom transport tags fails with transport tag error", () => {
    runOnEachPlatform(async platform => {
      const mockSetTransportTags = jest.fn();
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          setTransportTags: mockSetTransportTags
        });
      });

      const expectedErrorMessage = "Failed to set transport tags because X and Y";
      mockSetTransportTags.mockRejectedValueOnce({
        code: E_TRANSPORT_TAG_ERROR,
        message: expectedErrorMessage
      });

      const eventTimelineApi = require("../../lib");

      const tags = {
        "key1": "value1",
        "key2": "value2",
        "key3": "value3"
      };
      await expect(eventTimelineApi.setTransportTags(tags))
        .rejects
        .toEqual(new TransportTaggingError(expectedErrorMessage));
    });
  });

  describe("Test set custom transport tags fails with non transport tag error", () => {
    runOnEachPlatform(async platform => {
      const mockSetTransportTags = jest.fn();
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({
          setTransportTags: mockSetTransportTags
        });
      });

      const errors = [
        { message: "message1" },
        { code: "some_error_code", message: "message2" },
        { code: null, message: "message3" },
        { code: undefined, message: "message4" },
        { code: "some_error_code", message: "message5", extraField: 1 }
      ];

      // for every test case defined above, we fail with a different error
      for (const error of errors) {
        const eventTimelineApi = require("../../lib");
        mockSetTransportTags.mockRejectedValueOnce(error);

        const tags = {
          "key1": "value1",
          "key2": "value2",
          "key3": "value3"
        };
        await expect(eventTimelineApi.setTransportTags(tags))
          .rejects
          .toEqual(error);
      }
    });
  });

  describe("Named/Default import tests", () => {

    beforeEach(() => {
      // We don't need to mock the individual APIs, just the module as a whole
      // so that the dependency is resolvable. (since we don't invoke the APIs during the test)
      jest.doMock("../../lib/timeline/event-timeline-native-module", () => {
        return () => ({});
      });
    });

    describe("Import getTimelineUpdates() ", () => {
      test("using a named import", async () => {
        const { getTimelineUpdates } = require("../../lib");
        expect(getTimelineUpdates).toBeTruthy();
      });

      test("using a default import", async () => {
        const { getTimelineUpdates } = require("../../lib").default;
        expect(getTimelineUpdates).toBeTruthy();
      });
    });

    describe("Import getTimelineEvents() ", () => {
      test("using a named import", async () => {
        const { getTimelineEvents } = require("../../lib");
        expect(getTimelineEvents).toBeTruthy();
      });

      test("using a default import", async () => {
        const { getTimelineEvents } = require("../../lib").default;
        expect(getTimelineEvents).toBeTruthy();
      });
    });

    describe("Import getTimelineEvent() ", () => {
      test("using a named import", async () => {
        const { getTimelineEvent } = require("../../lib");
        expect(getTimelineEvent).toBeTruthy();
      });

      test("using a default import", async () => {
        const { getTimelineEvent } = require("../../lib").default;
        expect(getTimelineEvent).toBeTruthy();
      });
    });

    describe("Import setTransportTags() ", () => {
      test("using a named import", async () => {
        const { setTransportTags } = require("../../lib");
        expect(setTransportTags).toBeTruthy();
      });

      test("using a default import", async () => {
        const { setTransportTags } = require("../../lib").default;
        expect(setTransportTags).toBeTruthy();
      });
    });

    describe("Import addTimelineUpdateListener() ", () => {
      test("using a named import", async () => {
        const { addTimelineUpdateListener } = require("../../lib");
        expect(addTimelineUpdateListener).toBeTruthy();
      });

      test("using a default import", async () => {
        const { addTimelineUpdateListener } = require("../../lib").default;
        expect(addTimelineUpdateListener).toBeTruthy();
      });
    });
  });
});
