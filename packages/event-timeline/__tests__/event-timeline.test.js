import {allEqual, runOnEachPlatform} from "../../../jest/test_util";
import {mockNativeEventTimelineModule} from "../jest/mockNativeModule";

describe('Event Timeline API tests', () => {
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
      },
    ];

    runOnEachPlatform(platform => {
      const mockGetTimelineUpdates = jest.fn();
      mockNativeEventTimelineModule(platform, {
        getTimelineUpdates: mockGetTimelineUpdates,
      });

      mockGetTimelineUpdates.mockImplementation(afterTimestamp => JSON.parse(JSON.stringify(expectedEvents)));

      const eventTimelineApi = require('../lib');
      const afterTimestamp = Date.now();
      const events = eventTimelineApi.getTimelineUpdates(afterTimestamp);

      expect(mockGetTimelineUpdates).toHaveBeenCalledWith(afterTimestamp);
      expect(events).toEqual(expectedEvents);
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
      },
    ];

    runOnEachPlatform(platform => {
      const mockGetTimelineEvents = jest.fn();
      mockNativeEventTimelineModule(platform, {
        getTimelineEvents: mockGetTimelineEvents,
      });

      mockGetTimelineEvents.mockImplementation(afterTimestamp => JSON.parse(JSON.stringify(expectedEvents)));

      const eventTimelineApi = require('../lib');
      const fromTimestamp = Date.now();
      const toTimestamp = Date.now() + 2000;
      const events = eventTimelineApi.getTimelineEvents(fromTimestamp, toTimestamp);

      expect(mockGetTimelineEvents).toHaveBeenCalledWith(fromTimestamp, toTimestamp);
      expect(events).toEqual(expectedEvents);
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
        getTimelineEvent: mockGetTimelineEvent,
      });

      mockGetTimelineEvent.mockImplementation(afterTimestamp => JSON.parse(JSON.stringify(expectedEvent)));

      const eventTimelineApi = require('../lib');
      const eventId = "event_id";
      const event = eventTimelineApi.getTimelineEvent(eventId);

      expect(mockGetTimelineEvent).toHaveBeenCalledWith(eventId);
      expect(event).toEqual(expectedEvent);
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

      const eventTimelineApi = require('../lib');
      const {TIMELINE_UPDATE_EVENT} = eventTimelineApi.events;
      const subscription = await eventTimelineApi.addTimelineUpdateListener(event => {

      });

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
})


