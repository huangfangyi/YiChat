//
//  HTGroup.h
//  HTMessage
//
//  Created by 非夜 on 2016/12/2.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 群组实例
 */
@interface HTGroup : NSObject

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

/**
 群主id
 */
@property (nonatomic, strong)NSString *owner;


@end
