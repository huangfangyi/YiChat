//
//  ProjectJudgeHelper.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectJudgeHelper : NSObject


+ (NSString *)helper_judgePhone:(NSString *)phoneNum;

+ (NSString *)helper_judgePassword:(NSString *)password;

/**
 *  字符仅数字或字母
 */
+ (BOOL)helper_InputOnlyNumOrChracter:(NSString *)inputChracter;

/**
 *  判断字符是否为中文
 */
+ (BOOL)helper_InputChinese:(NSString *)inputChracter;

/**
 *  判断字符是否为数字
 */
+ (BOOL)helper_InputNum:(NSString *)inputChracter;

/**
 *  判断是否为字母
 */
+ (BOOL)helper_InputChracter:(NSString *)inputChracter;

+ (BOOL)helper_judgeStrIsFullOfCharacters:(NSString *)characters;

+ (BOOL)helper_judgeStrIsNill:(NSString *)str;

+ (BOOL)helper_judgeStrLengthIsZero:(NSString *)str;

+ (BOOL )helper_judgeStrLengthMoreThanZero:(NSString *)str;

/**
 *  判断字符串是否为纯数字
 */
+ (BOOL )helper_judgeStrIsFullOfNumber:(NSString *)str;

@end

NS_ASSUME_NONNULL_END
