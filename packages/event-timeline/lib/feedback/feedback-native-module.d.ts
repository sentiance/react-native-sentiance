import { type NativeModule } from "@sentiance-react-native/core/lib/generated/native-module";
import { type OccupantRoleFeedback, type OccupantRoleFeedbackResult } from "./types";
export declare const NATIVE_MODULE_NAME = "SentianceFeedback";
export interface FeedbackModule extends NativeModule {
    submitOccupantRoleFeedback(transportId: string, occupantRoleFeedback: OccupantRoleFeedback): Promise<OccupantRoleFeedbackResult>;
}
export default function (): FeedbackModule;
