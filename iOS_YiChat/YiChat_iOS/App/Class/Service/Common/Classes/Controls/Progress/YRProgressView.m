//
//  YRProgressView.m
//  NcNetcarDrive
//
//  Created by yunlian on 2017/6/16.
//  Copyright © 2017年 YANG RUI. All rights reserved.
//

#import "YRProgressView.h"
#import "YRProgressDrawView.h"
#import "ServiceGlobalDef.h"
@interface YRProgressView ()
{
    UIView *_cBackView;
    UIView *_cControlBack;
    
    //小数
    NSString *_dProgressValue;
    NSString *_dAlertText;
    
    CGSize _dProgerssSize;
    CGSize _dTextSize;
    
    YRProgressViewStyle _dStyle;
    NSInteger _dType;
    
    YRProgressDrawView *_cDrawnView;
    UILabel *_cProgressText;
}
@end

#define ScreenH [UIScreen mainScreen].bounds.size.height
#define ScreenW  [UIScreen mainScreen].bounds.size.width
@implementation YRProgressView

+ (id)showProgressViewWithFrame:(CGRect)frame Style:(YRProgressViewStyle)progressStyle text:(NSString *)text{
    return [[self alloc] initWithFrame:frame progressStyle:progressStyle text:text];
}

//加载
+ (id)showProgressViewWithProgressText:(NSString *)text{
    return [[self alloc] initWithFrame:CGRectMake(0, 0, ScreenW, ScreenH) progressStyle:YRProgressViewStyleShowDefault text:text];
}

//进度百分比
+ (id)showProgressViewWithProgressValue:(double)progress{
    return [[self alloc] initWithFrame:CGRectMake(0, 0, ScreenW, ScreenH) progressStyle:YRProgressViewStyleShowProgressValue text:[NSString stringWithFormat:@"%.1f%@",progress * 100,@"%"]];
}

- (id)initWithFrame:(CGRect)frame progressStyle:(YRProgressViewStyle)progressStyle text:(NSString *)text{
    self=[super initWithFrame:frame];
    if(self){
        [self systemInitialWithStyle:progressStyle text:text];
        
        [self makeBackView];
        
        [self makeControlBack];
    }
    return self;
}

- (void)updateUI{
    _dProgerssSize=[self getProgressSectionSize];
    _dTextSize=[self getProgressViewTextSize];
    _cBackView.frame = self.bounds;
    CGSize size=CGSizeZero;
    CGRect drawnViewRect = CGRectZero;
    CGRect textRect = CGRectZero;
    
    if(_dStyle == YRProgressViewStyleShowProgressValue){
        size = CGSizeMake(_dProgerssSize.width, _dProgerssSize.height);
        drawnViewRect = CGRectMake(_cControlBack.frame.size.width / 2 - _dProgerssSize.width / 2,0, _dProgerssSize.width, _dProgerssSize.height);
        textRect = CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cControlBack.frame.size.height - _dTextSize.height, _dTextSize.width, _dTextSize.height);
        
    }
    else{
        size=CGSizeMake(_dProgerssSize.width, _dProgerssSize.height + _dTextSize.height);
        drawnViewRect =  CGRectMake(_cControlBack.frame.size.width / 2 - _dProgerssSize.height / 2,0, _dProgerssSize.width, _dProgerssSize.height);
        textRect = CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cControlBack.frame.size.height - _dTextSize.height, _dTextSize.width, _dTextSize.height);
        
    }
    
    _cControlBack.frame = CGRectMake(self.frame.size.width / 2 -size.width / 2, self.frame.size.height / 2  - size.height / 2, size.width, size.height);
    
    _cDrawnView.frame = drawnViewRect;
    
    _cProgressText.frame = textRect;
    
}

- (void)systemInitialWithStyle:(YRProgressViewStyle)progressStyle text:(NSString *)text{
    _dStyle=progressStyle;
    
    if(progressStyle == 0  || progressStyle == 1){
        _dAlertText=text;
        _dProgerssSize=[self getProgressSectionSize];
        _dTextSize=[self getProgressViewTextSize];
        
        [[YRGeneralApis yrGeneral_ApisGetAppDelegate].window addSubview:self];
        [[YRGeneralApis yrGeneral_ApisGetAppDelegate].window bringSubviewToFront:self];
    }
    else{
        _dAlertText=text;
        _dProgressValue=text;
        _dProgerssSize=[self getProgressSectionSize];
        _dTextSize=[self getProgressViewTextSize];
        
        [[YRGeneralApis yrGeneral_ApisGetAppDelegate].window addSubview:self];
        [[YRGeneralApis yrGeneral_ApisGetAppDelegate].window bringSubviewToFront:self];
    }
    
}

