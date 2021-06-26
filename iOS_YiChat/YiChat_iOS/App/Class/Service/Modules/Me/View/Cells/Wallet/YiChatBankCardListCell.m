//
//  YiChatBankCardListCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/13.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBankCardListCell.h"

@interface YiChatBankCardListCell ()
@property (nonatomic,strong) UILabel *bankName;
@property (nonatomic,strong) UILabel *bankCardNum;
@end

@implementation YiChatBankCardListCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (UIColor *) colorWithHexString: (NSString *)color
{
    NSString *cString = [[color stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    // String should be 6 or 8 characters
    if ([cString length] < 6) {
        return [UIColor clearColor];
    }
    // 判断前缀
    if ([cString hasPrefix:@"0X"])
        cString = [cString substringFromIndex:2];
    if ([cString hasPrefix:@"#"])
        cString = [cString substringFromIndex:1];
    if ([cString length] != 6)
        return [UIColor clearColor];
    // 从六位数值中找到RGB对应的位数并转换
    NSRange range;
    range.location = 0;
    range.length = 2;
    //R、G、B
    NSString *rString = [cString substringWithRange:range];
    range.location = 2;
    NSString *gString = [cString substringWithRange:range];
    range.location = 4;
    NSString *bString = [cString substringWithRange:range];
    // Scan values
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r / 255.0f) green:((float) g / 255.0f) blue:((float) b / 255.0f) alpha:1.0f];
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self == [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        UIView *bgV = [[UIView alloc]initWithFrame:CGRectZero];
        bgV.backgroundColor = [self colorWithHexString:@"2E65A5"];
        [self.contentView addSubview:bgV];
        [bgV mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.mas_equalTo(0);
            make.top.mas_equalTo(2);
            make.bottom.mas_equalTo(-2);
        }];
        
        self.bankName = [[UILabel alloc]initWithFrame:CGRectZero];
//        self.bankName.text = @"中国工商银行";
        self.bankName.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.bankName];
        [self.bankName mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(20);
            make.top.mas_equalTo(12);
            make.right.mas_equalTo(0);
            make.height.mas_equalTo(30);
        }];
        
        self.bankCardNum = [[UILabel alloc]initWithFrame:CGRectZero];
        self.bankCardNum.textColor = [UIColor whiteColor];
        self.bankCardNum.textAlignment = NSTextAlignmentCenter;
//        self.bankCardNum.text = [self getNewStarBankNumWitOldNum:@"563547364738483778334"];
        [self.contentView addSubview:self.bankCardNum];
        [self.bankCardNum mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(20);
            make.bottom.mas_equalTo(-12);
            make.right.mas_equalTo(0);
            make.height.mas_equalTo(30);
        }];
    }
    
    return self;
}

-(void)setModel:(YiChatBankCardInfoModel *)model{
    _model = model;
    self.bankName.text = model.bankName;
    self.bankCardNum.text = [self getNewStarBankNumWitOldNum:model.bankNumber];
}

-(NSString *)getNewStarBankNumWitOldNum:(NSString *)bankCardNum{
    NSString *bankNum = bankCardNum;
    NSMutableString *mutableStr;
    if (bankNum.length) {
        mutableStr = [NSMutableString stringWithString:bankNum];
        for (int i = 0 ; i < mutableStr.length; i ++) {
            if (i < mutableStr.length - 4) {
                [mutableStr replaceCharactersInRange:NSMakeRange(i, 1) withString:@"*"];
            }
        }
        NSString *text = mutableStr;
        text = [text stringByReplacingOccurrencesOfString:@" " withString:@""];
        NSString *newString = @"";
        while (text.length > 0) {
            NSString *subString = [text substringToIndex:MIN(text.length, 4)];
            newString = [newString stringByAppendingString:subString];
            if (subString.length == 4) {
                newString = [newString stringByAppendingString:@" "];
            }
            text = [text substringFromIndex:MIN(text.length, 4)];
        }
        return newString;
    }
    return bankNum;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
