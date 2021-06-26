//
//  ZFChatStorageHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatStorageHelper.h"
#import "ZFChatStorageManager.h"
#import "YRVoiceConverter.h"
#import "ZFChatGlobal.h"

@implementation ZFChatStorageHelper

+ (NSString *)zfChatStorageHelper_getChatVoiceRecorderPath{
    return [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVoiceRecorderPath];
}

+ (NSString *)zfChatStorageHelper_getTranslatedChatRecorderFilePathWithFileName:(NSString *)fileName{
    if(fileName && [fileName isKindOfClass:[NSString class]]){
        ZFChatStorageManager *storage = [ZFChatStorageManager sharedManager];
        
        NSString *recorderTranslatedItemPath = [storage zfChatStorageManager_getVoiceRecorderTranslatedItemPath];
        
        return [[recorderTranslatedItemPath stringByAppendingPathComponent:fileName] stringByAppendingPathExtension:@"amr"];
    }
    return nil;
    
    
}

+ (NSString *)zfChatStorageHelper_translateChatRecorderFilePathToRecorderAmrPath:(NSString *)wavPath{
    if(wavPath && [wavPath isKindOfClass:[NSString class]]){
        if([wavPath hasSuffix:@".wav"]){
            ZFChatStorageManager *storage = [ZFChatStorageManager sharedManager];
            
            NSString *recorderItemPath = [storage zfChatStorageManager_getVoiceRecorderItemPath];
            
            NSString *recorderTranslatedItemPath = [storage zfChatStorageManager_getVoiceRecorderTranslatedItemPath];
            
            NSRange range = [wavPath rangeOfString:recorderItemPath];
            
            if(range.location != NSNotFound && (wavPath.length - 1) >= (range.location + range.length + 1)){
                NSString *fileName = [wavPath substringFromIndex:(range.location + range.length + 1)];
                
                if(fileName.length >=4){
                    fileName = [fileName substringToIndex:fileName.length - 4];
                }
                return [[[recorderTranslatedItemPath stringByAppendingString:@"/"] stringByAppendingString:fileName] stringByAppendingString:@".amr"];
            }
        }
    }
    return nil;
}

+ (BOOL)zfChatStorageHelper_convertAmrToWav:(NSString *)aAmrPath wavSavePath:(NSString *)aSavePath{
    return [YRVoiceConverter ConvertAmrToWav:aAmrPath wavSavePath:aSavePath];
}

+ (BOOL)zfChatStorageHelper_convertWavToAmr:(NSString *)aWavPath amrSavePath:(NSString *)aSavePath{
    return [YRVoiceConverter ConvertWavToAmr:aWavPath amrSavePath:aSavePath];
}

+ (NSString *)zfChatStorageHelper_getMP4FileFullPath{
   return  [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVideoRecorderMP4ItemPath];
}

+ (void)zfChatStorageHelper_convertMovToMP4WithMovPath:(NSString *)mp4Path savePath:(NSString *)path hanlde:(void(^) (BOOL success,NSString *path,NSString *errorStr))handle{
    
    [YRGeneralApis yrGeneralApisTranlateMovToMP4WithPath:[NSURL fileURLWithPath:mp4Path] savePath:path hanlde:^(BOOL success, NSString *path, NSString *errorStr) {
        handle(success,path,errorStr);
    }];
}

@end
