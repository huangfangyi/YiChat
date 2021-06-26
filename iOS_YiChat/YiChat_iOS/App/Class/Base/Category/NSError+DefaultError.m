//
//  NSError+DefaultError.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NSError+DefaultError.h"

NSString *const NSCommonErrorDomain = @"NSCommonErrorDomain";

@implementation NSError (DefaultError)

+ (NSError *)errorCode:(NSCommonErrorCode)code{
    return [self errorCode:code userInfo:nil];
}

+ (NSError *)errorCode:(NSCommonErrorCode)code userInfo:(nullable NSDictionary*)userInfo{
    if (userInfo) {
        return [NSError errorWithDomain:NSCommonErrorDomain code:code userInfo:userInfo];
    }else{
        /*
         @{
         NSLocalizedDescriptionKey:@"返回的消息？",
         NSLocalizedFailureReasonErrorKey:@"失败原因",
         NSLocalizedRecoverySuggestionErrorKey:@"意见：恢复初始化",
         @"自定义":@"自定义的内容",
         }];
        */
        return [NSError errorWithDomain:NSCommonErrorDomain code:code userInfo: @{
                                                                                  NSLocalizedDescriptionKey:@"返回的消息？",
                                                                                  NSLocalizedFailureReasonErrorKey:@"失败原因",
                                                                                  NSLocalizedRecoverySuggestionErrorKey:@"意见：恢复初始化",
                                                                                  @"自定义":@"自定义的内容",
                                                                                  }];
               
    }
}

+ (NSError *)errorWithDes:(NSString *)des{
   
    if(des && [des isKindOfClass:[NSString class]]){
        return  [NSError errorWithDomain:NSCommonErrorDomain code:NSCommonErrorCodefailed userInfo: @{
                                                                                                      NSLocalizedFailureReasonErrorKey:des
                                                                                                      }];
    }
    else{
        return nil;
    }
    
}

- (NSString *)getErrorDes{
    if(self){
        return self.userInfo[NSLocalizedFailureReasonErrorKey];
    }
    else{
        return nil;
    }
}

@end
