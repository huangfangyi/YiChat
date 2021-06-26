//
//  HTMessageDownloadHelper.m
//  HTMessage
//
//  Created by 非夜 on 17/4/9.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTMessageDownloadHelper.h"
#import "HTMessageDefines.h"
#import "HTClient.h"
#import "QSTools.h"

@implementation HTMessageDownloadHelper

- (void)downLoadActionWithMessage:(HTMessage *)message completion:(void(^)(HTMessage * message))completion {
    
    //url包含中文无法下载，需要转码
    NSString *path = message.body.remotePath;
    NSCharacterSet *charSet = [NSCharacterSet characterSetWithCharactersInString:path];
    path = [path stringByAddingPercentEncodingWithAllowedCharacters:charSet];
    
    // 创建下载路径
    NSURL *url = [NSURL URLWithString:path];
    __block HTMessage * tempMessage = message;
    
    // 创建NSURLSession对象
    NSURLSession *session = [NSURLSession sharedSession];
    
    // 创建下载任务,其中location为下载的临时文件路径
    NSURLSessionDownloadTask *downloadTask = [session downloadTaskWithURL:url completionHandler:^(NSURL *location, NSURLResponse *response, NSError *error) {
        
        // 文件将要移动到的指定目录
//        NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
//        
//        // 新文件路径
//        NSString *newFilePath = [documentsPath stringByAppendingPathComponent:response.suggestedFilename];
        NSString *newFilePath = nil;

        if ([message.body.messageType isEqualToString:@"Audio"]) {
            newFilePath = [self AudioFilePath];
        }
        else  if([message.body.messageType isEqualToString:@"Video"]) {
            newFilePath = [self VideoFilePath];
        }
        else  if([message.body.messageType isEqualToString:@"Image"] || [message.body.messageType isEqualToString:@"Position"]) {
            newFilePath = [self imageFilePath];
        }
        else {
            newFilePath = [self filePath:tempMessage];
        }
        
        // 移动文件到新路径
        [[NSFileManager defaultManager] moveItemAtPath:location.path toPath:newFilePath error:nil];
        
        tempMessage.body.localPath = newFilePath;
        
        tempMessage.downLoadState = DownloadStateSuccessed;
        
        [[HTClient sharedInstance].messageManager updateOneNormalMessage:tempMessage];
        
        completion(tempMessage);
        
    }];
    
    // 开始下载任务
    [downloadTask resume];
}

- (NSString *)imageFilePath {
    NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];

    NSString *filePath = [documentsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@file",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
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

- (NSString *)AudioFilePath {
    
    NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];

    NSString *filePath = [documentsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@audio",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            NSString *fileName = [NSString stringWithFormat:@"%@.amr", [QSTools creatUUID]];
            filePath = [filePath stringByAppendingPathComponent:fileName];
        }
    }else{
        NSString *fileName = [NSString stringWithFormat:@"%@.amr", [QSTools creatUUID]];
        filePath = [filePath stringByAppendingPathComponent:fileName];
    }
    return filePath;
}

- (NSString *)VideoFilePath {
    NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    
    NSString *filePath = [documentsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@file",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            NSString *fileName = [NSString stringWithFormat:@"%@.mp4", [QSTools creatUUID]];
            filePath = [filePath stringByAppendingPathComponent:fileName];
        }
    }else{
        NSString *fileName = [NSString stringWithFormat:@"%@.mp4", [QSTools creatUUID]];
        filePath = [filePath stringByAppendingPathComponent:fileName];
    }
    return filePath;
}

- (NSString *)filePath:(HTMessage *)message {
    NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    
    NSString *filePath = [documentsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@file",[HTClient sharedInstance].currentUsername,HT_FILE_PATH]];
    if (![[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error = nil;
        [[NSFileManager defaultManager] createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&error];
        if (error) {
            NSLog(@"%@", error);
        }else{
            filePath = [filePath stringByAppendingPathComponent:message.body.fileName];
        }
    }else{
        filePath = [filePath stringByAppendingPathComponent:message.body.fileName];
    }
    return filePath;
}

@end
