declare module "@sentiance-react-native/user-context" {
  import { EmitterSubscription } from "react-native";

  export type SegmentCategory = "LEISURE" | "MOBILITY" | "WORK_LIFE";
  export type SegmentSubcategory =
    | "COMMUTE"
    | "DRIVING"
    | "ENTERTAINMENT"
    | "FAMILY"
    | "HOME"
    | "SHOPPING"
    | "SOCIAL"
    | "TRANSPORT"
    | "TRAVEL"
    | "WELLBEING"
    | "WINING_AND_DINING"
    | "WORK";
  export type SegmentType =
    | "AGGRESSIVE_DRIVER"
    | "ANTICIPATIVE_DRIVER"
    | "BAR_GOER"
    | "CITY_DRIVER"
    | "CITY_HOME"
    | "CITY_WORKER"
    | "CULTURE_BUFF"
    | "DIE_HARD_DRIVER"
    | "DISTRACTED_DRIVER"
    | "DOG_WALKER"
    | "EARLY_BIRD"
    | "EASY_COMMUTER"
    | "EFFICIENT_DRIVER"
    | "FOODIE"
    | "FREQUENT_FLYER"
    | "FULLTIME_WORKER"
    | "GREEN_COMMUTER"
    | "HEALTHY_BIKER"
    | "HEALTHY_WALKER"
    | "HEAVY_COMMUTER"
    | "HOME_BOUND"
    | "HOMEBODY"
    | "HOMEWORKER"
    | "ILLEGAL_DRIVER"
    | "LATE_WORKER"
    | "LEGAL_DRIVER"
    | "LONG_COMMUTER"
    | "MOBILITY"
    | "MOBILITY__HIGH"
    | "MOBILITY__LIMITED"
    | "MOBILITY__MODERATE"
    | "MOTORWAY_DRIVER"
    | "MUSIC_LOVER"
    | "NATURE_LOVER"
    | "NIGHT_OWL"
    | "NIGHTWORKER"
    | "NORMAL_COMMUTER"
    | "PARTTIME_WORKER"
    | "PET_OWNER"
    | "PHYSICAL_ACTIVITY__HIGH"
    | "PHYSICAL_ACTIVITY__LIMITED"
    | "PHYSICAL_ACTIVITY__MODERATE"
    | "PUBLIC_TRANSPORTS_COMMUTER"
    | "PUBLIC_TRANSPORTS_USER"
    | "RECENTLY_CHANGED_JOB"
    | "RECENTLY_MOVED_HOME"
    | "RESTO_LOVER"
    | "RURAL_HOME"
    | "RURAL_WORKER"
    | "SHOPAHOLIC"
    | "SHORT_COMMUTER"
    | "SLEEP_DEPRIVED"
    | "SOCIAL_ACTIVITY"
    | "SOCIAL_ACTIVITY__HIGH"
    | "SOCIAL_ACTIVITY__LIMITED"
    | "SOCIAL_ACTIVITY__MODERATE"
    | "SPORTIVE"
    | "STUDENT"
    | "TOWN_HOME"
    | "TOWN_WORKER"
    | "UBER_PARENT"
    | "WORK_LIFE_BALANCE"
    | "WORK_TRAVELLER"
    | "WORKAHOLIC";

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

  export type VenueSignificance =
    | "UNKNOWN"
    | "HOME"
    | "WORK"
    | "POINT_OF_INTEREST"

  /**
   * The list of venue types that are currently supported by the venue-type mapping model:
   *
   * <ul>
   *   <li><b>DRINK_DAY</b> - Cafes, coffee bars, tea rooms, etc</li>
   *   <li><b>DRINK_EVENING</b> - Bars, pubs and in general places where one goes for drinks in evenings.</li>
   *   <li><b>EDUCATION_INDEPENDENT</b> - Educational institutions visited by the user on his own for their own studies. High schools, universities, colleges, etc.</li>
   *   <li><b>EDUCATION_PARENTS</b> - Schools and kindergartens visited by parents.</li>
   *   <li><b>HEALTH</b> - Hospitals, clinics, emergency rooms.</li>
   *   <li><b>INDUSTRIAL</b> - Buildings tagged as “industrial” on OSM, built for some manufacturing process.</li>
   *   <li><b>LEISURE_BEACH</b> - Beaches, resorts and swimming areas.</li>
   *   <li><b>LEISURE_DAY</b> - Bowling, billiards and other entertainment places.</li>
   *   <li><b>LEISURE_EVENING</b> - Cinemas, theatres and music halls.</li>
   *   <li><b>LEISURE_MUSEUM</b> - Museums.</li>
   *   <li><b>LEISURE_NATURE</b> - Forests, lakes, national parks, etc.</li>
   *   <li><b>LEISURE_PARK</b> - City parks, gardens, zoos.</li>
   *   <li><b>OFFICE</b> - Office buildings. For example, of private lawyers, notaries or company representatives.</li>
   *   <li><b>RELIGION</b> - Churches, mosques and other religion related buildings.</li>
   *   <li><b>RESIDENTIAL</b> - Apartment blocks, houses.</li>
   *   <li><b>RESTO_MID</b> - Food courts, restaurants, snack bars.</li>
   *   <li><b>RESTO_SHORT</b> - Ice cream, fast food, donut stores.</li>
   *   <li><b>SHOP_LONG</b> - Supermarkets, malls, wholesales, shopping centres.</li>
   *   <li><b>SHOP_SHORT</b> - Small grocery stores, butchers, bakers.</li>
   *   <li><b>SPORT</b> - Gyms, sport centres. Venues visited to exercise.</li>
   *   <li><b>SPORT_ATTEND</b> - Stadiums. Venues visited to attend a sport event.</li>
   *   <li><b>TRAVEL_BUS</b> - Bus stops.</li>
   *   <li><b>TRAVEL_CONFERENCE</b> - Conference, convention, exhibition centres.</li>
   *   <li><b>TRAVEL_FILL</b> - Gas stations.</li>
   *   <li><b>TRAVEL_HOTEL</b> - Hotels, motels, guest rooms, etc.</li>
   *   <li><b>TRAVEL_LONG</b> - Airports</li>
   *   <li><b>TRAVEL_SHORT</b> - Public transport stations, railway stations.</li>
   * </ul>
   */
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

