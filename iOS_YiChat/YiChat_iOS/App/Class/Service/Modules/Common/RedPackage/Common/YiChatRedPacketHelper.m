//
//  YiChatRedPacketHelper.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketHelper.h"
#import "WXApi.h"
#import <CommonCrypto/CommonDigest.h>
#import <AlipaySDK/AlipaySDK.h>

@implementation YiChatRedPacketHelper
+(void)receiveRedPacketID:(NSString *)packetID redBlock:(RedPacketDetailBlock)redPacketBlock{
    NSDictionary *param = [ProjectRequestParameterModel getReceiveRedPacketParametersWithPacketId:packetID];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper receiveRedPacketWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatRedPacketDetailModel *model = [YiChatRedPacketDetailModel mj_objectWithKeyValues:dataDic];
                redPacketBlock(model,dataDic);
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+(void)receiveRedPacketDetailPacketID:(NSString *)packetID redBlock:(RedPacketDetailBlock)redPacketBlock{
    NSDictionary *param = [ProjectRequestParameterModel getReceiveRedPacketParametersWithPacketId:packetID];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper receiveRedPacketDetailWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatRedPacketDetailModel *model = [YiChatRedPacketDetailModel mj_objectWithKeyValues:dataDic];
                redPacketBlock(model,dataDic);
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+(void)searchBalance:(BalanceBlock)balance{
    [ProjectRequestHelper searchBalanceWithParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                NSString *balan = [NSString stringWithFormat:@"%@",dataDic[@"data"][@"balance"]];
                balance(balan);
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+(void)sendSMSCode:(NSString *)mobile smsCode:(SendSmsCodeBlock)smsCode{
    NSDictionary *param = [ProjectRequestParameterModel getSendSMSParametersWithMobile:mobile];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [ProjectRequestHelper sendSMSCodeWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:(BOOL)YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                NSString *codeStr = [NSString stringWithFormat:@"%@",dataDic[@"data"]];
                smsCode(codeStr);
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"验证码已发送"];
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
    
}

+(void)weChatPayType:(NSString *)type money:(NSString *)money payBlock:(PayBlock)payBlock{
    NSDictionary *param = [ProjectRequestParameterModel setWeChatPayWithMoney:money type:type];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];

    [ProjectRequestHelper weChatPayWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                NSDictionary *productDic = dataDic[@"data"];
                NSLog(@"== %@",productDic);
                if ([productDic isKindOfClass:[NSDictionary class]]) {
                    [WXApi registerApp:[NSString stringWithFormat:@"%@",productDic[@"appid"]]];
                    PayReq *request = [[PayReq alloc] init];
                    /** 微信分配的公众账号ID -> APPID */
                    request.partnerId = [NSString stringWithFormat:@"%@",productDic[@"partnerid"]];
                    /** 预支付订单 从服务器获取 */
                    request.prepayId = [NSString stringWithFormat:@"%@",productDic[@"prepayid"]];
                    /** 商家根据财付通文档填写的数据和签名 <暂填写固定值Sign=WXPay>*/
                    request.package = [NSString stringWithFormat:@"%@",productDic[@"package"]];;
                    /** 随机串，防重发 */
                    request.nonceStr= [NSString stringWithFormat:@"%@",productDic[@"noncestr"]];
                    /** 时间戳，防重发 */
                    NSString *s = [NSString stringWithFormat:@"%@",productDic[@"timestamp"]];
                    
                    /** 商家根据微信开放平台文档对数据做的签名, 可从服务器获取，也可本地生成*/
                    request.sign= [NSString stringWithFormat:@"%@",productDic[@"sign"]];
                    /* 调起支付 */
                    request.timeStamp= [s intValue];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([WXApi sendReq:request]) {
                            payBlock(YES,@"");
                        }else{
                            payBlock(NO,@"");
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"未安装微信客户端,请使用其他支付方式"];
                        }
                    });
                }
                
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+(void)queryTradeNo:(NSString *)tradeNo status:(PayStatus)status{
    NSDictionary *param = [ProjectRequestParameterModel setWeChatPayWithTradeNo:tradeNo];
//    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper payTradeStatusWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                NSString *sta = [NSString stringWithFormat:@"%@",dataDic[@"data"]];
                if ([sta isEqualToString:@"1"]) {
                    status(YES);
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"支付失败"];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
//                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
//            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
    
}

+(void)aliPayType:(NSString *)type money:(NSString *)money payBlock:(PayBlock)payBlock{
    NSDictionary *param = [ProjectRequestParameterModel setWeChatPayWithMoney:money type:type];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper aliPayWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;

                dispatch_async(dispatch_get_main_queue(), ^{
                    NSString *appScheme = @"alipayYichatPay";
                    NSString *order = [NSString stringWithFormat:@"%@",dataDic[@"data"]];
                    NSString *orderStr = [order stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
                    
                    NSArray *array = [orderStr componentsSeparatedByString:@"&"];
                    for (NSString *str in array) {
                        if ([str containsString:@"biz_content="]) {
                            NSString *s = [str stringByReplacingOccurrencesOfString:@"biz_content=" withString:@""];
                            NSData *data = [s dataUsingEncoding:NSUTF8StringEncoding];
                            NSDictionary *dataDic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
                            if (dataDic) {
                                NSString *out_trade_no = [NSString stringWithFormat:@"%@",dataDic[@"out_trade_no"]];
                                payBlock(YES,out_trade_no);
                            }
                        }
                    }
                    
                    
                    [[AlipaySDK defaultService] payOrder:order fromScheme:appScheme callback:^(NSDictionary *resultDic) {
                        NSLog(@"sendRed = %@",resultDic);
                    }];
                    
                });
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+(void)weChatPay:(NSDictionary *)dataDic payBlock:(PayBlock)payBlock{
    NSDictionary *productDic = dataDic[@"data"];
    [WXApi registerApp:[NSString stringWithFormat:@"%@",productDic[@"appid"]]];
    PayReq *request = [[PayReq alloc] init];
    /** 微信分配的公众账号ID -> APPID */
    request.partnerId = [NSString stringWithFormat:@"%@",productDic[@"partnerid"]];
    /** 预支付订单 从服务器获取 */
    request.prepayId = [NSString stringWithFormat:@"%@",productDic[@"prepayid"]];
    /** 商家根据财付通文档填写的数据和签名 <暂填写固定值Sign=WXPay>*/
    request.package = @"Sign=WXPay";
    /** 随机串，防重发 */
    request.nonceStr= [NSString stringWithFormat:@"%@",productDic[@"noncestr"]];
    /** 时间戳，防重发 */
    NSString *s = [NSString stringWithFormat:@"%@",productDic[@"timestamp"]];
    
    /** 商家根据微信开放平台文档对数据做的签名, 可从服务器获取，也可本地生成*/
    request.sign= [NSString stringWithFormat:@"%@",productDic[@"sign"]];
    /* 调起支付 */
    request.timeStamp= [s intValue];
    //                [WXApi sendReq:request];
    if ([WXApi sendReq:request]) {
        payBlock(YES,@"");
    }else{
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"未安装微信客户端,请使用其他支付方式"];
    }
}

+(void)sendRedPacketGroup:(BOOL)isGroup param:(NSDictionary *)param successDic:(SendRedPacket)successDic{
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    if (isGroup) {
        [ProjectRequestHelper sendGroupRedPackageWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    YiChatRedPacketDetailModel *model = [YiChatRedPacketDetailModel mj_objectWithKeyValues:obj];
                    if (model.code == 0) {
                        successDic((NSDictionary *)obj);
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"红包发送成功"];
                    } else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                    }
                    
                }else if([obj isKindOfClass:[NSString class]]){
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
                [ProjectRequestHelper progressHidden:progress];
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
        
    }else{
        [ProjectRequestHelper sendSingleRedPackageWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    YiChatRedPacketDetailModel *model = [YiChatRedPacketDetailModel mj_objectWithKeyValues:obj];
                    if (model.code == 0) {
                        successDic((NSDictionary *)obj);
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"红包发送成功"];
                    } else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                    }
                }
                else if([obj isKindOfClass:[NSString class]]){
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
                [ProjectRequestHelper progressHidden:progress];
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }
}

-(PayReq *)weChatResult:(NSDictionary *)result{
    NSDictionary *productDic = result[@"data"];
    [WXApi registerApp:[NSString stringWithFormat:@"%@",productDic[@"appid"]]];
    PayReq *request = [[PayReq alloc] init];
    /** 微信分配的公众账号ID -> APPID */
    request.partnerId = [NSString stringWithFormat:@"%@",productDic[@"partnerid"]];
    /** 预支付订单 从服务器获取 */
    request.prepayId = [NSString stringWithFormat:@"%@",productDic[@"prepayid"]];
    /** 商家根据财付通文档填写的数据和签名 <暂填写固定值Sign=WXPay>*/
    request.package = @"Sign=WXPay";
    /** 随机串，防重发 */
    request.nonceStr= [NSString stringWithFormat:@"%@",productDic[@"noncestr"]];
    /** 时间戳，防重发 */
    NSString *s = [NSString stringWithFormat:@"%@",productDic[@"timestamp"]];
    
    /** 商家根据微信开放平台文档对数据做的签名, 可从服务器获取，也可本地生成*/
    request.sign= [NSString stringWithFormat:@"%@",productDic[@"sign"]];
    /* 调起支付 */
    request.timeStamp= [s intValue];
    return request;
}

@end
