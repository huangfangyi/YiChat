//
//  ZFChatMessageHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


NS_ASSUME_NONNULL_BEGIN
@class HTMessage;
@class HTCmdMessage;

@interface ZFChatMessageHelper : NSObject

+ (HTMessage *)sendTextMessage:(NSString *)text
                            to:(NSString *)to
                   messageType:(NSString *)messageType
                    messageExt:(NSDictionary *)messageExt;

+ (HTCmdMessage * )sendCmdMessage:(NSString *)aBody to:(NSString *)aTo chatType:(NSString *)aChatType;

+ (HTMessage *)sendLocationMessageWithLatitude:(double)latitude
                                     longitude:(double)longitude
                                       address:(NSString *)address
                                            to:(NSString *)to
                                   messageType:(NSString *)messageType
                                    andSSImage:(UIImage *)mapImage
                                    messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendImageMessageWithImage:(UIImage *)image
                                      to:(NSString *)to
                             messageType:(NSString *)messageType
                              messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendAudioMessageWithLocalPath:(NSString *)localPath
                                    duration:(NSInteger)duration
                                          to:(NSString *)to
                                 messageType:(NSString *)messageType
                                  messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendVideoMessageWithURL:(NSURL *)url
                                    to:(NSString *)to
                           messageType:(NSString *)messageType
                            andSSImage:(UIImage *)mapImage
                          andVideoTime:(NSInteger)videoDurtion
                            messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendFileMessageWithFilePath:(NSString *)filePath
                                        to:(NSString *)to
                               messageType:(NSString *)messageType
                                  fileName:(NSString *)fileName
                                messageExt:(NSDictionary *)messageExt;

+ (void)sendWithDrawnMessageWithUserId:(NSString *)userid groupRole:(NSInteger)groupRole message:(HTMessage *)message completion:(void (^)(HTMessage *, NSError *))blocked;

+ (HTMessage *)translateCommonMessageToWithDrawnMessage:(HTMessage *)message opid:(NSString *)opid opNick:(NSString *)opNick userInfo:(NSDictionary *)userInfo;

+ (NSString *)getWithDrawMessageTranslateMessageWithMsg:(HTMessage *)message groupRole:(NSInteger)groupRole  isSender:(BOOL)isSender;

+ (HTMessage *)translateCommonMessageToWithDrawnMessageForReceive:(HTMessage *)message opid:(NSString *)opid opNick:(NSString *)opNick  userInfo:(NSDictionary *)userInfo;

+ (NSString *)getRedPackageContentMessageWithMsg:(HTMessage *)message;

+ (void)sendSetManagerCmdWithGroupId:(NSString *)groupId  userId:(NSString *)userId completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)sendCancelSetManagerCmdWithGroupId:(NSString *)groupId userId:(NSString *)userId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)sendSilenceGroupWithGroupId:(NSString *)groupId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)sendCancelSilenceGroupWithGroupId:(NSString *)groupId  completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)sendZhenGroupWithGroupId:(NSString *)groupId content:(NSString *)content completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)groupNoticeWithGroupId:(NSString *)groupId content:(NSString *)content title:(NSString *)title msgId:(NSString *)msgId completion:(void (^)(HTCmdMessage *cmd, NSError *error))blocked;

+ (void)upDateMsgType:(NSString *)type to:(NSString *)to;
@end

NS_ASSUME_NONNULL_END
