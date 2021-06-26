//
//  YRNetWorkOperation.m
//  NetCar
//
//  Created by yunlian on 16/10/10.
//  Copyright © 2016年 hl. All rights reserved.
//

#import "YRNetWorkOperation.h"
#import "YRNetWorkRequest.h"
#import "YRURLSession.h"
#import "YRNetWorkTask.h"
#import "YRNetWorkApis.h"
#import <AFNetworking/AFNetworking.h>
#import "RXAESEncryptor.h"
#import "NSString+URLEncoding.h"

@interface YRNetWorkOperation()

@property (nonatomic,strong)    NSURLSessionTask *task;

@property (nonatomic,strong,readonly)  YRNetWorkRequest *request;

@property (nonatomic,copy) yrNetWorkRequestGetObj getProgress;


@end

@implementation YRNetWorkOperation

- (id)initWithRequest:(YRNetWorkRequest *)request{
    self=[super init];
    if(self)
    {
        _request=request;
    } 
    return self;
}

- (NSURLRequest *)getUrlRequest{
    NSString *parameterMontagedStr = [self getParameterMontagedString];
    //获取request URL
    NSURL *requesturl = [self getRequestURLWithType:[self.request yrNetWorkRequestGetRequestMethod] interface:[self.request yrNetWorkRequestGetInterface] parameters:parameterMontagedStr];
    
    NSMutableURLRequest *urlrequest = [self getUrlRequestWithUrl:requesturl.absoluteString];
    return urlrequest;
}

- (NSMutableURLRequest *)getUrlRequestWithUrl:(NSString *)url{
    YRNetWorkRequestMethod method = [_request yrNetWorkRequestGetRequestMethod];
    
    NSMutableURLRequest *urlrequest = nil;
    
    WS(weakSelf);
    
    NSDictionary *parameter = [self.request yrNetWorkRequestGetParameters];
    if(method == YRNetWorkRequestMethodPost || method == YRNetWorkRequestMethodPut){
        
        urlrequest  = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        
        NSData *data = [NSJSONSerialization dataWithJSONObject:parameter options:NSJSONWritingPrettyPrinted error:nil];
    
        
        if([self.request yrNetWorkRequestGetSecretPolicy]){
            
            NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            NSString *bodyStr =  [RXAESEncryptor encryptAES:str key:[self.request yrNetWorkRequestGetSecurityPublicKey]];
            bodyStr = [bodyStr urlEncodeString];
            data =  [bodyStr dataUsingEncoding:NSUTF8StringEncoding];
            
        }
        
        [urlrequest setHTTPBody:data];
        
        [self setRequestConfigWithRequest:urlrequest];
        [self setRequestHeaderWithRequest:urlrequest];
        [self setSystemRequestHeaderWithRequest:urlrequest];
    }
    else if(method == YRNetWorkRequestMethodHead || method == YRNetWorkRequestMethodGet){
        urlrequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        
        [self setRequestConfigWithRequest:urlrequest];
        [self setRequestHeaderWithRequest:urlrequest];
        [self setSystemRequestHeaderWithRequest:urlrequest];
    }
    else if(method == YRNetWorkRequestMethodDelete){
        urlrequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        
        [self setRequestConfigWithRequest:urlrequest];
        [self setRequestHeaderWithRequest:urlrequest];
        [self setSystemRequestHeaderWithRequest:urlrequest];
    }
    return urlrequest;
}

