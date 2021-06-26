//
//  QSTools.h
//  HTMessageSDK
//
//  Created by 非夜 on 17/2/15.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTools : NSObject

+ (NSString *)creatUUID;

/**
  *  将普通字符串转换成base64字符串
  *
  *  @param text 普通字符串
  *
  *  @return base64字符串
  */
+ (NSString *)base64StringFromText:(NSString *)text;

@end
