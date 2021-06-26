//
//  YiChatGroupMemberOperationVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/26.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupMemberOperationVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchBarView.h"
#import "YiChatGroupSelectePersonView.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "YiChatConnectionModel.h"
#import "YiChatPhoneConnectionIndexView.h"
#import "YiChatGroupSelectePersonCell.h"
#import "ZFChatHelper.h"
#import "ProjectRequestHelper.h"
#import "YiChatGroupInfoModel.h"
#import "ZFChatMessageHelper.h"
#import "ZFGroupHelper.h"


typedef NS_ENUM(NSUInteger,YiChatGroupMemberOperationStyle){
    YiChatGroupMemberOperationStyleDeleteGroupMember = 0,
    YiChatGroupMemberOperationStyleAddGroupMember,
    YiChatGroupMemberOperationStyleSetGroupManager,
    YiChatGroupMemberOperationStyleUnknown
};

@interface YiChatGroupMemberOperationVC ()<UIGestureRecognizerDelegate,UIScrollViewDelegate>

@property (nonatomic,assign) NSInteger currentPage;
@property (nonatomic,assign) BOOL isFetchConversation;

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) YiChatGroupSelectePersonView *selectePerson;

@property (nonatomic,strong) YiChatConnectionModel *model;

@property (nonatomic,strong) YiChatPhoneConnectionIndexView *indexView;

@property (nonatomic,strong) NSMutableArray <YiChatUserModel *>*selelectPersonContain;

@property (nonatomic,strong) dispatch_semaphore_t selectePersonLock;

@property (nonatomic,strong) dispatch_semaphore_t loadGroupMemberLock;

@property (nonatomic,assign) YiChatGroupMemberOperationStyle operationStyle;


@end

@implementation YiChatGroupMemberOperationVC

