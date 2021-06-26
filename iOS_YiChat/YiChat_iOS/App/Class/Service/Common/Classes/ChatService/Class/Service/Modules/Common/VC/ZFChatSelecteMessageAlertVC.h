//
//  ZFChatSelecteMessageAlertVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatSelecteMessageAlertVC : ProjectTableVC

@property (nonatomic,copy) void(^zfPersonCardSelecte)(YiChatUserModel *model);

@property (nonatomic,strong) NSString *groupId;

@property (nonatomic,assign) NSInteger groupPower;

+ (id)initialVC;

@end

NS_ASSUME_NONNULL_END
