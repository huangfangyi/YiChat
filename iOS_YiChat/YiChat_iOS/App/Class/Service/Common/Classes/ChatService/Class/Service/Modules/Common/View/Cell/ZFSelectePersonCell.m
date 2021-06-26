//
//  ZFSelectePersonCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFSelectePersonCell.h"
#import "ZFChatGlobal.h"
#import "ProjectTableCell+ServiceExtension.h"
#import <UIImageView+WebCache.h>

@interface ZFSelectePersonCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *nick;

@property (nonatomic,strong) UIImageView *selecteIcon;

@property (nonatomic,assign) BOOL selecteState;


@end

@implementation ZFSelectePersonCell

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
        [self makeUIForSelectePerson];
    }
    
    else if(_type == 1){
        [self makeUIForUnselecte];
    }
    
}

- (void)makeUIForSelectePerson{
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

- (void)makeUIForUnselecte{
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
    
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + 10.0;
    CGFloat w = self.sCellWidth - x - 10.0;
    
    _nick = [[UILabel alloc] initWithFrame:CGRectMake(x, 0, w, self.sCellHeight)];
    [self.contentView addSubview:_nick];
    _nick.textAlignment = NSTextAlignmentLeft;
    _nick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _nick.font = PROJECT_TEXT_FONT_COMMON(14.0);
}

- (void)setCellModel:(NSDictionary *)cellModel{
    if(cellModel && [cellModel isKindOfClass:[NSDictionary class]]){
        
        _cellModel = cellModel;
        _nick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
        
        id obj = objc_getAssociatedObject(_cellModel, @"state");
        
        if(obj && [obj isKindOfClass:[NSNumber class]]){
            NSNumber *selecteState = obj;
            
            if(selecteState && [selecteState isKindOfClass:[NSNumber class]]){
                _selecteState = selecteState.boolValue;
                _selecteIcon.image = [self getSelcteIconWithState:_selecteState];
            }
        }
        
        NSNumber *canSelecteState = objc_getAssociatedObject(_cellModel, @"selecteState");
        if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
            if(canSelecteState.boolValue){
                _selecteIcon.image = [UIImage imageNamed:@"cannotSelecteCirce.png"];
                _nick.textColor = PROJECT_COLOR_TEXTGRAY;
            }
        }
        
        NSString *url = [self getIconUrl:cellModel];
        NSString *nick = @"";
        UIImage *placeHolder = nil;
        
        if([cellModel.allKeys containsObject:@"remark"]){
            nick = cellModel[@"remark"];
        }
        else{
            if([cellModel.allKeys containsObject:@"nick"]){
                nick = cellModel[@"nick"];
                placeHolder = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
            }
            
            if([cellModel.allKeys containsObject:@"name"]){
                nick = cellModel[@"name"];
                placeHolder = [UIImage imageNamed:PROJECT_ICON_GROUPDEFAULT];
            }
        }
        
        
        WS(weakSelf);
        [ProjectHelper projectHelper_asyncLoadNetImage:url imageView:_icon placeHolder:placeHolder invocation:^NSString * _Nonnull{
            return [weakSelf getIconUrl:weakSelf.cellModel];
        }];
        
        
        if([nick isKindOfClass:[NSString class]]){
            _nick.text = nick;
        }
    }
}

- (NSString *)getIconUrl:(NSDictionary *)cellModel{
    NSString *url = nil;
    if([cellModel.allKeys containsObject:@"avatar"]){
        url = cellModel[@"avatar"];
    }
    
    if([cellModel.allKeys containsObject:@"imgurlde"]){
        url = cellModel[@"imgurlde"];
        
    }
    return url;
}

- (void)setIcon:(UIImage *)icon name:(NSString *)name{
    if(icon && [icon isKindOfClass:[UIImage class]]){
        _icon.image = icon;
    }
    if(name && [name isKindOfClass:[NSString class]]){
        _nick.text = name;
    }
}

- (void)selecteBtnMethod:(UIButton *)btn{
    NSNumber *canSelecteState = objc_getAssociatedObject(_cellModel, @"selecteState");
    if(canSelecteState && [canSelecteState isKindOfClass:[NSNumber class]]){
        if(!canSelecteState.boolValue){
            _selecteState = !_selecteState;
            _selecteIcon.image = [self getSelcteIconWithState:_selecteState];
            
            objc_setAssociatedObject(_cellModel, @"state", [NSNumber numberWithBool:_selecteState], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
            
            if(self.zfSelectePerson){
                self.zfSelectePerson(_cellModel, _selecteState);
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
