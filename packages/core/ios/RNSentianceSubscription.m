//
//  Subscription.m
//  DoubleConversion
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#import <Foundation/Foundation.h>
#import "RNSentianceSubscription.h"


@interface RNSentianceSubscription ()

@property (nonatomic, readwrite) NSInteger subscriptionId;
@property (nonatomic, strong, readwrite) NSString *eventType;

@end

@implementation RNSentianceSubscription

- (instancetype)initWithEventType:(NSString *)eventType subscriptionId:(NSInteger)subscriptionId {
    self = [super init];
    if (self) {
        self.eventType = eventType;
        self.subscriptionId = subscriptionId;
    }
    return self;
}

@end
