//
//  HTFileMessageManager.m
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTFileMessageManager.h"
#import "QSTools.h"
#import "HTClient.h"
#import "HTMessageDefines.h"


#define DocumentPath  [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]

@implementation HTFileMessageManager

+ (void)handleTextMessage:(HTMessage *)aMessage {
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2001;
}

+ (void)HandleImageMessage:(HTMessage *)aMessage withImage:(UIImage *)aImage {
    if (aMessage.body == nil) {
        return;
    }
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2002;
    // 原始图片data
    NSData * imageData = nil;
    if ([HTClient sharedInstance].messageManager.sendImageQuality > 0 && [HTClient sharedInstance].messageManager.sendImageQuality <= 1) {
        imageData = UIImageJPEGRepresentation(aImage, [HTClient sharedInstance].messageManager.sendImageQuality);
    }else {
        imageData = UIImageJPEGRepresentation(aImage, 0.6);
    }
    NSDictionary * transFormDicI = [self createThumbnailImageLocalPath:aImage withMessageId:aMessage.msgId andScale:-1];
    if (![transFormDicI[@"saveResult"] boolValue]) {
        return;
    }
    NSString * imageFilePath = nil;
    NSString * thumbLocalPath = transFormDicI[@"filePath"];;
    if ([transFormDicI[@"scaleType"] intValue] == 1) {
        imageFilePath = transFormDicI[@"filePath"];
    }else{
        imageFilePath = [self imageFilePath];
        BOOL res = [[NSFileManager defaultManager] createFileAtPath:imageFilePath
                                                           contents:imageData
                                                         attributes:nil];
        if (!res) {
            return;
        }
    }
    aMessage.body.size = [NSString stringWithFormat:@"%f,%f",aImage.size.width,aImage.size.height];
    aMessage.body.localPath = imageFilePath;
    aMessage.body.thumbnailLocalPath = thumbLocalPath;
    aMessage.body.fileName = [NSString stringWithFormat:@"%@.png",[QSTools creatUUID]];
    
}

+ (void)HandleAudioMessage:(HTMessage *)aMessage withFilePath:(NSString *)aFilePath {
    if (aMessage.body == nil) {
        return;
    }
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2003;
    aMessage.body.localPath = aFilePath;
    aMessage.body.fileName = [NSString stringWithFormat:@"%@.amr",[QSTools creatUUID]];
}

+ (void)handleVideoMessage:(HTMessage *)aMessage withFilePath:(NSURL *)aFilePath andThumbnailImage:(UIImage *)aImage {
    if (aMessage.body == nil) {
        return;
    }
    if (aMessage.body == nil) {
        return;
    }
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2004;
    NSDictionary * transFormDicI = [self createThumbnailImageLocalPath:aImage withMessageId:[QSTools creatUUID] andScale:120];
    if (![transFormDicI[@"saveResult"] boolValue]) {
        NSLog(@"视频发送失败pre");
        return;
    }
    NSString *thumbLocalPath = transFormDicI[@"filePath"];
    aMessage.body.thumbnailLocalPath = thumbLocalPath;
    aMessage.body.localPath = [aFilePath path];
    aMessage.body.fileName = [NSString stringWithFormat:@"%@.mp4",[QSTools creatUUID]];
}

+ (void)HandlePositionMessage:(HTMessage *)aMessage withImage:(UIImage *)aImage {
    if (aMessage.body == nil) {
        return;
    }
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2006;
    NSData * imageData = UIImageJPEGRepresentation(aImage, 1);
    NSString * imageFilePath = [self imageFilePath];
    BOOL res = [[NSFileManager defaultManager] createFileAtPath:imageFilePath
                                                       contents:imageData
                                                     attributes:nil];
    if (!res) {
        NSLog(@"地图截图写入失败");
        return;
    }
    aMessage.body.localPath = imageFilePath;
    aMessage.body.fileName = [NSString stringWithFormat:@"%@.png",[QSTools creatUUID]];
}


+ (void)handleFileMessage:(HTMessage *)aMessage withFilePath:(NSString *)aFilePath withFileName:(NSString *)fileName{
    if (aMessage.body == nil) {
        return;
    }
    if (aMessage.body == nil) {
        return;
    }
    [self messageCommenConfig:aMessage];
    aMessage.msgType = 2005;
    aMessage.body.localPath = aFilePath;
    aMessage.body.fileName = fileName;
    NSData * data = [NSData dataWithContentsOfFile:aFilePath];
    aMessage.body.fileSize = data.length;
}

