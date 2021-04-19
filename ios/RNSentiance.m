#import "RNSentiance.h"
#import <SENTSDK/SENTSDK.h>
#import <SENTSDK/SENTSDKStatus.h>
#import <SENTSDK/SENTPublicDefinitions.h>
#import "RNSentianceNativeInitialization.h"

@interface SENTSDK (Bindings)
- (BOOL)userExists;
- (BOOL)isThirdPartyLinked;
@end

@interface RNSentiance()

@property (nonatomic, strong) void (^userLinkSuccess)(void);
@property (nonatomic, strong) void (^userLinkFailed)(void);
@property (nonatomic, strong) MetaUserLinker userLinker;
@property (nonatomic, strong) SdkStatusHandler sdkStatusHandler;
@property (assign) BOOL userLinkingEnabled;
@property (assign) BOOL hasListeners;

@end

@implementation RNSentiance

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"SDKStatusUpdate", @"SDKTripTimeout", @"SDKUserLink", @"SDKUserActivityUpdate", @"SDKCrashEvent", @"SDKTripProfile", @"VehicleCrashEvent"];
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

        [[SENTSDK sharedInstance] initWithConfig:config success:^{
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
            [[SENTSDK sharedInstance] start:sdkStatusHandler];
        }
        else {
            NSTimeInterval interval = stopEpochTimeMs.longValue / 1000;
            NSDate* date = [NSDate dateWithTimeIntervalSince1970:interval];
            [[SENTSDK sharedInstance] startWithStopDate:date completion:sdkStatusHandler];
        }
    } @catch (NSException *e) {
        if (reject && !resolved) {
            reject(e.name, e.reason, nil);
            resolved = YES;
        }
    }
}

- (MetaUserLinker) getUserLinker {
    if(self.userLinker != nil) return self.userLinker;

    __weak typeof(self) weakSelf = self;

    self.userLinker = ^(NSString *installId, void (^linkSuccess)(void),
                        void (^linkFailed)(void)) {
        weakSelf.userLinkSuccess = linkSuccess;
        weakSelf.userLinkFailed = linkFailed;
        [weakSelf sendEventWithName:@"SDKUserLink" body:[weakSelf convertInstallIdToDict:installId]];
    };

    return self.userLinker;
}

