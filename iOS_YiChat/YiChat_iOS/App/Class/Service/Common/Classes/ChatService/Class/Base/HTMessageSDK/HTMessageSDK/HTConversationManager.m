/*! 
@header  HTConversationManager.m

@abstract 

@author  Created by 非夜 on 16/12/27.

@version 1.0 16/12/27 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.

*/

#import "HTConversationManager.h"
#import "HTDBManager.h"
#import "GCDMulticastDelegate.h"

@interface HTConversationManager()

@property (nonatomic,strong)GCDMulticastDelegate <HTConversationDelegate> *conversationManagerDelegate;

@property (nonatomic,strong) dispatch_semaphore_t conversationLock;

@end

@implementation HTConversationManager

- (id)init {
    if (self = [super init]) {
        self.conversationManagerDelegate = (GCDMulticastDelegate <HTConversationDelegate> *)[[GCDMulticastDelegate alloc] init];
        return self;
    }
    return nil;
}

- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)delegateQueue {
    if (delegateQueue == nil || delegateQueue == NULL) {
        [self.conversationManagerDelegate addDelegate:aDelegate delegateQueue:dispatch_get_main_queue()];
    }else{
        [self.conversationManagerDelegate addDelegate:aDelegate delegateQueue:delegateQueue];
    }
}

- (void)removeDelegate:(id)aDelegate {
    [self.conversationManagerDelegate removeDelegate:aDelegate];
}

- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)chatterId {
    [[HTDBManager sharedInstance] deleteOneChatterAllMessagesByChatterId:chatterId];
}

- (void)fetchNormessagesByChatterId:(NSString *)chatterId andTimestamp:(NSInteger)timestamp withOffsetSize:(NSInteger )offset completion:(void(^)(NSArray *result))resultBlocked {
    [[HTDBManager sharedInstance] fetchNormessagesByChatterId:chatterId andTimestamp:timestamp withOffsetSize:offset completion:resultBlocked];
}

- (void)deleteOneConversationWithChatterId:(NSString *)chatterId isCleanAllHistoryMessage:(BOOL)isClean {
    HTConversation * needDeleteModel = nil;
    for (HTConversation * model in self.conversations) {
        if ([model.chatterId isEqualToString:chatterId]) {
            needDeleteModel = model;
            break;
        }
    }
    [self.conversations removeObject:needDeleteModel];
    [[HTDBManager sharedInstance] deleteOneConversationWithChatterId:chatterId];
    if (self.conversationManagerDelegate && [self.conversationManagerDelegate hasDelegateThatRespondsToSelector:@selector(conversationChanged)]) {
        [self.conversationManagerDelegate conversationChanged];
    }
    if (isClean == YES) {
        [self deleteOneChatterAllMessagesByChatterId:chatterId];
    }
}

- (dispatch_semaphore_t)conversationLock{
    if(!_conversationLock){
        _conversationLock = dispatch_semaphore_create(1);
    }
    return _conversationLock;
}


- (void)updataOneConversationWithChatterConversation:(HTConversation*)conversationModel isReadAllMessage:(BOOL)readAll {
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        dispatch_semaphore_wait(self.conversationLock, DISPATCH_TIME_FOREVER);
        
        if ([conversationModel.lastMessage.chatType isEqualToString:@"1"]) {
            conversationModel.chatterId = conversationModel.lastMessage.isSender ? conversationModel.lastMessage.to : conversationModel.lastMessage.from;
        }else{
            conversationModel.chatterId = conversationModel.lastMessage.to;
        }
        
        BOOL isExist = NO;
        for (HTConversation * model in self.conversations) {
            if ([model.chatterId isEqualToString:conversationModel.chatterId]) {
                if (conversationModel.unreadMessageCount == -1 && !readAll) {
                }else{
                    conversationModel.unreadMessageCount = readAll ? 0 : model.unreadMessageCount;
                }
                
                if (model.conversationExt) {
                    NSMutableDictionary * addExt = model.conversationExt.mutableCopy;
                    if (conversationModel.conversationExt) {
                        [addExt setValuesForKeysWithDictionary:conversationModel.conversationExt];
                    }
                    conversationModel.conversationExt = addExt.copy;
                }
                
                if (![model isKindOfClass:[HTConversation class]]) {
                    break;
                }
                
                NSInteger index = [self.conversations indexOfObject:model];
                if(self.conversations.count - 1 >= index){
                    [self.conversations replaceObjectAtIndex:index withObject:conversationModel];
                }
                isExist = YES;
                break;
            }
        }
        if (!isExist) {
            [self.conversations addObject:conversationModel];
            conversationModel.unreadMessageCount = readAll ? 0 : 1;
        }
        [[HTDBManager sharedInstance] updataOneConversationWithChatterConversationModel:conversationModel];
        if (self.conversationManagerDelegate && [self.conversationManagerDelegate hasDelegateThatRespondsToSelector:@selector(conversationChanged)]) {
            [self.conversationManagerDelegate conversationChanged];
        }
        dispatch_semaphore_signal(self.conversationLock);
        
    });
    
}

- (void)loadAllConversationsFromDBCompletion:(void(^)(NSArray *))aResultBlock {
    [[HTDBManager sharedInstance] loadAllConversationsFromDBCompletion:^(NSArray *results) {
        [self.conversations removeAllObjects];
        [self.conversations addObjectsFromArray:results];
        
        if (self.conversationManagerDelegate && [self.conversationManagerDelegate hasDelegateThatRespondsToSelector:@selector(conversationChanged)]) {
            [self.conversationManagerDelegate conversationChanged];
        }
    }];
}

#pragma mark - getter

- (NSMutableArray *)conversations {
    if (_conversations == nil) {
        _conversations = @[].mutableCopy;
    }
    return _conversations;
}

@end
