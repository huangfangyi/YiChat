//
//  ProjectAlbumBrowser.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectBaseCollectiionView.h"
NS_ASSUME_NONNULL_BEGIN

@interface ProjectAlbumBrowser : ProjectBaseCollectiionView

+ (id)showWithAssets:(NSArray *)assetArray index:(NSInteger)index;

@end

NS_ASSUME_NONNULL_END
