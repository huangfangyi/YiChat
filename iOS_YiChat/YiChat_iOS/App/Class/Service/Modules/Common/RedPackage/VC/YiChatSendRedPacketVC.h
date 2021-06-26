//
//  YiChatSendRedPacketVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"
#import "YiChatRedPacketDetailModel.h"
NS_ASSUME_NONNULL_BEGIN
typedef void(^SendRedPacketBlock)(NSDictionary *redDic,BOOL isGroup);

@interface YiChatSendRedPacketVC : NavProjectVC
+ (id)initialVC;
@property (nonatomic,strong) NSString *groupMembersNum;//群成员数
@property (nonatomic,strong) NSString *chatId;
@property (nonatomic,assign) BOOL isGroup;//是否是群红包  yes：群红包  no：单聊红包

@property (nonatomic,copy) SendRedPacketBlock sendRedPacketBlock;

@end

NS_ASSUME_NONNULL_END
