//
//  YiChatSendDynamicToolBar.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSendDynamicToolBar.h"
#import "ServiceGlobalDef.h"
#import "YiChatSendDynamicToolBarCell.h"
#import "ProjectBrowseManager.h"
#import "ProjectAssetManager.h"
#import <Photos/Photos.h>
#import "ZFChatStorageHelper.h"
@interface YiChatSendDynamicToolBar ()

@property (nonatomic,strong) NSMutableArray *toolBarDataArr;

@end


static NSString *identifier = @"YiChatSendDynamicToolBarCell";

#define YiChatSendDynamicToolBar_MaxSelectePhotos 9
#define YiChatSendDynamicToolBar_MaxSelecteVideo 1

@implementation YiChatSendDynamicToolBar

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _toolBarDataArr = [NSMutableArray arrayWithCapacity:0];
        
        YiChatSendDynamicBarModel *cellModel = [[YiChatSendDynamicBarModel alloc] init];
        
        cellModel.icon = [UIImage imageNamed:@"copy2@3x.png"];
        
        cellModel.type = YiChatSendDynamicBarModelTypeAdd;
        
        if(cellModel){
            [_toolBarDataArr addObject:cellModel];
        }
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
  
    WS(weakSelf);
    
    CGFloat w = (self.frame.size.width - 10.0 * 5) / 4;
    CGFloat h = w;
    CGFloat magin = 10.0;
    //设置行与行之间的间距最小距离
    CGFloat lineSpace = 10.0;
    CGFloat interalSpace = 10.0;
    
    [self setProjectCollectionViewNumItem:^NSUInteger(UICollectionView * _Nonnull collection, NSUInteger section) {
        return weakSelf.toolBarDataArr.count;
    }];
    
    [self setProjectCollectionViewNumSection:^NSUInteger(UICollectionView * _Nonnull collection) {
        return 1;
    }];
    
    [self setProjectCollectionViewItemSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSIndexPath * _Nonnull index) {
        return CGSizeMake(w, h);
    }];
    
    [self setProjectCollectionViewItemInset:^UIEdgeInsets(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return UIEdgeInsetsMake(magin, magin, magin, magin);
    }];
    
    [self setProjectCollectionViewItem:^UICollectionViewCell * _Nonnull(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        YiChatSendDynamicToolBarCell *cell = [collection dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:index];
        
        cell.size = w;
        
        if((weakSelf.toolBarDataArr.count - 1) >= index.row){
            cell.model = weakSelf.toolBarDataArr[index.row];
        }
        cell.YiChatSendDynamicBarDidClickCancel = ^(id  _Nonnull model, NSIndexPath * _Nonnull index) {
            if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
    
                YiChatSendDynamicBarModel *barModel = model;
                if(barModel.type != YiChatSendDynamicBarModelTypeAdd){
                    
                    [weakSelf.toolBarDataArr removeObject:model];
                    [weakSelf.collectionView reloadData];
                    
                    weakSelf.yiChatSendDynamicToolBarDidDeleteResource(@[model]);
                }
            }
        };
        return cell;
    }];
    
    [self setProjectCollectionViewSelecteItem:^(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        
        YiChatSendDynamicBarModel *cellModel = [weakSelf getDynamicSendModel:index.row];
        
        if(cellModel && [cellModel isKindOfClass:[YiChatSendDynamicBarModel class]]){
            
            if(cellModel.type == YiChatSendDynamicBarModelTypeAdd){
                
                YiChatSendDynamicBarModel *tmpModel = [weakSelf getDynamicSendModel:0];
                
                if(tmpModel){
                    if(tmpModel.type == YiChatSendDynamicBarModelTypeImage){
                        
                        if(weakSelf.toolBarDataArr.count < (YiChatSendDynamicToolBar_MaxSelectePhotos + 1)){
                            //可以添加照片
                            [weakSelf selecteResourceIndex:index pickNum: YiChatSendDynamicToolBar_MaxSelectePhotos + 1 - weakSelf.toolBarDataArr.count];
                            
                        }
                        else{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"只能添加9张照片"];
                        }
                    }
                    
                    
                    else if(tmpModel.type == YiChatSendDynamicBarModelTypeVideo){
                        if(weakSelf.toolBarDataArr.count == 1 + YiChatSendDynamicToolBar_MaxSelecteVideo){
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"只能添加1个视频"];
                        }
                        else if(weakSelf.toolBarDataArr.count == 1){
                            //可以添加视频
                            [weakSelf selecteResourceIndex:index pickNum:1];
                        }
                    }
                    
                    else if(tmpModel.type == YiChatSendDynamicBarModelTypeAdd){
                        //视频照片都可以添加
                        [weakSelf selecteResourceIndex:index pickNum:YiChatSendDynamicToolBar_MaxSelectePhotos];
                    }
                }
                
            }
            else{
                if(weakSelf.toolBarDataArr.count > 1){
                    ProjectBrowseManager *manager =  [[ProjectBrowseManager alloc] init];
                    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                    NSMutableArray *bgArr = [NSMutableArray arrayWithCapacity:0];
                    if(!cellModel.isVideo){
                        for (int i = 0; i < weakSelf.toolBarDataArr.count - 1; i ++) {
                            YiChatSendDynamicBarModel *tmpModel = weakSelf.toolBarDataArr[i];
                            if(tmpModel && !(tmpModel.type == YiChatSendDynamicBarModelTypeAdd)){
                                if(tmpModel.icon){
                                    [tmp addObject:tmpModel.icon];
                                }
                                if(tmpModel.itemBgView){
                                    [bgArr addObject:tmpModel.itemBgView];
                                }
                               
                            }
                        }
                        if(cellModel.itemBgView){
                            [manager showImageBrowseWithIcons:tmp bgViews:bgArr currentIndex:index.row];
                        }
                    }
                    else{
                        YiChatSendDynamicBarModel *tmpModel = [weakSelf getDynamicSendModel:index.row];
                        
                        if(tmpModel.localUrl && [tmpModel.localUrl isKindOfClass:[NSString class]] && cellModel.itemBgView){
                            [manager showVideoBrowseWithDataSouce:@[tmpModel.localUrl] withSourceObjs:@[cellModel.itemBgView] currentIndex:index.row];
                        }
                    }
                }
            }
        }
        
    }];
    
    [self setProjectCollectionViewLineSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return lineSpace;
    }];
    
    [self setProjectCollectionViewInteritemSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return interalSpace;
    }];
    
    [self setProjectCollectionViewHeaderSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self setProjectCollectionViewFooterSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self makeCollectionView];
    
    self.collectionView.backgroundColor = [UIColor whiteColor];
    
    [self.collectionView registerClass:[YiChatSendDynamicToolBarCell class] forCellWithReuseIdentifier:identifier];

    
    if (@available(iOS 10.0,*)) {
        self.collectionView.prefetchingEnabled = NO;
    }
    
    self.collectionView.pagingEnabled = YES;
    
    [self layoutSubview];
    
    
}


