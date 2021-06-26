//
//  ZFTransportPresenter.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFTransportPresenter.h"
#import "ZFChatHelper.h"
#import "ZFChatMessageHelper.h"
#import "ZFChatGlobal.h"
#import "ZFChatRequestHelper.h"
#import "ZFChatStorageHelper.h"
#import "ZFChatStorageApis.h"

@interface ZFTransportPresenter ()

@property (nonatomic,strong) ZFChatNotifyEntity *sendMsgTimeNotify;

@property (nonatomic,strong) HTMessage *msg;

@property (nonatomic,strong) NSArray *to;

@property (nonatomic,strong) NSMutableArray *sendMessageContainArr;

@property (nonatomic,strong) UIImage *photoMessageImage;

@property (nonatomic,strong) NSString *fileMessagePath;

@property (nonatomic,strong) dispatch_semaphore_t lock;

@property (nonatomic,assign) NSInteger num;

@property (nonatomic,copy) void(^zfChatTransportInvocation)();

@property (nonatomic,strong) NSDictionary *userInfoDic;

@property (nonatomic,strong) dispatch_semaphore_t sendLock;

@end

@implementation ZFTransportPresenter

- (void)dealloc{
    [self removeNotifys];
}

- (id)initWithMessage:(HTMessage *)message{
    self = [super init];
    if(self){
        if(message && [message isKindOfClass:[HTMessage class]]){
            _msg = message;
            _num = 0;
            _sendMessageContainArr = [NSMutableArray arrayWithCapacity:0];
            _lock = dispatch_semaphore_create(1);
            _sendLock  = dispatch_semaphore_create(5);
            
            [self addNotify];
        }
    }
    return self;
}

- (void)transportMsgTo:(NSArray *)to invocation:(void(^)(void))completion{
    if(to && [to isKindOfClass:[NSArray class]]){
        if(to.count > 0){
            if(_msg && [_msg isKindOfClass:[HTMessage class]]){
                ZFMessageType messageType = [ZFChatHelper zfChatHeler_getMessageTypeWithHTMessage:_msg];
                
                _to = to;
                _zfChatTransportInvocation = completion;
                
                [self tranportMsgWithMessage:_msg messageType:messageType tos:to];
                return;
            }
        }
    }
    completion();
}

- (void)tranportMsgWithMessage:(HTMessage *)msg messageType:(ZFMessageType)messageType tos:(NSArray *)to{
    
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        _msg = msg;
        
        [ZFChatHelper getCurrentUserDicInvocation:^(NSDictionary *dic) {
            NSDictionary * userInfoDic = dic;
            
            if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
                _userInfoDic = userInfoDic;
            }
            
            [ProjectHelper helper_getGlobalThread:^{
                for (int i = 0; i < to.count; i ++) {
                    NSString *toStr = to[i];
                    if([toStr isKindOfClass:[NSString class]] && toStr){
                        [self sendMessageWithTo:toStr userDic:userInfoDic];
                    }
                }
            }];
            
            
        }];
    }
}

- (void)addNotify{
    
    _sendMsgTimeNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleReceiveMSGTime target:self sel:@selector(didReceiveSendTimeMsg:)];
    [_sendMsgTimeNotify addNotify];
    
}

- (void)removeNotifys{
    [_sendMsgTimeNotify removeMotify];
    _sendMsgTimeNotify = nil;
}

- (void)sendMessageWithTo:(NSString *)to userDic:(NSDictionary *)userInfoDic{
    
    if(_msg && [_msg isKindOfClass:[HTMessage class]] && to && [to isKindOfClass:[NSString class]] && userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
        HTMessage *msg = [[HTMessage alloc] init];
        msg.to = to;
        msg.from = [ZFChatHelper zfChatHelper_getCurrentUser];
        msg.chatType = self.type;
        msg.msgType = _msg.msgType;
        msg.msgId = [self creatUUID];
        msg.body = _msg.body;
        msg.isSender = YES;
        msg.downLoadState = DownloadStateSuccessed;
        msg.sendState = SendStateSending;
        msg.timestamp = [[NSDate date] timeIntervalSince1970] * 1000;
        
        [self sendMessage:msg userDic:userInfoDic];
    }
}

- (void)sendMessage:(HTMessage *)message userDic:(NSDictionary *)userInfoDic{
    if(message && [message isKindOfClass:[HTMessage class]]){
        
        dispatch_semaphore_wait(_sendLock, DISPATCH_TIME_FOREVER);
        
        if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
            [self dealSendMsg:userInfoDic message:message];
        }
        [ProjectHelper helper_getGlobalThread:^{
            
            if(self.ZFTransportPresenterTransPortProgress){
                self.ZFTransportPresenterTransPortProgress(_num, _to.count);
            }
        }];
    }
}

- (void)dealSendMsg:(NSDictionary *)userInfoDic message:(HTMessage *)message{
    
    if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
        
        
        if(message && [message isKindOfClass:[HTMessage class]]){
            [ZFChatHelper addMoreEXTToMessage:message withExt:userInfoDic];
            
            [self.sendMessageContainArr addObject:message];
            
            [ZFChatHelper zfChatHelper_sendMessageUnneedUpload:message completion:^(HTMessage * _Nonnull message, NSError * _Nonnull error) {
                
                if(message.sendState == SendStateFail){
                    _num ++;
                    if(_num == _to.count){
                        if(_zfChatTransportInvocation){
                            _zfChatTransportInvocation();
                        }
                    }
                }
            }];
        }
    }
}

- (void)didReceiveSendTimeMsg:(NSNotification *)obj{
    [ProjectHelper helper_getGlobalThread:^{
        NSArray *messageArr = [obj object];
        
        if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
         
            for (HTMessage *message in messageArr) {
                
                if(message && [message isKindOfClass:[HTMessage class]]){
                    
                    for (int i = 0; i < self.sendMessageContainArr.count; i ++) {
                        HTMessage *obj = self.sendMessageContainArr[i];
                        
                        if(obj && [obj isKindOfClass:[HTMessage class]]){
                            
                            NSString *msgId = obj.msgId;
                            
                            if(msgId && [msgId isKindOfClass:[NSString class]] && message.msgId && [message.msgId isKindOfClass:[NSString class]]){
                                
                                if([msgId isEqualToString:message.msgId]){
                                    
                                    obj.timestamp = message.timestamp;
                                    
//                                    [self uploadMessage:obj];
                                    
                                    [self refreshDataSource:obj];
                                    
                                    _num ++;
                                    if(_num == _to.count){
                                        if(_zfChatTransportInvocation){
                                            _zfChatTransportInvocation();
                                        }
                                    }
                                    
                                    dispatch_semaphore_signal(self.sendLock);
                                    
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }];
}

- (void)uploadMessage:(HTMessage *)message{
    [ProjectHelper helper_getGlobalThread:^{
        [ZFChatRequestHelper zfRequest_uploadMessage:message chatType:self.type];
    }];
}


- (void)refreshDataSource:(HTMessage *)message
{
    if(message && [message isKindOfClass:[HTMessage class]]){
        
        if( message.to && [message.to isKindOfClass:[NSString class]]){
            [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:message];
            [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:message chatId:message.to isReadAllMessage:NO];
        }
    }
}

- (NSString *)creatUUID {
    NSString *uuid = [[NSUUID UUID] UUIDString];
    return uuid;
}

@end
