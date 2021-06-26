//
//  ProjectAlbumnModel.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/19.
//  Copyright © 2019年 GSY. All rights reserved.
//
#import "ProjectAlbumnModel.h"
#import "ProjectAssetApis.h"

@interface ProjectAssetModel ()

@end

@implementation ProjectAssetModel

+ (instancetype)modelWithAsset:(PHAsset *)asset type:(ProjectAssetModelMediaType)type{
    ProjectAssetModel *model = [[ProjectAssetModel alloc] init];
    model.asset = asset;
    model.isSelected = NO;
    model.type = type;
    return model;
}

+ (instancetype)modelWithAsset:(PHAsset *)asset type:(ProjectAssetModelMediaType)type timeLength:(NSString *)timeLength {
    ProjectAssetModel *model = [self modelWithAsset:asset type:type];
    model.timeLength = timeLength;
    model.isSelected = NO;
    return model;
}



@end

@interface ProjectAlbumnModel ()

@end

static NSString *connect_AlbumnInfo_Key = @"connect";
static NSString *fetchResult_AlbumnInfo_Key = @"fetchResult";

@implementation ProjectAlbumnModel

- (void)projectAlbumnModelGetAllAlumnSources:(void(^)(ProjectAssetModelArr *modelArr))alumnInfo{
    [self getAllAlbumsAllowPickingVideo:_allowPickingVideo allowPickingImage:_allowPickingImage needFetchAssets:YES handle:^(ProjectAlbumnModelAlbumnsInfoList *albumnListInfo) {
        
        ProjectAlbumnModelAlbumnsInfo *info = albumnListInfo.firstObject;
        
        PHFetchResult *result = [self getProjectAlbumnInfoFetchRequest:info];
        
        NSMutableArray *assetArr = [NSMutableArray arrayWithCapacity:0];
        
        [result enumerateObjectsUsingBlock:^(PHAsset *asset, NSUInteger idx, BOOL * _Nonnull stop) {
            ProjectAssetModel *assetModel = [ProjectAssetModel modelWithAsset:asset type:[ProjectAssetApis getAssetType:asset]];
            
            if (assetModel) {
                [assetArr addObject:assetModel];
            }
        }];
        
        if(alumnInfo){
            alumnInfo(assetArr);
        }
    }];
}

- (void)projectAlbumnModelGetAllAlumnsHandle:(void(^)(ProjectAlbumnModelAlbumnsInfoList * albumnListInfo))alumnListInfoHandle{
    [self getAllAlbumsAllowPickingVideo:_allowPickingVideo allowPickingImage:_allowPickingImage needFetchAssets:YES handle:^(ProjectAlbumnModelAlbumnsInfoList *albumnListInfo) {
        
        alumnListInfoHandle(albumnListInfo);
        
        for (ProjectAlbumnModelAlbumnsInfo * info in albumnListInfo) {
            NSLog(@"----->%@",info);
        }
    }];
}

