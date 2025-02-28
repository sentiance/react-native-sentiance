import { runOnEachPlatform } from "../../../jest/test_util.js";
import { mockNativeUserContextModule } from "../jest/mockNativeModule";

describe("User context API tests", () => {

  beforeEach(() => jest.resetModules());

  describe("Request user context", () => {

    runOnEachPlatform(async platform => {
      const mockNativeRequestUserContext = jest.fn();
      mockNativeUserContextModule(platform, {
        requestUserContext: mockNativeRequestUserContext
      });

      const userContextApi = require("../lib");

      await userContextApi.requestUserContext();
      expect(mockNativeRequestUserContext).toHaveBeenLastCalledWith(false);

      await userContextApi.requestUserContext(true);
      expect(mockNativeRequestUserContext).toHaveBeenLastCalledWith(true);

      await userContextApi.requestUserContext(false);
      expect(mockNativeRequestUserContext).toHaveBeenLastCalledWith(false);
    });
  });

  describe("Test add/remove user context updates listener", () => {
    runOnEachPlatform(async platform => {
      // TODO: Ideally, we would mock the SentianceEventEmitter and avoid tapping into its implementation details,
      //  such as subscription ID assignments etc...

      const activeModule = mockNativeUserContextModule(platform, {});
      const addNativeListener = activeModule.addNativeListener;
      const removeNativeListener = activeModule.removeNativeListener;

      const userContextApi = require("../lib");
      const { USER_CONTEXT_UPDATE_EVENT } = userContextApi.events;

      const listener = () => undefined;

      // addNativeListener should always instruct the native code to provide
      // all sorts of events (provisional and non-provisional)
      const subscription1 = await userContextApi.addUserContextUpdateListener(listener);
      expect(addNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 1, { includeProvisionalEvents: true });
      const subscription2 = await userContextApi.addUserContextUpdateListener(listener, false);
      expect(addNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 2, { includeProvisionalEvents: true });
      const subscription3 = await userContextApi.addUserContextUpdateListener(listener, true);
      expect(addNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 3, { includeProvisionalEvents: true });

      await subscription1.remove();
      expect(removeNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 1);
      await subscription2.remove();
      expect(removeNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 2);
      await subscription3.remove();
      expect(removeNativeListener).toHaveBeenLastCalledWith(USER_CONTEXT_UPDATE_EVENT, 3);
    });
  });
});


