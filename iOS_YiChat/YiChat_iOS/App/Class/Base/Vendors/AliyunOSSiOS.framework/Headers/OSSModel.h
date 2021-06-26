//
//  OSSModel.h
//  oss_ios_sdk
//
//  Created by zhouzhuo on 8/16/15.
//  Copyright (c) 2015 aliyun.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@class OSSAllRequestNeededMessage;
@class OSSFederationToken;
@class OSSTask;

typedef NS_ENUM(NSInteger, OSSOperationType) {
    OSSOperationTypeGetService,
    OSSOperationTypeCreateBucket,
    OSSOperationTypeDeleteBucket,
    OSSOperationTypeGetBucket,
    OSSOperationTypeGetBucketACL,
    OSSOperationTypeHeadObject,
    OSSOperationTypeGetObject,
    OSSOperationTypePutObject,
    OSSOperationTypePutObjectACL,
    OSSOperationTypeAppendObject,
    OSSOperationTypeDeleteObject,
    OSSOperationTypeCopyObject,
    OSSOperationTypeInitMultipartUpload,
    OSSOperationTypeUploadPart,
    OSSOperationTypeCompleteMultipartUpload,
    OSSOperationTypeAbortMultipartUpload,
    OSSOperationTypeListMultipart
};

typedef NS_ENUM(NSInteger, OSSClientErrorCODE) {
    OSSClientErrorCodeNetworkingFailWithResponseCode0,
    OSSClientErrorCodeSignFailed,
    OSSClientErrorCodeFileCantWrite,
    OSSClientErrorCodeInvalidArgument,
    OSSClientErrorCodeNilUploadid,
    OSSClientErrorCodeTaskCancelled,
    OSSClientErrorCodeNetworkError,
    OSSClientErrorCodeCannotResumeUpload,
    OSSClientErrorCodeExcpetionCatched,
    OSSClientErrorCodeNotKnown
};

typedef void (^OSSNetworkingUploadProgressBlock) (int64_t bytesSent, int64_t totalBytesSent, int64_t totalBytesExpectedToSend);
typedef void (^OSSNetworkingDownloadProgressBlock) (int64_t bytesWritten, int64_t totalBytesWritten, int64_t totalBytesExpectedToWrite);
typedef void (^OSSNetworkingCompletionHandlerBlock) (id responseObject, NSError *error);
typedef void (^OSSNetworkingOnRecieveDataBlock) (NSData * data);

typedef NSString * (^OSSCustomSignContentBlock) (NSString * contentToSign, NSError **error);
typedef OSSFederationToken * (^OSSGetFederationTokenBlock) ();

/**
 扩展NSString
 */
@interface NSString (OSS)
- (NSString *)oss_stringByAppendingPathComponentForURL:(NSString *)aString;
- (NSString *)oss_trim;
@end

/**
 扩展NSDictionary
 */
@interface NSDictionary (OSS)
- (NSString *)base64JsonString;
@end

/**
 扩展NSDate
 */
@interface NSDate (OSS)
+ (void)oss_setClockSkew:(NSTimeInterval)clockSkew;
+ (NSDate *)oss_dateFromString:(NSString *)string;
+ (NSDate *)oss_clockSkewFixedDate;
- (NSString *)oss_asStringValue;
@end

/**
 线程安全的字典
 */
@interface OSSSyncMutableDictionary : NSObject
@property (nonatomic, strong) NSMutableDictionary *dictionary;
@property (nonatomic, strong) dispatch_queue_t dispatchQueue;

- (id)objectForKey:(id)aKey;
- (NSArray *)allKeys;
- (void)setObject:(id)anObject forKey:(id <NSCopying>)aKey;
- (void)removeObjectForKey:(id)aKey;
@end

/**
 FederationToken类
 */
@interface OSSFederationToken : NSObject
@property (nonatomic, strong) NSString * tAccessKey;
@property (nonatomic, strong) NSString * tSecretKey;
@property (nonatomic, strong) NSString * tToken;

