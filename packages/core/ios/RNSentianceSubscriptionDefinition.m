//
//  RNSentianceSubscriptionMapping.m
//  DoubleConversion
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#import <Foundation/Foundation.h>
#import "RNSentianceSubscriptionDefinition.h"

@interface RNSentianceSubscriptionDefinition ()

@property (nonatomic, strong, readwrite) NSString *eventType;
@property (nonatomic, strong, readwrite) SentianceBlock nativeSubscribeLogic;
@property (nonatomic, strong, readwrite) SentianceBlock nativeUnsubscribeLogic;
@property (nonatomic, readwrite) SENTSubscriptionType subscriptionType;

@end

@implementation RNSentianceSubscriptionDefinition

- (instancetype)initWithEventType:(NSString *)eventType
             nativeSubscribeLogic:(SentianceBlock)nativeSubscribeLogic
           nativeUnsubscribeLogic:(SentianceBlock)nativeUnsubscribeLogic
                 subscriptionType:(SENTSubscriptionType)subscriptionType {
    self = [super init];
    if (self) {
        self.eventType = eventType;
        self.nativeSubscribeLogic = nativeSubscribeLogic;
        self.nativeUnsubscribeLogic = nativeUnsubscribeLogic;
        self.subscriptionType = subscriptionType;
    }
    return self;
}

@end
