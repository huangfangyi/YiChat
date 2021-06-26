//
//  ZFChatRecorder.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatRecorder : NSObject

@property (nonatomic,strong) NSString *filePath;

@property (nonatomic,strong) AVAudioRecorder *recorder;


/**
 * filePath 录音文件的存储路径 包括文件名
 */
+ (id)createChatRecorderWithFilePath:(NSString *)filePath;

- (void)changeRecorderPath:(NSString *)filePath;

@end

NS_ASSUME_NONNULL_END
