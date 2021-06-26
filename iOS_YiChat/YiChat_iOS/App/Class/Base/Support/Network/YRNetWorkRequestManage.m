//
//  YRNetWorkRequestManage.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/17.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "YRNetWorkRequestManage.h"

typedef BOOL(^RunloopBlock)(void);

@interface YRNetWorkRequestManage ()

@property (nonatomic,strong) dispatch_queue_t networkRequestTask_CONCURRENT;

/** 数组  */
@property(nonatomic,strong)  NSMutableArray *runloopTasks;

@property (nonatomic,strong) NSString *requestBaseUrl;

@property (nonatomic,strong) dispatch_semaphore_t taskLock;

@end

static YRNetWorkRequestManage *manager = nil;

@implementation YRNetWorkRequestManage

+ (instancetype)sharedManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        
        manager = [[YRNetWorkRequestManage alloc] init];
        
        manager.taskLock = dispatch_semaphore_create(1);
        
        [manager initialTask];
        
        manager.networkRequestTask_CONCURRENT = dispatch_queue_create("networkRequestTask_queue", DISPATCH_QUEUE_CONCURRENT);
    });
    return manager;
}

- (YRNetWorkRequestManage *(^)(NSString *baseUrl))addBaseUrl{
    WS(weakSelf);
    return ^YRNetWorkRequestManage *(NSString *baseUrl){
        weakSelf.requestBaseUrl = baseUrl;
        return weakSelf;
    };
}


- (void)initialTask{
    _requestTask = [NSMutableArray arrayWithCapacity:0];
}

- (void)addRequestTask:(YRNetWorkTask *)taskobj{
    if(taskobj && [taskobj isKindOfClass:[YRNetWorkTask class]]){
        
        [ProjectHelper helper_getGlobalThread:^{
            
            dispatch_semaphore_wait(_taskLock, DISPATCH_TIME_FOREVER);
            
            YRNetWorkTask *task = taskobj;
            
            [_requestTask addObject:task];
            
            NSLog(@"request task ---> add %@",task.requestTask.currentRequest);
            
            dispatch_semaphore_signal(_taskLock);
            
            if([task.requestTask isKindOfClass:[NSURLSessionTask class]]  && task.resumeState == YRNetWorkTaskResumeStateWait){
                
              //  [self addObserverForRequestTaskWithTask:task];
                
                NSLog(@"request task begin  ---> %@ 当前请求任务数 : %ld interface ",task.identider,_requestTask.count);
                
                if(task.resumeThread == 0){
                    [task resumeAysnc];
                }
                else{
                    [task resume];
                }
                
            }
        }];
        
       
    }
}

- (void)removeRequstTask:(YRNetWorkTask *)task{
    WS(weakSelf);
    
    if(_requestTask && [_requestTask isKindOfClass:[NSArray class]]){
        
        [ProjectHelper helper_getGlobalThread:^{
            dispatch_semaphore_wait(_taskLock, DISPATCH_TIME_FOREVER);
                   
                   YRNetWorkTask *obj = task;
                   if(obj && [obj isKindOfClass:[YRNetWorkTask class]]){
                       
                       NSURLSessionTask *sessionTask = obj.requestTask;
                                                                                 
                       if(obj.resumeState != YRNetWorkTaskResumeStateEnd){
                           obj.resumeState = YRNetWorkTaskResumeStateEnd;
                           [sessionTask cancel];
                       }
                                                                                 
                       if(obj.resumeThread == YRNetWorkTaskResumeThreadSerial){
                           [obj unlock];
                       }
                                                                                 
                                                                        
                       if(obj){
                           [weakSelf.requestTask removeObject:obj];
                       }
                                                                                 
                       NSLog(@"移除task ---> %@ 当前请求任务数 : %ld",task.requestTask.currentRequest,weakSelf.requestTask.count);
                    }
                  
        dispatch_semaphore_signal(_taskLock);
    }];
        
       
        
       
    }
}

- (void)removeTaskWithName:(NSString *)taskName{
    WS(weakSelf);
    
    [_requestTask enumerateObjectsUsingBlock:^(YRNetWorkTask * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSURLSessionTask *sessionTask = obj.requestTask;
        
        if([obj.identider isEqualToString:taskName]){
            
            [weakSelf removeRequstTask:obj];
            
        }
    }];
}

- (void)addObserverForRequestTaskWithTask:(YRNetWorkTask *)task{
    if(task.isAddObserver != YES){
        task.isAddObserver = YES;
        [task addObserver:self forKeyPath:@"resumeState" options:NSKeyValueObservingOptionNew context:nil];
    }
}

- (void)removeObserverForRequestTaskWithTask:(YRNetWorkTask *)task{
    if(task.isAddObserver){
        [task removeObserver:self forKeyPath:@"resumeState"];
        task.isAddObserver = NO;
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context{
    if([object isKindOfClass:[YRNetWorkTask class]]){
        NSInteger state = [change[@"new"] intValue];
        if(state == 10 && object && [object isKindOfClass:[YRNetWorkTask class]]){
            [self removeRequstTask:object];
        }
    }
}

- (void)stopTaskWithName:(NSArray *)name{
    WS(weakSelf);
    
    if(name.count < 2){
        if(name.count != 0){
            [self removeTaskWithName:name.lastObject];
        }
        else{
            return;
        }
    }
    else{
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        
        dispatch_apply(name.count, queue, ^(size_t i ) {
            NSString *requestName = name[i];
            
            [weakSelf removeTaskWithName:requestName];
        });
    }
}

-(void)addRunloopObserver{
    //获取当前RunLoop
    CFRunLoopRef runloop = CFRunLoopGetCurrent();
    //定义一个上下文
    CFRunLoopObserverContext context = {
        0,
        (__bridge void *)(self),
        &CFRetain,
        &CFRelease,
        NULL,
    };
    //定义一个观察者
    static CFRunLoopObserverRef defaultModeObserver;
    //创建观察者
    defaultModeObserver = CFRunLoopObserverCreate(NULL, kCFRunLoopBeforeWaiting, YES, NSIntegerMax - 999, &Callback, &context);
    //添加当前RunLoop的观察者
    CFRunLoopAddObserver(runloop, defaultModeObserver, kCFRunLoopCommonModes);
    //C语言里面有Creat\new\copy 就需要 释放 ARC 管不了!!
    CFRelease(defaultModeObserver);
}

//添加新的任务的方法!
-(void)addTask:(RunloopBlock)unit {
    [self.runloopTasks addObject:unit];
}

//回调函数
static void Callback(CFRunLoopObserverRef observer, CFRunLoopActivity activity, void *info){
    
    NSRunLoop *currentRunLoop = [NSRunLoop currentRunLoop];
    NSLog(@"%p %@",currentRunLoop, currentRunLoop.currentMode);
    
    //从数组里面取代码!! info 哥么就是 self
    YRNetWorkRequestManage * vc = (__bridge YRNetWorkRequestManage *)info;
    if (vc.runloopTasks.count == 0) {
        return;
    }
    BOOL result = NO;
    while (result == NO && vc.runloopTasks.count) {
        //取出任务
        RunloopBlock unit = vc.runloopTasks.firstObject;
        //执行任务
        result = unit();
        //干掉第一个任务
        [vc.runloopTasks removeObjectAtIndex:0];
    }
}

@end
