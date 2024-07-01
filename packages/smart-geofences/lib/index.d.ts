declare module "@sentiance-react-native/smart-geofences" {
  import { EmitterSubscription } from "react-native";
  import { SdkStatus } from "@sentiance-react-native/core";

  export type SmartGeofenceEventType = "ENTRY" | "EXIT";

  export interface SmartGeofencesRefreshFailureReason {
    /**
     * The feature is not enabled for your app.
     */
    FEATURE_NOT_ENABLED: "FEATURE_NOT_ENABLED";
    /**
     * Failed to refresh the monitored geofences due to network usage being restricted.
     * In most cases, this means the device is connected to a mobile data network, but you have disabled
     * mobile data network usage using the {@link SentianceCore.setIsAllowedToUseMobileData core module's mobile usage API}.
     */
    NETWORK_USAGE_RESTRICTED: "NETWORK_USAGE_RESTRICTED";
    /**
     * A network error was encountered when attempting to refresh the monitored geofences.
     */
    NETWORK_ERROR: "NETWORK_ERROR";
    /**
     * A server error was encountered when attempting to refresh the monitored geofences.
     */
    SERVER_ERROR: "SERVER_ERROR";
    /**
     * The application tried to refresh the list of monitored geofences more than once within
     * the last 30 seconds.
     */
    TOO_MANY_FREQUENT_CALLS: "TOO_MANY_FREQUENT_CALLS";
    /**
     * An unexpected error occurred. Check the corresponding {@link SmartGeofencesRefreshError#details} for more info.
     */
    UNEXPECTED_ERROR: "UNEXPECTED_ERROR";
    /**
     * No Sentiance user is present on device. Call {@link SentianceCore.createUser} to create a user.
     */
    NO_USER: "NO_USER";
    /**
     * The user is disabled remotely.
     */
    USER_DISABLED_REMOTELY: "USER_DISABLED_REMOTELY";
  }

  export enum DetectionMode {
    /**
     * The Smart Geofences feature is not enabled for your app.
     */
    FEATURE_NOT_ENABLED = "FEATURE_NOT_ENABLED",
    /**
     * Geofence entry/exit detections are disabled. Check the {@link SdkStatus } to find out why.
     */
    DISABLED = "DISABLED",
    /**
     * Geofence entry/exit detections are limited to when the app is visible in the foreground,
     * due to restricted location permission.
     */
    FOREGROUND = "FOREGROUND",
    /**
     * Geofence entry/exit detections are running unrestricted in the background.
     */
    BACKGROUND = "BACKGROUND"
  }

  export interface Location {
    timestamp: number;
    latitude: number;
    longitude: number;
    accuracy?: number;
    altitude?: number;
    provider?: string; // Android only
  }

  export interface SmartGeofence {
    sentianceId: string;
    latitude: number;
    longitude: number;
    radius: number;
    externalId: string;
  }

  export interface SmartGeofenceEvent {
    timestamp: number;
    triggeringLocation: Location;
    eventType: SmartGeofenceEventType;
    geofences: SmartGeofence[];
  }

  export interface SmartGeofencesRefreshError {
    reason: SmartGeofencesRefreshFailureReason;
    details?: string;
  }

  export interface SentianceSmartGeofences {
    /**
     * Refreshes the list of geofences.
     *
     * @returns A Promise that resolves if the refresh was successful.
     *          The Promise does not return any value upon resolution.
     *
     * @throws {SmartGeofencesRefreshError} If the refresh fails, the Promise
     *         is rejected with an object of type `SmartGeofencesRefreshError`.
     *         This object contains a `reason` property detailing the nature of the error.
     *
     * @example
     * try {
     *     await refreshGeofences();
     *     console.log("Geofences refreshed successfully.");
     * } catch (error) {
     *     const refreshError = error.userInfo as SmartGeofencesRefreshError;
     *     console.error("Error refreshing geofences:", refreshError.reason);
     * }
     */
    refreshGeofences(): Promise<void>;

    /**
     * Registers a listener for smart geofence events.
     *
     * This function sets up an event listener that will execute the provided callback
     * function whenever a smart geofence event occurs (could be an entry, or an exit event).
     *
     * @param {Function} onSmartGeofenceEvent - A callback function that is called whenever
     *        a smart geofence event is triggered. The function receives a single argument:
     *        a `SmartGeofenceEvent` object containing details about the event.
     *
     * @returns {Promise<EmitterSubscription>} A Promise that resolves to an EmitterSubscription.
     *          This subscription object can be used to unsubscribe from the event notifications
     *          in the future, by calling `remove()` on the returned subscription object.
     *
     * @example
     * const subscription = await addSmartGeofenceEventListener(event => {
     *   console.log('Geofence event received:', event);
     * });
     * console.log('Listener registered, subscription:', subscription);
     */
    addSmartGeofenceEventListener(onSmartGeofenceEvent: (smartGeofenceEvent: SmartGeofenceEvent) => void): Promise<EmitterSubscription>;

    /**
     * Retrieves the geofence entry/exit detection mode.
     *
     * @example
     * const detectionMode = await getDetectionMode();
     * console.log('Detection mode is currently:', detectionMode);
     */
    getDetectionMode(): Promise<DetectionMode>;
  }

  const SentianceSmartGeofences: SentianceSmartGeofences;
  export default SentianceSmartGeofences;
}
