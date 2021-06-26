//
//  YiChatStorageManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatStorageManager : NSObject

+ (id)sharedManager;


- (NSString *)getUserInfoStorageFileItem;

- (void)storageUserInfo:(id)model withKey:(NSString *)key;

- (void)getStorageUserInfoWithKey:(NSString *)key handle:(void(^)(id obj))handle;


- (NSString *)getUserConnectionStorageFileItem;

- (void)storageUserConnection:(id)model withKey:(NSString *)key;

- (void)getStorageUserConnectionWithKey:(NSString *)key handle:(void(^)(id obj))handle;


- (NSString *)getGroupInfoStorageFileItem;

- (void)storageGroupInfo:(id)model withKey:(NSString *)key;

- (void)getStorageGroupInfoWithKey:(NSString *)key handle:(void(^)(id obj))handle;


- (NSString *)getGroupInfoMemberListStorageFileItem;

- (void)storageGroupInfoMemberlist:(id)model withKey:(NSString *)key;

- (void)getStorageGroupInfoMemberListWithKey:(NSString *)key handle:(void(^)(id obj))handle;


- (NSString *)getMessageInfoUnreadMessageStorageFileItem;

- (void)storageUnreadMessage:(id)model withKey:(NSString *)key;

- (void)getUnreadMessageWithKey:(NSString *)key handle:(void(^)(id obj))handle;



- (void)storageNotify:(id)model withKey:(NSString *)key;

- (void)getStorageNotifyWithKey:(NSString *)key handle:(void(^)(id obj))handle;



- (void)storageMessageShutUp:(id)model withKey:(NSString *)key;

- (void)getStorageMessageShutUpWithKey:(NSString *)key handle:(void(^)(id obj))handle;

- (void)storageMessageAlert:(id)model withKey:(NSString *)key;

- (void)removeStorageMessageAlertWithKey:(NSString *)key;

- (void)getStorageMessageAlertWithKey:(NSString *)key handle:(void(^)(id obj))handle;

@end

NS_ASSUME_NONNULL_END
