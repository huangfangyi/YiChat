//
//  ProjectSearchView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectSearchView.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchUntilies.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ProjectSearchMsgCell.h"
#import "HTClient.h"
#import "ProjectSearchMsgModel.h"
#import "ZFChatManage.h"
#import "ProjectSearchCell.h"
@interface ProjectSearchView ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,UIGestureRecognizerDelegate>
{
    BOOL _isSearching;
    BOOL _keyBoardIsShow;
    
}
@property (nonatomic)  CGRect destinationRect;
@property (nonatomic,strong) NSMutableArray <NSString *>*searchEvent;
@property (nonatomic,strong) NSMutableArray *dataSourceArr;
@property (nonatomic) ProjectSearchBarStyle searchStyle;

@property (nonatomic,strong) UIView *topView;
@property (nonatomic,strong) UIView *searchBar;
@property (nonatomic,strong) UITextField *searchContentInput;

@property (nonatomic,strong) UITableView *cTable;

@property (nonatomic,strong) dispatch_semaphore_t requestLock;
@property (nonatomic,strong) NSMutableArray *groupArr;

@property (nonatomic,copy) NSString *groupIdString;

@property (nonatomic,strong) NSArray *userConnection;

@property (nonatomic,assign) BOOL isLoadingUserConnection;

@property (nonatomic,strong) dispatch_semaphore_t searchConnectionLock;




@property (nonatomic,strong) NSMutableArray *friendArr;
@property (nonatomic,strong) NSMutableArray *groupNameArr;
    
@property (nonatomic,copy) HelperReturnInvocation getDataInvocation;
    
@property (nonatomic,strong) YiChatConnectionModel *connectionModel;


@end


#define ProjectSearchBar_SearchBarH 36.0f
@implementation ProjectSearchView

- (void)dealloc{
    [self removeKeyboardAppearNotify];
    [self removeKeyboardDisAppearNotify];
}

- (id)initWithFrame:(CGRect)frame style:(NSInteger)searchStyle{
    self = [super initWithFrame:CGRectMake(frame.origin.x, frame.origin.y + PROJECT_SIZE_NAVH, frame.size.width, frame.size.height)];
    if(self){
        _destinationRect = frame;
        _searchStyle = searchStyle;
        self.backgroundColor=[UIColor whiteColor];
        self.searchEvent = [NSMutableArray arrayWithCapacity:0];
        _dataSourceArr = [NSMutableArray arrayWithCapacity:0];
        self.requestLock = dispatch_semaphore_create(1);
        self.searchConnectionLock = dispatch_semaphore_create(1);
        self.friendArr = [NSMutableArray new];
        self.groupNameArr = [NSMutableArray new];
        self.isLoadingUserConnection = NO;
        
        [self registeKeyboardAppearNotify];
        [self registeKeyboardDisAppearNotify];
        
        [self animate_begin];
        
        [self makeUI];
        
        [_searchContentInput becomeFirstResponder];
        
        if (searchStyle == ProjectSearchBarStyleSearchMessage){
            _groupArr = [NSMutableArray new];
            [_groupArr addObjectsFromArray:[[HTClient sharedInstance].groupManager groups]];
            for (HTGroup *group in _groupArr) {
                if (self.groupIdString) {
                    self.groupIdString = [NSString stringWithFormat:@"%@,%@",self.groupIdString,group.groupId];
                }else{
                    self.groupIdString = group.groupId;
                }
            }
        }
    }
    return self;
}

- (void)getSearchOriginData:(id(^)(void))getDataInvocation{
    self.getDataInvocation = getDataInvocation;
}
    
- (void)animate_begin{
    WS(weakSelf);
    self.alpha = 0;
    [UIView animateWithDuration:0.38 animations:^{
        weakSelf.frame = weakSelf.destinationRect;
        weakSelf.alpha = 1;
    }];
}

