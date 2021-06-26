//
//  YiChatGroupInfoStorage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/17.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupInfoStorage.h"
#import "ProjectStorageApis.h"

@interface YiChatGroupInfoStorage ()

@end

#define YiChatUserInfoStorage_FileItemName @"YiChatGroupInfo"
#define YiChatUserInfoStorage_MemberlistFileItemName @"YiChatGroupInfoMemberlist"
@implementation YiChatGroupInfoStorage

- (id)init{
    self = [super init];
    if(self){
        _groupInfoFileItemPath =  [self getChatStorageItem];
        _operationDataLock = dispatch_semaphore_create(1);
        
        _groupInfoMemberlistFileItemPath = [self getMemberlistStorageItem];
        _memberlistOperationDataLock = dispatch_semaphore_create(1);
        
    }
    return self;
}

- (NSString *)getChatStorageItem{
    NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
    return [[[path stringByAppendingString:@"/"] stringByAppendingString:YiChatUserInfoStorage_FileItemName] stringByAppendingString:@"/"];
}

- (NSString *)getMemberlistStorageItem{
    NSString *path = [ProjectStorageApis projectStorageApis_getCacheDirectoryPath];
    return [[[path stringByAppendingString:@"/"] stringByAppendingString:YiChatUserInfoStorage_MemberlistFileItemName] stringByAppendingString:@"/"];
}

@end
