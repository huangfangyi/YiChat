//
//  HTDBManager.m
//  HTMessage
//
//  Created by 非夜 on 17/1/4.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import "HTDBManager.h"
#import "QSDatabase.h"
#import "QSDatabaseQueue.h"
#import "HTClient.h"
#import "NSObject+QSModel.h"

// db名称
static NSString *dbName = @"HTMessage.db";
// 临时会话列表字段
static NSString *dbChatterTableName = @"dbChatterTableName";
static NSString *dbChatterId = @"dbChatterId";
static NSString *dbChatterUnreadCount = @"dbChatterUnreadCount";
static NSString *dbChatterLeastMessage = @"dbChatterLeastMessage";
static NSString *dbChatterTimestamp = @"dbChatterTimestamp";
static NSString *dbChatterExt = @"dbChatterExt";
// 聊天记录字段
static NSString *dbMessageTableName = @"dbMessageTableName";
static NSString *dbMessageString = @"dbMessageString";
static NSString *dbMessageId = @"dbMessageId";
static NSString *dbMessageTimestamp = @"dbMessageTimestamp";
static NSString *dbMessageChatterId = @"dbMessageChatterId";
// 群组字段
static NSString *dbGroupTableName = @"dbGroupTableName";
static NSString *dbGroupId = @"dbGroupId";
static NSString *dbGroupModel = @"dbGroupModel";

@interface HTDBManager()

@property(nonatomic, strong) NSString *dbPath;
@property(nonatomic, strong) NSString *currentUserName;
@property(nonatomic, strong) dispatch_queue_t dbQueue;

@end

static HTDBManager *_sharedManger = nil;

@implementation HTDBManager

+ (instancetype)sharedInstance {
    // 不登录,根本不让碰存储
    NSString *currentUser = [[HTClient sharedInstance] currentUsername];
    if (currentUser.length > 0) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            _sharedManger = [HTDBManager new];
        });
        return _sharedManger;
    } else {
        return nil;
    }
}

// must init/reinit all kinds tables after a new user login success

- (void)createMessageTable {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
    NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS '%@' ('%@' TEXT,'%@' VARCHAR(13),'%@' VARCHAR(36),'%@' VARCHAR(36) PRIMARY KEY)",
                                table,dbMessageString,dbMessageTimestamp,dbMessageChatterId,dbMessageId];;
    [self performExecuteUpdateDBControlWithSql:sqlCreateTable showInfo:@"创建消息表失败"];
}

- (void)createConversationTable {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbChatterTableName];
    NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS '%@' ('%@' VARCHAR(11),'%@' VARCHAR(13),'%@' VARCHAR(36) PRIMARY KEY,'%@' TEXT,'%@' TEXT)",
                                table,dbChatterUnreadCount,dbChatterTimestamp,dbChatterId,dbChatterLeastMessage,dbChatterExt];
    [self performExecuteUpdateDBControlWithSql:sqlCreateTable showInfo:@"创建db会话表失败"];
}

- (void)createGroupTable {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbGroupTableName];
    NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS '%@' ('%@' TEXT,'%@' VARCHAR(36) PRIMARY KEY)",table,dbGroupModel,dbGroupId];
    [self performExecuteUpdateDBControlWithSql:sqlCreateTable showInfo:@"创建db群组表失败"];
}

// about message

- (void)insertOneNormalMessage:(HTMessage *)message {
    NSString * chatterId = nil;
    if ([message.chatType isEqualToString:@"1"]) {
        if (message.isSender == YES) {
            chatterId = message.to;
        }else{
            chatterId = message.from;
        }
    }else{
        chatterId = message.to;
    }
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
    NSString * messageStr = [message modelToJSONString];
    NSString *replaceSql = [NSString stringWithFormat:@"INSERT INTO '%@'('%@','%@','%@','%@' ) VALUES ('%@','%ld','%@','%@')",table,dbMessageId, dbMessageTimestamp, dbMessageString,dbMessageChatterId,message.msgId,message.timestamp,messageStr,chatterId];
    [self performExecuteUpdateDBControlWithSql:replaceSql showInfo:@"插入一条消息失败"];
}

