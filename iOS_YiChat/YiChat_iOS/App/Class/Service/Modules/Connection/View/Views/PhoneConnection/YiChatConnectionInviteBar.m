//
//  YiChatConnectionInviteBar.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConnectionInviteBar.h"
#import "ServiceGlobalDef.h"

@interface YiChatConnectionInviteBar ()

@property (nonatomic,strong) NSArray *dataSources;

@property (nonatomic,strong) UILabel *numLab;

@end

@implementation YiChatConnectionInviteBar

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    CGFloat labW = 30.0;
    CGFloat labH = labW;
    
    CGFloat w = 130.0;
    CGFloat h = 30.0;
    
    UILabel *lab =  [[UILabel alloc] initWithFrame:CGRectMake(self.frame.size.width / 2 - (labW + w + 10.0) / 2, self.frame.size.height / 2 - labH / 2, labW, labH)];
    [self addSubview:lab];
    lab.textAlignment = NSTextAlignmentCenter;
    lab.textColor = [UIColor whiteColor];
    lab.font = PROJECT_TEXT_FONT_COMMON(15.0);
    _numLab = lab;
    lab.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    
    UILabel *des = [[UILabel alloc] initWithFrame:CGRectMake(lab.frame.origin.x + lab.frame.size.width + 10.0, lab.frame.origin.y + lab.frame.size.height / 2 - h / 2, w, h)];
    [self addSubview:des];
    des.textAlignment = NSTextAlignmentLeft;
    des.textColor = PROJECT_COLOR_APPMAINCOLOR;
    des.font = PROJECT_TEXT_FONT_COMMON(14.0);
    des.text = [NSString stringWithFormat:@"%@%@",@"邀请使用",PROJECT_TEXT_APPNAME];
    
    UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:self.bounds target:self method:@selector(clearBtnMethod:)];
    [self addSubview:btn];
}

- (void)clearBtnMethod:(UIButton *)btn{
    if(self.yiChatInvitebarClick){
        self.yiChatInvitebarClick(_dataSources);
    }
}

- (void)updateUIWithDataSource:(NSArray *)datasource{
    if([datasource isKindOfClass:[NSArray class]]){
        _dataSources = datasource;
        _numLab.text = [NSString stringWithFormat:@"%ld",_dataSources.count];
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