- (SdkStatusHandler) getSdkStatusUpdateHandler {
    if(self.sdkStatusHandler != nil) return self.sdkStatusHandler;

    __weak typeof(self) weakSelf = self;

    [self setSdkStatusHandler:^(SENTSDKStatus *status) {
        if (weakSelf.hasListeners) {
            [weakSelf sendEventWithName:@"SDKStatusUpdate" body:[weakSelf convertSdkStatusToDict:status]];
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
    return value;
}

RCT_EXPORT_METHOD(getValueForKey:(NSString *)key
                  value:(NSString *)defaultValue
                  resolver:(RCTPromiseResolveBlock)resolve){

    NSString *value = [self getValueForKey:key value:defaultValue];
    if (value == nil) {
        resolve(defaultValue);
    } else {
        resolve(value);
    }
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
    [self startSDK:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(startWithStopDate:(nonnull NSNumber *)stopEpochTimeMs
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [self startSDKWithStopEpochTimeMs:stopEpochTimeMs resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(stop:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTSDK* sdk = [SENTSDK sharedInstance];
        [sdk stop];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getInitState:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTSDKInitState initState = [[SENTSDK sharedInstance] getInitState];
        resolve([self convertInitStateToString:initState]);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}


RCT_EXPORT_METHOD(getSdkStatus:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSDictionary* dict = [self convertSdkStatusToDict:[[SENTSDK sharedInstance] getSdkStatus]];
        resolve(dict);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getVersion:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSString *version = [[SENTSDK sharedInstance] getVersion];
        resolve(version);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserId:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSString *userId = [[SENTSDK sharedInstance] getUserId];
        resolve(userId);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserAccessToken:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    __block BOOL hasReceivedToken = NO;
    @try {
        __weak typeof(self) weakSelf = self;
        [[SENTSDK sharedInstance] getUserAccessToken:^(NSString* token) {
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

RCT_EXPORT_METHOD(addUserMetadataField:(NSString *)label
                  value:(NSString *)value
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        if (label == nil || value == nil) {
            reject(@"E_SDK_MISSING_PARAMS", @"label and value are required", nil);
            return;
        }

        [[SENTSDK sharedInstance] addUserMetadataField:label value:value];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(removeUserMetadataField:(NSString *)label
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        if (label == nil) {
            reject(@"E_SDK_MISSING_PARAMS", @"label is required", nil);
            return;
        }

        [[SENTSDK sharedInstance] removeUserMetadataField:label];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(addUserMetadataFields:(NSDictionary *)metadata
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        if (metadata == nil) {
            reject(@"E_SDK_MISSING_PARAMS", @"metadata object is required", nil);
            return;
        }

        [[SENTSDK sharedInstance] addUserMetadataFields:metadata];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(startTrip:(NSDictionary *)metadata
                  hint:(nonnull NSNumber *)hint
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTTransportMode mode = [hint intValue] == -1 ? SENTTransportModeUnknown : (SENTTransportMode)hint;
        [[SENTSDK sharedInstance] startTrip:metadata transportModeHint:mode success:^ {
            resolve(@(YES));
        } failure:^(SENTSDKStatus *status) {
            reject(@"E_SDK_START_TRIP_ERROR", @"", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(stopTrip:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        [[SENTSDK sharedInstance] stopTrip:^{
            resolve(@(YES));
        } failure:^(SENTSDKStatus *status) {
            reject(@"E_SDK_STOP_TRIP_ERROR", @"", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(isTripOngoing:(NSString *)type
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTTripType tripType;
        if ([type isEqualToString:@"TRIP_TYPE_SDK"]) {
            tripType = SENTTripTypeSDK;
        } else if ([type isEqualToString:@"TRIP_TYPE_EXTERNAL"]) {
            tripType = SENTTripTypeExternal;
        }

        BOOL isTripOngoing = [[SENTSDK sharedInstance] isTripOngoing:tripType];
        resolve(@(isTripOngoing));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(submitDetections:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        [[SENTSDK sharedInstance] submitDetections:^ {
            resolve(@(YES));
        } failure: ^ {
            reject(@"E_SDK_SUBMIT_DETECTIONS_ERROR", @"Submission failed", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getWiFiQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long wifiQuotaLimit = [[SENTSDK sharedInstance] getWifiQuotaLimit];
        resolve(@(wifiQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getWiFiQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long wifiQuotaUsage = [[SENTSDK sharedInstance] getWiFiQuotaUsage];
        resolve(@(wifiQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getMobileQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long mobileQuotaLimit = [[SENTSDK sharedInstance] getMobileQuotaLimit];
        resolve(@(mobileQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getMobileQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long mobileQuotaUsage = [[SENTSDK sharedInstance] getMobileQuotaUsage];
        resolve(@(mobileQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getDiskQuotaLimit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long diskQuotaLimit = [[SENTSDK sharedInstance] getDiskQuotaLimit];
        resolve(@(diskQuotaLimit));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getDiskQuotaUsage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        long long diskQuotaUsage = [[SENTSDK sharedInstance] getDiskQuotaUsage];
        resolve(@(diskQuotaUsage));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(disableBatteryOptimization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"This is an Android only method.");
    resolve(@(YES));
}

RCT_EXPORT_METHOD(updateSdkNotification:(NSString *)title
                  message:(NSString *)message
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"This is an Android only method.");
    resolve(@(YES));
}

RCT_EXPORT_METHOD(addTripMetadata:(NSDictionary *)metadata
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"This is an Android only method.");
    resolve(@(YES));
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
    @try {
        __weak typeof(self) weakSelf = self;
        [[SENTSDK sharedInstance] setUserActivityListener:^(SENTUserActivity *userActivity) {
            NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
            if(weakSelf.hasListeners) {
                [weakSelf sendEventWithName:@"SDKUserActivityUpdate" body:userActivityDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getUserActivity:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTUserActivity *userActivity = [[SENTSDK sharedInstance] getUserActivity];
        NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
        resolve(userActivityDict);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(reset:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [[SENTSDK sharedInstance] reset:^{
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

RCT_EXPORT_METHOD(listenCrashEvents:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        __weak typeof(self) weakSelf = self;

        [[SENTSDK sharedInstance] setCrashListener:^(NSDate *date, CLLocation *lastKnownLocation){
            if(weakSelf.hasListeners) {
                NSDictionary *crashEventDict = [self convertCrashEventToDict:date lastKnownLocation:lastKnownLocation];
                [weakSelf sendEventWithName:@"SDKCrashEvent" body:crashEventDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(listenTripProfiles:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        __weak typeof(self) weakSelf = self;
        [[SENTSDK sharedInstance] setTripProfileHandler:^(SENTTripProcessingTripProfile *tripProfile) {
            NSDictionary *tripProfileDict = [self convertTripProfileToDict:tripProfile];
            if(weakSelf.hasListeners) {
                [weakSelf sendEventWithName:@"SDKTripProfile" body:tripProfileDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(updateTripProfileConfig:(NSDictionary *)config
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        if (config == nil || config[@"enableFullProfiling"] == nil) {
            reject(@"E_SDK_MISSING_PARAMS", @"enableFullProfiling is not provided", nil);
            return;
        }

        [[SENTSDK sharedInstance] setFullTripProfilingEnabled: [config[@"enableFullProfiling"] boolValue]];

        if (config[@"speedLimit"] != nil) {
            [[SENTSDK sharedInstance] setSpeedLimit: [config[@"speedLimit"] doubleValue]];
        }

        resolve(@(YES));
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
    @try {
        __weak typeof(self) weakSelf = self;

        [[SENTSDK sharedInstance] setVehicleCrashHandler:^(SENTVehicleCrashEvent *crashEvent) {
            if(weakSelf.hasListeners) {
                NSDictionary *crashEventDict = [self convertVehicleCrashEventToDict:crashEvent];
                [weakSelf sendEventWithName:@"VehicleCrashEvent" body:crashEventDict];
            }
        }];
        resolve(@(YES));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(invokeDummyVehicleCrash:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [[SENTSDK sharedInstance] invokeDummyVehicleCrash];
    resolve(@(YES));
}

RCT_EXPORT_METHOD(isVehicleCrashDetectionSupported:(NSString *)type
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    SENTTripType tripType;
    if ([type isEqualToString:@"TRIP_TYPE_SDK"]) {
        tripType = SENTTripTypeSDK;
    } else if ([type isEqualToString:@"TRIP_TYPE_EXTERNAL"]) {
        tripType = SENTTripTypeExternal;
    } else {
        return resolve(@(NO));
    }
    
    BOOL supported = [[SENTSDK sharedInstance] isVehicleCrashDetectionSupported:tripType];
    resolve(supported ? @(YES) : @(NO));
}

- (BOOL)isThirdPartyLinked {
    return [[SENTSDK sharedInstance] isThirdPartyLinked];
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

- (void)tripTimeoutReceived
{
    __weak typeof(self) weakSelf = self;
    [[SENTSDK sharedInstance] setTripTimeOutListener:^ {
        if (weakSelf.hasListeners) {
            [weakSelf sendEventWithName:@"SDKTripTimeout" body:nil];
        }
    }];
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
                           @"canDetect":@(status.canDetect),
                           @"isRemoteEnabled":@(status.isRemoteEnabled),
                           @"isLocationPermGranted":@(status.isLocationPermGranted),
                           @"isBgAccessPermGranted":@(status.isBgAccessPermGranted),
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

- (NSDictionary*)convertCrashEventToDict:(NSDate*)date lastKnownLocation:(CLLocation*)lastKnownLocation {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    double time = [date timeIntervalSince1970] * 1000;
    dict[@"time"] = @(time);


    if(lastKnownLocation != nil) {
        NSDictionary *location = @{
                                   @"latitude": @(lastKnownLocation.coordinate.latitude),
                                   @"longitude": @(lastKnownLocation.coordinate.longitude)
                                   };
        dict[@"lastKnownLocation"] = location;
    }
    return [dict copy];
}

- (NSString*)convertVehicleModeToString:(SENTTripProcessingVehicleMode) vehicleMode {
    switch (vehicleMode) {
        case SENTTripProcessingVehicleModeIdle:
            return @"IDLE";
        case SENTTripProcessingVehicleModeVehicle:
            return @"VEHICLE";
        case SENTTripProcessingVehicleModeNoVehicle:
            return @"NOT_VEHICLE";
        default:
            return @"UNKNOWN";
    }
}

- (NSDictionary*)convertTripProfileToDict:(SENTTripProcessingTripProfile*) tripProfile {

    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    dict[@"tripId"] = tripProfile.tripId;

    NSMutableArray *transportSegmentsArray = [[NSMutableArray alloc] init];

    if (tripProfile.transportSegments.count > 0) {
        for (SENTTripProcessingTransportSegment *transportSegment in tripProfile.transportSegments) {
            NSMutableDictionary *transportSegmentDict = [[NSMutableDictionary alloc] init];
            double startTime = [transportSegment.startDate timeIntervalSince1970] * 1000;
            transportSegmentDict[@"startTime"] = @(startTime);
            double endTime = [transportSegment.endDate timeIntervalSince1970] * 1000;
            transportSegmentDict[@"endTime"] = @(endTime);
            transportSegmentDict[@"distance"] = @(transportSegment.distance);
            transportSegmentDict[@"averageSpeed"] = @(transportSegment.averageSpeed);
            transportSegmentDict[@"topSpeed"] = @(transportSegment.topSpeed);
            transportSegmentDict[@"percentOfTimeSpeeding"] = @(transportSegment.speedingPercentage);
            transportSegmentDict[@"vehicleMode"] = [self convertVehicleModeToString:transportSegment.vehicleMode];

            NSMutableArray *hardEventsArray = [[NSMutableArray alloc] init];
            if (transportSegment.hardEvents.count > 0) {
                for (SENTTripProcessingHardEvent *hardEvent in transportSegment.hardEvents) {
                    NSMutableDictionary *hardEventDict = [[NSMutableDictionary alloc] init];
                    hardEventDict[@"magnitude"] = @(hardEvent.magnitude);
                    double timestamp = [hardEvent.date timeIntervalSince1970] * 1000;
                    hardEventDict[@"timestamp"] = @(timestamp);
                    [hardEventsArray addObject:hardEventDict];
                }
            }
            [transportSegmentDict setValue:hardEventsArray forKey:@"hardEvents"];
            [transportSegmentsArray addObject:transportSegmentDict];
        }
    }

    [dict setValue:transportSegmentsArray forKey:@"transportSegments"];

    return [dict copy];
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

@end
