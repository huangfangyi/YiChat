//
//  ZFMessageGroupTransportVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN

@class ZFChatConfigure;
@interface ZFMessageGroupTransportVC : ProjectTableVC

@property (nonatomic,strong)ZFChatConfigure *chat;

+ (id)initialVC;
@end

NS_ASSUME_NONNULL_END
