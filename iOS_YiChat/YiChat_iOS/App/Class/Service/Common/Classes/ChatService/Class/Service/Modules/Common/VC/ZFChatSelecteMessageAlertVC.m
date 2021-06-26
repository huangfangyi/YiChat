//
//  ZFChatSelecteMessageAlertVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatSelecteMessageAlertVC.h"
#import "ZFChatGlobal.h"
#import "ZFConnectionIndexView.h"
#import "ZFConnectionModel.h"
#import "ZFSelectePersonCardCell.h"
#import "ZFRequestManage.h"
#import "ZFGroupHelper.h"
#import "ZFMessageGroupTransportVC.h"

#import "ZFChatConfigure.h"
#import "ZFTransportPresenter.h"
#import "ZFChatHelper.h"
#import "ProjectSearchBarView.h"

@interface ZFChatSelecteMessageAlertVC ()

@property (nonatomic,strong) ZFConnectionIndexView *indexView;

@property (nonatomic,strong) ZFConnectionModel *model;

@property (nonatomic,strong) NSArray *canontSelecteItemsArr;

@property (nonatomic,assign) BOOL isShowAlertAll;

@end

@implementation ZFChatSelecteMessageAlertVC

+ (id)initialVC{
    ZFChatSelecteMessageAlertVC *transport = [ZFChatSelecteMessageAlertVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson") leftItem:nil rightItem:nil];
    return transport;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if(self.groupPower > 0){
        _isShowAlertAll = YES;
    }
    else{
        _isShowAlertAll = NO;
    }
    
    [self makeTable];
    
    [self loadData];
    // Do any additional setup after loading the view.
}

- (void)loadData{
    [self loadListData:^(NSArray * _Nonnull originDataArr, NSArray * _Nonnull listArr) {
        if(listArr && [listArr isKindOfClass:[NSArray class]] && originDataArr && [originDataArr isKindOfClass:[NSArray class]]){
            
            self.model = [[ZFConnectionModel alloc] init];
            self.model.originDataArr = originDataArr;
            if(listArr.count > 0){
                
                NSMutableArray *listDicArr = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < listArr.count; i ++) {
                    NSDictionary *dic = listArr[i];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        NSString *key = dic.allKeys.lastObject;
                        
                        if(key && [key isKindOfClass:[NSString class]]){
                            NSArray *userArr = dic[key];
                            NSMutableArray *userDicArr = [NSMutableArray arrayWithCapacity:0];
                            for (int j = 0; j < userArr.count; j ++) {
                                id obj = userArr[j];
                                if(obj){
                                    NSDictionary *tmp = [ProjectBaseModel translateObjPropertyToDic:obj];
                                    
                                    if(tmp && [tmp isKindOfClass:[NSDictionary class]]){
                                        [userDicArr addObject:tmp];
                                    }
                                }
                            }
                            [listDicArr addObject:@{key:userDicArr}];
                        }
                    }
                }
                self.model.connectionModelArr = listDicArr;
            }
            
            [self loadCanNotSelecteData:^(NSArray *cannotSelecteDataArr) {
                self.canontSelecteItemsArr = cannotSelecteDataArr;
                [self dealDataSourceCanNotSelecte];
                [self tableUpdate];
            }];
        }
    }];
}

- (void)loadListData:(void(^)(NSArray *originDataArr,NSArray *listArr))invocation{
    [self loadConection:invocation];
}

- (void)loadConection:(void(^)(NSArray *originDataArr,NSArray *listArr))invocation{
    
    NSString *groupId = @"";
    if(self.groupId && [self.groupId isKindOfClass:[NSString class]]){
        groupId = self.groupId;
    }
    
    [[YiChatUserManager defaultManagaer] fetchGroupMemberslistWithGroupId:groupId invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0; i < groupMemberlist.count; i ++) {
                YiChatUserModel *model = groupMemberlist[i];
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    NSDictionary *dic = [ProjectBaseModel translateObjPropertyToDic:model];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        [tmp addObject:dic];
                    }
                }
            }
            if(tmp){
                YiChatConnectionModel *model = [[YiChatConnectionModel alloc] initWithUsersArr:tmp];
                if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                    invocation(model.originDataArr,model.connectionModelArr);
                    return ;
                }
            }
        }
        invocation(nil,nil);
    }];
}

