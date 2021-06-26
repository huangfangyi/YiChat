//
//  ZFChatManage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatManage.h"
#import "ZFRequestManage.h"
#import "ZFChatGlobal.h"
#import "ZFChatRequestHelper.h"
#import "ZFChatHelper.h"
#import "NSString+URLEncoding.h"
#import "ProjectTranslateHelper.h"
#import "HTDeviceManager.h"
#import "YiChatGroupZhenView.h"
#import "KLCPopup.h"
#import "YiChatShowZhenView.h"
#import "ZFChatUIHelper.h"
#import "ZFChatMessageHelper.h"
#import "HTMessageDefines.h"
#import "YiChatStorageManager.h"
#import "ZFChatVC.h"

static const CGFloat kDefaultPlaySoundInterval = 0.5;
static ZFChatManage *manage = nil;

@interface ZFChatManage ()<HTClientDelegate,HTConversationDelegate,HTMessageDelegate>{
    AVAudioPlayer *_ringPlayer;
}

@property (nonatomic,weak) HTGroupManager *groupManager;

@property (nonatomic,strong) ZFChatConfigureEntity *configureEntity;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_commomMsgReceive;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_cmdMsgReceive;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_receiveMSGTime;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_conversationChange;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_friendNotify;
//语音视频通话
@property (nonatomic,strong) ZFChatNotifyEntity *notify_voiceVideoCall;
//朋友圈消息提醒
@property (nonatomic,strong) ZFChatNotifyEntity *notify_dynamicMSG;
//群组信息更新提醒
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_infoChange;
//群组列表变更提醒
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_listChange;
//群组语音视频通话
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_voiceVideoCall;
//群禁言通知
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_ShutUp;
//群解除禁言通知
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_restShutUp;
//群设置管理员
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_setManager;
//群取消设置管理员d
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_cancelSetManager;
//加群申请
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_addApply;
//群解散
@property (nonatomic,strong) ZFChatNotifyEntity *notify_group_delete;
//静音通知
@property (nonatomic,strong) ZFChatNotifyEntity *notify_system_quite;
//app退入后台
@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeBackground;
//app进入前台活跃
@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeActive;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_changeAppBradge;

@property (nonatomic,strong) dispatch_semaphore_t receiveCommonMsgLock;

@property (nonatomic,strong) dispatch_semaphore_t receiveCMDMsgLock;

@property (nonatomic,strong) dispatch_semaphore_t receiveMsgTimeLock;

@property (nonatomic,strong) NSMutableDictionary *groupMessageLoadStateDic;

@property (nonatomic,strong) KLCPopup *popView;

// 上一次响铃
@property (strong, nonatomic) NSDate *lastPlaySoundDate;
    
@property (nonatomic,strong) dispatch_semaphore_t sendMessageLock;
@property (nonatomic,strong) dispatch_semaphore_t uploadmessagelock;
    
@property (nonatomic,strong) NSOperationQueue *sendMessageQueue;
@property (nonatomic,strong) dispatch_queue_t receiveCmdMessageQueue;
@property (nonatomic,strong) NSOperationQueue *receiveMsgTimeMessageQueue;

@end

@implementation ZFChatManage

+ (id)defaultManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manage = [[self alloc] init];
        manage.receiveCommonMsgLock = dispatch_semaphore_create(1);
        manage.receiveCMDMsgLock = dispatch_semaphore_create(1);
        manage.receiveMsgTimeLock = dispatch_semaphore_create(2);
        
        manage.sendMessageLock = dispatch_semaphore_create(1);
        manage.uploadmessagelock = dispatch_semaphore_create(1);
        
        manage.sendMessageQueue = [[NSOperationQueue alloc] init];
        manage.sendMessageQueue.maxConcurrentOperationCount = 2;
        
        manage.receiveCmdMessageQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        
        manage.receiveMsgTimeMessageQueue = [[NSOperationQueue alloc] init];
        manage.receiveMsgTimeMessageQueue.maxConcurrentOperationCount = 2;
    });
    return manage;
}

- (HTGroupManager *)getGroupManager{
    return self.groupManager;
}

- (void)configureChatWithEntity:(ZFChatConfigureEntity *)entity  delegate:(id<ZFChatManageDelegate>)delagte{
    
    if([entity isKindOfClass:[ZFChatConfigureEntity class]]){
        _configureEntity = entity;
    }
    [self htsdkInitial];
    
    [self addHTClientDelegate];
    
    [self addDelegate:delagte];
    
    [self initialGroupChatListState];
}

- (void)initialGroupChatListState{
    self.groupMessageLoadStateDic = [NSMutableDictionary dictionaryWithCapacity:0];
}

- (void)needUpdateGroupChatListState:(NSString *)groupId state:(BOOL)state{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        if(state){
            [self.groupMessageLoadStateDic setObject:@"1" forKey:groupId];
        }
        else{
            [self.groupMessageLoadStateDic removeObjectForKey:groupId];
        }
    }
}

- (BOOL)getGroupChatListState:(NSString *)groupId{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        NSString *state = [self.groupMessageLoadStateDic objectForKey:groupId];
        if(state && [state isKindOfClass:[NSString class]]){
            if(state.length > 0){
                return [state boolValue];
            }
        }
    }
    return NO;
}

- (void)removeAllGroupChatListState{
    [self.groupMessageLoadStateDic removeAllObjects];
}

- (void)autoLogin:(void(^)(BOOL isSuccess))invocation{
    
    BOOL autoLogin = NO;
    autoLogin = [HTClient sharedInstance].autoLogin;
    if(autoLogin){
        invocation(YES);
    }
    else{
        invocation(NO);
    }
}


// xmpp物理连接 self.xmppStream connectWithTimeout
- (void)loginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion{
    
    [[ZFRequestManage defaultManager] zfRequuest_LoginWithUserName:userName password:password completion:^(BOOL success, NSString * _Nonnull des) {
      
        [ProjectHelper helper_getMainThread:^{
            if(success){
                
                BOOL isAutoLogin = NO;
                
                if(self.configureEntity.isAutoLogin){
                    isAutoLogin = self.configureEntity.isAutoLogin;
                    [[HTClient sharedInstance] setAutoLogin:isAutoLogin];
                }
                
                [self chatLoginWithState:YES];
            }
            else{
                [self chatLoginWithState:NO];
            }
            if(completion){
                completion(success,des);
            }
        }];
    }];
}

/*
 XMPPPresence *presence = [XMPPPresence presenceWithType:@"unavailable"];
 [self.xmppStream sendElement:presence];
 [self.xmppStream disconnect];
 [self clearnAllDBAbouts];
*/
- (void)logout{
    HTClient *client = [HTClient sharedInstance];
    client.autoLogin = NO;
    [client logout];
}

- (void)loginSuccessDeal{
    
    [self addNotifies];
    [self addMessageConversationDelegate];
    [self addConversationManagerDelegate];
    [self addGroupManagerDelegate];
    
}

- (void)addDelegate:(id<ZFChatManageDelegate>)delagte{
    if(delagte){
        _zfChatDelegate = delagte;
    }
}

