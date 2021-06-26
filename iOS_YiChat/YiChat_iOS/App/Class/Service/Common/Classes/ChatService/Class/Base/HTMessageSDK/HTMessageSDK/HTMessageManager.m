/*!
 @header  HTMessageManager.m
 
 @abstract
 
 @author  Created by 非夜 on 16/12/27.
 
 @version 1.0 16/12/27 Creation(HTMessage Born)
 
 Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
 
 */

#import "HTMessageManager.h"
#import "HTDBManager.h"
#import "GCDMulticastDelegate.h"
#import "HTMessageDownloadHelper.h"


@interface HTMessageManager()

@property (nonatomic,strong)GCDMulticastDelegate <HTMessageDelegate> *messageManagerDelegate;

@end

@implementation HTMessageManager

- (id)init {
    if (self = [super init]) {
        self.sendImageQuality = 0.6;
        self.messageManagerDelegate = (GCDMulticastDelegate <HTMessageDelegate> *)[[GCDMulticastDelegate alloc] init];
        return self;
    }
    return nil;
}

- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)delegateQueue {
    if (delegateQueue == nil || delegateQueue == NULL) {
        [self.messageManagerDelegate addDelegate:aDelegate delegateQueue:dispatch_get_main_queue()];
    }else{
        [self.messageManagerDelegate addDelegate:aDelegate delegateQueue:delegateQueue];
    }
}

- (void)removeDelegate:(id)aDelegate {
    [self.messageManagerDelegate removeDelegate:aDelegate];
}

- (void)didReceiveMessages:(NSArray *)aMessage {
    if (self.messageManagerDelegate && [self.messageManagerDelegate hasDelegateThatRespondsToSelector:@selector(didReceiveMessages:)]) {
        [self.messageManagerDelegate didReceiveMessages:aMessage];
    }
}

- (void)didReceiveCMDMessage:(NSArray *)aCMDMessages {
    
    if (self.messageManagerDelegate && [self.messageManagerDelegate hasDelegateThatRespondsToSelector:@selector(didReceiveCMDMessage:)]) {
        [self.messageManagerDelegate didReceiveCMDMessage:aCMDMessages];
    }
}

/**
 接收到时间修正消息
 
 @param aMessages 普通消息数组
 */
- (void)didReceiveSeriveTimeCorrectMessages:(NSArray *)aMessages{
    if (self.messageManagerDelegate && [self.messageManagerDelegate hasDelegateThatRespondsToSelector:@selector(didReceiveSeriveTimeCorrectMessages:)]) {
        [self.messageManagerDelegate didReceiveSeriveTimeCorrectMessages:aMessages];
    }
}

- (void)insertOneNormalMessage:(HTMessage *)message {
    [[HTDBManager sharedInstance] insertOneNormalMessage:message];
}
- (void)updateOneNormalMessage:(HTMessage *)message {
    [[HTDBManager sharedInstance] updateOneNormalMessage:message];
}
- (void)deleteOneNormalMessage:(HTMessage *)message {
    [[HTDBManager sharedInstance] deleteOneNormalMessage:message];
}

- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)chatId{
    [[HTDBManager sharedInstance] deleteOneChatterAllMessagesByChatterId:chatId];
}

/**
 *  获取单聊消息
 */
- (void)getSingleChatMessagesWithContent:(NSString *)contentStr
                                    from:(NSString *)from
                                      to:(NSString *)to
                               timestamp:(NSInteger)timestamp
                              completion:(void(^)(NSArray <HTMessage * >*))completion{
    
    [[HTDBManager sharedInstance] getSingleChatMessagesWithContent:contentStr from:from to:to timestamp:timestamp completion:completion];
}

- (void)downLoadMessage:(HTMessage *)message completion:(void(^)(HTMessage * message))completion {
    HTMessageDownloadHelper * helper = [HTMessageDownloadHelper new];
    [helper downLoadActionWithMessage:message completion:completion];
}

@end

