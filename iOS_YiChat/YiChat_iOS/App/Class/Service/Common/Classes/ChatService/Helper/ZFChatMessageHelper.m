//
//  ZFChatMessageHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatMessageHelper.h"
#import "HTFileMessageManager.h"
#import "ZFChatGlobal.h"
#import "ZFChatMessageHelper.h"
#import "ProjectTranslateHelper.h"
#import "ZFChatManage.h"
#import "ZFGroupHelper.h"
#import "ZFChatHelper.h"

@implementation ZFChatMessageHelper

+ (HTMessage *)sendTextMessage:(NSString *)text
                            to:(NSString *)to
                   messageType:(NSString *)messageType
                    messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.content = text;
    message.body = body;
    [HTFileMessageManager handleTextMessage:message];
    return message;
}

+ (HTCmdMessage * )sendCmdMessage:(NSString *)aBody to:(NSString *)aTo chatType:(NSString *)aChatType {
    HTCmdMessage * cmdMessage = [HTCmdMessage new];
    cmdMessage.to = aTo;
    cmdMessage.chatType = aChatType;
    cmdMessage.body = aBody;
    [HTFileMessageManager handleCMDMessage:cmdMessage];
    return cmdMessage;
}

+ (HTMessage *)sendLocationMessageWithLatitude:(double)latitude
                                     longitude:(double)longitude
                                       address:(NSString *)address
                                            to:(NSString *)to
                                   messageType:(NSString *)messageType
                                    andSSImage:(UIImage *)mapImage
                                    messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.address = address;
    body.latitude = latitude;
    body.longitude = longitude;
    message.body = body;
    [HTFileMessageManager HandlePositionMessage:message withImage:mapImage];
    return message;
}

+ (HTMessage *)sendImageMessageWithImage:(UIImage *)image
                                      to:(NSString *)to
                             messageType:(NSString *)messageType
                              messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    message.body = body;
    [HTFileMessageManager HandleImageMessage:message withImage:image];
    return message;
}

///var/mobile/Containers/Data/Application/6876CA90-5375-46D0-97AD-DF5DC6CAD46B/Documents/AudioFile/306E8600-375A-4F1C-BECA-36AF2F3FD5B2.amr
+ (HTMessage *)sendAudioMessageWithLocalPath:(NSString *)localPath
                                    duration:(NSInteger)duration
                                          to:(NSString *)to
                                 messageType:(NSString *)messageType
                                  messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.audioDuration = [NSString stringWithFormat:@"%d",(int)duration];
    message.body = body;
    [HTFileMessageManager HandleAudioMessage:message withFilePath:localPath];
    return message;
}

+ (HTMessage *)sendVideoMessageWithURL:(NSURL *)url
                                    to:(NSString *)to
                           messageType:(NSString *)messageType
                            andSSImage:(UIImage *)mapImage
                          andVideoTime:(NSInteger)videoDurtion
                            messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.videoDuration = videoDurtion;
    
    if(mapImage && [mapImage isKindOfClass:[UIImage class]]){
        CGSize videoSize = CGSizeMake(mapImage.size.width, mapImage.size.height);
        CGFloat w = 0;
        CGFloat h = 0;
        if(videoSize.width > videoSize.height){
            w = 200.0;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:videoSize.width / videoSize.height width:w height:0];
        }
        else{
            h = 200.0;
            w = [ProjectHelper helper_GetWidthOrHeightIntoScale:videoSize.width / videoSize.height width:0 height:h];
        }
        body.size = [NSString stringWithFormat:@"%f,%f",w,h];
    }
    message.body = body;
    [HTFileMessageManager handleVideoMessage:message withFilePath:url andThumbnailImage:mapImage];
    return message;
}

+ (HTMessage *)sendFileMessageWithFilePath:(NSString *)filePath
                                        to:(NSString *)to
                               messageType:(NSString *)messageType
                                  fileName:(NSString *)fileName
                                messageExt:(NSDictionary *)messageExt {
    HTMessage * message = [HTMessage new];
    message.to = to;
    message.chatType = messageType;
    message.ext = messageExt;
    HTMessageBody * body = [HTMessageBody new];
    body.fileName = fileName;
    message.body = body;
    [HTFileMessageManager handleFileMessage:message withFilePath:filePath withFileName:fileName];
    return message;
}

