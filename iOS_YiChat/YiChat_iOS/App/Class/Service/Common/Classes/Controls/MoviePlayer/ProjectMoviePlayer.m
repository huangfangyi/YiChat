//
//  ProjectMoviePlayer.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/27.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectMoviePlayer.h"
#import <AVFoundation/AVFoundation.h>


@interface ProjectMoviePlayer ()

@property (nonatomic,strong) AVPlayer *player;//播放器对象

@end

@implementation ProjectMoviePlayer

- (id)initWithFrame:(CGRect)frame showInView:(UIView *)bgView playerItem:(AVPlayerItem *)playerItem{
    self = [super initWithFrame:frame];
    if(self){
        //创建播放器层
        AVPlayerLayer *playerLayer = [AVPlayerLayer playerLayerWithPlayer:self.player];
        playerLayer.frame = self.bounds;
        
        [self.layer addSublayer:playerLayer];
        [bgView addSubview:self];
        
        if(playerItem){
            self.playerItem = playerItem;
        }
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame showInView:(UIView *)bgView url:(NSString *)url{
    self = [super initWithFrame:frame];
    if(self){
        
        //创建播放器层
        AVPlayerLayer *playerLayer = [AVPlayerLayer playerLayerWithPlayer:self.player];
        playerLayer.frame = self.bounds;
        
        [self.layer addSublayer:playerLayer];
        [bgView addSubview:self];
        
        if(url){
            self.videoUrl = [NSURL URLWithString:url];
        }
    }
    return self;
}

- (AVPlayer *)player {
    if (!_player) {
        _player = [AVPlayer playerWithPlayerItem:[self getAVPlayerItem]];
        [self addAVPlayerNtf:_player.currentItem];
    }
    
    return _player;
}

- (AVPlayerItem *)getAVPlayerItem {
    
    AVPlayerItem *playerItem= nil;
    if(self.videoUrl){
        playerItem = [AVPlayerItem playerItemWithURL:self.videoUrl];
    }
    else{
        playerItem = _playerItem;
    }
    return playerItem;
}

- (void)setPlayerItem:(AVPlayerItem *)playerItem{
    _playerItem = nil;
    _playerItem = playerItem;
    [self removeAvPlayerNtf];
    [self nextPlayer];
}

- (void)setVideoUrl:(NSURL *)videoUrl {
    _videoUrl = videoUrl;
    [self removeAvPlayerNtf];
    [self nextPlayer];
}

- (void)nextPlayer {
    [self.player seekToTime:CMTimeMakeWithSeconds(0, _player.currentItem.duration.timescale)];
    [self.player replaceCurrentItemWithPlayerItem:[self getAVPlayerItem]];
    [self addAVPlayerNtf:self.player.currentItem];
}

- (void)addAVPlayerNtf:(AVPlayerItem *)playerItem {
    //监控状态属性
    [playerItem addObserver:self forKeyPath:@"status" options:NSKeyValueObservingOptionNew context:nil];
    //监控网络加载情况属性
    [playerItem addObserver:self forKeyPath:@"loadedTimeRanges" options:NSKeyValueObservingOptionNew context:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playbackFinished:) name:AVPlayerItemDidPlayToEndTimeNotification object:self.player.currentItem];
}

- (void)removeAvPlayerNtf {
    AVPlayerItem *playerItem = self.player.currentItem;
    [playerItem removeObserver:self forKeyPath:@"status"];
    [playerItem removeObserver:self forKeyPath:@"loadedTimeRanges"];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)pausePlayer{
    if (self.player.rate == 1) {
        [self.player pause];//如果在播放状态就停止
        if(self.playerDidPlayPause){
            self.playerDidPlayPause();
        }
    }
}

- (void)remove{
    if (self.player.rate == 1) {
        [self.player pause];
    }
    _playerItem = nil;
    [self.player replaceCurrentItemWithPlayerItem:nil];
    [self removeAvPlayerNtf];
}

- (void)play{
    if (self.player.rate == 0) {
        [self.player play];
    }
}

/**
 *  通过KVO监控播放器状态
 *
 *  @param keyPath 监控属性
 *  @param object  监视器
 *  @param change  状态改变
 *  @param context 上下文
 */
-(void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context{
    AVPlayerItem *playerItem = object;
    if ([keyPath isEqualToString:@"status"]) {
        AVPlayerStatus status= [[change objectForKey:@"new"] intValue];
        if(status==AVPlayerStatusReadyToPlay){
            if(self.readyToPlay){
                self.readyToPlay();
            }
            NSLog(@"正在播放...，视频总长度:%.2f",CMTimeGetSeconds(playerItem.duration));
        }
        if(status == AVPlayerStatusUnknown){
            if(self.playerStateUnknown){
                self.playerStateUnknown();
            }
        }
        if(status == AVPlayerStatusFailed){
            if(self.playerStateFailed){
                self.playerStateFailed();
            }
        }
    }else if([keyPath isEqualToString:@"loadedTimeRanges"]){
        NSArray *array=playerItem.loadedTimeRanges;
        CMTimeRange timeRange = [array.firstObject CMTimeRangeValue];//本次缓冲时间范围
        float startSeconds = CMTimeGetSeconds(timeRange.start);
        float durationSeconds = CMTimeGetSeconds(timeRange.duration);
        NSTimeInterval totalBuffer = startSeconds + durationSeconds;//缓冲总长度
        NSLog(@"共缓冲：%.2f",totalBuffer);
    }
}

- (void)playbackFinished:(NSNotification *)ntf {
    NSLog(@"视频播放完成");
    if(self.playerDidPlayFinish){
        self.playerDidPlayFinish();
    }

}

- (void)replay{
    [self.player seekToTime:CMTimeMake(0, 1)];
    [self.player play];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
