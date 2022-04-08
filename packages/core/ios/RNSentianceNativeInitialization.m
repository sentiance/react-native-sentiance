#import "RNSentianceNativeInitialization.h"

@implementation RNSentianceNativeInitialization

+ (RNSentianceNativeInitialization *)sharedObject {
    static RNSentianceNativeInitialization *rnSentianceNativeInitialization = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        rnSentianceNativeInitialization = [[self alloc] init];
    });
    return rnSentianceNativeInitialization;
}

- (NSString *) getRNSentianceDirectoryPath:(NSString *) docDir {
    return [docDir stringByAppendingPathComponent:@"RNSentiance"];
}

- (NSString *) getNativeInitializationFilePath:(NSFileManager *)fileManager {
    NSString *docDir = [[fileManager URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject].path;
    NSString *sentianceDir = [self getRNSentianceDirectoryPath:docDir];
    NSString* path = [sentianceDir stringByAppendingPathComponent:@"rnsentiance_initialize_natively"];
    return path;
}

- (BOOL)isFlagFileExists {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString* path = [self getNativeInitializationFilePath:fileManager];
    if([fileManager fileExistsAtPath:path]) {
        return YES;
    } else {
        return NO;
    }
}

- (void)createFlagFile:(NSError **)error {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *docDir = [[fileManager URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject].path;
    NSString *sentianceDir = [self getRNSentianceDirectoryPath:docDir];
    NSString* path = [self getNativeInitializationFilePath:fileManager];
    
    [fileManager createDirectoryAtPath:sentianceDir withIntermediateDirectories:YES
                            attributes:@{NSFileProtectionKey:NSFileProtectionNone}
                                 error:error];
    
    if (*error == nil) {
        [fileManager createFileAtPath:path contents:nil attributes:@{NSFileProtectionKey:NSFileProtectionNone}];
    }
}

- (void)removeFlagFile:(NSError **)error {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString* path = [self getNativeInitializationFilePath:fileManager];
    [fileManager removeItemAtPath:path error:error];
}

@end
