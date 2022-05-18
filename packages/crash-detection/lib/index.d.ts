declare module "react-native-sentiance-crash-detection" {
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
    lastKnownLocation?: Location;
  }

  export interface SentianceCrashDetection {
    listenVehicleCrashEvents(): Promise<boolean>;
    invokeDummyVehicleCrash(): Promise<boolean>;
    isVehicleCrashDetectionSupported(): Promise<boolean>;
    addVehicleCrashEventListener(onVehicleCrash: (crashEvent: CrashEvent) => void): Promise<EmitterSubscription>;
  }

  const SentianceCrashDetection: SentianceCrashDetection;
  export default SentianceCrashDetection;
}
