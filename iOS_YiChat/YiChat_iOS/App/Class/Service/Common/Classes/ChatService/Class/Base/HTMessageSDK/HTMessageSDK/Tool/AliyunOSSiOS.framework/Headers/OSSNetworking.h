//
//  OSSNetworking.h
//  oss_ios_sdk
//
//  Created by zhouzhuo on 8/16/15.
//  Copyright (c) 2015 aliyun.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OSSModel.h"

@class OSSSyncMutableDictionary;
@class OSSNetworkingRequestDelegate;
@class OSSExecutor;

/**
 定义重试类型
 */
typedef NS_ENUM(NSInteger, OSSNetworkingRetryType) {
    OSSNetworkingRetryTypeUnknown,
    OSSNetworkingRetryTypeShouldRetry,
    OSSNetworkingRetryTypeShouldNotRetry,
    OSSNetworkingRetryTypeShouldRefreshCredentialsAndRetry,
    OSSNetworkingRetryTypeShouldCorrectClockSkewAndRetry
};

/**
 重试处理器
 */
@interface OSSURLRequestRetryHandler : NSObject
@property (nonatomic, assign) uint32_t maxRetryCount;

- (OSSNetworkingRetryType)shouldRetry:(uint32_t)currentRetryCount
                      requestDelegate:(OSSNetworkingRequestDelegate *)delegate
                             response:(NSHTTPURLResponse *)response
                                error:(NSError *)error;

- (NSTimeInterval)timeIntervalForRetry:(uint32_t)currentRetryCount
                             retryType:(OSSNetworkingRetryType)retryType;

+ (instancetype)defaultRetryHandler;
@end

/**
 网络参数设置
 */
@interface OSSNetworkingConfiguration : NSObject
@property (nonatomic, assign) uint32_t maxRetryCount;
@property (nonatomic, assign) uint32_t maxConcurrentRequestCount;
@property (nonatomic, assign) BOOL enableBackgroundTransmitService;
@property (nonatomic, strong) NSString * backgroundSessionIdentifier;
@property (nonatomic, assign) NSTimeInterval timeoutIntervalForRequest;
@property (nonatomic, assign) NSTimeInterval timeoutIntervalForResource;
@property (nonatomic, strong) NSString * proxyHost;
@property (nonatomic, strong) NSNumber * proxyPort;
@end

/**
 对操作发起的每一次请求构造一个信息代理
 */
@interface OSSNetworkingRequestDelegate : NSObject

@property (nonatomic, strong) NSMutableArray * interceptors;
@property (nonatomic, strong) OSSAllRequestNeededMessage * allNeededMessage;
@property (nonatomic, strong) NSMutableURLRequest * internalRequest;
@property (nonatomic, assign) OSSOperationType operType;
@property (nonatomic, assign) BOOL isAccessViaProxy;

@property (nonatomic, assign) BOOL isRequestCancelled;

@property (nonatomic, strong) OSSHttpResponseParser * responseParser;

@property (nonatomic, strong) NSData * uploadingData;
@property (nonatomic, strong) NSURL * uploadingFileURL;

@property (nonatomic, assign) int64_t payloadTotalBytesWritten;

@property (nonatomic, assign) BOOL isBackgroundUploadFileTask;
@property (nonatomic, assign) BOOL isHttpdnsEnable;

@property (nonatomic, strong) OSSURLRequestRetryHandler * retryHandler;
@property (nonatomic, assign) uint32_t currentRetryCount;
@property (nonatomic, strong) NSError * error;
@property (nonatomic, assign) BOOL isHttpRequestNotSuccessResponse;
@property (nonatomic, strong) NSMutableData * httpRequestNotSuccessResponseBody;

@property (atomic, strong) NSURLSessionDataTask * currentSessionTask;

@property (nonatomic, copy) OSSNetworkingUploadProgressBlock uploadProgress;
@property (nonatomic, copy) OSSNetworkingDownloadProgressBlock downloadProgress;
@property (nonatomic, copy) OSSNetworkingCompletionHandlerBlock completionHandler;
@property (nonatomic, copy) OSSNetworkingOnRecieveDataBlock onRecieveData;

- (OSSTask *)buildInternalHttpRequest;
- (void)reset;
- (void)cancel;
@end

/**
 包含一次网络请求所需的所有信息
 */
@interface OSSAllRequestNeededMessage : NSObject
@property (nonatomic, strong) NSString * endpoint;
@property (nonatomic, strong) NSString * httpMethod;
@property (nonatomic, strong) NSString * bucketName;
@property (nonatomic, strong) NSString * objectKey;
@property (nonatomic, strong) NSString * contentType;
@property (nonatomic, strong) NSString * contentMd5;
@property (nonatomic, strong) NSString * range;
@property (nonatomic, strong) NSString * date;
@property (nonatomic, strong) NSMutableDictionary * headerParams;
@property (nonatomic, strong) NSMutableDictionary * querys;

@property (nonatomic, assign) BOOL isHostInCnameExcludeList;

- (instancetype)initWithEndpoint:(NSString *)endpoint
                      httpMethod:(NSString *)httpMethod
                      bucketName:(NSString *)bucketName
                       objectKey:(NSString *)objectKey
                            type:(NSString *)contentType
                             md5:(NSString *)contentMd5
                           range:(NSString *)range
                            date:(NSString *)date
                    headerParams:(NSMutableDictionary *)headerParams
                          querys:(NSMutableDictionary *)querys;

- (OSSTask *)validateRequestParamsInOperationType:(OSSOperationType)operType;
@end

/**
 每个OSSClient持有一个OSSNetworking用以收发网络请求
 */
@interface OSSNetworking : NSObject <NSURLSessionDelegate>
@property (nonatomic, strong) NSURLSession * dataSession;
@property (nonatomic, strong) NSURLSession * uploadFileSession;
@property (nonatomic, assign) BOOL isUsingBackgroundSession;
@property (nonatomic, strong) OSSSyncMutableDictionary * sessionDelagateManager;
@property (nonatomic, strong) OSSNetworkingConfiguration * configuration;
@property (nonatomic, strong) OSSExecutor * taskExecutor;

- (instancetype)initWithConfiguration:(OSSNetworkingConfiguration *)configuration;
- (OSSTask *)sendRequest:(OSSNetworkingRequestDelegate *)request;
@end
