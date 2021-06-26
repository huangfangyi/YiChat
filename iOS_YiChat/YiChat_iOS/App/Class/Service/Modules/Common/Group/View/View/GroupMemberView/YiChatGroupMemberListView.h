//
//  YiChatGroupMemberListView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectBaseCollectiionView.h"
#import "YiChatGroupMemberListModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupMemberListView : ProjectBaseCollectiionView

@property (nonatomic,weak) UIViewController *controlVC;

@property (nonatomic,copy) void(^yiChatGroupMemberListViewClickMore)();

@property (nonatomic,copy) void(^yiChatGroupMemberListViewClickItems)(id model);

@property (nonatomic,copy) void(^yiChatGroupMemberListViewDidFreshUI)(CGSize size);
    
@property (nonatomic,copy) NSInteger(^yiChatGroupMemberListViewFetchUserPower)(void);
    
@property (nonatomic,copy) void(^yiChatGroupMemberListViewShutUp)(id model);

- (id)initWithFrame:(CGRect)frame datasoRerces:(NSArray *)dataSource isHasAdd:(BOOL)isHasAdd isHasDelete:(BOOL)isHasDelete isLoadAll:(BOOL)isLoadAll;

- (void)changeIsHasAdd:(BOOL)isHasAdd isHasDelete:(BOOL)isHasDelete;

- (void)changeDataSource:(NSArray *)dataSource;

- (void)updateAddDeleteUIData;

- (void)refreshUI;
    
- (void)removeMenu;

@end

NS_ASSUME_NONNULL_END
