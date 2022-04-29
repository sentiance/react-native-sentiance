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
- (NSString*)convertVenueType:(SENTStationaryVenueType)type;
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

- (NSString*)convertVenueType:(SENTStationaryVenueType)type {
    switch (type) {
        case SENTStationaryVenueTypeHome:
            return @"HOME";
        case SENTStationaryVenueTypeWork:
            return @"WORK";
        case SENTStationaryVenueTypePointOfInterest:
            return @"POINT_OF_INTEREST";
        case SENTStationaryVenueTypeUnknown:
        default:
            return @"UNKNOWN";
    }
}

- (NSDictionary*)convertVisit:(SENTVisit*)visit {
    return @{
        @"startTime": [visit.startDate description],
        @"endTime": [visit.endDate description],
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
    
    dict[@"venueType"] = [self convertVenueType:event.venueType];
    
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
    
    return @{
        @"category": category,
        @"subcategory": subcategory,
        @"type": type,
        @"id": @(segment.uniqueId),
        @"startTime": [segment.startDate description],
        @"attributes": [self convertSegmentAttributesToDict:segment.attributes],
    };
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

@end
