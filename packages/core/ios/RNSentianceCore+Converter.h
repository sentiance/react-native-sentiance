//
//  RNSentianceCore+Converter.h
//  RNSentianceCore
//
//  Created by Sebouh Aguehian on 10/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//
#import "RNSentianceCore.h"

typedef NS_ENUM(NSInteger, UIBackgroundRefreshStatus);

@interface RNSentianceCore (Converter)

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
- (NSDictionary*)convertVehicleCrashDiagnosticToDict:(SENTVehicleCrashDiagnostic*) crashDiagnostic;
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
- (NSString *)stringifyUserContextError:(SENTRequestUserContextError *)userContextError;
- (NSString*)convertBackgroundRefreshStatus:(UIBackgroundRefreshStatus)backgroundRefreshStatus;
- (NSSet<NSString*> *)convertIntegerTransmittableDataTypes:(NSArray<NSNumber*>*)intDataTypes;
- (NSSet<NSNumber*> *)convertStringTransmittableDataTypes:(NSArray<NSString*>*)stringDataTypes;
- (NSDictionary *)convertDrivingInsights:(SENTDrivingInsights *)drivingInsights;
- (NSArray *)convertHarshDrivingEvents:(NSArray<SENTHarshDrivingEvent*> *)harshDrivingEvents;
- (NSArray *)convertPhoneUsageEvents:(NSArray<SENTPhoneUsageEvent*> *)phoneUsageEvents;
- (NSArray *)convertCallWhileMovingEvents:(NSArray<SENTCallWhileMovingEvent*> *)callWhileMovingEvents;
- (NSArray *)convertSpeedingEvents:(NSArray<SENTSpeedingEvent*> *)speedingEvents;
- (NSMutableDictionary*)convertEvent:(SENTTimelineEvent*)event;
- (NSError *)convertSmartGeofencesRefreshError:(SENTSmartGeofencesRefreshError *)refreshError;
- (NSString *)stringifySmartGeofencesDetectionMode:(SENTSmartGeofenceDetectionMode)detectionMode;
- (NSDictionary*)convertSmartGeofenceEvent:(SENTSmartGeofenceEvent*)event;
- (SENTSafetyScoreRequestParameters*)convertToSafetyScoreRequestParameters:(NSDictionary*)params;
- (NSNumber * _Nullable)occupantRoleFeedbackFromString:(NSString * _Nonnull)stringValue;
- (NSString *_Nonnull)convertSENTOccupantRoleFeedbackResultToString:(SENTOccupantRoleFeedbackResult)feedbackResult;

@end
