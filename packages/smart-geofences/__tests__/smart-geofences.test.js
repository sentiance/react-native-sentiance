import { allEqual, runOnEachPlatform } from "../../../jest/test_util";
import { mockNativeSmartGeofencesModule } from "../jest/mockNativeModule";

describe("Smart Geofences API tests", () => {
  beforeEach(() => jest.resetModules());

  describe("Test the get detection mode API", () => {

    runOnEachPlatform(platform => {
      const mockGetDetectionMode = jest.fn();
      mockNativeSmartGeofencesModule(platform, {
        getDetectionMode: mockGetDetectionMode
      });

      const smartGeofencesModule = require("../lib");

      const detectionModes = [
        "FEATURE_NOT_ENABLED",
        "DISABLED",
        "FOREGROUND",
        "BACKGROUND"
      ];
      detectionModes.forEach(detectionMode => {
        mockGetDetectionMode.mockResolvedValueOnce(detectionMode);
        expect(smartGeofencesModule.getDetectionMode()).resolves.toEqual(detectionMode);
      });
    });
  });

  describe("Test refresh of geofences succeeds", () => {

    runOnEachPlatform(platform => {
      const mockRefreshGeofences = jest.fn();
      mockNativeSmartGeofencesModule(platform, {
        refreshGeofences: mockRefreshGeofences
      });

      mockRefreshGeofences.mockResolvedValueOnce(undefined);

      const smartGeofencesModule = require("../lib");
      const testScenario = async () => await smartGeofencesModule.refreshGeofences();
      return expect(testScenario()).resolves.toBe(undefined);
    });
  });

  describe("Test refresh of geofences fails", () => {

    runOnEachPlatform(async platform => {

      const mockRefreshGeofences = jest.fn();
      mockNativeSmartGeofencesModule(platform, {
        refreshGeofences: mockRefreshGeofences
      });

      const expectedError = {
        reason: "insert reason here",
        details: "insert details here"
      };
      mockRefreshGeofences.mockRejectedValueOnce(JSON.parse(JSON.stringify(expectedError)));

      const smartGeofencesModule = require("../lib");
      const testScenario = async () => await smartGeofencesModule.refreshGeofences();
      await expect(testScenario()).rejects.toEqual(expectedError);
    });
  });

  describe("Test add/remove smart geofence event listener", () => {

    runOnEachPlatform(async platform => {
      let capturedEventNames = [];
      let capturedSubscriptionIds = [];
      const expectedNativeModuleFuncInvocations = 2;

      const activeModule = mockNativeSmartGeofencesModule(platform, {});
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

      const smartGeofencesModule = require("../lib");
      const { SMART_GEOFENCE_EVENT } = smartGeofencesModule.events;
      const subscription = await smartGeofencesModule.addSmartGeofenceEventListener(() => undefined);

      expect(addNativeListener).toHaveBeenCalled();
      await subscription.remove();
      expect(removeNativeListener).toHaveBeenCalled();

      expect(capturedEventNames[0]).toEqual(SMART_GEOFENCE_EVENT);
      expect(capturedEventNames.length).toEqual(expectedNativeModuleFuncInvocations);
      expect(capturedSubscriptionIds.length).toEqual(expectedNativeModuleFuncInvocations);
      expect(allEqual(capturedEventNames)).toBeTruthy();
      expect(allEqual(capturedSubscriptionIds)).toBeTruthy();
    });
  });
});


