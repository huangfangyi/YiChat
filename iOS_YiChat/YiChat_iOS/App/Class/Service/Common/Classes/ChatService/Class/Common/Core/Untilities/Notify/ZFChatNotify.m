//
//  ZFChatNotify.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatNotify.h"
#import "ZFChatGlobal.h"

//登录状态的通知
static NSString * const ZFCHAT_NOTIFICATION_LOGINCHANGE = @"ZFCHAT_NOTIFICATION_LOGINCHANGE";
//通过成功过后 xmpp连接状态改变的通知
static NSString * const ZFCHAT_NOTIFICATION_XMPPCONNECTSTATE = @"ZFCHAT_NOTIFICATION_XMPPCONNECTSTATE";

//收到消息
static NSString * const ZFCHAT_NOTIFICATION_RECEIVECOMMONMSG = @"ZFCHAT_NOTIFICATION_RECEIVECOMMONMSG";
//收到cmd消息
static NSString * const ZFCHAT_NOTIFICATION_REICEIVECMDMSG = @"ZFCHAT_NOTIFICATION_REICEIVECMDMSG";
//收到消息Time
static NSString * const ZFCHAT_NOTIFICATION_REICEIVEMSGTIME = @"ZFCHAT_NOTIFICATION_REICEIVEMSGTIME";
//未读消息数
static NSString * const ZFCHAT_NOTIFICATION_RELOADUNREADNUM = @"ZFCHAT_NOTIFICATION_RELOADUNREADNUM";
//语音视频通话
static NSString * const ZFCHAT_NOTIFICATION_VOICEVIDEOCALL = @"ZFCHAT_NOTIFICATION_VOICEVIDEOCALL";
//朋友圈消息提醒
static NSString * const ZFCHAT_NOTIFICATION_DYNAMICMSG = @"ZFCHAT_NOTIFICATION_DYNAMICMSG";
//群列表更新提醒
static NSString * const ZFCHAT_NOTIFICATION_GROUP_LISTUPDATE = @"ZFCHAT_NOTIFICATION_GROUP_LISTUPDATE";
//群组信息更新提醒
static NSString * const ZFCHAT_NOTIFICATION_GROUP_INGOUPDATE = @"ZFCHAT_NOTIFICATION_GROUP_INGOUPDATE";
//群组语音视频通话
static NSString * const ZFCHAT_NOTIFICATION_GROUP_VOICEVIDEOCALL = @"ZFCHAT_NOTIFICATION_GROUP_VOICEVIDEOCALL";
//群禁言通知
static NSString * const ZFCHAT_NOTIFICATION_GROUP_SHUTUP = @"ZFCHAT_NOTIFICATION_GROUP_SHUTUP";
//群解除禁言通知
static NSString * const ZFCHAT_NOTIFICATION_GROUP_RESETSHUTUP = @"ZFCHAT_NOTIFICATION_GROUP_RESETSHUTUP";
//群设置管理员
static NSString * const ZFCHAT_NOTIFICATION_GROUP_SETMANAGER = @"ZFCHAT_NOTIFICATION_GROUP_SETMANAGER";
//群取消设置管理员
static NSString * const ZFCHAT_NOTIFICATION_GROUP_CANCELSETMANAGER =@"ZFCHAT_NOTIFICATION_GROUP_CANCELSETMANAGER";
//加群申请
static NSString * const ZFCHAT_NOTIFICATION_GROUP_ADDAPPLY = @"ZFCHAT_NOTIFICATION_GROUP_ADDAPPLY";
//群解散
static NSString * const ZFCHAT_NOTIFICATION_GROUP_DELETE = @"ZFCHAT_NOTIFICATION_GROUP_DELETE";
//静音通知
static NSString * const ZFCHAT_NOTIFICATION_SYSTEM_QUITE = @"ZFCHAT_NOTIFICATION_SYSTEM_QUITE";
//app退入后台
static NSString * const ZFCHAT_NOTIFICATION_APP_BECOMEBACKGROUND = @"ZFCHAT_NOTIFICATION_APP_BECOMEBACKGROUND";
//app进入前台活跃
static NSString * const ZFCHAT_NOTIFICATION_APP_BECOMEACTIVE = @"ZFCHAT_NOTIFICATION_APP_BECOMEACTIVE";
//frined notify
static NSString * const ZFCHAT_NOTIFICATION_FRIEND_NOTIFY = @"ZFCHAT_NOTIFICATION_FRIEND_NOTIFY";

//更改app 下标数字
static NSString * const ZFCHAT_NOTIFICATION_APP_CHNAGEBRADGENUM = @"ZFCHAT_NOTIFICATION_APP_CHNAGEBRADGENUM";


@implementation ZFChatNotify

