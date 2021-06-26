//
//  YiChatAddFriendsMainCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatAddFriendsMainCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import <SDWebImage/UIImageView+WebCache.h>

@interface YiChatAddFriendsMainCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,strong) UILabel *content;

@property (nonatomic,strong) UIButton *clearBtn;


@end

@implementation YiChatAddFriendsMainCell

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

- (void)makeUI{
    
    if(_type == 0){
        self.contentView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        [self makeUIForType0];
    }
    else if(_type == 1){
        self.contentView.backgroundColor = [UIColor whiteColor];
        [self makeUIForType1];
    }
    else if(_type == 2){
        self.contentView.backgroundColor = [UIColor whiteColor];
        [self makeUIForType2];
    }
}

- (void)makeUIForType0{
    _title = [[UILabel alloc] init];
    [self.contentView addSubview:_title];
    _title.textAlignment = NSTextAlignmentLeft;
    _title.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _title.font = PROJECT_TEXT_FONT_COMMON(15.0);
    
    _icon = [[UIImageView alloc] init];
    [self.contentView addSubview:_icon];
    
    UIButton *clearBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectMake(0, 0, self.sCellWidth, self.sCellHeight) target:self method:@selector(myQRCodeMethod:)];
    [self.contentView addSubview:clearBtn];
    _clearBtn = clearBtn;
}

- (void)myQRCodeMethod:(UIButton *)btn{
    if(self.cellMyQrCodeClick){
        self.cellMyQrCodeClick();
    }
}

- (void)makeUIForType1{
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(10.0, 10.0, self.sCellHeight - 20.0, self.sCellHeight - 20.0)];
    [self.contentView addSubview:_icon];
    _icon.layer.cornerRadius = 10.0;
    _icon.clipsToBounds = YES;
    
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + 10.0;
    CGFloat w = self.sCellWidth - x - 10.0;
    
    CGFloat itemH = (self.sCellHeight - 10.0) / 2;
    
    _title = [[UILabel alloc] initWithFrame:CGRectMake(x, 5.0, w, itemH)];
    [self.contentView addSubview:_title];
    _title.textAlignment = NSTextAlignmentLeft;
    _title.textColor = PROJECT_COLOR_APPTEXT_MAINCOLOR;
    _title.font = PROJECT_TEXT_FONT_COMMON(15.0);
    
    _content = [[UILabel alloc] initWithFrame:CGRectMake(x,_title.frame.origin.y + _title.frame.size.height, w, itemH)];
    [self.contentView addSubview:_content];
    _content.textAlignment = NSTextAlignmentLeft;
    _content.textColor = PROJECT_COLOR_APPTEXT_SUBCOLOR;
    _content.font = PROJECT_TEXT_FONT_COMMON(12.0);
}


- (void)makeUIForType2{
    [self makeUIForType1];
}

- (void)updateType:(NSInteger)type{
    _type = type;
}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    _cellModel = cellModel;
    
    NSString *url = _cellModel.iconUrl;
    NSString *title = _cellModel.titleStr;
    NSString *content = _cellModel.contentStr;
    
    
    if([url isKindOfClass:[NSString class]]){
        if([url hasPrefix:@"http"]){
            [_icon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
        }
        else{
            _icon.image = [UIImage imageNamed:url];
        }
    }
    
    if([title isKindOfClass:[NSString class]]){
        if(_type == 0){
            CGRect rect = [ProjectHelper helper_getFontSizeWithString:title useSetFont:_title.font withWidth:MAXFLOAT andHeight:MAXFLOAT];
            _title.frame = CGRectMake(self.sCellWidth / 2 - rect.size.width / 2 - 40.0, 0, rect.size.width, 25.0);
            _icon.frame = CGRectMake(_title.frame.origin.x + _title.frame.size.width + 10.0, _title.frame.origin.y + _title.frame.size.height / 2 - 25.0 / 2, 25.0, 25.0);
            if(!_icon.image){
                _icon.image = [UIImage imageNamed:@"me_qr@3x.png"];
            }
            _clearBtn.frame = CGRectMake(_title.frame.origin.x, _title.frame.origin.y, _title.frame.size.width + 40.0, _title.frame.size.height);
        }
        _title.text = title;
        
    }
    
    if([content isKindOfClass:[NSString class]]){
        _content.text = content;
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
