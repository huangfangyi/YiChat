//
//  YiChatConversationVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConversationVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatMenuView.h"
#import "ProjectSearchBarView.h"
#import "XYQRCodeScan.h"
#import "ZFChatUIHelper.h"
#import "ZFChatHelper.h"
#import "ProjectCommonCellModel.h"
#import "ZFChatRequestHelper.h"
#import "YiChatConversationCell.h"
#import "YiChatMsgInfoVC.h"
#import "HTDBManager.h"
#import "YiChatServiceClient.h"
#import "YiChatConversationMenuView.h"
#import <JPUSHService.h>
#import "YiChatXcxVC.h"
#import "YiChatServiceClient.h"
static NSString *placedTop = @"PlacedTop";//置顶

@interface YiChatConversationVC ()<UITableViewDataSource,UITableViewDelegate,UIScrollViewDelegate>

@property (nonatomic,strong) YiChatMenuView *menu;

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) NSArray *dataSourceArr;

@property (nonatomic,strong) ZFChatNotifyEntity *connectNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *conversationChangeNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeActive;
@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeBackground;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_friendNotify;
    
@property (nonatomic,strong) ZFChatNotifyEntity *notify_groupDeleteNotify;

@property (nonatomic,assign) BOOL isLoadConversation;

@property (nonatomic,strong) YiChatConversationMenuView *xcxMenu;

@end

@implementation YiChatConversationVC

+ (id)initialVC{
    YiChatConversationVC *conversation = [YiChatConversationVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_9 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"conversationMain") leftItem:nil rightItem:[UIImage imageNamed:@"copy2@3x.png"]];
    return conversation;
}

- (void)dealloc{
    [self removeNotify];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _isLoadConversation = NO;
    
    [self makeTable];
  
    [self addNotify];
    
    [self updateUser];
    
    [self getSystemConfig];
    
    // Do any additional setup after loading the view.
}


- (void)updateUser{
    [ProjectHelper helper_getGlobalThread:^{
        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            
        }];
    }];
}

- (void)getSystemConfig{
    //更新通讯录
    [[YiChatUserManager defaultManagaer] updateUserConnectionInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        
    }];
    NSNumber *version = [NSNumber numberWithInteger:[[ProjectHelper helper_getAppVersionCode] integerValue]];
    
    if(version && [version isKindOfClass:[NSNumber class]]){
        [ProjectRequestHelper getSystemConfigWithParameters:@{@"type":@"1",@"currentVersion":version} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token]  progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *data = obj[@"data"];
                    if(data && [data isKindOfClass:[NSDictionary class]]){
                        NSArray *unreadListArr = data[@"unreadCountList"];
                        NSDictionary *versionDic = data[@"iosVersion"];
                        NSNumber *createGroupAuthStatusStr = data[@"createGroupAuthStatus"];
                        NSDictionary *sharedDic = data[@"share"];
                        
                        if(sharedDic && [sharedDic isKindOfClass:[NSDictionary class]]){
                            YiChatUserManager *manager = [YiChatUserManager defaultManagaer];
                            manager.sharedLink = sharedDic[@"iosLink"];
                            manager.sharedContent = sharedDic[@"content"];
                        }
                        
                        [self dealUnreadCount:unreadListArr];
                        [self dealSystemVersion:versionDic];
                        [self dealGroupPower:createGroupAuthStatusStr];
                        
                    }
                }
                else{
                    [self autoGetSystemConfig];
                }
            }];
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [self autoGetSystemConfig];
        }];
    }
}

- (void)autoGetSystemConfig{

}

- (void)dealSystemVersion:(NSDictionary *)versionDic{
    if(versionDic && [versionDic isKindOfClass:[NSDictionary class]]){
        
    }
}

- (void)dealUnreadCount:(NSArray *)unreadMesArr{
    if(unreadMesArr && [unreadMesArr isKindOfClass:[NSArray class]]){
        if(unreadMesArr.count > 0 ){
            [[YiChatUserManager defaultManagaer] storageUnreadMessage:unreadMesArr];
            
            [[YiChatServiceClient defaultChatClient] dealUnreadMessage];
        }
    }
}

