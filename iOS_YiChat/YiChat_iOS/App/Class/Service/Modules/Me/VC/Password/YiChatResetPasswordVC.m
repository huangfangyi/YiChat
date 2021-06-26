//
//  YiChatResetPasswordVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatResetPasswordVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectClickView.h"
#import "ProjectJudgeHelper.h"
#import "YiChatUserManager.h"
#import "ProjectRequestHelper.h"
#import "ProjectTextInputView.h"

@interface YiChatResetPasswordVC ()<UIGestureRecognizerDelegate,UITextFieldDelegate>

//找回密码 type == 0 重置密码 type ==1
@property (nonatomic,assign) NSInteger type ;

@property (nonatomic,strong) UIView *inputViews;

@property (nonatomic,strong) ProjectTextInputView *inputPhoneNum;
@property (nonatomic,strong) ProjectTextInputView *inputCertify;
@property (nonatomic,strong) ProjectTextInputView *inputPassword;
@property (nonatomic,strong) ProjectTextInputView *inputPasswordAgain;


@property (nonatomic,strong) NSString *certifyNumStorage;
@property (nonatomic,strong) NSString *phoneNumStorage;

@end

@implementation YiChatResetPasswordVC

+ (id)initialVC{
    YiChatResetPasswordVC *reset = [YiChatResetPasswordVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"resetPassword") leftItem:nil rightItem:@"完成"];
    reset.type = 1;
    return reset;
}

+ (id)initialVCForGetBackPassword{
    YiChatResetPasswordVC *reset = [YiChatResetPasswordVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"getbackPassword") leftItem:nil rightItem:@"完成"];
    reset.type = 0;
    return reset;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeUI];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    
    NSString *phone = self.inputPhoneNum.textInput.text;
    NSString *newPhoneCertify = [ProjectJudgeHelper helper_judgePhone:phone];
    NSString *certify = self.inputCertify.textInput.text;
    
    
    if(newPhoneCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:newPhoneCertify];
        return;
    }
    
    if(YiChatProjext_CertifyPower){
        if(certify.length < 1){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入验证码"];
            return;
        }
        
        if(!(self.certifyNumStorage && [self.certifyNumStorage isKindOfClass:[NSString class]])){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请发送验证码校验"];
            return;
        }
        if(![certify isEqualToString:self.certifyNumStorage]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"验证码输入错误"];
            return;
        }
        
        if(!_phoneNumStorage){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"参数出错"];
            return;
        }
        if(![_phoneNumStorage isEqualToString:phone]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"手机号输入出错"];
            return;
        }
    }
    
    NSString *password = _inputPassword.textInput.text;
    NSString *passwordAgain = _inputPasswordAgain.textInput.text;
    
    NSString *newPasswordCertify = [ProjectJudgeHelper helper_judgePassword:password];
    NSString *newPasswordAgainCertify = [ProjectJudgeHelper helper_judgePassword:passwordAgain];
    
    if(newPasswordCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:newPasswordCertify];
        return;
    }
    if(newPasswordAgainCertify){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:newPasswordAgainCertify];
        return;
    }
    
    if(![password isEqualToString:passwordAgain]){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"新密码输入不一致"];
        return;
    }
    
    
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel getResetPasswordParamWithPassword:password mobile:phone];
    
    [ProjectRequestHelper resetPasswordWithParameters:param headerParameters:nil progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                [ProjectHelper helper_getMainThread:^{
                    
                    if(_type == 1){
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"密码重置成功"];
                    }
                    else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"密码找回成功"];
                    }
                    [weakSelf.navigationController popViewControllerAnimated:YES];
                    
                    
                }];
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
       
    }];
}

- (void)makeUI{
    [self.view addSubview:self.inputViews];
    if(self.type == 1){
        self.inputPhoneNum.textInput.delegate = self;
        self.inputPhoneNum.textInput.text = YiChatUserInfo_Mobile;
    }
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    if(textField == self.inputPhoneNum.textInput){
        if(self.type == 1){
            return NO;
        }
    }
    return YES;
}


