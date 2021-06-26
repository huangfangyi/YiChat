//
//  ProjectPhotoCell.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectPhotoCell.h"
#import "ProjectDef.h"
#import "ProjectAssetManager.h"
#import <TZImagePickerController/TZImagePickerController.h>
#import <TZImagePickerController/TZProgressView.h>
#import <TZImagePickerController/UIView+Layout.h>
#import "ProjectClickView.h"
@interface ProjectPhotoCell ()


@property (nonatomic,strong) UIImageView *imageView;

@property (nonatomic, assign) int32_t bigImageRequestID;

@property (nonatomic,strong) TZProgressView *progressView;

@property (nonatomic,strong) ProjectClickView *selecteItem;

@property (nonatomic,strong) UILabel *selecteAppearLab;

@end

@implementation ProjectPhotoCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self makeUI];
        
        [self registeUnselecte];
    }
    return self;
}

- (void)registeUnselecte{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(unselecteItem:) name:@"GSYNotificationUnselectePhotoItem" object:nil];
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"GSYNotificationUnselectePhotoItem" object:nil];
}

- (void)unselecteItem:(NSNotification *)notify{
    id obj = [notify object];
    if([obj isKindOfClass:[NSArray class]]){
        [self updateSelecteIndexWithNumArr:obj];
    }
}

- (void)makeUI{
    _imageView = [UIImageView new];
    _imageView.backgroundColor = [UIColor whiteColor];
    _imageView.alpha = 1;
    
    [self.contentView addSubview:_imageView];
    
    WS(weakSelf);
    
//    ProjectClickView *click = [[ProjectClickView alloc] initWithFrame:CGRectZero bgView:self.contentView];
//    click.icon.image = [UIImage imageNamed:@"selecteAlbum@3x.png"];
//    click.clickInvocation = ^(NSString * _Nonnull identify) {
//        [weakSelf selecteBtnMethod:nil];
//    };
//    self.selecteItem = click;
//    [self.contentView addSubview:click];
//
//    _selecteAppearLab = [[UILabel alloc] init];
//    _selecteAppearLab.textAlignment = NSTextAlignmentCenter;
//    _selecteAppearLab.textColor = [UIColor redColor];
//    _selecteAppearLab.backgroundColor = [UIColor whiteColor];
//    _selecteAppearLab.font = [UIFont systemFontOfSize:12];
//    [self.contentView addSubview:_selecteAppearLab];
}

- (void)selecteBtnMethod:(UIButton *)btn{
    WS(weakSelf);
    
    if(_model.isSelected == NO){
        BOOL iscanSelecte = YES;
        if(self.ProjectIsCanSelecte){
            iscanSelecte = self.ProjectIsCanSelecte();
        }
        if(iscanSelecte == NO){
            return;
        }
        else{
            PHAsset *asset = _model.asset;
            
            [[ProjectAssetManager assetManager] requestImageDataForAsset:asset completion:^(NSData *imageData, NSString *dataUTI, UIImageOrientation orientation, NSDictionary *info) {
                
                if(imageData == nil){
                    if([info[PHImageResultIsInCloudKey] integerValue] == YES){
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(0, NO,@"从icloud同步图片失败");
                        }
                    }
                    else{
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(0, NO,@"图片获取失败");
                        }
                    }
                }
                else{
                    
                    UIImage *icon = [[UIImage alloc] initWithData:imageData];
                    UIImage *iconFix = [[ProjectAssetManager assetManager] fixOrientation:icon];
                    NSData *data = UIImagePNGRepresentation(iconFix);
                    if(data){
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(1, NO,@"");
                        }
                        
                        [ProjectHelper helper_getMainThread:^{
                            weakSelf.model.isSelected = !weakSelf.model.isSelected;
                            
                            if(weakSelf.projectPhotoCellSelecte){
                                id  obj =  weakSelf.projectPhotoCellSelecte(weakSelf. model.isSelected,weakSelf.representedAssetIdentifier);
                                if([obj isKindOfClass:[NSArray class]]){
                                    NSArray *arr = obj;
                                    if(arr.count != 0){
                                        [weakSelf updateSelecteIndexWithNumArr:arr];
                                    }
                                }
                                else{
                                    weakSelf.model.isSelected = !weakSelf.model.isSelected;
                                }
                            }
                            if (weakSelf.model.isSelected) {
                                [UIView showOscillatoryAnimationWithLayer:weakSelf.imageView.layer type:TZOscillatoryAnimationToBigger];
                                weakSelf.selecteAppearLab.hidden = NO;
                                
                            } else { // 取消选中，取消大图的获取
                                weakSelf.selecteAppearLab.hidden = YES;
                                [weakSelf cancelBigImageRequest];
                            }
                        }];
                        weakSelf.model.albumnPhotoData = data;
                    }
                    else{
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(0, NO,@"图片获取失败");
                        }
                    }
                }
                
            } progressHandler:^(double progress, NSError *error, BOOL *stop, NSDictionary *info) {
                if(weakSelf.ProjectVideoDownLoadProgress){
                    weakSelf.ProjectVideoDownLoadProgress(progress, YES,@"");
                }
            }];
        }
    }
    else{
        weakSelf.model.isSelected = !weakSelf.model.isSelected;
        
        if(weakSelf.projectPhotoCellSelecte){
            id obj =  weakSelf.projectPhotoCellSelecte(weakSelf.model.isSelected,weakSelf.representedAssetIdentifier);
            if([obj isKindOfClass:[NSArray class]]){
                NSArray *arr = obj;
                if(arr.count != 0){
                    [weakSelf updateSelecteIndexWithNumArr:arr];
                }
            }
            else{
                weakSelf.model.isSelected = !weakSelf.model.isSelected;
            }
        }
        
        if (weakSelf.model.isSelected) {
            [UIView showOscillatoryAnimationWithLayer:weakSelf.imageView.layer type:TZOscillatoryAnimationToBigger];
            // 用户选中了该图片，提前获取一下大图
            weakSelf.selecteAppearLab.hidden = NO;
            
        } else { // 取消选中，取消大图的获取
            weakSelf.selecteAppearLab.hidden = YES;
        }
    }
}

