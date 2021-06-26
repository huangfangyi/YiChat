//
//  ProjectClickView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/1.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectClickView.h"
#import "ServiceGlobalDef.h"

@interface ProjectClickView ()
{
    NSString *_title;
    NSInteger _type;
}
@end

@implementation ProjectClickView

- (id)initWithFrame:(CGRect)frame bgView:(UIView *)vgView{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    UIImageView *icon = [UIImageView new];
    [self addSubview:icon];
    
    UILabel *lab = [UILabel new];
    [self addSubview:lab];
    
    _icon = icon;
    _lab = lab;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = self.bounds;
    [btn addTarget:self action:@selector(clearBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:btn];
}


+ (id)createClickViewWithFrame:(CGRect)frame
                         title:(NSString *)title
                          type:(NSInteger)type{
    return [[self alloc] initWithFrame:frame title:title type:type];
}

- (id)initWithFrame:(CGRect)frame title:(NSString *)title type:(NSInteger)type{
    self = [super initWithFrame:frame];
    if(self){
        _title = title;
        _type = type;
        
        [self maekeUIForTypes];
    }
    return self;
}

- (void)maekeUIForTypes{
    UILabel *lab = [[UILabel alloc] initWithFrame:self.bounds];
    [self addSubview:lab];
    lab.text = _title;
    lab.textAlignment = NSTextAlignmentCenter;
    lab.font = PROJECT_TEXT_FONT_COMMON(14);
    
    if(_type != -1){
        if(_type == 0){
            lab.textColor = [UIColor whiteColor];
            lab.backgroundColor = PROJECT_COLOR_BlLUE;
        }
        else if(_type == 1){
            lab.backgroundColor = [UIColor clearColor];
            lab.textColor = PROJECT_COLOR_BlLUE;
        }
        
        self.layer.cornerRadius = 10.0;
        self.clipsToBounds = YES;
    }
    self.userInteractionEnabled = YES;
    _lab = lab;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = self.bounds;
    [btn addTarget:self action:@selector(clearBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:btn];
}

- (void)clearBtnMethod:(UIButton *)btn{
    if(self.clickInvocation){
        if(self.identify){
            self.clickInvocation(self.identify);
        }
        else{
            self.clickInvocation(nil);
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

@implementation ProjectClickViewModel



@end