+ (id)initialVC{
    YiChatGroupMemberOperationVC *createGroup = [YiChatGroupMemberOperationVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson") leftItem:nil rightItem:@"确定"];
    return createGroup;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _selelectPersonContain = [NSMutableArray arrayWithCapacity:0];
    self.loadGroupMemberLock = dispatch_semaphore_create(1);
    self.selectePersonLock = dispatch_semaphore_create(1);
    self.view.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    [self operationStyle];
    
    [self.view addSubview:self.searchBar];
    
    [self.view addSubview:self.selectePerson];
    
    [self makeTable];

    // Do any additional setup after loading the view.
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [self keybordResign];

    NSArray *members = [self getSelecteMembers];
    
    NSArray *selectMembersModel = [self getSelecteMembersModel];
    
    if([members isKindOfClass:[NSArray class]] && members.count > 0){
        WS(weakSelf);
        
        NSString *groupId = nil;
        if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
            groupId = [self.groupInfoModel getGroupId];
        }
        else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
            groupId = self.groupId;
        }
        
        if(_operationStyle == YiChatGroupMemberOperationStyleDeleteGroupMember){
            
            [ProjectHelper helper_getGlobalThread:^{
                
                NSString *groupOwnerId = [ZFGroupHelper groupByGroupId:groupId].owner;
                NSMutableArray *userIds = [NSMutableArray arrayWithCapacity:0];
                NSMutableArray *nicks = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < members.count; i ++) {
                    id user = members[i];
                    
                    if(user && [user isKindOfClass:[NSDictionary class]] && groupId){
                        NSString *userid = user[@"uid"];
                        NSString *nick = user[@"nickName"];
                        
                        
                        if(!(userid && [userid isKindOfClass:[NSString class]])){
                            userid = @"";
                        }
                        
                        if(!(nick && [nick isKindOfClass:[NSString class]])){
                            nick = @"";
                        }
                        
                        [userIds addObject:userid];
                        [nicks addObject:nick];
                    }
                }
                
                [ZFGroupHelper deleteMembersWithGroupOwnerId:groupOwnerId userIds:userIds andGroupId:groupId andNickname:nicks success:^{
                    
                } failure:^(NSError * _Nonnull error) {
                    
                }];
                
                
                NSMutableArray *tmpMember = [NSMutableArray arrayWithCapacity:0];
                
                if([self.groupMemberList isKindOfClass:[NSArray class]] && self.groupMemberList){
                    
                    for (int i = 0; i < self.groupMemberList.count; i ++) {
                        YiChatUserModel *model = self.groupMemberList[i];
                        
                        BOOL isHas = NO;
                        if(model && [model isKindOfClass:[YiChatUserModel class]]){
                            
                            for (int j = 0; j < selectMembersModel.count; j ++) {
                                
                                YiChatUserModel *tmp = selectMembersModel[j];
                                
                                if(tmp && [tmp isKindOfClass:[YiChatUserModel class]]){
                                    if(tmp.userId == model.userId){
                                        isHas = YES;
                                        break;
                                    }
                                }
                            }
                            
                            if(isHas == NO){
                                [tmpMember addObject:model];
                            }
                        }
                    }
                }
                
                self.groupMemberList = tmpMember;
                
                [[YiChatUserManager defaultManagaer] updateGroupInfoGroupMemberList:tmpMember groupId:groupId];
                
                [self delteModelUsersWithUsersArr:selectMembersModel];
                [self addUserModelState];
                
                [self.selelectPersonContain removeAllObjects];
                [self uiClean];
                
                [ProjectHelper helper_getMainThread:^{
                    [self tableUpdate];
                    
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                          [self dismissViewControllerAnimated:YES completion:nil];
                    });
                   
                }];
                
            }];
           
        }
        else if(_operationStyle == YiChatGroupMemberOperationStyleAddGroupMember){
            
            
            
            if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                
                if((self.groupInfoModel.memberCount + members.count) > YiChatProject_CreateGroupNum){
                    
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"群组人数不能大于%d人",YiChatProject_CreateGroupNum]];
                    return;
                }
            }
            
            [ZFGroupHelper addMemberWithUserIds:members andGroupId:groupId byUser:YiChatUserInfo_Nick success:^{
                
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                
                if(self.groupMemberList.count > 0){
                    [tmp addObjectsFromArray:self.groupMemberList];
                }
                if(selectMembersModel && [selectMembersModel isKindOfClass:[NSArray class]]){
                    [tmp addObjectsFromArray:selectMembersModel];
                }
                
                self.groupMemberList = tmp;
                
                [self.selelectPersonContain removeAllObjects];
                [self uiClean];
                [self loadData];
                
                [[YiChatUserManager defaultManagaer] updateGroupInfoGroupMemberList:tmp groupId:groupId];
                
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self dismissViewControllerAnimated:YES completion:nil];
                });
                
            } failure:^(NSError * _Nonnull error) {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"添加群成员失败"];
                [self.selelectPersonContain removeAllObjects];
                [self uiClean];
                [self loadData];
            }];
        }
        
        else if(_operationStyle == YiChatGroupMemberOperationStyleSetGroupManager){
            //设置管理员
            NSMutableString *str = [NSMutableString stringWithCapacity:0];
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0;i < members.count;i ++) {
                id user = members[i];
                
                if(user && [user isKindOfClass:[NSDictionary class]] && groupId){
                    NSString *userid = user[@"uid"];
                    NSString *nick = user[@"nickName"];
                    
                    if(!(userid && [userid isKindOfClass:[NSString class]])){
                        userid = @"";
                    }
                    
                    if(!(nick && [nick isKindOfClass:[NSString class]])){
                        nick = @"";
                    }
                    
                    [arr addObject:userid];
                    
                    if(i == 0){
                        [str appendString:userid];
                    }
                    else{
                        [str appendString:[@"," stringByAppendingString:userid]];
                    }
                }
            }
            
            NSDictionary *param = [ProjectRequestParameterModel setGroupManagerParamWithGroupId:groupId userIds:str status:1];
            
            [self setGroupManagerWithParam:param invocation:^(BOOL isSuccess) {
                if(isSuccess){
                    
                    for (int i = 0; i < arr.count; i ++) {
                        NSString *userIdStr = arr[i];
                        
                        if(userIdStr && [userIdStr isKindOfClass:[NSString class]]){
                            [ZFChatMessageHelper sendSetManagerCmdWithGroupId:groupId userId:userIdStr  completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                                
                            }];
                        }
                       
                    }
                    
                    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                    
                    if(weakSelf.managerList.count > 0){
                        [tmp addObjectsFromArray:weakSelf.managerList];
                    }
                    if(selectMembersModel && [selectMembersModel isKindOfClass:[NSArray class]]){
                        [tmp addObjectsFromArray:selectMembersModel];
                    }
                    
                    weakSelf.managerList = tmp;
                    
                    [ProjectHelper helper_getMainThread:^{
                        [weakSelf.selelectPersonContain removeAllObjects];
                        [weakSelf uiClean];
                        [weakSelf loadData];
                        
                        [weakSelf dismissViewControllerAnimated:YES completion:nil];
                    }];
                }
                else{
                    [ProjectHelper helper_getMainThread:^{
                        [weakSelf.selelectPersonContain removeAllObjects];
                        [weakSelf uiClean];
                    }];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"添加群管理员失败"];
                }
            }];
            
        }
    }
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

