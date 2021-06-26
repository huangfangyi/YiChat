//
//  ProjectSearchCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"
#import "YiChatUserModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectSearchCell : ProjectTableCell

@property (nonatomic,strong) YiChatUserModel *userModel;
    
@property (nonatomic,copy) void(^ProjectSearchCellSelecte)(YiChatUserModel *model,BOOL selecteState);

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;


@end

NS_ASSUME_NONNULL_END
