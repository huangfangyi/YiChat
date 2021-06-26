//
//  ZFConnectionModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface ZFConnectionModel : ProjectBaseModel

@property (nonatomic,strong) NSArray *originDataArr;

/**
 *  @[ @{ @"S": @[ NSDic } ]
 */
@property (nonatomic,strong) NSArray *connectionModelArr;

- (id)initWithUsersArr:(NSArray *)arr;

@end

NS_ASSUME_NONNULL_END
