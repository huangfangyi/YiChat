//
//  YiChatConnectionVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConnectionVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchBarView.h"
#import "YiChatConnectionCell.h"
#import "ProjectCommonCellModel.h"
#import "YiChatAddFriendsVC.h"

#import "YiChatPhoneConnectionIndexView.h"
#import "YiChatContactMatchVC.h"
#import "ZFChatUIHelper.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "YiChatConnectionModel.h"

#import "YiChatFriendInfoVC.h"
#import "ZFChatHelper.h"
#import "ZFChatFriendHelper.h"
#import "YiChatMassView.h"
#import <KLCPopup.h>
#import "ZFChatMessageHelper.h"
#import "ZFTransportPresenter.h"
#import "ZFMessageTransportVC.h"
#import "ZFChatConfigure.h"
@interface YiChatConnectionVC ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) YiChatConnectionModel *model;

@property (nonatomic,strong) NSMutableArray *dataSourceArr;

@property (nonatomic,assign) NSInteger currentPage;

@property (nonatomic,strong) NSArray *toolCellData;

@property (nonatomic,strong) YiChatPhoneConnectionIndexView *indexView;

@property (nonatomic) BOOL isFetchConversation;

@property (nonatomic,assign) NSInteger unixDate;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_friendNotify;

@property (nonatomic,assign) BOOL isNewMessage;

@property (nonatomic,strong) KLCPopup *popView;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_changeAppBradge;
@end

#define YiChatConnectionVC_CellH PROJECT_SIZE_COMMON_CELLH
@implementation YiChatConnectionVC

- (void)dealloc{
    [self removeNotify];
}

+ (id)initialVC{
    YiChatConnectionVC *connection = [YiChatConnectionVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_5 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"connectionMain") leftItem:nil rightItem:nil];
    return connection;
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        WS(weakSelf);
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder = PROJECT_TEXT_LOCALIZE_NAME(@"connectionMainSearchPlaceHolder");
        [_searchBar initialSearchType:1];
        [_searchBar initialSearchStyle:1];
        [_searchBar createUI];
        _searchBar.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
            if(obj && [obj isKindOfClass:[YiChatUserModel class]]){
                
                [ProjectHelper helper_getMainThread:^{
                    YiChatFriendInfoVC *friendInfo = [YiChatFriendInfoVC initialVC];
                    friendInfo.model = obj;
                    friendInfo.hidesBottomBarWhenPushed = YES;
                    [weakSelf.navigationController pushViewController:friendInfo animated:YES];
                }];
                
            }
        };
    }
    return _searchBar;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _isFetchConversation = NO;
    _isNewMessage = NO;
    _unixDate = 0;
    
    [self makeTable];
    
    [self addNotify];
    // Do any additional setup after loading the view.
}

- (void)addNotify{
    _notify_friendNotify = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleFriendNotify target:self sel:@selector(friendNotifyAction:)];
    [_notify_friendNotify addNotify];
    
    _notify_app_changeAppBradge = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleChangeBradgeNum];
    
}

- (void)removeNotify{
    [_notify_friendNotify removeMotify];
    _notify_friendNotify = nil;
    
    [_notify_app_changeAppBradge removeMotify];
    _notify_app_changeAppBradge = nil;
}

- (void)friendNotifyAction:(NSNotification *)notify{
    id obj = [notify object];
    if(obj && [obj isKindOfClass:[NSDictionary class]]){
        NSString *action = obj[@"type"];
        ZFMessageType type = [ZFChatHelper zfChatHeler_getMessageTypeWithAction:[action integerValue]];
        
        if(type == ZFMessageTypeFriendApply){
            _isNewMessage = YES;
            [ProjectHelper helper_getMainThread:^{
                [self.cTable reloadData];
            }];
        }
        
        if(type == ZFMessageTypeFriendApplyAgree || type == ZFMessageTypeFriendDeleteMe){
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [self loadConnectionData];
            });
        }
    }
}

