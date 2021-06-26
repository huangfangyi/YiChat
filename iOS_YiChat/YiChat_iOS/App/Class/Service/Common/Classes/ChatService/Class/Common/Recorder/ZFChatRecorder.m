//
//  ZFChatRecorder.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatRecorder.h"
#import "ZFChatGlobal.h"

@interface ZFChatRecorder ()

@end

@implementation ZFChatRecorder

- (id)initWithFilePath:(NSString *)filePath{
    self = [super init];
    if(self){
        _filePath = filePath;
        
        [self getRecorderWithFilePath:filePath];
    }
    return self;
}

+ (id)createChatRecorderWithFilePath:(NSString *)filePath{
    return [[self alloc] initWithFilePath:filePath];
}

/**
 * filePath 录音文件的存储路径 包括文件名
 */
- (void)makeRecorderWithFilePath:(NSString *)filePath{
    _recorder = [self getRecorderWithFilePath:filePath];
    
    _filePath = filePath;
}

- (void)changeRecorderPath:(NSString *)filePath{
    
    if([filePath isKindOfClass:[NSString class]]){
        
        [_recorder stop];
        _recorder.delegate = nil;
        _recorder = nil;
        
        _recorder = [self getRecorderWithFilePath:filePath];
        
        _filePath = filePath;
    }
}

- (AVAudioRecorder *)getRecorderWithFilePath:(NSString *)filePath{
    NSDictionary *setting = [self getRecorderSettings];
    
    NSError *error = nil;
    
    NSString *path = filePath;
    
    AVAudioRecorder *recorder = [[AVAudioRecorder alloc] initWithURL:[NSURL URLWithString:path] settings:setting error:&error];
    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryRecord error:nil];
    recorder.meteringEnabled = YES;
    
    return recorder;
}

+ (NSUInteger)getDurationWithVideo:(NSURL *)videoUrl{
    
    NSDictionary *opts = [NSDictionary dictionaryWithObject:@(NO) forKey:AVURLAssetPreferPreciseDurationAndTimingKey];
    AVURLAsset *urlAsset = [AVURLAsset URLAssetWithURL:videoUrl options:opts]; // 初始化视频媒体文件
    NSUInteger second = 0;
    second = urlAsset.duration.value / urlAsset.duration.timescale; // 获取视频总时长,单位秒
    
    return second;
}

- (NSDictionary *)getRecorderSettings{
    
    NSMutableDictionary * settings = @{}.mutableCopy;
    [settings setObject:[NSNumber numberWithFloat:8000.0] forKey:AVSampleRateKey];
    [settings setObject:[NSNumber numberWithInt: kAudioFormatLinearPCM] forKey:AVFormatIDKey];
    [settings setObject:@1 forKey:AVNumberOfChannelsKey];//设置成一个通道，iPnone只有一个麦克风，一个通道已经足够了
    [settings setObject:@16 forKey:AVLinearPCMBitDepthKey];//采样的位数
    
    return settings;
}
@end
