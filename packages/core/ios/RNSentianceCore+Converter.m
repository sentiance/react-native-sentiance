//
//  RNSentianceCore+Converter.m
//  RNSentianceCore
//
//  Created by Sebouh Aguehian on 10/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "RNSentianceCore+Converter.h"
@import UIKit.UIApplication;
@import CoreLocation;

static NSString * const SmartGeofencesErrorDomain = @"com.sentiance.SmartGeofencesModule";


@interface RNSentianceCore (Private)

- (NSString*)convertTimelineEventTypeToString:(SENTTimelineEventType)type;
- (NSDictionary*)convertGeolocation:(SENTGeolocation*)location;
- (NSDictionary*)convertCllocation:(CLLocation*)location;
- (NSString*)convertVenueSignificance:(SENTVenueSignificance)type;
- (NSDictionary*)convertVenue:(SENTVenue*)venue;
- (void)addStationaryEventInfoToDict:(NSMutableDictionary*)dict event:(SENTStationaryEvent*)event;
- (NSString*)convertTransportModeToString:(SENTTimelineTransportMode) mode;
- (void)addTransportEventInfoToDict:(NSMutableDictionary*)dict event:(SENTTransportEvent*)event;
- (nullable NSString*)convertSegmentCategoryToString:(SENTSegmentCategory)category;
- (nullable NSString*)convertSegmentSubcategoryToString:(SENTSegmentSubcategory)subcategory;
- (nullable NSString*)convertSegmentTypeToString:(SENTSegmentType)type;
- (NSArray*)convertSegmentAttributes:(NSArray<SENTAttribute *>*)attributes;
- (nullable NSDictionary*)convertSegment:(SENTSegment*)segment;
- (NSString*)convertBackgroundRefreshStatus:(UIBackgroundRefreshStatus)backgroundRefreshStatus;

@end

@implementation RNSentianceCore (Converter)

