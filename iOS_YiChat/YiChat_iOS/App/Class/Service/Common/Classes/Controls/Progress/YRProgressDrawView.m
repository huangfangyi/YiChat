//
//  YRProgressDrawView.m
//  NcNetcarDrive
//
//  Created by yunlian on 2017/6/19.
//  Copyright © 2017年 YANG RUI. All rights reserved.
//

#import "YRProgressDrawView.h"
@interface YRProgressDrawView ()
{
    NSString *_dProgressValue;
    CGFloat _dLineWidth;
    UIColor *_dLineColor;
    CGFloat _dRadius;
    YRProgressDrawnStyle _dProgressStyle;

}

@property (nonatomic,strong) UILabel *progressLab;
@property (nonatomic,assign) CGFloat dBeginRadius;
@property (nonatomic,assign) CGFloat dEndRadius;
@property (nonatomic,assign) CGFloat number;
@property (nonatomic,assign) NSTimer *dTimer;
@end

#define ScreenH [UIScreen mainScreen].bounds.size.height
#define ScreenW  [UIScreen mainScreen].bounds.size.width

#define WS(weakSelf)  __weak __typeof(&*self)weakSelf = self;

#define YRProgressDrawView_CircleColor [UIColor blackColor]
@implementation YRProgressDrawView


- (void)dealloc{
    
    [_dTimer invalidate];
    _dTimer=nil;
}

- (id)initWithFrame:(CGRect)frame progressStyle:(YRProgressDrawnStyle)progressStyle value:(NSString *)value lineWidth:(CGFloat)width lineColor:(UIColor *)color{
    self=[super initWithFrame:frame];
    if(self){
        [self initialSystemWithValue:value lineWidth:width lineColor:YRProgressDrawView_CircleColor progressStyle:progressStyle];
        
        [self drawnUI];
        
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame progressStyle:(YRProgressDrawnStyle)progressStyle{
    self=[super initWithFrame:frame];
    if(self){
        [self initialSystemWithLineWidth:3 lineColor:YRProgressDrawView_CircleColor progressStyle:progressStyle];
        
        [self drawnUI];
    }
    return self;
}

- (void)initialSystemWithValue:(NSString *)value lineWidth:(CGFloat)width lineColor:(UIColor *)color progressStyle:(YRProgressDrawnStyle)progressStyle{
    _dProgressValue=value;
    _dLineWidth=width;
    _dLineColor=color;
    _dProgressStyle=progressStyle;
    _number=0;
    _dRadius=self.frame.size.width / 7;
    _dBeginRadius=0;
    _dEndRadius=[value floatValue] * 2 * M_PI;
}

- (void)initialSystemWithLineWidth:(CGFloat)width lineColor:(UIColor *)color progressStyle:(YRProgressDrawnStyle)progressStyle{
    _dLineWidth=width;
    _dLineColor=color;
    _dProgressStyle=progressStyle;
    _number=0.2;
    _dRadius=self.frame.size.width / 8;
    _dBeginRadius=0;
}

- (void)setProgressRadius:(CGFloat)radius{
    _dRadius=radius;
    [self setNeedsDisplay];
}

- (void)drawnUI{
    if(_dProgressStyle == YRProgressDrawnStyleAnimate){
        [self makeAnimate];
    }
    else if(_dProgressStyle == YRProgressDrawnStyleProgressAnimate){
        
        _dBeginRadius = 0;
        _dEndRadius = 0;
        
        self.progressLab.text = @"0.0%";
        
        [self setNeedsDisplay];
    }
}

- (UILabel *)progressLab{
    if(!_progressLab){
        CGFloat w = _dRadius;
        _progressLab = [[UILabel alloc] initWithFrame:CGRectMake(self.frame.size.width / 2 - w / 2, self.frame.size.height / 2 - w / 2,w , w)];
        _progressLab.textColor = [UIColor whiteColor];
        _progressLab.font = [UIFont systemFontOfSize:13];
        _progressLab.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_progressLab];
    }
    return _progressLab;
}

#pragma mark animate

- (void)makeAnimate{
    
    [self beginPlayWithScaleLocation:0];
    
    WS(weakSelf);
    
    _dTimer=[NSTimer scheduledTimerWithTimeInterval:0.04 target:self selector:@selector(animateProgressTimerMethod:) userInfo:nil repeats:YES];
    
    [[NSRunLoop mainRunLoop] addTimer:_dTimer forMode:NSDefaultRunLoopMode];
    [[NSRunLoop mainRunLoop] addTimer:_dTimer forMode:UITrackingRunLoopMode];
}

- (void)animateProgressTimerMethod:(NSTimer *)timer{
    _number += (2 * M_PI * 0.1);
    if(_number >= 2 * M_PI){
        _number = 0;
        _dBeginRadius = 0;
    }
    [self beginPlayWithScaleLocation:_number];
}

- (void)stopAnimate{
    [YRGeneralApis yrGeneralApis_getMainThread:^{
        [_dTimer invalidate];
        _dTimer=nil;
        
        [self removeFromSuperview];
    }];
}


- (void)beginPlayWithScaleLocation:(CGFloat)scale{
    
    dispatch_queue_t queue = dispatch_get_main_queue();
    
    dispatch_async(queue, ^{
        _dBeginRadius = _number;
        _dEndRadius =_dBeginRadius + 2 * M_PI * 0.4;
        
        [self setNeedsDisplay];
    });
    
}


#pragma mark  progressAnimate

- (void)setProgressValue:(NSString *)progressValue{
    
    _dProgressValue=progressValue;
    _dBeginRadius = 0;
    if([progressValue floatValue] != 0){
        _dEndRadius= 2 * M_PI * [progressValue floatValue];
    }
    else{
        _dEndRadius = 0;
    }
    if(progressValue.floatValue >=0 && progressValue.floatValue<= 1){
        self.progressLab.text = [NSString stringWithFormat:@"%.1f%@",[progressValue floatValue] * 100,@"%"];
    }
    else if(progressValue.floatValue > 1){
        self.progressLab.text = [NSString stringWithFormat:@"%.1f%@",[progressValue floatValue],@"%"];
    }
    [self setNeedsDisplay];
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    //获得处理的上下文
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetStrokeColorWithColor(context, _dLineColor.CGColor);
    CGContextSetLineWidth(context, _dLineWidth);
    /*
        CGContextAddArc(CGContextRef c, CGFloat x, CGFloat y, // 圆心(x,y)
        CGFloat radius, // 半径
        CGFloat startAngle, CGFloat endAngle, // 开始、结束弧度
        int clockwise // 绘制方向，YES:逆时针;NO:顺时针)
    */
    CGContextAddArc(context, self.frame.size.width / 2, self.frame.size.height / 2, _dRadius, _dBeginRadius, _dEndRadius, 0);
    CGContextDrawPath(context, kCGPathStroke);
}


@end
