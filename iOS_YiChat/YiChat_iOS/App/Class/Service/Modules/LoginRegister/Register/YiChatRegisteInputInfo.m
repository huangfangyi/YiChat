//
//  YiChatRegisteInputInfo.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRegisteInputInfo.h"
#import "ServiceGlobalDef.h"
#import "ProjectClickView.h"
#import "ProjectRequestHelper.h"
#import "ProjectJudgeHelper.h"
#import "ProjectConfigure.h"
#import "YiChatUserManager.h"
#import "YiChatServiceClient.h"
#import "ProjectHelper.h"
#import "YiChatAgreementVC.h"
@interface YiChatRegisteInputInfo ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) UIView *topView;

@property (nonatomic,strong) UIView *inputViews;


@property (nonatomic,strong) NSString *user_Icon_Str;
@property (nonatomic,strong) UIImageView *user_icon;
@property (nonatomic,strong) UITextField *user_nick;
@property (nonatomic,strong) UITextField *user_password;
@property (nonatomic,strong) ProjectClickView *userAgreement;
@end

@implementation YiChatRegisteInputInfo

+ (id)initialVC{
    YiChatRegisteInputInfo *inputInfo = [YiChatRegisteInputInfo initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"registerInfo") leftItem:@"返回" rightItem:nil];
    return inputInfo;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.topView];
    [self.view addSubview:self.inputViews];
    [self.view addSubview:self.userAgreement];
    UIButton *registerBtn = [[UIButton alloc] initWithFrame:CGRectZero];
    [registerBtn setTitle:@"注册" forState:UIControlStateNormal];
    [registerBtn addTarget:self action:@selector(submit) forControlEvents:UIControlEventTouchUpInside];
    [registerBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    registerBtn.layer.masksToBounds = YES;
    registerBtn.backgroundColor = PROJECT_COLOR_BlLUE;
    registerBtn.layer.cornerRadius = 5;
    [self.view addSubview:registerBtn];
    [registerBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.inputViews.mas_bottom).offset(150);
        make.left.mas_equalTo(20);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(45);
    }];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0)
    {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;      // 手势有效设置为YES  无效为NO
        self.navigationController.interactivePopGestureRecognizer.delegate = self;    // 手势的代理设置为self
    }
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0)
    {
        self.navigationController.interactivePopGestureRecognizer.enabled = YES;      // 手势有效设置为YES  无效为NO
        self.navigationController.interactivePopGestureRecognizer.delegate = self;    // 手势的代理设置为self
    }
    
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)submit{
    [_user_password resignFirstResponder];
    [_user_nick resignFirstResponder];
    
    NSString *phoneCertify = [ProjectJudgeHelper helper_judgePhone:_phoneNumStr];
    if(phoneCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:phoneCertify];
        return;
    }
    
    if(_user_nick.text.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入昵称"];
        return;
    }
    if(_user_password.text.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请设置登录密码"];
        return;
    }
    NSString *platFormStr = @"0";
    
    NSDictionary *parameters = [ProjectRequestParameterModel getRegisterParametersWithMobile:_phoneNumStr nick:_user_nick.text password:_user_password.text avrater:_user_Icon_Str platform:platFormStr];
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper registeWithParameters:parameters headerParameters:nil  progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            NSDictionary *res = obj;
            if([res isKindOfClass:[NSDictionary class]]){
                id data = res[@"data"];
                if([data isKindOfClass:[NSDictionary class]]){
                    
                    [[YiChatUserManager defaultManagaer] updateUserModelWithDic:data];
                    
                    [[YiChatUserManager defaultManagaer] storageUserDic:data];
                    
                    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_loginWithUserName:YiChatUserInfo_UserIdStr password:YiChatUserInfo_ImPassword invocation:^(BOOL state) {
                       [ProjectRequestHelper progressHidden:progress];
                    }];
                }
                else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"数据出错"];
                    [ProjectRequestHelper progressHidden:progress];
                    return;
                }
            }
            else if([res isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                [ProjectRequestHelper progressHidden:progress];
                return;
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
        [ProjectRequestHelper progressHidden:progress];
        return;
    }];
    
}

- (UIView *)topView{
    if(!_topView){
        _topView = [[UIView alloc] initWithFrame:CGRectMake(0, 20.0 + PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, PROJECT_SIZE_WIDTH, 100.0)];
        
        UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _topView.frame.size.width, 30.0)];
        [_topView addSubview:lab];
        lab.text = @"请设置你的个人头像和昵称";
        lab.textAlignment = NSTextAlignmentCenter;
        lab.font = PROJECT_TEXT_FONT_COMMON(14);
        lab.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
        
        CGFloat w = 80.0;
        CGFloat h = w ;
        
        UIImageView *icon = [[UIImageView alloc] initWithFrame:CGRectMake(_topView.frame.size.width / 2 - w / 2, lab.frame.origin.y + lab.frame.size.height + 10.0, w, h)];
        icon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
        icon.layer.cornerRadius = 10.0;
        icon.clipsToBounds = YES;
        icon.userInteractionEnabled = NO;
        _user_icon = icon;
        [_topView addSubview:icon];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = icon.frame;
        [btn addTarget:self action:@selector(iconBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
        [_topView addSubview:btn];
        
    }
    return _topView;
}

