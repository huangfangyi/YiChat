//
//  ProjectAlbumnModel.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/19.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ProjectAssetConfig.h"
NS_ASSUME_NONNULL_BEGIN


@interface ProjectAlbumnModel : NSObject

/// Sort photos ascending by modificationDate，Default is YES
/// 对照片排序，按修改时间升序，默认是YES。如果设置为NO,最新的照片会显示在最前面，内部的拍照按钮会排在第一个
@property (nonatomic, assign) BOOL sortAscendingByModificationDate;

@property (nonatomic, assign) BOOL allowPickingVideo;

@property (nonatomic, assign) BOOL allowPickingImage;

@property (nonatomic,strong)  ProjectAssetModelArr *modelArr;
//
//@property (nonatomic, assign) CGFloat sourceWidth;
//
//@property (nonatomic, strong) NSString *name;        ///< The album name
//@property (nonatomic, assign) NSInteger count;       ///< Count of photos the album contain
//@property (nonatomic, strong) PHFetchResult *result;
//
//@property (nonatomic, strong) NSArray *models;
//@property (nonatomic, strong) NSArray *selectedModels;
//@property (nonatomic, assign) NSUInteger selectedCount;

- (void)projectAlbumnModelGetAllAlumnSources:(void(^)(ProjectAssetModelArr *modelArr))alumnInfo;

- (void)projectAlbumnModelGetAllAlumnsHandle:(void(^)(ProjectAlbumnModelAlbumnsInfoList * albumnListInfo))alumnListInfoHandle;

@end

@interface ProjectAssetModel : NSObject

@property (nonatomic, strong) PHAsset *asset;
@property (nonatomic, assign) BOOL isSelected;      ///< The select status of a photo, default is No
@property (nonatomic, assign) ProjectAssetModelMediaType type;
@property (nonatomic, copy)   NSString *timeLength;

@property (nonatomic, assign) PHImageRequestID reuqestId;
@property (nonatomic,assign)  NSInteger identify;

@property (nonatomic,strong)  AVURLAsset *urlAsset;

@property (nonatomic,assign) BOOL isLoaded;

@property (nonatomic,strong) NSData *albumnPhotoData;

/// Init a photo dataModel With a PHAsset
/// 用一个PHAsset实例，初始化一个照片模型
+ (instancetype)modelWithAsset:(PHAsset *)asset type:(ProjectAssetModelMediaType)type;
+ (instancetype)modelWithAsset:(PHAsset *)asset type:(ProjectAssetModelMediaType)type timeLength:(NSString *)timeLength;

@end
NS_ASSUME_NONNULL_END
