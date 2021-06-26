//
//  ZFChatFriendHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatHelper.h"
NS_ASSUME_NONNULL_BEGIN

@interface ZFChatFriendHelper : NSObject

+ (void)zfChatFriendHelperAddFriendWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked;

+ (void)zfChatFriendHelperAgreeFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked;

+ (void)zfChatFriendHelperDisagreeFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked;

+ (void)zfChatFriendHelperDeleteFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked;
    
//发送个人名片
+ (void)zfChatFriendHelperSendSingleCradWithUserId:(NSString *)userid  userNick:(NSString *)userNick userAvtar:(NSString *)avtar  completion:(void (^)(HTMessage *, NSError *))blocked;
@end

NS_ASSUME_NONNULL_END
