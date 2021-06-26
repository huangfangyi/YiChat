//
//  YiChatLoginVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatLoginVC.h"
#import "ProjectTextInputView.h"
#import "ServiceGlobalDef.h"
#import "LoginRegisterTitleView.h"
#import "ProjectClickView.h"
#import "YiChatRegisterVC.h"
#import "ProjectJudgeHelper.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "YiChatServiceClient.h"
#import "YiChatResetPasswordVC.h"
#import <JPUSHService.h>
#import "WXApi.h"
#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>
typedef NS_ENUM(NSUInteger,YichatLoginStyle){
    YichatLoginStyleCertify,YichatLoginStylePassword
};

@interface YiChatLoginVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) UIScrollView *scroll;

@property (nonatomic,strong) LoginRegisterTitleView *titleView;
@property (nonatomic,strong) UIView *inputView;

@property (nonatomic,strong) ProjectTextInputView *inputAreaCountry;
@property (nonatomic,strong) ProjectTextInputView *inputCertify;
@property (nonatomic,strong) ProjectTextInputView *inputPassword;
@property (nonatomic,strong) ProjectTextInputView *inputPhoneNum;

@property (nonatomic,assign) YichatLoginStyle style;

@property (nonatomic,assign) BOOL isPushIn;

@property (nonatomic,strong) UIView *backView;

@property (nonatomic,strong) ProjectClickView *userAgreement;
@property (nonatomic,strong) ProjectClickView *registered;
@property (nonatomic,strong) NSString *certifyNumStorage;

@property (nonatomic,strong) UIButton *weichatLoginBtn;

@property (nonatomic,strong) UIButton *qqLoginBtn;

@property (nonatomic,assign) BOOL isLogin;
@end

#define YiChatLoginVC_InputCellH 45.0f
#define YiChatLoginVC_ClickBtnH PROJECT_SIZE_CLICKBTN_H

@implementation YiChatLoginVC

+ (id)initialForCertify:(BOOL)isPushIn{
    YiChatLoginVC *login = nil;
    if(isPushIn == YES){
        login = [YiChatLoginVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:@"" leftItem:nil rightItem:nil];
    }
    else{
        login = [YiChatLoginVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_11 centeritem:@"" leftItem:nil rightItem:nil];
    }
    login.isPushIn = isPushIn;
    login.style = YichatLoginStyleCertify;
    return login;
}

+ (id)initialForPassword:(BOOL)isPushIn{
    YiChatLoginVC *login = nil;
    if(isPushIn == YES){
        login = [YiChatLoginVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:@"" leftItem:nil rightItem:nil];
    }
    else{
        login = [YiChatLoginVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_11 centeritem:@"" leftItem:nil rightItem:nil];
    }
    login.isPushIn = isPushIn;
    login.style = YichatLoginStylePassword;
    return login;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _isLogin = NO;
    [self.view addSubview:self.scroll];
    
    if(self.style == YichatLoginStylePassword){
        self.inputPhoneNum.textInput.placeholder = [NSString stringWithFormat:@"请输入手机号码"];
    }
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    NSString *storageUser = [self getStorageUserName];
    NSString *password = [self getStoragePassword];
    _inputPhoneNum.textInput.text = storageUser;
    _inputPassword.textInput.text = password;
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [self resignkeyboard];
    
    YiChatRegisterVC *registerVC = [YiChatRegisterVC initialForCertify:YES];
    [self.navigationController pushViewController:registerVC animated:YES];
}

- (UIScrollView *)scroll{
    if(!_scroll){
        CGFloat w = self.view.frame.size.width;
        CGFloat h = self.view.frame.size.height - (self.totalNavH);
        
        _scroll = [ProjectHelper helper_factoryMakeScrollViewWithFrame:CGRectMake(0,self.totalNavH,w,h ) contentSize:CGSizeMake(w,h) pagingEnabled:NO showsHorizontalScrollIndicator:NO showsVerticalScrollIndicator:NO scrollEnabled:YES];
        _scroll.bounces = NO;
        _scroll.userInteractionEnabled = YES;
        [_scroll addSubview:self.backView];
        
    }
    return _scroll;
}

