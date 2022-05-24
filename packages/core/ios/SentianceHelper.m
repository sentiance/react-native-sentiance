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

- (SENTInitializationResult *)initializeSDKWithLaunchOptions:(nullable NSDictionary *)launchOptions {
    return [self initializeSDKWithPlatformUrl:nil isAppSessionDataCollectionAllowed:NO launchOptions:nil];
}

- (SENTInitializationResult *)initializeSDKWithPlatformUrl:(NSString *)platformUrl 
                                             launchOptions:(nullable NSDictionary *)launchOptions {
    return [self initializeSDKWithPlatformUrl:platformUrl isAppSessionDataCollectionAllowed:NO launchOptions:launchOptions];
}


- (SENTInitializationResult *)initializeSDKWithPlatformUrl:(NSString *)platformUrl
                         isAppSessionDataCollectionAllowed:(BOOL *)isAppSessionDataCollectionAllowed
                                             launchOptions:(nullable NSDictionary *)launchOptions {
    SENTOptions *options = [[SENTOptions alloc] init];
    options.platformUrl = platformUrl;
    options.isAppSessionDataCollectionAllowed = isAppSessionDataCollectionAllowed;
    return [[Sentiance sharedInstance] initializeWithOptions:options launchOptions:launchOptions];
}

@end

