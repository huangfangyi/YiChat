//
//  YRNetWorkOperation.h
//  NetCar
//
//  Created by yunlian on 16/10/10.
//  Copyright © 2016年 hl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YRNetWorkConfig.h"

@class NSURLSessionTask;
@class YRNetWorkRequest;
@interface YRNetWorkOperation : NSObject


- (id)initWithRequest:(YRNetWorkRequest *)request;

- (NSURLRequest *)getUrlRequest;

- (void)resumeRequestSuccess:(void(^)(id, NSURLResponse *))successHandle fail:(void(^)(NSError *))failHandle;

//+ (NSString *)yrNetWorkApisGetSha1String:(NSString *)srcString;
@end

