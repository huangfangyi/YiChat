/*!
@header  HTMessage.h

@abstract 

@author  Created by 非夜 on 16/11/25.

@version 1.0 16/11/25 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
*/

#import <Foundation/Foundation.h>
#import "HTMessageBody.h"


/**
 发送状态，只本地作为一个记录，方便UI处理及自动发送
 
 - SendStateSending: 消息正在发送中
 - SendState_Fail: 消息发送失败
 - SendStateSuccessed: 消息发送成功
 */
typedef NS_ENUM(NSUInteger,SendState){
    SendStateSending = 0,
    SendStateFail,
    SendStateSuccessed
};

/**
 消息实例
 */
@interface HTMessage : NSObject

/**
 1000+ Noti,2000+ Normal,300+ CMD
 */
@property(nonatomic,assign)NSUInteger type;

/**
 聊天类型，单聊，群聊，聊天室，机器人
 */
@property(nonatomic,strong)NSString * chatType;

/**
 发送者id
 */
@property(nonatomic,strong)NSString *from;

/**
 接收者id
 */
@property(nonatomic,strong)NSString *to;

/**
 消息体内容
 */
@property(nonatomic,strong)HTMessageBody *body;

/**
 消息id
 */
@property(nonatomic,strong)NSString * msgId;

/**
 消息类型，2001文字，2002图片，2003语音，2004视频，2005文件，2006位置
 */
@property(nonatomic,assign)NSUInteger msgType;

/**
 消息在服务器的时间戳
 */
@property(nonatomic,assign)NSUInteger timestamp;

/**
 消息的拓展字段
 */
@property(nonatomic,strong)NSDictionary *ext;

/**
 是否是自己发送的
 */
@property(nonatomic,assign)BOOL isSender;

/**
 是否消息已读，针对语音消息
 */
//@property(nonatomic,assign)BOOL isRead;

/**
 发送状态
 */
@property(nonatomic,assign)SendState sendState;

/**
 文件下载状态，主要应用于语音消息
 */
@property(nonatomic,assign)DownloadState  downLoadState;

@end
