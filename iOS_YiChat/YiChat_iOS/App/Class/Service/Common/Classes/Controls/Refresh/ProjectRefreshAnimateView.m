//
//  ProjectRefreshAnimateView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectRefreshAnimateView.h"
#import "ProjectDef.h"
#import "ProjectRefreshApis.h"

@interface ProjectRefreshAnimateView ()

@property (nonatomic,strong) NSArray <UIImage *>*icons;

@property (nonatomic,strong) UIImageView *animateImage;

@end

@implementation ProjectRefreshAnimateView

+ (instancetype)createAnimateView{
    
    return [[self alloc] initWithFrame:CGRectMake(0, 0, 10, 10)];
}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _icons = [ProjectRefreshApis getRefresAnimateImages];
        
        UIImage *icon = _icons.lastObject;
        
        CGFloat w = 30.0;
        CGFloat h = [ProjectHelper helper_GetWidthOrHeightIntoScale:icon.size.width / icon.size.height width:w height:0];
        
        self.frame = CGRectMake(0, 0, w, h);
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) andImg:nil];
    _animateImage = icon;
    icon.animationDuration = 0.4;
    [self addSubview:icon];
    
}

- (void)animateAtPoint:(CGPoint)point{
    _animateImage.animationImages = _icons;
    self.frame = [self getAniamtePosionIntoPoint:point];
    [_animateImage startAnimating];
    
}

- (void)stopAnimate{
    [_animateImage stopAnimating];
    _animateImage.animationImages = nil;
}

- (void)animateToPoin:(CGPoint)point isNeedStop:(BOOL)isNeed{
   
    CGRect end = [self getAniamtePosionIntoPoint:point];
    _animateImage.animationImages = _icons;
    [_animateImage startAnimating];
    
    WS(weakSelf);
    [UIView animateWithDuration:0.5 animations:^{
        weakSelf.frame = end;
    } completion:^(BOOL finished) {
        if(finished){
            if(isNeed == YES){
                _isRereshing = NO;
                [weakSelf.animateImage stopAnimating];
                weakSelf.animateImage.animationImages = nil;
            }
        }
    }];
}

- (void)beginAnimateToPoin:(CGPoint)point{
    if(_isRereshing == YES){
        return;
    }
    _isRereshing = YES;
    [self animateToPoin:point isNeedStop:NO];
}

- (void)endAnimateToPoint:(CGPoint)point{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self animateToPoin:point isNeedStop:YES];
    });
}

- (void)animateFromPoint:(CGPoint )pointBegin pointEnd:(CGPoint)pointEnd isNeedStop:(BOOL)isNeed{
    CGRect begin = [self getAniamtePosionIntoPoint:pointBegin];
    CGRect end = [self getAniamtePosionIntoPoint:pointEnd];
    _animateImage.animationImages = _icons;
    [_animateImage startAnimating];
    
    self.frame = begin;
    WS(weakSelf);
    [UIView animateWithDuration:0.5 animations:^{
        weakSelf.frame = end;
    } completion:^(BOOL finished) {
        if(isNeed == YES){
            [weakSelf.animateImage stopAnimating];
            weakSelf.animateImage.animationImages = nil;
        }
    }];
}

- (CGRect)getAniamtePosionIntoPoint:(CGPoint)point{
    return  CGRectMake(point.x - self.frame.size.width / 2, point.y - self.frame.size.height / 2, self.frame.size.width, self.frame.size.height);
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
