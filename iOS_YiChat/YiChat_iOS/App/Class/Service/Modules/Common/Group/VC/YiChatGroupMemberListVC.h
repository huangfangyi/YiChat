//
//  YiChatGroupMemberListVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/2.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

@class YiChatGroupInfoModel;
@interface YiChatGroupMemberListVC : NavProjectVC

@property (nonatomic,strong) NSString *groupId;

@property (nonatomic,strong) YiChatGroupInfoModel *groupInfoModel;

@property (nonatomic,strong) NSArray *groupMemberList;


+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
