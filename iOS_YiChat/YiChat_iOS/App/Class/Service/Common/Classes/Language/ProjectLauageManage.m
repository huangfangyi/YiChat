//
//  ProjectLauageManage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectLauageManage.h"


//英语
#define ProjectLanguage_En @"en-US"
//简体汉语
#define ProjectLanguage_ChSimp @"zh-Hans-US"

#define ProjectLanguage_LanguageStorage_key @"CFFFLanguageLocal"
#define ProjectLanguage_System_LanguageStorage_key @"CFFFSystemLanguage"

static ProjectLauageManage *manager = nil;
@interface ProjectLauageManage ()

@end

@implementation ProjectLauageManage

+ (id)sharedLanguage{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[self alloc] init];
    });
    return manager;
}

- (void)initialLanguage{
    //获取当前语言 先获取存储的语言 若未存储默认获取系统的语言 若存储了使用存储的语言
    //存储系统语言 若进来系统语言不一致 则使用系统语言 若一致 使用存储的语言  若无存储的语言 则默认使用系统语言
    NSString *storageSetLanguage = [self getStorageSetLanguage];
    NSString *storageSystemLanguage = [self getStorageSystemLanguage];
    NSString *currentSystemLanguage = [self getCurrentSystemLanguage];
    
    if(storageSetLanguage != nil && storageSystemLanguage != nil){
        if([currentSystemLanguage isEqualToString:storageSystemLanguage]){
            //相同 使用 storageSetLanguage
            self.currentLanguage = storageSetLanguage;
        }
        else{
            self.currentLanguage = currentSystemLanguage;
            //存储系统语言
            [self storageSystemLanguage:currentSystemLanguage];
        }
    }
    else if(storageSetLanguage == nil && storageSystemLanguage != nil){
        
        if([currentSystemLanguage isEqualToString:storageSystemLanguage]){
            //相同 使用 storageSetLanguage
        }
        else{
            //存储系统语言
            [self storageSystemLanguage:currentSystemLanguage];
        }
        self.currentLanguage = currentSystemLanguage;
    }
    else if(storageSetLanguage != nil && storageSystemLanguage == nil){
        self.currentLanguage = storageSetLanguage;
        [self storageSystemLanguage:currentSystemLanguage];
    }
    else if(storageSetLanguage == nil && storageSystemLanguage == nil){
        self.currentLanguage = storageSystemLanguage;
        [self storageSystemLanguage:currentSystemLanguage];
    }
    
    self.currentBundle = [self getBundleWithLanguageWord:self.currentLanguage];
    
}

- (NSString *)getAppearWordWithKey:(NSString *)key{
    if(self.currentBundle == nil){
        return @"";
    }
    if(key.length != 0 && key != nil){
        return [self.currentBundle localizedStringForKey:key value:nil table:@"LocalizedString"];
    }
    else{
        return @"";
    }
}

- (NSBundle *)getBundleWithLanguageWord:(NSString *)languageWord{
    NSString *path = nil;
    if([languageWord isEqualToString:ProjectLanguage_En]){
        path = [[NSBundle mainBundle] pathForResource:@"en" ofType:@"lproj"];
        
    }
    else if([languageWord isEqualToString:ProjectLanguage_ChSimp]){
        path = [[NSBundle mainBundle] pathForResource:@"zh-Hans" ofType:@"lproj"];
    }
    else{
        path = [[NSBundle mainBundle] pathForResource:@"Base" ofType:@"lproj"];
    }
    return  [NSBundle bundleWithPath:path];
    
}


- (void)setCurrentLanguageWithLanguage:(NSString *)language{
    
}

- (NSString *)getCurrentSystemLanguage{
    NSArray *languages = [NSLocale preferredLanguages];
    NSString *currentLanguage = [languages objectAtIndex:0];
    NSLog ( @"%@" , currentLanguage);
    return currentLanguage;
}

- (ProjectLauageType)getLanguageWithLanguageWord:(NSString *)languageWord{
    if([languageWord isEqualToString:ProjectLanguage_En]){
        return ProjectLauageTypeEnglish;
    }
    else if([languageWord isEqualToString:ProjectLanguage_ChSimp]){
        return ProjectLauageTypeSimpleChinese;
    }
    else{
        return ProjectLauageTypeUnsupport;
    }
}

#pragma mark judge


#pragma mark storage

- (NSString *)getStorageSetLanguage{
    NSString *storage =  [[NSUserDefaults standardUserDefaults] objectForKey:ProjectLanguage_LanguageStorage_key];
    if(storage.length == 0 || storage == nil){
        return nil;
    }
    else{
        return storage;
    }
}


- (void)storageSetLanguage:(NSString *)str{
    if(str.length !=0 && str != nil){
        [[NSUserDefaults standardUserDefaults] setObject:str forKey:ProjectLanguage_LanguageStorage_key];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

- (NSString *)getStorageSystemLanguage{
    NSString *storage =  [[NSUserDefaults standardUserDefaults] objectForKey:ProjectLanguage_System_LanguageStorage_key];
    if(storage.length == 0 || storage == nil){
        return nil;
    }
    else{
        return storage;
    }
}

- (void)storageSystemLanguage:(NSString *)str{
    if(str.length !=0 && str != nil){
        [[NSUserDefaults standardUserDefaults] setObject:str forKey:ProjectLanguage_System_LanguageStorage_key];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

@end