- (void)getAllAlbumsAllowPickingVideo:(BOOL)allowPickingVideo allowPickingImage:(BOOL)allowPickingImage needFetchAssets:(BOOL)needFetchAssets handle:(void(^)(ProjectAlbumnModelAlbumnsInfoList * albumnListInfo))alumnListInfo{
    NSMutableArray *albumArr = [NSMutableArray array];
    PHFetchOptions *option = [[PHFetchOptions alloc] init];
    if (!allowPickingVideo) option.predicate = [NSPredicate predicateWithFormat:@"mediaType == %ld", PHAssetMediaTypeImage];
    if (!allowPickingImage) option.predicate = [NSPredicate predicateWithFormat:@"mediaType == %ld",
                                                PHAssetMediaTypeVideo];
    // option.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"modificationDate" ascending:self.sortAscendingByModificationDate]];
    if (!self.sortAscendingByModificationDate) {
        option.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"creationDate" ascending:self.sortAscendingByModificationDate]];
    }
    // 我的照片流 1.6.10重新加入..
    PHFetchResult *myPhotoStreamAlbum = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeAlbum subtype:PHAssetCollectionSubtypeAlbumMyPhotoStream options:nil];
    PHFetchResult *smartAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeSmartAlbum subtype:PHAssetCollectionSubtypeAlbumRegular options:nil];
    PHFetchResult *topLevelUserCollections = [PHCollectionList fetchTopLevelUserCollectionsWithOptions:nil];
    PHFetchResult *syncedAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeAlbum subtype:PHAssetCollectionSubtypeAlbumSyncedAlbum options:nil];
    PHFetchResult *sharedAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeAlbum subtype:PHAssetCollectionSubtypeAlbumCloudShared options:nil];
    NSArray *allAlbums = @[myPhotoStreamAlbum,smartAlbums,topLevelUserCollections,syncedAlbums,sharedAlbums];
    
    for (PHFetchResult *fetchResult in allAlbums) {
        //PHAssetCollection：PHCollection的子类，单个资源的集合，如相册、时刻等
        ProjectAlbumnModelAlbumnsInfo *tmp = nil;
        for (PHAssetCollection *collection in fetchResult) {
            // 有可能是PHCollectionList类的的对象，过滤掉
            if (![collection isKindOfClass:[PHAssetCollection class]]) continue;
            // 过滤空相册
            if (collection.estimatedAssetCount <= 0 && ![self isCameraRollAlbum:collection]) continue;
            PHFetchResult *fetchResult = [PHAsset fetchAssetsInAssetCollection:collection options:option];
            if (fetchResult.count < 1 && ![self isCameraRollAlbum:collection]) continue;
           
            
            if (collection.assetCollectionSubtype == PHAssetCollectionSubtypeSmartAlbumAllHidden) continue;
            if (collection.assetCollectionSubtype == 1000000201) continue; //『最近删除』相册
            
            
            if ([self isCameraRollAlbum:collection]) {
                
                tmp = [self getProjectAlumnInfoWithCollection:collection fetchResult:fetchResult];
                
//                [albumArr insertObject:[self modelWithResult:fetchResult name:collection.localizedTitle isCameraRoll:YES needFetchAssets:needFetchAssets] atIndex:0];
            } else {
//                [albumArr addObject:[self modelWithResult:fetchResult name:collection.localizedTitle isCameraRoll:NO needFetchAssets:needFetchAssets]];
//                NSLog(@"%@",collection.localizedTitle);
                [albumArr addObject:[self getProjectAlumnInfoWithCollection:collection fetchResult:fetchResult]];
            }
        }
        
        if(tmp != nil){
            [albumArr insertObject:tmp atIndex:0];
        }
    }
//    if (completion) {
//        completion(albumArr);
//    }
    if(alumnListInfo){
        alumnListInfo(albumArr);
    }
}

- (ProjectAlbumnModelAlbumnsInfo *)getProjectAlumnInfoWithCollection:(id)connection fetchResult:(id)fetchResult{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    if(connection != nil){
        [dic setObject:connection forKey:connect_AlbumnInfo_Key];
    }
    if(fetchResult != nil){
        [dic setObject:fetchResult forKey:fetchResult_AlbumnInfo_Key];
    }
    ProjectAlbumnModelAlbumnsInfo *info = dic;
    return info;
}

- (PHFetchResult *)getProjectAlbumnInfoFetchRequest:(ProjectAlbumnModelAlbumnsInfo *)abumnInfo{
    return abumnInfo[fetchResult_AlbumnInfo_Key];
}




- (BOOL)isCameraRollAlbum:(PHAssetCollection *)metadata {
    NSString *versionStr = [[UIDevice currentDevice].systemVersion stringByReplacingOccurrencesOfString:@"." withString:@""];
    if (versionStr.length <= 1) {
        versionStr = [versionStr stringByAppendingString:@"00"];
    } else if (versionStr.length <= 2) {
        versionStr = [versionStr stringByAppendingString:@"0"];
    }
    CGFloat version = versionStr.floatValue;
    // 目前已知8.0.0 ~ 8.0.2系统，拍照后的图片会保存在最近添加中
    if (version >= 800 && version <= 802) {
        return ((PHAssetCollection *)metadata).assetCollectionSubtype == PHAssetCollectionSubtypeSmartAlbumRecentlyAdded;
    } else {
        return ((PHAssetCollection *)metadata).assetCollectionSubtype == PHAssetCollectionSubtypeSmartAlbumUserLibrary;
    }
}


