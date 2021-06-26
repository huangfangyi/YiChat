//
//  ProjectAnimateView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/17.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectAnimateView.h"
#import "ProjectDef.h"

@interface ProjectAnimateView ()

@property (nonatomic,assign) CGRect destinationFrame;

@property (nonatomic,assign) CGRect hiddenFrame;

@end

@implementation ProjectAnimateView

+ (id)appearWithControlUIFrame:(CGRect)frame{
    return [[self alloc] initWithFrame:frame];
}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
        _destinationFrame = frame;
        _hiddenFrame = CGRectMake(frame.origin.x, frame.origin.y + frame.size.height, frame.size.width, frame.size.height);
        
        self.frame = CGRectMake(0, 0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_HEIGHT);
        self.backgroundColor = [UIColor clearColor];
        
        UIView *back = [[UIView alloc] initWithFrame:self.bounds];
        self.backView = back;
        [self addSubview:back];
        back.backgroundColor = [UIColor blackColor];
        back.alpha = 0.3;
        
        
        UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        clearBtn.frame = self.bounds;
        [self addSubview:clearBtn];
        [clearBtn addTarget:self action:@selector(disappear) forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:self.controlUIBackView];
        
        
    }
    return self;
}

- (void)disappear{
    [self disappearAnimateCompletionHandle:^{
        
    }];
}

- (UIView *)controlUIBackView{
    if(!_controlUIBackView){
        _controlUIBackView = [[UIView alloc] initWithFrame:_hiddenFrame];
        _controlUIBackView.userInteractionEnabled = YES;
    }
    return _controlUIBackView;
}

- (ProjectAnimateView *(^)(UIView *))addSubView{
    return ^ProjectAnimateView *(UIView * subView){
        [self.controlUIBackView addSubview:subView];
        return self;
    };
}

- (void)showAnimateCompletionHandle:(void(^)(void))handle{
    
    WS(weakSelf);
    _isShow = YES;
    self.hidden = NO;
    [self bringSubviewToFront:self.controlUIBackView];
    
    [UIView animateWithDuration:0.38 animations:^{
        weakSelf.controlUIBackView.frame = weakSelf.destinationFrame;
    } completion:^(BOOL finished) {
        if(finished){
            handle();
        }
    }];
}

- (void)disappearAnimateCompletionHandle:(void(^)(void))handle{
    WS(weakSelf);
    _isShow = NO;
    [UIView animateWithDuration:0.38 animations:^{
        weakSelf.controlUIBackView.frame = weakSelf.hiddenFrame;
        weakSelf.controlUIBackView.alpha = 0;
    } completion:^(BOOL finished) {
        self.hidden = YES;
        if(finished){
            handle();
        }
        if(weakSelf.ProjectAnimateWillDisappear){
            weakSelf.ProjectAnimateWillDisappear();
        }
    }];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
