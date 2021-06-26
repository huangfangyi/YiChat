//
//  YiChatGroupInfoVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupInfoVC.h"
#import "ServiceGlobalDef.h"

#import "YiChatGroupInfoModel.h"
#import "ZFChatHelper.h"
#import "YiChatGroupMemberListView.h"
#import "YiChatGroupInfoCell.h"

#import "YiChatUserManager.h"
#import "ProjectClickView.h"

#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"

#import "YiChatGroupMemberOperationVC.h"

#import "YiChatChangeGroupDesVC.h"
#import "YiChatChangeGroupNameVC.h"
#import "YiChatFriendInfoVC.h"

#import "YiChatGroupManagerListVC.h"

#import "ZFGroupHelper.h"

#import "YiChatGroupMemberListVC.h"

#import "ZFChatMessageHelper.h"
#import "YiChatGroupAnnouncementVC.h"

#import "YiChatGroupShutUpList.h"
#import "YiChatSearchGroupMsgVC.h"
@interface YiChatGroupInfoVC ()<UIScrollViewDelegate>

@property (nonatomic,strong) NSArray *toolCellDataArr;

@property (nonatomic,strong) YiChatGroupMemberListView *groupMemberListView;

@property (nonatomic,strong) NSArray *groupMemberList;

@property (nonatomic,strong) NSArray *totalMemberlist;

//0 普通成员 1 管理员 2群主
@property (nonatomic,assign) NSInteger currentPersonPower;

@property (nonatomic,assign) BOOL groupSilenceState;

@property (nonatomic,assign) BOOL isFirst;

@end


#define YiChatGroupInfoVC_GroupMemberList @"群成员列表"
#define YiChatGroupInfoVC_GroupName @"群名称"
#define YiChatGroupInfoVC_GroupAvtor @"群头像"
#define YiChatGroupInfoVC_GroupID @"群ID"
#define YiChatGroupInfoVC_GroupDes @"群描述"
#define YiChatGroupInfoVC_GroupMsgClose @"消息免打扰"
#define YiChatGroupInfoVC_GroupSilence @"群禁言"
#define YiChatGroupInfoVC_GroupManagerList @"管理员列表"
#define YiChatGroupInfoVC_GroupShutUpPersonList @"群禁言列表"
#define YiChatGroupInfoVC_DeleteGroupMsg @"清空聊天记录"
//#define YiChatGroupInfoVC_GroupDescribe @"群描述"
#define YiChatGroupInfoVC_QueryGroupMsg @"查询聊天记录"
@implementation YiChatGroupInfoVC

+ (id)initialVC{
    YiChatGroupInfoVC *groupInfo = [YiChatGroupInfoVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"groupDetail") leftItem:nil rightItem:@"群公告"];
    return groupInfo;
}

- (void)dealloc{

}

- (void)viewDidLoad {
    [super viewDidLoad];
    _isFirst = YES;
    
    [self makeTable];
    
    [self groupMemberList];
    
    [self updateGroupInfo];
    // Do any additional setup after loading the view.
}

//群公告
- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    BOOL isManager = NO;
    if (self.currentPersonPower == 1 || self.currentPersonPower == 2) {
        isManager = YES;
    }
    YiChatGroupAnnouncementVC *vc = [YiChatGroupAnnouncementVC initialVCWithManeger:isManager];
    vc.groupID = self.groupId;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];

    [ProjectHelper helper_getGlobalThread:^{
        if(!_isFirst){
            
            [self fetchGroupInfo];
            
            [self fetchGroupMemberBackGroud];
        }
        
       
    }];
    _isFirst = NO;
}