- (void)uiClean{
    [ProjectHelper helper_getMainThread:^{
        [self changeSelecteNum];
        
        WS(weakSelf);
        [self.selectePerson changeSelectePersons:@[] invocation:^(CGRect frame) {
            [ProjectHelper helper_getMainThread:^{
                CGFloat x = frame.origin.x + frame.size.width;
                
                [UIView animateWithDuration:0.4 animations:^{
                     weakSelf.searchBar.frame = CGRectMake(x, weakSelf.searchBar.frame.origin.y, weakSelf.view.frame.size.width - x, weakSelf.searchBar.frame.size.height);
                    
                    [weakSelf.searchBar refreshUI];
                }];
            }];
        }];
    }];
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        
        WS(weakSelf);
        
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder = PROJECT_TEXT_LOCALIZE_NAME(@"connectionMainSearchPlaceHolder");
        [_searchBar initialSearchType:ProjectSearchBarViewPageStyleSearchPageSearch];
        if(self.operationStyle == YiChatGroupMemberOperationStyleAddGroupMember){
             [_searchBar initialSearchStyle:4];
        }
        else if(self.operationStyle == YiChatGroupMemberOperationStyleDeleteGroupMember){
             [_searchBar initialSearchStyle:5];
        }
        else if(self.operationStyle == YiChatGroupMemberOperationStyleSetGroupManager){
             [_searchBar initialSearchStyle:6];
        }
      
        [_searchBar getSearchOriginData:^id _Nonnull{
            
            YiChatConnectionModel *model = [[YiChatConnectionModel alloc] init];
            
            NSMutableArray *connectionArr = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < weakSelf.model.connectionModelArr.count; i ++) {
                NSDictionary *dic = weakSelf.model.connectionModelArr[i];
                
                NSMutableDictionary *connectionUserDic = [NSMutableDictionary dictionaryWithCapacity:0];
                
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    NSString *key = dic.allKeys.lastObject;
                    
                    if(key && [key isKindOfClass:[NSString class]]){
                        NSArray *users = dic[key];
                        if(users && [users isKindOfClass:[NSArray class]]){
                            
                            NSMutableArray *connectionUserArr = [NSMutableArray arrayWithCapacity:0];
                            
                            for (int j = 0; j < users.count; j ++) {
                                YiChatUserModel *userModel = users[j];
                                if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
                                    
                                    YiChatUserModel *tmp = userModel.mutableCopy;
                                    
                                    if(tmp){
                                        NSNumber *state = objc_getAssociatedObject(userModel, @"state");
                                        NSNumber *canSelecteState = objc_getAssociatedObject(userModel, @"selecteState");
                                        
                                        if(state && [state isKindOfClass:[NSNumber class]]){
                                             objc_setAssociatedObject(tmp, @"state", state, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                        }
                                        if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
                                            objc_setAssociatedObject(tmp, @"selecteState", canSelecteState, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                        }
                                        
                                        [connectionUserArr addObject:tmp];
                                        
    
                                    }
                                    
                                }
                            }
                            [connectionUserDic addEntriesFromDictionary:@{key:connectionUserArr}];
                            
                            [connectionArr addObject:connectionUserDic];
                        }
                    }
                }
            }
            
            model.connectionModelArr = connectionArr;
            model.originDataArr = [weakSelf.model.originDataArr mutableCopy];
            return model;
            
        }];
        _searchBar.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
            if(obj && [obj isKindOfClass:[YiChatConnectionModel class]]){
                weakSelf.model = obj;
            }
            [weakSelf changeSeletcePersonToContain];
        };
        [_searchBar createUI];
    }
    return _searchBar;
}
    
