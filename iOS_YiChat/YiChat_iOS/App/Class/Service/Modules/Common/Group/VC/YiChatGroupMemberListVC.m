//
//  YiChatGroupMemberListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/2.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupMemberListVC.h"
#import "YiChatFriendInfoVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupMemberListView.h"
#import "YiChatGroupMemberOperationVC.h"
#import "ZFGroupHelper.h"

@interface YiChatGroupMemberListVC ()

@property (nonatomic,strong) YiChatGroupMemberListView *groupMemberListView;

@property (nonatomic,assign) BOOL isFirst;

@property (nonatomic,assign) NSInteger page;

//0 普通成员 1 管理员 2群主
@property (nonatomic,assign) NSInteger currentPersonPower;

@end

@implementation YiChatGroupMemberListVC

+ (id)initialVC{
    YiChatGroupMemberListVC *manager = [YiChatGroupMemberListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"groupMemberList") leftItem:nil rightItem:nil];
    return manager;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.view addSubview:self.groupMemberListView];
    
    _page = 1;
    
    if(_groupInfoModel && [_groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
         [self updateGroupInfoConfigure];
        
        if(!(_groupMemberList && [_groupMemberList isKindOfClass:[NSArray class]])){
            [self loadGroupMember];
        }
        else{
            [ProjectHelper helper_getMainThread:^{
                if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                    if(self.groupMemberList.count > 0){
                        [self.groupMemberListView changeDataSource:self.groupMemberList];
                    }
                }
                [self.groupMemberListView updateAddDeleteUIData];
            }];
        }
    }
    else{
        [self fetchGroupInfoInvocation:^{
            [self updateGroupInfoConfigure];
            
            if(!(_groupMemberList && [_groupMemberList isKindOfClass:[NSArray class]])){
                [self loadGroupMember];
            }
            else{
                [ProjectHelper helper_getMainThread:^{
                    if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                        if(self.groupMemberList.count > 0){
                            [self.groupMemberListView changeDataSource:self.groupMemberList];
                        }
                    }
                    [self.groupMemberListView updateAddDeleteUIData];
                }];
            }
        }];
    }
    
    _isFirst = NO;
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if(_isFirst == YES){
       
    }
    
    [self fetchGroupInfoInvocation:^{
        
        [self updateGroupInfoConfigure];
        
        [self loadGroupMember];
    }];
    
    _isFirst = YES;
}

- (void)updateGroupInfoConfigure{
    YiChatGroupInfoModel *model = _groupInfoModel;
    
    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
        _groupInfoModel = model;
        
        _currentPersonPower = model.roleType;
        
        if(_currentPersonPower == 2 || _currentPersonPower == 1){
            [self.groupMemberListView changeIsHasAdd:YES isHasDelete:YES];
        }
        else{
            [self.groupMemberListView changeIsHasAdd:NO isHasDelete:NO];
        }
    }
}

- (void)loadGroupMember{
    [self loadGroupMemberDataInvocation:^{
        
        [ProjectHelper helper_getMainThread:^{
            
            if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                if(self.groupMemberList.count > 0){
                    [self.groupMemberListView changeDataSource:self.groupMemberList];
                }
            }
            [self.groupMemberListView updateAddDeleteUIData];
        }];
        
    }];
}

- (void)refreshGroupMember{
    _page = 1;
    
    [self loadGroupMember];
}

- (void)fetchGroupInfoInvocation:(void(^)(void))invocation{
    NSString *groupId = nil;
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    else if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[NSString class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                _groupInfoModel = model;
            
            }
            invocation();
        }];
    }
    else{
        invocation();
    }
}

- (void)loadGroupMemberDataInvocation:(void(^)(void))invocation{
    WS(weakSelf);
    NSString *groupId = nil;
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    else if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    if(!groupId){
        invocation();
        return ;
    }
    
    [[YiChatUserManager defaultManagaer] updateGroupMemberslistWithGroupId:groupId invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            if(groupMemberlist.count >0 ){
                self.groupMemberList = groupMemberlist;
            }
        }
        invocation();
    }];
}

- (YiChatGroupMemberListView *)groupMemberListView{
    if(!_groupMemberListView){
        WS(weakSelf);
        
        _groupMemberListView = [[YiChatGroupMemberListView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.view.frame.size.width, self.view.frame.size.height - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)) datasoRerces:@[] isHasAdd:NO isHasDelete:NO isLoadAll:YES];
        _groupMemberListView.yiChatGroupMemberListViewDidFreshUI = ^(CGSize size) {
            
            
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
              
                if(groupId && [groupId isKindOfClass:[NSString class]]){
                    
                    [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                            [[YiChatUserManager defaultManagaer] addLocalGroupMemberShutUpWithGroupId:groupId userId:[tmp getOriginDic] groupInfo:model];
                        }
                    }];
                    
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
                        if(tmp.type != 0){
                            operation.groupMemberList = weakSelf.groupMemberList;
                        }
                        [weakSelf presentViewController:operation animated:YES completion:nil];
                    }
                }
            }
        };
        _groupMemberListView.collectionView.bounces = YES;
        _groupMemberListView.userInteractionEnabled = YES;
        _groupMemberListView.collectionView.scrollEnabled = YES;
        _groupMemberListView.collectionView.alwaysBounceVertical=YES;
        _groupMemberListView.collectionView.pagingEnabled = NO;
        _groupMemberListView.layout.scrollDirection = UICollectionViewScrollDirectionVertical;
    }
    return _groupMemberListView;
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
