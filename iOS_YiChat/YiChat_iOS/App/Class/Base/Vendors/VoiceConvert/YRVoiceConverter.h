//
//  VoiceConverter.h
//  Jeans
//
//  Created by Jeans Huang on 12-7-22.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

@interface YRVoiceConverter : NSObject

/**
 *  转换wav到amr
 *
 *  @param aWavPath  wav文件路径
 *  @param aSavePath amr保存路径
 *
 *  @return 0失败 1成功
 */
+ (int)ConvertWavToAmr:(NSString *)aWavPath amrSavePath:(NSString *)aSavePath;

/**
 *  转换amr到wav
 *
 *  @param aAmrPath  amr文件路径
 *  @param aSavePath wav保存路径
 *
 *  @return 0失败 1成功
 */
+ (int)ConvertAmrToWav:(NSString *)aAmrPath wavSavePath:(NSString *)aSavePath;

/**
	获取录音设置.
    建议使用此设置，如有修改，则转换amr时也要对应修改参数，比较麻烦
	@returns 录音设置
 */
+ (NSDictionary*)GetAudioRecorderSettingDict;

@end
