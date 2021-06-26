//
//  ZFChatStorageManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatStorageManager : NSObject

+ (id)sharedManager;

- (NSString *)zfChatStorageManager_getVoiceRecorderPath;

- (NSString *)zfChatStorageManager_getVoiceRecorderItemPath;

- (NSString *)zfChatStorageManager_getVoiceRecorderTranslatedItemPath;

- (NSString *)zfChatStorageManager_getVideoRecorderMOVPath;

- (NSString *)zfChatStorageManager_getVideoRecorderMP4ItemPath;

- (NSString *)productFileName;

- (BOOL)convertWavFile:(NSString *)wavFilePath toAmrPath:(NSString *)amrPath;

- (BOOL)convertAmrFile:(NSString *)amrFilePath toWavPath:(NSString *)wavPath;
@end

NS_ASSUME_NONNULL_END
