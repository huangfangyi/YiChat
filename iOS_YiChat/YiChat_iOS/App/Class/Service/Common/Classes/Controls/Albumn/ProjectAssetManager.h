//
//  ProjectAssetManager.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "ProjectAssetConfig.h"
NS_ASSUME_NONNULL_BEGIN

@interface ProjectAssetManager : NSObject

/// Default is 600px / 默认600像素宽
@property (nonatomic, assign) CGFloat photoPreviewMaxWidth;

@property (nonatomic,assign) NSInteger columnNumber;

@property (nonatomic,assign) CGFloat itemH;

@property (nonatomic,assign) CGFloat magin;

@property (nonatomic,assign) CGFloat interitemSpaceW;

@property (nonatomic,assign) CGFloat lineSpaceW;

+ (id)assetManager;

- (NSString *)getExportVideoItemPath;

- (PHImageRequestID)getPhotoWithAsset:(PHAsset *)asset photoWidth:(CGFloat)photoWidth completion:(void (^)(UIImage *photo,NSDictionary *info,BOOL isDegraded))completion progressHandler:(void (^)(double progress, NSError *error, BOOL *stop, NSDictionary *info))progressHandler networkAccessAllowed:(BOOL)networkAccessAllowed ;

- (void)getVideoWithAsset:(PHAsset *)asset completion:(void (^)(AVPlayerItem *playItem, NSDictionary *dic))completion ;

- (void)getVideoOutputPathWithAsset:(PHAsset *)asset success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure;

- (void)getVideoOutPutWithAsset:(PHAsset *)asset success:(void (^)(AVAsset *__nullable asset, AVAudioMix *__nullable audioMix, NSDictionary *__nullable info))success progress:(void (^)(double progress, NSError *__nullable error, BOOL *stop, NSDictionary *__nullable info))progress;

- (void)getVideoOutputPathWithAsset:(PHAsset *)asset presetName:(NSString *)presetName success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure;

- (void)exportVideoWithVideoAsset:(AVURLAsset *)videoAsset presetName:(NSString *)presetName success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure;

- (void)exportImgData:(NSData *)data success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure;

- (PHImageRequestID)requestImageDataForAsset:(PHAsset *)asset completion:(void (^)(NSData *imageData, NSString *dataUTI, UIImageOrientation orientation, NSDictionary *info))completion progressHandler:(void (^)(double progress, NSError *error, BOOL *stop, NSDictionary *info))progressHandler ;

- (NSString *)getNewTimeFromDurationSecond:(NSInteger)duration;

- (UIImage *)fixOrientation:(UIImage *)aImage ;
@end

NS_ASSUME_NONNULL_END
