//
//  ProjectSearchCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectSearchCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectTableCell+ServiceExtension.h"

@interface ProjectSearchCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *nick;
    
@property (nonatomic,strong) UIImageView *selecteIcon;
    
@property (nonatomic,assign) BOOL selecteState;

@end

@implementation ProjectSearchCell

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
    
    if(_type == 1){
        
        _selecteIcon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.sCellHeight / 2 - 18.0 / 2, 18.0, 18.0) andImg:nil];
        [self.contentView addSubview:_selecteIcon];
        
        
        _icon = [[UIImageView alloc] initWithFrame:CGRectMake(_selecteIcon.frame.origin.x + _selecteIcon.frame.size.width + PROJECT_SIZE_NAV_BLANK, 10.0, self.sCellHeight - 20.0, self.sCellHeight - 20.0)];
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
        
        UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectMake(0, 0, self.sCellWidth, self.sCellHeight) target:self method:@selector(selecteBtnMethod:)];
        [self.contentView addSubview:btn];
        
    }
    else{
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

    }
}

- (void)setUserModel:(YiChatUserModel *)userModel{
    if((userModel && [userModel isKindOfClass:[YiChatUserModel class]])){
        _userModel = userModel;
        
        _nick.text = [_userModel appearName];
        
        [self imageLoadIconWithUrl:userModel.avatar placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] imageControl:_icon];
        
        if(_type == 1){
            id obj = objc_getAssociatedObject(_userModel, @"state");
            
            if(obj && [obj isKindOfClass:[NSNumber class]]){
                NSNumber *selecteState = obj;
                
                if(selecteState && [selecteState isKindOfClass:[NSNumber class]]){
                    _selecteState = selecteState.boolValue;
                    _selecteIcon.image = [self getSelcteIconWithState:_selecteState];
                }
            }
            
            NSNumber *canSelecteState = objc_getAssociatedObject(_userModel, @"selecteState");
            if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
                if(canSelecteState.boolValue){
                    _selecteIcon.image = [UIImage imageNamed:@"cannotSelecteCirce.png"];
                    _nick.textColor = PROJECT_COLOR_TEXTGRAY;
                }
            }
        }
    }
}

- (void)selecteBtnMethod:(UIButton *)btn{
    NSNumber *canSelecteState = objc_getAssociatedObject(_userModel, @"selecteState");
    if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
        if(!canSelecteState.boolValue){
            _selecteState = !_selecteState;
            _selecteIcon.image = [self getSelcteIconWithState:_selecteState];
            
            objc_setAssociatedObject(_userModel, @"state", [NSNumber numberWithBool:_selecteState], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
            
            if(self.ProjectSearchCellSelecte){
                self.ProjectSearchCellSelecte(_userModel, _selecteState);
            }
        }
    }
}
    
- (UIImage *)getSelcteIconWithState:(BOOL)state{
    if(state){
        return [UIImage imageNamed:@"selecteCircle@3x.png"];
    }
    else{
        return [UIImage imageNamed:@"unselecteCircle@3x.png"];
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
