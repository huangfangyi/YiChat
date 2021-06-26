//
//  ProjectBaseModel.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectBaseModel : NSObject

- (instancetype)initWithCoder:(NSCoder *)aDecoder;
//反序列化数据

- (void)encodeWithCoder:(NSCoder *)aCoder;

- (id)initWithDic:(NSDictionary *)dic;

+ (void)autoProductPropertyListWithDic:(NSDictionary *)dic;

+ (void)autoProductDefGetPropertyListValue;

+ (void)getPropertyListCallBack:(void(^)(NSString *propertyName))callBack;

- (void)setValue:(id)value forUndefinedKey:(NSString *)key;

- (id)getIvarValueWithName:(NSString *)ivarName;

- (NSNumber *)getIvarNumValueWithName:(NSString *)ivarName;


- (void)setModelValue:(id)value forKey:(NSString *)key;

+ (NSDictionary *)translateObjPropertyToDic:(id )obj;

@end

NS_ASSUME_NONNULL_END
