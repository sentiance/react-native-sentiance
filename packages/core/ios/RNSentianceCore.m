#import "RNSentianceCore.h"
#import <SENTSDK/SENTSDK.h>
#import <SENTSDK/SENTSDKStatus.h>
#import <SENTSDK/SENTPublicDefinitions.h>
#import "RNSentianceNativeInitialization.h"
#import "RNSentianceCore+Converter.h"
#import "RNSentianceErrorCodes.h"
#import "RNSentianceSubscriptionsManager.h"

#define REJECT_IF_SDK_NOT_INITIALIZED(reject) if ([self isSdkNotInitialized]) {                            \
reject(ESDKNotInitialized, @"Sdk not initialized", nil); \
return;                                                  \
}

@interface RNSentianceCore()

@property (nonatomic, strong) void (^userLinkSuccess)(void);
@property (nonatomic, strong) void (^userLinkFailed)(void);
@property (nonatomic, strong) RNSentianceSubscriptionsManager* subscriptionsManager;
@property (nonatomic, strong) SENTUserLinker userLinker;
@property (nonatomic, strong) SdkStatusHandler sdkStatusHandler;
@property (assign) BOOL userLinkingEnabled;
@property (assign) BOOL hasListeners;

@end

@implementation RNSentianceCore

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE(SentianceCore)

- (NSArray<NSString *> *)supportedEvents
{
    return @[SdkStatusUpdateEvent, TripTimeoutEvent, UserLinkEvent, UserActivityUpdateEvent, VehicleCrashEvent, VehicleCrashDiagnosticEvent, UserLinkEvent, UserContextUpdateEvent, DrivingInsightsReadyEvent, TimelineUpdateEvent, SmartGeofenceEvent];
}