/**
 指明Token的失效时间，为linux时间对应的毫秒数，即自UTC时间1970年1月1日经过的毫秒数
 */
@property (atomic, assign) int64_t expirationTimeInMilliSecond;

/**
 指明Token的失效时间，格式为GMT字符串，如: "2015-11-03T08:51:05Z"
 */
@property (atomic, strong) NSString * expirationTimeInGMTFormat;
@end

/**
 CredentialProvider协议，要求实现加签接口
 */
@protocol OSSCredentialProvider <NSObject>
@optional
- (NSString *)sign:(NSString *)content error:(NSError **)error;
@end

/**
 用明文AK/SK实现的加签器，建议只在测试模式时使用
 */
@interface OSSPlainTextAKSKPairCredentialProvider : NSObject <OSSCredentialProvider>
@property (nonatomic, strong) NSString * accessKey;
@property (nonatomic, strong) NSString * secretKey;

- (instancetype)initWithPlainTextAccessKey:(NSString *)accessKey
                                 secretKey:(NSString *)secretKey;
@end

/**
 用户自实现加签接口的加签器
 */
@interface OSSCustomSignerCredentialProvider : NSObject <OSSCredentialProvider>
@property (nonatomic, copy) NSString * (^signContent)(NSString *, NSError **);

- (instancetype)initWithImplementedSigner:(OSSCustomSignContentBlock)signContent;
@end

/**
 用户自实现的通过获取FederationToken来加签的加签器
 */
@interface OSSFederationCredentialProvider : NSObject <OSSCredentialProvider>
@property (nonatomic, strong) OSSFederationToken * cachedToken;
@property (nonatomic, copy) OSSFederationToken * (^federationTokenGetter)();

- (instancetype)initWithFederationTokenGetter:(OSSGetFederationTokenBlock)federationTokenGetter;
- (OSSFederationToken *)getToken:(NSError **)error;
@end

@interface OSSStsTokenCredentialProvider : NSObject <OSSCredentialProvider>
@property (nonatomic, strong) NSString * accessKeyId;
@property (nonatomic, strong) NSString * secretKeyId;
@property (nonatomic, strong) NSString * securityToken;

- (OSSFederationToken *)getToken;
- (instancetype)initWithAccessKeyId:(NSString *)accessKeyId
                        secretKeyId:(NSString *)secretKeyId
                      securityToken:(NSString *)securityToken;
@end

/**
 OSSClient可以设置的参数
 */
@interface OSSClientConfiguration : NSObject

/**
 最大重试次数
 */
@property (nonatomic, assign) uint32_t maxRetryCount;

/**
 最大并发请求数
 */
@property (nonatomic, assign) uint32_t maxConcurrentRequestCount;

/**
 是否开启后台传输服务
 注意：只在上传文件时有效
 */
@property (nonatomic, assign) BOOL enableBackgroundTransmitService;

/**
 是否使用Httpdns解析域名
 */
@property (nonatomic, assign) BOOL isHttpdnsEnable;

/**
 设置后台传输服务使用session的Id
 */
@property (nonatomic, strong) NSString * backgroundSesseionIdentifier;

/**
 请求超时时间
 */
@property (nonatomic, assign) NSTimeInterval timeoutIntervalForRequest;

/**
 单个Object下载的最长持续时间
 */
@property (nonatomic, assign) NSTimeInterval timeoutIntervalForResource;

/**
 设置代理Host、端口
 */
@property (nonatomic, strong) NSString * proxyHost;
@property (nonatomic, strong) NSNumber * proxyPort;

/**
 设置Cname排除列表
 */
@property (nonatomic, strong, setter=setCnameExcludeList:) NSArray * cnameExcludeList;

@end

@protocol OSSRequestInterceptor <NSObject>
- (OSSTask *)interceptRequestMessage:(OSSAllRequestNeededMessage *)request;
@end

/**
 构造请求过程中做加签
 */
@interface OSSSignerInterceptor : NSObject <OSSRequestInterceptor>
@property (nonatomic, strong) id<OSSCredentialProvider> credentialProvider;

