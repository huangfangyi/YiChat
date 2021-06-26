//
//  YiChatSettingMainCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@class ProjectCommonCellModel;

@interface YiChatSettingMainCell : ProjectTableCell

@property (nonatomic,strong) ProjectCommonCellModel *cellModel;

/**
 *  type == 0 后面带文字 type == 1 带switch开关
 */
+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

- (void)updateType:(NSInteger)type;

- (void)setSwitch;

@end

NS_ASSUME_NONNULL_END
