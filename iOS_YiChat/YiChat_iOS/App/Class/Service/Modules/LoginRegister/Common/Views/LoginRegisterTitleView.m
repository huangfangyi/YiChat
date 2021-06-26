//
//  LoginRegisterTitleView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/23.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "LoginRegisterTitleView.h"
#import "ServiceGlobalDef.h"
#import <SDWebImage/UIImageView+WebCache.h>
@interface LoginRegisterTitleView ()
{
    NSString *_title;
    
    NSString *_userName;
    NSString *_iconUrl;
}

@end

@implementation LoginRegisterTitleView

- (id)initWithFrame:(CGRect)frame
             titile:(NSString *)title
             bgView:(UIView *)bgView{
    self = [super initWithFrame:frame];
    if(self){
        _title = title;
        [bgView addSubview:self];
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:self.bounds andfont:PROJECT_TEXT_FONT_COMMON(18.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    _titleLab = lab;
    [self addSubview:lab];
    _titleLab.text = _title;
}

- (id)initWithFrame:(CGRect)frame
           userIcon:(NSString *)iconUrl
           userName:(NSString *)userName
             bgView:(UIView *)bgView{
    self = [super initWithFrame:frame];
    if(self){
        _userName = userName;
        _iconUrl = iconUrl;
        
        [bgView addSubview:self];
        
        [self makeUserIconNameUI];
    }
    return self;
}

- (void)makeUserIconNameUI{
    CGFloat blank = 20.0;
    
    CGFloat w;
    
    if(self.frame.size.width > self.frame.size.height){
       w = self.frame.size.height - 30.0;
    }
    else{
        w = self.frame.size.width - 30.0;
    }
    
    CGFloat h = w;
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width / 2 - w / 2, 0, w, h) andImg:nil];
    [self addSubview:icon];
    _userIcon = icon;
    icon.layer.cornerRadius = 10.0;
    icon.clipsToBounds = YES;
    
    
    UILabel *user = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, (icon.frame.origin.y + icon.frame.size.height), self.frame.size.width, self.frame.size.height - (icon.frame.origin.y + icon.frame.size.height)) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [self addSubview:user];
    _userLab = user;
    
    if([_iconUrl hasPrefix:@"http"] && [_iconUrl isKindOfClass:[NSString class]]){
        [_userIcon sd_setImageWithURL:[NSURL URLWithString:_iconUrl] placeholderImage:[[self class] appIcon]];
    }
    else{
        _userIcon.image = [[self class] appIcon];
    }
    
    if([_userName isKindOfClass:[NSString class]]){
        _userLab.text = _userName;
    }
    
}

+ (UIImage *)appIcon{
    NSDictionary *infoPlist = [[NSBundle mainBundle] infoDictionary];
    NSString *icon = [[infoPlist valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconFiles"] lastObject];
    UIImage* image = [UIImage imageNamed:icon];
    return image;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
