//
//  ZFConnectionModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFConnectionModel.h"
#import "ProjectTranslateHelper.h"

@implementation ZFConnectionModel

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

/**
 *  @{@"S":@[dic]}
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
                
                NSString *nick = obj[@"nick"] ;
                if([nick isKindOfClass:[NSString class]]){
                    NSString *characters = [ProjectTranslateHelper helper_getFirstCharacterFromStr:nick];
                    if(characters){
                        [charactersArr addObject:characters];
                        
                        if(obj){
                             [dataArr addObject:@{characters:obj}];
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


@end
