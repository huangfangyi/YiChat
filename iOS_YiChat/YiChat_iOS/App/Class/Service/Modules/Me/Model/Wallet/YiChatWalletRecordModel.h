//
//  YiChatWalletRecordModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YiChatBassModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatWalletRecordListModel : NSObject
@property (nonatomic,strong) NSString *ctime;
@property (nonatomic,strong) NSString *dateDesc;
@property (nonatomic,strong) NSString *memo;
@property (nonatomic,strong) NSString *money;
@property (nonatomic,strong) NSString *moneyDesc;

@property (nonatomic,strong) NSString *userId;
@property (nonatomic,strong) NSString *nick;
@property (nonatomic,strong) NSString *avatar;
@property (nonatomic,strong) NSString *receiveTime;
@property (nonatomic,strong) NSString *type;
@property (nonatomic,strong) NSString *packetId;
@property (nonatomic,assign) NSInteger totalCount;
@property (nonatomic,assign) NSInteger receiveCount;


@property (nonatomic,assign) NSInteger status;
@property (nonatomic,strong) NSString *time;
@property (nonatomic,strong) NSString *refuseReason;
@end

@interface YiChatWalletRecordModel : YiChatBassModel
@property (nonatomic,strong) NSArray *data;
@end

NS_ASSUME_NONNULL_END