- (void)fetchFriendApply{
    
    NSDictionary *token = [ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token];
    WS(weakSelf);
    
    [ProjectRequestHelper fetchFriendApplyNumWithParameters:@{} headerParameters:token progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj){
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSNumber *num = obj[@"data"];
                    if(num && [num isKindOfClass:[NSNumber class]]){
                        if(num.integerValue > 0){
                            weakSelf.isNewMessage = YES;
                            
                           
                            
                            [ProjectHelper helper_getMainThread:^{
                                
                                [weakSelf.notify_app_changeAppBradge postNotifyWithContent:@{@"tabNum":Project_TabIdengtify_Connection,@"num":[NSString stringWithFormat:@"%d",num.integerValue]}];
                                
                                [self.cTable reloadData];
                            }];
                            return;
                        }
                    }
                }
            }
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self loadConnectionData];
    
    [self fetchFriendApply];
    
    [[YiChatUserManager defaultManagaer] getMessageNotifyDataWithChatId:YiChatNotify_FriendApply invocation:^(id  _Nonnull data) {
        if(data && [data isKindOfClass:[NSDictionary class]]){
            NSDictionary *dic = data;
            if(dic.allKeys.count != 0){
                _isNewMessage = YES;
                
                [ProjectHelper helper_getMainThread:^{
                    [self.cTable reloadData];
                }];
            }
        }
    }];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
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
    [[YiChatUserManager defaultManagaer] connectionLoadInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        
        _isFetchConversation = NO;
        
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            weakSelf.model = model;
            
            [weakSelf tableUpdate];
        }
        else if([error isKindOfClass:[NSString class]] && error){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            return;
        }
    }];
}

- (void)tableUpdate{
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    if( [self.toolCellData isKindOfClass:[NSArray class]]){
        if(self.toolCellData.count != 0){
            [tmp addObject:[NSNumber numberWithInteger:self.toolCellData.count]];
        }
    }
    if(tmp.count == 0){
        [tmp addObject:[NSNumber numberWithInteger:0]];
    }
    
    for (int i = 0; i < self.model.connectionModelArr.count; i++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSString *key =  dic.allKeys.lastObject;
            NSArray * row= dic[key];
            if([row isKindOfClass:[NSArray class]] && row){
                 [tmp addObject:[NSNumber numberWithInteger:row.count]];
            }
        }
    }
    self.sectionsRowsNumSet = tmp;
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
        [self updateIndexView];
    }];
}

- (void)updateScections:(NSInteger)section invocation:(void(^)(void))invocation{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    if( [self.toolCellData isKindOfClass:[NSArray class]]){
        if(self.toolCellData.count != 0){
            [tmp addObject:[NSNumber numberWithInteger:self.toolCellData.count]];
        }
    }
    if(tmp.count == 0){
        [tmp addObject:[NSNumber numberWithInteger:0]];
    }
    
    NSArray *keys = [self getCharacterArrs];
    
    if(keys && [keys isKindOfClass:[NSArray class]]){
        for (int i = 0; i < keys.count; i ++) {
            NSString *key =  keys[i];
            NSDictionary *dic = _model.connectionModelArr[i];
            if(key && [key isKindOfClass:[NSString class]] && [dic isKindOfClass:[NSDictionary class]] && dic){
                NSArray *row = dic[key];
                if([row isKindOfClass:[NSArray class]] && row){
                    [tmp addObject:[NSNumber numberWithInteger:row.count]];
                }
            }
        }
    }
    self.sectionsRowsNumSet = tmp;
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
        [self updateIndexView];
        invocation();
    }];
}

- (void)loadSystemData{
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    NSArray *iconArr = @[@"connect_cont_icon.png",@"connect_add_icon.png",@"connecr_group_icon.png",@"connect_mass_icon.png"];
    //,@"手机通讯录匹配"
    NSArray *textArr = @[@"新的朋友",@"添加好友",@"已保存的群聊",@"消息群发"];
    for (int i = 0; i < textArr.count; i ++) {
        ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
        model.titleStr = textArr[i];
        if((iconArr.count - 1) >= i){
            if(iconArr[i] != nil){
                model.iconUrl = iconArr[i];
            }
        }
        if(model != nil){
            [tool addObject:model];
        }
    }
    _toolCellData = tool;
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:_toolCellData.count]];
}

