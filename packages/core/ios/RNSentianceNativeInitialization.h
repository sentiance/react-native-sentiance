@interface RNSentianceNativeInitialization: NSObject
+ (RNSentianceNativeInitialization *)sharedObject;
- (BOOL) isFlagFileExists;
- (void) createFlagFile:(NSError **)error;
- (void) removeFlagFile:(NSError **)error;
@end
