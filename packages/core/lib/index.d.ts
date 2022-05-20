declare module "react-native-sentiance-core" {
  import {EmitterSubscription} from "react-native";

  export type LocationPermission = "ALWAYS" | "ONLY_WHILE_IN_USE" | "NEVER";
  export type SdkInitState =
    "NOT_INITIALIZED"
    | "INIT_IN_PROGRESS"
    | "INITIALIZED"
    | "RESETTING"
    | "UNRECOGNIZED_STATE";
  export type TripType = "TRIP_TYPE_SDK" | "TRIP_TYPE_EXTERNAL";

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

  export interface UserInfo {
    userId: string,
    tokenId: string,
    tokenExpiryDate: string,
    isTokenExpired: boolean
  }

  export interface UserLinkingResult {
    userInfo: UserInfo
  }

  export interface CreateUserResult {
    userInfo: UserInfo
  }

  export interface UserCreationOptions {
    appId: string,
    appSecret: string,
    authCode: string,
    platformUrl: string,
    linker: (installId: string) => boolean
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

  export interface TripInfo {
    type: "TRIP_TYPE_SDK" | "TRIP_TYPE_EXTERNAL" | "TRIP_TYPE_UNRECOGNIZED" | "ANY";
  }

  export interface UserActivity {
    type: "USER_ACTIVITY_TYPE_TRIP" | "USER_ACTIVITY_TYPE_STATIONARY" | "USER_ACTIVITY_TYPE_UNKNOWN" | "USER_ACTIVITY_TYPE_UNRECOGNIZED";
    tripInfo?: TripInfo;
    stationaryInfo?: StationaryInfo;
  }

  export interface MetadataObject {
    [key: string]: string;
  }

  export interface UserAccessToken {
    tokenId: string;
    expiryDate?: string; // Android only
  }

  export interface ResetResult {
    initState: string
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

  export interface EnableDisableDetectionsResult {
    sdkStatus: SdkStatus,
    detectionStatus: string
  }

  export interface SentianceCore {
    userLinkCallback(linkResult: boolean): void;

    userExists(): Promise<boolean>;

    enableDetections(): Promise<EnableDisableDetectionsResult>;

    enableDetectionsWithExpiryDate(expiryEpochTimeMs: number | null): Promise<EnableDisableDetectionsResult>;

    disableDetections(): Promise<EnableDisableDetectionsResult>;

    reset(): Promise<ResetResult>;

    isUserLinked(): Promise<boolean>;

    getVersion(): Promise<string>;

    getUserId(): Promise<string>;

    requestUserAccessToken(): Promise<UserAccessToken>;

    addUserMetadataField(label: string, value: string): Promise<void>;

    addUserMetadataFields(label: MetadataObject): Promise<void>;

    removeUserMetadataField(label: string): Promise<void>;

    getUserActivity(): Promise<UserActivity>;

    listenUserActivityUpdates(): Promise<void>;

    listenTripTimeout(): Promise<void>;

    startTrip(metadata: MetadataObject | null, hint: TransportMode): Promise<void>;

    stopTrip(): Promise<void>;

    isTripOngoing(type: TripType): Promise<boolean>;

    submitDetections(): Promise<void>;

    updateSdkNotification(title: string, message: string): Promise<void>;

    addTripMetadata(metadata: MetadataObject): Promise<boolean>;

    setAppSessionDataCollectionEnabled(enabled: boolean): Promise<void>;

    isAppSessionDataCollectionEnabled(): Promise<boolean>;

    getInitState(): Promise<SdkInitState>;

    getSdkStatus(): Promise<SdkStatus>;

    getWiFiQuotaLimit(): Promise<string>;

    getWiFiQuotaUsage(): Promise<string>;

    getMobileQuotaLimit(): Promise<string>;

    getMobileQuotaUsage(): Promise<string>;

    getDiskQuotaLimit(): Promise<string>;

    getDiskQuotaUsage(): Promise<string>;

    disableBatteryOptimization(): Promise<void>;

    createUser(options: UserCreationOptions): Promise<CreateUserResult>;

    linkUser(): Promise<UserLinkingResult>;

    linkUserWithAuthCode(authCode: string): Promise<UserLinkingResult>;

    addSdkStatusUpdateListener(onSdkStatusUpdated: (sdkStatus: SdkStatus) => void): Promise<EmitterSubscription>;

    addTripTimeoutListener(onTripTimedOut: () => void): Promise<EmitterSubscription>;

    addOnDetectionsEnabledListener(onDetectionsEnabled: (sdkStatus: SdkStatus) => void): Promise<EmitterSubscription>;

    addSdkUserActivityUpdateListener(onUserActivityUpdated: (userActivity: UserActivity) => void): Promise<EmitterSubscription>;
  }

  const SentianceCore: SentianceCore;
  export default SentianceCore;
}