- (instancetype)initWithCredentialProvider:(id<OSSCredentialProvider>)credentialProvider;
@end

/**
 构造请求过程中修改UA
 */
@interface OSSUASettingInterceptor : NSObject <OSSRequestInterceptor>
@end

/**
 构造请求过程中设置发起请求的标准时间
 */
@interface OSSTimeSkewedFixingInterceptor : NSObject <OSSRequestInterceptor>
@end

/**
 下载时指定范围
 */
@interface OSSRange : NSObject
@property (nonatomic, assign) int64_t startPosition;
@property (nonatomic, assign) int64_t endPosition;

- (instancetype)initWithStart:(int64_t)start
                      withEnd:(int64_t)end;

/**
 * 转换为字符串: 'bytes=${start}-${end}'
 */
- (NSString *)toHeaderString;
@end


#pragma mark RequestAndResultClass

/**
 请求头的基类
 */
@interface OSSRequest : NSObject
/**
 指明该请求是否需要鉴权，单次有效
 */
@property (nonatomic, assign) BOOL isAuthenticationRequired;

/**
 指明该请求是否已经被取消
 */
@property (nonatomic, assign) BOOL isCancelled;

/**
 取消这个请求
 */
- (void)cancel;
@end

/**
 请求结果的基类
 */
@interface OSSResult : NSObject

/**
 请求HTTP响应码
 */
@property (nonatomic, assign) NSInteger httpResponseCode;

/**
 请求HTTP响应头部，以KV形式放在字典中
 */
@property (nonatomic, strong) NSDictionary * httpResponseHeaderFields;

/**
 x-oss-request-id是由Aliyun OSS创建，并唯一标识这个response的UUID。如果在使用OSS服务时遇到问题，可以凭借该字段联系OSS工作人员，快速定位问题。
 */
@property (nonatomic, strong) NSString * requestId;
@end

/**
 罗列用户拥有的所有Bucket的请求。
 */
@interface OSSGetServiceRequest : OSSRequest

/**
 限定返回的bucket name必须以prefix作为前缀，可以不设定，不设定时不过滤前缀信息
 */
@property (nonatomic, strong) NSString * prefix;

/**
 设定结果从marker之后按字母排序的第一个开始返回，可以不设定，不设定时从头开始返回
 */
@property (nonatomic, strong) NSString * marker;

/**
 限定此次返回bucket的最大数，如果不设定，默认为100，max-keys取值不能大于1000
 */
@property (nonatomic, assign) int32_t maxKeys;


/**
 根据参数各字段构造URL中的查询串
 */
- (NSMutableDictionary *)getQueryDict;
@end

/**
 罗列用户拥有的所有Bucket的请求结果
 */
@interface OSSGetServiceResult : OSSResult

/**
 Bucket拥有者的用户ID
 */
@property (nonatomic, strong) NSString * ownerId;

/**
 Bucket拥有者的名称 (目前和ID一致)。
 */
@property (nonatomic, strong) NSString * ownerDispName;

/**
 本次查询结果的前缀，当bucket未全部返回时才有此节点
 */
@property (nonatomic, strong) NSString * prefix;

/**
 标明这次GetService(ListBucket)的起点，当bucket未全部返回时才有此节点
 */
@property (nonatomic, strong) NSString * marker;

/**
 响应请求内返回结果的最大数目，当bucket未全部返回时才有此节点
 */
@property (nonatomic, assign) int32_t maxKeys;

/**
 指明是否所有的结果都已经返回：“true”表示本次没有返回全部结果；“false”表示本次已经返回了全部结果。当bucket未全部返回时才有此节点。
 */
@property (nonatomic, assign) BOOL isTruncated;

/**
 表示下一次GetService(ListBucket)可以以此为marker，将未返回的结果返回。当bucket未全部返回时才有此节点。
 */
@property (nonatomic, strong) NSString * nextMarker;

/**
 保存bucket信息的容器，结构上是一个数组，数组每个元素是一个字典，字典的key有 ["Name", "CreationDate", "Location" ]
 */
