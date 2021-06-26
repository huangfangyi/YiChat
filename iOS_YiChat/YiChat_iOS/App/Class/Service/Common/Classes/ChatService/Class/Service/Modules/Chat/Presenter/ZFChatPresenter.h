//
//  ZFChatPresenter.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatGlobal.h"

NS_ASSUME_NONNULL_BEGIN
@class ZFChatConfigure;
@class  ZFChatVC;
@class YiChatUserModel;
@class YiChatGroupInfoModel;
@class HTMessage;
@interface ZFChatPresenter : NSObject

@property (nonatomic,strong) NSMutableArray *chatMessageDataArr;

@property (nonatomic,weak)  ZFChatVC *controlVC;

@property (nonatomic,strong) YiChatUserModel *chatUserInfo;

@property (nonatomic,strong) YiChatGroupInfoModel *chatGroupInfoModel;

@property (nonatomic,strong) NSString *chatId;

+ (id)initialVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType;

- (void)loadMessage;

- (void)updateGroupChatMessgae;

- (void)loadChatInfoInvocation:(void(^)(id model,NSString *error))invocation isUpdate:(BOOL)isUpdate;

- (void)updateChatUI;

- (void)withDrawMessageWithIndexPath:(NSIndexPath *)index;

- (void)copyMessageWithIndexPath:(NSIndexPath *)index;

- (void)deleteMessageWithIndexPath:(NSIndexPath *)index;

- (NSInteger)getPower;

- (void)addRefresh;

- (void)beginRefresh;

- (void)endRefresh;

- (ZFChatConfigure *)getChatDataWithIndex:(NSInteger)index;

- (void)addDataToChatMessageData:(NSArray *)chatDataArr;

- (void)insertDataToChatMessageData:(NSArray *)chatDataArr;

- (void)zfchatAddSendReachbility;

- (void)removeChatMessageDataWithMsgId:(NSString *)msgId isNeedLoadData:(BOOL)isLoadData isNeedScrollToDone:(BOOL)isNeedScrollToDone;

- (void)addNotify;

- (void)removeNotifys;

- (NSString *)getChatUserId;

- (void)makeChartBarWithFrame:(CGRect)frame bgview:(UIView *)bgView;

- (ZFChatType)getChatType;

- (void)resignChatTool;

- (void)changeChatToolBarTextInputContent:(NSString *)content;

- (void)addMessageAlertInfo:(NSDictionary *)info;

- (void)repeatSendMessahe:(HTMessage *)message isScrollToDone:(BOOL)isScrollToDown;

- (void)sendMessage:(HTMessage *)message isScrollToDone:(BOOL)isScrollToDown;

- (void)reloadData:(BOOL)isScrollToDown;

- (void)reloadData:(BOOL)isScrollToDown completion:(void(^)(void))completion;

- (void)sendRedPackgeMessage:(NSString *)title redPackeId:(NSString *)redPackgeId redPackeDes:(NSString *)redPackgeDes redPackgeName:(NSString *)redPackgeName;

- (void)getRedPackge:(NSString *)redPackgeSenderNick sendUserId:(NSString *)senderUserId redPackgeGetNick:(NSString *)redPackgeGetNick;

- (NSDictionary *)getAllOfPhotoMessageUrls:(NSString *)messageId;
    
- (NSDictionary *)getAllOfVideoMessageUrls:(NSString *)messageId;

-(void)loadNotice;

- (void)clean;
@end

NS_ASSUME_NONNULL_END
