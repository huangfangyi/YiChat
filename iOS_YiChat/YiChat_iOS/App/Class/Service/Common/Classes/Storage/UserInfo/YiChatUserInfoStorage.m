//
//  YiChatUserInfoStorage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatUserInfoStorage.h"
#import "ProjectStorageApis.h"

@interface YiChatUserInfoStorage ()

@end

#define YiChatUserInfoStorage_FileItemName @"YiChatUserInfo"
#define YiChatUserInfoStorage_UserConnectionFileItemName @"YiChatUserConnection"
@implementation YiChatUserInfoStorage

- (id)init{
    self = [super init];
    if(self){
        _userInfoFileItemPath =  [self getChatStorageItem];
        _operationDataLock = dispatch_semaphore_create(1);
        
        _userConnectionFileItemPath = [self getUserConnectionStorageItem];
        _connectionOperationDataLock = dispatch_semaphore_create(1);
    }
    return self;
}

- (NSString *)getChatStorageItem{
    NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
    return [[[path stringByAppendingString:@"/"] stringByAppendingString:YiChatUserInfoStorage_FileItemName] stringByAppendingString:@"/"];
}

- (NSString *)getUserConnectionStorageItem{
    NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
    return [[[path stringByAppendingString:@"/"] stringByAppendingString:YiChatUserInfoStorage_UserConnectionFileItemName] stringByAppendingString:@"/"];
}


@end
