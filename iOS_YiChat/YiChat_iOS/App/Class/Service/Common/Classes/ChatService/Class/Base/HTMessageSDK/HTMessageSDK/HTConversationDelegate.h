/*!
 @header  HTConversationDelegate.h
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 */

#import <Foundation/Foundation.h>


/**
 ~cn:聊天会话代理 ~en:conversation delegate
 */
@protocol HTConversationDelegate <NSObject>

@optional
/**
 ~cn:聊天会话内容变更回调 ~en:a callback when conversation changed
 */
- (void)conversationChanged;

@end
