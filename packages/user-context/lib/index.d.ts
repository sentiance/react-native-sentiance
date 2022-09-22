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

  type TransportMode =
    | "UNKNOWN"
    | "BYCICLE"
    | "WALKING"
    | "RUNNING"
    | "TRAM"
    | "TRAIN"
    | "CAR"
    | "BUS"
    | "MOTORCYCLE";

  export interface Event {
    startTime: string;
    endTime: string | null;
    durationInSeconds: number | null;
    type: string;
    // stationary event fields
    location: EventLocation | null;
    venueSignificance: string | null;
    venueCandidates: VenueCandidate[] | null;
    // transport event fields
    transportMode: TransportMode | null;
  }

  export interface EventLocation {
    latitude: string;
    longitude: string;
    accuracy: string;
  }

  export interface VenueCandidate {
    venue: Venue;
    likelihood: number;
    visits: Visit[];
  }

  export interface Venue {
    name: string | null;
    location: EventLocation | null;
    venueLabels: VenueLabels;
  }

  export interface VenueLabels {
    [label: string]: string;
  }

  export interface Visit {
    startTime: string;
    endTime: string;
    durationInSeconds: number;
  }

  export interface Segment {
    category: SegmentCategory;
    subcategory: SegmentSubcategory;
    type: SegmentType;
    id: number;
    startTime: string;
    endTime: string | null;
    attributes: SegmentAttribute[];
  }

  export interface SegmentAttribute {
    name: string;
    value: number;
  }

  export interface UserContext {
    events: Event[];
    activeSegments: Segment[];
    lastKnownLocation: EventLocation | null;
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
