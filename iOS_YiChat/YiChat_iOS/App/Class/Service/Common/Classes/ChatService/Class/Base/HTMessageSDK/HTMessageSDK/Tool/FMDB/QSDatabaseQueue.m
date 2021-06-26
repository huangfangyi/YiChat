//
//  QSDatabasePool.m
//  QSDB
//
//  Created by August Mueller on 6/22/11.
//  Copyright 2011 Flying Meat Inc. All rights reserved.
//

#import "QSDatabaseQueue.h"
#import "QSDatabase.h"

/*
 
 Note: we call [self retain]; before using dispatch_sync, just incase 
 QSDatabaseQueue is released on another thread and we're in the middle of doing
 something in dispatch_sync
 
 */
 
@implementation QSDatabaseQueue

@synthesize path = _path;

+ (id)databaseQueueWithPath:(NSString*)aPath {
    
    QSDatabaseQueue *q = [[self alloc] initWithPath:aPath];
    
    QSDBAutorelease(q);
    
    return q;
}

- (id)initWithPath:(NSString*)aPath {
    
    self = [super init];
    
    if (self != nil) {
        
        _db = [QSDatabase databaseWithPath:aPath];
        QSDBRetain(_db);
        
        if (![_db open]) {
            NSLog(@"Could not create database queue for path %@", aPath);
            QSDBRelease(self);
            return 0x00;
        }
        
        _path = QSDBReturnRetained(aPath);
        
        _queue = dispatch_queue_create([[NSString stringWithFormat:@"QSDB.%@", self] UTF8String], NULL);
    }
    
    return self;
}

- (void)dealloc {
    
    QSDBRelease(_db);
    QSDBRelease(_path);
    
    if (_queue) {
        QSDBDispatchQueueRelease(_queue);
        _queue = 0x00;
    }
#if ! __has_feature(objc_arc)
    [super dealloc];
#endif
}

- (void)close {
    QSDBRetain(self);
    dispatch_sync(_queue, ^() { 
        [_db close];
        QSDBRelease(_db);
        _db = 0x00;
    });
    QSDBRelease(self);
}

- (QSDatabase*)database {
    if (!_db) {
        _db = QSDBReturnRetained([QSDatabase databaseWithPath:_path]);
        
        if (![_db open]) {
            NSLog(@"QSDatabaseQueue could not reopen database for path %@", _path);
            QSDBRelease(_db);
            _db  = 0x00;
            return 0x00;
        }
    }
    
    return _db;
}

- (void)inDatabase:(void (^)(QSDatabase *db))block {
    QSDBRetain(self);
    
    dispatch_sync(_queue, ^() {
        
        QSDatabase *db = [self database];
        block(db);
        
        if ([db hasOpenResultSets]) {
            NSLog(@"Warning: there is at least one open result set around after performing [QSDatabaseQueue inDatabase:]");
        }
    });
    
    QSDBRelease(self);
}


- (void)beginTransaction:(BOOL)useDeferred withBlock:(void (^)(QSDatabase *db, BOOL *rollback))block {
    QSDBRetain(self);
    dispatch_sync(_queue, ^() { 
        
        BOOL shouldRollback = NO;
        
        if (useDeferred) {
            [[self database] beginDeferredTransaction];
        }
        else {
            [[self database] beginTransaction];
        }
        
        block([self database], &shouldRollback);
        
        if (shouldRollback) {
            [[self database] rollback];
        }
        else {
            [[self database] commit];
        }
    });
    
    QSDBRelease(self);
}

- (void)inDeferredTransaction:(void (^)(QSDatabase *db, BOOL *rollback))block {
    [self beginTransaction:YES withBlock:block];
}

- (void)inTransaction:(void (^)(QSDatabase *db, BOOL *rollback))block {
    [self beginTransaction:NO withBlock:block];
}

#if SQLITE_VERSION_NUMBER >= 3007000
- (NSError*)inSavePoint:(void (^)(QSDatabase *db, BOOL *rollback))block {
    
    static unsigned long savePointIdx = 0;
    __block NSError *err = 0x00;
    QSDBRetain(self);
    dispatch_sync(_queue, ^() { 
        
        NSString *name = [NSString stringWithFormat:@"savePoint%ld", savePointIdx++];
        
        BOOL shouldRollback = NO;
        
        if ([[self database] startSavePointWithName:name error:&err]) {
            
            block([self database], &shouldRollback);
            
            if (shouldRollback) {
                [[self database] rollbackToSavePointWithName:name error:&err];
            }
            else {
                [[self database] releaseSavePointWithName:name error:&err];
            }
            
        }
    });
    QSDBRelease(self);
    return err;
}
#endif

@end
