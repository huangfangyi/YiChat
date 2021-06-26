//
//  YiChatSendDynamicToolBarCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/10.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class YiChatSendDynamicBarModel;
@interface YiChatSendDynamicToolBarCell : UICollectionViewCell

@property (nonatomic,strong) YiChatSendDynamicBarModel *model;

@property (nonatomic,assign) CGFloat size;

@property (nonatomic,strong) UIImageView *icon;;

@property (nonatomic,copy) void(^YiChatSendDynamicBarDidClickCancel)(id model,NSIndexPath *index);

@end

NS_ASSUME_NONNULL_END
