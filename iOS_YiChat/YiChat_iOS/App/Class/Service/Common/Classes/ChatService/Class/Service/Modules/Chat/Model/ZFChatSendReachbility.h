//
//  ZFChatSendReachbility.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatSendReachbility : NSObject

@property (nonatomic,copy) void(^ZFChatSendReachbilityCanSendMsg)(void);

- (void)addSendTask;

- (void)clean;

@end

NS_ASSUME_NONNULL_END
