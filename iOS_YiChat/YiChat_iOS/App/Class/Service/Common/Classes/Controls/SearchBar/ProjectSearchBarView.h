//
//  ProjectSearchBarView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger,ProjectSearchBarViewPageStyle) {
    ProjectSearchBarViewPageStyleCurrentPageSearch=0,
    ProjectSearchBarViewPageStyleSearchPageSearch
};

@interface ProjectSearchBarView : UIView

@property (nonatomic,strong) NSString *placeHolder;

@property (nonatomic,copy) void(^projectSearchBarSearchResult)(id obj);
    
@property (nonatomic,copy) void(^projectSearchBarInputResult)(id obj);


- (void)getSearchOriginData:(id(^)(void))getDataInvocation;
    
/**
 *  type == 1 进入搜索页面搜素 type == 0 在当前页面搜素
 */
- (void)initialSearchType:(NSInteger)type;

/**
 * style == 0 message搜素
 * style == 1 connection搜索
 * style == 2 addFriends
 */
- (void)initialSearchStyle:(NSInteger)style;

- (void)createUI;

- (void)refreshUI;

- (void)resignKeyBoard;

@end

NS_ASSUME_NONNULL_END