- (void)dealGroupPower:(NSNumber *)power{
    if(power && [power isKindOfClass:[NSNumber class]]){
        //全局创建群权限  0没有 1有
        NSInteger createGroupPower = power.integerValue;
        YiChatUserManager *manager = [YiChatUserManager defaultManagaer];
        if(YiChatProject_IsControlCreatGroupPower){
            manager.createGroupPower = createGroupPower;
        }
        else{
            manager.createGroupPower = 1;
        }
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self makeUIForConnect];
    [self loadData];
    
    [[YiChatServiceClient defaultChatClient] dealUnreadMessage];
    [self.xcxMenu reloadDataWhenNoData];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if(self.menu){
        self.menu.hidden = YES;
        [self.menu clean];
        self.menu = nil;
    }
}

//小程序初始化
- (YiChatConversationMenuView *)xcxMenu{
    if(!_xcxMenu){
        _xcxMenu = [YiChatConversationMenuView createMenu];
        __weak typeof(self) weakSelf = self;
        _xcxMenu.loadDataDoneBlock = ^(BOOL isData) {
            if (!isData) {
                [[NSUserDefaults standardUserDefaults] setObject:@"1" forKey:@"ISNOXCXDADA"];
                [[NSUserDefaults standardUserDefaults] synchronize];
            }else{
                [[NSUserDefaults standardUserDefaults] setObject:@"0" forKey:@"ISNOXCXDADA"];
                [[NSUserDefaults standardUserDefaults] synchronize];
            }
//            if (!isData) {
//                if(weakSelf.cTable.contentInset.top != 0){
//                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//                        [UIView animateWithDuration:0.25 animations:^{
//                            weakSelf.cTable.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
//                        }];
//                    });
//
//                }
//            }
        };
        
        self.xcxMenu.zfConversationItemClick = ^(ZFMenuEntity * _Nonnull entity) {
            YiChatXcxVC *vc = [YiChatXcxVC initialVCName:entity.title];
            vc.url = entity.url;
            [weakSelf.navigationController pushViewController:vc animated:YES];
            if(weakSelf.cTable.contentInset.top != 0){
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [UIView animateWithDuration:0.25 animations:^{
                        weakSelf.cTable.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
                    }];
                });
                
            }
        };
    }
    return _xcxMenu;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    if ([[NSUserDefaults standardUserDefaults] stringForKey:@"ISNOXCXDADA"]) {
        NSString *str = [[NSUserDefaults standardUserDefaults] stringForKey:@"ISNOXCXDADA"];
        if ([str isEqualToString:@"1"]) {
            return;
        }
    }
    
    if(scrollView.contentOffset.y <= - 120.0){
        
        if(self.cTable.contentInset.top != self.xcxMenu.frame.size.height){
            [UIView animateWithDuration:0.35 animations:^{
                self.cTable.contentInset = UIEdgeInsetsMake(self.xcxMenu.frame.size.height, 0, 0, 0);
            }];
        }
    }
    else{
        if(self.cTable.contentInset.top != 0){
            //            [UIView animateWithDuration:0.25 animations:^{
            //                self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
            //            }];
            
        }
    }
    
    if(self.cTable.contentInset.top != 0 && scrollView.contentOffset.y > - self.xcxMenu.frame.size.height){
        [UIView animateWithDuration:0.25 animations:^{
            self.cTable.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
        }];
    }
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

- (void)addNotify{
    _connectNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleXMPPConnectionState target:self sel:@selector(xmppConnectionChange:)];
    [_connectNotify addNotify];
    
    _conversationChangeNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleConversationChanged target:self sel:@selector(conversationChanged:)];
    [_conversationChangeNotify addNotify];
    
    _notify_app_becomeBackground = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeBackground target:self sel:@selector(appDidEndBackgraound:)];
    [_notify_app_becomeBackground addNotify];
    
    _notify_app_becomeActive = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeActive target:self sel:@selector(appDidBeginActive:)];
    [_notify_app_becomeActive addNotify];
    
    _notify_friendNotify = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleFriendNotify target:self sel:@selector(friendNotifyAction:)];
    [_notify_friendNotify addNotify];
    
    _notify_groupDeleteNotify = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleFriendNotify target:self sel:@selector(groupDeleteNotify:)];
    [_notify_groupDeleteNotify addNotify];
}
    
