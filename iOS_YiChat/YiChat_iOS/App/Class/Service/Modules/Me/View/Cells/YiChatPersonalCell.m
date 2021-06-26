//
//  YiChatPersonalCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPersonalCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "UIImageView+LoadNetIcon.h"
#import "ProjectTableCell+ServiceExtension.h"
@interface YiChatPersonalCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;
@property (nonatomic,strong) UILabel *userName;
@property (nonatomic,strong) UILabel *userId;

@end

@implementation YiChatPersonalCell

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine type:type];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
    if(self){
        _type = type;
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)updateType:(NSInteger)type{
    _type = type;
}

- (void)makeUI{
    if(_type == 0){
        [self makeUIWithType_0];
    }
    else if(_type == 1){
        [self makeUIWithType_1];
    }
    
}

- (void)makeUIWithType_0{
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = PROJECT_SIZE_NAV_BLANK;
    CGFloat w = self.sCellHeight - PROJECT_SIZE_NAV_BLANK * 2;
    CGFloat h = w;
    
    CGFloat qrcodeW = 25.0;
    CGSize rightArrow = CGSizeMake([self getRightArrowSize].size.width, [self getRightArrowSize].size.height);
    
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(x, y, w, h) andImg:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    [self.contentView addSubview:icon];
    _icon = icon;
    
    x = icon.frame.origin.x + icon.frame.size.width + PROJECT_SIZE_NAV_BLANK;
    w = self.sCellWidth - x - PROJECT_SIZE_NAV_BLANK - 10.0 - qrcodeW - rightArrow.width;
    
    UILabel *name = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x , icon.frame.origin.y, w, icon.frame.size.height / 2) andfont:PROJECT_TEXT_FONT_COMMON(15) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:name];
    _userName = name;
    
    UILabel *userid = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(name.frame.origin.x, name.frame.origin.y + name.frame.size.height, name.frame.size.width, name.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(13) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:userid];
    _userId = userid;
    
    UIImageView *qrcode = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectZero andImg:[UIImage imageNamed:@"me_qr@3x.png"]];
    [self.contentView addSubview:qrcode];
    qrcode.userInteractionEnabled = NO;
    [qrcode mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(qrcodeW, qrcodeW));
        make.right.mas_equalTo(-35);
    }];
    
    UIButton *qrcodeBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectZero target:self method:@selector(qrcodeBtnMethod:)];
//    [qrcodeBtn setImage:[UIImage imageNamed:@"me_qr@3x.png"] forState:UIControlStateNormal];
    [self.contentView addSubview:qrcodeBtn];
    [qrcodeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(qrcodeW, qrcodeW));
        make.right.mas_equalTo(-35);
    }];
}

- (void)qrcodeBtnMethod:(UIButton *)btn{
    if(self.YiChatPersonalCellClickQrcode){
        self.YiChatPersonalCellClickQrcode();
    }
}

- (void)makeUIWithType_1{
    CGSize rightArrow = CGSizeMake([self getRightArrowSize].size.width, [self getRightArrowSize].size.height);
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, PROJECT_SIZE_NAV_BLANK, self.sCellHeight - PROJECT_SIZE_NAV_BLANK * 2, self.sCellHeight - PROJECT_SIZE_NAV_BLANK * 2)];
    [self.contentView addSubview:_icon];
    
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + PROJECT_SIZE_NAV_BLANK;
    CGFloat w = self.sCellWidth - x - PROJECT_SIZE_NAV_BLANK - rightArrow.width - 10.0;
    
    CGFloat itemH = (self.sCellHeight - 10.0);
    
    _userName = [[UILabel alloc] initWithFrame:CGRectMake(x, 5.0, w, itemH)];
    [self.contentView addSubview:_userName];
    _userName.textAlignment = NSTextAlignmentLeft;
    _userName.textColor = PROJECT_COLOR_APPTEXT_MAINCOLOR;
    _userName.font = PROJECT_TEXT_FONT_COMMON(15.0);

}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    if([cellModel isKindOfClass:[ProjectCommonCellModel class]]){
          _cellModel = cellModel;
        
        NSString *defaultUrl = nil;
        if(_type == 0){
            defaultUrl = PROJECT_ICON_USERDEFAULT;
            if([cellModel.contentStr isKindOfClass:[NSString class]]){
                _userId.text = cellModel.contentStr;
            }
            
        }
        else if(_type == 1){
            defaultUrl = PROJECT_ICON_USERDEFAULT;
        }
        
        if([cellModel.titleStr isKindOfClass:[NSString class]]){
            _userName.text = cellModel.titleStr;
        }
        
        [self imageLoadIconWithUrl:cellModel.iconUrl placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] imageControl:_icon];

    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
