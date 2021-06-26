//
//  YiChatGroupMemberOperationVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/26.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupInfoModel;
@interface YiChatGroupMemberOperationVC : ProjectTableVC

// 0 删除群成员 1 增加群成员 2设置管理员 3 艾特群成员
@property (nonatomic,assign) NSInteger operationType;
@property (nonatomic,strong) YiChatGroupInfoModel *groupInfoModel;

@property (nonatomic,strong) NSArray *groupMemberList;

@property (nonatomic,strong) NSString *groupId;
@property (nonatomic,strong) NSArray *managerList;

+ (id)initialVC;
@end

NS_ASSUME_NONNULL_END
