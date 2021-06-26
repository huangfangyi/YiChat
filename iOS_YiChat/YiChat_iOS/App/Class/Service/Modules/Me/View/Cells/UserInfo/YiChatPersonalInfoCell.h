//
//  YiChatPersonalInfoCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN
@class ProjectCommonCellModel;
@interface YiChatPersonalInfoCell : ProjectTableCell

@property (nonatomic,strong) ProjectCommonCellModel *cellModel;


+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

- (void)updateType:(NSInteger)type;

@end

NS_ASSUME_NONNULL_END
