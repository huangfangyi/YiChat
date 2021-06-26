//
//  ZFChatUntilities.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#ifndef ZFChatUntilities_h
#define ZFChatUntilities_h


//2001文字，2002图片，2003语音，2004视频，2005文件，2006位置
typedef NS_ENUM(NSUInteger,ZFMessageType){
    ZFMessageTypeUnknown = -1,
    ZFMessageTypeText = 0,
    ZFMessageTypePhoto = 1,
    ZFMessageTypeVoice = 2,
    ZFMessageTypeVideo = 3,
    ZFMessageTypeFile = 4,
    ZFMessageTypeLocation = 5,
    ZFMessageTypeRedPackageReceiveOrSend,
    ZFMessageTypePersonCard,//个人名片
    ZFMessageTypeRedPackageGet,
    ZFMessageTypeGroupMsgNotify,
    ZFMessageTypeWithdrawn,
    ZFMessageTypeGroupSetManager,
    ZFMessageTypeGroupCancelSetManager,
    ZFMessageTypeGroupSilence,
    ZFMessageTypeGroupCancelSilence,
    ZFMessageTypeGroupMemberSilence,
    ZFMessageTypeCancelGroupMemberSilence,
    ZFMessageTypeFriendApply,
    ZFMessageTypeFriendApplyAgree,
    ZFMessageTypeFriendApplyDisAgree,
    ZFMessageTypeFriendDeleteMe,
    ZFMessageTypeGroupZhen,
    ZFMessageTypeGroupNotice
};

typedef NS_ENUM(NSUInteger,ZFChatType){
    ZFChatTypeUnknown = -1,
    ZFChatTypeChat = 0,
    ZFChatTypeGroup = 1,
};


/**
 ~cn:网络连接状态 ~en:network status
 
 - HTConnectionConnected: ~cn:已连接 ~en:connected
 - HTConnectionConnecting: ~cn:正在连接 ~en:connecting
 - HTConnectionDisconnected: ~cn:已断开连接 ~en:disconnected
 */
typedef NS_ENUM(NSInteger,ZFConnectionState){
    ZFConnectionStateConnected = 0,
    ZFConnectionStateConnecting = 1,
    ZFConnectionStateDisconnected,
};


#define ZFChatMsgContent_ChatMsg @"ZFChatMsg"
#define ZFChatMsgContent_ChatMsgType @"ZFChatMsgType"
#define ZFChatMsgContent_ChatType @"ZFChatType"


#endif /* ZFChatUntilities_h */
