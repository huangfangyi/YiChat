//
//  ProjectTranslateHelper.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectTranslateHelper.h"
#import "YRGeneralApis.h"
#import "ProjectDef.h"
#import "ProjectJudgeHelper.h"

@implementation ProjectTranslateHelper

/**
 *  从汉子，拼音，英文str中获取首字母 返回的首字母大写
 */
+ (NSString *)helper_getFirstCharacterFromStr:(NSString *)aString{
    if([aString isEqualToString:@""]){
        return @"*";
    }
    //转成了可变字符串
    NSMutableString *str = [NSMutableString stringWithString:aString];
    //先转换为带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformMandarinLatin,NO);
    //再转换为不带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformStripDiacritics,NO);
    //转化为大写拼音
    NSString *pinYin = [str capitalizedString];
    //获取并返回首字母
    
    NSString *strReturn =  [pinYin substringToIndex:1];
    if(![ProjectJudgeHelper helper_judgeStrIsFullOfCharacters:strReturn]){
        return @"*";
    }
    else{
        return strReturn;
    }
}

/**
 *  获取索引数据源 @{@"":@[]}
 */
+ (NSArray *)helper_getIndexArrWithArr:(NSArray *)objArr key:(NSString *)key{
    WS(weakSelf);
    __block  NSMutableArray *dataSouce = [NSMutableArray arrayWithCapacity:0];
    
    if(objArr.count != 0){
        NSMutableArray *sectionArr = [NSMutableArray arrayWithCapacity:0];
        //遍历返回的好友列表数据
        [objArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            NSString *sectionName = [self helper_getFirstCharacterFromStr:obj[key]];
            
            if(sectionName != nil){
                [sectionArr addObject:sectionName];
            }
        }];
        
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
        
        [sectionArr enumerateObjectsUsingBlock:^(id  _Nonnull obj1, NSUInteger idx, BOOL * _Nonnull stop) {
            [dic setObject:obj1 forKey:obj1];
        }];
        
        [sectionArr removeAllObjects];
        [sectionArr addObjectsFromArray:dic.allKeys];
        
        for (int i = 0; i < sectionArr.count; ++i) {
            
            //遍历数组的每一个`索引`（不包括最后一个,因为比较的是j+1）
            for (int j = 0; j < sectionArr.count-1; ++j) {
                
                //根据索引的`相邻两位`进行`比较`
                if ([sectionArr[j] compare:sectionArr[j+1] options:NSLiteralSearch] == NSOrderedDescending) {
                    
                    [sectionArr exchangeObjectAtIndex:j withObjectAtIndex:j+1];
                }
                
            }
        }
        
        [sectionArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *name = obj;
            
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            [objArr enumerateObjectsUsingBlock:^(id  _Nonnull obj1, NSUInteger idx, BOOL * _Nonnull stop) {
                NSString *tempName = [self helper_getFirstCharacterFromStr:obj1[key]];
                
                if([name isEqualToString:tempName]){
                    
                    if(obj1 != nil){
                        [arr addObject:obj1];
                    }
                }
            }];
            if(name != nil && arr!= nil){
                [dataSouce addObject:@{name:arr}];
            }
        }];
    }
    
    return dataSouce;
}

/**
 *  获取索引数据源 @{@"":@[]}
 */
+ (NSArray *)helper_getIndexArrWithFriendModelArr:(NSArray *)objArr{
    WS(weakSelf);
    __block  NSMutableArray *dataSouce = [NSMutableArray arrayWithCapacity:0];
    
    if(objArr.count != 0){
        NSMutableArray *sectionArr = [NSMutableArray arrayWithCapacity:0];
        //遍历返回的好友列表数据
        [objArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            NSString *sectionName = nil;
            
            if(sectionName != nil){
                [sectionArr addObject:sectionName];
            }
        }];
        
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
        
        [sectionArr enumerateObjectsUsingBlock:^(id  _Nonnull obj1, NSUInteger idx, BOOL * _Nonnull stop) {
            [dic setObject:obj1 forKey:obj1];
        }];
        
        [sectionArr removeAllObjects];
        [sectionArr addObjectsFromArray:dic.allKeys];
        
        for (int i = 0; i < sectionArr.count; ++i) {
            
            //遍历数组的每一个`索引`（不包括最后一个,因为比较的是j+1）
            for (int j = 0; j < sectionArr.count-1; ++j) {
                
                //根据索引的`相邻两位`进行`比较`
                if ([sectionArr[j] compare:sectionArr[j+1] options:NSLiteralSearch] == NSOrderedDescending) {
                    
                    [sectionArr exchangeObjectAtIndex:j withObjectAtIndex:j+1];
                }
                
            }
        }
        
        [sectionArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *name = obj;
            
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            [objArr enumerateObjectsUsingBlock:^(id  _Nonnull obj1, NSUInteger idx, BOOL * _Nonnull stop) {
                NSString *tempName = nil;
                
                if([name isEqualToString:tempName]){
                    
                    if(obj1 != nil){
                        [arr addObject:obj1];
                    }
                }
            }];
            if(name != nil && arr!= nil){
                [dataSouce addObject:@{name:arr}];
            }
        }];
    }
    
    return dataSouce;
}

//+ (NSAttributedString *)helper_getChatTextWithString:(NSString *)string font:(UIFont *)font{
//    return [YRGeneralApis yrGeneralApis_tranlateStringToAttributedString:string font:font];
//}


+ (NSDictionary *)helper_dictionaryWithJsonString:(NSString *)jsonString {
    
    if (jsonString == nil) {
        
        return nil;
        
    }
    
    
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    
    NSError *err;
    
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                         
                                                        options:NSJSONReadingMutableContainers
                         
                                                          error:&err];
    if(err) {
        
        NSLog(@"json解析失败：%@",err);
        
        return nil;
        
    }
    
    return dic;
    
}

+ (NSDictionary *)helper_translateObjPropertyToDic:(id )obj{
    
    if(obj){
        if([obj isKindOfClass:[NSDictionary class]]){
            
            return obj;
        }
        else{
            NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
            
            unsigned int count;
            objc_property_t * property = class_copyPropertyList([obj class], &count);
            
            for (int i = 0; i < count; i ++) {
                objc_property_t propertyEntity = property[i];
                
                NSString *name = [[NSString alloc] initWithCString:property_getName(propertyEntity) encoding:NSUTF8StringEncoding];
                if(name && [name isKindOfClass:[NSString class]] && obj){
                    dic[name] = [obj valueForKey:name];
                }
            }
            
            return dic;
        }
    }
    return nil;
}

+ (NSString *)helper_convertJsonObjToJsonData:(NSDictionary *)dict
{
    if(dict && [dict isKindOfClass:[NSDictionary class]]){
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
        NSString *jsonString;
        if (!jsonData) {
            NSLog(@"%@",error);
        }else{
            jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
        }
        NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];
        NSRange range = {0,jsonString.length};
        //去掉字符串中的空格
        [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];
        NSRange range2 = {0,mutStr.length};
        //去掉字符串中的换行符
        [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range2];
        return mutStr;
    }
    else{
        return nil;
    }
    
}

+ (NSString *)helper_securityPhoneNumWithPhone:(NSString *)number{
    if([ProjectJudgeHelper helper_judgePhone:number] == nil){
        NSString *numberString = [number stringByReplacingCharactersInRange:NSMakeRange(3, 4) withString:@"****"];
        return numberString;
    }
    else{
        return @"";
    }
}


@end
