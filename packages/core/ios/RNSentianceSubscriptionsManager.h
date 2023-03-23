//
//  SentianceSubscriptionsManager.h
//  Pods
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#ifndef SentianceSubscriptionsManager_h
#define SentianceSubscriptionsManager_h

#import "RNSentianceFoundation.h"
#import "RNSentianceSubscriptionDefinition.h"
#import <SENTSDK/SENTSDK.h>

@interface RNSentianceSubscriptionsManager : NSObject

- (void)addSupportedSubscriptionForEventType:(NSString *)eventType
                        nativeSubscribeLogic:(SentianceBlock)nativeSubscribeLogic
                      nativeUnsubscribeLogic:(SentianceBlock)nativeUnsubscribeLogic
                            subscriptionType:(SENTSubscriptionType)subscriptionType;
- (void)addSubscriptionForEventType:(NSString *)eventType subscriptionId:(NSInteger)subscriptionId;
- (void)removeSubscriptionForId:(NSInteger)subscriptionId eventType:(NSString *)eventType;

@end

#endif /* SentianceSubscriptionsManager_h */
