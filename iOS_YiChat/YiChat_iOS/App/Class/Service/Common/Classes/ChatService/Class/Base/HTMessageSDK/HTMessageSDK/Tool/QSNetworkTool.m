//
//  QSNetworkTool.m
//  HTMessage
//
//  Created by 非夜 on 17/2/13.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "QSNetworkTool.h"
#import "QSAESManager.h"

@interface QSNetworkTool()<NSURLSessionDataDelegate>

@property (nonatomic,strong) NSURLSession *session;

@property (nonatomic,strong) NSURLSession *httpsSession;

@property (nonatomic,strong) NSString * sslCerPath;

@property (nonatomic,assign)BOOL isSession;

@property (nonatomic,assign)BOOL isHttpSession;

@end

@implementation QSNetworkTool

+ (NSString *)serializeParameters:(id)parameters {
    if ([parameters isKindOfClass:[NSString class]]) {
        return parameters;
    }else if([parameters isKindOfClass:[NSDictionary class]]){
        NSMutableArray *parts = [NSMutableArray array];
        for (id key in parameters) {
            id value = [parameters objectForKey:key];
            NSString *part = [NSString stringWithFormat: @"%@=%@",key,value];
            [parts addObject:part];
        }
        return [parts componentsJoinedByString: @"&"];
    }else{
        NSCAssert(1, @"parameters is not illegal!");
        return nil;
    }
}

- (NSURLSession *)session {
    if (_session == nil) {
        NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
        // An operation queue for scheduling the delegate calls and completion handlers. The queue should be a serial queue, in order to ensure the correct ordering of callbacks. If nil, the session creates a serial operation queue for performing all delegate method calls and completion handler calls.
        _session = [NSURLSession sessionWithConfiguration:config delegate:nil delegateQueue:nil];
    }
    return _session;
}

- (NSURLSession *)httpsSession {
    if (_httpsSession == nil) {
        NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
        _httpsSession = [NSURLSession sessionWithConfiguration:config delegate:self delegateQueue:nil];
    }
    return _httpsSession;
}

- (void)requestWithMutableURLRequest:(NSMutableURLRequest *)request success:(QSSuccessBlock)QSSuccessBlock failure:(QSFailureBlock)QSFailureBlock {
    self.isSession = YES;
    NSURLSessionDataTask *dataTask = [self.session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error){
        [self.session finishTasksAndInvalidate];
        NSDictionary *dict = nil;
        if (data) {
            dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        }
        if (self.isSemaphoreSignal) {
            if (error == nil) {
                QSSuccessBlock(dict,data);
            }else{
                QSFailureBlock(error);
            }
        }else {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error == nil) {
                    QSSuccessBlock(dict,data);
                }else{
                    QSFailureBlock(error);
                }
            });
        }
        
    }];
    [dataTask resume];
}

- (void)requestHTTPSWithCerPath:(NSString *)cerPath withMutableURLRequest:(NSMutableURLRequest *)request success:(QSSuccessBlock)QSSuccessBlock failure:(QSFailureBlock)QSFailureBlock {
    self.sslCerPath = cerPath;
    self.isHttpSession = YES;
    NSURLSessionDataTask *dataTask = [self.httpsSession dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error){
        [self.httpsSession finishTasksAndInvalidate];
        NSDictionary *dict = nil;
        if (data) {
            dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error == nil) {
                QSSuccessBlock(dict,data);
            }else{
                QSFailureBlock(error);
            }
        });
    }];
    [dataTask resume];
}


#pragma mark NSURLSessionDataDelegate
// only with https
- (void)URLSession:(NSURLSession *)session didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition, NSURLCredential * _Nullable))completionHandler
{
    
    NSLog(@"证书认证");
    if ([[[challenge protectionSpace] authenticationMethod] isEqualToString: NSURLAuthenticationMethodServerTrust]) {
        do
        {
            SecTrustRef serverTrust = [[challenge protectionSpace] serverTrust];
            NSCAssert(serverTrust != nil, @"serverTrust is nil");
            if(nil == serverTrust)
                break; /* failed */
            /**
             *  导入多张CA证书（Certification Authority，支持SSL证书以及自签名的CA），请替换掉你的证书名称
             */
            NSString *cerPath = [[NSBundle mainBundle] pathForResource:[self.sslCerPath copy] ofType:@"cer"];//自签名证书
            NSData* caCert = [NSData dataWithContentsOfFile:cerPath];
            
            NSCAssert(caCert != nil, @"caCert is nil");
            if(nil == caCert)
                break; /* failed */
            
            SecCertificateRef caRef = SecCertificateCreateWithData(NULL, (__bridge CFDataRef)caCert);
            NSCAssert(caRef != nil, @"caRef is nil");
            if(nil == caRef)
                break; /* failed */
            
            //可以添加多张证书
            NSArray *caArray = @[(__bridge id)(caRef)];
            
            NSCAssert(caArray != nil, @"caArray is nil");
            if(nil == caArray)
                break; /* failed */
            
            //将读取的证书设置为服务端帧数的根证书
            OSStatus status = SecTrustSetAnchorCertificates(serverTrust, (__bridge CFArrayRef)caArray);
            NSCAssert(errSecSuccess == status, @"SecTrustSetAnchorCertificates failed");
            if(!(errSecSuccess == status))
                break; /* failed */
            
            SecTrustResultType result = -1;
            //通过本地导入的证书来验证服务器的证书是否可信
            status = SecTrustEvaluate(serverTrust, &result);
            if(!(errSecSuccess == status))
                break; /* failed */
            NSLog(@"stutas:%d",(int)status);
            NSLog(@"Result: %d", result);
            
            BOOL allowConnect = (result == kSecTrustResultUnspecified) || (result == kSecTrustResultProceed);
            if (allowConnect) {
                NSLog(@"success");
            }else {
                NSLog(@"error");
            }
            
            /* kSecTrustResultUnspecified and kSecTrustResultProceed are success */
            if(! allowConnect)
            {
                break; /* failed */
            }
            
#if 0
            /* Treat kSecTrustResultConfirm and kSecTrustResultRecoverableTrustFailure as success */
            /*   since the user will likely tap-through to see the dancing bunnies */
            if(result == kSecTrustResultDeny || result == kSecTrustResultFatalTrustFailure || result == kSecTrustResultOtherError)
                break; /* failed to trust cert (good in this case) */
#endif
            // The only good exit point
            NSLog(@"信任该证书");
            NSURLCredential *credential = [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust];
            completionHandler(NSURLSessionAuthChallengeUseCredential,credential);
            return [[challenge sender] useCredential: credential
                          forAuthenticationChallenge: challenge];
            
        }
        while(0);
    }
    
    // other
    NSURLCredential *credential = [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust];
    completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge,credential);
    return [[challenge sender] cancelAuthenticationChallenge: challenge];
}

//- (void)dealloc {
//    
//    if (self.session && self.isSession) {
//        [self.session invalidateAndCancel];
//    }
//    if (self.httpsSession && self.isHttpSession) {
//        [self.httpsSession invalidateAndCancel];
//    }
//}

@end
