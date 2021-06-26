//
//  YiChatChangeGroupDesVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeGroupDesVC.h"
#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"
#import "YiChatUserManager.h"
#import "ZFGroupHelper.h"
#import "ZFChatHelper.h"

@interface YiChatChangeGroupDesVC ()<UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatChangeUserInfoInputView *input;

@end

@implementation YiChatChangeGroupDesVC

+ (id)initialVC{
    YiChatChangeGroupDesVC *info = [YiChatChangeGroupDesVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"changeGroupDes") leftItem:nil rightItem:@"完成"];
    return info;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.view addSubview:self.input];
    
    if(self.groupInfo && [self.groupInfo isKindOfClass:[YiChatGroupInfoModel class]]){
        NSString *name = self.groupInfo.groupDescription;
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
    NSString *name = self.groupInfo.groupDescription;
    
    if([change isEqualToString:name] || change.length == 0){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写要修改的信息"];
        return;
    }
    if(self.groupInfo && [self.groupInfo isKindOfClass:[YiChatGroupInfoModel class]]){
        NSString *groupId = [self.groupInfo getGroupId];
        
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
            if(group && [group isKindOfClass:[HTGroup class]]){
                
                group.groupDescription = change;

                if(!([group.groupAvatar isKindOfClass:[NSString class]] && group.groupAvatar)){
                    group.groupAvatar = @"";
                }
                if(!([group.groupName isKindOfClass:[NSString class]] && group.groupName)){
                    group.groupName = @"";
                }
                
                [ZFGroupHelper updateGroup:group withNickname:YiChatUserInfo_Nick success:^(HTGroup * _Nonnull aGroup) {
                    if(group && [group isKindOfClass:[HTGroup class]]){
                        [ProjectHelper helper_getMainThread:^{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群描述修改成功"];
                            self.groupInfo.groupDescription = change;
                            
                            [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.groupInfo
                                                                               invocation:^(BOOL isSuccess) {
                                                                                   
                                                                               }];
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
        _input = [[YiChatChangeUserInfoInputView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + 10.0, self.view.frame.size.width, 160.0) placeHolder:@"" headerText:@"修改群详情" footerText:nil isTextView:YES];
        
    }
    return _input;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_input resignKeyBoard];
}
@end
