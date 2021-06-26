//
//  ZFChatNotify.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSUInteger,ZFChatNotifyStyle){
    ZFChatNotifyStyleLoginState = 10000,
    ZFChatNotifyStyleXMPPConnectionState,
    ZFChatNotifyStyleConversationChanged,
    ZFChatNotifyStyleDynamicMSG,
    ZFChatNotifyStyleVoiceVideoCall,
    ZFChatNotifyStyleReceiveCommonMsg,
    ZFChatNotifyStyleReceiveCMDMsg,
    ZFChatNotifyStyleReceiveMSGTime,
    ZFChatNotifyStyleFriendNotify,
    //群组列表更新
    ZFChatNotifyStyleGroupListUpdate,
    ZFChatNotifyStyleGroupInfoUpdate,
    ZFChatNotifyStyleGroupVoiceVideoCall,
    ZFChatNotifyStyleGroupShutUp,
    ZFChatNotifyStyleGroupRestShutUp,
    ZFChatNotifyStyleGroupSetManager,
    ZFChatNotifyStyleGroupCancelSetManager,
    ZFChatNotifyStyleGroupAddApply,
    ZFChatNotifyStyleGroupDelete,
    ZFChatNotifyStyleSystemQuite,
    ZFChatNotifyStyleAppBecomeBackground,
    ZFChatNotifyStyleAppBecomeActive,
    ZFChatNotifyStyleChangeBradgeNum
    
};
@interface ZFChatNotify : NSObject

+ (void)addNotifyWithStyle:(ZFChatNotifyStyle)style target:(id)target SEL:(SEL)action;

+ (void)removeNotifyWithStyle:(ZFChatNotifyStyle)style target:(id)target;

+ (void)postNotifyWithStyle:(ZFChatNotifyStyle)style content:(id)content;

+ (NSString *)getNotifyNameWithStyle:(ZFChatNotifyStyle)style;

@end

NS_ASSUME_NONNULL_END