+ (void)sendWithDrawnMessageWithUserId:(NSString *)userid groupRole:(NSInteger)groupRole message:(HTMessage *)message completion:(void (^)(HTMessage *, NSError *))blocked{
    
    if(userid && [userid isKindOfClass:[NSString class]] && message && [message isKindOfClass:[HTMessage class]]){
        if(message.chatType && [message.chatType isKindOfClass:[NSString class]]){
            NSString *messageid = message.msgId;
            
            
            if(messageid && [messageid isKindOfClass:[NSString class]]){
                NSString *nick = @"";
            
                if(YiChatUserInfo_Nick && [YiChatUserInfo_Nick isKindOfClass:[NSString class]]){
                    nick = YiChatUserInfo_Nick;
                }
                
                NSDictionary *bodyDic = @{@"action":@"6000",@"opId":YiChatUserInfo_UserIdStr,@"opNick":nick,@"msgId":messageid};
                
                NSString *chatType = message.chatType;
                
                if(chatType && [chatType isKindOfClass:[NSString class]]){
                    
                    NSString *bodyString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
                    
                    if(bodyString && [bodyString isKindOfClass:[NSString class]]){
                        HTCmdMessage * cmdMessage = [self sendCmdMessage:bodyString to:userid chatType:chatType];
                        if(cmdMessage && [cmdMessage isKindOfClass:[HTCmdMessage class]]){
                            
                            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
                            }];
                        }
                    }
                }
            }
        }
    }
}

+ (HTMessage *)translateCommonMessageToWithDrawnMessage:(HTMessage *)message opid:(NSString *)opid opNick:(NSString *)opNick userInfo:(NSDictionary *)userInfo{
    
    if(!(opNick && [opNick isKindOfClass:[NSString class]])){
        opNick = @"";
    }
    
    if(message && [message isKindOfClass:[HTMessage class]] && opid && [opid isKindOfClass:[NSString class]]){
        if(userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
            
            NSMutableDictionary *userInfoDic = [NSMutableDictionary dictionaryWithCapacity:0];
            if(message.ext && [message.ext isKindOfClass:[NSDictionary class]]){
                [userInfoDic addEntriesFromDictionary:message.ext.mutableCopy];
            }
            [userInfoDic setObject:@"6001" forKey:@"action"];
            [userInfoDic setObject:opid forKey:@"opId"];
            [userInfoDic setObject:opNick forKey:@"opNick"];
            if(userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
                message.ext = userInfoDic.copy;
            }
            
            return message;
        }
    }
    return nil;
}

+ (HTMessage *)translateCommonMessageToWithDrawnMessageForReceive:(HTMessage *)message opid:(NSString *)opid opNick:(NSString *)opNick  userInfo:(NSDictionary *)userInfo{
    
    if(!(opNick && [opNick isKindOfClass:[NSString class]])){
        opNick = @"";
    }
    
    if(message && [message isKindOfClass:[HTMessage class]]  && opid && [opid isKindOfClass:[NSString class]]){
        if(userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
            
            NSMutableDictionary *userInfoDic = message.ext.mutableCopy;
            [userInfoDic setObject:@"6001" forKey:@"action"];
            [userInfoDic setObject:opid forKey:@"opId"];
            [userInfoDic setObject:opNick forKey:@"opNick"];
            if(userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
                message.ext = userInfoDic.copy;
            }
            
            return message;
        }
    }
    return nil;
}

+ (NSString *)getWithDrawMessageTranslateMessageWithMsg:(HTMessage *)message groupRole:(NSInteger)groupRole  isSender:(BOOL)isSender{
    
    NSString *from = message.from;
    NSString *userid = [ZFChatHelper zfChatHelper_getCurrentUser];
    NSString *content = @"";
    if(message.ext && [message.ext isKindOfClass:[NSDictionary class]]){
        NSString *opid = message.ext[@"opId"];
        NSString *nick = message.ext[@"nick"];
        NSString *opNick = message.ext[@"opNick"];
        
        if(!(opNick && [opNick isKindOfClass:[NSString class]])){
            opNick = @"";
        }
        
        if(message.chatType && [message.chatType isKindOfClass:[NSString class]] && opid && [opid isKindOfClass:[NSString class]] && userid && [userid isKindOfClass:[NSString class]] && from && [from isKindOfClass:[NSString class]]){
            
            if([opid isEqualToString:from]){
                if([opid isEqualToString:userid]){
                    content = @"你撤回了一条消息";
                }
                else{
                    content = [NSString stringWithFormat:@"%@%@",nick,@"撤回了一条消息"];
                }
            }
            else if(![opid isEqualToString:from]){
                if([opid isEqualToString:userid]){
                     content = [NSString stringWithFormat:@"你撤回了%@%@",nick,@"一条消息"];
                }
                else{
                    if([from isEqualToString:userid]){
                        content = [NSString stringWithFormat:@"%@撤回了你的%@",opNick,@"一条消息"];
                    }
                    else{
                        content = [NSString stringWithFormat:@"%@撤回了%@的%@",opNick,nick,@"一条消息"];
                    }
                }
            }
        }
    }
    return content;
}