// Will be called when this module's first listener is added.
- (void)startObserving {
    self.hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    self.hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (void)onDrivingInsightsReadyWithInsights:(SENTDrivingInsights *)insights {
    [self sendEventWithName:DrivingInsightsReadyEvent body:[self convertDrivingInsights:insights]];
}

- (void)onEventTimelineUpdateWithEvent:(SENTTimelineEvent * _Nonnull)event {
    // Explicitly left blank, even though it's optional
}

- (void)onProvisionalAwareEventTimelineUpdateWithEvent:(SENTTimelineEvent * _Nonnull)event {
    [self sendEventWithName:TimelineUpdateEvent body:[self convertEvent:event]];
}

- (void)onSmartGeofenceEvent:(SENTSmartGeofenceEvent * _Nonnull)smartGeofenceEvent {
    [self sendEventWithName:SmartGeofenceEvent body:[self convertSmartGeofenceEvent:smartGeofenceEvent]];
}

- (void)didUpdateProvisionalAwareUserContext:(SENTUserContext *)userContext forCriteriaMask:(SENTUserContextUpdateCriteria)criteriaMask {
    NSDictionary *dict = @{
        @"userContext": [self convertUserContextToDict:userContext],
        @"criteria": [self convertUserContextCriteriaToArray:criteriaMask]
    };
    [self sendEventWithName:UserContextUpdateEvent body:dict];
}

- (void)didUpdateUserContext:(SENTUserContext *)userContext
             forCriteriaMask:(SENTUserContextUpdateCriteria)criteriaMask {
    // Explicitly left blank, even though it's optional
}

- (instancetype)init
{
    self = [super init];
    _subscriptionsManager = [RNSentianceSubscriptionsManager new];
    __weak typeof(self) weakSelf = self;

    [_subscriptionsManager addSupportedSubscriptionForEventType:TimelineUpdateEvent nativeSubscribeLogic:^{
        [[Sentiance sharedInstance] setEventTimelineDelegate:weakSelf];
    } nativeUnsubscribeLogic:^{
        [[Sentiance sharedInstance] setEventTimelineDelegate:nil];
    } subscriptionType:SENTSubscriptionTypeSingle];

    [_subscriptionsManager addSupportedSubscriptionForEventType:DrivingInsightsReadyEvent nativeSubscribeLogic:^{
        [[Sentiance sharedInstance] setDrivingInsightsReadyDelegate:weakSelf];
    } nativeUnsubscribeLogic:^{
        [[Sentiance sharedInstance] setDrivingInsightsReadyDelegate:nil];
    } subscriptionType:SENTSubscriptionTypeSingle];

    [_subscriptionsManager addSupportedSubscriptionForEventType:SmartGeofenceEvent nativeSubscribeLogic:^{
        [[Sentiance sharedInstance] setSmartGeofenceEventsDelegate:weakSelf];
    } nativeUnsubscribeLogic:^{
        [[Sentiance sharedInstance] setSmartGeofenceEventsDelegate:nil];
    } subscriptionType:SENTSubscriptionTypeSingle];

    [_subscriptionsManager addSupportedSubscriptionForEventType:UserContextUpdateEvent nativeSubscribeLogic:^{
        [Sentiance sharedInstance].criteriaMaskForUserContextUpdates = SENTUserContextUpdateCriteriaAll;
        [[Sentiance sharedInstance] setUserContextDelegate:weakSelf];
    } nativeUnsubscribeLogic:^{
        [[Sentiance sharedInstance] setUserContextDelegate:nil];
    } subscriptionType:SENTSubscriptionTypeSingle];

    return self;
}

- (void) initSDK:(NSString *)appId
          secret:(NSString *)secret
         baseURL:(nullable NSString *)baseURL
     shouldStart:(BOOL)shouldStart
        resolver:(RCTPromiseResolveBlock)resolve
        rejecter:(RCTPromiseRejectBlock)reject
{
    @try {
        __weak typeof(self) weakSelf = self;
        SENTConfig *config;
        if(weakSelf.userLinkingEnabled){
            config = [[SENTConfig alloc] initWithAppId:appId secret:secret link:weakSelf.getUserLinker launchOptions:@{}];
        }else{
            config = [[SENTConfig alloc] initWithAppId:appId secret:secret link:nil launchOptions:@{}];
        }

        [config setDidReceiveSdkStatusUpdate:weakSelf.getSdkStatusUpdateHandler];

        if (baseURL && baseURL.length > 0) {
            config.baseURL = baseURL;
        }

        [[Sentiance sharedInstance] initWithConfig:config success:^{
            if (shouldStart) {
                [weakSelf startSDK:resolve rejecter:reject];
            }
            else if (resolve) {
                resolve(@(YES));
            }
        } failure:^(SENTInitIssue issue) {
            if (reject) {
                reject([weakSelf convertInitIssueToString: issue], @"", nil);
            }
        }];
    } @catch (NSException *e) {
        if (reject) {
            reject(e.name, e.reason, nil);
        }
    }
}

- (BOOL) initSDKIfUserLinkingCompleted:(NSString *)appId
                                secret:(NSString *)secret
                               baseURL:(nullable NSString *)baseURL
                           shouldStart:(BOOL)shouldStart
                              resolver:(RCTPromiseResolveBlock)resolve
                              rejecter:(RCTPromiseRejectBlock)reject
{
    BOOL isThirdPartyLinked = [self isThirdPartyLinked];
    if (isThirdPartyLinked) {
        [self initSDK:appId secret:secret baseURL:baseURL shouldStart:shouldStart resolver:resolve rejecter:reject];
        return YES;
    }
    return NO;
}

- (void) startSDK:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject {
    [self startSDKWithStopEpochTimeMs:nil resolver:resolve rejecter:reject];
}

- (void) startSDKWithStopEpochTimeMs:(nullable NSNumber*) stopEpochTimeMs resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject {
    __block BOOL resolved = NO;

    @try {
        __weak typeof(self) weakSelf = self;

        SdkStatusHandler sdkStatusHandler =  ^(SENTSDKStatus* status) {
            NSLog(@"SDK started properly.");
            if (resolve && !resolved) {
                resolve([weakSelf convertSdkStatusToDict:status]);
                resolved = YES;
            }
        };

        if (stopEpochTimeMs == nil) {
            [[Sentiance sharedInstance] start:sdkStatusHandler];
        }
        else {
            NSTimeInterval interval = stopEpochTimeMs.longValue / 1000;
            NSDate* date = [NSDate dateWithTimeIntervalSince1970:interval];
            [[Sentiance sharedInstance] startWithStopDate:date completion:sdkStatusHandler];
        }
    } @catch (NSException *e) {
        if (reject && !resolved) {
            reject(e.name, e.reason, nil);
            resolved = YES;
        }
    }
}

- (SENTUserLinker) getUserLinker {
    if (self.userLinker != nil) {
        return self.userLinker;
    }

    __weak typeof(self) weakSelf = self;

    self.userLinker = ^(NSString *installId, void (^linkSuccess)(void),
                        void (^linkFailed)(void)) {
        weakSelf.userLinkSuccess = linkSuccess;
        weakSelf.userLinkFailed = linkFailed;
        [weakSelf sendEventWithName:UserLinkEvent body:[weakSelf convertInstallIdToDict:installId]];
    };

    return self.userLinker;
}


- (SdkStatusHandler) getSdkStatusUpdateHandler {
    if(self.sdkStatusHandler != nil) return self.sdkStatusHandler;

    __weak typeof(self) weakSelf = self;

    [self setSdkStatusHandler:^(SENTSDKStatus *status) {
        if (weakSelf.hasListeners) {
            [weakSelf sendEventWithName:SdkStatusUpdateEvent body:[weakSelf convertSdkStatusToDict:status]];
        }
    }];
    return self.sdkStatusHandler;
}

RCT_EXPORT_METHOD(userLinkCallback:(BOOL)success) {
    if (success) {
        self.userLinkSuccess();
    } else {
        self.userLinkFailed();
    }
}

- (NSString *) getValueForKey:(NSString *)key value:(NSString *)defaultValue {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSString *value = [prefs stringForKey:key];

    if (value == nil) {
        return defaultValue;
    }

    return value;
}

RCT_EXPORT_METHOD(getValueForKey:(NSString *)key
                  value:(NSString *)defaultValue
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject){

    NSString *value = [self getValueForKey:key value:defaultValue];
    resolve(value);
}

RCT_EXPORT_METHOD(setValueForKey:(NSString *)key
                  value:(NSString *)value) {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setObject:value forKey:key];
}