- (void)updateOneNormalMessage:(HTMessage *)message {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
    NSString * messageStr = [message modelToJSONString];
    NSString *update = [NSString stringWithFormat:@"UPDATE '%@' SET dbMessageString = '%@' WHERE dbMessageId = '%@'",table,messageStr,message.msgId];
    [self performExecuteUpdateDBControlWithSql:update showInfo:@"更新一条消息失败"];
}

- (void)deleteOneNormalMessage:(HTMessage *)message {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
    NSString *updateSql = [NSString stringWithFormat:@"DELETE FROM '%@' WHERE dbMessageId = '%@'", table,message.msgId];
    [self performExecuteUpdateDBControlWithSql:updateSql showInfo:@"删除一条消息失败"];
}
    
- (void)getLocalMessageWithMessageId:(NSString *)messsageId invocation:(void(^)(HTMessage *msg))invocation{
    
    QSDatabaseQueue *queue =
    [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(self.dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            
            NSLog(@"开始查询单聊消息");
            
            NSString *table =
            [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
            NSString *selectSql = [NSString
                                   stringWithFormat:@"SELECT * FROM '%@' WHERE  dbMessageId = '%@'", table,messsageId];
            
            
            
            
            NSMutableArray * messages = @[].mutableCopy;
            QSResultSet *rs = [db2 executeQuery:selectSql];
            
            NSLog(@"开始查询记录 %@",rs);
            
            
            while ([rs next]) {
            
                NSString * messageString = [rs stringForColumn:dbMessageString];
                HTMessage *message = [HTMessage modelWithJSON:messageString];
                if(message){
                    [messages addObject:message];
                }
            }
            
            
            invocation(messages.firstObject);
        }];
    });
    
}

// about conversation

- (void)deleteOneChatterAllMessagesByChatterId:(NSString *)chatterId {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
    NSString *delSql = [NSString stringWithFormat:@"DELETE FROM '%@' WHERE dbMessageChatterId = '%@'", table,chatterId];
    [self performExecuteUpdateDBControlWithSql:delSql showInfo:@"删除一条会话的所有消息失败"];
}

- (void)fetchNormessagesByChatterId:(NSString *)chatterId andTimestamp:(NSInteger)timestamp withOffsetSize:(NSInteger )offset completion:(void(^)(NSArray *result))resultBlocked {
    QSDatabaseQueue *queue =
    [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(self.dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            
            NSString *table =
            [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
            NSString *selectSql = [NSString
                                   stringWithFormat:@"SELECT * FROM '%@' WHERE dbMessageChatterId = '%@' AND dbMessageTimestamp < '%ld' ORDER BY dbMessageTimestamp DESC Limit '%ld'", table,chatterId,timestamp,offset];
            if (timestamp == -1) {
                selectSql = [NSString
                             stringWithFormat:@"SELECT * FROM '%@' WHERE dbMessageChatterId = '%@' ORDER BY dbMessageTimestamp DESC Limit '%ld'", table,chatterId,offset];
            }
            NSMutableArray * messages = @[].mutableCopy;
            QSResultSet *rs = [db2 executeQuery:selectSql];
            while ([rs next]) {
                NSString * messageString = [rs stringForColumn:dbMessageString];
                HTMessage *message = [HTMessage modelWithJSON:messageString];
                [messages addObject:message];
            }
            [messages sortUsingComparator:^NSComparisonResult(HTMessage *  _Nonnull obj1, HTMessage *  _Nonnull obj2) {
                if (obj1.timestamp > obj2.timestamp) {
                    return (NSComparisonResult)NSOrderedDescending;
                }else{
                    return (NSComparisonResult)NSOrderedAscending;
                }
                
            }];
            resultBlocked(messages.copy);
        }];
    });
}

- (void)deleteOneConversationWithChatterId:(NSString *)chatterId{
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbChatterTableName];
    NSString *delSql = [NSString stringWithFormat:@"DELETE FROM '%@' WHERE dbChatterId = '%@'", table,chatterId];
    [self performExecuteUpdateDBControlWithSql:delSql showInfo:@"从数据库中删除一条会话失败"];
}

