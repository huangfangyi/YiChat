/*!
 @header  HTClient.m
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 
 */

#import "HTClient.h"
#import "XMPPFramework.h"
#import "HTMessageBodyManager.h"
#import "HTDBManager.h"
#import "HTMessageCategoryUploader.h"
#import "HTMessageDefines.h"
#import "HTCmdMessage.h"
#import "NSObject+QSModel.h"
#import "QSTools.h"
#import "GCDMulticastDelegate.h"
#import "HTMessageDefines.h"
#import "Reachability.h"
#import "QSNetworkTool.h"
#import "NSData+XMPP.h"
#import "NSString+URLEncoding.h"

typedef void(^sendCompletion)(HTMessage *message,NSError *error);
typedef void(^sendCMDCompletion)(HTCmdMessage *message,NSError *error);
typedef void(^BoolBlock)(BOOL isSuccess);

@interface HTClient()<XMPPStreamDelegate>

@property(nonatomic,strong)XMPPStream       *xmppStream;
@property(nonatomic,strong)XMPPReconnect    *xmppReconnect;
@property(nonatomic,strong)XMPPRoster       *xmppRoster;
@property(nonatomic,strong)XMPPAutoPing     *xmppAutoPing;
@property(nonatomic,strong)NSDateFormatter  *inputFormatter;
@property(nonatomic,strong)NSString         *kPassword;
@property(nonatomic,copy)BoolBlock          loginResult;
@property(nonatomic,copy)BoolBlock          registerResult;
@property(nonatomic,copy)sendCompletion   kSendCompleionBlocked;
@property(nonatomic,copy)sendCMDCompletion kSendCMDCompleionBlocked;
@property(nonatomic,assign)BOOL             hadLoadOfflineMessage;
@property(nonatomic,strong)NSMutableArray   *offlineMessages;
@property(nonatomic,assign)BOOL             isBeginTimer;
// 是否是自动登陆的
@property(nonatomic,assign)BOOL             loginByAuto;

// 放置刚发送的消息用作回调
@property(nonatomic,strong)NSMutableDictionary * sendingContain;

@property(nonatomic,strong)NSString * deviceToken;

@property (nonatomic,strong)GCDMulticastDelegate <HTClientDelegate> *clientDelegate;

// 判断是否已经尝试连接过了，如果APP刚启动时候是有网络的，xmpp连接成功了，之后再断网，xmpp会调用重连流程，如果一启动APP的时候，就是断网状态，xmpp在稍后网络状态良好的情况下不会自动调用重连
// 此处为了解决xmpp不会自动重连的问题，监听启动APP的网络状态，如果首次进入的时候网络状态是无网络，则hasTryConnectFailed = YES；等下次在进入的时候走自动登流程，且只走一次
@property (nonatomic,assign)BOOL hasTryConnectFailed;
@property (nonatomic,assign)BOOL hasBeenConnectedBefore;
// 注册网络监听
@property (nonatomic,strong)Reachability * hostReach;

@property (nonatomic,strong) dispatch_semaphore_t autoLoigLock;

@property (nonatomic,strong) dispatch_semaphore_t sendMessageLock;
@end

static HTClient *__client = nil;

@implementation HTClient

#pragma mark - singleton

+ (HTClient *)sharedInstance{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        __client = [[HTClient alloc] init];
        __client.autoLoigLock = dispatch_semaphore_create(1);
        __client.sendMessageLock = dispatch_semaphore_create(1);
    });
    return __client;
}

#pragma mark - init

- (id)init {
    if (self = [super init]) {
        self.clientDelegate = (GCDMulticastDelegate <HTClientDelegate> *)[[GCDMulticastDelegate alloc] init];
        //注册程序进入前台通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector (foregroudEvent) name:UIApplicationWillEnterForegroundNotification object:nil];
        //注册程序进入后台通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector (backgroudEvent) name:UIApplicationDidEnterBackgroundNotification object:nil];
        
        [self checkNewworkState];
        return self;
    }
    return nil;
}

- (void)initializeSDK {
    
    NSUserDefaults * df = [NSUserDefaults standardUserDefaults];
    NSString * uuid = [df valueForKey:@"YICHAT_UUID"];
    if (uuid && uuid.length > 0) {
        
    }else{
        [df setObject:[QSTools creatUUID] forKey:@"YICHAT_UUID"];
        [df synchronize];
    }
    
    NSString * username = [[NSUserDefaults standardUserDefaults] valueForKey:kXMPP_USERNAME];
    NSString * password = [[NSUserDefaults standardUserDefaults] valueForKey:kXMPP_PASSWORD];
    
    NSLog(@"initializeSDK %@ %@",username,password);
    if (username.length >0 && password.length > 0) {
        NSLog(@"自动登录条件判断");
        if ([[[NSUserDefaults standardUserDefaults] valueForKey:[NSString stringWithFormat:@"%@_autoLogin",username]] boolValue]) {
             NSLog(@"自动登录条件判断 %@ %@",username,password);
            _currentUsername = username;
            [self checkCanAutoLogin:username password:password];
        }
    }
}

/**
~cn:初始化OSS ~en:initializeOSS
*/
- (void)initializeOSSWithOSSAccessKey:(NSString *)OSSAccessKeyStr AndOSSSecretKey:(NSString *)OSSSecretKeyStr AndOSSEndPoint:(NSString *)OSSEndPointStr AndOSSBucket:(NSString *)OSSBucketStr AndChatFileHost:(NSString *)chatFileHostStr{
    _config_OSSAccessKey = OSSAccessKeyStr;
    _config_OSSSecretKey = OSSSecretKeyStr;
    _config_OSSEndPoint = OSSEndPointStr;
    _config_OSSBucket = OSSBucketStr;
    _config_chatFileHost = chatFileHostStr;
}

- (void)initializeImIp:(NSString *)imIp AndBusinessIp:(NSString *)businessIp{
    _config_xmppIP = imIp;
    _config_serviceIP = businessIp;
}

- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)delegateQueue {
    if (delegateQueue == nil || delegateQueue == NULL) {
        [self.clientDelegate addDelegate:aDelegate delegateQueue:dispatch_get_main_queue()];
    }else{
        [self.clientDelegate addDelegate:aDelegate delegateQueue:delegateQueue];
    }
}

- (void)removeDelegate:(id)aDelegate {
    [self.clientDelegate removeDelegate:self];
}