- (YiChatSendDynamicBarModel *)getDynamicSendModel:(NSInteger)index{
    if((_toolBarDataArr.count - 1) >= index){
        YiChatSendDynamicBarModel *model = _toolBarDataArr[index];
        if([model isKindOfClass:[YiChatSendDynamicBarModel class]] && model){
            return model;
        }
    }
    return nil;
}

/**
 * type = 0 视频，图片
 * type = 1 视频
 * type = 2 图片
 * type = - 1 出错不可发送
 */
- (NSInteger)getCanSendSourceType{
    if(_toolBarDataArr.count > 0){
        YiChatSendDynamicBarModel *model = _toolBarDataArr.firstObject;
        if(model.type == YiChatSendDynamicBarModelTypeAdd){
            return 0;
        }
        else if(model.type == YiChatSendDynamicBarModelTypeVideo){
            return 1;
        }
        else if(model.type == YiChatSendDynamicBarModelTypeImage){
            return 2;
        }
    }
    return -1;
}

- (void)selecteResourceIndex:(NSIndexPath *)index pickNum:(NSInteger)pickNum{
    WS(weakSelf);
    [ProjectUIHelper projectActionSheetWithListArr:@[@"相机",@"相册"] click:^(NSInteger row) {
        if(row == 0){
            [weakSelf selecteResourceWithType:4 pickNum:1 index:index];
        }
        else if(row == 1){
            if([weakSelf getCanSendSourceType] == 0){
                [weakSelf selecteResourceWithType:3 pickNum:1 index:index];
            }
            else if([weakSelf getCanSendSourceType] == 2){
                [weakSelf selecteResourceWithType:2 pickNum:pickNum index:index];
            }
        }
    }];
}

