//
//  ZFVoiceInputAlertView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFVoiceInputAlertView : UIView

- (void)makeUI;

//录音状态 == 1 提示取消状态 == 0
- (void)changeUIWithState:(NSInteger)state;

- (void)changeVolumn:(CGFloat)value;
@end

@interface ZFVoiceVolumnCheck : UIView

- (void)makeUI;

- (void)changeUIWithVolumnValue:(CGFloat)value;
@end

NS_ASSUME_NONNULL_END
