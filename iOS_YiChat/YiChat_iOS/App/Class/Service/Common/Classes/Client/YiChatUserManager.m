//
//  YiChatUserManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatUserManager.h"
#import "ServiceGlobalDef.h"
#import "ProjectRequestHelper.h"
#import "YiChatStorageManager.h"
#import "ZFGroupHelper.h"
#import "ZFChatHelper.h"
#import "ProjectTranslateHelper.h"
#import "RXAESEncryptor.h"
#import "NSObject+YYModel.h"
#import "NSString+URLEncoding.h"
#import "NSData+AES.h"
#import "YiChatServiceClient.h"

static YiChatUserManager *manager = nil;

@interface YiChatUserManager ()

@property (nonatomic,strong) NSMutableDictionary *fetchUserInfoDic;

@property (nonatomic,strong) NSMutableDictionary *fetchUserConnectionDic;

@property (nonatomic,strong) NSMutableDictionary *fetchGroupInfoDic;

@property (nonatomic,strong) NSMutableDictionary *fetchGroupInfoMemberlistDic;

@property (nonatomic,strong) NSMutableArray *fetchUserInfoArr;

@property (nonatomic,strong) NSMutableArray *fetchUserConnectionArr;

@property (nonatomic,strong) NSMutableArray *fetchGroupInfoArr;

@property (nonatomic,strong) NSMutableArray *fetchGroupInfoMemberlistArr;

@property (nonatomic,strong) dispatch_semaphore_t chatListStateLock;

@property (nonatomic,strong) NSMutableDictionary *chatListUpdateChatList;

@end

@implementation YiChatUserManager

+ (id)defaultManagaer{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[self alloc] init];
        manager.fetchUserInfoDic = [NSMutableDictionary dictionaryWithCapacity:0];
        manager.fetchGroupInfoDic = [NSMutableDictionary dictionaryWithCapacity:0];
        manager.fetchGroupInfoMemberlistDic = [NSMutableDictionary dictionaryWithCapacity:0];
        manager.fetchUserConnectionDic = [NSMutableDictionary dictionaryWithCapacity:0];
        
        manager.fetchUserInfoArr = [NSMutableArray arrayWithCapacity:0];
        manager.fetchGroupInfoArr = [NSMutableArray arrayWithCapacity:0];
        manager.fetchGroupInfoMemberlistArr = [NSMutableArray arrayWithCapacity:0];
        manager.fetchUserConnectionArr = [NSMutableArray arrayWithCapacity:0];
        manager.chatListStateLock = dispatch_semaphore_create(1);
        manager.chatListUpdateChatList = [NSMutableDictionary dictionaryWithCapacity:0];
        
        manager.createGroupPower = 0;
    });
    return manager;
}

//-----过滤字符串中的emoji
+ (NSString *)disable_emoji:(NSString *)text {
   NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"[^\\u0020-\\u007E\\u00A0-\\u00BE\\u2E80-\\uA4CF\\uF900-\\uFAFF\\uFE30-\\uFE4F\\uFF00-\\uFFEF\\u0080-\\u009F\\u2000-\\u201f\r\n]"options:NSRegularExpressionCaseInsensitive error:nil];
   NSString *modifiedString = [regex stringByReplacingMatchesInString:text
                                                              options:0
                                                                range:NSMakeRange(0, [text length])
                                                         withTemplate:@""];
   return modifiedString;
}


- (void)logoutClean{
    [self removeCashUserDicInfo];
    self.userModel = nil;
}

- (void)yichatUserClient_recordChatObjctUpdateChatListWithChatId:(NSString *)chatId state:(YiChatUpdateChatlistState)state{
    if(chatId && [chatId isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
              dispatch_semaphore_wait(self.chatListStateLock, DISPATCH_TIME_FOREVER);
              
              [_chatListUpdateChatList setObject:[NSNumber numberWithInt:state] forKey:chatId];
              
              dispatch_semaphore_signal(self.chatListStateLock);
          }];
    }
}

- (void)yichatUserClient_recordAllChatObjctUpdateChatListWithState:(YiChatUpdateChatlistState)state{
    [ProjectHelper helper_getGlobalThread:^{
        
        for (int i = 0; i < self.chatListUpdateChatList.allKeys.count; i ++) {
            NSString *chatId = self.chatListUpdateChatList.allKeys[i];
            if(chatId && [chatId isKindOfClass:[NSString class]]){
                if(chatId && [chatId isKindOfClass:[NSString class]]){
                    [self yichatUserClient_recordChatObjctUpdateChatListWithChatId:chatId state:state];
                }
            }
        }
    }];
}

- (void)yichatUserClient_getChatObjctUpdateChatListWithChatId:(NSString *)chatId invocation:(void(^)(YiChatUpdateChatlistState state))invocation{
    if(chatId && [chatId isKindOfClass:[NSString class]]){
        NSNumber *state = self.chatListUpdateChatList[chatId];
        if(state && [state isKindOfClass:[NSNumber class]]){
            invocation(state.intValue);
        }
        else{
            [self yichatUserClient_recordChatObjctUpdateChatListWithChatId:chatId state:YiChatUpdateChatlistStateNeedUpdate];
            invocation(YiChatUpdateChatlistStateNeedUpdate);
        }
    }
    
}

- (void)fetchUserConnectionInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    NSString *userId = YiChatUserInfo_UserIdStr;
    if(userId && [userId isKindOfClass:[NSString class]]){
        
        [[YiChatStorageManager sharedManager] getStorageUserConnectionWithKey:userId handle:^(id  _Nonnull obj) {
            if(obj && [obj isKindOfClass:[YiChatConnectionModel class]]){
                
                if(invocation){
                    invocation(obj,nil);
                }
            }
            else{
                [self updateUserConnectionInvocation:invocation];
            }
        }];
    }
}

- (void)connectionLoadInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    [self fetchUserConnectionInvocation:invocation];
}

