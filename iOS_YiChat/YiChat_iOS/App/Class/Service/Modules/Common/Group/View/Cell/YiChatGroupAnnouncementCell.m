//
//  YiChatGroupAnnouncementCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupAnnouncementCell.h"

@interface YiChatGroupAnnouncementCell ()

@property (nonatomic,strong) UILabel *titleLa;
@property (nonatomic,strong) UILabel *infoLa;
@property (nonatomic,strong) UILabel *dateLa;
@property (nonatomic,strong) UILabel *sendPson;
@end

@implementation YiChatGroupAnnouncementCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self == [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.titleLa = [[UILabel alloc]initWithFrame:CGRectMake(15, 10, 300, 20)];
        self.titleLa.font = [UIFont systemFontOfSize:15];
        [self.contentView addSubview:self.titleLa];
        
        self.sendPson = [[UILabel alloc] initWithFrame:CGRectZero];
        self.sendPson.textAlignment = NSTextAlignmentRight;
        self.sendPson.font = [UIFont systemFontOfSize:13];
        [self.contentView addSubview:self.sendPson];
        [self.sendPson mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.titleLa.mas_centerY).offset(0);
            make.right.right.mas_equalTo(-10);
        }];
        
        self.infoLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.infoLa.textColor = [UIColor grayColor];
        self.infoLa.alpha = 0.6;
        self.infoLa.numberOfLines = 0;
        self.infoLa.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.infoLa];

        self.dateLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.dateLa.textColor = [UIColor grayColor];
        self.dateLa.alpha = 0.6;
        self.dateLa.textAlignment = NSTextAlignmentLeft;
        self.dateLa.font = [UIFont systemFontOfSize:13];
        [self.contentView addSubview:self.dateLa];
        [self.dateLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.bottom.mas_equalTo(-10);
            make.size.mas_equalTo(CGSizeMake(150, 20));
        }];
    }
    return self;
}

-(void)setModel:(YiChatGroupNoticeInfoModel *)model{
    _model = model;
    self.titleLa.text = model.title;
    self.dateLa.text = model.timeDesc;
    self.infoLa.text = model.content;
    self.sendPson.text = model.nick;

    if (model.content.length == 0 || model.content == nil || [model.content isEqualToString:@""]) {
        [self.infoLa mas_updateConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.right.mas_equalTo(-15);
            make.height.mas_equalTo(20);
            make.bottom.mas_equalTo(-40);
        }];
    }else{
        NSMutableParagraphStyle *paraStyle = [[NSMutableParagraphStyle alloc] init];
        paraStyle.lineSpacing = 3;
        NSDictionary *dic = @{ NSFontAttributeName:[UIFont systemFontOfSize:14], NSParagraphStyleAttributeName:paraStyle };
        CGSize size = [model.content boundingRectWithSize:CGSizeMake(PROJECT_SIZE_WIDTH - 30, MAXFLOAT) options: NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:dic context:nil].size;
        [self.infoLa mas_updateConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.right.mas_equalTo(-15);
            make.height.mas_equalTo(size.height);
            make.bottom.mas_equalTo(-40);
        }];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