- (void)animate_end:(ProjectSearchBarSearchViewInvocation)invocation{
    WS(weakSelf);
    [UIView animateWithDuration:0.3 animations:^{
        weakSelf.frame = CGRectMake(weakSelf.destinationRect.origin.x, weakSelf.destinationRect.origin.y + PROJECT_SIZE_NAVH, weakSelf.destinationRect.size.width, weakSelf.destinationRect.size.height);
        weakSelf.alpha = 0.3;
    } completion:^(BOOL finished) {
        if(finished){
            invocation();
        }
    }];
}

- (void)makeUI{
    [self addSubview:self.topView];
    
}

- (UIView *)topView{
    if(!_topView){
        _topView = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH, self.frame.size.width, PROJECT_SIZE_NAVH) backGroundColor:[UIColor whiteColor]];
        
        [_topView addSubview:self.searchBar];
        
        CGFloat x = _searchBar.frame.origin.x + _searchBar.frame.size.width;
        CGFloat h = _topView.frame.size.height;
        CGFloat w = _topView.frame.size.width - x;
        CGFloat y = 0;
        
        CGFloat btnW = w / 2;
        
        NSInteger condition = 2;
        
        /*
                   ProjectSearchBarStyleNone = -1,
                   ProjectSearchBarStyleSearchMessage = 0,
                   ProjectSearchBarStyleSearchConnection = 1,
                   ProjectSearchBarStyleSearchAddFriends = 2,
                   ProjectSearchBarStyleSearchCreateGroup,
                   ProjectSearchBarStyleSearchAddGroupMember,
                   ProjectSearchBarStyleSearchDeleteGroupMember,
                   ProjectSearchBarStyleSearchSetGroupManager,
                   ProjectSearchBarStyleSearchPersonCard
        */
        
        if(_searchStyle == ProjectSearchBarStyleSearchMessage || _searchStyle == ProjectSearchBarStyleSearchConnection){
            btnW = w;
            condition = 1;
        }
        
        for (int i = 0; i < 2; i ++) {
            
            UIButton *cancel = [ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(x + i * btnW, y,btnW , h) andBtnType:UIButtonTypeRoundedRect];
            [_topView addSubview:cancel];
            [cancel setTitleColor:PROJECT_COLOR_TEXTCOLOR_BLACK forState:UIControlStateNormal];
            cancel.tintColor = [UIColor clearColor];
            cancel.backgroundColor = [UIColor clearColor];
            [_topView addSubview:cancel];
            
            if(i == 0){
                [cancel setTitle:@"取消" forState:UIControlStateNormal];
                [cancel addTarget:self action:@selector(cancelBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
            }
            else{
                [cancel setTitle:@"确定" forState:UIControlStateNormal];
                [cancel addTarget:self action:@selector(sureBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
            }
            
        }
        
       
    }
    return _topView;
}

- (void)cancelBtnMethod:(UIButton *)btn{
    if(self.cancelClick){
        [self.searchContentInput resignFirstResponder];
        self.cancelClick();
    }
}

- (void)sureBtnMethod:(UIButton *)btn{
    [_searchContentInput resignFirstResponder];
       
       if(_searchStyle == ProjectSearchBarStyleSearchCreateGroup || _searchStyle == ProjectSearchBarStyleSearchAddGroupMember || _searchStyle == ProjectSearchBarStyleSearchDeleteGroupMember || _searchStyle == ProjectSearchBarStyleSearchSetGroupManager){
           if(self.projectSearchBarSearchResult){
               self.projectSearchBarSearchResult(self.connectionModel);
           }
       }
}

- (UIView *)searchBar{
    if(!_searchBar){
        
        UIView *searchBar = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(10.0, _topView.frame.size.height / 2 - ProjectSearchBar_SearchBarH / 2, 290.0 / 375.0 * _topView.frame.size.width - 15.0, ProjectSearchBar_SearchBarH) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
        _searchBar = searchBar;
        searchBar.layer.cornerRadius = 6.0;
        
        UIImage *searchIcon=[UIImage imageNamed:@"search@3x.png"];
        CGFloat blank = (10.0 / 345.0) * searchBar.frame.size.width;
        
        CGFloat x = blank;
        
        CGFloat searchIconH = 16.0 / 36.0 * searchBar.frame.size.height;
        CGFloat searchIconW = [ProjectHelper helper_GetWidthOrHeightIntoScale:searchIcon.size.width / searchIcon.size.height width:0 height:searchIconH];
        
        CGFloat y = (searchBar.frame.size.height - searchIconH) / 2;
        
        
        UIImageView *searchImg=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(x, y, searchIconW, searchIconH) andImg:searchIcon];
        [searchBar addSubview:searchImg];
        
        x = x * 2 + searchImg.frame.size.width;
        CGFloat w = searchBar.frame.size.width - x - blank;
        CGFloat h = 20.0;
        y = searchImg.frame.origin.y + searchImg.frame.size.height / 2 - h / 2;
        
        UITextField *searchTextInput = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(x, y, w, h) withPlaceholder:@"搜索" fontSize:[UIFont systemFontOfSize:14.0] isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
        [searchBar addSubview:searchTextInput];
        searchTextInput.returnKeyType = UIReturnKeySearch;
        _searchContentInput = searchTextInput;
        searchTextInput.delegate = self;
        
    }
    return _searchBar;
}


- (UITableView *)cTable{
    if(!_cTable){
        _cTable = [ProjectHelper helper_factoryMakeTableViewWithFrame:CGRectMake(0,  _topView.frame.origin.y + _topView.frame.size.height + 10.0, self.frame.size.width, self.frame.size.height - ( _topView.frame.origin.y + _topView.frame.size.height - 10.0)) backgroundColor:[UIColor whiteColor] style:UITableViewStyleGrouped bounces:YES pageEnabled:NO superView:self object:self];
    }
    return _cTable;
}

- (void)registeKeyboardAppearNotify{
    PROJECT_Method_KeyboardAddObserver;
}

- (void)removeKeyboardAppearNotify{
    PROJECT_Method_KeyboardRemoveObserver;
}

- (void)registeKeyboardDisAppearNotify{
    PROJECT_Method_KeyBoardDisappearObserver;
}

- (void)removeKeyboardDisAppearNotify{
    PROJECT_Method_KeyBoardDisappearRemoveObserver;
}

- (void)keyboardDidShow:(NSNotification *)notify{
    _keyBoardIsShow = YES;
    UIView *keyboad = [ProjectHelper helper_GetKeyboardView];
    NSDictionary* info = [notify userInfo];
    NSValue* aValue = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGSize keyboardSize= [aValue CGRectValue].size;
    
    if(keyboardSize.height == 0){
        keyboardSize = CGSizeMake(0, 0);
        
    }
    //[self.inputDelegate yrChatTextInputViewDidBeginEditeWithKeyBoardView:_cKeyBoardView keyboardSize:keyboardSize];
}

- (void)keyboardDidHidden:(NSNotification *)notify{
    //[self.inputDelegate yrChatTextInputViewDidEndEdite];
}

- (void)requestSearchContentWithText:(NSString *)content{
   
    if(_searchStyle == ProjectSearchBarStyleSearchAddFriends){
        [self searchAddFriends:content];
    }
    
    if(_searchStyle == ProjectSearchBarStyleSearchMessage){
        [self searchMessageContent:content];
    }
    
    if(_searchStyle == ProjectSearchBarStyleSearchConnection){
        [ProjectHelper helper_getGlobalThread:^{
            [self searchConnection:content];
        }];
    }
    if(_searchStyle == ProjectSearchBarStyleSearchCreateGroup || _searchStyle == ProjectSearchBarStyleSearchAddGroupMember){
        
        [self searchUerConnection:content];
    }
    if(_searchStyle == ProjectSearchBarStyleSearchDeleteGroupMember || _searchStyle == ProjectSearchBarStyleSearchSetGroupManager){
        
        [self searchUerConnection:content];
        
    }
    if(_searchStyle == ProjectSearchBarStyleSearchPersonCard){
        [ProjectHelper helper_getGlobalThread:^{
            [self searchConnection:content];
        }];
    }
}
    
- (void)searchUerConnection:(NSString *)text{
    if(!(text && [text isKindOfClass:[NSString class]])){
        return;
    }
    if(!self.connectionModel){
        id obj = self.getDataInvocation();
        
        
        if([obj isKindOfClass:[YiChatConnectionModel class]] && obj){
            YiChatConnectionModel *model = obj;
            
            self.connectionModel = model;
        }
        else{
            return;
        }
    }
    
    if(self.getDataInvocation && self.connectionModel && [self.connectionModel isKindOfClass:[YiChatConnectionModel class]]){
      
        
        if(_searchStyle == ProjectSearchBarStyleSearchCreateGroup){
            
            NSArray *usersModel = [self.connectionModel getUserModels];
            
            self.userConnection = usersModel;
        }
        else if(_searchStyle == ProjectSearchBarStyleSearchAddGroupMember){
            NSArray *usersModel = [self.connectionModel getUserModels];
            
            self.userConnection = usersModel;
        }
        
        else if(_searchStyle == ProjectSearchBarStyleSearchDeleteGroupMember){
            NSArray *usersModel = [self.connectionModel getUserModels];
            
            self.userConnection = usersModel;
        }
        
        else if(_searchStyle == ProjectSearchBarStyleSearchSetGroupManager){
            NSArray *usersModel = [self.connectionModel getUserModels];
            
            self.userConnection = usersModel;
        }
       
        
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < self.userConnection.count; i ++) {
            YiChatUserModel *moel = self.userConnection[i];
            if(moel && [moel isKindOfClass:[YiChatUserModel class]]){
                NSString *nick = [moel nickName];
                NSString *rank = [moel remarkName];
                
                BOOL isHas = NO;
                if(rank && [rank isKindOfClass:[NSString class]]){
                    if([rank rangeOfString:text].location != NSNotFound){
                        isHas = YES;
                    }
                }
                
                if(nick && [nick isKindOfClass:[NSString class]]){
                    if([nick rangeOfString:text].location != NSNotFound){
                        isHas = YES;
                    }
                }
                
                
                if(isHas == YES){
                    [tmp addObject:moel];
                }
            }
        }
        [self.dataSourceArr removeAllObjects];
        
        [self.dataSourceArr addObjectsFromArray:tmp];
        [ProjectHelper helper_getMainThread:^{
            [self.cTable reloadData];
        }];
    }
    
    
}