- (void)updateGroupInfoBack{
    NSString *groupId = nil;
      if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
          groupId = self.groupId;
      }
      else if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[NSString class]]){
          groupId = [self.groupInfoModel getGroupId];
      }
      
      if(groupId && [groupId isKindOfClass:[NSString class]]){
          [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
          }];
      }
}
- (void)updateGroupInfo{
    NSString *groupId = nil;
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    else if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[NSString class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    
    WS(weakSelf);
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                
                _groupInfoModel = model;
                
                _currentPersonPower = model.roleType;
                
                _groupSilenceState = model.groupSilentStatus;
                
               
                [ProjectHelper helper_getMainThread:^{
                    if(_currentPersonPower == 2 || _currentPersonPower == 1){
                        [self.groupMemberListView changeIsHasAdd:YES isHasDelete:YES];
                    }
                    else{
                        [self.groupMemberListView changeIsHasAdd:NO isHasDelete:NO];
                    }
                    
                    self.cTable.tableFooterView = [self getTableFooterView];
                }];
                
                [self loadGroupMemberDataInvocation:^{
                    
                    [ProjectHelper helper_getMainThread:^{
                        
                        if(weakSelf.groupMemberList && [weakSelf.groupMemberList isKindOfClass:[NSArray class]]){
                            if(weakSelf.groupMemberList.count > 0){
                                [weakSelf.groupMemberListView changeDataSource:weakSelf.groupMemberList];
                            }
                        }
                        [weakSelf.groupMemberListView updateAddDeleteUIData];
                        
                        [weakSelf tableUpdate];
                    }];
                    
                }];
            }
        }];
    }
}

- (void)fetchGroupInfo{
    NSString *groupId = nil;
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    else if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[NSString class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    
    WS(weakSelf);
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                _groupInfoModel = model;
                
                _currentPersonPower = model.roleType;
                
                _groupSilenceState = model.groupSilentStatus;
                
                
                [ProjectHelper helper_getMainThread:^{
                    if(_currentPersonPower == 2 || _currentPersonPower == 1){
                        [self.groupMemberListView changeIsHasAdd:YES isHasDelete:YES];
                    }
                    else{
                        [self.groupMemberListView changeIsHasAdd:NO isHasDelete:NO];
                    }
                    
                    self.cTable.tableFooterView = [self getTableFooterView];
                }];
                
                [self loadGroupMemberDataInvocation:^{
                    
                    [ProjectHelper helper_getMainThread:^{
                        
                        if(weakSelf.groupMemberList && [weakSelf.groupMemberList isKindOfClass:[NSArray class]]){
                            if(weakSelf.groupMemberList.count > 0){
                                [weakSelf.groupMemberListView changeDataSource:weakSelf.groupMemberList];
                            }
                        }
                        [weakSelf.groupMemberListView updateAddDeleteUIData];
                        
                        [weakSelf tableUpdate];
                    }];
                    
                }];
            }
        }];
    }
}

- (void)loadGroupMemberDataInvocation:(void(^)(void))invocation{
    WS(weakSelf);
    NSString *groupId = nil;
    if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    if(!groupId){
        invocation();
        return ;
    }
    
    [[YiChatUserManager defaultManagaer] requestGroupMemberslistQuicklyWithGroupId:groupId invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            if(groupMemberlist.count > 0){
                self.groupMemberList = groupMemberlist;
            }
        }
        invocation();
    }];
}

- (void)fetchGroupMemberBackGroud{
    NSString *groupId = nil;
    if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    if(!groupId){
        return ;
    }
    
    [[YiChatUserManager defaultManagaer] updateGroupMemberslistWithGroupId:groupId invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            if(groupMemberlist.count >0 ){
                self.totalMemberlist = groupMemberlist;
            }
        }
    }];
}

- (void)makeTable{
    
    _toolCellDataArr = [self getOriginCell];
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < _toolCellDataArr.count; i ++) {
        NSArray *obj = _toolCellDataArr[i];
        if([obj isKindOfClass:[NSArray class]]){
            [tmp addObject:[NSNumber numberWithInteger:obj.count]];
        }
    }
    self.sectionsRowsNumSet = tmp;
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom);
}

- (void)tableUpdate{
    _toolCellDataArr = [self getOriginCell];
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < _toolCellDataArr.count; i ++) {
        NSArray *obj = _toolCellDataArr[i];
        if([obj isKindOfClass:[NSArray class]]){
            [tmp addObject:[NSNumber numberWithInteger:obj.count]];
        }
    }
    self.sectionsRowsNumSet = tmp;
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
    }];
    
}

