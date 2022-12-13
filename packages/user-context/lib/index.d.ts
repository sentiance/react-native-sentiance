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
    | "BEAUTY_QUEEN"
    | "BRAND_LOYAL__BAR"
    | "BRAND_LOYAL__CAFE"
    | "BRAND_LOYAL__RESTAURANT"
    | "BRAND_LOYAL__RETAIL"
    | "BRAND_LOYALTY"
    | "BRAND_LOYALTY__GAS_STATIONS"
    | "BRAND_LOYALTY__RESTAURANT_BAR"
    | "BRAND_LOYALTY__SUPERMARKET"
    | "CITY_DRIVER"
    | "CITY_HOME"
    | "CITY_WORKER"
    | "CLUBBER"
    | "CULTURE_BUFF"
    | "DIE_HARD_DRIVER"
    | "DISTRACTED_DRIVER"
    | "DO_IT_YOURSELVER"
    | "DOG_WALKER"
    | "EARLY_BIRD"
    | "EASY_COMMUTER"
    | "EFFICIENT_DRIVER"
    | "FASHIONISTA"
    | "FOODIE"
    | "FREQUENT_FLYER"
    | "FULLTIME_WORKER"
    | "GAMER"
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
    | "RESTO_LOVER__AMERICAN"
    | "RESTO_LOVER__ASIAN"
    | "RESTO_LOVER__BARBECUE"
    | "RESTO_LOVER__FASTFOOD"
    | "RESTO_LOVER__FRENCH"
    | "RESTO_LOVER__GERMAN"
    | "RESTO_LOVER__GREEK"
    | "RESTO_LOVER__GRILL"
    | "RESTO_LOVER__INTERNATIONAL"
    | "RESTO_LOVER__ITALIAN"
    | "RESTO_LOVER__MEDITERRANEAN"
    | "RESTO_LOVER__MEXICAN"
    | "RESTO_LOVER__SEAFOOD"
    | "RESTO_LOVER__SNACK"
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

  export interface Event {
    startTime: string;
    startTimeEpoch: number; // in milliseconds
    endTime: string | null;
    endTimeEpoch: number | null; // in milliseconds
    durationInSeconds: number | null;
    type: string;
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

  export interface Waypoint {
    latitude: number;
    longitude: number;
    accuracy: number;   // in meters
    timestamp: number;  // UTC epoch time in milliseconds
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
  }

  export interface SentianceUserContext {
    requestUserContext(): Promise<UserContext>;

    addUserContextUpdateListener(
      onUserContextUpdated: (userContext: UserContext) => void
    ): Promise<EmitterSubscription>;
  }

  const SentianceUserContext: SentianceUserContext;
  export default SentianceUserContext;
}
