//
//  ZFChatSendReachbility.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatSendReachbility.h"

@interface ZFChatSendReachbility ()

@property (nonatomic,weak) NSTimer *timer;

@property (nonatomic,assign) NSInteger taskNum;

@end

@implementation ZFChatSendReachbility

- (void)dealloc{
    [_timer invalidate];
    _timer = nil;
}


- (id)init{
    self = [super init];
    if(self){
        _taskNum = 1;
    }
    return self;
}

- (void)addSendTask{
    [ProjectHelper helper_getMainThread:^{
       [self timer];
    }];
    _taskNum ++;
}

- (NSTimer *)timer{
    if(!_timer){
        _timer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(timeAction:) userInfo:nil repeats:YES];
    }
    return _timer;
    
}

- (void)clean{
    self.ZFChatSendReachbilityCanSendMsg = nil;
    if(_timer){
        [_timer invalidate];
        _timer = nil;
    }
}

- (void)timeAction:(NSTimer *)timer{
    
    if(self.ZFChatSendReachbilityCanSendMsg){
        self.ZFChatSendReachbilityCanSendMsg();
    }
    _taskNum --;
    if(_taskNum <= 0){
        _taskNum = 1;
        [self.timer invalidate];
        self.timer = nil;
    }
}
@end
