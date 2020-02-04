#import "RNSentiance.h"
#import <SENTSDK/SENTSDK.h>
#import <SENTSDK/SENTSDKStatus.h>
#import <SENTSDK/SENTPublicDefinitions.h>

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
    return @[@"SDKStatusUpdate", @"SDKTripTimeout", @"SDKUserLink", @"SDKUserActivityUpdate"];
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
         baseURL:(NSString *)baseURL
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

        if (baseURL.length > 0) {
            config.baseURL = baseURL;
        }

        [[SENTSDK sharedInstance] initWithConfig:config success:^{
            if (shouldStart) {
                [weakSelf startSDK:resolve rejecter:reject];
            }
            else if (resolve) {
                resolve(nil);
            }
        } failure:^(SENTInitIssue issue) {
            if (reject) {
                reject(@"", [weakSelf convertInitIssueToString: issue], nil);
            }
        }];
    } @catch (NSException *e) {
        if (reject) {
            reject(e.name, e.reason, nil);
        }
    }
}

- (void) startSDK:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject {
    __block BOOL resolved = NO;

    @try {
        __weak typeof(self) weakSelf = self;
        [[SENTSDK sharedInstance] start:^(SENTSDKStatus* status) {
            NSLog(@"SDK started properly.");
            if (resolve && !resolved) {
                resolve([weakSelf convertSdkStatusToDict:status]);
                resolved = YES;
            }
            if (weakSelf.hasListeners) {
                [weakSelf sendEventWithName:@"SDKStatusUpdate" body:[weakSelf convertSdkStatusToDict:status]];
            }
        }];
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
    __block BOOL timeout = false;

    self.userLinker = ^(NSString *installId, void (^linkSuccess)(void),
                        void (^linkFailed)(void)) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

            //set timeout for listeners to set
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 10.0 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
                timeout = YES;
            });

            //wait for JS listener
            while(!weakSelf.hasListeners && !timeout){}

            if(timeout){
                linkFailed();
            }else{
                weakSelf.userLinkSuccess = linkSuccess;
                weakSelf.userLinkFailed = linkFailed;
                [weakSelf sendEventWithName:@"SDKUserLink" body:[weakSelf convertInstallIdToDict:installId]];
            }
        });
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
                  baseURL:(NSString *)baseURL
                  shouldStart:(BOOL)shouldStart
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    if (appId == nil || secret == nil) {
        reject(@"", @"INVALID_CREDENTIALS", nil);
        return;
    }
    [self initSDK:appId secret:secret baseURL:baseURL shouldStart:shouldStart resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(initWithUserLinkingEnabled:(NSString *)appId
                  secret:(NSString *)secret
                  baseURL:(NSString *)baseURL
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

RCT_EXPORT_METHOD(stop:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTSDK* sdk = [SENTSDK sharedInstance];
        [sdk stop];
        resolve(nil);
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
            reject(@"", @"Couldn't access token", nil);
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
            @throw([NSException exceptionWithName:@"NilException" reason:@"Atempt to insert nil object" userInfo:nil]);
        }

        [[SENTSDK sharedInstance] addUserMetadataField:label value:value];
        resolve(nil);
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
            @throw([NSException exceptionWithName:@"NilException" reason:@"Atempt to insert nil object" userInfo:nil]);
        }

        [[SENTSDK sharedInstance] removeUserMetadataField:label];
        resolve(nil);
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
            @throw([NSException exceptionWithName:@"NilException" reason:@"Atempt to insert nil object" userInfo:nil]);
        }

        [[SENTSDK sharedInstance] addUserMetadataFields:metadata];
        resolve(nil);
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
            resolve(nil);
        } failure:^(SENTSDKStatus *status) {
            reject(@"", @"Couldn't start trip", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(stopTrip:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        [[SENTSDK sharedInstance] stopTrip:^{
            resolve(nil);
        } failure:^(SENTSDKStatus *status) {
            reject(@"", @"Couldn't stop trip", nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(isTripOngoing:(NSInteger)type
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        SENTTripType tripType = type;
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
            resolve(nil);
        } failure: ^ {
            reject(@"", @"Couldn't submit all detections", nil);
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
    @try {
        NSLog(@"This is an Android only method.");
        resolve(nil);
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(deleteKeychainEntries:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self deleteAllKeysForSecClass:kSecClassGenericPassword];
    [self deleteAllKeysForSecClass:kSecClassInternetPassword];
    [self deleteAllKeysForSecClass:kSecClassCertificate];
    [self deleteAllKeysForSecClass:kSecClassKey];
    [self deleteAllKeysForSecClass:kSecClassIdentity];
}

RCT_EXPORT_METHOD(listenUserActivityUpdates)
{
    __weak typeof(self) weakSelf = self;
    [[SENTSDK sharedInstance] setUserActivityListener:^(SENTUserActivity *userActivity) {
        NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
        if(weakSelf.hasListeners) {
            [weakSelf sendEventWithName:@"SDKUserActivityUpdate" body:userActivityDict];
        }
    }];
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
    [self reset:resolve rejecter:reject];
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
    if(userActivityType.length > 0) {
        [dict setObject:userActivityType forKey:@"type"];
    }


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
    if (installId.length == 0) {
        return @{};
    }
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
    } else
        return @"";
}

- (NSString*)convertQuotaStatusToString:(SENTQuotaStatus) status {
    switch (status) {
        case SENTQuotaStatusOK:
            return @"OK";
        case SENTQuotaStatusWarning:
            return @"WARNING";
        case SENTQuotaStatusExceeded:
            return @"EXCEEDED";
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
    }
}

- (NSString*)convertTripTypeToString:(SENTTripType) tripType {
    switch (tripType) {
        case SENTTripTypeSDK:
            return @"TRIP_TYPE_SDK";
        case SENTTripTypeExternal:
            return @"TRIP_TYPE_EXTERNAL";
    }
}
@end
