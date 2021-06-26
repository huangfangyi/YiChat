//
//  AFHTTPSessionManager+FormRequests.h
//  HTMessage
//
//  Created by Nicole on 2017/10/19.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "AFHTTPSessionManager.h"

@interface AFHTTPSessionManager (FormRequests)

+ (id)FRManager;

- (void)FRPOST:(NSString *)URLString
    parameters:(id)parameters
       success:(void(^)(NSURLSessionTask *task, id responseObject,NSDictionary *responseDictionary))success
       failure:(void (^)(NSURLSessionTask *task, NSError *error))failure;


@end
