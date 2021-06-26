//
//  YiChatConnectionCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@class ProjectCommonCellModel;
@class YiChatUserModel;

@interface YiChatConnectionCell : ProjectTableCell

@property (nonatomic,strong) ProjectCommonCellModel *cellModel;

@property (nonatomic,strong) YiChatUserModel *userModel;

@property (nonatomic,strong) UIView *connectionNewMessageIcons;

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

@end

NS_ASSUME_NONNULL_END