- (void)changeSeletcePersonToContain{
    [self.selelectPersonContain removeAllObjects];
    
    for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        if(dic && [dic isKindOfClass:[NSDictionary class]]){
            NSString *key = dic.allKeys.lastObject;
            if(key && [key isKindOfClass:[NSString class]]){
                
                NSArray *users = dic[key];
                if(users && [users isKindOfClass:[NSArray class]]){
                    for (int j = 0; j < users.count; j ++) {
                        YiChatUserModel *model = users[j];
                        if(model && [model isKindOfClass:[YiChatUserModel class]]){
                            
                            id obj = objc_getAssociatedObject(model, @"state");
                            
                            if(obj && [obj isKindOfClass:[NSNumber class]]){
                                NSNumber *selecteState = obj;
                                
                                if(selecteState.boolValue){
                                    
                                    NSNumber *canSelecteState = objc_getAssociatedObject(model, @"selecteState");
                                    if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
                                        
                                        if(!canSelecteState.boolValue){
                                             [self.selelectPersonContain addObject:model];
                                        }
                                    }
                                   
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    [ProjectHelper helper_getMainThread:^{
        [self changeSelecteNum];
        
        [self.selectePerson changeSelectePersons:[self.selelectPersonContain copy] invocation:^(CGRect frame) {
            
            CGFloat x = frame.origin.x + frame.size.width;
            
            [UIView animateWithDuration:0.4 animations:^{
                self.searchBar.frame = CGRectMake(x, self.searchBar.frame.origin.y, self.view.frame.size.width - x, self.searchBar.frame.size.height);
            }];
        }];
        
        [self tableUpdate];
    }];
}
    
    

- (YiChatGroupSelectePersonView *)selectePerson{
    if(!_selectePerson){
        _selectePerson = [[YiChatGroupSelectePersonView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH,0, ProjectUIHelper_SearchBarH)];
        _selectePerson.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    return _selectePerson;
}

- (YiChatGroupMemberOperationStyle)operationStyle{
    
    YiChatGroupMemberOperationStyle style;
    
    if(_operationType == 0){
        style = YiChatGroupMemberOperationStyleDeleteGroupMember;
    }
    else if(_operationType == 1){
        style = YiChatGroupMemberOperationStyleAddGroupMember;
    }
    else if(_operationType == 2){
        style = YiChatGroupMemberOperationStyleSetGroupManager;
    }
    else{
        style = YiChatGroupMemberOperationStyleUnknown;
    }
    _operationStyle = style;
    
    return _operationStyle;
}

- (void)makeTable{
    self.sectionsRowsNumSet = @[];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,self.searchBar.frame.origin.y + self.searchBar.frame.size.height, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (self.searchBar.frame.origin.y + self.searchBar.frame.size.height)  - PROJECT_SIZE_SafeAreaInset.bottom);
}

- (void)tableUpdate{
    [self tableUpdateRowsNums];
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
        [self updateIndexView];
    }];
}

- (void)tableUpdateRowsNums{
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.model.connectionModelArr.count; i++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]]){
            NSString *key =  dic.allKeys.lastObject;
            NSArray * row= dic[key];
            if([row isKindOfClass:[NSArray class]]){
                [tmp addObject:[NSNumber numberWithInteger:row.count]];
            }
        }
    }
    
    self.sectionsRowsNumSet = tmp;
}

- (YiChatPhoneConnectionIndexView *)indexView{
    if(!_indexView){
        
        NSArray *characters = [self getCharacterArrs];
        
        WS(weakSelf);
        
        _indexView = [[YiChatPhoneConnectionIndexView alloc] initWithData:characters bgView:self.view];
        _indexView.yichatIndexViewClick = ^(NSInteger clickIndex) {
            if(weakSelf.sectionsRowsNumSet.count - 1 >= (clickIndex + 1)){
                [weakSelf.cTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:clickIndex + 1] atScrollPosition:UITableViewScrollPositionTop animated:YES];
            }
        };
    }
    return _indexView;
}

- (void)updateIndexView{
    NSArray *characters = [self getCharacterArrs];
    [self.indexView updateUIWithData:characters];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self loadData];
}