- (void)addConnectionFriends:(NSArray *)friendsInfoDic model:(YiChatConnectionModel *)model invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    
    [ProjectHelper helper_getGlobalThread:^{
        
        if(friendsInfoDic && [friendsInfoDic isKindOfClass:[NSArray class]]){
            if(friendsInfoDic.count > 0){
                if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                    
                    NSMutableArray *originData = [model.originDataArr mutableCopy];
                    NSMutableArray *tmpData = [model.originDataArr mutableCopy];
                    
                    if(originData && [originData isKindOfClass:[NSArray class]]){
                        if(originData.count > 0){
                            for (int i = 0; i < friendsInfoDic.count; i ++) {
                                NSDictionary *friendDic = friendsInfoDic[i];
                                
                                
                                
                                if(friendDic && [friendDic isKindOfClass:[NSDictionary class]]){
                                    
                                    
                                    
                                    NSString *deleteId = [NSString stringWithFormat:@"%ld",[friendDic[@"userId"] integerValue]];
                                    if(deleteId && [deleteId isKindOfClass:[NSString class]]){
                                        
                                        BOOL isHas = NO;
                                        for (int j = 0; j < originData.count; j ++) {
                                            NSDictionary *dic = originData[j];
                                            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                                
                                                NSString *userId = [NSString stringWithFormat:@"%ld",[dic[@"userId"] integerValue]];
                                                
                                                if(userId && [userId isKindOfClass:[NSString class]]){
                                                    if([userId isEqualToString:deleteId]){
                                                        isHas = YES;
                                                        if((tmpData.count - 1 >= j) && friendDic){
                                                            [tmpData replaceObjectAtIndex:j withObject:friendDic];
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if(isHas == NO){
                                            [tmpData addObject:friendDic];
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(tmpData && [tmpData isKindOfClass:[NSArray class]]){
                        if(tmpData.count > 0){
                            
                            YiChatConnectionModel *model = [[YiChatConnectionModel alloc] initWithUsersArr:tmpData];
                            if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                                
                                [[YiChatStorageManager sharedManager] storageUserConnection:model withKey:YiChatUserInfo_UserIdStr];
                                invocation(model,nil);
                            }
                        }
                        else{
                            if(invocation){
                                invocation(nil,nil);
                            }
                        }
                    }
                    else{
                        if(invocation){
                            invocation(nil,nil);
                        }
                    }
                }
            }
        }
    }];
}

- (void)addConnectionFriends:(NSArray *)friendsInfoDicArr invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    [self connectionLoadInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            [self addConnectionFriends:friendsInfoDicArr model:model invocation:invocation];
        }
    }];
}

- (void)deleteConnectionFriends:(NSArray *)friendId invocation:(void (^)(YiChatConnectionModel * _Nonnull, NSString * _Nonnull))invocation{
    [self connectionLoadInvocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            [self deleteConnectionFriends:friendId model:model invocation:invocation];
        }
    }];
}

- (void)deleteConnectionFriends:(NSArray *)friendId model:(YiChatConnectionModel *)model invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    
    [ProjectHelper helper_getGlobalThread:^{
        
        if(friendId && [friendId isKindOfClass:[NSArray class]]){
            if(friendId.count > 0){
                
                if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                    
                    NSMutableArray *connectionArr = [NSMutableArray arrayWithCapacity:0];
                    [connectionArr addObjectsFromArray:model.connectionModelArr];
                    
                    NSMutableArray *originDataArr = [NSMutableArray arrayWithCapacity:0];
                    
                    for (int i = 0; i < connectionArr.count; i ++) {
                        
                        NSDictionary *connectDic = connectionArr[i];
                        
                        if(connectDic && [connectDic isKindOfClass:[NSDictionary class]]){
                            
                            NSString *key = connectDic.allKeys.lastObject;
                            
                            if(key && [key isKindOfClass:[NSString class]]){
                                
                                NSArray *userArr = connectDic[key];
                                
                                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                                [tmp addObjectsFromArray:userArr];
                                
                                if(tmp && [tmp isKindOfClass:[NSArray class]]){
                                    
                                    for (int k = 0; k < friendId.count; k ++) {
                                        NSString *friendtmp = friendId[k];
                                        
                                        if(friendtmp && [friendtmp isKindOfClass:[NSString class]]){
                                            
                                            for (int j = 0; j < tmp.count; j ++) {
                                                YiChatUserModel *user = tmp[j];
                                                
                                                if(user && [user isKindOfClass:[YiChatUserModel class]]){
                                                   
                                                    NSString *userId = [user getUserIdStr];
                                                    
                                                    if([userId isEqualToString:friendtmp]){
                                                    
                                                        [tmp removeObjectAtIndex:j];
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                if(tmp.count > 0){
                                    [connectionArr replaceObjectAtIndex:i withObject:@{key:tmp}];
                                }
                                else{
                                    [connectionArr removeObjectAtIndex:i];
                                }
                            }
                        }
                    }
                    
                    
                    dispatch_apply(connectionArr.count, dispatch_get_main_queue(), ^(size_t i ) {
                        
                        NSDictionary *connectDic = connectionArr[i];
                        
                        if(connectDic && [connectDic isKindOfClass:[NSDictionary class]]){
                            
                            NSString *key = connectDic.allKeys.lastObject;
                            
                            if(key && [key isKindOfClass:[NSString class]]){
                                
                                NSArray *userArr = connectDic[key];
                                
                                for (int j = 0; j < userArr.count; j ++) {
                                    YiChatUserModel *user = userArr[j];
                                    
                                    if(user && [user isKindOfClass:[YiChatUserModel class]]){
                                        
                                        NSDictionary *data = [ProjectBaseModel translateObjPropertyToDic:user];
                                        
                                        if(data){
                                            [originDataArr addObject:data];
                                        }
                                    }
                                }
                            }
                        }
                        
                    });
                    
                    
                    YiChatConnectionModel *model = [[YiChatConnectionModel alloc] init];
                    model.originDataArr = originDataArr;
                    model.connectionModelArr = connectionArr;
                    if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                        
                        [[YiChatStorageManager sharedManager] storageUserConnection:model withKey:YiChatUserInfo_UserIdStr];
                        
                        if(invocation){
                            invocation(model,nil);
                        }
                    }
                    else{
                        if(invocation){
                            invocation(nil,nil);
                        }
                    }
                    
                    /*
                    NSMutableArray *originData = [model.originDataArr mutableCopy];
                    if(originData && [originData isKindOfClass:[NSArray class]]){
                        
                        if(originData.count > 0){
                            for (int i = 0; i < friendId.count; i ++) {
                                NSString *deleteId = friendId[i];
                                if(deleteId && [deleteId isKindOfClass:[NSString class]]){
                                    
                                    for (int j = 0; j < originData.count; j ++) {
                                        NSDictionary *dic = originData[j];
                                        if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                            NSString *userId = [NSString stringWithFormat:@"%ld",[dic[@"userId"] integerValue]];
                                            if(userId && [userId isKindOfClass:[NSString class]]){
                                                if([userId isEqualToString:deleteId]){
                                                    if((originData.count - 1) >= j){
                                                        [originData removeObjectAtIndex:j];
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(originData && [originData isKindOfClass:[NSArray class]]){
                        
                        YiChatConnectionModel *model = [[YiChatConnectionModel alloc] initWithUsersArr:originData];
                        
                        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                            
                            [[YiChatStorageManager sharedManager] storageUserConnection:model withKey:YiChatUserInfo_UserIdStr];
                            invocation(model,nil);
                        }
                    }
                    else{
                        if(invocation){
                            invocation(nil,nil);
                        }
                    }
                    */
                }
            }
        }
    }];
}

- (YiChatConnectionModel *)deleteConectionModelData:(YiChatConnectionModel *)model withFriendId:(NSString *)friendId key:(NSString *)removeKey{
    
    if(model && [model isKindOfClass:[YiChatConnectionModel class]] && friendId && [friendId isKindOfClass:[NSString class]] && removeKey && [removeKey isKindOfClass:[NSString class]]){
        
        NSArray *listArr = model.connectionModelArr;
        NSMutableArray *listMutab = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < listArr.count; i ++) {
            NSDictionary *dic = listArr[i];
            NSMutableDictionary *mubDic = [NSMutableDictionary dictionaryWithCapacity:0];
                                           
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                NSString *key = dic.allKeys.lastObject;
                if(key && [key isKindOfClass:[NSString class]]){
                    
                    NSArray *listUser = dic[key];
                    if([key isEqualToString:removeKey]){
                        
                        NSMutableArray *listMutableUser = [listUser mutableCopy];
                        
                        if(listUser && [listUser isKindOfClass:[NSArray class]]){
                            for (int j = 0; j < listUser.count; j ++) {
                                
                                YiChatUserModel *user = listUser[j];
                                if(user && [user isKindOfClass:[YiChatUserModel class]]){
                                    
                                    NSString *userId = [user getUserIdStr];
                                    if(userId && [userId isKindOfClass:[NSString class]]){
                                        if([userId isEqualToString:friendId]){
                                            if((listMutableUser.count - 1) >= j){
                                                [listMutableUser removeObjectAtIndex:j];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if(listMutableUser && [listMutableUser isKindOfClass:[NSArray class]]){
                            if(listMutableUser.count > 0){
                                if(listMutableUser.count > 0){
                                    [mubDic addEntriesFromDictionary:@{key:listMutableUser}];
                                }
                            }
                        }
                    }
                    else{
                        [mubDic addEntriesFromDictionary:@{key:listUser}];
                    }
                }
                else{
                    NSLog(@"%@",key);
                }
            }
            if(mubDic && mubDic.allKeys.count != 0){
                [listMutab addObject:mubDic];
            }
        }
        YiChatConnectionModel *tmpModel = [[YiChatConnectionModel alloc] init];
        tmpModel.connectionModelArr = listMutab;
        
        return tmpModel;
    }
    return nil;
}

- (void)updateConnectionModel:(YiChatConnectionModel *)model{
    if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
        NSString *userid = YiChatUserInfo_UserIdStr;
        if(userid && [userid isKindOfClass:[NSString class]]){
            [ProjectHelper helper_getGlobalThread:^{
               [[YiChatStorageManager sharedManager] storageUserConnection:model withKey:userid];
            }];
        }
    }
}

- (void)updateUserConnectionInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    NSString *userId = YiChatUserInfo_UserIdStr;
    [self addRequestUserConnectionWithUserid:userId invocation:invocation];
    
    [self requestConnection:^(YiChatConnectionModel *model, NSString *error) {
        if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
            
            [[YiChatStorageManager sharedManager] storageUserConnection:model withKey:userId];
            if(invocation){
                invocation(model,nil);
            }
            [self invocationUserConnectionWithModel:model des:error userId:userId];
            
        }
        else{
            if(invocation){
                invocation(model,error);
            }
            [self invocationUserConnectionWithModel:model des:error userId:userId];
        }
    }];
}

- (void)requestConnection:(void(^)(YiChatConnectionModel *model , NSString *error))invocation{
    
    NSDictionary *param = [ProjectRequestParameterModel getFriendListParamWithUserId:YiChatUserInfo_UserIdStr pageNo:[NSString stringWithFormat:@"%d",-1]];
    
    [ProjectRequestHelper getFriendListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSArray * data = obj[@"data"];
                if([data isKindOfClass:[NSArray class]]){
                    if(data.count != 0){
                        
                        if(data.count != 0){
                            YiChatConnectionModel *model = [[YiChatConnectionModel alloc] initWithUsersArr:data];
                            
                            if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                                if(invocation){
                                    invocation(model,nil);
                                }
                                return ;
                            }
                        }
                    }
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                if(invocation){
                    invocation(nil,obj);
                }
                return;
            }
            if(invocation){
                invocation(nil,nil);
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        if(invocation){
            invocation(nil,nil);
        }
    }];
}

- (void)fetchUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation{
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
            if(invocation){
                invocation(self.userModel,nil);
            }
        }
        else{
            [[YiChatStorageManager sharedManager] getStorageUserInfoWithKey:userId handle:^(id  _Nonnull obj) {
                if(obj && [obj isKindOfClass:[YiChatUserModel class]]){
                    if(invocation){
                        invocation(obj,nil);
                    }
                }
                else{
                    [self updateUserInfoWithUserId:userId invocation:invocation];
                }
            }];
        }
    }
}

- (void)updateUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation{
    if(userId && [userId isKindOfClass:[NSString class]]){
        
        [self addRequestUserInfoWithUserid:userId invocation:invocation];
        
        [self requestOtherUserInfo:userId invocation:^(id  _Nonnull data, NSString * _Nullable des) {
            if(data && [data isKindOfClass:[NSDictionary class]]){
                YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:data];
                
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    
                    if(model.userId == YiChatUserInfo_UserId){
                        NSString *token = YiChatUserInfo_Token;
                        YiChatUserModel *tmpModel = self.userModel;
                        self.userModel = model;
                        self.userModel.token = token;
                        
                        NSDictionary *dic = [ProjectBaseModel translateObjPropertyToDic:self.userModel];
                        if(dic && [dic isKindOfClass:[NSDictionary class]]){
                            if(dic.allKeys.count != 0){
                                 [self storageUserDic:dic];
                            }
                            else{
                                self.userModel = tmpModel;
                            }
                        }
                        else{
                            self.userModel = tmpModel;
                        }
                    }
                    
                    [[YiChatStorageManager sharedManager] storageUserInfo:model withKey:userId];
                    if(invocation){
                        invocation(model,nil);
                    }
                    [self invocationUserInfoWithModel:model des:des userId:userId];
                    return ;
                }
                else{
                    if(invocation){
                        invocation(nil,nil);
                    }
                    [self invocationUserInfoWithModel:model des:des userId:userId];
                }
            }
            if(invocation){
                invocation(nil,nil);
            }
        } progress:nil];
    }
    else{
        if(invocation){
            invocation(nil,nil);
        }
    }
}

- (void)updateUserInfoWithModel:(YiChatUserModel *)userModel{
    if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
        [[YiChatStorageManager sharedManager] storageUserInfo:userModel withKey:[NSString stringWithFormat:@"%ld",userModel.userId]];
    }
}

