@interface SentDataManager : NSObject

@property (nonatomic, retain) NSString *APPID;
@property (nonatomic, retain) NSString *SECRET;

+ (SentDataManager *)sharedInstance;

@end
