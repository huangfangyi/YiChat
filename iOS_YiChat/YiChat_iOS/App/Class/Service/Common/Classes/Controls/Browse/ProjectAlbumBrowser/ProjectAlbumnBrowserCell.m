//
//  ProjectAlbumnBrowserCell.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectAlbumnBrowserCell.h"
#import "ProjectDef.h"
#import "ProjectAssetManager.h"
#import "ProjectAlbumBrowserModel.h"
#import "ProjectMoviePlayer.h"

@interface ProjectAlbumnBrowserCell ()

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UIView *progress;

@property (nonatomic,strong) UIButton *playBtn;

@property (nonatomic,assign) CGSize size;

@property (nonatomic,assign) BOOL isPlay;

@property (nonatomic,strong) ProjectMoviePlayer *player;

@end

@implementation ProjectAlbumnBrowserCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(photoPreviewCollectionViewDidScroll) name:@"photoPreviewCollectionViewDidScroll" object:nil];
        
        [self makeUI];
    }
    return self;
}


- (void)makeUI{
    _icon = [UIImageView new];
    _icon.alpha = 1;
    _icon.layer.cornerRadius = 5.0;
    
    [self.contentView addSubview:_icon];
    
    self.contentView.layer.cornerRadius = 10.0;
    
    self.contentView.backgroundColor = [UIColor clearColor];
    
    CGFloat w = 50.0;
    CGFloat h = 50.0;
    
    UIImage *icon = [UIImage imageNamed:@"alivc_shortVideo_play@3x.png"];
    if(icon != nil){
        h = [ProjectHelper helper_GetWidthOrHeightIntoScale:icon.size.width / icon.size.height width:w height:0];
    }
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(0, 0, w, h);
    [self.contentView addSubview:btn];
    [btn addTarget:self action:@selector(btnMethod:) forControlEvents:UIControlEventTouchUpInside];
    _playBtn  = btn;
    [btn setImage:icon forState:UIControlStateNormal];
    _playBtn.hidden = YES;
    
}

- (void)dealloc{
    [self playerClean];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)photoPreviewCollectionViewDidScroll{
    [self playerClean];
    
}

- (void)playerClean{
    [_player remove];
    [_player removeFromSuperview];
    _player = nil;
}

- (ProjectMoviePlayer *)player{
      WS(weakSelf);
    
    if(!_player){
        
        ProjectMoviePlayer *player = [[ProjectMoviePlayer alloc] initWithFrame:CGRectMake(0, 0,_size.width, _size.height) showInView:self.contentView playerItem:nil];
        
        player.playerDidPlayFinish = ^{
            weakSelf.playBtn.hidden = NO;
            
            [weakSelf.contentView bringSubviewToFront:weakSelf.playBtn];
        };
        player.playerDidPlayPause = ^{
        
            weakSelf.playBtn.hidden = NO;
            
            [weakSelf.contentView bringSubviewToFront:weakSelf.playBtn];
        };
        player.readyToPlay = ^{
            [weakSelf removeProgress];
        };
        player.playerStateFailed = ^{
            [weakSelf removeProgress];
        };
        
        _player = player;
    }
    return _player;
}


- (void)btnMethod:(UIButton *)btn{
    WS(weakSelf);
    
    self.playBtn.hidden = YES;
    
    if(!_model.playerItem){
        [self progress];
        [[ProjectAssetManager assetManager] getVideoWithAsset:_model.asset completion:^(AVPlayerItem * _Nonnull playItem, NSDictionary * _Nonnull dic) {
            [ProjectHelper helper_getMainThread:^{
                
                weakSelf.model.playerItem = playItem;
                weakSelf.player.playerItem = playItem;
                [weakSelf.player play];
            }];
            
        }];
    }
    else{
        weakSelf.player.playerItem = [_model.playerItem copy];
        [weakSelf.player play];
    }
    self.player.hidden = NO;
    [self.contentView bringSubviewToFront:self.player];
    
}