-(void)searchMessageContent:(NSString *)content{
    [self.dataSourceArr removeAllObjects];
    for (HTGroup *group in _groupArr) {
        if ([group.groupName rangeOfString:content].location != NSNotFound) {
            [self.groupNameArr addObject:group];
            YiChatUserModel *model = [YiChatUserModel new];
            model.avatar = group.groupAvatar;
            model.nick = group.groupName;
            
        }
    }
    
    [self singleChatMessages:content];
//    [self searchGoupeMessage:content];
}

-(void)searchGoupeMessage:(NSString *)content{
    NSLog(@"%@",[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token]);
    WS(weakSelf);
    NSDictionary *dic = [ProjectRequestParameterModel getSearchMessageListParametersWithSearchContent:content groupId:@""];
    [ProjectRequestHelper searchMsgListWithParameters:dic headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            if([obj isKindOfClass:[NSDictionary class]]){
                
                id data = obj[@"data"];

                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dic = (NSDictionary *)obj;
                    NSArray *arr = dic[@"data"];
                    NSMutableArray *groupArr = [NSMutableArray new];
                    NSMutableDictionary *gDic = [NSMutableDictionary new];
                    for (NSDictionary *dataDic in arr) {
                        HTMessage *msg = [[ZFChatManage defaultManager] translateRequestHttpDataToHTMessage:dataDic];
                        if (msg) {
                            gDic[msg.to] = @"1";
                            [groupArr addObject:msg];
                        }
                    }
                    
                    NSMutableArray *newGarr = [NSMutableArray new];
                    for (NSString *gID in gDic.allKeys) {
                        NSMutableArray *gArr = [NSMutableArray new];
                        for (HTMessage *msg in groupArr) {
                            if ([gID isEqualToString:msg.to]) {
                                [gArr addObject:msg];
                            }
                        }
                        [newGarr addObject:gArr.copy];
                    }

                    NSArray *msgArr = weakSelf.dataSourceArr.copy;
                    for (NSInteger i = 0; i < msgArr.count; i++) {
                        HTMessage *msg = msgArr[i];
                        //from 发送者   to:接受者
                        NSLog(@"to:%@  from:%@",msg.to,msg.from);
                        if ([msg.chatType isEqualToString:@"2"]) {
                            [self.dataSourceArr removeObject:msg];
                        }
                    }
                    
                    YiChatUserModel *userModel = [YiChatUserModel mj_objectWithKeyValues:[[YiChatUserManager defaultManagaer] getCashUserDicInfo]];
                    NSString *oldUid = @"";
                    NSArray *singleChatArr = weakSelf.dataSourceArr.copy;
                    [self.dataSourceArr removeAllObjects];
                    [self.dataSourceArr addObjectsFromArray:newGarr];
                    for (HTMessage *msg in singleChatArr) {
                        NSString *uid = @"";
                        if (msg.to.integerValue != userModel.userId) {
                            uid = msg.to;
                            if ([uid isEqualToString:oldUid]) {
                                break;
                            }else{
                                oldUid = uid;
                            }
                        }else{
                            uid = msg.from;
                            uid = msg.to;
                            if ([uid isEqualToString:oldUid]) {
                                break;
                            }else{
                                oldUid = uid;
                            }
                        }
                        NSMutableArray *array = [NSMutableArray new];
                        for (HTMessage *m in singleChatArr) {
                            if ([uid isEqualToString:m.to] || [uid isEqualToString:m.from]) {
                                [array addObject:m];
                            }
                        }
                        [weakSelf.dataSourceArr addObject:array];
                    }
                    [weakSelf searchFriend:content];
//                    dispatch_async(dispatch_get_main_queue(), ^{
//                        [weakSelf.cTable reloadData];
//                    });

                }
                else if([data isKindOfClass:[NSArray class]]){
                    if(data){
                        if(weakSelf.projectSearchBarSearchResult){
                            weakSelf.projectSearchBarSearchResult(data);
                        }
                    }
                }
                return;
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];

                return ;
            }
            
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
    
}

