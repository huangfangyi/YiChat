//
//  YiChatChangeUserIdVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeUserIdVC.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ProjectJudgeHelper.h"

@interface YiChatChangeUserIdVC ()<UIGestureRecognizerDelegate,UITextFieldDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatChangeUserIdVC

+ (id)initialVC{
    YiChatChangeUserIdVC *info = [YiChatChangeUserIdVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"changeUserNum") leftItem:nil rightItem:@"完成"];
    return info;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view addSubview:self.input];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [_input resignKeyBoard];
    
    WS(weakSelf);
    NSString *change = [_input getInputText];
    
    if(YiChatUserInfo_AppId){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"%@号只能设置一次",PROJECT_TEXT_APPNAME]];
        return;
    }
    
    if([change isEqualToString:YiChatUserInfo_AppId] || change.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写要修改的信息"];
        return;
    }
    
    
    NSDictionary *param = [ProjectRequestParameterModel getUpdateUserInfoParamWithUserId:YiChatUserInfo_UserIdStr nick:nil gender:nil avatar:nil appId:change mobile:nil password:nil];
    
    [ProjectRequestHelper getUpdateInfoWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.navigationController popViewControllerAnimated:YES];
                }];
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
    }];
}

- (YiChatChangeUserInfoInputView *)input{
    if(!_input){
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 90.0) placeHolder:@"可以使用8-15个数字，字母（必须字母开头）" headerText:[NSString stringWithFormat:@"%@号",PROJECT_TEXT_APPNAME] footerText:[NSString stringWithFormat:@"%@号只能设置一次",PROJECT_TEXT_APPNAME]];
        [_input changeInputText:YiChatUserInfo_AppId];
        UITextField *text = [_input getInputTextControl];
        [text addTarget:self action:@selector(textInputValueChanged:) forControlEvents:UIControlEventEditingChanged];
        if(text && [text isKindOfClass:[UITextField class]]){
            text.delegate = self;
        }
    }
    return _input;
}
    
- (void)textInputValueChanged:(UITextField *)text{
    NSString *str = text.text;
    NSMutableString *result = [NSMutableString stringWithCapacity:0];
    if(str.length > 0){
        for (int i = 0; i < str.length; i ++) {
            NSString *tmp = [str substringWithRange:NSMakeRange(i, 1)];
            if([ProjectJudgeHelper helper_InputOnlyNumOrChracter:tmp]){
                [result appendString:tmp];
            }
        }
        if(result.length > 15){
            str = [result substringToIndex:15];
        }
        else{
            str = result;
        }
        text.text = str;
    }
}
    
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
    if(!YiChatUserInfo_AppId){
        return YES;
    }
    else{
        return NO;
    }
}
    

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_input resignKeyBoard];
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