- (NSArray *)getOriginCell{
    if(_currentPersonPower >= 1){
        //YiChatGroupInfoVC_GroupDes  #define YiChatGroupInfoVC_DeleteGroupMsg @"清空群消息"
//        #define YiChatGroupInfoVC_GroupDescribe @"群描述"
     return @[@[YiChatGroupInfoVC_GroupMemberList],@[YiChatGroupInfoVC_GroupID,YiChatGroupInfoVC_GroupName,YiChatGroupInfoVC_GroupAvtor,YiChatGroupInfoVC_GroupDes],@[YiChatGroupInfoVC_GroupMsgClose,YiChatGroupInfoVC_GroupSilence],@[YiChatGroupInfoVC_GroupManagerList],@[YiChatGroupInfoVC_GroupShutUpPersonList,YiChatGroupInfoVC_DeleteGroupMsg,YiChatGroupInfoVC_QueryGroupMsg]];
    }
    else{
        //YiChatGroupInfoVC_GroupDes
         return @[@[YiChatGroupInfoVC_GroupMemberList],@[YiChatGroupInfoVC_GroupID,YiChatGroupInfoVC_GroupName,YiChatGroupInfoVC_GroupAvtor,YiChatGroupInfoVC_GroupDes],@[YiChatGroupInfoVC_GroupMsgClose,YiChatGroupInfoVC_DeleteGroupMsg,YiChatGroupInfoVC_QueryGroupMsg]];
    }
}

- (UIView *)getTableFooterView{
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, PROJECT_SIZE_COMMON_CELLH + 60.0) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
    
    ProjectClickView *click = [ProjectClickView createClickViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 30.0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_COMMON_CELLH) title:[self getTableFooterBtnTitleWithPowerLevel:_currentPersonPower] type:0];
    [back addSubview:click];
    
    WS(weakSelf);
    click.clickInvocation = ^(NSString * _Nonnull identify) {
        if(weakSelf.currentPersonPower == 2){
            //解散群组
            [ZFGroupHelper deleteGroupWithGroupId:[weakSelf.groupInfoModel getGroupId] success:^{
                
                [ProjectHelper helper_getMainThread:^{
                    NSArray *vcs = weakSelf.navigationController.viewControllers;
                    
                    [weakSelf.navigationController popToViewController:vcs[vcs.count - 1 - 2] animated:YES];
                }];
                
            } failure:^(NSError * _Nonnull error) {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
            }];
        }
        else if(weakSelf.currentPersonPower == 1 || weakSelf.currentPersonPower == 0){
            //退出群组
            [ZFGroupHelper exitGroupWithGroupId:[weakSelf.groupInfoModel getGroupId] withNickname:YiChatUserInfo_Nick success:^{
                
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"已退出群组"];
                
                [ProjectHelper helper_getMainThread:^{
                    NSArray *vcs = weakSelf.navigationController.viewControllers;
                    
                    [weakSelf.navigationController popToViewController:vcs[vcs.count - 1 - 2] animated:YES];
                }];
                
            } failure:^(NSError * _Nonnull error) {
                 [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
            }];
        }
       
    };
    
    return back;
}

