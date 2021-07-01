declare module "react-native-sentiance" {
  import { EventSubscriptionVendor, NativeEventEmitter, EmitterSubscription } from "react-native";

  export type SdkInitState = "NOT_INITIALIZED" | "INIT_IN_PROGRESS" | "INITIALIZED" | "RESETTING" | "UNRECOGNIZED_STATE";

  export type SdkResetFailureReason = "SDK_INIT_IN_PROGRESS" | "SDK_RESET_IN_PROGRESS" | "SDK_RESET_UNKNOWN_ERROR";

  export type InitIssue = "INVALID_CREDENTIALS" | "CHANGED_CREDENTIALS" | "SERVICE_UNREACHABLE" | "LINK_FAILED" | "SDK_RESET_IN_PROGRESS" | "INITIALIZATION_ERROR";

  export type VehicleMode = "IDLE" | "VEHICLE" | "NOT_VEHICLE" | "UNKNOWN";

  export type TripType = "TRIP_TYPE_SDK" | "TRIP_TYPE_EXTERNAL";

  export type LocationPermission = "ALWAYS" | "ONLY_WHILE_IN_USE" | "NEVER";

  export type SegmentCategory = "LEISURE" | "MOBILITY" | "WORK_LIFE";

  export type SegmentSubcategory = "COMMUTE" | "DRIVING" | "ENTERTAINMENT" | "FAMILY" | "HOME" | "SHOPPING" | "SOCIAL" | "TRANSPORT" | "TRAVEL" | "WELLBEING" | "WINING_AND_DINING" | "WORK";

  export type SegmentType = "AGGRESSIVE_DRIVER", "ANTICIPATIVE_DRIVER", "BAR_GOER", "BEAUTY_QUEEN", "BRAND_LOYAL__BAR", "BRAND_LOYAL__CAFE", 
                            "BRAND_LOYAL__RESTAURANT", "BRAND_LOYAL__RETAIL", "BRAND_LOYALTY", "BRAND_LOYALTY__GAS_STATIONS", 
                            "BRAND_LOYALTY__RESTAURANT_BAR", "BRAND_LOYALTY__SUPERMARKET", "CITY_DRIVER", "CITY_HOME", "CITY_WORKER", 
                            "CLUBBER", "CULTURE_BUFF", "DIE_HARD_DRIVER", "DISTRACTED_DRIVER", "DO_IT_YOURSELVER", "DOG_WALKER", "EARLY_BIRD", 
                            "EASY_COMMUTER", "EFFICIENT_DRIVER", "FASHIONISTA", "FOODIE", "FREQUENT_FLYER", "FULLTIME_WORKER", "GAMER", 
                            "GREEN_COMMUTER", "HEALTHY_BIKER", "HEALTHY_WALKER", "HEAVY_COMMUTER", "HOME_BOUND", "HOMEBODY", "HOMEWORKER", 
                            "ILLEGAL_DRIVER", "LATE_WORKER", "LEGAL_DRIVER", "LONG_COMMUTER", "MOBILITY", "MOBILITY__HIGH", "MOBILITY__LIMITED", 
                            "MOBILITY__MODERATE", "MOTORWAY_DRIVER", "MUSIC_LOVER", "NATURE_LOVER", "NIGHT_OWL", "NIGHTWORKER", "NORMAL_COMMUTER", 
                            "PARTTIME_WORKER", "PET_OWNER", "PHYSICAL_ACTIVITY__HIGH", "PHYSICAL_ACTIVITY__LIMITED", "PHYSICAL_ACTIVITY__MODERATE", 
                            "PUBLIC_TRANSPORTS_COMMUTER", "PUBLIC_TRANSPORTS_USER", "RECENTLY_CHANGED_JOB", "RECENTLY_MOVED_HOME", "RESTO_LOVER", 
                            "RESTO_LOVER__AMERICAN", "RESTO_LOVER__ASIAN", "RESTO_LOVER__BARBECUE", "RESTO_LOVER__FASTFOOD", "RESTO_LOVER__FRENCH", 
                            "RESTO_LOVER__GERMAN", "RESTO_LOVER__GREEK", "RESTO_LOVER__GRILL", "RESTO_LOVER__INTERNATIONAL", "RESTO_LOVER__ITALIAN", 
                            "RESTO_LOVER__MEDITERRANEAN", "RESTO_LOVER__MEXICAN", "RESTO_LOVER__SEAFOOD", "RESTO_LOVER__SNACK", "RURAL_HOME", 
                            "RURAL_WORKER", "SHOPAHOLIC", "SHORT_COMMUTER", "SLEEP_DEPRIVED", "SOCIAL_ACTIVITY", "SOCIAL_ACTIVITY__HIGH", 
                            "SOCIAL_ACTIVITY__LIMITED", "SOCIAL_ACTIVITY__MODERATE", "SPORTIVE", "STUDENT", "TOWN_HOME", "TOWN_WORKER", 
                            "UBER_PARENT", "WORK_LIFE_BALANCE", "WORK_TRAVELLER", "WORKAHOLIC";

  export enum TransportMode {
    UNKNOWN = 1,
    CAR,
    BICYCLE,
    ON_FOOT,
    TRAIN,
    TRAM,
    BUS,
    PLANE,
    BOAT,
    METRO,
    RUNNING,
  }

  export interface SdkStatus {
    startStatus: string;
    canDetect: boolean;
    isRemoteEnabled: boolean;
    isAccelPresent: boolean;
    isGyroPresent: boolean;
    isGpsPresent: boolean;
    wifiQuotaStatus: string;
    mobileQuotaStatus: string;
    diskQuotaStatus: string;
    locationPermission: LocationPermission;
    isBgAccessPermGranted?: boolean; // iOS only
    isActivityRecognitionPermGranted?: boolean; // Android only
    locationSetting?: string; // Android only
    isAirplaneModeEnabled?: boolean; // Android only
    isLocationAvailable?: boolean; // Android only
    isGooglePlayServicesMissing?: boolean; // Android only
    isBatteryOptimizationEnabled?: boolean; // Android only
    isBatterySavingEnabled?: boolean; // Android only
    isBackgroundProcessingRestricted?: boolean; // Android only
    isPreciseLocationAuthorizationGranted?: boolean; // iOS only
  }

  export interface UserAccessToken {
    tokenId: string;
    expiryDate?: string; // Android only
  }

  export interface MetadataObject {
    [key: string]: string;
  }

  export interface TripInfo {
    type: "TRIP_TYPE_SDK" | "TRIP_TYPE_EXTERNAL" | "TRIP_TYPE_UNRECOGNIZED" | "ANY";
  }

  export interface Location {
    latitude: string;
    longitude: string;
    accuracy?: string; // Android only
    altitude?: string; // Android only
    provider?: string; // Android only
  }

  export interface StationaryInfo {
    location?: Location;
  }

  export interface UserActivity {
    type: "USER_ACTIVITY_TYPE_TRIP" | "USER_ACTIVITY_TYPE_STATIONARY" | "USER_ACTIVITY_TYPE_UNKNOWN" | "USER_ACTIVITY_TYPE_UNRECOGNIZED";
    tripInfo?: TripInfo;
    stationaryInfo?: StationaryInfo;
  }

  export interface TripProfileConfig {
    enableFullProfiling: boolean;
    speedLimit?: number;
  }

  export interface CrashEvent {
    time: number;
    lastKnownLocation?: Location;
  }

  export interface VehicleCrashEvent {
    time: number;
    location?: Location;
    magnitude?: number;
    speedAtImpact?: number;
    deltaV?: number;
    confidence?: number;
  }

  export interface HardEvent {
    magnitude: number;
    timestamp: number;
  }

  export interface TransportSegments {
    startTime: number;
    endTime: number;
    distance?: number;
    averageSpeed?: number;
    topSpeed?: number;
    percentOfTimeSpeeding?: number;
    vehicleMode: VehicleMode;
    hardEvents: HardEvent[];
  }

  export interface TripProfile {
    tripId: string;
    transportSegments: TransportSegments[];
  }

  export type SdkEvent = "SDKStatusUpdate" | "SDKUserLink" | "SDKUserActivityUpdate" | "SDKTripProfile" | "SDKTripTimeout" | "VehicleCrashEvent" | "UserContextUpdateEvent";

  export type SDKStatusUpdateListener = (sdkStatus: SdkStatus) => void;

  export type SDKUserLinkListener = (param: { installId: string }) => void;

  export type SDKUserActivityUpdateListener = (userActivity: UserActivity) => void;

  export type SDKCrashEventListener = (crashEvent: CrashEvent) => void;

  export type SDKTripProfileListener = (tripProfile: TripProfile) => void;

  export type SdkEventListener =
    SDKStatusUpdateListener |
    SDKUserLinkListener |
    SDKUserActivityUpdateListener |
    SDKCrashEventListener |
    SDKTripProfileListener;

  export interface VenueLabels {
    [labels: string]: string;
  }

  export interface Venue {
    name?: string;
    location: Location;
    VenueLabels: VenueLabels;
  }

  export interface Visit {
    startTime: string;
    endTime: string;
  }

  export interface VenueCandidate {
    venue: Venue;
    likelihood: number;
    visits: Visit[];
  }

  export interface Event {
    startTime: string;
    endTime?: string;
    type: "UNKNOWN" | "STATIONARY" | "OFF_THE_GRID" | "IN_TRANSPORT";
  }

  export interface OffTheGridEvent extends Event {}

  export interface UnknownEvent extends Event {}

  export interface StationaryEvent extends Event {
    venueType: "UNKNOWN" | "HOME" | "WORK" | "POINT_OF_INTEREST";
    venueCandidates: VenueCandidate[];
  }

  export interface TransportEvent extends Event {
    transportMode: "UNKNOWN" | "BYCICLE" | "WALKING" | "RUNNING" | "VEHICLE" | "RAIL";
  }

  export interface Moment {

  }

  export interface SegmentAttribute {
    name: string,
    value: number
  }

  export interface Segment {
    category: SegmentCategory;
    subcategory: SegmentSubcategory;
    type: SegmentType;
    id: number;
    startTime: string;
    endTime?: string;
    attributes: SegmentAttribute[];
  }

  export interface UserContext {
    events: Event[];
    activeMoments: Moment[];
    activeSegments: Segment[];
    lastKnownLocation?: Location;
    home?: Venue;
    work?: Venue;
  }

  export type UserContextUpdateCriteria = "CURRENT_EVENT" | "ACTIVE_MOMENTS" | "ACTIVE_SEGMENTS" | "VISITED_VENUES";

  export interface UserContextUpdateEvent {
    userContext: UserContext;
    criteria: UserContextUpdateCriteria[];
  }

  export interface RNSentianceConstructor extends EventSubscriptionVendor {
    init(
      appId: string,
      secret: string,
      baseURL: string | null,
      shouldStart: boolean
    ): Promise<boolean | SdkStatus>;
    initWithUserLinkingEnabled(
      appId: string,
      secret: string,
      baseURL: string | null,
      shouldStart: boolean
    ): Promise<boolean | SdkStatus>;
    start(): Promise<SdkStatus>;
    startWithStopDate(stopEpochTimeMs: number): Promise<SdkStatus>;
    stop(): Promise<boolean>;
    reset(): Promise<boolean>;
    getInitState(): Promise<SdkInitState>;
    getSdkStatus(): Promise<SdkStatus>;
    getVersion(): Promise<string>;
    getUserId(): Promise<string>;
    getUserAccessToken(): Promise<UserAccessToken>;
    addUserMetadataField(label: string, value: string): Promise<boolean>;
    addUserMetadataFields(metadata: MetadataObject): Promise<boolean>;
    removeUserMetadataField(label: string): Promise<boolean>;
    getWiFiQuotaLimit(): Promise<string>;
    getWiFiQuotaUsage(): Promise<string>;
    getMobileQuotaLimit(): Promise<string>;
    getMobileQuotaUsage(): Promise<string>;
    getDiskQuotaLimit(): Promise<string>;
    getDiskQuotaUsage(): Promise<string>;
    disableBatteryOptimization(): Promise<boolean>;
    getUserActivity(): Promise<UserActivity>;
    listenUserActivityUpdates(): Promise<boolean>;
    listenTripProfiles(): Promise<boolean>;
    updateTripProfileConfig(config: TripProfileConfig): Promise<boolean>;
    userLinkCallback(success: boolean): void;
    getValueForKey(key: string, defaultValue: string): Promise<string>;
    setValueForKey(key: string, value: string): void;
    startTrip(metadata: MetadataObject|null, hint: TransportMode): Promise<boolean>;
    stopTrip(): Promise<boolean>;
    isTripOngoing(type: TripType): Promise<boolean>;
    submitDetections(): Promise<boolean>;
    updateSdkNotification(title: string, message: string): Promise<boolean>;
    addTripMetadata(metadata: MetadataObject): Promise<boolean>;
    isThirdPartyLinked(): Promise<boolean>;
    isNativeInitializationEnabled(): Promise<boolean>;
    enableNativeInitialization(): Promise<boolean>;
    disableNativeInitialization(): Promise<boolean>;
    listenVehicleCrashEvents(): Promise<boolean>;
    invokeDummyVehicleCrash(): Promise<boolean>;
    isVehicleCrashDetectionSupported(): Promise<boolean>;
    getUserContext(): Promise<UserContext>;
    listenUserContextUpdates(): Promise<boolean>;
    setAppSessionDataCollectionEnabled(enabled: boolean): Promise<boolean>;
    isAppSessionDataCollectionEnabled(): Promise<boolean>;
  }

  export interface RNSentianceEventEmitter extends NativeEventEmitter {
    addListener(eventType: SdkEvent, listener: SdkEventListener, context?: any): EmitterSubscription;
  }

  export interface RNSentianceEventSubscriptions {
    sdkStatusSubscription?: EmitterSubscription;
    sdkUserLinkSubscription?: EmitterSubscription;
    sdkUserActivityUpdateSubscription?: EmitterSubscription;
    sdkCrashEventSubscription?: EmitterSubscription;
    sdkTripProfileListener?: EmitterSubscription;
  }

  const RNSentiance: RNSentianceConstructor;
  export default RNSentiance;
}
