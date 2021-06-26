//
//  ZFChatStorageManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatStorageManager.h"
#import "ZFChatStorageVoiceManager.h"
#import "ZFChatStorageVideoManager.h"
#import "ZFChatStorageApis.h"
#import "ProjectStorageApis.h"
#import "YRVoiceConverter.h"
static ZFChatStorageManager *zfStorageManager = nil;
@interface ZFChatStorageManager ()

@end

@implementation ZFChatStorageManager

+ (id)sharedManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        zfStorageManager = [[self alloc] init];
    });
    return zfStorageManager;
}

- (NSString *)zfChatStorageManager_getVoiceRecorderPath{
    ZFChatStorageVoiceManager *manager = [ZFChatStorageVoiceManager sharedManager];
    
    NSString *fileName = [[self productFileName] stringByAppendingString:@".wav"];
    
    NSString *fileItem =  manager.recorderVoiceItemPath;
    
    return [self getFilePathWithFileName:fileName fileItem:fileItem];
}

- (NSString *)zfChatStorageManager_getVoiceRecorderItemPath{
    ZFChatStorageVoiceManager *manager = [ZFChatStorageVoiceManager sharedManager];
    
    return [self getFilePathWithFileItem:manager.recorderVoiceItemPath];
}

- (NSString *)zfChatStorageManager_getVoiceRecorderTranslatedItemPath{
    ZFChatStorageVoiceManager *manager = [ZFChatStorageVoiceManager sharedManager];
    
    return [self getFilePathWithFileItem:manager.recorderTranslatedVoiceItemPath];
}

- (NSString *)zfChatStorageManager_getVideoRecorderMOVPath{
    ZFChatStorageVideoManager *manager = [ZFChatStorageVideoManager sharedManager];
    
    NSString *fileName = [[ZFChatStorageApis zfChatStorageApis_randomString] stringByAppendingString:@".mov"];
    
    NSString *fileItem =  manager.recorderVideoMOVItemPath;
    
    return [self getFilePathWithFileName:fileName fileItem:fileItem];
}

- (NSString *)zfChatStorageManager_getVideoRecorderMP4ItemPath{
    ZFChatStorageVideoManager *manager = [ZFChatStorageVideoManager sharedManager];
    
    NSString *fileName = [[ZFChatStorageApis zfChatStorageApis_randomString] stringByAppendingString:@".mp4"];
    
    NSString *fileItem =  manager.recorderVideoExportTranslatedMP4ItemPath;
    
    return [self getFilePathWithFileName:fileName fileItem:fileItem];
}

- (NSString *)getFilePathWithFileName:(NSString *)fileName fileItem:(NSString *)fileItem{
    if(fileItem && fileName){
        if(![[NSFileManager defaultManager] fileExistsAtPath:fileItem]){
            [ProjectStorageApis projectStorageApis_CreateItemWithPath:fileItem];
        }
        NSString *filePath = [[fileItem stringByAppendingString:@"/"] stringByAppendingString:fileName];
    
        return filePath;
    }
    return nil;
}

- (NSString *)getFilePathWithFileItem:(NSString *)fileItem{
    if(fileItem){
        if(![[NSFileManager defaultManager] fileExistsAtPath:fileItem]){
            [ProjectStorageApis projectStorageApis_CreateItemWithPath:fileItem];
        }
        return fileItem;
    }
    return nil;
}

- (NSString *)productFileName{
    return [ZFChatStorageApis zfChatStorageApis_randomString];
}

- (BOOL)convertWavFile:(NSString *)wavFilePath toAmrPath:(NSString *)amrPath{
    if(wavFilePath && [wavFilePath isKindOfClass:[NSString class]] && amrPath && [amrPath isKindOfClass:[NSString class]]){
        return [YRVoiceConverter ConvertWavToAmr:wavFilePath amrSavePath:amrPath];
    }
    return NO;
}

- (BOOL)convertAmrFile:(NSString *)amrFilePath toWavPath:(NSString *)wavPath{
    if(amrFilePath && [amrFilePath isKindOfClass:[NSString class]] && wavPath && [wavPath isKindOfClass:[NSString class]]){
        return [YRVoiceConverter ConvertAmrToWav:amrFilePath wavSavePath:wavPath];
    }
    return NO;
}
@end
