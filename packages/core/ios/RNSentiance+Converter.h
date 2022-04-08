//
//  RNSentiance+Converter.h
//  RNSentiance
//
//  Created by Sebouh Aguehian on 10/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//
#import "RNSentiance.h"

@interface RNSentiance (Converter)

- (NSDictionary*)convertUserContextToDict:(SENTUserContext*) userContext;
- (NSMutableArray*)convertUserContextCriteriaToArray:(SENTUserContextUpdateCriteria)criteriaMask;

@end