- (YiChatGroupMemberListView *)groupMemberListView{
    if(!_groupMemberListView){
        WS(weakSelf);
        
        _groupMemberListView = [[YiChatGroupMemberListView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, 100) datasoRerces:@[] isHasAdd:NO isHasDelete:NO isLoadAll:NO];
        _groupMemberListView.yiChatGroupMemberListViewDidFreshUI = ^(CGSize size) {
            NSIndexPath *index = [NSIndexPath indexPathForRow:0 inSection:0];
            NSString *title = [weakSelf getCellTitleWithIndex:index];
            if([title isEqualToString:YiChatGroupInfoVC_GroupMemberList]){
                [weakSelf.cTable reloadData];
            }
        };
        _groupMemberListView.yiChatGroupMemberListViewFetchUserPower = ^NSInteger{
            return weakSelf.currentPersonPower;
        };
        _groupMemberListView.yiChatGroupMemberListViewShutUp = ^(id  _Nonnull model) {
            
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                
                YiChatUserModel *tmp = model;
                
                NSString *groupId = nil;
                if(weakSelf.groupId && [weakSelf.groupId isKindOfClass:[NSString class]]){
                    groupId = weakSelf.groupId;
                }
                else if(weakSelf.groupInfoModel && [weakSelf.groupInfoModel isKindOfClass:[NSString class]]){
                    groupId = [weakSelf.groupInfoModel getGroupId];
                }
                
                if(groupId && [groupId isKindOfClass:[NSString class]]){
                    [[YiChatUserManager defaultManagaer] addLocalGroupMemberShutUpWithGroupId:groupId userId:[tmp getOriginDic] groupInfo:weakSelf.groupInfoModel];
                    
                    [ZFGroupHelper setGroupMemberShutUpWithGroupId:groupId userId:tmp.getUserIdStr status:YES invocation:^(BOOL isSuccess, NSString * _Nonnull des) {
                        
                        
                    }];
                }
                
            }
            
          
        
        };
        _groupMemberListView.yiChatGroupMemberListViewClickItems = ^(id  _Nonnull model) {
            if(weakSelf.currentPersonPower > 0){
                if(model){
                    if([model isKindOfClass:[YiChatUserModel class]]){
                        YiChatUserModel *tmp = model;
                        if(tmp && [tmp isKindOfClass:[YiChatUserModel class]]){
                            
                            [ProjectHelper helper_getMainThread:^{
                                YiChatFriendInfoVC *info = [YiChatFriendInfoVC initialVC];
                                info.userId = [NSString stringWithFormat:@"%ld",tmp.userId];
                                [weakSelf.navigationController pushViewController:info animated:YES];
                            }];
                        }
                    }
                    else if([model isKindOfClass:[YiChatGroupMemberListModel class]]){
                        YiChatGroupMemberListModel *tmp = model;
                        YiChatGroupMemberOperationVC *operation = [YiChatGroupMemberOperationVC initialVC];
                        operation.operationType = tmp.type;
                        operation.groupInfoModel = weakSelf.groupInfoModel;
                        operation.groupMemberList = weakSelf.totalMemberlist;
                        [weakSelf presentViewController:operation animated:YES completion:nil];
                    }
                }
            }
        };
        _groupMemberListView.yiChatGroupMemberListViewClickMore = ^{
            YiChatGroupMemberListVC *groupMemberlist =  [YiChatGroupMemberListVC initialVC];
            groupMemberlist.groupInfoModel = weakSelf.groupInfoModel;
            groupMemberlist.groupMemberList = weakSelf.totalMemberlist;
            groupMemberlist.groupId = weakSelf.groupId;
            [weakSelf.navigationController pushViewController:groupMemberlist animated:YES];
        };
    }
    return _groupMemberListView;
}

- (NSString *)getTableFooterBtnTitleWithPowerLevel:(NSInteger)powerLevel{
    if(powerLevel == 0){
        return @"删除并退出";
    }
    else if(powerLevel == 1){
        return @"删除并退出";
    }
    else if(powerLevel == 2){
        return @"解散群组";
    }
    return nil;
}

