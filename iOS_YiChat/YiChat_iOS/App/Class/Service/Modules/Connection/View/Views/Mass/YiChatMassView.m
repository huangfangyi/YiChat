//
//  YiChatMassView.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatMassView.h"

@interface YiChatMassView ()
@property (nonatomic,strong) UITextView *textView;
@end

@implementation YiChatMassView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self == [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor whiteColor];
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 10;
        
        if (@available(iOS 13.0, *)) {
          //  self.overrideUserInterfaceStyle = UIUserInterfaceStyleLight;
        } else {
            // Fallback on earlier versions
        }
        
        UILabel *la = [[UILabel alloc]initWithFrame:CGRectZero];
        la.text = @"群发消息";
        la.textAlignment = NSTextAlignmentCenter;
        [self addSubview:la];
        [la mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.mas_equalTo(0);
            make.top.mas_equalTo(10);
            make.height.mas_equalTo(20);
        }];
        
        self.textView = [[UITextView alloc]initWithFrame:CGRectZero];
//        self.textView
        self.textView.layer.masksToBounds = YES;
        self.textView.layer.borderWidth = 0.5;
        self.textView.layer.borderColor = [UIColor lightGrayColor].CGColor;
        [self addSubview:self.textView];
        [self.textView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.left.mas_equalTo(20);
            make.right.mas_equalTo(-20);
            make.top.mas_equalTo(40);
            make.height.mas_equalTo(120);
        }];
        
        UIButton *cacel = [[UIButton alloc]initWithFrame:CGRectZero];
        [cacel setTitle:@"取消" forState:UIControlStateNormal];
        cacel.backgroundColor = [UIColor redColor];
        cacel.tag = 0;
        cacel.layer.masksToBounds = YES;
        cacel.layer.cornerRadius = 5;
        [cacel addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:cacel];
        [cacel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(20);
            make.bottom.mas_equalTo(-20);
            make.size.mas_equalTo(CGSizeMake(130, 40));
        }];
        
        UIButton *done = [[UIButton alloc]initWithFrame:CGRectZero];
        [done setTitle:@"确定" forState:UIControlStateNormal];
        done.layer.masksToBounds = YES;
        done.layer.cornerRadius = 5;
        done.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
        done.tag = 1;
        [done addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:done];
        [done mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-20);
            make.bottom.mas_equalTo(-20);
            make.size.mas_equalTo(CGSizeMake(130, 40));
        }];
    }
    
    return self;
}

-(void)clickBtn:(UIButton *)sender{
    [self endEditing:YES];
    self.massBlock(self.textView.text, sender.tag == 0? NO : YES);
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
