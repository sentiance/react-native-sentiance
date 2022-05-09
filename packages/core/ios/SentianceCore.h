
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <SENTSDK/SENTSDK.h>

@interface SentianceCore : RCTEventEmitter <RCTBridgeModule, SENTUserContextDelegate>
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

@end
