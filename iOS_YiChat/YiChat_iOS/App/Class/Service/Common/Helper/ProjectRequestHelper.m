//
//  ProjectRequestHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectRequestHelper.h"
#import "ProjectHelper.h"
#import "ProjectDef.h"
#import "YRNetWorkRequestManage.h"
#import "ServiceGlobalDef.h"
#import "HTUploadHelper.h"
#import "YiChatServiceClient.h"
#import "RXAESEncryptor.h"


@implementation ProjectRequestHelper

+ (NSString *)requestHelper_productRequestNameWithIdentifier:(NSString *)identifier{
    return [NSString stringWithFormat:@"%@%ld%@%@",identifier,[ProjectHelper helper_GetRandowNum],@"_",[ProjectHelper helper_GetCurrentTimeString]];
}

+ (void)requestHelper_feltRequestData:(NSData *)data response:(NSHTTPURLResponse *)response handle:(void(^)(id obj,BOOL isNeedLogin))handle{
    
    
    NSError *error = nil;
    NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    NSDictionary *object = [RXAESEncryptor decryptAES:str key:YiChatProject_NetWork_SecretKey];
    
   // NSDictionary *object=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
    
    if([object isKindOfClass:[NSDictionary class]]){
        
        if([object.allKeys containsObject:@"code"] && [object.allKeys containsObject:@"success"]){
            id code = object[@"code"];
            id success = object[@"success"];
            if(([code isKindOfClass:[NSString class]] || [code isKindOfClass:[NSNumber class]])  && ([success isKindOfClass:[NSString class]] || [success  isKindOfClass:[NSNumber class]])){
                if([success integerValue]){
                    handle(object,NO);
                    return;
                }
                else{
                    
                    if([object.allKeys containsObject:@"msg"]){
                        NSString *msg = object[@"msg"];
                        if([msg isKindOfClass:[NSString class]]){
                            handle(msg,NO);
                            if([code isEqualToString:@"003"]){
                                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:msg];
                                [[YiChatServiceClient defaultChatClient] loginOut];
                            }
                            return;
                        }
                    }
                }
            }
            else{
                handle(@"数据格式出错",NO);
                return;
            }
        }
        else{
            handle(@"数据格式出错",NO);
            return;
        }
        

    }
    
    handle(REQUEST_ALERT_NETWORKERROR,NO);
    return;
}

/*
 YRNetWorkRequestMethodGet=10,
 YRNetWorkRequestMethodHead=11,
 YRNetWorkRequestMethodPost = 20,
 YRNetWorkRequestMethodPut = 21,
 YRNetWorkRequestMethodDelete,
 YRNetWorkRequestMethodVoiceUpload
*/
+ (void)requestHelper_httpRequestWithInterface:(NSString *)interface
                                    parameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                 requestMethod:(NSInteger)method
                                      progress:(id)progress
                          progressIsAutoHidden:(BOOL)isAutoHidden
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail{
    
    NSString *identiy = Request_Identifier(@"iOS");
    
    if(identifierBlock){
        identifierBlock(identiy);
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [YRNetWorkRequest configureUrlAddress:YiChatProject_NetWork_BaseUrl key:YiChatProject_NetWork_SecretKey];
    });
    
    NSMutableDictionary *tmp = [NSMutableDictionary dictionaryWithCapacity:0];
    if(headerParameters && [headerParameters isKindOfClass:[NSDictionary class]]){
        [tmp addEntriesFromDictionary:headerParameters];
    }
    [tmp addEntriesFromDictionary:@{@"None-AES":[NSString stringWithFormat:@"%d",YiChatProject_NetWork_IsNeedResponseDataAes]}];
    
    headerParameters = tmp;
    
    YRNetWorkRequest *request = [[YRNetWorkRequest alloc] initWithInterface:interface requestmethod:method parameters:parameters headerParameters:headerParameters isNeedScret:screteState identifer:identiy isAsyn:isAsyn];
    
    YRNetWorkOperation *operation = [[YRNetWorkOperation alloc] initWithRequest:request];
    
    NSURLRequest *urlrequest  = [operation getUrlRequest];
    
  //  NSString *str = [[NSString alloc] initWithData:urlrequest.HTTPBody encoding:NSUTF8StringEncoding];
    
    NSInteger thread = 0;
    
    isAsyn == YES ? (thread = 0) : (thread = 1);
    
    __block id progressView = progress;
    
    [self progressShow:progressView];
    
    WS(weakSelf);
    
    YRNetWorkTask *task = [[YRNetWorkTask alloc] initTaskWithRequest:urlrequest identifier:identiy ResumeThread:thread requestSuccess:^(id responseObject, NSHTTPURLResponse *response) {
        sucess(responseObject,response);
        
        [weakSelf progressHidden:progressView];
    } fail:^(NSError *error) {
        fail(error.localizedDescription,nil);
        if(isAutoHidden){
            [weakSelf progressHidden:progressView];
        }
    }];
    
    [[YRNetWorkRequestManage sharedManager] addRequestTask:task];
}

