//
//  YRNetWorkConfig.h
//  PinDi
//
//  Created by yunlian on 2017/3/25.
//  Copyright © 2017年 YANG RUI. All rights reserved.
//

#ifndef YRNetWorkConfig_h
#define YRNetWorkConfig_h

#import <CommonCrypto/CommonHMAC.h>
#import <CommonCrypto/CommonDigest.h>

#import "YRNetWorkAlertDef.h"
#import "ProjectDef.h"


#define TIMEOUTINTERVAL 20.0f
#define CACHEPOLICY NSURLRequestReloadIgnoringLocalAndRemoteCacheData

#define HTTP_SECRET @"123456"

#define YRNetWorkConfig_TIMEOUTINTERVAL 20.0f
#define YRNetWorkConfig_CACHEPOLICY NSURLRequestReloadIgnoringLocalAndRemoteCacheData
#define YRNetWorkConfig_PublicSecrit @"YilinkejiGood!"

#define YRNetWorkConfig_AppCode @"ydzw"
#define YRNetWorkConfig_SubSystemCode @"ydzw"

typedef NS_ENUM(NSUInteger,YRNetWorkRequestMethod){
    YRNetWorkRequestMethodGet=10,
    YRNetWorkRequestMethodHead=11,
    YRNetWorkRequestMethodPost = 20,
    YRNetWorkRequestMethodPut = 21,
    YRNetWorkRequestMethodDelete,
    YRNetWorkRequestMethodVoiceUpload
};

typedef NS_ENUM(NSUInteger,YRNetWorkRequestProgressViewStyle){
    YRNetWorkRequestProgressViewStyleWholePage,YRNetWorkRequestProgressViewStyleNone
};


typedef NS_ENUM(NSUInteger,YRNetWorkTaskResumeThread){
    YRNetWorkTaskResumeThreadAsyn = 0,YRNetWorkTaskResumeThreadSerial
};

typedef NS_ENUM(NSUInteger,YRNetWorkTaskResumeState){
    YRNetWorkTaskResumeStateWait = 0,
    YRNetWorkTaskResumeStateBegin = 0,
    YRNetWorkTaskResumeStateEnd = 10
};

typedef void(^YRNetWorkTaskRequestSuccessHandle)(id  responseObject ,NSHTTPURLResponse *response);
typedef void(^YRNetWorkTaskRequestFailHandle)(NSError *  error);

typedef id(^yrNetWorkRequestGetObj)(void);

#define WS(weakSelf)  __weak __typeof(&*self)weakSelf = self;

//#define YRNetWork_SecurityPublicKey PROJECT_NetWork_SecretKey

#endif /* YRNetWorkConfig_h */

