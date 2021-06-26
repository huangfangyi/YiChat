//
//  YiChatGroupMemberSingleCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupMemberSingleCell.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupMemberListModel.h"
#import "UIView+LoadIconExtension.h"
#import "YiChatUserModel.h"
#import <UIImageView+WebCache.h>

@interface YiChatGroupMemberSingleCell ()

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,assign) NSIndexPath  *index;

@end

@implementation YiChatGroupMemberSingleCell

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    CGFloat iconH = self.contentView.frame.size.height - 20.0;
    _icon = [UIImageView new];
    _icon.frame = CGRectMake(self.contentView.frame.size.width / 2 - iconH / 2 , 5.0,iconH , iconH);
    _icon.layer.cornerRadius = iconH / 2;;
    _icon.clipsToBounds = YES;
    [self.contentView addSubview:_icon];
    
    _title = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, _icon.frame.origin.y + _icon.frame.size.height, self.contentView.frame.size.width, self.contentView.frame.size.height - (_icon.frame.origin.y + _icon.frame.size.height)) andfont:PROJECT_TEXT_FONT_COMMON(12.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentCenter];
    [self.contentView addSubview:_title];
    
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = _icon.frame;
    [self.contentView addSubview:btn];
    [btn addTarget:self action:@selector(btnMethod:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)setModel:(YiChatGroupMemberListModel *)model{
    if(model && [model isKindOfClass:[YiChatGroupMemberListModel class]]){
        _userModel = nil;
        _model = model;
        _icon.image = nil;
        
        if(_model.iconUrl && [_model.iconUrl isKindOfClass:[NSString class]]){
            _icon.image = [UIImage imageNamed:_model.iconUrl];
        }
        
        if(_model.name && [_model.name isKindOfClass:[NSString class]]){
            _title.text = _model.name;
        }
        else{
            _title.text = @"";
        }
    }
}

- (void)setUserModel:(YiChatUserModel *)userModel{
    if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
        _model = nil;
        _userModel = userModel;
        
        NSString *url = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:userModel.avatar];
        
        [_icon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
            if(!error && image){
                if(self && self.userModel && [self.userModel isKindOfClass:[YiChatUserModel class]]){
                    
                    NSString *currentCellUrl = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:self.userModel.avatar];
                    
                    if(currentCellUrl && [currentCellUrl isKindOfClass:[NSString class]]){
                        if(imageURL && [imageURL isKindOfClass:[NSURL class]]){
                            if(currentCellUrl.length > 0){
                                if([imageURL.absoluteString isEqualToString:currentCellUrl]){
                                    _icon.image = image;
                                    return ;
                                }
                            }
                        }
                    }
                }
            }
            _icon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
        }];
        
        _title.text = [userModel appearName];
    }
}

- (void)btnMethod:(UIButton *)btn{
    if(self.yiChatGroupMemberSingleCellClick){
        if(_model){
            self.yiChatGroupMemberSingleCellClick(_model);
        }
        else if(_userModel){
            [self becomeFirstResponder];
            self.yiChatGroupMemberSingleCellClick(_userModel);
        }
    }
}
    
- (UIImageView *)getIconBack{
    return _icon;
}
    
- (BOOL)canBecomeFirstResponder{
    return YES;
}

@end
