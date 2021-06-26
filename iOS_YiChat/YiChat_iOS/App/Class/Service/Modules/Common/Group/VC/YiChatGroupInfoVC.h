//
//  YiChatGroupInfoVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupInfoModel;
@interface YiChatGroupInfoVC : ProjectTableVC

@property (nonatomic,strong) NSString *groupId;

@property (nonatomic,strong) YiChatGroupInfoModel *groupInfoModel;


+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