- (void)chatConfigureClean{
    [self removeNotifycations];
    [self removeMessageManagerDelagte];
    [self removeConversationManagerDelegate];
    [self removeGroupManagerDelegate];
    _groupManager = nil;
}

- (void)chatLoginWithState:(BOOL)state{
    if(state){
        [self loginSuccessDeal];
    }
    else{
        [self chatConfigureClean];
    }
    
}

- (void)addNotifies{
    
    _notify_conversationChange = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleConversationChanged];
    
    _notify_commomMsgReceive = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleReceiveCommonMsg];
    
    _notify_receiveMSGTime = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleReceiveMSGTime];
    
    _notify_cmdMsgReceive = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleReceiveCMDMsg];
    
    _notify_voiceVideoCall = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupVoiceVideoCall];
    
    _notify_dynamicMSG = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleDynamicMSG];
    
    _notify_group_voiceVideoCall = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupVoiceVideoCall];
    
    _notify_group_ShutUp = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupShutUp];
    
    _notify_group_restShutUp= [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupRestShutUp];
    
    _notify_group_infoChange = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupInfoUpdate];
    
    _notify_group_listChange = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupListUpdate];
    
    _notify_group_voiceVideoCall = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupVoiceVideoCall];
    
    _notify_group_setManager = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupSetManager];
    
    _notify_group_cancelSetManager = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupCancelSetManager];
    
    _notify_group_addApply = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupAddApply];
    
    _notify_group_delete = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleGroupDelete superNotifyName:HT_DELETE_GROUP_INFO target:self sel:@selector(groupDeleted:)];
    [_notify_group_delete addSuperNotify];
    
    _notify_system_quite = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleSystemQuite target:self sel:@selector(chatSystemQuite:)];
    
    
    _notify_app_becomeActive = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleAppBecomeActive superNotifyName:UIApplicationDidBecomeActiveNotification target:self sel:@selector(appBecomeActive:)];
    [_notify_app_becomeActive addSuperNotify];
    
    _notify_app_becomeBackground = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleAppBecomeActive superNotifyName:UIApplicationDidEnterBackgroundNotification target:self sel:@selector(appEnterBackground:)];
     [_notify_app_becomeBackground addSuperNotify];
    
    _notify_app_changeAppBradge = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleChangeBradgeNum];
    
    _notify_friendNotify = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleFriendNotify];
}

- (void)removeNotifycations{
    [self notifyEntityRemove:_notify_group_delete];
    [self notifyEntityRemove:_notify_conversationChange];
    [self notifyEntityRemove:_notify_commomMsgReceive];
    [self notifyEntityRemove:_notify_cmdMsgReceive];
    [self notifyEntityRemove:_notify_receiveMSGTime];
    [self notifyEntityRemove:_notify_voiceVideoCall];
    [self notifyEntityRemove:_notify_dynamicMSG];
    [self notifyEntityRemove:_notify_group_voiceVideoCall];
    [self notifyEntityRemove:_notify_group_ShutUp];
    [self notifyEntityRemove:_notify_group_restShutUp];
    [self notifyEntityRemove:_notify_group_voiceVideoCall];
    [self notifyEntityRemove:_notify_group_setManager];
    [self notifyEntityRemove:_notify_group_cancelSetManager];
    [self notifyEntityRemove:_notify_group_addApply];
    [self notifyEntityRemove:_notify_system_quite];
    [self notifyEntityRemove:_notify_app_becomeActive];
    [self notifyEntityRemove:_notify_app_becomeBackground];
    [self notifyEntityRemove:_notify_group_listChange];
    [self notifyEntityRemove:_notify_group_infoChange];
    [self notifyEntityRemove:_notify_app_changeAppBradge];
    [self notifyEntityRemove:_notify_friendNotify];
    
}

- (void)notifyEntityRemove:(ZFChatNotifyEntity *)entity{
    if([entity isKindOfClass:[ZFChatNotifyEntity class]] && entity){
        [entity removeMotify];
        if(entity.style == ZFChatNotifyStyleAppBecomeActive || entity.style == ZFChatNotifyStyleAppBecomeBackground || entity.style == ZFChatNotifyStyleGroupDelete){
            [entity removeSuperNotify];
        }
        entity = nil;
    }
}
    
- (void)groupDeleted:(NSNotification *)notify{
    
    id obj = notify.userInfo;
    
    ////[[NSNotificationCenter defaultCenter] postNotificationName:HT_DELETE_GROUP_INFO object:self userInfo:@{@"groupId":messageDic[@"data"][@"gid"],@"isSender":@NO}];
    if(obj && [obj isKindOfClass:[NSDictionary class]]){
        [_notify_group_delete postNotifyWithContent:obj];
    }
    
    
    
}

- (void)chatSystemQuite:(NSNotification *)notify{
    
}

- (void)appBecomeActive:(NSNotification *)notify{
    if(_notify_app_becomeActive){
        [_notify_app_becomeActive postNotifyWithContent:nil];
        
        YiChatUserManager *user = [YiChatUserManager defaultManagaer];
        [user yichatUserClient_recordAllChatObjctUpdateChatListWithState:YiChatUpdateChatlistStateNeedUpdate];
    }
}

- (void)appEnterBackground:(NSNotification *)notify{
    if(_notify_app_becomeBackground){
        [_notify_app_becomeBackground postNotifyWithContent:nil];
    }
}

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style{
    return [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:style];
}

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style target:(id)target sel:(SEL)selector{
    return [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:style target:target sel:selector];
}

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style superNotifyName:(NSString *)superNotifyName target:(id)target sel:(SEL)selector{
    return [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:style superNotifyName:superNotifyName target:target sel:selector];
}

