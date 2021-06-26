//
//  ZFChatStorageVoiceManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/31.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatStorageVoiceManager : NSObject

@property (nonatomic,strong) NSString *recorderVoiceItemPath;

@property (nonatomic,strong) NSString *recorderTranslatedVoiceItemPath;

+ (id)sharedManager;

@end

NS_ASSUME_NONNULL_END