- (void)updateSelecteIndexWithNumArr:(NSArray *)numArr{
    WS(weakSelf);
    
    [numArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSString *num = obj;
        if([num isEqualToString:weakSelf.representedAssetIdentifier]){
            weakSelf.selecteAppearLab.hidden = NO;
            weakSelf.selecteAppearLab.text = [NSString stringWithFormat:@"%ld",idx + 1];
        }
    }];
}

- (void)setModel:(ProjectAssetModel *)model size:(CGFloat)width{
    _model = model;
    
    self.representedAssetIdentifier = model.asset.localIdentifier;
    
    WS(weakSelf);
    
    PHAsset *set  = (PHAsset *)model.asset;
    
    _imageView.frame = CGRectMake(0, 0, width, width);
    
    _selecteItem.frame = CGRectMake(width - 18.0 - 5.0,  5.0, 18.0, 18.0);
    
    _selecteItem.icon.frame = _selecteItem.bounds;
    
    _selecteAppearLab.frame = _selecteItem.frame;
    
    [self.contentView bringSubviewToFront:_selecteAppearLab];
    
    CGFloat progressW = width / 3;
    
    self.progressView.frame = CGRectMake(width / 2 - progressW / 2, width / 2 - progressW / 2, progressW, progressW);
    
    int32_t imageRequestID =  [[ProjectAssetManager assetManager] getPhotoWithAsset:set photoWidth:width completion:^(UIImage * _Nonnull photo, NSDictionary * _Nonnull info, BOOL isDegraded) {
        
        if ([self.representedAssetIdentifier isEqualToString:model.asset.localIdentifier]) {
            [ProjectHelper helper_getMainThread:^{
                weakSelf.imageView.image = [ProjectHelper helper_getSquareIconFromImage:photo];
            }];
        } else {
            // NSLog(@"this cell is showing other asset");
            [[PHImageManager defaultManager] cancelImageRequest:self.imageRequestID];
        }
        
    } progressHandler:^(double progress, NSError * _Nonnull error, BOOL * _Nonnull stop, NSDictionary * _Nonnull info) {
        
    } networkAccessAllowed:NO];
    
    if (imageRequestID && self.imageRequestID && imageRequestID != self.imageRequestID) {
        [[PHImageManager defaultManager] cancelImageRequest:self.imageRequestID];
        // NSLog(@"cancelImageRequest %d",self.imageRequestID);
    }
    self.imageRequestID = imageRequestID;
    
    // 如果用户选中了该图片，提前获取一下大图
    if (model.isSelected) {
        self.selecteAppearLab.hidden = NO;
        
        if(model.isLoaded == NO){
            [self requestBigImage];
        }
    } else {
        [self cancelBigImageRequest];
        
        self.selecteAppearLab.hidden = YES;;
    }
    
    [self setNeedsLayout];
}



- (TZProgressView *)progressView {
    if (_progressView == nil) {
        _progressView = [[TZProgressView alloc] init];
        _progressView.hidden = YES;
        [self.contentView addSubview:_progressView];
    }
    return _progressView;
}

- (void)hideProgressView {
    if (_progressView) {
        self.progressView.hidden = YES;
        self.imageView.alpha = 1.0;
    }
}

- (void)requestBigImage {
    if (_bigImageRequestID) {
        [[PHImageManager defaultManager] cancelImageRequest:_bigImageRequestID];
    }
    WS(weakSelf);

    
    _bigImageRequestID = [[ProjectAssetManager assetManager] requestImageDataForAsset:_model.asset completion:^(NSData *imageData, NSString *dataUTI, UIImageOrientation orientation, NSDictionary *info) {
        [self hideProgressView];
        weakSelf.model.isLoaded = YES;
        if ([weakSelf.representedAssetIdentifier isEqualToString:weakSelf.model.asset.localIdentifier]){
            weakSelf.model.albumnPhotoData = imageData;
        }
    } progressHandler:^(double progress, NSError *error, BOOL *stop, NSDictionary *info) {
        if (self.model.isSelected) {
            progress = progress > 0.02 ? progress : 0.02;;
            self.progressView.progress = progress;
            self.progressView.hidden = NO;
            self.imageView.alpha = 0.4;
            if (progress >= 1) {
                [self hideProgressView];
            }
        } else {
            // 快速连续点几次，会EXC_BAD_ACCESS...
            // *stop = YES;
            [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
            [self cancelBigImageRequest];
        }
    }];
}

- (void)cancelBigImageRequest {
    [self hideProgressView];
    
    if (_bigImageRequestID) {
        [[PHImageManager defaultManager] cancelImageRequest:_bigImageRequestID];
    }
}


@end
