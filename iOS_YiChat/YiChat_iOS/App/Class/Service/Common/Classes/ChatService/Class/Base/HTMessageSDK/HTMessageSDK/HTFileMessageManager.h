//
//  HTFileMessageManager.h
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"
#import "HTCmdMessage.h"

/**
 ~cn:预处理所有类型的消息 ~en:pre control all type message
 */
@interface HTFileMessageManager : NSObject

/**
 ~cn:文本消息 ~en:Text message
 
 @param aMessage ~cn:要处理的消息实例 ~en:pre to control message
 */
+ (void)handleTextMessage:(HTMessage *)aMessage;
/**
 ~cn:图片消息 ~en:Image message
 
 @param aMessage ~cn:要处理的消息实例 ~en:pre to control message
 */
+ (void)HandleImageMessage:(HTMessage *)aMessage withImage:(UIImage *)aImage;
/**
 ~cn:语音消息 ~en:Audio message
 
 @param aMessage ~cn:要处理的消息实例 ~en:pre to control message
 */
+ (void)HandleAudioMessage:(HTMessage *)aMessage withFilePath:(NSString *)aFilePath;
/**
 ~cn:视频消息 ~en:Video message
 
 @param aMessage ~cn:要处理的消息实例 ~en:pre to control message
 */
+ (void)handleVideoMessage:(HTMessage *)aMessage withFilePath:(NSURL *)aFilePath andThumbnailImage:(UIImage *)aImage;
/**
 ~cn:位置消息 ~en:location message
 
 @param aMessage ~cn:要处理的消息实例 ~en:pre to control message
 */
+ (void)HandlePositionMessage:(HTMessage *)aMessage withImage:(UIImage *)aImage;

/**
 文件消息
 
 @param aMessage 要处理的消息实例
 @param aFilePath 文件路径
 @param fileName 文件名称
 */
+ (void)handleFileMessage:(HTMessage *)aMessage withFilePath:(NSString *)aFilePath withFileName:(NSString *)fileName;
/**
 ~cn:透传消息 ~en:Cmd message
 
 @param aCMDMessage ~cn:要处理的透传消息实例 ~en:pre to control Cmd message
 */
+ (void)handleCMDMessage:(HTCmdMessage *)aCMDMessage;

@end
