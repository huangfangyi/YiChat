//
//  HTMessageCategoryUploader.m
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTMessageCategoryUploader.h"
#import "HTMessageUploadHelper.h"
#import "HTMessageDefines.h"

@implementation HTMessageCategoryUploader

+ (void)sendMessage:(HTMessage *)message  withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess, HTMessage * kMessage))resultBlocked {
    if (message.msgType == 2001) {
        resultBlocked(YES,message);
        resultBlocked = nil;
    }else if(message.msgType == 2002){
        HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:[NSData dataWithContentsOfFile:message.body.localPath] withProgressBlock:^(CGFloat progress) {
        } andMessageId:message.msgId andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if (isSuccess) {
                message.body.remotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,remotePath];
                message.sendState = SendStateSuccessed;
                resultBlocked(YES,message);
            }else{
                message.sendState = SendStateFail;
                resultBlocked(NO,message);
            }
        }];
        upload.messageType = message.msgType;
        [upload uploadObjectAsync];
    }
    else if(message.msgType == 2003){
        HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:message.body.localPath withProgressBlock:^(CGFloat progress) {
        } andMessageId:message.msgId andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if (isSuccess) {
                message.body.remotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,remotePath];
                message.sendState = SendStateSuccessed;
                resultBlocked(YES,message);
            }else{
                message.sendState = SendStateFail;
                resultBlocked(NO,message);
            }
        }];
        upload.messageType = message.msgType;
        [upload uploadObjectAsync];
    }
    else if(message.msgType == 2004){
        HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:[NSData dataWithContentsOfFile:message.body.thumbnailLocalPath] withProgressBlock:^(CGFloat progress) {
        } andMessageId:nil andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if (isSuccess) {
                message.body.thumbnailRemotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,remotePath];
                HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:[NSURL fileURLWithPath:message.body.localPath] withProgressBlock:^(CGFloat progress) {
                } andMessageId:message.msgId andSendResult:^(BOOL isSuccess, NSString *bRemotePath) {
                    if (isSuccess) {
                        message.body.remotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,bRemotePath];
                        message.sendState = SendStateSuccessed;
                        resultBlocked(YES,message);
                    }else{
                        message.sendState = SendStateFail;
                        resultBlocked(NO,message);
                    }
                }];
                upload.messageType = message.msgType;
                [upload uploadObjectAsync];
            }else{
                message.sendState = SendStateFail;
                resultBlocked(NO,message);
            }
        }];
        upload.messageType = message.msgType;
        [upload uploadObjectAsync];
    }
    else if(message.msgType == 2005){
        HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:message.body.localPath withProgressBlock:^(CGFloat progress) {
        } andMessageId:message.msgId andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if (isSuccess) {
                message.body.remotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,remotePath];
                message.sendState = SendStateSuccessed;
                resultBlocked(YES,message);
            }else{
                message.sendState = SendStateFail;
                resultBlocked(NO,message);
            }
        }];
        upload.messageType = message.msgType;
        upload.fileName = message.body.fileName;
        [upload uploadObjectAsync];
    }
    else if(message.msgType == 2006){
        HTMessageUploadHelper * upload = [[HTMessageUploadHelper alloc] initWithData:[NSData dataWithContentsOfFile:message.body.localPath] withProgressBlock:^(CGFloat progress) {
        } andMessageId:message.msgId andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if (isSuccess) {
                message.body.remotePath = [NSString stringWithFormat:@"%@%@",CHAT_FILE_HOST,remotePath];
                message.sendState = SendStateSuccessed;
                resultBlocked(YES,message);
            }else{
                message.sendState = SendStateFail;
                resultBlocked(NO,message);
            }
        }];
        upload.messageType = message.msgType;
        [upload uploadObjectAsync];
    }
    
}

@end