- (NSString *)getCellTitleWithIndex:(NSIndexPath *)index{
    if((_toolCellDataArr.count - 1) >= index.section){
        NSArray *obj = _toolCellDataArr[index.section];
        if([obj isKindOfClass:[NSArray class]] && obj){
            id tmp = obj[index.row];
            if(tmp && [tmp isKindOfClass:[NSString class]]){
                return tmp;
            }
        }
    }
    return nil;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    NSString *title = [self getCellTitleWithIndex:index];
    
    if([title isEqualToString:YiChatGroupInfoVC_GroupMemberList]){
        return self.groupMemberListView.frame.size.height;
    }
    if([title isEqualToString:YiChatGroupInfoVC_GroupAvtor]){
        return PROJECT_SIZE_COMMON_CELLH * 3 / 2;
    }
    if([title isEqualToString:YiChatGroupInfoVC_GroupDes]){
        return PROJECT_SIZE_COMMON_CELLH * 2;
    }
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 10.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    //第一段cell 群成员列表
    //第二段cell 群信息
    
    NSString *title = [self getCellTitleWithIndex:indexPath];
    
    YiChatGroupInfoCell *infoCell = nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    CGFloat cellW = self.cTable.frame.size.width;
    if([title isEqualToString:YiChatGroupInfoVC_GroupMemberList]){
        //群成员列表
        static NSString *infoCellStr = @"YiChatGroupInfoVC_GroupMemberList";
        infoCell = [tableView dequeueReusableCellWithIdentifier:infoCellStr];
        if(!infoCell){
            infoCell = [YiChatGroupInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:infoCellStr indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] isHasDownLine:[NSNumber numberWithBool:NO] type:0];
        }
        
        [infoCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [infoCell.contentView addSubview:self.groupMemberListView];
        
        return infoCell;
    }
    
     else if([title isEqualToString:YiChatGroupInfoVC_GroupName] || [title isEqualToString:YiChatGroupInfoVC_GroupManagerList] || [title isEqualToString:YiChatGroupInfoVC_GroupID] || [title isEqualToString:YiChatGroupInfoVC_GroupDes] || [title isEqualToString:YiChatGroupInfoVC_GroupShutUpPersonList] || [title isEqualToString:YiChatGroupInfoVC_DeleteGroupMsg] || [title isEqualToString:YiChatGroupInfoVC_QueryGroupMsg]){

           static NSString *infoCellStr = @"YiChatGroupInfoVC_GroupTittleContent";
           infoCell = [tableView dequeueReusableCellWithIdentifier:infoCellStr];
           if(!infoCell){
               infoCell = [YiChatGroupInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:infoCellStr indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
           }
           
        
        BOOL isHasArrow = NO;
        BOOL isHasDownLine = YES;
        NSString *content = nil;
        
        if(_currentPersonPower > 0){
            isHasArrow = YES;
        }
        
        if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
            if([title isEqualToString:YiChatGroupInfoVC_GroupName]){
                content = _groupInfoModel.groupName;
            }
            else if([title isEqualToString:YiChatGroupInfoVC_GroupManagerList]){
                content = @"";
            }
            else if([title isEqualToString:YiChatGroupInfoVC_GroupShutUpPersonList]){
                content = @"";
            }
            else if([title isEqualToString:YiChatGroupInfoVC_GroupDes]){
                content = _groupInfoModel.groupDescription;
            }
            else if([title isEqualToString:YiChatGroupInfoVC_GroupID]){
                content = [_groupInfoModel getGroupId];
                isHasArrow = NO;
            }
            
            [infoCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:isHasArrow] downLine:[NSNumber numberWithBool:isHasDownLine] cellHeight:[NSNumber numberWithFloat:cellH]];
            
            [infoCell setValueForTitle:title content:content];
        }
        else{
            [infoCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:isHasArrow] downLine:[NSNumber numberWithBool:isHasDownLine] cellHeight:[NSNumber numberWithFloat:cellH]];
            
            [infoCell setValueForTitle:title content:@""];
        }
        
        return infoCell;
    }
    
    else if([title isEqualToString:YiChatGroupInfoVC_GroupAvtor]){
     
        static NSString *infoCellStr = @"YiChatGroupInfoVC_GroupTitleIcon";
        infoCell = [tableView dequeueReusableCellWithIdentifier:infoCellStr];
        if(!infoCell){
            infoCell = [YiChatGroupInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:infoCellStr indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] isHasDownLine:[NSNumber numberWithBool:YES] type:2];
        }
        
        BOOL isHasArrow = NO;
        
        if(_currentPersonPower > 0){
            isHasArrow = YES;
        }
        
        [infoCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:isHasArrow] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [infoCell setValueForTitle:title contentIcon:_groupInfoModel.groupAvatar];
        
        return infoCell;
    }
    
    else if([title isEqualToString:YiChatGroupInfoVC_GroupMsgClose] || [title isEqualToString:YiChatGroupInfoVC_GroupSilence]){
        
        NSString *groupId = nil;
        if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
            groupId = [self.groupInfoModel getGroupId];
        }
        else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
            groupId = self.groupId;
        }
       
        static NSString *infoCellStr = @"YiChatGroupInfoVC_GroupTtitleSwitch";
        infoCell = [tableView dequeueReusableCellWithIdentifier:infoCellStr];
        if(!infoCell){
            infoCell = [YiChatGroupInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:infoCellStr indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] isHasDownLine:[NSNumber numberWithBool:YES] type:3];
        }
        
        [infoCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        if([title isEqualToString:YiChatGroupInfoVC_GroupSilence]){
            
            if(_groupSilenceState == 0){
                [infoCell setValueForTitle:title contentSwitch:NO];
            }
            else{
                [infoCell setValueForTitle:title contentSwitch:YES];
            }
        }
        else{
            [infoCell setValueForTitle:title contentSwitch:NO];
            
            if(groupId && [groupId isKindOfClass:[NSString class]]){
                [[YiChatUserManager defaultManagaer] getMessageShutUpStateWithChatId:groupId invocation:^(NSString * _Nonnull state) {
                    if(infoCell.sIndexPath.section == indexPath.section && infoCell.sIndexPath.row == indexPath.row && state && [state isKindOfClass:[NSString class]]){
                        [ProjectHelper helper_getMainThread:^{
                            [infoCell setValueForTitle:title contentSwitch:[state boolValue]];
                        }];
                    }
                }];
            }
            else{
                [infoCell setValueForTitle:title contentSwitch:NO];
            }
        }
        
        WS(weakSelf);
        infoCell.YiChatGroupInfoCellDidClickSwitch = ^(NSString * _Nonnull title, BOOL state) {
            if(title && [title isKindOfClass:[NSString class]]){
                if([title isEqualToString:YiChatGroupInfoVC_GroupMsgClose]){
                    
                    if(groupId && [groupId isKindOfClass:[NSString class]]){
                        NSString *stateStr = [NSString stringWithFormat:@"%d",state];
                        [[YiChatUserManager defaultManagaer] storageMessageShutUpStateWithChatId:groupId state:stateStr];
                    }
                }
                else if([title isEqualToString:YiChatGroupInfoVC_GroupSilence]){
                    
                    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
                    
                    YiChatGroupInfoCell *infoCell =  [weakSelf.cTable cellForRowAtIndexPath:indexPath];
                    
                    state = !state;
                    
                    [[YiChatUserManager defaultManagaer] setGroupSilenceWithGroupId:weakSelf.groupId state:state invocation:^(BOOL isSuccess, NSString * _Nonnull des) {
                        
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                            if([progress respondsToSelector:@selector(hidden)]){
                                    [progress performSelector:@selector(hidden)];
                            }
                        });
                        
                        
                        
                        if(isSuccess){
                            [ProjectHelper helper_getMainThread:^{
                                
                                if(infoCell && isSuccess){
                                    [infoCell changeGroupSilenceState:state];
                                }
                                
                                if(weakSelf.groupInfoModel && [weakSelf.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                                    
                                    if(state){
                                        //禁言
                                        [ZFChatMessageHelper sendSilenceGroupWithGroupId:weakSelf.groupId completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                                            
                                        }];
                                    }
                                    else{
                                        //取消禁言
                                        [ZFChatMessageHelper sendCancelSilenceGroupWithGroupId:weakSelf.groupId completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                                            
                                        }];
                                    }
                                    
                                    weakSelf.groupInfoModel.groupSilentStatus = state;
                                    
                                    [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:weakSelf.groupInfoModel invocation:^(BOOL isSuccess) {
                                       
                                    }];
                                }
                            }];
                        }
                        else{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:des];
                        }
                    }];
                }
            }
        };
        
        return infoCell;
    }

    
    return [UITableViewCell new];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString *title = [self getCellTitleWithIndex:indexPath];
    if(_currentPersonPower > 0){
        if([title isEqualToString:YiChatGroupInfoVC_GroupName]){
            YiChatChangeGroupNameVC *name = [YiChatChangeGroupNameVC initialVC];
            name.groupInfo = self.groupInfoModel;
            [self.navigationController pushViewController:name animated:YES];
        }
        else if([title isEqualToString:YiChatGroupInfoVC_GroupAvtor]){
            
            [ProjectUIHelper projectActionSheetWithListArr:@[@"相机",@"相册"] click:^(NSInteger row) {
                if(row == 0){
                    
                    [ProjectUIHelper projectPhotoVideoPickerWWithType:5 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                        manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
                            if(editedIcon && [editedIcon isKindOfClass:[UIImage class]]){
                                [self uploadImage:editedIcon];
                            }
                        };
                        [self presentViewController:nav animated:YES completion:nil];
                    }];
                    
                }
                else if(row == 1){
                    
                    [ProjectUIHelper projectPhotoVideoPickerWWithType:6 pickNum:1 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                        
                        manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
                            if(images && [images isKindOfClass:[NSArray class]]){
                                if(images.count == 1){
                                    [self uploadImage:images.firstObject];
                                }
                            }
                        };
                        [self presentViewController:nav animated:YES completion:nil];
                    }];
                    
                }
            }];
        }
        else if([title isEqualToString:YiChatGroupInfoVC_GroupDes]){
            YiChatChangeGroupDesVC *name = [YiChatChangeGroupDesVC initialVC];
            name.groupInfo = self.groupInfoModel;
            [self.navigationController pushViewController:name animated:YES];
        }
    }
    if([title isEqualToString:YiChatGroupInfoVC_GroupManagerList]){
        NSString *groupId = [self getGroupId];
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            YiChatGroupManagerListVC *managerlist = [YiChatGroupManagerListVC initialVC];
            managerlist.groupId = [self getGroupId];
            managerlist.onwerId = [ZFGroupHelper groupByGroupId:groupId].owner;
            [self.navigationController pushViewController:managerlist animated:YES];
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组信息异常"];
        }
    }
    if([title isEqualToString:YiChatGroupInfoVC_GroupShutUpPersonList]){
        NSString *groupId = [self getGroupId];
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            YiChatGroupShutUpList *managerlist = [YiChatGroupShutUpList initialVC];
            managerlist.groupId = [self getGroupId];
            [self.navigationController pushViewController:managerlist animated:YES];
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组信息异常"];
        }
    }
    
    if([title isEqualToString:YiChatGroupInfoVC_DeleteGroupMsg]){
        NSString *groupId = [self getGroupId];
        HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
        [manager deleteOneChatterAllMessagesByChatterId:groupId];
        [manager deleteOneConversationWithChatterId:groupId isCleanAllHistoryMessage:YES];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"clearMsg" object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:@"clearMsg" object:nil];
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [ZFChatMessageHelper upDateMsgType:@"2" to:groupId];
            if([progress respondsToSelector:@selector(hidden)]){
                [progress performSelector:@selector(hidden)];
            }
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"清除完成"];
        });
    }
    
    if([title isEqualToString:YiChatGroupInfoVC_QueryGroupMsg]){
        YiChatSearchGroupMsgVC *vc = [YiChatSearchGroupMsgVC initialVC];
        vc.chatId = [self getGroupId];
        vc.chatType = @"2";
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (NSString *)getGroupId{
    NSString *groupId = nil;
    if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    return groupId;
}

- (void)pushVCWithName:(NSString *)name{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:@selector(initialVC)];
            vc.hidesBottomBarWhenPushed = YES;
            if(vc){
                [self.navigationController pushViewController:vc animated:YES];
            }
        }
    }
}

