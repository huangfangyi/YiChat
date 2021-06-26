//
//  YiChatChangeGroupDesVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupInfoModel;
@interface YiChatChangeGroupDesVC : NavProjectVC

@property (nonatomic,strong) YiChatGroupInfoModel *groupInfo;

+ (id)initialVC;
@end

NS_ASSUME_NONNULL_END