- (void)makeTable{
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_async(group, queue, ^{
        [self loadSystemData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [self.view addSubview:self.cTable];
        self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom - PROJECT_SIZE_TABH);
        [self.view addSubview:self.indexView];
    });
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

- (NSArray *)getCharacterArrs{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _model.connectionModelArr.count; i ++) {
        NSDictionary *dic = _model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            id key = dic.allKeys.lastObject;
            if(key){
                [arr addObject:key];
            }
        }
    }
    return arr;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return YiChatConnectionVC_CellH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(section == 0){
        return ProjectUIHelper_SearchBarH;
    }
    else{
        return 30.0;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    if(section == 0 && self.toolCellData.count != 0){
        return self.searchBar;
    }
    else{
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, 30.0)];
        back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        [back addSubview:lab];
        NSString *key = [self getKeyWithIndex:section];
        if([key isKindOfClass:[NSString class]] && key){
            lab.text = key;
        }
        return back;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatConnectionCell *cell =  nil;
    if(indexPath.section == 0){
        static NSString *str = @"YiChatConnection_ToolIcon";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatConnectionCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:YiChatConnectionVC_CellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:YiChatConnectionVC_CellH]];
        
        if(indexPath.row <= (_toolCellData.count - 1)){
            cell.cellModel = _toolCellData[indexPath.row];
        }
        
        cell.connectionNewMessageIcons.hidden = YES;
        if(cell.cellModel.titleStr && [cell.cellModel.titleStr isKindOfClass:[NSString class]]){
            if([cell.cellModel.titleStr isEqualToString:@"新的朋友"]){
                if(self.isNewMessage == YES){
                    cell.connectionNewMessageIcons.hidden = NO;
                }
            }
        }
    }
    else{
        static NSString *str = @"YiChatConnection_User";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatConnectionCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:YiChatConnectionVC_CellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
         [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:YiChatConnectionVC_CellH]];
        
        YiChatUserModel *model = [self getUserModelWithIndex:indexPath];
        cell.connectionNewMessageIcons.hidden = YES;
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
            cell.userModel = model;
        }
    }

    return cell;
}

- (NSString *)getKeyWithIndex:(NSInteger)sectionCurrent{
    NSInteger section = sectionCurrent- 1;
    if(section >= 0){
        NSArray *allKeys = [self getCharacterArrs];
        if(allKeys && [allKeys isKindOfClass:[NSArray class]]){
            if(allKeys.count > 0){
                if(allKeys.count - 1 >= section){
                    return allKeys[section];
                }
            }
        }
    }
    return nil;
}

- (YiChatUserModel *)getUserModelWithIndex:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section - 1;
    
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

