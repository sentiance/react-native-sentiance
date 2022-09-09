declare module "@sentiance-react-native/crash-detection" {
  import {EmitterSubscription} from "react-native";

  export interface Location {
    latitude: string;
    longitude: string;
    accuracy?: string; // Android only
    altitude?: string; // Android only
    provider?: string; // Android only
  }

  export interface CrashEvent {
    time: number;
    location: Location;
    magnitude: number;
    speedAtImpact: number;
    deltaV: number;
    confidence: number;
  }

  export type VehicleCrashDetectionState = 
  "CANDIDATE_DETECTED" 
  | "CANDIDATE_DISCARDED_WEAK_IMPACT" 
  | "CANDIDATE_DISCARDED_NON_VEHICLE_TRANSPORT_MODE" 
  | "CANDIDATE_DISCARDED_PRE_IMPACT_NOISE"
  | "CANDIDATE_DISCARDED_LOW_SPEED_BEFORE_IMPACT"  
  | "CANDIDATE_DISCARDED_POST_IMPACT_NOISE"  
  | "CANDIDATE_DISCARDED_HIGH_SPEED_AFTER_IMPACT"
  | "CANDIDATE_NOT_DETECTED";

  export interface VehicleCrashDiagnostic {
    crashDetectionState: VehicleCrashDetectionState;
    crashDetectionStateDescription: string;
  }
  export interface SentianceCrashDetection {
    listenVehicleCrashEvents(): Promise<boolean>;
    invokeDummyVehicleCrash(): Promise<boolean>;
    isVehicleCrashDetectionSupported(): Promise<boolean>;
    addVehicleCrashEventListener(onVehicleCrash: (crashEvent: CrashEvent) => void): Promise<EmitterSubscription>;
    addVehicleCrashDiagnosticListener(onVehicleCrashDiagnostic: (diagnostic: VehicleCrashDiagnostic) => void): Promise<EmitterSubscription>;
  }

  const SentianceCrashDetection: SentianceCrashDetection;
  export default SentianceCrashDetection;
}
