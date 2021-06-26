/*!
 @header  HTMessageManager.h
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import "HTMessageDelegate.h"


/**
 消息管理
 */
@interface HTMessageManager : NSObject

/**
 发送的图片的图片质量 0 < sendImageQuality < 1，默认为0.6
 */
@property(nonatomic,assign)CGFloat sendImageQuality;

/**
 添加回调代理
 
 @param aDelegate 要添加的代理
 @param aDelegateQueue 执行代理方法的队列
 */
- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)aDelegateQueue;

/**
 移除回调代理
 
 @param aDelegate 要移除的代理
 */
- (void)removeDelegate:(id)aDelegate;

/**
 接收到普通消息
 
 @param aMessage 普通消息数组
 */
- (void)didReceiveMessages:(NSArray *)aMessage;

/**
 接收到透传消息
 
 @param aCMDMessages 透传消息
 */
- (void)didReceiveCMDMessage:(NSArray *)aCMDMessages;

/**
 接收到时间修正消息
 
 @param aMessages 普通消息数组
 */
- (void)didReceiveSeriveTimeCorrectMessages:(NSArray *)aMessages;

/**
 存储一条聊天消息
 
 @param aMessage 消息实例
 */
- (void)insertOneNormalMessage:(HTMessage *)aMessage;

/**
 更新一条消息
 
 @param aMessage 消息实例
 */
- (void)updateOneNormalMessage:(HTMessage *)aMessage;

/**
 删除一条消息
 
 @param aMessage 消息实例
 */
- (void)deleteOneNormalMessage:(HTMessage *)aMessage;

- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)chatId;

/**
 *  获取单聊消息
 */
- (void)getSingleChatMessagesWithContent:(NSString *)contentStr
                                    from:(NSString *)from
                                      to:(NSString *)to
                               timestamp:(NSInteger)timestamp
                              completion:(void(^)(NSArray <HTMessage * >*))completion;
/**
 下载一条消息
 
 @param message 消息实例
 @param completion 完成回调
 */
- (void)downLoadMessage:(HTMessage *)message completion:(void(^)(HTMessage * message))completion;

@end

