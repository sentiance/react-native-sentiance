//
//  SentianceHelper.m
//  DoubleConversion
//
//  Created by Hassan Shakeel on 10/05/2022.
//

#import <Foundation/Foundation.h>
#import "SentianceHelper.h"
#import <SENTSDK/SENTSDK.h>
#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "SentianceCore+Converter.h"

@implementation SentianceHelper


- (SENTInitializationResult *)initializeSDK:(NSString * _Nullable)platformUrl  isAppSessionDataCollectionAllowed:(BOOL * _Nullable)isAppSessionDataCollectionAllowed {
    
    SENTOptions *options = [[SENTOptions alloc] init];
    options.platformUrl = platformUrl;
    options.isAppSessionDataCollectionAllowed = isAppSessionDataCollectionAllowed;
    return [[Sentiance sharedInstance] initializeWithSENTOptions:options];
}

@end
