//
//  YiChatRedPacketHelper.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YiChatRedPacketDetailModel.h"
NS_ASSUME_NONNULL_BEGIN
typedef void(^RedPacketDetailBlock)(YiChatRedPacketDetailModel *redPacketModel,NSDictionary *redDic);

typedef void(^BalanceBlock)(NSString *balance);

typedef void(^SendSmsCodeBlock)(NSString *code);

typedef void(^PayBlock)(BOOL isInstallation, NSString *out_trade_no);
typedef void(^PayStatus)(BOOL status);
typedef void(^SendRedPacket)(NSDictionary * successDic);
static NSString *WXPayonResp = @"wxPayonResp";
static NSString *ALIPayResp = @"aliPayResp";

@interface YiChatRedPacketHelper : NSObject

//获取红包详情
+(void)receiveRedPacketDetailPacketID:(NSString *)packetID redBlock:(RedPacketDetailBlock)redPacketBlock;

//获取余额
+(void)searchBalance:(BalanceBlock)balance;

+(void)sendSMSCode:(NSString *)mobile smsCode:(SendSmsCodeBlock)smsCode;
//拆红包、
+(void)receiveRedPacketID:(NSString *)packetID redBlock:(RedPacketDetailBlock)redPacketBlock;

+(void)weChatPayType:(NSString *)type money:(NSString *)money payBlock:(PayBlock)payBlock;

+(void)aliPayType:(NSString *)type money:(NSString *)money payBlock:(PayBlock)payBlock;

+(void)sendRedPacketGroup:(BOOL)isGroup param:(NSDictionary *)param successDic:(SendRedPacket)successDic;

+(void)queryTradeNo:(NSString *)tradeNo status:(PayStatus)status;
@end

NS_ASSUME_NONNULL_END
