//
//  ZFChatStorageApis.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatStorageApis.h"
#import "ProjectStorageApis.h"
#import "ZFChatGlobalDef.h"

@implementation ZFChatStorageApis

/*
 /libary/cache/ZFChat
 */
+ (NSString *)videoStorageApis_getRecorderVoiceStoragePath{
    
    NSString *path = [self getChatStorageItem];
    return [path stringByAppendingPathComponent:ZFChatStorage_RecoderVoice_Item];
}

+ (NSString *)videoStorageApis_getRecorderTranslatedVoiceStoragePath{
    
    NSString *path = [self getChatStorageItem];
    return [path stringByAppendingPathComponent:ZFChatStorage_RecoderTranslatedVoice_Item];
}

+ (NSString *)videoStorageApis_getRecorderVideoMOVStoragePath{
    
    NSString *path = [self getChatStorageItem];
    return [path stringByAppendingPathComponent:ZFChatStorage_RecorderVideoMOV_Item];
}

+ (NSString *)videoStorageApis_getRecorderVideoMP4StoragePath{
    
    NSString *path = [self getChatStorageItem];
    return [path stringByAppendingPathComponent:ZFChatStorage_RecorderVideoMP4_Item];
}

+ (NSString *)getChatStorageItem{
    NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
    return [[path stringByAppendingString:ZFChatStorage_Item] stringByAppendingString:@"/"];
}

+ (NSString *)zfChatStorageApis_randomString{
    CFUUIDRef puuid = CFUUIDCreate(nil);
    CFStringRef uuidString = CFUUIDCreateString(nil, puuid);
    NSString * result = (NSString *)CFBridgingRelease(CFStringCreateCopy( NULL, uuidString));
    CFRelease(puuid);
    CFRelease(uuidString);
    return result;
}

@end
