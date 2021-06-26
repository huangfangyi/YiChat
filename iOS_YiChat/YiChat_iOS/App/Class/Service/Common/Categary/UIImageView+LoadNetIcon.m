//
//  UIImageView+LoadNetIcon.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "UIImageView+LoadNetIcon.h"
#import <SDWebImage/UIImageView+WebCache.h>

@implementation UIImageView (LoadNetIcon)

- (void)sd_loadNetIconWithUrlStr:(NSString *)urlStr placeHolder:(UIImage *)placeHodler{
    
    NSString * str1 = [urlStr stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSURL *url = [NSURL URLWithString:str1];
    
    [self sd_setImageWithURL:url placeholderImage:placeHodler];
}

@end
