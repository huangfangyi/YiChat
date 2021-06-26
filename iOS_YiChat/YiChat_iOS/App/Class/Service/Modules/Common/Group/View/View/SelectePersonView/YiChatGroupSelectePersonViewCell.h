//
//  YiChatGroupSelectePersonViewCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class YiChatUserModel;
@interface YiChatGroupSelectePersonViewCell : UICollectionViewCell

@property (nonatomic,strong) YiChatUserModel *model;
@property (nonatomic,copy) void(^yiChatGroupSelecteCellClick)(NSIndexPath *index);


- (void)setModelWithModel:(YiChatUserModel *)model index:(NSIndexPath *)index;
@end

NS_ASSUME_NONNULL_END
