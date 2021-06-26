//
//  ZFMessageTransportVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFMessageTransportVC.h"
#import "ZFChatGlobal.h"
#import "ZFConnectionIndexView.h"
#import "ZFConnectionModel.h"
#import "ZFSelectePersonCell.h"
#import "ZFRequestManage.h"
#import "ZFGroupHelper.h"
#import "ZFMessageGroupTransportVC.h"

#import "ZFChatConfigure.h"
#import "ZFTransportPresenter.h"
#import "ZFChatHelper.h"


@interface ZFMessageTransportVC ()

@property (nonatomic,strong) dispatch_semaphore_t selectePersonLock;

@property (nonatomic,strong) NSMutableArray <NSDictionary *>*selelectPersonContain;

@property (nonatomic,strong) ZFConnectionIndexView *indexView;

@property (nonatomic,strong) ZFConnectionModel *model;

@property (nonatomic,strong) NSArray *canontSelecteItemsArr;

@property (nonatomic,assign) BOOL totalSelecteState;

@end

@implementation ZFMessageTransportVC

+ (id)initialVC{
    ZFMessageTransportVC *transport = [ZFMessageTransportVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson") leftItem:nil rightItem:@"发送"];
    return transport;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _totalSelecteState = NO;
    self.selectePersonLock = dispatch_semaphore_create(1);
    self.view.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    _selelectPersonContain = [NSMutableArray arrayWithCapacity:0];
    
    [self makeTable];
    
    [self loadData];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    WS(weakSelf);
    if(self.selelectPersonContain && [self.selelectPersonContain isKindOfClass:[NSArray class]]){
        if(self.selelectPersonContain.count > 0){
            
            if(self.chat.msg && [self.chat.msg isKindOfClass:[HTMessage class]]){
                
                ZFTransportPresenter *present = [[ZFTransportPresenter alloc] initWithMessage:self.chat.msg];
                present.type = @"1";
               
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < self.selelectPersonContain.count; i ++) {
                    NSString *userId = [NSString stringWithFormat:@"%ld",[self.selelectPersonContain[i][@"userId"] integerValue]];
                    if(userId && [userId isKindOfClass:[NSString class]]){
                        [tmp addObject:userId];
                    }
                }
                
                if(tmp.count > 0){
                    
                    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
                    present.ZFTransportPresenterTransPortProgress = ^(NSInteger num, NSInteger totalNum) {
                        CGFloat percent = (CGFloat)num / totalNum;
                        
                        [ProjectHelper helper_getMainThread:^{
                            if([progress respondsToSelector:@selector(setProgressText:)]){
                                [progress performSelector:@selector(setProgressText:) withObject:[NSString stringWithFormat:@"已完成%.1f  %%",percent * 100]];
                            }
                        }];
                    };
                    [present transportMsgTo:tmp invocation:^{
                        if([progress respondsToSelector:@selector(hidden)]){
                            [progress performSelector:@selector(hidden)];
                        }
                        [ProjectHelper helper_getMainThread:^{
                            [weakSelf.navigationController popViewControllerAnimated:YES];
                        }];
                    }];
                }
                
            }
        }
    }
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
    [[ZFRequestManage defaultManager] zfRequest_connectionListWithPage:0 success:^(NSArray * _Nonnull originDataArr, NSArray * _Nonnull listArr) {
            invocation(originDataArr,listArr);
            
        } fail:^(NSString * _Nonnull error) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
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

- (void)tableUpdate{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    if(_isShowSelecteAll){
        [tmp addObject:[NSNumber numberWithInteger:2]];
    }
    else{
        [tmp addObject:[NSNumber numberWithInteger:1]];
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

- (NSString *)getKeyWithIndex:(NSInteger)sectionCurrent{
    NSInteger section = sectionCurrent - 1;
    
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
    return 30.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    if(section == 0){
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        [back addSubview:lab];
        lab.text = @"群组";
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

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ZFSelectePersonCell *cell = nil;
    
    if(indexPath.section == 0){
        static NSString *str = @"YiChatGroupListCell_group";
        
        CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
        
        cell = [tableView dequeueReusableCellWithIdentifier:str];
        
        if(!cell){
            cell = [ZFSelectePersonCell  initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.cTable.frame.size.width] isHasDownLine:[NSNumber numberWithFloat:YES] type:1];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        if(indexPath.row == 0){
            [cell setIcon:[UIImage imageNamed:PROJECT_ICON_GROUPDEFAULT] name:@"我的群组"];
        }
        else{
            [cell setIcon:[UIImage imageNamed:@"connect_mass_icon.png"] name:@"发给所有好友"];
        }
        
    }
    else{
        static NSString *str = @"YiChatGroupListCell_SelecteItemList";
        
        CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
        
        cell = [tableView dequeueReusableCellWithIdentifier:str];
        
        if(!cell){
            cell = [ZFSelectePersonCell  initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.cTable.frame.size.width] isHasDownLine:[NSNumber numberWithFloat:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.cellModel = [self getItemDataWithIndex:indexPath];
        
        WS(weakSelf);
        cell.zfSelectePerson = ^(NSDictionary * _Nonnull model, BOOL state) {
            [weakSelf keybordResign];
            [ProjectHelper helper_getGlobalThread:^{
                [weakSelf changeSeletcePersonToContain:model state:state];
            }];
        };
    }
    
    return cell;
}

- (void)changeSeletcePersonToContain:(NSDictionary *)model state:(BOOL)state{
    dispatch_semaphore_wait(self.selectePersonLock, DISPATCH_TIME_FOREVER);
    
    __block BOOL isHas = NO;
    WS(weakSelf);
    
    if(self.selelectPersonContain.count != 0){
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        
        dispatch_apply(weakSelf.selelectPersonContain.count,queue , ^(size_t i) {
            
            NSString *modelId = [weakSelf getListItemIdWithListData:model];
            NSString *selecteItemId = [weakSelf getListItemIdWithListData:weakSelf.selelectPersonContain[i]];
            if(modelId && [modelId isKindOfClass:[NSString class]] && selecteItemId && [selecteItemId isKindOfClass:[NSString class]]){
                if([modelId isEqualToString:selecteItemId]){
                    isHas = YES;
                }
            }
        });
        
    }
    
    if(isHas == NO){
        if(state){
            if(model && [model isKindOfClass:[NSDictionary class]]){
                [weakSelf.selelectPersonContain addObject:model];
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf changeSelecteNum];
                    dispatch_semaphore_signal(weakSelf.selectePersonLock);
                }];
                
                return;
            }
        }
    }
    if(isHas == YES){
        
        if(state == NO){
            if(model && [model isKindOfClass:[NSDictionary class]]){
                
                for (int i = 0; i < weakSelf.selelectPersonContain.count; i ++) {
                    
                    NSString *modelId = [weakSelf getListItemIdWithListData:model];
                    NSString *selecteItemId = [weakSelf getListItemIdWithListData:weakSelf.selelectPersonContain[i]];
                    
                    if(modelId && [modelId isKindOfClass:[NSString class]] && selecteItemId && [selecteItemId isKindOfClass:[NSString class]]){
                        if([modelId isEqualToString:selecteItemId]){
                            [weakSelf.selelectPersonContain removeObjectAtIndex:i];
                        }
                    }
                }
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf changeSelecteNum];
                   
                    dispatch_semaphore_signal(weakSelf.selectePersonLock);
                }];
                
                return;
            }
        }
    }
    
    dispatch_semaphore_signal(weakSelf.selectePersonLock);
}

- (void)keybordResign{
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if(indexPath.section == 0){
        if(indexPath.row == 0){
            ZFMessageGroupTransportVC *groupTransPort = [ZFMessageGroupTransportVC initialVC];
            groupTransPort.chat = self.chat;
            [self.navigationController pushViewController:groupTransPort animated:YES];
        }
        else{
            _totalSelecteState = !_totalSelecteState;
            [self dealTotalSelecte];
            [self tableUpdate];
            [self changeSelecteNum];
        }
    }
    
}

- (void)dealTotalSelecte{
    
    [self.selelectPersonContain removeAllObjects];
    
    for (int i = 0; i < self.model.connectionModelArr.count; i ++) {
        NSDictionary *dic = self.model.connectionModelArr[i];
        
        if([dic isKindOfClass:[NSDictionary class]] && dic){
            NSArray *arr = self.model.connectionModelArr[i][dic.allKeys.lastObject];
            
            [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                
                if([obj isKindOfClass:[NSDictionary class]]){
                    
                    if(_totalSelecteState == NO){
                        objc_setAssociatedObject(obj, @"state", [NSNumber numberWithBool:YES], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        [self.selelectPersonContain addObject:obj];
                    }
                    else{
                        
                        objc_setAssociatedObject(obj, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                        
                    }
                }
            }];
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