- (HTMessage *)translateRequestHttpDataToHTMessage:(NSDictionary *)dic{
    
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSString *dataString = dic[@"content"];
        
        if (![dataString isKindOfClass:[NSString class]]) {
            dataString = @"";
        }
        NSString *msgString = nil;
        if(dataString && [dataString isKindOfClass:[NSString class]]){
            msgString = [dataString specialURLDecodedString];
        }
        
        NSDictionary *msgDic = nil;
        if(msgString && [msgString isKindOfClass:[NSString class]]){
            msgDic = [YRGeneralApis yrGeneralApis_dictionaryWithJsonString:msgString];
        }
        
        if(!msgDic){
            msgDic = [YRGeneralApis yrGeneralApis_dictionaryWithJsonString:dataString];
        }
        if(msgDic && [msgDic isKindOfClass:[NSDictionary class]]){
             msgDic = msgDic[@"data"];
        }
        
        NSLog(@"%@\n%@",msgDic,msgString);
        if(msgDic && [msgDic isKindOfClass:[NSDictionary class]]){
            HTMessage *msg = [[HTMessage alloc] init];
            
            if ([msgDic[@"timestamp"] isKindOfClass:[NSNumber class]] && msgDic[@"timestamp"]) {
                msg.timestamp = [msgDic[@"timestamp"] integerValue];
            }
            if(msg.timestamp == 0){
                msg.timestamp = [dic[@"time"] integerValue];
            }
            
            if(msgDic[@"from"] && [msgDic[@"from"] isKindOfClass:[NSString class]]){
                msg.from = msgDic[@"from"];
            }
            msg.chatType = @"2";
            if(msgDic[@"msgId"] && [msgDic[@"msgId"] isKindOfClass:[NSString class]]){
                msg.msgId = msgDic[@"msgId"];
            }
            if(msgDic[@"to"] && [msgDic[@"to"] isKindOfClass:[NSString class]]){
                msg.to = msgDic[@"to"];
            }
            if(msgDic[@"type"] && [msgDic[@"type"] isKindOfClass:[NSNumber class]]){
                msg.type = [msgDic[@"type"] integerValue];
            }
            if(msgDic[@"msgType"] && [msgDic[@"msgType"] isKindOfClass:[NSNumber class]]){
                msg.msgType = [msgDic[@"msgType"] integerValue];
            }
            if(msgDic[@"downLoadState"] && [msgDic[@"downLoadState"] isKindOfClass:[NSNumber class]]){
                msg.downLoadState = [msgDic[@"downLoadState"] integerValue];
            }
            msg.body = [HTMessageBody new];
            NSDictionary *body = msgDic[@"body"];
            if(body && [body isKindOfClass:[NSDictionary class]]){
                if(body[@"content"] && [body[@"content"] isKindOfClass:[NSString class]]){
                    msg.body.content = body[@"content"];
                }
                
                NSString *userId = [ZFChatHelper zfChatHelper_getCurrentUser];
                NSString *msgDicFrom = msgDic[@"from"];
                if(userId && [userId isKindOfClass:[NSString class]] && msgDicFrom && [msgDicFrom isKindOfClass:[NSString class]]){
                    msg.isSender = [msgDicFrom isEqualToString:userId] ? YES : NO;
                }
                else{
                    msg.isSender = NO;
                }
                
                msg.sendState = SendStateSuccessed;
                //Text,Image,Audio,Video,Position,File
                NSString *msgType = nil;
                NSString *type = nil;
                
                if(msgDic[@"msgType"] && [msgDic[@"msgType"] isKindOfClass:[NSNumber class]]){
                     type = msgDic[@"msgType"];
                }
                
                if(type && [type isKindOfClass:[NSString class]]){
                    if ([type integerValue] == 2001) {
                        msgType = @"Text";
                    }else if ([type integerValue] == 2002){
                        msgType = @"Image";
                    }else if ([type integerValue] == 2003){
                        msgType = @"Audio";
                    }else if ([type integerValue] == 2004){
                        msgType = @"Video";
                    }else if ([type integerValue] == 2005){
                        msgType = @"Position";
                    }else if ([type integerValue] == 2006){
                        msgType = @"File";
                    }
                    msg.body.messageType = msgType;
                }
                
                if(body[@"audioDuration"] && [body[@"audioDuration"] isKindOfClass:[NSString class]]){
                    if([body[@"audioDuration"] length] > 0){
                        msg.body.audioDuration = body[@"audioDuration"];
                    }
                    else{
                        msg.body.audioDuration = @"";
                    }
                }
                else if(body[@"audioDuration"] && [body[@"audioDuration"] isKindOfClass:[NSNumber class]]){
                    msg.body.audioDuration = [NSString stringWithFormat:@"%ld",[body[@"audioDuration"] integerValue]];
                }
                else{
                    msg.body.audioDuration = @"";
                }
                
                if(body[@"videoDuration"] && [body[@"videoDuration"] isKindOfClass:[NSNumber class]]){
                    msg.body.videoDuration = [body[@"videoDuration"] floatValue];
                }
                else if(body[@"videoDuration"] && [body[@"videoDuration"] isKindOfClass:[NSNumber class]]){
                    msg.body.audioDuration = [NSString stringWithFormat:@"%ld",[body[@"videoDuration"] integerValue]];
                }
                else{
                    msg.body.videoDuration = 0;
                }
                
                if(body[@"fileName"] && [body[@"fileName"] isKindOfClass:[NSString class]]){
                    if([body[@"fileName"] length] > 0){
                        msg.body.fileName = body[@"fileName"];
                    }
                    else{
                        msg.body.fileName = @"";
                    }
                }
                else{
                    msg.body.fileName = @"";
                }
                
                if(body[@"thumbnailRemotePath"] && [body[@"thumbnailRemotePath"] isKindOfClass:[NSString class]]){
                    msg.body.thumbnailRemotePath = body[@"thumbnailRemotePath"];
                }
                
                if(body[@"size"] && [body[@"size"] isKindOfClass:[NSString class]]){
                    msg.body.size = body[@"size"];
                }
                
                if(body[@"latitude"] && [body[@"latitude"] isKindOfClass:[NSNumber class]]){
                    msg.body.latitude = [[NSString stringWithFormat:@"%@",body[@"latitude"]] floatValue];
                }
                else{
                    msg.body.latitude = 0;
                }
                
                if(body[@"longitude"] && [body[@"longitude"] isKindOfClass:[NSNumber class]]){
                    msg.body.longitude = [[NSString stringWithFormat:@"%@",body[@"longitude"]] floatValue];
                }
                else{
                    msg.body.longitude = 0;
                }
                
                if(body[@"localPath"] && [body[@"localPath"] isKindOfClass:[NSString class]]){
                    msg.body.localPath = body[@"localPath"];
                }
                else{
                    msg.body.localPath = @"";
                }
                
                if(body[@"remotePath"] && [body[@"remotePath"] isKindOfClass:[NSString class]]){
                    msg.body.remotePath = body[@"remotePath"];
                }
                else{
                    msg.body.remotePath = @"";
                }
                
            }
            
            if(msgDic[@"ext"] && [msgDic[@"ext"] isKindOfClass:[NSDictionary class]]){
                msg.ext = msgDic[@"ext"];
            }
            
            return msg;
        }
    }
    return nil;
    
}

- (NSString *)textFromBase64String:(NSString *)base64 {
    
    NSData *data = [[NSData alloc] initWithBase64EncodedString:base64 options:0];
    NSString *text = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    return text;
}

- (void)updateUnreadMessage{
    [self setupUnreadMessage];
}

- (void)setupUnreadMessage {
    NSInteger  unreadCount = 0;
    for (HTConversation * model  in [HTClient sharedInstance].conversationManager.conversations) {
        if(model.unreadMessageCount >= 1){
             unreadCount += (model.unreadMessageCount - 1);
        }
    }
    
    [_notify_app_changeAppBradge postNotifyWithContent:@{@"tabNum":Project_TabIdengtify_Conversation,@"num":[NSString stringWithFormat:@"%ld",unreadCount]}];
    
    [ProjectHelper helper_getMainThread:^{
       [UIApplication sharedApplication].applicationIconBadgeNumber = unreadCount;
    }];
}

- (void)cleanConnectionMessage{
    [_notify_app_changeAppBradge postNotifyWithContent:@{@"tabNum":Project_TabIdengtify_Connection,@"num":[NSString stringWithFormat:@"%ld",0]}];
}

- (void)addMessageConversationDelegate{
    [self addMessageManagerDelegate];
    [self addConversationManagerDelegate];
}

