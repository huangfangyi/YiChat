//
//  YiChatSendDynamicBarModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/11.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"
#import <UIKit/UIKit.h>
#import <Photos/Photos.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger,YiChatSendDynamicBarModelType){
    YiChatSendDynamicBarModelTypeImage,YiChatSendDynamicBarModelTypeVideo,YiChatSendDynamicBarModelTypeAdd
};

@interface YiChatSendDynamicBarModel : ProjectBaseModel

@property (nonatomic,assign) BOOL isVideo;

@property (nonatomic,strong) UIImage *icon;

@property (nonatomic,strong) PHAsset *asset;

@property (nonatomic,strong) NSString *identify;

@property (nonatomic,assign) YiChatSendDynamicBarModelType type;

@property (nonatomic,strong) UIView *itemBgView;

@property (nonatomic,strong) NSString *remoteUrl;

@property (nonatomic,strong) NSString *localUrl;

@end

NS_ASSUME_NONNULL_END
