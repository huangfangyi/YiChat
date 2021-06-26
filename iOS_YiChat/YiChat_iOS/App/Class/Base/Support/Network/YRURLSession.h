//
//  YRURLSession.h
//  CFFF
//
//  Created by e冻网 on 2018/8/6.
//  Copyright © 2018年 ED. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YRNetWorkTask.h"
@interface YRURLSession : NSURLSession

- (YRNetWorkTask *)yrDataTaskWithRequest:(NSURLRequest *)request completionHandler:(void (^)(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error))completionHandler;
@end
