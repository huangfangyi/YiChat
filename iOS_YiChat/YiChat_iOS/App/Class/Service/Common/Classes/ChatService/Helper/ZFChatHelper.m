//
//  ZFChatHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatHelper.h"
#import "ZFChatGlobal.h"
#import "ZFChatResourceHelper.h"
#import "ProjectRequestHelper.h"
#import "ProjectAssetManager.h"
#import "ProjectTranslateHelper.h"
#import "ProjectLocationManager.h"
#import "HTDBManager.h"


@implementation ZFChatHelper

+ (void)zfChatHelper_chatConfigureWithEntity:(ZFChatConfigureEntity *)entity delegate:(id<ZFChatManageDelegate>)delagte{
    [[ZFChatManage defaultManager] configureChatWithEntity:entity delegate:delagte];
    
    [[ProjectConfigure defaultConfigure] navInitial];
    
    [[ProjectConfigure defaultConfigure] tabInitial];
    
    //[[ProjectLocationManager defualtLocationManager] initialMapConfigure];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
       [ZFChatResourceHelper ZFResourceHelperLoadEmojiView];
    });
}

+ (BOOL)zfChatHelper_getIsAutoLogin{
    return [[HTClient sharedInstance] autoLogin];
}

+ (void)zfChatHelper_loginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion{
    
    [[ZFChatManage defaultManager] loginWithUserName:userName password:password completion:completion];
}

+ (void)autoLogin:(void(^)(BOOL isSuccess))invocation{
    [[ZFChatManage defaultManager] autoLogin:invocation];
}

+ (void)zfChatHelper_updateUnreadMessage{
    [[ZFChatManage defaultManager] updateUnreadMessage];
}

+ (void)zfChatHelper_cleanConnectionMessage{
    [[ZFChatManage defaultManager] cleanConnectionMessage];
}

+ (void)zfChatHelper_sendMessage:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked{
    [[ZFChatManage defaultManager] zfChat_sendMessage:aMessage completion:aBlocked];
}

+ (void)zfChatHelper_sendMessageUnneedUpload:(HTMessage *)aMessage completion:(void(^)(HTMessage *message,NSError *error))aBlocked{
    [[HTClient sharedInstance] sendMessageUnNeedUpload:aMessage completion:aBlocked];
}

+ (void)zfChatHelper_addDelegate:(id<ZFChatManageDelegate>)delagte{
    [[ZFChatManage defaultManager] addDelegate:delagte];
}

+ (void)zfChatHelper_loginOut{
    [[ZFChatManage defaultManager] logout];
}

+ (void)zfChatHelper_chatClean{
    [[ZFChatManage defaultManager] chatConfigureClean];
}

+ (void)zfCahtHelper_updateLocalConcersationWithMsg:(HTMessage *)msg chatId:(NSString *)chatId isReadAllMessage:(BOOL)isReadAllMessage{
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        HTConversation * converModel = [HTConversation new];
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            converModel.chatterId = chatId;
        }
        else{
            return;
        }
        converModel.lastMessage = msg;
        [[HTClient sharedInstance].conversationManager updataOneConversationWithChatterConversation:converModel isReadAllMessage:isReadAllMessage];
    }
}
    
    
+ (void)zfChatHelper_getLocalMsgWithMsgid:(NSString *)msgid invocation:(void(^)(HTMessage *msg))invocation{
    if(msgid && [msgid isKindOfClass:[NSString class]]){
        [[HTDBManager sharedInstance] getLocalMessageWithMessageId:msgid invocation:^(HTMessage *msg) {
            if(invocation){
                invocation(msg);
            }
        }];
    }
    else{
        invocation(nil);
    }
}

+ (void)zfCahtHelper_updateLocalConcersationWithMsg:(HTMessage *)msg chatId:(NSString *)chatId unreadCount:(NSInteger)unread isReadAllMessage:(BOOL)isReadAllMessage{
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        HTConversation * converModel = [HTConversation new];
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            converModel.chatterId = chatId;
        }
        else{
            return;
        }
        converModel.unreadMessageCount = unread;
        converModel.lastMessage = msg;
        [[HTClient sharedInstance].conversationManager updataOneConversationWithChatterConversation:converModel isReadAllMessage:isReadAllMessage];
        if(isReadAllMessage == NO && unread > 0){
            converModel.unreadMessageCount = unread;
        }
    }
}

+ (void)zfCahtHelper_updateLocalConcersationWithConversation:(HTConversation *)conversation isReadAllMessage:(BOOL)isReadAllMessage{
    if(conversation && [conversation isKindOfClass:[HTConversation class]]){
        [[HTClient sharedInstance].conversationManager updataOneConversationWithChatterConversation:conversation isReadAllMessage:isReadAllMessage];
    }
}

