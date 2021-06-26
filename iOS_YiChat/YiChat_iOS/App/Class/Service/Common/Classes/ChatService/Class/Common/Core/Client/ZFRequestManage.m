//
//  ZFRequestManage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFRequestManage.h"
#import "HTClient.h"
#import "ZFChatGlobal.h"
#import <AFNetworking/AFHTTPSessionManager.h>
#import "AFHTTPSessionManager+FormRequests.h"

static ZFRequestManage *manage = nil;
@interface ZFRequestManage ()

@end

@implementation ZFRequestManage

+ (id)defaultManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manage = [[self alloc] init];
    });
    
    return manage;
}


- (void)zfRequuest_LoginWithUserName:(NSString *)userName password:(NSString *)password completion:(void(^)(BOOL success,NSString *des))completion{
    
    if(![userName isKindOfClass:[NSString class]] || !userName){
        completion(NO,@"用户名非法");
        return;
    }
    
    if(![password isKindOfClass:[NSString class]] || !password){
        completion(NO,@"密码非法");
        return;
    }

    
    [[HTClient sharedInstance] loginWithUsername:userName password:password completion:^(BOOL result) {
        completion(result,nil);
    }];
}

- (void)zfRequuest_loadSingleChatMessageRecorderWithChatId:(NSString *)chatId lastestMessageTimeUnix:(NSInteger)timeUnix numsForPage:(NSInteger)nums completion:(void(^)(NSArray *messageArr,NSString *error))completion{
    
    if(chatId && [chatId isKindOfClass:[NSString class]]){
        
        [[HTClient sharedInstance].conversationManager fetchNormessagesByChatterId:chatId andTimestamp:timeUnix withOffsetSize:nums completion:^(NSArray *tempArray) {
            completion(tempArray,nil);
        }];
    }
}

- (void)zfRequuest_loadGroupChatMessageRecorderWithChatId:(NSString *)chatId userId:(NSString *)userId lastestMessageTimeUnix:(NSInteger)timeUnix success:(void(^)(NSArray *messageArr))successHandle fail:(void(^)(NSString *error))failHandle{
    
    if(chatId && [chatId isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
        AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
        
        [manager FRPOST:@"/api/message/list" parameters:@{@"uid":userId,@"referId":chatId,@"timestamp":[NSString stringWithFormat:@"%ld",timeUnix]} success:^(NSURLSessionTask *task, id responseObject, NSDictionary *responseDictionary) {
            if(responseDictionary && [responseDictionary isKindOfClass:[NSDictionary class]]){
                if ([responseDictionary[@"code"] integerValue] == 1) {
                    
                    NSLog(@"load group msg--->%@",responseDictionary);
                    NSArray *msgArr = responseDictionary[@"data"];
                    
                    if(msgArr && [msgArr isKindOfClass:[NSArray class]]){
                        successHandle(msgArr);
                    }
                    else{
                        failHandle(@"获取聊天记录出错");
                    }
                }
                else{
                    failHandle(@"获取聊天记录出错");
                }
            }
            else{
                failHandle(@"获取聊天记录出错");
            }
            
        } failure:^(NSURLSessionTask *task, NSError *error) {
            failHandle(error.localizedDescription);
        }];
    }
    else{
        failHandle(@"获取聊天记录参数出错");
    }
}

- (void)zfRequest_connectionListWithPage:(NSInteger)page success:(void(^)(NSArray *originDataArr,NSArray *listArr))successHandle fail:(void(^)(NSString *error))failHandle{
    
    [[YiChatUserManager defaultManagaer] fetchUserConnectionInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            if(model.originDataArr && [model.originDataArr isKindOfClass:[NSArray class]]){
                if(model.originDataArr.count != 0){
                    successHandle(model.originDataArr,model.connectionModelArr);
                    return ;
                }
            }
        }
        else if(error && [error isKindOfClass:[NSString class]]){
            failHandle(error);
            return;
        }
        failHandle(nil);
    }];
}

- (void)zfRequest_logout{
    [[HTClient sharedInstance] logout];
}
@end