-(void)singleChatMessages:(NSString *)content{
    YiChatUserModel *user = [YiChatUserModel mj_objectWithKeyValues:[[YiChatUserManager defaultManagaer] getCashUserDicInfo]];
    [[HTClient sharedInstance].messageManager getSingleChatMessagesWithContent:content from:[NSString stringWithFormat:@"%ld",user.userId] to:nil timestamp:-1 completion:^(NSArray<HTMessage *> *resArr) {
        [self.dataSourceArr addObjectsFromArray:resArr];
        [[HTClient sharedInstance].messageManager getSingleChatMessagesWithContent:content from:nil to:[NSString stringWithFormat:@"%ld",user.userId] timestamp:-1 completion:^(NSArray<HTMessage *> *resArr) {
            [self.dataSourceArr addObjectsFromArray:resArr];
            [self searchGoupeMessage:content];
        }];
    }];
}

- (void)searchAddFriends:(NSString *)content{
    if(![content isKindOfClass:[NSString class]] && content.length <= 0){
        return;
    }
    
    [ProjectHelper helper_getGlobalThread:^{
        WS(weakSelf);
        dispatch_semaphore_wait(self.requestLock, DISPATCH_TIME_FOREVER);
        
          NSDictionary *dic = [ProjectRequestParameterModel searchUserParamWithContent:content];
        
        [ProjectRequestHelper searchUserWithParameters:dic headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
                if([obj isKindOfClass:[NSDictionary class]]){
                    
                    id data = obj[@"data"];
                    if([data isKindOfClass:[NSDictionary class]]){
                        if(data){
                            if(self.projectSearchBarSearchResult){
                                self.projectSearchBarSearchResult(data);
                            }
                        }
                    }
                    else if([data isKindOfClass:[NSArray class]]){
                        if(data){
                            if(self.projectSearchBarSearchResult){
                                self.projectSearchBarSearchResult(data);
                            }
                        }
                    }
                    dispatch_semaphore_signal(weakSelf.requestLock);
                    
                    return;
                }
                else if([obj isKindOfClass:[NSString class]]){
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                     dispatch_semaphore_signal(weakSelf.requestLock);
                    return ;
                }
                dispatch_semaphore_signal(weakSelf.requestLock);
                
            }];
           
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            dispatch_semaphore_signal(weakSelf.requestLock);
        }];
    }];
}

