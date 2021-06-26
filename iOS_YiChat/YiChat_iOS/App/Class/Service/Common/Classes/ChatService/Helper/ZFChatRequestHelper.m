//
//  ZFChatRequestHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/11.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatRequestHelper.h"
#import "ZFRequestManage.h"
#import "HTMessage.h"
#import "ProjectRequestHelper.h"
#import "NSObject+YYModel.h"
#import "ProjectTranslateHelper.h"
#import "ZFChatHelper.h"
#import "ProjectHelper.h"
#import "NSString+URLEncoding.h"

@implementation ZFChatRequestHelper

+ (void)zfRequestHelper_LoginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion{
    [[ZFRequestManage defaultManager] zfRequuest_LoginWithUserName:userName password:password completion:completion];
}

+ (void)zfRequestHelper_loadSingleChatMessageRecorderWithChatId:(NSString *)chatId lastestMessageTimeUnix:(NSInteger)timeUnix numsForPage:(NSInteger)nums completion:(void(^)(NSArray *messageArr,NSString *error))completion;{
    [[ZFRequestManage defaultManager] zfRequuest_loadSingleChatMessageRecorderWithChatId:chatId lastestMessageTimeUnix:timeUnix numsForPage:nums completion:completion];
}

+ (void)zfRequestHelper_loadGroupChatMessageRecorderWithChatId:(NSString *)chatId userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle{
    
    if(chatId && [chatId isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
        NSDictionary *parameter = @{@"referId":chatId,@"referType":[NSNumber numberWithInt:2],@"time":[NSNumber numberWithInteger:timeUnix],@"pageSize":@"20"};
        
        NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
        
        [ProjectRequestHelper getMessageListWithParameters:parameter headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    id msgArr = obj[@"data"];
                    if(msgArr && [msgArr isKindOfClass:[NSArray class]]){
                        successHandle(msgArr);
                    }
                    else{
                        failHandle(@"获取群众聊天记录出错");
                    }
                    
                }
                else if(obj && [obj isKindOfClass:[NSString class]]){
                    failHandle(obj);
                }
                else{
                    failHandle(@"获取群众聊天记录出错");
                }
            }];
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            failHandle(error);
        }];
        
    }
    
//    [[ZFRequestManage defaultManager] zfRequuest_loadGroupChatMessageRecorderWithChatId:chatId userId:userId lastestMessageTimeUnix:timeUnix success:successHandle fail:failHandle];
}

+ (void)zfRequestHelper_loadChatMessageRecorderWithChatId:(NSString *)chatId chatType:(NSInteger)chatType userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle{
    
    if(chatId && [chatId isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
        NSDictionary *parameter = @{@"referId":chatId,@"referType":[NSNumber numberWithInt:chatType],@"time":[NSNumber numberWithInteger:timeUnix],@"pageSize":@"20"};
        
        NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
        
        [ProjectRequestHelper getMessageListWithParameters:parameter headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    id msgArr = obj[@"data"];
                    if(msgArr && [msgArr isKindOfClass:[NSArray class]]){
                        successHandle(msgArr);
                    }
                    else{
                        failHandle(@"获取群聊天记录出错");
                    }
                    
                }
                else if(obj && [obj isKindOfClass:[NSString class]]){
                    failHandle(obj);
                }
                else{
                    failHandle(@"获取群聊天记录出错");
                }
            }];
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            failHandle(error);
        }];
        
    }
    
//    [[ZFRequestManage defaultManager] zfRequuest_loadGroupChatMessageRecorderWithChatId:chatId userId:userId lastestMessageTimeUnix:timeUnix success:successHandle fail:failHandle];
}

+ (void)zfRequestHelper_logout{
    [[ZFRequestManage defaultManager] zfRequest_logout];
}

+ (void)zfRequestUpdateUnixTimeWithTime:(NSUInteger)unixTime{
    
    NSDictionary *parameter = @{@"timestamp":[NSNumber numberWithInteger:unixTime]};
    
    NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
    
    [ProjectRequestHelper updateTimeUnixWithParameters:parameter headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

+ (void)zfRequestCheckTokenInvocation:(void(^)(BOOL isNeedLoginOut))invocation{
    
    NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
    
    [ProjectRequestHelper checkTokenWithParameters:tokenDic headerParameters:nil progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                NSNumber *res = obj[@"data"];
                if(res && [res isKindOfClass:[NSNumber class]]){
                    if(res.integerValue == 0){
                        invocation(NO);
                        return ;
                    }
                }
            }
            invocation(YES);
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        invocation(YES);
    }];
}


