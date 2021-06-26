//
//  ProjectVideoCell.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectVideoCell.h"
#import "ProjectDef.h"
#import "ProjectAssetApis.h"
#import "ProjectAssetManager.h"
#import "ProjectClickView.h"
#import <TZImagePickerController/TZImagePickerController.h>
#import <TZImagePickerController/TZProgressView.h>
#import <TZImagePickerController/UIView+Layout.h>

@interface ProjectVideoCell ()



@property (nonatomic,strong) ProjectClickView *selecteItem;

@property (nonatomic,strong) UIImageView *imageView;

@property (nonatomic,strong) UILabel *timeLab;

@property (nonatomic,strong) TZProgressView *progressView;

@property (nonatomic,strong) UILabel *selecteAppearLab;



@end

@implementation ProjectVideoCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self makeUI];
        
        [self registeUnselecte];
    }
    return self;
}

- (void)registeUnselecte{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(unselecteItem:) name:@"GSYNotificationUnselecteVideoItem" object:nil];
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"GSYNotificationUnselecteVideoItem" object:nil];
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
    
    _timeLab = [UILabel new];
    [self.contentView addSubview:_timeLab];
    _timeLab.backgroundColor = [UIColor blackColor];
    _timeLab.textColor = [UIColor whiteColor];
    _timeLab.font = [UIFont systemFontOfSize:12.0];
    _timeLab.textAlignment = NSTextAlignmentRight;
    
    WS(weakSelf);
    
    ProjectClickView *click = [[ProjectClickView alloc] initWithFrame:CGRectZero bgView:self.contentView];
    click.icon.image = [UIImage imageNamed:@"selecteAlbum@3x.png"];
    click.clickInvocation = ^(NSString * _Nonnull identify) {
        [weakSelf selecteBtnMethod:nil];
    };
    self.selecteItem = click;
    [self.contentView addSubview:click];
    
    _selecteAppearLab = [[UILabel alloc] init];
    _selecteAppearLab.textAlignment = NSTextAlignmentCenter;
    _selecteAppearLab.textColor = [UIColor redColor];
    _selecteAppearLab.backgroundColor = [UIColor whiteColor];
    _selecteAppearLab.font = [UIFont systemFontOfSize:12];
    [self.contentView addSubview:_selecteAppearLab];
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
            PHVideoRequestOptions* options = [[PHVideoRequestOptions alloc] init];
            options.version = PHVideoRequestOptionsVersionCurrent;
            options.deliveryMode = PHVideoRequestOptionsDeliveryModeHighQualityFormat;
            options.networkAccessAllowed = YES;
            
            options.progressHandler = ^(double progress, NSError * _Nullable error, BOOL * _Nonnull stop, NSDictionary * _Nullable info) {
                
                if(weakSelf.ProjectVideoDownLoadProgress){
                    weakSelf.ProjectVideoDownLoadProgress(progress, YES,@"");
                }
            };
            [[PHImageManager defaultManager] requestAVAssetForVideo:_model.asset options:options resultHandler:^(AVAsset* avasset, AVAudioMix* audioMix, NSDictionary* info){
                // NSLog(@"Info:\n%@",info);
                if(avasset == nil){
                    if([info[PHImageResultIsInCloudKey] integerValue] == YES){
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(0, NO,@"从icloud同步视频失败");
                        }
                    }
                    else{
                        if(weakSelf.ProjectVideoDownLoadProgress){
                            weakSelf.ProjectVideoDownLoadProgress(0, NO,@"视频获取失败");
                        }
                    }
                }
                else{
                    
                    if(weakSelf.ProjectVideoDownLoadProgress){
                        weakSelf.ProjectVideoDownLoadProgress(1, NO,@"");
                    }
                   
                    AVURLAsset *videoAsset = (AVURLAsset*)avasset;
                    
                    weakSelf.model.urlAsset = videoAsset;
                    
                    [ProjectHelper helper_getMainThread:^{
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
                    }];
                }
                
                //videoAsset.URL.path
                // NSLog(@"AVAsset URL: %@",myAsset.URL);
                //PHImageResultIsInCloudKey == yes
                
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
    
//   __block  ProjectAssetVideoProgressHandler videoHandle = ^(double progress, NSError *__nullable error, BOOL *stop, NSDictionary *__nullable info){
//
//    };
    WS(weakSelf);
    
    _imageView.frame = CGRectMake(0, 0, width, width);
    
    _timeLab.frame = CGRectMake(0, width - 20.0, width, 20.0);
    
    _selecteItem.frame = CGRectMake(width - 18.0 - 5.0,  5.0, 18.0, 18.0);
    
    _selecteItem.icon.frame = _selecteItem.bounds;
    
    _selecteAppearLab.frame = _selecteItem.frame;
    
    [self.contentView bringSubviewToFront:_selecteAppearLab];
    
    PHAsset *set  = (PHAsset *)model.asset;
    
    _timeLab.text = [[ProjectAssetManager assetManager] getNewTimeFromDurationSecond:set.duration];
    
     int32_t imageRequestID =   [[ProjectAssetManager assetManager] getPhotoWithAsset:set photoWidth:width completion:^(UIImage * _Nonnull photo, NSDictionary * _Nonnull info, BOOL isDegraded) {
         
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
    
    if (_model.isSelected) {
        [UIView showOscillatoryAnimationWithLayer:_imageView.layer type:TZOscillatoryAnimationToBigger];
        _selecteAppearLab.hidden = NO;
        // 用户选中了该图片，提前获取一下大图
    } else { // 取消选中，取消大图的获取
        _selecteAppearLab.hidden = YES;
    }
}


@end
