//
//  YiChatGroupSelectePersonCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"
@class YiChatUserModel;
NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupSelectePersonCell : ProjectTableCell


@property (nonatomic,strong) YiChatUserModel *cellModel;

@property (nonatomic,copy) void(^yiChatGroupSelecte)(YiChatUserModel *model , BOOL state);



+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

- (UITextField *)getGroupNameInput;


@end

NS_ASSUME_NONNULL_END
