//
//  YiChatRedPacketDetail.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBassModel.h"
#import <MJExtension.h>
NS_ASSUME_NONNULL_BEGIN


@interface YiChatRedPacketInfoModel : YiChatBassModel
@property (nonatomic,strong) NSString *avatar;
@property (nonatomic,assign) BOOL maxStatus;
@property (nonatomic,strong) NSString *money;
@property (nonatomic,strong) NSString *moneyDesc;
@property (nonatomic,strong) NSString *nick;
@property (nonatomic,strong) NSString *receiveTime;
@property (nonatomic,strong) NSString *userId;
@end

@interface YiChatRedPacketListModel : YiChatBassModel
@property (nonatomic,strong) NSString *avatar;
@property (nonatomic,strong) NSString *content;
@property (nonatomic,strong) NSString *money;
@property (nonatomic,strong) NSString *nick;
@property (nonatomic,strong) NSString *num;
@property (nonatomic,assign) double receiveMoney;
@property (nonatomic,strong) NSString *receiveNum;
@property (nonatomic,assign) NSInteger status;
@property (nonatomic,strong) NSString *userId;
@property (nonatomic,strong) NSArray *list;
@property (nonatomic,strong) NSString *packetId;
@end

@interface YiChatRedPacketDetailModel : YiChatBassModel
@property (nonatomic,strong) YiChatRedPacketListModel *data;
@end

NS_ASSUME_NONNULL_END
