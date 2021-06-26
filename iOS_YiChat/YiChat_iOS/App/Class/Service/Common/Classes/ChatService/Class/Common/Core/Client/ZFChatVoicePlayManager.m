//
//  ZFChatVoicePlayManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/15.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatVoicePlayManager.h"
#import "ZFChatGlobal.h"
@interface ZFChatVoicePlayManager ()<AVAudioPlayerDelegate>
{
    AVAudioPlayer *_player;
    NSTimer *_timer;
    NSInteger _time;
    
}

@property (nonatomic,copy) zfChatVoicePlayProgressInvocation progress;
@property (nonatomic,copy) zfChatVoicePlayCompleteHandel complete;
@property (nonatomic,copy) zfChatVoicePlayErrorHandel failture;

@property (nonatomic,strong) NSString *url;

@end

static ZFChatVoicePlayManager *manager = nil;
@implementation ZFChatVoicePlayManager

- (void)dealloc{
    [self playClean];
    [self removeNotify];
}

+ (id)sharedVoicePlayerManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[ZFChatVoicePlayManager alloc] init];
        
        manager.playMode = ZFChatVoicePlayModeBack;
        
        [manager addNotify];
    });
    return manager;
}

- (void)addNotify{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(sensorStateChange:) name:@"UIDeviceProximityStateDidChangeNotification" object:nil];
}

- (void)removeNotify{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"UIDeviceProximityStateDidChangeNotification" object:nil];
}

-(void)sensorStateChange:(NSNotificationCenter *)notification{
    if(_player){
        if(_player.isPlaying){
            if ([[UIDevice currentDevice] proximityState] == YES)
                
            {
                
                NSLog(@"Device is close to user");
                
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
                
                
                
            }
            
            else
                
            {
                
                NSLog(@"Device is not close to user");
                
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
                
            }
            
        }
        
    }
}

- (void)playVoiceWithUrl:(NSString *)url progress:(zfChatVoicePlayProgressInvocation)progress completion:(zfChatVoicePlayCompleteHandel)success failure:(zfChatVoicePlayErrorHandel)failure{
    [ProjectHelper helper_getMainThread:^{
        
        if(url && [url isKindOfClass:[NSString class]]){
            [self playClean];
            
            _progress = progress;
            _complete = success;
            _failture = failure;
            
            NSString *playUrl = url;
            _url = url;
            
            _player = [self getAudioPlayerWithURL:playUrl];
            _player.delegate = self;
            _player.numberOfLoops=0;
            [_player prepareToPlay];
            [_player play];
            
            _playMode = ZFChatVoicePlayModeBack;
            [[UIDevice currentDevice] setProximityMonitoringEnabled:YES];
            _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerMethod:) userInfo:nil repeats:YES];
        }
    }];
}
    
- (BOOL)playVoicePlayingState{
    if(_player && [_player isKindOfClass:[AVAudioPlayer class]]){
        return _player.isPlaying;
    }
    return NO;
}
    
- (void)changePlayVoiceMode{
    
    if(_player && [_player isKindOfClass:[AVAudioPlayer class]]){
        if(_player.isPlaying){
            if ([[[AVAudioSession sharedInstance] category] isEqualToString:AVAudioSessionCategoryPlayback]){
                //切换为听筒播放
                _playMode = ZFChatVoicePlayModeNear;
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
            }
            else{
                //切换为扬声器播放
                _playMode = ZFChatVoicePlayModeBack;
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
            }
        }
    }
}

- (void)timerMethod:(NSTimer *)timer{
    _time ++;
    if(_time > _player.duration * 2){
        [self playErrorWithErrorDes:@"播放出错"];
        return;
    }
    if(_progress){
        _progress(_player.currentTime / _player.duration,_player.duration);
    }
    NSLog(@"%@%f",@"当前音频播放进度", _player.currentTime / _player.duration);
}

- (NSInteger)getVoiceResourceDurationWithUrl:(NSString *)url{
    if(url != nil){
        double duration =  [self getAudioPlayerWithURL:url].duration;
        NSInteger durationInt = [[NSString stringWithFormat:@"%f",duration] integerValue];
        if(duration > durationInt){
            return durationInt + 1;
        }
        else{
            return durationInt;
        }
    }
    else{
        return 0;
    }
}

/**
 *  AVAudioPlayer需要用下载下来的音频文件初始化
 */
- (AVAudioPlayer *)getAudioPlayerWithURL:(NSString *)fileUrl{
    NSURL *pathUrl=[NSURL fileURLWithPath:fileUrl];
    
    
    AVAudioPlayer *player=[[AVAudioPlayer alloc] initWithContentsOfURL:pathUrl error:nil];
    
    AVAudioSession *audioSession=[AVAudioSession sharedInstance];
    [audioSession setCategory:AVAudioSessionCategoryPlayback error:nil];
    //        [audioSession setCategory:AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionAllowBluetooth error:nil];
    [audioSession setActive:YES error:nil];
    return player;
}

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag{
    
    [self playComplete];
}


- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error{
    
    [self playErrorWithErrorDes:error.localizedDescription];
}


- (void)playClean{
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIDevice currentDevice] setProximityMonitoringEnabled:NO];
        
    });
    [_player stop];
    [_timer invalidate];
    _playMode = ZFChatVoicePlayModeBack;
    _timer = nil;
    _progress = nil;
    _complete = nil;
    _failture = nil;
    _time = 0;
    _player = nil;
    _url = nil;
    
}

- (void)playComplete{
    if(_player){
        [_player stop];
    }
    if(_complete){
        _complete(_url);
    }
    [self playClean];
    
}

- (void)playErrorWithErrorDes:(NSString *)errorDes{
    if(_player){
        [_player stop];
    }
    if(_failture){
        _failture(_url,errorDes);
    }
    [self playClean];
}

- (void)stopPlay{
    [self playClean];
}
@end
