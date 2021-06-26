//
//  YiChatServiceClient.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatServiceClient.h"
#import "ServiceGlobalDef.h"
#import "ProjectConfigure.h"
#import "HTClient.h"
#import "YiChatUserManager.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ZFChatRequestHelper.h"

#import "WXApi.h"
#import "AFHTTPSessionManager+FormRequests.h"
#import "YiChatConversationVC.h"
#import "YiChatVersionUpdateView.h"
#import <KLCPopup.h>
#import <JPUSHService.h>


#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>
#import "YiChatStorageManager.h"
static YiChatServiceClient *manager = nil;

@interface YiChatServiceClient ()<ZFChatManageDelegate,WXApiDelegate,TencentSessionDelegate>

@property (nonatomic,strong) id progress;

@property (nonatomic,copy) void(^weiChatLoginInvocation)(BOOL isSuccess,NSString *error);
@property (nonatomic,copy) void(^qqLoginInvocation)(BOOL isSuccess,NSString *error);
@property (nonatomic,assign) BOOL isWeichatLogining;
@property (nonatomic,assign) BOOL isQQLogining;
@property (nonatomic,assign) BOOL isDealingUnreadMessage;

@property (nonatomic,assign) BOOL xmppIsLogin;

@property (nonatomic,strong) KLCPopup *popView;

@property (nonatomic,strong)TencentOAuth *tencentOAth;
@end

@implementation YiChatServiceClient

+ (id)defaultChatClient{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager =[[self alloc] init];
        
    });
    return manager;
}

//版本更新调用处
- (void)yiChatServiceClient_initial{
    
    ZFChatConfigureEntity *entity = [[ZFChatConfigureEntity alloc] init];
    entity.isAutoLogin = YES;
    [ZFChatHelper zfChatHelper_chatConfigureWithEntity:entity delegate:self];
    self.isDealingUnreadMessage = NO;
    [WXApi registerApp:YiChatProject_WeiChat_AppKey];
    
    [ProjectHelper helper_getGlobalThread:^{
        [self checkVersionUpdateState:^(BOOL state) {
            
        }];
    }];
}

-(void)checkVersionUpdateState:(void (^)(BOOL))invocation{
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *app_build = [infoDictionary objectForKey:@"CFBundleVersion"];
    NSDictionary *param = [ProjectRequestParameterModel checkVersionWithType:@"1" currentVersion:app_build];
//    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    WS(weakSelf);
    [ProjectRequestHelper checkVersionWithParameters:param headerParameters:@{} progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:dataDic];
                if (model.code == 0) {
                    if ([dataDic[@"data"] isKindOfClass:[NSDictionary class]]) {
                        NSDictionary *dic = dataDic[@"data"];
                        NSString *version = dic[@"version"];
                        NSString *updateStatus = [NSString stringWithFormat:@"%@",dic[@"updateStatus"]];
                        BOOL isMandatory = NO;
                        if ([updateStatus isEqualToString:@"1"]) {
                            isMandatory = YES;
                        }
                        
                        if (app_build.integerValue < version.integerValue) {
                            
                            dispatch_async(dispatch_get_main_queue(), ^{
                                YiChatVersionUpdateView *view = [[YiChatVersionUpdateView alloc] initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 30, 200) isMandatory:isMandatory version:version];
                                view.versionBlock = ^(BOOL isCancel) {
                                    if (isCancel) {
                                        [weakSelf.popView dismiss:YES];
                                    }else{
                                        if ([[UIApplication sharedApplication]canOpenURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@",dic[@"downloadUrl"]]]]) {
                                            [[UIApplication sharedApplication]openURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@",dic[@"downloadUrl"]]]];
                                        }
                                    }
                                };
                                weakSelf.popView = [KLCPopup popupWithContentView:view showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
                                [weakSelf.popView show];
                            });
                        }else{
                            invocation(NO);
                        }
                    }
                    
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)yiChatServiceClient_AutoLogin:(void(^)(BOOL isSuccess))invocation{
    [ZFChatHelper autoLogin:^(BOOL isSuccess) {
        if(isSuccess){
            YiChatUserManager *manager = [YiChatUserManager defaultManagaer];
            NSDictionary *dic = [manager getCashUserDicInfo];
            
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                if(dic.allKeys.count != 0){
                    [manager updateUserModelWithDic:dic];
                    [[ProjectConfigure defaultConfigure] jumpToMain];
                    invocation(YES);
                    return;
                }
            }
        }
        invocation(NO);
    }];
}

- (void)yiChatServiceClient_loginWithUserName:(NSString *)userName
                                     password:(NSString *)password
                                   invocation:(void(^)(BOOL state))invocation{
    //14015718 784567
    //14015717 472577
    //14015718 784567
     WS(weakSelf);
           
    [ZFChatHelper zfChatHelper_loginWithUserName:userName password:password completion:^(BOOL success, NSString * _Nonnull des) {
        
        [ProjectHelper helper_getMainThread:^{
            
            invocation(success);
            
            if(success){
                weakSelf.xmppIsLogin = YES;
                [ZFChatHelper removeAllGroupChatListState];
                [[ProjectConfigure defaultConfigure] jumpToMain];
                [self setJGJushTagWithGroupAdd:YES];
            }
            else{
                //还在当前登录页面
                 weakSelf.xmppIsLogin = NO;
                if(des && [des isKindOfClass:[NSString class]]){
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:des];
                }
                else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"登录失败,请重试"];
                }
                [weakSelf logoutClean];
                [weakSelf userCashClean];
            }
        }];
    
    }];
}