//- (void)soundAndVibrationWithMessage:(HTMessage *)message{
//#if !TARGET_IPHONE_SIMULATOR
//    UIApplicationState state =
//    [[UIApplication sharedApplication] applicationState];
//    switch (state) {
//        case UIApplicationStateActive:
//            [self playSoundAndVibration];
//            break;
//        case UIApplicationStateInactive:
//            [self playSoundAndVibration];
//            break;
//        case UIApplicationStateBackground:
//            [HTLocalPushManager showNotificationWithMessage:message];
//            break;
//        default:
//            break;
//    }
//#endif
//}


//- (void)playSoundAndVibration{
//    NSTimeInterval timeInterval = [[NSDate date]
//                                   timeIntervalSinceDate:self.lastPlaySoundDate];
//    if (timeInterval < kDefaultPlaySoundInterval) {
//        //如果距离上次响铃和震动时间太短, 则跳过响铃
//        NSLog(@"skip ringing & vibration %@, %@", [NSDate date], self.lastPlaySoundDate);
//        return;
//    }
//
//    //保存最后一次响铃时间
//    self.lastPlaySoundDate = [NSDate date];
//    if (!self.isMute) {
//        // 收到消息时，播放音频
//        [[HTDeviceManager sharedInstance] playNewMessageSound];
//        //收到消息时，震动
//        [[HTDeviceManager sharedInstance] playVibration];
//    }
//}

#pragma mark HTSDK intial

- (void)htsdkInitial{
   
    HTClient *client = [HTClient sharedInstance];
    [client initializeImIp:YiChatProject_NetWork_XMPPIP AndBusinessIp:YiChatProject_NetWork_BaseUrl];
    [client initializeOSSWithOSSAccessKey:YiChatProject_NetWork_OSSAccessKey AndOSSSecretKey:YiChatProject_NetWork_OSSSecretKey AndOSSEndPoint:YiChatProject_NetWork_OSSEndPoint AndOSSBucket:YiChatProject_NetWork_OSSBucket AndChatFileHost:YiChatProject_NetWork_ChatFileHost];
    [client initializeSDK];
    
    
    client.htClientGetUserInfo = ^NSDictionary *{
        
        __block NSMutableDictionary *userInfo = [NSMutableDictionary dictionaryWithCapacity:0];
        __block dispatch_semaphore_t lock = dispatch_semaphore_create(0);
        
        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                NSDictionary *user = [ProjectBaseModel translateObjPropertyToDic:model];
                
                if(user && [user isKindOfClass:[NSDictionary class]]){
                     [userInfo addEntriesFromDictionary:user];
                }
                [userInfo setValue:YiChatUserInfo_Token forKey:@"token"];
                
                dispatch_semaphore_signal(lock);
            }
        }];
        
        dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
        
        return userInfo;
    };
    
    //initializeSDK中 检查是否可以自动登录，
    /*
     
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
     
     */
}

#pragma mark
#pragma mark HTCient delegate
#pragma mark