  export type SemanticTime =
    | "UNKNOWN"
    | "MORNING"
    | "LATE_MORNING"
    | "LUNCH"
    | "AFTERNOON"
    | "EARLY_EVENING"
    | "EVENING"
    | "NIGHT";

  export type UserContextUpdateCriteria =
    | "CURRENT_EVENT"
    | "ACTIVE_SEGMENTS"
    | "VISITED_VENUES";

  export type OccupantRole =
    | "DRIVER"
    | "PASSENGER"
    | "UNAVAILABLE";

  export type TransportTags = { [key: string]: string };

  export interface Event {
    id: string;
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    lastUpdateTime: string;
    lastUpdateTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    durationInSeconds: number | null;
    type: string;
    /**
     * Indicates whether the event is provisional.
     *
     * <p>A provisional event is identified based on real-time detections, but may change in the near future
     * as more data is collected and processed, to filter out unwanted artifacts.
     * For example, a provisional car transport may get identified, followed by a provisional bus transport.
     * After the full trip is complete, these provisional events may get merged into a single final car event.</p>
     *
     * <p>Final events are generated independently of the provisional events, and have unique event IDs. They are
     * not linked to the provisional events they may resemble, replace, or overlap with.</p>
     *
     * <p>Currently, provisional events apply only to 'transport' types, as the SDK tries to determine the mode of
     * transport in (near) real time. When the full trip is complete (e.g. the user becomes stationary),
     * the collected data is reprocessed to produce a more accurate and cleaned up list of transport events.</p>
     */
    isProvisional: boolean;
    // stationary event fields
    location: GeoLocation | null;
    venue: Venue | null;
    // transport event fields
    transportMode: TransportMode | null;
    waypoints: Waypoint[];
    distance?: number; // in meters
    transportTags: TransportTags;
    occupantRole: OccupantRole;
  }

  export interface GeoLocation {
    latitude: number;
    longitude: number;
    accuracy: number;
  }

  export interface Waypoint {
    latitude: number;
    longitude: number;
    accuracy: number;   // in meters
    timestamp: number;  // UTC epoch time in milliseconds
    speedInMps?: number;  // in meters per second
    /**
     * - If {@link isSpeedLimitInfoSet} is `false`, then this value will be `undefined`.
     * - If {@link hasUnlimitedSpeedLimit} is `true`, then this value will be `Number.MAX_VALUE`.
     */
    speedLimitInMps?: number;  // in meters per second
    hasUnlimitedSpeedLimit: boolean;
    isSpeedLimitInfoSet: boolean;
    isSynthetic: boolean;
  }

  export interface Venue {
    location: GeoLocation | null;
    significance: VenueSignificance;
    type: VenueType;
  }

  export interface Segment {
    category: SegmentCategory;
    subcategory: SegmentSubcategory;
    type: SegmentType;
    id: number;
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    attributes: SegmentAttribute[];
  }

  export interface SegmentAttribute {
    name: string;
    value: number;
  }

  export interface UserContext {
    events: Event[];
    activeSegments: Segment[];
    lastKnownLocation: GeoLocation | null;
    home: Venue | null;
    work: Venue | null;
    semanticTime: SemanticTime;
  }

  export interface UserContextUpdate {
    readonly criteria: UserContextUpdateCriteria[];
    readonly userContext: UserContext;
  }

  export interface SentianceUserContext {
    requestUserContext(includeProvisionalEvents?: boolean): Promise<UserContext>;

    addUserContextUpdateListener(
      onUserContextUpdated: (userContextUpdate: UserContextUpdate) => void,
      includeProvisionalEvents?: boolean
    ): Promise<EmitterSubscription>;
  }

  const SentianceUserContext: SentianceUserContext;
  export default SentianceUserContext;
}
