
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <SENTSDK/SENTSDK.h>
#import <SENTSDK/SENTSDK-Swift.h>

static NSString * _Nonnull const UserLinkEvent = @"SENTIANCE_USER_LINK_EVENT";
static NSString * _Nonnull const SdkStatusUpdateEvent = @"SENTIANCE_STATUS_UPDATE_EVENT";
static NSString * _Nonnull const UserActivityUpdateEvent = @"SENTIANCE_USER_ACTIVITY_UPDATE_EVENT";
static NSString * _Nonnull const ON_DETECTIONS_ENABLED = @"SENTIANCE_ON_DETECTIONS_ENABLED_EVENT";
static NSString * _Nonnull const TripTimeoutEvent = @"SENTIANCE_ON_TRIP_TIMED_OUT_EVENT";
static NSString * _Nonnull const VehicleCrashEvent = @"SENTIANCE_VEHICLE_CRASH_EVENT";
static NSString * _Nonnull const VehicleCrashDiagnosticEvent = @"SENTIANCE_VEHICLE_CRASH_DIAGNOSTIC_EVENT";
static NSString * _Nonnull const UserContextUpdateEvent = @"SENTIANCE_USER_CONTEXT_UPDATE_EVENT";
static NSString * _Nonnull const DrivingInsightsReadyEvent = @"SENTIANCE_DRIVING_INSIGHTS_READY_EVENT";
static NSString * _Nonnull const TimelineUpdateEvent = @"SENTIANCE_TIMELINE_UPDATE_EVENT";
static NSString * _Nonnull const SmartGeofenceEvent = @"SENTIANCE_SMART_GEOFENCE_EVENT";

@interface RNSentianceCore : RCTEventEmitter <RCTBridgeModule, SENTUserContextDelegate, SENTDrivingInsightsReadyDelegate, SENTEventTimelineDelegate, SENTSmartGeofenceEventDelegate>
typedef void (^SdkStatusHandler)(SENTSDKStatus * _Nonnull status);
- (SENTUserLinker _Nonnull ) getUserLinker;
- (SdkStatusHandler _Nonnull) getSdkStatusUpdateHandler;
- (void) initSDK: (NSString * _Nonnull)appId secret:(NSString * _Nonnull)secret baseURL:(NSString * _Nullable)baseURL shouldStart:(BOOL)shouldStart resolver:(RCTPromiseResolveBlock _Nullable)resolve  rejecter:(RCTPromiseRejectBlock _Nullable)reject;
- (BOOL) initSDKIfUserLinkingCompleted: (NSString * _Nonnull)appId secret:(NSString * _Nonnull)secret baseURL:(NSString * _Nullable)baseURL shouldStart:(BOOL)shouldStart resolver:(RCTPromiseResolveBlock _Nonnull)resolve  rejecter:(RCTPromiseRejectBlock _Nonnull)reject;
- (BOOL) isThirdPartyLinked;
- (NSString * _Nullable) getValueForKey: (NSString * _Nonnull)key value:(NSString * _Nullable)defaultValue;
- (void) setValueForKey: (NSString * _Nonnull)key value:(NSString * _Nullable)value;
- (void) startSDKWithStopEpochTimeMs: (nullable NSNumber*) stopEpochTimeMs resolver:(RCTPromiseResolveBlock _Nonnull )resolve  rejecter:(RCTPromiseRejectBlock _Nonnull )reject;
- (void) startSDK: (RCTPromiseResolveBlock _Nullable )resolve rejecter:(RCTPromiseRejectBlock _Nullable )reject;
- (BOOL) isNativeInitializationEnabled;
- (void) disableSDKNativeInitialization: (RCTPromiseResolveBlock _Nullable)resolve rejecter:(RCTPromiseRejectBlock _Nullable)reject;
- (void) enableSDKNativeInitialization: (RCTPromiseResolveBlock _Nullable)resolve rejecter:(RCTPromiseRejectBlock _Nullable)reject;
- (void) setTransmittableDataTypes: (NSArray *)types;
- (NSArray*) getTransmittableDataTypes:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject;
@end