- (NSURL *)getRequestURLWithType:(YRNetWorkRequestMethod)requestMethod interface:(NSString *)interface parameters:(NSString *)parameters{
    
    NSString *baseURL=[_request yrNetWorkRequestGetBaseURL];
    NSURL *url = nil;
    if(requestMethod == YRNetWorkRequestMethodGet || requestMethod == YRNetWorkRequestMethodHead || requestMethod == YRNetWorkRequestMethodDelete){
        NSDictionary *param = [_request yrNetWorkRequestGetParameters];
        
        if([param.allKeys containsObject:@""] && param.allKeys.count != 0){
            NSMutableString *valueStr = [NSMutableString stringWithCapacity:0];
            
            [param.allKeys enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                if(![obj isEqualToString:@""]){
                    [valueStr appendString:[NSString stringWithFormat:@"%@%@",@"/",param[obj]]];
                }
            }];
            url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@%@",baseURL,interface,valueStr]];
        }
        else if([param.allKeys containsObject:@"asKey"] || [param.allKeys containsObject:@"rqKey"]){
           
            NSArray *addressArr = param[@"asKey"];
            NSMutableString *valueStr = [NSMutableString stringWithCapacity:0];
            for (int i = 0; i<addressArr.count; i ++) {
                NSString *key = addressArr[i];
                if([param.allKeys containsObject:key]){
                     [valueStr appendString:[NSString stringWithFormat:@"%@%@",@"/",param[key]]];
                }
            }
            
            NSArray *reqKeyArr = param[@"rqKey"];
            
            NSMutableString *reqStr = [NSMutableString stringWithCapacity:0];
            
            for (int i = 0; i<reqKeyArr.count; i ++) {
                NSString *key = reqKeyArr[i];
                if([param.allKeys containsObject:key]){
                    if(i == 0){
                        if(i != reqKeyArr.count - 1){
                            [reqStr appendString:[NSString stringWithFormat:@"%@%@=%@%@",@"?",key,param[key],@"&"]];
                        }
                        else{
                            [reqStr appendString:[NSString stringWithFormat:@"%@%@=%@",@"?",key,param[key]]];
                        }
                    }
                    else{
                        if(i != reqKeyArr.count - 1){
                            [reqStr appendString:[NSString stringWithFormat:@"%@=%@%@",key,param[key],@"&"]];
                        }
                        else{
                            [reqStr appendString:[NSString stringWithFormat:@"%@=%@",key,param[key]]];
                        }
                    }
                }
            }
            
            NSMutableString *urlStr = [NSMutableString stringWithCapacity:0];
            [urlStr appendString:[NSString stringWithFormat:@"%@%@",baseURL,interface]];
            if(valueStr.length != 0){
                [urlStr appendString:[NSString stringWithFormat:@"%@",valueStr]];
            }
            if(reqStr.length != 0){
                [urlStr appendString:[NSString stringWithFormat:@"%@",reqStr]];
            }
            
            url = [NSURL URLWithString:urlStr];
            return url;
            
        }
        else{
            if(parameters.length != 0){
                url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@%@%@",baseURL,interface,@"?",parameters]];
            }
            else{
                 url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",baseURL,interface]];
            }
        }
    }
    else if(requestMethod == YRNetWorkRequestMethodPost || requestMethod == YRNetWorkRequestMethodPut){
        url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",baseURL,interface]];
    }

    return url;
}

- (NSData *)getRequestBodyData{
    YRNetWorkRequestMethod method = [_request yrNetWorkRequestGetRequestMethod];
    if(method == YRNetWorkRequestMethodPut || method == YRNetWorkRequestMethodPost){
        NSDictionary *parameters = [_request yrNetWorkRequestGetParameters];
        NSString *str = [self getMontagedStringWithDic:parameters];
        
        return [str dataUsingEncoding:NSUTF8StringEncoding];
    }
    else{
        return nil;
    }
}

- (void)setRequestConfigWithRequest:(NSMutableURLRequest *)request{
    YRNetWorkRequestMethod method = [_request yrNetWorkRequestGetRequestMethod];
    
    switch (method) {
        case YRNetWorkRequestMethodGet:
        {
            [request setHTTPMethod:@"GET"];
            break;
        }
        case YRNetWorkRequestMethodHead:
        {
            [request setHTTPMethod:@"HEAD"];
            break;
        }
        case YRNetWorkRequestMethodPost:
        {
            [request setHTTPMethod:@"POST"];
            break;
        }
        case YRNetWorkRequestMethodPut:
        {
            [request setHTTPMethod:@"PUT"];
            
            break;
        }
        case YRNetWorkRequestMethodDelete:
        {
            [request setHTTPMethod:@"DELETE"];
            
            break;
        }
            
        default:
            break;
    }
    request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
    request.timeoutInterval = 20.0;
}

