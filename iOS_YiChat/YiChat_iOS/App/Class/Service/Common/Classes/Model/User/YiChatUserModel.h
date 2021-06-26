//
//  YiChatUserModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

#define yiChatGetUserAppearName(model) [model appearName]
#define yiChatGetUserRealName(model) [model realName]
#define yiChatGetUserNickName(model) [model nickName]
@interface YiChatUserModel : ProjectBaseModel

//im密码
@property (nonatomic, strong) NSString *imPassword;

@property (nonatomic, strong) NSString *token;

@property (nonatomic, assign) NSInteger userId;

@property (nonatomic, strong) NSString *nick;

@property (nonatomic, strong) NSString *avatar;

@property (nonatomic, assign) NSInteger gender;

@property (nonatomic, strong) NSString *appId;

@property (nonatomic, strong) NSString *mobile;

@property (nonatomic, strong) NSString *payPasswordStatus;

@property (nonatomic,strong) NSString *remark;

- (NSString *)userIcon;

- (NSString *)getUserIdStr;

- (NSString *)userPhone;

- (NSString *)userGendar;

- (NSString *)appearName;

- (NSString *)remarkName;

- (NSString *)realName;

- (NSString *)nickName;

- (NSDictionary *)getOriginDic;

- (NSDictionary *)getFullDic;

@end

NS_ASSUME_NONNULL_END
