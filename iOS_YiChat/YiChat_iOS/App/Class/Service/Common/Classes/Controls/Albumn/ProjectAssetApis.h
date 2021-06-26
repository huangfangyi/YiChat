//
//  ProjectAssetApis.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "ProjectAssetConfig.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectAssetApis : NSObject

+ (UIImage *)scaleImage:(UIImage *)image toSize:(CGSize)size;

+ (UIImage *)fixOrientation:(UIImage *)aImage;

+ (int)degressFromVideoFileWithAsset:(AVAsset *)asset;

+ (AVMutableVideoComposition *)fixedCompositionWithAsset:(AVAsset *)videoAsset;

+ (BOOL)isVideo:(PHAsset *)asset;

+ (ProjectAssetModelMediaType)getAssetType:(PHAsset *)asset;

+ (void)getVideoAssetThumb:(AVURLAsset *)urlAsset invocation:(void(^)(BOOL isSuccess,UIImage *thubm))invocation;

@end

NS_ASSUME_NONNULL_END
