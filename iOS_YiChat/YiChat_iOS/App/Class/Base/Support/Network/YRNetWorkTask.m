//
//  YRNetWorkTask.m
//  CFFF
//
//  Created by e冻网 on 2018/8/6.
//  Copyright © 2018年 ED. All rights reserved.
//

#import "YRNetWorkTask.h"
#import "YRNetWorkRequestManage.h"

@interface YRNetWorkTask ()

@property (nonatomic,strong) NSURLRequest *request;

@property (nonatomic,copy) YRNetWorkTaskRequestSuccessHandle success;
@property (nonatomic,copy) YRNetWorkTaskRequestFailHandle fail;
@property (nonatomic,strong) dispatch_semaphore_t lock;

@end

@implementation YRNetWorkTask

- (void)dealloc{
}

- (void)createThreadLock{
    _lock = dispatch_semaphore_create(0);
}

- (void)waitUntilThreadDone{
    dispatch_semaphore_wait(_lock, DISPATCH_TIME_FOREVER);
}

- (void)unlock{
    dispatch_semaphore_signal(_lock);
}
/**
 * resumeThread = 0 代表当前线程异步执行   resumeThread = 1 代表当前请求线程同步执行
 */
- (instancetype)initTaskWithRequest:(NSURLRequest *)request
                         identifier:(NSString *)identifier
                       ResumeThread:(NSInteger)resumeThread
                     requestSuccess:(YRNetWorkTaskRequestSuccessHandle)success
                               fail:(YRNetWorkTaskRequestFailHandle)fail{
    self = [super init];
    if(self){
        _request = request;
        _success = success;
        _fail = fail;
        _identider = identifier;
        _resumeThread = resumeThread;
        
        [self initialTask];
    }
    return self;
}

- (void)initialTask{
    WS(weakSelf);
    
    NSURLSession *session=[NSURLSession sharedSession];
    
    NSURLSessionTask *task=[session dataTaskWithRequest:_request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        //子线程执行 若想刷新UI 得回到主线程
        
        WS(weakSelf);
        
        if(error){
            if(weakSelf.fail){
                weakSelf.fail(error);
                weakSelf.fail = nil;
            }
        }else{
            if(weakSelf.success){
                weakSelf.success(data,response);
                weakSelf.success = nil;
            }
            
        }
        if(weakSelf.resumeThread == YRNetWorkTaskResumeThreadSerial){
            [weakSelf unlock];
        }
        weakSelf.resumeState = YRNetWorkTaskResumeStateEnd;
        if(weakSelf){
            [[YRNetWorkRequestManage sharedManager] removeRequstTask:weakSelf];
        }
        
    }];
    _requestTask = task;
    _resumeState = YRNetWorkTaskResumeStateWait;
}


- (void)resumeWithThread:(dispatch_queue_t)queue{
    if([_requestTask isKindOfClass:[NSURLSessionTask class]]){
        
        NSURLSessionTask *task = (NSURLSessionTask *)_requestTask;
        
        self.resumeState = YRNetWorkTaskResumeStateBegin;
        
        dispatch_async(queue, ^{
            [task resume];
        });
    }
}

- (void)resumeAysnc{
    if([_requestTask isKindOfClass:[NSURLSessionTask class]]){
        
        NSURLSessionTask *task = (NSURLSessionTask *)_requestTask;
        
        self.resumeState = YRNetWorkTaskResumeStateBegin;
        
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [task resume];
        });
    }
}

- (void)resume{
    if([_requestTask isKindOfClass:[NSURLSessionTask class]]){
        
        NSURLSessionTask *task = (NSURLSessionTask *)_requestTask;
        
        self.resumeState = YRNetWorkTaskResumeStateBegin;
        
        [self createThreadLock];
        
        [task resume];
        
        [self waitUntilThreadDone];

    }
}

- (void)addRequestTask:(id)task resumeThread:(NSInteger)resumeThread{
    if(task != nil && [task isKindOfClass:[YRNetWorkTask class]]){
        _requestTask = task;
        _resumeThread = resumeThread;
    }
}

@end
