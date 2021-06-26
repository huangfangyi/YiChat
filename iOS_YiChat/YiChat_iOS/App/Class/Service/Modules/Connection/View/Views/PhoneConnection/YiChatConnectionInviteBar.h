//
//  YiChatConnectionInviteBar.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatConnectionInviteBar : UIView

@property (nonatomic,copy) void(^yiChatInvitebarClick)(NSArray *dataArr);

- (id)initWithFrame:(CGRect)frame;

- (void)updateUIWithDataSource:(NSArray *)datasource;

@end

NS_ASSUME_NONNULL_END