RCT_EXPORT_METHOD(init:(NSString *)appId
                  secret:(NSString *)secret
                  baseURL:(nullable NSString *)baseURL
                  shouldStart:(BOOL)shouldStart
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    if (appId == nil || secret == nil) {
        reject(@"INVALID_CREDENTIALS", @"", nil);
        return;
    }
    [self initSDK:appId secret:secret baseURL:baseURL shouldStart:shouldStart resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(initWithUserLinkingEnabled:(NSString *)appId
                  secret:(NSString *)secret
                  baseURL:(nullable NSString *)baseURL
                  shouldStart:(BOOL)shouldStart
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    self.userLinkingEnabled = YES;
    [self init:appId secret:secret baseURL:baseURL shouldStart:shouldStart resolver:resolve rejecter:reject];

}

RCT_EXPORT_METHOD(start:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [self startSDK:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(enableDetections:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] enableDetectionsWithCompletionHandler:^(SENTEnableDetectionsResult * _Nullable result, SENTEnableDetectionsError * _Nullable error) {
        if (error != nil) {
            reject(ESDKEnableDetectionsError, [self stringifyEnableDetectionsError:error], nil);
        }
        else {
            resolve([self convertEnableDetectionsResult: result]);
        }
    }];
}

RCT_EXPORT_METHOD(startWithStopDate:(nonnull NSNumber *)stopEpochTimeMs
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [self startSDKWithStopEpochTimeMs:stopEpochTimeMs resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(enableDetectionsWithExpiryDate:(nonnull NSNumber *)expiryEpochTimeMs
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    if (expiryEpochTimeMs == nil) {
        [self enableDetections:resolve rejecter:reject];
    }
    else {
        NSTimeInterval interval = expiryEpochTimeMs.longValue / 1000;
        NSDate* date = [NSDate dateWithTimeIntervalSince1970:interval];
        [[Sentiance sharedInstance] enableDetectionsWithExpiryDate:date completionHandler:^(SENTEnableDetectionsResult * _Nullable result, SENTEnableDetectionsError * _Nullable error) {
            if (error != nil) {
                reject(ESDKEnableDetectionsError, [self stringifyEnableDetectionsError:error], nil);
            }
            else {
                resolve([self convertEnableDetectionsResult: result]);
            }
        }];
    }
}

RCT_EXPORT_METHOD(stop:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        Sentiance* sdk = [Sentiance sharedInstance];
        [sdk stop];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(disableDetections:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] disableDetectionsWithCompletionHandler:^(SENTDisableDetectionsResult * _Nullable result, SENTDisableDetectionsError * _Nullable error) {
            if (error != nil) {
                reject(ESDKDisableDetectionsError, [self stringifyDisableDetectionsError:error], nil);
            }
            else {
                resolve([self convertDisableDetectionsResult: result]);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getInitState:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTSDKInitState initState = [[Sentiance sharedInstance] getInitState];
        resolve([self convertInitStateToString:initState]);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}


RCT_EXPORT_METHOD(getSdkStatus:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        NSDictionary* dict = [self convertSdkStatusToDict:[[Sentiance sharedInstance] getSdkStatus]];
        resolve(dict);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getVersion:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSString *version = [[Sentiance sharedInstance] getVersion];
        resolve(version);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserId:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        NSString *userId = [[Sentiance sharedInstance] getUserId];
        resolve(userId);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserAccessToken:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    __block BOOL hasReceivedToken = NO;
    @try {
        __weak typeof(self) weakSelf = self;
        [[Sentiance sharedInstance] getUserAccessToken:^(NSString* token) {
            if (hasReceivedToken) {
                return;
            }
            NSDictionary* dict = [weakSelf convertTokenToDict:token];
            hasReceivedToken = YES;
            resolve(dict);
        } failure:^() {
            reject(@"E_SDK_GET_TOKEN_ERROR", @"Something went wrong while obtaining a user token.", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(requestUserAccessToken:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] requestUserAccessTokenWithCompletionHandler:^(SENTUserAccessTokenResult * _Nullable result, SENTUserAccessTokenError * _Nullable error) {
            if (error != nil) {
                reject(ESDKGetTokenError, [self stringifyRequestUserAccessTokenError:error], nil);
            }
            else {
                resolve([self convertRequestUserAccessTokenResult: result]);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(addUserMetadataField:(NSString *)label
                  value:(NSString *)value
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        if (label == nil || value == nil) {
            reject(ESDKMissingParams, @"label and value are required", nil);
            return;
        }

        [[Sentiance sharedInstance] addUserMetadataField:label value:value];
        resolve(nil);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(removeUserMetadataField:(NSString *)label
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        if (label == nil) {
            reject(ESDKMissingParams, @"label is required", nil);
            return;
        }

        [[Sentiance sharedInstance] removeUserMetadataField:label];
        resolve(nil);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(addUserMetadataFields:(NSDictionary *)metadata
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        if (metadata == nil) {
            reject(ESDKMissingParams, @"metadata object is required", nil);
            return;
        }

        [[Sentiance sharedInstance] addUserMetadataFields:metadata];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(startTrip:(NSDictionary *)metadata
                  hint:(nonnull NSNumber *)hint
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        SENTTransportMode mode = [hint intValue] == -1 ? SENTTransportModeUnknown : (SENTTransportMode)hint;
        [[Sentiance sharedInstance] startTrip:metadata transportModeHint:mode success:^ {
            resolve(@(YES));
        } failure:^(SENTSDKStatus *status) {
            reject(@"E_SDK_START_TRIP_ERROR", @"", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(startTripNewAPI:(NSDictionary *)metadata
                  hint:(nonnull NSNumber *)hint
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        SENTTransportMode mode = [hint intValue] == -1 ? SENTTransportModeUnknown : (SENTTransportMode)hint;
        [[Sentiance sharedInstance] startTripWithMetadata:metadata transportModeHint:mode completionHandler:^(SENTStartTripResult * _Nullable result, SENTStartTripError * _Nullable error) {
            if (error != nil) {
                reject(ESDKStartTripError, [self stringifyStartTripError:error], nil);
            }
            else {
                resolve([self convertStartTripResult:result]);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(stopTrip:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] stopTrip:^{
            resolve(@(YES));
        } failure:^(SENTSDKStatus *status) {
            reject(@"E_SDK_STOP_TRIP_ERROR", @"", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(stopTripNewAPI:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] stopTripWithCompletionHandler:^(SENTStopTripResult * _Nullable result, SENTStopTripError * _Nullable error) {
            if (error != nil) {
                reject(ESDKStopTripError, [self stringifyStopTripError:error], nil);
            }
            else {
                resolve([self convertStopTripResult:result]);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(isTripOngoing:(NSString *)type
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    if (type.length == 0) {
        reject(ESDKMissingParams, @"trip type is required", nil);
        return;
    }

    @try {
        SENTTripType tripType;
        if ([type isEqualToString:@"TRIP_TYPE_SDK"]) {
            tripType = SENTTripTypeSDK;
        } else if ([type isEqualToString:@"TRIP_TYPE_EXTERNAL"]) {
            tripType = SENTTripTypeExternal;
        }

        BOOL isTripOngoing = [[Sentiance sharedInstance] isTripOngoing:tripType];
        resolve(@(isTripOngoing));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(submitDetections:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] submitDetections:^ {
            resolve(@(YES));
        } failure: ^ {
            reject(@"E_SDK_SUBMIT_DETECTIONS_ERROR", @"Submission failed", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(submitDetectionsNewAPI:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] submitDetectionsWithCompletionHandler:^(SENTSubmitDetectionsResult * _Nullable result, SENTSubmitDetectionsError * _Nullable error) {
            if (error != nil) {
                reject(ESDKSubmitDetectionsError, [self stringifySubmitDetectionsError:error], nil);
            }
            else {
                resolve([self convertSubmitDetectionsResult:result]);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getWiFiQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long wifiQuotaLimit = [[Sentiance sharedInstance] getWifiQuotaLimit];
        resolve(@(wifiQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getWiFiQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long wifiQuotaUsage = [[Sentiance sharedInstance] getWiFiQuotaUsage];
        resolve(@(wifiQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getMobileQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long mobileQuotaLimit = [[Sentiance sharedInstance] getMobileQuotaLimit];
        resolve(@(mobileQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getMobileQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long mobileQuotaUsage = [[Sentiance sharedInstance] getMobileQuotaUsage];
        resolve(@(mobileQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getDiskQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long diskQuotaLimit = [[Sentiance sharedInstance] getDiskQuotaLimit];
        resolve(@(diskQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getDiskQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        long long diskQuotaUsage = [[Sentiance sharedInstance] getDiskQuotaUsage];
        resolve(@(diskQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(disableBatteryOptimization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSLog(@"This is an Android only method.");
    resolve(nil);
}

RCT_EXPORT_METHOD(updateSdkNotification:(NSString *)title
                  message:(NSString *)message
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSLog(@"This is an Android only method.");
    resolve(nil);
}

RCT_EXPORT_METHOD(addTripMetadata:(NSDictionary *)metadata
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSLog(@"This is an Android only method.");
    resolve(nil);
}

RCT_EXPORT_METHOD(deleteKeychainEntries:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self deleteAllKeysForSecClass:kSecClassGenericPassword];
    [self deleteAllKeysForSecClass:kSecClassInternetPassword];
    [self deleteAllKeysForSecClass:kSecClassCertificate];
    [self deleteAllKeysForSecClass:kSecClassKey];
    [self deleteAllKeysForSecClass:kSecClassIdentity];
}

RCT_EXPORT_METHOD(listenUserActivityUpdates:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        __weak typeof(self) weakSelf = self;
        [[Sentiance sharedInstance] setUserActivityListener:^(SENTUserActivity *userActivity) {
            NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
            if(weakSelf.hasListeners) {
                [weakSelf sendEventWithName:UserActivityUpdateEvent body:userActivityDict];
            }
        }];
        resolve(nil);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserActivity:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        SENTUserActivity *userActivity = [[Sentiance sharedInstance] getUserActivity];
        NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
        resolve(userActivityDict);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(isNativeInitializationEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isEnabled = [self isNativeInitializationEnabled];
    resolve(@(isEnabled));
}

RCT_EXPORT_METHOD(enableNativeInitialization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self enableSDKNativeInitialization:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(disableNativeInitialization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self disableSDKNativeInitialization:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(listenVehicleCrashEvents:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        __weak typeof(self) weakSelf = self;

        [[Sentiance sharedInstance] setVehicleCrashHandler:^(SENTVehicleCrashEvent *crashEvent) {
            if(weakSelf.hasListeners) {
                NSDictionary *crashEventDict = [self convertVehicleCrashEventToDict:crashEvent];
                [weakSelf sendEventWithName:VehicleCrashEvent body:crashEventDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(listenVehicleCrashDiagnostic:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        __weak typeof(self) weakSelf = self;

        [[Sentiance sharedInstance] setVehicleCrashDiagnosticHandler:^(SENTVehicleCrashDiagnostic *crashDiagnostic) {
            if(weakSelf.hasListeners) {
                NSDictionary *crashDiagnosticEventDict = [self convertVehicleCrashDiagnosticToDict:crashDiagnostic];
                [weakSelf sendEventWithName:VehicleCrashDiagnosticEvent body:crashDiagnosticEventDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(invokeDummyVehicleCrash:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] invokeDummyVehicleCrash];
    resolve(@(YES));
}

RCT_EXPORT_METHOD(isVehicleCrashDetectionSupported:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    BOOL supported = [[Sentiance sharedInstance] isVehicleCrashDetectionSupported];
    resolve(supported ? @(YES) : @(NO));
}

RCT_EXPORT_METHOD(isThirdPartyLinked:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isThirdPartyLinked = [self isThirdPartyLinked];
    resolve(@(isThirdPartyLinked));
}

- (BOOL)isThirdPartyLinked {
    return [[Sentiance sharedInstance] isUserLinked];
}

RCT_EXPORT_METHOD(requestUserContext:(BOOL)includeProvisionalEvents resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    __weak typeof(self) weakSelf = self;

    [[Sentiance sharedInstance] requestUserContextIncludingProvisionalEvents:(BOOL)includeProvisionalEvents completionHandler:^(SENTUserContext * _Nullable userContext, SENTRequestUserContextError * _Nullable error) {
        if (error) {
            reject(ESDKRequestUserContextError, [self stringifyUserContextError:error], nil);
        }
        else {
            resolve([weakSelf convertUserContextToDict:userContext]);
        }
    }];
}

RCT_EXPORT_METHOD(setAppSessionDataCollectionEnabled:(BOOL)enabled
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    // TODO
    resolve(nil);
}

RCT_EXPORT_METHOD(isAppSessionDataCollectionEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    // TODO
    resolve(@(NO));
}

RCT_EXPORT_METHOD(createUnlinkedUser:(NSString *)appId secret:(NSString *)secret platformUrl:(NSString *)platformUrl resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTUserCreationOptions *options = [[SENTUserCreationOptions alloc] initWithAppId:appId secret:secret linker: nil];
    options.platformUrl = platformUrl;
    [[Sentiance sharedInstance] createUserWithOptions:options completionHandler:^(SENTUserCreationResult * _Nullable result, SENTUserCreationError * _Nullable error) {
        if (error != nil) {
            reject(ESDKCreateUserError, [self stringifyUserCreationError:error], nil);
        }
        else {
            resolve([self convertUserCreationResult: result]);
        }
    }];
}

RCT_EXPORT_METHOD(createLinkedUser:(NSString *)appId secret:(NSString *)secret platformUrl:(NSString *)platformUrl resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTUserCreationOptions *options = [[SENTUserCreationOptions alloc] initWithAppId:appId secret:secret linker: self.getUserLinker];
    options.platformUrl = platformUrl;
    [[Sentiance sharedInstance] createUserWithOptions:options completionHandler:^(SENTUserCreationResult * _Nullable result, SENTUserCreationError * _Nullable error) {
        if (error != nil) {
            reject(ESDKCreateUserError, [self stringifyUserCreationError:error], nil);
        }
        else {
            resolve([self convertUserCreationResult: result]);
        }
    }];
}

RCT_EXPORT_METHOD(createLinkedUserWithAuthCode:(NSString *)authCode platformUrl:(NSString *)platformUrl resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTUserCreationOptions *options = [[SENTUserCreationOptions alloc] initWithAuthenticationCode: authCode];
    options.platformUrl = platformUrl;
    [[Sentiance sharedInstance] createUserWithOptions:options completionHandler:^(SENTUserCreationResult * _Nullable result, SENTUserCreationError * _Nullable error) {
        if (error != nil) {
            reject(ESDKCreateUserError, [self stringifyUserCreationError:error], nil);
        }
        else {
            resolve([self convertUserCreationResult: result]);
        }
    }];
}

RCT_EXPORT_METHOD(linkUserWithAuthCode:(NSString *)authCode resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] linkUserWithAuthCode:authCode completionHandler:^(SENTUserLinkingResult * _Nullable result, SENTUserLinkingError * _Nullable error) {
        if (error != nil) {
            reject(ESDKUserLinkAuthCodeError, [self stringifyUserLinkingError:error], nil);
        }
        else {
            resolve([self convertUserLinkingResult: result]);
        }
    }];
}

RCT_EXPORT_METHOD(linkUser:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] linkUserWithLinker:[self getUserLinker] completionHandler:^(SENTUserLinkingResult * _Nullable result, SENTUserLinkingError * _Nullable error) {
        if (error != nil) {
            reject(ESDKUserLinkError, [self stringifyUserLinkingError:error], nil);
        }
        else {
            resolve([self convertUserLinkingResult: result]);
        }
    }];

}

RCT_EXPORT_METHOD(userExists:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL userExists = [[Sentiance sharedInstance] userExists];
    resolve(@(userExists));
}

RCT_EXPORT_METHOD(isUserLinked:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isUserLinked = [[Sentiance sharedInstance] isUserLinked];
    resolve(@(isUserLinked));
}

RCT_EXPORT_METHOD(reset:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [[Sentiance sharedInstance] reset:^{
        [self disableSDKNativeInitialization:resolve rejecter:reject];
    } failure:^(SENTResetFailureReason reason) {
        NSString *message = @"Resetting the SDK failed";
        switch(reason) {
            case SENTResetFailureReasonInitInProgress:
                reject(@"SDK_INIT_IN_PROGRESS", message, nil);
                break;
            case SENTResetFailureReasonResetting:
                reject(@"SDK_RESET_IN_PROGRESS", message, nil);
                break;
            default:
                reject(@"SDK_RESET_UNKNOWN_ERROR", message, nil);
        }
    }];
}

RCT_EXPORT_METHOD(resetNewAPI:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [[Sentiance sharedInstance] resetWithCompletionHandler:^(SENTResetResult * _Nullable result, SENTResetError * _Nullable error) {
        if (error != nil) {
            reject(ESDKResetError, [self stringifyResetError:error], nil);
        }
        else {
            resolve([self convertResetResult: result]);
            [self disableSDKNativeInitialization:nil rejecter:nil];
        }
    }];
}

RCT_EXPORT_METHOD(listenSdkStatusUpdates:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] setDidReceiveSdkStatusUpdateHandler: [self getSdkStatusUpdateHandler]];
    resolve(nil);
}

RCT_EXPORT_METHOD(listenTripTimeout:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    __weak typeof(self) weakSelf = self;
    [[Sentiance sharedInstance] setTripTimeoutListener:^ {
        if (weakSelf.hasListeners) {
            [weakSelf sendEventWithName:TripTimeoutEvent body:nil];
        }
    }];
    resolve(nil);
}

RCT_EXPORT_METHOD(setTransmittableDataTypes:(NSArray *)types resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSSet *typesSet = [NSSet setWithArray:types];
    NSSet *convertedTypes = [self convertStringTransmittableDataTypes:types];
    [[Sentiance sharedInstance] setTransmittableDataTypes:convertedTypes];
}

RCT_EXPORT_METHOD(getTransmittableDataTypes:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSSet *types = [[Sentiance sharedInstance] transmittableDataTypes];
    NSSet *convertedTypes = [self convertIntegerTransmittableDataTypes:types];
    NSArray *array = [convertedTypes allObjects];
    resolve(array);
}

RCT_EXPORT_METHOD(getDrivingInsights:(NSString*)transportId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTDrivingInsights *drivingInsights = [[Sentiance sharedInstance] getDrivingInsightsForTransportId:transportId];
    if (drivingInsights == nil) {
        resolve(nil);
        return;
    }
    resolve([self convertDrivingInsights:drivingInsights]);
}

RCT_EXPORT_METHOD(getHarshDrivingEvents:(NSString*)transportId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSArray<SENTHarshDrivingEvent *> *harshDrivingEvents = [[Sentiance sharedInstance] getHarshDrivingEventsForTransportId:transportId];
    resolve([self convertHarshDrivingEvents:harshDrivingEvents]);
}

RCT_EXPORT_METHOD(getPhoneUsageEvents:(NSString*)transportId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSArray<SENTPhoneUsageEvent *> *phoneUsageEvents = [[Sentiance sharedInstance] getPhoneUsageEventsForTransportId:transportId];
    resolve([self convertPhoneUsageEvents:phoneUsageEvents]);
}

RCT_EXPORT_METHOD(getCallWhileMovingEvents:(NSString*)transportId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSArray<SENTCallWhileMovingEvent *> *callWhileMovingEvents = [[Sentiance sharedInstance] getCallsWhileMovingEventsForTransportId:transportId];
    resolve([self convertCallWhileMovingEvents:callWhileMovingEvents]);
}

RCT_EXPORT_METHOD(getSpeedingEvents:(NSString*)transportId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSArray<SENTSpeedingEvent *> *speedingEvents = [[Sentiance sharedInstance] getSpeedingEventsForTransportId:transportId];
    resolve([self convertSpeedingEvents:speedingEvents]);
}

RCT_EXPORT_METHOD(getAverageOverallSafetyScore:(NSDictionary *)params resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTSafetyScoreRequestParameters* requestParams = [self convertToSafetyScoreRequestParameters:params];
    resolve([[Sentiance sharedInstance] objc_averageOverallSafetyScoreWithParams:requestParams]);
}

RCT_EXPORT_METHOD(addNativeListener:(NSString *)eventName subscriptionId:(NSInteger)subscriptionId payload:(nullable NSDictionary*)payload resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [_subscriptionsManager addSubscriptionForEventType:eventName subscriptionId:subscriptionId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeNativeListener:(NSString *)eventName subscriptionId:(NSInteger)subscriptionId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [_subscriptionsManager removeSubscriptionForId:subscriptionId eventType:eventName];

    resolve(nil);
}

RCT_EXPORT_METHOD(getTimelineEvent:(NSString *)eventId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    SENTTimelineEvent* event = [[Sentiance sharedInstance] getTimelineEventWithEventId:eventId];
    if (event == nil) {
        resolve(nil);
    } else {
        resolve([self convertEvent:event]);
    }
}

RCT_EXPORT_METHOD(getTimelineUpdates:(nonnull NSNumber *)afterEpochTimeMs includeProvisionalEvents:(BOOL)includeProvisionalEvents  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSTimeInterval interval = afterEpochTimeMs.longValue / 1000.0;
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:interval];

    NSArray<SENTTimelineEvent *>* events;
    if (includeProvisionalEvents) {
        events = [[Sentiance sharedInstance] getTimelineUpdatesIncludingProvisionalEventsAfter:date];
    } else {
        events = [[Sentiance sharedInstance] getTimelineUpdatesAfter:date];
    }

    NSMutableArray *array = [[NSMutableArray alloc] init];
    for (SENTTimelineEvent* event in events) {
        [array addObject:[self convertEvent:event]];
    }

    resolve(array);
}

RCT_EXPORT_METHOD(getTimelineEvents:(nonnull NSNumber *)fromEpochTimeMs toEpochTimeMs:(nonnull NSNumber *)toEpochTimeMs includeProvisionalEvents:(BOOL)includeProvisionalEvents resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSTimeInterval fromInterval = fromEpochTimeMs.longValue / 1000.0;
    NSDate* fromDate = [NSDate dateWithTimeIntervalSince1970:fromInterval];
    NSTimeInterval toInterval = toEpochTimeMs.longValue / 1000.0;
    NSDate* toDate = [NSDate dateWithTimeIntervalSince1970:toInterval];

    NSArray<SENTTimelineEvent *>* events;
    if (includeProvisionalEvents) {
        events = [[Sentiance sharedInstance] getTimelineEventsIncludingProvisionalOnesFrom:fromDate to:toDate];
    } else {
        events = [[Sentiance sharedInstance] getTimelineEventsFrom:fromDate to:toDate];
    }

    NSMutableArray *array = [[NSMutableArray alloc] init];
    for (SENTTimelineEvent* event in events) {
        [array addObject:[self convertEvent:event]];
    }

    resolve(array);
}

RCT_EXPORT_METHOD(setIsAllowedToUseMobileData:(BOOL)isAllowed resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    [[Sentiance sharedInstance] setAllowedToUseMobileData:isAllowed];

    resolve(nil);
}

RCT_EXPORT_METHOD(isAllowedToUseMobileData:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    resolve(@(Sentiance.sharedInstance.isAllowedToUseMobileData));
}

RCT_EXPORT_METHOD(refreshGeofences:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        [[Sentiance sharedInstance] refreshSmartGeofencesWithCompletionHandler:^(SENTSmartGeofencesRefreshResult * _Nullable result, SENTSmartGeofencesRefreshError * _Nullable error) {
            if (error != nil) {
                reject(ESDKSmartGeofencesRefreshError, @"Failed to refresh smart geofences", [self convertSmartGeofencesRefreshError:error]);
            }
            else {
                resolve(nil);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getDetectionMode:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    @try {
        resolve([self stringifySmartGeofencesDetectionMode:Sentiance.sharedInstance.smartGeofenceDetectionMode]);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(setTransportTags:(NSDictionary *)tags
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);
    NSError *error = nil;

    [[Sentiance sharedInstance] setTransportTags:tags error:&error];

    if (error != nil) {
        if ([error.domain isEqualToString:@"SENTTransportTaggingDomain"]) {
            reject(ESDKTransportTaggingError, error.localizedDescription, nil);
        } else {
            reject(@"E_SDK_SET_TRANSPORT_TAGS", @"Unexpected error while attempting to set transport tags", error);
        }
    } else {
        resolve(nil);
    }
}

RCT_EXPORT_METHOD(submitOccupantRoleFeedback:(NSString *)transportId
                  occupantFeedbackRole:(NSString *)occupantFeedbackRoleString
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    REJECT_IF_SDK_NOT_INITIALIZED(reject);

    NSNumber *feedbackValue = [self occupantRoleFeedbackFromString:occupantFeedbackRoleString];
    if (!feedbackValue) {
        // This should never happen, as JS validates input before calling native code.
        reject(@"E_SDK_INVALID_FEEDBACK_TYPE", @"Invalid occupant role provided.", nil);
        return;
    }

    SENTOccupantRoleFeedback occupantRoleFeedback = feedbackValue.integerValue;
    id<SENTFeedback> feedback = [[Sentiance sharedInstance] feedback];

    if (!feedback) {
        reject(ESDKFeedbackNotAvailableError, @"Feedback service is not available.", nil);
        return;
    }

    SENTOccupantRoleFeedbackResult result = [feedback submitOccupantRoleFeedbackWithTransportId:transportId feedbackRole:occupantRoleFeedback];

    resolve([self convertSENTOccupantRoleFeedbackResultToString:result]);
}

- (BOOL)isNativeInitializationEnabled {
    return [[RNSentianceNativeInitialization sharedObject] isFlagFileExists];
}

- (void)enableSDKNativeInitialization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject {
    if([[RNSentianceNativeInitialization sharedObject] isFlagFileExists]) {
        if (resolve) {
            resolve(@(YES));
        }
        return;
    }

    NSError *error;
    [[RNSentianceNativeInitialization sharedObject] createFlagFile:&error];

    if (error != nil) {
        if (reject) {
            reject(@"ERROR_CREATING_DIR", error.description, nil);
        }
    } else if (resolve) {
        resolve(@(YES));
    }
}

- (void)disableSDKNativeInitialization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject {
    if([[RNSentianceNativeInitialization sharedObject] isFlagFileExists]) {
        NSError *error;
        [[RNSentianceNativeInitialization sharedObject] removeFlagFile:&error];
        if (error != nil) {
            if (reject) {
                reject(@"ERROR_REMOVE_FILE", error.description, nil);
            }
        } else if (resolve) {
            resolve(@(YES));
        }
    } else if (resolve) {
        resolve(@(YES));
    }
}

-(void)deleteAllKeysForSecClass:(CFTypeRef)secClass {
    NSMutableDictionary* dict = [NSMutableDictionary dictionary];
    [dict setObject:(__bridge id)secClass forKey:(__bridge id)kSecClass];
    SecItemDelete((__bridge CFDictionaryRef) dict);
}

- (BOOL)isSdkNotInitialized {
    return [Sentiance sharedInstance].initState != SENTSDKInitStateInitialized;
}

@end
