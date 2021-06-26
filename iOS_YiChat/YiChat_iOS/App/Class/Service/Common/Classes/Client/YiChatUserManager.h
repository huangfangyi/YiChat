//
//  YiChatUserManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YiChatUserModel.h"
#import "YiChatGroupInfoModel.h"
#import <UIKit/UIKit.h>
#import "YiChatConnectionModel.h"
NS_ASSUME_NONNULL_BEGIN

#define YiChatUserInfo_Token  [[YiChatUserManager defaultManagaer] userModel].token
#define YiChatUserInfo_UserId [[YiChatUserManager defaultManagaer] userModel].userId
#define YiChatUserInfo_UserIdStr [[YiChatUserManager defaultManagaer] getUserIdStr]
#define YiChatUserInfo_Nick  [[YiChatUserManager defaultManagaer] userModel].nick
#define YiChatUserInfo_Avatar [[YiChatUserManager defaultManagaer] userModel].avatar
#define YiChatUserInfo_Gender  [[YiChatUserManager defaultManagaer] userModel].gender
#define YiChatUserInfo_GenderStr  [[YiChatUserManager defaultManagaer] getUserGendarStr]
#define YiChatUserInfo_Mobile [[YiChatUserManager defaultManagaer] userModel].mobile
#define YiChatUserInfo_AppId [[YiChatUserManager defaultManagaer] userModel].appId
#define YiChatUserInfo_ImPassword [[YiChatUserManager defaultManagaer] userModel].imPassword

#define YiChatNotify_FriendApply @"YiChatNotify_FriendApply"

typedef NS_ENUM(NSUInteger,YiChatUpdateChatlistState){
    YiChatUpdateChatlistStateNeedUpdate = 0,
    YiChatUpdateChatlistStateUpdated,
};

@interface YiChatUserManager : NSObject

@property (nonatomic,strong) YiChatUserModel *userModel;

@property (nonatomic,assign) NSInteger createGroupPower;
    
@property (nonatomic,strong) NSString *sharedLink;

@property (nonatomic,strong) NSString *sharedContent;
    

+ (id)defaultManagaer;

//-----过滤字符串中的emoji
+ (NSString *)disable_emoji:(NSString *)text;

- (void)logoutClean;

- (void)yichatUserClient_recordChatObjctUpdateChatListWithChatId:(NSString *)chatId state:(YiChatUpdateChatlistState)state;

- (void)yichatUserClient_recordAllChatObjctUpdateChatListWithState:(YiChatUpdateChatlistState)state;

- (void)yichatUserClient_getChatObjctUpdateChatListWithChatId:(NSString *)chatId invocation:(void(^)(YiChatUpdateChatlistState state))invocation;

- (void)updateUserModelWithDic:(NSDictionary *)dic;

- (void)storageUserDic:(NSDictionary *)dic;

- (void)storageUnreadMessage:(NSArray *)dic;

- (void)getUnreadMessagess:(void(^)(NSArray *dic))invocation;

- (void)removeUnreadMessages;

//chatId 为通知类消息类型 YiChatNotify_FriendApply
- (void)getMessageNotifyDataWithChatId:(NSString *)chatId invocation:(void(^)(id data))invocation;

- (void)storageMessageNotifyDataWithChatId:(NSString *)chatId obj:(id)objData;

- (void)getMessageShutUpStateWithChatId:(NSString *)chatId invocation:(void(^)(NSString *state))invocation;

- (void)storageMessageShutUpStateWithChatId:(NSString *)chatId state:(NSString *)state;

- (void)removeCashUserDicInfo;

- (NSDictionary *)getCashUserDicInfo;

- (void)fetchUserConnectionInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (void)connectionLoadInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (void)addConnectionFriends:(NSArray *)friendsInfoDic model:(YiChatConnectionModel *)model invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (void)addConnectionFriends:(NSArray *)friendsInfoDicArr invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (void)deleteConnectionFriends:(NSArray *)friendId invocation:(void (^)(YiChatConnectionModel * _Nonnull, NSString * _Nonnull))invocation;

- (void)deleteConnectionFriends:(NSArray *)friendId model:(YiChatConnectionModel *)model invocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (YiChatConnectionModel *)deleteConectionModelData:(YiChatConnectionModel *)model withFriendId:(NSString *)friendId key:(NSString *)removeKey;

- (void)updateUserConnectionInvocation:(void(^)(YiChatConnectionModel *model,NSString *error))invocation;

- (void)fetchUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation;

- (void)updateUserInfoWithUserId:(NSString *)userId invocation:(void(^)(YiChatUserModel *model,NSString *error))invocation;

- (void)updateUserInfoWithModel:(YiChatUserModel *)userModel;

- (void)fetchGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation;

- (void)updateGroupInfoWithGroupId:(NSString *)groupId invocation:(void(^)(YiChatGroupInfoModel *model,NSString *error))invocation;
    
- (void)removeLocalGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSString *)userId groupInfo:(YiChatGroupInfoModel *)model;
    
- (void)addLocalGroupMemberShutUpWithGroupId:(NSString *)groupId userId:(NSDictionary *)userInfo groupInfo:(YiChatGroupInfoModel *)model;
    
- (void)updateGroupInfoGroupMemberList:(NSArray *)list groupId:(NSString *)groupId;

- (void)fetchGroupMemberslistWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation;

- (void)updateGroupMemberslistWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation;

- (void)requestGroupMemberslistQuicklyWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation;

- (void)requestGroupMemberslistWithGroupId:(NSString *)groupId pageNo:(int)pageNo pageSize:(int)pageSize invocation:(void(^)(NSArray *groupMemberlist,NSString *error))invocation;

- (void)judgeFriendshipWithFriendId:(NSString *)friendId invocation:(void(^)(NSString * frinedShip))invocation;

- (void)updateGroupInfoWithModel:(YiChatGroupInfoModel *)model invocation:(void(^)(BOOL isSuccess))invocation;

- (void)judgeUserSelfRoleInGroup:(NSString *)groupId  invocation:(void(^)(NSString *role))invocation;

- (void)fetchUserCreateGroupAuthInvocation:(void(^)(BOOL isHasAuth,NSString *des))invocation;

- (void)fetchGroupManagerListWithGroupId:(NSString *)groupId invocation:(void(^)(NSArray *managerList))invocation;

- (void)setGroupSilenceWithGroupId:(NSString *)groupId state:(NSInteger)state invocation:(void(^)(BOOL isSuccess,NSString *des))invocation;

- (void)storageInputPhone:(NSString *)phone password:(NSString *)password;

- (NSString *)getStorageInputPhone;

- (NSString *)getStorageInputPassword;

- (void)removeCashUserNamePassword;

- (NSString *)getUserGendarStr;

- (NSString *)getUserIdStr;

- (NSString *)getQRCodeImageString;

- (BOOL)judgeQRCodeStringIsAppString:(NSString *)qrcodeString;

- (NSDictionary *)decodeQRCodeImageStringIntoJsonDic:(NSString *)string;

- (NSInteger)getQRCodeStringType:(NSDictionary *)dic;

- (NSString *)getQRCodeStringUserId:(NSDictionary *)dic;
@end

NS_ASSUME_NONNULL_END
