//
//  ZFChatCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger,ZFChatCellType){
    ZFChatCellTypeText,ZFChatCellTypePhoto,ZFChatCellTypeVoice,ZFChatCellTypeVideo,ZFChatCellTypeLocation,ZFChatCellTypeRedPackgeSendOrReceive,ZFChatCellTypePersonCard,ZFChatCellTypeCommonCMDMessage
};

@class ZFChatConfigure;
@interface ZFChatCell : ProjectTableCell

@property (nonatomic,strong) ZFChatConfigure *chatModel;

@property (nonatomic,copy) void(^zfChatCellClickVoiceAction)(ZFChatConfigure *chatModel,NSIndexPath *index);

@property (nonatomic,copy) void(^zfChatCellClickSendFailAction)(ZFChatConfigure *chatModel,NSIndexPath *index);

@property (nonatomic,copy) void(^zfChatCellClickIconAction)(NSString *userId,NSIndexPath *index);

@property (nonatomic,copy) void(^zfChatCellLongpressClickAction)(ZFChatConfigure *chatModel,NSIndexPath *index);

@property (nonatomic,copy) void(^zfChatCellRedPackgeClickAction)(ZFChatConfigure *chatModel,NSIndexPath *index);
    
@property (nonatomic,copy) void(^zfChatCellPersonCardClickAction)(ZFChatConfigure *chatModel,NSIndexPath *index);

@property (nonatomic,copy) void(^zfChatCellUserIconLongPressAction)(ZFChatConfigure *chatModel,NSIndexPath *index);
    
@property (nonatomic,copy) void(^zfChatCellReviewPhotoMessageAction)(ZFChatConfigure *chatModel,NSIndexPath *index,UIView *backView);
    
@property (nonatomic,copy) void(^zfChatCellReviewVideoMessageAction)(ZFChatConfigure *chatModel,NSIndexPath *index,UIView *backView);

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(ZFChatCellType)type chatType:(NSInteger)chatType;

- (void)changeVoicePlayUIWithState:(BOOL)state;
    
- (void)voiceClickMethod:(UIButton *)click;

- (void)changeGroupRoleIcon:(NSInteger)power;

- (UIView *)getCellBack;

- (UIView *)getIconBack;

- (UIView *)getMessageBack;

@end

NS_ASSUME_NONNULL_END
