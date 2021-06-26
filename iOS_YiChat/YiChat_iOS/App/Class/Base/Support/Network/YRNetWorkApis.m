//
//  YRNetWorkApis.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/17.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "YRNetWorkApis.h"

@implementation YRNetWorkApis

+ (NSString *)yrNetWorkApisTranslateKeyValuePairsToURLConnectCharaters:(NSDictionary *)parameters{
    NSMutableArray *keyArr=[NSMutableArray arrayWithCapacity:0];
    [keyArr addObjectsFromArray:[parameters allKeys]];
    
    for (int i=0; i<keyArr.count ; i++) {
        
        for (int j=0; j<keyArr.count - i - 1; j++) {
            NSString *key_i=keyArr[j];
            NSString *key_j=keyArr[j+1];
            if([key_i compare:key_j] >0){
                [keyArr exchangeObjectAtIndex:j withObjectAtIndex:j+1];
            }
        }
    }
    
    NSMutableArray *valueArr=[NSMutableArray arrayWithCapacity:0];
    
    for (int i=0; i<keyArr.count; i++) {
        NSString *key=keyArr[i];
        id object = parameters[key];
    
        if([object isKindOfClass:[NSString class]]){
            [valueArr addObject:parameters[key]];
        }
        else if([object isKindOfClass:[NSNumber class]]){
             NSString *str = nil;
            if ([object isKindOfClass:NSClassFromString(@"__NSCFNumber")]) {
                
                if (strcmp([object objCType], @encode(float)) == 0)
                {
                    str = [NSString stringWithFormat:@"%f",[object floatValue]];
                }
                else if (strcmp([object objCType], @encode(double)) == 0)
                {
                    str = [NSString stringWithFormat:@"%f",[object doubleValue]];
                }
                else if (strcmp([object objCType], @encode(int)) == 0)
                {
                    str = [NSString stringWithFormat:@"%d",[object intValue]];
                }
                else if (strcmp([object objCType], @encode(long)) == 0)
                {
                    str = [NSString stringWithFormat:@"%ld",[object integerValue]];
                }
            }
            if(str){
                [valueArr addObject:str];
            }
        }
        else {
            NSError *error = nil;
            NSData *data = [NSJSONSerialization dataWithJSONObject:object options:NSJSONWritingPrettyPrinted error:&error];
            NSString *jsonstr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            if(jsonstr != nil){
                [valueArr addObject:jsonstr];
            }
            else{
                [valueArr addObject:@""];
            }
        }
    }
    
    NSMutableString *keyValue=[NSMutableString stringWithCapacity:0];
    for (int i=0; i<keyArr.count; i++) {
        if(i!=keyArr.count - 1){
            [keyValue appendFormat:@"%@=%@&",keyArr[i],valueArr[i]];
        }
        else{
            [keyValue appendFormat:@"%@=%@",keyArr[i],valueArr[i]];
        }
    }
    return keyValue;
}

#pragma mark 时间戳转时间
+ (NSString *)timeStrIntoTimeStr:(NSString *)timeStr{
    NSInteger num = timeStr.integerValue/1000;
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"];
    
    NSDate*confromTimesp = [NSDate dateWithTimeIntervalSince1970:num];
    NSString*confromTimespStr = [formatter stringFromDate:confromTimesp];
    return confromTimespStr;
}

@end
