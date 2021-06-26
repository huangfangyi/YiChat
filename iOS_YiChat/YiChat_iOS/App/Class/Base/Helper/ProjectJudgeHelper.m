//
//  ProjectJudgeHelper.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectJudgeHelper.h"

@implementation ProjectJudgeHelper

+ (NSString *)helper_judgePhone:(NSString *)phoneNum{
    if([self helper_judgeStrIsNill:phoneNum] || [self helper_judgeStrLengthIsZero:phoneNum]){
        return @"手机号码不能为空";
    }
    if([self helper_judgeStrLengthMoreThanZero:phoneNum]){
        if(phoneNum.length != 11){
            return @"手机号位数不正确";
        }
        else{
            if(![self helper_judgeStrIsFullOfNumber:phoneNum]){
                return @"手机号必须为纯数字";
            }
            else{
                return nil;
            }
        }
    }
    return @"手机号格式错误";
    
}

+ (NSString *)helper_judgePassword:(NSString *)password{
    //6-20位，由字母数字下划线中划线组成
    if([self helper_judgeStrLengthMoreThanZero:password] == YES){
        if(password.length >=6 && password.length <=20){
            for (int i = 0 ; i <password.length; i ++) {
                NSString *loc = [password substringWithRange:NSMakeRange(i, 1)];
                if([self helper_InputNum:loc] == YES || [self helper_InputChracter:loc] == YES || [loc isEqualToString:@"_"] || [loc isEqualToString:@"-"]){
                }
                else{
                    return @"密码只能由字母数字下划线中划线组成";
                }
            }
            return nil;
        }
        else{
            return @"密码位数不正确";
        }
    }
    else{
        return @"密码不能为空";
    }
}

+ (BOOL)helper_InputOnlyNumOrChracter:(NSString *)inputChracter
{
    
    NSString *regex =@"[a-zA-Z0-9]*";
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    
    if ([pred evaluateWithObject:inputChracter]) {
        
        return YES;
        
    }
    
    return NO;
    
}

+ (BOOL)helper_InputChinese:(NSString *)inputChracter

{
    
    NSString *regex = @"[\u4e00-\u9fa5]+";
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    
    if ([pred evaluateWithObject:inputChracter]) {
        
        return YES;
        
    }
    
    return NO;
    
}

+ (BOOL)helper_InputNum:(NSString *)inputChracter

{
    
    NSString *regex =@"[0-9]*";
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    
    if ([pred evaluateWithObject:inputChracter]) {
        
        return YES;
        
    }
    
    return NO;
    
}


+ (BOOL)helper_InputChracter:(NSString *)inputChracter

{
    
    NSString *regex =@"[a-zA-Z]*";
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    
    if ([pred evaluateWithObject:inputChracter]) {
        
        return YES;
        
    }
    
    return NO;
    
}

+ (BOOL)helper_judgeStrIsNill:(NSString *)str{
    if(str != nil){
        return NO;
    }
    else{
        return YES;
    }
}

+ (BOOL)helper_judgeStrLengthIsZero:(NSString *)str{
    if(str != nil && str.length == 0){
        return YES;
    }
    else{
        return NO;
    }
}

+ (BOOL )helper_judgeStrLengthMoreThanZero:(NSString *)str{
    if(![self helper_judgeStrIsNill:str]){
        if(str.length >0){
            return YES;
        }
    }
    return NO;
}

+ (BOOL )helper_judgeStrIsFullOfNumber:(NSString *)str{
    NSArray *numArr = @[@"0",@"1",@"2",@"3",@"4",@"5",@"6",@"7",@"8",@"9"];
    
    for (int i = 0; i < str.length; i ++) {
        NSString *temp = [str substringWithRange:NSMakeRange(i, 1)];
        if(![numArr containsObject:temp]){
            return NO;
        }
    }
    return YES;
}

+ (BOOL)helper_judgeStrIsFullOfCharacters:(NSString *)characters{
    NSString *regex =@"[a-zA-Z]*";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    if ([pred evaluateWithObject:characters])
    {
        return YES;
        
    }
    return NO;
}


@end