+ (void)requestHelper_feltRequestDataWithCode:(NSData *)data response:(NSHTTPURLResponse *)response handle:(void(^)(id obj,BOOL isNeedLogin,NSInteger code))handle{
    
    
    NSError *error = nil;
    NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    NSDictionary *object = [RXAESEncryptor decryptAES:str key:YiChatProject_NetWork_SecretKey];
    
   // NSDictionary *object=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
    
    if([object isKindOfClass:[NSDictionary class]]){
        
        if([object.allKeys containsObject:@"code"] && [object.allKeys containsObject:@"success"]){
            id code = object[@"code"];
            id success = object[@"success"];
            if(([code isKindOfClass:[NSString class]] || [code isKindOfClass:[NSNumber class]])  && ([success isKindOfClass:[NSString class]] || [success  isKindOfClass:[NSNumber class]])){
                if([success integerValue]){
                    handle(object,NO,[code integerValue]);
                    return;
                }
                else{
                    
                    if([object.allKeys containsObject:@"msg"]){
                        NSString *msg = object[@"msg"];
                        if([msg isKindOfClass:[NSString class]]){
                            handle(msg,NO,[code integerValue]);
                            if([code isEqualToString:@"003"]){
                                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:msg];
                                [[YiChatServiceClient defaultChatClient] loginOut];
                            }
                            return;
                        }
                    }
                }
            }
            else{
                handle(@"数据格式出错",NO,-1);
                return;
            }
        }
        else{
            handle(@"数据格式出错",NO,-1);
            return;
        }
        

    }
    
    handle(REQUEST_ALERT_NETWORKERROR,NO,-1);
    return;
}


+ (void)progressShow:(id)progress{
    
}

+ (void)progressHidden:(id)progress{
    if(progress){
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            
            if([progress respondsToSelector:@selector(hidden)]){
                [progress performSelector:@selector(hidden)];
            }
        });
    }
}

+ (void)advertisementMainWithParameters:(NSDictionary *)parameters
headerParameters:(NSDictionary *)headerParameters
        progress:(id)progress
        isScrete:(BOOL)screteState
          isAsyn:(BOOL)isAsyn
       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
   successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/home/url" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getSystemConfigWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/config" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendCertifyWithPhoneNum:(NSString *)phone
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    
    NSDictionary *parama = [ProjectRequestParameterModel sendCertifyParametersWithMobile:phone];
    
    [self requestHelper_httpRequestWithInterface:@"/api/sms/send" parameters:parama headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)registeWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                        fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/user/register" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
    
}

