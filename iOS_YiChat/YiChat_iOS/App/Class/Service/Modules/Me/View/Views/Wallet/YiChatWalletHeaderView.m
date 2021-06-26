//
//  YiChatWalletHeaderView.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/25.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatWalletHeaderView.h"

@interface YiChatWalletHeaderView ()
@property (nonatomic,strong) UILabel *balanceLa;
@end

@implementation YiChatWalletHeaderView


-(instancetype)initWithFrame:(CGRect)frame{
    if (self == [super initWithFrame:frame]) {
        self.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
        UIImageView *imageView = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"pic_charge"]];
        [self addSubview:imageView];
        [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(60);
            make.centerX.mas_equalTo(-32);
            make.size.mas_equalTo(CGSizeMake(20, 20));
        }];
        
        UILabel *la = [[UILabel alloc]init];
        la.textColor = [UIColor whiteColor];
        la.font = [UIFont systemFontOfSize:14];
        la.text = @"账户余额";
        [self addSubview:la];
        [la mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(imageView.mas_right).offset(2);
            make.centerY.equalTo(imageView);
            make.height.mas_equalTo(20);
        }];
        
        
        self.balanceLa = [[UILabel alloc]init];
        self.balanceLa.textColor = [UIColor whiteColor];
        self.balanceLa.font = [UIFont fontWithName:@"Helvetica-Bold" size:18];
        self.balanceLa.text = @"0.00";
        [self addSubview:self.balanceLa];
        [self.balanceLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(imageView.mas_bottom).offset(15);
            make.centerX.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        UIView *bottomView = [[UIView alloc]initWithFrame:CGRectMake(0, 200, PROJECT_SIZE_WIDTH, 50)];
        bottomView.backgroundColor = [UIColor colorWithRed:102/255.0 green:165/255.0 blue:158/255.0 alpha:1];
        [self addSubview:bottomView];
        UIView *line = [[UIView alloc]initWithFrame:CGRectZero];
        line.backgroundColor = [UIColor whiteColor];
        [bottomView addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(0.5, 30));
        }];
        
        CGFloat S = PROJECT_SIZE_WIDTH / 4.0;
        UIImageView *liftImg = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"ic_in"]];
        [bottomView addSubview:liftImg];
        [liftImg mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(-(20 + S));
            make.centerY.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(45, 45));
        }];
        
        UILabel *topUp = [[UILabel alloc]init];
        topUp.textColor = [UIColor whiteColor];
        topUp.font = [UIFont systemFontOfSize:14];
        topUp.text = @"充值";
        [bottomView addSubview:topUp];
        [topUp mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(liftImg.mas_right).offset(10);
            make.centerY.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        
        UIImageView *rightImg = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"ic_out"]];
        [bottomView addSubview:rightImg];
        [rightImg mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(S - 20);
            make.centerY.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(45, 45));
        }];
        
        UILabel *withdrawal = [[UILabel alloc]init];
        withdrawal.textColor = [UIColor whiteColor];
        withdrawal.font = [UIFont systemFontOfSize:14];
        withdrawal.text = @"提现";
        [bottomView addSubview:withdrawal];
        [withdrawal mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(rightImg.mas_right).offset(10);
            make.centerY.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        UIButton *topUpB = [[UIButton alloc]init];
        topUpB.tag = 0;
        [topUpB addTarget:self action:@selector(click:) forControlEvents:UIControlEventTouchUpInside];
        [bottomView addSubview:topUpB];
        [topUpB mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.top.bottom.mas_equalTo(0);
            make.width.mas_equalTo(PROJECT_SIZE_WIDTH / 2);
        }];
        
        UIButton *withdrawalB = [[UIButton alloc]init];
        withdrawalB.tag = 1;
        [withdrawalB addTarget:self action:@selector(click:) forControlEvents:UIControlEventTouchUpInside];
        [bottomView addSubview:withdrawalB];
        [withdrawalB mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.top.bottom.mas_equalTo(0);
            make.width.mas_equalTo(PROJECT_SIZE_WIDTH / 2);
        }];
        
        if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
            liftImg.hidden = YES;
            topUp.hidden = YES;
            topUpB.hidden = YES;
        }
    }
    return self;
}

-(void)setBalance:(NSString *)balance{
    _balance = balance;
    self.balanceLa.text = balance;
}

-(void)click:(UIButton *)sender{
    if (sender.tag == 0) {//充值
        self.walletHeaderBlock(NO);
    }else{
        self.walletHeaderBlock(YES);
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
