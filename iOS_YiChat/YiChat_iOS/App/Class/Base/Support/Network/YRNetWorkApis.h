//
//  YRNetWorkApis.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/17.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YRNetWorkApis : NSObject

#pragma mark  General APis

+ (NSString *)yrNetWorkApisTranslateKeyValuePairsToURLConnectCharaters:(NSDictionary *)parameters;
#pragma mark 时间戳转时间
+  (NSString *)timeStrIntoTimeStr:(NSString *)timeStr;;
@end

NS_ASSUME_NONNULL_END
