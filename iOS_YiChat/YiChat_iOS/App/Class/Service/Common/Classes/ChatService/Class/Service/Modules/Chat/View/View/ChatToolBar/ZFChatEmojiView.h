//
//  ZFChatEmojiView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatEmojiView : UIView

@property (nonatomic) CGRect originRect;

- (void)createUI;

- (void)XYChatEmojiView_appearWithAnimate:(void(^)(void))animate;

- (void)XYChatEmojiView_disappearWithAnimate:(void(^)(void))finishInvocation;

- (void)updateHorizontal;

- (void)addSelecteDefaultEmojiInvocation:(NSDictionary *)dic;

- (void)addSendDefaultEmojiInvocation:(NSDictionary *)dic;
@end


@interface ZFChatMenuSingle : UIView

@property (nonatomic,strong) UIImage *icon;
@property (nonatomic,strong) NSString *str;

@property (nonatomic,strong) UIImageView *menuIcon;
@property (nonatomic,strong) UILabel *menuLab;

- (void)addInvocation:(NSDictionary *)invocation;

- (void)changeUIForSelecte:(BOOL)selecte;

- (void)createUI;

@end

NS_ASSUME_NONNULL_END
