
#import "SentDataManager.h"

@implementation SentDataManager

static id _instance;

@synthesize APPID;
@synthesize SECRET;

+ (instancetype)sharedInstance
{
    static dispatch_once_t once;
    static id sharedInstance;

    dispatch_once(&once, ^{
        sharedInstance = [[self alloc] init];
    });

    return sharedInstance;
}

@end