//
//  ZFChatVoicePlayManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/15.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <AVFoundation/AVAudioPlayer.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^zfChatVoicePlayProgressInvocation) (CGFloat progress,CGFloat duration);
typedef void(^zfChatVoicePlayCompleteHandel)(NSString *url);
typedef void(^zfChatVoicePlayErrorHandel)(NSString *url,NSString *error);

typedef NS_ENUM(NSUInteger,ZFChatVoicePlayMode){
    ZFChatVoicePlayModeBack, //扬声器
    ZFChatVoicePlayModeNear //听筒
};

@interface ZFChatVoicePlayManager : NSObject
    
@property (nonatomic,assign) ZFChatVoicePlayMode playMode;

+ (id)sharedVoicePlayerManager;

- (void)playVoiceWithUrl:(NSString *)url progress:(zfChatVoicePlayProgressInvocation)progress completion:(zfChatVoicePlayCompleteHandel)success failure:(zfChatVoicePlayErrorHandel)failure;
    
- (BOOL)playVoicePlayingState;

- (void)stopPlay;
    
- (void)changePlayVoiceMode;

@end

NS_ASSUME_NONNULL_END
