//
//  YiChatDynamicPresenter.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicPresenter.h"
#import "ServiceGlobalDef.h"
#import <MJRefresh.h>
#import "YiChatDynamicVC.h"

#import "YiChatDynamicDataSource.h"
#import "ProjectRefreshAnimateView.h"

#import "YiChatDynamicListViewHeader.h"
#import "YiChatDynamicListToolBarAppearView.h"
#import "YiChatDynamicCommitView.h"

@interface YiChatDynamicPresenter ()

@property (nonatomic,strong) ProjectRefreshAnimateView *refreshHeader;

@property (nonatomic,assign) NSInteger currentPage;

@property (nonatomic,strong) NSMutableArray *dynamicDataSourceArr;

@property (nonatomic,strong) YiChatDynamicListViewHeader *header;
@property (nonatomic,strong) YiChatDynamicListToolBarAppearView *toolCommitLikeBar;
@property (nonatomic,strong) YiChatDynamicCommitView *commitView;

@property (nonatomic,strong) dispatch_semaphore_t praiseLock;

@property (nonatomic,assign) BOOL isRereshing;

@property (nonatomic,assign) NSInteger totalDynamicCount;

@end

@implementation YiChatDynamicPresenter

- (id)init{
    self = [super init];
    if(self){
         [self systemInitial];
    }
    return self;
}

- (id)initWithUserId:(NSString *)userId{
    self = [super init];
    if(self){
        [self systemInitial];
    }
    return self;
}

- (void)systemInitial{
    _isRereshing = NO;
    _currentPage = 1;
    _dynamicDataSourceArr = [NSMutableArray arrayWithCapacity:0];
    _praiseLock = dispatch_semaphore_create(1);
    _totalDynamicCount = 0;
}

- (void)addDynamicHeader{
    self.controlVC.cTable.tableHeaderView = self.header;
    [self.header updateData];
    
    self.controlVC.cTable.contentInset = UIEdgeInsetsMake(- 60.0, 0, 0, 0);
}

- (void)addtoolCommitLikeBar{
    [self.controlVC.view addSubview:self.toolCommitLikeBar];
     self.toolCommitLikeBar.hidden = YES;
}

- (void)addCommitView{
    [self.controlVC.view addSubview:self.commitView];
}

- (void)commitViewActive:(NSString *)trandId{
    [self.commitView beginActive:trandId];
}

- (void)commitViewResign{
    [self.commitView removeActive];
}

- (YiChatDynamicListViewHeader *)header{
    if(!_header){
        WS(weakSelf);
        
        _header = [YiChatDynamicListViewHeader create];
        _header.YiChatDynamicListViewHeaderClickBackGroud = ^{
            
            if([weakSelf judgeIsSelfDynamic]){
                [ProjectUIHelper projectActionSheetWithListArr:@[@"相机",@"相册"] click:^(NSInteger row) {
                    if(row == 0){
                        
                        [ProjectUIHelper projectPhotoVideoPickerWWithType:5 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                            manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
                                if(editedIcon && [editedIcon isKindOfClass:[UIImage class]]){
                                    [weakSelf uploadImage:editedIcon invocation:^(BOOL isSuccess, NSString *des) {
                                        if(!isSuccess){
                                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:des];
                                        }
                                    }];
                                }
                            };
                            [weakSelf.controlVC presentViewController:nav animated:YES completion:nil];
                        }];
                        
                    }
                    else if(row == 1){
                        
                        [ProjectUIHelper projectPhotoVideoPickerWWithType:6 pickNum:1 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                            
                            manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
                                if(images && [images isKindOfClass:[NSArray class]]){
                                    if(images.count == 1){
                                        [weakSelf uploadImage:images.firstObject invocation:^(BOOL isSuccess, NSString *des) {
                                            if(!isSuccess){
                                                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:des];
                                            }
                                        }];
                                    }
                                }
                            };
                            [weakSelf.controlVC presentViewController:nav animated:YES completion:nil];
                        }];
                        
                    }
                }];
            }
            
            
        };
        if(self.dynamicUserId && [self.dynamicUserId isKindOfClass:[NSString class]]){
            _header.userIdStr = self.dynamicUserId;
        }
        else{
            _header.userIdStr = YiChatUserInfo_UserIdStr;
        }
    }
    return _header;
}