- (UIView *)progress{
    if(!_progress){
       // _progress = [GSYUIHelper GSYUIHelper_getProgressWithText:@""];
    }
    return _progress;
}

- (void)removeProgress{
    [ProjectHelper helper_getMainThread:^{
        if([_progress respondsToSelector:@selector(hidden)]){
            [_progress performSelector:@selector(hidden)];
            _progress = nil;
        }
    }];
}

- (void)progressValue:(CGFloat)progress{
    [ProjectHelper helper_getMainThread:^{
        if([self.progress respondsToSelector:@selector(setProgressvalueWithValue:)]){
            [ProjectHelper helper_performInstanceSelectorWithTarget:self.progress initialMethod:@selector(setProgressvalueWithValue:) flags:@[[NSString stringWithFormat:@"%.1f%",progress]]];
        }
    }];
}

- (void)setModel:(ProjectAlbumBrowserModel * _Nonnull)model size:(CGSize)size{
    _model = model;
    
    WS(weakSelf);
    _size = size;
    
    [self playerClean];
    
    if(model.asset.mediaType == PHAssetMediaTypeVideo){
        
        _playBtn.frame = CGRectMake(size.width / 2 - _playBtn.frame.size.width / 2, size.height / 2 - _playBtn.frame.size.height / 2, _playBtn.frame.size.width, _playBtn.frame.size.height);
        
        _playBtn.hidden = NO;
        
        [self progress];

        [[ProjectAssetManager assetManager] getPhotoWithAsset:model.asset photoWidth:size.width completion:^(UIImage * _Nonnull photo, NSDictionary * _Nonnull info, BOOL isDegraded) {
            [weakSelf removeProgress];
            [ProjectHelper helper_getMainThread:^{
                CGFloat w = size.width;
                CGFloat h = [ProjectHelper helper_GetWidthOrHeightIntoScale:photo.size.width / photo.size.height width:w height:0];
                weakSelf.icon.frame = CGRectMake(size.width / 2 - w / 2, size.height / 2 - h / 2, w, h);
                weakSelf.icon.image = photo;
            }];

        } progressHandler:^(double progress, NSError * _Nonnull error, BOOL * _Nonnull stop, NSDictionary * _Nonnull info) {

        } networkAccessAllowed:YES];
        
        [self.contentView bringSubviewToFront:_playBtn];
    }
    else if(model.asset.mediaType == PHAssetMediaTypeImage){
        [[ProjectAssetManager assetManager] getPhotoWithAsset:model.asset photoWidth:size.width completion:^(UIImage * _Nonnull photo, NSDictionary * _Nonnull info, BOOL isDegraded) {
            [weakSelf removeProgress];
            [ProjectHelper helper_getMainThread:^{
                CGFloat w = size.width;
                CGFloat h = [ProjectHelper helper_GetWidthOrHeightIntoScale:photo.size.width / photo.size.height width:w height:0];
                weakSelf.icon.frame = CGRectMake(size.width / 2 - w / 2, size.height / 2 - h / 2, w, h);
                weakSelf.icon.image = photo;
            }];
            
        } progressHandler:^(double progress, NSError * _Nonnull error, BOOL * _Nonnull stop, NSDictionary * _Nonnull info) {
            
            [weakSelf progressValue:progress];
            
        } networkAccessAllowed:YES];
    }
}

- (UIImage *)getVideoThumbWithAsset:(AVAsset *)asset size:(CGSize)size{
    AVAssetImageGenerator *_generator =
    [[AVAssetImageGenerator alloc] initWithAsset:asset];
    _generator.maximumSize = size;
    _generator.appliesPreferredTrackTransform = YES;
    _generator.requestedTimeToleranceAfter = kCMTimeZero;
    _generator.requestedTimeToleranceBefore = kCMTimeZero;
    CMTime time = CMTimeMake(0 * 1000, 1000);
    CGImageRef image = [_generator copyCGImageAtTime:time
                                          actualTime:NULL
                                               error:nil];
    UIImage *picture = [UIImage imageWithCGImage:image];
    CGImageRelease(image);
    return picture;
}


@end
