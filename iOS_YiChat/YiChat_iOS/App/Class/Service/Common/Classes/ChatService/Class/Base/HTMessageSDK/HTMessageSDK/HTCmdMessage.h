/*!
 @header  HTCmdMessage.h
 
 @abstract
 
 @author  Created by 非夜 on 16/12/14.
 
 @version 1.0 16/12/14 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 */

#import <Foundation/Foundation.h>

/**
 ~cn:透传消息 ~en:Cmd message
 */
@interface HTCmdMessage : NSObject

/**
 ~cn:发送者 ~en:sender
 */
@property(nonatomic,strong)NSString * from;

/**
 ~cn:接收者 ~en:receiver
 */
@property(nonatomic,strong)NSString * to;

/**
 ~cn:消息类型，包括单聊群聊 ~en:mesage type, include: single chat & group chat
 */
@property(nonatomic,strong)NSString * chatType;

/**
 ~cn:消息id ~en:messagId
 */
@property(nonatomic,strong)NSString * msgId;

/**
 ~cn:附带消息内容 ~en:expand content
 */
@property(nonatomic,strong)NSString * body;

/**
 ~cn:消息在服务器的时间戳 ~en:timestamp at server
 */
@property(nonatomic,assign)NSUInteger timestamp;

@end
