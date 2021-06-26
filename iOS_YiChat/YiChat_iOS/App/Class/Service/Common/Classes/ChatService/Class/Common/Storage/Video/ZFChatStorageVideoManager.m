//
//  ZFChatStorageVideoManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatStorageVideoManager.h"
#import "ZFChatStorageApis.h"

static ZFChatStorageVideoManager *zfChatStorageVideoManager = nil;

@interface ZFChatStorageVideoManager ()

@end

@implementation ZFChatStorageVideoManager

+ (id)sharedManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        zfChatStorageVideoManager = [[self alloc] init];
        zfChatStorageVideoManager.recorderVideoMOVItemPath = [ZFChatStorageApis videoStorageApis_getRecorderVideoMOVStoragePath];
        
        zfChatStorageVideoManager.recorderVideoExportTranslatedMP4ItemPath = [ZFChatStorageApis videoStorageApis_getRecorderVideoMP4StoragePath];
    });
    return zfChatStorageVideoManager;
}

@end
