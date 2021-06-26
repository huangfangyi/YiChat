//
//  ProjectAnimateView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/17.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectAnimateView : UIView

@property (nonatomic,assign) BOOL isShow;

@property (nonatomic,strong) UIView *controlUIBackView;

@property (nonatomic,strong) UIView *backView;

@property (nonatomic,copy) void(^ProjectAnimateWillDisappear)();

+ (id)appearWithControlUIFrame:(CGRect)frame;

- (ProjectAnimateView *(^)(UIView *))addSubView;

- (void)showAnimateCompletionHandle:(void(^)(void))handle;

- (void)disappearAnimateCompletionHandle:(void(^)(void))handle;

@end

NS_ASSUME_NONNULL_END
