//
//  YiChatCollectionCell.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class YiChatCollectionEntity;
@interface YiChatCollectionCell : UITableViewCell

@property (nonatomic,copy) void(^voiceResourcePlayInvocation)(BOOL isNeedPlay,NSString *url);

@property (nonatomic,copy) void(^voiceResourceStopPlay)(NSString *url,NSIndexPath *index);

@property (nonatomic,copy) void(^voiceResourceStartPlay)(NSString *url,NSIndexPath *index);

@property (nonatomic,strong) NSIndexPath *index;

- (id)initWithStyle:(UITableViewCellStyle)style
    reuseIdentifier:(NSString *)reuseIdentifier
               type:(NSInteger)type;


- (void)dic:(YiChatCollectionEntity *)dic cellSize:(CGSize)size sourceSize:(CGSize)size;

@end

NS_ASSUME_NONNULL_END