- (void)bindDeviceToken:(NSData *)aDeviceToken {
    NSString *deviceString = [[aDeviceToken description] stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    deviceString = [deviceString stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    NSUserDefaults * df = [NSUserDefaults standardUserDefaults];
    [df setObject:deviceString forKey:@"ht_devicetoken"];
    [df synchronize];
    self.deviceToken = deviceString;
}

#pragma mark public

- (void)setAutoLogin:(BOOL)autoLogin {
    if ([HTClient sharedInstance].currentUsername.length > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:@(autoLogin) forKey:[NSString stringWithFormat:@"%@_autoLogin",self.currentUsername]];
        [[NSUserDefaults standardUserDefaults] setValue:self.currentUsername forKey:kXMPP_USERNAME];
        [[NSUserDefaults standardUserDefaults] setValue:self.kPassword forKey:kXMPP_PASSWORD];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

- (BOOL)autoLogin {
    NSString * username = [[NSUserDefaults standardUserDefaults] valueForKey:kXMPP_USERNAME];
    NSString * password = [[NSUserDefaults standardUserDefaults] valueForKey:kXMPP_PASSWORD];
    if (username.length >0 && password.length > 0) {
        if ([[[NSUserDefaults standardUserDefaults] valueForKey:[NSString stringWithFormat:@"%@_autoLogin",username]] boolValue]) {
            return YES;
        }
    }
    return NO;
}

- (void)registerWithUsername:(NSString *)username password:(NSString *)password completion:(BoolBlock)blocked {
    if ([self.xmppStream isConnected]) {
        [self.xmppStream disconnect];
    }
    self.kPassword = password;
    self.registerResult = blocked;
    [self loginWithUsername:username password:password completion:^(BOOL result) {
        if (!result) {
            if ([self.xmppStream isConnected] && [self.xmppStream supportsInBandRegistration]) {
                [self.xmppStream setMyJID:[XMPPJID jidWithUser:username domain:kXMPP_DOMAIN resource:kXMPP_RESOURCE]];
                NSError *error ;
                if (![self.xmppStream registerWithPassword:password error:&error]) {
                    if (self.registerResult) {
                        self.registerResult(NO);
                        self.registerResult = nil;
                    }
                }
            }
        }
    }];
}

- (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(BoolBlock)blocked {
    self.kPassword = password;
    self.loginResult = blocked;
   	if ([self.xmppStream isConnected]) {
        [self.xmppStream disconnect];
    }
    _currentUsername = username;
    NSString * resource = kXMPP_RESOURCE;
    if (self.deviceToken) {
        resource = [NSString stringWithFormat:@"%@:%@",kXMPP_RESOURCE,self.deviceToken];
    }else{
        NSUserDefaults * df = [NSUserDefaults standardUserDefaults];
        NSString *localDeviceToken = [df objectForKey:@"ht_devicetoken"];
        if (localDeviceToken && localDeviceToken.length > 0) {
            resource = [NSString stringWithFormat:@"%@:%@",kXMPP_RESOURCE,localDeviceToken];
        }
    }
    [self.xmppStream setMyJID:[XMPPJID jidWithUser:username domain:kXMPP_DOMAIN resource:resource]];
    NSError *error = nil;
    if (![self.xmppStream connectWithTimeout:XMPPStreamTimeoutNone error:&error]) {
        
         NSLog(@"xmpp connect result  %@",[NSThread currentThread]);
        if (self.loginResult) {
            self.loginResult(NO);
            self.loginResult = nil;
        }
    }
}

- (void)logout {
    XMPPPresence *presence = [XMPPPresence presenceWithType:@"unavailable"];
    [self.xmppStream sendElement:presence];
    [self.xmppStream disconnect];
    [self clearnAllDBAbouts];
}

- (void)sendCallCMDMessage:(HTCmdMessage *)kCMDMessage completion:(void (^)(HTCmdMessage *, NSError *))blocked {
    if (blocked) {
        self.kSendCMDCompleionBlocked = blocked;
    }
    kCMDMessage.from = self.currentUsername;
    XMPPMessage *mes = nil;
    if ([kCMDMessage.chatType isEqualToString:@"1"]) {
        mes = [XMPPMessage messageWithType:@"chat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@%@",kCMDMessage.to,kXMPP_DOMAIN]] elementID:kCMDMessage.msgId];
    }else {
        mes = [XMPPMessage messageWithType:@"groupchat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@muc.%@",kCMDMessage.to,kXMPP_DOMAIN]]  elementID:kCMDMessage.msgId];
    }
    NSDictionary *cmdDicI = @{@"type":@"5000",@"data":[kCMDMessage modelToJSONObject]};
    NSString *encodeStr = [[cmdDicI modelToJSONString] urlEncodeString];
//    NSData *encodeData = [messageString dataUsingEncoding:NSUTF8StringEncoding];
//    NSString *encodeStr = [encodeData xmpp_base64Encoded];
    [mes addChild:[DDXMLNode elementWithName:@"body" stringValue:encodeStr]];
    DDXMLElement *newElement = [DDXMLElement elementWithName:@"request"];//设置一个新的节点
    DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"urn:xmpp:receipts"];//设置一个新的节点
    [newElement addAttribute:newnode];
    
    if ([kCMDMessage.chatType isEqualToString:@"1"]) {
        [mes addChild:newElement];
    }
    [self.xmppStream sendElement:mes];
}

- (void)sendCMDMessage:(HTCmdMessage *)kCMDMessage completion:(void (^)(HTCmdMessage *, NSError *))blocked {
    if (blocked) {
        self.kSendCMDCompleionBlocked = blocked;
    }
    kCMDMessage.from = self.currentUsername;
    XMPPMessage *mes = nil;
    if ([kCMDMessage.chatType isEqualToString:@"1"]) {
        mes = [XMPPMessage messageWithType:@"chat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@%@",kCMDMessage.to,kXMPP_DOMAIN]] elementID:kCMDMessage.msgId];
    }else {
        mes = [XMPPMessage messageWithType:@"groupchat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@muc.%@",kCMDMessage.to,kXMPP_DOMAIN]]  elementID:kCMDMessage.msgId];
    }
    NSDictionary *cmdDicI = @{@"type":@"1000",@"data":[kCMDMessage modelToJSONObject]};
    
    NSString *messageString = [cmdDicI modelToJSONString];
    NSString *encodeStr = [messageString urlEncodeString];
//    NSString *encodeStr = [encodeData xmpp_base64Encoded];
    
    [mes addChild:[DDXMLNode elementWithName:@"body" stringValue:encodeStr]];
    DDXMLElement *newElement = [DDXMLElement elementWithName:@"request"];//设置一个新的节点
    DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"urn:xmpp:receipts"];//设置一个新的节点
    [newElement addAttribute:newnode];
    if ([kCMDMessage.chatType isEqualToString:@"1"]) {
        [mes addChild:newElement];
    }
    [self.xmppStream sendElement:mes];
}

