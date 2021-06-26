//
//  ProjectStorageApis.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/29.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectStorageApis : NSObject

/**
 *  创建文件夹
 */
+ (BOOL)projectStorageApis_CreateItemWithPath:(NSString *)path;

/**
 *  获取目录下所有文件
 */
+ (NSArray *)projectStorageApis_GetAllFilesNameIntoItem:(NSString *)path;

/**
 *  判断文件是否存在
 */
+ (BOOL)projectStorageApis_JudgeFileIsExistsInPath:(NSString *)path;

/**
 *  判断文件夹是否存在
 */
+ (BOOL)projectStorageApis_JudgeItemIsExistsInPath:(NSString *)path;

+ (BOOL)projectStorageApis_removeItemAtPath:(NSString *)path;


+ (NSString *)projectStorageApis_getHomeDirectoryPath;

+ (NSString *)projectStorageApis_getTempDirectoryPath;

+ (NSString *)projectStorageApis_getDocumentDirecoryPath;

+ (NSString *)projectStorageApis_getPeferenceDirecoryPath;

+ (NSString *)projectStorageApis_getLibraryDirectoryPath;

+ (NSString *)projectStorageApis_getCacheDirectoryPath;

@end

NS_ASSUME_NONNULL_END
