//
//  YiChatRegisterVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRegisterVC.h"
#import "LoginRegisterTitleView.h"
#import "ServiceGlobalDef.h"
#import "ProjectTextInputView.h"
#import "ProjectClickView.h"
#import "ProjectRequestHelper.h"
#import "ProjectJudgeHelper.h"
#import "YiChatAgreementVC.h"

@interface YiChatRegisterVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) UIScrollView *scroll;

@property (nonatomic,strong) LoginRegisterTitleView *titleView;
@property (nonatomic,strong) UIView *inputView;

@property (nonatomic,strong) ProjectTextInputView *inputAreaCountry;
@property (nonatomic,strong) ProjectTextInputView *inputCertify;
@property (nonatomic,strong) ProjectTextInputView *inputPhoneNum;

@property (nonatomic,assign) BOOL isPushIn;

@property (nonatomic,strong) UIView *backView;

@property (nonatomic,strong) ProjectClickView *userAgreement;

@property (nonatomic,strong) NSString *certifyNumStorage;
@property (nonatomic,strong) NSString *phoneNumStorage;

@end

#define YiChatRegisteVC_InputCellH 45.0f
#define YiChatRegisteVC_ClickBtnH 50.0f
@implementation YiChatRegisterVC

+ (id)initialForCertify:(BOOL)isPushIn{
    YiChatRegisterVC *registerVC = nil;
    if(isPushIn == YES){
        registerVC = [YiChatRegisterVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:@"" leftItem:nil rightItem:nil];
    }
    else{
        registerVC = [YiChatRegisterVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_11 centeritem:@"" leftItem:nil rightItem:nil];
    }
    registerVC.isPushIn = isPushIn;
    return registerVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    if(_isPushIn == NO){
//        UILabel *lab = [self navBarGetLeftBarItem];
//        lab.text = PROJECT_TEXT_APPNAME;
//        lab.textColor = [UIColor blackColor];
    }
    [self.view addSubview:self.scroll];
    // Do any additional setup after loading the view.
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
        //短信验证码登录
        _titleView = [[LoginRegisterTitleView alloc] initWithFrame:CGRectMake(0, 20.0, self.backView.frame.size.width, 100.0) titile:[NSString stringWithFormat:@"%@%@",@"欢迎来到",PROJECT_TEXT_APPNAME] bgView:self.backView];
    }
    return _titleView;
}

- (UIView *)inputView{
    if(!_inputView){
        
        _inputView = [[UIView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.titleView.frame.origin.y + self.titleView.frame.size.height, self.backView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, YiChatRegisteVC_InputCellH)];
        
     //   [_inputView addSubview:self.inputAreaCountry];
        [_inputView addSubview:self.inputPhoneNum];
        
        if(YiChatProjext_CertifyPower){
            _inputView.frame = CGRectMake(PROJECT_SIZE_NAV_BLANK, self.titleView.frame.origin.y + self.titleView.frame.size.height, self.backView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, YiChatRegisteVC_InputCellH * 2);
            [_inputView addSubview:self.inputCertify];
        }
        
        ProjectClickView *certifyLog = [ProjectClickView createClickViewWithFrame:CGRectMake(_inputView.frame.origin.x, _inputView.frame.origin.y + _inputView.frame.size.height + 20.0, _inputView.frame.size.width, YiChatRegisteVC_ClickBtnH) title:@"下一步" type:0];
        certifyLog.userInteractionEnabled = YES;
        
        WS(weakSelf);
        certifyLog.clickInvocation = ^(NSString * _Nonnull identify) {
            [weakSelf goRegister];
        };
        [_backView addSubview:certifyLog];
        
        _inputView.userInteractionEnabled = YES;
    }
    return _inputView;
}

- (ProjectTextInputView *)inputAreaCountry{
    if(!_inputAreaCountry){
        _inputAreaCountry = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, 0, self.inputView.frame.size.width, YiChatRegisteVC_InputCellH)];
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
        //CGRectMake(0, self.inputAreaCountry.frame.origin.y + self.inputAreaCountry.frame.size.height, self.inputView.frame.size.width, YiChatRegisteVC_InputCellH)
        
        _inputPhoneNum = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, 0, self.inputView.frame.size.width, YiChatRegisteVC_InputCellH)];
        _inputPhoneNum.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        _inputPhoneNum.inputStyle = ProjectInputViewStylePhoneInput;
        [_inputPhoneNum createUI];
    }
    return _inputPhoneNum;
}