@property (nonatomic, strong) NSArray * buckets;
@end

/**
 创建Bucket的请求
 */
@interface OSSCreateBucketRequest : OSSRequest

/**
 要创建的Bucket的名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 指定Bucket所在的数据中心。
 关于数据中心和终端域名的更多内容，参见访问域名和数据中心<a>https://docs.aliyun.com/#/pub/oss/product-documentation/domain-region</a>
 */
@property (nonatomic, strong) NSString * location;

/**
 设置Bucket 访问权限。目前Bucket有三种访问权限：public-read-write，public-read和private。
 */
@property (nonatomic, strong) NSString * xOssACL;
@end

/**
 创建Bucket的请求结果
 */
@interface OSSCreateBucketResult : OSSResult

/**
 Bucket所在的数据中心
 */
@property (nonatomic, strong) NSString * location;
@end

/**
 删除Bucket的请求
 */
@interface OSSDeleteBucketRequest : OSSRequest

/**
 Bucket的名称
 */
@property (nonatomic, strong) NSString * bucketName;
@end

/**
 删除Bucket的请求结果
 */
@interface OSSDeleteBucketResult : OSSResult
@end

/**
 罗列Bucket中Objects的请求
 */
@interface OSSGetBucketRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 是一个用于对Object名字进行分组的字符。所有名字包含指定的前缀且第一次出现delimiter字符之间的object作为一组元素——CommonPrefixes。
 */
@property (nonatomic, strong) NSString * delimiter;

/**
 设定结果从marker之后按字母排序的第一个开始返回。
 */
@property (nonatomic, strong) NSString * marker;

/**
 限定此次返回object的最大数，如果不设定，默认为100，max-keys取值不能大于1000。
 */
@property (nonatomic, assign) int32_t maxKeys;

/**
 限定返回的object key必须以prefix作为前缀。注意使用prefix查询时，返回的key中仍会包含prefix。
 */
@property (nonatomic, strong) NSString * prefix;

/**
 根据请求的各个字段生成URL中的查询串
 */
- (NSMutableDictionary *)getQueryDict;
@end

/**
 罗列Bucket中Objects的请求结果
 */
@interface OSSGetBucketResult : OSSResult

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 限定返回的object key必须以prefix作为前缀。注意使用prefix查询时，返回的key中仍会包含prefix。
 */
@property (nonatomic, strong) NSString * prefix;

/**
 设定结果从marker之后按字母排序的第一个开始返回。
 */
@property (nonatomic, strong) NSString * marker;

/**
 限定此次返回object的最大数，如果不设定，默认为100，max-keys取值不能大于1000。
 */
@property (nonatomic, assign) int32_t maxKeys;

/**
 是一个用于对Object名字进行分组的字符。所有名字包含指定的前缀且第一次出现delimiter字符之间的object作为一组元素——CommonPrefixes。
 */
@property (nonatomic, strong) NSString * delimiter;

/**
 如果因为max-keys的设定无法一次完成listing，返回结果会附加一个<NextMarker>，提示继续listing可以以此为marker。
 NextMarker中的值仍在list结果之中。
 */
@property (nonatomic, strong) NSString * nextMarker;

/**
 指明是否所有的结果都已经返回； “true”表示本次没有返回全部结果；“false”表示本次已经返回了全部结果。
 */
@property (nonatomic, assign) BOOL isTruncated;

/**
 装载文件信息的容器，结构为一个数组，数组中元素是一个个字典，代表每个文件，字典的key有 [ "Key", "LastModified", "ETag", "Type", "Size", "StorageClass", "Owner" ]
 */
@property (nonatomic, strong) NSArray * contents;

/**
 装载公共前缀信息的容器，结构为一个数组，数组中元素是NSString，每个代表一个前缀
 */
@property (nonatomic, strong) NSArray * commentPrefixes;
@end

/**
 获取指定Bucket的读写权限
 */
@interface OSSGetBucketACLRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;
@end

/**
 获取指定Bucket的ACL的请求结果
 */