- (ProjectCommonCellModel *)getCommonCellModelWithIndex:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *dataModel = nil;
    if(indexPath.section == 0 && indexPath.row <= (_toolCellData.count - 1)){
        ProjectCommonCellModel *model = _toolCellData[indexPath.row];
        if(model && [model isKindOfClass:[ProjectCommonCellModel class]]){
            dataModel = model;
        }
    }
    return dataModel;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if(indexPath.section == 0){
        NSString *title = nil;
        ProjectCommonCellModel *model = [self getCommonCellModelWithIndex:indexPath];
        if(model){
            title = model.titleStr;
            if([title isKindOfClass:[NSString class]]){
                if([title isEqualToString:@"添加好友"]){
                    [self pushVCWithName:@"YiChatAddFriendsVC"];
                }
                if([title isEqualToString:@"手机通讯录匹配"]){
                    [self pushVCWithName:@"YiChatContactMatchVC"];
                    
                }
                if([title isEqualToString:@"新的朋友"]){
                    [self pushVCWithName:@"YiChatCheckFriendsListVC"];
                    _isNewMessage = NO;
                    [ZFChatHelper zfChatHelper_cleanConnectionMessage];
                    
                    [[YiChatUserManager defaultManagaer] storageMessageNotifyDataWithChatId:YiChatNotify_FriendApply obj:@{}];
                }
                if([title isEqualToString:@"已保存的群聊"]){
                    [self pushVCWithName:@"YiChatGroupListVC"];
                }
                
                if([title isEqualToString:@"消息群发"]){
                
                    WS(weakSelf);
                    YiChatMassView *showView = [[YiChatMassView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 30, 250)];
                    showView.massBlock = ^(NSString * _Nonnull content, BOOL isSend) {
                        [weakSelf.popView dismiss:YES];
                        if(!isSend ){
                            return ;
                        }
                        
                        HTMessage *msg = [ZFChatMessageHelper sendTextMessage:content to:@"" messageType:@"1" messageExt:@{}];
                        
                        ZFMessageTransportVC *vc = [ZFMessageTransportVC initialVC];
                        vc.isShowSelecteAll = YES;
                        vc.hidesBottomBarWhenPushed = YES;
                        vc.chat = [[ZFChatConfigure alloc] initWithHTMsg:msg];
                        [weakSelf.navigationController pushViewController:vc animated:YES];
                        
                        
                    };
                    weakSelf.popView = [KLCPopup popupWithContentView:showView showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
                    [weakSelf.popView showAtCenter:CGPointMake(PROJECT_SIZE_WIDTH / 2, PROJECT_SIZE_HEIGHT / 2 - 100) inView:weakSelf.view];
                }
            }
        }
    }
    else{
        YiChatFriendInfoVC *friendInfo = [YiChatFriendInfoVC initialVC];
        friendInfo.model = [self getUserModelWithIndex:indexPath];
        friendInfo.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:friendInfo animated:YES];
    }
}

//进入编辑模式，按下出现的编辑按钮后
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView setEditing:NO animated:YES];
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        YiChatUserModel *model = [self getUserModelWithIndex:indexPath];
        
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
            
            [self deleteFriendWithModel:model index:indexPath];
        }
    }
}

- (void)deleteFriendWithModel:(YiChatUserModel *)model index:(NSIndexPath *)index{
    NSString *userId = [model getUserIdStr];
    
    NSString *key = [self getKeyWithIndex:index.section];
    
    NSDictionary *param = [ProjectRequestParameterModel getDeleteFriendParamWithFriendId:userId];
    
    [ProjectRequestHelper deleteFriendWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                
                YiChatConnectionModel *tmpModel = [self.model mutableCopy];
                
                YiChatConnectionModel *connectModel = [[YiChatUserManager defaultManagaer] deleteConectionModelData:self.model withFriendId:userId key:key];

                if(connectModel && [connectModel isKindOfClass:[YiChatConnectionModel class]]){
                    self.model = connectModel;
                }
                
                [self updateScections:index.section invocation:^{
                    
                    [[YiChatUserManager defaultManagaer] deleteConnectionFriends:@[userId] model:tmpModel invocation:^(YiChatConnectionModel * _Nonnull connectionModel, NSString * _Nonnull error) {
                        
                        if(connectionModel && [connectionModel isKindOfClass:[YiChatConnectionModel class]]){
                            self.model = connectionModel;
                        }
                    }];
                    
                    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull currentModel, NSString * _Nonnull error) {
                        
                        [ZFChatFriendHelper zfChatFriendHelperDeleteFriendApplyWithUserId:userId userInfo:[currentModel getOriginDic] completion:^(HTCmdMessage * _Nonnull cmdMSG, NSError * _Nonnull error) {
                            
                        }];
                    }];
                    
                    HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
                    [manager deleteOneChatterAllMessagesByChatterId:userId];
                    [manager deleteOneConversationWithChatterId:userId isCleanAllHistoryMessage:YES];
                }];
            }
            else if(obj && [obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

//修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    return @"  删   除   ";
}

-(UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath{
    if(indexPath.section != 0){
         return   UITableViewCellEditingStyleDelete;
    }
    else{
         return   UITableViewCellEditingStyleNone;
    }
}

//先要设Cell可编辑
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

//设置进入编辑状态时，Cell不会缩进
- (BOOL)tableView: (UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
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
