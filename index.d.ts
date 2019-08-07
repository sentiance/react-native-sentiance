import { EventSubscriptionVendor } from "react-native";

declare module "react-native-sentiance" {
  interface RNSentianceConstructor extends EventSubscriptionVendor {
    init(appId: string, secret: string, baseURL: string): Promise<any>;
    initWithUserLinkingEnabled(
      appId: string,
      secret: string,
      baseURL: string,
      shouldStart: boolean
    ): Promise<any>;
    start(): Promise<any>;
    stop(): Promise<any>;
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
    userLinkCallback(success: Boolean): void;
    getValueForKey(key: string, defaultValue: string): Promise<any>;
    setValueForKey(key: string, value: string): Promise<any>;
  }

  const RNSentiance: RNSentianceConstructor;
  export default RNSentiance;
}