+ (void)loginWithParameters:(NSDictionary *)parameters
           headerParameters:(NSDictionary *)headerParameters
                   progress:(id)progress
                   isScrete:(BOOL)screteState
                     isAsyn:(BOOL)isAsyn
                  identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
              successHandle:(RequestHelperRequestSuccessHandle)sucess
                       fail:(RequestHelperRequestFailHandle)fail{
        [self requestHelper_httpRequestWithInterface:@"/api/login" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}
    
+ (void)webLoginWithInterface:(NSString *)interface
              headerParameters:(NSDictionary *)headerParameters
                      progress:(id)progress
                      isScrete:(BOOL)screteState
                        isAsyn:(BOOL)isAsyn
                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                          fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:interface parameters:@{} headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodGet progress:progress progressIsAutoHidden:NO isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)loginoutWithParameters:(NSDictionary *)parameters
              headerParameters:(NSDictionary *)headerParameters
                      progress:(id)progress
                      isScrete:(BOOL)screteState
                        isAsyn:(BOOL)isAsyn
                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                          fail:(RequestHelperRequestFailHandle)fail{
    
      [self requestHelper_httpRequestWithInterface:@"/api/login/out" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)getUserInfoWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
      [self requestHelper_httpRequestWithInterface:@"/api/user/info" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getUpdateInfoWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    
      [self requestHelper_httpRequestWithInterface:@"/api/user/info/update" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)searchUserWithParameters:(NSDictionary *)parameters
                headerParameters:(NSDictionary *)headerParameters
                        progress:(id)progress
                        isScrete:(BOOL)screteState
                          isAsyn:(BOOL)isAsyn
                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                            fail:(RequestHelperRequestFailHandle)fail{
    
     [self requestHelper_httpRequestWithInterface:@"/api/user/search" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)resetPasswordWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/user/password/reset" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getFriendListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
         [self requestHelper_httpRequestWithInterface:@"/api/friend/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)addFriendWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    
         [self requestHelper_httpRequestWithInterface:@"/api/friend/apply" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)fetchFriendApplyNumWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    
     [self requestHelper_httpRequestWithInterface:@"/api/friend/apply/unread" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

/**
 *  审核好友申请
 */
+ (void)checkFriendApplyWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail{
         [self requestHelper_httpRequestWithInterface:@"/api/friend/apply/check" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

/**
 *  待审核申请列表
 */
+ (void)checkFriendApplyDataListWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail{
         [self requestHelper_httpRequestWithInterface:@"/api/friend/apply/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)friendApplyDeleteWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/friend/apply/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
};

+ (void)deleteFriendWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail{
         [self requestHelper_httpRequestWithInterface:@"/api/friend/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)setFriendsRemarkNameWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/friend/remark/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)judgeFriendStatusWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/friend/status" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getGroupInfoWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/group/info" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getGroupMemberListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
       [self requestHelper_httpRequestWithInterface:@"/api/group/user/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)setGroupManagerRequestWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/admin/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)setGroupSilenceRequestWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/silent/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}
    
+ (void)setGroupMemberSilenceRequestWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/member/silent/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getGroupManagerListWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/admin/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)updateTimeUnixWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/user/timestamp/update" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

//获取用户在群组中的角色
+ (void)getUserGroupRoleWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/group/role" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

//群禁言
+ (void)setGroupManagerShutSendMsgWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/silent/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

//群禁言状态
+ (void)getGroupShutSendMsgStateWithParameters:(NSDictionary *)parameters
                                headerParameters:(NSDictionary *)headerParameters
                                        progress:(id)progress
                                        isScrete:(BOOL)screteState
                                          isAsyn:(BOOL)isAsyn
                                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                                            fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/group/silent/status" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

//
+ (void)searchMsgListWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/message/search/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)uploadMessageWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    
        [self requestHelper_httpRequestWithInterface:@"/api/message/upload" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)updateMessageWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;
{
    [self requestHelper_httpRequestWithInterface:@"/api/message/update" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)getMessageListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
       [self requestHelper_httpRequestWithInterface:@"/api/message/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendSingleRedPackageWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/create/single" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendGroupRedPackageWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/create/group" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)sendRedPackageInfoWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/send/info" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendRedPackageListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/send/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)groupAuthCreateWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/auth/group/create" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)xcxWithParameters:(NSDictionary *)parameters
                     headerParameters:(NSDictionary *)headerParameters
                             progress:(id)progress
                             isScrete:(BOOL)screteState
                               isAsyn:(BOOL)isAsyn
                            identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                        successHandle:(RequestHelperRequestSuccessHandle)sucess
                                 fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/app/small/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)receiveRedPackageInfoWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/receive/info" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)receiveRedPackageListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/receive/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)receiveRedPacketWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/receive" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)receiveRedPacketDetailWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/packet/detail" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)setPayPassWord:(NSDictionary *)parameters
      headerParameters:(NSDictionary *)headerParameters
              progress:(id)progress
              isScrete:(BOOL)screteState
                isAsyn:(BOOL)isAsyn
             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
         successHandle:(RequestHelperRequestSuccessHandle)sucess
                  fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/pay/password/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)searchBalanceWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/balance" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)setBalanceListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/balance/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)groupNoticePublishWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/group/notice/publish" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)groupNoticeListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/group/notice/last" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendSMSCodeWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/sms/send" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)signWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/sign" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)withdrawApplyWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/withdraw/apply" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)withdrawConfigParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/withdraw/config" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)withdrawApplyListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/withdraw/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)addBankCardWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/bank/card/add" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)feedbackWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/feedback" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)sendDynamicWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/publish" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)bankCardListWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    
    [self requestHelper_httpRequestWithInterface:@"/api/user/bank/card/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)deleteDynamicWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)getAllDynamicListWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)getFriendFynamiclistWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/friend/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)dynamicPraiseWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/praise" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)weChatPayWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/pay/weixin/pre" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)aliPayWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/pay/alipay/pre" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)payTradeStatusWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/pay/trade/status" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)dynamicCancelPraiseWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/praise/cancle" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


//评论回复评论
+ (void)dynamicCommandWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/comment" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)deleteDynamicCommandWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/comment/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}


+ (void)dynamicCommandListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/comment/list" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)dynamicGetBackImageWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/background" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)dynamicSetBackImageWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/trend/background/set" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)deleteBankCardWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/user/bank/card/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)deleteGroupNoticeWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/group/notice/delete" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)checkVersionWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/version" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)commonUpLoadImage:(UIImage *)image progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    image = [ProjectHelper helper_getSquareIconFromImage:image];
    
    NSData *data = UIImageJPEGRepresentation(image, 0.6);
    
    [ProjectRequestHelper uploadImageWithData:data withProgressBlock:progressBlocked andMessageId:nil andSendResult:resultBlocked];

}

