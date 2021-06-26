//
//  ZFMessageGroupTransportVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFMessageGroupTransportVC.h"
#import "ZFChatGlobal.h"
#import "ZFConnectionIndexView.h"
#import "ZFConnectionModel.h"
#import "ZFSelectePersonCell.h"
#import "ZFRequestManage.h"
#import "ZFGroupHelper.h"

#import "ZFGroupHelper.h"
#import "ZFMessageGroupTransportVC.h"

#import "ZFChatConfigure.h"
#import "ZFTransportPresenter.h"
#import "ZFChatHelper.h"
@interface ZFMessageGroupTransportVC ()

@property (nonatomic,strong) dispatch_semaphore_t selectePersonLock;

@property (nonatomic,strong) NSMutableArray <NSDictionary *>*selelectPersonContain;

@property (nonatomic,strong) ZFConnectionIndexView *indexView;

@property (nonatomic,strong) NSArray *model;

@end

@implementation ZFMessageGroupTransportVC

+ (id)initialVC{
    ZFMessageGroupTransportVC *transport = [ZFMessageGroupTransportVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"selectePerson") leftItem:nil rightItem:@"发送"];
    return transport;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.selectePersonLock = dispatch_semaphore_create(1);
    self.view.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    _selelectPersonContain = [NSMutableArray arrayWithCapacity:0];
    
    [self makeTable];
    
    [self loadData];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(self.selelectPersonContain && [self.selelectPersonContain isKindOfClass:[NSArray class]]){
        if(self.selelectPersonContain.count > 0){
            if(self.chat.msg && [self.chat.msg isKindOfClass:[HTMessage class]]){
                
                ZFTransportPresenter *present = [[ZFTransportPresenter alloc] initWithMessage:self.chat.msg];
                present.type = @"2";
                
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < self.selelectPersonContain.count; i ++) {
                    NSString *userId = [NSString stringWithFormat:@"%ld",[self.selelectPersonContain[i][@"gid"] integerValue]];
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
                            
                            NSArray *viewcontrollers = self.navigationController.viewControllers;
                            
                            [self.navigationController popToViewController:viewcontrollers[viewcontrollers.count - 1 - 2] animated:YES];
                        }];
                    }];
                }
                
            }
        }
    }
}

- (void)loadData{
    [self loadListData:^(NSArray *listDataArr) {
        if(listDataArr && [listDataArr isKindOfClass:[NSArray class]]){
            
            for (int i = 0; i < listDataArr.count; i ++) {
                id obj = listDataArr[i];
                objc_setAssociatedObject(obj, @"state", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
                
                objc_setAssociatedObject(obj, @"selecteState", [NSNumber numberWithBool:NO], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
            }
            
            self.model = listDataArr;
            
            [self tableUpdate];
        }
    }];
}

- (void)loadListData:(void(^)(NSArray *listDataArr))invocation{
    [self loadGroups:invocation];
}

- (void)loadGroups:(void(^)(NSArray *listDataArr))invocation{
    [ZFGroupHelper getSelfGroups:^(NSArray * _Nonnull aGroups) {
        
        [ProjectHelper helper_getGlobalThread:^{
            if(aGroups.count){
                
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                for (int i = 0; i < aGroups.count; i ++) {
                    NSDictionary *info = [YiChatGroupInfoModel translateObjPropertyToDic:aGroups[i]];
                    
                    if([info isKindOfClass:[NSDictionary class]] && info){
                        
                        [tmp addObject:info];
                    }
                }
                if(tmp && [tmp isKindOfClass:[NSArray class]]){
                    invocation(tmp);
                    return ;
                }
            }
            invocation(nil);
        }];
        
        
    } failure:^(NSError * _Nonnull error) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
        invocation(nil);
    }];
}

- (void)loadCanNotSelecteData:(void(^)(NSArray *cannotSelecteDataArr))invocation{
    invocation(nil);
}

- (NSString *)getlistItemIdWithIdex:(NSInteger)index{
    if(self.model && [self.model isKindOfClass:[ZFConnectionModel class]]){
        if(self.model && [self.model isKindOfClass:[NSArray class]]){
            if(self.model.count - 1 >= index){
                return [self getListItemIdWithListData:self.model[index]];
            }
        }
    }
    return nil;
}

- (NSString *)getListItemIdWithListData:(NSDictionary *)data{
    if(data && [data isKindOfClass:[NSDictionary class]]){
        if([data.allKeys containsObject:@"gid"]){
            if([data[@"gid"] isKindOfClass:[NSString class]] && data[@"gid"]){
                return [NSString stringWithFormat:@"%ld",[data[@"gid"] integerValue]];
            }
        }
        return data[@""];
    }
    return nil;
}

- (NSDictionary *)getItemDataWithIndex:(NSIndexPath *)indexPath{
    NSInteger row = indexPath.row;
    
    if(row <= (_model.count - 1)){
        NSDictionary *dic = _model[row];
        return dic;
        
    }
    return nil;
}

- (void)makeTable{
    self.sectionsRowsNumSet = @[];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom);
}

- (void)tableUpdate{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    if(self.model && [self.model isKindOfClass:[NSArray class]]){
        [tmp addObject:[NSNumber numberWithInteger:self.model.count]];
    }
    self.sectionsRowsNumSet = tmp;
    
    [ProjectHelper helper_getMainThread:^{
        [self.cTable reloadData];
    }];
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

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 0.0001f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ZFSelectePersonCell *cell = nil;
    
    static NSString *str = @"YiChatGroupListCell_grouplistTransport";
    
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