- (void)fetchGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [[YiChatStorageManager sharedManager] getStorageGroupInfoWithKey:groupId handle:^(id  _Nonnull obj) {
            if(obj && [obj isKindOfClass:[YiChatGroupInfoModel class]]){
                
                if(invocation){
                    invocation(obj,nil);
                }
            }
            else{
                [self updateGroupInfoWithGroupId:groupId invocation:invocation];
            }
        }];
    }
}
    
- (void)removeLocalGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSString *)userId groupInfo:(YiChatGroupInfoModel *)model{
    
    if(groupId && [groupId isKindOfClass:[NSString class]] && model && [model isKindOfClass:[YiChatGroupInfoModel class]] && userId && [userId isKindOfClass:[NSString class]]){
        YiChatGroupInfoModel *tmpModel = model.mutableCopy;
        
        NSArray *silenceList = tmpModel.silentList.mutableCopy;
        
        
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
        [tmp addObjectsFromArray:silenceList];
        
        
        for (int i = 0; i < tmp.count; i ++) {
            NSDictionary *dic = tmp[i];
            id obj = dic[@"userId"];
            if(obj){
                if([obj isKindOfClass:[NSString class]]){
                    if([obj isEqualToString:userId]){
                        [tmp removeObjectAtIndex:i];
                    }
                }
                else if([obj isKindOfClass:[NSNumber class]]){
                    if([obj integerValue] == userId.integerValue){
                        [tmp removeObjectAtIndex:i];
                    }
                }
            }
        }
        
        tmpModel.silentList = tmp;
        
        [[YiChatStorageManager sharedManager] storageGroupInfo:tmpModel withKey:groupId];
    }
}
    