- (void)sendMessage:(HTMessage *)message completion:(void(^)(HTMessage *message,NSError *error))blocked {
    
    if (blocked) {
        self.kSendCompleionBlocked = blocked;
    }
    [HTMessageCategoryUploader sendMessage:message withProgressBlock:nil andMessageId:message.msgId andSendResult:^(BOOL isSuccess, HTMessage *aMessage) {
        // 添加至发送的队列中
        [self.sendingContain setObject:message forKey:message.msgId];
        if (isSuccess == YES) {
            // 重新构造messageBody
            NSDictionary *messageBodyOld = [HTMessageBodyManager convertMessageBodyToDicWithMessageBody:message];
            NSMutableDictionary * messageBodyDic = messageBodyOld.mutableCopy;
            // 重新构造message
            NSMutableDictionary * messageDic = ((NSDictionary *)[aMessage modelToJSONObject]).mutableCopy;
            [messageBodyDic removeObjectForKey:@"localPath"];
            [messageBodyDic removeObjectForKey:@"thumbnailLocalPath"];
            // 此处处理兼容安卓
            [messageDic setObject:messageBodyDic.copy forKey:@"body"];
            [messageDic removeObjectForKey:@"downLoadState"];
            [messageDic removeObjectForKey:@"isSender"];
            [messageDic removeObjectForKey:@"sendState"];
            NSDictionary * messageDicI = @{@"type":@"2000",@"data":messageDic};
            
            NSString *encodeStr = [[messageDicI modelToJSONString] urlEncodeString];
            //            NSData *encodeData = [messageString dataUsingEncoding:NSUTF8StringEncoding];
            //            NSString *encodeStr = [encodeData xmpp_base64Encoded];
            //            NSLog(@"加密了的base64消息：%@",encodeStr);
            XMPPMessage *mes = nil;
            if ([aMessage.chatType isEqualToString:@"1"]) {
                mes =  [XMPPMessage messageWithType:@"chat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@%@",aMessage.to,kXMPP_DOMAIN]] elementID:aMessage.msgId];
            }else {
                mes =  [XMPPMessage messageWithType:@"groupchat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@muc.%@",aMessage.to,kXMPP_DOMAIN]] elementID:aMessage.msgId];
            }
            [mes addChild:[DDXMLNode elementWithName:@"body" stringValue:encodeStr]];
            
            DDXMLElement *newElement = [DDXMLElement elementWithName:@"request"];//设置一个新的节点
            DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"urn:xmpp:receipts"];//设置一个新的节点
            [newElement addAttribute:newnode];
            if ([aMessage.chatType isEqualToString:@"1"]) {
                [mes addChild:newElement];
            }
            else if([aMessage.chatType isEqualToString:@"2"]){
                [mes addChild:newElement];
            }
            [self.xmppStream sendElement:mes];
            [self storeMessage:aMessage];
        }else{
            [self handleSendMessageStatus:aMessage.msgId withError:[NSError new]];
        }
    }];
   
}

- (void)sendMessageUnNeedUpload:(HTMessage *)message completion:(void(^)(HTMessage *message,NSError *error))blocked{
    
   
    if(message && [message isKindOfClass:[HTMessage class]]){
        if (blocked) {
            self.kSendCompleionBlocked = blocked;
        }
        
        message.sendState = SendStateSuccessed;
        [self.sendingContain setObject:message forKey:message.msgId];
        
        HTMessage *aMessage = message;
        // 重新构造messageBody
        NSDictionary *messageBodyOld = [HTMessageBodyManager convertMessageBodyToDicWithMessageBody:message];
        NSMutableDictionary * messageBodyDic = messageBodyOld.mutableCopy;
        // 重新构造message
        NSMutableDictionary * messageDic = ((NSDictionary *)[aMessage modelToJSONObject]).mutableCopy;
        [messageBodyDic removeObjectForKey:@"localPath"];
        [messageBodyDic removeObjectForKey:@"thumbnailLocalPath"];
        // 此处处理兼容安卓
        [messageDic setObject:messageBodyDic.copy forKey:@"body"];
        [messageDic removeObjectForKey:@"downLoadState"];
        [messageDic removeObjectForKey:@"isSender"];
        [messageDic removeObjectForKey:@"sendState"];
        
        NSDictionary *tmp = [self htClientFeltSendUploadDic:messageDic];
        
        if(tmp && [tmp isKindOfClass:[NSDictionary class]]){
            
            NSDictionary * messageDicI = @{@"type":@"2000",@"data":tmp};
            NSString *encodeStr = [[messageDicI modelToJSONString] urlEncodeString];
            //    NSData *encodeData = [messageString dataUsingEncoding:NSUTF8StringEncoding];
            //    NSString *encodeStr = [encodeData xmpp_base64Encoded];
            
            XMPPMessage *mes = nil;
            if ([aMessage.chatType isEqualToString:@"1"]) {
                mes =  [XMPPMessage messageWithType:@"chat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@%@",aMessage.to,kXMPP_DOMAIN]] elementID:aMessage.msgId];
            }else {
                mes =  [XMPPMessage messageWithType:@"groupchat" to:[XMPPJID jidWithString:[NSString stringWithFormat:@"%@@muc.%@",aMessage.to,kXMPP_DOMAIN]] elementID:aMessage.msgId];
            }
            [mes addChild:[DDXMLNode elementWithName:@"body" stringValue:encodeStr]];
            
            DDXMLElement *newElement = [DDXMLElement elementWithName:@"request"];//设置一个新的节点
            DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"urn:xmpp:receipts"];//设置一个新的节点
            [newElement addAttribute:newnode];
            if ([aMessage.chatType isEqualToString:@"1"]) {
                [mes addChild:newElement];
            }
            else if([aMessage.chatType isEqualToString:@"2"]){
                [mes addChild:newElement];
            }
            [self.xmppStream sendElement:mes];
            [self storeMessage:aMessage];
        }
    }
    else{
        blocked(message,[NSError new]);
    }
    
}

- (NSDictionary *)htClientFeltSendUploadDic:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSMutableDictionary *tmpDic = [NSMutableDictionary dictionaryWithCapacity:0];
        [tmpDic addEntriesFromDictionary:dic];
        if([tmpDic.allKeys containsObject:@"type"]){
            [tmpDic removeObjectForKey:@"type"];
        }
        if([tmpDic.allKeys containsObject:@"isRead"]){
            [tmpDic removeObjectForKey:@"isRead"];
        }
        if([tmpDic.allKeys containsObject:@"body"]){
            NSDictionary *bodyDic = tmpDic[@"body"];
            if(bodyDic && [bodyDic isKindOfClass:[NSDictionary class]]){
                NSMutableDictionary *bodyDicTmp = [NSMutableDictionary dictionaryWithCapacity:0];
                [bodyDicTmp addEntriesFromDictionary:bodyDic];
                if([bodyDicTmp.allKeys containsObject:@"fileSize"]){
                    [bodyDicTmp removeObjectForKey:@"fileSize"];
                }
                if([bodyDicTmp.allKeys containsObject:@"thumbnailLocalPath"]){
                    [bodyDicTmp removeObjectForKey:@"thumbnailLocalPath"];
                }
                [tmpDic removeObjectForKey:@"body"];
                [tmpDic addEntriesFromDictionary:@{@"body":bodyDicTmp}];
            }
        }
        return tmpDic;
    }
    return nil;
}

#pragma mark XMPPStreamDelegate

