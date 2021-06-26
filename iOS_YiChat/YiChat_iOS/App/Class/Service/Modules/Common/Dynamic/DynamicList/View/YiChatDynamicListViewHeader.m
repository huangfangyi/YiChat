//
//  YiChatDynamicListViewHeader.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicListViewHeader.h"
#import "ServiceGlobalDef.h"
#import "YiChatDynamicUIConfigure.h"
#import <UIImageView+WebCache.h>
@interface YiChatDynamicListViewHeader ()

@property (nonatomic,weak) YiChatDynamicUIConfigure *configure;

@property (nonatomic,strong) UIImageView *backImage;

@property (nonatomic,strong) UIImageView *userIcon;

@property (nonatomic,strong) UILabel *userNick;

@end

@implementation YiChatDynamicListViewHeader

+ (id)create{
    YiChatDynamicListViewHeader *header = [YiChatDynamicListViewHeader new];
    header.configure = [YiChatDynamicUIConfigure initialUIConfigure];
    header.frame = CGRectMake(0, 0,header.configure.dynamicHeaderSize.width, header.configure.dynamicHeaderSize.height);
    [header makeUI];
    return header;
}

- (void)makeUI{
    _backImage = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _configure.dynamicBackGroundImgSize.width, _configure.dynamicBackGroundImgSize.height)];
    [self addSubview:_backImage];
    _backImage.userInteractionEnabled = NO;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn addTarget:self action:@selector(changeBackGroundMethod:) forControlEvents:UIControlEventTouchUpInside];
    btn.frame = _backImage.frame;
    [self addSubview:btn];
    
    CGFloat x = self.frame.size.width - _configure.dynamicUserSelfIconSize.width - _configure.contentBlank;
    CGFloat y = _backImage.frame.origin.y + _backImage.frame.size.height - _configure.dynamicUserSelfIconSize.height / 2;
    CGFloat w = _configure.dynamicUserSelfIconSize.width;
    CGFloat h = _configure.dynamicUserSelfIconSize.height;
    
    _userIcon = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    [self addSubview:_userIcon];
    
    _userNick = [[UILabel alloc] initWithFrame:CGRectMake(_userIcon.frame.origin.x - _configure.contentBlank - _configure.dynamicUserSelfNickSize.width, _userIcon.frame.origin.y + 10.0, _configure.dynamicUserSelfNickSize.width, _configure.dynamicUserSelfNickSize.height)];
    [self addSubview:_userNick];
    _userNick.textColor = [UIColor whiteColor];
    _userNick.textAlignment = NSTextAlignmentRight;
}

- (void)changeBackgroundImage:(NSString *)userIcon{
     UIImage *placeHolder = [UIImage imageNamed:@"dynamicBackDefault.png"];
    if(userIcon && [userIcon isKindOfClass:[NSString class]]){
        if(userIcon.length >0){
            [_backImage sd_setImageWithURL:[NSURL URLWithString:userIcon] placeholderImage:placeHolder];
            return;
        }
    }
    _backImage.image = placeHolder;
}

- (void)updateUserInfo{
    NSString *userId = _userIdStr;
    if(userId && [userId isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                [ProjectHelper helper_getMainThread:^{
                    NSString *url = [model avatar];
                    NSString *nick = [model nickName];
                    if(url && [url isKindOfClass:[NSString class]]){
                        [_userIcon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                    }
                    else{
                        _userIcon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
                    }
                    if(nick && [nick isKindOfClass:[NSString class]]){
                        _userNick.text = nick;
                    }
                }];
            }
        }];
    }
}

- (void)updateData{
    [self changeBackgroundImage:self.backImageUrl];
    
    [self updateUserInfo];
}

- (void)changeBackGroundMethod:(UIButton *)btn{
    if(self.YiChatDynamicListViewHeaderClickBackGroud){
        self.YiChatDynamicListViewHeaderClickBackGroud();
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
