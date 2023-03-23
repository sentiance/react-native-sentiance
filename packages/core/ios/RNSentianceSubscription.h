//
//  Subscription.h
//  Pods
//
//  Created by Mohammed Aouf Zouag on 27/3/2023.
//

#import <Foundation/Foundation.h>


@interface RNSentianceSubscription : NSObject

@property (nonatomic, readonly) NSInteger subscriptionId;
@property (nonatomic, strong, readonly) NSString *eventType;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithEventType:(NSString*)eventType subscriptionId:(NSInteger)subscriptionId NS_DESIGNATED_INITIALIZER;

@end
