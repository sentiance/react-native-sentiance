#import "RNSentiance.h"
#import <SENTSDK/SENTSDK.h>
#import <SENTSDK/SENTSDKStatus.h>
#import <SENTSDK/SENTPublicDefinitions.h>

@interface RNSentiance()

@property (nonatomic, strong) void (^metaUserLinkSuccess)(void);
@property (nonatomic, strong) void (^metaUserLinkFailed)(void);
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
    return @[@"SDKStatusUpdate", @"TripTimeout", @"SDKMetaUserLink", @"UserActivity"];
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

- (void)initializeSDK:(NSString *)appId secret:(NSString *)secret baseURL:(NSString *)baseURL resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject; {
    if (appId == nil || secret == nil) {
        reject(@"", @"INVALID_CREDENTIALS", nil);
        return;
    }
    @try {
        __weak typeof(self) weakSelf = self;
        MetaUserLinker metaUserlink = ^(NSString *installId, void (^linkSuccess)(void),
                                        void (^linkFailed)(void)) {
            if (weakSelf.hasListeners) {
                weakSelf.metaUserLinkSuccess = linkSuccess;
                weakSelf.metaUserLinkFailed = linkFailed;
                [weakSelf sendEventWithName:@"SDKMetaUserLink" body:[self convertInstallIdToDict:installId]];
            } else {
                linkFailed();
            }
        };
        SENTConfig *config = [[SENTConfig alloc] initWithAppId:appId secret:secret link:metaUserlink launchOptions:@{}];
        if (baseURL != nil) {
            config.baseURL = baseURL;
        }
        [config setDidReceiveSdkStatusUpdate:^(SENTSDKStatus *status) {
            if (weakSelf.hasListeners) {
                [weakSelf sendEventWithName:@"SDKStatusUpdate" body:[self convertSdkStatusToDict:status]];
            }
        }];

        [[SENTSDK sharedInstance] initWithConfig:config success:^{
            resolve(nil);
        } failure:^(SENTInitIssue issue) {
            reject(@"", [weakSelf convertInitIssueToString: issue], nil);
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(metaUserLinkCallback:(BOOL)success) {
    if (success) {
        self.metaUserLinkSuccess();
    } else {
        self.metaUserLinkFailed();
    }
}

RCT_EXPORT_METHOD(initWithBaseUrl:(NSString *)appId
                  secret:(NSString *)secret
                  baseURL:(NSString *)baseURL
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    __weak typeof(self) weakSelf = self;
    [weakSelf initializeSDK:appId secret:secret baseURL:baseURL resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(init:(NSString *)appId
                  secret:(NSString *)secret
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    __weak typeof(self) weakSelf = self;
    [weakSelf initializeSDK:appId secret:secret baseURL:nil resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(start:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        __weak typeof(self) weakSelf = self;
        [[SENTSDK sharedInstance] start:^(SENTSDKStatus* status) {
            if ([status startStatus] == SENTStartStatusStarted) {
                NSLog(@"SDK started properly.");
                resolve([weakSelf convertSdkStatusToDict:status]);
            } else if ([status startStatus] == SENTStartStatusPending) {
                NSLog(@"Something prevented the SDK to start properly. Once fixed, the SDK will start automatically.");
                resolve([weakSelf convertSdkStatusToDict:status]);
            } else {
                NSLog(@"SDK did not start.");
                reject(@"", @"SDK did not start.", nil);
            }
        }];
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
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


//Deprecated method use -(SENTSDKInitState)getInitState;
RCT_EXPORT_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        BOOL isInitialized = [[SENTSDK sharedInstance] isInitialised];
        resolve(@(isInitialized));
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
            [weakSelf sendEventWithName:@"TripTimeout" body:nil];
        }
    }];
}

- (void)userActivityReceived {
    __weak typeof(self) weakSelf = self;
    [[SENTSDK sharedInstance] setUserActivityListerner:^(SENTUserActivity *userActivity) {
        NSDictionary *userActivityDict = [self convertUserActivityToDict:userActivity];
        if(weakSelf.hasListeners) {
            [weakSelf sendEventWithName:@"UserActivity" body:userActivityDict];
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
    if(userActivity.tripInfo) {
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
    if(userActivity.stationaryInfo) {
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
