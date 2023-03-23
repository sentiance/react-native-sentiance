//
//  RNSentianceSubscriptionMapping.h
//  Pods
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#ifndef RNSentianceSubscriptionMapping_h
#define RNSentianceSubscriptionMapping_h

#import "RNSentianceFoundation.h"
typedef NS_ENUM(NSUInteger, SENTSubscriptionType) {
    SENTSubscriptionTypeSingle,
    SENTSubscriptionTypeMultiple,
};

@interface RNSentianceSubscriptionDefinition : NSObject

@property (nonatomic, strong, readonly) NSString *eventType;
@property (nonatomic, strong, readonly) SentianceBlock nativeSubscribeLogic;
@property (nonatomic, strong, readonly) SentianceBlock nativeUnsubscribeLogic;
@property (nonatomic, readonly) SENTSubscriptionType subscriptionType;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithEventType:(NSString *)eventType
             nativeSubscribeLogic:(SentianceBlock)nativeSubscribeLogic
           nativeUnsubscribeLogic:(SentianceBlock)nativeUnsubscribeLogic
                 subscriptionType:(SENTSubscriptionType)subscriptionType NS_DESIGNATED_INITIALIZER;

@end

#endif /* RNSentianceSubscriptionMapping_h */
