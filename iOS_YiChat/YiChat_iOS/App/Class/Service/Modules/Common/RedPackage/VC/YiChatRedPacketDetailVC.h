//
//  YiChatRedPacketDetailVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"
#import "YiChatRedPacketDetailModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatRedPacketDetailVC : ProjectTableVC
+ (id)initialVC;
@property (nonatomic,copy) NSString *groupMembersNum;//群成员数
@property (nonatomic,copy) NSString *groupId;//群ID
@property (nonatomic,assign) BOOL isGroup;//是否是群红包  yes：群红包  no：单聊红包
@property (nonatomic,strong) NSString *packetId;
@property (nonatomic,strong) NSDictionary *redDic;//红包内容信息

@property (nonatomic,strong) YiChatRedPacketDetailModel *redModel;
@end

NS_ASSUME_NONNULL_END
