//
//  YiChatSetFriendsRemarkNameVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSetFriendsRemarkNameVC.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ZFGroupHelper.h"
#import "ZFChatHelper.h"

@interface YiChatSetFriendsRemarkNameVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatSetFriendsRemarkNameVC

+ (id)initialVC{
    YiChatSetFriendsRemarkNameVC *remark = [YiChatSetFriendsRemarkNameVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"setFriendsRemarkName") leftItem:nil rightItem:@"完成"];
    return remark;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
     [self.view addSubview:self.input];
    if(self.userModel && [self.userModel isKindOfClass:[YiChatUserModel class]]){
        [self.input changeInputText:[self.userModel remarkName]];
    }
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [_input resignKeyBoard];
    WS(weakSelf);
    
    NSString *userId = nil;
    userId = [self.userModel getUserIdStr];
    if(!(userId && [userId isKindOfClass:[NSString class]])){
        userId = self.userId;
    }
       
    NSString *change = [_input getInputText];
    NSString *remark = [self.userModel remarkName];
    
    if(remark && [remark isKindOfClass:[NSString class]]){
        if([change isEqualToString:remark] || change.length == 0){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写要修改的信息"];
            return;
        }
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        NSDictionary *param = [ProjectRequestParameterModel setFriendsRemarkNameWithFriendId:userId remark:change];
        
        [ProjectRequestHelper setFriendsRemarkNameWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
           [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
               
               if([obj isKindOfClass:[NSDictionary class]]){
                   
                   NSDictionary *dic = obj[@"data"];
                   if(dic && [dic isKindOfClass:[NSDictionary class]]){
                       YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:dic];
                       if(model && [model isKindOfClass:[YiChatUserModel class]]){
                           [[YiChatUserManager defaultManagaer] updateUserInfoWithModel:model];
                           self.userModel = model;
                           [ProjectHelper helper_getMainThread:^{
                               [self.input changeInputText:change];
                               [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"好友备注修改成功"];
                               
                               [self.navigationController popViewControllerAnimated:YES];
                           }];
                           return ;
                       }
                   }
                   [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改好友备注出错"];
                   
               }
               else if([obj isKindOfClass:[NSString class]]){
                   [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
               }
               else{
                   [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改好友备注出错"];
               }
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改好友备注出错"];
        }];
    }
    else{
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改好友备注出错"];
        return;
    }
}

- (YiChatChangeUserInfoInputView *)input{
    if(!_input){
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 90.0) placeHolder:@"好友备注" headerText:@"修改备注" footerText:nil];
        
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
