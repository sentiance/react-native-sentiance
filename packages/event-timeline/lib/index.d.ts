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
    transportTags: TransportTags;
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

  export type TransportTags = { [key: string]: string };

  export interface SentianceEventTimeline {
    getTimelineUpdates(afterEpochTimeMs: number): Promise<Event[]>;
    getTimelineEvents(fromEpochTimeMs: number, toEpochTimeMs: number): Promise<Event[]>;
    getTimelineEvent(eventId: string): Promise<Event | null>;
    addTimelineUpdateListener(onTimelineUpdated: (event: Event) => void): Promise<EmitterSubscription>;

    /**
     * Sets the tags that will be assigned to a detected transport.
     *
     * @remarks
     * The provided tags will be assigned to a transport at the moment the transport ends. When you
     * receive an {@link Event} representing the ended transport, it will include these tags.
     *
     * The supplied tags are persisted and applied to future transports, even after the app is restarted.
     * By calling this method again, you will replace the tags that will be assigned to future transports.
     *
     * You can include up to 6 tags (key-value pairs), and each tag component (key or value) must
     * be at most 256 characters.
     *
     * @example Setting custom transport tags
     * try {
     *   await SentianceEventTimeline.setTransportTags({
     *    key1: "value1",
     *    key2: "value2"
     *   });
     *   console.log('Transport tags have been set.');
     * } catch (error) {
     *   if (error instanceof TransportTagsError) {
     *     console.error(`Failed to set transport tags: ${error.message}`);
     *   } else {
     *     console.error(`Error: ${error.message}`);
     *   }
     * }
     *
     * @param tags The transport tags to set
     *
     * @throws TransportTaggingError if the supplied tags count is more than 6, or if one of the tag components exceeds
     * the 256 characters limit.
     */
    setTransportTags(tags: TransportTags): Promise<void>;
  }

  /**
   * @deprecated Use SentianceEventTimeline instead.
   */
  export type EventTimelineApi = SentianceEventTimeline;

  const sentianceEventTimeline: SentianceEventTimeline;
  export default sentianceEventTimeline;
}
