//
//  YiChatGroupManagerListVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupManagerListVC : ProjectTableVC

@property (nonatomic,strong) NSString *groupId;
@property (nonatomic,strong) NSString *onwerId;

+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
