//
//  YiChatSendDynamicToolBar.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseCollectiionView.h"
#import "YiChatSendDynamicBarModel.h"
NS_ASSUME_NONNULL_BEGIN

@class PHAsset;
@interface YiChatSendDynamicToolBar : ProjectBaseCollectiionView

@property (nonatomic,weak) UIViewController *bgVC;

@property (nonatomic,copy) void(^yiChatSendDynamicToolBarDidSelelcteResource)(NSArray *resource);

@property (nonatomic,copy) void(^yiChatSendDynamicToolBarDidDeleteResource)(NSArray *resource);

- (id)initWithFrame:(CGRect)frame;

- (void)addResourceForRefreshUI:(NSArray *)dataModelArr;
@end

NS_ASSUME_NONNULL_END
