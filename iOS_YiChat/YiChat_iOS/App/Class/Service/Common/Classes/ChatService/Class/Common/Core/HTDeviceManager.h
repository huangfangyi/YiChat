//
//  HTDeviceManager.h
//  HTMessage
//
//  Created by ZhangFeng on 17/2/21.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h>

@interface HTDeviceManager : NSObject

+(HTDeviceManager *)sharedInstance;

// The system sound for a new message
- (SystemSoundID)playNewMessageSound;

- (void)playVibration;

@end
