/*!
@header  HTMessageDelegate.h

@abstract 

@author  Created by 非夜 on 16/12/27.

@version 1.0 16/12/27 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
*/

#import <Foundation/Foundation.h>
#import "HTMessage.h"


/**
 消息代理
 */
@protocol HTMessageDelegate <NSObject>

@optional

/**
 接收到普通消息

 @param aMessages 普通消息数组
 */
- (void)didReceiveMessages:(NSArray *)aMessages;

/**
 接收到透传消息

 @param aCMDMessages 透传消息数组
 */
- (void)didReceiveCMDMessage:(NSArray *)aCMDMessages;

/**
 接收到时间修正消息
 
 @param aMessages 普通消息数组
 */
- (void)didReceiveSeriveTimeCorrectMessages:(NSArray *)aMessages;

@end
