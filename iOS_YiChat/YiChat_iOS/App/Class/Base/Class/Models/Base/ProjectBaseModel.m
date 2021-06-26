//
//  ProjectBaseModel.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectBaseModel.h"
#import <objc/message.h>

@interface ProjectBaseModel ()<NSCoding>

@end

@implementation ProjectBaseModel

- (id)initWithDic:(NSDictionary *)dic{
    self = [super init];
    if(self){
        [[self class] autoProductPropertyListWithDic:dic];
        
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super init];
    if (self) {
       
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)aCoder {
  
}


- (id)getIvarValueWithName:(NSString *)ivarName{
    unsigned int count = 0 ;
    Ivar *ivars = class_copyIvarList([self class], &count);
    for (int i = 0; i<count; i++) {
        Ivar ivar = ivars[i];
        const char *name = ivar_getName(ivar);
        NSString *key = [NSString stringWithUTF8String:name];
        if([[NSString stringWithFormat:@"%@%@",@"_",ivarName] isEqualToString:key]){
            
            return [self valueForKey:key];
        }
    }
    free(ivars);
    return nil;
}

- (NSNumber *)getIvarNumValueWithName:(NSString *)ivarName{
    unsigned int count = 0 ;
    Ivar *ivars = class_copyIvarList([self class], &count);
    for (int i = 0; i<count; i++) {
        Ivar ivar = ivars[i];
        const char *name = ivar_getName(ivar);
        NSString *key = [NSString stringWithUTF8String:name];
        if([[NSString stringWithFormat:@"%@%@",@"_",ivarName] isEqualToString:key]){
            
            NSNumber *num = (NSNumber *)[self valueForKey:key];
            return num;
            
        }
    }
    free(ivars);
    return nil;
}

+ (void)autoProductPropertyListWithDic:(NSDictionary *)dic{
    
    NSMutableString *proprety = [[NSMutableString alloc] init];
    
    [dic enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        
        NSString *str = nil;
        
        if ([obj isKindOfClass:NSClassFromString(@"__NSCFString")] || [obj isKindOfClass:NSClassFromString(@"NSTaggedPointerString")] || [obj isKindOfClass:NSClassFromString(@"__NSCFConstantString")]) {
            str = [NSString stringWithFormat:@"@property (nonatomic, strong) NSString *%@;",key];
        }
        if ([obj isKindOfClass:NSClassFromString(@"__NSCFNumber")]) {
            
            if (strcmp([obj objCType], @encode(float)) == 0)
            {
                str = [NSString stringWithFormat:@"@property (nonatomic, assign) float %@;",key];
            }
            else if (strcmp([obj objCType], @encode(double)) == 0)
            {
                str = [NSString stringWithFormat:@"@property (nonatomic, assign) double %@;",key];
            }
            else if (strcmp([obj objCType], @encode(int)) == 0)
            {
                str = [NSString stringWithFormat:@"@property (nonatomic, assign) int %@;",key];
            }
            else if (strcmp([obj objCType], @encode(long)) == 0)
            {
                str = [NSString stringWithFormat:@"@property (nonatomic, assign) NSInteger %@;",key];
            }
            else{
                str = [NSString stringWithFormat:@"@property (nonatomic, strong) NSNumber *%@;",key];
            }
            
            
        }
        if ([obj isKindOfClass:NSClassFromString(@"__NSCFArray")] || [obj isKindOfClass:[NSArray class]]) {
            str = [NSString stringWithFormat:@"@property (nonatomic, strong) NSArray *%@;",key];
        }
        
        if ([obj isKindOfClass:NSClassFromString(@"__NSCFDictionary")] || [obj isKindOfClass:[NSDictionary class]]) {
            str = [NSString stringWithFormat:@"@property (nonatomic, strong) NSDictionary *%@;",key];
        }
        if ([obj isKindOfClass:NSClassFromString(@"__NSCFBoolean")]) {
            str = [NSString stringWithFormat:@"@property (nonatomic, assign) BOOL %@;",key];
        }
        
        [proprety appendFormat:@"\n%@\n",str];
    }];
    
//    NSLog(@"%@",proprety);
    
}

+ (void)autoProductDefGetPropertyListValue{
    NSMutableString *str = [NSMutableString stringWithCapacity:0];
    [str appendString:@"begin ----- >"];
    [str appendString:@"\r\n"];
    [self getPropertyListCallBack:^(NSString * _Nonnull propertyName) {
        NSString *defineName = [NSString stringWithFormat:@"#define XYModelHelper_nearByCompany_Info_%@(a) [XYModelHelper   XYModelHelper_getBussinessNearbyModelInfoValueWithKey:%@%@%@%@ model:a]",propertyName,@"@",@"\"",propertyName,@"\""];
        
        [str appendString:defineName];
        [str appendString:@"\r\n"];
    }];
    
    
  //  NSLog(@"%@",str);
}

+ (void)getPropertyListCallBack:(void(^)(NSString *propertyName))callBack{
    unsigned int count;
    objc_property_t *propertys = class_copyPropertyList(self, &count);
    
    for (unsigned int i = 0; i < count; i++) {
        objc_property_t o_t =  propertys[i];
        NSString *propertyName = [NSString stringWithFormat:@"%s", property_getName(o_t)];
        callBack(propertyName);
    }
    free(propertys);
    
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key{
  //  NSLog(@"----->%@ %@:%@",self,key,value);
}

- (void)setNilValueForKey:(NSString *)key{
  //   NSLog(@"----->%@ %@",self,key);
}

- (void)setModelValue:(id)value forKey:(NSString *)key{
    [self setValue:value forKey:key];
}

+ (NSDictionary *)translateObjPropertyToDic:(id )obj{
    
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
@end
