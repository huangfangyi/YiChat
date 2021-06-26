//
//  ProjectMoviePlayer.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/27.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class AVPlayerItem;
@interface ProjectMoviePlayer : UIView

@property (copy, nonatomic) NSURL *videoUrl;

@property (copy,nonatomic) AVPlayerItem *playerItem;

@property (nonatomic,copy) void(^playerDidPlayFinish)();

@property (nonatomic,copy) void(^playerDidPlayPause)();

@property (nonatomic,copy) void(^readyToPlay)();

@property (nonatomic,copy) void(^playerStateUnknown)();

@property (nonatomic,copy) void(^playerStateFailed)();

- (id)initWithFrame:(CGRect)frame showInView:(UIView *)bgView url:(NSString *)url;

- (id)initWithFrame:(CGRect)frame showInView:(UIView *)bgView playerItem:(AVPlayerItem *)playerItem;

- (void)pausePlayer;

- (void)replay;

- (void)play;

- (void)remove;

@end

NS_ASSUME_NONNULL_END
