declare module "@sentiance-react-native/driving-insights" {
  import { EmitterSubscription } from "react-native";

  export interface DrivingInsights {
    transportEvent: TransportEvent,
    safetyScores: SafetyScores
  }

  export interface SafetyScores {
    /**
     * Smooth driving score, between 0 and 1, where 1 is the perfect score.
     */
    smoothScore?: number;
    /**
     * Focused driving score, between 0 and 1, where 1 is the perfect score.
     */
    focusScore?: number;
    /**
     * Legal driving score, between 0 and 1, where 1 is the perfect score.
     */
    legalScore?: number;
    /**
     * Call while moving driving score, between 0 and 1, where 1 is the perfect score.
     */
    callWhileMovingScore?: number;
    /**
     * Overall driving score, between 0 and 1, where 1 is the perfect score.
     */
    overallScore?: number;
  }

  export interface DrivingEvent {
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    endTime: string;
    endTimeEpoch: number; // in milliseconds
    waypoints: Waypoint[];
  }

  export type HarshDrivingEventType =
    | "ACCELERATION"
    | "BRAKING"
    | "TURN";

  export interface HarshDrivingEvent extends DrivingEvent {
    magnitude: number;
    confidence: number;
    type: HarshDrivingEventType;
  }

  export interface PhoneUsageEvent extends DrivingEvent {
  }

  export interface CallWhileMovingEvent extends DrivingEvent {
    maxTravelledSpeedInMps?: number;
    minTravelledSpeedInMps?: number;
  }

  export interface SpeedingEvent extends DrivingEvent {
  }

  export interface TransportEvent {
    id: string;
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    lastUpdateTime: string;
    lastUpdateTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    durationInSeconds: number | null;
    type: string;
    transportMode: TransportMode | null;
    waypoints: Waypoint[];
    distance?: number; // in meters
    transportTags: TransportTags;
  }

  export type TransportTags = { [key: string]: string };

  export type TransportMode =
    | "UNKNOWN"
    | "BICYCLE"
    | "WALKING"
    | "RUNNING"
    | "TRAM"
    | "TRAIN"
    | "CAR"
    | "BUS"
    | "MOTORCYCLE";

  export interface Waypoint {
    latitude: number;
    longitude: number;
    accuracy: number;   // in meters
    timestamp: number;  // UTC epoch time in milliseconds
    speedInMps?: number;  // in meters per second
    speedLimitInMps?: number;  // in meters per second
    hasUnlimitedSpeedLimit: boolean;
    isSpeedLimitInfoSet: boolean;
    isSynthetic: boolean;
  }

  export type OccupantRole =
    | "DRIVER"
    | "PASSENGER"
    | "UNAVAILABLE";

  export interface SafetyScoreRequestParameters {
    /**
     * A period of the last 7, 14 or 30 days.
     */
    period: 7 | 14 | 30;
    /**
     * Transport modes of interest.
     *
     * @defaultValue "ALL_MODES"
     */
    transportModes: TransportMode[] | "ALL_MODES";
    /**
     * Occupant roles of interest.
     *
     * @defaultValue "ALL_ROLES"
     */
    occupantRoles: OccupantRole[] | "ALL_ROLES";
  }

  export interface SentianceDrivingInsights {
    /**
     * Adds a listener that will be invoked when the driving insights for a completed transport becomes ready.
     *
     * @param onDrivingInsightsReady - listener to receive the driving insights
     */
    addDrivingInsightsReadyListener(onDrivingInsightsReady: (drivingInsights: DrivingInsights) => void): Promise<EmitterSubscription>;

    /**
     * Returns the driving insights for a given transport, or `null` if there are no driving insights or the transport ID is invalid.
     *
     * @param transportId - the id of the desired transport
     */
    getDrivingInsights(transportId: string): Promise<DrivingInsights>;

    /**
     * Returns the harsh driving events for a completed transport.
     *
     * @param transportId - the id of the desired transport
     */
    getHarshDrivingEvents(transportId: string): Promise<HarshDrivingEvent[]>;

    /**
     * Returns the phone usage events for a completed transport.
     *
     * @param transportId - the id of the desired transport
     */
    getPhoneUsageEvents(transportId: string): Promise<PhoneUsageEvent[]>;

    /**
     * Returns the call while moving events for a completed transport.
     *
     * @param transportId - the id of the desired transport
     */
    getCallWhileMovingEvents(transportId: string): Promise<CallWhileMovingEvent[]>;

    /**
     * Returns the speeding events for a completed transport.
     *
     * @param transportId - the id of the desired transport
     */
    getSpeedingEvents(transportId: string): Promise<SpeedingEvent[]>;

    /**
     * Returns the average overall safety score for a given set of parameters.
     *
     * @param params - the parameters for which the score will be calculated.
     * @returns the average overall safety score, or `null` if a score is not available.
     * @throws {@link Error} if any of the inputs provided is invalid.
     * @example
     * ```
     * import drivingInsights from "@sentiance-react-native/driving-insights";
     * const params = {
     *    period: 7,
     *    transportModes: ["CAR", "BUS"],
     *    occupantRoles: "ALL_ROLES"
     * }
     * const avgOverallScore = await drivingInsights.getAverageOverallSafetyScore(params);
     * ```
     */
    getAverageOverallSafetyScore(params: SafetyScoreRequestParameters): Promise<number>;
  }

  const SentianceDrivingInsights: SentianceDrivingInsights;
  export default SentianceDrivingInsights;
}