- (BOOL)judgeIsSelfDynamic{
    if(!(self.dynamicUserId && [self.dynamicUserId isKindOfClass:[NSString class]])){
        return YES;
    }
    else{
        if(self.dynamicUserId && [self.dynamicUserId isKindOfClass:[NSString class]]){
            if([self.dynamicUserId isEqualToString:YiChatUserInfo_UserIdStr]){
                return YES;
            }
            else{
                return NO;
            }
        }
    }
    return NO;
}

- (YiChatDynamicListToolBarAppearView *)toolCommitLikeBar{
    if(!_toolCommitLikeBar){
        WS(weakSelf);
        _toolCommitLikeBar = [YiChatDynamicListToolBarAppearView create];
        
        _toolCommitLikeBar.YiChatDynamicListToolBarAppearViewCommitClick = ^(NSString * _Nonnull trendId, NSInteger idnex) {
            
            [weakSelf scrolltoolCommitLikeBarDisappear];
            
            [weakSelf commitViewActive:trendId];
            
        };
        _toolCommitLikeBar.YiChatDynamicListToolBarAppearViewLikeClick = ^(NSString * _Nonnull trendId, NSInteger idnex) {
            
            
            [weakSelf scrolltoolCommitLikeBarDisappear];
            
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    
                    [weakSelf addLikeWithUserModel:model trendId:trendId index:idnex];
                }
            }];
        };
    }
    return _toolCommitLikeBar;
}

- (void)addLikeWithUserModel:(YiChatUserModel *)model trendId:(NSString *)trenid index:(NSInteger)idnex{
    NSString *nick = [model nickName];
    NSString *userId = [model getUserIdStr];
    
    YiChatDynamicPraiseEntityModel *entity = [[YiChatDynamicPraiseEntityModel alloc] init];
    entity.nick = nick;
    entity.userId = [userId integerValue];
    
    if(self.dynamicDataSourceArr.count - 1 >= idnex){
        YiChatDynamicDataSource *data = self.dynamicDataSourceArr[idnex];
        NSArray *arr = data.model.praiseList;
        
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
        
        if(arr && [arr isKindOfClass:[NSArray class]]){
            if(arr.count > 0 ){
                
                NSMutableArray *arrTmp = [NSMutableArray arrayWithCapacity:0];
                [arrTmp addObjectsFromArray:arr];
                
                BOOL isHas = NO;
                for (int i = 0; i < arrTmp.count; i ++) {
                    if(arr.count - 1 >= i){
                        YiChatDynamicPraiseEntityModel *entityTmp = arrTmp[i];
                        NSString *userIdStr = [NSString stringWithFormat:@"%ld",entityTmp.userId];
                        
                        if([userIdStr isEqualToString:YiChatUserInfo_UserIdStr]){
                            isHas = YES;
                            [arrTmp removeObjectAtIndex:i];
                        }
                    }
                }
                
                if(isHas){
                    //取消点赞
                    data.model.praiseList = arrTmp;
                    data.model.praiseCount --;
                    
                    [self reqeustDisLikeWithTrendId:[data getTrendId]];
                    
                }
                else{
                    //点赞
                    [tmp addObjectsFromArray:arr];
                    [tmp addObject:entity];
                    
                    
                    data.model.praiseCount ++;
                    data.model.praiseList = tmp;
                    
                    [self requestLikeWithTrendId:[data getTrendId]];
                }
            }
            else{
                data.model.praiseCount ++;
                [tmp addObject:entity];
                data.model.praiseList = tmp;
                
                [self requestLikeWithTrendId:[data getTrendId]];
            }
        }
        else{
            data.model.praiseCount ++;
            [tmp addObject:entity];
            data.model.praiseList = tmp;
            
            [self requestLikeWithTrendId:[data getTrendId]];
        }
        [data update];
        [self tableUpdateInvocation:^{
            
        }];
    }
}