- (UIView *)backView{
    if(!_backView){
        _backView = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0,0, self.view.frame.size.width, self.scroll.contentSize.height) backGroundColor:[UIColor whiteColor]];
        _backView.userInteractionEnabled = YES;
        [self makeUI];
    }
    return _backView;
}

- (LoginRegisterTitleView *)titleView{
    if(!_titleView){
        if(_style == YichatLoginStyleCertify){
            //短信验证码登录
            _titleView = [[LoginRegisterTitleView alloc] initWithFrame:CGRectMake(0, 20.0, self.backView.frame.size.width, 100.0) titile:@"使用短信验证码登录" bgView:self.backView];
        }
        else if(_style == YichatLoginStylePassword){
            //需要输入用户名 密码登录
            _titleView = [[LoginRegisterTitleView alloc] initWithFrame:CGRectMake(0, 20.0, self.backView.frame.size.width, 100.0) userIcon:@"" userName:@"" bgView:self.backView];
        }
    }
    return _titleView;
}

- (UIView *)inputView{
    if(!_inputView){
        
        if(_style == YichatLoginStyleCertify){
            
            _inputView = [[UIView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.titleView.frame.origin.y + self.titleView.frame.size.height, self.backView.frame.size.width - PROJECT_SIZE_NAV_BLANK  * 2, YiChatLoginVC_InputCellH * 2)];
            
          //  [_inputView addSubview:self.inputAreaCountry];
            [_inputView addSubview:self.inputPhoneNum];
            [_inputView addSubview:self.inputCertify];
            
            ProjectClickView *certifyLog = [ProjectClickView createClickViewWithFrame:CGRectMake(_inputView.frame.origin.x, _inputView.frame.origin.y + _inputView.frame.size.height + 20.0, _inputView.frame.size.width, YiChatLoginVC_ClickBtnH) title:@"同意并继续" type:0];
            certifyLog.userInteractionEnabled = YES;
            
            certifyLog.clickInvocation = ^(NSString * _Nonnull identify) {
                [self loginForCertify];
            };
            [_backView addSubview:certifyLog];
            
//            NSString *text = @"使用密码登录";
//            CGFloat textW = [ProjectHelper helper_getFontSizeWithString:text useFont:14.0 withWidth:200.0 andHeight:certifyLog.frame.size.height].size.width;
//
//            ProjectClickView *passwordLogin = [ProjectClickView createClickViewWithFrame:CGRectMake(certifyLog.frame.origin.x + certifyLog.frame.size.width / 2 - textW / 2, certifyLog.frame.origin.y + certifyLog.frame.size.height + 20.0, textW, certifyLog.frame.size.height) title:text type:1];
//
//            passwordLogin.userInteractionEnabled = YES;
//
//            passwordLogin.clickInvocation = ^(NSString * _Nonnull identify) {
//                UIViewController *vc = [[self class] initialForPassword:YES];
//                [self.navigationController pushViewController:vc animated:YES];
//            };
//            [_backView addSubview:passwordLogin];
            
        }
        else if(_style == YichatLoginStylePassword){
            _inputView = [[UIView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.titleView.frame.origin.y + self.titleView.frame.size.height, self.backView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, YiChatLoginVC_InputCellH * 2)];
            
       //     [_inputView addSubview:self.inputAreaCountry];
            [_inputView addSubview:self.inputPhoneNum];
            [_inputView addSubview:self.inputPassword];
            
            ProjectClickView *certifyLog = [ProjectClickView createClickViewWithFrame:CGRectMake(_inputView.frame.origin.x, _inputView.frame.origin.y + _inputView.frame.size.height + 20.0, _inputView.frame.size.width, YiChatLoginVC_ClickBtnH) title:@"登录" type:0];
            certifyLog.userInteractionEnabled = YES;
            
            certifyLog.clickInvocation = ^(NSString * _Nonnull identify) {
                [self loginForPassword];
            };
            [_backView addSubview:certifyLog];
             
             /*
            
            NSString *text = @"使用短信验证码登录";
             CGFloat textW = [ProjectHelper helper_getFontSizeWithString:text useFont:14.0 withWidth:200.0 andHeight:certifyLog.frame.size.height].size.width;
            
            ProjectClickView *passwordLogin = [ProjectClickView createClickViewWithFrame:CGRectMake(certifyLog.frame.origin.x + certifyLog.frame.size.width / 2 - textW / 2, certifyLog.frame.origin.y + certifyLog.frame.size.height + 20.0, textW, certifyLog.frame.size.height) title:text type:1];
             passwordLogin.userInteractionEnabled = YES;
            
            passwordLogin.clickInvocation = ^(NSString * _Nonnull identify) {
                NavProjectVC *vc = [[self class] initialForCertify:YES];
                 [vc setNavH:self.navigationController.navigationBar.frame.size.height];
                [self.navigationController pushViewController:vc animated:YES];
            };
            [_backView addSubview:passwordLogin];
            */
        }
        
        _inputView.userInteractionEnabled = YES;
    }
    return _inputView;
}

