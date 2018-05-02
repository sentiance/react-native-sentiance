
#import "RNSentiance.h"
#import <SENTTransportDetectionSDK/SENTSDK.h>
#import <SENTTransportDetectionSDK/SENTConfig.h>
#import <SENTTransportDetectionSDK/SENTSDKStatus.h>
#import <SENTTransportDetectionSDK/SENTInitIssue.h>
#import <SENTTransportDetectionSDK/SENTTrip.h>
#import <SENTTransportDetectionSDK/SENTToken.h>

@implementation RNSentiance
{
  bool hasListeners;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"SDKStatusUpdate", @"TripTimeout"];
}

// Will be called when this module's first listener is added.
-(void)startObserving {
  hasListeners = YES;
  // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
  hasListeners = NO;
  // Remove upstream listeners, stop unnecessary background tasks
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
    SENTConfig *config = [[SENTConfig alloc] initWithAppId:appId secret:secret launchOptions:@{}];
    [config setDidReceiveSdkStatusUpdate:^(SENTSDKStatus *status) {
      if (hasListeners) {
        [self sendEventWithName:@"SDKStatusUpdate" body:[self convertSdkStatusToDict:status]];
      }
    }];

    [[SENTSDK sharedInstance] initWithConfig:config success:^{
      resolve(nil);
    } failure:^(SENTInitIssue issue) {
      reject(@"", [self convertInitIssueToString: issue], nil);
    }];
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(start:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    [[SENTSDK sharedInstance] start:^(SENTSDKStatus* status) {
      if ([status startStatus] == SENTStartStatusStarted) {
        // SDK started properly.
        resolve(@"STARTED");
      } else if ([status startStatus] == SENTStartStatusPending) {
        // Something prevented the SDK to start properly. Once fixed, the SDK will start automatically.
        resolve(@"PENDING");
      } else {
        // SDK did not start.
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
    bool isInitialized = [[SENTSDK sharedInstance] isInitialised];
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
  @try {
    [[SENTSDK sharedInstance] getUserAccessToken:^(SENTToken* token) {
      NSMutableDictionary* dict = [self convertTokenToDict:token];
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

    [[SENTSDK sharedInstance] addUserMetadataField:label value:value success:^() {
      resolve(nil);
    } failure:^() {
      reject(@"", @"Couldn't add user metadata field", nil);
    }];
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(removeUserMetadataField:(NSString *)label
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    if (label == nil) {
      @throw([NSException exceptionWithName:@"NilException" reason:@"Atempt to insert nil object" userInfo:nil]);
    }

    [[SENTSDK sharedInstance] removeUserMetadataField:label success:^() {
      resolve(nil);
    } failure:^() {
      reject(@"", @"Couldn't remove user metadata field", nil);
    }];
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(addUserMetadataFields:(NSDictionary *)metadata
                  resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    if (metadata == nil) {
      @throw([NSException exceptionWithName:@"NilException" reason:@"Atempt to insert nil object" userInfo:nil]);
    }

    [[SENTSDK sharedInstance] addUserMetadataFields:metadata success:^() {
      resolve(nil);
    } failure:^() {
      reject(@"", @"Couldn't add user metadata fields", nil);
    }];
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
    [[SENTSDK sharedInstance] startTrip:metadata transportModeHint:mode];
    resolve(nil);
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(stopTrip:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    SENTTrip* tripObj = [[SENTSDK sharedInstance] stopTrip];
    resolve([self convertTripToDict:tripObj]);
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(isTripOngoing:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    bool isTripOngoing = [[SENTSDK sharedInstance] isTripOngoing];
    resolve(@(isTripOngoing));
  } @catch (NSException *e) {
    reject(e.name, e.reason, nil);
  }
}

RCT_EXPORT_METHOD(submitDetections:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    [[SENTSDK sharedInstance] submitDetections:^(BOOL status, NSError* error) {
      if (status) {
        resolve(nil);
      } else {
        reject(@"", @"Couldn't submit all detections", nil);
      }
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

- (void)tripTimeoutReceived
{
  [[SENTSDK sharedInstance] setTripTimeOutListener:^(SENTTrip *trip) {
    if (hasListeners) {
      [self sendEventWithName:@"TripTimeout" body:[self convertTripToDict:trip]];
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

- (NSMutableDictionary*)convertTokenToDict:(SENTToken*) token {
  NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

  if (token == nil) {
    return dict;
  }

  NSTimeInterval interval = [token.expiryDate timeIntervalSince1970];
  NSInteger time = interval * 1000;

  [dict setValue:token.tokenId forKey:@"tokenId"];
  [dict setValue:[NSNumber numberWithLongLong:(long)time] forKey:@"expiryDate"];

  return dict;
}

- (NSMutableDictionary*)convertTripToDict:(SENTTrip*) trip {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];

    if (trip == nil)
        return dict;

    [dict setValue:trip.tripId forKey:@"tripId"];
    [dict setValue:[NSNumber numberWithLongLong:trip.start] forKey:@"start"];
    [dict setValue:[NSNumber numberWithLongLong:trip.stop] forKey:@"stop"];
    [dict setValue:[NSNumber numberWithLongLong:trip.distance] forKey:@"distance"];
    [dict setValue:trip.pWaypointsArray forKey:@"waypoints"];

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