- (void)xmppStream:(XMPPStream *)sender didNotRegister:(NSXMLElement *)error {
    [self.xmppStream disconnect];
    if (self.registerResult) {
        self.registerResult(NO);
    }
    NSLog(@"注册名已存在");
}
- (void)xmppStreamDidRegister:(XMPPStream *)sender {
    [self.xmppStream disconnect];
    if (self.registerResult) {
        self.registerResult(YES);
    }
    NSLog(@"注册成功");
}

// 登陆部分
- (void)xmppStreamDidConnect:(XMPPStream *)sender {
    
    NSError *error = nil;
    if (![self.xmppStream authenticateWithPassword:self.kPassword error:&error]) {
        NSLog(@"Error authenticating: %@", error);
    }
}

- (void)xmppStreamDidAuthenticate:(XMPPStream *)sender {
    
    
    self.hasBeenConnectedBefore = YES;
    if (!self.loginByAuto) {
        [self initAllDBAbouts];
    }
    else{
        NSLog(@"xmpp 自动登录 initAllDBAbouts %@", [NSThread currentThread]);
        
        [self initAllDBAbouts];
    }
    
    [self uploadNewDeviceId];
    // 完成认证，发送在线状态
    XMPPPresence *presence = [XMPPPresence presence];
    [self.xmppStream sendElement:presence];
    // 登陆成功回调
    if (self.loginResult) {
        self.loginResult(YES);
        self.loginResult = nil;
    }
    if(self.autoLogin && self.loginByAuto){
        if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(accountDidAutoLoginSuccess)]) {
            [self.clientDelegate accountDidAutoLoginSuccess];
        }
    }
    
    [self handlOfflineMessage];
}

- (void)xmppStream:(XMPPStream *)sender didNotAuthenticate:(NSXMLElement *)error {
    
    NSLog(@"xmppStream  error = %@",error);
    // 认证错误
    if (self.loginResult) {
        self.loginResult(NO);
        self.loginResult = nil;
    }
}

// 消息发送成功
- (void)xmppStream:(XMPPStream *)sender didSendMessage:(XMPPMessage *)message {
    NSLog(@"\n消息发送成功\n");
    if (self.kSendCompleionBlocked) {
        if ([[message elementForName:@"body"] stringValue]) {
            NSString * decodeStr = [[[message elementForName:@"body"] stringValue] specialURLDecodedString];
//            NSData *decodeData = [messageStr dataUsingEncoding:NSUTF8StringEncoding];
//            NSData *decodeDataTemp = [decodeData xmpp_base64Decoded];
//            NSString *decodeStr =  [[NSString alloc] initWithData:decodeDataTemp encoding:NSUTF8StringEncoding];
            NSDictionary * messageDic = [self stringToDictionaryWithString:decodeStr];
            [self handleSendMessageStatus:messageDic[@"data"][@"msgId"] withError:nil];
        }
    }
}

// 消息发送失败
- (void)xmppStream:(XMPPStream *)sender didFailToSendMessage:(XMPPMessage *)message error:(NSError *)error {
    NSLog(@"\n消息发送失败\n");
    if (self.kSendCompleionBlocked) {
        if ([[message elementForName:@"body"] stringValue]) {
            NSString * decodeStr = [[[message elementForName:@"body"] stringValue] specialURLDecodedString];
//            NSData *decodeData = [messageStr dataUsingEncoding:NSUTF8StringEncoding];
//            NSData *decodeDataTemp = [decodeData xmpp_base64Decoded];
//            NSString *decodeStr =  [[NSString alloc] initWithData:decodeDataTemp encoding:NSUTF8StringEncoding];
            NSDictionary * messageDic = [self stringToDictionaryWithString:decodeStr];
            [self handleSendMessageStatus:messageDic[@"data"][@"msgId"] withError:error];
        }
    }
}

// 消息接收部分
// 单聊消息会走此代理
- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message {
    NSLog(@"xmppStream  xmppmessageReceive ==== %@",message);
    NSString *ackMsgId = message.elementID;
    NSLog(@"elementID >>>> %@ ___ type >>>> %@",ackMsgId,message.type);
    
    if ([[message elementForName:@"body"] stringValue]) {
        NSArray * delayArray = [message elementsForName:@"delay"];
        NSString * stamp;
        if (delayArray.count == 0) {
            NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
            NSTimeInterval a=[dat timeIntervalSince1970]*1000;
            stamp = [NSString stringWithFormat:@"%f", a];
        }else{
            DDXMLElement *element = [delayArray objectAtIndex:0];
            stamp = [[element attributeForName:@"stamp"] stringValue];
        }
        
//
//        NSInteger trans = [self messageTimestampFormat:stamp];
//
//        [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:[NSString stringWithFormat:@"收到消息的时间 %@ \r\n  收到消息的时间 %@ \r\n 消息转换时间戳%ld",delayArray,stamp,trans] clickBtns:@[@"是"] invocation:^(NSInteger row) {
//
//        }];
//
        NSString *bodyStr = [[message elementForName:@"body"] stringValue];
        
        NSString * decodeStr = [bodyStr specialURLDecodedString];
//        NSData *decodeData = [messageStr dataUsingEncoding:NSUTF8StringEncoding];
//        NSData *decodeDataTemp = [decodeData xmpp_base64Decoded];
//        NSString *decodeStr =  [[NSString alloc] initWithData:decodeDataTemp encoding:NSUTF8StringEncoding];
        
        NSDictionary * messageDic = [self stringToDictionaryWithString:decodeStr];
        NSLog(@"Dic ====== %@",decodeStr);
        
        NSInteger type = [messageDic[@"type"] intValue];
        if (type == 2000){
            HTMessage *message = [HTMessage modelWithJSON:messageDic[@"data"]];
            message.type = type;
            message.isSender = NO;
            message.timestamp = [self messageTimestampFormat:stamp];
            if ([message.chatType isEqualToString:@"1"]) {
                [self sendACKToIMServerWithMessageId:ackMsgId];
            }
            // 收到群消息在handle里面处理
            [self preHandleMessage:message];
        }else if(type == 1000){
            HTCmdMessage *message = [HTCmdMessage modelWithJSON:messageDic[@"data"]];
            message.timestamp = [self messageTimestampFormat:stamp];
            if ([message.chatType isEqualToString:@"1"]) {
                [self sendACKToIMServerWithMessageId:ackMsgId];
            }else{
                [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:stamp]]];
            }
            [self.messageManager didReceiveCMDMessage:@[message]];
            if (messageDic[@"data"][@"body"]) {
                NSDictionary * bodyDic = [self stringToDictionaryWithString:messageDic[@"data"][@"body"]];
                if([bodyDic[@"action"] integerValue] == 2004) {
                    [self controlCMDMessage:message];
                }
            }
        }else if(type == 4000){
            
            [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:stamp]]];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:HT_DELETE_GROUP_INFO object:self userInfo:@{@"groupId":messageDic[@"data"][@"gid"],@"isSender":@NO}];
            
            [self.conversationManager deleteOneConversationWithChatterId:messageDic[@"data"][@"gid"] isCleanAllHistoryMessage:YES];
            
        }else if(type == 3000){
            
            NSString * xmppTo = [[message attributeForName:@"from"] stringValue];
            [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:stamp]]];
            HTMessage *message = [HTMessage new];
            message.from = messageDic[@"data"][@"uid"];
            message.to = [[xmppTo componentsSeparatedByString:@"@"] objectAtIndex:0];
            message.msgId = [QSTools creatUUID];
            message.chatType = @"2";
            message.type = 2000;
            message.isSender = NO;
            message.msgType = 2001;
            message.ext = @{@"action":@"2005"};
            message.timestamp = [self messageTimestampFormat:stamp];
            HTMessageBody * body = [HTMessageBody new];
            body.content = [NSString stringWithFormat:@"\"%@\"退出了群聊",messageDic[@"data"][@"nickname"]];
            message.body = body;
            [self handleNormalMessage:message];
            
        }else if(type == 5000){
            HTCmdMessage *message = [HTCmdMessage modelWithJSON:messageDic[@"data"]];
            message.timestamp = [self messageTimestampFormat:stamp];
            if ([message.chatType isEqualToString:@"1"]) {
                [self sendACKToIMServerWithMessageId:ackMsgId];
            }else{
                [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:stamp]]];
            }
            [self.messageManager didReceiveCMDMessage:@[message]];
        }
    }
    else{
        NSLog(@"收到没有body 的消息");
        
        NSArray * delayArray = [message elementsForName:@"delay"];
        NSString * stamp;
        NSString * msgId;
        if (delayArray.count == 0) {
            NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
            NSTimeInterval a=[dat timeIntervalSince1970]*1000;
            stamp = [NSString stringWithFormat:@"%f", a];
        }else{
            DDXMLElement *element = [delayArray objectAtIndex:0];
            stamp = [[element attributeForName:@"stamp"] stringValue];
        }
        
        
        NSArray * receiveArray = [message elementsForName:@"received"];
        if (receiveArray.count == 0) {
            return;
        }else{
            DDXMLElement *element = [receiveArray objectAtIndex:0];
            msgId = [[element  attributeForName:@"id"] stringValue];
            
            NSLog(@"receive%@",msgId);
            
        }
        
        if(!(msgId && [msgId isKindOfClass:[NSString class]])){
            return;
        }
        
        HTMessage *msg = [HTMessage new];
        msg.timestamp = [self messageTimestampFormat:stamp];;
        msg.msgId = msgId;
        
        [self.messageManager didReceiveSeriveTimeCorrectMessages:@[msg]];
        
    }
}