-(void)searchFriend:(NSString *)text{
    WS(weakSelf);
    if(!(text && [text isKindOfClass:[NSString class]])){
        return;
    }
    
    dispatch_semaphore_wait(self.searchConnectionLock, DISPATCH_TIME_FOREVER);
    [self loadUserConnectionHandle:^(BOOL isSuccess) {
        if(isSuccess && weakSelf.userConnection){
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0; i < weakSelf.userConnection.count; i ++) {
                YiChatUserModel *moel = weakSelf.userConnection[i];
                if(moel && [moel isKindOfClass:[YiChatUserModel class]]){
                    NSString *nick = [moel nickName];
                    NSString *rank = [moel remarkName];
                    
                    BOOL isHas = NO;
                    if(nick && [nick isKindOfClass:[NSString class]]){
                        if([nick rangeOfString:text].location != NSNotFound){
                            isHas = YES;
                        }
                    }
                    if(rank && [rank isKindOfClass:[NSString class]]){
                        if([nick rangeOfString:text].location != NSNotFound){
                            isHas = YES;
                        }
                    }
                    
                    if(isHas == YES){
                        [tmp addObject:moel];
                    }
                }
            }
            [weakSelf.friendArr removeAllObjects];
            
            [weakSelf.friendArr addObjectsFromArray:tmp];
            [ProjectHelper helper_getMainThread:^{
                [weakSelf.cTable reloadData];
            }];
            
        }
        
        dispatch_semaphore_signal(weakSelf.searchConnectionLock);
    }];
}