- (NSString*)convertTimelineEventTypeToString:(SENTTimelineEventType)type {
    switch (type) {
        case SENTTimelineEventTypeStationary:
            return @"STATIONARY";
        case SENTTimelineEventTypeOffTheGrid:
            return @"OFF_THE_GRID";
        case SENTTimelineEventTypeInTransport:
            return @"IN_TRANSPORT";
        case SENTTimelineEventTypeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (NSDictionary*)convertGeolocation:(SENTGeolocation*)location {
    return @{
        @"latitude": @(location.latitude),
        @"longitude": @(location.longitude),
        @"accuracy": @(location.accuracyInMeters)
    };
}

- (NSDictionary*)convertCllocation:(CLLocation*)location {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[@"timestamp"] = @((long) ([location.timestamp timeIntervalSince1970] * 1000));
    dict[@"latitude"] = @(location.coordinate.latitude);
    dict[@"longitude"] = @(location.coordinate.longitude);

    if (location.horizontalAccuracy >= 0) {
        dict[@"accuracy"] = @(location.horizontalAccuracy);
    }
    if (location.verticalAccuracy > 0) {
        dict[@"altitude"] = @(location.altitude);
    }

    return dict;
}

- (NSString*)convertSemanticTime:(SENTSemanticTime)semanticTime {
    switch (semanticTime) {
        case SENTSemanticTimeMorning:
            return @"MORNING";
        case SENTSemanticTimeLateMorning:
            return @"LATE_MORNING";
        case SENTSemanticTimeLunch:
            return @"LUNCH";
        case SENTSemanticTimeAfternoon:
            return @"AFTERNOON";
        case SENTSemanticTimeEarlyEvening:
            return @"EARLY_EVENING";
        case SENTSemanticTimeEvening:
            return @"EVENING";
        case SENTSemanticTimeNight:
            return @"NIGHT";
        case SENTSemanticTimeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (NSString*)convertVenueSignificance:(SENTVenueSignificance)type {
    switch (type) {
        case SENTVenueSignificanceHome:
            return @"HOME";
        case SENTVenueSignificanceWork:
            return @"WORK";
        case SENTVenueSignificancePointOfInterest:
            return @"POINT_OF_INTEREST";
        case SENTVenueSignificanceUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (NSString*)convertVenueType:(SENTVenueType)type {
    switch (type) {
        case SENTVenueTypeDrinkDay:
            return @"DRINK_DAY";
        case SENTVenueTypeDrinkEvening:
            return @"DRINK_EVENING";
        case SENTVenueTypeEducationIndependent:
            return @"EDUCATION_INDEPENDENT";
        case SENTVenueTypeEducationParents:
            return @"EDUCATION_PARENTS";
        case SENTVenueTypeHealth:
            return @"HEALTH";
        case SENTVenueTypeIndustrial:
            return @"INDUSTRIAL";
        case SENTVenueTypeLeisureBeach:
            return @"LEISURE_BEACH";
        case SENTVenueTypeLeisureDay:
            return @"LEISURE_DAY";
        case SENTVenueTypeLeisureEvening:
            return @"LEISURE_EVENING";
        case SENTVenueTypeLeisureMuseum:
            return @"LEISURE_MUSEUM";
        case SENTVenueTypeLeisureNature:
            return @"LEISURE_NATURE";
        case SENTVenueTypeLeisurePark:
            return @"LEISURE_PARK";
        case SENTVenueTypeOffice:
            return @"OFFICE";
        case SENTVenueTypeReligion:
            return @"RELIGION";
        case SENTVenueTypeResidential:
            return @"RESIDENTIAL";
        case SENTVenueTypeRestoMid:
            return @"RESTO_MID";
        case SENTVenueTypeRestoShort:
            return @"RESTO_SHORT";
        case SENTVenueTypeShopLong:
            return @"SHOP_LONG";
        case SENTVenueTypeShopShort:
            return @"SHOP_SHORT";
        case SENTVenueTypeSport:
            return @"SPORT";
        case SENTVenueTypeSportAttend:
            return @"SPORT_ATTEND";
        case SENTVenueTypeTravelBus:
            return @"TRAVEL_BUS";
        case SENTVenueTypeTravelConference:
            return @"TRAVEL_CONFERENCE";
        case SENTVenueTypeTravelFill:
            return @"TRAVEL_FILL";
        case SENTVenueTypeTravelHotel:
            return @"TRAVEL_HOTEL";
        case SENTVenueTypeTravelLong:
            return @"TRAVEL_LONG";
        case SENTVenueTypeTravelShort:
            return @"TRAVEL_SHORT";
        case SENTVenueTypeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (NSDictionary*)convertVenue:(SENTVenue*)venue {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (venue.location != nil) {
        dict[@"location"] = [self convertGeolocation:venue.location];
    }

    dict[@"significance"] = [self convertVenueSignificance:venue.significance];
    dict[@"type"] = [self convertVenueType:venue.type];

    return dict;
}

- (void)addStationaryEventInfoToDict:(NSMutableDictionary*)dict event:(SENTStationaryEvent*)event {
    if (event.location != nil) {
        dict[@"location"] = [self convertGeolocation:event.location];
    }

    dict[@"venue"] = [self convertVenue:event.venue];
}

- (NSString*)convertTransportModeToString:(SENTTimelineTransportMode)mode {
    switch (mode) {
        case SENTTimelineTransportModeBicycle:
            return @"BYCICLE";
        case SENTTimelineTransportModeWalking:
            return @"WALKING";
        case SENTTimelineTransportModeRunning:
            return @"RUNNING";
        case SENTTimelineTransportModeTram:
            return @"TRAM";
        case SENTTimelineTransportModeTrain:
            return @"TRAIN";
        case SENTTimelineTransportModeCar:
            return @"CAR";
        case SENTTimelineTransportModeBus:
            return @"BUS";
        case SENTTimelineTransportModeMotorcycle:
            return @"MOTORCYCLE";
        case SENTTimelineTransportModeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (void)addTransportEventInfoToDict:(NSMutableDictionary*)dict event:(SENTTransportEvent*)event {
    dict[@"transportMode"] = [self convertTransportModeToString:event.transportMode];
    dict[@"waypoints"] = [self convertWaypointArray:event.waypoints];

    if (event.distanceInMeters != nil) {
        dict[@"distance"] = event.distanceInMeters;
    }
}

- (NSDictionary*)convertWaypoint:(SENTWaypoint*)waypoint {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[@"latitude"] = @(waypoint.latitude);
    dict[@"longitude"] = @(waypoint.longitude);
    dict[@"accuracy"] = @(waypoint.accuracyInMeters);
    dict[@"timestamp"] = @(waypoint.timestamp * 1000);

    if (waypoint.isSpeedSet) {
        dict[@"speedInMps"] = @(waypoint.speedInMps);
    }
    if (waypoint.isSpeedLimitInfoSet) {
        dict[@"speedLimitInMps"] = @(waypoint.speedLimitInMps);
    }
    dict[@"isSpeedLimitInfoSet"] = @(waypoint.isSpeedLimitInfoSet);
    dict[@"hasUnlimitedSpeedLimit"] = @(waypoint.isSpeedLimitUnlimited);

    return dict;
}

- (NSArray<NSDictionary *> *)convertWaypointArray:(NSArray<SENTWaypoint *> *)waypoints {
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for (SENTWaypoint *waypoint in waypoints) {
        [array addObject:[self convertWaypoint:waypoint]];
    }
    return array;
}

- (NSMutableDictionary*)convertEvent:(SENTTimelineEvent*)event {
    NSMutableDictionary *eventDict = [[NSMutableDictionary alloc] init];

    [self _addBaseEventFields:eventDict event:event];

    eventDict[@"type"] = [self convertTimelineEventTypeToString:event.type];

    if (event.type == SENTTimelineEventTypeStationary) {
        [self addStationaryEventInfoToDict:eventDict event:(SENTStationaryEvent*)event];
    }
    else if (event.type == SENTTimelineEventTypeInTransport) {
        [self addTransportEventInfoToDict:eventDict event:(SENTTransportEvent*)event];
    }

    return eventDict;
}

- (NSMutableDictionary*) _addBaseEventFields:(NSMutableDictionary*) eventDict event: (SENTTimelineEvent*)event {
    eventDict[@"id"] = [event eventId];
    eventDict[@"startTime"] = [event.startDate description];
    eventDict[@"startTimeEpoch"] = @((long) (event.startDate.timeIntervalSince1970 * 1000));
    eventDict[@"lastUpdateTime"] = [event.lastUpdateDate description];
    eventDict[@"lastUpdateTimeEpoch"] = @((long) (event.lastUpdateDate.timeIntervalSince1970 * 1000));
    if (event.endDate != nil) {
        eventDict[@"endTime"] = [event.endDate description];
        eventDict[@"endTimeEpoch"] = @((long) (event.endDate.timeIntervalSince1970 * 1000));

        NSInteger durationInSeconds = event.durationInSeconds;
        if (durationInSeconds != SENTDurationUnknown) {
            eventDict[@"durationInSeconds"] = [NSNumber numberWithInt:(int)durationInSeconds];
        }
    }
    return eventDict;
}

- (nullable NSString*)convertSegmentCategoryToString:(SENTSegmentCategory)category {
    switch (category) {
        case SENTSegmentCategoryLeisure:
            return @"LEISURE";
        case SENTSegmentCategoryMobility:
            return @"MOBILITY";
        case SENTSegmentCategoryWorkLife:
            return @"WORK_LIFE";
        default:
            return nil;
    }
}

- (nullable NSString*)convertSegmentSubcategoryToString:(SENTSegmentSubcategory)subcategory {
    switch (subcategory) {
        case SENTSegmentSubcategoryCommute:
            return @"COMMUTE";
        case SENTSegmentSubcategoryDriving:
            return @"DRIVING";
        case SENTSegmentSubcategoryEntertainment:
            return @"ENTERTAINMENT";
        case SENTSegmentSubcategoryFamily:
            return @"FAMILY";
        case SENTSegmentSubcategoryHome:
            return @"HOME";
        case SENTSegmentSubcategoryShopping:
            return @"SHOPPING";
        case SENTSegmentSubcategorySocial:
            return @"SOCIAL";
        case SENTSegmentSubcategoryTransport:
            return @"TRANSPORT";
        case SENTSegmentSubcategoryTravel:
            return @"TRAVEL";
        case SENTSegmentSubcategoryWellbeing:
            return @"WELLBEING";
        case SENTSegmentSubcategoryWiningAndDining:
            return @"WINING_AND_DINING";
        case SENTSegmentSubcategoryWork:
            return @"WORK";
        default:
            return nil;
    }
}

- (nullable NSString*)convertSegmentTypeToString:(SENTSegmentType)type {
    switch (type) {
        case SENTSegmentTypeAggressiveDriver:
            return @"AGGRESSIVE_DRIVER";
        case SENTSegmentTypeAnticipativeDriver:
            return @"ANTICIPATIVE_DRIVER";
        case SENTSegmentTypeBarGoer:
            return @"BAR_GOER";
        case SENTSegmentTypeCityDriver:
            return @"CITY_DRIVER";
        case SENTSegmentTypeCityHome:
            return @"CITY_HOME";
        case SENTSegmentTypeCityWorker:
            return @"CITY_WORKER";
        case SENTSegmentTypeCultureBuff:
            return @"CULTURE_BUFF";
        case SENTSegmentTypeDieHardDriver:
            return @"DIE_HARD_DRIVER";
        case SENTSegmentTypeDistractedDriver:
            return @"DISTRACTED_DRIVER";
        case SENTSegmentTypeDogWalker:
            return @"DOG_WALKER";
        case SENTSegmentTypeEarlyBird:
            return @"EARLY_BIRD";
        case SENTSegmentTypeEasyCommuter:
            return @"EASY_COMMUTER";
        case SENTSegmentTypeEfficientDriver:
            return @"EFFICIENT_DRIVER";
        case SENTSegmentTypeFoodie:
            return @"FOODIE";
        case SENTSegmentTypeFrequentFlyer:
            return @"FREQUENT_FLYER";
        case SENTSegmentTypeFulltimeWorker:
            return @"FULLTIME_WORKER";
        case SENTSegmentTypeGreenCommuter:
            return @"GREEN_COMMUTER";
        case SENTSegmentTypeHealthyBiker:
            return @"HEALTHY_BIKER";
        case SENTSegmentTypeHealthyWalker:
            return @"HEALTHY_WALKER";
        case SENTSegmentTypeHeavyCommuter:
            return @"HEAVY_COMMUTER";
        case SENTSegmentTypeHomeBound:
            return @"HOME_BOUND";
        case SENTSegmentTypeHomebody:
            return @"HOMEBODY";
        case SENTSegmentTypeHomeworker:
            return @"HOMEWORKER";
        case SENTSegmentTypeIllegalDriver:
            return @"ILLEGAL_DRIVER";
        case SENTSegmentTypeLateWorker:
            return @"LATE_WORKER";
        case SENTSegmentTypeLegalDriver:
            return @"LEGAL_DRIVER";
        case SENTSegmentTypeLongCommuter:
            return @"LONG_COMMUTER";
        case SENTSegmentTypeMobility:
            return @"MOBILITY";
        case SENTSegmentTypeMobilityHigh:
            return @"MOBILITY__HIGH";
        case SENTSegmentTypeMobilityLimited:
            return @"MOBILITY__LIMITED";
        case SENTSegmentTypeMobilityModerate:
            return @"MOBILITY__MODERATE";
        case SENTSegmentTypeMotorwayDriver:
            return @"MOTORWAY_DRIVER";
        case SENTSegmentTypeMusicLover:
            return @"MUSIC_LOVER";
        case SENTSegmentTypeNatureLover:
            return @"NATURE_LOVER";
        case SENTSegmentTypeNightOwl:
            return @"NIGHT_OWL";
        case SENTSegmentTypeNightworker:
            return @"NIGHTWORKER";
        case SENTSegmentTypeNormalCommuter:
            return @"NORMAL_COMMUTER";
        case SENTSegmentTypeParttimeWorker:
            return @"PARTTIME_WORKER";
        case SENTSegmentTypePetOwner:
            return @"PET_OWNER";
        case SENTSegmentTypePhysicalActivityHigh:
            return @"PHYSICAL_ACTIVITY__HIGH";
        case SENTSegmentTypePhysicalActivityLimited:
            return @"PHYSICAL_ACTIVITY__LIMITED";
        case SENTSegmentTypePhysicalActivityModerate:
            return @"PHYSICAL_ACTIVITY__MODERATE";
        case SENTSegmentTypePublicTransportsCommuter:
            return @"PUBLIC_TRANSPORTS_COMMUTER";
        case SENTSegmentTypePublicTransportsUser:
            return @"PUBLIC_TRANSPORTS_USER";
        case SENTSegmentTypeRecentlyChangedJob:
            return @"RECENTLY_CHANGED_JOB";
        case SENTSegmentTypeRecentlyMovedHome:
            return @"RECENTLY_MOVED_HOME";
        case SENTSegmentTypeRestoLover:
            return @"RESTO_LOVER";
        case SENTSegmentTypeRuralHome:
            return @"RURAL_HOME";
        case SENTSegmentTypeRuralWorker:
            return @"RURAL_WORKER";
        case SENTSegmentTypeShopaholic:
            return @"SHOPAHOLIC";
        case SENTSegmentTypeShortCommuter:
            return @"SHORT_COMMUTER";
        case SENTSegmentTypeSleepDeprived:
            return @"SLEEP_DEPRIVED";
        case SENTSegmentTypeSocialActivity:
            return @"SOCIAL_ACTIVITY";
        case SENTSegmentTypeSocialActivityHigh:
            return @"SOCIAL_ACTIVITY__HIGH";
        case SENTSegmentTypeSocialActivityLimited:
            return @"SOCIAL_ACTIVITY__LIMITED";
        case SENTSegmentTypeSocialActivityModerate:
            return @"SOCIAL_ACTIVITY__MODERATE";
        case SENTSegmentTypeSportive:
            return @"SPORTIVE";
        case SENTSegmentTypeStudent:
            return @"STUDENT";
        case SENTSegmentTypeTownHome:
            return @"TOWN_HOME";
        case SENTSegmentTypeTownWorker:
            return @"TOWN_WORKER";
        case SENTSegmentTypeUberParent:
            return @"UBER_PARENT";
        case SENTSegmentTypeWorkLifeBalance:
            return @"WORK_LIFE_BALANCE";
        case SENTSegmentTypeWorkTraveller:
            return @"WORK_TRAVELLER";
        case SENTSegmentTypeWorkaholic:
            return @"WORKAHOLIC";
        default:
            return nil;
    }
}

- (NSArray*)convertSegmentAttributes:(NSArray<SENTAttribute *>*)attributes {
    NSMutableArray *attributeArray = [[NSMutableArray alloc] init];

    for (SENTAttribute* attribute in attributes) {
        NSMutableDictionary *attributeDict = [[NSMutableDictionary alloc] init];
        attributeDict[@"name"] = attribute.name;
        attributeDict[@"value"] = @(attribute.value);
        [attributeArray addObject:attributeDict];
    }

    return attributeArray;
}

- (nullable NSDictionary*)convertSegment:(SENTSegment*)segment {
    NSString* category = [self convertSegmentCategoryToString:segment.category];
    NSString* subcategory = [self convertSegmentSubcategoryToString:segment.subcategory];
    NSString* type = [self convertSegmentTypeToString:segment.type];

    if (category == nil || subcategory == nil || type == nil) {
        return nil;
    }

    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[@"category"] = category;
    dict[@"subcategory"] = subcategory;
    dict[@"type"] = type;
    dict[@"id"] = @(segment.uniqueId);
    dict[@"startTime"] = [segment.startDate description];
    dict[@"startTimeEpoch"] = @((long) (segment.startDate.timeIntervalSince1970 * 1000));

    if (segment.endDate != nil) {
        dict[@"endTime"] = [segment.endDate description];
        dict[@"endTimeEpoch"] = @((long) (segment.endDate.timeIntervalSince1970 * 1000));
    }
    dict[@"attributes"] = [self convertSegmentAttributes:segment.attributes];

    return dict;
}

- (NSDictionary*)convertUserContextToDict:(SENTUserContext*)userContext {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    // Events
    NSMutableArray *events = [[NSMutableArray alloc] init];
    for (SENTTimelineEvent* event in userContext.events) {
        [events addObject:[self convertEvent:event]];
    }
    dict[@"events"] = events;

    // Segments
    NSMutableArray *segments = [[NSMutableArray alloc] init];
    for (SENTSegment* segment in userContext.activeSegments) {
        [segments addObject:[self convertSegment:segment]];
    }
    dict[@"activeSegments"] = segments;

    // Last know location
    if (userContext.lastKnownLocation != nil) {
        dict[@"lastKnownLocation"] = [self convertGeolocation:userContext.lastKnownLocation];
    }

    // Home
    if (userContext.home != nil) {
        dict[@"home"] = [self convertVenue:userContext.home];
    }

    // Home
    if (userContext.work != nil) {
        dict[@"work"] = [self convertVenue:userContext.work];
    }

    dict[@"semanticTime"] = [self convertSemanticTime:userContext.semanticTime];

    return [dict copy];
}

- (NSMutableArray*)convertUserContextCriteriaToArray:(SENTUserContextUpdateCriteria)criteriaMask {
    NSMutableArray* criteria = [[NSMutableArray alloc] init];

    if (criteriaMask & SENTUserContextUpdateCriteriaCurrentEvent) {
        [criteria addObject:@"CURRENT_EVENT"];
    }

    if (criteriaMask & SENTUserContextUpdateCriteriaVisitedVenues) {
        [criteria addObject:@"VISITED_VENUES"];
    }

    if (criteriaMask & SENTUserContextUpdateCriteriaActiveSegments) {
        [criteria addObject:@"ACTIVE_SEGMENTS"];
    }

    return criteria;
}

- (NSDictionary*)convertUserActivityToDict:(SENTUserActivity*)userActivity {
    if(userActivity == nil) {
        return @{};
    }

    //SENTUserActivity
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    //SENTUserActivityType
    NSString *userActivityType = [self convertUserActivityTypeToString:userActivity.type];
    [dict setObject:userActivityType forKey:@"type"];


    //SENTTripInfo
    if(userActivity.type == SENTUserActivityTypeTRIP ) {
        NSMutableDictionary *tripInfoDict = [[NSMutableDictionary alloc] init];
        NSString *tripInfo = [self convertTripTypeToString:userActivity.tripInfo.type];

        if(tripInfo.length > 0) {
            [tripInfoDict setObject:tripInfo forKey:@"type"];
        }

        if(tripInfoDict.allKeys.count > 0) {
            [dict setObject:tripInfoDict forKey:@"tripInfo"];
        }
    }

    //SENTStationaryInfo
    if(userActivity.type == SENTUserActivityTypeSTATIONARY) {
        NSMutableDictionary *stationaryInfoDict = [[NSMutableDictionary alloc] init];

        if(userActivity.stationaryInfo.location) {
            NSDictionary *location = @{
                                       @"latitude": @(userActivity.stationaryInfo.location.coordinate.latitude),
                                       @"longitude": @(userActivity.stationaryInfo.location.coordinate.longitude)
                                       };
            [stationaryInfoDict setObject:location forKey:@"location"];
        }

        if(stationaryInfoDict.allKeys.count > 0) {
            [dict setObject:stationaryInfoDict forKey:@"stationaryInfo"];
        }

    }

    return [dict copy];

}

- (NSDictionary*)convertSdkStatusToDict:(SENTSDKStatus*) status {
    if (status == nil) {
        return @{};
    }

    NSDictionary *dict = @{
                           @"startStatus":[self convertStartStatusToString:status.startStatus],
                           @"detectionStatus":[self convertDetectionStatusToString:status.detectionStatus],
                           @"canDetect":@(status.canDetect),
                           @"isRemoteEnabled":@(status.isRemoteEnabled),
                           @"locationPermission":[self convertLocationPermissionToString:status.locationPermission],
                           @"isPreciseLocationAuthorizationGranted":@(status.isPreciseLocationAuthorizationGranted),
                           @"isLocationAvailable":@(status.isLocationAvailable),
                           @"isAccelPresent":@(status.isAccelPresent),
                           @"isGyroPresent":@(status.isGyroPresent),
                           @"isGpsPresent":@(status.isGpsPresent),
                           @"wifiQuotaStatus":[self convertQuotaStatusToString:status.wifiQuotaStatus],
                           @"mobileQuotaStatus":[self convertQuotaStatusToString:status.mobileQuotaStatus],
                           @"diskQuotaStatus":[self convertQuotaStatusToString:status.diskQuotaStatus],
                           @"userExists":@(status.userExists),
                           @"isBatterySavingEnabled":@(status.isDeviceLowPowerModeEnabled),
                           @"isActivityRecognitionPermGranted":@(status.isMotionActivityPermissionGranted),
                           @"backgroundRefreshStatus":[self convertBackgroundRefreshStatus:status.backgroundRefreshStatus]
                           };

    return dict;
}

- (NSDictionary*)convertInstallIdToDict:(NSString*) installId {
    return @{ @"installId":installId };
}


- (NSDictionary*)convertTokenToDict:(NSString*) token {
    if (token.length == 0) {
        return @{};
    }
    return @{ @"tokenId":token };
}

- (NSString*)convertInitIssueToString:(SENTInitIssue) issue {
    if (issue == SENTInitIssueInvalidCredentials) {
        return @"INVALID_CREDENTIALS";
    } else if (issue == SENTInitIssueChangedCredentials) {
        return @"CHANGED_CREDENTIALS";
    } else if (issue == SENTInitIssueServiceUnreachable) {
        return @"SERVICE_UNREACHABLE";
    } else if (issue == SENTInitIssueLinkFailed) {
        return @"LINK_FAILED";
    } else if (issue == SENTInitIssueResetInProgress) {
        return @"SDK_RESET_IN_PROGRESS";
    } else
        return @"INITIALIZATION_ERROR";
}

- (NSString*)convertQuotaStatusToString:(SENTQuotaStatus) status {
    switch (status) {
        case SENTQuotaStatusOK:
            return @"OK";
        case SENTQuotaStatusWarning:
            return @"WARNING";
        case SENTQuotaStatusExceeded:
            return @"EXCEEDED";
        default:
            return @"UNRECOGNIZED_STATUS";
    }
}

- (NSString*)convertLocationPermissionToString:(SENTLocationPermission) status {
    switch (status) {
        case SENTLocationPermissionAlways:
            return @"ALWAYS";
        case SENTLocationPermissionWhileInUse:
            return @"ONLY_WHILE_IN_USE";
        case SENTLocationPermissionNever:
        default:
            return @"NEVER";
    }
}

- (NSString*)convertInitStateToString:(SENTSDKInitState) state {
    switch (state) {
        case SENTSDKInitStateNotInitialized:
            return @"NOT_INITIALIZED";
        case SENTSDKInitStateInProgress:
            return @"INIT_IN_PROGRESS";
        case SENTSDKInitStateInitialized:
            return @"INITIALIZED";
        case SENTSDKInitStateResetting:
            return @"RESETTING";
        default:
            return @"UNRECOGNIZED_STATE";
    }
}

- (NSString*)convertUserActivityTypeToString:(SENTUserActivityType) activityType {
    switch (activityType) {
        case SENTUserActivityTypeTRIP:
            return @"USER_ACTIVITY_TYPE_TRIP";
        case SENTUserActivityTypeSTATIONARY:
            return @"USER_ACTIVITY_TYPE_STATIONARY";
        case SENTUserActivityTypeUNKNOWN:
            return @"USER_ACTIVITY_TYPE_UNKNOWN";
        default:
            return @"USER_ACTIVITY_TYPE_UNRECOGNIZED";
    }
}

- (NSString*)convertTripTypeToString:(SENTTripType) tripType {
    switch (tripType) {
        case SENTTripTypeSDK:
            return @"TRIP_TYPE_SDK";
        case SENTTripTypeExternal:
            return @"TRIP_TYPE_EXTERNAL";
        default:
            return @"TRIP_TYPE_UNRECOGNIZED";
    }
}

- (NSDictionary*)convertVehicleCrashEventToDict:(SENTVehicleCrashEvent*) crashEvent {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    double time = [crashEvent.date timeIntervalSince1970] * 1000;
    dict[@"time"] = @(time);


    if(crashEvent.location != nil) {
        NSDictionary *location = @{
                                   @"latitude": @(crashEvent.location.coordinate.latitude),
                                   @"longitude": @(crashEvent.location.coordinate.longitude)
                                   };
        dict[@"location"] = location;
    }

    dict[@"magnitude"] = @(crashEvent.magnitude);
    dict[@"speedAtImpact"] = @(crashEvent.speedAtImpact);
    dict[@"deltaV"] = @(crashEvent.deltaV);
    dict[@"confidence"] = @(crashEvent.confidence);

    NSMutableArray *precedingLocations = [[NSMutableArray alloc] init];
    for (CLLocation* location in crashEvent.precedingLocations) {
        [precedingLocations addObject:[self convertCllocation:location]];
    }
    dict[@"precedingLocations"] = precedingLocations;

    return [dict copy];
}

- (NSString *)_vehicleCrashDiagnosticStateName:(SENTVehicleCrashDetectionState)crashDetectionState {
        switch (crashDetectionState) {
        case 0:
            return @"CANDIDATE_DETECTED";
        case 1:
            return @"CANDIDATE_DISCARDED_WEAK_IMPACT";
        case 2:
            return @"CANDIDATE_DISCARDED_NON_VEHICLE_TRANSPORT_MODE";
        case 3:
            return @"CANDIDATE_DISCARDED_PRE_IMPACT_NOISE";
        case 4:
            return @"CANDIDATE_DISCARDED_LOW_SPEED_BEFORE_IMPACT";
        case 5:
            return @"CANDIDATE_DISCARDED_POST_IMPACT_NOISE";
        case 6:
            return @"CANDIDATE_DISCARDED_HIGH_SPEED_AFTER_IMPACT";
        default:
            return @"CANDIDATE_NOT_DETECTED";
    }
}

- (NSDictionary*)convertVehicleCrashDiagnosticToDict:(SENTVehicleCrashDiagnostic*) crashDiagnostic {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    dict[@"crashDetectionState"] = [self _vehicleCrashDiagnosticStateName: crashDiagnostic.crashDetectionState];
    dict[@"crashDetectionStateDescription"] = crashDiagnostic.crashDetectionStateDescription;
    return [dict copy];
}

- (NSDictionary *)convertUserCreationResult:(SENTUserCreationResult *)userCreationResult {
    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];

    userInfo[@"userId"] = userCreationResult.userInfo.userId;
    userInfo[@"tokenId"] = userCreationResult.userInfo.token.tokenId;

    NSString *tokenExpiryDate = [[SENTDate alloc]initWithNSDate: userCreationResult.userInfo.token.expiryDate].description;
    userInfo[@"tokenExpiryDate"] = tokenExpiryDate;
    userInfo[@"isTokenExpired"] = @(userCreationResult.userInfo.token.isExpired);

    NSMutableDictionary *userCreationResultDict = [[NSMutableDictionary alloc] init];
    userCreationResultDict[@"userInfo"] = userInfo;
    return userCreationResultDict;
}

- (NSString *)stringifyUserCreationError:(SENTUserCreationError *)userCreationError {
    NSString *reason;
    switch (userCreationError.failureReason) {
        case SENTUserCreationFailureReasonSdkResetInProgress:
            reason = @"SDK_RESET_IN_PROGRESS";
            break;
        case SENTUserCreationFailureReasonUserCreationInProgress:
            reason = @"USER_CREATION_IN_PROGRESS";
        case SENTUserCreationFailureReasonUserAlreadyExists:
            reason = @"USER_ALREADY_EXISTS";
            break;
        case SENTUserCreationFailureReasonNetworkError:
            reason = @"NETWORK_ERROR";
            break;
        case SENTUserCreationFailureReasonServerError:
            reason = @"SERVER_ERROR";
            break;
        case SENTUserCreationFailureReasonUnexpectedError:
            reason = @"UNEXPECTED_ERROR";
            break;
        case SENTUserCreationFailureReasonAppSideLinkingFailed:
            reason = @"APP_SIDE_LINKING_FAILED";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, userCreationError.details];
}

- (NSString*)convertStartStatusToString:(SENTStartStatus) status {
    switch (status) {
        case SENTStartStatusNotStarted:
            return @"NOT_STARTED";
        case SENTStartStatusPending:
            return @"PENDING";
        case SENTStartStatusStarted:
            return @"STARTED";
        case SENTStartStatusExpired:
            return @"EXPIRED";
        default:
            return @"UNRECOGNIZED_STATUS";
    }
}

- (NSString*)convertDetectionStatusToString:(SENTDetectionStatus) detectionStatus {
    switch (detectionStatus) {

        case SENTDetectionStatusDisabled:
            return @"DISABLED";
        case SENTDetectionStatusExpired:
            return @"EXPIRED";
        case SENTDetectionStatusEnabledButBlocked:
            return @"ENABLED_BUT_BLOCKED";
        case SENTDetectionStatusEnabledAndDetecting:
            return @"ENABLED_AND_DETECTING";
    }
}

- (NSDictionary *)convertUserLinkingResult:(SENTUserLinkingResult *)userLinkingResult {
    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];

    userInfo[@"userId"] = userLinkingResult.userInfo.userId;
    userInfo[@"tokenId"] = userLinkingResult.userInfo.token.tokenId;

    NSString *tokenExpiryDate = [[SENTDate alloc]initWithNSDate: userLinkingResult.userInfo.token.expiryDate].description;
    userInfo[@"tokenExpiryDate"] = tokenExpiryDate;
    userInfo[@"isTokenExpired"] = @(userLinkingResult.userInfo.token.isExpired);

    NSMutableDictionary *userLinkingResultDict = [[NSMutableDictionary alloc] init];
    userLinkingResultDict[@"userInfo"] = userInfo;
    return userLinkingResultDict;
}

- (NSString *)stringifyUserLinkingError:(SENTUserLinkingError *)userLinkingError {
    NSString *reason;
    switch (userLinkingError.failureReason) {
        case SENTUserLinkingFailureReasonNoUser:
            reason = @"NO_USER";
            break;
        case SENTUserLinkingFailureReasonUserAlreadyLinked:
            reason = @"USER_ALREADY_LINKED";
            break;
        case SENTUserLinkingFailureReasonNetworkError:
            reason = @"NETWORK_ERROR";
            break;
        case SENTUserLinkingFailureReasonServerError:
            reason = @"SERVER_ERROR";
            break;
        case SENTUserLinkingFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            break;
        case SENTUserLinkingFailureReasonUnexpectedError:
            reason = @"UNEXPECTED_ERROR";
            break;
        case SENTUserLinkingFailureReasonAppSideLinkingFailed:
            reason = @"APP_SIDE_LINKING_FAILED";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, userLinkingError.details];
}

- (NSDictionary *)convertResetResult:(SENTResetResult *)resetResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    dict[@"initState"] = [self convertInitStateToString:resetResult.initState];

    return dict;
}

- (NSString *)stringifyResetError:(SENTResetError *)resetError {
    NSString *reason;
    NSString *details;
    switch (resetError.failureReason) {
        case SENTResetFailureReasonInitInProgress:
            reason = @"SDK_INIT_IN_PROGRESS";
            details = @"SDK initialization is in progress. Resetting at this time is not allowed.";
            break;
        case SENTResetFailureReasonResetting:
            reason = @"SDK_RESET_IN_PROGRESS";
            details = @"SDK initialization is in progress. Resetting at this time is not allowed.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSDictionary *)convertStartTripResult:(SENTStartTripResult *)startTripResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    // empty object

    return dict;
}

- (NSString *)stringifyStartTripError:(SENTStartTripError *)startTripError {
    NSString *reason;
    NSString *details;
    switch (startTripError.failureReason) {
        case SENTStartTripFailureReasonNoUser:
            reason = @"NO_USER";
            details = @"No Sentiance user present on the device.";
            break;
        case SENTStartTripFailureReasonDetectionsDisabled:
            reason = @"DETECTIONS_DISABLED";
            details = @"Detections are disabled. Enable them first before starting a trip.";
            break;
        case SENTStartTripFailureReasonDetectionsBlocked:
            reason = @"DETECTIONS_BLOCKED";
            details = @"Detections are enabled but not running. Check the SDK's status to find out why.";
            break;
        case SENTStartTripFailureReasonTripAlreadyStarted:
            reason = @"TRIP_ALREADY_STARTED";
            details = @"An external trip is already started. To start a new trip, call `stopTrip()` first.";
            break;
        case SENTStartTripFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            details = @"The user is disabled remotely.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSDictionary *)convertStopTripResult:(SENTStopTripResult *)stopTripResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    // empty object

    return dict;
}

- (NSString *)stringifyStopTripError:(SENTStopTripError *)stopTripError {
    NSString *reason;
    NSString *details;
    switch (stopTripError.failureReason) {
        case SENTStopTripFailureReasonNoOngoingTrip:
            reason = @"NO_ONGOING_TRIP";
            details = @"There is no ongoing external trip.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];

}

- (NSDictionary *)convertRequestUserAccessTokenResult:(SENTUserAccessTokenResult *)userAccessTokenResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    dict[@"tokenId"] = userAccessTokenResult.token.tokenId;

    NSString *tokenExpiryDate = [[SENTDate alloc]initWithNSDate: userAccessTokenResult.token.expiryDate].description;
    dict[@"expiryDate"] = tokenExpiryDate;

    return dict;
}

- (NSString *)stringifyRequestUserAccessTokenError:(SENTUserAccessTokenError *)userAccessTokenError {
    NSString *reason;
    NSString *details;
    switch (userAccessTokenError.failureReason) {
        case SENTUserAccessTokenFailureReasonNoUser:
            reason = @"NO_USER";
            details = @"No Sentiance user present on the device.";
            break;
        case SENTUserAccessTokenFailureReasonNetworkError:
            reason = @"NETWORK_ERROR";
            details = @"A network error occurred. This can happen when the existing token is expired, and it was not possible to contact the Sentiance Platform to refresh it.";
            break;
        case SENTUserAccessTokenFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            details = @"The user is disabled remotely.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSDictionary *)convertSubmitDetectionsResult:(SENTSubmitDetectionsResult *)submitDetectionsResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    // empty object

    return dict;
}

- (NSString *)stringifySubmitDetectionsError:(SENTSubmitDetectionsError *)submitDetectionsError {
    NSString *reason;
    NSString *details;
    switch (submitDetectionsError.failureReason) {

        case SENTSubmitDetectionsFailureReasonNoUser:
            reason = @"NO_USER";
            details = @"No Sentiance user present on the device.";
            break;
        case SENTSubmitDetectionsFailureReasonNetworkError:
            reason = @"NETWORK_ERROR";
            details = @"A network error occurred.";
            break;
        case SENTSubmitDetectionsFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            details = @"The user is disabled remotely.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSDictionary *)convertEnableDetectionsResult:(SENTEnableDetectionsResult *)enableDetectionsResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    dict[@"sdkStatus"] = [self convertSdkStatusToDict:enableDetectionsResult.sdkStatus];
    dict[@"detectionStatus"] = [self convertDetectionStatusToString:enableDetectionsResult.detectionStatus];

    return dict;
}

- (NSString *)stringifyEnableDetectionsError:(SENTEnableDetectionsError *)enableDetectionsError {
    NSString *reason;
    NSString *details;
    switch (enableDetectionsError.failureReason) {

        case SENTEnableDetectionsFailureReasonNoUser:
            reason = @"NO_USER";
            details = @"No Sentiance user present on the device.";
            break;
        case SENTEnableDetectionsFailureReasonPastExpiryDate:
            reason = @"PAST_EXPIRY_DATE";
            details = @"Expiry date is in past.";
            break;
        case SENTEnableDetectionsFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            details = @"The user is disabled remotely.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSDictionary *)convertDisableDetectionsResult:(SENTDisableDetectionsResult *)disableDetectionsResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    dict[@"sdkStatus"] = [self convertSdkStatusToDict:disableDetectionsResult.sdkStatus];
    dict[@"detectionStatus"] = [self convertDetectionStatusToString:disableDetectionsResult.detectionStatus];

    return dict;
}
- (NSString *)stringifyDisableDetectionsError:(SENTDisableDetectionsError *)disableDetectionsError {
    // Disable detections always succeed
    return @"";
}

- (NSString *)stringifyUserContextError:(SENTRequestUserContextError *)userContextError {
    NSString *reason;
    NSString *details;

    switch (userContextError.failureReason) {
        case SENTRequestUserContextFailureReasonNoUser:
            reason = @"NO_USER";
            details = @"No Sentiance user present on the device.";
            break;
        case SENTRequestUserContextFailureReasonFeatureNotEnabled:
            reason = @"FEATURE_NOT_ENABLED";
            details = @"Feature not enabled. Contact Sentiance support to enable it.";
            break;
        case SENTRequestUserContextFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            details = @"The user is disabled remotely.";
            break;
    }
    return [NSString stringWithFormat:@"Reason: %@ - %@", reason, details];
}

- (NSString *)convertBackgroundRefreshStatus:(UIBackgroundRefreshStatus)backgroundRefreshStatus {
    if (backgroundRefreshStatus == UIBackgroundRefreshStatusAvailable) {
        return @"AVAILABLE";
    } else if(backgroundRefreshStatus == UIBackgroundRefreshStatusDenied) {
        return @"DENIED";
    } else if(backgroundRefreshStatus == UIBackgroundRefreshStatusRestricted) {
        return @"RESTRICTED";
    }
    return @"";
}

- (NSSet<NSString*> *)convertIntegerTransmittableDataTypes:(NSArray<NSNumber*>*)intDataTypes {
    NSMutableSet *typesSet = [[NSMutableSet alloc] init];
    NSDictionary *dict = @{
      @(SENTTransmittableDataTypeAll): @"ALL",
      @(SENTTransmittableDataTypeSdkInfo): @"SDK_INFO",
      @(SENTTransmittableDataTypeVehicleCrashInfo): @"VEHICLE_CRASH_INFO",
      @(SENTTransmittableDataTypeGeneralDetections): @"GENERAL_DETECTIONS"
    };

    for(NSNumber* intDataType in intDataTypes) {
      [typesSet addObject:dict[intDataType]];
    }
    return [typesSet copy];
}

- (NSSet<NSNumber*> *)convertStringTransmittableDataTypes:(NSArray<NSString*>*)stringDataTypes {
    NSMutableSet *typesSet = [[NSMutableSet alloc] init];
    NSDictionary *dict = @{
      @"ALL": @(SENTTransmittableDataTypeAll),
      @"SDK_INFO": @(SENTTransmittableDataTypeSdkInfo),
      @"VEHICLE_CRASH_INFO": @(SENTTransmittableDataTypeVehicleCrashInfo),
      @"GENERAL_DETECTIONS": @(SENTTransmittableDataTypeGeneralDetections)
    };

    for(NSString * strType in stringDataTypes) {
      [typesSet addObject:dict[strType]];
    }
    return [typesSet copy];
}

- (NSDictionary<NSString *, NSDictionary<NSString *, NSNumber *> *> *)convertDrivingInsights:(SENTDrivingInsights *)drivingInsights {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *transportEventDict = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *safetyScoresDict = [[NSMutableDictionary alloc] init];

    [self _addBaseEventFields:transportEventDict event:drivingInsights.transportEvent];
    [self addTransportEventInfoToDict:transportEventDict event:drivingInsights.transportEvent];
    dict[@"transportEvent"] = transportEventDict;

    NSNumber* smoothScore = drivingInsights.safetyScores.smoothScore;
    if (smoothScore != nil) {
        safetyScoresDict[@"smoothScore"] = smoothScore;
    }

    NSNumber* focusScore = drivingInsights.safetyScores.focusScore;
    if (focusScore != nil) {
        safetyScoresDict[@"focusScore"] = focusScore;
    }

    NSNumber* callWhileMovingScore = drivingInsights.safetyScores.callWhileMovingScore;
    if (callWhileMovingScore != nil) {
        safetyScoresDict[@"callWhileMovingScore"] = callWhileMovingScore;
    }

    NSNumber* legalScore = drivingInsights.safetyScores.legalScore;
    if (legalScore != nil) {
        safetyScoresDict[@"legalScore"] = legalScore;
    }

    NSNumber* overallScore = drivingInsights.safetyScores.overallScore;
    if (overallScore != nil) {
        safetyScoresDict[@"overallScore"] = overallScore;
    }

    dict[@"safetyScores"] = safetyScoresDict;

    return dict;
}

- (NSArray<NSDictionary<NSString *, NSNumber *> *> *)convertHarshDrivingEvents:(NSArray<SENTHarshDrivingEvent*> *)harshDrivingEvents {
    NSMutableArray <NSDictionary<NSString *, NSNumber *> *> *array = [[NSMutableArray alloc] init];
    for (SENTHarshDrivingEvent *event in harshDrivingEvents) {
        [array addObject:[self convertHarshDrivingEvent:event]];
    }
    return array;
}

- (NSDictionary<NSString *, NSNumber *> *)convertHarshDrivingEvent:(SENTHarshDrivingEvent *)harshDrivingEvent {
    NSMutableDictionary<NSString *, NSNumber *> *dict = [self convertDrivingEvent:harshDrivingEvent];
    dict[@"magnitude"] = @(harshDrivingEvent.magnitude);
    return dict;
}

- (NSArray<NSDictionary<NSString *, NSNumber *> *> *)convertPhoneUsageEvents:(NSArray<SENTPhoneUsageEvent*> *)phoneUsageEvents {
    NSMutableArray <NSDictionary<NSString *, NSNumber *> *> *array = [[NSMutableArray alloc] init];
    for (SENTPhoneUsageEvent *event in phoneUsageEvents) {
        [array addObject:[self convertPhoneUsageEvent:event]];
    }
    return array;
}

- (NSDictionary<NSString *, NSNumber *> *)convertPhoneUsageEvent:(SENTPhoneUsageEvent *)phoneUsageEvent {
    return [self convertDrivingEvent:phoneUsageEvent];
}

- (NSArray<NSDictionary<NSString *, NSNumber *> *> *)convertCallWhileMovingEvents:(NSArray<SENTCallWhileMovingEvent*> *)callWhileMovingEvents {
    NSMutableArray <NSDictionary<NSString *, NSNumber *> *> *array = [[NSMutableArray alloc] init];
    for (SENTCallWhileMovingEvent *event in callWhileMovingEvents) {
        [array addObject:[self convertCallWhileMovingEvent:event]];
    }
    return array;
}

- (NSDictionary<NSString *, NSNumber *> *)convertCallWhileMovingEvent:(SENTCallWhileMovingEvent *)callWhileMovingEvent {
    NSMutableDictionary<NSString *, NSNumber *> *dict = [self convertDrivingEvent:callWhileMovingEvent];
    if (callWhileMovingEvent.minTraveledSpeedInMps != nil) {
        dict[@"minTravelledSpeedInMps"] = callWhileMovingEvent.minTraveledSpeedInMps;
    }
    if (callWhileMovingEvent.maxTraveledSpeedInMps != nil) {
        dict[@"maxTravelledSpeedInMps"] = callWhileMovingEvent.maxTraveledSpeedInMps;
    }
    return dict;
}

- (NSArray<NSDictionary<NSString *, NSNumber *> *> *)convertSpeedingEvents:(NSArray<SENTSpeedingEvent*> *)speedingEvents {
    NSMutableArray <NSDictionary<NSString *, NSNumber *> *> *array = [[NSMutableArray alloc] init];
    for (SENTSpeedingEvent *event in speedingEvents) {
        [array addObject:[self convertSpeedingEvent:event]];
    }
    return array;
}

- (NSDictionary<NSString *, NSNumber *> *)convertSpeedingEvent:(SENTSpeedingEvent *)speedingEvent {
    NSMutableDictionary *dict = [self convertDrivingEvent:speedingEvent];
    dict[@"waypoints"] = [self convertWaypointArray:speedingEvent.waypoints];
    return dict;
}

- (NSMutableDictionary<NSString *, NSNumber *> *)convertDrivingEvent:(SENTDrivingEvent *)drivingEvent {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    dict[@"startTime"] = [drivingEvent.startDate description];
    dict[@"startTimeEpoch"] = @((long) (drivingEvent.startDate.timeIntervalSince1970 * 1000));
    dict[@"endTime"] = [drivingEvent.endDate description];
    dict[@"endTimeEpoch"] = @((long) (drivingEvent.endDate.timeIntervalSince1970 * 1000));
    return dict;
}

- (NSError *)convertSmartGeofencesRefreshError:(SENTSmartGeofencesRefreshError *)refreshError {
    NSString *reason;
    NSString *details = refreshError.details == nil ? @"": refreshError.details;

    switch (refreshError.failureReason) {
        case SENTSmartGeofencesRefreshFailureReasonNetworkUsageRestricted:
            reason = @"NETWORK_USAGE_RESTRICTED";
            break;
        case SENTSmartGeofencesRefreshFailureReasonNetworkError:
            reason = @"NETWORK_ERROR";
            break;
        case SENTSmartGeofencesRefreshFailureReasonServerError:
            reason = @"SERVER_ERROR";
            break;
        case SENTSmartGeofencesRefreshFailureReasonTooManyFrequentCalls:
            reason = @"TOO_MANY_FREQUENT_CALLS";
            break;
        case SENTSmartGeofencesRefreshFailureReasonFeatureNotEnabled:
            reason = @"FEATURE_NOT_ENABLED";
            break;
        case SENTSmartGeofencesRefreshFailureReasonNoUser:
            reason = @"NO_USER";
            break;
        case SENTSmartGeofencesRefreshFailureReasonUserDisabledRemotely:
            reason = @"USER_DISABLED_REMOTELY";
            break;
        case SENTSmartGeofencesRefreshFailureReasonUnexpectedError:
        default:
            reason = @"UNEXPECTED_ERROR";
            break;
    }

    NSMutableDictionary *errorInfo = [NSMutableDictionary dictionary];
    errorInfo[@"reason"] = reason;
    errorInfo[@"details"] = details;

    NSError *error = [NSError errorWithDomain:SmartGeofencesErrorDomain
                                         code:0
                                         userInfo:errorInfo];

    return error;
}

- (NSString *)stringifySmartGeofencesDetectionMode:(SENTSmartGeofenceDetectionMode)detectionMode {
    switch (detectionMode) {
        case SENTSmartGeofenceDetectionModeBackground:
            return @"BACKGROUND";
        case SENTSmartGeofenceDetectionModeForeground:
            return @"FOREGROUND";
        case SENTSmartGeofenceDetectionModeDisabled:
            return @"DISABLED";
        case SENTSmartGeofenceDetectionModeFeatureNotEnabled:
        default:
            return @"FEATURE_NOT_ENABLED";
    }
}

- (NSDictionary*)convertSmartGeofenceEvent:(SENTSmartGeofenceEvent*)event {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    dict[@"timestamp"] = @((long) ([event.eventDate timeIntervalSince1970] * 1000));
    dict[@"triggeringLocation"] = [self convertCllocation:event.triggeringLocation];

    NSString *eventType;
    switch(event.eventType) {
        case SENTSmartGeofenceEventTypeEntry:
            eventType = @"ENTRY";
            break;
        case SENTSmartGeofenceEventTypeExit:
            eventType = @"EXIT";
            break;
    }

    dict[@"eventType"] = eventType;
    dict[@"geofences"] = [self convertSmartGeofences:event.geofences];

    return dict;
}

- (NSArray<NSDictionary<NSString *, NSNumber *> *> *)convertSmartGeofences:(NSArray<SENTSmartGeofence*> *)smartGeofences {
    NSMutableArray <NSDictionary*> *array = [[NSMutableArray alloc] init];
    for (SENTSmartGeofence *smartGeofence in smartGeofences) {
        [array addObject:[self convertSmartGeofence:smartGeofence]];
    }
    return array;
}

- (NSDictionary*)convertSmartGeofence:(SENTSmartGeofence*)smartGeofence {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    dict[@"sentianceId"] = smartGeofence.sentianceId;
    dict[@"latitude"] = @(smartGeofence.latitude);
    dict[@"longitude"] = @(smartGeofence.longitude);
    dict[@"radius"] = @(smartGeofence.radius);
    dict[@"externalId"] = smartGeofence.externalId;

    return dict;
}

@end
