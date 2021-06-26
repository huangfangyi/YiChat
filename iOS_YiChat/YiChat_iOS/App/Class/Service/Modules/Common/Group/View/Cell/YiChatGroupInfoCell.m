//
//  YiChatGroupInfoCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupInfoCell.h"
#import "YiChatGroupInfoModel.h"
#import "ServiceGlobalDef.h"
#import "ProjectTableCell+ServiceExtension.h"
@interface YiChatGroupInfoCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,strong) UILabel *contentLab;

@property (nonatomic,strong) UIImageView *contentIcon;

@property (nonatomic,strong) UISwitch *contentSwitch;

@end

@implementation YiChatGroupInfoCell

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
        
    }
    else if(_type == 1){
        //title + content
        [self makeTitle];
        
        [self makeContent];
        
        _contentLab.numberOfLines = 0;

    }
    else if(_type == 2){
        //title + icon
        [self makeTitle];
        
        [self makeContentIcon];
    }
    else if(_type == 3){
        //title + content
        [self makeTitle];
        
        [self makeContentSwitch];
    }
}

- (void)makeTitle{
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat w = (self.sCellWidth - PROJECT_SIZE_NAV_BLANK * 2) / 2;
    
    _title = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_title];
    _title.textAlignment = NSTextAlignmentLeft;
    _title.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _title.font = PROJECT_TEXT_FONT_COMMON(15.0);
}

- (void)makeContent{
    CGFloat w = self.sCellWidth - self.title.frame.origin.x - self.title.frame.size.width - PROJECT_SIZE_NAV_BLANK * 1.5  - [self getRightArrowSize].size.width;
    CGFloat x = self.sCellWidth - (self.title.frame.origin.x + self.title.frame.size.width  + PROJECT_SIZE_NAV_BLANK);
    
    _contentLab = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_contentLab];
    _contentLab.textAlignment = NSTextAlignmentRight;
    _contentLab.textColor = PROJECT_COLOR_TEXTGRAY;
    _contentLab.font = PROJECT_TEXT_FONT_COMMON(14.0);
}

- (void)makeContentIcon{
    CGFloat w = self.sCellHeight * 0.7;
    CGFloat x = self.sCellWidth - w - PROJECT_SIZE_NAV_BLANK -  [self getRightArrowSize].size.width - PROJECT_SIZE_NAV_BLANK;
    
    _contentIcon = [[UIImageView alloc] initWithFrame:CGRectMake(x, self.sCellHeight / 2 - w / 2, w, w)];
    _contentIcon.layer.cornerRadius = 2.0;
    _contentIcon.clipsToBounds = YES;
    [self.contentView addSubview:_contentIcon];
}

- (void)makeContentSwitch{
    CGFloat w = 50.0;
    CGFloat x = self.sCellWidth - w - PROJECT_SIZE_NAV_BLANK;
    CGFloat h = 30.0;
    
    _contentSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(x, self.sCellHeight / 2 - h / 2, w,  h )];
    [self.contentView addSubview:_contentSwitch];
    [_contentSwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    
}

- (void)switchAction:(UISwitch *)contentSwitch{
    
    if(![self.title.text isEqualToString:@"群禁言"]){
        _switchState = !_switchState;
        [_contentSwitch setOn:_switchState];
    }
    else{
        [_contentSwitch setOn:_switchState];
    }
    
    if(self.YiChatGroupInfoCellDidClickSwitch){
        self.YiChatGroupInfoCellDidClickSwitch(self.title.text,_switchState);
    }
}

- (void)changeGroupSilenceState:(BOOL)state{
    _switchState = state;
    [_contentSwitch setOn:state animated:YES];
}

- (void)setValueForTitle:(NSString *)title content:(NSString *)content{
    if(title && [title isKindOfClass:[NSString class]]){
        _title.text = title;
    }
    if(content && [content isKindOfClass:[NSString class]]){
        _contentLab.text = content;
        
        CGFloat w = 0;
        CGFloat x = 0;
        
        if(self.sIsHasRightArrow == NO){
            w = self.sCellWidth - self.title.frame.origin.x - self.title.frame.size.width - PROJECT_SIZE_NAV_BLANK * 1.5;
            x = self.sCellWidth - w - PROJECT_SIZE_NAV_BLANK;
            _contentLab.frame = CGRectMake(x, 0, w, self.sCellHeight);
        }
        else{
            w = self.sCellWidth - self.title.frame.origin.x - self.title.frame.size.width - PROJECT_SIZE_NAV_BLANK * 1.5 - [self getRightArrowSize].size.width;
            x = self.sCellWidth - w - PROJECT_SIZE_NAV_BLANK * 2 - [self getRightArrowSize].size.width;
            _contentLab.frame = CGRectMake(x, 0, w, self.sCellHeight);
        }
    }
    
}

- (void)setValueForTitle:(NSString *)title contentIcon:(NSString *)url{
    if(title && [title isKindOfClass:[NSString class]]){
        _title.text = title;
    }
    
    [self imageLoadIconWithUrl:url placeHolder:[UIImage imageNamed:PROJECT_ICON_GROUPDEFAULT] imageControl:_contentIcon];
    
    CGFloat w = _contentIcon.frame.size.width;
    CGFloat x = 0;
    
    if(self.sIsHasRightArrow == NO){
        x = self.sCellWidth - w  - PROJECT_SIZE_NAV_BLANK;
        _contentIcon.frame = CGRectMake(x, self.sCellHeight / 2 - w / 2, w, w);
    }
    else{
        x = self.sCellWidth - w - PROJECT_SIZE_NAV_BLANK -  [self getRightArrowSize].size.width - PROJECT_SIZE_NAV_BLANK;
        _contentIcon.frame = CGRectMake(x, self.sCellHeight / 2 - w / 2, w, w);
    }
    
}

- (void)setValueForTitle:(NSString *)title contentSwitch:(BOOL)state{
    if(title && [title isKindOfClass:[NSString class]]){
        _title.text = title;
    }
    _switchState = state;
    [_contentSwitch setOn:state];
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
