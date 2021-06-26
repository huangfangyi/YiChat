
//
//  YiChatCreateGroupVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatCreateGroupVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchBarView.h"
#import "YiChatGroupSelectePersonView.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "YiChatConnectionModel.h"
#import "YiChatPhoneConnectionIndexView.h"
#import "YiChatGroupSelectePersonCell.h"
#import "ZFChatHelper.h"
@interface YiChatCreateGroupVC ()<UIGestureRecognizerDelegate,UIScrollViewDelegate>

{
  
}

@property (nonatomic,assign) NSInteger currentPage;
@property (nonatomic,assign) BOOL isFetchConversation;

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) YiChatGroupSelectePersonView *selectePerson;

@property (nonatomic,strong) YiChatConnectionModel *model;

@property (nonatomic,strong) YiChatPhoneConnectionIndexView *indexView;

@property (nonatomic,strong) NSMutableArray <YiChatUserModel *>*selelectPersonContain;

@property (nonatomic,strong) dispatch_semaphore_t selectePersonLock;

@property (nonatomic,strong) UITextField *groupNameInput;

@property (nonatomic,assign) BOOL isCreate;



@end

@implementation YiChatCreateGroupVC

+ (id)initialVC{
    YiChatCreateGroupVC *createGroup = [YiChatCreateGroupVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"createGroup") leftItem:nil rightItem:@"确定"];
    return createGroup;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _selelectPersonContain = [NSMutableArray arrayWithCapacity:0];
    self.selectePersonLock = dispatch_semaphore_create(1);
    self.view.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    _isCreate = NO;
    
    [self.view addSubview:self.searchBar];
    
    [self.view addSubview:self.selectePerson];
    
    [self makeTable];
    
    [self loadConnectionData];
    // Do any additional setup after loading the view.
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        WS(weakSelf);
        
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder = PROJECT_TEXT_LOCALIZE_NAME(@"connectionMainSearchPlaceHolder");
        [_searchBar initialSearchType:ProjectSearchBarViewPageStyleSearchPageSearch];
        [_searchBar initialSearchStyle:3];
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
    
- (YiChatGroupSelectePersonView *)selectePerson{
    if(!_selectePerson){
        _selectePerson = [[YiChatGroupSelectePersonView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH,0, ProjectUIHelper_SearchBarH)];
        _selectePerson.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    return _selectePerson;
}


- (void)makeTable{
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:0]];
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,self.searchBar.frame.origin.y + self.searchBar.frame.size.height, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (self.searchBar.frame.origin.y + self.searchBar.frame.size.height)  - PROJECT_SIZE_SafeAreaInset.bottom);
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
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    
    [self keybordResign];
    
//    if(_groupNameInput.text.length == 0){
//        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入群名称"];
//        return;
//    }
   
    
    if(_selelectPersonContain.count < 2){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组人数不能少于3人"];
        return;
    }
    if(_selelectPersonContain.count > YiChatProject_CreateGroupNum){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"群组人数不能大于%d人",YiChatProject_CreateGroupNum]];
        
        return;
    }
    
    if(!_isCreate){
        _isCreate = YES;
    }
    else{
        return;
    }
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    NSArray *members = [self getSelecteMembers];
    
    NSString *name = [YiChatUserManager disable_emoji:[self getGroupName]];

    HTGroup *group = [HTGroup new];
    group.groupDescription = @"";
    group.groupAvatar = @"";
    group.groupName = name;
    group.owner = YiChatUserInfo_UserIdStr;
    
    if([members isKindOfClass:[NSArray class]] && members.count >= 2){
        WS(weakSelf);
        [ZFGroupHelper createGroup:group withMembers:members success:^(HTGroup * _Nonnull aGroup) {
            
            weakSelf.isCreate = NO;
            [ProjectRequestHelper progressHidden:progress];
            
            if(aGroup){
    
                if(weakSelf.createEndStyle == 0){
                    [ProjectHelper helper_getMainThread:^{
                        [self.navigationController popViewControllerAnimated:YES];
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组创建成功"];
                    }];
                   
                    return ;
                }
            }
        } failure:^(NSError * _Nonnull error) {
             weakSelf.isCreate = NO;
             [ProjectRequestHelper progressHidden:progress];
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
        }];
    }
}

