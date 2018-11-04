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
    return @[@"SDKStatusUpdate", @"TripTimeout", @"SDKMetaUserLink"];
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

RCT_EXPORT_METHOD(metaUserLinkCallback:(BOOL)success) {
    if (success) {
        self.metaUserLinkSuccess();
    } else {
        self.metaUserLinkFailed();
    }
}

RCT_EXPORT_METHOD(init:(NSString *)appId
                  secret:(NSString *)secret
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
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

RCT_EXPORT_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        BOOL isInitialized = [[SENTSDK sharedInstance] isInitialised];
        resolve(@(isInitialized));
    } @catch (NSException *e) {
        reject(e.name, e.reason, nil);
    }
}

RCT_EXPORT_METHOD(getSdkStatus:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSMutableDictionary* dict = [self convertSdkStatusToDict:[[SENTSDK sharedInstance] getSdkStatus]];
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
            NSMutableDictionary* dict = [weakSelf convertTokenToDict:token];
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
        }
                                    failure:^(SENTSDKStatus *status) {
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

- (NSMutableDictionary*)convertSdkStatusToDict:(SENTSDKStatus*) status {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (status == nil) {
        return dict;
    }

    [dict setValue:[self convertStartStatusToString:status.startStatus] forKey:@"startStatus"];
    [dict setValue:@(status.canDetect) forKey:@"canDetect"];
    [dict setValue:@(status.isRemoteEnabled) forKey:@"isRemoteEnabled"];
    [dict setValue:@(status.isLocationPermGranted) forKey:@"isLocationPermGranted"];
    [dict setValue:@(status.isBgAccessPermGranted) forKey:@"isBgAccessPermGranted"];
    [dict setValue:@(status.isAccelPresent) forKey:@"isAccelPresent"];
    [dict setValue:@(status.isGyroPresent) forKey:@"isGyroPresent"];
    [dict setValue:@(status.isGpsPresent) forKey:@"isGpsPresent"];
    [dict setValue:[self convertQuotaStatusToString:status.wifiQuotaStatus] forKey:@"wifiQuotaStatus"];
    [dict setValue:[self convertQuotaStatusToString:status.mobileQuotaStatus] forKey:@"mobileQuotaStatus"];
    [dict setValue:[self convertQuotaStatusToString:status.diskQuotaStatus] forKey:@"diskQuotaStatus"];

    return dict;
}

- (NSMutableDictionary*)convertInstallIdToDict:(NSString*) installId {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (installId == nil) {
        return dict;
    }
    [dict setValue:installId forKey:@"installId"];
    return dict;
}


- (NSMutableDictionary*)convertTokenToDict:(NSString*) token {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (token == nil) {
        return dict;
    }
    [dict setValue:token forKey:@"tokenId"];
    return dict;
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

@end
