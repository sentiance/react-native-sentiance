//
//  RNSentianceConverter.m
//  RNSentiance
//
//  Created by Sebouh Aguehian on 10/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "SentianceCore+Converter.h"

@interface SentianceCore (Private)

- (NSString*)convertTimelineEventTypeToString:(SENTTimelineEventType)type;
- (NSDictionary*)convertGeolocation:(SENTGeolocation*)location;
- (NSString*)convertVenueSignificance:(SENTVenueSignificance)type;
- (NSDictionary*)convertVisit:(SENTVisit*)visit;
- (NSDictionary*)convertVenue:(SENTVenue*)venue;
- (NSDictionary*)convertVenueCandidate:(SENTVenueCandidate*)candidate;
- (void)addStationaryEventInfoToDict:(NSMutableDictionary*)dict event:(SENTStationaryEvent*)event;
- (NSString*)convertTransportModeToString:(SENTTimelineTransportMode) mode;
- (void)addTransportEventInfoToDict:(NSMutableDictionary*)dict event:(SENTTransportEvent*)event;
- (NSMutableDictionary*)convertEvent:(SENTTimelineEvent*)event;
- (nullable NSString*)convertSegmentCategoryToString:(SENTSegmentCategory)category;
- (nullable NSString*)convertSegmentSubcategoryToString:(SENTSegmentSubcategory)subcategory;
- (nullable NSString*)convertSegmentTypeToString:(SENTSegmentType)type;
- (NSMutableDictionary*)convertSegmentAttributesToDict:(NSArray<SENTAttribute *>*)attributes;
- (nullable NSDictionary*)convertSegment:(SENTSegment*)segment;
@end

@implementation SentianceCore (Converter)

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

