//
//  YiChatStorageManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatStorageManager.h"
#import "ProjectStorageApis.h"
#import "YiChatUserInfoStorage.h"
#import "YiChatGroupInfoStorage.h"
#import "ProjectHelper.h"
#import "YiChatMessageInfoStorage.h"

static YiChatStorageManager *yichatStorage = nil;
@interface YiChatStorageManager ()

@property (nonatomic,strong) YiChatUserInfoStorage *userInfoStorage;

@property (nonatomic,strong) YiChatGroupInfoStorage *groupInfoStorage;

@property (nonatomic,strong) YiChatMessageInfoStorage *messageInfoStorage;

@end

@implementation YiChatStorageManager

+ (id)sharedManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        yichatStorage = [[self alloc] init];
        yichatStorage.userInfoStorage = [[YiChatUserInfoStorage alloc] init];
        yichatStorage.groupInfoStorage = [[YiChatGroupInfoStorage alloc] init];
        yichatStorage.messageInfoStorage = [[YiChatMessageInfoStorage alloc] init];
    });
    return yichatStorage;
}

- (NSString *)getUserConnectionStorageFileItem{
    return [self getFilePathWithFileItem:self.userInfoStorage.userConnectionFileItemPath];
}

- (void)storageUserConnection:(id)model withKey:(NSString *)key{
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.userInfoStorage.connectionOperationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.userInfoStorage.userConnectionFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(self.userInfoStorage.connectionOperationDataLock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(self.userInfoStorage.connectionOperationDataLock);
    }
}

- (void)getStorageUserConnectionWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(self.userInfoStorage.connectionOperationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.userInfoStorage.userConnectionFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(self.userInfoStorage.connectionOperationDataLock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(self.userInfoStorage.connectionOperationDataLock);
    }];
}


- (NSString *)getUserInfoStorageFileItem{
    return [self getFilePathWithFileItem:self.userInfoStorage.userInfoFileItemPath];
}

- (void)storageUserInfo:(id)model withKey:(NSString *)key{
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.userInfoStorage.operationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.userInfoStorage.userInfoFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(self.userInfoStorage.operationDataLock);
        }];
       
    }
    else{
        dispatch_semaphore_signal(self.userInfoStorage.operationDataLock);
    }
    
}

- (void)getStorageUserInfoWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(self.userInfoStorage.operationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.userInfoStorage.userInfoFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(self.userInfoStorage.operationDataLock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(self.userInfoStorage.operationDataLock);
    }];
}

- (NSString *)getGroupInfoStorageFileItem{
     return [self getFilePathWithFileItem:self.groupInfoStorage.groupInfoFileItemPath];
}

- (void)storageGroupInfo:(id)model withKey:(NSString *)key{
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.groupInfoStorage.operationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.groupInfoStorage.groupInfoFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(self.groupInfoStorage.operationDataLock);
        }];
        
    }
    else{
         dispatch_semaphore_signal(self.groupInfoStorage.operationDataLock);
    }
}

- (void)getStorageGroupInfoWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(self.groupInfoStorage.operationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.groupInfoStorage.groupInfoFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(self.groupInfoStorage.operationDataLock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(self.groupInfoStorage.operationDataLock);
    }];
}


- (NSString *)getGroupInfoMemberListStorageFileItem{
    return [self getFilePathWithFileItem:self.groupInfoStorage.groupInfoMemberlistFileItemPath];
}

- (void)storageGroupInfoMemberlist:(id)model withKey:(NSString *)key{
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(self.groupInfoStorage.memberlistOperationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.groupInfoStorage.groupInfoMemberlistFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(self.groupInfoStorage.memberlistOperationDataLock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(self.groupInfoStorage.memberlistOperationDataLock);
    }
}

- (void)getStorageGroupInfoMemberListWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(self.groupInfoStorage.memberlistOperationDataLock, DISPATCH_TIME_FOREVER);
            
            NSString *fileItemPath = self.groupInfoStorage.groupInfoMemberlistFileItemPath;
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(self.groupInfoStorage.memberlistOperationDataLock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(self.groupInfoStorage.memberlistOperationDataLock);
    }];
}

- (void)writeItems:(id)storageItem IntoFilePath:(NSString *)filepath{
    if(storageItem){
        [NSKeyedArchiver archiveRootObject:storageItem toFile:filepath];
    }
}


