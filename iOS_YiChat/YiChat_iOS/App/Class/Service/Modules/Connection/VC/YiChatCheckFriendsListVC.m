//
//  YiChatCheckFriendsListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatCheckFriendsListVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatCheckFriendsListCell.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ProjectCommonCellModel.h"
#import "YiChatFriendInfoVC.h"
#import "ZFChatFriendHelper.h"

@interface YiChatCheckFriendsListVC ()

@property (nonatomic,assign) NSInteger currentPage;

@property (nonatomic,strong) NSMutableArray *dataArr;

@end

@implementation YiChatCheckFriendsListVC

+ (id)initialVC{
    YiChatCheckFriendsListVC *checkFriend = [YiChatCheckFriendsListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"newFriends") leftItem:nil rightItem:@"清除所有"];
    return checkFriend;
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel getFriendApplyDeleteWithFId:@""];
    [ProjectRequestHelper friendApplyDeleteWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:obj];
                if (model.code == 0) {
                    [weakSelf.dataArr removeAllObjects];
                    [ProjectHelper helper_getMainThread:^{
                        self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:weakSelf.dataArr.count]];
                        [weakSelf.cTable reloadData];
                    }];
                }
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
       
    }];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _dataArr = [NSMutableArray arrayWithCapacity:0];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self refreshData];
}

- (void)refreshData{
    _currentPage = 1;
    [self loadDataWithPage:_currentPage];
}

- (void)loadMoreData{
    _currentPage ++;
    [self loadDataWithPage:_currentPage];
}

