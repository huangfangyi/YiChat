//
//  YRNetWorkTask.h
//  CFFF
//
//  Created by e冻网 on 2018/8/6.
//  Copyright © 2018年 ED. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YRNetWorkConfig.h"

@interface YRNetWorkTask : NSObject

@property (nonatomic) BOOL isAddObserver;

@property (nonatomic,strong) NSString *identider;

@property (nonatomic,strong,readonly) NSURLSessionTask *requestTask;

@property (nonatomic) YRNetWorkTaskResumeThread resumeThread;

@property (nonatomic) YRNetWorkTaskResumeState resumeState;


/**
 * resumeThread = 0 代表当前线程异步执行   resumeThread = 1 代表当前请求线程同步执行
 */
- (instancetype)initTaskWithRequest:(NSURLRequest *)request
                         identifier:(NSString *)identifier
                       ResumeThread:(NSInteger)resumeThread
                     requestSuccess:(YRNetWorkTaskRequestSuccessHandle)success
                               fail:(YRNetWorkTaskRequestFailHandle)fail;

- (void)resume;

- (void)resumeAysnc;

- (void)resumeWithThread:(dispatch_queue_t)queue;

- (void)unlock;

@end