+ (NSString *)getRedPackageContentMessageWithMsg:(HTMessage *)message{
    
    if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
        if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
            NSString *content = message.body.content;
            
            if(message.ext && [message.ext isKindOfClass:[NSDictionary class]]){
                
                if([message.chatType isEqualToString:@"2"]){
                    NSString *msgFrom = message.from;
                    NSString *redpackageFrom = message.ext[@"msgFrom"];
                    
                    NSString *redpackageNick = @"";
                    if(message.ext[@"msgFromNick"] && [message.ext[@"msgFromNick"] isKindOfClass:[NSString class]]){
                        redpackageNick = message.ext[@"msgFromNick"];
                    }
                    NSString *msgFromNick = @"";
                    if(message.ext[@"nick"] && [message.ext[@"nick"] isKindOfClass:[NSString class]]){
                        msgFromNick = message.ext[@"nick"];
                    }
                    
                    if(msgFrom && [msgFrom isKindOfClass:[NSString class]] && redpackageFrom && [redpackageFrom isKindOfClass:[NSString class]]){
                        
                        if([msgFrom isEqualToString:YiChatUserInfo_UserIdStr] && [redpackageFrom isEqualToString:YiChatUserInfo_UserIdStr]){
                            content = @"你领取了自己的红包";
                        }
                        else if([msgFrom isEqualToString:YiChatUserInfo_UserIdStr] && ![redpackageFrom isEqualToString:YiChatUserInfo_UserIdStr]){
                            //你领取了xx的红包
                            content = [NSString stringWithFormat:@"%@领取了%@的红包",@"你",redpackageNick];
                        }
                        else if(![msgFrom isEqualToString:YiChatUserInfo_UserIdStr] && [redpackageFrom isEqualToString:YiChatUserInfo_UserIdStr]){
                            //xx领取了你的红包
                            content = [NSString stringWithFormat:@"%@领取了%@的红包",msgFromNick,@"你"];
                        }
                        else{
                            content = [NSString stringWithFormat:@"%@领取了%@的红包",msgFromNick,redpackageNick];
                        }
                    }
                }
                return content;
                
            }
            else{
                NSString *msgFrom = message.from;
                if(msgFrom && [msgFrom isKindOfClass:[NSString class]]){
                    if(![msgFrom isEqualToString:YiChatUserInfo_UserIdStr]){
                        content = @"红包已被领取";
                    }
                    else{
                        content = @"";
                    }
                }
            }
            
        }
    }
    return @"";
}

+ (void)sendSetManagerCmdWithGroupId:(NSString *)groupId userId:(NSString *)userId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    [self sendSetOrCancelSetGroupManagerWithGroupId:groupId userId:userId type:1 completion:blocked];
}

+ (void)sendCancelSetManagerCmdWithGroupId:(NSString *)groupId userId:(NSString *)userId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    [self sendSetOrCancelSetGroupManagerWithGroupId:groupId userId:userId type:0 completion:blocked];
}

+ (void)sendSetOrCancelSetGroupManagerWithGroupId:(NSString *)groupId userId:(NSString *)userId type:(NSInteger)type completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    
    if(groupId && [groupId isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
        
        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            NSString *userId = [model getUserIdStr];
            NSString *nick = [model nickName];
            NSString *avatar = [model avatar];
            
            NSMutableDictionary *dic =[NSMutableDictionary dictionaryWithCapacity:0];
            [dic setObject:groupId forKey:@"groupId"];
            
            if(userId && [userId isKindOfClass:[NSString class]]){
                [dic setObject:userId forKey:@"userId"];
            }
            if(nick && [nick isKindOfClass:[NSString class]]){
                [dic setObject:nick forKey:@"nick"];
            }
            if(avatar && [avatar isKindOfClass:[NSString class]]){
                [dic setObject:avatar forKey:@"avatar"];
            }
            
            NSString *action = nil;
            if(type == 1){
                action = @"30002";
            }
            else{
                action = @"30003";
            }
            
            NSDictionary * bodyDic = @{
                                       @"action":action,
                                       @"data":[ProjectTranslateHelper helper_convertJsonObjToJsonData:dic]
                                       };
            NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
            
            HTCmdMessage * cmdMessage = [self sendCmdMessage:modelString to:groupId chatType:@"2"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:blocked];
        }];
    }
    else{
        blocked(nil,[NSError errorWithDes:@"参数出错"]);
    }
}

+ (void)sendSilenceGroupWithGroupId:(NSString *)groupId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    [self sendSetOrCancelSetGroupSilenceWithGroupId:groupId type:1 completion:blocked];
}

+ (void)sendCancelSilenceGroupWithGroupId:(NSString *)groupId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
     [self sendSetOrCancelSetGroupSilenceWithGroupId:groupId type:0 completion:blocked];
}

