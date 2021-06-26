//
//  ZFChatRequestHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/11.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class HTMessage;
@class HTConversation;
@interface ZFChatRequestHelper : NSObject

+ (void)zfRequestHelper_LoginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion;

+ (void)zfRequestHelper_loadSingleChatMessageRecorderWithChatId:(NSString *)chatId lastestMessageTimeUnix:(NSInteger)timeUnix numsForPage:(NSInteger)nums completion:(void(^)(NSArray *messageArr,NSString *error))completion;

+ (void)zfRequestHelper_loadGroupChatMessageRecorderWithChatId:(NSString *)chatId userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle;

+ (void)zfRequestHelper_loadChatMessageRecorderWithChatId:(NSString *)chatId chatType:(NSInteger)chatType userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle;

+ (void)zfRequestHelper_loadConversations:(void(^)(NSArray <HTConversation *>* conversationArr))invocation;

+ (void)zfRequestHelper_logout;

+ (void)zfRequestUpdateUnixTimeWithTime:(NSUInteger)unixTime;

+ (void)zfRequest_uploadMessage:(HTMessage *)message chatType:(NSString *)chatType;

+ (void)zfRequest_uploadMessage:(HTMessage *)message chatType:(NSString *)chatType completion:(void(^)(BOOL isCompletion,id des))block;

+ (void)zfRequestCheckTokenInvocation:(void(^)(BOOL isNeedLoginOut))invocation;
@end

NS_ASSUME_NONNULL_END
