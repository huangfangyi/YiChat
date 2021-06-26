//
//  YiChatGrabRedPacketView.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/10.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  抢红包view

#import "YiChatGrabRedPacketView.h"

#define Spacing 20  
#define AvtarRadius 30
@interface YiChatGrabRedPacketView ()
@property (nonatomic,strong) UIImageView *avtarImage;
@property (nonatomic,strong) UILabel *nameLabel;
@property (nonatomic,strong) UILabel *contentLabel;

@property (nonatomic,strong) UIButton *openRedButton;
@end


@implementation YiChatGrabRedPacketView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        UIImageView *bgImage = [[UIImageView alloc]initWithFrame:frame];
        bgImage.image = [UIImage imageNamed:@"RedPacketBG"];
        [self addSubview:bgImage];

        UIButton *back = [[UIButton alloc]initWithFrame:CGRectZero];
        [back setBackgroundImage:[UIImage imageNamed:@"chose"] forState:UIControlStateNormal];
        [back addTarget:self action:@selector(dissmissView) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:back];
        [back mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(10);
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(30, 30));
        }];
        
        self.avtarImage = [[UIImageView alloc]initWithFrame:CGRectZero];
        [self.avtarImage sd_setImageWithURL:[NSURL URLWithString:self.model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
        self.avtarImage.layer.masksToBounds = YES;
        self.avtarImage.layer.cornerRadius = AvtarRadius;
        self.avtarImage.backgroundColor = [UIColor blueColor];
        [self addSubview:self.avtarImage];
        [self.avtarImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(50);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(AvtarRadius * 2, AvtarRadius * 2));
        }];
        
        self.nameLabel = [self setLabelFont:15 color:[UIColor whiteColor] alignment:NSTextAlignmentCenter];
        self.nameLabel.text = self.model.nick;
        [self addSubview:self.nameLabel];
        [self.nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.avtarImage.mas_bottom).offset(10);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(frame.size.width, 20));
        }];
        
        UILabel *la = [self setLabelFont:14 color:[UIColor groupTableViewBackgroundColor] alignment:NSTextAlignmentCenter];
        la.text = @"发了一个红包";
        la.alpha = 0.6;
        [self addSubview:la];
        [la mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.nameLabel.mas_bottom).offset(10);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(frame.size.width, 20));
        }];
        
        self.contentLabel = [self setLabelFont:23 color:[UIColor whiteColor] alignment:NSTextAlignmentCenter];
        self.contentLabel.numberOfLines = 2;
        self.contentLabel.text = @"恭喜发财，大吉大利";
        [self addSubview:self.contentLabel];
        [self.contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(la.mas_bottom).offset(20);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(frame.size.width - 20, 45));
        }];
        
        self.openRedButton = [[UIButton alloc]initWithFrame:CGRectZero];
        [self.openRedButton setBackgroundImage:[UIImage imageNamed:@"openRedPacket"] forState:UIControlStateNormal];
        [self.openRedButton addTarget:self action:@selector(openRedPacket) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:self.openRedButton];
        [self.openRedButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.contentLabel.mas_bottom).offset(20);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(80, 80));
        }];
        
//        self.luckButton = [[UIButton alloc]initWithFrame:CGRectZero];
//        [self.luckButton addTarget:self action:@selector(toViewLuck) forControlEvents:UIControlEventTouchUpInside];
//        [self addSubview:self.luckButton];
//        [self.luckButton mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.centerX.mas_equalTo(0);
//            make.bottom.mas_equalTo(-10);
//            make.size.mas_equalTo(CGSizeMake(130, 30));
//        }];
//
//        UILabel *la1 = [[UILabel alloc]initWithFrame:CGRectZero];
//        la1.text = @"查看大家手气";
//        la1.textColor = [UIColor whiteColor];
//        [self.luckButton addSubview:la1];
//        [la1 mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.centerY.mas_equalTo(0);
//            make.left.mas_equalTo(5);
//            make.size.mas_equalTo(CGSizeMake(150, 20));
//        }];
//
//        UIImageView *rightArrow = [[UIImageView alloc]initWithFrame:CGRectZero];
//        rightArrow.image = [UIImage imageNamed:@"right_arrow"];
//        [self.luckButton addSubview:rightArrow];
//        [rightArrow mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.centerY.mas_equalTo(0);
//            make.right.mas_equalTo(-5);
//            make.size.mas_equalTo(CGSizeMake(20, 20));
//        }];
    }
    return self;
}

-(void)dissmissView{
    self.redPacketBlock([YiChatRedPacketDetailModel new],NO);
}

-(void)openRedPacket{
    self.openRedButton.userInteractionEnabled = NO;
    CABasicAnimation *transformAnima = [CABasicAnimation animationWithKeyPath:@"transform.rotation.y"];
    transformAnima.toValue = [NSNumber numberWithFloat: M_PI];
    transformAnima.duration = 0.5;
    transformAnima.cumulative = YES;
    transformAnima.autoreverses = NO;
    transformAnima.repeatCount = HUGE_VALF;
    transformAnima.fillMode = kCAFillModeForwards;
    transformAnima.removedOnCompletion = NO;
    transformAnima.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear];
    self.openRedButton.layer.zPosition = 5;
    self.openRedButton.layer.zPosition = self.openRedButton.layer.frame.size.width/2.f;
    [self.openRedButton.layer addAnimation:transformAnima forKey:@"rotationAnimationY"];
    __weak typeof(self) weakSelf = self;
    [YiChatRedPacketHelper receiveRedPacketID:self.model.packetId redBlock:^(YiChatRedPacketDetailModel * _Nonnull redPacketModel, NSDictionary * _Nonnull redDic) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf.openRedButton.layer removeAnimationForKey:@"rotationAnimationY"];
            if (redPacketModel.code == 0) {
                weakSelf.redPacketBlock(redPacketModel, YES);
            }else{
                weakSelf.redPacketBlock(redPacketModel, NO);
            }
        });
    }];
}

-(UILabel *)setLabelFont:(CGFloat)font color:(UIColor *)color alignment:(NSTextAlignment)alignment{
    UILabel *la = [[UILabel alloc]init];
    la.textAlignment = alignment;
    la.textColor = color;
    return la;
}

-(void)toViewLuck{
    self.redPacketBlock([YiChatRedPacketDetailModel new],YES);
}

-(void)setModel:(YiChatRedPacketListModel *)model{
    _model = model;
    self.contentLabel.text = model.content;
    [self.avtarImage sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    self.nameLabel.text = model.nick;
}

-(void)setIsNo:(BOOL)isNo{
    if (isNo) {
        self.openRedButton.hidden = YES;
        self.contentLabel.text = @"等待对方领取";
        self.luckButton.hidden = YES;
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
