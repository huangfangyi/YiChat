//
//  HTConversation.h
//  HTMessage
//
//  Created by 非夜 on 16/10/31.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"

/**
 ~cn:聊天会话 ~en:conversation
 */
@interface HTConversation : NSObject

/**
 ~cn:聊天对象id ~en:chatter
 */
@property (nonatomic,strong)NSString * chatterId;

/**
 ~cn:一条会话的未读消息数量 ~en:a conversation on the number of unread messages
 
 */
@property (nonatomic,assign)NSInteger unreadMessageCount;

/**
 ~cn:一条会话对应的最后一条聊天消息 ~en:a conversation last a chat message
 
 */
@property (nonatomic,strong)HTMessage *lastMessage;

/**
 ~cn:会话的拓展 ~en:the extionsion for conversation
 */
@property (nonatomic,strong)NSDictionary *conversationExt;

@end
