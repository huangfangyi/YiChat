//
//  ZFChatStorageApis.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatStorageApis : NSObject

/*
 /libary/cache/ZFChat
 */
+ (NSString *)videoStorageApis_getRecorderVoiceStoragePath;

+ (NSString *)videoStorageApis_getRecorderTranslatedVoiceStoragePath;

+ (NSString *)videoStorageApis_getRecorderVideoMOVStoragePath;

+ (NSString *)videoStorageApis_getRecorderVideoMP4StoragePath;

+ (NSString *)zfChatStorageApis_randomString;

@end

NS_ASSUME_NONNULL_END
