//
//  ZFRequestManage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFRequestManage : NSObject

+ (id)defaultManager;

- (void)zfRequuest_LoginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion;

- (void)zfRequuest_loadSingleChatMessageRecorderWithChatId:(NSString *)chatId lastestMessageTimeUnix:(NSInteger)timeUnix numsForPage:(NSInteger)nums completion:(void(^)(NSArray *messageArr,NSString *error))completion;

- (void)zfRequuest_loadGroupChatMessageRecorderWithChatId:(NSString *)chatId userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle;

- (void)zfRequest_connectionListWithPage:(NSInteger)page success:(void(^)(NSArray *originDataArr,NSArray *listArr))successHandle fail:(void(^)(NSString *error))failHandle;

- (void)zfRequest_logout;
@end

NS_ASSUME_NONNULL_END
