//
//  YRNetWorkRequest.m
//  NetCar
//
//  Created by yunlian on 16/10/10.
//  Copyright © 2016年 hl. All rights reserved.
//

#import "YRNetWorkRequest.h"

@interface YRNetWorkRequest()

@property (nonatomic,strong,readonly) NSString *url;
@property (nonatomic,strong,readonly) NSString *interface;
@property (nonatomic,strong,readonly) NSDictionary *parameters;
@property (nonatomic,strong,readonly) NSDictionary *headerParameters;
@property (nonatomic,readonly) YRNetWorkRequestMethod requestMethod;
@property (nonatomic,readonly) BOOL isNeedAddScret;
@property (nonatomic,readonly) YRNetWorkRequestProgressViewStyle isShowLoading;
@property (nonatomic,readonly) NSString *requestIdentifer;

@property (nonatomic,readonly) BOOL isAsyn;

@property (nonatomic,strong) NSDictionary *requestBodySystemParameters;

@property (nonatomic,strong) NSString *securityPublicKey;
@end


static NSString *networkUrl = nil;

static NSString *networkKey = nil;

@implementation YRNetWorkRequest

- (id)initWithInterface:(NSString *)interface
             requestmethod:(YRNetWorkRequestMethod)requestmethod
                parameters:(NSDictionary *)parameters
          headerParameters:(NSDictionary *)headerParameters
               isNeedScret:(BOOL)isNeedScret
              identifer:(NSString *)identidier
                 isAsyn:(BOOL)isAsyn
{
    self = [super init];
    if(self){
        _url=[YRNetWorkRequest yrNetWorkApisGetBaseURL];
        _interface=interface;
        _headerParameters=headerParameters;
        _requestMethod=requestmethod;
        _parameters=parameters;
        _isNeedAddScret=isNeedScret;
        _isAsyn = isAsyn;
        _securityPublicKey = networkKey;
    }
    return self;
}

+ (id)initialWithInterface:(NSString *)interface
             requestmethod:(YRNetWorkRequestMethod)requestmethod
              parameters:(NSDictionary *)parameters
                headerParameters:(NSDictionary *)headerParameters
                  isNeedScret:(BOOL)isNeedScret
                 identifer:(NSString *)identidier
                    isAsyn:(BOOL)isAsyn
{
    return [[self alloc] initWithInterface:interface requestmethod:requestmethod parameters:parameters headerParameters:headerParameters isNeedScret:isNeedScret identifer:identidier isAsyn:isAsyn];
}

- (void)addSystemRequestParameters:(NSDictionary *)systemparameters{
    if([systemparameters isKindOfClass:[NSDictionary class]]){
        if(systemparameters.allKeys.count != 0){
            _requestBodySystemParameters = systemparameters;
        }
    }
}

#pragma mark help

- (NSDictionary *)yrNetWorkRequestGetHeaderParameters{
    return _headerParameters;
}

- (YRNetWorkRequestProgressViewStyle)yrNetWorkRequestGetProgressLoadingState{
    return _isShowLoading;
}

- (BOOL)yrNetWorkRequestGetSecretPolicy{
    return _isNeedAddScret;
}

- (NSString *)yrNetWorkRequestGetBaseURL{
    return _url;
}

- (NSString *)yrNetWorkRequestGetInterface{
    return _interface;
}

- (NSDictionary *)yrNetWorkRequestGetParameters{
    return _parameters;
}

- (YRNetWorkRequestMethod)yrNetWorkRequestGetRequestMethod{
    return _requestMethod;
}

- (NSString *)yrNetWorkRequestGetRequestIndentider{
    return _requestIdentifer;
}

- (BOOL)yrNetWorkRequestGetRequestAsyn{
    return _isAsyn;
}

+ (NSString *)yrNetWorkApisGetBaseURL{
    return networkUrl;
}

- (NSDictionary *)yrNetWorkRequestGetHttpBodyQueryDic:(NSDictionary *)dic{
    return @{@"queryParamters":dic};
}

- (NSDictionary *)yrNetWorkRequestGetSystemHttpBodyDic{
    return self.requestBodySystemParameters;
}

- (NSString *)yrNetWorkRequestGetSecurityPublicKey{
    return self.securityPublicKey;
}

+ (void)configureUrlAddress:(NSString *)urlAddress key:(NSString *)key{
    if(urlAddress && [urlAddress isKindOfClass:[NSString class]]){
        networkUrl = urlAddress;
    }
    
    if(key && [key isKindOfClass:[NSString class]]){
        networkKey = key;
    }
}
@end