- (void)loadData{
    
    if(_operationStyle == YiChatGroupMemberOperationStyleAddGroupMember){
        [ProjectHelper helper_getGlobalThread:^{
            if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                if(self.groupMemberList.count == 0){
                    [self loadGroupMemberData];
                }
                else if(self.model && [self.model isKindOfClass:[YiChatConnectionModel class]]){
                    [ProjectHelper helper_getMainThread:^{
                        [self addUserModelState];
                        
                        [self tableUpdate];
                    }];
                }
                else{
                     [self loadConnectionData];
                }
            }
            else{
                 [self loadGroupMemberData];
            }
        }];
    }
    else if(_operationStyle == YiChatGroupMemberOperationStyleDeleteGroupMember){
        [ProjectHelper helper_getGlobalThread:^{
            if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                if(self.groupMemberList.count == 0){
                    [self loadGroupMemberData];
                }
                else{
                    [ProjectHelper helper_getGlobalThread:^{
                        [self dealGroupMemberTranslate];
                        [self addUserModelState];
                        [self tableUpdate];
                    }];
                }
            }
            else{
                [self loadGroupMemberData];
            }
        }];
    }
    
    else if(_operationStyle == YiChatGroupMemberOperationStyleSetGroupManager){
        [ProjectHelper helper_getGlobalThread:^{
            if(self.managerList && [self.managerList isKindOfClass:[NSArray class]]){
                if(self.managerList.count > 0){
                    if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
                        if(self.groupMemberList.count > 0){
                            [self addUserModelState];
                            [self tableUpdate];
                        }
                        else{
                            [self loadGroupMemberData];
                        }
                    }
                    else{
                        [self loadGroupMemberData];
                    }
                }
                else{
                     [self loadManagerData];
                }
            }
            else{
                 [self loadManagerData];
            }
        }];
    }
}

- (void)loadGroupMemberData{
    
    WS(weakSelf);
    NSString *groupId = nil;
    if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        groupId = [self.groupInfoModel getGroupId];
    }
    else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    else{
        return;
    }
    
    [[YiChatUserManager defaultManagaer] requestGroupMemberslistWithGroupId:groupId pageNo:1 pageSize:10000 invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            
            self.groupMemberList = [groupMemberlist copy];
            if(self.operationType == 1){
                if(self.model){
                    [ProjectHelper helper_getMainThread:^{
                        [self tableUpdate];
                    }];
                }
                else{
                    [self loadConnectionData];
                }
            }
            else if(self.operationType == 0){
                [ProjectHelper helper_getGlobalThread:^{
                    [self dealGroupMemberTranslate];
                    [self addUserModelState];
                    [self tableUpdate];
                }];
            }
            else if(self.operationType == 2){
                [self dealGroupMemberTranslate];
                [self addUserModelState];
                [self tableUpdate];
            }
        }
        else if(error && [error isKindOfClass:[NSString class]]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
        }
        
        
    }];
}

- (void)loadManagerData{
    WS(weakSelf);
    NSString *groupId = nil;
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    if(!groupId){
        return ;
    }
    [[YiChatUserManager defaultManagaer] fetchGroupManagerListWithGroupId:groupId invocation:^(NSArray * _Nonnull managerList) {
        if(managerList && [managerList isKindOfClass:[NSArray class]]){
            weakSelf.managerList = managerList;
            if(weakSelf.operationType == 2){
                [weakSelf loadGroupMemberData];
            }
        }
       
    }];
}

- (void)dealGroupMemberTranslate{
    if(self.groupMemberList && [self.groupMemberList isKindOfClass:[NSArray class]]){
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
        for (int i = 0; i < self.groupMemberList.count; i ++) {
            YiChatUserModel *model = self.groupMemberList[i];
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                NSDictionary *dic = [ProjectBaseModel translateObjPropertyToDic:model];
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    [tmp addObject:dic];
                }
            }
        }
        if(tmp){
            self.model = [[YiChatConnectionModel alloc] initWithUsersArr:tmp];
        }
    }
}

- (void)loadConnectionData{
    _currentPage = - 1;
    [self loadConnectionDataWithPage:_currentPage];
}

- (void)loadConnectionDataWithPage:(NSInteger)page{
    if(_isFetchConversation){
        return;
    }
    _isFetchConversation = YES;
    
    WS(weakSelf);
    [[YiChatUserManager defaultManagaer] fetchUserConnectionInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        weakSelf.isFetchConversation = NO;
        
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            weakSelf.model = model;
            
            if(weakSelf.operationType == 1){
                [weakSelf addUserModelState];
                
                [weakSelf tableUpdate];
            }
            return ;
        }
        else if(error && [error isKindOfClass:[NSString class]]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            return;
        }
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"数据出错"];
    }];
}

