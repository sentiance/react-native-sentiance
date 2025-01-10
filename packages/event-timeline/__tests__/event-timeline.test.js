import { allEqual, runOnEachPlatform } from "../../../jest/test_util";
import { mockNativeEventTimelineModule } from "../jest/mockNativeModule";
import { E_TRANSPORT_TAG_ERROR, TransportTaggingError } from "../lib/errors/errors.js";

describe("Event Timeline API tests", () => {
  beforeEach(() => jest.resetModules());

  describe("Get timeline updates returns expected results", () => {

    const now = Date.now();
    const expectedEvents = [
      {
        id: "event_id1",
        startTime: now.toString(),
        startTimeEpoch: now,
        lastUpdateTime: now.toString(),
        lastUpdateTimeEpoch: now,
        type: "UNKNOWN",
        waypoints: []
      },
      {
        id: "event_id2",
        startTime: now.toString(),
        startTimeEpoch: now,
        lastUpdateTime: now.toString(),
        lastUpdateTimeEpoch: now,
        type: "TRANSPORT",
        waypoints: []
      }
    ];

    runOnEachPlatform(platform => {
      const mockGetTimelineUpdates = jest.fn();
      mockNativeEventTimelineModule(platform, {
        getTimelineUpdates: mockGetTimelineUpdates
      });

      mockGetTimelineUpdates.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedEvents)));

      const eventTimelineApi = require("../lib");
      const afterTimestamp = Date.now();

      expect(eventTimelineApi.getTimelineUpdates(afterTimestamp)).resolves.toBe(expectedEvents);
      expect(mockGetTimelineUpdates).toHaveBeenCalledWith(afterTimestamp);
    });
  });

  describe("Get timeline events returns expected results", () => {

    const now = Date.now();
    const expectedEvents = [
      {
        id: "event_id1",
        startTime: now.toString(),
        startTimeEpoch: now,
        lastUpdateTime: now.toString(),
        lastUpdateTimeEpoch: now,
        type: "UNKNOWN",
        waypoints: []
      },
      {
        id: "event_id2",
        startTime: now.toString(),
        startTimeEpoch: now,
        lastUpdateTime: now.toString(),
        lastUpdateTimeEpoch: now,
        type: "TRANSPORT",
        waypoints: []
      }
    ];

    runOnEachPlatform(platform => {
      const mockGetTimelineEvents = jest.fn();
      mockNativeEventTimelineModule(platform, {
        getTimelineEvents: mockGetTimelineEvents
      });

      mockGetTimelineEvents.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedEvents)));

      const eventTimelineApi = require("../lib");
      const fromTimestamp = Date.now();
      const toTimestamp = Date.now() + 2000;

      expect(eventTimelineApi.getTimelineEvents(fromTimestamp, toTimestamp)).resolves.toBe(expectedEvents);
      expect(mockGetTimelineEvents).toHaveBeenCalledWith(fromTimestamp, toTimestamp);
    });
  });

  describe("Get single timeline event returns expected results", () => {

    const now = Date.now();
    const expectedEvent = {
      id: "event_id1",
      startTime: now.toString(),
      startTimeEpoch: now,
      lastUpdateTime: now.toString(),
      lastUpdateTimeEpoch: now,
      type: "UNKNOWN",
      waypoints: []
    };

    runOnEachPlatform(platform => {
      const mockGetTimelineEvent = jest.fn();
      mockNativeEventTimelineModule(platform, {
        getTimelineEvent: mockGetTimelineEvent
      });

      mockGetTimelineEvent.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedEvent)));

      const eventTimelineApi = require("../lib");
      const eventId = "event_id";

      expect(eventTimelineApi.getTimelineEvent(eventId)).resolves.toBe(expectedEvent);
      expect(mockGetTimelineEvent).toHaveBeenCalledWith(eventId);
    });
  });

  describe("Test add/remove timeline update listener", () => {

    runOnEachPlatform(async platform => {
      let capturedEventNames = [];
      let capturedSubscriptionIds = [];
      const expectedNativeModuleFuncInvocations = 2;

      const activeModule = mockNativeEventTimelineModule(platform, {});
      const addNativeListener = activeModule.addNativeListener
        .mockImplementation((eventName, subscriptionId) => {
          capturedEventNames.push(eventName);
          capturedSubscriptionIds.push(subscriptionId);
        });
      const removeNativeListener = activeModule.removeNativeListener
        .mockImplementation((eventName, subscriptionId) => {
          capturedEventNames.push(eventName);
          capturedSubscriptionIds.push(subscriptionId);
        });

      const eventTimelineApi = require("../lib");
      const { TIMELINE_UPDATE_EVENT } = eventTimelineApi.events;
      const subscription = await eventTimelineApi.addTimelineUpdateListener(() => undefined);

      expect(addNativeListener).toHaveBeenCalled();
      await subscription.remove();
      expect(removeNativeListener).toHaveBeenCalled();

      expect(capturedEventNames[0]).toBe(TIMELINE_UPDATE_EVENT);
      expect(capturedEventNames.length).toBe(expectedNativeModuleFuncInvocations);
      expect(capturedSubscriptionIds.length).toBe(expectedNativeModuleFuncInvocations);
      expect(allEqual(capturedEventNames)).toBeTruthy();
      expect(allEqual(capturedSubscriptionIds)).toBeTruthy();
    });
  });

  describe("Test set custom transport tags succeeds", () => {
    runOnEachPlatform(async platform => {
      const mockSetTransportTags = jest.fn();
      mockNativeEventTimelineModule(platform, {
        setTransportTags: mockSetTransportTags
      });

      const expectedResult = null;
      mockSetTransportTags.mockResolvedValueOnce(expectedResult);

      const eventTimelineApi = require("../lib");
      const tags = {
        "key1": "value1",
        "key2": "value2",
        "key3": "value3"
      };

      await expect(eventTimelineApi.setTransportTags(tags)).resolves.toEqual(expectedResult);
    });
  });

  describe("Test set custom transport tags fails with transport tag error", () => {
    runOnEachPlatform(async platform => {
      const mockSetTransportTags = jest.fn();
      mockNativeEventTimelineModule(platform, {
        setTransportTags: mockSetTransportTags
      });

      const expectedErrorMessage = "Failed to set transport tags because X and Y";
      mockSetTransportTags.mockRejectedValueOnce({
        code: E_TRANSPORT_TAG_ERROR,
        message: expectedErrorMessage
      });

      const eventTimelineApi = require("../lib");

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
      mockNativeEventTimelineModule(platform, {
        setTransportTags: mockSetTransportTags
      });

      const errors = [
        { message: "message1" },
        { code: "some_error_code", message: "message2" },
        { code: null, message: "message3" },
        { code: undefined, message: "message4" },
        { code: "some_error_code", message: "message5", extraField: 1 },
      ];

      // for every test case defined above, we fail with a different error
      for (const error of errors) {
        const eventTimelineApi = require("../lib");

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
});


