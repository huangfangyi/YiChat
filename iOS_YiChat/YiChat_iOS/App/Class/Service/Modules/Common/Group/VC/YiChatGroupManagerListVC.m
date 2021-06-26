//
//  YiChatGroupManagerListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupManagerListVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupManagerListCell.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ZFGroupHelper.h"
#import "YiChatGroupMemberOperationVC.h"
#import <MJRefresh/MJRefresh.h>
#import "ZFChatMessageHelper.h"
#import "HTGroup.h"

@interface YiChatGroupManagerListVC ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong) NSArray *managerListData;

@property (nonatomic,strong) NSString *userPower;

@property (nonatomic,assign) BOOL isFirst;

@end

@implementation YiChatGroupManagerListVC

+ (id)initialVC{
    YiChatGroupManagerListVC *manager = [YiChatGroupManagerListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"groupManagerList") leftItem:nil rightItem:[UIImage imageNamed:@"copy2@3x.png"]];
    return manager;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];

    [self changeNavRightBtnWithType:NO];
    
    [self judgeUserRoleInGroup];
    _isFirst = YES;
    // Do any additional setup after loading the view.
}

- (void)addReresh{
  //  self.cTable.mj_header = []
}

- (void)judgeUserRoleInGroup{
    NSString *groupid = self.groupId;
    WS(weakSelf);
    if(groupid && [groupid isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] judgeUserSelfRoleInGroup:groupid invocation:^(NSString * _Nonnull role) {
            if(role && [role isKindOfClass:[NSString class]]){
                weakSelf.userPower = role;
            }
            else{
                weakSelf.userPower = @"0";
            }
            [ProjectHelper helper_getMainThread:^{
                if([self.userPower isKindOfClass:[NSString class]] && self.userPower){
                    if([self.userPower integerValue] == 2){
                        [self changeNavRightBtnWithType:YES];
                    }
                    else{
                        [self changeNavRightBtnWithType:NO];
                    }
                }
            }];
        }];
    }
}

- (void)changeNavRightBtnWithType:(BOOL)isCanClick{
    id image = [self navBarGetRightBarItem];
    if(image && [image isKindOfClass:[UIImageView class]]){
        UIImageView *icon = image;
        if(isCanClick){
            icon.image = [UIImage imageNamed:@"copy2@3x.png"];
        }
        else{
            icon.image = nil;
        }
    }
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self loadlist];
    
    _isFirst = NO;
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [ProjectHelper helper_getGlobalThread:^{
        [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:self.groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            
        }];
    }];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(self.userPower && [self.userPower isKindOfClass:[NSString class]]){
        if([self.userPower integerValue] == 2){
            
            if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
             
                
                if(self.managerListData && [self.managerListData isKindOfClass:[NSArray class]]){
                    
                    HTGroup *groupInfo = [ZFGroupHelper getHTGroupWithGroupId:self.groupId];
                    NSString *groupOnwer = @"";
                    
                    if(groupInfo.owner && [groupInfo.owner isKindOfClass:[NSString class]]){
                        
                        groupOnwer = groupInfo.owner;
                        
                    }
                    
                    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                    [arr addObjectsFromArray:self.managerListData];
                    
                    BOOL isHas = NO;
                    for (int i = 0; i < arr.count; i ++) {
                        YiChatUserModel *user = arr[i];
                        if([[user getUserIdStr] isEqualToString:groupOnwer]){
                            isHas = YES;
                        }
                    }
                    
                    if(!isHas){
                        if(groupOnwer.length > 0){
                            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:groupOnwer invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                
                                [ProjectHelper helper_getMainThread:^{
                                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                        [arr addObject:model];
                                    }
                                    YiChatGroupMemberOperationVC *addManager = [YiChatGroupMemberOperationVC initialVC];
                                    addManager.groupId = self.groupId;
                                    addManager.operationType = 2;
                                    addManager.managerList = arr;
                                    [self presentViewController:addManager animated:YES completion:nil];
                                }];
                                
                            }];
                            return;
                        }
                        
                    }
                    
                    YiChatGroupMemberOperationVC *addManager = [YiChatGroupMemberOperationVC initialVC];
                    addManager.groupId = self.groupId;
                    addManager.operationType = 2;
                    addManager.managerList = arr;
                    [self presentViewController:addManager animated:YES completion:nil];
                }
                
                
            }
        }
    }
}

