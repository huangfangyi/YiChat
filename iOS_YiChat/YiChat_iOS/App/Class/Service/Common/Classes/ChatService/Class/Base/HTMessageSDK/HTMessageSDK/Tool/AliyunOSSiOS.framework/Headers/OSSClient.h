//
//  OSSClient.h
//  oss_ios_sdk
//
//  Created by zhouzhuo on 8/16/15.
//  Copyright (c) 2015 aliyun.com. All rights reserved.
//

#import <Foundation/Foundation.h>
@class OSSGetServiceRequest;
@class OSSCreateBucketRequest;
@class OSSDeleteBucketRequest;
@class OSSHeadObjectRequest;
@class OSSGetBucketRequest;
@class OSSGetBucketACLRequest;
@class OSSGetObjectRequest;
@class OSSPutObjectRequest;
@class OSSPutObjectACLRequest;
@class OSSDeleteObjectRequest;
@class OSSCopyObjectRequest;
@class OSSInitMultipartUploadRequest;
@class OSSUploadPartRequest;
@class OSSCompleteMultipartUploadRequest;
@class OSSListPartsRequest;
@class OSSAbortMultipartUploadRequest;
@class OSSAppendObjectRequest;
@class OSSResumableUploadRequest;
@class OSSTask;
@class OSSExecutor;

@class OSSNetworking;
@class OSSClientConfiguration;
@protocol OSSCredentialProvider;

/**
 OSSClient是OSS服务的iOS客户端，它为调用者提供了一系列的方法，用于和OSS服务进行交互。
 一般来说，全局内只需要保持一个OSSClient，用来调用各种操作。
 */
@interface OSSClient : NSObject

/**
 OSS访问域名
 */
@property (nonatomic, strong) NSString * endpoint;

/**
 用以收发网络请求
 */
@property (nonatomic, strong) OSSNetworking * networking;

/**
 提供访问所需凭证
 */
@property (nonatomic, strong) id<OSSCredentialProvider> credentialProvider;

/**
 客户端设置
 */
@property (nonatomic, strong) OSSClientConfiguration * clientConfiguration;

/**
 任务队列
 */
@property (nonatomic, strong, readonly) OSSExecutor * ossOperationExecutor;

/**
 初始化OSSClient，使用默认的本地设置
 @endpoint 指明Bucket所在的Region域名
 @credentialProvider 需要实现的签名器
 */
- (instancetype)initWithEndpoint:(NSString *)endpoint
              credentialProvider:(id<OSSCredentialProvider>) credentialProvider;

/**
 初始化OSSClient，使用自定义设置
 @endpoint 指明Bucket所在的Region域名
 @credentialProvider 需要实现的签名器
 @conf 可以设置一些本地参数如重试次数、超时时间等
 */
- (instancetype)initWithEndpoint:(NSString *)endpoint
              credentialProvider:(id<OSSCredentialProvider>)credentialProvider
             clientConfiguration:(OSSClientConfiguration *)conf;

#pragma mark restful-api

/**
 对应RESTFul API：GetService
 获取请求者当前拥有的全部Bucket。
 注意：
 1. 尚不支持STS;
 2. 当所有的bucket都返回时，返回的xml中不包含Prefix、Marker、MaxKeys、IsTruncated、NextMarker节点，如果还有部分结果未返回，则增加上述节点，其中NextMarker用于继续查询时给marker赋值。
 */
- (OSSTask *)getService:(OSSGetServiceRequest *)request;

/**
 对应RESTFul API：PutBucket
 用于创建Bucket（不支持匿名访问）。默认情况下，创建的Bucket位于默认的数据中心：oss-cn-hangzhou。
 用户可以显式指定Bucket位于的数据中心，从而最优化延迟，最小化费用或者满足监管要求等。
 注意：
 1. 尚不支持STS。
 */
- (OSSTask *)createBucket:(OSSCreateBucketRequest *)request;

/**
 对应RESTFul API：DeleteBucket
 用于删除某个Bucket。
 */
- (OSSTask *)deleteBucket:(OSSDeleteBucketRequest *)request;

/**
 对应RESTFul API：GetBucket
 用来list Bucket中所有Object的信息，可以通过prefix，marker，delimiter和max-keys对list做限定，返回部分结果。
 */
- (OSSTask *)getBucket:(OSSGetBucketRequest *)request;

/**
 对应RESTFul API：GetBucketACL
 用来获取某个Bucket的访问权限。
 */
- (OSSTask *)getBucketACL:(OSSGetBucketACLRequest *)request;

/**
 对应RESTFul API：HeadObject
 只返回某个Object的meta信息，不返回文件内容。
 */
- (OSSTask *)headObject:(OSSHeadObjectRequest *)request;

/**
 对应RESTFul API：GetObject
 用于获取某个Object，此操作要求用户对该Object有读权限。
 */
- (OSSTask *)getObject:(OSSGetObjectRequest *)request;

/**
 对应RESTFul API：PutObject
 用于上传文件。
 */
- (OSSTask *)putObject:(OSSPutObjectRequest *)request;

/**
 Put Object ACL接口用于修改Object的访问权限。目前Object有三种访问权限：private, public-read, public-read-write。
 Put Object ACL操作通过Put请求中的“x-oss-object-acl”头来设置，这个操作只有Bucket Owner有权限执行。如果操作成功，则返回200；否则返回相应的错误码和提示信息。
 */
