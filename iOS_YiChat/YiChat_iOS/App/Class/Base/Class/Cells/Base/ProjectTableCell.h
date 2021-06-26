//
//  ProjectTableCell.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectTableCell : UITableViewCell

@property (nonatomic,strong,readonly) NSIndexPath *sIndexPath;
@property (nonatomic,readonly) CGFloat sCellHeight;
@property (nonatomic,readonly) CGFloat sCellWidth;
@property (nonatomic,readonly) BOOL sIsHasDownline;
@property (nonatomic,readonly) BOOL sIsHasRightArrow;

@property (nonatomic,strong) UIView *sCDownLine;

@property (nonatomic,strong) UIImageView *sCRightArrow;


/**
 *  common
 */
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth;

/**
 *  down line
 */
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine;

/**
 *  right arrow
 */
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasRightArrow:(NSNumber *)isHasRightArrow;

/**
 *  down line & right arrow
 */
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine isHasRightArrow:(NSNumber *)isHasRightArrow;

- (void)updateSystemConfigWithIndexPath:(NSIndexPath *)indexPaths arrow:(NSNumber *)isHasArrows downLine:(NSNumber *)isHasDownLines cellHeight:(NSNumber *)cellHeight;

- (CGRect)getRightArrowSize;

@end

NS_ASSUME_NONNULL_END
