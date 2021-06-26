//
//  ProjectSearchView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^ProjectSearchBarSearchViewInvocation)(void);
typedef void(^ProjectSearchMsgResult)(NSInteger searchStyle,id obj);
@interface ProjectSearchView : UIView

@property (nonatomic,copy) void(^projectSearchBarSearchResult)(id obj);
@property (nonatomic,copy) ProjectSearchBarSearchViewInvocation cancelClick;
@property (nonatomic,copy) ProjectSearchMsgResult cellClick;
    
@property (nonatomic,copy) void(^projectSearchBarInputResult)(id obj);

- (id)initWithFrame:(CGRect)frame style:(NSInteger)searchStyle;
    
- (void)getSearchOriginData:(id(^)(void))getDataInvocation;

- (void)animate_begin;

- (void)animate_end:(ProjectSearchBarSearchViewInvocation)invocation;

@end

NS_ASSUME_NONNULL_END