- (void)searchConnection:(NSString *)text{
    WS(weakSelf);
    if(!(text && [text isKindOfClass:[NSString class]])){
        return;
    }
    
    dispatch_semaphore_wait(self.searchConnectionLock, DISPATCH_TIME_FOREVER);
    
    
    [self loadUserConnectionHandle:^(BOOL isSuccess) {
       
        if(isSuccess && weakSelf.userConnection){
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < weakSelf.userConnection.count; i ++) {
                YiChatUserModel *moel = weakSelf.userConnection[i];
                if(moel && [moel isKindOfClass:[YiChatUserModel class]]){
                    NSString *nick = [moel nickName];
                    NSString *rank = [moel remarkName];
                    
                    BOOL isHas = NO;
                    if(rank && [rank isKindOfClass:[NSString class]]){
                        if([rank rangeOfString:text].location != NSNotFound){
                            isHas = YES;
                        }
                    }
                    
                    if(nick && [nick isKindOfClass:[NSString class]]){
                        if([nick rangeOfString:text].location != NSNotFound){
                            isHas = YES;
                        }
                    }
                   
                    
                    if(isHas == YES){
                        [tmp addObject:moel];
                    }
                }
            }
            [weakSelf.dataSourceArr removeAllObjects];
            
            [weakSelf.dataSourceArr addObjectsFromArray:tmp];
            [ProjectHelper helper_getMainThread:^{
               [weakSelf.cTable reloadData];
            }];
            
        }
        
         dispatch_semaphore_signal(weakSelf.searchConnectionLock);
    }];
}

