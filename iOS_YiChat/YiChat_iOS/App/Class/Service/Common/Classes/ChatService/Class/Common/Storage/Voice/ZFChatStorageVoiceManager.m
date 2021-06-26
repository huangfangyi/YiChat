//
//  ZFChatStorageVoiceManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatStorageVoiceManager.h"
#import "ZFChatStorageApis.h"

static ZFChatStorageVoiceManager *zfChatStorageVoiceManager = nil;

@interface ZFChatStorageVoiceManager ()

@end

@implementation ZFChatStorageVoiceManager

+ (id)sharedManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        zfChatStorageVoiceManager = [[self alloc] init];
        zfChatStorageVoiceManager.recorderVoiceItemPath = [ZFChatStorageApis videoStorageApis_getRecorderVoiceStoragePath];
        
        zfChatStorageVoiceManager.recorderTranslatedVoiceItemPath = [ZFChatStorageApis videoStorageApis_getRecorderTranslatedVoiceStoragePath];
    });
    return zfChatStorageVoiceManager;
}


@end
