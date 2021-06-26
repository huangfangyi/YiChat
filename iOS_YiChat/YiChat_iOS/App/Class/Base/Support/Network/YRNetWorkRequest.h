//
//  YRNetWorkRequest.h
//  NetCar
//
//  Created by yunlian on 16/10/10.
//  Copyright © 2016年 hl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YRNetWorkConfig.h"

@interface YRNetWorkRequest : NSObject


- (id)initWithInterface:(NSString *)interface
          requestmethod:(YRNetWorkRequestMethod)requestmethod
             parameters:(NSDictionary *)parameters
       headerParameters:(NSDictionary *)headerParameters
            isNeedScret:(BOOL)isNeedScret
              identifer:(NSString *)identidier
                 isAsyn:(BOOL)isAsyn;

+ (id)initialWithInterface:(NSString *)interface
             requestmethod:(YRNetWorkRequestMethod)requestmethod
                parameters:(NSDictionary *)parameters
          headerParameters:(NSDictionary *)headerParameters
               isNeedScret:(BOOL)isNeedScret
                 identifer:(NSString *)identidier
                    isAsyn:(BOOL)isAsyn;

- (void)addSystemRequestParameters:(NSDictionary *)systemparameters;

- (NSDictionary *)yrNetWorkRequestGetHeaderParameters;

- (NSDictionary *)yrNetWorkRequestGetParameters;

- (YRNetWorkRequestMethod)yrNetWorkRequestGetRequestMethod;

- (NSString *)yrNetWorkRequestGetBaseURL;

- (NSString *)yrNetWorkRequestGetInterface;

- (YRNetWorkRequestProgressViewStyle)yrNetWorkRequestGetProgressLoadingState;

- (NSString *)yrNetWorkRequestGetRequestIndentider;

- (BOOL)yrNetWorkRequestGetSecretPolicy;

- (BOOL)yrNetWorkRequestGetRequestAsyn;

- (NSDictionary *)yrNetWorkRequestGetSystemHttpBodyDic;

- (NSDictionary *)yrNetWorkRequestGetHttpBodyQueryDic:(NSDictionary *)dic;

- (NSString *)yrNetWorkRequestGetSecurityPublicKey;

+ (void)configureUrlAddress:(NSString *)urlAddress key:(NSString *)key;
@end
