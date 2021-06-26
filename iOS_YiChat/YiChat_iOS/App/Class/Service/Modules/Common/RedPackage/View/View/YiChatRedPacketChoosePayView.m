//
//  YiChatRedPacketChoosePayView.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/3.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//选择红包支付方式界面

#import "YiChatRedPacketChoosePayView.h"
#import "YiChatPasswordTextView.h"
#import <KLCPopup.h>

#define Spacing 20

@interface YiChatRedPacketChoosePayView (){
    NSArray *payArr;
}
@property (nonatomic,strong) UILabel *moneyLa;
@property (nonatomic,strong) UIImageView *iconImageView;
@property (nonatomic,strong) UILabel *payLa;

@property (nonatomic,strong) YiChatPasswordTextView *passwordTextView;

@property (nonatomic,strong) UIButton *payButton;

@property (nonatomic,assign) BOOL isAliPay;

@property (nonatomic,strong) KLCPopup *popView;
@end

@implementation YiChatRedPacketChoosePayView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        if (@available(iOS 13.0, *)) {
         //   self.overrideUserInterfaceStyle = UIUserInterfaceStyleLight;
        } else {
            // Fallback on earlier versions
        }
        
        if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 1) {
            payArr = @[@"支付宝支付",@"微信支付",@"余额支付",@"取消"];
        }
        
        if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 0) {
            payArr = @[@"支付宝支付",@"余额支付",@"取消"];
        }
        
        if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 1) {
            payArr = @[@"微信支付",@"余额支付",@"取消"];
        }
        
        if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
            payArr = @[@"余额支付",@"取消"];
        }
        
        UIButton *back = [[UIButton alloc]initWithFrame:CGRectZero];
        [back setBackgroundImage:[UIImage imageNamed:@"chose"] forState:UIControlStateNormal];
        [back addTarget:self action:@selector(dissmissView) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:back];
        [back mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(15);
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(30, 30));
        }];
        
        UILabel *title = [self setTitle:@"请支付" font:18];
        title.textAlignment = NSTextAlignmentCenter;
        [self addSubview:title];
        [title mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.centerY.equalTo(back.mas_centerY).offset(0);
            make.size.mas_equalTo(CGSizeMake(200, 20));
        }];
        
        UIView *topLine = [[UIView alloc]initWithFrame:CGRectZero];
        topLine.backgroundColor = [UIColor groupTableViewBackgroundColor];
        [self addSubview:topLine];
        [topLine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(back.mas_bottom).offset(10);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(frame.size.width, 1));
        }];
        
        UILabel *yichat = [self setTitle:@"聊聊红包" font:14];
        yichat.textAlignment = NSTextAlignmentCenter;
        [self addSubview:yichat];
        [yichat mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.top.equalTo(topLine.mas_bottom).offset(10);
            make.size.mas_equalTo(CGSizeMake(200, 20));
        }];
        
        self.moneyLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.moneyLa.font = [UIFont fontWithName:@"Helvetica-Bold" size:40];
        self.moneyLa.text = @"￥0.00";
        self.moneyLa.textAlignment = NSTextAlignmentCenter;
        [self addSubview:self.moneyLa];
        [self.moneyLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.top.equalTo(yichat.mas_bottom).offset(15);
            make.size.mas_equalTo(CGSizeMake(200, 45));
        }];
        
        UIView *centerLine = [[UIView alloc]initWithFrame:CGRectZero];
        centerLine.backgroundColor = [UIColor groupTableViewBackgroundColor];
        [self addSubview:centerLine];
        [centerLine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(yichat.mas_bottom).offset(70);
            make.left.mas_equalTo(Spacing);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - Spacing, 1));
        }];
        
        self.iconImageView = [UIImageView new];
        self.iconImageView.image = [UIImage imageNamed:@"balanceIcon"];
        [self addSubview:self.iconImageView];
        [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(20);
            make.top.equalTo(centerLine.mas_bottom).offset(15);
            make.size.mas_equalTo(CGSizeMake(20, 20));
        }];
        
        self.payLa = [self setTitle:@"余额支付" font:14];
        [self addSubview:self.payLa];
        [self.payLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.iconImageView);
            make.left.equalTo(self.iconImageView.mas_right).offset(10);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - 50, 20));
        }];
        
        UIButton *payBtn = [[UIButton alloc]initWithFrame:CGRectZero];
        [payBtn addTarget:self action:@selector(choosePay) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:payBtn];
        [payBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.payLa);
            make.left.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(frame.size.width, 50));
        }];
        
        UIView *bottomLine = [[UIView alloc]initWithFrame:CGRectZero];
        bottomLine.backgroundColor = [UIColor groupTableViewBackgroundColor];
        [self addSubview:bottomLine];
        [bottomLine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(yichat.mas_bottom).offset(120);
            make.left.mas_equalTo(Spacing);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - Spacing, 1));
        }];
        
        self.passwordTextView = [[YiChatPasswordTextView alloc]initWithFrame:CGRectZero];
        self.passwordTextView.elementCount = 6;
        self.passwordTextView.elementBorderColor = [UIColor colorWithRed:135/255.0 green:206/255.0 blue:250/255.0 alpha:1];
        [self addSubview:self.passwordTextView];
        [self.passwordTextView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.top.equalTo(bottomLine.mas_bottom).offset(30);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - 40, 50));
        }];
        
        __weak typeof(self) weakSelf = self;
        self.passwordTextView.passwordDidChangeBlock = ^(NSString * _Nonnull password) {
            NSString *psw = [password stringByReplacingOccurrencesOfString:@" " withString:@""];
            if (psw.length == 6) {
                weakSelf.payType(RedPacketPayBalance, psw);
            }
        };

        self.payButton = [[UIButton alloc]initWithFrame:CGRectZero];
        self.payButton.layer.masksToBounds = YES;
        self.payButton.layer.cornerRadius = 5;
        self.payButton.hidden = YES;
        [self.payButton setTitle:@"立即支付" forState:UIControlStateNormal];
        [self.payButton addTarget:self action:@selector(alipayAndWwchatpay) forControlEvents:UIControlEventTouchUpInside];
        self.payButton.backgroundColor = [UIColor colorWithRed:247/255.0 green:68/255.0 blue:77/255.0 alpha:1];
        [self addSubview:self.payButton];
        [self.payButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.top.equalTo(bottomLine.mas_bottom).offset(30);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - 40, 40));
        }];
        
    }
    return self;
}