- (void)makeBackView{
    _cBackView=[YRGeneralApis yrGeneralApis_FactoryMakeViewWithFrame:self.bounds backGroundColor:[UIColor clearColor]];
    [self addSubview:_cBackView];
}

- (void)makeControlBack{
    [self performSelector:@selector(backAnimate) withObject:nil afterDelay:0.2];
}

- (void)backAnimate{
    
    CGSize size=CGSizeZero;
    
    if(_dStyle == YRProgressViewStyleShowProgressValue){
        size = CGSizeMake(_dProgerssSize.width, _dProgerssSize.height);
    }
    else{
        size=CGSizeMake(_dProgerssSize.width, _dProgerssSize.height + _dTextSize.height);
    }
    
    _cControlBack=[YRGeneralApis yrGeneralApis_FactoryMakeViewWithFrame:CGRectMake(self.frame.size.width / 2 -size.width / 2, self.frame.size.height / 2  - size.height / 2, size.width, size.height) backGroundColor:[UIColor whiteColor]];
    
    _cControlBack.layer.cornerRadius=10;
    [self addSubview:_cControlBack];
    
    [self makeControlBackSubviews];
    
    [UIView animateWithDuration:0.2 animations:^{
        _cBackView.backgroundColor = [UIColor blackColor];
        _cBackView.alpha = 0.5;
    }];
}

- (void)makeControlBackSubviews{
    [self makeProgressControl];
    
    [self makeTextControl];
    
    [self adJustSubFrame];
    
}

- (void)makeProgressControl{
    CGSize progressSize=_dProgerssSize;
    
    if(_dStyle == YRProgressViewStyleShowDefault || _dStyle == YRProgressViewStyleShowProgressingText){
        
        CGFloat w=progressSize.width;
        CGFloat h=progressSize.height;
        
        _cDrawnView=[[YRProgressDrawView alloc] initWithFrame:CGRectMake(_cControlBack.frame.size.width / 2 - w / 2,0, w, h) progressStyle:YRProgressDrawnStyleAnimate];
        [_cControlBack addSubview:_cDrawnView];
        
    }
    else{
        CGFloat w=progressSize.width;
        CGFloat h=progressSize.height;
        
        _cDrawnView=[[YRProgressDrawView alloc] initWithFrame:CGRectMake(_cControlBack.frame.size.width / 2 - w / 2,progressSize.height / 2  - h / 2, w, h) progressStyle:YRProgressDrawnStyleProgressAnimate value:_dProgressValue lineWidth:3 lineColor:[UIColor whiteColor]];
        [_cControlBack addSubview:_cDrawnView];
    }
    _cDrawnView.backgroundColor=[UIColor clearColor];
}

