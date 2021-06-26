//
//  YiChatChangeNickNameVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeNickNameVC.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"

@interface YiChatChangeNickNameVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatChangeNickNameVC

+ (id)initialVC{
    YiChatChangeNickNameVC *info = [YiChatChangeNickNameVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"changeNickName") leftItem:nil rightItem:@"完成"];
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
    
    if([change isEqualToString:YiChatUserInfo_Nick] || change.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写要修改的信息"];
        return;
    }
    
    NSDictionary *param = [ProjectRequestParameterModel getUpdateUserInfoParamWithUserId:YiChatUserInfo_UserIdStr nick:change gender:nil avatar:nil appId:nil mobile:nil password:nil];
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
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 90.0) placeHolder:@"昵称" headerText:@"输入你的昵称" footerText:nil];
        [_input changeInputText:YiChatUserInfo_Nick];
        
    }
    return _input;
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
