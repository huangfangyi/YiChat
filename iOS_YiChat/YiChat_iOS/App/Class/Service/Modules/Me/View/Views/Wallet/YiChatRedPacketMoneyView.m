//
//  YiChatRedPacketMoneyView.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/25.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketMoneyView.h"

@interface YiChatRedPacketMoneyView ()
@property (nonatomic,strong) UILabel *countLa;
@property (nonatomic,strong) UILabel *titleLa;
@end
@implementation YiChatRedPacketMoneyView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self == [super initWithFrame:frame]) {
        self.countLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.countLa.textAlignment = NSTextAlignmentCenter;
        self.countLa.textColor = [UIColor redColor];
        self.countLa.font = [UIFont systemFontOfSize:13];
        [self addSubview:self.countLa];
        [self.countLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.left.right.mas_equalTo(0);
            make.bottom.equalTo(self.mas_centerY).offset(0);;
        }];
        
        self.titleLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.titleLa.textAlignment = NSTextAlignmentCenter;
        self.titleLa.textColor = [UIColor lightGrayColor];
        self.titleLa.font = [UIFont systemFontOfSize:13];
        [self addSubview:self.titleLa];
        [self.titleLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.left.right.mas_equalTo(0);
            make.top.equalTo(self.mas_centerY).offset(0);;
        }];
    }
    return self;
}

-(void)setCount:(NSString *)count{
    _count = count;
    self.countLa.text = count;
}

-(void)setTitle:(NSString *)title{
    _title = title;
    self.titleLa.text = title;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