- (void)addLocalGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSDictionary *)userInfo groupInfo:(YiChatGroupInfoModel *)model{
     if(groupId && [groupId isKindOfClass:[NSString class]] && model && [model isKindOfClass:[YiChatGroupInfoModel class]] && userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
         
         
         YiChatGroupInfoModel *tmpModel = model.mutableCopy;
         
         NSArray *silenceList = tmpModel.silentList.mutableCopy;
         
         
         NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
         [tmp addObjectsFromArray:silenceList];
         
         NSString *userId = userInfo[@"userId"];
         
         if(userId){
             
             for (int i = 0; i < tmp.count; i ++) {
                 NSDictionary *dic = tmp[i];
                 id obj = dic[@"userId"];
                 if(obj){
                     if([obj isKindOfClass:[NSString class]]){
                         if([obj isEqualToString:userId]){
                             [tmp removeObjectAtIndex:i];
                         }
                     }
                     else if([obj isKindOfClass:[NSNumber class]]){
                         if([obj integerValue] == userId.integerValue){
                             [tmp removeObjectAtIndex:i];
                         }
                     }
                 }
             }
             
             [tmp addObject:userInfo];
             
             tmpModel.silentList = tmp;
             
             [[YiChatStorageManager sharedManager] storageGroupInfo:tmpModel withKey:groupId];
         }
     }
}
    
- (void)updateGroupInfoGroupMemberList:(NSArray *)list groupId:(NSString *)groupId{
     if(groupId && [groupId isKindOfClass:[NSString class]] && list && [list isKindOfClass:[NSArray class]]){
         [self fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
             
             if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                YiChatGroupInfoModel *tmpModel = model.mutableCopy;
                 tmpModel.memberCount = list.count;
                 
                 [[YiChatStorageManager sharedManager] storageGroupInfo:tmpModel withKey:groupId];
                 
             }
         }];
     }
}
    
- (void)updateGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            
            [self addRequestGroupInfoWithGroupid:groupId invocation:invocation];
            
            [self requestGroupInfoWithGroupId:groupId invocation:^(id data, NSString * _Nullable des) {
                
                if(data && [data isKindOfClass:[NSDictionary class]]){
                    YiChatGroupInfoModel *model = [[YiChatGroupInfoModel alloc] initWithDic:data];
    
                    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                        
                       [[YiChatStorageManager sharedManager] storageGroupInfo:model withKey:groupId];
                        
                        NSString *groupId = [model getGroupId];
                        
                        [self dealGroupChatListWithGroupId:groupId model:model];
                        
                        if(invocation){
                            invocation(model,nil);
                        }
                         [self invocationGroupInfoWithModel:model des:des groupId:groupId];
                        return ;
                    }
                    else{
                        if(invocation){
                            invocation(nil,nil);
                        }
                         [self invocationGroupInfoWithModel:model des:des groupId:groupId];
                        return ;
                    }
                }
                if(invocation){
                    invocation(nil,nil);
                }
            } progress:nil];
        }
        else{
            if(invocation){
                invocation(nil,nil);
            }
        }
    }];
}

- (void)dealGroupChatListWithGroupId:(NSString *)groupId model:(YiChatGroupInfoModel *)model{
    if([ZFChatHelper getGroupChatListState:groupId] == NO){
        if(model.lastList && [model.lastList isKindOfClass:[NSArray class]] && groupId && [groupId isKindOfClass:[NSString class]]){
            if(model.lastList.count > 0){
                
                NSMutableArray *arr =[NSMutableArray arrayWithCapacity:0];
                [ZFChatHelper zfChatHeler_deleteOneChatterAllMessagesByChatterId:groupId];
                for (int i = 0; i < model.lastList.count; i ++) {
                    NSDictionary *dic = model.lastList[i];
                   
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        NSNumber *time = dic[@"time"];
                        HTMessage *msg = [[ZFChatManage defaultManager] translateRequestHttpDataToHTMessage:dic];
                        if(time && [time isKindOfClass:[NSNumber class]]){
                            msg.timestamp = [time integerValue];
                        }
                        
                        if(msg && [msg isKindOfClass:[HTMessage class]]){
                            [ZFChatHelper zfChatHeler_insertMessage:msg];
                            [arr addObject:msg];
                        }
                    }
                }
                HTConversation *conversation = [HTConversation new];
                if(arr.count > 0){
                    conversation.lastMessage = arr.lastObject;
                    [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:arr.lastObject];
                }
                else{
                    conversation.lastMessage = nil;
                }
               
                [ZFChatHelper zfCahtHelper_updateLocalConcersationWithConversation:conversation isReadAllMessage:YES];
                
                [ZFChatHelper needUpdateGroupChatListState:groupId state:YES];
            }
        }
    }
}

