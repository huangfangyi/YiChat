/*!
@header  HTGroupManager

@abstract 

@author  Created by 非夜 on 16/12/19.

@version 1.0 16/12/19 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
*/

#import <Foundation/Foundation.h>
#import "HTGroup.h"
#import "HTGroupDelegate.h"

/**
 群管理类
 */
@interface HTGroupManager : NSObject

/**
 缓存的群列表
 */
@property(nonatomic,strong)NSArray<HTGroup *> * groups;

/**
 添加回调代理
 
 @param aDelegate 要添加的代理
 @param aDelegateQueue 执行代理方法的队列
 */
- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)aDelegateQueue;

/**
 移除回调代理

 @param aDelegate 要移除的回调代理
 */
- (void)removeDelegate:(id)aDelegate;

/**
 初始化群组列表，会更新缓存
 */
- (void)initGroups;

/**
 创建一个群组
 
 @param aGroup 群组实例
 @param aMembers 添加的用户
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)createGroup:(HTGroup *)aGroup withMembers:(NSArray *)aMembers success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 更新群信息
 
 @param aGroup 群组实例
 @param aNickname 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)updateGroup:(HTGroup *)aGroup withNickname:(NSString *)aNickname success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 通过群id解散一个群
 
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)deleteGroupWithGroupId:(NSString *)aGroupId success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 退出群组
 
 @param aGroupId 群id
 @param aNickname 退出者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)exitGroupWithGroupId:(NSString *)aGroupId withNickname:(NSString *)aNickname success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 给一个群组添加新的成员
 
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param nick     邀请人昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)addMemberWithUserIds:(NSArray *)aMembers andGroupId:(NSString *)aGroupId byUser:(NSString *)nick success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 自己加入群
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param admin 群管理员
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)addMemberWithUserIds:(NSArray *)members andGroupId:(NSString *)groupId andAdmin:(NSString *)admin success:(void (^)(void))success failure:(void (^)(NSError *error))failure;

/**
 移除一名群成员
 
 @param aUserId 被移除的用户id
 @param aGroupId 群id
 @param aNickName 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)deleteMemberWithUserId:(NSString *)aUserId andGroupId:(NSString *)aGroupId andNickname:(NSString *)aNickName success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 
 /**
 新增管理员可移除一名群成员
 
 @param userId 被移除的用户id
 @param groupId 群id
 @param nickName 操作者昵称
 @param success 成功回调
 @param failure 失败回调
 */
- (void)deleteMemberWithUserId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName andAdminId:(NSString *)adminId success:(void (^)(void))success failure:(void (^)(NSError *))failure;

- (void)deleteMembersWithGroupOwnerId:(NSString *)groupOwnerId  userIds:(NSArray *)userIds andGroupId:(NSString *)groupId andNickname:(NSArray *)aNickNames success:(void (^)(void))success failure:(void (^)(NSError *))failure;

/**
 
 /**
 新增管理员 群主可移除一名群成员
 
 @param userId 被移除的用户id
 @param groupId 群id
 @param nickName 操作者昵称
 @param success 成功回调
 @param failure 失败回调
 */
- (void)deleteMemberWithGroupOwnerId:(NSString *)groupOwnerId userId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure;

/**
 
 获取一个群信息
 
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)getSingleGroupInfoWithGroupId:(NSString *)aGroupId  success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 获取自己的群列表，包括群列表会更新数据库和缓存
 
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
- (void)getSelfGroups:(void (^)(NSArray * aGroups))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 通过群id获取内存中的群实例

 @param aGroupId 想要群id
 @return 获取到的群实例
 */
- (HTGroup *)groupByGroupId:(NSString *)aGroupId;


@end
