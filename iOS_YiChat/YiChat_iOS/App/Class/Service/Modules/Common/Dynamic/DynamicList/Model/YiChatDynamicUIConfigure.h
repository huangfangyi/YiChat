//
//  YiChatDynamicUIConfigure.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatDynamicUIConfigure : NSObject

@property (nonatomic,assign) CGSize dynamicBackGroundImgSize;
@property (nonatomic,assign) CGSize dynamicHeaderSize;

@property (nonatomic,assign) CGSize dynamicUserIconSize;

@property (nonatomic,assign) CGFloat contentBlank;

@property (nonatomic,strong) UIFont *dynamicUserFont;
@property (nonatomic,strong) UIColor *dynamicUserColor;

@property (nonatomic,strong) UIFont *dynamicContentFont;
@property (nonatomic,strong) UIColor *dynamicContentColor;

@property (nonatomic,strong) UIFont *dynamicPraiseFont;
@property (nonatomic,strong) UIColor *dynamicPraiseColor;

@property (nonatomic,strong) UIFont *dynamicCommitFont;
@property (nonatomic,strong) UIColor *dynamicCommitColor;
@property (nonatomic,strong) UIFont *dynamicCommitUserNickFont;
@property (nonatomic,strong) UIColor *dynamicCommitUserNickColor;

@property (nonatomic,strong) UIFont *dynamicTimeFont;
@property (nonatomic,strong) UIColor *dynamicTimeColor;

@property (nonatomic,assign) CGSize dynamicDeleteBtnSize;

@property (nonatomic,strong) UIFont *dynamicDeleteFont;
@property (nonatomic,strong) UIColor *dynamicDeleteColor;

@property (nonatomic,assign) CGSize dynamicUserSelfIconSize;
@property (nonatomic,strong) UIFont *dynamicUserSelfNickFont;
@property (nonatomic,strong) UIColor *dynamicUserSelfNickColor;
@property (nonatomic,assign) CGSize dynamicUserSelfNickSize;

//评论点赞提醒
@property (nonatomic,assign) CGSize dynamicRemindSize;

//点击出现评论，点赞
@property (nonatomic,assign) CGSize dynamicToolClickSize;
@property (nonatomic,assign) CGSize dynamicCommitClickSize;
@property (nonatomic,assign) CGSize dynamicPraiseClickSize;
@property (nonatomic,assign) CGSize dynamicLikeIconSize;
@property (nonatomic,assign) CGSize dynamicCommitIconSize;


@property (nonatomic,assign) NSInteger numOfLineIcons;
@property (nonatomic,assign) NSInteger maxIconsAppear;

@property (nonatomic,strong) UIImage *userPlaceHolderIcon;

@property (nonatomic,strong) UIImage *videoPlayIcon;
@property (nonatomic,assign) CGSize videoPlayIconSize;

@property (nonatomic,strong) UIImage *dynamicToolClickIcon;
@property (nonatomic,strong) UIImage *dynamicLikeClickIcon;
@property (nonatomic,strong) UIImage *dynamicCommitClickIcon;
@property (nonatomic,strong) UIImage *dynamicLikeIcon;

@property (nonatomic,assign) CGSize dynamicToolBarSize;

@property (nonatomic,assign) CGSize singleImageSize;
@property (nonatomic,assign) CGSize videoSize;

@property (nonatomic,assign) CGRect userIconRect;
@property (nonatomic,assign) CGSize userNickSize;
@property (nonatomic,assign) CGFloat contentMaxSize;
@property (nonatomic,assign) CGFloat praiseMaxSize;
@property (nonatomic,assign) CGFloat commitMaxSize;

@property (nonatomic,assign) CGFloat imagesInterBlank;


+ (id)initialUIConfigure;

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str;

- (NSAttributedString *)tranlateStringToAttributedString:(NSString *)string font:(UIFont *)font;

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str withMaxSize:(CGSize)maxSize font:(UIFont *)font;
@end

NS_ASSUME_NONNULL_END
