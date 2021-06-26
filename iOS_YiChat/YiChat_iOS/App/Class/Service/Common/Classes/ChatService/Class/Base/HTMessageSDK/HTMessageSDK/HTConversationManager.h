/*!
 @header  HTConversationManager.h
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import "HTConversationDelegate.h"
#import "HTConversation.h"


/**
 ~cn:会话管理 ~en:conversation manager
 */
@interface HTConversationManager : NSObject

/**
 ~cn:缓存的会话列表 ~en:conversation list cached
 */
@property(nonatomic,strong)NSMutableArray * conversations;

/**
 ~cn:添加回调代理 ~en:add a callback delegate
 
 @param aDelegate ~cn:要添加的代理 ~en:to add a delegate
 @param aQueue ~cn:执行代理方法的队列 ~en:the queue of executive agent method
 
 */
- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)aQueue;

/**
 ~cn:移除回调代理 ~en:remove a delegate
 
 @param aDelegate ~cn:要移除的代理 ~en:to remove a delegate
 */
- (void)removeDelegate:(id)aDelegate;

/**
 ~cn:根据会话id从数据库中删除一条回话的所有对应的聊天记录 ~en:according to the conversationId is removed from the database a reply of all the corresponding chat record
 
 @param aChatterId ~cn:会话id ~en:conversationId
 */
- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)aChatterId;

/**
 ~cn:获取一条会话对应的聊天记录 ~en:get all messages in a conversation
 
 @param aChatterId ~cn:会话id ~en:conversationId
 @param aTimestamp ~cn:根据聊天消息的时间信息查询聊天记录,13位毫米级时间戳 ~en:according to the time the chat message information query chat records, 13 mm level timestamp
 @param aOffset ~cn:每次查询多少条聊天记录 ~en:how many messages per query
 
 @param aBlocked ~cn:查询结果 ~en:query result
 */
- (void)fetchNormessagesByChatterId:(NSString *)aChatterId andTimestamp:(NSInteger)aTimestamp withOffsetSize:(NSInteger )aOffset completion:(void(^)(NSArray *result))aBlocked;

/**
 ~cn:根据会话id删除一条会话包括是否删除该会话对应的所有聊天记录 ~en:according to the conversationId to remove a conversation include whether to remove the session all chat record
 
 @param aChatterId ~cn:会话id ~en:conversationId
 @param aIsClean ~cn:是否删除该会话对应的所有聊天记录 ~en:whether to remove the conversation all chat record
 
 */
- (void)deleteOneConversationWithChatterId:(NSString *)aChatterId isCleanAllHistoryMessage:(BOOL)aIsClean;

/**
 ~cn:更新一条会话 ~en:update a conversation
 
 @param aConversation ~cn:需要更新的会话实例 ~en:a conversation that need to update
 @param aReaded ~cn:是否标记该会话为所有消息已读 ~en:mark all messages readed in a conversation
 */
- (void)updataOneConversationWithChatterConversation:(HTConversation*)aConversation isReadAllMessage:(BOOL)aReaded;

/**
 ~cn:从数据库中读取所有会话，缓存会对应更新 ~en:read from the database all conversations, the cache will be corresponding update
 
 
 @param aBlocked ~cn:查询到的会话列表 ~en:Query to the conversation list
 
 */
- (void)loadAllConversationsFromDBCompletion:(void(^)(NSArray *result))aBlocked;

@end