- (void)addCommitWithUserModel:(YiChatUserModel *)model trendId:(NSString *)trenid index:(NSInteger)idnex content:(NSString *)content{
    if(content && [content isKindOfClass:[NSString class]]){
        if(content.length > 0){
            YiChatDynamicCommitEntityModel *commitModel = [[YiChatDynamicCommitEntityModel alloc] init];
            commitModel.content = content;
            commitModel.userId = model.userId;
            commitModel.nick = [model nickName];
            commitModel.trendId = [trenid integerValue];
            
            YiChatDynamicDataSource *data = self.dynamicDataSourceArr[idnex];
            NSArray *arr = data.model.commentList;
            
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            
            if(arr && [arr isKindOfClass:[NSArray class]]){
                if(arr.count > 0){
                    [tmp addObjectsFromArray:arr];
                    [tmp addObject:commitModel];
                }
                else{
                    [tmp addObject:commitModel];
                }
            }
            else{
                [tmp addObject:commitModel];
            }
            data.model.commentList = tmp;
            data.model.commentCount ++;
            
            [self requestCommitWithTrendId:trenid content:content];
            
            [data update];
            [self tableUpdateInvocation:^{
                
            }];
        }
    }
}

- (void)removeCommentWithIndex:(NSIndexPath *)index{
    YiChatDynamicDataSource *dataource = [self getDataSourceWithIndex:index.section];
    if(dataource && [dataource isKindOfClass:[YiChatDynamicDataSource class]]){
        if(dataource.getCommentList.count > 0){
            NSArray *commitList = dataource.model.commentList;
            
            if(commitList.count - 1 >=  index.row){
                
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                [arr addObjectsFromArray:commitList];
                
                YiChatDynamicCommitEntityModel *commit = commitList[index.row];
                
                if(commit && [commit isKindOfClass:[YiChatDynamicCommitEntityModel class]]){
                    NSString *commentid = [NSString stringWithFormat:@"%ld",commit.commentId];
                    if(commentid && [commentid isKindOfClass:[NSString class]]){
                        [self deleteCommitWithCommentId:commentid];
                    }
                    
                    [arr removeObjectAtIndex:index.row];
                    
                    dataource.model.commentCount --;
                    dataource.model.commentList = arr;
                    
                    
                    [dataource update];
                    [self tableUpdateInvocation:^{
                        
                    }];
                }
                
            }
        }
    }
}

- (void)removeDynamicWithTrendId:(NSString *)trendId indx:(NSInteger)indx{
    
    if(trendId && [trendId isKindOfClass:[NSString class]]){
        if(self.dynamicDataSourceArr.count - 1 >= indx){
            for (int i = 0; i < self.dynamicDataSourceArr.count; i ++) {
                if(self.dynamicDataSourceArr.count - 1 >= i){
                    YiChatDynamicDataSource *data = self.dynamicDataSourceArr[i];
                    NSString *tmpTrendId = [data getTrendId];
                    if(tmpTrendId && [tmpTrendId isKindOfClass:[NSString class]]){
                        if([tmpTrendId isEqualToString:trendId]){
                            if(self.dynamicDataSourceArr.count - 1 >= i){
                                [self.dynamicDataSourceArr removeObjectAtIndex:i];
                            }
                        }
                    }
                }
            }
            
            [self tableUpdateInvocation:^{
                
            }];
            [self deleteDynamicWithTrendId:trendId];
        }
    }
    
}

- (void)requestLikeWithTrendId:(NSString *)trendId{
    [ProjectHelper helper_getGlobalThread:^{
        if(trendId && [trendId isKindOfClass:[NSString class]]){
            
            dispatch_semaphore_wait(self.praiseLock, DISPATCH_TIME_FOREVER);
            
            NSDictionary *param = [ProjectRequestParameterModel dynamicPraiseWithTrendId:[trendId integerValue]];
            
            [ProjectRequestHelper dynamicPraiseWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                
            } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                dispatch_semaphore_signal(self.praiseLock);
                
                [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                    
                }];
            } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                dispatch_semaphore_signal(self.praiseLock);
            }];
        }
    }];
}

