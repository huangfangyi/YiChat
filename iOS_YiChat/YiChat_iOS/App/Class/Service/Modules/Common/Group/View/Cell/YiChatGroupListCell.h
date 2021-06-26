//
//  YiChatGroupListCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupInfoModel;
@interface YiChatGroupListCell : ProjectTableCell

@property (nonatomic,strong) YiChatGroupInfoModel *infoModel;

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

@end

NS_ASSUME_NONNULL_END
