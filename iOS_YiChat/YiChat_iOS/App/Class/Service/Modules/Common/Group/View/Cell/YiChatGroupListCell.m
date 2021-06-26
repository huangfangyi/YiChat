//
//  YiChatGroupListCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupListCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectTableCell+ServiceExtension.h"
#import "YiChatGroupInfoModel.h"
@interface YiChatGroupListCell ()
{
    NSInteger _type;
}


@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *nick;

@end

@implementation YiChatGroupListCell

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
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 10.0, self.sCellHeight - 20.0, self.sCellHeight - 20.0)];
    [self.contentView addSubview:_icon];
    if(_type == 0){
        _icon.layer.cornerRadius = 5.0;
        _icon.clipsToBounds = YES;
    }
    else{
        _icon.layer.cornerRadius = _icon.frame.size.height / 2;
        _icon.clipsToBounds = YES;
    }
    
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + PROJECT_SIZE_NAV_BLANK;
    CGFloat w = self.sCellWidth - x - PROJECT_SIZE_NAV_BLANK;
    
    _nick = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_nick];
    _nick.textAlignment = NSTextAlignmentLeft;
    _nick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _nick.font = PROJECT_TEXT_FONT_COMMON(14.0);
    
}

- (void)setInfoModel:(YiChatGroupInfoModel *)infoModel{
    if(infoModel && [infoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        _infoModel = infoModel;
        
        WS(weakSelf);
        [ProjectHelper projectHelper_asyncLoadNetImage:infoModel.groupAvatar imageView:_icon placeHolder:[UIImage imageNamed:PROJECT_ICON_GROUPDEFAULT] invocation:^NSString * _Nonnull{
            return weakSelf.infoModel.groupAvatar;
        }];
        
        if(infoModel.groupName && [infoModel.groupName isKindOfClass:[NSString class]]){
            _nick.text = infoModel.groupName;
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
