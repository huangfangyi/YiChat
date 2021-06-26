//
//  YiChatFriendInfoView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YiChatUserModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatFriendInfoView : UIView

@property (nonatomic,strong) YiChatUserModel *userModel;

- (id)initWithFrame:(CGRect)frame;

@end

NS_ASSUME_NONNULL_END
