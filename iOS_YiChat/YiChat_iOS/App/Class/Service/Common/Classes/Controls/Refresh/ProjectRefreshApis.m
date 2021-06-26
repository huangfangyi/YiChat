//
//  ProjectRefreshApis.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectRefreshApis.h"

@implementation ProjectRefreshApis

+ (NSArray *)projectRefreshApis_getGifImagesWithName:(NSString *)name{
    
    NSURL *fileUrl = [[NSBundle mainBundle] URLForResource:name withExtension:@"gif"]; //加载GIF图片
    CGImageSourceRef gifSource = CGImageSourceCreateWithURL((CFURLRef) fileUrl, NULL);           //将GIF图片转换成对应的图片源
    size_t frameCout = CGImageSourceGetCount(gifSource);                                         //获取其中图片源个数，即由多少帧图片组成
    NSMutableArray *frames = [[NSMutableArray alloc] init];                                      //定义数组存储拆分出来的图片
    for (size_t i = 0; i < frameCout; i++) {
        CGImageRef imageRef = CGImageSourceCreateImageAtIndex(gifSource, i, NULL); //从GIF图片中取出源图片
        UIImage *imageName = [UIImage imageWithCGImage:imageRef];                  //将图片源转换成UIimageView能使用的图片源
        [frames addObject:imageName];                                              //将图片加入数组中
        CGImageRelease(imageRef);
    }
    
    return frames;
}

+ (NSArray *)getRefresAnimateImages{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 1; i < 5; i ++) {
        NSString *str = [NSString stringWithFormat:@"refresh%d.png",i];
        [arr addObject:[UIImage imageNamed:str]];
    }
    return arr;
}

@end
