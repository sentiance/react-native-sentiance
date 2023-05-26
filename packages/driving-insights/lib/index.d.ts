declare module "@sentiance-react-native/driving-insights" {
  import {EmitterSubscription} from "react-native";

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
  }

  export interface DrivingEvent {
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    endTime: string;
    endTimeEpoch: number; // in milliseconds
  }

  export interface HarshDrivingEvent extends DrivingEvent {
    magnitude: number
  }

  export interface PhoneUsageEvent extends DrivingEvent {
  }

  export interface TransportEvent {
    id: string;
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    durationInSeconds: number | null;
    type: string;
    transportMode: TransportMode | null;
    waypoints: Waypoint[];
    distance?: number; // in meters
  }

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
  }

  export interface SentianceDrivingInsights {
    addDrivingInsightsReadyListener(onDrivingInsightsReady: (drivingInsights: DrivingInsights) => void): Promise<EmitterSubscription>;

    getDrivingInsights(transportId: string): Promise<DrivingInsights>;

    getHarshDrivingEvents(transportId: string): Promise<HarshDrivingEvent[]>;

    getPhoneUsageEvents(transportId: string): Promise<PhoneUsageEvent[]>;
  }

  const SentianceDrivingInsights: SentianceDrivingInsights;
  export default SentianceDrivingInsights;
}