// 4 相机
// 2 相册选照片
// 3 相册选视频 照片
- (void)selecteResourceWithType:(NSInteger)type pickNum:(NSInteger)pickNum index:(NSIndexPath *)index{
     WS(weakSelf);
    
    NSInteger canSelecteType = [self getCanSendSourceType];
    
    [ProjectUIHelper projectPhotoVideoPickerWWithType:type pickNum:pickNum invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
        
        manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
        
            if(canSelecteType == 0 || canSelecteType == 2){
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < images.count; i ++) {
                    YiChatSendDynamicBarModel *model =  [weakSelf addItemWithImage:images[i] identify:[NSString stringWithFormat:@"%ld",index.row]];
                    if(model){
                        [arr addObject:model];
                    }
                }
                if(arr.count >0 && self.yiChatSendDynamicToolBarDidSelelcteResource){
                    self.yiChatSendDynamicToolBarDidSelelcteResource(arr);
                    return;
                }
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"当前状态只能选择视频"];
            }
        };
        manager.yrPickerManagerDidTakeVideos = ^(UIImage * _Nonnull coverImage, NSString * _Nonnull movFilePath) {
            
            if(canSelecteType == 1 || canSelecteType == 0){
                
                if(coverImage && [coverImage isKindOfClass:[UIImage class]] && movFilePath && [movFilePath isKindOfClass:[NSString class]]){
                    
                    
                    NSString *savePath = [ZFChatStorageHelper zfChatStorageHelper_getMP4FileFullPath];
                    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@"视频导出中..."];
                    
                    [ZFChatStorageHelper zfChatStorageHelper_convertMovToMP4WithMovPath:movFilePath savePath:savePath hanlde:^(BOOL success, NSString * _Nonnull path, NSString * _Nonnull errorStr) {
                        
                        [ProjectHelper helper_getMainThread:^{
                            [ProjectHelper helper_performInstanceSelectorWithTarget:progress initialMethod:@selector(hidden) flags:nil];
                        }];
                        
                        if(success && path && [path isKindOfClass:[NSString class]]){
                            
                            YiChatSendDynamicBarModel *model =  [weakSelf addVideoItemWithImage:coverImage asset:path identify:[NSString stringWithFormat:@"%ld",index.row]];
                            if(model && self.yiChatSendDynamicToolBarDidSelelcteResource){
                                self.yiChatSendDynamicToolBarDidSelelcteResource(@[model]);
                                return ;
                            }
                        }
                        else{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"视频导出失败"];
                        }
                    }];
                }
                
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"当前状态只能选择图片"];
            }
            
        };
        
        manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
            
            if(canSelecteType == 0 || canSelecteType == 2){
                YiChatSendDynamicBarModel *model =  [weakSelf addItemWithImage:editedIcon identify:[NSString stringWithFormat:@"%ld",index.row]];
                if(model && self.yiChatSendDynamicToolBarDidSelelcteResource){
                    self.yiChatSendDynamicToolBarDidSelelcteResource(@[model]);
                    return ;
                }
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"当前状态只能选择视频"];
            }
            
        };
        manager.yrPickerManagerDidPickerVideos = ^(UIImage * _Nonnull coverImage, PHAsset * _Nonnull asset) {
            WS(wsself);
            
            if(canSelecteType == 0 || canSelecteType == 1){
                [self dealSelcteVideo:asset coverImage:coverImage index:index];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"当前状态只能选择图片"];
            }
            
        };
        
        if([nav respondsToSelector:@selector(maxImagesCount)]){
            if(YiChatSendDynamicToolBar_MaxSelectePhotos >= (weakSelf.toolBarDataArr.count - 1)){
                [nav setValue:[NSNumber numberWithInteger:YiChatSendDynamicToolBar_MaxSelectePhotos - (weakSelf.toolBarDataArr.count - 1)] forKey:@"maxImagesCount"];
            }
        }
        
        [weakSelf.bgVC presentViewController:nav animated:YES completion:nil];
    }];
}