- (void)updataOneConversationWithChatterConversationModel:(HTConversation*)conversationModel{
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbChatterTableName];
    NSString *replaceSql = [NSString stringWithFormat:@"REPLACE INTO '%@'('%@','%@','%@','%@','%@' ) VALUES ('%ld','%@','%@','%ld','%@')",table,dbChatterUnreadCount, dbChatterId,dbChatterLeastMessage,dbChatterTimestamp,dbChatterExt,conversationModel.unreadMessageCount,conversationModel.chatterId,[conversationModel.lastMessage modelToJSONString],conversationModel.lastMessage.timestamp,[self dictionaryToStringWithDictionary:conversationModel.conversationExt]];
    [self performExecuteUpdateDBControlWithSql:replaceSql showInfo:@"更新一条会话失败"];
}

/**
 *  获取单聊消息
 */
- (void)getSingleChatMessagesWithContent:(NSString *)contentStr
                                    from:(NSString *)from
                                      to:(NSString *)to
                               timestamp:(NSInteger)timestamp
                              completion:(void(^)(NSArray <HTMessage * >*))completion{
    QSDatabaseQueue *queue =
    [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(self.dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            
            NSLog(@"开始查询单聊消息");
            
            NSString *table =
            [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbMessageTableName];
            NSString *selectSql = [NSString
                                   stringWithFormat:@"SELECT * FROM '%@' WHERE  dbMessageTimestamp < '%ld'", table,timestamp];
            
            if(timestamp == -1){
                selectSql = [NSString
                             stringWithFormat:@"SELECT * FROM '%@'", table];
            }
           
            
            NSMutableArray * messages = @[].mutableCopy;
            QSResultSet *rs = [db2 executeQuery:selectSql];
            
            NSLog(@"开始查询记录 %@",rs);
            
            
            while ([rs next]) {
                NSString * messageString = [rs stringForColumn:dbMessageString];
                HTMessage *message = [HTMessage modelWithJSON:messageString];
                
                NSLog(@"查询记录 %@ %@",rs,message);
                
                if ([message.body.content isKindOfClass:[NSString class]]) {
                    if(message.body.content != nil && message.body.content.length > 0) {
                        if([message.body.content containsString:contentStr]){
                            [messages addObject:message];
                        }
                    }
                }
            }
            
            [messages sortUsingComparator:^NSComparisonResult(HTMessage *  _Nonnull obj1, HTMessage *  _Nonnull obj2) {
                if (obj1.timestamp > obj2.timestamp) {
                    return (NSComparisonResult)NSOrderedDescending;
                }else{
                    return (NSComparisonResult)NSOrderedAscending;
                }
            }];
            
            NSMutableArray *completionArr = [NSMutableArray arrayWithCapacity:0];
            
            NSLog(@"查询聊天消息完成 %@ messages ->%@",[NSString stringWithFormat:@"%ld",timestamp],messages);
            
            if(from != nil && to != nil){
                [messages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    HTMessage *msg = obj;
                    if([msg.from isEqualToString:from] && [msg.to isEqualToString:to]){
                        [completionArr addObject:msg];
                    }
                }];
                completion(completionArr.copy);
                return ;
            }
            
            if(from == nil && to != nil){
                [messages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    HTMessage *msg = obj;
                    if([msg.to isEqualToString:to]){
                        [completionArr addObject:msg];
                    }
                }];
                completion(completionArr.copy);
                return ;
            }
            if(to == nil && from != nil){
                [messages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    HTMessage *msg = obj;
                    if([msg.from isEqualToString:from]){
                        [completionArr addObject:msg];
                    }
                }];
                completion(completionArr.copy);
                return ;
            }
            
            if(from == nil && to == nil){
                completion(messages.copy);
                return ;
            }
            
            completion(messages.copy);
        }];
    });
}

// about conversation

- (void)loadAllConversationsFromDBCompletion:(void(^)(NSArray *))aResultBlock {
    
    QSDatabaseQueue *queue =
    [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(_dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            
            NSMutableArray * conversations = @[].mutableCopy;
            NSString *table =
            [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbChatterTableName];
            NSString *selectSql = [NSString
                                   stringWithFormat:@"SELECT * FROM '%@' order by dbChatterTimestamp DESC", table];
            QSResultSet *rs = [db2 executeQuery:selectSql];
            while ([rs next]) {
                NSInteger unread = [rs longLongIntForColumn:dbChatterUnreadCount];
                NSString *chatid = [rs stringForColumn:dbChatterId];
                NSString *least = [rs stringForColumn:dbChatterLeastMessage];
                NSDictionary *ext = [self stringToDictionaryWithString:[rs stringForColumn:dbChatterExt]];
                HTConversation *conversation = [HTConversation new];
                conversation.unreadMessageCount = unread;
                conversation.chatterId = chatid;
                conversation.conversationExt = ext;
                HTMessage * message = [HTMessage modelWithJSON:least];
                conversation.lastMessage = message;
                [conversations addObject:conversation];
            }
            aResultBlock(conversations.copy);
        }];
    });
}

