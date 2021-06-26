//
//  YiChatDynamicCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatDynamicDataSource;
@interface YiChatDynamicCell : ProjectTableCell

@property (nonatomic,strong) YiChatDynamicDataSource *model;

@property (nonatomic,strong) NSIndexPath *index;

@property (nonatomic,copy) void(^YiChatDynamicLongPress)(YiChatDynamicDataSource *model,NSIndexPath *index);
@property (nonatomic,copy) void(^YiChatDynamicUserNickClick)(YiChatDynamicDataSource *model,NSIndexPath *index);

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(NSInteger)type;

- (void)setModel:(YiChatDynamicDataSource *)model index:(NSIndexPath *)index;

- (UIView *)getCellBack;

@end

NS_ASSUME_NONNULL_END
