declare module "@sentiance-react-native/event-timeline" {
  import {EmitterSubscription} from "react-native";

  export interface Event {
    id: string;
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    lastUpdateTime: string;
    lastUpdateTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    durationInSeconds: number | null;
    type: EventType;
    // stationary event fields
    location: GeoLocation | null;
    venue: Venue | null;
    // transport event fields
    transportMode: TransportMode | null;
    waypoints: Waypoint[];
    distance?: number; // in meters
  }

  export interface GeoLocation {
    latitude: number;
    longitude: number;
    accuracy: number;
  }

  export interface Venue {
    location: GeoLocation | null;
    significance: VenueSignificance;
    type: VenueType;
  }

  export type VenueType =
    | "UNKNOWN"
    | "DRINK_DAY"
    | "DRINK_EVENING"
    | "EDUCATION_INDEPENDENT"
    | "EDUCATION_PARENTS"
    | "HEALTH"
    | "INDUSTRIAL"
    | "LEISURE_BEACH"
    | "LEISURE_DAY"
    | "LEISURE_EVENING"
    | "LEISURE_MUSEUM"
    | "LEISURE_NATURE"
    | "LEISURE_PARK"
    | "OFFICE"
    | "RELIGION"
    | "RESIDENTIAL"
    | "RESTO_MID"
    | "RESTO_SHORT"
    | "SHOP_LONG"
    | "SHOP_SHORT"
    | "SPORT"
    | "SPORT_ATTEND"
    | "TRAVEL_BUS"
    | "TRAVEL_CONFERENCE"
    | "TRAVEL_FILL"
    | "TRAVEL_HOTEL"
    | "TRAVEL_LONG"
    | "TRAVEL_SHORT"

  export type VenueSignificance =
    | "UNKNOWN"
    | "HOME"
    | "WORK"
    | "POINT_OF_INTEREST";

  export type EventType =
    | "UNKNOWN"
    | "STATIONARY"
    | "OFF_THE_GRID"
    | "IN_TRANSPORT";

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
  }

  export interface SentianceEventTimeline {
    getTimelineUpdates(afterEpochTimeMs: number): Promise<Event[]>;
    getTimelineEvents(fromEpochTimeMs: number, toEpochTimeMs: number): Promise<Event[]>;
    getTimelineEvent(eventId: string): Promise<Event | null>;
    addTimelineUpdateListener(onTimelineUpdated: (event: Event) => void): Promise<EmitterSubscription>;
  }

  /**
   * @deprecated Use SentianceEventTimeline instead.
   */
  export type EventTimelineApi = SentianceEventTimeline;

  const sentianceEventTimeline: SentianceEventTimeline;
  export default sentianceEventTimeline;
}
