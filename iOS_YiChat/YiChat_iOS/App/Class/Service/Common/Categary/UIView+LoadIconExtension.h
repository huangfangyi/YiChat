//
//  UIView+LoadIconExtension.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIView (LoadIconExtension)

- (void)imageLoadIconWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl;

@end

NS_ASSUME_NONNULL_END
