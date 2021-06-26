//
//  YiChatGroupInfoStorage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/17.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupInfoStorage : NSObject

@property (nonatomic,strong) NSString *groupInfoFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t operationDataLock;

@property (nonatomic,strong) NSString *groupInfoMemberlistFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t memberlistOperationDataLock;

@end

NS_ASSUME_NONNULL_END
