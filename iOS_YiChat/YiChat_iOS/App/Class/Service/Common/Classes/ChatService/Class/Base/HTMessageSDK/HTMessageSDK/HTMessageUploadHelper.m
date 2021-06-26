//
//  HTMessageUploadHelper.m
//  HTMessage
//
//  Created by 非夜 on 16/11/22.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTMessageUploadHelper.h"
#import <AliyunOSSiOS/OSSService.h>
#import "HTMessageDefines.h"
#import "QSTools.h"

@interface HTMessageUploadHelper()

@property (nonatomic,strong)OSSClient * client;
@property (nonatomic,strong)void(^kProgressBlocked)(CGFloat progress);
@property (nonatomic,strong)void(^kResultBlocked)(BOOL isSuccess,NSString *remotePath);
@property (nonatomic,strong)id kData;
@property (nonatomic,strong)NSString *kMessageId;

@end

@implementation HTMessageUploadHelper

- (id)initWithData:(id)data withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    
    if (self = [super init]) {
        [self initOSSClient];
        self.kData = data;
        self.kProgressBlocked = progressBlocked;
        self.kResultBlocked = resultBlocked;
        self.kMessageId = messageId;
        return self;
    }
    return nil;
}

- (void)initOSSClient {
    
    id<OSSCredentialProvider> credential = [[OSSPlainTextAKSKPairCredentialProvider alloc] initWithPlainTextAccessKey:OSSAccessKey secretKey:OSSSecretKey];
    OSSClientConfiguration * conf = [OSSClientConfiguration new];
    conf.maxRetryCount = 2;
    conf.timeoutIntervalForRequest = 30;
    conf.timeoutIntervalForResource = 24 * 60 * 60;
    
    self.client = [[OSSClient alloc] initWithEndpoint:OSSEndPoint credentialProvider:credential clientConfiguration:conf];
}

// 消息类型，2001文字，2002图片，2003语音，2004视频，2005文件，2006位置

- (void)uploadObjectAsync{
    
    OSSPutObjectRequest * put = [OSSPutObjectRequest new];
    put.bucketName = OSSBucket;
    
    switch (self.messageType) {
        case 2002:{
            put.objectKey = [NSString stringWithFormat:@"%@.png", self.kMessageId.length > 0 ? self.kMessageId : [QSTools creatUUID]];
            put.uploadingData = self.kData;
        }
            break;
        case 2003:{
            put.objectKey = [NSString stringWithFormat:@"%@.amr", [QSTools creatUUID]];
            put.uploadingFileURL = [NSURL fileURLWithPath:self.kData];
        }
            break;
        case 2004:{
            if ([self.kData isKindOfClass:[NSData class]]) {
                put.objectKey = [NSString stringWithFormat:@"%@.png", self.kMessageId.length > 0 ? self.kMessageId : [QSTools creatUUID]];
                put.uploadingData = self.kData;
            }else {
                put.uploadingFileURL = self.kData;
                put.objectKey = [NSString stringWithFormat:@"%@.mp4", [QSTools creatUUID]];
            }
        }
            break;
        case 2005:{
            put.uploadingFileURL = [NSURL fileURLWithPath:self.kData];
            put.objectKey = self.fileName;
        }
            break;
        case 2006:{
            put.objectKey = [NSString stringWithFormat:@"%@.png", self.kMessageId.length > 0 ? self.kMessageId : [QSTools creatUUID]];
            put.uploadingData = self.kData;
        }
            break;
            
        default:
            break;
    }
    put.uploadProgress = ^(int64_t bytesSent, int64_t totalByteSent, int64_t totalBytesExpectedToSend) {
        if (self.kProgressBlocked) {
            self.kProgressBlocked(bytesSent * 1.0 / totalByteSent);
        }
    };
    put.contentType = @"";
    put.contentMd5 = @"";
    put.contentEncoding = @"";
    put.contentDisposition = @"";
    
    OSSTask * putTask = [self.client putObject:put];
    [putTask continueWithBlock:^id(OSSTask *task) {
        NSLog(@"objectKey: %@", put.objectKey);
        if (!task.error) {
            NSLog(@"upload object success!");
            self.kResultBlocked(YES,put.objectKey);
        } else {
            NSLog(@"upload object failed, error: %@" , task.error);
            self.kResultBlocked(NO,nil);
        }
        return nil;
    }];
}

@end
