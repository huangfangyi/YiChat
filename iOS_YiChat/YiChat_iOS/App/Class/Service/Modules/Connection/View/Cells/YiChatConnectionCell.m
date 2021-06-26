//
//  YiChatConnectionCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConnectionCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "YiChatUserModel.h"
#import "ProjectTableCell+ServiceExtension.h"
@interface YiChatConnectionCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *nick;

@end

@implementation YiChatConnectionCell

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
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(10.0, 10.0, self.sCellHeight - 20.0, self.sCellHeight - 20.0)];
    [self.contentView addSubview:_icon];
    if(_type == 0){
        _icon.layer.cornerRadius = 5.0;
        _icon.clipsToBounds = YES;
    }
    else{
        _icon.layer.cornerRadius = _icon.frame.size.height / 2;
        _icon.clipsToBounds = YES;
    }
    
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + 10.0;
    CGFloat w = self.sCellWidth - x - 10.0;
    
    _nick = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_nick];
    _nick.textAlignment = NSTextAlignmentLeft;
    _nick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _nick.font = PROJECT_TEXT_FONT_COMMON(14.0);
    
    _connectionNewMessageIcons = [[UIView alloc] initWithFrame:CGRectMake(self.sCellWidth - 10.0 - PROJECT_SIZE_NAV_BLANK, self.sCellHeight / 2 - 10.0 / 2, 10.0, 10.0)];
    [self.contentView addSubview:_connectionNewMessageIcons];
    _connectionNewMessageIcons.layer.cornerRadius = 5.0;
    _connectionNewMessageIcons.clipsToBounds = YES;
    _connectionNewMessageIcons.backgroundColor = [UIColor redColor];
    

}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    if(cellModel && [cellModel isKindOfClass:[ProjectCommonCellModel class]]){
        _cellModel = cellModel;
        
        NSString *url = _cellModel.iconUrl;
        NSString *nick = _cellModel.titleStr;
        [self imageLoadIconWithUrl:url placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] imageControl:_icon];
        
        if([nick isKindOfClass:[NSString class]]){
            [UIView performWithoutAnimation:^{
               _nick.text = nick;
            }];
        }
    }
}

- (void)setUserModel:(YiChatUserModel *)userModel{
    if((userModel && [userModel isKindOfClass:[YiChatUserModel class]])){
        _userModel = userModel;
        
        _nick.text = [_userModel appearName];
        
        WS(weakSelf);
        [ProjectHelper projectHelper_asyncLoadNetImage:userModel.avatar imageView:_icon placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] invocation:^NSString * _Nonnull{
            return weakSelf.userModel.avatar;
        }];

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
