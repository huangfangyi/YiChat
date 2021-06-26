//
//  ProjectConfigure.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/23.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectConfigure : NSObject

@property (nonatomic,assign) CGSize navSize;

@property (nonatomic,assign) CGSize statusSize;

@property (nonatomic,assign) CGSize tabSize;

@property (nonatomic,assign) CGFloat screenHeight;

@property (nonatomic,assign) UIEdgeInsets safeArea;



+ (id)defaultConfigure;

- (CGFloat)screenHeight;

- (void)navInitial;

- (void)tabInitial;

- (UITabBarController *)getTabBarVC;

- (void)jumpToMain;

- (UIViewController *)getAppAppearLoginVC;

- (void)backToLogin;
@end

NS_ASSUME_NONNULL_END
