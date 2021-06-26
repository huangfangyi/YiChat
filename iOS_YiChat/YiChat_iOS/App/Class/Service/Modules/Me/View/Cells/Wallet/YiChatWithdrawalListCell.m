//
//  YiChatWithdrawalListCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/26.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatWithdrawalListCell.h"

@interface YiChatWithdrawalListCell ()
@property (nonatomic,strong) UILabel *withdrawalMoneyLa;//status
@property (nonatomic,strong) UILabel *statusLa;
@property (nonatomic,strong) UILabel *timeLa;
@property (nonatomic,strong) UILabel *reasonLa;
@end

@implementation YiChatWithdrawalListCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        UIImageView *im = [[UIImageView alloc]initWithFrame:CGRectZero];
        im.image = [UIImage imageNamed:@"withdraw_log_list_icon"];
        im.layer.masksToBounds = YES;
        im.layer.cornerRadius = 25.0f;
        [self.contentView addSubview:im];
        [im mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
        
        self.withdrawalMoneyLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.withdrawalMoneyLa.font = [UIFont systemFontOfSize:16];
//        self.withdrawalMoneyLa.text = @"到账金额：22元";
        [self.contentView addSubview:self.withdrawalMoneyLa];
        [self.withdrawalMoneyLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(im.mas_right).offset(10);
            make.top.mas_equalTo(5);
            make.height.mas_equalTo(20);
        }];
        
        self.statusLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.statusLa.font = [UIFont systemFontOfSize:14];
//        self.statusLa.text = @"拒绝";
        self.statusLa.textAlignment = NSTextAlignmentRight;
        [self.contentView addSubview:self.statusLa];
        [self.statusLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(im.mas_centerY).offset(0);
            make.right.mas_equalTo(-10);
            make.height.mas_equalTo(20);
        }];
        
        self.reasonLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.reasonLa.font = [UIFont systemFontOfSize:14];
//        self.reasonLa.text = @"拒绝原因：";
        self.reasonLa.textAlignment = NSTextAlignmentRight;
        [self.contentView addSubview:self.reasonLa];
        [self.reasonLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(im.mas_centerY).offset(0);
            make.left.equalTo(im.mas_right).offset(10);
            make.height.mas_equalTo(20);
        }];
        
        self.timeLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.timeLa.font = [UIFont systemFontOfSize:13];
        self.timeLa.textColor = [UIColor lightGrayColor];
//        self.timeLa.text = @"操作时间:2019-07-26 16:08:31";
        self.timeLa.textAlignment = NSTextAlignmentRight;
        [self.contentView addSubview:self.timeLa];
        [self.timeLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(im.mas_right).offset(10);
            make.bottom.mas_equalTo(-5);
            make.height.mas_equalTo(20);
        }];
    }
    return self;
}

-(void)setModel:(YiChatWalletRecordListModel *)model{
    
    
    _model = model;
//    0处理中 1审核通过 2拒绝
    if (model.status == 0) {
        self.withdrawalMoneyLa.text = [NSString stringWithFormat:@"申请提现金额：%@元",model.money];
        self.statusLa.text = @"处理中";
        self.statusLa.textColor = [UIColor blackColor];
        self.reasonLa.text = @"";
    }else if (model.status == 1){
        self.withdrawalMoneyLa.text = [NSString stringWithFormat:@"实际到账金额：%@元",model.money];
        self.statusLa.text = @"审核通过";
        self.statusLa.textColor = PROJECT_COLOR_APPMAINCOLOR;
        self.reasonLa.text = @"";
    }else{
        self.withdrawalMoneyLa.text = [NSString stringWithFormat:@"申请提现金额：%@元",model.money];
        self.statusLa.text = @"拒绝";
        self.statusLa.textColor = [UIColor redColor];
        self.reasonLa.text = [NSString stringWithFormat:@"拒绝原因：%@",model.refuseReason];
    }
    self.timeLa.text = [NSString stringWithFormat:@"操作时间：%@",model.time];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
