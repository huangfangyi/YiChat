//
//  YiChatServiceClient.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatHelper.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatServiceClient : NSObject

+ (id)defaultChatClient;

- (void)yiChatServiceClient_initial;

- (void)yiChatServiceClient_AutoLogin:(void(^)(BOOL isSuccess))invocation;

- (void)yiChatServiceClient_loginWithUserName:(NSString *)userName
                                     password:(NSString *)password
                                   invocation:(void(^)(BOOL state))invocation;

- (void)loginOut;

- (void)dealUnreadMessage;

- (void)yichatServiceClient_weichatLogin:(void(^)(BOOL isSuccess,NSString *error))invocation;

- (void)yichatServiceClient_qqLogin:(void(^)(BOOL isSuccess,NSString *error))invocation;

-(void)updateJGJushTagWithGroup;

-(void)checkVersionUpdateState:(void(^)(BOOL state))invocation;

-(void)setJGJushTagWithGroupAdd:(BOOL)isAdd;
@end

NS_ASSUME_NONNULL_END
