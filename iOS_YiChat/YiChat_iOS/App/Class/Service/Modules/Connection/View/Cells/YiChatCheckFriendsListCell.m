//
//  YiChatCheckFriendsListCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatCheckFriendsListCell.h"
#import "ServiceGlobalDef.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "ProjectCommonCellModel.h"
#import "YiChatUserModel.h"
#import "ProjectClickView.h"
@interface YiChatCheckFriendsListCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *nick;

@property (nonatomic,strong) ProjectClickView *add;
@property (nonatomic,strong) ProjectClickView *reject;

@property (nonatomic,strong) UILabel *stateLab;

@end

@implementation YiChatCheckFriendsListCell

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
    
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 10.0, self.sCellHeight - 10.0 * 2, self.sCellHeight - 10.0 * 2)];
    [self.contentView addSubview:_icon];
    if(_type == 0){
        _icon.layer.cornerRadius = 5.0;
        _icon.clipsToBounds = YES;
    }
    else{
        _icon.layer.cornerRadius = _icon.frame.size.height / 2;
        _icon.clipsToBounds = YES;
    }
    CGFloat certifyW = 40.0;
    CGFloat certifyH = 25.0;
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + PROJECT_SIZE_NAV_BLANK;
    CGFloat w = self.sCellWidth - x - PROJECT_SIZE_NAV_BLANK - (certifyW + PROJECT_SIZE_NAV_BLANK) * 2;
    
    _nick = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_nick];
    _nick.textAlignment = NSTextAlignmentLeft;
    _nick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _nick.font = PROJECT_TEXT_FONT_COMMON(14.0);
    
    
    for (int i = 0; i < 1; i ++) {
        NSString *title = nil;
        NSInteger type;
        if(i == 0){
            title = @"同意";
            type = 0;
        }
        else{
            title = @"拒绝";
            type = 1;
        }
        
        ProjectClickView *certifyLog = [ProjectClickView createClickViewWithFrame:CGRectMake(self.sCellWidth - (certifyW + PROJECT_SIZE_NAV_BLANK) * (2 - 1), self.sCellHeight / 2 - certifyH / 2, certifyW, certifyH) title:title type:type];
        certifyLog.userInteractionEnabled = YES;
        certifyLog.lab.font = PROJECT_TEXT_FONT_COMMON(13.0);
        certifyLog.clickInvocation = ^(NSString * _Nonnull identify) {
            if(i == 0){
                if(self.YiChatCheckFriendsListCellClickAdd){
                    self.YiChatCheckFriendsListCellClickAdd(self.cellModel);
                }
            }
            else if(i == 1){
                if(self.YiChatCheckFriendsListCellClickRefuse){
                    self.YiChatCheckFriendsListCellClickRefuse(self.cellModel);
                }
            }
        };
        if(i == 0){
            _add = certifyLog;
        }
        else{
            _reject = certifyLog;
        }
        [self.contentView addSubview:certifyLog];
    }
    w = certifyW * 1.5;
    x = _add.frame.origin.x + _add.frame.size.width / 2 - w / 2;
    //x = _add.frame.origin.x + (_reject.frame.size.width + _reject.frame.origin.x - _add.frame.origin.x) / 2 - w / 2;
    
    _stateLab = [[UILabel alloc] initWithFrame:CGRectMake(x, self.sCellHeight / 2 - certifyH / 2,w, certifyH)];
    [self.contentView addSubview:_stateLab];
    _stateLab.textAlignment = NSTextAlignmentLeft;
    _stateLab.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _stateLab.font = PROJECT_TEXT_FONT_COMMON(14.0);
    
}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    _cellModel = cellModel;
    
    NSString *url = _cellModel.iconUrl;
    NSString *nick = _cellModel.titleStr;
    
    NSString *state = _cellModel.state;
    
    if([url isKindOfClass:[NSString class]]){
        if([url hasPrefix:@"http"] && url.length > 0){
        
            NSString * str1 = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            NSURL *value = [NSURL URLWithString:str1];
            
            [_icon sd_setImageWithURL:value placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
        }
        else{
            _icon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
        }
    }
    else{
        _icon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
    }
    
    if(state && [state isKindOfClass:[NSString class]]){
        if([state integerValue] == 0){
            _add.hidden = NO;
            _reject.hidden = NO;
            _stateLab.hidden = YES;
        }
        else if([state integerValue] == 1){
            _add.hidden = YES;
            _reject.hidden = YES;
            _stateLab.hidden = NO;
            
            _stateLab.text = @"已同意";
        }else{
            _add.hidden = YES;
            _reject.hidden = YES;
            _stateLab.hidden = NO;
            _stateLab.text = @"被通过";
        }
    }
    
    if([nick isKindOfClass:[NSString class]]){
        _nick.text = nick;
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
