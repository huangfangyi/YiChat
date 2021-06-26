//
//  ProjectTableCell+ServiceExtension.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell+ServiceExtension.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "ProjectDef.h"
#import "ProjectStorageApis.h"

@implementation ProjectTableCell (ServiceExtension)

- (void)imageLoadIconWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl{
    if([url isKindOfClass:[NSString class]] && [imageControl isKindOfClass:[UIImageView class]] && imageControl && url){
        if([url hasPrefix:@"http"]){
            
            NSString * str1 = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            NSURL *value = [NSURL URLWithString:str1];
            
            if(value && [value isKindOfClass:[NSURL class]]){
                
                [imageControl sd_setImageWithURL:value placeholderImage:placeHolder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                    if(self){
                        if(!error){
                            [ProjectHelper helper_getMainThread:^{
                                imageControl.image = image;
                            }];
                        }
                        else{
                            [ProjectHelper helper_getMainThread:^{
                                imageControl.image = placeHolder;
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

- (void)imageLoadIconDealSizeWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl{
    if([url isKindOfClass:[NSString class]] && [imageControl isKindOfClass:[UIImageView class]] && imageControl && url){
        if([url hasPrefix:@"http"]){
            
            NSString * str1 = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            NSURL *value = [NSURL URLWithString:str1];
            
            if(value && [value isKindOfClass:[NSURL class]]){
                [[SDWebImageDownloader sharedDownloader] downloadImageWithURL:value options:SDWebImageDownloaderUseNSURLCache progress:^(NSInteger receivedSize, NSInteger expectedSize, NSURL * _Nullable targetURL) {
                    
                } completed:^(UIImage * _Nullable image, NSData * _Nullable data, NSError * _Nullable error, BOOL finished) {
                    [ProjectHelper helper_getMainThread:^{
                        if(imageControl && image){
                            imageControl.image =  [ProjectHelper helper_getSquareIconFromImage:image];;
                        }
                    }];
                }];
            }
        }
        else if([url hasSuffix:@"png"] || [url hasSuffix:@"jpg"]){
            UIImage *icon = [[UIImage alloc] initWithContentsOfFile:url];
            if(icon == nil){
                imageControl.image = [UIImage imageNamed:url];
            }
            else{
                imageControl.image = icon;
            }
        }
    }
    if(url == nil && placeHolder && [placeHolder isKindOfClass:[UIImage class]]  && [imageControl isKindOfClass:[UIImageView class]]){
        imageControl.image = placeHolder;
    }
}

@end