- (void)loadUserConnectionHandle:(void(^)(BOOL isSuccess))invocation{
    WS(weakSelf);
    if(_isLoadingUserConnection){
        invocation(NO);
        return;
    }
    _isLoadingUserConnection = YES;
    
    [[YiChatUserManager defaultManagaer] fetchUserConnectionInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        
        weakSelf.isLoadingUserConnection = NO;
        
        NSArray *usersModel = [model getUserModels];
        
        weakSelf.userConnection = usersModel;
        
        invocation(YES);
    }];
}

#pragma mark delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    if(textField == _searchContentInput){
        
        __block NSString *text = [textField.text mutableCopy];
        
        [ProjectHelper helper_getGlobalThread:^{
             [self requestSearchContentWithText:text];
        }];
        
        [textField resignFirstResponder];
        textField.text = @"";
    }
    return YES;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    if (self.searchStyle == ProjectSearchBarStyleSearchMessage)
        return 3;
    else
        return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (self.searchStyle == ProjectSearchBarStyleSearchMessage){
        if (section == 0) {
            return self.groupNameArr.count;
        }else if (section == 1){
            return self.friendArr.count;
        }else{
            return self.dataSourceArr.count;
        }
    }else{
        return _dataSourceArr.count;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    if(_searchStyle == ProjectSearchBarStyleSearchMessage){
        
        if (indexPath.section == 2) {
            ProjectSearchMsgCell *msgCell = [tableView dequeueReusableCellWithIdentifier:@"msgCell"];
            if (msgCell == nil) {
                msgCell = [[ProjectSearchMsgCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"msgCell"];
            }
            msgCell.dataArr = self.dataSourceArr[indexPath.row];
            return msgCell;
        }else{
            static NSString *str = @"searchConnection";
            ProjectSearchCell *searchCell = [self getCellWithTableView:tableView connectionCellStyle:ProjectSearchBarStyleSearchConnection reuseIdentifier:str indexPath:indexPath isHasDownLine:YES isHasRightArrow:NO];
            
            [searchCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:[self getTableCellHWithIndex:indexPath]]];
            
            if (indexPath.section == 0) {
                HTGroup *group = self.groupNameArr[indexPath.row];
                YiChatUserModel *model = [YiChatUserModel new];
                model.avatar = group.groupAvatar;
                model.nick = group.groupName;
                searchCell.userModel = model;
            }else{
                searchCell.userModel = self.friendArr[indexPath.row];
            }
            
            return searchCell;
        }
    }
    else if(_searchStyle == ProjectSearchBarStyleSearchConnection || _searchStyle == ProjectSearchBarStyleSearchPersonCard){
         static NSString *str = @"searchConnection";
        ProjectSearchCell *searchCell = [self getCellWithTableView:tableView connectionCellStyle:0 reuseIdentifier:str indexPath:indexPath isHasDownLine:YES isHasRightArrow:NO];
        
        [searchCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:[self getTableCellHWithIndex:indexPath]]];
        
        if(self.dataSourceArr.count - 1 >= indexPath.row){
            searchCell.userModel = self.dataSourceArr[indexPath.row];
        }
        
        return searchCell;
    }
    else if(_searchStyle == ProjectSearchBarStyleSearchCreateGroup || _searchStyle == ProjectSearchBarStyleSearchAddGroupMember || _searchStyle == ProjectSearchBarStyleSearchDeleteGroupMember || _searchStyle == ProjectSearchBarStyleSearchSetGroupManager){
        static NSString *str = @"connectionDealSelecte";
        ProjectSearchCell *searchCell = [self getCellWithTableView:tableView connectionCellStyle:1 reuseIdentifier:str indexPath:indexPath isHasDownLine:YES isHasRightArrow:NO];
        
        [searchCell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:[self getTableCellHWithIndex:indexPath]]];
        
        if(self.dataSourceArr.count - 1 >= indexPath.row){
            searchCell.userModel = self.dataSourceArr[indexPath.row];
        }
        
        searchCell.ProjectSearchCellSelecte = ^(YiChatUserModel * _Nonnull model, BOOL selecteState) {
            
        };
        
        return searchCell;
    }
    
    UITableViewCell *cell =[UITableViewCell new];
    return cell;
}