- (id)progressShow{
    return [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
}

- (void)progressHiddenWithProgress:(id)progress{
    if([progress respondsToSelector:@selector(hidden)]){
        [progress performSelector:@selector(hidden)];
    }
}

#pragma mark ZFChatManageDelegate

- (void)zfChatManageDelegate_AutoLoginIsFail:(BOOL)state{
    if(state){
        _xmppIsLogin = NO;
        [self logoutClean];
        [self userCashClean];
    }
}

- (void)zfChatManageDelegate_DidLoginFromOtherDevice{
    
    WS(weakSelf);
    [ZFChatRequestHelper zfRequestCheckTokenInvocation:^(BOOL isNeedLoginOut) {
        if(isNeedLoginOut){
            [ProjectHelper helper_getMainThread:^{
                //弹出提示 直接退回到登录页
              //  [weakSelf loginOut];
            }];
        }
    }];
}

- (void)zfChatManageDelegate_DidAutoLoginSuccess{
    _xmppIsLogin = YES;
    
    [ZFChatHelper removeAllGroupChatListState];
    
    [self dealUnreadMessage];
//    if (![[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%@%@",YiChatUserInfo_UserIdStr,JPUSHSETTAG]]) {
    [self setJGJushTagWithGroupAdd:YES];
//    }
}

- (void)dealUnreadMessage{
    [ProjectHelper helper_getGlobalThread:^{
        if(_xmppIsLogin == NO){
            return ;
        }
        
        [[YiChatUserManager defaultManagaer] getUnreadMessagess:^(NSArray * _Nonnull dic) {
            NSArray *arr = dic;
            if(arr && [arr isKindOfClass:[NSArray class]]){
                if(arr.count > 0){
                    NSArray *unreadMesArr = arr;
                    
                    if(self.isDealingUnreadMessage == YES){
                        return ;
                    }
                    
                    self.isDealingUnreadMessage = YES;
                    
                    dispatch_apply(unreadMesArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
                        NSDictionary *dic = unreadMesArr[i];
                        if(dic && [dic isKindOfClass:[NSDictionary class]]){
                            
                            NSString *groupId = [NSString stringWithFormat:@"%ld",[dic[@"groupId"] integerValue]];
                            NSNumber *unreadCount = dic[@"unreadCount"];
                            NSDictionary *lastMessage = dic[@"lastmessage"];
                            
                            if(lastMessage && [lastMessage isKindOfClass:[NSDictionary class]]){
                                NSNumber *time = lastMessage[@"time"];
                                
                                HTMessage *msg = [ZFChatHelper translateRequestHttpDataToHTMessage:lastMessage];
                                if(msg && [msg isKindOfClass:[HTMessage class]] && groupId && [groupId isKindOfClass:[NSString class]] && unreadCount && [unreadCount isKindOfClass:[NSNumber class]]){
                                    if(time && [time isKindOfClass:[NSNumber class]]){
                                        msg.timestamp = [time integerValue];
                                    }
                                    
                                    [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:msg];
                                    
                                    [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:msg chatId:groupId unreadCount:(unreadCount.integerValue + 1) isReadAllMessage:NO];
                                }
                            }
                        }
                    });
                    [ZFChatHelper zfChatHelper_updateUnreadMessage];
                    [[YiChatUserManager defaultManagaer] removeUnreadMessages];
                    self.isDealingUnreadMessage = NO;
                    
                }
            }
        }];
    }];
}