- (void)loadCanNotSelecteData:(void(^)(NSArray *cannotSelecteDataArr))invocation{
    invocation(nil);
}

- (NSString *)getCanNotSelecteItemIdWithIdex:(NSInteger)index{
    if(self.canontSelecteItemsArr && [self.canontSelecteItemsArr isKindOfClass:[NSArray class]]){
        if(self.canontSelecteItemsArr.count - 1 >= index){
            return [self getListItemIdWithListData:self.canontSelecteItemsArr[index]];
        }
    }
    return nil;
}

- (NSString *)getlistItemIdWithIdex:(NSInteger)index{
    if(self.model && [self.model isKindOfClass:[ZFConnectionModel class]]){
        if(self.model.connectionModelArr && [self.model.connectionModelArr isKindOfClass:[NSArray class]]){
            if(self.model.connectionModelArr.count - 1 >= index){
                return [self getListItemIdWithListData:self.model.connectionModelArr[index]];
            }
        }
    }
    return nil;
}

- (NSString *)getListItemIdWithListData:(NSDictionary *)data{
    if(data && [data isKindOfClass:[NSDictionary class]]){
        if([data.allKeys containsObject:@"userId"]){
            if([data[@"userId"] isKindOfClass:[NSNumber class]] && data[@"userId"]){
                return [NSString stringWithFormat:@"%ld",[data[@"userId"] integerValue]];
            }
        }
        return data[@""];
    }
    return nil;
}

- (NSDictionary *)getItemDataWithIndex:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section - 1;
    
    if(!_isShowAlertAll){
        section = indexPath.section;
    }
    
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

- (void)dealDataSourceCanNotSelecte{
    for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
            
            [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                
                if([obj isKindOfClass:[NSDictionary class]]){
                    
                    __block BOOL ishas = NO;
                    
                    dispatch_apply(self.canontSelecteItemsArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t j ) {
                        NSString *listItemId = [self getlistItemIdWithIdex:idx];
                        NSString *cannotSelecteItemId = [self getCanNotSelecteItemIdWithIdex:j];
                        
                        if(listItemId && [listItemId isKindOfClass:[NSString class]] ){
                            if(cannotSelecteItemId && [cannotSelecteItemId isKindOfClass:[NSString class]]){
                                if([listItemId isEqualToString:cannotSelecteItemId]){
                                    ishas = YES;
                                }
                            }
                        }
                    });
                    
                    if(ishas == YES){
                        objc_setAssociatedObject(obj, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        //selecteState
                        objc_setAssociatedObject(obj, @"selecteState", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                    }
                    else{
                        
                        objc_setAssociatedObject(obj, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        objc_setAssociatedObject(obj, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                    }
                }
            }];
        }
    }
}



- (void)makeTable{
    self.sectionsRowsNumSet = @[];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom);
}

//- (ProjectSearchBarView *)searchBar{
//    if(!_searchBar){
//        WS(weakSelf);
//        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
//        _searchBar.placeHolder = PROJECT_TEXT_LOCALIZE_NAME(@"connectionMainSearchPlaceHolder");
//        [_searchBar initialSearchType:1];
//        [_searchBar initialSearchStyle:7];
//        [_searchBar createUI];
//        _searchBar.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
//            if(obj && [obj isKindOfClass:[YiChatUserModel class]]){
//                if(weakSelf.zfPersonCardSelecte){
//                    weakSelf.zfPersonCardSelecte(obj);
//                }
//
//                [weakSelf dismissViewControllerAnimated:YES completion:nil];
//            }
//
//        };
//    }
//    return _searchBar;
//}

