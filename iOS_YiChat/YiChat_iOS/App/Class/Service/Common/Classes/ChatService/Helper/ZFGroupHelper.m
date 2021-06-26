//
//  ZFGroupHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFGroupHelper.h"
#import "HTGroup.h"
#import "ZFChatManage.h"
#import "ZFChatHelper.h"
#import <objc/message.h>
#import "ZFChatMessageHelper.h"
#import "NSObject+YYModel.h"

@implementation ZFGroupHelper

+ (HTGroupManager *)getGroupManager{
    return  [[ZFChatManage defaultManager] getGroupManager];
}


+ (HTGroup *)getHTGroupWithGroupId:(NSString *)groupId{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroupManager *manager = [self getGroupManager];
        for (HTGroup *group in manager.groups) {
            if(group){
                NSString *groupIdTmp = group.groupId;
                if([groupIdTmp isKindOfClass:[NSString class]] && groupIdTmp){
                    if([groupIdTmp isEqualToString:groupId]){
                        return group;
                    }
                }
            }
        }
    }
    return nil;
}

/**
 创建一个群组
 @param aGroup 群组实例
 @param aMembers 添加的用户
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)createGroup:(HTGroup *)aGroup withMembers:(NSArray *)aMembers success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] createGroup:aGroup withMembers:aMembers success:aSuccess failure:aFailure];
}

/**
 更新群信息
 
 @param aGroup 群组实例
 @param aNickname 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)updateGroup:(HTGroup *)aGroup withNickname:(NSString *)aNickname success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] updateGroup:aGroup withNickname:aNickname success:aSuccess failure:aFailure];
}

/**
 通过群id解散一个群
 
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)deleteGroupWithGroupId:(NSString *)aGroupId success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] deleteGroupWithGroupId:aGroupId success:aSuccess failure:aFailure];
}

/**
 退出群组
 
 @param aGroupId 群id
 @param aNickname 退出者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)exitGroupWithGroupId:(NSString *)aGroupId withNickname:(NSString *)aNickname success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] exitGroupWithGroupId:aGroupId withNickname:aNickname success:aSuccess failure:aFailure];
}

/**
 给一个群组添加新的成员
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param nick     邀请人昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)addMemberWithUserIds:(NSArray *)aMembers andGroupId:(NSString *)aGroupId byUser:(NSString *)nick success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] addMemberWithUserIds:aMembers andGroupId:aGroupId byUser:nick success:aSuccess failure:aFailure];
}

/**
 自己加入群
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param admin 群管理员
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)addMemberWithUserIds:(NSArray *)members andGroupId:(NSString *)groupId andAdmin:(NSString *)admin success:(void (^)(void))success failure:(void (^)(NSError *error))failure{
    [[self getGroupManager] addMemberWithUserIds:members andGroupId:groupId andAdmin:admin success:success failure:failure];
}

/**
 移除一名群成员
 
 @param aUserId 被移除的用户id
 @param aGroupId 群id
 @param aNickName 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)deleteMemberWithUserId:(NSString *)aUserId andGroupId:(NSString *)aGroupId andNickname:(NSString *)aNickName success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] deleteMemberWithUserId:aUserId andGroupId:aGroupId andNickname:aNickName success:aSuccess failure:aFailure];
}


//群主 管理员可以调
+ (void)deleteMemberWithGroupOwnerId:(NSString *)groupOwnerId userId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure{
    [[HTClient sharedInstance].groupManager deleteMemberWithGroupOwnerId:groupOwnerId userId:userId andGroupId:groupId andNickname:aNickName success:success failure:failure];
}

+ (void)deleteMembersWithGroupOwnerId:(NSString *)groupOwnerId  userIds:(NSArray *)userIds andGroupId:(NSString *)groupId andNickname:(NSArray *)aNickNames  success:(void (^)(void))success failure:(void (^)(NSError *))failure{
    [[HTClient sharedInstance].groupManager deleteMembersWithGroupOwnerId:groupOwnerId userIds:userIds andGroupId:groupId andNickname:aNickNames  success:success failure:failure];
}

/**
 
 /**
 新增管理员可移除一名群成员
 
 @param userId 被移除的用户id
 @param groupId 群id
 @param nickName 操作者昵称
 @param success 成功回调
 @param failure 失败回调
 */