- (void)groupDeleteNotify:(NSNotification *)notify{
    NSDictionary *dic = [notify object];
    
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSString *groupId = dic[@"groupId"];
        NSNumber *isSender = dic[@"isSender"];
        
        if(groupId && [groupId isKindOfClass:[NSString class]] && isSender && [isSender isKindOfClass:[NSNumber class]]){
            if(!isSender.boolValue){
                NSString *chatId = groupId;
                
                HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
                [manager deleteOneChatterAllMessagesByChatterId:chatId];
                [manager deleteOneConversationWithChatterId:chatId isCleanAllHistoryMessage:YES];
                [[HTDBManager sharedInstance] deleteOneConversationWithChatterId:chatId];
                
                
                NSMutableArray *mutableArray = [NSMutableArray new];
                [mutableArray addObjectsFromArray:self.dataSourceArr.mutableCopy];
                
                for (int i = 0; i < mutableArray.count; i ++) {
                    HTConversation *conversation = mutableArray[i];
                    if(conversation && [conversation isKindOfClass:[HTConversation class]]){
                        if([conversation.chatterId isEqualToString:chatId]){
                            [mutableArray removeObjectAtIndex:i];
                        }
                    }
                }
                
                self.dataSourceArr = mutableArray;
                
                [self tableReloadData];

            }
        }
    }
}

- (void)friendNotifyAction:(NSNotification *)notify{
    id obj = [notify object];
    if(obj && [obj isKindOfClass:[NSDictionary class]]){
        NSString *action = obj[@"type"];
        NSDictionary *msg = obj[@"msg"];
        ZFMessageType type = [ZFChatHelper zfChatHeler_getMessageTypeWithAction:[action integerValue]];
        
        if(type == ZFMessageTypeFriendApplyAgree || type == ZFMessageTypeFriendDeleteMe){
            
            if([msg isKindOfClass:[NSDictionary class]] && msg){
                NSString *userId = [NSString stringWithFormat:@"%ld",[msg[@"userId"] integerValue]];
                if(userId && [userId isKindOfClass:[NSString class]]){
                    
                    if(type == ZFMessageTypeFriendDeleteMe){
                        
                       
                        [[YiChatUserManager defaultManagaer] deleteConnectionFriends:@[userId] invocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull des) {
                            
                        }];
                        
                        HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
                        [manager deleteOneChatterAllMessagesByChatterId:userId];
                        [manager deleteOneConversationWithChatterId:userId isCleanAllHistoryMessage:YES];
                        [[HTDBManager sharedInstance] deleteOneConversationWithChatterId:userId];
                        
                       
                        NSMutableArray *mutableArray = [NSMutableArray new];
                        [mutableArray addObjectsFromArray:self.dataSourceArr.mutableCopy];
                        
                        for (int i = 0; i < mutableArray.count; i ++) {
                            HTConversation *conversation = mutableArray[i];
                            if(conversation && [conversation isKindOfClass:[HTConversation class]]){
                                if([conversation.chatterId isEqualToString:userId]){
                                    [mutableArray removeObjectAtIndex:i];
                                }
                            }
                        }
                        
                        self.dataSourceArr = mutableArray;
                        
                        [self tableReloadData];
                        
                    }
                    
                    /*
                     {
                     action = 1001;
                     data =     {
                     avatar = "http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/F3921EE7-7E45-43F4-9582-221FC1282AD7.png";
                     nick = huahua;
                     reason = "";
                     userId = 14012457;
                     };
                     }
                     
                    */
                    
                    else if(type == ZFMessageTypeFriendApplyAgree){
                        
                        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                            
                            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                NSDictionary *userInfoDic = [model getOriginDic];
                                if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
                                    
                                    [[YiChatUserManager defaultManagaer] addConnectionFriends:@[userInfoDic] invocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
                                        
                                    }];
                                    
                                }
                            }
                            
                        }];
                        
                    }
                }
            }
        }
        
    }
}

- (void)xmppConnectionChange:(NSNotification *)notify{
    id obj = [notify object];
    
    if([obj isKindOfClass:[NSNumber class]] && obj){
        NSInteger state = [obj integerValue];
        [ProjectHelper helper_getMainThread:^{
            if(state == 0){
                [self makeUIForConnect];
            }
            else if(state == 1){
                [self makeUIForConnecting];
            }
            else if(state == 2){
                [self makeUIForDisconnect];
            }
            else{
                [self makeUIForConnect];
            }
        }];
    }
}