- (NSString*)VenueSignificance:(SENTVenueSignificance)type {
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

- (NSDictionary*)convertVisit:(SENTVisit*)visit {
    return @{
        @"startTime": [visit.startDate description],
        @"startTimeEpoch": [NSString stringWithFormat:@"%d",visit.startDate.timeIntervalSince1970],
        @"endTime": [visit.endDate description],
        @"endTimeEpoch": [NSString stringWithFormat:@"%d",visit.startDate.timeIntervalSince1970],
        @"durationInSeconds": [NSNumber numberWithInt:(int)visit.durationInSeconds],
    };
}

- (NSDictionary*)convertVenue:(SENTVenue*)venue {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (venue.name != nil) {
        dict[@"name"] = venue.name;
    }

    if (venue.location != nil) {
        dict[@"location"] = [self convertGeolocation:venue.location];
    }

    NSMutableDictionary* labels = [[NSMutableDictionary alloc] init];
    [venue.labels enumerateKeysAndObjectsUsingBlock:
        ^(NSString * _Nonnull key, NSString * _Nonnull obj, BOOL * _Nonnull stop) {
            labels[key] = obj;
    }];
    dict[@"venueLabels"] = labels;

    return dict;
}

- (NSDictionary*)convertVenueCandidate:(SENTVenueCandidate*)candidate {
    NSMutableArray* visits = [[NSMutableArray alloc] init];
    for (SENTVisit* visit in candidate.visits) {
        [visits addObject:[self convertVisit:visit]];
    }

    return @{
        @"venue": [self convertVenue:candidate.venue],
        @"likelihood": @(candidate.likelihood),
        @"visits": visits
    };
}

- (void)addStationaryEventInfoToDict:(NSMutableDictionary*)dict event:(SENTStationaryEvent*)event {
    if (event.location != nil) {
        dict[@"location"] = [self convertGeolocation:event.location];
    }

    dict[@"venueSignificance"] = [self convertVenueSignificance:event.venueSignificance];

    NSMutableArray* venueCandidates = [[NSMutableArray alloc] init];
    for (SENTVenueCandidate* candidate in event.venueCandidates) {
        [venueCandidates addObject:[self convertVenueCandidate:candidate]];
    }
    dict[@"venueCandidates"] = venueCandidates;
}

- (NSString*)convertTransportModeToString:(SENTTimelineTransportMode)mode {
    switch (mode) {
        case SENTTimelineTransportModeWalking:
            return @"WALKING";
        case SENTTimelineTransportModeRunning:
            return @"RUNNING";
        case SENTTimelineTransportModeBicycle:
            return @"BYCICLE";
        case SENTTimelineTransportModeVehicle:
            return @"VEHICLE";
        case SENTTimelineTransportModeRail:
            return @"RAIL";
        case SENTTimelineTransportModeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (void)addTransportEventInfoToDict:(NSMutableDictionary*)dict event:(SENTTransportEvent*)event {
    dict[@"transportMode"] = [self convertTransportModeToString:event.transportMode];
}

- (NSMutableDictionary*)convertEvent:(SENTTimelineEvent*)event {
    NSMutableDictionary *eventDict = [[NSMutableDictionary alloc] init];
    eventDict[@"startTime"] = [event.startDate description];
    if (event.endDate != nil) {
        eventDict[@"endTime"] = [event.endDate description];

        NSInteger durationInSeconds = event.durationInSeconds;
        if (durationInSeconds != SENTDurationUnknown) {
            eventDict[@"durationInSeconds"] = [NSNumber numberWithInt:(int)durationInSeconds];
        }
    }

    eventDict[@"type"] = [self convertTimelineEventTypeToString:event.type];

    if (event.type == SENTTimelineEventTypeStationary) {
        [self addStationaryEventInfoToDict:eventDict event:(SENTStationaryEvent*)event];
    }
    else if (event.type == SENTTimelineEventTypeInTransport) {
        [self addTransportEventInfoToDict:eventDict event:(SENTTransportEvent*)event];
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
        case SENTSegmentTypeBeautyQueen:
            return @"BEAUTY_QUEEN";
        case SENTSegmentTypeBrandLoyalBar:
            return @"BRAND_LOYAL__BAR";
        case SENTSegmentTypeBrandLoyalCafe:
            return @"BRAND_LOYAL__CAFE";
        case SENTSegmentTypeBrandLoyalRestaurant:
            return @"BRAND_LOYAL__RESTAURANT";
        case SENTSegmentTypeBrandLoyalRetail:
            return @"BRAND_LOYAL__RETAIL";
        case SENTSegmentTypeBrandLoyalty:
            return @"BRAND_LOYALTY";
        case SENTSegmentTypeBrandLoyaltyGasStations:
            return @"BRAND_LOYALTY__GAS_STATIONS";
        case SENTSegmentTypeBrandLoyaltyRestaurantBar:
            return @"BRAND_LOYALTY__RESTAURANT_BAR";
        case SENTSegmentTypeBrandLoyaltySupermarket:
            return @"BRAND_LOYALTY__SUPERMARKET";
        case SENTSegmentTypeCityDriver:
            return @"CITY_DRIVER";
        case SENTSegmentTypeCityHome:
            return @"CITY_HOME";
        case SENTSegmentTypeCityWorker:
            return @"CITY_WORKER";
        case SENTSegmentTypeClubber:
            return @"CLUBBER";
        case SENTSegmentTypeCultureBuff:
            return @"CULTURE_BUFF";
        case SENTSegmentTypeDieHardDriver:
            return @"DIE_HARD_DRIVER";
        case SENTSegmentTypeDistractedDriver:
            return @"DISTRACTED_DRIVER";
        case SENTSegmentTypeDoItYourselver:
            return @"DO_IT_YOURSELVER";
        case SENTSegmentTypeDogWalker:
            return @"DOG_WALKER";
        case SENTSegmentTypeEarlyBird:
            return @"EARLY_BIRD";
        case SENTSegmentTypeEasyCommuter:
            return @"EASY_COMMUTER";
        case SENTSegmentTypeEfficientDriver:
            return @"EFFICIENT_DRIVER";
        case SENTSegmentTypeFashionista:
            return @"FASHIONISTA";
        case SENTSegmentTypeFoodie:
            return @"FOODIE";
        case SENTSegmentTypeFrequentFlyer:
            return @"FREQUENT_FLYER";
        case SENTSegmentTypeFulltimeWorker:
            return @"FULLTIME_WORKER";
        case SENTSegmentTypeGamer:
            return @"GAMER";
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
        case SENTSegmentTypeRestoLoverAmerican:
            return @"RESTO_LOVER__AMERICAN";
        case SENTSegmentTypeRestoLoverAsian:
            return @"RESTO_LOVER__ASIAN";
        case SENTSegmentTypeRestoLoverBarbecue:
            return @"RESTO_LOVER__BARBECUE";
        case SENTSegmentTypeRestoLoverFastfood:
            return @"RESTO_LOVER__FASTFOOD";
        case SENTSegmentTypeRestoLoverFrench:
            return @"RESTO_LOVER__FRENCH";
        case SENTSegmentTypeRestoLoverGerman:
            return @"RESTO_LOVER__GERMAN";
        case SENTSegmentTypeRestoLoverGreek:
            return @"RESTO_LOVER__GREEK";
        case SENTSegmentTypeRestoLoverGrill:
            return @"RESTO_LOVER__GRILL";
        case SENTSegmentTypeRestoLoverInternational:
            return @"RESTO_LOVER__INTERNATIONAL";
        case SENTSegmentTypeRestoLoverItalian:
            return @"RESTO_LOVER__ITALIAN";
        case SENTSegmentTypeRestoLoverMediterranean:
            return @"RESTO_LOVER__MEDITERRANEAN";
        case SENTSegmentTypeRestoLoverMexican:
            return @"RESTO_LOVER__MEXICAN";
        case SENTSegmentTypeRestoLoverSeafood:
            return @"RESTO_LOVER__SEAFOOD";
        case SENTSegmentTypeRestoLoverSnack:
            return @"RESTO_LOVER__SNACK";
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

- (NSMutableDictionary*)convertSegmentAttributesToDict:(NSArray<SENTAttribute *>*)attributes {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    for (SENTAttribute* attribute in attributes) {
        dict[attribute.name] = @(attribute.value);
    }

    return dict;
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
    dict[@"startTimeEpoch"] = [NSString stringWithFormat:@"%d",segment.startDate.timeIntervalSince1970];

    if (segment.endDate != nil) {
        dict[@"endTime"] = [segment.endDate description];
        dict[@"endTimeEpoch"] = [NSString stringWithFormat:@"%d",segment.endDate.timeIntervalSince1970];
    }
    dict[@"attributes"] = [self convertSegmentAttributesToDict:segment.attributes];

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

    return [dict copy];
}

- (NSMutableArray*)convertUserContextCriteriaToArray:(SENTUserContextUpdateCriteria)criteriaMask {
    NSMutableArray* criteria = [[NSMutableArray alloc] init];

    if (criteriaMask & SENTUserContextUpdateCriteriaCurrentEvent) {
        [criteria addObject:@"CURRENT_EVENT"];
    }

    if (criteriaMask & SENTUserContextUpdateCriteriaActiveMoments) {
        [criteria addObject:@"ACTIVE_MOMENTS"];
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
                           @"isAccelPresent":@(status.isAccelPresent),
                           @"isGyroPresent":@(status.isGyroPresent),
                           @"isGpsPresent":@(status.isGpsPresent),
                           @"wifiQuotaStatus":[self convertQuotaStatusToString:status.wifiQuotaStatus],
                           @"mobileQuotaStatus":[self convertQuotaStatusToString:status.mobileQuotaStatus],
                           @"diskQuotaStatus":[self convertQuotaStatusToString:status.diskQuotaStatus]
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
        case SENTNotInitialized:
            return @"NOT_INITIALIZED";
        case SENTInitInProgress:
            return @"INIT_IN_PROGRESS";
        case SENTInitialized:
            return @"INITIALIZED";
        case SENTResetting:
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
    return [dict copy];
}

- (NSDictionary *)convertUserCreationResult:(SENTUserCreationResult *)userCreationResult {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    dict[@"userId"] = userCreationResult.userInfo.installId;
    dict[@"tokenId"] = userCreationResult.userInfo.token.tokenId;
    dict[@"tokenExpiryDate"] = userCreationResult.userInfo.token.expiryDate;
    dict[@"isTokenExpired"] = @(NO);

    return dict;
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
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];

    dict[@"userId"] = userLinkingResult.userInfo.installId;
    dict[@"tokenId"] = userLinkingResult.userInfo.token.tokenId;
    dict[@"tokenExpiryDate"] = userLinkingResult.userInfo.token.expiryDate;
    dict[@"isTokenExpired"] = @(NO);

    return dict;
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
    dict[@"expiryDate"] = userAccessTokenResult.token.expiryDate;

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

@end
