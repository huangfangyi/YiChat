//
//  HTDBManager.h
//  HTMessage
//
//  Created by 非夜 on 17/1/4.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"
#import "HTConversation.h"
#import "HTGroup.h"

@interface HTDBManager : NSObject

// sharedInstance

+ (HTDBManager *)sharedInstance;

// create tables by all kinds,must create these before all below controls 

- (void)createMessageTable;
- (void)createConversationTable;
- (void)createGroupTable;

// message

- (void)insertOneNormalMessage:(HTMessage *)message;
- (void)updateOneNormalMessage:(HTMessage *)message;
- (void)deleteOneNormalMessage:(HTMessage *)message;
- (void)getLocalMessageWithMessageId:(NSString *)messsageId invocation:(void(^)(HTMessage *msg))invocation;
    

// conversation
- (void)loadAllConversationsFromDBCompletion:(void(^)(NSArray *))aResultBlock;
- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)chatterId;
- (void)fetchNormessagesByChatterId:(NSString *)chatterId andTimestamp:(NSInteger)timestamp withOffsetSize:(NSInteger )offset completion:(void(^)(NSArray *result))resultBlocked;
- (void)deleteOneConversationWithChatterId:(NSString *)chatterId;
- (void)updataOneConversationWithChatterConversationModel:(HTConversation*)conversationModel;

/**
 *  获取单聊消息
 */
- (void)getSingleChatMessagesWithContent:(NSString *)contentStr
                                    from:(NSString *)from
                                      to:(NSString *)to
                               timestamp:(NSInteger)timestamp
                              completion:(void(^)(NSArray <HTMessage * >*))completion;

// group

- (void)insertOrUpdateOneGroup:(HTGroup *)groupModel;
- (void)deleteOneGroup:(NSString *)groupId;
- (void)fetchAllGroupsFromDBCompelation:(void(^)(NSArray *result))resultBlocked;


@end
