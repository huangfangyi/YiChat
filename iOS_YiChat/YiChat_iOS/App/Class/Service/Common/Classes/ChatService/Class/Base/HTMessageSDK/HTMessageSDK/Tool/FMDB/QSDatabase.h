#import <Foundation/Foundation.h>
#import "sqlite3.h"
#import "QSResultSet.h"
#import "QSDatabasePool.h"


#if ! __has_feature(objc_arc)
    #define QSDBAutorelease(__v) ([__v autorelease]);
    #define QSDBReturnAutoreleased QSDBAutorelease

    #define QSDBRetain(__v) ([__v retain]);
    #define QSDBReturnRetained QSDBRetain

    #define QSDBRelease(__v) ([__v release]);

	#define QSDBDispatchQueueRelease(__v) (dispatch_release(__v));
#else
    // -fobjc-arc
    #define QSDBAutorelease(__v)
    #define QSDBReturnAutoreleased(__v) (__v)

    #define QSDBRetain(__v)
    #define QSDBReturnRetained(__v) (__v)

    #define QSDBRelease(__v)

	#if TARGET_OS_IPHONE
		// Compiling for iOS
		#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 60000
			// iOS 6.0 or later
			#define QSDBDispatchQueueRelease(__v)
		#else
			// iOS 5.X or earlier
			#define QSDBDispatchQueueRelease(__v) (dispatch_release(__v));
		#endif
	#else
		// Compiling for Mac OS X
		#if MAC_OS_X_VERSION_MIN_REQUIRED >= 1080     
			// Mac OS X 10.8 or later
			#define QSDBDispatchQueueRelease(__v)
		#else
			// Mac OS X 10.7 or earlier
			#define QSDBDispatchQueueRelease(__v) (dispatch_release(__v));
		#endif
	#endif
#endif


@interface QSDatabase : NSObject  {
    
    sqlite3*            _db;
    NSString*           _databasePath;
    BOOL                _logsErrors;
    BOOL                _crashOnErrors;
    BOOL                _traceExecution;
    BOOL                _checkedOut;
    BOOL                _shouldCacheStatements;
    BOOL                _isExecutingStatement;
    BOOL                _inTransaction;
    int                 _busyRetryTimeout;
    
    NSMutableDictionary *_cachedStatements;
    NSMutableSet        *_openResultSets;
    NSMutableSet        *_openFunctions;

}


@property (atomic, assign) BOOL traceExecution;
@property (atomic, assign) BOOL checkedOut;
@property (atomic, assign) int busyRetryTimeout;
@property (atomic, assign) BOOL crashOnErrors;
@property (atomic, assign) BOOL logsErrors;
@property (atomic, retain) NSMutableDictionary *cachedStatements;


+ (id)databaseWithPath:(NSString*)inPath;
- (id)initWithPath:(NSString*)inPath;

- (BOOL)open;
#if SQLITE_VERSION_NUMBER >= 3005000
- (BOOL)openWithFlags:(int)flags;
#endif
- (BOOL)close;
- (BOOL)goodConnection;
- (void)clearCachedStatements;
- (void)closeOpenResultSets;
- (BOOL)hasOpenResultSets;

// encryption methods.  You need to have purchased the sqlite encryption extensions for these to work.
- (BOOL)setKey:(NSString*)key;
- (BOOL)rekey:(NSString*)key;

- (NSString *)databasePath;

- (NSString*)lastErrorMessage;

- (int)lastErrorCode;
- (BOOL)hadError;
- (NSError*)lastError;

- (sqlite_int64)lastInsertRowId;

- (sqlite3*)sqliteHandle;

- (BOOL)update:(NSString*)sql withErrorAndBindings:(NSError**)outErr, ...;
- (BOOL)executeUpdate:(NSString*)sql, ...;
- (BOOL)executeUpdateWithFormat:(NSString *)format, ...;
- (BOOL)executeUpdate:(NSString*)sql withArgumentsInArray:(NSArray *)arguments;
- (BOOL)executeUpdate:(NSString*)sql withParameterDictionary:(NSDictionary *)arguments;

- (QSResultSet *)executeQuery:(NSString*)sql, ...;
- (QSResultSet *)executeQueryWithFormat:(NSString*)format, ...;
- (QSResultSet *)executeQuery:(NSString *)sql withArgumentsInArray:(NSArray *)arguments;
- (QSResultSet *)executeQuery:(NSString *)sql withParameterDictionary:(NSDictionary *)arguments;

- (BOOL)rollback;
- (BOOL)commit;
- (BOOL)beginTransaction;
- (BOOL)beginDeferredTransaction;
- (BOOL)inTransaction;
- (BOOL)shouldCacheStatements;
- (void)setShouldCacheStatements:(BOOL)value;

#if SQLITE_VERSION_NUMBER >= 3007000
- (BOOL)startSavePointWithName:(NSString*)name error:(NSError**)outErr;
- (BOOL)releaseSavePointWithName:(NSString*)name error:(NSError**)outErr;
- (BOOL)rollbackToSavePointWithName:(NSString*)name error:(NSError**)outErr;
- (NSError*)inSavePoint:(void (^)(BOOL *rollback))block;
#endif

+ (BOOL)isSQLiteThreadSafe;
+ (NSString*)sqliteLibVersion;

- (int)changes;

- (void)makeFunctionNamed:(NSString*)name maximumArguments:(int)count withBlock:(void (^)(sqlite3_context *context, int argc, sqlite3_value **argv))block;

@end

@interface QSStatement : NSObject {
    sqlite3_stmt *_statement;
    NSString *_query;
    long _useCount;
}

@property (atomic, assign) long useCount;
@property (atomic, retain) NSString *query;
@property (atomic, assign) sqlite3_stmt *statement;

- (void)close;
- (void)reset;

@end