@interface OSSGetBucketACLResult : OSSResult

/**
 获取到的Bucket的ACL，有 private/public-read/public-read-write
 */
@property (nonatomic, strong) NSString * aclGranted;
@end

/**
 获取Object Meta信息的请求
 */
@interface OSSHeadObjectRequest : OSSRequest

/**
 Object所在Bucket的名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;
@end

/**
 获取Object Meta信息的结果
 */
@interface OSSHeadObjectResult : OSSResult

/**
 Obejct的Meta信息
 */
@property (nonatomic, strong) NSDictionary * objectMeta;
@end

/**
 下载Object的请求头
 */
@interface OSSGetObjectRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 指定文件传输的范围。如，设定 bytes=0-9，表示传送第0到第9这10个字符。
 */
@property (nonatomic, strong) OSSRange * range;

/**
 如果希望Object直接下载到文件中，通过这个字段指明文件地址
 */
@property (nonatomic, strong) NSURL * downloadToFileURL;

/**
 图片处理配置
 */
@property (nonatomic, strong) NSString * xOssProcess;

/**
 回调下载进度
 */
@property (nonatomic, copy) OSSNetworkingDownloadProgressBlock downloadProgress;

/**
 Object下载过程中，会在接收每一段数据后回调这个Block
 */
@property (nonatomic, copy) OSSNetworkingOnRecieveDataBlock onRecieveData;
@end

/**
 下载Object的请求结果
 */
@interface OSSGetObjectResult : OSSResult

/**
 如果下载时未指明下载到文件，那么Object会被下载到内存中，类型为NSData
 */
@property (nonatomic, strong) NSData * downloadedData;

/**
 下载文件时的HTTP响应头的KV字典
 */
@property (nonatomic, strong) NSDictionary * objectMeta;
@end

/**
 修改Object的访问权限请求头
 */
@interface OSSPutObjectACLRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 */
@property (nonatomic, strong) NSString * acl;

@end

/**
 修改Object的访问权限响应
 */
@interface OSSPutObjectACLResult : OSSResult
@end

/**
 上传Object的请求头
 */
@interface OSSPutObjectRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 从内存中的NSData上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSData * uploadingData;

/**
 从文件上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSURL * uploadingFileURL;

/**
 server回调参数设置
 */
@property (nonatomic, strong) NSDictionary * callbackParam;

/**
 server回调变量设置
 */
@property (nonatomic, strong) NSDictionary * callbackVar;

/**
 设置文件类型
 */
@property (nonatomic, strong) NSString * contentType;

/**
 根据协议RFC 1864对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码为一个消息的Content-MD5值。
 该请求头可用于消息合法性的检查（消息内容是否与发送时一致）。虽然该请求头是可选项，OSS建议用户使用该请求头进行端到端检查。
 */
@property (nonatomic, strong) NSString * contentMd5;

/**
 指定该Object被下载时的名称；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentDisposition;

/**
 指定该Object被下载时的内容编码格式；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentEncoding;

/**
 指定该Object被下载时的网页的缓存行为；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * cacheControl;

/**
 过期时间（milliseconds）；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * expires;

/**
 可以在这个字段中携带以x-oss-meta-为前缀的参数，则视为user meta，比如x-oss-meta-location。一个Object可以有多个类似的参数，但所有的user meta总大小不能超过8k。
 如果上传时还需要指定其他HTTP请求头字段，也可以在这里设置
 */
@property (nonatomic, strong) NSDictionary * objectMeta;

/**
 上传进度回调
 */
@property (nonatomic, copy) OSSNetworkingUploadProgressBlock uploadProgress;
@end

/**
 上传Object的请求结果
 */
@interface OSSPutObjectResult : OSSResult

/**
 ETag (entity tag) 在每个Object生成的时候被创建，用于标示一个Object的内容。
 对于Put Object请求创建的Object，ETag值是其内容的MD5值；对于其他方式创建的Object，ETag值是其内容的UUID。
 ETag值可以用于检查Object内容是否发生变化。
 */
@property (nonatomic, strong) NSString * eTag;

