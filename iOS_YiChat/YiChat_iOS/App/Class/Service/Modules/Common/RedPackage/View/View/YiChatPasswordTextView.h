//
//  YiChatPasswordTextView.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/8.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatPasswordTextView : UIView
/**
 the password is user inputed
 */
@property (nonatomic, copy) void(^passwordDidChangeBlock)(NSString *password);

/**
 set element count, default is 4, remove all elements and creat new elemets when it was set
 */
@property (nonatomic, assign) NSInteger elementCount;

/**
 set element color, default is balck color
 */
@property (nonatomic, strong) UIColor *elementBorderColor;

/**
 set element margein, default is 4 point
 */
@property (nonatomic, assign) CGFloat elementMargin;

/**
 auto hide the keyboard when input password was completed, default is YES
 */
@property (nonatomic, assign) BOOL autoHideKeyboard;

/**
 set element border width, default's 1 point
 */
@property (nonatomic, assign) CGFloat elementBorderWidth;

/**
 clear all password
 */
- (void)clearPassword;


- (void)showKeyboard;

- (void)hideKeyboard;
@end

NS_ASSUME_NONNULL_END
