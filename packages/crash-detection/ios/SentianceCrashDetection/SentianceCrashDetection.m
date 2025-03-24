//
//  SentianceCrashDetection.h
//  SentianceCrashDetection
//
//  Created by Mohammed Aouf Zouag on 18/03/2025.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(SentianceCrashDetection, NSObject)

RCT_EXTERN_METHOD(isVehicleCrashDetectionSupported:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(invokeDummyVehicleCrash:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

@end
