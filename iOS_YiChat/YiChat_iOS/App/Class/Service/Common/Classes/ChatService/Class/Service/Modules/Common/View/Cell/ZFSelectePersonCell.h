//
//  ZFSelectePersonCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface ZFSelectePersonCell : ProjectTableCell

@property (nonatomic,strong) NSDictionary *cellModel;

@property (nonatomic,copy) void(^zfSelectePerson)(NSDictionary *model , BOOL state);

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;

- (void)setIcon:(UIImage *)icon name:(NSString *)name;

@end

NS_ASSUME_NONNULL_END