- (BOOL)xmppReconnect:(XMPPReconnect *)sender shouldAttemptAutoReconnect:(SCNetworkReachabilityFlags)reachabilityFlags{
    NSLog(@"xmppStream  AutoReconnect : \n %u ", reachabilityFlags);
    return YES;
}

//- (void)xmppReconnect:(XMPPReconnect *)sender didDetectAccidentalDisconnect:(SCNetworkConnectionFlags)connectionFlags {
//
//}

-(void)xmppStreamDidDisconnect:(XMPPStream *)sender withError:(NSError *)error {
    if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(connectionStateDidChange:)]) {
        [self.clientDelegate connectionStateDidChange:HTConnectionStateDisconnected];
    }
    
    NSLog(@"连接失败，%@",error);
    if (self.loginResult) {
        self.loginResult(NO);
        self.loginResult = nil;
    }
}

- (void)xmppStreamWillConnect:(XMPPStream *)sender {
    NSLog(@"%@",self.clientDelegate);
    if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(connectionStateDidChange:)]) {
        [self.clientDelegate connectionStateDidChange:HTConnectionStateConnecting];
    }
    NSLog(@"xmppStream 将要开始连接");
}

- (void)xmppStream:(XMPPStream *)sender socketDidConnect:(GCDAsyncSocket *)socket {
    
    if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(connectionStateDidChange:)]) {
        [self.clientDelegate connectionStateDidChange:HTConnectionStateConnected];
    }
    NSLog(@"xmppStream socket连接成功");
}

/**
 * This method is called if an XMPP error is received.
 * In other words, a <stream:error/>.
 *
 * However, this method may also be called for any unrecognized xml stanzas.
 *
 * Note that standard errors (<iq type='error'/> for example) are delivered normally,
 * via the other didReceive...: methods.
 **/
- (void)xmppStream:(XMPPStream *)sender didReceiveError:(NSXMLElement *)error {
    
    NSLog(@"xmppStream  didReceiveError - > %@",error);
    NSArray * delayArray = [error elementsForName:@"conflict"];
    if (delayArray.count > 0) {
        DDXMLElement *element = [delayArray objectAtIndex:0];
        NSString * xmlString = [element XMLString];
        if ([xmlString containsString:@"conflict"]) {
            if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(userAccountDidLoginFromOtherDevice)]) {
               // [self logout];
                [self.clientDelegate userAccountDidLoginFromOtherDevice];
            }
        }
    }
}

- (void)xmppStream:(XMPPStream *)sender willSecureWithSettings:(NSMutableDictionary *)settings {
    
    if (/* DISABLES CODE */ (NO))
    {
        [settings setObject:[NSNumber numberWithBool:YES] forKey:(NSString *)kCFStreamSSLAllowsAnyRoot];
    }
    
    [settings setObject:[NSNumber numberWithBool:YES] forKey:(NSString *)kCFStreamPropertySSLPeerTrust];
    
    if (/* DISABLES CODE */ (NO))
    {
        [settings setObject:[NSNull null] forKey:(NSString *)kCFStreamSSLPeerName];
    }
    else
    {
        // Google does things incorrectly (does not conform to RFC).
        // Because so many people ask questions about this (assume xmpp framework is broken),
        // I've explicitly added code that shows how other xmpp clients "do the right thing"
        // when connecting to a google server (gmail, or google apps for domains).
        
        NSString *expectedCertName = nil;
        NSString *serverDomain = self.xmppStream.hostName;
        NSString *virtualDomain = [self.xmppStream.myJID domain];
        if ([serverDomain isEqualToString:@"talk.google.com"]){
            if ([virtualDomain isEqualToString:@"gmail.com"]){
                expectedCertName = virtualDomain;
            }else{
                expectedCertName = serverDomain;
            }
        }else if (serverDomain == nil){
            expectedCertName = virtualDomain;
        }else{
            expectedCertName = serverDomain;
        }
        if (expectedCertName){
            [settings setObject:expectedCertName forKey:(NSString *)kCFStreamSSLPeerName];
        }
    }
}

- (void)xmppStreamDidSecure:(XMPPStream *)sender {
    NSLog(@"验证完成");
}

#pragma mark getter