- (void)delteModelUsersWithUsersArr:(NSArray *)usersArr{
    NSMutableArray *modelsArr = [NSMutableArray arrayWithCapacity:0];

    for (int i = 0; i < _model.connectionModelArr.count; i ++) {
        NSDictionary *dic = _model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSString *key = dic.allKeys.lastObject;
            NSArray *userModelArr = dic[key];
            NSMutableArray *tmpArr = [NSMutableArray arrayWithCapacity:0];
            
            if(userModelArr && [userModelArr isKindOfClass:[NSArray class]]){
                
                
                for (int j = 0; j < userModelArr.count; j ++) {
                    
                    __block BOOL isHas = NO;
                    YiChatUserModel *model = userModelArr[j];
                    
                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                        
                        [usersArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                            YiChatUserModel *temp = obj;
                            if(temp && [temp isKindOfClass:[YiChatUserModel class]]){
                                if(model.userId == temp.userId){
                                    isHas = YES;
                                }
                            }
                        }];
                        
                        if(isHas == NO){
                            [tmpArr addObject:userModelArr[j]];
                        }
                    }
                }
                
                if(key && [key isKindOfClass:[NSString class]] && tmpArr){
                    [modelsArr addObject:@{key:tmpArr}];
                }
            }
        }
    }
    self.model.connectionModelArr = modelsArr;
}

- (NSArray *)getSelecteMembers{
    NSMutableArray * members = @[].mutableCopy;
    for (YiChatUserModel * entity in self.selelectPersonContain) {
        if(entity && [entity isKindOfClass:[YiChatUserModel class]]){
            
            NSString *str = [NSString stringWithFormat:@"%ld",entity.userId];
            NSString *name = [entity nickName];
            NSMutableDictionary *tmp = [NSMutableDictionary dictionaryWithCapacity:0];
            
            if(str && [str isKindOfClass:[NSString class]]){
                [tmp addEntriesFromDictionary:@{@"uid":str}];
            }
            if(name && [name isKindOfClass:[NSString class]]){
                [tmp addEntriesFromDictionary:@{@"nickName":name}];
            }
            
            if(tmp && [tmp isKindOfClass:[NSDictionary class]]){
                [members addObject:tmp];
            }
        }
    }
    return members;
}

- (NSArray *)getSelecteMembersModel{
    NSMutableArray * members = @[].mutableCopy;
    for (YiChatUserModel * entity in self.selelectPersonContain) {
        if(entity && [entity isKindOfClass:[YiChatUserModel class]]){
            [members addObject:entity];
        }
    }
    return members;
}

- (NSArray *)getCharacterArrs{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _model.connectionModelArr.count; i ++) {
        NSDictionary *dic = _model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]]){
            id key = dic.allKeys.lastObject;
            if(key){
                [arr addObject:key];
            }
        }
    }
    return arr;
}

- (YiChatUserModel *)getUserModelWithIndex:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section;
    
    if(section <= (_model.connectionModelArr.count - 1)){
        NSDictionary *dic = _model.connectionModelArr[section];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSArray *arr = _model.connectionModelArr[section][dic.allKeys.lastObject];
            if([arr isKindOfClass:[NSArray class]] && (arr.count - 1) >= indexPath.row){
                return arr[indexPath.row];
            }
        }
        
    }
    return nil;
}

- (NSArray <NSIndexPath *>*)getIndexPathWithUserModelsArr:(NSArray *)userModels{
    NSMutableArray *indexArr = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < _model.connectionModelArr.count; i ++) {
        NSDictionary *dic = _model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSString *key = dic.allKeys.lastObject;
            NSArray *userModelArr = dic[key];
            if(userModelArr && [userModelArr isKindOfClass:[NSArray class]]){
                for (int j = 0; j < userModelArr.count; j ++) {
                    YiChatUserModel *model = userModelArr[j];
                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                        
                        [userModelArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                            YiChatUserModel *temp = obj;
                            if(temp && [temp isKindOfClass:[YiChatUserModel class]]){
                                if(model.userId == temp.userId){
                                    [indexArr addObject:[NSIndexPath indexPathForRow:j inSection:i]];
                                }
                            }
                        }];
                        
                    }
                }
            }
        }
    }
    return indexArr;
}

- (NSString *)getKeyWithIndex:(NSInteger)sectionCurrent{
    NSInteger section = sectionCurrent;
    
    if(section <= (_model.connectionModelArr.count - 1)){
        NSDictionary *dic = _model.connectionModelArr[section];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            return dic.allKeys.lastObject;
        }
        
    }
    return nil;
}

