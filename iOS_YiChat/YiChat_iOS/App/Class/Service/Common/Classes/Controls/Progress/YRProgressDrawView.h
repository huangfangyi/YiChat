//
//  YRProgressDrawView.h
//  NcNetcarDrive
//
//  Created by yunlian on 2017/6/19.
//  Copyright © 2017年 YANG RUI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YRGeneralApis.h"
typedef NS_ENUM(NSUInteger,YRProgressDrawnStyle) {
    //加载动画
    YRProgressDrawnStyleAnimate=0,
    //进度加载
    YRProgressDrawnStyleProgressAnimate
};
@interface YRProgressDrawView : UIView

//进度转圈
- (id)initWithFrame:(CGRect)frame progressStyle:(YRProgressDrawnStyle)progressStyle value:(NSString *)value lineWidth:(CGFloat)width lineColor:(UIColor *)color;

//转圈动画
- (id)initWithFrame:(CGRect)frame progressStyle:(YRProgressDrawnStyle)progressStyle;

- (void)stopAnimate;

- (void)setProgressRadius:(CGFloat)radius;

- (void)setProgressValue:(NSString *)progressValue;
@end
