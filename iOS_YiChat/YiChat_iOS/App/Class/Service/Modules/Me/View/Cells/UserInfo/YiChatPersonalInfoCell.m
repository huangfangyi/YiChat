//
//  YiChatPersonalInfoCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPersonalInfoCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "UIImageView+LoadNetIcon.h"
#import "ProjectTableCell+ServiceExtension.h"

@interface YiChatPersonalInfoCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,strong) UILabel *content;

@property (nonatomic,strong) UIImageView *iconContent;

@end

@implementation YiChatPersonalInfoCell


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
    
    [self commonUI];
    
    if(_type == 0){
        [self makeUIWithType_0];
    }
    else if(_type == 1){
        [self makeUIWithType_1];
    }
    
}

- (void)commonUI{
   
    UILabel *title = [ProjectHelper helper_factoryMakeLabelWithFrame:[self getTitleFrame] andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:title];
    _title = title;
}

- (CGRect)getTitleFrame{
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = 0;
    CGFloat w = 100.0;
    CGFloat h = self.sCellHeight;
    return CGRectMake(x, y, w, h);
}

- (void)makeUIWithType_0{
   
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:[self getIconFrame] andImg:[UIImage imageNamed:@""]];
    
    [self.contentView addSubview:icon];
    _iconContent = icon;
    
}

- (CGRect)getIconFrame{
    CGFloat itemH = self.sCellHeight - 10.0 * 2;
    CGFloat x = self.sCellWidth - [self getRightArrowSize].size.width - PROJECT_SIZE_NAV_BLANK - 10.0 - itemH;
    return CGRectMake(x ,self.title.frame.origin.y + self.title.frame.size.height / 2 - itemH / 2, itemH, itemH);
}

- (void)makeUIWithType_1{
   UILabel *content = [ProjectHelper helper_factoryMakeLabelWithFrame:[self getContentFrame] andfont:PROJECT_TEXT_FONT_COMMON(13.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentRight];
    [self.contentView addSubview:content];
    _content = content;
}

- (CGRect)getContentFrame{
    CGFloat itemW = self.sCellWidth - (self.title.frame.origin.x + self.title.frame.size.width + [self getRightArrowSize].size.width + PROJECT_SIZE_NAV_BLANK - 10.0);
    CGFloat x = self.sCellWidth - [self getRightArrowSize].size.width - PROJECT_SIZE_NAV_BLANK - 10.0 - itemW;
    return   CGRectMake(x, 0, itemW, self.sCellHeight);
}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    if([cellModel isKindOfClass:[ProjectCommonCellModel class]]){
        _cellModel = cellModel;
        _title.frame = [self getTitleFrame];
        
        if([_cellModel.titleStr isKindOfClass:[NSString class]]){
            _title.text = _cellModel.titleStr;
        }
        
        if(_type == 0){
            _iconContent.frame = [self getIconFrame];
            
            if([_cellModel.contentUrl isKindOfClass:[NSString class]]){
                [self imageLoadIconWithUrl:_cellModel.contentUrl placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] imageControl:_iconContent];
            }
            
        }
        else if(_type == 1){
            _content.frame = [self getContentFrame];
            if([_cellModel.contentStr isKindOfClass:[NSString class]]){
               
                _content.text = _cellModel.contentStr;
            }
        }
        
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
