//
//  YiChatGroupMemberSingleCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupMemberListModel;
@class YiChatUserModel;
@interface YiChatGroupMemberSingleCell : UICollectionViewCell

@property (nonatomic,strong) YiChatGroupMemberListModel *model;

@property (nonatomic,strong) YiChatUserModel *userModel;

@property (nonatomic,copy) void(^yiChatGroupMemberSingleCellClick)(id model);

- (UIImageView *)getIconBack;

@end

NS_ASSUME_NONNULL_END
