//
//  YiChatGroupShutUpList.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupShutUpList : ProjectTableVC
    
@property (nonatomic,strong) NSString *groupId;
    
+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