- (void)reqeustDisLikeWithTrendId:(NSString *)trendId{
    [ProjectHelper helper_getGlobalThread:^{
        
        if(trendId && [trendId isKindOfClass:[NSString class]]){
              dispatch_semaphore_wait(self.praiseLock, DISPATCH_TIME_FOREVER);
            NSDictionary *param = [ProjectRequestParameterModel dynamicCancelPraiseWithTrendId:[trendId integerValue]];
            
            [ProjectRequestHelper dynamicCancelPraiseWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                
            } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                dispatch_semaphore_signal(self.praiseLock);
                [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                    
                }];
                
            } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                dispatch_semaphore_signal(self.praiseLock);
            }];
        }
    }];
}

- (void)requestCommitWithTrendId:(NSString *)trendId content:(NSString *)content{
    if(trendId && [trendId isKindOfClass:[NSString class]] && content && [content isKindOfClass:[NSString class]]){
        
        NSDictionary *param = [ProjectRequestParameterModel dynamicCommandWithTrendId:[trendId integerValue] content:content commentId:-1];
        
        [ProjectRequestHelper dynamicCommandWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }
}

- (void)deleteCommitWithCommentId:(NSString *)commentId{
    if(commentId && [commentId isKindOfClass:[NSString class]]){
        
        NSDictionary *param = [ProjectRequestParameterModel deleteDynamicCommandWithCommentId:[commentId integerValue]];
        
        [ProjectRequestHelper deleteDynamicCommandWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }
}

- (void)deleteDynamicWithTrendId:(NSString *)trendId{
    if(trendId && [trendId isKindOfClass:[NSString class]]){
       
        NSDictionary *param = [ProjectRequestParameterModel deleteDynamicWithTrendId:[trendId integerValue]];
        
        [ProjectRequestHelper deleteDynamicWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }
}

- (void)adjustKeyboadTableScroll:(CGRect)rect keyBoadSize:(CGSize)size{
    
    CGFloat delta =
    CGRectGetMaxY(rect) - (PROJECT_SIZE_HEIGHT - size.height);
    
    CGPoint offset = self.controlVC.cTable.contentOffset;
    offset.y += delta;
    if (offset.y < 0) {
        offset.y = 0;
    }
    
    offset.y += self.commitView.frame.size.height;
    
    CGFloat rowH = 0;
    if(self.dynamicDataSourceArr.count > 0){
        if(self.dynamicDataSourceArr.count -1 >= self.toolCommitLikeBar.index){
            NSInteger row = self.toolCommitLikeBar.index;
            if(self.dynamicDataSourceArr.count - 1 >= row){
                YiChatDynamicDataSource *data = self.dynamicDataSourceArr[row];
                for (int i = 0; i < data.showCommentStrRectArr.count; i ++) {
                    if(data.showCommentStrRectArr.count - 1 >= i){
                        NSValue *value = data.showCommentStrRectArr[i];
                        if(value && [value isKindOfClass:[NSValue class]]){
                            rowH += (value.CGRectValue.size.height + 10.0);
                        }
                    }
                }
            }
        }
    }
    
    
    offset.y += rowH;
    
    CGFloat h = self.controlVC.cTable.contentSize.height - self.controlVC.cTable.frame.size.height;
    if(h >= offset.y){
        [self.controlVC.cTable setContentOffset:offset animated:YES];
    }
    else{
        offset.y = h;
        [self.controlVC.cTable setContentOffset:offset animated:YES];
    }
}

- (YiChatDynamicCommitView *)commitView{
    if(!_commitView){
        WS(weakSelf);
        _commitView = [YiChatDynamicCommitView create];
        
        _commitView.YiChatDynamicCommitViewSend = ^(NSString * _Nonnull text, NSString * _Nonnull trendId) {
            
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    [weakSelf addCommitWithUserModel:model trendId:trendId index:weakSelf.toolCommitLikeBar.index content:text];
                }
            }];
            
        };
        _commitView.YiChatDynamicCommitViewKeyBoardShow = ^(CGSize keyBoard) {
            
            NSInteger section = weakSelf.toolCommitLikeBar.index;
            if(!(section <= weakSelf.dynamicDataSourceArr.count - 1)){
                section = (weakSelf.dynamicDataSourceArr.count - 1);
            }
            UIView *cell =
            [weakSelf.controlVC.cTable headerViewForSection:section];
            CGRect rect = [cell.superview convertRect:cell.frame toView:weakSelf.controlVC.view];
            
            [weakSelf adjustKeyboadTableScroll:rect keyBoadSize:keyBoard];

        };
        _commitView.hidden = YES;
    }
    return _commitView;
}