+ (void)addNotifyWithStyle:(ZFChatNotifyStyle)style target:(id)target SEL:(SEL)action{
    
    NSString *name = [self getNotifyNameWithStyle:style];
    
    [[NSNotificationCenter defaultCenter] addObserver:target
                                             selector:action
                                                 name:name
                                               object:nil];
}

+ (void)removeNotifyWithStyle:(ZFChatNotifyStyle)style target:(id)target{
    
     NSString *name = [self getNotifyNameWithStyle:style];
    
      [[NSNotificationCenter defaultCenter] removeObserver:target name:name object:nil];
}

+ (void)postNotifyWithStyle:(ZFChatNotifyStyle)style content:(id)content{
    
     NSString *name = [self getNotifyNameWithStyle:style];
    
     [[NSNotificationCenter defaultCenter] postNotificationName:name object:content];
}

+ (NSString *)getNotifyNameWithStyle:(ZFChatNotifyStyle)style{
    //登录状态的通知
    if(style == ZFChatNotifyStyleLoginState){
        return  ZFCHAT_NOTIFICATION_LOGINCHANGE;
    }
    //通过成功过后 xmpp连接状态改变的通知
    else if(style == ZFChatNotifyStyleXMPPConnectionState){
        return ZFCHAT_NOTIFICATION_XMPPCONNECTSTATE;
    }
    //未读消息数
    else if(style == ZFChatNotifyStyleConversationChanged){
        return ZFCHAT_NOTIFICATION_RELOADUNREADNUM;
    }
     //语音视频通话
    else if(style == ZFChatNotifyStyleVoiceVideoCall){
        return ZFCHAT_NOTIFICATION_VOICEVIDEOCALL;
    }
    //朋友圈消息提醒
    else if(style == ZFChatNotifyStyleDynamicMSG){
        return ZFCHAT_NOTIFICATION_DYNAMICMSG;
    }
     //群组语音视频通话
    else if(style == ZFChatNotifyStyleGroupVoiceVideoCall){
        return ZFCHAT_NOTIFICATION_GROUP_VOICEVIDEOCALL;
    }
    //群禁言通知
    else if(style == ZFChatNotifyStyleGroupShutUp){
        return ZFCHAT_NOTIFICATION_GROUP_SHUTUP;
    }
    //群解除禁言通知
    else if(style == ZFChatNotifyStyleGroupRestShutUp){
        return ZFCHAT_NOTIFICATION_GROUP_RESETSHUTUP;
    }
    //群设置管理员
    else if(style == ZFChatNotifyStyleGroupSetManager){
        return ZFCHAT_NOTIFICATION_GROUP_SETMANAGER;
    }
    else if(style == ZFChatNotifyStyleGroupListUpdate){
        return ZFCHAT_NOTIFICATION_GROUP_SETMANAGER;
    }
    else if(style == ZFChatNotifyStyleGroupInfoUpdate){
        return ZFCHAT_NOTIFICATION_GROUP_SETMANAGER;
    }
    //群取消设置管理员
    else if(style == ZFChatNotifyStyleGroupCancelSetManager){
        return ZFCHAT_NOTIFICATION_GROUP_CANCELSETMANAGER;
    }
    //加群申请
    else if(style == ZFChatNotifyStyleGroupAddApply){
        return ZFCHAT_NOTIFICATION_GROUP_ADDAPPLY;
    }
    //静音通知
    else if(style == ZFChatNotifyStyleSystemQuite){
        return ZFCHAT_NOTIFICATION_SYSTEM_QUITE;
    }
    //app退入后台
    else if(style == ZFChatNotifyStyleAppBecomeBackground){
        return ZFCHAT_NOTIFICATION_APP_BECOMEBACKGROUND;
    }
    //app进入前台活跃
    else if(style == ZFChatNotifyStyleAppBecomeActive){
        return ZFCHAT_NOTIFICATION_APP_BECOMEACTIVE;
    }
    else if(style == ZFChatNotifyStyleReceiveCommonMsg){
        return ZFCHAT_NOTIFICATION_RECEIVECOMMONMSG;
    }
    else if(style == ZFChatNotifyStyleReceiveCMDMsg){
        return ZFCHAT_NOTIFICATION_REICEIVECMDMSG;
    }
    else if(style == ZFChatNotifyStyleReceiveMSGTime){
        return ZFCHAT_NOTIFICATION_REICEIVEMSGTIME;
    }
    else if(style == ZFChatNotifyStyleChangeBradgeNum){
        return ZFCHAT_NOTIFICATION_APP_CHNAGEBRADGENUM;
    }
    else if(style == ZFChatNotifyStyleFriendNotify){
        return ZFCHAT_NOTIFICATION_FRIEND_NOTIFY;
    }
    else if(style == ZFChatNotifyStyleGroupDelete){
        return ZFCHAT_NOTIFICATION_GROUP_DELETE;
    }
    return nil;
}

@end
