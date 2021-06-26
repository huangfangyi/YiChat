//
//  ProjectRefreshAnimateView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectRefreshAnimateView : UIView

@property (nonatomic,assign) BOOL isRereshing;

+ (instancetype)createAnimateView;

- (void)animate;

- (void)stopAnimate;

- (void)beginAnimateToPoin:(CGPoint)point;

- (void)endAnimateToPoint:(CGPoint)point;

- (void)animateFromPoint:(CGPoint )pointBegin pointEnd:(CGPoint)pointEnd isNeedStop:(BOOL)isNeed;

@end

NS_ASSUME_NONNULL_END