- (void)tableUpdate{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    if(_isShowAlertAll){
        [tmp addObject:[NSNumber numberWithInteger:0]];
    }
    
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


- (ZFConnectionIndexView *)indexView{
    if(!_indexView){
        
        NSArray *characters = [self getCharacterArrs];
        
        WS(weakSelf);
        
        _indexView = [[ZFConnectionIndexView alloc] initWithData:characters bgView:self.view];
        _indexView.zfIndexViewClick = ^(NSInteger clickIndex) {
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
        if([dic isKindOfClass:[NSDictionary class]]){
            id key = dic.allKeys.lastObject;
            if(key){
                [arr addObject:key];
            }
        }
    }
    return arr;
}

- (NSString *)getKeyWithIndex:(NSInteger)sectionCurrent{
    NSInteger section = sectionCurrent - 1;
    if(!_isShowAlertAll){
           section = sectionCurrent;
       }
    
    if(section <= (_model.connectionModelArr.count - 1)){
        NSDictionary *dic = _model.connectionModelArr[section];
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            return dic.allKeys.lastObject;
        }
        
    }
    return nil;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(_isShowAlertAll){
        if(section == 0){
               return ProjectUIHelper_SearchBarH;
        }
    }
    return 30.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    
    if(_isShowAlertAll){
        if(section == 0){
            back.backgroundColor = [UIColor whiteColor];
            
            UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(18.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
            [back addSubview:lab];
            lab.text = @"全体成员";
            
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            btn.frame = lab.frame;
            [btn addTarget:self action:@selector(messageAlertAll:) forControlEvents:UIControlEventTouchUpInside];
            [back addSubview:btn];
            
        }
        else{
            back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
            
            UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
            [back addSubview:lab];
            NSString *key = [self getKeyWithIndex:section];
            if([key isKindOfClass:[NSString class]] && key){
                lab.text = key;
            }
        }
    }
    else{
        back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
                   
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        [back addSubview:lab];
        NSString *key = [self getKeyWithIndex:section];
        if([key isKindOfClass:[NSString class]] && key){
            lab.text = key;
        }
    }
    
    
    return back;
}

- (void)messageAlertAll:(UIButton *)btn{
    YiChatUserModel *model = [YiChatUserModel new];
    model.userId = -1;
    model.nick = @"全体成员";
    
    WS(weakSelf);
    [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"确认选择?" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
        
        if(row == 0){
            if(self.zfPersonCardSelecte){
                self.zfPersonCardSelecte(model);
            }
            
            [self.navigationController popViewControllerAnimated:YES];
        }
    }];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ZFSelectePersonCardCell *cell = nil;
    
    NSInteger num = 0;
    if(!_isShowAlertAll){
        num = -1;
    }
    if(indexPath.section != num){
        static NSString *str = @"YiChatGroupListCell_SelectePersonCardItemList";
          
          CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
          
          
          cell = [tableView dequeueReusableCellWithIdentifier:str];
          
          if(!cell){
              cell = [ZFSelectePersonCardCell  initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.cTable.frame.size.width] isHasDownLine:[NSNumber numberWithFloat:YES] type:0];
          }
          
          [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
          
          cell.cellModel = [self getItemDataWithIndex:indexPath];
    }
    else{
        cell = [ZFSelectePersonCardCell new];
    }
    
    return cell;
}

- (void)changeSeletcePersonToContain:(NSDictionary *)model state:(BOOL)state{
    
}

- (void)keybordResign{
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSDictionary *dic = [self getItemDataWithIndex:indexPath];
    
    YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:dic];
    
    WS(weakSelf);
    [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"确认选择?" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
        
        if(row == 0){
            if(weakSelf.zfPersonCardSelecte){
                weakSelf.zfPersonCardSelecte(model);
            }
            
            [weakSelf.navigationController popViewControllerAnimated:YES];
        }
    }];
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