- (void)fetchGroupMemberslistWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation{
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [[YiChatStorageManager sharedManager] getStorageGroupInfoMemberListWithKey:groupId handle:^(id  _Nonnull obj) {
            if(obj && [obj isKindOfClass:[NSArray class]]){
                if(invocation){
                    invocation(obj,nil);
                }
            }
            else{
                [self updateGroupMemberslistWithGroupId:groupId invocation:invocation];
            }
        }];
    }
}

- (void)updateGroupMemberslistWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation{
    
    [self addRequestUserInfoMemberlistWithGroupId:groupId invocation:invocation];
    
    [self requestGroupMemberslistWithGroupId:groupId pageNo:1 pageSize:10000 invocation:^(NSArray * _Nonnull groupMemberlist, NSString * _Nonnull error) {
        
        if(groupMemberlist && [groupMemberlist isKindOfClass:[NSArray class]]){
            if(groupMemberlist.count > 0){
                [[YiChatStorageManager sharedManager] storageGroupInfoMemberlist:groupMemberlist withKey:groupId];
            }
        }
        if(invocation){
            invocation(groupMemberlist,error);
        }
        
        [self invocationGroupInfoMemberList:groupMemberlist des:error groupId:groupId];
    }];
}

- (void)addRequestUserInfoMemberlistWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *model,NSString *error))invocation{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        NSString *state = self.fetchGroupInfoMemberlistDic[groupId];
        if(state && [state isKindOfClass:[NSString class]]){
            if([state intValue]){
                if(invocation){
                    [self.fetchGroupInfoMemberlistArr addObject:invocation];
                }
                return;
            }
        }
        [self.fetchGroupInfoMemberlistDic setObject:@"1" forKey:groupId];
    }
}

- (void)invocationGroupInfoMemberList:(NSArray *)memberlist des:(NSString *)des groupId:(NSString *)groupId{
    [self.fetchGroupInfoMemberlistDic removeObjectForKey:groupId];
    
    typedef void(^UserBlock)(id data,NSString * _Nullable des);
    
    for (int i = 0; i < self.fetchGroupInfoMemberlistArr.count; i ++) {
        UserBlock  block = self.fetchGroupInfoMemberlistArr[i];
        if(block){
            if(memberlist && [memberlist isKindOfClass:[NSArray class]]){
                block(memberlist,des);
            }
            else{
                block(nil,des);
            }
        }
        if(self.fetchGroupInfoMemberlistArr.count - 1 >= i){
            [self.fetchGroupInfoMemberlistArr removeObject:block];
        }
    }
}


- (void)requestGroupMemberslistQuicklyWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation{
    [self requestGroupMemberslistWithGroupId:groupId pageNo:1 pageSize:20 invocation:invocation];
}

- (void)requestGroupMemberslistWithGroupId:(NSString *)groupId pageNo:(int)pageNo pageSize:(int)pageSize invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation{
    
    NSDictionary *param = [ProjectRequestParameterModel getGroupMemberListParamWithGroupId:groupId pageNo:pageNo pageSize:pageSize];
    
    [ProjectRequestHelper getGroupMemberListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSArray *arr =  obj[@"data"];
                if(arr && [arr isKindOfClass:[NSArray class]]){
                    NSMutableArray *userList = [NSMutableArray arrayWithCapacity:0];
                    
                    __block dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
                    
                    
                    for (int i = 0; i < arr.count; i ++) {
                        id obj = arr[i];
                        if([obj isKindOfClass:[NSDictionary class]] && obj){
                            
                            YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:obj];
                            
                            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                
                                [[YiChatStorageManager sharedManager] getStorageUserInfoWithKey:[model getUserIdStr] handle:^(id  _Nonnull userObj) {
                                    
                                    if(userObj && [userObj isKindOfClass:[YiChatUserModel class]]){
                                        [userList addObject:userObj];
                                    }
                                    else{
                                        [userList addObject:model];
                                    }
                                    dispatch_semaphore_signal(semaphore);
                                }];
                                
                                dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
                                
                            }
                           
                        }
                        
                    }
                    
                    
                    if(invocation){
                        invocation(userList,nil);
                    }
                    
                }
                else{
                    if(invocation){
                        invocation(nil,@"获取群组成员列表出错");
                    }
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                if(invocation){
                    invocation(nil,obj);
                }
            }
            else{
                if(invocation){
                    invocation(nil,@"请求出错");
                }
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        if(invocation){
            invocation(nil,nil);
        }
    }];
}

- (void)addRequestUserConnectionWithUserid:(NSString *)userId invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation{
    if(userId && [userId isKindOfClass:[NSString class]]){
        if(self.fetchUserConnectionDic[userId]){
            if(invocation){
                [self.fetchUserConnectionArr addObject:invocation];
            }
            return;
        }
        [self.fetchUserConnectionDic setObject:@"1" forKey:userId];
    }
}

- (void)invocationUserConnectionWithModel:(YiChatConnectionModel *)model des:(NSString *)des userId:(NSString *)userId{
    [self.fetchUserConnectionDic removeObjectForKey:userId];
    
    typedef void(^UserBlock)(id data,NSString * _Nullable des);
    
    for (int i = 0; i < self.fetchUserConnectionArr.count; i ++) {
        UserBlock  block = self.fetchUserConnectionArr[i];
        if(block){
            if(model && [model isKindOfClass:[YiChatConnectionModel class]]){
                block(model,des);
            }
            else{
                block(nil,des);
            }
        }
        if(self.fetchUserConnectionArr.count - 1 >= i){
            [self.fetchUserConnectionArr removeObject:block];
        }
    }
}

- (void)addRequestUserInfoWithUserid:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation{
    if(userId && [userId isKindOfClass:[NSString class]]){
        if(self.fetchUserInfoDic[userId]){
            if(invocation){
                [self.fetchUserInfoArr addObject:invocation];
            }
            return;
        }
        [self.fetchUserInfoDic setObject:@"1" forKey:userId];
    }
}

- (void)invocationUserInfoWithModel:(YiChatUserModel *)model des:(NSString *)des userId:(NSString *)userId{
    [self.fetchUserInfoDic removeObjectForKey:userId];
    
    typedef void(^UserBlock)(id data,NSString * _Nullable des);
    
    for (int i = 0; i < self.fetchUserInfoArr.count; i ++) {
        UserBlock  block = self.fetchUserInfoArr[i];
        if(block){
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                block(model,des);
            }
            else{
                block(nil,des);
            }
        }
        if(self.fetchUserInfoArr.count - 1 >= i){
            [self.fetchUserInfoArr removeObject:block];
        }
    }
}

- (void)addRequestGroupInfoWithGroupid:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        if(self.fetchGroupInfoDic[groupId]){
            if(invocation){
                [self.fetchGroupInfoArr addObject:invocation];
            }
            return;
        }
        [self.fetchGroupInfoDic setObject:@"1" forKey:groupId];
    }
}