- (void)loadDataWithPage:(NSInteger)page{
    WS(weakSelf);
    
    NSDictionary *param = [ProjectRequestParameterModel getCheckFriendApplyDataListParamWithUserId:YiChatUserInfo_UserIdStr pageNo:[NSString stringWithFormat:@"%ld",page]];
    
    //[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""]
    
    [ProjectRequestHelper checkFriendApplyDataListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSArray *data = obj[@"data"];
                if([data isKindOfClass:[NSArray class]]){
                    if(data.count == 0){
                    }
                    else{
                        NSMutableArray *tmpArr = [NSMutableArray arrayWithCapacity:0];
                        for (int i = 0; i < data.count; i ++) {
                            NSDictionary *tmp = data[i];
                            if(tmp && [tmp isKindOfClass:[NSDictionary class]]){
                                NSString *title = tmp[@"nick"];
                                NSString *urlIcon = tmp[@"avatar"];
                                NSString *fid = [NSString stringWithFormat:@"%ld",[tmp[@"fid"] integerValue]];
                                NSString *state = [NSString stringWithFormat:@"%ld",[tmp[@"status"] integerValue]];
                                NSString *userId= [NSString stringWithFormat:@"%ld",[tmp[@"userId"] integerValue]];
                                
                                ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                                if(title && [title isKindOfClass:[NSString class]]){
                                    model.titleStr = title;
                                }
                                if(urlIcon && [urlIcon isKindOfClass:[NSString class]]){
                                    model.iconUrl = urlIcon;
                                }
                                if(fid && [fid isKindOfClass:[NSString class]]){
                                    model.desStr = fid;
                                }
                                if(state && [state isKindOfClass:[NSString class]]){
                                    model.state = state;
                                }
                                if(userId && [userId isKindOfClass:[NSString class]]){
                                    model.ids = userId;
                                }
                                
                                if(model){
                                    [tmpArr addObject:model];
                                }
                            }
                        }
                        
                        if(weakSelf.currentPage == 1){
                            [weakSelf.dataArr removeAllObjects];
                            [weakSelf.dataArr addObjectsFromArray:tmpArr];
                        }
                        else{
                            [weakSelf.dataArr addObjectsFromArray:tmpArr];
                        }
                    }
                }
                
                [ProjectHelper helper_getMainThread:^{
                    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:weakSelf.dataArr.count]];
                    [weakSelf.cTable reloadData];
                }];
                
                return ;
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                return ;
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)makeTable{
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y - PROJECT_SIZE_TABH);
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 10.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatCheckFriendsListCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    static NSString *str = @"YiChatCheckFriendsListVC_addFriend";
    cell =  [tableView dequeueReusableCellWithIdentifier:str];
    if(!cell){
        cell = [YiChatCheckFriendsListCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
    }
    
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
   
    cell.cellModel = [self getModelWithIndex:indexPath];
    
    WS(weakSelf);
    
    cell.YiChatCheckFriendsListCellClickAdd = ^(ProjectCommonCellModel * _Nonnull model) {
        [weakSelf dealApplyWithModel:model stataus:@"1"];
        if(weakSelf.dataArr.count - 1 >= indexPath.row){
            [weakSelf.cTable reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        }
    };
    cell.YiChatCheckFriendsListCellClickRefuse = ^(ProjectCommonCellModel * _Nonnull model) {
        [weakSelf dealApplyWithModel:model stataus:@"0"];
        if(weakSelf.dataArr.count - 1 >= indexPath.row){
            [weakSelf.cTable reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        }
    };
    
    return cell;
}

- (void)dealApplyWithModel:(ProjectCommonCellModel *)model stataus:(NSString *)status{
    WS(weakSelf);
    
    [self agreeApplyWithFid:model.desStr applyUserid:model.ids status:status successHandle:^(BOOL isSuccess) {
        
        if(isSuccess){
            
            model.state = @"1";
            [ProjectHelper helper_getMainThread:^{
                [weakSelf.cTable reloadData];
            }];
        }
    }];
}

- (void)agreeApplyWithFid:(NSString *)fid applyUserid:(NSString *)applyUserId status:(NSString *)status successHandle:(void(^)(BOOL isSuccess))invocation{
    WS(weakSelf);
    NSString *userId = YiChatUserInfo_UserIdStr;
    
    if(userId && fid && status && applyUserId){
        if([userId isKindOfClass:[NSString class]] && [fid isKindOfClass:[NSString class]] && [status isKindOfClass:[NSString class]]){
            NSDictionary *param = [ProjectRequestParameterModel getCheckFriendApplyParamWithUserId:userId fid:fid status:status];
            
            [ProjectRequestHelper checkFriendApplyWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                
            } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                    
                    if([obj isKindOfClass:[NSDictionary class]]){
                        //审核成功
                        invocation(YES);
                        
                        if([status isEqualToString:@"1"]){
                            [[YiChatUserManager defaultManagaer] updateUserConnectionInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
                                
                            }];
                        }
                        
                        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                NSDictionary *dic = [model getOriginDic];
                                
                                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                    if([status isEqualToString:@"0"]){
                                        [ZFChatFriendHelper zfChatFriendHelperDisagreeFriendApplyWithUserId:applyUserId userInfo:dic completion:nil];
                                    }
                                    else if([status isEqualToString:@"1"]){
                                        [ZFChatFriendHelper zfChatFriendHelperAgreeFriendApplyWithUserId:applyUserId userInfo:dic completion:nil];
                                        
                                        
                                    }
                                }
                            }
                        }];
                       
                        
                        return ;
                    }
                    else if([obj isKindOfClass:[NSString class]]){
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                    }
                    invocation(NO);
                    
                }];
            } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
                invocation(NO);
            }];
        }
    }
}

- (ProjectCommonCellModel *)getModelWithIndex:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *dataModel = nil;
    if(indexPath.row <= (_dataArr.count - 1)){
        ProjectCommonCellModel *tmp = _dataArr[indexPath.row];
        if([tmp isKindOfClass:[ProjectCommonCellModel class]]){
            return tmp;
        }
    }
    return dataModel;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
    YiChatFriendInfoVC *friendInfo = [YiChatFriendInfoVC initialVC];
//    friendInfo.model = [self getModelWithIndex:indexPath];
    friendInfo.userId = model.ids;
    friendInfo.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:friendInfo animated:YES];
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
    
}
// 定义编辑样式
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
    
}
// 进入编辑模式，按下出现的编辑按钮后,进行删除操作
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        WS(weakSelf);
        ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
        NSDictionary *param = [ProjectRequestParameterModel getFriendApplyDeleteWithFId:model.ids];
        [ProjectRequestHelper friendApplyDeleteWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:obj];
                    if (model.code == 0) {
                        [weakSelf.dataArr removeObjectAtIndex:indexPath.row];
                        [ProjectHelper helper_getMainThread:^{
                            self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:weakSelf.dataArr.count]];
                            [weakSelf.cTable reloadData];
                        }];
                    }
                }
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
        }];
    }
    
}
// 修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath {
    return @"删除";
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