- (ProjectTextInputView *)inputAreaCountry{
    if(!_inputAreaCountry){
        _inputAreaCountry = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, 0, self.inputView.frame.size.width, YiChatLoginVC_InputCellH)];
        _inputAreaCountry.inputStyle = ProjectInputViewStyleSelecteAreaCountry;
        _inputAreaCountry.row = 0;
        
        _inputAreaCountry.clickRowsInvocation = ^(NSInteger row) {
            
        };
        _inputAreaCountry.isShowArrow = [NSNumber numberWithBool:NO];
        _inputAreaCountry.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        [_inputAreaCountry createUI];
    }
    return _inputAreaCountry;
}

- (ProjectTextInputView *)inputPhoneNum{
    if(!_inputPhoneNum){
        //CGRectMake(0, self.inputAreaCountry.frame.origin.y + self.inputAreaCountry.frame.size.height, self.inputView.frame.size.width, YiChatLoginVC_InputCellH)
        //
        _inputPhoneNum = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, 0, self.inputView.frame.size.width, YiChatLoginVC_InputCellH)];
        _inputPhoneNum.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        _inputPhoneNum.inputStyle = ProjectInputViewStylePhoneInput;
        [_inputPhoneNum createUI];
    }
    return _inputPhoneNum;
}

- (ProjectTextInputView *)inputCertify{
    if(!_inputCertify){
        _inputCertify = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, self.inputPhoneNum.frame.origin.y + self.inputPhoneNum.frame.size.height, self.inputView.frame.size.width, YiChatLoginVC_InputCellH)];
        _inputCertify.inputStyle = ProjectInputViewStyleInputCertify;
        _inputCertify.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        
        WS(weakSelf);
        HelperReturnBOOLInvocation click = ^BOOL(void){
            
            __block BOOL isCan = NO;

            NSString *phone = weakSelf.inputPhoneNum.textInput.text;
            
            NSString *phoneCertify = [ProjectJudgeHelper helper_judgePhone:phone];
            if(phoneCertify){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:phoneCertify];
                return NO;
            }
            
            dispatch_semaphore_t lock = dispatch_semaphore_create(0);
            
            [ProjectRequestHelper sendCertifyWithPhoneNum:phone headerParameters:nil progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                
            } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                
                [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                    if(obj && [obj isKindOfClass:[NSDictionary class]]){
                        isCan = YES;
                        if(obj[@"data"] && [obj[@"data"] isKindOfClass:[NSString class]]){
                            weakSelf.certifyNumStorage = obj[@"data"];
                        }
                    }
                    else if(obj && [obj isKindOfClass:[NSString class]]){
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                    }
                    else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"短信验证码发送出错"];
                    }
                    
                    dispatch_semaphore_signal(lock);
                }];
                
                
            } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
                dispatch_semaphore_signal(lock);
            }];
            
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            return isCan;
        };
        
        [_inputCertify addCertifyInvocation:@{@"click":click}];
        [_inputCertify createUI];
    }
    return _inputCertify;
}