/**
 如果设置了server回调，回调的响应内容
 */
@property (nonatomic, strong) NSString * serverReturnJsonString;
@end

/**
 * append object request
 */
@interface OSSAppendObjectRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 指定从何处进行追加。首次追加操作的position必须为0，后续追加操作的position是Object的当前长度。
 例如，第一次Append Object请求指定position值为0，content-length是65536；那么，第二次Append Object需要指定position为65536。
 每次操作成功后，响应头部x-oss-next-append-position也会标明下一次追加的position。
 */
@property (nonatomic, assign) int64_t appendPosition;

/**
 从内存中的NSData上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSData * uploadingData;

/**
 从文件上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSURL * uploadingFileURL;

/**
 设置文件类型
 */
@property (nonatomic, strong) NSString * contentType;

/**
 根据协议RFC 1864对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码为一个消息的Content-MD5值。
 该请求头可用于消息合法性的检查（消息内容是否与发送时一致）。虽然该请求头是可选项，OSS建议用户使用该请求头进行端到端检查。
 */
@property (nonatomic, strong) NSString * contentMd5;

/**
 指定该Object被下载时的名称；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentDisposition;

/**
 指定该Object被下载时的内容编码格式；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentEncoding;

/**
 指定该Object被下载时的网页的缓存行为；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * cacheControl;

/**
 过期时间（milliseconds）；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * expires;

/**
 可以在这个字段中携带以x-oss-meta-为前缀的参数，则视为user meta，比如x-oss-meta-location。一个Object可以有多个类似的参数，但所有的user meta总大小不能超过8k。
 如果上传时还需要指定其他HTTP请求头字段，也可以在这里设置
 */
@property (nonatomic, strong) NSDictionary * objectMeta;

/**
 上传进度回调
 */
@property (nonatomic, copy) OSSNetworkingUploadProgressBlock uploadProgress;
@end

/**
 * append object result
 */
@interface OSSAppendObjectResult : OSSResult

/**
 ETag (entity tag) 在每个Object生成的时候被创建，用于标示一个Object的内容。
 对于Put Object请求创建的Object，ETag值是其内容的MD5值；对于其他方式创建的Object，ETag值是其内容的UUID。
 ETag值可以用于检查Object内容是否发生变化。
 */
@property (nonatomic, strong) NSString * eTag;

/**
 指明下一次请求应当提供的position。实际上就是当前Object长度。
 当Append Object成功返回，或是因position和Object长度不匹配而引起的409错误时，会包含此header。
 */
@property (nonatomic, assign, readwrite) int64_t xOssNextAppendPosition;
@end

/**
 删除指定Object
 */
@interface OSSDeleteObjectRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;
@end

/**
 删除指定Object的响应
 */
@interface OSSDeleteObjectResult : OSSResult
@end

/**
 复制一个Object的请求
 */
@interface OSSCopyObjectRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 复制源地址（必须有可读权限）
 */
@property (nonatomic, strong) NSString * sourceCopyFrom;

/**
 设置文件类型
 */
@property (nonatomic, strong) NSString * contentType;

/**
 根据协议RFC 1864对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码为一个消息的Content-MD5值。
 该请求头可用于消息合法性的检查（消息内容是否与发送时一致）。虽然该请求头是可选项，OSS建议用户使用该请求头进行端到端检查。
 */
@property (nonatomic, strong) NSString * contentMd5;

/**
 可以在这个字段中携带以x-oss-meta-为前缀的参数，则视为user meta，比如x-oss-meta-location。一个Object可以有多个类似的参数，但所有的user meta总大小不能超过8k。
 如果上传时还需要指定其他HTTP请求头字段，也可以在这里设置
 */
@property (nonatomic, strong) NSDictionary * objectMeta;
@end

/**
 复制Object的请求结果
 */
@interface OSSCopyObjectResult : OSSResult

/**
 新Object最后更新时间。
 */
@property (nonatomic, strong) NSString * lastModifed;

/**
 新Object的ETag值。
 */
