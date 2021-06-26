//
//  YiChatTransactionRecordsCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatTransactionRecordsCell.h"

@interface YiChatTransactionRecordsCell ()
@property (nonatomic,strong) UILabel *reson;
@property (nonatomic,strong) UILabel *date;
@property (nonatomic,strong) UILabel *money;
@property (nonatomic,strong) UILabel *state;
@end

@implementation YiChatTransactionRecordsCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.reson = [[UILabel alloc]initWithFrame:CGRectZero];
        self.reson.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.reson];
        [self.reson mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.height.mas_equalTo(20);
            make.top.mas_equalTo(15);
        }];
        
        
        self.date = [[UILabel alloc]initWithFrame:CGRectZero];
        self.date.textColor = [UIColor lightGrayColor];
        self.date.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.date];
        [self.date mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.height.mas_equalTo(20);
            make.bottom.mas_equalTo(-10);
        }];
        
        
        self.money = [[UILabel alloc]initWithFrame:CGRectZero];
        self.money.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.money];
        [self.money mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-10);
            make.height.mas_equalTo(20);
            make.centerY.equalTo(self.reson);
        }];
        
        self.state = [[UILabel alloc]initWithFrame:CGRectZero];
        self.state.font = [UIFont systemFontOfSize:14];
        self.state.textColor = [UIColor lightGrayColor];
        [self.contentView addSubview:self.state];
        [self.state mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-10);
            make.height.mas_equalTo(20);
            make.bottom.mas_equalTo(-10);
        }];
    }
    return self;
}

-(void)setModel:(YiChatWalletRecordListModel *)model{
    _model = model;
    self.date.text = model.ctime;
    NSString *m = @"";
    if (![model.moneyDesc containsString:@"-"]) {
        self.money.textColor = [UIColor redColor];
        m = model.moneyDesc;
    }else{
        self.money.textColor = [UIColor greenColor];
        m = model.moneyDesc;
    }
    self.money.text = m;
    self.reson.text = model.memo;
    self.state.text = @"交易成功";
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