- (NSString *)getGroupName{
    NSMutableArray *selectepersonNameArr = [NSMutableArray arrayWithCapacity:0];
    NSArray *selecteMembers = self.selelectPersonContain;
    
    NSInteger num = 0;
    NSInteger length = 0;
    
    for (int i = 0; i < selecteMembers.count ; i ++) {
        if(selecteMembers.count - 1 >= i){
            YiChatUserModel *model = selecteMembers[i];
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                NSString *nick = [model nickName];
                if(nick && [nick isKindOfClass:[NSString class]]){
                    [selectepersonNameArr addObject:nick];
                    
                    if(length < YiChatProject_Group_GroupNameLimitLength){
                        if((length + nick.length + 1) <= YiChatProject_Group_GroupNameLimitLength){
                            length += (nick.length + 1);
                            num = i;
                        }
                        else{
                            if(length == 0){
                                length += (nick.length + 1);
                                num = i;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    if(selectepersonNameArr.count > 0 && (selectepersonNameArr.count - 1 >= num)){
        if(num != 0){
            NSArray *arr =  [selectepersonNameArr subarrayWithRange:NSMakeRange(0, num)];
            if(arr.count > 0){
                return  [arr componentsJoinedByString:@","];
            }
        }
        else{
            return selectepersonNameArr.firstObject;
        }
      
    }
    return @"";
}

- (NSArray *)getSelecteMembers{
    NSMutableArray * members = @[].mutableCopy;
    for (YiChatUserModel * entity in self.selelectPersonContain) {
        [members addObject:[NSString stringWithFormat:@"%ld",entity.userId]];
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
            
            [weakSelf addUserModelState];
            
            [weakSelf tableUpdate];
            
            return ;
        }
        else if(error && [error isKindOfClass:[NSString class]]){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            return;
        }
    }];
}

- (void)addUserModelState{
    for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
            [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                if([obj isKindOfClass:[YiChatUserModel class]]){
                    YiChatUserModel *user = obj;
                    
                    objc_setAssociatedObject(user, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                    objc_setAssociatedObject(user, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                }
            }];
        }
    }
}

- (void)tableUpdate{
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    
  //  [tmp addObject:[NSNumber numberWithInteger:1]];
    
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
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
        [self updateIndexView];
    }];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(section == 0){
        return 0.00001;
    }
    else{
        return 30.0;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
      UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    if(section == 0){
        return back;
    }
    else{
      
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
    YiChatGroupSelectePersonCell *cell = nil;
    
    if(indexPath.section >= 0){
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
    }
    
    return cell;
}

- (void)changeSelecteNum{
    UILabel *lab = nil;
    if(self.selelectPersonContain.count > 0){
        lab = [self navBarGetCenterBarItem];
        if([lab isKindOfClass:[UILabel class]] && lab){
            lab.text = [NSString stringWithFormat:@"%@(%ld)",PROJECT_TEXT_LOCALIZE_NAME(@"createGroup"),self.selelectPersonContain.count];
        }
    }
    else{
        lab = [self navBarGetCenterBarItem];
        if([lab isKindOfClass:[UILabel class]] && lab){
            lab.text = PROJECT_TEXT_LOCALIZE_NAME(@"createGroup");
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
                        
                        [UIView animateWithDuration:0.4 animations:^{
                             weakSelf.searchBar.frame = CGRectMake(x, weakSelf.searchBar.frame.origin.y, weakSelf.view.frame.size.width - x, weakSelf.searchBar.frame.size.height);
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
                        
                        [UIView animateWithDuration:0.4 animations:^{
                            weakSelf.searchBar.frame = CGRectMake(x, weakSelf.searchBar.frame.origin.y, weakSelf.view.frame.size.width - x, weakSelf.searchBar.frame.size.height);
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
                                    [self.selelectPersonContain addObject:model];
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
    [_groupNameInput resignFirstResponder];
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