- (void)addUserModelState{
    if(_operationStyle == YiChatGroupMemberOperationStyleAddGroupMember){
        
        for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
            NSDictionary *dic = self.model.connectionModelArr[i];
            if([dic isKindOfClass:[NSDictionary class]] && dic){
                NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
                
                [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    
                    if([obj isKindOfClass:[YiChatUserModel class]]){
                        YiChatUserModel *user = obj;
                        
                        __block BOOL ishas = NO;
                        
                        dispatch_apply(self.groupMemberList.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t j ) {
                            
                            YiChatUserModel *tmp = self.groupMemberList[j];
                            if(tmp && [tmp isKindOfClass:[YiChatUserModel class]]){
                                if(tmp.userId == user.userId){
                                    ishas = YES;
                                }
                            }
                        });
                        
                        if(ishas == YES){
                            objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            
                            objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        }
                        else{
                            
                            objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        }
                        
                    }
                }];
            }
        }
    }
    else if(_operationStyle == YiChatGroupMemberOperationStyleDeleteGroupMember){
        
        NSString *groupId = @"";
        if(self.groupInfoModel && [self.groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
            groupId = [self.groupInfoModel getGroupId];
        }
        else if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
            groupId = self.groupId;
        }
        
        HTGroup *group = [ZFGroupHelper groupByGroupId:groupId];
        
        NSString *groupOwner = @"";
        
        if(group.owner && [group.owner isKindOfClass:[NSString class]]){
            groupOwner = group.owner;
        }
        
        [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            
            NSArray *managerList = @[];
            
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                managerList = model.adminList;
            }
            
            for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
                NSDictionary *dic = self.model.connectionModelArr[i];
                if([dic isKindOfClass:[NSDictionary class]] && dic){
                    NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
                    [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                        if([obj isKindOfClass:[YiChatUserModel class]]){
                            YiChatUserModel *user = obj;
                            if(user.userId == YiChatUserInfo_UserId){
                                objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            }
                            else if([[user getUserIdStr] isEqualToString:groupOwner]){
                                objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            }
                            else{
                                
                                BOOL isHas = NO;
                                
                                for (int i = 0; i < managerList.count; i ++) {
                                    NSDictionary *dic = managerList[i];
                                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                        NSString *managerUserId = [NSString stringWithFormat:@"%ld",[dic[@"userId"] integerValue]];
                                        if([managerUserId isEqualToString:[user getUserIdStr]]){
                                            isHas = YES;
                                            break;
                                        }
                                    }
                                }
                                if(isHas){
                                    objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                    objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                }
                                else{
                                    objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                    objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                                }
                                
                             
                            }
                        }
                    }];
                }
            }
            
            
        }];
        
        
        
    }
    else if(_operationStyle == YiChatGroupMemberOperationStyleSetGroupManager){
        for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
            NSDictionary *dic = self.model.connectionModelArr[i];
            if([dic isKindOfClass:[NSDictionary class]] && dic){
                NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
                
                [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    
                    if([obj isKindOfClass:[YiChatUserModel class]]){
                        YiChatUserModel *user = obj;
                        
                        __block BOOL ishas = NO;
                        if(self.managerList.count != 0){
                            dispatch_apply(self.managerList.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t j ) {
                                
                                YiChatUserModel *tmp = self.managerList[j];
                                if(tmp && [tmp isKindOfClass:[YiChatUserModel class]]){
                                    if(tmp.userId == user.userId){
                                        ishas = YES;
                                    }
                                }
                            });
                        }
                        
                        if(ishas == YES){
                            objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            
                            objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        }
                        else{
                            
                            objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                            objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        }
                        
                    }
                }];
            }
        }
    }
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 30.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [back addSubview:lab];
    NSString *key = [self getKeyWithIndex:section];
    if([key isKindOfClass:[NSString class]] && key){
        lab.text = key;
    }
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatGroupSelectePersonCell *cell = nil;
    
    static NSString *str = @"YiChatGroupListCell_group";
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    cell = [tableView dequeueReusableCellWithIdentifier:str];
    
    if(!cell){
        cell = [YiChatGroupSelectePersonCell  initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.cTable.frame.size.width] isHasDownLine:[NSNumber numberWithFloat:YES] type:0];
    }
    
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
    
    cell.cellModel = [self getUserModelWithIndex:indexPath];
    
    WS(weakSelf);
    cell.yiChatGroupSelecte = ^(YiChatUserModel * _Nonnull model, BOOL state) {
        [weakSelf keybordResign];
        [ProjectHelper helper_getGlobalThread:^{
            [weakSelf changeSeletcePersonToContain:model state:state];
        }];
    };
    
    return cell;
}

