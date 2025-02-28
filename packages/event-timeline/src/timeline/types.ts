import { type EmitterSubscription } from "react-native";
import { type SentianceFeedback } from "../feedback/types";

/**
 * Represents an occurrence of an event that was detected for a user.
 */
export interface Event {
  /**
   * The unique identifier of the event.
   */
  id: string;
  /**
   * An ISO 8601 representation of the start time of the event.
   */
  startTime: string;
  /**
   * The start epoch time of the event, in milliseconds.
   */
  startTimeEpoch: number;
  /**
   * An ISO 8601 representation of the last update time of the event.
   */
  lastUpdateTime: string;
  /**
   * The last update epoch time of the event, in milliseconds.
   */
  lastUpdateTimeEpoch: number;
  /**
   * An ISO 8601 representation of the end time of the event if the event has ended, otherwise it is `null`.
   */
  endTime: string | null;
  /**
   * The end epoch time of the event in milliseconds if the event has ended, otherwise it is `null`.
   */
  endTimeEpoch: number | null;
  /**
   * the duration of the event in seconds if it has ended, otherwise returns it is `null`.
   */
  durationInSeconds: number | null;
  /**
   * The event type. Based on this type, you can determine whether the event is a transport or a stationary etc...
   */
  type: EventType;
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
  /**
   * The location where the user is stationary at.
   */
  location: GeoLocation | null;
  /**
   * The venue of this stationary.
   */
  venue: Venue | null;
  // transport event fields
  /**
   * The mode of transportation if the event is a transport, `null` otherwise.
   */
  transportMode: TransportMode | null;
  /**
   * The waypoints collected during the transport if the event is a transport, empty otherwise.
   */
  waypoints: Waypoint[];
  /**
   * The distance travelled during the transport in meters. If the distance cannot be computed then it is `null`.
   */
  distance?: number; // in meters
  /**
   * The set of tags that you have previously set (if any) for this transport, if the event is a transport - empty otherwise.
   * @see {SentianceEventTimeline.setTransportTags}
   */
  transportTags: TransportTags;
  /**
   * The detected user occupant role.
   */
  occupantRole: OccupantRole;
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

/**
 * @internal
 */
export const OCCUPANT_ROLE_VALUES = [
  "DRIVER",
  "PASSENGER",
  "UNAVAILABLE"
] as const;

export type OccupantRole = (typeof OCCUPANT_ROLE_VALUES)[number];

export interface Waypoint {
  latitude: number;
  longitude: number;
  accuracy: number;   // in meters
  timestamp: number;  // UTC epoch time in milliseconds
  speedInMps?: number;  // in meters per second
  speedLimitInMps?: number;  // in meters per second
  hasUnlimitedSpeedLimit: boolean;
  isSpeedLimitInfoSet: boolean;
  isSynthetic: boolean;
}

export type TransportTags = { [key: string]: string };

export interface SentianceEventTimeline {
  /**
   * Returns all updated events in the event timeline after the specified date, sorted by the last update time.
   *
   * @remarks
   * This method returns all events that started after `afterEpochTimeMs`, but it may also return events
   * that started before `afterEpochTimeMs`, if they were updated afterward. The returned result is not
   * necessarily the complete list of events that were captured by the SDK from the result's first event until the
   * last, because events that were not updated will be excluded. To get a complete and ordered list of
   * events for a given date range, use {@link getTimelineEvents} instead.
   *
   * You can use this method along with {@link addTimelineUpdateListener} to stay
   * up to date with the latest events in the timeline. For example, to make sure you don't miss out on timeline
   * updates, you can first set an update listener, then follow up by using this method to query for potential
   * updates since the last update you received.
   *
   * @param afterEpochTimeMs - The timestamp (in milliseconds) to retrieve updates from. The specified timestamp is **exclusive**.
   * @param includeProvisionalEvents - Optional parameter. Set to `true` if you want to include provisional events. Default is `false`.
   *
   * @returns A Promise that resolves with an array of Event objects that occurred in the specified time range.
   * If no events are found, an empty array is returned.
   *
   * @example
   * ```typescript
   * import timeline from "@sentiance-react-native/event-timeline";
   *
   * // Get timeline updates including provisional events from one hour ago
   * const oneHourAgo = Date.now() - (60 * 60 * 1000);
   * const events = await timeline.getTimelineUpdates(oneHourAgo, true);
   * ```
   */
  getTimelineUpdates(
    afterEpochTimeMs: number,
    includeProvisionalEvents?: boolean
  ): Promise<Event[]>;

