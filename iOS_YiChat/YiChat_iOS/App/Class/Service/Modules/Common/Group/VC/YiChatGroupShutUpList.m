//
//  YiChatGroupShutUpList.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupShutUpList.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupManagerListCell.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ZFGroupHelper.h"
#import "YiChatGroupMemberOperationVC.h"
#import <MJRefresh/MJRefresh.h>
#import "ZFChatMessageHelper.h"

@interface YiChatGroupShutUpList ()<UITableViewDelegate,UITableViewDataSource>
    
@property (nonatomic,strong) NSArray *shutUpListData;
    
@property (nonatomic,strong) NSString *userPower;
    
@property (nonatomic,assign) BOOL isFirst;

@end

@implementation YiChatGroupShutUpList

+ (id)initialVC{
    YiChatGroupShutUpList *manager = [YiChatGroupShutUpList initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"groupShutUpList") leftItem:nil rightItem:nil];
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
//    id image = [self navBarGetRightBarItem];
//    if(image && [image isKindOfClass:[UIImageView class]]){
//        UIImageView *icon = image;
//        if(isCanClick){
//            icon.image = [UIImage imageNamed:@"copy2@3x.png"];
//        }
//        else{
//            icon.image = nil;
//        }
//    }
}
    
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self loadlist];
    
    _isFirst = NO;
}
    
- (void)loadlist{
    
    WS(weakSelf);
    [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:self.groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
            if(model.silentList && [model.silentList isKindOfClass:[NSArray class]]){
                
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < model.silentList.count; i ++) {
                    YiChatUserModel *userModel = [[YiChatUserModel alloc] initWithDic:model.silentList[i]];
                    if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
                        [arr addObject:userModel];
                    }
                }
                
                weakSelf.shutUpListData = arr;
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf reloadData];;
                }];
            }
        }
    }];
    
 
    
}
    
    
- (void)makeTable{
    
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:_shutUpListData.count]];
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom);
}
    
- (void)reloadData{
    [ProjectHelper helper_getMainThread:^{
        self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:_shutUpListData.count]];
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
        if(self.shutUpListData.count - 1 >= indexPath.row && self.shutUpListData.count > 0){
            YiChatUserModel *user = self.shutUpListData[indexPath.row];
            if(user && [user isKindOfClass:[YiChatUserModel class]]){
                if([[user getUserIdStr] isEqualToString:YiChatUserInfo_UserIdStr]){;
                    return UITableViewCellEditingStyleNone;
                }
                
                if(self.userPower && [self.userPower isKindOfClass:[NSString class]]){
                    if([self.userPower integerValue] < 1){
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
            
            if(self.shutUpListData.count - 1 >= indexPath.row && self.shutUpListData.count > 0){
                YiChatUserModel *user = self.shutUpListData[indexPath.row];
                
                if(user && [user isKindOfClass:[YiChatUserModel class]]){
                  
                    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                    
                    [tmp addObjectsFromArray:self.shutUpListData];
                    [tmp removeObjectAtIndex:indexPath.row];
                    
                    self.shutUpListData = tmp;
                    
                    [self reloadData];
                    
                    WS(weakSelf);
                    
                    [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:_groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                         [[YiChatUserManager defaultManagaer] removeLocalGroupMemberShutUpWithGroupId:_groupId userId:[user getUserIdStr] groupInfo:model];
                    }];
                   
                    
                    [ZFGroupHelper setGroupMemberShutUpWithGroupId:self.groupId userId:[user getUserIdStr] status:NO invocation:^(BOOL isSuccess, NSString * _Nonnull des) {
                        
                        if(isSuccess){
                            
                            
                        }
                        
                    }];
                    
                  
                }
            }
        }
    }
    
    //修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
        return @"取消禁言";
}
    //设置进入编辑状态时，Cell不会缩进
- (BOOL)tableView: (UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath
    {
        return NO;
    }
    
- (YiChatUserModel *)getDataWithIndex:(NSInteger)index{
    if(self.shutUpListData && [self.shutUpListData isKindOfClass:[NSArray class]]){
        if(self.shutUpListData.count - 1 >= index){
            return self.shutUpListData[index];
        }
    }
    return nil;
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