- (void)toolCommitLikeBarAppearWithPoint:(CGPoint)point model:(YiChatDynamicDataSource *)model index:(NSInteger )section{
    self.toolCommitLikeBar.hidden = NO;
    self.toolCommitLikeBar.trendId = [model getTrendId];
    self.toolCommitLikeBar.index = section;
    [self.toolCommitLikeBar changeToOriginPosition:point];
    [self.toolCommitLikeBar beginAppearToPoint:CGPointMake(point.x - self.toolCommitLikeBar.frame.size.width - 10.0, point.y) isAnimate:YES];
}

- (void)toolCommitLikeBarDisappear{
    
    WS(weakSelf);
    [self.toolCommitLikeBar disappearToPoint:CGPointMake(self.toolCommitLikeBar.frame.origin.x + self.toolCommitLikeBar.frame.size.width + 10.0, self.toolCommitLikeBar.frame.origin.y) isAnimate:YES invacation:^{
        weakSelf.toolCommitLikeBar.hidden = YES;
    }];
}

- (void)scrolltoolCommitLikeBarDisappear{
     self.toolCommitLikeBar.hidden = YES;
    WS(weakSelf);
    [self.toolCommitLikeBar disappearToPoint:CGPointMake(self.toolCommitLikeBar.frame.origin.x + self.toolCommitLikeBar.frame.size.width + 10.0, self.toolCommitLikeBar.frame.origin.y) isAnimate:YES invacation:^{
        weakSelf.toolCommitLikeBar.hidden = YES;
    }];
}

- (void)addHeaderRefresh{
    _refreshHeader = [ProjectRefreshAnimateView createAnimateView];
    [self.controlVC.view addSubview:_refreshHeader];
    
}

- (void)headerRereshBegin{
    WS(weakSelf);
    [ProjectHelper helper_getMainThread:^{
        [weakSelf.refreshHeader beginAnimateToPoin:CGPointMake(30,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH * 2)];
    }];
}

- (void)HeaderRefreshEnd{
    WS(weakSelf);
    [ProjectHelper helper_getMainThread:^{
        _isRereshing = NO;
        [weakSelf.refreshHeader endAnimateToPoint:CGPointMake(30,0)];
    }];
}

- (void)addLoadMore{
    WS(weakSelf);
    self.controlVC.cTable.mj_footer = [MJRefreshAutoNormalFooter footerWithRefreshingBlock:^{
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            
            [weakSelf loadDynamicListMore];
        });
    }];
}

- (void)beginLoadMore{
    if(self.controlVC){
        [self.controlVC.cTable.mj_footer beginRefreshing];
    }
}

- (void)endLoadMore{
    if(self.controlVC){
        [self.controlVC.cTable.mj_footer endRefreshing];
    }
}

- (void)refreshDynamicList{
    _currentPage = 1;

    if(_isRereshing == YES){
        return;
    }
    _isRereshing = YES;
    
    [self headerRereshBegin];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
       [self loadData];
    });
}

- (void)loadDynamicListMore{
    
    if(self.dynamicDataSourceArr.count < self.totalDynamicCount){
        [self.controlVC.cTable.mj_footer resetNoMoreData];
        
        self.currentPage ++;
        
        [self loadData];
    }
    else{
        [self.controlVC.cTable.mj_footer endRefreshingWithNoMoreData];
    }
}

- (void)loadData{
    WS(weakSelf);
    NSString *userid = @"";
    
    if(self.dynamicUserId && [self.dynamicUserId isKindOfClass:[NSString class]]){
        userid = self.dynamicUserId;
        [self loadFriendDynamicList];
    }
    else{
        userid = YiChatUserInfo_UserIdStr;
        [self loadDynamicList];
    }
    
    [self getDynamicBackGroudWithUserId:[userid integerValue] invocation:^(BOOL isSuccess, NSString *des, NSString *url) {
        
        [ProjectHelper helper_getMainThread:^{
            if(isSuccess && url && [url isKindOfClass:[NSString class]]){
                weakSelf.header.backImageUrl = url;
            }
            
            [weakSelf.header updateData];
        }];
    }];
}