- (ProjectTextInputView *)inputPassword{
    if(!_inputPassword){
        CGRect frame  = CGRectMake(0, self.inputPhoneNum.frame.origin.y + self.inputPhoneNum.frame.size.height, self.inputView.frame.size.width, YiChatLoginVC_InputCellH);
        
        _inputPassword = [[ProjectTextInputView alloc] initWithFrame:frame];
        _inputPassword.inputStyle = ProjectInputViewPasswordInput;
        _inputPassword.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        [_inputPassword createUI];
    }
    return _inputPassword;
}

- (ProjectClickView *)userAgreement{
    if(!_userAgreement){
//        if(_style == YichatLoginStyleCertify){
//
//
//            NSString *agree = @"《用户使用协议》";
//            NSString *str = [NSString stringWithFormat:@"%@%@",@"点击‘同意并继续’表示已阅读并同意\r\n",agree];
//
//            CGFloat textW = [ProjectHelper helper_getFontSizeWithString:str useFont:12.0 withWidth:_backView.frame.size.width - 20.0 andHeight:40.0].size.width;
//
//            _userAgreement = [[ProjectClickView alloc] initWithFrame:CGRectMake(_backView.frame.size.width / 2 - textW / 2, _backView.frame.size.height - 40.0 - 10.0, textW, 40.0) bgView:_backView];
//
//            _userAgreement.lab.frame = _userAgreement.bounds;
//            _userAgreement.lab.numberOfLines = 0;
//            _userAgreement.lab.textAlignment = NSTextAlignmentCenter;
//
//            _userAgreement.clickInvocation = ^(NSString * _Nonnull identify) {
//                //用户使用协议
//            };
//            _userAgreement.lab.attributedText = [ProjectHelper helper_factoryMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:NSMakeRange(0, str.length - agree.length) font:12 andFont:12 color:PROJECT_COLOR_TEXTGRAY color:PROJECT_COLOR_BlLUE withText:str];
//
//        }
//        else{
            WS(weakSelf);
            NSString *str = @"忘记密码";
             CGFloat textW = [ProjectHelper helper_getFontSizeWithString:str useFont:12.0 withWidth:_backView.frame.size.width - 20.0 andHeight:40.0].size.width;
            CGFloat b_h = 30 + PROJECT_SIZE_SafeAreaInset.bottom;
            if (PROJECT_SIZE_SafeAreaInset.bottom == 0) {
                b_h = 50 + PROJECT_SIZE_SafeAreaInset.bottom;
            }
        
            _userAgreement = [[ProjectClickView alloc] initWithFrame:CGRectMake(_backView.frame.size.width/2 - textW - 20,  _backView.frame.size.height - b_h, textW, 40.0) bgView:_backView];
            _userAgreement.lab.frame = _userAgreement.bounds;
            _userAgreement.lab.numberOfLines = 0;
            
            _userAgreement.lab.font = PROJECT_TEXT_FONT_COMMON(12.0);
            _userAgreement.lab.textAlignment = NSTextAlignmentCenter;
            _userAgreement.lab.text = str;
            _userAgreement.lab.textColor = PROJECT_COLOR_TEXTGRAY;
            _userAgreement.clickInvocation = ^(NSString * _Nonnull identify) {
                //忘记密码
                YiChatResetPasswordVC *getback = [YiChatResetPasswordVC initialVCForGetBackPassword];
                [weakSelf.navigationController pushViewController:getback animated:YES];
            };
            
//        }
    }
    return _userAgreement;
}

