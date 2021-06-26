//
//  ZFMessageTransportVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN
@class ZFChatConfigure;
@interface ZFMessageTransportVC : ProjectTableVC

@property (nonatomic,strong)ZFChatConfigure *chat;

@property (nonatomic,assign) BOOL isShowSelecteAll;

+ (id)initialVC;
@end

NS_ASSUME_NONNULL_END
