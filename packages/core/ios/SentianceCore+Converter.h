//
//  RNSentiance+Converter.h
//  RNSentiance
//
//  Created by Sebouh Aguehian on 10/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//
#import "SentianceCore.h"

@interface SentianceCore (Converter)

- (NSDictionary*) convertUserContextToDict:(SENTUserContext*) userContext;
- (NSMutableArray*) convertUserContextCriteriaToArray:(SENTUserContextUpdateCriteria)criteriaMask;
- (NSDictionary*) convertUserCreationResult:(SENTUserCreationResult*) userCreationResult;
- (NSString*) stringifyUserCreationError:(SENTUserCreationError*) userCreationError;
- (NSString*)convertStartStatusToString:(SENTStartStatus) status;
- (NSString*)convertDetectionStatusToString:(SENTDetectionStatus) detectionStatus;
- (NSDictionary*)convertUserActivityToDict:(SENTUserActivity*)userActivity;
- (NSDictionary*)convertSdkStatusToDict:(SENTSDKStatus*) status;
- (NSDictionary*)convertInstallIdToDict:(NSString*) installId;
- (NSDictionary*)convertTokenToDict:(NSString*) token;
- (NSString*)convertInitIssueToString:(SENTInitIssue) issue;
- (NSString*)convertQuotaStatusToString:(SENTQuotaStatus) status;
- (NSString*)convertLocationPermissionToString:(SENTLocationPermission) status;
- (NSString*)convertInitStateToString:(SENTSDKInitState) state;
- (NSString*)convertUserActivityTypeToString:(SENTUserActivityType) activityType;
- (NSString*)convertTripTypeToString:(SENTTripType) tripType;
- (NSDictionary*)convertVehicleCrashEventToDict:(SENTVehicleCrashEvent*) crashEvent;
- (NSDictionary *)convertUserLinkingResult:(SENTUserLinkingResult *)userLinkingResult;
- (NSString *)stringifyUserLinkingError:(SENTUserLinkingError *)userLinkingError;
- (NSDictionary *)convertResetResult:(SENTResetResult *)resetResult;
- (NSString *)stringifyResetError:(SENTResetError *)resetError;
- (NSDictionary *)convertStartTripResult:(SENTStartTripResult *)startTripResult;
- (NSString *)stringifyStartTripError:(SENTStartTripError *)startTripError;
- (NSDictionary *)convertStopTripResult:(SENTStopTripResult *)stopTripResult;
- (NSString *)stringifyStopTripError:(SENTStopTripError *)stopTripError;
- (NSDictionary *)convertRequestUserAccessTokenResult:(SENTUserAccessTokenResult *)userAccessTokenResult;
- (NSString *)stringifyRequestUserAccessTokenError:(SENTUserAccessTokenError *)userAccessTokenError;
- (NSDictionary *)convertSubmitDetectionsResult:(SENTSubmitDetectionsResult *)submitDetectionsResult;
- (NSString *)stringifySubmitDetectionsError:(SENTSubmitDetectionsError *)submitDetectionsError;
- (NSDictionary *)convertEnableDetectionsResult:(SENTEnableDetectionsResult *)enableDetectionsResult;
- (NSString *)stringifyEnableDetectionsError:(SENTEnableDetectionsError *)enableDetectionsError;
- (NSDictionary *)convertDisableDetectionsResult:(SENTDisableDetectionsResult *)disableDetectionsResult;
- (NSString *)stringifyDisableDetectionsError:(SENTDisableDetectionsError *)disableDetectionsError;
@end
