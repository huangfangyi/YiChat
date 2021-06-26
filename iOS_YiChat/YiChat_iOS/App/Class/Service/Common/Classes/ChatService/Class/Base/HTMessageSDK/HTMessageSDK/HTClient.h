/*!
 @header  HTClient.h
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import "HTMessageManager.h"
#import "HTConversationManager.h"
#import "HTGroupManager.h"
#import "HTMessage.h"
#import "HTCmdMessage.h"
#import "HTClientDelegate.h"

/**
 HTMessage ~cn:消息Client ~en:message Client
 */
@interface HTClient : NSObject

@property (nonatomic,strong,readonly) NSString *config_xmppIP;
@property (nonatomic,strong,readonly) NSString *config_serviceIP;

@property (nonatomic,strong,readonly) NSString *config_OSSAccessKey;
@property (nonatomic,strong,readonly) NSString *config_OSSSecretKey;
@property (nonatomic,strong,readonly) NSString *config_OSSEndPoint;
@property (nonatomic,strong,readonly) NSString *config_OSSBucket;
@property (nonatomic,strong,readonly) NSString *config_chatFileHost;

@property (nonatomic,copy) NSDictionary *(^htClientGetUserInfo)(void);

/**
 ~cn:获取Client单例 ~en:get Client singleton
 
 @return Client实例
 */
+(HTClient *)sharedInstance;

/**
 ~cn:初始化IM服务器和业务服务器 ~en:initializeSDK IMIp Business IP
 */
- (void)initializeImIp:(NSString *)imIp AndBusinessIp:(NSString *)businessIp;

/**
 ~cn:初始化OSS ~en:initializeOSS
 */
- (void)initializeOSSWithOSSAccessKey:(NSString *)OSSAccessKeyStr AndOSSSecretKey:(NSString *)OSSSecretKeyStr AndOSSEndPoint:(NSString *)OSSEndPointStr AndOSSBucket:(NSString *)OSSBucketStr AndChatFileHost:(NSString *)chatFileHostStr;

/**
 ~cn:初始化SDK ~en:initializeSDK
 */
- (void)initializeSDK;

/**
 ~cn:SDK版本号 ~en:SDK Version
 */
@property (nonatomic, strong, readonly) NSString *SDKVersion;

/**
 ~cn:当前登录账号 ~en:current login username
 */
@property (nonatomic, strong, readonly) NSString *currentUsername;

/**
 ~cn:是否自动登陆 ~en:whether automatic login
 */
@property (nonatomic,assign)BOOL autoLogin;

/**
 ~cn:消息模块 ~en:message modular manager
 */
@property (nonatomic, strong, readonly) HTMessageManager *messageManager;

/**
 ~cn:会话模块 ~en:conversation modular manager
 */
@property (nonatomic, strong, readonly) HTConversationManager *conversationManager;

/**
 ~cn:群组模块 ~en:group modular manager
 */
@property (nonatomic, strong, readonly) HTGroupManager *groupManager;

/**
 ~cn:添加回调代理 ~en:add a callback delegate
 
 @param aDelegate ~cn:要添加的代理 ~en:to add a delegate
 @param aQueue ~cn:执行代理方法的队列 ~en:the queue of executive agent method
 
 */
- (void)addDelegate:(id<HTClientDelegate>)aDelegate delegateQueue:(dispatch_queue_t)aQueue;

/**
 ~cn:移除回调代理 ~en:remove a delegate
 
 @param aDelegate ~cn:要移除的代理 ~en:to remove a delegate
 */
- (void)removeDelegate:(id)aDelegate;
/**
 ~cn:注册用户，不推荐使用，建议后台通过REST API注册 ~en:not recommended, it is recommended that the background by REST API registration
 
 @param aUsername ~cn:用户名 ~en:username
 @param aPassword ~cn:密码 ~en:password
 @param aBlocked ~cn:注册回调 ~en:a callback when register
 */
-(void)registerWithUsername:(NSString *)aUsername password:(NSString *)aPassword completion:(void(^)(BOOL result))aBlocked;

/**
 ~cn:登陆 ~en:login
 
 @param aUsername ~cn:用户名 ~en:username
 @param aPassword ~cn:密码 ~en:password
 @param aBlocked ~cn:登陆回调 ~en:a callback when register
 */
- (void)loginWithUsername:(NSString *)aUsername password:(NSString *)aPassword completion:(void(^)(BOOL result))aBlocked;

/**
 ~cn:退出登陆 ~en:logout
 */
- (void)logout;

/**
 ~cn:发送一条普通消息 ~en:send a normal message
 
 @param aMessage ~cn:消息实例 ~en:message entity
 @param aBlocked ~cn:发送回调 ~en:a callback when sending
 */
- (void)sendMessage:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked;

//需要手动把remotefilepath 设置完成 再传值
- (void)sendMessageUnNeedUpload:(HTMessage *)message completion:(void(^)(HTMessage *message,NSError *error))blocked;

/**
 ~cn:发送一套透传消息 ~en:send a Cmd message
 
 @param aCMDMessage ~cn:透传啊消息实例 ~en:Cmd message entity
 @param aBlocked ~cn:发送回调 ~en:a callback when sending
 */
- (void)sendCMDMessage:(HTCmdMessage *)aCMDMessage completion:(void(^)(HTCmdMessage *message,NSError *error))aBlocked;


/**
 ~cn:发送一条语音视频通话透传 ~en:send a Call Cmd message
 
 @param aCMDMessage ~cn:透传啊消息实例 ~en:Cmd message entity
 @param aBlocked ~cn:发送回调 ~en:a callback when sending
 */
- (void)sendCallCMDMessage:(HTCmdMessage *)aCMDMessage completion:(void (^)(HTCmdMessage *, NSError *error))aBlocked;

/**
 ~cn:绑定设备DeviceToken ~en:bind DeviceToken
 
 @param aDeviceToken ~cn:获取到的DeviceToken ~en:the DeviceToken
 */
- (void)bindDeviceToken:(NSData *)aDeviceToken;

@end
