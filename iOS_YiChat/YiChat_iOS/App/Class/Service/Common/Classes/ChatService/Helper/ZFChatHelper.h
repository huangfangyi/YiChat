//
//  ZFChatHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatManage.h"
#import "ZFGroupHelper.h"
#import <Photos/Photos.h>
#import "ZFChatVoicePlayManager.h"
#import "YiChatUserManager.h"

NS_ASSUME_NONNULL_BEGIN


@interface ZFChatHelper : NSObject

+ (BOOL)zfChatHelper_getIsAutoLogin;

+ (void)zfChatHelper_chatConfigureWithEntity:(ZFChatConfigureEntity *)entity delegate:(id<ZFChatManageDelegate>)delagte;

+ (void)zfChatHelper_loginOut;

+ (void)zfChatHelper_chatClean;

+ (void)zfChatHelper_loginWithUserName:(NSString *)userName
                              password:(NSString *)password
                            completion:(void(^)(BOOL success,NSString *des))completion;

+ (void)autoLogin:(void(^)(BOOL isSuccess))invocation;

+ (void)zfChatHelper_sendMessage:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked;

+ (void)zfChatHelper_sendMessageUnneedUpload:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked;

+ (void)zfChatHelper_exportVideoWithPhasset:(PHAsset *)aset success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure;

+ (void)zfCahtHelper_updateLocalConcersationWithMsg:(HTMessage *)msg chatId:(NSString *)chatId isReadAllMessage:(BOOL)isReadAllMessage;

+ (void)zfCahtHelper_updateLocalConcersationWithConversation:(HTConversation *)conversation isReadAllMessage:(BOOL)isReadAllMessage;
    
+ (void)zfChatHelper_getLocalMsgWithMsgid:(NSString *)msgid invocation:(void(^)(HTMessage *msg))invocation;

+ (void)zfCahtHelper_updateLocalConcersationWithMsg:(HTMessage *)msg chatId:(NSString *)chatId unreadCount:(NSInteger)unread isReadAllMessage:(BOOL)isReadAllMessage;

+ (void)zfCahtHelper_updateLocalMessageWithMsg:(HTMessage *)msg;

+ (void)zfChatHelper_updateUnreadMessage;

+ (void)zfChatHelper_cleanConnectionMessage;

+ (ZFChatNotifyEntity *)zfChatHelper_getChatNotifyWithStyle:(ZFChatNotifyStyle)style
                                                     target:(id)target
                                                        sel:(SEL)selector;



+ (ZFChatNotifyEntity *)zfChatHelper_getChatNotifyWithStyle:(ZFChatNotifyStyle)style;

+ (NSString *)zfChatHelper_getCurrentUser;

+ (void)fetchUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation;

+ (void)fetchGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation;

+ (void)updateGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation;

+ (NSString *)zfChatHelper_getCurrentUserSession;

+ (NSDictionary *)zfChatHelper_getRequestTokenDic;

+ (NSDictionary *)getUserDicWithModel:(id)model;

+ (void)zfChatHelper_getCurrentUserInfo:(void(^)(YiChatUserModel *model,NSString *error))invocation;

+ (void)getCurrentUserDicInvocation:(void(^)(NSDictionary *))invocation;

+ (void)addMoreEXTToMessage:(HTMessage *)message withExt:(NSDictionary *)ext;

+ (HTMessage *)translateRequestHttpDataToHTMessage:(NSDictionary *)dic;

+ (void)zfChatHelper_playVoiceWithUrl:(NSString *)url progress:(zfChatVoicePlayProgressInvocation)progress completion:(zfChatVoicePlayCompleteHandel)success failure:(zfChatVoicePlayErrorHandel)failure;

+ (void)zfChatHelper_stopPlayVoice;
    
+ (ZFChatVoicePlayMode)zfChatHelper_getPlayVoiceMode;
    
+ (BOOL)zfChatHelper_getPlayVoicePlayingState;
    
+ (void)zfChatHelper_ChangePlayVoiceMode;

+ (ZFMessageType)zfChatHeler_getMessageTypeWithHTMessage:(HTMessage *)messgae;

+ (ZFMessageType)zfChatHeler_getMessageTypeWithAction:(NSInteger )extAction;

+ (void)needUpdateGroupChatListState:(NSString *)groupId state:(BOOL)state;

+ (BOOL)getGroupChatListState:(NSString *)groupId;

+ (void)removeAllGroupChatListState;

+ (void)zfChatHeler_deleteOneChatterAllMessagesByChatterId:(NSString *)chatId;

+ (void)zfChatHeler_deleteLocalMessageWithMessage:(HTMessage *)msg;

+ (void)zfChatHeler_insertMessage:(HTMessage *)msg;

+ (NSDictionary *)getCMDMessageBody:(HTCmdMessage *)cmdMsg;

+ (NSDictionary *)getCmdMessageExtData:(HTCmdMessage *)cmdMsg;

+ (NSInteger)getCMDMessageAction:(HTCmdMessage *)cmdMsg;

+ (NSDictionary *)zfchatFeltSendUploadDic:(NSDictionary *)dic;
@end

NS_ASSUME_NONNULL_END