-(ProjectClickView *)registered{
    if(!_registered){
        
        WS(weakSelf);
        NSString *str = @"注册";
        CGFloat textW = [ProjectHelper helper_getFontSizeWithString:str useFont:12.0 withWidth:_backView.frame.size.width - 20.0 andHeight:40.0].size.width;
        CGFloat b_h = 30 + PROJECT_SIZE_SafeAreaInset.bottom;
        if (PROJECT_SIZE_SafeAreaInset.bottom == 0) {
            b_h = 50 + PROJECT_SIZE_SafeAreaInset.bottom;
        }
        _registered = [[ProjectClickView alloc] initWithFrame:CGRectMake(_backView.frame.size.width/2 + textW + 20,  _backView.frame.size.height - b_h, textW, 40.0) bgView:_backView];
        _registered.lab.frame = _registered.bounds;
        _registered.lab.numberOfLines = 0;
        
        _registered.lab.font = PROJECT_TEXT_FONT_COMMON(12.0);
        _registered.lab.textAlignment = NSTextAlignmentCenter;
        _registered.lab.text = str;
        _registered.lab.textColor = PROJECT_COLOR_TEXTGRAY;
        _registered.clickInvocation = ^(NSString * _Nonnull identify) {
            [weakSelf resignkeyboard];
            
            YiChatRegisterVC *registerVC = [YiChatRegisterVC initialForCertify:YES];
            [weakSelf.navigationController pushViewController:registerVC animated:YES];
        };
    }
    return _registered;
}