- (XMPPStream *)xmppStream {
    if (!_xmppStream) {
        _xmppStream = [[XMPPStream alloc] init];
        // 支持真机后台运行
#if !TARGET_IPHONE_SIMULATOR
        {
            _xmppStream.enableBackgroundingOnSocket = YES;
        }
#endif
        //socket 连接的时候 要知道host port 然后connect
        [_xmppStream setHostName:kXMPP_HOST];
        [_xmppStream setHostPort:kXMPP_PORT];
        //为什么是addDelegate? 因为xmppFramework 大量使用了多播代理multicast-delegate ,代理一般是1对1的，但是这个多播代理是一对多得，而且可以在任意时候添加或者删除
        [_xmppStream addDelegate:self delegateQueue:dispatch_get_main_queue()];
        
        //1.autoPing 发送的时一个stream:ping 对方如果想表示自己是活跃的，应该返回一个pong
        _xmppAutoPing = [[XMPPAutoPing alloc] init];
        //所有的Module模块，都要激活active
        [_xmppAutoPing activate:_xmppStream];
        
        //autoPing由于它会定时发送ping,要求对方返回pong,因此这个时间我们需要设置
        [_xmppAutoPing setPingInterval:1000];
        //不仅仅是服务器来得响应;如果是普通的用户，一样会响应
        [_xmppAutoPing setRespondsToQueries:YES];
        //这个过程是C---->S  ;观察 S--->C(需要在服务器设置）
        //2.autoReconnect 自动重连，当我们被断开了，自动重新连接上去，并且将上一次的信息自动加上去
        _xmppReconnect = [[XMPPReconnect alloc] init];
        [_xmppReconnect activate:_xmppStream];
        [_xmppReconnect setAutoReconnect:YES];
        
    }
    return _xmppStream;
}

// format server message received timestamp

- (NSDateFormatter *)inputFormatter {
    if (_inputFormatter == nil) {
        _inputFormatter = [NSDateFormatter new];
        // 0 时区
        //        NSTimeZone* GTMzone = [NSTimeZone timeZoneForSecondsFromGMT:0];
        // 东八区

        NSTimeZone* GTMzone = [NSTimeZone timeZoneForSecondsFromGMT:8*3600];
        [_inputFormatter setTimeZone:GTMzone];
        [_inputFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"];

    }
    return _inputFormatter;
}

- (NSMutableArray *)offlineMessages {
    if (_offlineMessages == nil) {
        _offlineMessages = @[].mutableCopy;
    }
    return _offlineMessages;
}

- (NSMutableDictionary *)sendingContain {
    if (_sendingContain == nil) {
        _sendingContain = @{}.mutableCopy;
    }
    return _sendingContain;
}

#pragma mark - private

- (void)handleSendMessageStatus:(NSString *)messageId withError:(NSError *)error {
    HTMessage * message = [self.sendingContain objectForKey:messageId];
    if (message) {
        if (!error) {
            message.sendState = SendStateSuccessed;
            self.kSendCompleionBlocked(message,nil);
        }else{
            message.sendState = SendStateFail;
            self.kSendCompleionBlocked(message,error);
        }
        [[HTDBManager sharedInstance] updateOneNormalMessage:message];
        [self.sendingContain removeObjectForKey:messageId];
    }
}

- (NSInteger )messageTimestampFormat:(NSString *)stamp {
    NSDate* inputDate = [self.inputFormatter dateFromString:stamp];
    
    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
    NSInteger a= ((long long)[dat timeIntervalSince1970]) * 1000;
    
    NSInteger timestamp = a;
    if(inputDate != nil){
         timestamp = ((long long)[inputDate timeIntervalSince1970]) * 1000;
    }
    
    if(abs((timestamp / 1000 -  a / 1000)) > (3600 * 24 * 1000)){
        timestamp = a;
    }
    
    NSDate* date = [NSDate dateWithTimeIntervalSinceNow:0];
    NSTimeInterval aa=[date timeIntervalSince1970]*1000;
             
    NSString *timeStr = [NSString stringWithFormat:@"%.0f",aa];
             
    timestamp +=  [[timeStr substringWithRange:NSMakeRange(timeStr.length - 3, 3)] integerValue];
       
  
    return timestamp;
}

- (void)sendACKToIMServerWithMessageId:(NSString *)messageId {
    XMPPMessage *mes = [XMPPMessage messageWithType:@"chat" to:[XMPPJID jidWithString:kXMPP_DOMAIN] elementID:[QSTools creatUUID]];
    DDXMLElement *newElement = [DDXMLElement elementWithName:@"received"];//设置一个新的节点
    DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"urn:xmpp:receipts"];//设置一个新的节点
    DDXMLNode *newnode1 = [DDXMLNode attributeWithName:@"id" stringValue:messageId];//设置一个新的节点
    [newElement addAttribute:newnode];
    [newElement addAttribute:newnode1];
    [mes addChild:newElement];
    NSLog(@"XMPPMes ====== %@",mes);

    [self.xmppStream sendElement:mes];
    
}

- (void)sendGroupACKToIMServerWithTimestamp:(NSString *)timestamp {
    DDXMLElement *newElement = [DDXMLElement elementWithName:@"ack" stringValue:[NSString stringWithFormat:@"%ld",(NSInteger)([[NSDate date] timeIntervalSince1970] * 1000)]];//设置一个新的节点
    DDXMLNode *newnode = [DDXMLNode attributeWithName:@"xmlns" stringValue:@"http://jabber.org/protocol/muc#timestamp"];//设置一个新的节点
    [newElement addAttribute:newnode];
    XMPPIQ * iq = [XMPPIQ iqWithType:@"set" to:[XMPPJID jidWithString:@"muc.app.im"] elementID:[QSTools creatUUID] child:newElement];
    [self.xmppStream sendElement:iq];
}

- (void)preHandleMessage:(HTMessage *)message {
    if (self.hadLoadOfflineMessage == NO) {
        [self.offlineMessages addObject:message];
    }else{
        [self handleAllMessage:message needGroupACK:YES];
    }
}

- (void)handleAllMessage:(HTMessage *)message needGroupACK:(BOOL)isAck{
    if ([message.chatType isEqualToString:@"2"]) {
        [self handleGroupAboutInfo:message];
        if (isAck) {
            [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:[NSString stringWithFormat:@"%ld",message.timestamp]]]];
        }
    }
    [self handleNormalMessage:message];
}

- (void)handleNormalMessage:(HTMessage *)message {
    HTConversation * converModel = [HTConversation new];
    converModel.lastMessage = message;
    [self.messageManager insertOneNormalMessage:message];
    [self.messageManager didReceiveMessages:@[message]];
    [self.conversationManager updataOneConversationWithChatterConversation:converModel isReadAllMessage:NO];
}

- (void)storeMessage:(HTMessage *)message {
    if(message && [message isKindOfClass:[HTMessage class]]){
        HTConversation * converModel = [HTConversation new];
        converModel.chatterId = message.to;
        converModel.lastMessage = message;
        [[HTDBManager sharedInstance] insertOneNormalMessage:message];
        [self.conversationManager updataOneConversationWithChatterConversation:converModel isReadAllMessage:YES];
    }
}

