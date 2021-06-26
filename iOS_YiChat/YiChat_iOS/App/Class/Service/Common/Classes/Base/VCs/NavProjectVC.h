//
//  NavProjectVC.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "BaseProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

/**
 *  导航条样式 分为logo 和普通
 *    LOGO 样式
 *      1.为左右图片按钮
 *    普通 样式
 *      1.左边图片返回按钮 中间文字title
 *      2.左边图片返回按钮 右边图片按钮 中间文字title
 *      3.左边图片返回按钮 右边文字按钮 中间文字title
 *      4.左边图片按钮 右边图片按钮 中间文件title
 *      5.中间文字title
 *      6.中间文件title 右边文字按钮
 *      7.无导航条
 *      8.左边图片按钮 右边文字按钮 中间文件title
 *      9.中间文件title 右边图片按钮
 *      10.左边图片返回按钮 文字title靠左 右边二个可点击的文字按钮 设有背景色
 *      11.左边文字按钮 中间文字  右边文字按钮
 *      12.左边文字按钮 中间文字 右边图片按钮
 *      13.左边文字图片返回按钮 中间文字 右边文字按钮
 *      14.左边文字图片返回按钮 中间文字 右边图片按钮
 */
typedef NS_ENUM(NSUInteger,ProjectNavBarStyle) {
    ProjectNavBarStyleLOGO_1=0,ProjectNavBarStyleCommon_1=10,ProjectNavBarStyleCommon_2,ProjectNavBarStyleCommon_3,ProjectNavBarStyleCommon_4,ProjectNavBarStyleCommon_5,ProjectNavBarStyleCommon_6,ProjectNavBarStyleCommon_7,ProjectNavBarStyleCommon_8,ProjectNavBarStyleCommon_9,ProjectNavBarStyleCommon_10,ProjectNavBarStyleCommon_11,ProjectNavBarStyleCommon_12,ProjectNavBarStyleCommon_13,ProjectNavBarStyleCommon_14
};

@interface NavProjectVC : BaseProjectVC

@property (nonatomic,assign) BOOL isNeedBackText;

@property (nonatomic,assign) CGFloat navH;
@property (nonatomic,assign) CGFloat statusH;
@property (nonatomic,assign) CGFloat totalNavH;

@property (nonatomic,strong) UILabel *backLab;

+ (id)initialVCWithNavBarStyle:(ProjectNavBarStyle)navBarStyle  centeritem:(id)centerItem leftItem:(id)leftItem rightItem:(id)rightItem;

- (void)setNavH:(CGFloat)navH;

- (void)makeUIForConnecting;

- (void)makeUIForConnect;

- (void)makeUIForDisconnect;

- (void)navBarButtonLeftItemMethod:(UIButton *)btn;

- (void)navBarButtonRightItemMethod:(UIButton *)btn;

- (void)navBarButtonRightItem_1Method:(UIButton *)btn;

- (void)navBarAddNetStatusAppearance;

- (void)removeNetStatusAppearance;

- (UIView *)navBarGetNavBar;

- (UIView *)navBarGetNavBarStatusView;

- (id)navBarGetLeftBarItem;

- (id)navBarGetCenterBarItem;

- (id)navBarGetRightBarItem;

@end

NS_ASSUME_NONNULL_END
