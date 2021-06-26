//
//  YiChatShowZhenView.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/16.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatShowZhenView.h"

static CGFloat avatar_w = 100.f;

@interface YiChatShowZhenView ()
@property (nonatomic,strong) UIImageView *avatarImageView;
@property (nonatomic,strong) UILabel *nickLabel;
@property (nonatomic,strong) UILabel *groupNameLabel;
@property (nonatomic,strong) UILabel *contentLabel;
//@property (nonatomic,strong) UILabel *nickLabel;
@end


@implementation YiChatShowZhenView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        UIImageView *im = [[UIImageView alloc]initWithFrame:frame];
        im.image = [UIImage imageNamed:@"WechatIMG690"];
//        im.userInteractionEnabled = NO;
        [self addSubview:im];
        
        self.avatarImageView = [[UIImageView alloc]initWithFrame:CGRectZero];
        self.avatarImageView.layer.masksToBounds = YES;
        self.avatarImageView.layer.cornerRadius = avatar_w / 2;
        self.avatarImageView.backgroundColor = [UIColor whiteColor];
        [self addSubview:self.avatarImageView];
        [self.avatarImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(0);
            make.top.mas_equalTo(avatar_w);
            make.size.mas_equalTo(CGSizeMake(avatar_w, avatar_w));
        }];
        
        self.nickLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.nickLabel.textAlignment = NSTextAlignmentCenter;
        self.nickLabel.textColor = [UIColor whiteColor];
        [self addSubview:self.nickLabel];
        [self.nickLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.avatarImageView.mas_bottom).offset(50);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        self.groupNameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.groupNameLabel.textAlignment = NSTextAlignmentCenter;
        self.groupNameLabel.textColor = [UIColor groupTableViewBackgroundColor];
        [self addSubview:self.groupNameLabel];
        [self.groupNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.nickLabel.mas_bottom).offset(20);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        self.contentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.contentLabel.textAlignment = NSTextAlignmentCenter;
        self.contentLabel.textColor = [UIColor groupTableViewBackgroundColor];
        [self addSubview:self.contentLabel];
        [self.contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.groupNameLabel.mas_bottom).offset(20);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        UIButton *cancel = [[UIButton alloc] initWithFrame:CGRectZero];
        [cancel setImage:[UIImage imageNamed:@"zhen_hulv"] forState:UIControlStateNormal];
//        cancel.backgroundColor = [UIColor whiteColor];
        cancel.layer.masksToBounds = YES;
        cancel.layer.cornerRadius = 35;
        [cancel addTarget:self action:@selector(dimiss) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:cancel];
        [cancel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(50);
            make.bottom.mas_equalTo(-70);
            make.size.mas_equalTo(CGSizeMake(70, 70));
        }];
        
        UILabel *la1 = [[UILabel alloc] initWithFrame:CGRectZero];
        la1.textAlignment = NSTextAlignmentCenter;
        la1.text = @"忽略";
        [self addSubview:la1];
        [la1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(cancel.mas_bottom).offset(10);
            make.centerX.equalTo(cancel.mas_centerX).offset(0);
            make.size.mas_equalTo(CGSizeMake(100, 20));
        }];
        
        UIButton *jump = [[UIButton alloc] initWithFrame:CGRectZero];
        [jump setImage:[UIImage imageNamed:@"tabbar_contacts"] forState:UIControlStateNormal];
        jump.backgroundColor = [UIColor whiteColor];
        [jump addTarget:self action:@selector(jumpGroup) forControlEvents:UIControlEventTouchUpInside];
        jump.layer.masksToBounds = YES;
        jump.layer.cornerRadius = 35;
        [self addSubview:jump];
        [jump mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-50);
            make.bottom.mas_equalTo(-70);
            make.size.mas_equalTo(CGSizeMake(70, 70));
        }];
        
        UILabel *la2 = [[UILabel alloc] initWithFrame:CGRectZero];
        la2.textAlignment = NSTextAlignmentCenter;
        la2.text = @"进群";
        [self addSubview:la2];
        [la2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(jump.mas_bottom).offset(10);
            make.centerX.equalTo(jump.mas_centerX).offset(0);
            make.size.mas_equalTo(CGSizeMake(100, 20));
        }];
    }
    return self;
}

-(void)setDic:(NSDictionary *)dic{
    _dic = dic;
    NSString *avatar = [NSString stringWithFormat:@"%@",dic[@"avatar"]];
    [self.avatarImageView sd_setImageWithURL:[NSURL URLWithString:avatar] placeholderImage:[UIImage imageNamed:@"complaint_icon"]];
    self.nickLabel.text = [NSString stringWithFormat:@"%@",dic[@"nick"]];
    self.groupNameLabel.text = [NSString stringWithFormat:@"%@  的群管理",dic[@"groupName"]];
    self.contentLabel.text = [NSString stringWithFormat:@"%@",dic[@"content"]];
}

-(void)dimiss{
    self.dimissBlock();
}

-(void)jumpGroup{
    self.promptBlock(self.dic);
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
@end