+ (void)sendSetOrCancelSetGroupSilenceWithGroupId:(NSString *)groupId type:(NSInteger)type  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
        NSString *groupName = group.groupName;
        
        [ZFChatHelper zfChatHelper_getCurrentUserInfo:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            NSString *adminId = [model getUserIdStr];
            NSString *adminNick = [model nickName];
            
            NSMutableDictionary *dic =[NSMutableDictionary dictionaryWithCapacity:0];
            [dic setObject:groupId forKey:@"groupId"];
            if(groupName && [groupName isKindOfClass:[NSString class]]){
                [dic setObject:groupName forKey:@"groupName"];
            }
            if(adminId && [adminId isKindOfClass:[NSString class]]){
                [dic setObject:adminId forKey:@"adminId"];
            }
            if(adminNick && [adminNick isKindOfClass:[NSString class]]){
                [dic setObject:adminNick forKey:@"adminNick"];
            }
            
            NSString *action = nil;
            if(type == 1){
                action = @"30000";
            }
            else{
                action = @"30001";
            }
            
            NSDictionary * bodyDic = @{
                                       @"action":action,
                                       @"data":dic
                                       };
             NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
            
            HTCmdMessage * cmdMessage = [self sendCmdMessage:modelString to:groupId chatType:@"2"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:blocked];
        }];
    }
    else{
        blocked(nil,[NSError errorWithDes:@"参数出错"]);
    }
}

+ (void)sendZhenGroupWithGroupId:(NSString *)groupId content:(NSString *)content completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
        NSString *groupName = group.groupName;
        
        [ZFChatHelper zfChatHelper_getCurrentUserInfo:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            NSString *avatar = [model avatar];
            NSString *adminNick = [model nickName];
            
            NSMutableDictionary *dic =[NSMutableDictionary dictionaryWithCapacity:0];
            [dic setObject:groupId forKey:@"groupId"];
            if(groupName && [groupName isKindOfClass:[NSString class]]){
                [dic setObject:groupName forKey:@"groupName"];
            }

            if(adminNick && [adminNick isKindOfClass:[NSString class]]){
                [dic setObject:adminNick forKey:@"adminNick"];
            }
        
            NSDictionary *data = @{
                                  @"groupId" : groupId,
                                  @"avatar" : avatar,
                                  @"nick" : adminNick,
                                  @"groupName" : groupName,
                                  @"content" : content
                                  };
            NSDictionary * bodyDic = @{
                                       @"action":@"40001",
                                       @"data":data
                                       };
            NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
            
            HTCmdMessage * cmdMessage = [self sendCmdMessage:modelString to:groupId chatType:@"2"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:blocked];
        }];
    }
    else{
        blocked(nil,[NSError errorWithDes:@"参数出错"]);
    }
    
}

+ (void)groupNoticeWithGroupId:(NSString *)groupId content:(NSString *)content title:(NSString *)title msgId:(NSString *)msgId completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group = [ZFGroupHelper getHTGroupWithGroupId:groupId];
        NSString *groupName = group.groupName;
        
        [ZFChatHelper zfChatHelper_getCurrentUserInfo:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            NSString *avatar = [model avatar];
            NSString *adminNick = [model nickName];
            
            NSMutableDictionary *dic =[NSMutableDictionary dictionaryWithCapacity:0];
            [dic setObject:groupId forKey:@"groupId"];
            if(groupName && [groupName isKindOfClass:[NSString class]]){
                [dic setObject:groupName forKey:@"groupName"];
            }
            
            if(adminNick && [adminNick isKindOfClass:[NSString class]]){
                [dic setObject:adminNick forKey:@"adminNick"];
            }
            
            NSDictionary *data = @{
                                   @"groupId" : groupId,
                                   @"title" : title,
                                   @"content" : content,
                                   @"id" : msgId
                                   };
            NSDictionary * bodyDic = @{
                                       @"action":@"40002",
                                       @"data":data
                                       };
            NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
            
            HTCmdMessage * cmdMessage = [self sendCmdMessage:modelString to:groupId chatType:@"2"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:blocked];
        }];
    }
    else{
        blocked(nil,[NSError errorWithDes:@"参数出错"]);
    }
    
}

+ (void)upDateMsgType:(NSString *)type to:(NSString *)to{
    HTConversationManager *conversationManager = [HTClient sharedInstance].conversationManager;
    HTConversation *aConversation = [HTConversation new];
    aConversation.chatterId = to;
//    
//    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
//    NSInteger a= ((long long)[dat timeIntervalSince1970]) * 1000;
//    
    HTMessage *msg = [ZFChatMessageHelper sendTextMessage:@"" to:to messageType:type messageExt:@{}];
    msg.timestamp = 0;
    aConversation.lastMessage = msg;
    [conversationManager updataOneConversationWithChatterConversation:aConversation isReadAllMessage:YES];
}

@end
