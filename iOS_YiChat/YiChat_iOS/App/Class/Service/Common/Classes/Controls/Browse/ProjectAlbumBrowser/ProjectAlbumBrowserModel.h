//
//  ProjectAlbumBrowserModel.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class PHAsset;
@class AVPlayerItem;
@interface ProjectAlbumBrowserModel : NSObject

@property (nonatomic,strong) PHAsset *asset;

@property (nonatomic,strong) AVPlayerItem *playerItem;

@end

NS_ASSUME_NONNULL_END
