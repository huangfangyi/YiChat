//
//  ZFSelectePersonCardCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/11.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface ZFSelectePersonCardCell : ProjectTableCell
    
@property (nonatomic,strong) NSDictionary *cellModel;
    
+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;
    
- (void)setIcon:(UIImage *)icon name:(NSString *)name;

@end

NS_ASSUME_NONNULL_END