// 处理群通知相关信息
- (void)handleGroupAboutInfo:(HTMessage *)message {
    if ([message.ext[@"action"] integerValue] == 2000) {
        HTGroup * group = [HTGroup new];
        group.owner = message.from;
        group.groupName = message.ext[@"groupName"];
        group.groupDescription = message.ext[@"groupDescription"];
        group.groupAvatar = message.ext[@"groupAvatar"];
        group.groupId = message.to;
        
        [[NSNotificationCenter defaultCenter] postNotificationName:HT_NEW_GROUP_CREATED object:nil userInfo:@{@"group":group,@"isSender":@NO}];
        
    }
    else if ([message.ext[@"action"] integerValue] == 2001) {
        HTGroup * group = [HTGroup new];
        group.owner = message.from;
        group.groupName = message.ext[@"groupName"];
        group.groupId = message.to;
        group.groupDescription = message.ext[@"groupDescription"];
        group.groupAvatar = message.ext[@"groupAvatar"];
        
        if ([message.ext[@"uid"] isEqualToString:self.currentUsername]) {
            message.body.content = [NSString stringWithFormat:@"管理员修改了群资料"];
        }else{
            message.body.content = [NSString stringWithFormat:@"\"%@\"修改了群资料",message.ext[@"nickName"]];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:HT_UPDATE_GROUP_INFO object:nil userInfo:@{@"group":group,@"nickName":message.ext[@"nickName"],@"isSender":@NO}];
        
    }
    
    //   else if ([message.ext[@"action"] integerValue] == 2002) {
    //       HTGroup * group = [HTGroup new];
    //       group.owner = message.from;
    //       group.groupDescription = message.ext[@"groupDescription"];
    //       group.groupId = message.to;
    //       if ([message.ext[@"uid"] isEqualToString:self.currentUsername]) {
    //           message.body.content = [NSString stringWithFormat:@"你修改了群描述"];
    //       }else{
    //           message.body.content = [NSString stringWithFormat:@"\"%@\"修改群描述",message.ext[@"nickName"]];
    //       }
    //       [self.groupManager updateGroupDescWithGroup:group isSender:NO];
    //   }
    else if ([message.ext[@"action"] integerValue] == 2003) {
        BOOL isHasThisGroup = NO;
        for (HTGroup * group in self.groupManager.groups) {
            if ([group.groupId isEqualToString:message.to]) {
                isHasThisGroup = YES;
                break;
            }
        }
        if (isHasThisGroup == NO) {
            HTGroup * group = [HTGroup new];
            group.owner = message.ext[@"owner"];
            group.groupName = message.ext[@"groupName"];
            group.groupDescription = message.ext[@"groupDescription"];
            group.groupAvatar = message.ext[@"groupAvatar"];
            group.groupId = message.to;
            [[NSNotificationCenter defaultCenter] postNotificationName:HT_NEW_GROUP_CREATED object:nil userInfo:@{@"group":group,@"isSender":@NO}];
            
        }
        NSArray *newGroupMemebers = message.ext[@"members"];
        NSString * showContent = newGroupMemebers[0][@"nickName"];
        for (int i = 1; i < newGroupMemebers.count; i++) {
            showContent = [NSString stringWithFormat:@"%@、%@",showContent,newGroupMemebers[i][@"nickName"]];
        }
        showContent = [NSString stringWithFormat:@"%@加入了群聊",showContent];
        message.body.content = showContent;
    }
    //   else if ([message.ext[@"action"] integerValue] == 2005) {
    //       HTGroup * group = [HTGroup new];
    //       group.owner = message.from;
    //       group.groupAvatar = message.ext[@"groupAvatar"];
    //       group.groupId = message.to;
    //       if ([message.ext[@"uid"] isEqualToString:self.currentUsername]) {
    //           message.body.content = [NSString stringWithFormat:@"你修改了群头像"];
    //       }else{
    //           message.body.content = [NSString stringWithFormat:@"\"%@\"修改了群头像",message.ext[@"nickName"]];
    //       }
    //       [self.groupManager updateGroupDescWithGroup:group isSender:NO];
    //   }
}

- (NSDictionary *)stringToDictionaryWithString:(NSString *)string {
    NSData *jsonData = [string dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&err];
    return dic;
}

- (void)initAllDBAbouts {
    
    // init db tables after login
    [[HTDBManager sharedInstance] createGroupTable];
    [[HTDBManager sharedInstance] createConversationTable];
    [[HTDBManager sharedInstance] createMessageTable];
    if (self.messageManager == nil) {
        _messageManager = [HTMessageManager new];
    }
    if (self.groupManager == nil) {
        _groupManager = [HTGroupManager new];
    }
    if (self.conversationManager == nil) {
        _conversationManager = [HTConversationManager new];
    }
    [self.groupManager initGroups];
    [self.conversationManager loadAllConversationsFromDBCompletion:nil];
}

- (void)clearnAllDBAbouts {
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:kXMPP_USERNAME];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:kXMPP_PASSWORD];
    [[NSUserDefaults standardUserDefaults] synchronize];
    self.hadLoadOfflineMessage = NO;
    self.isBeginTimer = NO;
    self.loginByAuto = NO;
    [self.offlineMessages removeAllObjects];
    [self.sendingContain removeAllObjects];
    _conversationManager = nil;
    _groupManager = nil;
    _messageManager = nil;
    
}

- (void)handlOfflineMessage {
    if (self.isBeginTimer == NO) {
        self.isBeginTimer = YES;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            NSArray *sortedArray = [self.offlineMessages sortedArrayUsingComparator: ^(HTMessage * obj1, HTMessage * obj2) {
                if (obj1.timestamp > obj2.timestamp) {
                    return (NSComparisonResult)NSOrderedDescending;
                }
                if (obj1.timestamp < obj2.timestamp) {
                    return (NSComparisonResult)NSOrderedAscending;
                }
                return (NSComparisonResult)NSOrderedSame;
            }];
            HTMessage *tempMessage = [sortedArray lastObject];
            [self sendGroupACKToIMServerWithTimestamp:[NSString stringWithFormat:@"%ld",[self messageTimestampFormat:[NSString stringWithFormat:@"%ld",tempMessage.timestamp]]]];
            NSEnumerator *enumerator = [sortedArray objectEnumerator];
            id obj = nil;
            while(obj = [enumerator nextObject]){
                [self handleAllMessage:obj needGroupACK:NO];
            }
            self.hadLoadOfflineMessage = YES;
        });
    }
}

#pragma mark - xmpp's private methods

- (void)dealloc {
    [self teardownStream];
}

- (void)teardownStream {
    [self.xmppStream removeDelegate:self];
    [self.xmppReconnect deactivate];
    [self.xmppStream disconnect];
    self.xmppStream = nil;
    self.xmppReconnect = nil;
}

#pragma mark - some private methods used in SDK