- (void)invocationGroupInfoWithModel:(YiChatGroupInfoModel *)model des:(NSString *)des groupId:(NSString *)groupId{
    [self.fetchGroupInfoDic removeObjectForKey:groupId];
    
    typedef void(^GroupBlock)(id data,NSString * _Nullable des);
    
    for (int i = 0; i < self.fetchGroupInfoArr.count; i ++) {
        GroupBlock  block = self.fetchGroupInfoArr[i];
        if(block){
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                block(model,des);
            }
            else{
                block(nil,des);
            }
        }
        if(self.fetchGroupInfoArr.count - 1 >= i){
            [self.fetchGroupInfoArr removeObject:block];
        }
    }
}

- (void)updateGroupInfoWithModel:(YiChatGroupInfoModel *)model invocation:(void(^)(BOOL isSuccess))invocation{
    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
        if([model getGroupId] && [[model getGroupId] isKindOfClass:[NSString class]]){
            if(invocation){
                invocation(YES);
            }
            [[YiChatStorageManager sharedManager] storageGroupInfo:model withKey:[model getGroupId]];
            return;
        }
    }
    if(invocation){
        invocation(NO);
    }
}

- (void)judgeFriendshipWithFriendId:(NSString *)friendId invocation:(void(^)(NSString * frinedShip))invocation{
    if(friendId && [friendId isKindOfClass:[NSString class]]){
        [self requestJudgeFriendshipWithFriendId:friendId invocation:^(NSString *friendShip) {
            if(invocation){
                invocation(friendShip);
            }
        }];
    }
    else{
        if(invocation){
            invocation(nil);
        }
    }
}

- (void)judgeUserSelfRoleInGroup:(NSString *)groupId  invocation:(void(^)(NSString *role))invocation{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group =  [ZFGroupHelper getHTGroupWithGroupId:groupId];
        if(group && [group isKindOfClass:[HTGroup class]]){
            NSString *owner = group.owner;
            if(owner && [owner isKindOfClass:[NSString class]]){
                
                if([owner isEqualToString:YiChatUserInfo_UserIdStr]){
                    //群主
                    if(invocation){
                        invocation(@"2");
                    }
                }
                else{
                    [self fetchUserRoleInGroup:groupId invocation:invocation];
                }
            }
            else{
                [self fetchUserRoleInGroup:groupId invocation:invocation];
            }
        }
        else{
            if(invocation){
                invocation(@"0");
            }
        }
    }
}

- (void)fetchUserRoleInGroup:(NSString *)groupId invocation:(void(^)(NSString *role))invocation{
    WS(weakSelf);
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [self fetchGroupInfoWithGroupId:groupId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                if(invocation){
                    invocation([NSString stringWithFormat:@"%ld",model.roleType]);
                }
            }
            else{
                if(invocation){
                    invocation(@"0");
                }
            }
        }];
    }
    else{
        if(invocation){
            invocation(@"0");
        }
    }
}

- (void)fetchUserCreateGroupAuthInvocation:(void(^)(BOOL isHasAuth,NSString *des))invocation{
    
    NSInteger power = self.createGroupPower;
    if(power){
        invocation(YES,nil);
    }
    else{
        invocation(NO,@"管理员设置了只有指定用户方可建群");
    }
//    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
//    [ProjectRequestHelper groupAuthCreateWithParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
//
//    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
//        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
//            if([obj isKindOfClass:[NSDictionary class]]){
//                invocation(YES,nil);
//                return;
//
//
//            }else if([obj isKindOfClass:[NSString class]]){
//                invocation(NO,obj);
//                return;
//            }
//            invocation(NO,@"获取权限出错");
//            return;
//
//        }];
//    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
//        invocation(NO,@"获取权限出错");
//    }];
//
}



- (void)fetchGroupManagerListWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *managerList))invocation{
    
    __block NSMutableArray *temp = [NSMutableArray arrayWithCapacity:0];
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        [self requestManagerListWithGroupId:groupId invocation:^(NSArray *managerList) {
            if(managerList && [managerList isKindOfClass:[NSArray class]]){
                
                if(managerList.count > 0){
                    YiChatUserModel *groupOwner = nil;
                    
                    if(temp && [temp isKindOfClass:[NSArray class]]){
                        if(temp.count > 0){
                            groupOwner = temp.lastObject;
                        }
                    }
                    
                    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                    [arr addObjectsFromArray:managerList];
                    
                    if(groupOwner && [groupOwner isKindOfClass:[YiChatUserModel class]]){
                        
                        for (int i = 0; i < arr.count; i ++) {
                            YiChatUserModel *modeltmp = managerList[i];
                            if(modeltmp && [modeltmp isKindOfClass:[YiChatUserModel class]]){
                                if(modeltmp.userId == groupOwner.userId && (arr.count - 1) >= i){
                                    [arr removeObjectAtIndex:i];
                                }
                            }
                        }
                    }
                    
                    if(arr.count > 0){
                        [temp addObjectsFromArray:arr];
                    }
                }
                if(invocation){
                    invocation(temp);
                }
                return ;
            }
            if(invocation){
                invocation(temp);
            }
        }];
    }
    else{
        if(invocation){
            invocation(temp);
        }
    }
}

- (void)setGroupSilenceWithGroupId:(NSString *)groupId state:(NSInteger)state invocation:(void(^)(BOOL isSuccess,NSString *des))invocation{
    
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        NSDictionary *param = [ProjectRequestParameterModel setGroupSilenceParamWithGroupId:groupId status:state];
        
        [ProjectRequestHelper setGroupSilenceRequestWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
                if([obj isKindOfClass:[NSDictionary class]]){
                    invocation(YES,nil);
                }
                else if([obj isKindOfClass:[NSString class]]){
                    invocation(NO,obj);
                }
                else{
                    invocation(NO,@"接口异常");
                }
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
             invocation(NO,@"网络异常");
        }];
    }
    else{
        invocation(NO,@"群参数出错");
    }
}

