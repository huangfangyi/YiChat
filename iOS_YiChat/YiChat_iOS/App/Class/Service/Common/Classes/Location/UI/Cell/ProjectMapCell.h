//
//  ProjectMapCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"
@class AMapPOI;
NS_ASSUME_NONNULL_BEGIN

@interface ProjectMapCell : ProjectTableCell

+ (id)initalWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine;

- (void)setNavValue:(AMapPOI *)dic selecte:(NSInteger)selecte;

@end

NS_ASSUME_NONNULL_END
