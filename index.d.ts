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
    getInitState(): Promise<any>;
    getSdkStatus(): Promise<any>;
    getVersion(): Promise<any>;
    getUserId(): Promise<any>;
    getUserAccessToken(): Promise<any>;
    addUserMetadataField(label: string, value: string): Promise<any>;
    removeUserMetadataField(label: string): Promise<any>;
    getWiFiQuotaLimit(): Promise<any>;
    getWiFiQuotaUsage(): Promise<any>;
    getMobileQuotaLimit(): Promise<any>;
    getMobileQuotaUsage(): Promise<any>;
    getDiskQuotaLimit(): Promise<any>;
    getDiskQuotaUsage(): Promise<any>;
    disableBatteryOptimization(): Promise<any>;
    getUserActivity(): Promise<any>;
    listenUserActivityUpdates(): void;
    listenCrashEvents(): Promise<any>;
    listenTripProfiles(): void;
    updateTripProfileConfig(config: {
      enableFullProfiling: boolean,
      speedLimit?: number
    }): Promise<any>;
    userLinkCallback(success: Boolean): void;
    getValueForKey(key: string, defaultValue: string): Promise<any>;
    setValueForKey(key: string, value: string): Promise<any>;
  }

  const RNSentiance: RNSentianceConstructor;
  export default RNSentiance;
}