- (void)setSystemRequestHeaderWithRequest:(NSMutableURLRequest *)request{
     [request setValue:@"application/json" forHTTPHeaderField:@"content-type"];
}

- (void)setRequestHeaderWithRequest:(NSMutableURLRequest *)request{
    NSDictionary *header = [_request yrNetWorkRequestGetHeaderParameters];
    for (int i = 0; i < header.allKeys.count ; i++) {
        NSString *key = header.allKeys[i];

        [request setValue:header[key] forHTTPHeaderField:key];
    }
}

- (NSString *)getMontagedStringWithDic:(NSDictionary *)dic{
    if(dic.allKeys.count !=0 ){
        return [YRNetWorkApis yrNetWorkApisTranslateKeyValuePairsToURLConnectCharaters:dic];;
    }
    else{
        return nil;
    }
}

- (NSString *)getParameterMontagedString{

    NSDictionary *requestParameters = [_request yrNetWorkRequestGetParameters];
    
    NSDictionary *systemParameters = _request.yrNetWorkRequestGetSystemHttpBodyDic;
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [dic addEntriesFromDictionary:systemParameters];
    if(requestParameters.allKeys != 0){
        [dic addEntriesFromDictionary:requestParameters];
    }
    
    requestParameters = dic;
    
    if(requestParameters.allKeys.count < 1){
        return @"";
    }
    
    BOOL isNeedSecret=[_request yrNetWorkRequestGetSecretPolicy];
    NSMutableString *parameterBody=[NSMutableString stringWithCapacity:0];
    
    NSString *parameterStr=[YRNetWorkApis yrNetWorkApisTranslateKeyValuePairsToURLConnectCharaters:requestParameters];
    
    if(isNeedSecret==YES){
        [parameterBody appendString:parameterStr];
    }
    else{
        [parameterBody appendString:parameterStr];
    }
    return parameterBody;
}

#pragma mark help
/*
- (NSString *)yrNetWorkApisParameterDealWithSignProduction{
    
    NSDictionary *requestparamertsDic=[_request yrNetWorkRequestGetParameters];
    
    NSDictionary *systemParametersDic = [_request yrNetWorkRequestGetSystemHttpBodyDic];
    
    NSMutableDictionary *parameterDic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [parameterDic addEntriesFromDictionary:requestparamertsDic];
    [parameterDic addEntriesFromDictionary:systemParametersDic];
    
    NSMutableString *undealeStr=[NSMutableString stringWithCapacity:0];
    
   // [undealeStr appendString:HTTP_SECRET];
    
    [undealeStr appendString:[YRNetWorkApis yrNetWorkApisTranslateKeyValuePairsToURLConnectCharaters:parameterDic]];
    
   // [undealeStr appendString:HTTP_SECRET];
    
    NSString *signValue=[MyMD5 md5:undealeStr];
    
    NSLog(@"http加前的串%@",undealeStr);
    
//    //先以16为参数告诉strtoul字符串参数表示16进制数字，然后使用0x%X转为数字类型
//    unsigned long red = strtoul([signValue UTF8String],0,16);
////    //strtoul如果传入的字符开头是“0x”,那么第三个参数是0，也是会转为十六进制的,这样写也可以：
////    unsigned long red = strtoul([@"0x6587" UTF8String],0,0);
//    NSLog(@"转换完的数字为：%lx",red);
    
    return signValue;
}

//哈希算法
+ (NSString *)yrNetWorkApisGetSha1String:(NSString *)srcString{
    const char *cstr = [srcString cStringUsingEncoding:NSUTF8StringEncoding];
    NSData *data = [NSData dataWithBytes:cstr length:srcString.length];
    
    uint8_t digest[CC_SHA1_DIGEST_LENGTH];
    
    CC_SHA1(data.bytes, data.length, digest);
    
    NSMutableString * result = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH *2];
    
    for(int i =0; i < CC_SHA1_DIGEST_LENGTH; i++) {
        [result appendFormat:@"%02x", digest[i]];
    }
    
    return result;
}
*/

@end