- (void)uploadImage:(UIImage *)image{
    if(image && [image isKindOfClass:[UIImage class]]){
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        
        [ProjectRequestHelper commonUpLoadImage:image progressBlock:^(CGFloat progress) {
            
        } sendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
            [ProjectHelper helper_getMainThread:^{
                if([progress respondsToSelector:@selector(hidden)]){
                    [progress performSelector:@selector(hidden)];
                }
            }];
            
            if(isSuccess){
                [self requestWithAvratorUrl:remotePath];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"上传头像失败"];
            }
        }];
    }
}

- (void)requestWithAvratorUrl:(NSString *)url{
    NSString *groupId = nil;
    if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    if(!groupId || !(url && [url isKindOfClass:[NSString class]])){
        return ;
    }
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
        if(group && [group isKindOfClass:[HTGroup class]]){
            
            if(!([group.groupDescription isKindOfClass:[NSString class]] && group.groupDescription)){
                group.groupDescription = @"";
            }
            group.groupAvatar = url;
            if(!([group.groupName isKindOfClass:[NSString class]] && group.groupName)){
                group.groupName = @"";
            }
            
            [ZFGroupHelper updateGroup:group withNickname:group.groupName success:^(HTGroup * _Nonnull aGroup) {
                if(group && [group isKindOfClass:[HTGroup class]]){
                    [ProjectHelper helper_getMainThread:^{
                        
                        self.groupInfoModel.groupAvatar = url;
                        [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.groupInfoModel
                                                                           invocation:^(BOOL isSuccess) {
                                                                               
                                                                           }];
                       [self tableUpdate];
                    }];
                }
                else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"修改群头像出错"];
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
}
    
- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    if(scrollView == self.cTable){
        if(_groupMemberListView){
            [_groupMemberListView removeMenu];
        }
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    if(_groupMemberListView){
        [_groupMemberListView removeMenu];
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