- (void)changeSelecteNum{
    UILabel *lab = nil;
    if(self.selelectPersonContain.count > 0){
        lab = [self navBarGetCenterBarItem];
        if([lab isKindOfClass:[UILabel class]] && lab){
            lab.text = [NSString stringWithFormat:@"%@(%ld)",PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson"),self.selelectPersonContain.count];
        }
    }
    else{
        lab = [self navBarGetCenterBarItem];
        if([lab isKindOfClass:[UILabel class]] && lab){
            lab.text = PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson");
        }
    }
}

- (void)changeSeletcePersonToContain:(YiChatUserModel *)model state:(BOOL)state{
    dispatch_semaphore_wait(self.selectePersonLock, DISPATCH_TIME_FOREVER);
    
    __block BOOL isHas = NO;
    WS(weakSelf);
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    
    dispatch_apply(weakSelf.selelectPersonContain.count,queue , ^(size_t i) {
        
        if(model.userId == weakSelf.selelectPersonContain[i].userId){
            isHas = YES;
        }
    });
    
    if(isHas == NO){
        if(state){
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                [weakSelf.selelectPersonContain addObject:model];
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf changeSelecteNum];
                    
                    [weakSelf.selectePerson changeSelectePersons:[weakSelf.selelectPersonContain copy] invocation:^(CGRect frame) {
                        
                        CGFloat x = frame.origin.x + frame.size.width;
                        
                        CGRect destinaFrame = CGRectMake(x, weakSelf.searchBar.frame.origin.y, weakSelf.view.frame.size.width - x, weakSelf.searchBar.frame.size.height);
                        
                        if(weakSelf.searchBar.frame.size.width == self.view.frame.size.width){
                            weakSelf.searchBar.frame = CGRectMake(weakSelf.searchBar.frame.origin.x, weakSelf.searchBar.frame.origin.y, destinaFrame.size.width, weakSelf.searchBar.frame.size.height);
                        }
                        
                        [UIView animateWithDuration:0.4 animations:^{
                            weakSelf.searchBar.frame = destinaFrame;
                        } completion:^(BOOL finished) {
                            [weakSelf.searchBar refreshUI];
                        }];
                    }];
                    
                    dispatch_semaphore_signal(weakSelf.selectePersonLock);
                }];
                
                return;
            }
        }
    }
    if(isHas == YES){
        if(state == NO){
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                
                for (int i = 0; i < weakSelf.selelectPersonContain.count; i ++) {
                    if(model.userId == weakSelf.selelectPersonContain[i].userId){
                        [weakSelf.selelectPersonContain removeObjectAtIndex:i];
                    }
                }
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf changeSelecteNum];
                    
                    [weakSelf.selectePerson changeSelectePersons:[weakSelf.selelectPersonContain copy] invocation:^(CGRect frame) {
                        
                        CGFloat x = frame.origin.x + frame.size.width;
                        CGRect destinaFrame = CGRectMake(x, weakSelf.searchBar.frame.origin.y, weakSelf.view.frame.size.width - x, weakSelf.searchBar.frame.size.height);
                        
                        
                        if(weakSelf.searchBar.frame.size.width == self.view.frame.size.width){
                            weakSelf.searchBar.frame = CGRectMake(weakSelf.searchBar.frame.origin.x, weakSelf.searchBar.frame.origin.y, destinaFrame.size.width, weakSelf.searchBar.frame.size.height);
                        }
                        
                        [UIView animateWithDuration:0.4 animations:^{
                            weakSelf.searchBar.frame = destinaFrame;
                        } completion:^(BOOL finished) {
                            [weakSelf.searchBar refreshUI];
                        }];
                    }];
                    
                    dispatch_semaphore_signal(weakSelf.selectePersonLock);
                }];
                
                return;
            }
        }
    }
    
    dispatch_semaphore_signal(weakSelf.selectePersonLock);
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self keybordResign];
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

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self keybordResign];
}

- (void)keybordResign{
    [self.searchBar resignKeyBoard];
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
