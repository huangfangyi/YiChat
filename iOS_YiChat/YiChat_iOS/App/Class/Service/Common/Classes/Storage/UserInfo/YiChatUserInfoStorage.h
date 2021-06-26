//
//  YiChatUserInfoStorage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatUserInfoStorage : NSObject

@property (nonatomic,strong) NSString *userInfoFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t operationDataLock;

@property (nonatomic,strong) NSString *userConnectionFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t connectionOperationDataLock;
@end

NS_ASSUME_NONNULL_END
