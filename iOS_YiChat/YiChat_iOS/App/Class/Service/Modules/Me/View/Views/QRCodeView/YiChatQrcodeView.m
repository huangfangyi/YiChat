//
//  YiChatQrcodeView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatQrcodeView.h"
#import "ServiceGlobalDef.h"
#import "NSString+URLEncoding.h"
#import "XYQRCodeProduct.h"
#import "YiChatUserManager.h"
#import <UIImageView+WebCache.h>
@interface YiChatQrcodeView ()

@property (nonatomic,strong) UIImageView *user_icon;

@property (nonatomic,strong) UILabel *user_nick;

@property (nonatomic,strong) UIImageView *qrcodeImg;

@property (nonatomic,strong) UILabel *desContent;


@end

@implementation YiChatQrcodeView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    CGFloat iconW = 196.0 / 1108.0 * self.frame.size.width;
    CGFloat iconH = iconW;
    CGFloat blank = 77.0 / 1108.0 * self.frame.size.width;
    CGFloat downTextH = iconH / 2;
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(blank, blank, iconW, iconH) andImg:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    [self addSubview:icon];
    _user_icon = icon;
    
    UILabel *name = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(icon.frame.origin.x + icon.frame.size.width + blank, icon.frame.origin.y + icon.frame.size.height / 2 - 30.0 / 2,self.frame.size.width - (icon.frame.origin.x + icon.frame.size.width + blank * 2), 30.0) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self addSubview:name];
    _user_nick = name;
    name.text = @"-----";
    
    CGFloat qrcodeH = self.frame.size.height - (icon.frame.origin.y * 4 + icon.frame.size.height + downTextH);
    CGFloat qrcdoeW = qrcodeH;
    
    if(qrcdoeW >= (self.frame.size.width - 20.0 * 4)){
        qrcdoeW = (self.frame.size.width - 20.0 * 4);
    }
    qrcodeH = qrcdoeW;
    
    UIImageView *qrCode = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width / 2 - qrcdoeW / 2, icon.frame.size.height + icon.frame.origin.y + blank, qrcdoeW, qrcodeH) andImg:nil];
    [self addSubview:qrCode];
    _qrcodeImg = qrCode;
    
//    NSDictionary *dic = @{};
//
//    NSData *data = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:nil];
//
//    NSString *jsonStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    NSString *qrStr = [self getQrCodeStr];
    
    qrCode.image = [self productQRCodeWithContent:qrStr size:qrCode.frame.size.height];
    _qrcode = qrCode.image;
    
    UILabel *textLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(icon.frame.origin.x, self.frame.size.height - downTextH - blank, self.frame.size.width - icon.frame.origin.x * 2, downTextH) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentCenter];
    textLab.text = [NSString stringWithFormat:@"使用%@扫描上面的二维码，加我",PROJECT_TEXT_APPNAME];
    [self addSubview:textLab];
    _desContent = textLab;
    
    
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
            [ProjectHelper helper_getMainThread:^{
                
                [_user_icon sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                _user_nick.text = [model appearName];
                
            }];
        }
    }];
}

- (UIImage *)productQRCodeWithContent:(NSString *)content size:(CGFloat)size{
    return  [XYQRCodeProduct qrCodeImageWithContent:content codeImageSize:size logo:[UIImage imageNamed:@"logo@3x.png"] logoFrame:CGRectMake(size / 2 - 30.0 / 2, size / 2 -30.0 / 2, 30, 30) red:100 / 255.0 green:100.0 / 255.0 blue:50.0 / 255.0];
}

- (NSString *)getQrCodeStr{
    return [[YiChatUserManager defaultManagaer] getQRCodeImageString];
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
