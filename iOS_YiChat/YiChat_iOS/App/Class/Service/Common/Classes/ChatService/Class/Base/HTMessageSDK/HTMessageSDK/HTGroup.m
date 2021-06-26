//
//  HTGroup.m
//  HTMessage
//
//  Created by 非夜 on 2016/12/2.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTGroup.h"

@implementation HTGroup

+ (NSDictionary *)modelCustomPropertyMapper {
    return @{
                 @"groupId"  : @"gid",
                 @"owner"  : @"creator",
                 @"groupName"  : @"name",
                 @"groupDescription"  : @"desc",
                 @"groupAvatar"  : @"imgurlde",
             };
}

@end
