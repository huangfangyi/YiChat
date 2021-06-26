//
//  ProjectAlbumnBrowserCell.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class ProjectAlbumBrowserModel ;
@class AVPlayerItem;
@interface ProjectAlbumnBrowserCell : UICollectionViewCell

@property (nonatomic,strong) ProjectAlbumBrowserModel *model;

- (void)setModel:(ProjectAlbumBrowserModel * _Nonnull)model size:(CGSize)size;
@end

NS_ASSUME_NONNULL_END
