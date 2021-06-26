//
//  ProjectAlertView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/21.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectAlertView : UIView

/**
 *  ProjectAlertViewStyleDefault=0,
 *  ProjectAlertViewStyleTitleOneButton,
 *  ProjectAlertViewStyleTitleTwoButton,
 *  ProjectAlertViewStyleAutoDisappear 仅有title content 3秒自动消失
 */
+ (id)appearAlertWithStyle:(NSInteger)style title:(NSString *)title content:(NSString *)content buttonsDataSource:(NSArray *)btnsResource clickInvocation:(NSDictionary *)clickInvocation;

- (void)show;

- (void)clean;

+ (void)yrAlertViewAlertMessgae:(NSString *)msg;

+ (void)yrAlertWithAlertMessage:(NSString *)message clickBtns:(NSArray *)arr invocation:(void(^)(NSInteger row))click;

@end

NS_ASSUME_NONNULL_END
