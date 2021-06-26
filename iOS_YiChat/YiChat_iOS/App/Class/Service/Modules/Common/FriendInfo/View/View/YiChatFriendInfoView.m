//
//  YiChatFriendInfoView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatFriendInfoView.h"
#import "ServiceGlobalDef.h"
#import <SDWebImage/UIImageView+WebCache.h>
@interface YiChatFriendInfoView ()

@property (nonatomic,strong) UILabel *user_Nick;

//闲聊号
@property (nonatomic,strong) UILabel *user_chatId;

@property (nonatomic,strong) UIImageView *user_icon;

@property (nonatomic,strong) UIImageView *user_Sex;

@end

@implementation YiChatFriendInfoView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    
     UIImageView *user_icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, PROJECT_SIZE_NAV_BLANK, self.frame.size.height - PROJECT_SIZE_NAV_BLANK * 2, self.frame.size.height - PROJECT_SIZE_NAV_BLANK * 2) andImg:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    [self addSubview:user_icon];
    _user_icon = user_icon;
    user_icon.layer.cornerRadius = 5.0;
    user_icon.clipsToBounds = YES;
    
    UILabel *nick = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectZero andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self addSubview:nick];
    _user_Nick = nick;
    
    CGFloat x = user_icon.frame.origin.x * 2 + user_icon.frame.size.width;
    CGFloat h = (user_icon.frame.size.height - 10.0) / 2;
    
    UILabel *user_chatId = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x, user_icon.frame.origin.y + user_icon.frame.size.height - 10.0 - h, self.frame.size.width - PROJECT_SIZE_NAV_BLANK - x, h) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [self addSubview:user_chatId];
    _user_chatId = user_chatId;
    
    UIImageView *user_sex = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectZero andImg:nil];
    [self addSubview:user_sex];
    _user_Sex = user_sex;
}

- (void)setUserModel:(YiChatUserModel *)userModel{
    if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
         _userModel = userModel;
        
        NSString *iconUrl = _userModel.avatar;
        NSString *nickStr = [_userModel nickName];
        NSString *chatId = _userModel.appId;
        
        if(iconUrl && [iconUrl isKindOfClass:[NSString class]]){
            [_user_icon sd_setImageWithURL:[NSURL URLWithString:iconUrl] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
        }
        
        if(nickStr && [nickStr isKindOfClass:[NSString class]]){
            CGFloat sexIconW = 15.0;
            CGFloat sexIconH = 15.0;
            
            CGRect rect = [ProjectHelper helper_getFontSizeWithString:nickStr useSetFont:_user_Nick.font withWidth:_user_chatId.frame.size.width - sexIconW andHeight:_user_chatId.frame.size.height];
            _user_Nick.frame = CGRectMake(_user_chatId.frame.origin.x, _user_icon.frame.origin.y, rect.size.width, _user_chatId.frame.size.height);
            _user_Nick.text = nickStr;
            
            _user_Sex.frame = CGRectMake(_user_Nick.frame.origin.x + _user_Nick.frame.size.width,_user_Nick.frame.origin.y + _user_Nick.frame.size.height / 2 - sexIconH / 2 , sexIconW, sexIconH);
            _user_Sex.image = [self getUserSexWithType:userModel.gender];
            
        }
        
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            _user_chatId.text = [NSString stringWithFormat:@"%@号：%@",PROJECT_TEXT_APPNAME,chatId];
        }
        
        
    }
}

- (UIImage *)getUserSexWithType:(NSInteger)sex{
    if(sex == 0){
        return [UIImage imageNamed:@""];
    }
    else if(sex == 1){
        return [UIImage imageNamed:@""];
    }
    return nil;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
