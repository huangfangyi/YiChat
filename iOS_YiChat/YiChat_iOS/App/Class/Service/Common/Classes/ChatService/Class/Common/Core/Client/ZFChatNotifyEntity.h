//
//  ZFChatNotifyEntity.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatNotify.h"
NS_ASSUME_NONNULL_BEGIN

@interface ZFChatNotifyEntity : NSObject

@property (nonatomic,assign) ZFChatNotifyStyle style;

@property (nonatomic,strong) NSString *superResponseNotifyName;

@property (nonatomic,strong) NSString *notifyName;

@property (nonatomic,weak) id target;

@property (nonatomic,assign) SEL selector;

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style;

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style target:(id)target sel:(SEL)selector;

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style superNotifyName:(NSString *)superNotfyName target:(id)target sel:(SEL)selector;

- (void)addNotify;

- (void)addSuperNotify;

- (void)removeMotify;

- (void)removeSuperNotify;

////postNotifyWithContent  默认回调在异步线程
- (void)postNotifyWithContent:(id)content;

@end

NS_ASSUME_NONNULL_END