- (void)appDidEndBackgraound:(NSNotification *)notfy{
    
}

- (void)appDidBeginActive:(NSNotification *)notfy{
    [self loadData];
}

- (void)conversationChanged:(NSNotification *)notify{
    //更新 会话
    [self loadData];
    
    YiChatServiceClient * client = [YiChatServiceClient defaultChatClient];
    [client updateJGJushTagWithGroup];
}

- (void)removeNotify{
    [_notify_groupDeleteNotify removeMotify];
    _notify_groupDeleteNotify = nil;
    
    [_connectNotify removeMotify];
    _connectNotify = nil;
    
    [_conversationChangeNotify removeMotify];
    _conversationChangeNotify = nil;
    
    [_notify_app_becomeBackground removeMotify];
    _notify_app_becomeBackground = nil;
    
    [_notify_app_becomeActive removeMotify];
    _notify_app_becomeActive = nil;
    
    [_notify_friendNotify removeMotify];
    _notify_friendNotify = nil;
}

- (void)loadData{
    WS(weakSelf);
    if(_isLoadConversation == YES){
        return;
    }
    
    _isLoadConversation = YES;
    [ProjectHelper helper_getGlobalThread:^{
        [ZFChatRequestHelper zfRequestHelper_loadConversations:^(NSArray<HTConversation *> * _Nonnull conversationArr) {
            [ProjectHelper helper_getGlobalThread:^{
                if(conversationArr && [conversationArr isKindOfClass:[NSArray class]]){
                    weakSelf.dataSourceArr = nil;
                    weakSelf.dataSourceArr = [weakSelf sortArr:conversationArr];
                    
                    if ([[NSUserDefaults standardUserDefaults] stringForKey:placedTop]) {
                        NSString *pId = [[NSUserDefaults standardUserDefaults] stringForKey:placedTop];
                        HTConversation *model;
                        NSInteger index = 0;
                        for (NSInteger i = 0; i < conversationArr.count; i++) {
                            HTConversation *m = conversationArr[i];
                            if ([m.chatterId isEqualToString:pId]) {
                                model = m;
                                index = i;
                            }
                        }
                        
                        if (model) {
                            NSMutableArray *mutableArray = [NSMutableArray new];
                            [mutableArray addObjectsFromArray:conversationArr.mutableCopy];
                            [mutableArray removeObjectAtIndex:index];
                            NSArray *newArr = mutableArray.copy;
                            [mutableArray removeAllObjects];
                            [mutableArray addObjectsFromArray:@[model]];
                            [mutableArray addObjectsFromArray:[self sortArr:newArr]];
                            self.dataSourceArr = mutableArray.copy;
                        }
                    }
                    [weakSelf tableReloadData];
                    [ZFChatHelper zfChatHelper_updateUnreadMessage];
                    _isLoadConversation = NO;
                }
                else{
                    _isLoadConversation = NO;
                }
            }];
        }];
    }];
}

-(NSArray *)sortArr:(NSArray *)sort{
    return [sort sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        HTConversation *m1 = obj1;
        HTConversation *m2 = obj2;
        if (m1.lastMessage.timestamp < m2.lastMessage.timestamp) {
            return NSOrderedDescending;
        }
        return NSOrderedAscending;
    }];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [self menu];
}