+ (void)deleteMemberWithUserId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName andAdminId:(NSString *)adminId success:(void (^)(void))success failure:(void (^)(NSError *))failure{
    [[self getGroupManager] deleteMemberWithUserId:userId andGroupId:groupId andNickname:aNickName andAdminId:adminId success:success failure:failure];
}

/**
 获取一个群信息
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)getSingleGroupInfoWithGroupId:(NSString *)aGroupId  success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] getSingleGroupInfoWithGroupId:aGroupId success:aSuccess failure:aFailure];
}

/**
 获取自己的群列表，包括群列表会更新数据库和缓存
 
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)getSelfGroups:(void (^)(NSArray * aGroups))aSuccess failure:(void (^)(NSError *error))aFailure{
    [[self getGroupManager] getSelfGroups:aSuccess failure:aFailure];
}

/**
 通过群id获取内存中的群实例
 
 @param aGroupId 想要群id
 @return 获取到的群实例
 */
+ (HTGroup *)groupByGroupId:(NSString *)aGroupId{
   return  [[self getGroupManager] groupByGroupId:aGroupId];
}


+ (NSString *)getGroupOwnerIdWithGroupId:(NSString *)groupId{
    if(groupId && [groupId isKindOfClass:[NSString class]]){
        HTGroup *group =  [ZFGroupHelper getHTGroupWithGroupId:groupId];
        if(group.owner && [group.owner isKindOfClass:[NSString class]]){
            return group.owner;
        }
    }
    return nil;
}

+ (void)getGroupRoleWithGroupId:(NSString *)groupId{
    
}

+ (void)judgeGroupIsExsit:(NSString *)groupId invocation:(void(^)(BOOL isExist))invocation{
    HTGroup *group = [self getHTGroupWithGroupId:groupId];
    if(group && [group isKindOfClass:[HTGroup class]]){
        invocation(YES);
    }
    else{
        invocation(NO);
    }
}
    
+ (void)setGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSString *)userId status:(BOOL)isShutUp invocation:(void(^)(BOOL isSuccess,NSString *des))invocation{
    if(groupId && [groupId isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
        
        NSInteger status = 0;
        
        if(isShutUp){
            status = 1;
        }
        else{
            status = 0;
        }
        
        NSDictionary *param = [ProjectRequestParameterModel setgroupMemberShutUpStateParamWithGroupId:[groupId integerValue] userId:[userId integerValue] status:status];
        
         NSDictionary *tokenDic = [ZFChatHelper zfChatHelper_getRequestTokenDic];
        
        [ProjectRequestHelper setGroupMemberSilenceRequestWithParameters:param headerParameters:tokenDic progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if(obj && [obj isKindOfClass:[NSDictionary class]]){
                    invocation(YES,nil);
                    
                     [self setGroupMemberCMDShutUpWithGroupId:groupId userId:userId status:isShutUp];
                }
                else if([obj isKindOfClass:[NSString class]] && obj){
                    invocation(NO,obj);
                }
                else{
                    invocation(NO,@"");
                }
            }];
            
            
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        }];
    }
    else{
        invocation(NO,@"参数出错");
    }
}
    
+ (void)setGroupMemberCMDShutUpWithGroupId:(NSString *)groupId userId:(NSString *)userId status:(BOOL)isShutUp{
    NSString *action = nil;
    
    if(isShutUp){
        action = @"30004";
    }
    else{
        action = @"30005";
    }
    
    NSDictionary * bodyDic = @{
                               @"action":action,
                               @"data":groupId
                               };
    
    HTCmdMessage * cmdMessage = [ZFChatMessageHelper sendCmdMessage:[bodyDic modelToJSONString] to:userId chatType:@"1"];
    [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:^(HTCmdMessage *message, NSError *error) {
        
    }];
}
@end
