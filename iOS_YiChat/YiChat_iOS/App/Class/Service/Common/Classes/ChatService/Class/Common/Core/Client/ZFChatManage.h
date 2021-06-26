//
//  ZFChatManage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatNotifyEntity.h"
#import "HTClient.h"
#import "ZFChatUntilities.h"


#define ZHChatContentMessageDic_getMSG(dic)
#define ZHChatContentMessageDic_getMSGType(dic)
#define ZHChatContentMessageDic_getMSGChatType(dic)

NS_ASSUME_NONNULL_BEGIN
@class ZFChatConfigureEntity;

@protocol ZFChatManageDelegate <NSObject>

@required

//自动登录状态
- (void)zfChatManageDelegate_AutoLoginIsFail:(BOOL)state;

- (void)zfChatManageDelegate_DidAutoLoginSuccess;

//当前登录账号在其它设备登录时会接收到此回调
- (void)zfChatManageDelegate_DidLoginFromOtherDevice;

@end

@interface ZFChatManage : NSObject


@property (nonatomic,assign) id<ZFChatManageDelegate>zfChatDelegate;

+ (id)defaultManager;

- (void)configureChatWithEntity:(ZFChatConfigureEntity *)entity  delegate:(id<ZFChatManageDelegate>)delagte;

- (void)needUpdateGroupChatListState:(NSString *)groupId state:(BOOL)state;

- (BOOL)getGroupChatListState:(NSString *)groupId;

- (void)removeAllGroupChatListState;

//调用登录接口
- (void)loginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion;

- (void)autoLogin:(void(^)(BOOL isSuccess))invocation;

- (void)addDelegate:(id<ZFChatManageDelegate>)delagte;

- (void)logout;

- (void)chatConfigureClean;

- (void)updateUnreadMessage;

- (void)cleanConnectionMessage;

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style;

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style target:(id)target sel:(SEL)selector;

- (HTGroupManager *)getGroupManager;

- (ZFMessageType)getContentMessageDicMessageType:(NSDictionary *)contentMessageDic;

- (ZFChatType)getContentMessageDicChatType:(NSDictionary *)contentMessageDic;

- (HTMessage *)getContentMessageDicMSGEntity:(NSDictionary *)contentMessageDic;

- (ZFMessageType)getMessageTypeWithMessageBodyTypeStr:(NSString *)type;

- (ZFMessageType)getMessageTypeWithAction:(NSInteger )extAction;

- (NSString *)getMesageBodyTypeStrInfoMessageType:(ZFMessageType)msgType;

- (ZFChatType)getMessageChatTypeWithChatTypeStr:(NSString *)chatType;

- (NSString *)getMessageChatTypeStrWithMessageChatType:(ZFChatType)chatType;

- (NSString *)getConversationTimeStrContentWithTimeInterval:(NSInteger)timeInterval
                                andTimeInterval:(NSInteger)timeIntervalLast;

- (HTMessage *)translateRequestHttpDataToHTMessage:(NSDictionary *)htmessage;

- (NSDictionary *)getCMDMessageBody:(HTCmdMessage *)cmdMsg;

- (NSDictionary *)getCmdMessageExtData:(HTCmdMessage *)cmdMsg;

- (NSInteger)getCMDMessageAction:(HTCmdMessage *)cmdMsg;
    
- (void)zfChat_sendMessage:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked;
@end

@interface ZFChatConfigureEntity : NSObject

@property (nonatomic,assign) BOOL isAutoLogin;


@end

NS_ASSUME_NONNULL_END
