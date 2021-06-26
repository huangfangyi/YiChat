//
//  YiChatConnectionModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatConnectionModel : ProjectBaseModel

@property (nonatomic,strong) NSArray *originDataArr;

/**
 *  @[ @{ @"S": @[ userModel } ]
 */
@property (nonatomic,strong) NSArray *connectionModelArr;

- (id)initWithUsersArr:(NSArray *)arr;

- (NSArray *)getUserModels;

+ (NSDictionary *)translateUserDicToConnectionEntityData:(NSDictionary *)userDic;
@end

NS_ASSUME_NONNULL_END
