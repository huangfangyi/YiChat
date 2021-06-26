//
//  YiChatConnectionModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConnectionModel.h"
#import "ServiceGlobalDef.h"
#import "YiChatUserModel.h"
#import "ProjectTranslateHelper.h"

@interface YiChatConnectionModel ()<NSCoding,NSCopying,NSMutableCopying>

@end

@implementation YiChatConnectionModel

- (id)mutableCopyWithZone:(NSZone *)zone{
    YiChatConnectionModel *model = [[YiChatConnectionModel allocWithZone:zone] init];
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

- (id)initWithUsersArr:(NSArray *)arr{
    self = [self init];
    if(self){
        WS(weakSelf);
        _originDataArr = arr;
        [self matchConnectionEntitys:arr withCharactersUp:^(NSArray *connectEntityDicArr) {
            weakSelf.connectionModelArr = [connectEntityDicArr mutableCopy];
        }];
    }
    return self;
}

+ (NSString *)getUserDicAppearName:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        
        NSString *nick = dic[@"nick"];
        NSString *remark = dic[@"remark"];
        
        if(remark && [remark isKindOfClass:[NSString class]]){
            if(remark.length > 0){
                return  remark;
            }
        }
        if(nick && [nick isKindOfClass:[NSString class]]){
            return  nick;
        }
    }
    return @"";
}


+ (NSDictionary *)translateUserDicToConnectionEntityData:(NSDictionary *)userDic{
    if(userDic && [userDic isKindOfClass:[NSDictionary class]]){
        NSString *nick = [self getUserDicAppearName:userDic];
        
        if([nick isKindOfClass:[NSString class]]){
            NSString *characters = [ProjectTranslateHelper helper_getFirstCharacterFromStr:nick];
            if(characters && [characters isKindOfClass:[NSString class]]){
                
                YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:userDic];
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    return @{@"nick":nick,@"model":model};
                }
            }
        }
    }
    return nil;
}

/**
 *  @{@"S":@[userModel]}
 */
- (void)matchConnectionEntitys:(NSArray *)entityArr withCharactersUp:(void(^)(NSArray *connectEntityDicArr))invocation{
    
    if(entityArr.count == 0 || entityArr == nil){
        invocation(nil);
    }
    else{
        
        NSMutableArray *charactersArr = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *dataArr = [NSMutableArray arrayWithCapacity:0];
        
        dispatch_apply(entityArr.count, dispatch_queue_create("characterSorts", 0), ^(size_t num) {
            NSDictionary *obj = entityArr[num];
            if([obj isKindOfClass:[NSDictionary class]]){
                
                NSString *nick = [[self class] getUserDicAppearName:obj];
                
                if([nick isKindOfClass:[NSString class]]){
                    NSString *characters = [ProjectTranslateHelper helper_getFirstCharacterFromStr:nick];
                    if(characters){
                        [charactersArr addObject:characters];
                        
                        YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:obj];
                        
                        
                        if(model){
                            [dataArr addObject:@{characters:model}];
                        }
                    }
                }
            }
        });
        
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
        
        [charactersArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if(obj){
                [dic setObject:obj forKey:obj];
            }
        }];
        
        NSMutableArray *sectionArr = [NSMutableArray arrayWithCapacity:0];
        
        [sectionArr addObjectsFromArray:dic.allKeys];
        
        //排序
        for (int i = 0; i < sectionArr.count; ++i) {
            
            //遍历数组的每一个`索引`（不包括最后一个,因为比较的是j+1）
            for (int j = 0; j < sectionArr.count-1; ++j) {
                
                //根据索引的`相邻两位`进行`比较`
                if ([sectionArr[j] compare:sectionArr[j+1] options:NSLiteralSearch] == NSOrderedDescending) {
                    
                    [sectionArr exchangeObjectAtIndex:j withObjectAtIndex:j+1];
                }
                
            }
        }
        
        for (int i = 0; i < sectionArr.count; i ++) {
            if([sectionArr[i] isKindOfClass:[NSString class]]){
                if([sectionArr[i] isEqualToString:@"*"]){
                    [sectionArr removeObjectAtIndex:i];
                    [sectionArr addObject:@"*"];
                    break;
                }
            }
        }
        
        NSMutableArray *resultArr = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < sectionArr.count; i ++) {
            
            NSMutableArray *personEntityArr = [NSMutableArray arrayWithCapacity:0];
            
            dispatch_apply(dataArr.count, dispatch_queue_create("characterSortsMatch", 0), ^(size_t j) {
                NSDictionary * obj = dataArr[j];
                if([obj isKindOfClass:[NSDictionary class]]){
                    
                    if(obj.allKeys.count != 0){
                        id key = obj.allKeys.lastObject;
                        if([key isKindOfClass:[NSString class]]){
                            if([key isEqualToString:sectionArr[i]]){
                                [personEntityArr addObject:obj[key]];
                            }
                        }
                    }
                }
            });
            
            id key = sectionArr[i];
            if([key isKindOfClass:[NSString class]] && personEntityArr.count != 0){
                [resultArr addObject:@{key:personEntityArr}];
            }
        }
        
        invocation([resultArr copy]);
        
    }
}

- (NSArray *)getUserModels{
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    
    if(self.connectionModelArr && [self.connectionModelArr isKindOfClass:[NSArray class]]){
        for (int i = 0; i < self.connectionModelArr.count; i ++) {
            NSDictionary *model = self.connectionModelArr[i];
            NSString *key = model.allKeys.lastObject;
            if(key && [key isKindOfClass:[NSString class]]){
                NSArray *users = model[key];
                if(users && [users isKindOfClass:[NSArray class]]){
                    for (int j = 0; j < users.count; j ++) {
                        YiChatUserModel *userModel = users[j];
                        if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
                            [tmp addObject:userModel];
                        }
                    }
                }
            }
        }
    }
    return tmp;
}
@end