- (ProjectSearchCell *)getCellWithTableView:(UITableView *)table connectionCellStyle:(NSInteger)cellStyle reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath isHasDownLine:(BOOL)isHasDownLine isHasRightArrow:(BOOL)isHasRightArrow{
    
    ProjectSearchCell *cell = [table dequeueReusableCellWithIdentifier:reuseIdentifier];
    if(!cell){
        CGFloat cellH = [self getTableCellHWithIndex:indexPath] ;
        
        cell = [ProjectSearchCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:_cTable.frame.size.width] isHasDownLine:[NSNumber numberWithBool:isHasDownLine] type:cellStyle];
    }
    return  cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    if (self.searchStyle == ProjectSearchBarStyleSearchMessage){
        UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, _cTable.frame.size.width, 30) backGroundColor:[UIColor whiteColor]];
        NSArray *arr = @[@"相关群聊",@"相关联系人",@"聊天记录"];
        UILabel *la = [[UILabel alloc] initWithFrame:CGRectMake(15, 0, 200, 30)];
        la.text = arr[section];
        la.font = [UIFont systemFontOfSize:14];
        [back addSubview:la];
        return back;
    }
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, _cTable.frame.size.width, 0) backGroundColor:[UIColor whiteColor]];
    
    return back;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return [self getTableCellHWithIndex:indexPath];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    if (self.searchStyle == ProjectSearchBarStyleSearchMessage){
        return 30;
    }
    return CGFLOAT_MIN;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return [self getTableFooterHWithSection:section];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if(_searchStyle == ProjectSearchBarStyleSearchMessage){
        NSMutableDictionary *dic = [NSMutableDictionary new];
        dic[@"type"] = [NSString stringWithFormat:@"%ld",indexPath.section];
        if (indexPath.section == 0) {
            HTGroup *group = self.groupNameArr[indexPath.row];
            dic[@"data"] = group.groupId;
        }else if (indexPath.section == 1){
            YiChatUserModel *model = self.friendArr[indexPath.row];
            dic[@"data"] = [NSString stringWithFormat:@"%ld",model.userId];
        }else{
            dic[@"data"] = self.dataSourceArr[indexPath.row];
        }
        self.cellClick(ProjectSearchBarStyleSearchMessage, dic.copy);
    }
    if(_searchStyle == ProjectSearchBarStyleSearchConnection){
        if(self.dataSourceArr.count - 1 >=  indexPath.row){
            self.cellClick(ProjectSearchBarStyleSearchConnection, self.dataSourceArr[indexPath.row]);
        }
    }
    if(_searchStyle == ProjectSearchBarStyleSearchPersonCard){
        [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"确认选择?" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
            if(row == 0){
                self.cellClick(ProjectSearchBarStyleSearchPersonCard, self.dataSourceArr[indexPath.row]);
            }
        }];
    }
}

- (CGFloat)getTableCellHWithIndex:(NSIndexPath *)index{
    return 60;
}

- (CGFloat)getTableHeaderHWithSection:(NSInteger)section{
    return 0.0001f;
}

- (CGFloat)getTableFooterHWithSection:(NSInteger)section{
    return 0;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_searchContentInput resignFirstResponder];
    
    if(_searchStyle == ProjectSearchBarStyleSearchCreateGroup || _searchStyle == ProjectSearchBarStyleSearchAddGroupMember || _searchStyle == ProjectSearchBarStyleSearchDeleteGroupMember || _searchStyle == ProjectSearchBarStyleSearchSetGroupManager){
        if(self.projectSearchBarSearchResult){
            self.projectSearchBarSearchResult(self.connectionModel);
        }
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
