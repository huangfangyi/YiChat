//
//  ZFChatFriendHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatFriendHelper.h"
#import "ZFChatMessageHelper.h"
#import "ProjectTranslateHelper.h"

@implementation ZFChatFriendHelper

//userInfo userId,nick,avata
+ (void)zfChatFriendHelperAddFriendWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked{
    if([userInfoDic isKindOfClass:[NSDictionary class]] && userInfoDic){
        NSDictionary * bodyDic = @{@"action":@"1000",@"data":userInfoDic};
        
        NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
        if(modelString && [modelString isKindOfClass:[NSString class]]){
            HTCmdMessage * cmdMessage = [ZFChatMessageHelper sendCmdMessage:modelString to:userid chatType:@"1"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
                blocked(message,error);
            }];
        }
        else{
            blocked(nil,[NSError errorWithDes:@"数据异常"]);
        }
    }
    else{
       blocked(nil,[NSError errorWithDes:@"数据异常"]);
    }
}

+ (void)zfChatFriendHelperAgreeFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked{
    if([userInfoDic isKindOfClass:[NSDictionary class]] && userInfoDic){
        NSDictionary * bodyDic = @{@"action":@"1001",@"data":userInfoDic};
        
        NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
        if(modelString && [modelString isKindOfClass:[NSString class]]){
            HTCmdMessage * cmdMessage = [ZFChatMessageHelper sendCmdMessage:modelString to:userid chatType:@"1"];
            
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
                blocked(message,error);
            }];
            
            NSMutableDictionary *ext = [NSMutableDictionary dictionaryWithCapacity:0];
            [ext addEntriesFromDictionary:userInfoDic];
            [ext addEntriesFromDictionary:@{@"action":@"50001"}];
            HTMessage *msg = [ZFChatMessageHelper sendTextMessage:@"我们已经成为好友了快来聊天吧～" to:userid messageType:@"1" messageExt:ext];
            
            [[HTClient sharedInstance] sendMessage:msg completion:^(HTMessage *message, NSError *error) {
                
            }];
            
        }
        else{
            blocked(nil,[NSError errorWithDes:@"数据异常"]);
        }
    }
    else{
        blocked(nil,[NSError errorWithDes:@"数据异常"]);
    }
}

+ (void)zfChatFriendHelperDisagreeFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked{
    if([userInfoDic isKindOfClass:[NSDictionary class]] && userInfoDic){
        NSDictionary * bodyDic = @{@"action":@"1002",@"data":userInfoDic};
        
        NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
        if(modelString && [modelString isKindOfClass:[NSString class]]){
            HTCmdMessage * cmdMessage = [ZFChatMessageHelper sendCmdMessage:modelString to:userid chatType:@"1"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
                blocked(message,error);
            }];
        }
        else{
            blocked(nil,[NSError errorWithDes:@"数据异常"]);
        }
    }
    else{
        blocked(nil,[NSError errorWithDes:@"数据异常"]);
    }
}

+ (void)zfChatFriendHelperDeleteFriendApplyWithUserId:(NSString *)userid  userInfo:(NSDictionary *)userInfoDic completion:(void (^)(HTCmdMessage *, NSError *))blocked{
    if([userInfoDic isKindOfClass:[NSDictionary class]] && userInfoDic){
        NSDictionary * bodyDic = @{@"action":@"1003",@"data":userInfoDic};
        
        NSString *modelString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:bodyDic];
        if(modelString && [modelString isKindOfClass:[NSString class]]){
            HTCmdMessage * cmdMessage = [ZFChatMessageHelper sendCmdMessage:modelString to:userid chatType:@"1"];
            [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
                blocked(message,error);
            }];
        }
        else{
            blocked(nil,[NSError errorWithDes:@"数据异常"]);
        }
    }
    else{
        blocked(nil,[NSError errorWithDes:@"数据异常"]);
    }
}

@end
