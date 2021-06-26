//
//  GJProgressView.h
//  GJCamera
//
//  Created by 郭杰 on 2018/9/22.
//  Copyright © 2018年 郭杰. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GJProgressView : UIView

    @property (assign, nonatomic) NSInteger timeMax;
//  颜色
    @property (nonatomic, strong) UIColor *lineColor;
    
- (void)clearProgress;

@end
