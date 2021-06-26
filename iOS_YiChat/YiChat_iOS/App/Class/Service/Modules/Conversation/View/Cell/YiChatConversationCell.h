//
//  YiChatConversationCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"
@class HTConversation;
@class YiChatUserModel;

NS_ASSUME_NONNULL_BEGIN

@interface YiChatConversationCell : ProjectTableCell

@property (nonatomic,strong) HTConversation *cellModel;

/**
 *  type == 0 固定的cell type == 1 单聊 type == 2 群聊
 */
+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

@end

NS_ASSUME_NONNULL_END
