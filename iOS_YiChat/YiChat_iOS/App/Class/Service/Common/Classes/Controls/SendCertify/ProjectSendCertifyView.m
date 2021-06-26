//
//  ProjectSendCertifyView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectSendCertifyView.h"
#import "ServiceGlobalDef.h"
#import "ProjectHelper.h"

typedef NS_ENUM(NSUInteger,ProjectSendCertifyCodeState){
    ProjectSendCertifyCodeStateUnsend=0,
    ProjectSendCertifyCodeStateSending
};


typedef BOOL(^ProjectSendCertifyViewInvocation) (void);

@interface ProjectSendCertifyView ()

{
    UILabel *_cSendCertifyAppearLab;
    UIButton *_cSendCertifyClickBtn;
    
}

@property (nonatomic,copy) ProjectSendCertifyViewInvocation click;
@property (nonatomic) ProjectSendCertifyCodeState codeState;
@property (nonatomic) NSInteger num;
@property (nonatomic,strong)  NSTimer *timer;

@end

@implementation ProjectSendCertifyView

+ (instancetype)buildObjWithFrame:(NSValue *)rectValue{
    return [[self alloc] initWithFrame:rectValue.CGRectValue];
}

- (void)createUI{
    
    _codeState = ProjectSendCertifyCodeStateUnsend;
    _num = 60;
    
    _cSendCertifyAppearLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0,self.frame.size.width, self.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(12.0) textColor:[UIColor whiteColor] textAlignment:NSTextAlignmentCenter];
    [self addSubview:_cSendCertifyAppearLab];
    
    _cSendCertifyClickBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:_cSendCertifyAppearLab.frame target:self method:@selector(clickMethod:)];
    [self addSubview:_cSendCertifyClickBtn];
    
    [self changeUIWithState:_codeState];
    
    
}

- (void)changeUIWithState:(ProjectSendCertifyCodeState)state{
    if(state == ProjectSendCertifyCodeStateUnsend){
        _cSendCertifyAppearLab.textColor = PROJECT_COLOR_BlLUE;
        _cSendCertifyAppearLab.text = @"获取验证码";
    }
    else{
        _cSendCertifyAppearLab.text = [NSString stringWithFormat:@"%ld%@",_num,@"s"];
        _cSendCertifyAppearLab.textColor = PROJECT_COLOR_BlLUE;
        
        _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerMethod:) userInfo:nil repeats:YES];
    }
}

- (void)sendCertify{
    [self clickMethod:_cSendCertifyClickBtn];
}

- (void)timerMethod:(NSTimer *)timer{
    _num -- ;
    if(_num >= 1){
        _cSendCertifyAppearLab.text = [NSString stringWithFormat:@"%ld%@",_num,@"s"];
    }
    else{
        _codeState = ProjectSendCertifyCodeStateUnsend;
        
        [_timer invalidate];
        _timer = nil;
        _num = 60.0;
        
        [self changeUIWithState:_codeState];
    }
}



- (void)clickMethod:(UIButton *)btn{
    WS(weakSelf);
    
    if(self.codeState == ProjectSendCertifyCodeStateUnsend){
        if(weakSelf.click){
            [ProjectHelper helper_getGlobalThread:^{
                BOOL isSuccess = weakSelf.click();
                if(isSuccess){
                    weakSelf.codeState = ProjectSendCertifyCodeStateSending;
                    [ProjectHelper helper_getMainThread:^{
                        [weakSelf changeUIWithState:weakSelf.codeState];
                    }];
                }
            }];
        }
       
    }
    else{
        
    }
}

- (void)addInvocation:(NSDictionary *)invocation{
    if([invocation.allKeys containsObject:@"click"]){
        _click = invocation[@"click"];
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