-(void)updateJGJushTagWithGroup{
    [self setJGJushTagWithGroupAdd:NO];
    
}

-(void)setJGJushTagWithGroupAdd:(BOOL)isAdd{
    NSString *state = [[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB];
    if ([state isEqualToString:@"1"]) {
        return;
    }
    [[YiChatStorageManager sharedManager] getStorageMessageShutUpWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
        NSMutableArray *groupArr = [NSMutableArray new];
        if ([obj isKindOfClass:[NSDictionary class]]) {
            NSDictionary *dic = (NSDictionary *)obj;
            
            for (NSString *key in dic.allKeys) {
                NSString *value = dic[key];
                if ([value isEqualToString:@"1"]) {
                    [groupArr addObject:key];
                }
            }
        }
        
    
        [ZFGroupHelper getSelfGroups:^(NSArray * _Nonnull aGroups) {
            NSMutableArray *arr = [NSMutableArray new];
            for (int i = 0; i < aGroups.count; i ++) {
                NSDictionary *info = [YiChatGroupInfoModel translateObjPropertyToDic:aGroups[i]];
                
                if([info isKindOfClass:[NSDictionary class]] && info){
                    YiChatGroupInfoModel *model = [[YiChatGroupInfoModel alloc] initWithGroupListInfoDic:info];
                    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                        if (![groupArr containsObject:model.groupId]) {
                            if (model.groupId != nil || model.groupId.length > 0) {
                                [arr addObject:model.groupId];
                            }
                            
                        }
                    }
                }
            }
            if (isAdd) {
                [JPUSHService addTags:[NSSet setWithArray:arr.copy] completion:nil seq:1];
            }else{
                [JPUSHService setTags:[NSSet setWithArray:arr.copy] completion:nil seq:1];
            }
            
        } failure:^(NSError * _Nonnull error) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
        }];
    }];
    
}

- (void)loginOut{
    
    [ProjectHelper helper_getGlobalThread:^{
        
        [self xmppLogout];
        
        _xmppIsLogin = NO;
        
        [self logoutClean];
        
        [self userCashClean];
        
        [[ProjectConfigure defaultConfigure] backToLogin];
    }];
}

- (void)logoutClean{
    [ZFChatHelper zfChatHelper_chatClean];
}

- (void)xmppLogout{
    [ZFChatHelper zfChatHelper_loginOut];
}

- (void)userCashClean{
      [[YiChatUserManager defaultManagaer] logoutClean];
}

- (void)yichatServiceClient_qqLogin:(void(^)(BOOL isSuccess,NSString *error))invocation{
    if ([TencentOAuth iphoneQQInstalled]) {
        if (!_isQQLogining) {
            _isQQLogining = YES;
            self.tencentOAth = [[TencentOAuth alloc] initWithAppId:YiChatProject_QQ_AppId andDelegate:self];
            //        self.tencentOAth.authMode = kAuthModeClientSideToken;
            NSArray *permissions = [NSArray arrayWithObjects:@"get_user_info",@"get_simple_userinfo", @"add_t", nil];
            [self.tencentOAth authorize:permissions];
            _qqLoginInvocation = invocation;
        }
    }else{
        [self invocationQQWithState:NO msg:@"请先安装qq客户端"];
    }
}

- (void)yichatServiceClient_weichatLogin:(void(^)(BOOL isSuccess,NSString *error))invocation{
    if ([WXApi isWXAppInstalled]) {
        if(_isWeichatLogining == NO){
            _isWeichatLogining = YES;
            
            SendAuthReq *req = [[SendAuthReq alloc]init];
            req.scope = @"snsapi_userinfo";
            req.openID = YiChatProject_WeiChat_AppKey;
            req.state = @"App";
            [WXApi sendReq:req];
            
            _weiChatLoginInvocation = invocation;
        }
        
    }
    else{
        [self invocationWithState:NO msg:@"请先安装微信客户端"];
    }
}

- (void)invocationWithState:(BOOL)state msg:(NSString *)msg{
    if(_weiChatLoginInvocation){
        _weiChatLoginInvocation(state,msg);
    }
    _isWeichatLogining = NO;
}

- (void)invocationQQWithState:(BOOL)state msg:(NSString *)msg{
    if(_qqLoginInvocation){
        _qqLoginInvocation(state,msg);
    }
    _isQQLogining = NO;
}