- (OSSTask *)putObjectACL:(OSSPutObjectACLRequest *)request;

/**
 对应RESTFul API：AppendObject
 以追加写的方式上传文件。通过Append Object操作创建的Object类型为Appendable Object，而通过Put Object上传的Object是Normal Object。
 */
- (OSSTask *)appendObject:(OSSAppendObjectRequest *)request;

/**
 对应RESTFul API：copyObject
 拷贝一个在OSS上已经存在的object成另外一个object，可以发送一个PUT请求给OSS，并在PUT请求头中添加元素“x-oss-copy-source”来指定拷贝源。
 OSS会自动判断出这是一个Copy操作，并直接在服务器端执行该操作。如果拷贝成功，则返回新的object信息给用户。
 该操作适用于拷贝小于1GB的文件。
 */
- (OSSTask *)copyObject:(OSSCopyObjectRequest *)request;

/**
 对应RESTFul API：DeleteObject
 用于删除某个Object。
 */
- (OSSTask *)deleteObject:(OSSDeleteObjectRequest *)request;

/**
 对应RESTFul API：InitiateMultipartUpload
 使用Multipart Upload模式传输数据前，必须先调用该接口来通知OSS初始化一个Multipart Upload事件。该接口会返回一个OSS服务器创建的全局唯一的Upload ID，用于标识本次Multipart Upload事件。
 用户可以根据这个ID来发起相关的操作，如中止Multipart Upload、查询Multipart Upload等。
 */
- (OSSTask *)multipartUploadInit:(OSSInitMultipartUploadRequest *)request;

/**
 对应RESTFul API：UploadPart
 初始化一个Multipart Upload之后，可以根据指定的Object名和Upload ID来分块（Part）上传数据。
 每一个上传的Part都有一个标识它的号码（part number，范围是1~10,000）。
 对于同一个Upload ID，该号码不但唯一标识这一块数据，也标识了这块数据在整个文件内的相对位置。
 如果你用同一个part号码，上传了新的数据，那么OSS上已有的这个号码的Part数据将被覆盖。除了最后一块Part以外，其他的part最小为100KB；
 最后一块Part没有大小限制。
 */
- (OSSTask *)uploadPart:(OSSUploadPartRequest *)request;

/**
 对应RESTFul API：CompleteMultipartUpload
 在将所有数据Part都上传完成后，必须调用Complete Multipart Upload API来完成整个文件的Multipart Upload。
 在执行该操作时，用户必须提供所有有效的数据Part的列表（包括part号码和ETAG）；OSS收到用户提交的Part列表后，会逐一验证每个数据Part的有效性。
 当所有的数据Part验证通过后，OSS将把这些数据part组合成一个完整的Object。
 */
- (OSSTask *)completeMultipartUpload:(OSSCompleteMultipartUploadRequest *)request;

/**
 对应RESTFul API：ListParts
 可以罗列出指定Upload ID所属的所有已经上传成功Part。
 */
- (OSSTask *)listParts:(OSSListPartsRequest *)request;

/**
 对应RESTFul API：AbortMultipartUpload
 该接口可以根据用户提供的Upload ID中止其对应的Multipart Upload事件。
 当一个Multipart Upload事件被中止后，就不能再使用这个Upload ID做任何操作，已经上传的Part数据也会被删除。
 */
- (OSSTask *)abortMultipartUpload:(OSSAbortMultipartUploadRequest *)request;

#pragma mark extention method

/**
 对一个Object签名出一个URL，可以把该URL转给第三方实现授权访问。
 @bucketName Object所在的Bucket名称
 @objectKey Object名称
 @interval 签名URL时，可以指定这个URL的有效时长是多久，单位是秒，比如说需要有效时长为1小时的URL，这里传入3600
 */
- (OSSTask *)presignConstrainURLWithBucketName:(NSString *)bucketName
                                withObjectKey:(NSString *)objectKey
                       withExpirationInterval:(NSTimeInterval)interval;

/**
 如果Object的权限是公共读或者公共读写，调用这个接口对该Object签名出一个URL，可以把该URL转给第三方实现授权访问。
 @bucketName Object所在的Bucket名称
 @objectKey Object名称
 */
- (OSSTask *)presignPublicURLWithBucketName:(NSString *)bucketName
                            withObjectKey:(NSString *)objectKey;

/**
 断点上传接口
 这个接口封装了分块上传的若干接口以实现断点上传，但是需要用户自行保存UploadId。
 对一个新文件，用户需要首先调用multipartUploadInit接口获得一个UploadId，然后调用此接口上传这个文件。
 如果上传失败，首先需要检查一下失败原因：
     如果非不可恢复的失败，那么可以用同一个UploadId和同一文件继续调用这个接口续传
     否则，需要重新获取UploadId，重新上传这个文件。
 详细参考demo。
 */
- (OSSTask *)resumableUpload:(OSSResumableUploadRequest *)request;

/**
 查看某个Object是否存在
 @bucketName Object所在的Bucket名称
 @objectKey Object名称
 
 return YES                     Object存在
 return NO && *error = nil      Object不存在
 return NO && *error != nil     发生错误
 */
- (BOOL)doesObjectExistInBucket:(NSString *)bucketName
                      objectKey:(NSString *)objectKey
                          error:(const NSError **)error;
@end
