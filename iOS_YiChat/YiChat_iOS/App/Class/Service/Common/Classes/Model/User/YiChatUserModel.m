//
//  YiChatUserModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatUserModel.h"
#import <objc/message.h>
@interface YiChatUserModel ()<NSCoding,NSMutableCopying>

@property (nonatomic,strong) NSDictionary *originDic;

@property (nonatomic,strong) NSDictionary *fullDic;

@end

@implementation YiChatUserModel
    
- (id)mutableCopyWithZone:(NSZone *)zone{
    YiChatUserModel *model = [[YiChatUserModel allocWithZone:zone] init];
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
    self = [super initWithCoder:aDecoder];
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
    [super encodeWithCoder:aCoder];
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

- (id)initWithDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        [self setValuesForKeysWithDictionary:dic];
        
        NSString *userId = [self getUserIdStr];
        NSString *userName = [self appearName];
        NSString *avrotor = [self avatar];
        if(!(userId && [userId isKindOfClass:[NSString class]])){
            userId = @"";
        }
        if(!(userName && [userName isKindOfClass:[NSString class]])){
            userName = @"";
        }
        if(!(avrotor && [avrotor isKindOfClass:[NSString class]])){
            avrotor = @"";
        }
        
        self.fullDic = dic;
        self.originDic = @{@"userId":userId,@"avatar":avrotor,@"nick":userName};
        
    }
    return self;
}

- (NSString *)userIcon{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.avatar && [model.avatar isKindOfClass:[NSString class]]){
            return model.avatar;
        }
    }
    return @"";
}

- (NSString *)getUserIdStr{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        return [NSString stringWithFormat:@"%ld",model.userId];
    }
    return @"";
}

- (NSString *)userPhone{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.mobile && [model.mobile isKindOfClass:[NSString class]]){
            return model.mobile;
        }
    }
    return @"";
}

- (NSString *)userGendar{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.gender == 1){
            return @"男";
        }
        else{
            return @"女";
        }
    }
    return @"";
}

- (NSString *)appearName{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.remark && [model.remark isKindOfClass:[NSString class]]){
            if(model.remark.length > 0){
                return model.remark;
            }
        }
        if(model.nick && [model.nick isKindOfClass:[NSString class]]){
            if(model.nick.length >0){
                return model.nick;
            }
        }
        if(model.mobile && [model.mobile isKindOfClass:[NSString class]]){
            if(model.mobile.length > 0){
                return model.mobile;
            }
        }
        else{
            return [NSString stringWithFormat:@"%ld",model.userId];
        }
    }
    return @"";
}

- (NSString *)remarkName{
    YiChatUserModel *model = self;
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.remark && [model.remark isKindOfClass:[NSString class]]){
            return model.remark;
        }
    }
    return @"";
}

- (NSString *)realName{
    YiChatUserModel *model = self;
    
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.nick && [model.nick isKindOfClass:[NSString class]]){
            return model.nick;
        }
    }
    return @"";
}

- (NSString *)nickName{
    YiChatUserModel *model = self;
    
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        if(model.nick && [model.nick isKindOfClass:[NSString class]]){
            if(model.nick.length > 0){
                return model.nick;
            }
        }
        if(model.mobile && [model.mobile isKindOfClass:[NSString class]]){
            if(model.mobile.length > 0){
                return model.mobile;
            }
        }
        else{
            return [NSString stringWithFormat:@"%ld",model.userId];
        }
    }
    return @"";
}

//userId,nick,avatar
- (NSDictionary *)getOriginDic{
    return self.originDic;
}

- (NSDictionary *)getFullDic{
    return self.fullDic;
}
@end
