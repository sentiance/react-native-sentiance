import { EventSubscriptionVendor } from "react-native";

interface SdkStatus {
  startStatus: string
  canDetect: boolean
  isRemoteEnabled: boolean
  isLocationPermGranted: boolean
  isAccelPresent: boolean
  isGyroPresent: boolean
  isGpsPresent: boolean
  wifiQuotaStatus: string
  mobileQuotaStatus: string
  diskQuotaStatus: string
  isBgAccessPermGranted?: boolean // iOS only
  isActivityRecognitionPermGranted?: boolean // Android only
  locationSetting?: string // Android only
  isAirplaneModeEnabled?: boolean // Android only
  isLocationAvailable?: boolean // Android only
  isGooglePlayServicesMissing?: boolean // Android only
  isBatteryOptimizationEnabled?: boolean // Android only
  isBatterySavingEnabled?: boolean // Android only
  isBackgroundProcessingRestricted?: boolean // Android only
}

interface UserAccessToken {
  tokenId: string
  expiryDate?: string // Android only
}

interface MetadataObject {
  [key: string]: string
}

type SdkInitState = "NOT_INITIALIZED" | "INIT_IN_PROGRESS" | "INITIALIZED" | "RESETTING" | "UNRECOGNIZED_STATE"

interface TripInfo {
  type: "TRIP_TYPE_SDK" | "TRIP_TYPE_EXTERNAL" | "TRIP_TYPE_UNRECOGNIZED" | "ANY"
}

interface Location {
  latitude: string
  longitude: string
  accuracy: string // Android only
  altitude: string // Android only
  provider?: string // Android only
}

interface StationaryInfo {
  location?: Location
}

interface UserActivity {
  type?: "USER_ACTIVITY_TYPE_TRIP" | "USER_ACTIVITY_TYPE_STATIONARY" | "USER_ACTIVITY_TYPE_UNKNOWN" | "USER_ACTIVITY_TYPE_UNRECOGNIZED"
  tripInfo?: TripInfo
  stationaryInfo?: StationaryInfo
}

interface TripProfileConfig {
  enableFullProfiling: boolean,
  speedLimit?: number
}

declare module "react-native-sentiance" {
  interface RNSentianceConstructor extends EventSubscriptionVendor {
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
    listenCrashEvents(): Promise<boolean>;
    listenTripProfiles(): Promise<boolean>;
    updateTripProfileConfig(config: TripProfileConfig): Promise<boolean>;
    userLinkCallback(success: boolean): void;
    getValueForKey(key: string, defaultValue: string): Promise<string>;
    setValueForKey(key: string, value: string): void;
  }

  const RNSentiance: RNSentianceConstructor;
  export default RNSentiance;
}
