
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <SENTSDK/SENTSDK.h>

@interface RNSentiance : RCTEventEmitter <RCTBridgeModule>
typedef void (^SdkStatusHandler)(SENTSDKStatus *status);
- (MetaUserLinker) getMetaUserLinker;
- (SdkStatusHandler) getSdkStatusUpdateHandler;
- (void) initSDK:(NSString *)appId secret:(NSString *)secret baseURL:(NSString *)baseURL shouldStart:(BOOL)shouldStart resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject;
@end