- (void)loadlist{
    
    WS(weakSelf);
    [[YiChatUserManager defaultManagaer] fetchGroupManagerListWithGroupId:self.groupId invocation:^(NSArray * _Nonnull managerList) {
        if(managerList && [managerList isKindOfClass:[NSArray class]]){
            weakSelf.managerListData = managerList;
        }
        [ProjectHelper helper_getMainThread:^{
            [weakSelf reloadData];;
        }];
    }];
   
}


- (void)makeTable{
    
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:_managerListData.count]];
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom);
}

- (void)reloadData{
    [ProjectHelper helper_getMainThread:^{
         self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:_managerListData.count]];
        [self.cTable reloadData];
    }];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 0.00001;
}

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section{
    return 0.0001l;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *str = @"YiChatGroupManagerListVCCell";
    YiChatGroupManagerListCell *cell = [tableView dequeueReusableCellWithIdentifier:str];
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    CGFloat cellW = self.cTable.frame.size.width;
    if(!cell){
        cell = [YiChatGroupManagerListCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
    }
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
    
    YiChatUserModel *model = [self getDataWithIndex:indexPath.row];
    cell.model = model;
    return cell;
}

-(UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.managerListData.count - 1 >= indexPath.row && self.managerListData.count > 0){
        YiChatUserModel *user = self.managerListData[indexPath.row];
        if(user && [user isKindOfClass:[YiChatUserModel class]]){
            if([[user getUserIdStr] isEqualToString:YiChatUserInfo_UserIdStr]){;
                return UITableViewCellEditingStyleNone;
            }
            if(self.onwerId && [self.onwerId isKindOfClass:[NSString class]]){
                if([[user getUserIdStr] isEqualToString:self.onwerId]){
                    return UITableViewCellEditingStyleNone;
                }
                
            }
            if(self.userPower && [self.userPower isKindOfClass:[NSString class]]){
                if([self.userPower integerValue] < 2){
                    return UITableViewCellEditingStyleNone;
                }
            }
        }
        
    }
    return   UITableViewCellEditingStyleDelete;
}
//先要设Cell可编辑
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}
//进入编辑模式，按下出现的编辑按钮后
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView setEditing:NO animated:YES];
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        if(self.managerListData.count - 1 >= indexPath.row && self.managerListData.count > 0){
            YiChatUserModel *user = self.managerListData[indexPath.row];
            
            if(user && [user isKindOfClass:[YiChatUserModel class]]){
//                if([[user getUserIdStr] isEqualToString:YiChatUserInfo_UserIdStr]){
//                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"不能移除自己"];
//                    return;
//                }
//                if(self.onwerId && [self.onwerId isKindOfClass:[NSString class]]){
//                    if([[user getUserIdStr] isEqualToString:self.onwerId]){
//                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"不能移除群主"];
//                        return;
//                    }
//                }
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                
                [tmp addObjectsFromArray:self.managerListData];
                [tmp removeObjectAtIndex:indexPath.row];
                
                self.managerListData = tmp;
                
                [self reloadData];
                
                NSString *groupid = self.groupId;
                if(user && [user isKindOfClass:[YiChatUserModel class]] && groupid && [groupid isKindOfClass:[NSString class]]){
                    NSDictionary *param = [ProjectRequestParameterModel setGroupManagerParamWithGroupId:groupid userIds:[user getUserIdStr] status:0];
                    
                    [self setGroupManagerWithParam:param invocation:^(BOOL isSuccess) {
                        if(isSuccess){
                            NSString *userIdStr = [user getUserIdStr];
                            if(userIdStr && [userIdStr isKindOfClass:[NSString class]]){
                                
                                [ZFChatMessageHelper sendCancelSetManagerCmdWithGroupId:groupid userId:userIdStr  completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                                    
                                }];
                            }
                        }
                    }];
                }
            }
        }
    }
}

//修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"移除";
}
//设置进入编辑状态时，Cell不会缩进
- (BOOL)tableView: (UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}

- (YiChatUserModel *)getDataWithIndex:(NSInteger)index{
    if(self.managerListData && [self.managerListData isKindOfClass:[NSArray class]]){
        if(self.managerListData.count - 1 >= index){
            return self.managerListData[index];
        }
    }
    return nil;
}

- (void)setGroupManagerWithParam:(NSDictionary *)param invocation:(void(^)(BOOL isSuccess))invocation{
    if(param && [param isKindOfClass:[NSDictionary class]]){
        
        [ProjectRequestHelper setGroupManagerRequestWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    invocation(YES);
                }
                else{
                    invocation(NO);
                }
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            invocation(NO);
        }];
    }
    else{
        invocation(NO);
    }
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

