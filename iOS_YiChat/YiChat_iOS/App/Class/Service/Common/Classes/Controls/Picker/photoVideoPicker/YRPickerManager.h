//
//  YRPickerManager.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/28.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <TZImagePickerController/TZImagePickerController.h>
NS_ASSUME_NONNULL_BEGIN

@interface YRPickerManager : NSObject

@property (nonatomic,copy) void(^yrPickerManagerDidTakeImages)(UIImage *originIcon,UIImage *editedIcon,BOOL isCancle);

@property (nonatomic,copy) void(^yrPickerManagerDidPickerImages)(NSArray <UIImage *>*images,NSArray *assets,BOOL isSelectOriginalPhoto);

@property (nonatomic,copy) void(^yrPickerManagerDidPickerVideos)(UIImage *coverImage,PHAsset *asset);

@property (nonatomic,copy) void(^yrPickerManagerDidTakeVideos)(UIImage *coverImage,NSString *movFilePath);

+ (instancetype)defaultManager;

/**
 * 可以拍照 可以选择图片 不能拍视频 不能选择视频
 */
- (UINavigationController *)pickerImageWithMaxCount:(NSInteger)count delegate:(id)obj;

/**
 *  用户只能拍视频或者选视频
 */
- (UINavigationController *)pickerVideoWithMaxCount:(NSInteger)count delegate:(id)obj;

/**
 *  只能在相册选择图片
 */
- (UINavigationController *)pickerImageFromAlumnWithMaxCount:(NSInteger)count delegate:(id)obj;

//只能拍照选照片
- (UINavigationController *)pickerImageTakeWithMaxCount:(NSInteger)count delegate:(id)obj;

/**
 *  只能在相册选择图片视频
 */
- (UINavigationController *)pickerImageVideoFromAlumnWithMaxCount:(NSInteger)count delegate:(id)obj;

//既能拍照选照片 又能录视频选视频
- (UINavigationController *)pickerImageOrVideoWithMaxCount:(NSInteger)count delegate:(id)obj;

@end

NS_ASSUME_NONNULL_END
