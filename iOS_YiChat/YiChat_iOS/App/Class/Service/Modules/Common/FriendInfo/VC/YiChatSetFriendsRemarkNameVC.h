//
//  YiChatSetFriendsRemarkNameVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatUserModel;
@interface YiChatSetFriendsRemarkNameVC : NavProjectVC

@property (nonatomic,strong) YiChatUserModel *userModel;

@property (nonatomic,strong) NSString *userId;

+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