- (void)iconBtnMethod:(UIButton *)btn{
    [ProjectUIHelper projectActionSheetWithListArr:@[@"相机",@"相册"] click:^(NSInteger row) {
        if(row == 0){
            
            [ProjectUIHelper projectPhotoVideoPickerWWithType:5 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
                    if(editedIcon && [editedIcon isKindOfClass:[UIImage class]]){
                        [self uploadImage:editedIcon];
                    }
                };
                [self presentViewController:nav animated:YES completion:nil];
            }];
            
        }
        else if(row == 1){
            
            [ProjectUIHelper projectPhotoVideoPickerWWithType:6 pickNum:1 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                
                manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
                    if(images && [images isKindOfClass:[NSArray class]]){
                        if(images.count == 1){
                            [self uploadImage:images.firstObject];
                        }
                    }
                };
                [self presentViewController:nav animated:YES completion:nil];
            }];
            
        }
    }];
}

- (UIView *)inputViews{
    if(!_inputViews){
          _inputViews = [[UIView alloc] initWithFrame:CGRectMake(0, self.topView.frame.origin.y + self.topView.frame.size.height + 20.0, PROJECT_SIZE_WIDTH, 120.0)];
        CGFloat blank = PROJECT_SIZE_NAV_BLANK;
        
        NSArray *placeholders = @[@"昵称（必填）",@"密码（必填）"];
        for (int i = 0; i < 2; i ++) {
            NSString *str = nil;
            if((placeholders.count - 1) >= i){
                str = placeholders[i];
            }
            
            UITextField *text = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(blank, 20 + i * (45.0 + 10.0), _inputViews.frame.size.width - blank * 2, 45.0) withPlaceholder:str fontSize:PROJECT_TEXT_FONT_COMMON(15.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
            [_inputViews addSubview:text];
            
            if(i == 0){
                _user_nick = text;
                
            }
            else{
                text.secureTextEntry = YES;
                _user_password = text;
            }
            
            UIView *line = [ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(blank, text.frame.origin.y + text.frame.size.height) width:text.frame.size.width];
            [_inputViews addSubview:line];
        }
    }
    return _inputViews;
}


- (void)uploadImage:(UIImage *)image{
    if(image && [image isKindOfClass:[UIImage class]]){
        
        [ProjectHelper helper_getMainThread:^{
            self.user_icon.image = [ProjectHelper helper_getSquareIconFromImage:image];
        }];
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        
        [ProjectRequestHelper commonUpLoadImage:image progressBlock:^(CGFloat progress) {
            
        } sendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
            [ProjectHelper helper_getMainThread:^{
                if([progress respondsToSelector:@selector(hidden)]){
                    [progress performSelector:@selector(hidden)];
                }
            }];
            
            if(isSuccess && remotePath && [remotePath isKindOfClass:[NSString class]]){
                _user_Icon_Str = remotePath;
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"上传头像失败"];
            }
        }];
    }
    
}

- (ProjectClickView *)userAgreement{
    if(!_userAgreement){
        WS(weakSelf);
        NSString *agree = @"《用户使用协议》";
        NSString *str = [NSString stringWithFormat:@"%@%@",@"点击‘注册’表示已阅读并同意\r\n",agree];
        CGFloat textW = [ProjectHelper helper_getFontSizeWithString:str useFont:12.0 withWidth:self.view.frame.size.width - 20.0 andHeight:40.0].size.width;
        
        _userAgreement = [[ProjectClickView alloc] initWithFrame:CGRectMake(self.view.frame.size.width / 2 - textW / 2, self.view.frame.size.height - 40.0 - 10.0, textW, 40.0) bgView:self.view];
        
        _userAgreement.clickInvocation = ^(NSString * _Nonnull identify) {
            //注册用户协议
            [weakSelf registeAgreementAction];
        };
        _userAgreement.lab.frame = _userAgreement.bounds;
        _userAgreement.lab.numberOfLines = 0;
        _userAgreement.lab.textAlignment = NSTextAlignmentCenter;
        _userAgreement.lab.attributedText = [ProjectHelper helper_factoryMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:NSMakeRange(0, str.length - agree.length) font:12 andFont:12 color:PROJECT_COLOR_TEXTGRAY color:PROJECT_COLOR_BlLUE withText:str];
    }
    return _userAgreement;
}

- (void)registeAgreementAction{
    YiChatAgreementVC *agree = [YiChatAgreementVC initialVCName:@"注册协议"];
    agree.url = @"http://agent.yichatsystem.com/";
    [self.navigationController pushViewController:agree animated:YES];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_user_password resignFirstResponder];
    [_user_nick resignFirstResponder];
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
