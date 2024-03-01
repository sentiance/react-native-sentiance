import { allEqual, runOnEachPlatform } from "../../../jest/test_util";
import { mockNativeDrivingInsightsModule } from "../jest/mockNativeModule";

describe("Driving insights tests", () => {
  beforeEach(() => jest.resetModules());

  describe("Get driving insights returns expected results", () => {

    const expectedTransportEvent = {
      id: "transport_event_id",
      transportMode: "CAR",
      distance: 200
    };
    const expectedSafetyScores = {
      smoothScore: 0.77,
      focusScore: 0.66,
      callWhileMovingScore: 0.55,
      legalScore: 0.44,
      overallScore: 0.33
    };

    runOnEachPlatform(async platform => {
      const mockGetDrivingInsights = jest.fn();
      mockNativeDrivingInsightsModule(platform, {
        getDrivingInsights: mockGetDrivingInsights
      });

      mockGetDrivingInsights.mockResolvedValueOnce({
        transportEvent: JSON.parse(JSON.stringify(expectedTransportEvent)),
        safetyScores: JSON.parse(JSON.stringify(expectedSafetyScores))
      });

      const drivingInsightsApi = require("../lib");
      const transportId = "transport_id";
      const drivingInsights = await drivingInsightsApi.getDrivingInsights(transportId);
      const { transportEvent, safetyScores } = drivingInsights;

      expect(mockGetDrivingInsights).toHaveBeenCalledWith(transportId);
      expect(transportEvent).toEqual(expectedTransportEvent);
      expect(safetyScores).toEqual(expectedSafetyScores);
    });
  });

  describe("Get harsh driving events returns expected results", () => {

    const expectedHarshEvents = [0.55, 0.44, 0.33].map(magnitude => ({
      startTime: Date(),
      endTime: Date(),
      magnitude
    }));

    runOnEachPlatform(async platform => {
      const mockGetHarshDrivingEvents = jest.fn();
      mockNativeDrivingInsightsModule(platform, {
        getHarshDrivingEvents: mockGetHarshDrivingEvents
      });

      mockGetHarshDrivingEvents.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedHarshEvents)));

      const drivingInsightsApi = require("../lib");
      const transportId = "transport_id";

      expect(drivingInsightsApi.getHarshDrivingEvents(transportId)).resolves.toBe(expectedHarshEvents);
      expect(mockGetHarshDrivingEvents).toHaveBeenCalledWith(transportId);
    });
  });

  describe("Get phone usage events returns expected results", () => {

    const expectedPhoneUsageEvents = [...Array(3)].map(() => ({
      startTime: Date(),
      endTime: Date()
    }));

    runOnEachPlatform(platform => {
      const mockGetPhoneUsageEvents = jest.fn();
      mockNativeDrivingInsightsModule(platform, {
        getPhoneUsageEvents: mockGetPhoneUsageEvents
      });

      mockGetPhoneUsageEvents.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedPhoneUsageEvents)));

      const drivingInsightsApi = require("../lib");
      const transportId = "transport_id";

      expect(drivingInsightsApi.getPhoneUsageEvents(transportId)).resolves.toBe(expectedPhoneUsageEvents);
      expect(mockGetPhoneUsageEvents).toHaveBeenCalledWith(transportId);
    });
  });

  describe("Get call while moving events returns expected results", () => {

    const expectedCallWhileMovingEvents = [...Array(3)].map((value, index) => ({
      startTime: Date(),
      endTime: Date(),
      maxTravelledSpeedInMps: 2.5 * (index + 1),
      minTravelledSpeedInMps: 1.9 * (index + 1)
    }));

    runOnEachPlatform(platform => {
      const mockGetCallWhileMovingEvents = jest.fn();
      mockNativeDrivingInsightsModule(platform, {
        getCallWhileMovingEvents: mockGetCallWhileMovingEvents
      });

      mockGetCallWhileMovingEvents.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedCallWhileMovingEvents)));

      const drivingInsightsApi = require("../lib");
      const transportId = "transport_id";

      expect(drivingInsightsApi.getCallWhileMovingEvents(transportId)).resolves.toBe(expectedCallWhileMovingEvents);
      expect(mockGetCallWhileMovingEvents).toHaveBeenCalledWith(transportId);
    });
  });

  describe("Get speeding events returns expected results", () => {

    const expectedSpeedingEvents = [...Array(3)].map((value, index) => ({
      startTime: Date(),
      endTime: Date(),
      waypoints: [
        {
          latitude: 12.5685,
          longitude: 34.596758,
          accuracy: 20.0,
          timestamp: Date.now(),
          speedInMps: 5.5,
          speedLimitInMps: 5.5,
          hasUnlimitedSpeedLimit: false
        },
        {
          latitude: 12.5685,
          longitude: 34.596758,
          accuracy: 20.0,
          timestamp: Date.now(),
          speedInMps: 5.5,
          hasUnlimitedSpeedLimit: false
        },
        {
          latitude: 12.5685,
          longitude: 34.596758,
          accuracy: 20.0,
          timestamp: Date.now(),
          speedInMps: 5.5,
          hasUnlimitedSpeedLimit: true
        }
      ]
    }));

    runOnEachPlatform(platform => {
      const mockGetSpeedingEvents = jest.fn();
      mockNativeDrivingInsightsModule(platform, {
        getSpeedingEvents: mockGetSpeedingEvents
      });

      mockGetSpeedingEvents.mockResolvedValueOnce(JSON.parse(JSON.stringify(expectedSpeedingEvents)));

      const drivingInsightsApi = require("../lib");
      const transportId = "transport_id";

      expect(drivingInsightsApi.getSpeedingEvents(transportId)).resolves.toBe(expectedSpeedingEvents);
      expect(mockGetSpeedingEvents).toHaveBeenCalledWith(transportId);
    });
  });

  describe("Test add/remove driving insights ready listener", () => {

    runOnEachPlatform(async platform => {
      let capturedEventNames = [];
      let capturedSubscriptionIds = [];
      const expectedNativeModuleFuncInvocations = 2;

      const activeModule = mockNativeDrivingInsightsModule(platform, {});
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

      const drivingInsightsApi = require("../lib");
      const { DRIVING_INSIGHTS_READY_EVENT } = drivingInsightsApi.events;
      const subscription = await drivingInsightsApi.addDrivingInsightsReadyListener(() => undefined);

      expect(addNativeListener).toHaveBeenCalled();
      await subscription.remove();
      expect(removeNativeListener).toHaveBeenCalled();

      expect(capturedEventNames[0]).toBe(DRIVING_INSIGHTS_READY_EVENT);
      expect(capturedEventNames.length).toBe(expectedNativeModuleFuncInvocations);
      expect(capturedSubscriptionIds.length).toBe(expectedNativeModuleFuncInvocations);
      expect(allEqual(capturedEventNames)).toBeTruthy();
      expect(allEqual(capturedSubscriptionIds)).toBeTruthy();
    });
  });
});


