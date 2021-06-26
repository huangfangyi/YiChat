//
//  YiChatAddFriendInputReason.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/8.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatAddFriendInputReason.h"
#import "ServiceGlobalDef.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ProjectRequestHelper.h"
#import "ZFChatFriendHelper.h"

@interface YiChatAddFriendInputReason ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatAddFriendInputReason

+ (id)initialVC{
    YiChatAddFriendInputReason *add = [YiChatAddFriendInputReason initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"addFriends") leftItem:nil rightItem:@"添加"];
    return add;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
     [self.view addSubview:self.input];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(self.friendId && [self.friendId isKindOfClass:[NSString class]]){
        [self addFriend];
    }
}

- (void)addFriend{
    NSString *friendId = self.friendId;
    NSString *reason = [self.input getInputText];
    
    NSDictionary *param = [ProjectRequestParameterModel getAddFriendParamWithReason:reason friendId:friendId];
    
    [ProjectRequestHelper addFriendWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"好友申请发送成功"];
                
                [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                    
                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                        NSDictionary *origin = [model getOriginDic];
                        if(origin && [origin isKindOfClass:[NSDictionary class]]){
                            
                            [ZFChatFriendHelper zfChatFriendHelperAddFriendWithUserId:friendId userInfo:origin completion:nil];
                        }
                    }
                }];
                
                [ProjectHelper helper_getMainThread:^{
                    NSArray *viewVCs = self.navigationController.viewControllers;
                    [self.navigationController popToViewController:viewVCs[viewVCs.count - 1 - 2] animated:YES];
                }];
            }
            else if(obj && [obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
    }];

}

- (YiChatChangeUserInfoInputView *)input{
    if(!_input){
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 90.0) placeHolder:@"" headerText:@"你需要发送验证申请" footerText:nil isTextView:NO];
        
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
