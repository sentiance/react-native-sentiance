import { isValidNativeModule, type NativeModule } from "@sentiance-react-native/core/lib/generated/native-module";
import requireNativeModule from "@sentiance-react-native/core/lib/generated/require-native-module";
import { type OccupantRoleFeedback, type OccupantRoleFeedbackResult } from "./types";

export const NATIVE_MODULE_NAME = "SentianceFeedback";

export interface FeedbackModule extends NativeModule {
  submitOccupantRoleFeedback(
    transportId: string,
    occupantRoleFeedback: OccupantRoleFeedback
  ): Promise<OccupantRoleFeedbackResult>;
}

export default function(): FeedbackModule {
  const module = requireNativeModule<FeedbackModule>({
    androidName: NATIVE_MODULE_NAME,
    isModuleVerified: function(unverifiedModule): unverifiedModule is FeedbackModule {
      return (
        typeof unverifiedModule.submitOccupantRoleFeedback === "function"
      ) && isValidNativeModule(unverifiedModule);
    }
  });

  if (!module) {
    throw new Error("Could not locate the feedback native module.");
  }

  return module;
}
