//
//  OSSDefine.h
//  AliyunOSSiOS
//
//  Created by zhouzhuo on 5/1/16.
//  Copyright © 2016 zhouzhuo. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef OSSDefine_h
#define OSSDefine_h

#define OSSUAPrefix                             @"aliyun-sdk-ios"
#define OSSSDKVersion                           @"2.5.2"

#define OSSListBucketResultXMLTOKEN             @"ListBucketResult"
#define OSSNameXMLTOKEN                         @"Name"
#define OSSDelimiterXMLTOKEN                    @"Delimiter"
#define OSSMarkerXMLTOKEN                       @"Marker"
#define OSSNextMarkerXMLTOKEN                   @"NextMarker"
#define OSSMaxKeysXMLTOKEN                      @"MaxKeys"
#define OSSIsTruncatedXMLTOKEN                  @"IsTruncated"
#define OSSContentsXMLTOKEN                     @"Contents"
#define OSSKeyXMLTOKEN                          @"Key"
#define OSSLastModifiedXMLTOKEN                 @"LastModified"
#define OSSETagXMLTOKEN                         @"ETag"
#define OSSTypeXMLTOKEN                         @"Type"
#define OSSSizeXMLTOKEN                         @"Size"
#define OSSStorageClassXMLTOKEN                 @"StorageClass"
#define OSSCommonPrefixesXMLTOKEN               @"CommonPrefixes"
#define OSSOwnerXMLTOKEN                        @"Owner"
#define OSSAccessControlListXMLTOKEN            @"AccessControlList"
#define OSSGrantXMLTOKEN                        @"Grant"
#define OSSIDXMLTOKEN                           @"ID"
#define OSSDisplayNameXMLTOKEN                  @"DisplayName"
#define OSSBucketsXMLTOKEN                      @"Buckets"
#define OSSBucketXMLTOKEN                       @"Bucket"
#define OSSCreationDate                         @"CreationDate"
#define OSSPrefixXMLTOKEN                       @"Prefix"
#define OSSUploadIdXMLTOKEN                     @"UploadId"
#define OSSLocationXMLTOKEN                     @"Location"
#define OSSNextPartNumberMarkerXMLTOKEN         @"NextPartNumberMarker"
#define OSSMaxPartsXMLTOKEN                     @"MaxParts"
#define OSSPartXMLTOKEN                         @"Part"
#define OSSPartNumberXMLTOKEN                   @"PartNumber"

#define OSSClientErrorDomain                    @"com.aliyun.oss.clientError"
#define OSSServerErrorDomain                    @"com.aliyun.oss.serverError"

#define OSSErrorMessageTOKEN                    @"ErrorMessage"

#define OSSHttpHeaderContentDisposition         @"Content-Disposition"
#define OSSHttpHeaderXOSSCallback               @"x-oss-callback"
#define OSSHttpHeaderXOSSCallbackVar            @"x-oss-callback-var"
#define OSSHttpHeaderContentEncoding            @"Content-Encoding"
#define OSSHttpHeaderContentType                @"Content-Type"
#define OSSHttpHeaderContentMD5                 @"Content-MD5"
#define OSSHttpHeaderCacheControl               @"Cache-Control"
#define OSSHttpHeaderExpires                    @"Expires"

#define OSSDefaultRetryCount                    3
#define OSSDefaultMaxConcurrentNum              5
#define OSSDefaultTimeoutForRequestInSecond     15
#define OSSDefaultTimeoutForResourceInSecond    7 * 24 * 60 * 60

#endif /* OSSDefine_h */