- (void)loadDynamicList{
    
    NSDictionary *param = [ProjectRequestParameterModel getFriendFynamiclistPageNo:_currentPage pageSize:12];
    
    [ProjectRequestHelper getFriendFynamiclistWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            
            [ProjectHelper helper_getMainThread:^{
                [self HeaderRefreshEnd];
                [self endLoadMore];
                           
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dic = obj[@"data"];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        NSArray *data = dic[@"list"];
                        if(data && [data isKindOfClass:[NSArray class]]){
                            
                                NSMutableArray *modelArr = [NSMutableArray arrayWithCapacity:0];
                                 for (int i = 0; i < data.count; i ++)
                                 {
                                    NSDictionary *dic = data[i];
                                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                            YiChatDynamicModel *model = [[YiChatDynamicModel alloc] initWithDic:dic];
                                            if(model && [model isKindOfClass:[YiChatDynamicModel class]]){
                                                YiChatDynamicDataSource *dataModel = [[YiChatDynamicDataSource alloc] initWithDynamicModel:model];
                                                if(dataModel && [dataModel isKindOfClass:[YiChatDynamicDataSource class]]){
                                                        [modelArr addObject:dataModel];
                                                    }
                                                }
                                            }
                                    }
                                    if(_currentPage == 1){
                                        [self.dynamicDataSourceArr removeAllObjects];
                                        [self.dynamicDataSourceArr addObjectsFromArray:modelArr];
                                        if(self.totalDynamicCount > 12){
                                            [self addLoadMore];
                                        }
                                    }
                                    else{
                                        [self.dynamicDataSourceArr addObjectsFromArray:modelArr];
                                    }
                                                             
                                    [self tableUpdateInvocation:^{
                                                                 
                                    }];
                                                             
                                }
                        }
                            NSNumber *count = obj[@"count"];
                            if(count && [count isKindOfClass:[NSNumber class]]){
                                self.totalDynamicCount = count.integerValue;
                            }
                    }
                    else if([obj isKindOfClass:[NSString class]]){
                         [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                    }
                    else{
                         [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取动态列表失败"];
                    }
            }];
           
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
      
    }];
}

- (void)loadFriendDynamicList{
    
    NSDictionary *param = [ProjectRequestParameterModel getDynamicListWithUserId:[self.dynamicUserId integerValue] pageNo:_currentPage pageSize:12];
    
    [ProjectRequestHelper getAllDynamicListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            [self HeaderRefreshEnd];
            [self endLoadMore];
            
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                NSNumber *count = obj[@"count"];
                if(count && [count isKindOfClass:[NSNumber class]]){
                    self.totalDynamicCount = count.integerValue;
                }
                
                NSDictionary *dic = obj[@"data"];
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    NSArray *data = dic[@"list"];
                    
                    if(data && [data isKindOfClass:[NSArray class]]){
                        
                        [ProjectHelper helper_getMainThread:^{
                            NSMutableArray *modelArr = [NSMutableArray arrayWithCapacity:0];
                            for (int i = 0; i < data.count; i ++) {
                                NSDictionary *dic = data[i];
                                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                    YiChatDynamicModel *model = [[YiChatDynamicModel alloc] initWithDic:dic];
                                    if(model && [model isKindOfClass:[YiChatDynamicModel class]]){
                                        YiChatDynamicDataSource *dataModel = [[YiChatDynamicDataSource alloc] initWithDynamicModel:model];
                                        if(dataModel && [dataModel isKindOfClass:[YiChatDynamicDataSource class]]){
                                            
                                            [modelArr addObject:dataModel];
                                        }
                                    }
                                }
                            }
                            if(_currentPage == 1){
                                [self.dynamicDataSourceArr removeAllObjects];
                                [self.dynamicDataSourceArr addObjectsFromArray:modelArr];
                                
                                if(self.totalDynamicCount > 12){
                                    [self addLoadMore];
                                }
                                
                            }
                            else{
                                [self.dynamicDataSourceArr addObjectsFromArray:modelArr];
                            }
                            
                            [self tableUpdateInvocation:^{
                                
                            }];
                        }];
                        
                        
                        return ;
                    }
                    
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取动态列表失败"];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
       
    }];
}

