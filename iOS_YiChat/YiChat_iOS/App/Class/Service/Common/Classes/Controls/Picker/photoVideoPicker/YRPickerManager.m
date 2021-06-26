//
//  YRPickerManager.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/28.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "YRPickerManager.h"
#import <TZImagePickerController/TZImagePickerController.h>
#import "YRGeneralApis.h"
#import "GJVideoViewController.h"

@interface YRPickerManager ()<TZImagePickerControllerDelegate,UINavigationControllerDelegate,UIImagePickerControllerDelegate>

@end

static YRPickerManager *manager = nil;

@implementation YRPickerManager

+ (instancetype)defaultManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[YRPickerManager alloc] init];
    });
    
    return manager;
}

/**
 * 可以拍照 可以选择图片 不能拍视频 不能选择视频
 */
- (UINavigationController *)pickerImageWithMaxCount:(NSInteger)count delegate:(id)obj{
    TZImagePickerController *picker = [[TZImagePickerController alloc] initWithMaxImagesCount:count delegate:obj];
    picker.allowTakeVideo = NO;
    picker.allowPickingVideo = NO;
    
    return picker;
}

//用户只能拍视频或者选视频
- (UINavigationController *)pickerVideoWithMaxCount:(NSInteger)count delegate:(id)obj{
    TZImagePickerController *picker = [[TZImagePickerController alloc] initWithMaxImagesCount:count delegate:obj];
    picker.allowTakePicture = NO;
    picker.allowPickingImage = NO;
    return picker;
}

/**
 *  只能在相册选择图片
 */
- (UINavigationController *)pickerImageFromAlumnWithMaxCount:(NSInteger)count delegate:(id)obj{
    TZImagePickerController *picker = [[TZImagePickerController alloc] initWithMaxImagesCount:count delegate:obj];
    picker.allowTakeVideo = NO;
    picker.allowPickingVideo = NO;
    picker.allowTakePicture = NO;
    
    return picker;
}

//只能拍照选照片
- (UINavigationController *)pickerImageTakeWithMaxCount:(NSInteger)count delegate:(id)obj{
    UIImagePickerController *picker = [YRGeneralApis yrGeneralApis_GetPickerControllerWithType:YRPickerControllerTypeCamera];
    picker.delegate = obj;
    return picker;
}

/**
 *  只能在相册选择图片视频
 */
- (UINavigationController *)pickerImageVideoFromAlumnWithMaxCount:(NSInteger)count delegate:(id)obj{
    TZImagePickerController *picker = [[TZImagePickerController alloc] initWithMaxImagesCount:count delegate:obj];
    picker.allowPickingVideo = YES;
    picker.allowPickingImage = YES;
    picker.allowTakeVideo = NO;
    picker.allowTakePicture = NO;
    return picker;
}

//既能拍照选照片 又能录视频选视频
- (UINavigationController *)pickerImageOrVideoWithMaxCount:(NSInteger)count delegate:(id)obj{
    
    GJVideoViewController *video =  [[GJVideoViewController alloc] init];
    video.takeBlock = ^(id item, UIImage *coverImage) {
        if([item isKindOfClass:[UIImage class]]){
            if(self.yrPickerManagerDidTakeImages){
                self.yrPickerManagerDidTakeImages(item, item, NO);
            }
        }
        else if([item isKindOfClass:[NSURL class]]){
            NSURL *url = item;
            NSString *urlStr = url.path;
            
            
            if(self.yrPickerManagerDidTakeVideos){
                self.yrPickerManagerDidTakeVideos(coverImage,urlStr);
            }
        }
    };
    return video;
}


- (void)imagePickerController:(TZImagePickerController *)picker didFinishPickingVideo:(UIImage *)coverImage sourceAssets:(PHAsset *)asset{
    if(self.yrPickerManagerDidPickerVideos){
        
        self.yrPickerManagerDidPickerVideos(coverImage, asset);
        self.yrPickerManagerDidPickerVideos = nil;
    }
}

- (void)imagePickerController:(TZImagePickerController *)picker didFinishPickingPhotos:(NSArray<UIImage *> *)photos sourceAssets:(NSArray *)assets isSelectOriginalPhoto:(BOOL)isSelectOriginalPhoto{
    if(self.yrPickerManagerDidPickerImages){
        self.yrPickerManagerDidPickerImages(photos, assets, isSelectOriginalPhoto);
        self.yrPickerManagerDidPickerImages = nil;
    }
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    if(self.yrPickerManagerDidTakeImages){
        self.yrPickerManagerDidTakeImages(nil,nil, YES);
        self.yrPickerManagerDidTakeImages = nil;
    }
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(nonnull NSDictionary<UIImagePickerControllerInfoKey,id> *)info{
    
    if(self.yrPickerManagerDidTakeImages){
        if(info){
            self.yrPickerManagerDidTakeImages(info[UIImagePickerControllerOriginalImage],info[UIImagePickerControllerEditedImage],NO);
        }
        else{
            self.yrPickerManagerDidTakeImages(nil, nil, NO);
        }
        self.yrPickerManagerDidTakeImages = nil;
    }
    [picker dismissViewControllerAnimated:YES completion:nil];
}

@end

