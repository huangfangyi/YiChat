//
//  YiChatGroupInfoModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupInfoModel : ProjectBaseModel

/**
 群组id
 */
@property (nonatomic ,strong)NSString *groupId;

/**
 群组名称
 */
@property (nonatomic ,strong)NSString *groupName;

/**
 群描述
 */
@property (nonatomic ,strong)NSString *groupDescription;

/**
 群头像
 */
@property (nonatomic ,strong)NSString *groupAvatar;

@property (nonatomic,assign) NSInteger memberCount;

/**
 群主id
 */
@property (nonatomic, strong)NSString *owner;

@property (nonatomic,strong) NSString *crateUnixTime;

@property (nonatomic,strong) NSString *version;

@property (nonatomic,strong) NSArray *adminList;

@property (nonatomic,strong) NSArray *silentList;

@property (nonatomic,assign) NSInteger roleType;

@property (nonatomic,strong) NSArray *lastList;

@property (nonatomic,assign) NSInteger groupSilentStatus;

- (id)initWithGroupListInfoDic:(NSDictionary *)dic;

- (id)initWithDic:(NSDictionary *)dic;

- (NSString *)getGroupId;

@end


NS_ASSUME_NONNULL_END