- (NSString *)getFilePathWithFileItem:(NSString *)fileItem{
    if(fileItem){
        if(![[NSFileManager defaultManager] fileExistsAtPath:fileItem]){
            [ProjectStorageApis projectStorageApis_CreateItemWithPath:fileItem];
        }
        return fileItem;
    }
    return nil;
}

- (NSString *)getFilePathWithFileName:(NSString *)fileName fileItem:(NSString *)fileItem{
    if(fileItem && fileName){
        if(![[NSFileManager defaultManager] fileExistsAtPath:fileItem]){
            [ProjectStorageApis projectStorageApis_CreateItemWithPath:fileItem];
        }
        NSString *filePath = [fileItem  stringByAppendingString:fileName];
        
        return filePath;
    }
    return nil;
}

- (NSString *)getMessageInfoUnreadMessageStorageFileItem{
     return [self getFilePathWithFileItem:self.messageInfoStorage.unreadMessageInfoFileItemPath];
}

- (void)storageUnreadMessage:(id)model withKey:(NSString *)key{
    
    dispatch_semaphore_t lock = self.messageInfoStorage.unreadMessageInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.unreadMessageInfoFileItemPath;
    
    
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(lock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(lock);
    }
}

- (void)getUnreadMessageWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    
    dispatch_semaphore_t lock = self.messageInfoStorage.unreadMessageInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.unreadMessageInfoFileItemPath;
    
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(lock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(lock);
    }];
}

- (void)storageNotify:(id)model withKey:(NSString *)key{
    dispatch_semaphore_t lock = self.messageInfoStorage.notifyInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.notifyInfoFileItemPath;
    
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(lock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(lock);
    }
}

- (void)getStorageNotifyWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    dispatch_semaphore_t lock = self.messageInfoStorage.notifyInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.notifyInfoFileItemPath;
    
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(lock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(lock);
    }];
}



- (void)storageMessageShutUp:(id)model withKey:(NSString *)key{
    dispatch_semaphore_t lock = self.messageInfoStorage.messageShutInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.messageShutInfoFileItemPath;
    
    
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(lock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(lock);
    }
}


- (void)getStorageMessageShutUpWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    dispatch_semaphore_t lock = self.messageInfoStorage.messageShutInfo_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.messageShutInfoFileItemPath;
    
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(lock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(lock);
    }];
}

- (void)storageMessageAlert:(id)model withKey:(NSString *)key{
    dispatch_semaphore_t lock = self.messageInfoStorage.messageAlert_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.messageAlertFileItemPath;
    
    
    if(model && key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
                
                [self writeItems:model IntoFilePath:fileFullPath];
            }
            
            dispatch_semaphore_signal(lock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(lock);
    }
}

- (void)removeStorageMessageAlertWithKey:(NSString *)key{
    dispatch_semaphore_t lock = self.messageInfoStorage.messageAlert_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.messageAlertFileItemPath;
    
    
    if(key && [key isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                BOOL isExist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:fileFullPath];
                
                if(isExist){
                    [ProjectStorageApis projectStorageApis_removeItemAtPath:fileFullPath];
                }
            }
            
            dispatch_semaphore_signal(lock);
        }];
        
    }
    else{
        dispatch_semaphore_signal(lock);
    }
}

- (void)getStorageMessageAlertWithKey:(NSString *)key handle:(void(^)(id obj))handle{
    dispatch_semaphore_t lock = self.messageInfoStorage.messageAlert_operationDataLock;
    NSString *fileItemPath = self.messageInfoStorage.messageAlertFileItemPath;
    
    [ProjectHelper helper_getGlobalThread:^{
        if(key && [key isKindOfClass:[NSString class]]){
            dispatch_semaphore_wait(lock, DISPATCH_TIME_FOREVER);
            
            if(fileItemPath && [fileItemPath isKindOfClass:[NSString class]] ){
                NSString *fileFullPath = [self getFilePathWithFileName:[key stringByAppendingString:@".archiver"] fileItem:fileItemPath];
                
                id obj = [NSKeyedUnarchiver unarchiveObjectWithFile:fileFullPath];
                
                handle(obj);
                dispatch_semaphore_signal(lock);
                return;
            }
        }
        handle(nil);
        dispatch_semaphore_signal(lock);
    }];
}

@end