//授权后回调 WXApiDelegate
- (void)onResp:(BaseResp *)resp
{
    /*
     ErrCode ERR_OK = 0(用户同意)
     ERR_AUTH_DENIED = -4（用户拒绝授权）
     ERR_USER_CANCEL = -2（用户取消）
     code    用户换取access_token的code，仅在ErrCode为0时有效
     state   第三方程序发送时用来标识其请求的唯一性的标志，由第三方程序调用sendReq时传入，由微信终端回传，state字符串长度不能超过1K
     lang    微信客户端当前语言
     country 微信用户当前国家信息
     */
    //判断是否为授权请求，否则与微信支付等功能发生冲突
    if ([resp isKindOfClass:[SendAuthResp class]]) {
        SendAuthResp *aresp = (SendAuthResp *)resp;
        if (aresp.errCode == 0) {
            //            NSLog(@"code==%@",aresp.code);
            [self getWechatAccessTokenWithCode:aresp.code];
            _isWeichatLogining = NO;
            return;
        }
        
        [self invocationWithState:NO msg:resp.errStr];
    }
    
    if([resp isKindOfClass:[PayResp class]]){
        //支付返回结果，实际支付结果需要去微信服务器端查询
        NSString *strMsg = [NSString stringWithFormat:@"支付结果"];
        if (resp.errCode == 0) {
            strMsg = @"支付结果：成功！";
            NSLog(@"支付成功－PaySuccess，retcode = %d", resp.errCode);
        }else{
            strMsg = [NSString stringWithFormat:@"支付结果：失败！"];
            NSLog(@"错误，retcode = %d, retstr = %@", resp.errCode,resp.errStr);
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:WXPayonResp object:nil userInfo:@{@"errCode" : @(resp.errCode)}];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:WXPayonResp object:nil];
    }
}

- (void)getWechatAccessTokenWithCode:(NSString *)code
{
    NSString *urlStr = [NSString stringWithFormat:@"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%@&secret=%@&code=%@&grant_type=authorization_code",YiChatProject_WeiChat_AppKey,YiChatProject_WeiChat_WechatSecrectKey,code];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlStr parameters:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSError *err;
        NSDictionary *dic = [NSJSONSerialization
                             JSONObjectWithData:responseObject
                             options:NSJSONReadingMutableContainers
                             error:&err];
        
        NSString *accessToken = dic[@"access_token"];
        NSString *openId = dic[@"openid"];
        [self getWechatUserInfoWithAccessToken:accessToken openId:openId];
        
    } failure:^(NSURLSessionTask *task, NSError *error) {
        [self invocationWithState:NO msg:error.localizedDescription];
    }];
}

- (void)getWechatUserInfoWithAccessToken:(NSString *)accessToken openId:(NSString *)openId
{
   
    NSString *urlStr =[NSString stringWithFormat:@"https://api.weixin.qq.com/sns/userinfo?access_token=%@&openid=%@",accessToken,openId];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlStr parameters:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSError *err;
        NSDictionary *dic = [NSJSONSerialization
                             JSONObjectWithData:responseObject
                             options:NSJSONReadingMutableContainers
                             error:&err];
        
        NSString *openID = dic[@"openid"];
        NSString *avatar = dic[@"headimgurl"];
        NSString *nickname = dic[@"nickname"];
        NSString *uniqueCode = dic[@"unionid"];
        NSInteger type = 0;
        NSString *deviceId = [ProjectHelper helper_getDeviceId];
        NSDictionary *param = [ProjectRequestParameterModel getthidLoginWithParameters:type nick:nickname uniqueCode:uniqueCode avatar:avatar deviceId:deviceId openId:openID];
        
        [ProjectRequestHelper thidLoginWithParameters:param headerParameters:nil progress:nickname isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    if(obj[@"data"] && [obj[@"data"] isKindOfClass:[NSDictionary class]]){
                        [self weichatLoginSuccessDeal:obj[@"data"]];
                    }
                    else{
                        [self invocationWithState:NO msg:@"微信登录出错"];
                    }
                }
                else if(obj && [obj isKindOfClass:[NSString class]]){
                     [self invocationWithState:NO msg:obj];
                }
                else{
                    [self invocationWithState:NO msg:@"微信登录出错"];
                }
            }];
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [self invocationWithState:NO msg:error];
        }];
        
    } failure:^(NSURLSessionTask *task, NSError *error) {
        [self invocationWithState:NO msg:@""];
    }];
}