- (ProjectTextInputView *)inputCertify{
    if(!_inputCertify){
        WS(weakSelf);
        
        _inputCertify = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(0, self.inputPhoneNum.frame.origin.y + self.inputPhoneNum.frame.size.height, self.inputView.frame.size.width, YiChatRegisteVC_InputCellH)];
        _inputCertify.inputStyle = ProjectInputViewStyleInputCertify;
        _inputCertify.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        
        HelperReturnBOOLInvocation click = ^BOOL(void){
            __block BOOL isCan = NO;
            __block BOOL isJudgePass = YES;
            __block NSString *phone = @"";
            
            dispatch_semaphore_t lockJudge = dispatch_semaphore_create(0);
          
            dispatch_async(dispatch_get_main_queue(), ^{
                
                phone = weakSelf.inputPhoneNum.textInput.text;
                
                 NSString *phoneCertify = [ProjectJudgeHelper helper_judgePhone:phone];
                if(phoneCertify){
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:phoneCertify];
                    isJudgePass = NO;
                }
                
                [weakSelf.inputPhoneNum.textInput resignFirstResponder];
                
                 dispatch_semaphore_signal(lockJudge);
            });
            dispatch_semaphore_wait(lockJudge, DISPATCH_TIME_FOREVER);
            
            if(!isJudgePass){
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
                            weakSelf.phoneNumStorage = phone;
                        }
                    }
                    else if(obj && [obj isKindOfClass:[NSString class]]){
//                        [weakSelf.inputPhoneNum.textInput setUserInteractionEnabled:YES];
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                    }
                    else{
                        [weakSelf.inputPhoneNum.textInput setUserInteractionEnabled:YES];
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

- (ProjectClickView *)userAgreement{
    if(!_userAgreement){
        WS(weakSelf);
        NSString *agree = @"《用户使用协议》";
        NSString *str = [NSString stringWithFormat:@"%@%@",@"点击‘注册’表示已阅读并同意\r\n",agree];
        CGFloat textW = [ProjectHelper helper_getFontSizeWithString:str useFont:12.0 withWidth:_backView.frame.size.width - 20.0 andHeight:40.0].size.width;
        
        _userAgreement = [[ProjectClickView alloc] initWithFrame:CGRectMake(_backView.frame.size.width / 2 - textW / 2, _backView.frame.size.height - 40.0 - 10.0, textW, 40.0) bgView:_backView];
        
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

- (void)makeUI{
    [self titleView];
    [self.backView addSubview:self.inputView];
//    [self.backView addSubview:self.userAgreement];
}

- (void)loginForCertify{
    
}

- (void)loginForPassword{
    
}

- (void)goRegister{
    
    NSString *inputPhone = _inputPhoneNum.textInput.text;
    NSString *phoneCertify = [ProjectJudgeHelper helper_judgePhone:_inputPhoneNum.textInput.text];
    if(phoneCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:phoneCertify];
        return;
    }

    if(YiChatProjext_CertifyPower){
        NSString *certifyCerity = _inputCertify.textInput.text;
        
        if(certifyCerity.length == 0 && certifyCerity == nil){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"验证码不能为空"];
            return;
        }
        
        if(self.certifyNumStorage && [self.certifyNumStorage isKindOfClass:[NSString class]]){
            if(![self.certifyNumStorage isEqualToString:certifyCerity]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"验证码输入有误 请重新输入"];
                return;
            }
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请完成验证码校验"];
            return;
        }
        
        if(!_phoneNumStorage){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"参数出错"];
            return;
        }
        if(![_phoneNumStorage isEqualToString:inputPhone]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"手机号输入出错"];
            return;
        }
    }
    self.inputPhoneNum.textInput.userInteractionEnabled = YES;
    
    UIViewController *vc = [ProjectHelper helper_getVCWithName:@"YiChatRegisteInputInfo" initialMethod:@selector(initialVC)];
    [vc setValue:inputPhone forKey:@"phoneNumStr"];
    [self.navigationController pushViewController:vc animated:YES];

}


- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_inputPhoneNum.textInput  resignFirstResponder];
    [_inputCertify.textInput resignFirstResponder];
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