@property (nonatomic, strong) NSString * eTag;
@end

/**
 初始化分块上传的请求
 */
@interface OSSInitMultipartUploadRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 设置文件类型
 */
@property (nonatomic, strong) NSString * contentType;

/**
 指定该Object被下载时的名称；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentDisposition;

/**
 指定该Object被下载时的内容编码格式；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * contentEncoding;

/**
 指定该Object被下载时的网页的缓存行为；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * cacheControl;

/**
 过期时间（milliseconds）；更详细描述请参照RFC2616。
 */
@property (nonatomic, strong) NSString * expires;

/**
 可以在这个字段中携带以x-oss-meta-为前缀的参数，则视为user meta，比如x-oss-meta-location。一个Object可以有多个类似的参数，但所有的user meta总大小不能超过8k。
 如果上传时还需要指定其他HTTP请求头字段，也可以在这里设置
 */
@property (nonatomic, strong) NSDictionary * objectMeta;
@end

/**
 初始化分块上传的请求结果
 */
@interface OSSInitMultipartUploadResult : OSSResult

/**
 唯一标示此次Multipart Upload事件的ID。
 */
@property (nonatomic, strong) NSString * uploadId;
@end

/**
 上传单个分块的请求
 */
@interface OSSUploadPartRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectkey;

/**
 唯一标示此次Multipart Upload事件的ID。
 */
@property (nonatomic, strong) NSString * uploadId;

/**
 指定本次上传分块的标识号码
 */
@property (nonatomic, assign) int partNumber;

/**
 根据协议RFC 1864对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码为一个消息的Content-MD5值。
 该请求头可用于消息合法性的检查（消息内容是否与发送时一致）。虽然该请求头是可选项，OSS建议用户使用该请求头进行端到端检查。
 */
@property (nonatomic, strong) NSString * contentMd5;

/**
 从内存中的NSData上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSData * uploadPartData;

/**
 从文件上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSURL * uploadPartFileURL;

/**
 上传进度回调
 */
@property (nonatomic, copy) OSSNetworkingUploadProgressBlock uploadPartProgress;
@end

/**
 上传单个分块的结果
 */
@interface OSSUploadPartResult : OSSResult
@property (nonatomic, strong) NSString * eTag;
@end

/**
 分块上传中每个分块的信息，这些信息将会在调用‘完成分块上传’的接口中使用
 */
@interface OSSPartInfo : NSObject

/**
 指定本次上传分块的标识号码
 */
@property (nonatomic, assign) int32_t partNum;

/**
 Part成功上传后，OSS返回的ETag值。
 */
@property (nonatomic, strong) NSString * eTag;

/**
 分块数据长度
 */
@property (nonatomic, assign) int64_t size;

+ (instancetype)partInfoWithPartNum:(int32_t)partNum
                               eTag:(NSString *)eTag
                               size:(int64_t)size;
@end

/**
 完成分块上传请求
 */
@interface OSSCompleteMultipartUploadRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 唯一标示此次Multipart Upload事件的ID。
 */
@property (nonatomic, strong) NSString * uploadId;

/**
 根据协议RFC 1864对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码为一个消息的Content-MD5值。
 该请求头可用于消息合法性的检查（消息内容是否与发送时一致）。虽然该请求头是可选项，OSS建议用户使用该请求头进行端到端检查。
 */
@property (nonatomic, strong) NSString * contentMd5;

/**
 各个分块的信息
 */
@property (nonatomic, strong) NSArray * partInfos;

/**
 server回调参数设置
 */
@property (nonatomic, strong) NSDictionary * callbackParam;

/**
 server回调变量设置
 */
@property (nonatomic, strong) NSDictionary * callbackVar;

/**
 完成分块上传附带的请求头
 */
@property (nonatomic, strong) NSDictionary * completeMetaHeader;
@end

/**
 完成分块上传请求的结果
 */
@interface OSSCompleteMultipartUploadResult : OSSResult

/**
 新创建Object的URL。
 */
@property (nonatomic, strong) NSString * location;

