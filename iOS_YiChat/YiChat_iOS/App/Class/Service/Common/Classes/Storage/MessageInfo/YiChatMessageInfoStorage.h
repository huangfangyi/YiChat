//
//  YiChatMessageInfoStorage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatMessageInfoStorage : NSObject

@property (nonatomic,strong) NSString *unreadMessageInfoFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t unreadMessageInfo_operationDataLock;

@property (nonatomic,strong) NSString *notifyInfoFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t notifyInfo_operationDataLock;



@property (nonatomic,strong) NSString *messageShutInfoFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t messageShutInfo_operationDataLock;


//@
@property (nonatomic,strong) NSString *messageAlertFileItemPath;

@property (nonatomic,strong) dispatch_semaphore_t messageAlert_operationDataLock;

@end

NS_ASSUME_NONNULL_END
