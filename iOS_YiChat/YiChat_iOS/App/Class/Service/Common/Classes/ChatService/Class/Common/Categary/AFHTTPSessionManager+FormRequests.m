//
//  AFHTTPSessionManager+FormRequests.m
//  HTMessage
//
//  Created by Nicole on 2017/10/19.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "AFHTTPSessionManager+FormRequests.h"
#import "ZFChatGlobal.h"

@implementation AFHTTPSessionManager (FormRequests)

+ (id)FRManager {
    return [self manager];
}

- (AFHTTPRequestSerializer <AFURLRequestSerialization> *)requestSerializer {
    return [AFHTTPRequestSerializer serializer];
}

- (AFHTTPResponseSerializer <AFURLResponseSerialization> *)responseSerializer {
    return [AFHTTPResponseSerializer serializer];
}

- (void)FRPOST:(NSString *)URLString
    parameters:(id)parameters
       success:(void(^)(NSURLSessionTask *task, id responseObject,NSDictionary *responseDictionary))success
       failure:(void (^)(NSURLSessionTask *task, NSError *error))failure {

    NSString *session = [[NSUserDefaults standardUserDefaults] objectForKey:@"userSession"];
    NSMutableDictionary * dicM = @{}.mutableCopy;
    if (parameters) {
        [dicM setValuesForKeysWithDictionary:(NSDictionary *)parameters];
        if (!([URLString isEqualToString:@"api/login"] || [URLString isEqualToString:@"api/register"] || [URLString isEqualToString:@"api/resetPassword"])) {
//            [dicM setObject:session forKey:@"session"];
        }
    }else{
        if (!([URLString isEqualToString:@"api/login"] || [URLString isEqualToString:@"api/register"] || [URLString isEqualToString:@"api/resetPassword"])) {
            [dicM setObject:session forKey:@"session"];
        }
    }
    
    [self POST:[NSString stringWithFormat:@"%@%@",YiChatProject_NetWork_BaseUrl,URLString] parameters:dicM.copy progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSError *err;
        NSDictionary *responDictionary = [NSJSONSerialization
                                          JSONObjectWithData:responseObject
                                          options:NSJSONReadingMutableContainers
                                          error:&err];
        success(task, responseObject, responDictionary);
        
        //        success(responseObject);
    } failure:^(NSURLSessionTask *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        failure(operation, error);
    }];
    
}

@end