/**
 ETag (entity tag) 在每个Object生成的时候被创建，用于标示一个Object的内容。
 Complete Multipart Upload请求创建的Object，ETag值是其内容的UUID。ETag值可以用于检查Object内容是否发生变化。.
 */
@property (nonatomic, strong) NSString * eTag;

/**
 如果设置了server回调，回调的响应内容
 */
@property (nonatomic, strong) NSString * serverReturnJsonString;
@end

/**
 罗列某次分块上传事件已经上传的分块请求
 */
@interface OSSListPartsRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 唯一标示此次Multipart Upload事件的ID。
 */
@property (nonatomic, strong) NSString * uploadId;

/**
 返回请求中最大的Part数目。
 */
@property (nonatomic, assign) int maxParts;

/**
 指定List的起始位置，只有Part Number数目大于该参数的Part会被列出。
 */
@property (nonatomic, assign) int partNumberMarker;
@end

/**
 罗列分块请求的结果
 */
@interface OSSListPartsResult : OSSResult

/**
 如果本次没有返回全部结果，响应请求中将包含NextPartNumberMarker元素，用于标明接下来请求的PartNumberMarker值。
 */
@property (nonatomic, assign) int nextPartNumberMarker;

/**
 返回请求中最大的Part数目。
 */
@property (nonatomic, assign) int maxParts;

/**
 标明是否本次返回的List Part结果列表被截断。“true”表示本次没有返回全部结果；“false”表示本次已经返回了全部结果。
 */
@property (nonatomic, assign) BOOL isTruncated;

/**
 保存Part信息的容器。
 */
@property (nonatomic, strong) NSArray * parts;
@end

/**
 取消分块上传事件请求
 */
@interface OSSAbortMultipartUploadRequest : OSSRequest

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 唯一标示此次Multipart Upload事件的ID。
 */
@property (nonatomic, strong) NSString * uploadId;
@end

/**
 取消分块上传事件的结果
 */
@interface OSSAbortMultipartUploadResult : OSSResult
@end

/**
 断点续传请求
 */
@interface OSSResumableUploadRequest : OSSRequest

/**
 一个续传事件对应着同一个唯一的UploadId
 */
@property (nonatomic, strong) NSString * uploadId;

/**
 Bucket名称
 */
@property (nonatomic, strong) NSString * bucketName;

/**
 Object名称
 */
@property (nonatomic, strong) NSString * objectKey;

/**
 从文件上传时，通过这个字段设置
 */
@property (nonatomic, strong) NSURL * uploadingFileURL;

/**
 自定义分块大小，最小100KB
 */
@property (nonatomic, assign) int64_t partSize;

/**
 上传进度
 */
@property (nonatomic, copy) OSSNetworkingUploadProgressBlock uploadProgress;

/**
 server回调参数设置
 */
@property (nonatomic, strong) NSDictionary * callbackParam;

/**
 server回调变量设置
 */
@property (nonatomic, strong) NSDictionary * callbackVar;

/**
 完成分块上传附带的请求头
 */
@property (nonatomic, strong) NSDictionary * completeMetaHeader;

/**
 当前正在处理的子请求
 */
@property (atomic, weak) OSSRequest * runningChildrenRequest;

- (void)cancel;
@end

/**
 断点续传的结果
 */
@interface OSSResumableUploadResult : OSSResult
/**
 如果设置了server回调，回调的响应内容
 */
@property (nonatomic, strong) NSString * serverReturnJsonString;
@end

#pragma mark 其他

/**
 HTTP响应内容解析器
 */
@interface OSSHttpResponseParser : NSObject
@property (nonatomic, strong) NSURL * downloadingFileURL;
@property (nonatomic, copy) OSSNetworkingOnRecieveDataBlock onRecieveBlock;

- (instancetype)initForOperationType:(OSSOperationType)operationType;
- (void)consumeHttpResponse:(NSHTTPURLResponse *)response;
- (OSSTask *)consumeHttpResponseBody:(NSData *)data;
- (id)constructResultObject;
- (void)reset;
@end