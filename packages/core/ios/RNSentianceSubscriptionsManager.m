//
//  SentianceSubscriptionsManager.m
//  DoubleConversion
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#import <Foundation/Foundation.h>
#import "RNSentianceSubscriptionsManager.h"
#import "RNSentianceSubscriptionDefinition.h"
#import "RNSentianceSubscription.h"

@interface RNSentianceSubscriptionsManager()

@property (nonatomic, strong) NSMutableArray<RNSentianceSubscription*> *subscriptions;
@property (nonatomic, strong) NSMutableDictionary<NSString *, RNSentianceSubscriptionDefinition *> *supportedSubscriptions;

@end

@implementation RNSentianceSubscriptionsManager

- (instancetype)init {
    self = [super init];
    _subscriptions = [[NSMutableArray alloc] init];
    _supportedSubscriptions = [[NSMutableDictionary alloc] init];
    return self;
}

- (void)addSupportedSubscriptionForEventType:(NSString *)eventType
                        nativeSubscribeLogic:(SentianceBlock)nativeSubscribeLogic
                      nativeUnsubscribeLogic:(SentianceBlock)nativeUnsubscribeLogic
                            subscriptionType:(SENTSubscriptionType)subscriptionType {
    @synchronized (_subscriptions) {
        if (_supportedSubscriptions[eventType] == nil) {
            RNSentianceSubscriptionDefinition *definition =
                [[RNSentianceSubscriptionDefinition alloc]initWithEventType:eventType
                                                             nativeSubscribeLogic:nativeSubscribeLogic
                                                           nativeUnsubscribeLogic:nativeUnsubscribeLogic
                                                           subscriptionType:subscriptionType];
            _supportedSubscriptions[eventType] = definition;
        }
    }
}

- (void)addSubscriptionForEventType:(NSString *)eventType subscriptionId:(NSInteger)subscriptionId {
    RNSentianceSubscriptionDefinition *definition = _supportedSubscriptions[eventType];
    
    if (definition != nil) {
        if ([self _shouldSubscribeNatively:definition]) {
            definition.nativeSubscribeLogic();
        }

        RNSentianceSubscription *subscription = [[RNSentianceSubscription alloc]initWithEventType:eventType
                                                                                   subscriptionId:subscriptionId];
        
        @synchronized (_subscriptions) {
            [_subscriptions addObject:subscription];
        }
    }
}

- (void)removeSubscriptionForId:(NSInteger)subscriptionId eventType:(NSString *)eventType {
    RNSentianceSubscriptionDefinition *definition = _supportedSubscriptions[eventType];
    
    if (definition != nil) {
        RNSentianceSubscription *subscription = [self _getExistingSubscriptionWithId:subscriptionId eventType:eventType];
        if (subscription != nil) {
            @synchronized (_subscriptions) {
                [_subscriptions removeObject:subscription];
            }
            
            if ([self _shouldUnsubscribeNatively:definition]) {
                definition.nativeUnsubscribeLogic();
            }
        }
    }
}

- (nullable RNSentianceSubscription *)_getExistingSubscriptionWithId:(NSInteger)subscriptionId eventType:(NSString *)eventType {
    @synchronized (_subscriptions) {
        for (RNSentianceSubscription *subscription in _subscriptions) {
            if (subscription.subscriptionId == subscriptionId && [eventType isEqualToString:subscription.eventType]) {
                return subscription;
            }
        }
        return nil;
    }
}

- (BOOL)_shouldSubscribeNatively:(RNSentianceSubscriptionDefinition*)definition {
    return [self _shouldInvokeNativeLogic:definition];
}

- (BOOL)_shouldUnsubscribeNatively:(RNSentianceSubscriptionDefinition*)definition {
    return [self _shouldInvokeNativeLogic:definition];
}

- (BOOL)_shouldInvokeNativeLogic:(RNSentianceSubscriptionDefinition*)definition {
    if (definition.subscriptionType == SENTSubscriptionTypeMultiple) {
        return true;
    }

    return definition.subscriptionType == SENTSubscriptionTypeSingle && [self _subscriptionExists:definition.eventType] == NO;
}

- (BOOL)_subscriptionExists:(NSString *)eventType {
    @synchronized (_subscriptions) {
        for (RNSentianceSubscription *subscription in _subscriptions) {
            if ([subscription.eventType isEqualToString:eventType]) {
                return YES;
            }
        }
        return NO;
    }
}

@end
