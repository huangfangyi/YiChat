//
//  NSError+DefaultError.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
//domain
FOUNDATION_EXPORT NSString *const NSCommonErrorDomain;
/**错误状态码*/
typedef NS_ENUM(NSInteger,NSCommonErrorCode){
    NSCommonErrorCodeUnKnow = -1000,
    NSCommonErrorCodeSucc = -1001,
    NSCommonErrorCodefailed = -1002,
};

@interface NSError (DefaultError)

+ (NSError *)errorCode:(NSCommonErrorCode)code;

+ (NSError *)errorCode:(NSCommonErrorCode)code userInfo:(nullable NSDictionary*)userInfo;

+ (NSError *)errorWithDes:(NSString *)des;

- (NSString *)getErrorDes;
@end

NS_ASSUME_NONNULL_END