- (UIView *)inputViews{
    if(!_inputViews){
        _inputViews = [[UIView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 20.0, PROJECT_SIZE_WIDTH, 0)];
        _inputViews.backgroundColor = [UIColor whiteColor];
        CGFloat blank = PROJECT_SIZE_NAV_BLANK;
        
        if(self.type == 0){
            _inputViews.frame = CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 20.0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_INPUT_CELLH * 3);
            //找回密码
            [_inputViews addSubview:self.inputPhoneNum];
            if(YiChatProjext_CertifyPower){
                  _inputViews.frame = CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 20.0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_INPUT_CELLH * 4);
                [_inputViews addSubview:self.inputCertify];
            }
            [_inputViews addSubview:self.inputPassword];
            [_inputViews addSubview:self.inputPasswordAgain];
        }
        else if(self.type == 1){
            _inputViews.frame = CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 20.0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_INPUT_CELLH * 3);
            
            [_inputViews addSubview:self.inputPhoneNum];
             if(YiChatProjext_CertifyPower){
                _inputViews.frame = CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 20.0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_INPUT_CELLH * 4);
                 [_inputViews addSubview:self.inputCertify];
             }
            
            [_inputViews addSubview:self.inputPassword];
            [_inputViews addSubview:self.inputPasswordAgain];
        }
    }
    return _inputViews;
}

- (ProjectTextInputView *)inputPhoneNum{
    if(!_inputPhoneNum){
        _inputPhoneNum = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, self.inputViews.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_INPUT_CELLH)];
        _inputPhoneNum.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        _inputPhoneNum.inputStyle = ProjectInputViewStylePhoneInput;
        [_inputPhoneNum createUI];
    }
    return _inputPhoneNum;
}

- (ProjectTextInputView *)inputCertify{
    if(!_inputCertify){
        _inputCertify = [[ProjectTextInputView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.inputPhoneNum.frame.origin.y + self.inputPhoneNum.frame.size.height, self.inputViews.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_INPUT_CELLH)];
        _inputCertify.inputStyle = ProjectInputViewStyleInputCertify;
        _inputCertify.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        
        WS(weakSelf);
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
                        [weakSelf.inputPhoneNum.textInput setUserInteractionEnabled:YES];
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

- (ProjectTextInputView *)inputPassword{
    if(!_inputPassword){
        CGFloat y = 0;
        
        if(YiChatProjext_CertifyPower){
            y = self.inputCertify.frame.origin.y + self.inputCertify.frame.size.height;
        }
        else{
            y = self.inputPhoneNum.frame.origin.y + self.inputPhoneNum.frame.size.height;
        }
        
        CGRect frame  = CGRectMake(PROJECT_SIZE_NAV_BLANK, y, self.inputViews.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_INPUT_CELLH);
        
        _inputPassword = [[ProjectTextInputView alloc] initWithFrame:frame];
        _inputPassword.inputStyle = ProjectInputViewPasswordInput;
        _inputPassword.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        [_inputPassword createUI];
        
    }
    return _inputPassword;
}

- (ProjectTextInputView *)inputPasswordAgain{
    if(!_inputPasswordAgain){
        CGRect frame  = CGRectMake(PROJECT_SIZE_NAV_BLANK, self.inputPassword.frame.origin.y + self.inputPassword.frame.size.height, self.inputViews.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_INPUT_CELLH);
        
        _inputPasswordAgain = [[ProjectTextInputView alloc] initWithFrame:frame];
        _inputPasswordAgain.inputStyle = ProjectInputViewPasswordInput;
        _inputPasswordAgain.isShowHorizontalLine = [NSNumber numberWithBool:YES];
        [_inputPasswordAgain createUI];
        _inputPasswordAgain.textInput.placeholder = @"请再次输入密码";
    }
    return _inputPasswordAgain;
}

- (void)resignKeyBoard{
    [_inputCertify.textInput resignFirstResponder];
    [_inputPhoneNum.textInput resignFirstResponder];
    [_inputPassword.textInput resignFirstResponder];
    [_inputPasswordAgain.textInput resignFirstResponder];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self resignText];
}

- (void)resignText{
    [_inputCertify.textInput resignFirstResponder];
    [_inputPhoneNum.textInput resignFirstResponder];
    [_inputPassword.textInput resignFirstResponder];
    [_inputPasswordAgain.textInput resignFirstResponder];
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
