//
//  HTUploadHelper.m
//  HTMessage
//
//  Created by 非夜 on 16/11/22.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTUploadHelper.h"
#import <AliyunOSSiOS/OSSService.h>
#import "ServiceGlobalDef.h"


@interface HTUploadHelper()

@property (nonatomic,strong)OSSClient * client;
@property (nonatomic,strong)void(^kProgressBlocked)(CGFloat progress);
@property (nonatomic,strong)void(^kResultBlocked)(BOOL isSuccess,NSString *remotePath);
@property (nonatomic,strong)id kData;
@property (nonatomic,strong)NSString *kMessageId;

@end

@implementation HTUploadHelper

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
    
    id<OSSCredentialProvider> credential = [[OSSPlainTextAKSKPairCredentialProvider alloc] initWithPlainTextAccessKey:YiChatProject_NetWork_OSSAccessKey secretKey:YiChatProject_NetWork_OSSSecretKey];
    OSSClientConfiguration * conf = [OSSClientConfiguration new];
    conf.maxRetryCount = 2;
    conf.timeoutIntervalForRequest = 30;
    conf.timeoutIntervalForResource = 24 * 60 * 60;
    
    self.client = [[OSSClient alloc] initWithEndpoint:YiChatProject_NetWork_OSSEndPoint credentialProvider:credential clientConfiguration:conf];
}

- (void)uploadObjectAsync{
    
    OSSPutObjectRequest * put = [OSSPutObjectRequest new];
    put.bucketName = YiChatProject_NetWork_OSSBucket;
    if ([self.kData isKindOfClass:[NSURL class]]) {
        put.uploadingFileURL = self.kData;
        put.objectKey = [NSString stringWithFormat:@"%@.mp4", [self creatUUID]];
    }else if([self.kData isKindOfClass:[NSData class]]){
        put.objectKey = [NSString stringWithFormat:@"%@.png", self.kMessageId.length > 0 ? self.kMessageId : [self creatUUID]];
        put.uploadingData = self.kData;
    }else{
        put.objectKey = [NSString stringWithFormat:@"%@.amr", [self creatUUID]];
        put.uploadingFileURL = [NSURL fileURLWithPath:self.kData];
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

- (NSString *)creatUUID {
    
    NSString *userId = [[YiChatUserManager defaultManagaer] getUserIdStr];
    if(userId && [userId isKindOfClass:[NSString class]]){
        return [NSString stringWithFormat:@"%@_%@%d",userId,[ProjectHelper helper_GetCurrentTimeString],(arc4random() % 1000)];
    }
    else{
        CFUUIDRef puuid = CFUUIDCreate(nil);
        CFStringRef uuidString = CFUUIDCreateString(nil, puuid);
        NSString *result = (__bridge NSString *)CFStringCreateCopy(NULL, uuidString);
        CFRelease(puuid);
        CFRelease(uuidString);
        return result;
    }
}

@end
