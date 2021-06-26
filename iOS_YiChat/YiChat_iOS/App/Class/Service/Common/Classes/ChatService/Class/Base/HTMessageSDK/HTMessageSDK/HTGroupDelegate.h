/*!
@header  HTGroupDelegate.h

@abstract 

@author  Created by 非夜 on 16/12/27.

@version 1.0 16/12/27 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
*/

#import <Foundation/Foundation.h>

/**
 群相关代理
 */
@protocol HTGroupDelegate <NSObject>

@optional

/**
 群列表更新
 */
- (void)didGroupListUpdatad;

/**
 群信息更新

 @param aGroup 更新过群信息的群实例
 */
- (void)groupInfoChanged:(HTGroup *)aGroup;
    

@end