- (YiChatMenuView *)menu{
    if(!_menu){
        
        WS(weakSelf);
        
        _menu = [[YiChatMenuView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        HelperintergerObjFlagInvocation click = ^void(NSInteger row,NSDictionary *data){
            [weakSelf.menu clean];
            weakSelf.menu = nil;
            if([data isKindOfClass:[NSDictionary class]]){
                if([data.allKeys containsObject:@"text"]){
                    NSString *str = data[@"text"];
                    if([str isEqualToString:@"发起群聊"]){
                        
                        [[YiChatUserManager defaultManagaer] fetchUserCreateGroupAuthInvocation:^(BOOL isHasAuth, NSString * _Nonnull des) {
                            if(isHasAuth){
                                
                                [ProjectHelper helper_getMainThread:^{
                                    UIViewController *vc = [ProjectHelper helper_getVCWithName:@"YiChatCreateGroupVC" initialMethod:@selector(initialVC)];
                                    if([vc respondsToSelector:@selector(createEndStyle)]){
                                        [vc setValue:[NSNumber numberWithInteger:0] forKey:@"createEndStyle"];
                                    }
                                    vc.hidesBottomBarWhenPushed = YES;
                                    if(vc){
                                        [weakSelf.navigationController pushViewController:vc animated:YES];
                                    }
                                }];
                                
                            }
                            else{
                                UIAlertController *alt = [UIAlertController alertControllerWithTitle:@"提示" message:des preferredStyle:UIAlertControllerStyleAlert];
                                
                                UIAlertAction *destructive = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDestructive handler:nil];
                                [alt addAction:destructive];
                                [weakSelf presentViewController:alt animated:YES completion:nil];
                            }
                        }];
                        
                    }
                    else if([str isEqualToString:@"添加好友"]){
                        [weakSelf pushVCWithName:@"YiChatAddFriendsVC"];
                    }
                    else if([str isEqualToString:@"扫一扫"]){
                        XYQRCodeScan *scan = [[XYQRCodeScan alloc] init];
                        scan.hidesBottomBarWhenPushed = YES;
                        [self.navigationController pushViewController:scan animated:YES];
                    }
                    else if([str isEqualToString:@"帮助"]){
                        [weakSelf pushVCWithName:@"YiChatHelperListVC"];
                    }
                }
            }
        };
        HelperIntergeFlagInvocation backClick = ^void(NSInteger row){
            weakSelf.menu = nil;
        };
        
        _menu.clickDic = @{@"click":click};
        _menu.backClickDic = @{@"click":backClick};
        
        [_menu createUI];
        [[ProjectHelper helper_getAppDelegate].window addSubview:_menu];;
    }
    return _menu;
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        WS(weakSelf);
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder = PROJECT_TEXT_LOCALIZE_NAME(@"conversationMainSearchPlaceHolder");
        _searchBar.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
            if ([obj isKindOfClass:[NSDictionary class]]) {
                NSLog(@"%@",obj);
                NSDictionary *dic = (NSDictionary *)obj;
                if ([dic[@"searchStyle"] isEqualToString:@"ProjectSearchBarStyleSearchMessage"]) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        id obj = dic[@"msgArr"];
                        if ([obj isKindOfClass:[NSDictionary class]]) {
                            NSDictionary *dic = (NSDictionary *)obj;
                            NSString *type = dic[@"type"];
                            if ([type isEqualToString:@"0"]) {
                                UIViewController *chat = [ZFChatUIHelper getChatVCWithChatId:(NSString *)dic[@"data"] chatType:@"2"];
                                chat.hidesBottomBarWhenPushed = YES;
                                [weakSelf.navigationController pushViewController:chat animated:YES];
                            }
                            
                            if ([type isEqualToString:@"1"]) {
                                UIViewController *chat = [ZFChatUIHelper getChatVCWithChatId:(NSString *)dic[@"data"] chatType:@"1"];
                                chat.hidesBottomBarWhenPushed = YES;
                                [weakSelf.navigationController pushViewController:chat animated:YES];
                            }
                            
                            if ([type isEqualToString:@"2"]) {
                                YiChatMsgInfoVC *vc = [YiChatMsgInfoVC initialVC];
                                vc.dataArr = (NSArray *)dic[@"data"];
                                [weakSelf.navigationController pushViewController:vc animated:YES];
                            }
                            
                        }
                        
                    });
                }
            }
        };
        [_searchBar initialSearchType:1];
        [_searchBar createUI];
        _searchBar.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    return _searchBar;
}

- (void)makeTable{
    
    self.tableStyle = 1;
    
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:self.dataSourceArr.count]];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH) - PROJECT_SIZE_TABH - PROJECT_SIZE_SafeAreaInset.bottom);
    [self.view addSubview:self.cTable];
    [self.cTable addSubview:self.xcxMenu];
}