- (void)requestManagerListWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *managerList))invocation{
    if(!(groupId && [groupId isKindOfClass:[NSString class]])){
        if(invocation){
            invocation(nil);
        }
        return;
    }
    
    [ProjectRequestHelper getGroupManagerListWithParameters:[ProjectRequestParameterModel getGroupManagerlistParamWithGroupId:groupId] headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                [ProjectHelper helper_getMainThread:^{
                    NSArray *arr =  obj[@"data"];
                    if(arr && [arr isKindOfClass:[NSArray class]]){
                        NSMutableArray *userList = [NSMutableArray arrayWithCapacity:0];
                        [arr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                            if([obj isKindOfClass:[NSDictionary class]] && obj){
                                YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:obj];
                                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                    [userList addObject:model];
                                }
                            }
                        }];
                        
                        if(userList.count > 0){
                            if(invocation){
                                invocation(userList);
                            }
                            return ;
                        }
                        else{
                            if(invocation){
                                invocation(nil);
                            }
                            return;
                        }
                       
                    }
                    
                }];
            }
            else if([obj isKindOfClass:[NSString class]]){
                if(invocation){
                    invocation(nil);
                }
                return ;
            }
            else{
                if(invocation){
                    invocation(nil);
                }
                return;
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        if(invocation){
            invocation(nil);
        }
        return ;
    }];
}


- (void)requestJudgeFriendshipWithFriendId:(NSString *)friendId invocation:(void(^)(NSString * frinedShip))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        NSDictionary *param = [ProjectRequestParameterModel getFriendShipParamWithFriendId:friendId];
        
        [ProjectRequestHelper judgeFriendStatusWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                
                if([obj isKindOfClass:[NSDictionary class]]){
                    id data = obj[@"data"];
                    if([data isKindOfClass:[NSString class]]){
                        if(invocation){
                            invocation(data);
                        }
                    }
                    else{
                        if(invocation){
                            invocation(nil);
                        }
                    }
                    return ;
                    
                }
                else {
                    if(invocation){
                        invocation(nil);
                    }
                    return ;
                }
            }];
            
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            if(invocation){
                invocation(nil);
            }
        }];
    }];
}

- (void)requestOtherUserInfo:(NSString *)userId invocation:(void(^)(id data,NSString * _Nullable des))invocation progress:(UIView *)progress{
    if(userId && [userId isKindOfClass:[NSString class]]){
        [self requestUserInfoWithUserId:userId invocation:^(id data, NSString * _Nullable des) {
            if(invocation){
                invocation(data,des);
            }
        } progress:progress];
    }
}

- (void)requestGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(id data,NSString * _Nullable des))invocation progress:(UIView *)progress{
    
    NSDictionary *groupInfoParam = [ProjectRequestParameterModel getGroupInfoParamWithGroupId:groupId];
    
    [ProjectRequestHelper getGroupInfoWithParameters:groupInfoParam headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            id objInvo = nil;
            if([obj isKindOfClass:[NSDictionary class]]){
                id data = obj[@"data"];
                objInvo = data;
                if([data isKindOfClass:[NSDictionary class]]){
                    if(invocation){
                        invocation(data,nil);
                    }
                }
                else{
                    if(invocation){
                        invocation(nil,nil);
                    }
                }
                return ;
                
            }
            else if([obj isKindOfClass:[NSString class]]){
                if(invocation){
                    invocation(nil,obj);
                }
                return ;
            }
            else{
                if(invocation){
                    invocation(nil,nil);
                }
            }
            
        }];
        
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        if(invocation){
            invocation(nil,nil);
        }
    }];
}

- (void)requestUserInfoWithUserId:(NSString *)userId invocation:(void(^)(id data,NSString * _Nullable des))invocation progress:(UIView *)progress{
    NSDictionary *userInfo = [ProjectRequestParameterModel getUserInfoParamWithUserId:userId];
    
    [ProjectRequestHelper getUserInfoWithParameters:userInfo headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            
            if([obj isKindOfClass:[NSDictionary class]]){
                id data = obj[@"data"];
                if([data isKindOfClass:[NSDictionary class]]){
                    if(invocation){
                        invocation(data,nil);
                    }
                }
                else{
                    if(invocation){
                        invocation(nil,nil);
                    }
                }
                return ;
                
            }
            else if([obj isKindOfClass:[NSString class]]){
                if(invocation){
                    invocation(nil,obj);
                }
                return ;
            }
            else{
                if(invocation){
                    invocation(nil,nil);
                }
            }
        }];
        
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        if(invocation){
            invocation(nil,nil);
        }
    }];
}

- (void)storageUserDic:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSData * data  = [NSKeyedArchiver archivedDataWithRootObject:dic];
        
        NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
        
        [user setObject:data forKey:@"userLoginInfo"];
        
        [user synchronize];
    }
}

- (void)storageInputPhone:(NSString *)phone password:(NSString *)password{
    NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
    
    if(phone && [phone isKindOfClass:[NSString class]]){
        [user setObject:phone forKey:@"yichatUserName"];
    }
    if(password && [password isKindOfClass:[NSString class]]){
        [user setObject:password forKey:@"yichatPassword"];
    }
    
    [user synchronize];
}

- (NSString *)getStorageInputPhone{
    NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
    return  [user objectForKey:@"yichatUserName"];
}

- (NSString *)getStorageInputPassword{
    NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
    return  [user objectForKey:@"yichatPassword"];
}

- (void)removeCashUserNamePassword{
    NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
    [user removeObjectForKey:@"yichatUserName"];
    [user removeObjectForKey:@"yichatPassword"];
}

- (void)storageUnreadMessage:(NSArray *)dic{
    [ProjectHelper helper_getGlobalThread:^{
        if(dic && [dic isKindOfClass:[NSArray class]]){
            [[YiChatStorageManager sharedManager] storageUnreadMessage:dic withKey:YiChatUserInfo_UserIdStr];
        }
    }];
}

- (void)getUnreadMessagess:(void(^)(NSArray *dic))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        [[YiChatStorageManager sharedManager] getUnreadMessageWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
            invocation(obj);
        }];
    }];
}

- (void)removeUnreadMessages{
    [ProjectHelper helper_getGlobalThread:^{
        [[YiChatStorageManager sharedManager] storageUnreadMessage:@[] withKey:YiChatUserInfo_UserIdStr];
    }];
}

//chatId 为通知类消息类型 YiChatNotify_FriendApply
- (void)getMessageNotifyDataWithChatId:(NSString *)chatId invocation:(void(^)(id data))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            [[YiChatStorageManager sharedManager] getStorageNotifyWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    if(invocation){
                        invocation(obj[chatId]);
                    }
                }
                else{
                    if(invocation){
                        invocation(nil);
                    }
                }
            }];
        }
        else{
            if(invocation){
                invocation(nil);
            }
        }
    }];
}

- (void)storageMessageNotifyDataWithChatId:(NSString *)chatId obj:(id)objData{
    [ProjectHelper helper_getGlobalThread:^{
        if(chatId && [chatId isKindOfClass:[NSString class]] && objData){
            NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
            [[YiChatStorageManager sharedManager] getStorageNotifyWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    [dic addEntriesFromDictionary:obj];
                    [dic setObject:objData forKey:chatId];
                    
                    [[YiChatStorageManager sharedManager] storageNotify:dic withKey:YiChatUserInfo_UserIdStr];
                }
                else{
                    [dic setObject:objData forKey:chatId];
                    
                    [[YiChatStorageManager sharedManager] storageNotify:dic withKey:YiChatUserInfo_UserIdStr];
                }
            }];
        }
    }];
}

