//
//  HTMessageBodyManager.m
//  HTMessage
//
//  Created by 非夜 on 2016/12/13.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTMessageBodyManager.h"
#import "NSObject+QSModel.h"


@implementation HTMessageBodyManager

+ (NSDictionary *)convertMessageBodyToDicWithMessageBody:(HTMessage *)aMessage {
    switch (aMessage.msgType) {
        case 2001:{
            IHTTextMessageBody * body = [IHTTextMessageBody new];
            body.content = aMessage.body.content;
            return [body modelToJSONObject];
        }
            break;
        case 2002:{
            IHTImageMessageBody * body = [IHTImageMessageBody new];
            body.thumbnailLocalPath = aMessage.body.thumbnailLocalPath;
            body.thumbnailRemotePath = aMessage.body.thumbnailRemotePath;
            body.size = aMessage.body.size;
            body.remotePath = aMessage.body.remotePath;
            body.localPath = aMessage.body.localPath;
            body.fileName = aMessage.body.fileName;
            return [body modelToJSONObject];
        }
            break;
        case 2003:{
            IHTAudioMessageBody * body = [IHTAudioMessageBody new];
            body.remotePath = aMessage.body.remotePath;
            body.localPath = aMessage.body.localPath;
            body.fileName = aMessage.body.fileName;
            body.audioDuration = aMessage.body.audioDuration;
            return [body modelToJSONObject];
        }
            break;
        case 2004:{
            IHTVideoMessageBody * body = [IHTVideoMessageBody new];
            body.thumbnailLocalPath = aMessage.body.thumbnailLocalPath;
            body.thumbnailRemotePath = aMessage.body.thumbnailRemotePath;
            body.remotePath = aMessage.body.remotePath;
            body.localPath = aMessage.body.localPath;
            body.fileName = aMessage.body.fileName;
            body.size = aMessage.body.size;
            body.videoDuration = aMessage.body.videoDuration;
            return [body modelToJSONObject];
        }
            break;
        case 2005:{
            IHTFileMessageBody * body = [IHTFileMessageBody new];
            body.remotePath = aMessage.body.remotePath;
            body.localPath = aMessage.body.localPath;
            body.fileName = aMessage.body.fileName;
            body.fileSize = aMessage.body.fileSize;
            return [body modelToJSONObject];
        }
            break;
        case 2006:{
            IHTLocationMessageBody * body = [IHTLocationMessageBody new];
            body.remotePath = aMessage.body.remotePath;
            body.localPath = aMessage.body.localPath;
            body.fileName = aMessage.body.fileName;
            body.fileSize = aMessage.body.fileSize;
            body.latitude = aMessage.body.latitude;
            body.longitude = aMessage.body.longitude;
            body.address = aMessage.body.address;
            return [body modelToJSONObject];
        }
            break;
        default:
            return nil;
            break;
    }
}


+ (void)reWriteMessageBodyWithMessage:(HTMessage *)aMessage {
    aMessage.body.messageType = [self exchangeMesssageType:aMessage.msgType];
}

+ (NSString *)exchangeMesssageType:(NSInteger)type {
    switch (type) {
        case 2001:
            return @"Text";
            break;
        case 2002:
            return @"Image";
            break;
        case 2003:
            return @"Audio";
            break;
        case 2004:
            return @"Video";
            break;
        case 2005:
            return @"File";
            break;
        case 2006:
            return @"Position";
            break;
        default:
            return nil;
            break;
    }
}

@end

@implementation IHTTextMessageBody

@end


@implementation IHTImageMessageBody

@end


@implementation IHTAudioMessageBody

@end


@implementation IHTVideoMessageBody

@end


@implementation IHTLocationMessageBody

@end


@implementation IHTFileMessageBody

@end