- (void)qqLoginSuccessDeal:(NSDictionary *)data{
    
    [[YiChatUserManager defaultManagaer] updateUserModelWithDic:data];
    
    [[YiChatUserManager defaultManagaer] storageUserDic:data];
    
    if ([[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB]) {
        NSString *state = [[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB];
        if (![state isEqualToString:@"1"]) {
            [JPUSHService setAlias:YiChatUserInfo_UserIdStr completion:nil seq:1];
        }
    }else{
        [JPUSHService setAlias:YiChatUserInfo_UserIdStr completion:nil seq:1];
    }
    WS(weakSelf);
    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_loginWithUserName:YiChatUserInfo_UserIdStr password:YiChatUserInfo_ImPassword invocation:^(BOOL state) {
        
        weakSelf.xmppIsLogin = state;
        
        [weakSelf invocationQQWithState:state msg:nil];
        
    }];
}

- (void)weichatLoginSuccessDeal:(NSDictionary *)data{
    
    [[YiChatUserManager defaultManagaer] updateUserModelWithDic:data];
    
    [[YiChatUserManager defaultManagaer] storageUserDic:data];
    if ([[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB]) {
        NSString *state = [[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB];
        if (![state isEqualToString:@"1"]) {
            [JPUSHService setAlias:YiChatUserInfo_UserIdStr completion:nil seq:1];
        }
    }else{
        [JPUSHService setAlias:YiChatUserInfo_UserIdStr completion:nil seq:1];
    }
    
    
    WS(weakSelf);
    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_loginWithUserName:YiChatUserInfo_UserIdStr password:YiChatUserInfo_ImPassword invocation:^(BOOL state) {
        
        weakSelf.xmppIsLogin = state;
        
        [weakSelf invocationWithState:state msg:nil];
        
    }];
}
- (void)tencentDidLogin {
    [self.tencentOAth getUserInfo];
}

- (void)tencentDidNotLogin:(BOOL)cancelled {
    if (cancelled){
        [self invocationQQWithState:NO msg:@"用户取消登录"];
    }
    else{
        [self invocationQQWithState:NO msg:@"登录失败"];
    }
}

- (void)tencentDidNotNetWork {
    [self invocationQQWithState:NO msg:@"无网络连接，请设置网络"];
}

- (void)getUserInfoResponse:(APIResponse*) response{
    NSLog(@"respons:%@",response.jsonResponse);
    _isQQLogining = NO;
    NSString *urlStr =[NSString stringWithFormat:@"https://graph.qq.com/oauth2.0/me?access_token=%@&unionid=1",self.tencentOAth.accessToken];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlStr parameters:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSString *result =[[ NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSString *str = [result stringByReplacingOccurrencesOfString:@"callback(" withString:@""];
        NSString *string = [str stringByReplacingOccurrencesOfString:@");" withString:@""];
        NSData *data =[string dataUsingEncoding:NSUTF8StringEncoding];
        NSError *err;
        NSDictionary *dic = [NSJSONSerialization
                             JSONObjectWithData:data
                             options:NSJSONReadingMutableContainers
                             error:&err];
        NSString *openID = dic[@"openid"];
        NSString *avatar = response.jsonResponse[@"figureurl_qq_2"];
        NSString *nickname = response.jsonResponse[@"nickname"];
        NSInteger type = 1;
        NSString *uniqueCode = dic[@"unionid"];

        NSString *deviceId = [ProjectHelper helper_getDeviceId];
        NSDictionary *param = [ProjectRequestParameterModel getthidLoginWithParameters:type nick:nickname uniqueCode:uniqueCode avatar:avatar deviceId:deviceId openId:openID];
        
        [ProjectRequestHelper thidLoginWithParameters:param headerParameters:nil progress:nickname isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    if(obj[@"data"] && [obj[@"data"] isKindOfClass:[NSDictionary class]]){
                        [self qqLoginSuccessDeal:obj[@"data"]];
                    }
                    else{
                        [self invocationQQWithState:NO msg:@"qq登录出错"];
                    }
                }
                else if(obj && [obj isKindOfClass:[NSString class]]){
                    [self invocationQQWithState:NO msg:obj];
                }
                else{
                    [self invocationQQWithState:NO msg:@"qq登录出错"];
                }
            }];
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [self invocationQQWithState:NO msg:error];
        }];
        
    } failure:^(NSURLSessionTask *task, NSError *error) {
        
        [self invocationQQWithState:NO msg:@""];
    }];
}

@end

