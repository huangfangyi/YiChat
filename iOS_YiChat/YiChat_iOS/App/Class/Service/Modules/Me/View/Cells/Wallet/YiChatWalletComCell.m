//
//  YiChatWalletComCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatWalletComCell.h"
#import <Masonry/Masonry.h>
@implementation YiChatWalletComCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.titleLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.titleLa.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.titleLa];
        [self.titleLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.mas_equalTo(15);
            make.height.mas_equalTo(20);
            make.width.mas_equalTo(100);
        }];
        
        self.textField = [[UITextField alloc]initWithFrame:CGRectZero];
        self.textField.font = [UIFont systemFontOfSize:14];
        self.textField.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.textField];
        [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.equalTo(self.titleLa.mas_right).offset(10);
            make.height.mas_equalTo(30);
            make.right.mas_equalTo(-15);
        }];
        
        self.codeBtn = [[UIButton alloc]initWithFrame:CGRectZero];
        self.codeBtn.layer.masksToBounds = YES;
        self.codeBtn.layer.cornerRadius = 3;
//        self.codeBtn.layer.borderWidth = 1.0;
//        self.codeBtn.layer.borderColor = [[UIColor blueColor] CGColor];
        [self.codeBtn setBackgroundImage:[self imageWithColor:PROJECT_COLOR_APPMAINCOLOR size:CGSizeMake(90, 30)] forState:UIControlStateNormal];
        [self.codeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [self.codeBtn setTitle:@"发送验证码" forState:UIControlStateNormal];
        self.codeBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:self.codeBtn];
        [self.codeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.width.mas_equalTo(90);
            make.height.mas_equalTo(30);
            make.right.mas_equalTo(-10);
        }];
    }
    return self;
}

- (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size {
    if (!color || size.width <= 0 || size.height <= 0)
        return nil;
    CGRect rect = CGRectMake(0.0f, 0.0f, size.width, size.height);
    UIGraphicsBeginImageContextWithOptions(rect.size, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
