//
//  QSTools.m
//  HTMessageSDK
//
//  Created by 非夜 on 17/2/15.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "QSTools.h"

@implementation QSTools

+ (NSString *)creatUUID {
    NSString *uuid = [[NSUUID UUID] UUIDString];
    return uuid;
}

/**
  *  将普通字符串转换成base64字符串
  *
  *  @param text 普通字符串
  *
  *  @return base64字符串
  */
+ (NSString *)base64StringFromText:(NSString *)text {
    
    NSData *data = [text dataUsingEncoding:NSUTF8StringEncoding];
    
    NSString *tempBase64String = [data base64EncodedStringWithOptions:0];
    
    NSString *base64String = [tempBase64String stringByReplacingOccurrencesOfString:@"+" withString:@"%2B"];
    
    return base64String;
    
}

@end
