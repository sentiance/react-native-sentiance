import { runOnEachPlatform } from "../../../jest/test_util";
import { mockNativeCoreModule } from "../jest/mockNativeModule";

describe("Core API tests", () => {
  beforeEach(() => jest.resetModules());

  describe("Set is allowed to use mobile data", () => {

    runOnEachPlatform(platform => {
      const mockSetIsAllowedToUseMobileData = jest.fn();
      mockNativeCoreModule(platform, {
        setIsAllowedToUseMobileData: mockSetIsAllowedToUseMobileData
      });

      const coreApi = require("../lib");

      coreApi.setIsAllowedToUseMobileData(false);
      coreApi.setIsAllowedToUseMobileData(true);
      coreApi.setIsAllowedToUseMobileData(false);

      expect(mockSetIsAllowedToUseMobileData).toHaveBeenNthCalledWith(1, false);
      expect(mockSetIsAllowedToUseMobileData).toHaveBeenNthCalledWith(2, true);
      expect(mockSetIsAllowedToUseMobileData).toHaveBeenNthCalledWith(3, false);
    });
  });

  describe("Is allowed to use mobile data", () => {
    runOnEachPlatform(platform => {
      const mockIsAllowedToUseMobileData = jest.fn();
      mockNativeCoreModule(platform, {
        isAllowedToUseMobileData: mockIsAllowedToUseMobileData
      });

      mockIsAllowedToUseMobileData.mockResolvedValueOnce(true);
      mockIsAllowedToUseMobileData.mockResolvedValueOnce(false);

      const coreApi = require("../lib");
      expect(coreApi.isAllowedToUseMobileData()).resolves.toBe(true);
      expect(coreApi.isAllowedToUseMobileData()).resolves.toBe(false);
    });
  });
});


