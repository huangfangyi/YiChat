//
//  YiChatChangeUserInfoInputView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatChangeUserInfoInputView : UIView


- (id)initWithFrame:(CGRect)frame
        placeHolder:(NSString *)placeHolder
         headerText:(NSString *)headerText
         footerText:(NSString *)footerText;

- (id)initWithFrame:(CGRect)frame
        placeHolder:(NSString *)placeHolder
         headerText:(NSString *)headerText
         footerText:(NSString *)footerText isTextView:(BOOL)isTextView;

- (void)resignKeyBoard;

- (NSString *)getInputText;

- (void)changeInputText:(NSString *)text;
    
- (UITextField *)getInputTextControl;

@end

NS_ASSUME_NONNULL_END