- (void)getDynamicBackGroudWithUserId:(NSInteger)userId invocation:(void(^)(BOOL isSuccess,NSString *des,NSString *url))invocation{
    NSDictionary *param = [ProjectRequestParameterModel getDynamicBackImageWithUserId:userId];
    
    [ProjectRequestHelper dynamicGetBackImageWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                NSString *data = obj[@"data"];
                if(data && [data isKindOfClass:[NSString class]]){
                    invocation(YES,nil,data);
                    return ;
                }
            }
            else if(obj && [obj isKindOfClass:[NSString class]]){
                invocation(NO,obj,nil);
                return;
            }
            invocation(NO,@"获取背景图失败",nil);
            
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)uploadImage:(UIImage *)image invocation:(void(^)(BOOL isSuccess,NSString *des))invocation{
    WS(weakSelf);
    [ProjectRequestHelper commonUpLoadImage:image progressBlock:nil sendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
        if(isSuccess){
            [weakSelf setDynamicBackGroudWithUserId:YiChatUserInfo_UserId url:remotePath invocation:^(BOOL isSuccess, NSString *des) {
                
                [ProjectHelper helper_getMainThread:^{
                    weakSelf.header.backImageUrl = remotePath;
                    [weakSelf.header updateData];
                }];
                invocation(isSuccess,des);
            }];
        }
        else{
            invocation(NO,@"上传背景图失败");
        }
    }];
}

- (void)setDynamicBackGroudWithUserId:(NSInteger)userId url:(NSString *)url invocation:(void(^)(BOOL isSuccess,NSString *des))invocation{
    NSDictionary *param = [ProjectRequestParameterModel setDynamicBackImageWithUserId:userId img:url];
    
    [ProjectRequestHelper dynamicSetBackImageWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                invocation(YES,nil);
                return ;
            }
            else if(obj && [obj isKindOfClass:[NSString class]]){
                invocation(NO,obj);
                return;
            }
            invocation(NO,@"设置背景图失败");
            
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)tableUpdateInvocation:(void(^)(void))completion{
    [ProjectHelper helper_getMainThread:^{
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < self.dynamicDataSourceArr.count; i ++) {
            YiChatDynamicDataSource *data = self.dynamicDataSourceArr[i];
            if(data && [data isKindOfClass:[YiChatDynamicDataSource class]]){
                if(data.showCommentListStrArr && [data.showCommentListStrArr isKindOfClass:[NSArray class]]){
                    [arr addObject:[NSNumber numberWithInteger:data.showCommentListStrArr.count]];
                }
                else{
                    [arr addObject:[NSNumber numberWithInteger:0]];
                }
            }
        }
        
        self.controlVC.sectionsRowsNumSet = arr;;
        
        [UIView transitionWithView:self.controlVC.cTable
                          duration:.2f
                           options:UIViewAnimationOptionTransitionCrossDissolve
                        animations:^{
                            [self.controlVC.cTable reloadData];
                        } completion:^(BOOL finished) {
                            
                        }];
        
        
        completion();
    }];
}

- (YiChatDynamicDataSource *)getDataSourceWithIndex:(NSInteger)section{
    if(self.dynamicDataSourceArr.count - 1 >= section){
        return self.dynamicDataSourceArr[section];
    }
    return nil;
}

- (void)copyCommitWithIndexPath:(NSIndexPath *)index{
    if(index && [index isKindOfClass:[NSIndexPath class]]){
        YiChatDynamicDataSource *dataSource = [self getDataSourceWithIndex:index.section];
        if(dataSource && [dataSource isKindOfClass:[YiChatDynamicDataSource class]]){
            
            NSArray *commetStr = dataSource.commentStrArr;
            
            if(commetStr.count - 1 >= index.row){
                NSString *str = commetStr[index.row];
                
                if(str && [str isKindOfClass:[NSString class]]){
                    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
                    pasteboard.string = str;
                }
            }
        }
    }
}

- (void)deleteCommitWithIndexPath:(NSIndexPath *)index{
    if(index && [index isKindOfClass:[NSIndexPath class]]){
        [self removeCommentWithIndex:index];
    
    }
}
@end