- (void)controlCMDMessage:(HTCmdMessage *)cmdMessage {
    NSDictionary * bodyDic = [self stringToDictionaryWithString:cmdMessage.body];
    
    [self.conversationManager deleteOneConversationWithChatterId:bodyDic[@"data"] isCleanAllHistoryMessage:YES];
    [[NSNotificationCenter defaultCenter] postNotificationName:HT_DELETE_GROUP_INFO object:self userInfo:@{@"groupId":bodyDic[@"data"],@"isSender":@NO}];
}

#pragma mark - check newwort state

/**
 *  网络监测,2G,3G,4G,WiFi
 */
- (void)checkNewworkState {
    _hostReach = [Reachability reachabilityWithHostName:@"www.baidu.com"];
    [_hostReach startNotifier];
    // 监测网络情况
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(reachabilityChanged:)
     name:kReachabilityChangedNotification
     object:nil];
}

- (void)reachabilityChanged:(NSNotification *)note {
    Reachability *curReach = [note object];
    NSParameterAssert([curReach isKindOfClass:[Reachability class]]);
    NetworkStatus status = [curReach currentReachabilityStatus];
    switch (status) {
        case NotReachable:
            self.hasTryConnectFailed = YES;
            break;
        case ReachableViaWiFi:
        {
            if (self.hasTryConnectFailed == YES && self.hasBeenConnectedBefore == NO) {
                [self.xmppStream disconnect];
                [self.offlineMessages removeAllObjects];
                [self.sendingContain removeAllObjects];
                _conversationManager = nil;
                _groupManager = nil;
                _messageManager = nil;
                [self initializeSDK];
            }
        }
            break;
        case ReachableViaWWAN:
        {
            if (self.hasTryConnectFailed == YES && self.hasBeenConnectedBefore == NO) {
                [self.xmppStream disconnect];
                [self.offlineMessages removeAllObjects];
                [self.sendingContain removeAllObjects];
                _conversationManager = nil;
                _groupManager = nil;
                _messageManager = nil;
                [self initializeSDK];
            }
        }
            break;
        default:
            break;
    }
}

#pragma mark - auto login API

- (void)checkCanAutoLogin:(NSString *)username password:(NSString *)password {
    
    NSLog(@"自动登录检查device ID");
    
    NSUserDefaults * df = [NSUserDefaults standardUserDefaults];
    NSString * uuid = [df valueForKey:@"YICHAT_UUID"];
    NSDictionary * requestDic = @{@"userId":[HTClient sharedInstance].currentUsername};
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0); //创建信号量
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@/api/getDeviceId.php",BUSINESS_HOST]]];
    
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[QSNetworkTool serializeParameters:requestDic] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setTimeoutInterval:3];
    QSNetworkTool * netWork = [QSNetworkTool new];
    netWork.isSemaphoreSignal = YES;
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *responseDicI, NSData *data) {
        if ([responseDicI[@"code"] integerValue] == 1) {
            if (![uuid isEqualToString:responseDicI[@"deviceId"]]) {
                [self setAutoLogin:NO];
                if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(userAccountDidLoginFromOtherDevice)]) {
                    [self.clientDelegate userAccountDidLoginFromOtherDevice];
                }
            }
        }else{
            [self setAutoLogin:NO];
            if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(userAccountDidLoginFromOtherDevice)]) {
                [self.clientDelegate userAccountDidLoginFromOtherDevice];
            }
        }
        dispatch_semaphore_signal(semaphore);   //发送信号
        
    } failure:^(NSError *error) {
        dispatch_semaphore_signal(semaphore);   //发送信号
    }];
    
    dispatch_semaphore_wait(semaphore,DISPATCH_TIME_FOREVER);  //等待
    NSLog(@"intial sdk judge auto login %@",[NSThread currentThread]);
    
    if (self.autoLogin) {
        
        _currentUsername = username;
        self.loginByAuto = YES;
        
        [self loginWithUsername:username password:password completion:^(BOOL result) {
            
            NSLog(@"xmpp 自动登录 reslut %@",[NSThread currentThread]);
            if (result == NO) {
                if (self.clientDelegate && [self.clientDelegate hasDelegateThatRespondsToSelector:@selector(didAutoLoginWithFailed:)]) {
                    [self.clientDelegate didAutoLoginWithFailed:YES];
                }
                [self clearnAllDBAbouts];
            }
        }];
        
        
       
    }
}

- (void)uploadNewDeviceId {
    NSUserDefaults * df = [NSUserDefaults standardUserDefaults];
    NSString * uuid = [df valueForKey:@"YICHAT_UUID"];
    
    NSDictionary * requestDic = @{@"userId":[HTClient sharedInstance].currentUsername,@"deviceId":uuid};
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@/api/updateDeviceId.php",BUSINESS_HOST]]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[QSNetworkTool serializeParameters:requestDic] dataUsingEncoding:NSUTF8StringEncoding]];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *responseDicI, NSData *data) {
        
    } failure:^(NSError *error) {
        
    }];
}

#pragma mark - backgroup task

UIBackgroundTaskIdentifier bgTask;

- (void)backgroudEvent {
    
    UIApplication * application = [UIApplication sharedApplication];
    
    bgTask = [application beginBackgroundTaskWithExpirationHandler:^{
        // 10分钟后执行这里，应该进行一些清理工作，如断开和服务器的连接等
        // ...
        // stopped or ending the task outright.
        [application endBackgroundTask:bgTask];
        bgTask = UIBackgroundTaskInvalid;
    }];
    if (bgTask == UIBackgroundTaskInvalid) {
        NSLog(@"failed to start background task!");
    }
    // Start the long-running task and return immediately.
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // Do the work associated with the task, preferably in chunks.
        __block NSTimeInterval timeRemain = 0;
        do{
            [NSThread sleepForTimeInterval:5];
            if (bgTask!= UIBackgroundTaskInvalid) {
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    timeRemain = [application backgroundTimeRemaining];
                    NSLog(@"Time remaining: %f",timeRemain);
                });
                
            }
        }while(bgTask!= UIBackgroundTaskInvalid && timeRemain > 0);
        // 如果改为timeRemain > 5*60,表示后台运行5分钟
        // done!
        // 如果没到10分钟，也可以主动关闭后台任务，但这需要在主线程中执行，否则会出错
        dispatch_async(dispatch_get_main_queue(), ^{
            if (bgTask != UIBackgroundTaskInvalid){
                // 和上面10分钟后执行的代码一样
                // ...
                // if you don't call endBackgroundTask, the OS will exit your app.
                [application endBackgroundTask:bgTask];
                bgTask = UIBackgroundTaskInvalid;
            }
        });
    });
}

- (void)foregroudEvent {
    UIApplication * application = [UIApplication sharedApplication];
    // 如果没到10分钟又打开了app,结束后台任务
    if (bgTask!=UIBackgroundTaskInvalid) {
        [application endBackgroundTask:bgTask];
        bgTask = UIBackgroundTaskInvalid;
    }
}

@end
