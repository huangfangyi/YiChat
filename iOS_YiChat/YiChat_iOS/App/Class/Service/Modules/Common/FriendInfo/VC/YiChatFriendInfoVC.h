//
//  YiChatFriendInfoVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectScrollVC.h"

NS_ASSUME_NONNULL_BEGIN

@class YiChatUserModel;
@interface YiChatFriendInfoVC : ProjectScrollVC

@property (nonatomic,strong) NSString *userId;
//需要预传
@property (nonatomic,strong) NSString *fromDes;
//需要预传
@property (nonatomic,strong) YiChatUserModel *model;
//需要预传
@property (nonatomic,strong) NSDictionary *infoDic;

+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
