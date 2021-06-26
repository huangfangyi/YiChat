//
//  ZFChatStorageVideoManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatStorageVideoManager : NSObject

@property (nonatomic,strong) NSString *recorderVideoMOVItemPath;

@property (nonatomic,strong) NSString *recorderVideoExportTranslatedMP4ItemPath;

+ (id)sharedManager;

@end

NS_ASSUME_NONNULL_END