- (void)insertOrUpdateOneGroup:(HTGroup *)groupModel {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbGroupTableName];
    NSString *replaceSql = [NSString stringWithFormat:@"Replace INTO '%@'('%@','%@' ) VALUES ('%@','%@')",table,dbGroupId,dbGroupModel,groupModel.groupId,[groupModel modelToJSONString]];
    [self performExecuteUpdateDBControlWithSql:replaceSql showInfo:@"插入更新一条群失败"];
}

- (void)deleteOneGroup:(NSString *)groupId {
    NSString *table = [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbGroupTableName];
    NSString *delSql = [NSString stringWithFormat:@"DELETE FROM '%@' WHERE dbGroupId = '%@'", table,groupId];
    [self performExecuteUpdateDBControlWithSql:delSql showInfo:@"删除一个群组失败"];
}

- (void)fetchAllGroupsFromDBCompelation:(void(^)(NSArray *result))resultBlocked {
    QSDatabaseQueue *queue =
    [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(_dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            
            NSString *table =
            [NSString stringWithFormat:@"%@_%@", self.currentUserName, dbGroupTableName];
            NSString *selectSql = [NSString
                                   stringWithFormat:@"SELECT * FROM '%@'", table];
            NSMutableArray * groups = @[].mutableCopy;
            QSResultSet *rs = [db2 executeQuery:selectSql];
            while ([rs next]) {
                NSString * groupString = [rs stringForColumn:dbGroupModel];
                HTGroup *group = [HTGroup modelWithJSON:groupString];
                [groups addObject:group];
            }
            resultBlocked(groups.copy);
        }];
    });
}

// init db in local path

- (NSString *)dbPath {
    if (_dbPath == nil) {
        
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
        NSString *documents = [paths objectAtIndex:0];
        _dbPath = [documents stringByAppendingPathComponent:dbName];
        
    }
    return _dbPath;
}

// init a only queue to control db to all control in order

- (dispatch_queue_t)dbQueue {
    if (_dbQueue == nil) {
        _dbQueue = dispatch_queue_create("com.feiye.htmessage", DISPATCH_QUEUE_SERIAL);
    }
    return _dbQueue;
}

// fetch current db owner

- (NSString *)currentUserName {
    _currentUserName = [[HTClient sharedInstance] currentUsername];
    return _currentUserName;
}

// all db control will perform this method

- (void)performExecuteUpdateDBControlWithSql:(NSString *)sql showInfo:(NSString *)info {
    QSDatabaseQueue *queue = [QSDatabaseQueue databaseQueueWithPath:self.dbPath];
    dispatch_async(self.dbQueue, ^{
        [queue inDatabase:^(QSDatabase *db2) {
            BOOL res = [db2 executeUpdate:sql];
            if (!res) {
                NSLog(@"%@",info);
            }
        }];
    });
}

// private

- (NSDictionary *)stringToDictionaryWithString:(NSString *)string {
    if (string == nil) {
        return nil;
    }
    NSData *jsonData = [string dataUsingEncoding:NSUTF8StringEncoding];
    if (jsonData == nil) {
        return nil;
    }
    NSError *err;
    NSDictionary *dic =
    [NSJSONSerialization JSONObjectWithData:jsonData
                                    options:NSJSONReadingMutableContainers
                                      error:&err];
    return dic;
}

- (NSString *)dictionaryToStringWithDictionary:(NSDictionary *)dic {
    if (dic == nil) {
        return nil;
    }
    NSData *jsonData =
    [NSJSONSerialization dataWithJSONObject:dic options:0 error:nil];
    if (jsonData == nil) {
        return nil;
    }
    NSString *jsonString =
    [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return jsonString;
}
@end