+ (void)commonUpLoadImageWithoutCrop:(UIImage *)image progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    NSData *data = UIImageJPEGRepresentation(image, 0.6);
    
    [ProjectRequestHelper uploadImageWithData:data withProgressBlock:progressBlocked andMessageId:nil andSendResult:resultBlocked];
}

+ (void)commonUpLoadVideo:(NSString *)localPath progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    
    HTUploadHelper *helper = [[HTUploadHelper alloc] initWithData:[NSURL URLWithString:localPath] withProgressBlock:progressBlocked andMessageId:nil andSendResult:resultBlocked];
    [helper uploadObjectAsync];
    
}

+ (void)uploadImageWithImage:(UIImage *)image userId:(NSString *)userId token:(NSDictionary *)tokenDic progressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    if(image && [image isKindOfClass:[UIImage class]] && userId && [userId isKindOfClass:[NSString class]] && tokenDic && [tokenDic isKindOfClass:[NSDictionary class]]){
        
        image = [ProjectHelper helper_getSquareIconFromImage:image];
        
        NSData *data = UIImageJPEGRepresentation(image, 0.6);
        
        [ProjectRequestHelper uploadImageWithData:data withProgressBlock:progressBlocked andMessageId:nil andSendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
            
            if(isSuccess){
                [self requestUploadWithUrl:remotePath userId:userId token:tokenDic sendResult:resultBlocked];
            }
            else{
                resultBlocked(NO,nil);
            }
        }];
    }
    else{
         resultBlocked(NO,nil);
    }
}

+ (void)requestUploadWithUrl:(NSString *)url userId:(NSString *)userId token:(NSDictionary *)tokenDic  sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    if(url && [url isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]] && tokenDic && [tokenDic isKindOfClass:[NSDictionary class]]){
        
        NSDictionary *param = [ProjectRequestParameterModel getUpdateUserInfoParamWithUserId:userId nick:nil gender:nil avatar:url appId:nil mobile:nil password:nil];
        
        [self getUpdateInfoWithParameters:param headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    
                    [ProjectHelper helper_getMainThread:^{
                        resultBlocked(YES,url);
                        
                    }];
                }
                else if([obj isKindOfClass:[NSString class]]){
                    resultBlocked(NO,nil);
                }
                else{
                    resultBlocked(NO,nil);
                }
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
             resultBlocked(NO,nil);
        }];
    }
    else{
        resultBlocked(NO,nil);
    }
}

+ (void)uploadImageWithData:(id)data withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked{
    if(data){
        HTUploadHelper * upload = [[HTUploadHelper alloc] initWithData:data withProgressBlock:progressBlocked andMessageId:messageId andSendResult:^(BOOL isSuccess, NSString *remotePath) {
            if(isSuccess){
                NSString *url = [NSString stringWithFormat:@"%@%@",YiChatProject_NetWork_ChatFileHost,remotePath];
                resultBlocked(YES,url);
            }
            else{
                resultBlocked(NO,nil);
            }
        }];
        [upload uploadObjectAsync];
    }
    else{
        resultBlocked(NO,nil);
    }
    
}

+ (void)checkTokenWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail{
     [self requestHelper_httpRequestWithInterface:@"/api/check/token" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

+ (void)thidLoginWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail{
    [self requestHelper_httpRequestWithInterface:@"/api/login/third" parameters:parameters headerParameters:headerParameters requestMethod:YRNetWorkRequestMethodPost progress:progress progressIsAutoHidden:YES isScrete:screteState isAsyn:isAsyn identider:identifierBlock successHandle:sucess fail:fail];
}

@end