- (void)makeTextControl{
    
    if(_dStyle == YRProgressViewStyleShowDefault || _dStyle == YRProgressViewStyleShowProgressingText){
        _cProgressText=[YRGeneralApis yrGeneralApis_FactoryMakeLabelWithFrame:CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cControlBack.frame.size.height - _dTextSize.height, _dTextSize.width, _dTextSize.height) andfont:PROJECT_TEXT_FONT_COMMON(16.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
        _cProgressText.numberOfLines=0;
        _cProgressText.text=_dAlertText;
        _cProgressText.backgroundColor=[UIColor clearColor];

    }
    else{
        _cProgressText=[YRGeneralApis yrGeneralApis_FactoryMakeLabelWithFrame:CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cControlBack.frame.size.height - _dTextSize.height, _dTextSize.width, _dTextSize.height) andfont:PROJECT_TEXT_FONT_COMMON(16.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
         _cProgressText.numberOfLines=0;
        _cProgressText.text=_dAlertText;
        _cProgressText.backgroundColor=[UIColor clearColor];
        
        _cProgressText.hidden = YES;
    }
    [_cControlBack addSubview:_cProgressText];
}

- (void)setProgressvalueWithValue:(NSString *)value{
    dispatch_queue_t queue = dispatch_get_main_queue();
    dispatch_async(queue, ^{
        _dProgressValue = value;
        [_cDrawnView setProgressValue:value];
        
        _cProgressText.hidden = YES;
    });
}

- (void)setProgressText:(NSString *)text{
    dispatch_queue_t queue = dispatch_get_main_queue();
    dispatch_async(queue, ^{
        _dAlertText = text;
        
        _cProgressText.text = text;
        _cProgressText.hidden = NO;
        
        [self adJustSubFrame];
        
    });
}

- (void)adJustSubFrame{
    _dTextSize = [self getProgressViewTextSize];
    
    CGSize size=CGSizeZero;
    
    if(_dStyle == YRProgressViewStyleShowProgressValue){
        size = CGSizeMake(_dProgerssSize.width, _dProgerssSize.height);
    }
    else{
        size=CGSizeMake(_dProgerssSize.width, _dProgerssSize.height + _dTextSize.height);
    }
    
    _cControlBack.frame  = CGRectMake(self.frame.size.width / 2 -size.width / 2, self.frame.size.height / 2  - size.height / 2, size.width, size.height);
    
    CGSize progressSize=_dProgerssSize;
    
    if(_dStyle == YRProgressViewStyleShowDefault || _dStyle == YRProgressViewStyleShowProgressingText){
        
        CGFloat w=progressSize.width;
        CGFloat h=progressSize.height;
        
         _cDrawnView.frame = CGRectMake(_cControlBack.frame.size.width / 2 - w / 2,0, w, h);
    }
    else{
        CGFloat w=progressSize.width;
        CGFloat h=progressSize.height;
        
        _cDrawnView.frame = CGRectMake(_cControlBack.frame.size.width / 2 - w / 2,progressSize.height / 2  - h / 2, w, h);
    }
    
    if(_dStyle == YRProgressViewStyleShowDefault || _dStyle == YRProgressViewStyleShowProgressingText){
        _cProgressText.frame = CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cDrawnView.frame.size.height + _cDrawnView.frame.origin.y, _dTextSize.width, _dTextSize.height);
    }
    else{
        _cProgressText.frame = CGRectMake(_cControlBack.frame.size.width / 2 - _dTextSize.width / 2, _cControlBack.frame.size.height - _dTextSize.height, _dTextSize.width, _dTextSize.height);
    }
}

- (void)setProgressvalueWithValue:(NSString *)value text:(NSString *)text{
    dispatch_queue_t queue = dispatch_get_main_queue();
    dispatch_async(queue, ^{
        _cProgressText.hidden = NO;
        _dProgressValue = value;
        [_cDrawnView setProgressValue:value];
        _cProgressText.text = text;
    });
}

- (void)hidden{
    [YRGeneralApis yrGeneralApis_getMainThread:^{
        if(_dStyle == 0  || _dStyle == 1){
            [_cDrawnView stopAnimate];
        }
        else{
        }
        for (id temp in self.subviews) {
            [temp removeFromSuperview];
        }
        [self removeFromSuperview];
    }];
}

- (CGFloat)getGeneralW{
    return self.frame.size.width  * 0.6;
}

- (CGSize)getProgressViewTextSize{
    if(_dStyle == YRProgressViewStyleShowProgressValue){
        CGSize progressSize = [self getProgressSectionSize];
        return CGSizeMake(progressSize.width / 2, progressSize.height / 2);
    }
    else{
        if(_dAlertText && [_dAlertText isKindOfClass:[NSString class]]){
            if(_dAlertText.length > 0){
                CGRect rect=[YRGeneralApis yrGeneralApis_FactoryGetFontSizeWithString:_dAlertText useFont:16 withWidth:MAXFLOAT andHeight:MAXFLOAT];
                CGFloat w=0;
                CGFloat h=0;
                CGFloat generalW=[self getGeneralW];
                if(rect.size.width < generalW){
                    w=generalW;
                }
                else{
                    w=rect.size.width + 15.0 * 2;
                }
                
                h=rect.size.height + 15.0 * 2;
                
                return CGSizeMake(w, h);
            }
        }
        return CGSizeMake(0, 0);
    }
}

- (CGSize)getProgressSectionSize{
    if(_dStyle == YRProgressViewStyleShowProgressValue){
        if(self.frame.size.width >= self.frame.size.height){
            return CGSizeMake(self.frame.size.height * 0.8, self.frame.size.height * 0.8);
        }
        else{
            return CGSizeMake(self.frame.size.width * 0.8, self.frame.size.width * 0.8);
        }
    }
    else{
        if(self.frame.size.width >= self.frame.size.height){
            return CGSizeMake(self.frame.size.height / 3, self.frame.size.height / 3);
        }
        else{
             return CGSizeMake(self.frame.size.width / 3, self.frame.size.width / 3);
        }
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
