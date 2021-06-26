//
//  QSMessageSDKHelper.m
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "QSMessageSDKHelper.h"
#import "HTFileMessageManager.h"

@implementation QSMessageSDKHelper

+ (HTMessage *)sendTextMessage:(NSString *)text
                            to:(NSString *)to
                   messageType:(NSString *)messageType
                    messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.content = text;
    message.body = body;
    [HTFileMessageManager handleTextMessage:message];
    return message;
}

+ (void )sendCmdMessage:(HTCmdMessage *)aCMDMessage {
    [HTFileMessageManager handleCMDMessage:aCMDMessage];
}

+ (HTMessage *)sendLocationMessageWithLatitude:(double)latitude
                                     longitude:(double)longitude
                                       address:(NSString *)address
                                            to:(NSString *)to
                                   messageType:(NSString *)messageType
                                    andSSImage:(UIImage *)mapImage
                                    messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.address = address;
    body.latitude = latitude;
    body.longitude = longitude;
    message.body = body;
    [HTFileMessageManager HandlePositionMessage:message withImage:mapImage];
    return message;
}

+ (HTMessage *)sendImageMessageWithImage:(UIImage *)image
                                      to:(NSString *)to
                             messageType:(NSString *)messageType
                              messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    message.body = body;
    [HTFileMessageManager HandleImageMessage:message withImage:image];
    return message;
}

+ (HTMessage *)sendAudioMessageWithLocalPath:(NSString *)localPath
                                    duration:(NSInteger)duration
                                          to:(NSString *)to
                                 messageType:(NSString *)messageType
                                  messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.audioDuration = [NSString stringWithFormat:@"%d",(int)duration];
    message.body = body;
    [HTFileMessageManager HandleAudioMessage:message withFilePath:localPath];
    return message;
}

+ (HTMessage *)sendVideoMessageWithURL:(NSURL *)url
                                    to:(NSString *)to
                           messageType:(NSString *)messageType
                            andSSImage:(UIImage *)mapImage
                          andVideoTime:(NSInteger)videoDurtion
                            messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.videoDuration = videoDurtion;
    message.body = body;
    [HTFileMessageManager handleVideoMessage:message withFilePath:url andThumbnailImage:mapImage];
    return message;
}

@end
