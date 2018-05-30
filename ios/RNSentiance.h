
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RNSentiance : RCTEventEmitter <RCTBridgeModule>

- (void)setConfig:(NSString*) appId secret:(NSString*) secret;

@end