- (void)tableReloadData{
    [ProjectHelper helper_getMainThread:^{
        if(self.dataSourceArr && [self.dataSourceArr isKindOfClass:[NSArray class]]){
            self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:self.dataSourceArr.count]];
            [self.cTable reloadData];
        }
    }];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_CONVERSATION_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return ProjectUIHelper_SearchBarH;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    return self.searchBar;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatConversationCell *cell =  nil;
    if(indexPath.section == 0){
        static NSString *str = @"YiChatConversationCell_Common";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatConversationCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:PROJECT_SIZE_CONVERSATION_CELLH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:PROJECT_SIZE_CONVERSATION_CELLH]];
        
        if(self.dataSourceArr.count > 0){
            if((self.dataSourceArr.count - 1) >= indexPath.row){
                HTConversation *model = self.dataSourceArr[indexPath.row];
                
                if(model && [model isKindOfClass:[HTConversation class]]){
                    if ([[NSUserDefaults standardUserDefaults] stringForKey:placedTop]) {
                        NSString *s = [[NSUserDefaults standardUserDefaults] stringForKey:placedTop];
                        if ([s isEqualToString:model.chatterId]) {
                            cell.backgroundColor = [UIColor colorWithWhite:0.7 alpha:0.3];
                        }else{
                            cell.backgroundColor = [UIColor whiteColor];
                        }
                    }else{
                        cell.backgroundColor = [UIColor whiteColor];
                    }
                    
                    
                    cell.cellModel = model;
                }
               
            }
        }
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if(self.dataSourceArr.count - 1 >= indexPath.row){
        HTConversation *model = self.dataSourceArr[indexPath.row];
        if(model && [model isKindOfClass:[HTConversation class]]){
            if(model.lastMessage && [model.lastMessage isKindOfClass:[HTMessage class]]){
                model.unreadMessageCount = 0;
                
                NSString *chatId =  model.chatterId;
                if(model.lastMessage && [model.lastMessage isKindOfClass:[HTMessage class]]){
                    if(model.lastMessage.chatType && [model.lastMessage.chatType isKindOfClass:[NSString class]]){
                        if([model.lastMessage.chatType isEqualToString:@"2"]){
                            if(model.lastMessage.to && [model.lastMessage.to isKindOfClass:[NSString class]]){
                                chatId = model.lastMessage.to;
                            }
                        }
                    }
                }
                if(chatId && [chatId isKindOfClass:[NSString class]] && model.lastMessage.chatType && [model.lastMessage.chatType isKindOfClass:[NSString class]]){
                    
                    if([model.lastMessage.chatType isEqualToString:@"1"]){
                        
                        [[YiChatUserManager defaultManagaer] connectionLoadInvocation:^(YiChatConnectionModel * _Nonnull connectionModel, NSString * _Nonnull error) {
                            
                            if(connectionModel && [connectionModel isKindOfClass:[YiChatConnectionModel class]]){
                                
                                
                                __block BOOL isHas = NO;
                                
                                for (int k = 0 ; k < connectionModel.connectionModelArr.count; k ++) {
                                    NSDictionary *dataDic = connectionModel.connectionModelArr[k];
                                    
                                    if(dataDic && [dataDic isKindOfClass:[NSDictionary class]]){
                                        if(dataDic && [dataDic isKindOfClass:[NSDictionary class]]){
                                            NSString *key = dataDic.allKeys.lastObject;
                                            
                                            if(key && [key isKindOfClass:[NSString class]]){
                                                NSArray *dataArr = dataDic[key];
                                                
                                                
                                                if(dataArr && [dataArr isKindOfClass:[NSArray class]]){
                                                    
                                                    dispatch_apply(dataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
                                                        YiChatUserModel *friendDic = dataArr[i];
                                                        
                                                        if(friendDic && [friendDic isKindOfClass:[YiChatUserModel class]]){
                                                            
                                                            NSString *storageId = [friendDic getUserIdStr];
                                                            
                                                            if(storageId && [storageId isKindOfClass:[NSString class]]){
                                                                
                                                                if([storageId isEqualToString:chatId]){
                                                                    isHas = YES;
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                if(isHas){
                                    
                                    [ProjectHelper helper_getMainThread:^{
                                        UIViewController *chat = [ZFChatUIHelper getChatVCWithChatId:chatId chatType:model.lastMessage.chatType];
                                        chat.hidesBottomBarWhenPushed = YES;
                                        [self.navigationController pushViewController:chat animated:YES];
                                        
                                        [ZFChatHelper zfCahtHelper_updateLocalConcersationWithConversation:model isReadAllMessage:YES];
                                    }];
                                    
                                }
                                else{
                                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"好友关系未同步"];
                                    HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
                                    [manager deleteOneChatterAllMessagesByChatterId:chatId];
                                    [manager deleteOneConversationWithChatterId:chatId isCleanAllHistoryMessage:YES];
                                    [self loadData];
                                }
                                
                                
                            }
                            
                        }];
                    }
                    else{
                        UIViewController *chat = [ZFChatUIHelper getChatVCWithChatId:chatId chatType:model.lastMessage.chatType];
                        chat.hidesBottomBarWhenPushed = YES;
                        [self.navigationController pushViewController:chat animated:YES];
                        
                        [ZFChatHelper zfCahtHelper_updateLocalConcersationWithConversation:model isReadAllMessage:YES];
                    }
                    
                    
                }
                
               
            }
        }
    }
}

- (NSArray<UITableViewRowAction *> *)tableView:(UITableView *)tableView editActionsForRowAtIndexPath:(NSIndexPath *)indexPath{
    HTConversation *model = self.dataSourceArr[indexPath.row];
    NSString *title = @"置顶";
    if ([[NSUserDefaults standardUserDefaults] stringForKey:placedTop]) {
        NSString *s = [[NSUserDefaults standardUserDefaults] stringForKey:placedTop];
        if ([s isEqualToString:model.chatterId]) {
            title = @"取消置顶";
        }
    }
    
    WS(weakSelf);
    
    UITableViewRowAction *action0 = [UITableViewRowAction rowActionWithStyle:UITableViewRowActionStyleNormal title:title handler:^(UITableViewRowAction *action, NSIndexPath *indexPath) {
        if(self.dataSourceArr.count - 1 >= indexPath.row){
            if ([title isEqualToString:@"置顶"]) {
                if(model && [model isKindOfClass:[HTConversation class]]){
                    if(model.lastMessage && [model.lastMessage isKindOfClass:[HTMessage class]]){
                        NSLog(@"====   %@",model.chatterId);
                        [[NSUserDefaults standardUserDefaults] setObject:model.chatterId forKey:placedTop];
                        [[NSUserDefaults standardUserDefaults] synchronize];
                    }
                }
                
                NSMutableArray *mutableArray = [NSMutableArray new];
                [mutableArray addObjectsFromArray:self.dataSourceArr.mutableCopy];
                [mutableArray removeObjectAtIndex:indexPath.row];
                self.dataSourceArr = mutableArray.copy;
                [mutableArray removeAllObjects];
                [mutableArray addObjectsFromArray:@[model]];
                [mutableArray addObjectsFromArray:self.dataSourceArr];
                self.dataSourceArr = mutableArray.copy;
            }else{
                self.dataSourceArr = [weakSelf sortArr:self.dataSourceArr];
                [[NSUserDefaults standardUserDefaults] removeObjectForKey:placedTop];
                
            }
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf tableReloadData];
            });
        }
        // 收回左滑出现的按钮(退出编辑模式)
        tableView.editing = NO;
    }];
    action0.backgroundColor = [UIColor lightGrayColor];
    UITableViewRowAction *action1 = [UITableViewRowAction rowActionWithStyle:UITableViewRowActionStyleDefault title:@"删除" handler:^(UITableViewRowAction *action, NSIndexPath *indexPath) {
        tableView.editing = NO;
        if(self.dataSourceArr.count - 1 >= indexPath.row){
            NSMutableArray *mutableArray = [NSMutableArray new];
            [mutableArray addObjectsFromArray:self.dataSourceArr.mutableCopy];
            [mutableArray removeObjectAtIndex:indexPath.row];
            self.dataSourceArr = mutableArray.copy;
            
            HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
            [manager deleteOneChatterAllMessagesByChatterId:model.chatterId];
            [manager deleteOneConversationWithChatterId:model.chatterId isCleanAllHistoryMessage:YES];
            [[HTDBManager sharedInstance] deleteOneConversationWithChatterId:model.chatterId];
            if ([title isEqualToString:@"取消置顶"]) {
                [[NSUserDefaults standardUserDefaults] removeObjectForKey:placedTop];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf tableReloadData];
            });
        }
    }];
    return @[action1, action0];
}

//先要设Cell可编辑
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

//设置进入编辑状态时，Cell不会缩进
- (BOOL)tableView: (UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
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
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