- (void)dealSelcteVideo:(PHAsset *)asset coverImage:(UIImage *)coverImage index:(NSIndexPath *)index{
    WS(weakSelf);
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@"视频导出中..."];
    
    PHVideoRequestOptions *option = [[PHVideoRequestOptions alloc] init];
    option.progressHandler = ^(double progress, NSError * _Nullable error, BOOL * _Nonnull stop, NSDictionary * _Nullable info) {
        
    };
    option.deliveryMode = PHVideoRequestOptionsDeliveryModeHighQualityFormat;
    
    [[PHImageManager defaultManager] requestAVAssetForVideo:asset options:option resultHandler:^(AVAsset * _Nullable assetAv, AVAudioMix * _Nullable audioMix, NSDictionary * _Nullable info) {
        
        if(assetAv){
            [[ProjectAssetManager assetManager] exportVideoWithVideoAsset:assetAv presetName:AVAssetExportPresetMediumQuality success:^(NSString * _Nonnull outputPath) {
                
                [ProjectHelper helper_getMainThread:^{
                    
                    [ProjectHelper helper_performInstanceSelectorWithTarget:progress initialMethod:@selector(hidden) flags:nil];
                    
                    if(outputPath){
                        
                        YiChatSendDynamicBarModel *model =  [weakSelf addVideoItemWithImage:coverImage asset:outputPath identify:[NSString stringWithFormat:@"%ld",index.row]];
                        if(model && self.yiChatSendDynamicToolBarDidSelelcteResource){
                            self.yiChatSendDynamicToolBarDidSelelcteResource(@[model]);
                        }
                    }
                    else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"视频添加失败"];
                        return ;
                    }
                }];
                
            } failure:^(NSString * _Nonnull errorMessage, NSError * _Nonnull error) {
                [ProjectHelper helper_getMainThread:^{
                    
                    [ProjectHelper helper_performInstanceSelectorWithTarget:progress initialMethod:@selector(hidden) flags:nil];
                }];
            }];
        }
        else{
            [ProjectHelper helper_getMainThread:^{
                [ProjectHelper helper_performInstanceSelectorWithTarget:progress initialMethod:@selector(hidden) flags:nil];
            }];
            
            if([info[@"PHImageResultIsInCloudKey"] integerValue]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"从icloud下载视频失败"];
                return;
            }
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"视频添加失败"];
        }
    }];
}

- (YiChatSendDynamicBarModel *)addItemWithImage:(UIImage *)image identify:(NSString *)identifyStr{
    if(image){
        YiChatSendDynamicBarModel *model = [[YiChatSendDynamicBarModel alloc] init];
        model.icon = image;
        model.type = YiChatSendDynamicBarModelTypeImage;
        model.identify = identifyStr;

        return model;
    }
    return nil;
}

- (YiChatSendDynamicBarModel *)addVideoItemWithImage:(UIImage *)image asset:(NSString *)videoUrl identify:(NSString *)identifyStr{
    if(image){
        YiChatSendDynamicBarModel *model = [[YiChatSendDynamicBarModel alloc] init];
        model.icon = image;
        model.type = YiChatSendDynamicBarModelTypeVideo;
        model.localUrl = videoUrl;
        model.identify = identifyStr;
  
        return model;
    }
    return nil;
}

- (void)addResourceForRefreshUI:(NSArray *)dataModelArr{
    if(dataModelArr && [dataModelArr isKindOfClass:[NSArray class]]){
        if(dataModelArr.count > 0){
            for (int i = 0; i < dataModelArr.count; i ++) {
                YiChatSendDynamicBarModel *model = dataModelArr[i];
                if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
                    if(self.toolBarDataArr.count < 1){
                        [self.toolBarDataArr addObject:model];
                    }
                    else{
                         [self.toolBarDataArr insertObject:model atIndex:0];
                    }
                }
            }
            [ProjectHelper helper_getMainThread:^{
                [self.collectionView reloadData];
            }];
            
        }
    }
}

- (void)layoutSubview{
    self.collectionView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
