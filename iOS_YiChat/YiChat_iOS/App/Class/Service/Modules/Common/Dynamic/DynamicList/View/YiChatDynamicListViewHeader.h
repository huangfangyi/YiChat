//
//  YiChatDynamicListViewHeader.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatDynamicListViewHeader : UIView

@property (nonatomic,strong) NSString *userIdStr;

@property (nonatomic,strong) NSString *backImageUrl;

@property (nonatomic,copy) void(^YiChatDynamicListViewHeaderClickBackGroud)();

+ (id)create;

- (void)updateData;

@end

NS_ASSUME_NONNULL_END
