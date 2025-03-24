declare module "@sentiance-react-native/crash-detection" {
  import { EmitterSubscription } from "react-native";

  export interface Location {
    timestamp: number;
    latitude: number;
    longitude: number;
    accuracy?: number;
    altitude?: number;
    provider?: string; // Android only
  }

  export interface CrashEvent {
    time: number;
    location: Location;
    precedingLocations: Location[];
    magnitude: number;
    speedAtImpact: number;
    deltaV: number;
    confidence: number;
    severity: CrashSeverity;
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

  export type CrashSeverity = "UNAVAILABLE" | "LOW" | "MEDIUM" | "HIGH";

  export interface VehicleCrashDiagnostic {
    crashDetectionState: VehicleCrashDetectionState;
    crashDetectionStateDescription: string;
  }

  export interface SentianceCrashDetection {
    /**
     * @deprecated Use `addVehicleCrashEventListener` instead
     */
    listenVehicleCrashEvents(): Promise<boolean>;

    /**
     * <p>Invokes a dummy vehicle crash event. Use this method to test your vehicle crash detection integration.</p>
     *
     * <p>Calling this method will invoke the listener you previously set via
     * {@link addVehicleCrashEventListener} (if any).
     * </p>
     *
     * <br/>
     * <p>Note that this function is intended for testing your integration, and will only run on debug versions of your
     * app (i.e. when the <code>android:debuggable</code> manifest flag is set to <code>true</code>).
     * </p>
     */
    invokeDummyVehicleCrash(): Promise<boolean>;

    /**
     * <p>Returns whether vehicle crash detection is supported on the device.</p>
     *
     * <br/>
     * <p>The result depends on multiple criteria, such as if vehicle crash detection is enabled for the user, and if
     * the necessary sensors are present on the device. As such, make sure a Sentiance user has been created before
     * calling this function. Otherwise, the result will always be <code>false</code>.
     * </p>
     *
     * @return <code>true</code> if vehicle crash detection is supported.
     */
    isVehicleCrashDetectionSupported(): Promise<boolean>;

    /**
     * <p>Registers a listener that is invoked when a vehicle crash is detected.</p>
     * @param onVehicleCrash - the callback to be invoked when a vehicle crash is detected.
     * @returns a subscription object that you can use to unregister the listener you previously set.
     */
    addVehicleCrashEventListener(onVehicleCrash: (crashEvent: CrashEvent) => void): Promise<EmitterSubscription>;

    /**
     * <p>Registers a listener that is invoked when vehicle crash diagnostic data becomes available.</p>
     * Use this function to receive diagnostic data while vehicle crash detection is running.
     * @param onVehicleCrashDiagnostic - the callback to be invoked with the received diagnostic data.
     * @returns a subscription object that you can use to unregister the listener you previously set.
     */
    addVehicleCrashDiagnosticListener(onVehicleCrashDiagnostic: (diagnostic: VehicleCrashDiagnostic) => void): Promise<EmitterSubscription>;
  }

  const SentianceCrashDetection: SentianceCrashDetection;
  export default SentianceCrashDetection;
}
