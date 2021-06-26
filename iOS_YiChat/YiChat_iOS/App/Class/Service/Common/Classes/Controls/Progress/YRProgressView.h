//
//  YRProgressView.h
//  NcNetcarDrive
//
//  Created by yunlian on 2017/6/16.
//  Copyright © 2017年 YANG RUI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YRGeneralApis.h"
typedef NS_ENUM(NSUInteger,YRProgressViewStyle) {
    YRProgressViewStyleShowDefault=0, //默认是加载小转圈
    YRProgressViewStyleShowProgressingText, // 加载小转圈
    YRProgressViewStyleShowProgressValue //加载进度小转圈
};

@interface YRProgressView : UIView

+ (id)showProgressViewWithFrame:(CGRect)frame Style:(YRProgressViewStyle)progressStyle text:(NSString *)text;

//加载动画
+ (id)showProgressViewWithProgressText:(NSString *)text;

+ (id)showProgressViewWithProgressValue:(double)progress;

- (void)setProgressvalueWithValue:(NSString *)value;

- (void)setProgressvalueWithValue:(NSString *)value text:(NSString *)text;

- (void)setProgressText:(NSString *)text;

- (void)hidden;

- (void)updateUI;
@end