- (void)getMessageShutUpStateWithChatId:(NSString *)chatId invocation:(void(^)(NSString *state))invocation{
    [ProjectHelper helper_getGlobalThread:^{
        
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            [[YiChatStorageManager sharedManager] getStorageMessageShutUpWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    if(invocation){
                        
                        NSString *state = obj[chatId];
                        if(state && [state isKindOfClass:[NSString class]]){
                             invocation(state);
                            return ;
                        }
                        else{
                            if(invocation){
                                invocation(@"0");
                            }
                        }
                       
                    }
                }
                else{
                    if(invocation){
                        invocation(@"0");
                    }
                }
            }];
        }
        else{
            if(invocation){
                invocation(@"0");
            }
        }
    }];
}

- (void)storageMessageShutUpStateWithChatId:(NSString *)chatId state:(NSString *)state{
    [ProjectHelper helper_getGlobalThread:^{
        if(chatId && [chatId isKindOfClass:[NSString class]] && state && [state isKindOfClass:[NSString class]]){
            
            NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
            
            [[YiChatStorageManager sharedManager] getStorageMessageShutUpWithKey:YiChatUserInfo_UserIdStr handle:^(id  _Nonnull obj) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    [dic addEntriesFromDictionary:obj];
                    [dic setObject:state forKey:chatId];
                    
                    [[YiChatStorageManager sharedManager] storageMessageShutUp:dic withKey:YiChatUserInfo_UserIdStr];
                }
                else{
                    [dic setObject:state forKey:chatId];
                      [[YiChatStorageManager sharedManager] storageMessageShutUp:dic withKey:YiChatUserInfo_UserIdStr];
                }
                YiChatServiceClient *client = [YiChatServiceClient defaultChatClient];
                [client updateJGJushTagWithGroup];
            }];
        }
    }];
}


- (NSDictionary *)getCashUserDicInfo{
    NSData *data = [[NSUserDefaults standardUserDefaults] objectForKey:@"userLoginInfo"];
    if([data isKindOfClass:[NSData class]] && data){
        NSDictionary *dic = [NSKeyedUnarchiver unarchiveObjectWithData:data];
        return dic;
    }
    return nil;
}

- (void)removeCashUserDicInfo{
    
    NSUserDefaults*user = [NSUserDefaults standardUserDefaults];
    if([user objectForKey:@"userLoginInfo"]){
        [user removeObjectForKey:@"userLoginInfo"];
    }
    self.createGroupPower = 0;
}

- (void)updateUserModelWithDic:(NSDictionary *)dic{
    if([dic isKindOfClass:[NSDictionary class]]){
        YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:dic];
        if(model){
            if(!model.token && _userModel.token){
                model.token = _userModel.token;
            }
            _userModel = model;
        }
    }
}

- (NSString *)getUserGendarStr{
    if(self.userModel){
        if(self.userModel.gender == 0){
            return @"女";
        }
        else{
            return @"男";
        }
    }
    return @"未知";
}

- (NSString *)getUserIdStr{
    if(self.userModel){
        return [NSString stringWithFormat:@"%ld",self.userModel.userId];
    }
    return @"";
}

- (NSString *)getQRCodeImageString{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    NSString *userId = YiChatUserInfo_UserIdStr;
    if(userId && [userId isKindOfClass:[NSString class]]){
        NSMutableDictionary *dataDic = [NSMutableDictionary dictionaryWithCapacity:0];
        [dataDic setObject:userId forKey:@"userId"];
        
        if(dataDic){
            [dic setObject:dataDic forKey:@"data"];
        }
    }
    [dic setObject:@"2" forKey:@"type"];
    
   
    
    NSString *jsonStr = [ProjectTranslateHelper helper_convertJsonObjToJsonData:dic];
    
    NSString *bodyStr =  [RXAESEncryptor encryptAES:jsonStr key:YiChatProject_NetWork_SecretKey];
    NSString *code = bodyStr;
    if(code && [code isKindOfClass:[NSString class]]){
        return code;
    }
    return nil;
}

//判断二维码是否内部生成的二维码
- (BOOL)judgeQRCodeStringIsAppString:(NSString *)qrcodeString{
    if(qrcodeString && [qrcodeString isKindOfClass:[NSString class]]){
        if(qrcodeString.length > 0){
            NSDictionary *decode = [self decodeQRCodeImageString:qrcodeString];
            
            if(decode && [decode isKindOfClass:[NSDictionary class]]){
                return YES;
            }
        }
    }
    return nil;
}

//解码后的string
- (NSInteger)getQRCodeStringType:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        if([dic.allKeys containsObject:@"type"]){
            return [dic[@"type"] integerValue];
        }
    }
    return -1;
}

- (NSString *)getQRCodeStringUserId:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        if([dic.allKeys containsObject:@"data"]){
            NSDictionary *user = dic[@"data"];
            if(user && [user isKindOfClass:[NSDictionary class]]){
                if([user.allKeys containsObject:@"userId"]){
                    return [NSString stringWithFormat:@"%ld",[user[@"userId"] integerValue]];
                }
            }
        }
    }
    return nil;
}

//加密后的串
- (NSDictionary *)decodeQRCodeImageStringIntoJsonDic:(NSString *)string{
    NSDictionary *dic = [self decodeQRCodeImageString:string];
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        return dic;
    }
    /*
    if(decodeString && [decodeString isKindOfClass:[NSString class]]){
        if(decodeString.length > 0){
            
            NSString *jsonString = decodeString;
            
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\t" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\r" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\0" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\r\n" withString:@""];
            
            NSData *jsonData = [jsonString dataUsingEncoding:NSASCIIStringEncoding];
            
            if(jsonData && [jsonData isKindOfClass:[NSData class]]){
                if(jsonData.length > 0){
                    NSError *err;
                    
                    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                         
                                                                        options:NSJSONReadingAllowFragments
                                         
                                                                          error:&err];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        return dic;
                    }
                }
            }
        }
    }
    */
    return nil;
}

- (NSDictionary *)decodeQRCodeImageString:(NSString *)string{
    if(string && [string isKindOfClass:[NSString class]]){
        NSDictionary *dic = [RXAESEncryptor decryptAES:string key:YiChatProject_NetWork_SecretKey];
        if(dic && [dic isKindOfClass:[NSDictionary class]]){
            return dic;
        }
        
        return nil;
        
    }
    return nil;
}
@end
