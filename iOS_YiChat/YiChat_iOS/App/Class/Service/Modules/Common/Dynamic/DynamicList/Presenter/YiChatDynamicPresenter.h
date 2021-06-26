//
//  YiChatDynamicPresenter.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YiChatDynamicDataSource.h"
NS_ASSUME_NONNULL_BEGIN
@class YiChatDynamicVC;

@interface YiChatDynamicPresenter : NSObject

@property (nonatomic,strong) NSString *dynamicUserId;

@property (nonatomic,weak)  YiChatDynamicVC *controlVC;

- (id)initWithUserId:(NSString *)userId;

- (YiChatDynamicDataSource *)getDataSourceWithIndex:(NSInteger)section;

- (void)addHeaderRefresh;

- (void)HeaderRefreshEnd;

- (void)addLoadMore;

- (void)addtoolCommitLikeBar;

- (void)addDynamicHeader;

- (void)toolCommitLikeBarAppearWithPoint:(CGPoint)point model:(YiChatDynamicDataSource *)model index:(NSInteger )section;

- (void)toolCommitLikeBarDisappear;

- (void)scrolltoolCommitLikeBarDisappear;

- (void)addCommitView;

- (void)commitViewActive:(NSString *)trandId;

- (void)commitViewResign;

- (void)refreshDynamicList;

- (void)loadDynamicListMore;

- (void)removeDynamicWithTrendId:(NSString *)trendId indx:(NSInteger)indx;

- (void)copyCommitWithIndexPath:(NSIndexPath *)index;

- (void)deleteCommitWithIndexPath:(NSIndexPath *)index;
@end

NS_ASSUME_NONNULL_END
