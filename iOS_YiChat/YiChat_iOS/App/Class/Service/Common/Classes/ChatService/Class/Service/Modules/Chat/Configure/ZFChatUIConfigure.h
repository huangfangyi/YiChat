//
//  ZFChatUIConfigure.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatUIConfigure : NSObject

@property (nonatomic,strong,readonly)  UIColor *timeAppearColor;
@property (nonatomic,strong,readonly)  UIColor *sendMSGTextColor;
@property (nonatomic,strong,readonly)  UIColor *receiveMSGTextColor;
@property (nonatomic,strong,readonly)  UIColor *sendMSGBackColor;
@property (nonatomic,strong,readonly)  UIColor *receiveMSGBackColor;
@property (nonatomic,strong,readonly)  UIColor *nickTextColor;

@property (nonatomic,strong,readonly)  UIColor *sendVoiceLabTextColor;
@property (nonatomic,strong,readonly)  UIColor *receiveVoceLabTextColor;

@property (nonatomic,strong,readonly)  UIFont *nickTextFont;
@property (nonatomic,strong,readonly)  UIFont *msgPersonNameFont;
@property (nonatomic,strong,readonly)  UIFont *msgFont;
@property (nonatomic,strong,readonly)  UIFont *headerTextFont;


@property (nonatomic,assign,readonly)  CGFloat messageBackMaxW;
@property (nonatomic,assign,readonly)  CGFloat messageTextMaxW;
@property (nonatomic,assign,readonly)  CGFloat messageIconW;
@property (nonatomic,assign,readonly)  CGSize messageNickSize;
@property (nonatomic,assign,readonly)  CGFloat messageBlank;
@property (nonatomic,assign,readonly)  CGFloat messageInterBlank;
@property (nonatomic,assign,readonly)  CGFloat messageMinCellH;

@property (nonatomic,assign,readonly)  CGSize sendIndicatorSize;
@property (nonatomic,assign,readonly)  CGSize sendFailImageSize;

@property (nonatomic,assign,readonly)  CGFloat photoMessageMinW;
@property (nonatomic,assign,readonly)  CGFloat photoMessageMaxW;
@property (nonatomic,assign,readonly)  CGSize messageImageLoadErrorSize;
@property (nonatomic,assign,readonly)  CGSize messageImageLoadingSize;

@property (nonatomic,assign,readonly)  CGFloat videoMessageMinW;
@property (nonatomic,assign,readonly)  CGFloat videoMessageMaxW;
@property (nonatomic,assign,readonly)  CGSize messageVideoLoadErrorSize;
@property (nonatomic,assign,readonly)  CGSize messageVideoLoadingSize;
@property (nonatomic,assign,readonly)  CGSize messageVideoImageSize;

@property (nonatomic,assign,readonly)  CGFloat mesaageVoiceH;
@property (nonatomic,assign,readonly)  CGFloat mesaageVoiceMinW;
@property (nonatomic,assign,readonly)  CGFloat messageVoiceInterBlank;
@property (nonatomic,assign,readonly)  CGSize messageVoiceIconSize;
    
@property (nonatomic,assign,readonly)  CGFloat mesaagePersonCardH;
@property (nonatomic,assign,readonly)  CGFloat mesaagePersonCardW;
@property (nonatomic,assign,readonly)  CGFloat mesaagePersonCardBlank;
@property (nonatomic,assign,readonly)  CGSize mesaagePersonCardIconSize;

@property (nonatomic,strong,readonly)  NSArray <UIImage *>*voiceLeftPlayIcons;
@property (nonatomic,strong,readonly)  NSArray <UIImage *>*voiceRightPlayIcons;

@property (nonatomic,strong,readonly)  UIImage *videoIcon;
@property (nonatomic,assign,readonly)  CGSize videoIconSize;
@property (nonatomic,strong,readonly)  UIColor *videoDurationColor;
@property (nonatomic,assign,readonly)  CGSize videoDurationSize;
@property (nonatomic,strong,readonly)  UIFont  *videoDurationFont;

@property (nonatomic,assign,readonly)  CGSize messageLocationSize;
@property (nonatomic,assign,readonly)  CGSize messageFileSize;

@property (nonatomic,assign,readonly)  CGFloat messageCommonCMDH;

@property (nonatomic,assign,readonly)  CGFloat messageCommonHeaderH;
@property (nonatomic,assign,readonly)  CGFloat messageCommonFooterH;

@property (nonatomic,assign,readonly)  UIImage *imageLoadErrorIcon;

@property (nonatomic,strong,readonly)  UIFont *commonCMDMSGFont;
@property (nonatomic,strong,readonly)  UIColor *commonCMDMSGTextColor;

@property (nonatomic,assign,readonly)  CGFloat redPackgeTitleSize;
@property (nonatomic,assign,readonly)  CGFloat redPackgeDesSize;
@property (nonatomic,assign,readonly)  CGFloat redPackgeDownSize;
@property (nonatomic,assign,readonly)  CGFloat redPackgeWidth;
@property (nonatomic,strong,readonly)  UIImage *redPakgeIcon;

@property (nonatomic,strong,readonly)  UIFont *redPakgeDesFont;
@property (nonatomic,strong,readonly)  UIFont *redPakgeTitleFont;

@property (nonatomic,strong,readonly)  UIColor *redPakgeDesColor;
@property (nonatomic,strong,readonly)  UIColor *redPakgeTitleColor;

@property (nonatomic,strong,readonly)  UIImage *commonGroupRoleIcon;
@property (nonatomic,strong,readonly)  UIImage *groupManagerIcon;
@property (nonatomic,strong,readonly)  UIImage *groupOwnerIcon;

+ (id)initialChatUIConfigure;

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str;

- (CGRect)getCommonCMDMessageRectWithText:(NSAttributedString *)str;

- (CGFloat)getVoiceMessageWidthWithDuration:(NSInteger)duration;

- (UIImage *)getVoiceRightAppearceIcon;

- (NSArray <UIImage *>*)getVoiceRightPlayAppearceIcons;

- (UIImage *)getVoiceLeftAppearceIcon;

- (NSArray <UIImage *>*)getVoiceLeftPlayAppearceIcons;

- (UIImage *)getGroupChatUserRoleWithPower:(NSInteger)power;

- (NSString *)getVideoMessageAppearDurationWithDuration:(CGFloat)duration;

- (NSAttributedString *)tranlateStringToAttributedString:(NSString *)string font:(UIFont *)font;

- (NSArray *)getWebsitesWithString:(NSString *)string;
@end

NS_ASSUME_NONNULL_END
