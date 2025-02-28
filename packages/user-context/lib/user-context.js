const { NativeModules, Platform } = require("react-native");
const { varToString } = require("@sentiance-react-native/core/lib/generated/utils");
const SentianceEventEmitter = require("@sentiance-react-native/core/lib/generated/sentiance-event-emitter").default;
const { SentianceUserContext, SentianceCore } = NativeModules;

const allUserContextCriteria = ["CURRENT_EVENT", "ACTIVE_SEGMENTS", "VISITED_VENUES"];
const SDK_USER_CONTEXT_UPDATE_EVENT = "SENTIANCE_USER_CONTEXT_UPDATE_EVENT";

let didLocateNativeModule = true;
let userContextModule = {};
if (Platform.OS === "android") {
  if (!SentianceUserContext) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceUserContext });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    userContextModule = SentianceUserContext;
  }
} else {
  if (!SentianceCore) {
    didLocateNativeModule = false;
    const nativeModuleName = varToString({ SentianceCore });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    userContextModule = SentianceCore;
  }
}

if (didLocateNativeModule) {
  const emitter = new SentianceEventEmitter(userContextModule);

  userContextModule._addUserContextUpdateListener = async (onUserContextUpdated, includeProvisionalEvents) => {
    const payload = {
      includeProvisionalEvents: true
    };
    return emitter.addListener(
      SDK_USER_CONTEXT_UPDATE_EVENT,
      async function(update) {
        if (includeProvisionalEvents) {
          // If the JS listener is interested in provisional events, we deliver the update as is
          onUserContextUpdated(update);
        } else {
          // Otherwise, we query for a fresh user context without provisional events
          const userContext = await userContextModule.requestUserContext(false);
          onUserContextUpdated({
            userContext,
            criteria: allUserContextCriteria
          });
        }
      },
      payload
    );
  };
}

module.exports = userContextModule;
module.exports.events = {
  USER_CONTEXT_UPDATE_EVENT: SDK_USER_CONTEXT_UPDATE_EVENT
};
