/*! 
@header  HTMessage.m

@abstract 

@author  Created by 非夜 on 16/11/25.

@version 1.0 16/11/25 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.

*/

#import "HTMessage.h"

@interface HTMessage ()<NSCoding,NSMutableCopying>

@end

@implementation HTMessage

- (id)mutableCopyWithZone:(NSZone *)zone{
    HTMessage *model = [[HTMessage allocWithZone:zone] init];
    [[[self class] getPropertyNameList:[model class]] enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        [model setValue:[self valueForKey:obj] forKey:obj];
    }];
    return model;
}

+ (NSArray *)getPropertyNameList:(Class)cls {
    NSMutableArray *propertyNameListArray = [NSMutableArray array];
    unsigned int count = 0;
    objc_property_t *properties = class_copyPropertyList(cls, &count);
    for (NSInteger i = 0 ; i < count; i ++) {
        const char *propertyCharName = property_getName(properties[i]);//c的字符串
        NSString *propertyOCName = [NSString stringWithFormat:@"%s",propertyCharName];//转化成oc 字符串
        [propertyNameListArray addObject:propertyOCName];
    }
    NSArray *dataArray = [NSArray arrayWithArray:propertyNameListArray];
    return dataArray;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super init];
    if (self) {
        unsigned int count = 0;
        objc_property_t *propertys = class_copyPropertyList(self.class, &count);
        
        for (NSInteger i = 0 ; i < count; i++) {
            objc_property_t property = propertys[i];
            const char *propertyNameChar = property_getName(property);
            NSString *propertyNameStr = [NSString stringWithUTF8String:propertyNameChar];
            id value = [aDecoder decodeObjectForKey:propertyNameStr];
            [self setValue:value forKey:propertyNameStr];//kvc
        }
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)aCoder {
    unsigned int count = 0;
    objc_property_t *propertys = class_copyPropertyList(self.class, &count);
    for (NSInteger i = 0 ; i < count; i++) {
        objc_property_t property = propertys[i];
        const char *propertyNameChar = property_getName(property);
        NSString *propertyNameStr = [NSString stringWithUTF8String:propertyNameChar];
        id value = [self valueForKey:propertyNameStr];//kvc
        [aCoder encodeObject:value forKey:propertyNameStr];
    }
}

@end
