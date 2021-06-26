//
//  LoginRegisterTitleView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/23.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface LoginRegisterTitleView : UIView

@property (nonatomic,strong) UILabel *titleLab;

@property (nonatomic,strong) UIImageView *userIcon;

@property (nonatomic,strong) UILabel *userLab;

- (id)initWithFrame:(CGRect)frame
             titile:(NSString *)title
             bgView:(UIView *)bgView;

- (id)initWithFrame:(CGRect)frame
           userIcon:(NSString *)iconUrl
           userName:(NSString *)userName
             bgView:(UIView *)bgView;

@end

NS_ASSUME_NONNULL_END
