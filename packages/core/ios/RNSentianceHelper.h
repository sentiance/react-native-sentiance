//
//  RNSentianceHelper.h
//  RNSentianceCore
//
//  Created by Hassan Shakeel on 10/05/2022.
//

#import <Foundation/Foundation.h>
#import "RNSentianceCore.h"
#import <SENTSDK/SENTSDK.h>
#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "RNSentianceCore+Converter.h"

@interface RNSentianceHelper: NSObject

- (SENTInitializationResult *)initializeSDKWithLaunchOptions:(nullable NSDictionary *)launchOptions
  NS_SWIFT_NAME(initializeSDK(launchOptions:));

- (SENTInitializationResult *)initializeSDKWithPlatformUrl:(NSString *)platformUrl 
                  							 launchOptions:(nullable NSDictionary *)launchOptions
  NS_SWIFT_NAME(initializeSDK(platformUrl:launchOptions:));

- (SENTInitializationResult *)initializeSDKWithPlatformUrl:(NSString *)platformUrl
					     isAppSessionDataCollectionAllowed:(BOOL *)isAppSessionDataCollectionAllowed
                  							 launchOptions:(nullable NSDictionary *)launchOptions
  NS_SWIFT_NAME(initializeSDK(platformUrl:isAppSessionDataCollectionAllowed:launchOptions:));

- (void)enableDetectionsIfUserExists;

@end

