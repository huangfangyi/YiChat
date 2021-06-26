//
//  ZFGroupHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class HTGroup;
@interface ZFGroupHelper : NSObject


/**
 创建一个群组
 
 @param aGroup 群组实例
 @param aMembers 添加的用户
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)createGroup:(HTGroup *)aGroup withMembers:(NSArray *)aMembers success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;


+ (HTGroup *)getHTGroupWithGroupId:(NSString *)groupId;

/**
 更新群信息
 
 @param aGroup 群组实例
 @param aNickname 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)updateGroup:(HTGroup *)aGroup withNickname:(NSString *)aNickname success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 通过群id解散一个群
 
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)deleteGroupWithGroupId:(NSString *)aGroupId success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 退出群组
 
 @param aGroupId 群id
 @param aNickname 退出者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)exitGroupWithGroupId:(NSString *)aGroupId withNickname:(NSString *)aNickname success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 给一个群组添加新的成员
 
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param nick     邀请人昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)addMemberWithUserIds:(NSArray *)aMembers andGroupId:(NSString *)aGroupId byUser:(NSString *)nick success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 自己加入群
 @param aMembers 添加的新的成员的id和昵称数组
 @param aGroupId 要添加的群id
 @param admin 群管理员
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)addMemberWithUserIds:(NSArray *)members andGroupId:(NSString *)groupId andAdmin:(NSString *)admin success:(void (^)(void))success failure:(void (^)(NSError *error))failure;


+ (void)deleteMemberWithGroupOwnerId:(NSString *)groupOwnerId userId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure;


+ (void)deleteMembersWithGroupOwnerId:(NSString *)groupOwnerId  userIds:(NSArray *)userIds andGroupId:(NSString *)groupId andNickname:(NSArray *)aNickNames success:(void (^)(void))success failure:(void (^)(NSError *))failure;
/**
 移除一名群成员
 
 @param aUserId 被移除的用户id
 @param aGroupId 群id
 @param aNickName 操作者昵称
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)deleteMemberWithUserId:(NSString *)aUserId andGroupId:(NSString *)aGroupId andNickname:(NSString *)aNickName success:(void (^)(void))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 
 /**
 新增管理员可移除一名群成员
 
 @param userId 被移除的用户id
 @param groupId 群id
 @param nickName 操作者昵称
 @param success 成功回调
 @param failure 失败回调
 */
+ (void)deleteMemberWithUserId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName andAdminId:(NSString *)adminId success:(void (^)(void))success failure:(void (^)(NSError *))failure;

/**
 
 
 获取一个群信息
 
 @param aGroupId 群id
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)getSingleGroupInfoWithGroupId:(NSString *)aGroupId  success:(void (^)(HTGroup *aGroup))aSuccess failure:(void (^)(NSError *error))aFailure;

/**
 获取自己的群列表，包括群列表会更新数据库和缓存
 
 @param aSuccess 成功回调
 @param aFailure 失败回调
 */
+ (void)getSelfGroups:(void (^)(NSArray * aGroups))aSuccess failure:(void (^)(NSError *error))aFailure;
/**
 通过群id获取内存中的群实例
 
 @param aGroupId 想要群id
 @return 获取到的群实例
 */
+ (HTGroup *)groupByGroupId:(NSString *)aGroupId;

+ (NSString *)getGroupOwnerIdWithGroupId:(NSString *)groupId;

+ (void)getGroupRoleWithGroupId:(NSString *)groupId;

+ (void)judgeGroupIsExsit:(NSString *)groupId invocation:(void(^)(BOOL isExist))invocation;
    
+ (void)setGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSString *)userId status:(BOOL)isShutUp invocation:(void(^)(BOOL isSuccess,NSString *des))invocation;
@end

NS_ASSUME_NONNULL_END
