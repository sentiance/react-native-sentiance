const { NativeModules, Platform } = require("react-native");
const { varToString } = require("@sentiance-react-native/core/lib/utils");

const {SentianceFeedback, SentianceCore} = NativeModules;

const VALID_OCCUPANT_ROLE_TYPES = ["DRIVER", "PASSENGER"];

let feedbackModule = {};
if (Platform.OS === "android") {
  if (!SentianceFeedback) {
    const nativeModuleName = varToString({ SentianceFeedback });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    feedbackModule = SentianceFeedback;
  }
} else {
  if (!SentianceCore) {
    const nativeModuleName = varToString({ SentianceCore });
    console.error(`Could not locate the native ${nativeModuleName} module.
    Make sure that your native code is properly linked, and that the module name you specified is correct.`);
  } else {
    feedbackModule = SentianceCore;
  }
}

const submitOccupantRoleFeedback = (transportId: string, occupantRoleFeedback: string) => {
  if (!_isOccupantRoleFeedbackTypeValid(occupantRoleFeedback)) {
    return Promise.reject(new Error("submitOccupantRoleFeedback was called with invalid feedback type: " + occupantRoleFeedback));
  }
  return feedbackModule.submitOccupantRoleFeedback(transportId, occupantRoleFeedback);
};

const _isOccupantRoleFeedbackTypeValid = (type: string): boolean => {
  return VALID_OCCUPANT_ROLE_TYPES.includes(type);
};

module.exports = {
  submitOccupantRoleFeedback
};