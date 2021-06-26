//
//  YRURLSession.m
//  CFFF
//
//  Created by e冻网 on 2018/8/6.
//  Copyright © 2018年 ED. All rights reserved.
//

#import "YRURLSession.h"

@implementation YRURLSession

- (YRNetWorkTask *)yrDataTaskWithRequest:(NSURLRequest *)request completionHandler:(void (^)(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error))completionHandler{
    return [self yrDataTaskWithRequest:request completionHandler:completionHandler];
}
@end