-(UIButton *)qqLoginBtn{
    if(!_qqLoginBtn){
        _qqLoginBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _qqLoginBtn.layer.cornerRadius = 25.0;
        _qqLoginBtn.clipsToBounds = YES;
        UIImage *icon = [UIImage imageNamed:@"QQ"];
        [_qqLoginBtn setImage:icon forState:UIControlStateNormal];
        _qqLoginBtn.tintColor = [UIColor clearColor];
        [_qqLoginBtn addTarget:self action:@selector(qqLogin:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _qqLoginBtn;
    
}

- (UIButton *)weichatLoginBtn{
    if(!_weichatLoginBtn){
        _weichatLoginBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _weichatLoginBtn.layer.cornerRadius = 25.0;
        _weichatLoginBtn.clipsToBounds = YES;
        UIImage *icon = [UIImage imageNamed:@"weichat.png"];
        [_weichatLoginBtn setImage:icon forState:UIControlStateNormal];
        _weichatLoginBtn.tintColor = [UIColor clearColor];
        [_weichatLoginBtn addTarget:self action:@selector(weichatLogin:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _weichatLoginBtn;
}

- (void)makeUI{
    [self titleView];
    [self.backView addSubview:self.inputView];
    [self.backView addSubview:self.userAgreement];
    [self.backView addSubview:self.registered];
    
    BOOL isQQ = NO;
    BOOL isWeChat = NO;
    if ([TencentOAuth iphoneQQInstalled] && YiChatProject_IsNeedQQLogin == 1) {
        isQQ = YES;
    }
    
    if ([WXApi isWXAppInstalled] && YiChatProject_IsNeedWeChatLogin == 1) {
        isWeChat = YES;
    }
    

    if (!isQQ && isWeChat) {
        [self.backView addSubview:self.weichatLoginBtn];
        [self.weichatLoginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-100);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
    }
    
    if (isQQ && !isWeChat) {
        [self.backView addSubview:self.qqLoginBtn];
        [self.qqLoginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-100);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
    }
    
    if (isQQ && isWeChat) {
        [self.backView addSubview:self.qqLoginBtn];
        [self.qqLoginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-100);
            make.centerX.mas_equalTo(-60);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
        
        [self.backView addSubview:self.weichatLoginBtn];
        [self.weichatLoginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-100);
            make.centerX.mas_equalTo(60);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
    }
    
}



- (NSString *)getStorageUserName{
    return [[YiChatUserManager defaultManagaer] getStorageInputPhone];
}

- (NSString *)getStoragePassword{
    return [[YiChatUserManager defaultManagaer] getStorageInputPassword];
}

- (NSString *)getStorageUserIcon{
    return @"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558613948991&di=45e73823886540e0f96474ae0a98dbcc&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201506%2F26%2F20150626043819_xfszF.thumb.700_0.jpeg";
}

- (void)loginForCertify{
    [self resignkeyboard];
    
    NSString *phone = _inputPhoneNum.textInput.text;
    NSString *certify = _inputCertify.textInput.text;
    
    NSString *phoneCertify = [ProjectJudgeHelper helper_judgePhone:phone];
    if(phoneCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:phoneCertify];
        return;
    }
    
    if(certify.length < 1){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"短信验证码不能为空"];
        return;
    }
    
     NSDictionary *param = [ProjectRequestParameterModel getcertifyLoginParamWithMobile:phone deviceId:[ProjectHelper helper_getDeviceId] password:certify platform:@"0" type:@"1"];
    
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper loginWithParameters:param headerParameters:nil progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                
                id data = obj[@"data"];
                if([data isKindOfClass:[NSDictionary class]]){
                    [[YiChatUserManager defaultManagaer] updateUserModelWithDic:data];
                    
                    [[YiChatUserManager defaultManagaer] storageUserDic:data];
                    
                    [[YiChatUserManager defaultManagaer] storageInputPhone:_inputPhoneNum.textInput.text password:@""];
                    
                    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_loginWithUserName:YiChatUserInfo_UserIdStr password:YiChatUserInfo_ImPassword invocation:^(BOOL state) {
                        
                        [ProjectRequestHelper progressHidden:progress];
                        
                    }];
                }
                else{
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"数据出错"];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        [ProjectRequestHelper progressHidden:progress];
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
    }];
}

- (void)loginForPassword{
    [self resignkeyboard];
    
    WS(weakSelf);
    NSString *user = _inputPhoneNum.textInput.text;
    
    
    if(!user){
        user = _inputPhoneNum.textInput.text;
    }
    
    if(user && [user isKindOfClass:[NSString class]]){
        if(user.length < 1){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入登录账号"];
            return;
        }
    }
    
    NSString *passwordStr = _inputPassword.textInput.text ;
    NSString *passwordCertify =[ProjectJudgeHelper helper_judgePassword:passwordStr];
    
    if(passwordCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:passwordCertify];
        return;
    }
    
    NSDictionary *param = [ProjectRequestParameterModel getLoginParamWithMobile:user deviceId:[ProjectHelper helper_getDeviceId] password:passwordStr platform:@"0"];
    
    if(_isLogin){
        return;
    }
    else{
        _isLogin = YES;
    }
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [[YiChatUserManager defaultManagaer] storageInputPhone:user password:passwordStr];
    
    [ProjectRequestHelper loginWithParameters:param headerParameters:nil progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                
                id data = obj[@"data"];
                if([data isKindOfClass:[NSDictionary class]]){
                    
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
                    
                    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_loginWithUserName:YiChatUserInfo_UserIdStr password:YiChatUserInfo_ImPassword invocation:^(BOOL state) {
                        
                        if(state){
                            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                                
                                weakSelf.isLogin = NO;
                            });
                        }
                        else{
                            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                                
                                weakSelf.isLogin = NO;
                            });
                        }
                       
                        [ProjectRequestHelper progressHidden:progress];
                    }];
                }
                else{
                      weakSelf.isLogin = NO;
                     [ProjectRequestHelper progressHidden:progress];
                     [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"数据出错"];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                 weakSelf.isLogin = NO;
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            else{
                weakSelf.isLogin = NO;
                [ProjectRequestHelper progressHidden:progress];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
          weakSelf.isLogin = NO;
         [ProjectRequestHelper progressHidden:progress];
         [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
    }];
    
}

- (void)qqLogin:(UIButton *)btn{
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [[YiChatServiceClient defaultChatClient] yichatServiceClient_qqLogin:^(BOOL isSuccess, NSString * _Nonnull error) {
        [ProjectRequestHelper progressHidden:progress];
        if(error && [error isKindOfClass:[NSString class]]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
        }
        
    }];
}

- (void)weichatLogin:(UIButton *)btn{
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [[YiChatServiceClient defaultChatClient] yichatServiceClient_weichatLogin:^(BOOL isSuccess, NSString * _Nonnull error) {
        
        [ProjectRequestHelper progressHidden:progress];
        if(error && [error isKindOfClass:[NSString class]]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
        }
        
    }];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self resignkeyboard];
}

- (void)resignkeyboard{
    [_inputPhoneNum.textInput  resignFirstResponder];
    [_inputCertify.textInput resignFirstResponder];
    [_inputPassword.textInput resignFirstResponder];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