+ (void)zfCahtHelper_updateLocalMessageWithMsg:(HTMessage *)msg{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if(msg && [msg isKindOfClass:[HTMessage class]]){
            [[HTClient sharedInstance].messageManager updateOneNormalMessage:msg];
        }
    });
}

+ (ZFChatNotifyEntity *)zfChatHelper_getChatNotifyWithStyle:(ZFChatNotifyStyle)style
                                     target:(id)target
                                        sel:(SEL)selector{
    return  [[ZFChatManage defaultManager] getNotifyEntitfyWithStyle:style target:target sel:selector];
}

+ (ZFChatNotifyEntity *)zfChatHelper_getChatNotifyWithStyle:(ZFChatNotifyStyle)style{
    return [[ZFChatManage defaultManager] getNotifyEntitfyWithStyle:style];
}


+ (void)fetchUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        if(userId && [userId isKindOfClass:[NSString class]]){
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:invocation];
        }
        else{
            invocation(nil,nil);
        }
    }];
}

+ (void)fetchGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:groupId invocation:invocation];
        }
        else{
            invocation(nil,nil);
        }
    }];
}

+ (void)updateGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:groupId invocation:invocation];
        }
        else{
            invocation(nil,nil);
        }
    }];
}

+ (NSString *)zfChatHelper_getCurrentUser{
    return YiChatUserInfo_UserIdStr;
}

+ (void)zfChatHelper_getCurrentUserInfo:(void(^)(YiChatUserModel *model,NSString *error))invocation{
     [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:invocation];
}

+ (NSString *)zfChatHelper_getCurrentUserSession{
    return YiChatUserInfo_Token;
}

+ (NSDictionary *)zfChatHelper_getRequestTokenDic{
    return [ProjectRequestParameterModel getTokenParamWithToken:[self zfChatHelper_getCurrentUserSession]];
}

+ (NSDictionary *)zfChatHelper_getUserInfoDic{
    YiChatUserManager *user = [YiChatUserManager defaultManagaer];
    
    NSDictionary *userInfo = [ProjectTranslateHelper helper_translateObjPropertyToDic:user.userModel];
    
    return  userInfo;
}

+ (void)zfChatHelper_exportVideoWithPhasset:(PHAsset *)aset success:(void (^)(NSString *outputPath))success failure:(void (^)(NSString *errorMessage, NSError *error))failure {
    [[ProjectAssetManager assetManager] getVideoOutputPathWithAsset:aset success:success failure:failure];
}

+ (NSDictionary *)getUserDicWithModel:(id)model{
    if(model && [model isKindOfClass:[YiChatUserModel class]] ){
        YiChatUserModel *userModel = model;
        
        NSMutableDictionary * userInfoDic = @{}.mutableCopy;
         [userInfoDic setObject:[NSString stringWithFormat:@"%ld",userModel.userId] forKey:@"userId"];
        
        if (userModel.nick.length > 0) {
            [userInfoDic setObject:userModel.nick forKey:@"nick"];
        }
        if (userModel.avatar.length > 0) {
            [userInfoDic setObject:userModel.avatar forKey:@"avatar"];
        }
//        if (userModel.medal.length > 0) {
//            [userInfoDic setObject:userModel.medal forKey:@"medal"];
//        }
//        if (userModel.rank.length > 0) {
//            [userInfoDic setObject:userModel.rank forKey:@"rank"];
//        }
//        if (userModel.medalstate.length > 0) {
//            [userInfoDic setObject:userModel.medalstate forKey:@"medalstate"];
//        }
//        if (userModel.medalUrl.length > 0) {
//            [userInfoDic setObject:userModel.medalUrl forKey:@"medalUrl"];
//        }
        
        return userInfoDic;
    }
    return nil;
}

+ (void)getCurrentUserDicInvocation:(void(^)(NSDictionary *))invocation{
    YiChatUserManager *user =  [YiChatUserManager defaultManagaer];
    [user fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
            NSDictionary *dic = [self getUserDicWithModel:model];
            invocation(dic);
            return;
        }
        invocation(nil);
    }];
    
}

