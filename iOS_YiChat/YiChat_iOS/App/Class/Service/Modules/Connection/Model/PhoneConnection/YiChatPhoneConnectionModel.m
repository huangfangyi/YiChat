//
//  YiChatPhoneConnectionModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPhoneConnectionModel.h"
#import <Contacts/Contacts.h>
#import "ServiceGlobalDef.h"
#import "ProjectTranslateHelper.h"

@interface YiChatContactEntity ()

@end

@implementation YiChatContactEntity


@end

@interface YiChatPhoneConnectionModel ()

@property (nonatomic,strong) NSMutableArray *connectionModelsArr;

@end

@implementation YiChatPhoneConnectionModel

- (void)fetchPhoneConnectionDataSuccess:(void(^)(NSArray *connectEntityArr))success
                                   fail:(void(^)(NSString *errorMsg,NSInteger code))fail
                           isNeedFectch:(BOOL)isNeedFetch{
    
    BOOL isRequest = NO;
    if(isNeedFetch){
        isRequest = YES;
    }
    else{
        if(self.connectionModelsArr.count == 0 || self.connectionModelsArr == nil){
            if(!self.connectionModelsArr){
                self.connectionModelsArr = [NSMutableArray arrayWithCapacity:0];
            }
            isRequest = YES;
            [self.connectionModelsArr removeAllObjects];
            self.connectionModelsArr = nil;
        }
    }
    
    if(isRequest){
        CNAuthorizationStatus status = [CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts];
        if (status == CNAuthorizationStatusNotDetermined) {
            CNContactStore *store = [[CNContactStore alloc] init];
            [store requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError*  _Nullable error) {
                if (error) {
                    fail(@"获取用户通讯录授权失败",0);
                }else {
                    NSLog(@"成功授权");
                    [self openContact:^(NSArray *connectEntityArr) {
                        success(connectEntityArr);
                    }];
                }
            }];
        }
        else if(status == CNAuthorizationStatusRestricted)
        {
            fail(@"获取用户通讯录权限-用户拒绝",0);
        }
        else if (status == CNAuthorizationStatusDenied)
        {
            fail(@"获取用户通讯录权限-用户拒绝",0);
        }
        else if (status == CNAuthorizationStatusAuthorized)//已经授权
        {
            //有通讯录权限-- 进行下一步操作
            [self openContact:^(NSArray *connectEntityArr) {
                success(connectEntityArr);
            }];
        }
    }
    else{
        success([self.connectionModelsArr copy]);
    }
}

//有通讯录权限-- 进行下一步操作
- (void)openContact:(void(^)(NSArray *connectEntityArr))success{
    // 获取指定的字段,并不是要获取所有字段，需要指定具体的字段
    NSArray *keysToFetch = @[CNContactGivenNameKey, CNContactFamilyNameKey, CNContactPhoneNumbersKey];
    CNContactFetchRequest *fetchRequest = [[CNContactFetchRequest alloc] initWithKeysToFetch:keysToFetch];
    CNContactStore *contactStore = [[CNContactStore alloc] init];
    
    WS(weakSelf);
    
    if(self.connectionModelsArr == nil){
        self.connectionModelsArr = [NSMutableArray arrayWithCapacity:0];
    }
    
    [contactStore enumerateContactsWithFetchRequest:fetchRequest error:nil usingBlock:^(CNContact * _Nonnull contact, BOOL * _Nonnull stop) {
        
        NSString *givenName = contact.givenName;
        NSString *familyName = contact.familyName;
        //拼接姓名
        NSString *nameStr = [NSString stringWithFormat:@"%@%@",contact.familyName,contact.givenName];
        
        NSArray *phoneNumbers = contact.phoneNumbers;
        
        //        CNPhoneNumber  * cnphoneNumber = contact.phoneNumbers[0];
        
        //        NSString * phoneNumber = cnphoneNumber.stringValue;
        
        for (CNLabeledValue *labelValue in phoneNumbers) {
            //遍历一个人名下的多个电话号码
            NSString *label = labelValue.label;
            //   NSString *    phoneNumber = labelValue.value;
            CNPhoneNumber *phoneNumber = labelValue.value;
            
            NSString * string = phoneNumber.stringValue ;
            
            //去掉电话中的特殊字符
            string = [string stringByReplacingOccurrencesOfString:@"+86" withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@"-" withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@"(" withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@")" withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@" " withString:@""];
            string = [string stringByReplacingOccurrencesOfString:@" " withString:@""];
            
            YiChatContactEntity *entity = [[YiChatContactEntity alloc] init];
            entity.connectionName = nameStr;
            entity.phoneNum = string;
            
            if(entity){
                [weakSelf.connectionModelsArr addObject:entity];
            }
        }
        //    *stop = YES; // 停止循环，相当于break；
        
    }];
    
     success([weakSelf.connectionModelsArr copy]);
}

/**
 *  @{@"S":@[YiChatContactEntity]}
 */
+ (void)matchConnectionEntitys:(NSArray *)entityArr withCharactersUp:(void(^)(NSArray *connectEntityDicArr))invocation{
    
    if(entityArr.count == 0 || entityArr == nil){
        invocation(nil);
    }
    else{
        
        NSMutableArray *charactersArr = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *dataArr = [NSMutableArray arrayWithCapacity:0];
        
        dispatch_apply(entityArr.count, dispatch_queue_create("characterSorts", 0), ^(size_t num) {
            YiChatContactEntity * obj = entityArr[num];
            if([obj isKindOfClass:[YiChatContactEntity class]]){
                
                if([obj.connectionName isKindOfClass:[NSString class]]){
                    NSString *characters = [ProjectTranslateHelper helper_getFirstCharacterFromStr:obj.connectionName];
                    if(characters){
                        [charactersArr addObject:characters];
                        [dataArr addObject:@{characters:obj}];
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
