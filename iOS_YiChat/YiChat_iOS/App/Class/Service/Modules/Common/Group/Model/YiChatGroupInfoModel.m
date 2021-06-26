//
//  YiChatGroupInfoModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupInfoModel.h"
#import <objc/message.h>

@interface YiChatGroupInfoModel ()<NSCoding,NSMutableCopying>

@end

@implementation YiChatGroupInfoModel
    
- (id)mutableCopyWithZone:(NSZone *)zone{
    YiChatGroupInfoModel *model = [[YiChatGroupInfoModel allocWithZone:zone] init];
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

- (id)initWithGroupListInfoDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        
        NSString *create_date = dic[@"create_date"];
        
        NSString *version = dic[@"version"];
        
        NSString *creator = dic[@"creator"];
        
        NSString *gid = dic[@"gid"];
        
        NSString *name = dic[@"name"];
        
        NSString *desc = dic[@"desc"];
        
        NSString *imgurlde = dic[@"imgurlde"];
        
        if(create_date && [create_date isKindOfClass:[NSString class]]){
            _crateUnixTime = create_date;
        }
        if(version && [version isKindOfClass:[NSString class]]){
            _version = version;
        }
        if(creator && [creator isKindOfClass:[NSNumber class]]){
            _owner = [NSString stringWithFormat:@"%ld",[creator integerValue]];;
        }
        if(gid && [gid isKindOfClass:[NSNumber class]]){
            _groupId = [NSString stringWithFormat:@"%ld",[gid integerValue]];;;
        }
        if(name && [name isKindOfClass:[NSString class]]){
            _groupName = name;
        }
        if(desc && [desc isKindOfClass:[NSString class]]){
            _groupDescription = desc;
        }
        if(imgurlde && [imgurlde isKindOfClass:[NSString class]]){
            _groupAvatar = imgurlde;
        }
        
    }
    return self;
}

- (id)initWithDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        if(dic && [dic isKindOfClass:[NSDictionary class]]){
            [self setValuesForKeysWithDictionary:dic];
            NSArray *adminlist = dic[@"adminList"];
            NSArray *silenceList = dic[@"silentList"];
            NSString *role = dic[@"roleType"];
            NSArray *lastList = dic[@"lastList"];
            
            if(adminlist && [adminlist isKindOfClass:[NSArray class]]){
                if(adminlist.count >0){
                    self.adminList = adminlist;
                }
            }
            
            if(silenceList && [adminlist isKindOfClass:[NSArray class]]){
                if(silenceList.count >0){
                    self.silentList = silenceList;
                }
            }
            
            if(role && [role isKindOfClass:[NSString class]]){
                if(role.length >0){
                    self.roleType = [role integerValue];
                }
            }
            if(lastList && [lastList isKindOfClass:[NSArray class]]){
                if(lastList.count > 0){
                    self.lastList = lastList;
                }
            }
            
        }
    }
    return self;
}

- (NSString *)getGroupId{
    if(self.groupId){
        return [NSString stringWithFormat:@"%ld",[self.groupId integerValue]];
    }
    return nil;
}
@end


/*
 @property (nonatomic, strong) NSString *create_date;
 
 @property (nonatomic, strong) NSString *version;
 
 @property (nonatomic, strong) NSString *creator;
 
 @property (nonatomic, strong) NSString *gid;
 
 @property (nonatomic, strong) NSString *name;
 
 @property (nonatomic, strong) NSString *desc;
 
 @property (nonatomic, strong) NSString *imgurlde;
*/