+ (void )addMoreEXTToMessage:(HTMessage *)message withExt:(NSDictionary *)ext{
    if(message && [message isKindOfClass:[HTMessage class]]){
        if (message.ext) {
            NSArray * allKeys = [message.ext allKeys];
            NSMutableDictionary * moreDic = [[NSMutableDictionary alloc] init];
            for (NSString * key in allKeys) {
                [moreDic setObject:message.ext[key] forKey:key];
            }
            for (NSString *key in [ext allKeys]) {
                [moreDic setObject:ext[key] forKey:key];
            }
            message.ext = moreDic.copy;
        }else{
            message.ext = ext;
        }
    }
}

+ (HTMessage *)translateRequestHttpDataToHTMessage:(NSDictionary *)dic{
    return  [[ZFChatManage defaultManager] translateRequestHttpDataToHTMessage:dic];
}

+ (void)zfChatHelper_playVoiceWithUrl:(NSString *)url progress:(zfChatVoicePlayProgressInvocation)progress completion:(zfChatVoicePlayCompleteHandel)success failure:(zfChatVoicePlayErrorHandel)failure{
    [[ZFChatVoicePlayManager sharedVoicePlayerManager] playVoiceWithUrl:url progress:progress completion:success failure:failure];
}

+ (void)zfChatHelper_stopPlayVoice{
    [[ZFChatVoicePlayManager sharedVoicePlayerManager] stopPlay];
}
    
+ (ZFChatVoicePlayMode)zfChatHelper_getPlayVoiceMode{
    return  [[ZFChatVoicePlayManager sharedVoicePlayerManager] playMode];
}
    
+ (BOOL)zfChatHelper_getPlayVoicePlayingState{
    return [[ZFChatVoicePlayManager sharedVoicePlayerManager] playVoicePlayingState];
}
    
+ (void)zfChatHelper_ChangePlayVoiceMode{
    [[ZFChatVoicePlayManager sharedVoicePlayerManager] changePlayVoiceMode];
}

+ (ZFMessageType)zfChatHeler_getMessageTypeWithHTMessage:(HTMessage *)messgae{
    if(messgae && [messgae isKindOfClass:[HTMessage class]]){
        if(messgae.msgType == 2001){
            return ZFMessageTypeText;
        }
        else if(messgae.msgType == 2002){
            return ZFMessageTypePhoto;
        }
        else if(messgae.msgType == 2003){
            return ZFMessageTypeVoice;
        }
        else if(messgae.msgType == 2004){
            return ZFMessageTypeVideo;
        }
        else if(messgae.msgType == 2005){
            return ZFMessageTypeFile;
        }
        else if(messgae.msgType == 2006){
            return ZFMessageTypeLocation;
        }
    }
    return ZFMessageTypeUnknown;
}

+ (ZFMessageType)zfChatHeler_getMessageTypeWithAction:(NSInteger )extAction{
    return  [[ZFChatManage defaultManager] getMessageTypeWithAction:extAction];
}

+ (void)needUpdateGroupChatListState:(NSString *)groupId state:(BOOL)state{
    [[ZFChatManage defaultManager] needUpdateGroupChatListState:groupId state:state];
}

+ (BOOL)getGroupChatListState:(NSString *)groupId{
    return [[ZFChatManage defaultManager] getGroupChatListState:groupId];
}

+ (void)removeAllGroupChatListState{
    return [[ZFChatManage defaultManager] removeAllGroupChatListState];
}

+ (void)zfChatHeler_deleteOneChatterAllMessagesByChatterId:(NSString *)chatId{
    [[HTClient sharedInstance].messageManager deleteOneChatterAllMessagesByChatterId:chatId];
}

+ (void)zfChatHeler_deleteLocalMessageWithMessage:(HTMessage *)msg{
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        [[HTClient sharedInstance].messageManager deleteOneNormalMessage:msg];
    }
}

+ (void)zfChatHeler_insertMessage:(HTMessage *)msg{
    [[HTClient sharedInstance].messageManager insertOneNormalMessage:msg];
}

+ (NSDictionary *)getCMDMessageBody:(HTCmdMessage *)cmdMsg{
    return [[ZFChatManage defaultManager] getCMDMessageBody:cmdMsg];
}

+ (NSDictionary *)getCmdMessageExtData:(HTCmdMessage *)cmdMsg{
    return [[ZFChatManage defaultManager] getCmdMessageExtData:cmdMsg];
}

+ (NSInteger)getCMDMessageAction:(HTCmdMessage *)cmdMsg{
    return [[ZFChatManage defaultManager] getCMDMessageAction:cmdMsg];
}

+ (NSDictionary *)zfchatFeltSendUploadDic:(NSDictionary *)dic{
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
                if([bodyDicTmp.allKeys containsObject:@"localPath"]){
                    [bodyDicTmp removeObjectForKey:@"localPath"];
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

@end