+ (void)zfRequest_uploadMessage:(HTMessage *)message chatType:(NSString *)chatType{
    
    if(message && [message isKindOfClass:[HTMessage class]]){
        
        NSString *userId = message.to;
        NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
        
        NSString *string = [message modelToJSONString];
        //    string = [string stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *dic = [ProjectTranslateHelper helper_dictionaryWithJsonString:string];
        
        if(dic && [dic isKindOfClass:[NSDictionary class]]){
            
            dic = [ZFChatHelper zfchatFeltSendUploadDic:dic];
            
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                NSDictionary *requestDic = @{@"type":@2000,@"data":dic};
                
                NSString *requestString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:requestDic];
                
                //   NSData *data = [requestString dataUsingEncoding:NSUTF8StringEncoding];
                NSString *base64MessageString = [requestString urlEncodeString];
                NSString *msgId = message.msgId;
                
                if(userId && [userId isKindOfClass:[NSString class]] && chatType && [chatType isKindOfClass:[NSString class]] && base64MessageString && msgId && [msgId isKindOfClass:[NSString class]]){
                    
                    
                    
                    NSMutableDictionary *params = @{@"referId":userId,
                                                    @"referType":[NSNumber numberWithInt:[chatType intValue]],
                                                    @"content":base64MessageString,
                                                    @"time":[NSNumber numberWithInteger:message.timestamp],
                                                    @"messageId":msgId
                                                    }.mutableCopy;
                    
                    [ProjectRequestHelper uploadMessageWithParameters:params headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                        
                        
                    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                        
                        
                    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                        
                    }];
                }
            }
        }
    }
}

+ (void)zfRequest_uploadMessage:(HTMessage *)message chatType:(NSString *)chatType completion:(void(^)(BOOL isCompletion,id des))block{
    
    if(message && [message isKindOfClass:[HTMessage class]]){
        
        NSString *userId = message.to;
        NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
        
        NSString *string = [message modelToJSONString];
        //    string = [string stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *dic = [ProjectTranslateHelper helper_dictionaryWithJsonString:string];
        
        if(dic && [dic isKindOfClass:[NSDictionary class]]){
            
            dic = [ZFChatHelper zfchatFeltSendUploadDic:dic];
            
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                NSDictionary *requestDic = @{@"type":@2000,@"data":dic};
                
                NSString *requestString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:requestDic];
                
                //   NSData *data = [requestString dataUsingEncoding:NSUTF8StringEncoding];
                NSString *base64MessageString = [requestString urlEncodeString];
                NSString *msgId = message.msgId;
                
                if(userId && [userId isKindOfClass:[NSString class]] && chatType && [chatType isKindOfClass:[NSString class]] && base64MessageString && msgId && [msgId isKindOfClass:[NSString class]]){
                    
                    
                    
                    NSMutableDictionary *params = @{@"referId":userId,
                                                    @"referType":[NSNumber numberWithInt:[chatType intValue]],
                                                    @"content":base64MessageString,
                                                    @"time":[NSNumber numberWithInteger:message.timestamp],
                                                    @"messageId":msgId
                                                    }.mutableCopy;
                    
                    [ProjectRequestHelper uploadMessageWithParameters:params headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
                        
                        
                    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                        if(block){
                            block(YES,data);
                        }
                        
                    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                        if(block){
                            block(YES,error);
                        }
                    }];
                }
            }
        }
    }
}

+ (void)zfRequestHelper_loadConversations:(void(^)(NSArray <HTConversation *>* conversationArr))invocation{
    HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
    NSArray *tempConversations = manager.conversations.mutableCopy;
    if(tempConversations && [tempConversations isKindOfClass:[NSArray class]]){
       
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];;
        
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
        
        for (HTConversation *model in tempConversations) {
            if(model && [model isKindOfClass:[HTConversation class]]){
                if(model && [model.chatterId isKindOfClass:[NSString class]]){
                    NSString *key = [model.chatterId mutableCopy];
                    if(!([key isEqual:[NSNull null]] || key == nil)){
                        if(key && [key isKindOfClass:[NSString class]]){
                            if(key.length > 0){
                                if(![key isEqualToString:@"(null)"]){
                                    [dic setObject:model forKey:key];
                                }
                            }
                        }
                    }
                   
                }
            }
        }
        if(dic.allKeys.count > 0){
            for (int i = 0; i < dic.allKeys.count; i ++) {
                NSString *key = dic.allKeys[i];
                if(key && [key isKindOfClass:[NSString class]]){
                    if(key.length >0 ){
                        HTConversation *model = dic[key];
                        if(model && [model isKindOfClass:[HTConversation class]] && key && [key isKindOfClass:[NSString class]]){
                            [arr addObject:model];
                        }
                    }
                }
            }
        }
        invocation(arr);
    }
    else{
        invocation(@[]);
    }
//    if(tempConversations && [tempConversations isKindOfClass:[NSArray class]]){
//        if(tempConversations.count >0){
//            invocation(tempConversations);
//        }
//        else{
//            [self fetchConverstionFromDB:invocation];
//        }
//    }
//    else{
//       [self fetchConverstionFromDB:invocation];
//    }
}

+ (void)fetchConverstionFromDB:(void(^)(NSArray <HTConversation *>* conversationArr))invocation{
    HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
    if(manager && [manager isKindOfClass:[HTConversationManager class]]){
        [manager loadAllConversationsFromDBCompletion:invocation];
        
    }
    else{
        invocation(nil);
    }
}
@end
