//
//  ProjectAssetConfig.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#ifndef ProjectAssetConfig_h
#define ProjectAssetConfig_h

#import <Photos/Photos.h>

typedef NSDictionary ProjectAlbumnModelAlbumnsInfo;
typedef NSArray ProjectAlbumnModelAlbumnsInfoList;
typedef NSArray ProjectAssetModelArr;

typedef enum : NSUInteger {
    ProjectAssetModelMediaTypePhoto = 0,
    ProjectAssetModelMediaTypeLivePhoto,
    ProjectAssetModelMediaTypePhotoGif,
    ProjectAssetModelMediaTypeVideo,
    ProjectAssetModelMediaTypeAudio
} ProjectAssetModelMediaType;

typedef void (^ProjectAssetVideoProgressHandler)(double progress, NSError *__nullable error, BOOL *stop, NSDictionary *__nullable info);

typedef void (^ProjectAssetPhotoProgressHandler)(double progress, NSError *__nullable error, BOOL *stop, NSDictionary *__nullable info);


#endif /* ProjectAssetConfig_h */