- (void)addHTClientDelegate{
    [[HTClient sharedInstance] addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)removeHTClientDelegate{
    [[HTClient sharedInstance] removeDelegate:self];
}

/**
 ~cn:自动登陆失败的回调 ~en:a callback when automatic login failed
 
 @param aFaile ~cn:是否失败，默认为NO ~en:failed or not, default is NO
 */
- (void)didAutoLoginWithFailed:(BOOL)aFaile{
    
    [ProjectHelper helper_getGlobalThread:^{
        if(aFaile){
           // [self chatLoginWithState:NO];
            
            if(self.zfChatDelegate){
                [self.zfChatDelegate zfChatManageDelegate_AutoLoginIsFail:YES];
            }
        }
    }];
}

/**
 ~cn:HTClient登陆成功后连接服务器的状态变化时会接收到该回调 ~en:a callback after has logged
 
 @param aConnectionState ~cn:当前的状态 ~en:current status
 */
- (void)connectionStateDidChange:(HTConnectionState)aConnectionState{
    [ProjectHelper helper_getGlobalThread:^{
        
        if(aConnectionState == HTConnectionStateConnected){
            YiChatUserManager *user = [YiChatUserManager defaultManagaer];
            [user yichatUserClient_recordAllChatObjctUpdateChatListWithState:YiChatUpdateChatlistStateNeedUpdate];
        }
        
        [ZFChatNotify postNotifyWithStyle:ZFChatNotifyStyleXMPPConnectionState content:[NSNumber numberWithInteger:aConnectionState]];
    }];
}

/**
 ~cn:当前登录账号在其它设备登录时会接收到此回调 ~en:a callback when the same account logged in anohter device
 */
- (void)userAccountDidLoginFromOtherDevice{
    if(self.zfChatDelegate){
        [self.zfChatDelegate zfChatManageDelegate_DidLoginFromOtherDevice];
    }
}

- (void)accountDidAutoLoginSuccess{
    
    [ProjectHelper helper_getGlobalThread:^{
        [self loginSuccessDeal];
        
        [self setupUnreadMessage];
        
        [ZFChatNotify postNotifyWithStyle:ZFChatNotifyStyleXMPPConnectionState content:[NSNumber numberWithInteger:3]];
        
        if(self.zfChatDelegate){
            [self.zfChatDelegate zfChatManageDelegate_DidAutoLoginSuccess];
        }
    }];
}

#pragma mark
#pragma mark messageManager delegate
#pragma mark

- (void)addMessageManagerDelegate{
  
    [[HTClient sharedInstance].messageManager removeDelegate:self];
    
    [[HTClient sharedInstance].messageManager addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)removeMessageManagerDelagte{
    [[HTClient sharedInstance].messageManager removeDelegate:self];
}

- (void)soundAndVibrationWithMessage:(id)message{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        if ([[NSUserDefaults standardUserDefaults] stringForKey:PROJECT_GLOBALNODISTURB]) {
            NSString *globalNoDisturb = [[NSUserDefaults standardUserDefaults] stringForKey:PROJECT_GLOBALNODISTURB];
            if ([globalNoDisturb isEqualToString:@"1"]) {
                return;
            }
        }
        NSString *from = @"";
        NSString *to = @"";
        NSString *chatType = @"";
        if(message && [message isKindOfClass:[HTMessage class]]){
            HTMessage *tmp = message;
            from = tmp.from;
            to = tmp.to;
            chatType = tmp.chatType;
        }
        else if(message && [message isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *tmp = message;
            from = tmp.from;
            to = tmp.to;
            chatType = tmp.chatType;
        }
        
        NSString *chatId = nil;
        if([chatType isEqualToString:@"1"]){
            chatId = from;
        }
        else if([chatType isEqualToString:@"2"]){
            chatId = to;
        }
        
        [[YiChatUserManager defaultManagaer] getMessageShutUpStateWithChatId:chatId invocation:^(NSString * _Nonnull state) {
            
            if(state && [state isKindOfClass:[NSString class]]){
                BOOL shutState = [state boolValue];
                if(shutState == YES){
                    return ;
                }
                
                [ProjectHelper helper_getMainThread:^{
#if !TARGET_IPHONE_SIMULATOR
                    UIApplicationState state = [[UIApplication sharedApplication] applicationState];
                    switch (state) {
                        case UIApplicationStateActive:
                            [self playSoundAndVibration];
                            break;
                        case UIApplicationStateInactive:
                            [self playSoundAndVibration];
                            //            break;
                            
                        case UIApplicationStateBackground:
                            //            [HTLocalPushManager showNotificationWithMessage:message];
                            break;
                        default:
                            break;
                    }
#endif
                }];

            }
        }];
        
    });

}

- (void)playSoundAndVibration{
    NSTimeInterval timeInterval = [[NSDate date]
                                   timeIntervalSinceDate:self.lastPlaySoundDate];
    if (timeInterval < kDefaultPlaySoundInterval) {
        //如果距离上次响铃和震动时间太短, 则跳过响
        return;
    }
    
    //保存最后一次响铃时间
    self.lastPlaySoundDate = [NSDate date];
//    if (!self.isMute) {
        // 收到消息时，播放音频
        [[HTDeviceManager sharedInstance] playNewMessageSound];
        //收到消息时，震动
        [[HTDeviceManager sharedInstance] playVibration];
//    }
}
/**
 接收到普通消息
 
 @param aMessages 普通消息数组
 */
- (void)didReceiveMessages:(NSArray *)aMessages{
    //消息拆分好过后 再post
    [self.receiveMsgTimeMessageQueue addOperationWithBlock:^{
        
        if(aMessages && [aMessages isKindOfClass:[NSArray class]]){
            
            NSMutableArray *messageArr = [NSMutableArray arrayWithCapacity:0];
            
            for (HTMessage *msg in aMessages) {
                
                if(msg && [msg isKindOfClass:[HTMessage class]]){
                    
                    if(msg.from && [msg.from isKindOfClass:[NSString class]]){
                        if([msg.from isEqualToString:YiChatUserInfo_UserIdStr]){
                            return;
                        }
                    }
                    
                    [ZFChatRequestHelper zfRequestUpdateUnixTimeWithTime:msg.timestamp];
                    
                    NSArray * tempConversations = [HTClient sharedInstance].conversationManager.conversations.copy;
                    
                    for (HTConversation *coversation in tempConversations) {
                        if(coversation && [coversation isKindOfClass:[HTConversation class]]){
                            
                            if([coversation.lastMessage.msgId isEqualToString:msg.msgId]){
                                
                                if(coversation.unreadMessageCount >= 1){
                                    coversation.unreadMessageCount ++;
                                }
                                else if(coversation.unreadMessageCount == 0){
                                    coversation.unreadMessageCount = 2;
                                }
                                else{
                                    coversation.unreadMessageCount = 1;
                                }
                            }
                        }
                    }
                    
                    
                    
                    if(msg.chatType && [msg.chatType isKindOfClass:[NSString class]]){
                        if([msg.chatType isEqualToString:@"2"]){
                            if(msg.from && [msg.from isKindOfClass:[NSString class]]){
                                NSString *userId = [ZFChatHelper zfChatHelper_getCurrentUser];
                                if(userId && [userId isKindOfClass:[NSString class]]){
                                    if(![userId isEqualToString:msg.from]){
                                        
                                        if(msg.msgType == 2001){
                                            
                                            if(msg.ext && [msg.ext isKindOfClass:[NSDictionary class]]){
                                                NSString *alert = msg.ext[@"atUser"];
                                                if(alert && [alert isKindOfClass:[NSString class]]){
                                                    if(alert.length > 0){
                                                        if([alert rangeOfString:userId].location != NSNotFound){
                                                             [[YiChatStorageManager sharedManager] storageMessageAlert:msg withKey:msg.to];
                                                        }
                                                        // @"atUser" : @"all"
                                                        else if([alert rangeOfString:@"all"].location != NSNotFound){
                                                            [[YiChatStorageManager sharedManager] storageMessageAlert:msg withKey:msg.to];
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                               
                            }
                            
                        }
                    }
                    
                    
                    [self soundAndVibrationWithMessage:msg];
                    [self setupUnreadMessage];
                    [self conversationChanged];
                    
                    NSDictionary *postEntity = [self buildPostMessageEntityWithMsgType:msg.body.messageType chatType:msg.chatType msg:msg];
                    
                    if(postEntity != nil && [postEntity isKindOfClass:[NSDictionary class]]){
                        [messageArr addObject:postEntity];
                    }
                }
            }
            
            if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
                if(messageArr.count != 0){
                    [self.notify_commomMsgReceive postNotifyWithContent:messageArr];
                }
            }
            
            
        }
    }];
}

/**
 接收到透传消息
 
 @param aCMDMessages 透传消息数组
 * action == 2001 群资料变更
 
 */
- (void)didReceiveCMDMessage:(NSArray *)aCMDMessages{
    //消息拆分好过后 再post
    if(aCMDMessages && [aCMDMessages isKindOfClass:[NSArray class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.receiveCMDMsgLock, DISPATCH_TIME_FOREVER);
            
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0; i < aCMDMessages.count; i ++) {
                HTCmdMessage *msg = aCMDMessages[i];
                if(msg && [msg isKindOfClass:[HTCmdMessage class]]){
                    
                    NSInteger action = [self getCMDMessageAction:msg];
                    ZFMessageType type = [self getMessageTypeWithAction:action];
                    NSDictionary *dic = [self getCmdMessageExtData:msg];
                    
                    [ZFChatRequestHelper zfRequestUpdateUnixTimeWithTime:msg.timestamp];
                    
                    if(type != ZFMessageTypeWithdrawn){
                        [self soundAndVibrationWithMessage:msg];
                    }
                    
                    if(type == ZFMessageTypeWithdrawn){
                        NSDictionary *body = [ZFChatHelper getCMDMessageBody:msg];
                        if(body && [body isKindOfClass:[NSDictionary class]]){
                            
                            NSString *msgid = body[@"msgId"];
                            NSString *opid = body[@"opId"];
                            NSString *opNick = body[@"opNick"];
                            NSString *from = msg.from;
                            NSString *to = msg.to;
                            NSString *chatType = msg.chatType;

                            NSString *currentUser = [ZFChatHelper zfChatHelper_getCurrentUser];
                            
                            NSString *chatId = @"";
                            
                            if(msgid && [msgid isKindOfClass:[NSString class]] && opid && [opid isKindOfClass:[NSString class]] && opNick && [opNick isKindOfClass:[NSString class]] && from && [from isKindOfClass:[NSString class]] && to && [to isKindOfClass:[NSString class]] && chatType && [chatType isKindOfClass:[NSString class]] && currentUser && [currentUser isKindOfClass:[NSString class]]){
                                
                                if([chatType isEqualToString:@"1"]){
                                    if([from isEqualToString:currentUser]){
                                        chatId = to;
                                    }
                                    else{
                                        chatId = from;
                                    }
                                }
                                else if([chatType isEqualToString:@"2"]){
                                    chatId = to;
                                }
                                
                                [ZFChatHelper zfChatHelper_getLocalMsgWithMsgid:msgid invocation:^(HTMessage * _Nonnull msg) {
                                    if(msg && [msg isKindOfClass:[HTMessage class]]){
                                        if(msg.msgId && [msg.msgId isKindOfClass:[NSString class]]){
                                            if([msgid isEqualToString:msg.msgId]){
                                               
                                                HTMessage *chatMsg = msg;
                                                if(chatMsg && [chatMsg isKindOfClass:[HTMessage class]]){
                                                    
                                                    HTMessage *new =  [ZFChatMessageHelper translateCommonMessageToWithDrawnMessageForReceive:chatMsg opid:opid opNick:opNick userInfo:msg.ext];
                                                    
                                                    if(new && [new isKindOfClass:[HTMessage class]]){
                                                        
                                                        
                                                        [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:new];
                                                        [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:new chatId:chatId isReadAllMessage:NO];
                                                        
                                                        
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                }];
                            }
                        
                        }

                    }
                    
                    if(type != ZFMessageTypeFriendApplyAgree && type != ZFMessageTypeFriendApplyDisAgree && type != ZFMessageTypeFriendApply && type != ZFMessageTypeFriendDeleteMe ){
                        [arr addObject:msg];
                        
                        if(type == ZFMessageTypeGroupZhen){
                            [self dealZhenCMDMessage:dic];
                        }
                    }
                    else{
                        
                        if(!(dic && [dic isKindOfClass:[NSDictionary class]])){
                            dic = @{};
                        }
                        
                        if(type == ZFMessageTypeFriendApply){
                            [_notify_app_changeAppBradge postNotifyWithContent:@{@"tabNum":Project_TabIdengtify_Connection,@"num":[NSString stringWithFormat:@"%d",1]}];
                            
                             [[YiChatUserManager defaultManagaer] storageMessageNotifyDataWithChatId:YiChatNotify_FriendApply obj:dic];
                        }
                        
                        /*
                         dic
                         {
                         "ADD_REASON" = "";
                         avatar = "http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/A056154C-2E18-47C9-BC45-388AF995E7C4.png";
                         nick = "\U5361\U54c7\U4f0a";
                         userId = 14012458;
                         }

                         
                         */
                        [_notify_friendNotify postNotifyWithContent:@{@"type":[NSString stringWithFormat:@"%ld",action],@"msg":dic}];
                    }
                    
                }
            }
            
            [self.notify_cmdMsgReceive postNotifyWithContent:arr];
            
            dispatch_semaphore_signal(self.receiveCMDMsgLock);
        }];
    }
}
    
- (void)didReceiveSeriveTimeCorrectMessages:(NSArray *)aMessages{
    if(aMessages && [aMessages isKindOfClass:[NSArray class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.receiveMsgTimeLock, DISPATCH_TIME_FOREVER);
            
            [ProjectHelper helper_getGlobalThread:^{
                
                if(aMessages && [aMessages isKindOfClass:[NSArray class]]){
                    for (HTMessage *message in aMessages) {
                        NSString *msgId = message.msgId;
                        
                        WS(weakSelf);
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                            
                            [ZFChatHelper zfChatHelper_getLocalMsgWithMsgid:msgId invocation:^(HTMessage * _Nonnull msg) {
                                if(msg && [msg isKindOfClass:[HTMessage class]]){
                                    if(msg.msgId && [msg.msgId isKindOfClass:[NSString class]]){
                                        if([msgId isEqualToString:msg.msgId]){
                                            msg.timestamp = message.timestamp;
                                            
                                            msg.sendState = SendStateSuccessed;
                                            
                                            [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:msg];
                                            
                                            [weakSelf uploadMessage:msg];
                                            
                                            [self.notify_receiveMSGTime postNotifyWithContent:@[message]];
                                        }
                                    }
                                }
                            }];
                            
                        });
                    }
                }
            }];
          //  [self.notify_receiveMSGTime postNotifyWithContent:aMessages];
            
            dispatch_semaphore_signal(self.receiveMsgTimeLock);
        }];
    }
}
    
- (void)uploadMessage:(HTMessage *)message{
      [ProjectHelper helper_getGlobalThread:^{
          dispatch_semaphore_wait(self.uploadmessagelock, DISPATCH_TIME_FOREVER);
          [ZFChatRequestHelper zfRequest_uploadMessage:message chatType:message.chatType completion:^(BOOL isCompletion, id  _Nonnull des) {
              dispatch_semaphore_signal(self.uploadmessagelock);
          }];
      }];
}

- (void)dealZhenCMDMessage:(NSDictionary *)dic{
    if (self.popView.isShowing) {
        return;
    }
    WS(weakSelf);
    UIViewController *vc = [self xs_getCurrentViewController];
    if ([vc isKindOfClass:NSClassFromString(@"ZFChatVC")]) {
        ZFChatVC *chatVC = (ZFChatVC *)vc;
        if ([[chatVC getChatId] isEqualToString:dic[@"groupId"]]) {
            return;
        }
    }
    [ProjectHelper helper_getMainThread:^{
      [vc.view endEditing:YES];
    }];

    SystemSoundID m_RingSound = 0;
    NSString *musicPath = [[NSBundle mainBundle] pathForResource:@"video_incoming" ofType:@"mp3"];
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL fileURLWithPath:musicPath], &m_RingSound);
    AudioServicesAddSystemSoundCompletion(kSystemSoundID_Vibrate, NULL, NULL, systemAudioCallback, NULL);
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    [self->_ringPlayer stop];
    NSURL *url = [[NSURL alloc] initFileURLWithPath:musicPath];
    self->_ringPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
    [self->_ringPlayer setVolume:1];
    self->_ringPlayer.numberOfLoops = -1; //设置音乐播放次数  -1为一直循环
    if([self->_ringPlayer prepareToPlay])
    {
        [self->_ringPlayer play]; //播放
    }
    
    YiChatShowZhenView *showView = [[YiChatShowZhenView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_HEIGHT)];
    showView.dic = dic;
    showView.dimissBlock = ^{
        [weakSelf stopRing];
        [weakSelf.popView dismiss:YES];
        AudioServicesRemoveSystemSoundCompletion(kSystemSoundID_Vibrate);
    };
    showView.promptBlock = ^(NSDictionary * _Nonnull groupMsg) {
        [weakSelf.popView dismiss:YES];
        [weakSelf stopRing];
        AudioServicesRemoveSystemSoundCompletion(kSystemSoundID_Vibrate);
        
        UINavigationController *nav = vc.navigationController;
        
        UIViewController *HTChatVC = [ZFChatUIHelper getChatVCWithChatId:dic[@"groupId"] chatType:@"2"];
        HTChatVC.hidesBottomBarWhenPushed = YES;
        if ([vc isKindOfClass:NSClassFromString(@"ZFChatVC")]) {
            [nav popViewControllerAnimated:NO];
        }
        [nav pushViewController:HTChatVC animated:YES];
    };
    dispatch_async(dispatch_get_main_queue(), ^{
        weakSelf.popView = [KLCPopup popupWithContentView:showView showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
        [weakSelf.popView show];
    });
}

- (void)stopRing{
    [_ringPlayer stop];
}

void systemAudioCallback(){
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
}

- (UIViewController *)xs_getCurrentViewController{
    UIWindow* window = [[[UIApplication sharedApplication] delegate] window];
    NSAssert(window, @"The window is empty");
    //获取根控制器
    UIViewController* currentViewController = window.rootViewController;
    //获取当前页面控制器
    BOOL runLoopFind = YES;
    while (runLoopFind){
        if (currentViewController.presentedViewController) {
            currentViewController = currentViewController.presentedViewController;
        } else if ([currentViewController isKindOfClass:[UINavigationController class]]) {
            UINavigationController* navigationController = (UINavigationController* )currentViewController;
            currentViewController = [navigationController.childViewControllers lastObject];
        } else if ([currentViewController isKindOfClass:[UITabBarController class]]){
            UITabBarController* tabBarController = (UITabBarController* )currentViewController;
            currentViewController = tabBarController.selectedViewController;
        } else {
            NSUInteger childViewControllerCount = currentViewController.childViewControllers.count;
            if (childViewControllerCount > 0) {
                currentViewController = currentViewController.childViewControllers.lastObject;
                return currentViewController;
            } else {
                return currentViewController;
            }
        }
    }
    return currentViewController;
}


/**
 
 创建群：                 群普通消息  message.ext = {"action":"2000","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像"}                      message.body.content ="\"某某群\"创建成功"
 
 更新群名称：            群普通消息  message.ext = {"action":"2001","groupName":"群名称","uid":"用户id","nickName":"用户昵称"}                                      UI展示为：群主自己 -> "你修改群名称为"新的群名称""  群成员 -> ""某人"修改群名称为"新的群名称""
 
 更新群描述：            群普通消息  message.ext = {"action":"2002","groupDescription":"群描述","uid":"用户id","nickName":"用户昵称"}
 
 更新群头像：            群普通消息  message.ext = {"action":"2005","groupAvatar":"群头像","uid":"用户id","nickName":"用户昵称"}
 
 往群里加人：          群普通消息  message.ext = {"action":"2003","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像","members":[{"uid":"第一个用户id","nickName":"第一个用户昵称"},{"uid":"第二个用户id","nickName":"第二个用户昵称"}]}
 
 UI展示为：被加的人:   "你加入了群聊"群名称  其他成员: ""某某"加入了群聊"
 
 从群里移除群成员：        群普通消息  message.ext = {"action":"2004","uid":"被移除的群成员id","nickName":"被移除的群成员昵称"}
 
 个人透传    message.body = {"action":"2004":"data":"当前群id"}
 */



//chatType == 1 单聊 chatType == 2 群聊
- (NSDictionary *)buildPostMessageEntityWithMsgType:(NSString *)type chatType:(NSString *)chatType msg:(HTMessage *)msg{
    if(msg == nil || ![msg isKindOfClass:[HTMessage class]]){
        return nil;
    }
    else{
        
        NSInteger contentMsgType = [self getMessageTypeWithMessageBodyTypeStr:type];
        NSInteger contentChatType = [self getMessageChatTypeWithChatTypeStr:chatType];
        
        
        return @{ZFChatMsgContent_ChatMsg:msg,ZFChatMsgContent_ChatMsgType:[NSString stringWithFormat:@"%ld",contentMsgType],ZFChatMsgContent_ChatType:[NSString stringWithFormat:@"%ld",contentChatType]};
    }
}

- (ZFMessageType)getContentMessageDicMessageType:(NSDictionary *)contentMessageDic{
    if(contentMessageDic && [contentMessageDic isKindOfClass:[NSDictionary class]]){
        return [contentMessageDic[ZFChatMsgContent_ChatMsgType] integerValue];
    }
    return ZFMessageTypeUnknown;
}

- (ZFChatType)getContentMessageDicChatType:(NSDictionary *)contentMessageDic{
    if(contentMessageDic && [contentMessageDic isKindOfClass:[NSDictionary class]]){
        return [contentMessageDic[ZFChatMsgContent_ChatType] integerValue];
    }
    return ZFChatTypeUnknown;
}

- (HTMessage *)getContentMessageDicMSGEntity:(NSDictionary *)contentMessageDic{
    if(contentMessageDic && [contentMessageDic isKindOfClass:[NSDictionary class]]){
        return contentMessageDic[ZFChatMsgContent_ChatMsg];
    }
    return nil;
}

- (ZFMessageType)getMessageTypeWithMessageBodyTypeStr:(NSString *)type{
    ZFMessageType contentMsgType;
    if ([type integerValue] == 2001) {
        contentMsgType = ZFMessageTypeText;
    }else if ([type integerValue] == 2002){
        contentMsgType = ZFMessageTypePhoto;
    }else if ([type integerValue] == 2003){
        contentMsgType = ZFMessageTypeVoice;
    }else if ([type integerValue] == 2004){
        contentMsgType = ZFMessageTypeVideo;
    }else if ([type integerValue] == 2005){
        contentMsgType = ZFMessageTypeLocation;
    }else if ([type integerValue] == 2006){
        contentMsgType = ZFMessageTypeFile;
    }
    else{
        contentMsgType = ZFMessageTypeUnknown;
    }
    return contentMsgType;
}

- (ZFMessageType)getMessageTypeWithAction:(NSInteger )extAction{
    if( extAction == 2000 || extAction == 2001 || extAction == 2002 || extAction == 2003 || extAction == 2004 || extAction == 2005 || extAction == 3001){
        return ZFMessageTypeGroupMsgNotify;
    }
    else if(extAction == 10007){
        return ZFMessageTypePersonCard;
    }
    else if(extAction == 30000 || extAction == 30001){
        if(extAction == 30000){
            return ZFMessageTypeGroupSilence;
        }
        else{
            return ZFMessageTypeGroupCancelSilence;
        }
    }
    else if(extAction == 30004 || extAction == 30005){
        if(extAction == 30004){
            return ZFMessageTypeGroupMemberSilence;
        }
        else{
            return ZFMessageTypeCancelGroupMemberSilence;
        }
    }
    else if(extAction == 30002 || extAction == 30003){
        if(extAction == 30002){
            return ZFMessageTypeGroupSetManager;
        }
        else{
            return ZFMessageTypeGroupCancelSetManager;
        }
    }
    else if(extAction == 1000 || extAction == 1001 || extAction == 1002 || extAction == 1003){
        if(extAction == 1000){
            return ZFMessageTypeFriendApply;
        }
        else if(extAction == 1001){
            return ZFMessageTypeFriendApplyAgree;
        }
        else if(extAction == 1002){
            return ZFMessageTypeFriendApplyDisAgree;
        }
        else if(extAction == 1003){
            return ZFMessageTypeFriendDeleteMe;
        }
    }
    else if(extAction == 10001){
        return ZFMessageTypeRedPackageReceiveOrSend;
    }
    else if(extAction == 10004){
        return ZFMessageTypeRedPackageGet;
    }
    else if(extAction == 40001){
        return ZFMessageTypeGroupZhen;
    }else if (extAction == 40002){
        return ZFMessageTypeGroupNotice;
    }
    else if(extAction == 6000 || extAction == 6001){
        return ZFMessageTypeWithdrawn;
    }
    else if(extAction == 50001){
        return ZFMessageTypeText;
    }
    return ZFMessageTypeUnknown;
}

- (NSString *)getMesageBodyTypeStrInfoMessageType:(ZFMessageType)msgType{
    if(msgType == ZFMessageTypeText){
        return @"2001";
    }
    else if(msgType == ZFMessageTypePhoto){
        return @"2002";
    }
    else if(msgType == ZFMessageTypeVoice){
        return @"2003";
    }
    else if(msgType == ZFMessageTypeVideo){
        return @"2004";
    }
    else if(msgType == ZFMessageTypeLocation){
        return @"2005";
    }
    else if(msgType == ZFMessageTypeFile){
        return @"2006";
    }
    else if(msgType == ZFMessageTypeUnknown){
        return @"-10000";
    }
    return @"-10000";
}

- (ZFChatType)getMessageChatTypeWithChatTypeStr:(NSString *)chatType{
    ZFChatType contentChatType;
    
    if([chatType integerValue] == 1){
        contentChatType = ZFChatTypeChat;
    }
    else if([chatType integerValue] == 2){
        contentChatType = ZFChatTypeGroup;
    }
    else{
        contentChatType = ZFChatTypeUnknown;
    }
    
    return contentChatType;
}

- (NSString *)getMessageChatTypeStrWithMessageChatType:(ZFChatType)chatType{
    if(chatType == ZFChatTypeChat){
        return @"1";
    }
    else if(chatType == ZFChatTypeGroup){
        return @"2";
    }
    return @"-10000";
}

- (NSString *)getConversationTimeStrContentWithTimeInterval:(NSInteger)timeInterval andTimeInterval:(NSInteger)timeIntervalLast{
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"MM-dd HH:mm:ss"];
    
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:timeIntervalLast];
    NSDate *current = [NSDate dateWithTimeIntervalSince1970:timeInterval];
    
    NSString*confromTimespStr = [formatter stringFromDate:date];
    
    NSInteger unixTime= (timeInterval - timeIntervalLast);
    
    if(unixTime < (24 * 3600))
    {
        if(unixTime < 0){
            NSInteger day = abs(unixTime / 3600 / 24);
            if(day == 0){
                day = 1;
            }
            confromTimespStr = @"刚刚";
        }
        else{
            [formatter setDateFormat:@"HH:mm"];
            confromTimespStr = [NSString stringWithFormat:@"%@%@",@"昨天",[formatter stringFromDate:date]];
            
            if(unixTime < 3600 && unixTime > 0){
                confromTimespStr =@"1个小时前";
                
                if(unixTime < 60 * 60 & unixTime >= 60){
                    confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime / 60,@"分钟前"];
                }
                else{
                    confromTimespStr =[NSString stringWithFormat:@"%d%@",1,@"分钟前"];
                    //confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime,@"秒前"];
                }
                
            }
            else if(unixTime >= 3600 && unixTime < 24 * 3600){
                confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime / 3600,@"小时前"];
            }
            else{
                confromTimespStr = @"刚刚";
            }
            
        }
        
    }
    else{
        confromTimespStr = [NSString stringWithFormat:@"%ld天前",unixTime / (24 * 3600)];
    }
    
    return confromTimespStr;
    
}


#pragma mark
#pragma mark conversationManager delegate
#pragma mark

- (void)addConversationManagerDelegate{
    [[HTClient sharedInstance].conversationManager removeDelegate:self];
    
    [[HTClient sharedInstance].conversationManager addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)removeConversationManagerDelegate{
    [[HTClient sharedInstance].conversationManager removeDelegate:self];
}

/**
 ~cn:聊天会话内容变更回调 ~en:a callback when conversation changed
 */
- (void)conversationChanged{
    [ProjectHelper helper_getGlobalThread:^{
       [_notify_conversationChange postNotifyWithContent:nil];
    }];
}

#pragma mark
#pragma mark Group delegate
#pragma mark

- (HTGroupManager *)groupManager{
    _groupManager = [HTClient sharedInstance].groupManager;
    return _groupManager;
}

- (void)addGroupManagerDelegate{
    
    [self.groupManager removeDelegate:self];
    
    [self.groupManager addDelegate:self delegateQueue:dispatch_get_main_queue()];
}

- (void)removeGroupManagerDelegate{
    [self.groupManager removeDelegate:self];
}

/**
 群列表更新
 */
- (void)didGroupListUpdatad{
    [ProjectHelper helper_getGlobalThread:^{
        if(_notify_group_listChange){
            [_notify_group_listChange postNotifyWithContent:nil];
        }
    }];
}

/**
 群信息更新
 
 @param aGroup 更新过群信息的群实例
 */
- (void)groupInfoChanged:(HTGroup *)aGroup{
    [ProjectHelper helper_getGlobalThread:^{
        if(_notify_group_infoChange){
            [_notify_group_infoChange postNotifyWithContent:nil];
        }
    }];
}


- (NSDictionary *)getCMDMessageBody:(HTCmdMessage *)cmdMsg
{
    if(cmdMsg && [cmdMsg isKindOfClass:[HTCmdMessage class]])
    {
        HTCmdMessage *msg = cmdMsg;
        if(msg.body && [msg.body isKindOfClass:[NSString class]]){
            
            NSDictionary *dic = [ProjectTranslateHelper helper_dictionaryWithJsonString:msg.body];
           
            return dic;
        }
    }
    return nil;
}

- (NSDictionary *)getCmdMessageExtData:(HTCmdMessage *)cmdMsg{
    NSDictionary *dic = [self getCMDMessageBody:cmdMsg];
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        id data = dic[@"data"];
 
        if ([data isKindOfClass:[NSDictionary class]]) {
            NSDictionary *extData = dic[@"data"];
            if(extData && [extData isKindOfClass:[NSDictionary class]]){
                return extData;
            }
        }
        
        if ([data isKindOfClass:[NSString class]]) {
            NSDictionary *dataDic = [ProjectTranslateHelper helper_dictionaryWithJsonString:dic[@"data"]];
            return dataDic;
        }
    }
    return nil;
}

- (NSInteger)getCMDMessageAction:(HTCmdMessage *)cmdMsg
{
    NSDictionary *dic = [self getCMDMessageBody:cmdMsg];
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSString *action = dic[@"action"];
        if(action && [action isKindOfClass:[NSNumber class]]){
            return [action integerValue];
        }
        if(action && [action isKindOfClass:[NSString class]]){
            return [action integerValue];
        }
    }
    return nil;
}

- (void)zfChat_sendMessage:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked{
    
    [ProjectHelper helper_getGlobalThread:^{
        dispatch_semaphore_wait(self.sendMessageLock, DISPATCH_TIME_FOREVER);
        
        [[HTClient sharedInstance] sendMessage:aMessage completion:^(HTMessage *message, NSError *error) {
            dispatch_semaphore_signal(self.sendMessageLock);
                   if(message){
                       if(message.msgType == 2002 || message.msgType == 2003 || message.msgType == 2004){
                           [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:message];
                       }
                   }
                   if(aBlocked){
                       aBlocked(message,error);
                   }
        }];
    }];
}


@end


@implementation ZFChatConfigureEntity

@end
