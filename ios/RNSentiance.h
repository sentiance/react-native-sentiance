
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <SENTSDK/SENTSDK.h>

@interface RNSentiance : RCTEventEmitter <RCTBridgeModule>
typedef void (^SdkStatusHandler)(SENTSDKStatus *status);
- (MetaUserLinker) getUserLinker;
- (SdkStatusHandler) getSdkStatusUpdateHandler;
- (void) initSDK:(NSString *)appId secret:(NSString *)secret baseURL:(NSString *)baseURL shouldStart:(BOOL)shouldStart resolver:(RCTPromiseResolveBlock)resolve  rejecter:(RCTPromiseRejectBlock)reject;
- (void) initSDKIfUserLinkingCompleted:(NSString *)appId secret:(NSString *)secret baseURL:(NSString *)baseURL shouldStart:(BOOL)shouldStart resolver:(RCTPromiseResolveBlock)resolve  rejecter:(RCTPromiseRejectBlock)reject;
- (BOOL) isThirdPartyLinked;
- (NSString *) getValueForKey:(NSString *)key value:(NSString *)defaultValue;
- (void) setValueForKey:(NSString *)key value:(NSString *)value;
- (void) startSDKWithStopEpochTimeMs:(nullable NSNumber*) stopEpochTimeMs resolver:(RCTPromiseResolveBlock _Nullable )resolve  rejecter:(RCTPromiseRejectBlock _Nullable )reject;
- (void) startSDK:(RCTPromiseResolveBlock _Nullable )resolve rejecter:(RCTPromiseRejectBlock _Nullable )reject;
- (BOOL) isNativeInitializationEnabled;
- (void) disableSDKNativeInitialization:(RCTPromiseResolveBlock _Nullable)resolve rejecter:(RCTPromiseRejectBlock _Nullable)reject;
- (void) enableSDKNativeInitialization:(RCTPromiseResolveBlock _Nullable)resolve rejecter:(RCTPromiseRejectBlock _Nullable)reject;
@end
