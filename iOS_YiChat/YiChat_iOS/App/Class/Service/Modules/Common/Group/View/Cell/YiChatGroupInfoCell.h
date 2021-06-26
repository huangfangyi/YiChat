//
//  YiChatGroupInfoCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@class YiChatGroupInfoModel;
@interface YiChatGroupInfoCell : ProjectTableCell

@property (nonatomic,assign) BOOL switchState;

@property (nonatomic,copy) void(^YiChatGroupInfoCellDidClickSwitch)(NSString *title,BOOL state);

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type;


- (void)setValueForTitle:(NSString *)title content:(NSString *)content;

- (void)setValueForTitle:(NSString *)title contentIcon:(NSString *)url;

- (void)setValueForTitle:(NSString *)title contentSwitch:(BOOL)state;

- (void)changeGroupSilenceState:(BOOL)state;

@end

NS_ASSUME_NONNULL_END