@end
/*
 typedef NS_ENUM(NSInteger, PHAssetCollectionType) {
 PHAssetCollectionTypeAlbum      = 1,  相册，系统外的
 PHAssetCollectionTypeSmartAlbum = 2,  智能相册，系统自己分配和归纳的
 PHAssetCollectionTypeMoment     = 3,  时刻，系统自动通过时间和地点生成的分组
 } PHOTOS_ENUM_AVAILABLE_IOS_TVOS(8_0, 10_0);
 *
 * typedef NS_ENUM(NSInteger, PHAssetCollectionSubtype) {
 
 // PHAssetCollectionTypeAlbum regular subtypes
 PHAssetCollectionSubtypeAlbumRegular         = 2, // 在iPhone中自己创建的相册
 PHAssetCollectionSubtypeAlbumSyncedEvent     = 3, // 从iPhoto（就是现在的图片app）中导入图片到设备
 PHAssetCollectionSubtypeAlbumSyncedFaces     = 4, // 从图片app中导入的人物照片
 PHAssetCollectionSubtypeAlbumSyncedAlbum     = 5, // 从图片app导入的相册
 PHAssetCollectionSubtypeAlbumImported        = 6, // 从其他的相机或者存储设备导入的相册
 
 // PHAssetCollectionTypeAlbum shared subtypes
 PHAssetCollectionSubtypeAlbumMyPhotoStream   = 100,  // 照片流，照片流和iCloud有关，如果在设置里关闭了iCloud开关，就获取不到了
 PHAssetCollectionSubtypeAlbumCloudShared     = 101,  // iCloud的共享相册，点击照片上的共享tab创建后就能拿到了，但是前提是你要在设置中打开iCloud的共享开关（打开后才能看见共享tab）
 
 // PHAssetCollectionTypeSmartAlbum subtypes
 PHAssetCollectionSubtypeSmartAlbumGeneric    = 200,
 PHAssetCollectionSubtypeSmartAlbumPanoramas  = 201,  // 全景图、全景照片
 PHAssetCollectionSubtypeSmartAlbumVideos     = 202,  // 视频
 PHAssetCollectionSubtypeSmartAlbumFavorites  = 203,  // 标记为喜欢、收藏
 PHAssetCollectionSubtypeSmartAlbumTimelapses = 204,  // 延时拍摄、定时拍摄
 PHAssetCollectionSubtypeSmartAlbumAllHidden  = 205,  // 隐藏的
 PHAssetCollectionSubtypeSmartAlbumRecentlyAdded = 206,  // 最近添加的、近期添加
 PHAssetCollectionSubtypeSmartAlbumBursts     = 207,  // 连拍
 PHAssetCollectionSubtypeSmartAlbumSlomoVideos = 208,  // Slow Motion,高速摄影慢动作（概念不懂）
 PHAssetCollectionSubtypeSmartAlbumUserLibrary = 209,  // 相机胶卷
 PHAssetCollectionSubtypeSmartAlbumSelfPortraits PHOTOS_AVAILABLE_IOS_TVOS(9_0, 10_0) = 210, // 使用前置摄像头拍摄的作品
 PHAssetCollectionSubtypeSmartAlbumScreenshots PHOTOS_AVAILABLE_IOS_TVOS(9_0, 10_0) = 211,  // 屏幕截图
 PHAssetCollectionSubtypeSmartAlbumDepthEffect PHOTOS_AVAILABLE_IOS_TVOS(10_2, 10_1) = 212,  // 在可兼容的设备上使用景深摄像模式拍的照片（概念不懂）
 PHAssetCollectionSubtypeSmartAlbumLivePhotos PHOTOS_AVAILABLE_IOS_TVOS(10_3, 10_2) = 213,  // Live Photo资源
 PHAssetCollectionSubtypeSmartAlbumAnimated PHOTOS_AVAILABLE_IOS_TVOS(11_0, 11_0) = 214,  // 没有解释
 PHAssetCollectionSubtypeSmartAlbumLongExposures PHOTOS_AVAILABLE_IOS_TVOS(11_0, 11_0) = 215,  // 没有解释
 // Used for fetching, if you don't care about the exact subtype
 PHAssetCollectionSubtypeAny = NSIntegerMax
 } PHOTOS_ENUM_AVAILABLE_IOS_TVOS(8_0, 10_0);
 
*/
