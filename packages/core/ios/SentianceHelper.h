//
//  SentianceHelper.h
//  Pods
//
//  Created by Hassan Shakeel on 10/05/2022.
//

#import <Foundation/Foundation.h>
#import "SentianceCore.h"
#import <SENTSDK/SENTSDK.h>
#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "SentianceCore+Converter.h"

@interface SentianceHelper: NSObject

- (SENTInitializationResult *)initializeSDK:(NSString *)platformUrl  isAppSessionDataCollectionAllowed:(BOOL *)isAppSessionDataCollectionAllowed;

@end
