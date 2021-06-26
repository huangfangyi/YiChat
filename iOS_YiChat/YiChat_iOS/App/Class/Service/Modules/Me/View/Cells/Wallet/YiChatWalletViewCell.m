//
//  YiChatWalletViewCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatWalletViewCell.h"
#import <Masonry.h>
@interface YiChatWalletViewCell ()
@property (nonatomic,strong) UIImageView *image;
@property (nonatomic,strong) UILabel *titleLa;
@end

@implementation YiChatWalletViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.image = [[UIImageView alloc]init];
        [self.contentView addSubview:self.image];
        [self.image mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.mas_equalTo(15);
            make.size.mas_equalTo(CGSizeMake(20, 20));
        }];
        
        self.titleLa = [[UILabel alloc]init];
        self.titleLa.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.titleLa];
        [self.titleLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.equalTo(self.image.mas_right).offset(10);
            make.height.mas_equalTo(20);
        }];
    }
    return self;
}

-(void)setImgString:(NSString *)imgString{
    _imgString = imgString;
    self.image.image = [UIImage imageNamed:imgString];
}

-(void)setTitleString:(NSString *)titleString{
    _titleString = titleString;
    self.titleLa.text = titleString;
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
