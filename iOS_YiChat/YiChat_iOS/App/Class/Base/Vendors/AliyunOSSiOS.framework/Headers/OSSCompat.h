//
//  OSSCompat.h
//  oss_ios_sdk_new
//
//  Created by zhouzhuo on 9/10/15.
//  Copyright (c) 2015 aliyun.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OSSService.h"

@class OSSCancellationTokenSource;

typedef OSSCancellationTokenSource OSSTaskHandler;

@interface OSSClient (Compat)

/**
 兼容老版本用法的上传数据接口
 建议更换使用：putObject
 */
- (OSSTaskHandler *)uploadData:(NSData *)data
               withContentType:(NSString *)contentType
                withObjectMeta:(NSDictionary *)meta
                  toBucketName:(NSString *)bucketName
                   toObjectKey:(NSString *)objectKey
                   onCompleted:(void(^)(BOOL, NSError *))onCompleted
                    onProgress:(void(^)(float progress))onProgress;

/**
 兼容老版本用法的下载数据接口
 建议更换使用：getObject
 */
- (OSSTaskHandler *)downloadToDataFromBucket:(NSString *)bucketName
                   objectKey:(NSString *)objectKey
                 onCompleted:(void(^)(NSData *, NSError *))onCompleted
                  onProgress:(void(^)(float progress))onProgress;

/**
 兼容老版本用法的上传文件接口
 建议更换使用：putObject
 */
- (OSSTaskHandler *)uploadFile:(NSString *)filePath
                withContentType:(NSString *)contentType
                 withObjectMeta:(NSDictionary *)meta
                   toBucketName:(NSString *)bucketName
                    toObjectKey:(NSString *)objectKey
                    onCompleted:(void(^)(BOOL, NSError *))onCompleted
                     onProgress:(void(^)(float progress))onProgress;

/**
 兼容老版本用法的下载文件接口
 建议更换使用：getObject
 */
- (OSSTaskHandler *)downloadToFileFromBucket:(NSString *)bucketName
                  objectKey:(NSString *)objectKey
                     toFile:(NSString *)filePath
                onCompleted:(void(^)(BOOL, NSError *))onCompleted
                 onProgress:(void(^)(float progress))onProgress;


/**
 兼容老版本用法的断点上传文件接口
 建议更换使用：resumableUpload
 */
- (OSSTaskHandler *)resumableUploadFile:(NSString *)filePath
          withContentType:(NSString *)contentType
           withObjectMeta:(NSDictionary *)meta
             toBucketName:(NSString *)bucketName
              toObjectKey:(NSString *)objectKey
              onCompleted:(void(^)(BOOL, NSError *))onCompleted
               onProgress:(void(^)(float progress))onProgress;

/**
 兼容老版本用法的删除Object接口
 建议更换使用：deleteObject
 */
- (void)deleteObjectInBucket:(NSString *)bucketName
                   objectKey:(NSString *)objectKey
                 onCompleted:(void(^)(BOOL, NSError *))onCompleted;
@end