//
//  ZFChatStorageHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatStorageHelper : NSObject

+ (NSString *)zfChatStorageHelper_getChatVoiceRecorderPath;

+ (NSString *)zfChatStorageHelper_getTranslatedChatRecorderFilePathWithFileName:(NSString *)fileName;

+ (NSString *)zfChatStorageHelper_translateChatRecorderFilePathToRecorderAmrPath:(NSString *)wavPath;

+ (NSString *)zfChatStorageHelper_getChatVideoExportPath;

+ (NSString *)zfChatStorageHelper_getChatVideoExportPathTranslatedPath:(NSString *)exportPath;

+ (NSString *)zfChatStorageHelper_getMP4FileFullPath;

+ (BOOL)zfChatStorageHelper_convertAmrToWav:(NSString *)aAmrPath wavSavePath:(NSString *)aSavePath;

+ (BOOL)zfChatStorageHelper_convertWavToAmr:(NSString *)aWavPath amrSavePath:(NSString *)aSavePath;

+ (void)zfChatStorageHelper_convertMovToMP4WithMovPath:(NSString *)mp4Path savePath:(NSString *)path hanlde:(void(^) (BOOL success,NSString *path,NSString *errorStr))handle;
@end

NS_ASSUME_NONNULL_END
