//
//  YiChatDynamicHeader.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class YiChatDynamicDataSource;
@interface YiChatDynamicHeader : UITableViewHeaderFooterView

@property (nonatomic,weak) UIViewController *controlVC;

@property (nonatomic,copy) void(^YiChatDynamicHeaderClickCommitLikeBar)(YiChatDynamicDataSource *model,CGPoint point);

@property (nonatomic,copy) void(^YiChatDynamicHeaderHideOrReport)(YiChatDynamicDataSource *model,CGPoint point);

@property (nonatomic,copy) void(^YiChatDynamicHeaderClickDelete)(YiChatDynamicDataSource *model);

@property (nonatomic,copy) void(^YiChatDynamicHeaderClickUserIcon)(YiChatDynamicDataSource *model);

@property (nonatomic,strong) YiChatDynamicDataSource *model;

+ (id)initialWithReuseIdentifier:(NSString *)reuseIdentifier type:(NSNumber *)type;

- (void)createUI;

- (void)updateType:(NSInteger)type;
@end

NS_ASSUME_NONNULL_END