//支付宝支付或者微信支付
-(void)alipayAndWwchatpay{
    if (self.isAliPay) {
        self.payType(RedPacketPayAli, @"");
    }else{
        self.payType(RedPacketPayWeChat, @"");
    }
}

//取消支付
-(void)dissmissView{
    self.dissmiss();
}

//选择支付方式
-(void)choosePay{
    [self endEditing:YES];
    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
        return;
    }
    CGFloat h = 90 + 60 * payArr.count;
    
    UIView *bgView = [[UIView alloc]initWithFrame:CGRectMake(10, 0, PROJECT_SIZE_WIDTH - 20, h)];
    bgView.layer.masksToBounds = YES;
    bgView.layer.cornerRadius = 10;
    bgView.backgroundColor = [UIColor whiteColor];
    
    UILabel *payLa = [[UILabel alloc]initWithFrame:CGRectMake(0, 10, PROJECT_SIZE_WIDTH - 20, 20)];
    payLa.textAlignment = NSTextAlignmentCenter;
    payLa.text = @"请选择支付方式";
    payLa.font = [UIFont systemFontOfSize:13];
    [bgView addSubview:payLa];
    
    
    CGFloat btn_y = 70;
    for (NSInteger i = 0; i < payArr.count; i++) {
        UIButton *btn = [[UIButton alloc]initWithFrame:CGRectMake(0, btn_y, PROJECT_SIZE_WIDTH - 20, 60)];
        [btn setTitle:payArr[i] forState:UIControlStateNormal];
        [btn setTitleColor:PROJECT_COLOR_APPMAINCOLOR forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(choosePayType:) forControlEvents:UIControlEventTouchUpInside];
        [bgView addSubview:btn];
        UIView *line = [[UIView alloc] initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 20, 0.5)];
        line.backgroundColor = [UIColor grayColor];
        [btn addSubview:line];
        btn_y = btn_y + 60 + 5;
    }
    
    self.popView = [KLCPopup popupWithContentView:bgView showType:KLCPopupShowTypeSlideInFromBottom dismissType:KLCPopupDismissTypeSlideOutToBottom maskType:KLCPopupMaskTypeClear dismissOnBackgroundTouch:YES dismissOnContentTouch:NO];
    KLCPopupLayout layout = {KLCPopupHorizontalLayoutCenter,KLCPopupVerticalLayoutBottom};
    [self.popView showWithLayout:layout];
}

-(UILabel *)setTitle:(NSString *)title font:(NSInteger)font{
    UILabel *yichat = [[UILabel alloc]initWithFrame:CGRectZero];
    yichat.text = title;
    yichat.font = [UIFont systemFontOfSize:font];
    return yichat;
}

-(void)setRedMoney:(NSString *)redMoney{
    _redMoney = redMoney;
    self.moneyLa.text = [NSString stringWithFormat:@"￥%@",redMoney];
}

-(void)setBalance:(NSString *)balance{
    _balance = balance;
    self.payLa.text = [NSString stringWithFormat:@"余额(%@元)",balance];
}

-(void)choosePayType:(UIButton *)sender{
    [self.popView dismiss:YES];
    NSString *title = sender.titleLabel.text;
    if ([title isEqualToString:@"支付宝支付"]) {
        self.iconImageView.image = [UIImage imageNamed:@"aliIcon"];
        self.payLa.text = @"支付宝支付";
        self.isAliPay = YES;
        self.payButton.hidden = NO;
        self.passwordTextView.hidden = YES;
    }
    
    if ([title isEqualToString:@"微信支付"]) {
        self.iconImageView.image = [UIImage imageNamed:@"wechatIcon"];
        self.payLa.text = @"微信支付";
        self.isAliPay = NO;
        self.payButton.hidden = NO;
        self.passwordTextView.hidden = YES;
    }
    
    if ([title isEqualToString:@"余额支付"]) {
        self.iconImageView.image = [UIImage imageNamed:@"balanceIcon"];
        self.payLa.text = [NSString stringWithFormat:@"余额(%@元)",self.balance];
        self.isAliPay = NO;
        self.payButton.hidden = YES;
        self.passwordTextView.hidden = NO;
    }
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self endEditing:YES];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
