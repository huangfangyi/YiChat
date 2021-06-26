//
//  YiChatMessageInfoStorage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatMessageInfoStorage.h"
#import "ProjectStorageApis.h"

@interface YiChatMessageInfoStorage ()

@end

#define YiChatMessageInfoStorage_unreadMessage_FileItemName @"YiChatYiChatMessageInfo_unreadMessage"
#define YiChatMessageInfoStorage_notify_FileItemName @"YiChatMessageInfoStorage_notify_FileItemName"
#define YiChatMessageInfoStorage_messageShutUp_FileItemName @"YiChatMessageInfoStorage_messageShutUp_FileItemName"
#define YiChatMessageInfoStorage_messageAlert_FileItemName @"YiChatMessageInfoStorage_messageAlert_FileItemName"

@implementation YiChatMessageInfoStorage

- (id)init{
    self = [super init];
    if(self){
        _unreadMessageInfoFileItemPath =  [self getMemberlistStorageItem:YiChatMessageInfoStorage_unreadMessage_FileItemName];
        _unreadMessageInfo_operationDataLock = dispatch_semaphore_create(1);
       
        _notifyInfoFileItemPath  =  [self getMemberlistStorageItem:YiChatMessageInfoStorage_notify_FileItemName];
        _notifyInfo_operationDataLock = dispatch_semaphore_create(1);
        
        _messageShutInfoFileItemPath =  [self getMemberlistStorageItem:YiChatMessageInfoStorage_messageShutUp_FileItemName];
        _messageShutInfo_operationDataLock = dispatch_semaphore_create(1);
        
        _messageAlertFileItemPath =  [self getMemberlistStorageItem:YiChatMessageInfoStorage_messageAlert_FileItemName];
        _messageAlert_operationDataLock = dispatch_semaphore_create(1);
    }
    return self;
}

- (NSString *)getMemberlistStorageItem:(NSString *)fileItemName{
    if(fileItemName && [fileItemName isKindOfClass:[NSString class]]){
        if(fileItemName.length > 0){
            NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
            return [[[path stringByAppendingString:@"/"] stringByAppendingString:fileItemName] stringByAppendingString:@"/"];
        }
    }
    return nil;
}
@end