+ (void)handleCMDMessage:(HTCmdMessage *)aCMDMessage {
    aCMDMessage.from = [HTClient sharedInstance].currentUsername;
    aCMDMessage.msgId = [QSTools creatUUID];
    aCMDMessage.timestamp = [[NSDate date] timeIntervalSince1970] * 1000;
}

+ (NSString *)imageFilePath {
    NSString *filePath = [DocumentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@file",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            NSString *fileName = [NSString stringWithFormat:@"%@.png", [QSTools creatUUID]];
            filePath = [filePath stringByAppendingPathComponent:fileName];
        }
    }else{
        NSString *fileName = [NSString stringWithFormat:@"%@.png", [QSTools creatUUID]];
        filePath = [filePath stringByAppendingPathComponent:fileName];
    }
    return filePath;
}

+ (NSString *)thumbnailFilePath:(NSString *)messageId{
    NSString *filePath = [DocumentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@file_thumbnail",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            NSString *fileName = [NSString stringWithFormat:@"%@.png", messageId];
            filePath = [filePath stringByAppendingPathComponent:fileName];
        }
    }else{
        NSString *fileName = [NSString stringWithFormat:@"%@.png", messageId];
        filePath = [filePath stringByAppendingPathComponent:fileName];
    }
    return filePath;
}

/**
 *  图片压缩
 */
+ (NSDictionary *)createThumbnailImageLocalPath:(UIImage *)image withMessageId:(NSString *)messageId andScale:(NSInteger)tempScale
{
    if (tempScale == -1) {
        tempScale = 200;
    }
    
    tempScale *= 2;
    
    CGFloat scaleSize = 1.0;
    NSString * resize = nil;
    int isScale = 1;
    if (image.size.width > tempScale || image.size.height > tempScale) {
        if (image.size.width > image.size.height) {
            scaleSize = tempScale / image.size.width;
            resize = [NSString stringWithFormat:@"?x-oss-process=image/resize,w_200"];
            isScale = 2;
            
        }else{
            scaleSize = tempScale / image.size.height;
            resize = [NSString stringWithFormat:@"?x-oss-process=image/resize,h_200"];
            isScale = 3;
        }
    }
    
    UIGraphicsBeginImageContext(CGSizeMake(image.size.width * scaleSize, image.size.height * scaleSize));
    [image drawInRect:CGRectMake(0, 0, image.size.width * scaleSize, image.size.height * scaleSize)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    NSData * imageData = UIImageJPEGRepresentation(scaledImage, 1);
    NSString * imageFilePath = [self thumbnailFilePath:messageId];
    BOOL res = [[NSFileManager defaultManager] createFileAtPath:imageFilePath
                                                       contents:imageData
                                                     attributes:nil];
    NSMutableDictionary * dicM = @{}.mutableCopy;
    [dicM setValue:@(res) forKey:@"saveResult"];
    [dicM setValue:@(isScale) forKey:@"scaleType"];
    [dicM setValue:imageFilePath forKey:@"filePath"];
    if (isScale > 1) {
        [dicM setValue:resize forKey:@"remoteFilePath"];
    }else{
        [dicM setValue:@"" forKey:@"remoteFilePath"];
    }
    return dicM.mutableCopy;
}

+ (NSString *)recordFilePath {
    NSString *filePath = [DocumentPath stringByAppendingPathComponent:@"SoundFile"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            NSString *fileName = [NSString stringWithFormat:@"/voice-%5.2f.amr", [[NSDate date] timeIntervalSince1970] ];
            filePath = [filePath stringByAppendingPathComponent:fileName];
        }
    }else{
        NSString *fileName = [NSString stringWithFormat:@"/voice-%5.2f.amr", [[NSDate date] timeIntervalSince1970] ];
        filePath = [filePath stringByAppendingPathComponent:fileName];
    }
    return filePath;
}

// 基本配置

+ (void)messageCommenConfig:(HTMessage *)message {
    message.from = [HTClient sharedInstance].currentUsername;
    message.msgId = [QSTools creatUUID];
    message.timestamp = [[NSDate date] timeIntervalSince1970] * 1000;
    message.type = 2000;
    message.sendState = SendStateSending;
    message.downLoadState = DownloadStateSuccessed;
    message.isSender = YES;
}

@end
