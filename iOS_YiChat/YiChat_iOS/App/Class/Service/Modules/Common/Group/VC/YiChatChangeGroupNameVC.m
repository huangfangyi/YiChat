//
//  YiChatChangeGroupNameVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeGroupNameVC.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ZFGroupHelper.h"
#import "ZFChatHelper.h"

@interface YiChatChangeGroupNameVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatChangeGroupNameVC
+ (id)initialVC{
    YiChatChangeGroupNameVC *info = [YiChatChangeGroupNameVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"changeGroupNickName") leftItem:nil rightItem:@"完成"];
    return info;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.view addSubview:self.input];
    
    if(self.groupInfo && [self.groupInfo isKindOfClass:[YiChatGroupInfoModel class]]){
        NSString *name = self.groupInfo.groupName;
        if(name && [name isKindOfClass:[NSString class]]){
            [self.input changeInputText:name];
        }
        else{
            [self.input changeInputText:@""];
        }
    }
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [_input resignKeyBoard];
    WS(weakSelf);
    NSString *change = [_input getInputText];
    NSString *name = self.groupInfo.groupName;
    
    if([change isEqualToString:name] || change.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写要修改的信息"];
        return;
    }
    if(self.groupInfo && [self.groupInfo isKindOfClass:[YiChatGroupInfoModel class]]){
         NSString *groupId = [self.groupInfo getGroupId];
        
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
            if(group && [group isKindOfClass:[HTGroup class]]){
                
                change = [YiChatUserManager disable_emoji:change];
                
                group.groupName = change;
                if(!([group.groupDescription isKindOfClass:[NSString class]] && group.groupDescription)){
                    group.groupDescription = @"";
                }
                if(!([group.groupAvatar isKindOfClass:[NSString class]] && group.groupAvatar)){
                    group.groupAvatar = @"";
                }
                
                [ZFGroupHelper updateGroup:group withNickname:YiChatUserInfo_Nick success:^(HTGroup * _Nonnull aGroup) {
                    if(group && [group isKindOfClass:[HTGroup class]]){
                        [ProjectHelper helper_getMainThread:^{
                            
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群昵称修改成功"];
                            self.groupInfo.groupName = change;
                            
                            [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.groupInfo
                                                                               invocation:^(BOOL isSuccess) {
                                                                         
                                                                               }];
                            
                            [self.navigationController popViewControllerAnimated:YES];
                        }];
                    }
                    else{
                       [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改群信息出错"];
                    }
                } failure:^(NSError * _Nonnull error) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
                }];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取群信息出错"];
                return;
            }
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取群信息出错"];
            return;
        }
    }
    else{
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取群信息出错"];
        return;
    }
   
 
}

- (YiChatChangeUserInfoInputView *)input{
    if(!_input){
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 90.0) placeHolder:@"群名称" headerText:@"修改群名称" footerText:nil];
        
    }
    return _input;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_input resignKeyBoard];
}
@end
