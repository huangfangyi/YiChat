//
//  YiChatGroupMemberListModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupMemberListModel : ProjectBaseModel

@property (nonatomic,strong) NSString *name;

@property (nonatomic,strong) NSString *iconUrl;

//0 删除 1 添加 2 展示群成员资料
@property (nonatomic,assign) NSInteger type;


@end

NS_ASSUME_NONNULL_END