  /**
   * Returns timeline events, such as transport and stationary events, that were captured during the specified
   * time range. The events are ordered from oldest to newest.
   *
   * @remarks
   * Events that were captured outside the specified date range are not returned, even if they were updated
   * during this date range. To get all updates, regardless of when an event was captured, use the
   * {@link getTimelineUpdates} function instead.
   *
   * @param fromEpochTimeMs - The start timestamp (in milliseconds, inclusive) of the time range.
   * @param toEpochTimeMs - The end timestamp (in milliseconds, inclusive) of the time range.
   * @param includeProvisionalEvents - Optional parameter. Set to `true` if you want to include provisional events. Default is `false`.
   *
   * @returns A Promise that resolves with an array of Event objects that occurred in the specified time range.
   * If no events are found, an empty array is returned.
   *
   * @example
   * ```typescript
   * import eventTimeline from "@sentiance-react-native/event-timeline";
   *
   * // Get all timeline events including provisional ones
   * const events = await eventTimeline.getTimelineEvents(0, Date.now(), true);
   * ```
   */
  getTimelineEvents(
    fromEpochTimeMs: number,
    toEpochTimeMs: number,
    includeProvisionalEvents?: boolean
  ): Promise<Event[]>;

  /**
   * Returns the timeline event with the specified ID.
   *
   * @param eventId - The ID of the event to query.
   *
   * @returns A Promise that resolves with a matching {@link Event} object, or `null` if no such event exists.
   *
   * @example
   * ```typescript
   * import eventTimeline from "@sentiance-react-native/event-timeline";
   * const event: Event | null = await eventTimeline.getTimelineEvent("some_event_id");
   * ```
   */
  getTimelineEvent(eventId: string): Promise<Event | null>;

  /**
   * Sets a listener that is invoked when the event timeline is updated. The listener receives the updated
   * events. An update can be triggered by the start of a new event, and the update or end of an existing one.
   * Every invocation of the listener will deliver an event that has a {@link Event.lastUpdateTimeEpoch | last update time}
   * that is equal to or greater than the previously delivered event's {@link Event.lastUpdateTimeEpoch | last update time}.
   *
   * @remarks
   * You can use this function along with {@link getTimelineUpdates} to stay up to date with the latest
   * events in the timeline. For example, to make sure you don't miss out on timeline updates, you can first set an
   * update listener using this method, then follow up by calling {@link getTimelineUpdates} to query for
   * potential updates since the last update you received.
   *
   * @param onTimelineUpdated - The callback to receive event timeline updates.
   * @param includeProvisionalEvents - Optional parameter. Set to `true` if you want to include provisional events. Default is `false`.
   *
   * @returns A Promise that resolves with a subscription object that you can use to remove the set listener.
   *
   * @example
   * ```typescript
   * import timeline from "@sentiance-react-native/event-timeline";
   *
   * // Set a timeline updater listener for non-provisional updates
   * const subscription = await timeline.addTimelineUpdateListener(
   *     (event) => console.log(event)
   * );
   *
   * // Remove the listener
   * await subscription.remove();
   * ```
   */
  addTimelineUpdateListener(
    onTimelineUpdated: (event: Event) => void,
    includeProvisionalEvents?: boolean
  ): Promise<EmitterSubscription>;

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
   * ```typescript
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
   * ```
   *
   * @param tags The transport tags to set
   *
   * @throws TransportTaggingError if the supplied tags count is more than 6, or if one of the tag components exceeds
   * the 256 characters limit.
   */
  setTransportTags(tags: TransportTags): Promise<void>;

  sentianceFeedback: SentianceFeedback;
}

/**
 * @deprecated Use SentianceEventTimeline instead.
 */
export type EventTimelineApi = SentianceEventTimeline;
