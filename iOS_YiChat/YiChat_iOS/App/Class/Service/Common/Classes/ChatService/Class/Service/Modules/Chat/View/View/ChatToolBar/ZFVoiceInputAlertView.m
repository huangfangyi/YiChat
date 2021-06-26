//
//  ZFVoiceInputAlertView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFVoiceInputAlertView.h"
#import "ZFChatGlobal.h"

@interface ZFVoiceInputAlertView ()

{
    ZFVoiceVolumnCheck *_check;
    UIView *_speakView;
    UIView *_cancleSpeakView;
    CGFloat _volumnValue;
}


@end

@implementation ZFVoiceInputAlertView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
    }
    return self;
}

- (void)makeUI{
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:self.bounds backGroundColor:[UIColor blackColor]];
    back.alpha=0.7;
    back.layer.cornerRadius=5;
    [self addSubview:back];
    
    _speakView = [self makeSpeakView];
    _cancleSpeakView = [self makeCancleSpeakView];
    
    _cancleSpeakView.hidden = YES;
    
}

//录音状态 == 1 提示取消状态 == 0
- (void)changeUIWithState:(NSInteger)state{
    
    if(state == 1){
        _cancleSpeakView.hidden = YES;
        _speakView.hidden = NO;
    }
    else if(state == 0){
        _cancleSpeakView.hidden = NO;
        _speakView.hidden = YES;
    }
}

- (void)changeVolumn:(CGFloat)value{
    _volumnValue = value;
    
    [_check changeUIWithVolumnValue:value];
}

- (UIView *)makeCancleSpeakView{
    
    UIView *cancelSpeakView=[ProjectHelper helper_factoryMakeViewWithFrame:self.bounds backGroundColor:[UIColor clearColor]];
    [self addSubview:cancelSpeakView];
    
    UILabel *alertLab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(5.0, cancelSpeakView.frame.size.height - 15.0 - 5.0, cancelSpeakView.frame.size.width - 10.0, 15.0) andfont:PROJECT_TEXT_FONT_COMMON(13.0) textColor:[UIColor whiteColor] textAlignment:NSTextAlignmentCenter];
    [cancelSpeakView addSubview:alertLab];
    alertLab.text=@"松开手指，取消发送";
    alertLab.layer.cornerRadius = 5.0;
    alertLab.backgroundColor=[UIColor redColor];
    
    UIImage *img = [self getIconWithSpeakState:0];
    
    CGFloat w = 55.0;
    CGFloat h = [ProjectHelper helper_GetWidthOrHeightIntoScale:img.size.width / img.size.height width:w height:0];
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(cancelSpeakView.frame.size.width / 2 - w / 2, (alertLab.frame.origin.y - 10.0) / 2 + 10  - h / 2 , w, h) andImg:img];
    [cancelSpeakView addSubview:icon];
    
    return cancelSpeakView;
}

- (UIView *)makeSpeakView{
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:self.bounds backGroundColor:[UIColor clearColor]];
    [self addSubview:back];
    
    UILabel *alertLab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(5.0, back.frame.size.height - 15.0 - 5.0, back.frame.size.width - 10.0, 15.0) andfont:PROJECT_TEXT_FONT_COMMON(13.0) textColor:[UIColor whiteColor] textAlignment:NSTextAlignmentCenter];
    [back addSubview:alertLab];
    alertLab.text=@"手指上滑，取消发送";
    
    UIImage *img = [self getIconWithSpeakState:1];
    
    CGFloat w = 55.0;
    CGFloat h = [ProjectHelper helper_GetWidthOrHeightIntoScale:img.size.width / img.size.height width:w height:0];
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(back.frame.size.width / 2 - w / 2 - 25.0, (alertLab.frame.origin.y - 10.0) / 2 + 10  - h / 2 , w, h) andImg:img];
    [back addSubview:icon];
    
    ZFVoiceVolumnCheck *check = [[ZFVoiceVolumnCheck alloc] initWithFrame:CGRectMake(icon.frame.origin.x +icon.frame.size.width + 5.0, icon.frame.origin.y + icon.frame.size.height - 5.0 - icon.frame.size.height * 0.9, 35.0, icon.frame.size.height * 0.9)];
    [back addSubview:check];
    check.backgroundColor = [UIColor clearColor];
    [check makeUI];
    _check = check;
    
    return back;
}

/**
 *  录音 state = 1 取消 state = 0
 */
- (UIImage *)getIconWithSpeakState:(NSInteger)state{
    if(state == 0){
        return [UIImage imageNamed:@"news_chat_voice_cancel@3x.png"];
    }
    else{
        return [UIImage imageNamed:@"news_chat_voice_out@3x.png"];
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

@interface ZFVoiceVolumnCheck ()
{
    NSMutableArray<UIView *>*_array;
}
@end

#define VoiceLever 10.0
@implementation ZFVoiceVolumnCheck

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
        [self systemInitial];
    }
    return self;
}

- (void)systemInitial{
    _array = [NSMutableArray arrayWithCapacity:0];
}

- (void)makeUI{
    NSInteger number = VoiceLever;
    CGFloat blank = 3.0;
    CGFloat y = blank;
    CGFloat h = (self.frame.size.height - (number - 1) * blank) / number;
    CGFloat perW=(self.frame.size.width - blank * 2 - 8.0)/ number;
    CGFloat w = self.frame.size.width - blank * 2;
    
    CGFloat itemW = 0;
    for (int i=0; i<number; i++) {
        itemW = - i * perW + w;
        
        UIView *view=[ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(self.frame.size.width / 2 - itemW / 2, y + i * (blank + h),itemW, h) backGroundColor:[UIColor whiteColor]];
        [self addSubview:view];
        [_array addObject:view];
    }
}

- (void)changeUIWithVolumnValue:(CGFloat)value{
    NSLog(@"%f",value);
    
    NSInteger temp = (30 - value) / 30 * VoiceLever;
    
    //    NSLog(@"show %ld",temp);
    for (NSInteger i = 0; i<_array.count; i++) {
        if(i <  VoiceLever - temp){
            _array[i].hidden = YES;
        }
        else{
            _array[i].hidden = NO;
        }
    }
}

@end

