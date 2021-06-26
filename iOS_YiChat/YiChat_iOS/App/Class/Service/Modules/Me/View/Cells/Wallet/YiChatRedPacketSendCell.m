//
//  YiChatRedPacketSendCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/28.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketSendCell.h"

@interface YiChatRedPacketSendCell ()
@property (nonatomic,strong) UIImageView *avatarImg;
@property (nonatomic,strong) UILabel *date;
@property (nonatomic,strong) UILabel *money;
@property (nonatomic,strong) UILabel *nickName;
@property (nonatomic,strong) UILabel *status;

@property (nonatomic,strong) UILabel *iconView;
@end


@implementation YiChatRedPacketSendCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.avatarImg = [[UIImageView alloc]initWithFrame:CGRectZero];
        self.avatarImg.layer.masksToBounds = YES;
        self.avatarImg.layer.cornerRadius = 20;
        [self.contentView addSubview:self.avatarImg];
        [self.avatarImg mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(40, 40));
            make.centerY.mas_equalTo(0);
        }];
        
        
        self.date = [[UILabel alloc]initWithFrame:CGRectZero];
        self.date.textColor = [UIColor lightGrayColor];
        self.date.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.date];
        [self.date mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.avatarImg.mas_right).offset(5);
            make.height.mas_equalTo(20);
            make.bottom.mas_equalTo(-10);
        }];
        
        self.nickName = [[UILabel alloc]initWithFrame:CGRectZero];
        self.nickName.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.nickName];
        [self.nickName mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.avatarImg.mas_right).offset(5);
            make.height.mas_equalTo(20);
            make.top.mas_equalTo(10);
        }];
        
        self.iconView = [[UILabel alloc]initWithFrame:CGRectZero];
        self.iconView.font = [UIFont systemFontOfSize:12];
        self.iconView.backgroundColor = [UIColor orangeColor];
        self.iconView.text = @"拼";
        self.iconView.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.iconView];
        [self.iconView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.nickName.mas_right).offset(2);
            make.size.mas_equalTo(CGSizeMake(15, 15));
            make.centerY.equalTo(self.nickName.mas_centerY).offset(0);
        }];
        
        self.money = [[UILabel alloc]initWithFrame:CGRectZero];
        self.money.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.money];
        [self.money mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-10);
            make.height.mas_equalTo(20);
            make.centerY.equalTo(self.nickName.mas_centerY).offset(0);
        }];
        
        self.status = [[UILabel alloc]initWithFrame:CGRectZero];
        self.status.font = [UIFont systemFontOfSize:12];
        self.status.textColor = [UIColor lightGrayColor];
        [self.contentView addSubview:self.status];
        [self.status mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-10);
            make.height.mas_equalTo(20);
            make.centerY.equalTo(self.date.mas_centerY).offset(0);
        }];
    }
    return self;
}

-(void)setModel:(YiChatWalletRecordListModel *)model{
    _model = model;
    self.date.text = model.receiveTime;
    self.money.text = [NSString stringWithFormat:@"%.2f元",model.money.floatValue];
    self.nickName.text = model.nick;
    [self.avatarImg sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
//    -红包状态 0已创建 1未抢完 2已抢完 3已超时
    if (model.status == 3) {
        self.status.text = [NSString stringWithFormat:@"已过期，%ld/%ld",model.receiveCount,model.totalCount];
    }else if (model.status == 2){
        self.status.text = [NSString stringWithFormat:@"已领完，%ld/%ld",model.receiveCount,model.totalCount];
    }else{
        self.status.text = @"";
    }
    
    if ([model.type isEqualToString:@"1"]) {
        self.iconView.hidden = NO;
    }else{
        self.iconView.hidden = YES;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}

@end
