//
//  UIView+LoadIconExtension.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "UIView+LoadIconExtension.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "ServiceGlobalDef.h"

@implementation UIView (LoadIconExtension)

- (void)imageLoadIconWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl{
    if([url isKindOfClass:[NSString class]] && [imageControl isKindOfClass:[UIImageView class]] && imageControl && url){
        if([url hasPrefix:@"http"]){
            
            NSString * str1 = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            NSURL *value = [NSURL URLWithString:str1];
            
            if(value && [value isKindOfClass:[NSURL class]]){
                [imageControl sd_setImageWithURL:value placeholderImage:placeHolder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                    if(self){
                        if(url && [url isEqualToString:imageURL.absoluteString] && imageControl && !error){
                            [ProjectHelper helper_getMainThread:^{
                                imageControl.image = image;
                            }];
                        }
                    }
                }];
            }
        }
        else if([url hasSuffix:@"png"] || [url hasSuffix:@"jpg"]){
            NSData *data = [[NSData alloc] initWithContentsOfFile:url];
            UIImage *icon = [[UIImage alloc] initWithData:data];
            
            if(icon == nil){
                imageControl.image = [UIImage imageNamed:url];
            }
            else{
                imageControl.image = icon;
            }
        }
        else if(url.length == 0 && placeHolder && [placeHolder isKindOfClass:[UIImage class]] ){
            imageControl.image = placeHolder;
        }
    }
    if(url == nil && placeHolder && [placeHolder isKindOfClass:[UIImage class]]  && [imageControl isKindOfClass:[UIImageView class]]){
        imageControl.image = placeHolder;
    }
}
@end
