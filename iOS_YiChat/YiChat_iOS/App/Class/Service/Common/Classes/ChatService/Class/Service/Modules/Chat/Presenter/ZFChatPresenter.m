//
//  ZFChatPresenter.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatPresenter.h"
#import "ZFChatToolBar.h"
#import "ZFChatGlobal.h"

#import "ProjectUIHelper.h"

#import "ZFChatStorageHelper.h"
#import "ZFChatHelper.h"
#import "ZFChatRequestHelper.h"
#import "ZFChatVC.h"
#import "ZFChatConfigure.h"
#import "ZFChatMessageHelper.h"
#import "ZFChatUIHelper.h"
#import <MJRefresh.h>
#import "YiChatSendRedPacketVC.h"

#import "NSObject+YYModel.h"
#import "ProjectTranslateHelper.h"
#import "NSString+URLEncoding.h"
#import "ZFGroupHelper.h"
#import "ZFChatSendReachbility.h"

#import "KLCPopup.h"
#import "YiChatShowZhenView.h"
#import "YiChatGroupZhenView.h"

#import "ZFChatHelper.h"

#import "YiChatGroupNoticeModel.h"
#import "ZFPersonCardSelecteVC.h"
#import "YiChatGroupMemberListVC.h"

#import "ZFChatSelecteMessageAlertVC.h"
@interface  ZFChatPresenter ()

@property (nonatomic,strong) NSString *type;

@property (nonatomic,strong) ZFChatToolBar *chatToolBar;

@property (nonatomic,strong) dispatch_queue_t receiveQueue;

@property (nonatomic,strong) dispatch_queue_t sendQueue;

@property (nonatomic,strong) ZFChatNotifyEntity *connectNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *msgNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *cmdMsgNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *sendMsgTimeNotify;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeActive;
@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeBackground;
    
@property (nonatomic,strong) ZFChatNotifyEntity *notify_groupDeleteNotify;
    
@property (nonatomic,strong) ZFChatNotifyEntity *notify_friendNotify;

@property (nonatomic,assign) NSInteger nearestChatMessageTimeUnix;

@property (nonatomic,strong) dispatch_semaphore_t dealMessageReceiveLock;

@property (nonatomic,strong) dispatch_semaphore_t dealMessageSendLock;

@property (nonatomic,strong) dispatch_semaphore_t dealReloadDataLock;

@property (nonatomic,strong) dispatch_semaphore_t dealRereshChatListlock;

@property (nonatomic,assign) NSInteger updateChatListTime;

@property (nonatomic,strong) MJRefreshHeader *refreshHeader;

@property (nonatomic,assign) BOOL isFirstLoad;

@property (nonatomic, assign) NSInteger groupPower;

@property (nonatomic,assign) NSInteger groupSilenceState;
    
@property (nonatomic,assign) NSInteger singleSilenceState;

@property (nonatomic,strong) ZFChatSendReachbility *sendReachBility;

@property (nonatomic,strong) KLCPopup *popView;

@property (nonatomic,strong) dispatch_semaphore_t sendImagesLock;
    
@property (nonatomic,assign) BOOL isLoadingMessageData;

@property (nonatomic,strong) NSMutableArray *messageAlertDicArr;

@property (nonatomic,strong) NSString *lastDeleteStr;

@end

@implementation ZFChatPresenter

- (void)dealloc{
    dispatch_semaphore_signal(_dealMessageReceiveLock);
    dispatch_semaphore_signal(_dealMessageSendLock);
    dispatch_semaphore_signal(_dealReloadDataLock);
    dispatch_semaphore_signal(_dealRereshChatListlock);
}

- (id)init{
    self = [super init];
    if(self){
        _nearestChatMessageTimeUnix = -1;
        _chatMessageDataArr = [NSMutableArray arrayWithCapacity:0];
        _dealMessageSendLock = dispatch_semaphore_create(1);
        _dealMessageReceiveLock = dispatch_semaphore_create(1);
        _dealReloadDataLock = dispatch_semaphore_create(1);
        _sendImagesLock = dispatch_semaphore_create(1);
        _dealRereshChatListlock = dispatch_semaphore_create(1);
        _isFirstLoad = YES;
        _isLoadingMessageData = NO;
        _groupPower = - 1;
        _messageAlertDicArr = [NSMutableArray arrayWithCapacity:0];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(clearMsg) name:@"clearMsg" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(searchLocalMsg:) name:@"SearchLocal_msg" object:nil];
    }
    return self;
}

-(void)clearMsg{
     [self.chatMessageDataArr removeAllObjects];
    
     [ProjectHelper helper_getMainThread:^{
         [self reloadData:YES];
     }];
}
//查询聊天记录
-(void)searchLocalMsg:(NSNotification *)noti{
    NSDictionary *data = noti.userInfo;
    id m = data[@"data"];
    if ([m isKindOfClass:[HTMessage class]]) {
        HTMessage *searchMsg = m;
        WS(weakSelf);
        [ZFChatRequestHelper zfRequestHelper_loadSingleChatMessageRecorderWithChatId:self.chatId lastestMessageTimeUnix:-1 numsForPage:100000 completion:^(NSArray * _Nonnull messageArr, NSString * _Nonnull error) {
            [weakSelf.controlVC.cTable.mj_header endRefreshing];
            if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
                if(messageArr.count > 0){
                    NSMutableArray *tmp = messageArr.mutableCopy;
                    NSInteger index = 0;
                    for (int i = 0; i < tmp.count; i ++) {
                        HTMessage *searchMsgtmp = tmp[i];
                        if(searchMsgtmp && [searchMsgtmp isKindOfClass:[HTMessage class]]){
                            if([searchMsgtmp.msgId isEqualToString: searchMsg.msgId]){
                                index = i;
                                break;
                            }          
                        }
                    }
                                 
                    if(index >= 5){
                        index = index - 5;
                    }
                    else{
                        index = 0;
                    }
                    NSMutableArray *data = [NSMutableArray arrayWithCapacity:0];
                    for (NSInteger i = index; i < tmp.count; i ++) {
                        if(tmp.count - 1 >= i ){
                            [data addObject:tmp[i]];
                        }
                    }
        
                    if(data.count > 0){
                        self.isFirstLoad = YES;
                        [self.chatMessageDataArr removeAllObjects];
                        HTMessage *first = data.firstObject;
                        _nearestChatMessageTimeUnix = first.timestamp;
                        BOOL isScrollToDown = NO;
                         [weakSelf dealRequestDataWithMessageListArr:data isNeedScrollToDown:isScrollToDown isAdd:YES];
                         weakSelf.isFirstLoad = NO;
                        weakSelf.isLoadingMessageData = NO;
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                            NSIndexPath *index = [NSIndexPath indexPathForRow:0 inSection:0];
                            [self.controlVC.cTable scrollToRowAtIndexPath:index atScrollPosition:UITableViewScrollPositionBottom animated:NO];
                        });
                    }
                }
            }
        }];
    }
}

- (ZFChatSendReachbility *)sendReachBility{
    if(!_sendReachBility){
        WS(weakSelf);
        _sendReachBility = [[ZFChatSendReachbility alloc] init];
        _sendReachBility.ZFChatSendReachbilityCanSendMsg = ^{
            dispatch_semaphore_signal(weakSelf.dealMessageSendLock);
        };
    }
    return _sendReachBility;
}

- (void)zfchatAddSendReachbility{
    [self sendReachBility];
}

- (void)removeSendReachBility{
    [self.sendReachBility clean];
    self.sendReachBility = nil;
}

+ (id)initialVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType{
    if([chatId isKindOfClass:[NSString class]]){
        if(chatId){
            ZFChatPresenter *presenter = [[ZFChatPresenter alloc] init];
            presenter.chatId = chatId;
            presenter.type = chatType;
            return presenter;
        }
    }
    return nil;
}

- (NSInteger)getPower{
    return _groupPower;
}

- (void)addRefresh{
    WS(weakSelf);
    
    self.controlVC.cTable.mj_header = [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        [weakSelf loadMessage];
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf endRefresh];
        });
    }];
}

- (void)beginRefresh{
    [self loadMessage];
}

- (void)endRefresh{
    if(self.controlVC){
        [self.controlVC.cTable.mj_header endRefreshing];
    }
}

- (void)updateChatUI{
     [self reloadData:NO];
}

- (void)loadChatInfoInvocation:(void(^)(id model,NSString *error))invocation isUpdate:(BOOL)isUpdate{
    if(self.chatId && [self.chatId isKindOfClass:[NSString class]]){
        if(self.getChatType == ZFChatTypeChat){
            
            [ZFChatHelper fetchUserInfoWithUserId:self.chatId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    self.chatUserInfo = model;
                }
                invocation(self.chatUserInfo,nil);
            }];
            
            [ProjectHelper helper_getMainThread:^{
                [self.chatToolBar changeAddViewWithSingleChat];
            }];
            
            return;
        }
        else if(self.getChatType == ZFChatTypeGroup){
            
            WS(weakSelf);
            
            if(isUpdate){
                [ZFChatHelper fetchGroupInfoWithGroupId:self.chatId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                        [weakSelf getGroupInfoDeal:model];
                    }
                    if(invocation){
                        invocation(self.chatGroupInfoModel,nil);
                    }
                    
                    [ZFChatHelper updateGroupInfoWithGroupId:self.chatId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                        
                        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                            [weakSelf getGroupInfoDeal:model];
                        }
                        
                        if(invocation){
                            invocation(self.chatGroupInfoModel,nil);
                        }
                        
                    }];
                        
                  
                }];
                
               
            }
            else{
                
                [ZFChatHelper fetchGroupInfoWithGroupId:self.chatId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                        [weakSelf getGroupInfoDeal:model];
                    }
                    invocation(self.chatGroupInfoModel,nil);
                    return;
                }];
            }
            return;
        }
        invocation(nil,nil);
    }
}
    
- (void)getGroupInfoDeal:(YiChatGroupInfoModel *)model{
    WS(weakSelf);
    self.chatGroupInfoModel = model;
    
    self.groupPower = model.roleType;
    
    [ProjectHelper helper_getMainThread:^{
        [self.chatToolBar changeAddViewWithPower:model.roleType];
    }];
    
    [self getGroupInfoSingleMemberSilenceWithModel:model invocation:^(BOOL isHas) {
        if(isHas){
            weakSelf.singleSilenceState = 1;
        }
        else{
            weakSelf.singleSilenceState = 0;
        }
        weakSelf.groupSilenceState = model.groupSilentStatus;
        
        [weakSelf dealGroupSilenceState];
    }];
}

- (void)getGroupInfoSingleMemberSilenceWithModel:(YiChatGroupInfoModel *)model invocation:(void(^)(BOOL isHas))invocation{
  
    
    [ProjectHelper helper_getGlobalThread:^{
        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
            self.chatGroupInfoModel = model;
            
        }
        BOOL isHas = NO;
        
        YiChatGroupInfoModel *model = self.chatGroupInfoModel;
        
        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
            
            if(model.silentList && [model.silentList isKindOfClass:[NSArray class]]){
                
                for (int i = 0; i < model.silentList.count; i ++) {
                    NSDictionary *dic = model.silentList[i];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        id userId = dic[@"userId"];
                        if(userId){
                            if([userId isKindOfClass:[NSNumber class]]){
                                if([userId integerValue]  == YiChatUserInfo_UserId){
                                    isHas = YES;
                                }
                            }
                            else if([userId isKindOfClass:[NSString class]]){
                                if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
                                    
                                    isHas = YES;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        invocation(isHas);
        
    }];
}
    
- (void)makeChartBarWithFrame:(CGRect)frame bgview:(UIView *)bgView{
    _chatToolBar = [[ZFChatToolBar alloc] initWithFrame:frame];
    _chatToolBar.originBackView = bgView;
    [_chatToolBar makeUI];
    WS(weakSelf);
    
    __weak ZFChatToolBar *tmp = _chatToolBar;
    
    _chatToolBar.zfChatToolBarTextChanged = ^(NSString * _Nonnull text) {
        BOOL isPushMessageAlert = NO;
               
               if(weakSelf.lastDeleteStr && [weakSelf.lastDeleteStr isKindOfClass:[NSString class]]){
                   if([text hasSuffix:@"@"] && weakSelf.lastDeleteStr.length < text.length){
                       isPushMessageAlert = YES;
                   }
                   if(text.length > 0){
                      
                       if(weakSelf.lastDeleteStr.length <= text.length){
                           NSString *last = [text substringWithRange:NSMakeRange(text.length - 1, 1)];
                           if([last isEqualToString:@"@"]){
                               isPushMessageAlert = YES;
                           }
                       }
                      
                   }
                  
               }
               else{
                   if(text && [text isKindOfClass:[NSString class]]){
                       if([text isEqualToString:@"@"]){
                       
                            isPushMessageAlert = YES;
                       }
                   }
               }
               
               if(isPushMessageAlert && weakSelf.getChatType == ZFChatTypeGroup){
                   
                   [ProjectHelper helper_getMainThread:^{
                       ZFChatSelecteMessageAlertVC *selecteMessageAlert = [ZFChatSelecteMessageAlertVC initialVC];
                       selecteMessageAlert.groupId = weakSelf.chatId;
                       selecteMessageAlert.groupPower = weakSelf.groupPower;
                       selecteMessageAlert.zfPersonCardSelecte = ^(YiChatUserModel * _Nonnull model) {
                           
                           if(model && [model isKindOfClass:[YiChatUserModel class]]){
                               if(model.userId == - 1){
                                   NSString *usernick = [model nickName];
                                   NSString *nick = [[@"@" stringByAppendingString:usernick] stringByAppendingString:@" "];
                                   
                                   [weakSelf addMessageAlertInfo:@{@"user":@"all",@"nick":nick}];
                                   [weakSelf changeChatToolBarTextInputContent:[usernick stringByAppendingString:@"  "]];
                               }
                               else{
                                   NSString *usernick = [model nickName];
                                   NSString *nick = [[@"@" stringByAppendingString:usernick] stringByAppendingString:@" "];
                                   
                                   [weakSelf addMessageAlertInfo:@{@"user":[model getUserIdStr],@"nick":nick}];
                                   [weakSelf changeChatToolBarTextInputContent:[usernick stringByAppendingString:@"  "]];
                               }
                              
                           }
                           
                       };
                       [weakSelf.controlVC.navigationController pushViewController:selecteMessageAlert animated:YES];
                   }];
               }
               
               if(weakSelf.lastDeleteStr && [weakSelf.lastDeleteStr isKindOfClass:[NSString class]]){
                   if(weakSelf.lastDeleteStr.length > text.length && [weakSelf.lastDeleteStr rangeOfString:text].location != NSNotFound){
                       
                       if(text && [text isKindOfClass:[NSString class]]){
                           if(weakSelf.messageAlertDicArr){
                               
                               NSDictionary *alertDic = weakSelf.messageAlertDicArr.lastObject;
                               if(alertDic && [alertDic isKindOfClass:[NSDictionary class]]){
                                   
                                   NSString *nick = alertDic[@"nick"];
                                   NSMutableString *nickTmp = [NSMutableString stringWithCapacity:0];
                                   for (int j = 0; j < nick.length; j ++) {
                                       NSString *str = [nick substringWithRange:NSMakeRange(j, 1)];
                                       if(str){
                                           if(![str isEqualToString:@" "]){
                                               [nickTmp appendString:str];
                                           }
                                       }
                                   }
                                   nick = nickTmp;
                                  
                                   if(nick && [nick isKindOfClass:[NSString class]]){
                                       
                                       if([nick rangeOfString:@"@"].location != NSNotFound){
                                           NSRange range = [text rangeOfString:nick];
                                           if(range.location != NSNotFound){
                                               
                                               if(range.location + range.length >= text.length){
                                                  NSArray *tmp = [text componentsSeparatedByString:nick];
                                                  NSString *result = @"";
                                                  
                                                  if(tmp && [tmp isKindOfClass:[NSArray class]]){
                                                      if(tmp.count != 0){
                                                         result = [tmp componentsJoinedByString:@""];
                                                      }
                                                  }
                                                  
                                                  [weakSelf.messageAlertDicArr removeLastObject];
                                                  
                                                   weakSelf.lastDeleteStr = result;
                                                   
                                                  [weakSelf.chatToolBar changeTextInputText:result];
                                                  
                                                  return;
                                               }
                                               
                                              
                                               
                                           }
                                       }
                                       
                                       
                                   }
                                  
                               }
                           }
                       }
                   }
               }
                weakSelf.lastDeleteStr = text;
    };
    
    _chatToolBar.zfChatToolBarAddViewSelecte = ^(NSInteger row,NSString *text) {
        
        if(text && [text isKindOfClass:[NSString class]]){
            if([text isEqualToString:@"拍照"]){
                if(![weakSelf judgeIsCanSendMSG]){
                    return;
                }
                
                //拍照
                [ProjectUIHelper projectPhotoVideoPickerWWithType:4 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                    
                    manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
                        if(editedIcon && [editedIcon isKindOfClass:[UIImage class]]){
                            [ProjectHelper helper_getGlobalThread:^{
                                [weakSelf sendImageMessage:editedIcon withExt:nil isScrollToDone:YES];
                            }];
                        }
                    };
                    
                    manager.yrPickerManagerDidTakeVideos = ^(UIImage * _Nonnull coverImage, NSString * _Nonnull movFilePath) {
                        
                        __block id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@"视频导出中."];
                        
                        [ProjectHelper helper_getGlobalThread:^{
                            
                            if(coverImage && [coverImage isKindOfClass:[UIImage class]] && movFilePath && [movFilePath isKindOfClass:[NSString class]]){
                                
                                
                                NSString *savePath = [ZFChatStorageHelper zfChatStorageHelper_getMP4FileFullPath];
                                
                                [ZFChatStorageHelper zfChatStorageHelper_convertMovToMP4WithMovPath:movFilePath savePath:savePath hanlde:^(BOOL success, NSString * _Nonnull path, NSString * _Nonnull errorStr) {
                                    
                                    [ProjectHelper helper_getMainThread:^{
                                        if([progress respondsToSelector:@selector(hidden)]){
                                            [progress performSelector:@selector(hidden)];
                                        }
                                    }];
                                    
                                    if(success){
                                        AVURLAsset*audioAsset = [AVURLAsset URLAssetWithURL:[NSURL fileURLWithPath:path] options:nil];
                                        CMTime audioDuration = audioAsset.duration;
                                        float audioDurationSeconds = CMTimeGetSeconds(audioDuration);
                                        
                                        [weakSelf sendVideoMessage:[NSURL URLWithString:path] withExt:nil withTime:audioDurationSeconds andThumbnailImage:coverImage];
                                        
                                    }
                                }];
                                
                                
                            }
                        }];
                        
                        
                    };
                    
                    [weakSelf.controlVC presentViewController:nav animated:YES completion:nil];
                    
                }];
            }
            else if([text isEqualToString:@"相册"]){
                if(![weakSelf judgeIsCanSendMSG]){
                    return;
                }
                
                //相册
                [ProjectUIHelper projectPhotoVideoPickerWWithType:3 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                    
                    manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
                        BOOL isScrollToDown = NO;
                        for (int i = 0; i < images.count; i ++) {
                            UIImage *image = images[i];
                            if(i == images.count - 1){
                                isScrollToDown = YES;
                            }
                            else{
                                isScrollToDown = YES;
                            }
                            if(image && [image isKindOfClass:[UIImage class]]){
                                [weakSelf sendImageMessage:image withExt:nil isScrollToDone:isScrollToDown];
                            }
                        }
                    };
                    
                    manager.yrPickerManagerDidPickerVideos = ^(UIImage * _Nonnull coverImage, PHAsset * _Nonnull asset) {
                        
                        if(coverImage && [coverImage isKindOfClass:[UIImage class]] && asset && [asset isKindOfClass:[PHAsset class]]){
                            
                            __block id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@"视频导出中."];;
                            
                            [ProjectHelper helper_getGlobalThread:^{
                                
                                [ZFChatHelper zfChatHelper_exportVideoWithPhasset:asset success:^(NSString * _Nonnull outputPath) {
                                    [ProjectHelper helper_getMainThread:^{
                                        if([progress respondsToSelector:@selector(hidden)]){
                                            [progress performSelector:@selector(hidden)];
                                        }
                                    }];
                                    
                                    NSData *data = [NSData dataWithContentsOfURL:[NSURL fileURLWithPath:outputPath]];
                                    
                                    if(data.length > 10485760 ){
                                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"视频大小超限"];
                                        return ;
                                    }
                                    
                                    
                                    if(outputPath && [outputPath isKindOfClass:[NSString class]]){
                                        
                                        [weakSelf sendVideoMessage:[NSURL fileURLWithPath:outputPath] withExt:nil withTime:asset.duration andThumbnailImage:coverImage];
                                    }
                                    
                                } failure:^(NSString * _Nonnull errorMessage, NSError * _Nonnull error) {
                                    [ProjectHelper helper_getMainThread:^{
                                        if([progress respondsToSelector:@selector(hidden)]){
                                            [progress performSelector:@selector(hidden)];
                                        }
                                    }];
                                }];
                            }];
                        }
                    };
                    
                    [weakSelf.controlVC presentViewController:nav animated:YES completion:nil];
                    
                }];
            }
            else if([text isEqualToString:@"红包"]){
                if(![weakSelf judgeIsCanSendMSG]){
                    return;
                }
                
                //红包点击
                YiChatSendRedPacketVC *sendVC = [YiChatSendRedPacketVC initialVC];
                sendVC.chatId = weakSelf.chatId;
                //群成员数量
                if (weakSelf.getChatType == ZFChatTypeChat) {
                    sendVC.isGroup = NO;
                }else{
                    sendVC.isGroup = YES;
                }
                //红包发送成功回调
                sendVC.sendRedPacketBlock = ^(NSDictionary * _Nonnull redDic, BOOL isGroup) {
                    NSDictionary *dic = redDic[@"data"];
                    [weakSelf sendRedPackgeMessage:[NSString stringWithFormat:@"%@",PROJECT_TEXT_APPNAME] redPackeId:[NSString stringWithFormat:@"%@",dic[@"packetId"]] redPackeDes:[NSString stringWithFormat:@"%@",dic[@"content"]] redPackgeName:[NSString stringWithFormat:@"%@",dic[@"content"]]];
                };
                
                UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:sendVC];
                [weakSelf.controlVC presentViewController:nav animated:YES completion:nil];
            }
            else if([text isEqualToString:@"震"]){
                YiChatGroupZhenView *zhenView = [[YiChatGroupZhenView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 60, 170)];
                zhenView.zhengBlock = ^(NSString * _Nonnull text, BOOL isCancel) {
                    [weakSelf.popView dismiss:YES];
                    if (isCancel) {
                        return ;
                    }
                    [weakSelf sendTextMessage:text withExt:nil];
                    [ZFChatMessageHelper sendZhenGroupWithGroupId:weakSelf.chatId content:text completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                        if (!error) {
                            NSLog(@"震消息发送成功");
                        }
                    }];
                };
                
                weakSelf.popView = [KLCPopup popupWithContentView:zhenView showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
                [weakSelf.popView show];
            }
            else if([text isEqualToString:@"个人名片"]){
                //个人名片
                ZFPersonCardSelecteVC *personcardselecte = [ZFPersonCardSelecteVC initialVC];
                personcardselecte.zfPersonCardSelecte = ^(YiChatUserModel * _Nonnull model) {
                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                        [weakSelf sendSingleCradWithUserId:[model getUserIdStr] userNick:[model nickName] userAvtar:[model avatar]];
                    }
                };
                [weakSelf.controlVC presentViewController:personcardselecte animated:YES completion:nil];;
            }
        }
        [tmp resignToolBar];
        
    };
    _chatToolBar.zfChatToolBarSendVoice = ^(id  _Nonnull messageVoice) {
        if(![weakSelf judgeIsCanSendMSG]){
            return;
        }
        
        if([messageVoice isKindOfClass:[NSString class]] && messageVoice){
            NSString *recorderPath = messageVoice;
            
            NSString *path = [ZFChatStorageHelper zfChatStorageHelper_translateChatRecorderFilePathToRecorderAmrPath:recorderPath];
            
            if(path && [path isKindOfClass:[NSString class]]){
                
                [ProjectHelper helper_getGlobalThread:^{
                    
                    NSData *data = [NSData dataWithContentsOfFile:recorderPath];
                    
                    AVAudioPlayer* player = [[AVAudioPlayer alloc] initWithData:data error:nil];
                    double duration = player.duration;
                    
                   BOOL isSuccess = [ZFChatStorageHelper zfChatStorageHelper_convertWavToAmr:recorderPath amrSavePath:path];
                    
                    NSString *uplodaPath = path;
                    
                    if(isSuccess && duration >= 1){
                        [weakSelf sendAudioMessage:uplodaPath andAudioTime:duration withExt:nil];
                    }
                    else if(duration < 1){
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"语音时间不能少于1s"];
                    }
                    else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"语音转码出错"];
                    }
                    
                }];
            }
            NSLog(@"%@",path);
        }
    };
    
    _chatToolBar.zfChatToolBarSendMessage = ^(id  _Nonnull messageText) {
        weakSelf.lastDeleteStr = nil;
        if(![weakSelf judgeIsCanSendMSG]){
            return;
        }
        if(messageText && [messageText isKindOfClass:[NSString class]]){
            NSString *sendMsg = messageText;
            if(sendMsg.length != 0){
                
                [weakSelf sendTextMessage:messageText withExt:nil];
            }
        }
    };
    [bgView addSubview:_chatToolBar];
    
    [_chatToolBar addObserver:weakSelf forKeyPath:@"frame" options:NSKeyValueObservingOptionNew context:nil];
}

- (BOOL)judgeIsCanSendMSG{
    if([self dealGroupSilenceState]){
         [ProjectUIHelper ProjectUIHelper_getAlertNoShadowWithMsm:@"群组已禁言"];
         return NO;
    }
    return YES;
}

- (void)resignChatTool{
    [_chatToolBar resignToolBar];
}

- (void)changeChatToolBarTextInputContent:(NSString *)content{
    [self.chatToolBar apendTextToInputText:content];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context{
    /*
        {
            kind = 1;
            new = "NSRect: {{0, 461}, {414, 49}}";
        }
    */
    if([change isKindOfClass:[NSDictionary class]] && change){
        CGRect rect = [change[@"new"] CGRectValue];
        
        CGFloat h = PROJECT_SIZE_HEIGHT - (self.controlVC.cTable.frame.origin.y) - (PROJECT_SIZE_HEIGHT - rect.origin.y);
        
        if(self.controlVC.cTable.frame.size.height != h){
            [UIView performWithoutAnimation:^{
                self.controlVC.cTable.frame = CGRectMake(self.controlVC.cTable.frame.origin.x, self.controlVC.cTable.frame.origin.y, self.controlVC.cTable.frame.size.width,h);
                
                [self controlVCTableScrollToDown];
            }];
         
        }
    }
}

- (void)addNotify{
    _connectNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleXMPPConnectionState target:self sel:@selector(xmppConnectionChange:)];
    [_connectNotify addNotify];
    
    _notify_groupDeleteNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleGroupDelete target:self sel:@selector(groupDelete:)];
    [_notify_groupDeleteNotify addNotify];
    
    _msgNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleReceiveCommonMsg target:self sel:@selector(didReceiveMsg:)];
    [_msgNotify addNotify];
    
    _cmdMsgNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleReceiveCMDMsg target:self sel:@selector(didReceiveCMDMsg:)];
    [_cmdMsgNotify addNotify];
    
    _sendMsgTimeNotify = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleReceiveMSGTime target:self sel:@selector(didReceiveSendTimeMsg:)];
    [_sendMsgTimeNotify addNotify];
    
    _notify_app_becomeBackground = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeBackground target:self sel:@selector(appDidEndBackgraound:)];
    [_notify_app_becomeBackground addNotify];
    
    _notify_app_becomeActive = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeActive target:self sel:@selector(appDidBeginActive:)];
    [_notify_app_becomeActive addNotify];
    
    _notify_friendNotify = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleFriendNotify target:self sel:@selector(friendNotifyAction:)];
    [_notify_friendNotify addNotify];
    
}

- (void)clean{
    [self removeNotifys];
    
    [self removeSendReachBility];
    
    [_chatToolBar removeObserver:self forKeyPath:@"frame"];
}

- (void)removeNotifys{
    
    [_notify_groupDeleteNotify removeMotify];
    _notify_groupDeleteNotify = nil;
    
    [_connectNotify removeMotify];
    _connectNotify = nil;
    
    [_msgNotify removeMotify];
    _msgNotify = nil;
    
    [_cmdMsgNotify removeMotify];
    _cmdMsgNotify = nil;
    
    [_sendMsgTimeNotify removeMotify];
    _sendMsgTimeNotify = nil;
    
    [_notify_app_becomeBackground removeMotify];
    _notify_app_becomeBackground = nil;
    
    [_notify_app_becomeActive removeMotify];
    _notify_app_becomeActive = nil;
    
    [_notify_friendNotify removeMotify];
    _notify_friendNotify = nil;
}


#pragma mark notify action

- (void)xmppConnectionChange:(NSNotification *)notify{
    id obj = [notify object];
    
    if([obj isKindOfClass:[NSNumber class]] && obj){
        
        NSInteger state = [obj integerValue];
        [ProjectHelper helper_getMainThread:^{
            if(state == 0){
                if([self.controlVC respondsToSelector:@selector(makeUIForConnect)]){
                    [self.controlVC performSelector:@selector(makeUIForConnect)];
                }
                
            }
            else if(state == 1){
                if([self.controlVC respondsToSelector:@selector(makeUIForConnecting)]){
                    [self.controlVC performSelector:@selector(makeUIForConnecting)];
                }
            }
            else if(state == 2){
                if([self.controlVC respondsToSelector:@selector(makeUIForDisconnect)]){
                    [self.controlVC performSelector:@selector(makeUIForDisconnect)];
                }
            }
            else if(state == 3){
                if(self.getChatType == ZFChatTypeGroup){
                    [self updateGroupChatMessgae];
                }
              
            }
            else{
                if([self.controlVC respondsToSelector:@selector(makeUIForConnect)]){
                    [self.controlVC performSelector:@selector(makeUIForConnect)];
                }
              
            }
        }];
    }
}
    
- (void)friendNotifyAction:(NSNotification *)notify{
    id obj = [notify object];
    if(obj && [obj isKindOfClass:[NSDictionary class]]){
        NSString *action = obj[@"type"];
        NSDictionary *msg = obj[@"msg"];
        ZFMessageType type = [ZFChatHelper zfChatHeler_getMessageTypeWithAction:[action integerValue]];
        
        if(type == ZFMessageTypeFriendApplyAgree || type == ZFMessageTypeFriendDeleteMe){
            
            if([msg isKindOfClass:[NSDictionary class]] && msg){
                NSString *userId = [NSString stringWithFormat:@"%ld",[msg[@"userId"] integerValue]];
                if(userId && [userId isKindOfClass:[NSString class]]){
                    
                    if(type == ZFMessageTypeFriendDeleteMe){
                        
                        [ProjectHelper helper_getMainThread:^{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"用户已移除与您的好友关系"];
                            [self.controlVC.navigationController popViewControllerAnimated:YES];
                        }];
                        
                        [[YiChatUserManager defaultManagaer] deleteConnectionFriends:@[userId] invocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull des) {
                            
                        }];
                        
                       
                        
                    }
                    
                    /*
                     {
                     action = 1001;
                     data =     {
                     avatar = "http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/F3921EE7-7E45-43F4-9582-221FC1282AD7.png";
                     nick = huahua;
                     reason = "";
                     userId = 14012457;
                     };
                     }
                     
                     */
                    
                   
                }
            }
        }
        
    }
}
    
- (void)groupDelete:(NSNotification *)notify{
    NSDictionary *dic = [notify object];
    
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        NSString *groupId = dic[@"groupId"];
        NSNumber *isSender = dic[@"isSender"];
        
        if(groupId && [groupId isKindOfClass:[NSString class]] && isSender && [isSender isKindOfClass:[NSNumber class]]){
            if(!isSender.boolValue){
                if([self.chatId isEqualToString:groupId]){
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组已解散"];
                    [ProjectHelper helper_getMainThread:^{
                        if(self.controlVC.navigationController.viewControllers.count > 2){
                            [self.controlVC.navigationController popToRootViewControllerAnimated:YES];
                        }
                        else{
                            [self.controlVC.navigationController popViewControllerAnimated:YES];
                        }
                    }];
                    
                }
            }
        }
    }
}

-(void)loadNotice{
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel setGroupNoticeListWithGroupId:self.chatId];
//    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper groupNoticeListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatGroupNoticeModel *model1 = [YiChatGroupNoticeModel mj_objectWithKeyValues:dataDic];
                if (model1.code == 0) {
                    if (model1.data.count == 0) {
                        return ;
                    }
                    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                        
                        YiChatGroupNoticeInfoModel *m = model1.data.lastObject;
                        NSString *key = [NSString stringWithFormat:@"%ld%@",model.userId,weakSelf.chatId];
                        NSString *msg = [NSString stringWithFormat:@"%@%@",m.noticeId,weakSelf.chatId];
                        
                        if ([[NSUserDefaults standardUserDefaults] objectForKey:key]) {
                            NSString *str = [[NSUserDefaults standardUserDefaults] stringForKey:key];
                            if (![str isEqualToString:msg]) {
                                [ProjectHelper helper_getMainThread:^{
                                    UIAlertController *aletr = [UIAlertController alertControllerWithTitle:m.title message:m.content preferredStyle:UIAlertControllerStyleAlert];
                                    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleCancel handler:nil];
                                    [aletr addAction:cancel];
                                    [weakSelf.controlVC presentViewController:aletr animated:YES completion:nil];
                                }];
                                [[NSUserDefaults standardUserDefaults] setObject:msg forKey:key];
                                [[NSUserDefaults standardUserDefaults] synchronize];
                            }
                        }else{
                            
                            [ProjectHelper helper_getMainThread:^{
                                UIAlertController *aletr = [UIAlertController alertControllerWithTitle:m.title message:m.content preferredStyle:UIAlertControllerStyleAlert];
                                UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleCancel handler:nil];
                                [aletr addAction:cancel];
                                [weakSelf.controlVC presentViewController:aletr animated:YES completion:nil];
                            }];
                           
                            [[NSUserDefaults standardUserDefaults] setObject:msg forKey:key];
                            [[NSUserDefaults standardUserDefaults] synchronize];
                        }
                        
                    }];
                    
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model1.msg];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
    
}
    
- (void)loadMessage{
    if(_isLoadingMessageData == NO){
        _isLoadingMessageData = YES;
    }
    else{
        return;
    }
    
    ZFChatType chatType = [self getChatType];
    [ProjectHelper helper_getGlobalThread:^{
        if(chatType == ZFChatTypeChat){
            [self loadSingleChatMessageData];
        }
        else if(chatType == ZFChatTypeGroup){
            [self loadGroupChatMessageData];
        }
    }];
}

- (void)updateGroupChatMessgae{
    
    NSInteger unix = [[NSDate date] timeIntervalSince1970];
    
    if(unix - _updateChatListTime > 5){
        _updateChatListTime = unix;
        self.isFirstLoad = YES;
        _nearestChatMessageTimeUnix = -1;
        if(self.getChatType == ZFChatTypeGroup){
            [self loadHttpGroupMSG];
        }
        else{
            [self loadHttpChatMSG];
        }
    }
    YiChatUserManager *user = [YiChatUserManager defaultManagaer];
    [user yichatUserClient_recordAllChatObjctUpdateChatListWithState:YiChatUpdateChatlistStateUpdated];
}

- (void)refreshMessage{
    _nearestChatMessageTimeUnix = -1;
    [self.chatMessageDataArr removeAllObjects];
  
    [self loadMessage];
    
    
    WS(weakSelf);
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [weakSelf endRefresh];
    });
    
}

- (void)uploadMessage:(HTMessage *)message{
    [ProjectHelper helper_getGlobalThread:^{
        [ZFChatRequestHelper zfRequest_uploadMessage:message chatType:self.type];
    }];
}

- (void)loadSingleChatMessageData{
    WS(weakSelf);
    
    [ZFChatRequestHelper zfRequestHelper_loadSingleChatMessageRecorderWithChatId:self.chatId lastestMessageTimeUnix:_nearestChatMessageTimeUnix numsForPage:20 completion:^(NSArray * _Nonnull messageArr, NSString * _Nonnull error) {
        [weakSelf.controlVC.cTable.mj_header endRefreshing];
        if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
            BOOL isScrollToDown = NO;
            if(weakSelf.isFirstLoad){
                isScrollToDown = YES;
            }
            [weakSelf dealRequestDataWithMessageListArr:messageArr isNeedScrollToDown:isScrollToDown isAdd:NO];
            weakSelf.isFirstLoad = NO;
            weakSelf.isLoadingMessageData = NO;
        }
        else{
            weakSelf.isLoadingMessageData = NO;
        }
    }];
}

- (void)loadGroupChatMessageData{
    
    WS(weakSelf);
    
    YiChatUserManager *user = [YiChatUserManager defaultManagaer];
    
    [user yichatUserClient_getChatObjctUpdateChatListWithChatId:self.chatId invocation:^(YiChatUpdateChatlistState state) {
        if(state == YiChatUpdateChatlistStateNeedUpdate){
            [weakSelf updateGroupChatMessgae];
        }
        else{
            if([ZFChatHelper getGroupChatListState:self.chatId]){
                   [self loadGroupLocalMsg];
               }
               else{
                   [ZFChatHelper fetchGroupInfoWithGroupId:self.chatId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
                       if(model.lastList && [model.lastList isKindOfClass:[NSArray class]]){
                           if(model.lastList.count >= 0 ){
                               [weakSelf loadGroupLocalMsg];
                           }
                           else{
                               [weakSelf loadHttpGroupMSG];
                           }
                       }
                       else{
                           [weakSelf loadGroupLocalMsg];
                       }
                   }];
               }
        }
    }];
}

- (void)loadGroupLocalMsg{
    WS(weakSelf);
    [ZFChatRequestHelper zfRequestHelper_loadSingleChatMessageRecorderWithChatId:self.chatId lastestMessageTimeUnix:_nearestChatMessageTimeUnix numsForPage:20 completion:^(NSArray * _Nonnull messageArr, NSString * _Nonnull error) {
        [weakSelf.controlVC.cTable.mj_header endRefreshing];
        if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
            if(messageArr.count > 0){
                BOOL isScrollToDown = NO;
                if(weakSelf.isFirstLoad){
                    isScrollToDown = YES;
                }
                
                [weakSelf dealRequestDataWithMessageListArr:messageArr isNeedScrollToDown:isScrollToDown isAdd:NO];
                weakSelf.isLoadingMessageData = NO;
                weakSelf.isFirstLoad = NO;
            }
            else{
                [weakSelf loadHttpGroupMSG];
            }
        }
        else{
            [weakSelf loadHttpGroupMSG];
        }
    }];
}

- (void)loadHttpChatMSG{
    [self loadChatList];
}

- (void)loadHttpGroupMSG{
    [self loadChatList];
}

- (void)loadChatList{
     WS(weakSelf);
    [ProjectHelper helper_getGlobalThread:^{
           
           dispatch_semaphore_wait(_dealRereshChatListlock, DISPATCH_TIME_FOREVER);
        
        NSInteger chattype = 0;
        if(self.getChatType == ZFChatTypeChat){
            chattype = 1;
        }
        else{
            chattype = 2;
        }
        
        [ZFChatRequestHelper zfRequestHelper_loadChatMessageRecorderWithChatId:self.chatId chatType:chattype userId:[ZFChatHelper zfChatHelper_getCurrentUser] lastestMessageTimeUnix:_nearestChatMessageTimeUnix success:^(NSArray * _Nonnull messageArr) {
                     
                     [weakSelf.controlVC.cTable.mj_header endRefreshing];
                     
                     [ProjectHelper helper_getGlobalThread:^{
                         if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
                             NSMutableArray *messages = [NSMutableArray arrayWithCapacity:0];
                             
                             
                             for (int i = 0; i < messageArr.count; i ++) {
                                 NSDictionary *dic = messageArr[i];
                                 
                                 if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                     HTMessage *msg = [[ZFChatManage defaultManager] translateRequestHttpDataToHTMessage:dic];
                                     if(weakSelf.getChatType == ZFChatTypeChat){
                                         msg.chatType = @"1";
                                     }
                                     else{
                                         msg.chatType = @"2";
                                     }
                                     if(msg && [msg isKindOfClass:[HTMessage class]]){
                                         [messages addObject:msg];
                                         
                                     }
                                 }
                             }
                             BOOL isScrollToDown = NO;
                             if(weakSelf.isFirstLoad){
                                 isScrollToDown = YES;
                             }
                             
                             for (int i = 0; i < messages.count; i ++) {
                                 HTMessage *tmp = messages[i];
                                 if(tmp && [tmp isKindOfClass:[HTMessage class]]){
                                     NSString *msgId = tmp.msgId;
                                     if([msgId isKindOfClass:[NSString class]] && msgId){
                                         
                                         [ZFChatHelper zfChatHelper_getLocalMsgWithMsgid:msgId invocation:^(HTMessage * _Nonnull msg) {
                                             /*
                                             
                                                 HTConversation * converModel = [HTConversation new];
                                                   converModel.lastMessage = message;
                                                   [self.messageManager insertOneNormalMessage:message];
                                                   [self.messageManager didReceiveMessages:@[message]];
                                                   [self.conversationManager updataOneConversationWithChatterConversation:converModel isReadAllMessage:NO];
                                              
                                             */
                                             
                                             if(!msg){
                                                 //存储拉取的聊天记录
                                                 tmp.timestamp += i;
                                                 [ZFChatHelper zfChatHeler_insertMessage:tmp];
                                                 [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:tmp chatId:nil isReadAllMessage:YES];
                                             }
                                         }];
                                     }
                                    
                                 }
                             }
                            
                             
                             
                             [weakSelf dealRequestDataWithMessageListArr:messages isNeedScrollToDown:isScrollToDown isAdd:NO];
                             weakSelf.isLoadingMessageData = NO;
                             weakSelf.isFirstLoad = NO;
                             
                             dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                                 if(weakSelf){
                                     dispatch_semaphore_signal(weakSelf.dealRereshChatListlock);
                                 }
                             });
                         }
                         else{
                             if(weakSelf){
                                 dispatch_semaphore_signal(weakSelf.dealRereshChatListlock);
                             }
                         }
                     }];
                     
                 } fail:^(NSString * _Nonnull error) {
                     dispatch_semaphore_signal(weakSelf.dealRereshChatListlock);
                     weakSelf.isLoadingMessageData = NO;
                     [weakSelf.controlVC.cTable.mj_header endRefreshing];
                 }];
             }];
}



- (void)dealRequestDataWithMessageListArr:(NSArray *)messageArr isNeedScrollToDown:(BOOL)isNeedScrollToDown isAdd:(BOOL)isAdd{
    if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
       
        if(messageArr.count > 0){
            NSArray *dealedArr = messageArr;
            
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < dealedArr.count; i ++) {
                HTMessage *msg = dealedArr[i];
                
                if(msg && [msg isKindOfClass:[HTMessage class]]){
                    NSString *msgId = msg.msgId;
                    
                    if(msgId && [msgId isKindOfClass:[NSString class]]){
                        NSInteger isHas = -1;
                        
                        isHas = [self searchDataUserMessageId:msgId];
                        
                        if(isHas != -1){
                            
                            ZFMessageType messageType = ZFMessageTypeUnknown;
                            ZFChatManage *manage = [ZFChatManage defaultManager];
                            if(msg.ext && [msg.ext isKindOfClass:[NSDictionary class]]){
                                if([msg.ext.allKeys containsObject:@"action"]){
                                    NSUInteger extAction = [msg.ext[@"action"] integerValue];
                                    messageType = [manage getMessageTypeWithAction:extAction];
                                }
                            }
                            
                            if(messageType == ZFMessageTypeWithdrawn){
                                ZFChatConfigure *chat = [[ZFChatConfigure alloc] initWithHTMsg:msg];
                                if(chat.chatType == ZFChatTypeGroup && chat.messageType == ZFMessageTypeWithdrawn ){
                                    chat.groupRole = _groupPower;
                                    [chat updateMSGConfire];
                                }
                                if(!isAdd){
                                    chat.voiceIsPlayed = YES;
                                }
                                
                                [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:msg];
                                [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:msg chatId:self.chatId isReadAllMessage:YES];
                                
                                if(self.chatMessageDataArr.count - 1 >= isHas){
                                    [self.chatMessageDataArr replaceObjectAtIndex:isHas withObject:chat];
                                }
                            }
                           
                            continue;
                        }
                        else{
                            ZFChatConfigure *chat = [[ZFChatConfigure alloc] initWithHTMsg:msg];
                            if(chat.messageType == ZFMessageTypeWithdrawn && chat.chatType == ZFChatTypeGroup){
                                if(msg.chatType && [msg.chatType isKindOfClass:[NSString class]]){
                                    if([self getChatType] == ZFChatTypeChat){
                                        if(![msg.chatType isEqualToString:@"1"]){
                                            continue;
                                        }
                                    }
                                    if([self getChatType] == ZFChatTypeGroup){
                                        if(![msg.chatType isEqualToString:@"2"]){
                                            continue;
                                        }
                                    }
                                }
                                chat.groupRole = _groupPower;
                                [chat updateMSGConfire];
                            }
                        
                            
                            if(!isAdd){
                                chat.voiceIsPlayed = YES;
                            }
                            
                            if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
                                if(YiChatProject_IsNeedAppearMemberRemovedAlert){
                                    [tmp addObject:chat];
                                }
                                else{
                                    if(chat.messageAction != 2004){
                                        [tmp addObject:chat];
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if(self.isFirstLoad && tmp.count > 0){
                [self.chatMessageDataArr removeAllObjects];
            }
            
            if(!isAdd){
                [self insertDataToChatMessageData:tmp];
                [self dealMSGTime];
            }
            else{
                [self dealAddMSGTime:tmp];
                [self addDataToChatMessageData:tmp];
            }
            
           
            if(self.chatMessageDataArr.count != 0){
                self.nearestChatMessageTimeUnix = [self.chatMessageDataArr.firstObject getMessageTime];
            }
           
            [ProjectHelper helper_getMainThread:^{
                [self reloadData:isNeedScrollToDown];
            }];
            
        }
    }
}

- (void)dealCMDDataWithMessageListArr:(NSArray *)messageArr isNeedScrollToDown:(BOOL)isNeedScrollToDown{
    if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
        
        if(messageArr.count > 0){
            NSArray *dealedArr = messageArr;
            
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < dealedArr.count; i ++) {
                HTCmdMessage *msg = dealedArr[i];
                
                if(msg && [msg isKindOfClass:[HTCmdMessage class]]){
                    ZFChatConfigure *model = [[ZFChatConfigure alloc] initWithHTCMDMsg:msg];
                    
                    NSDictionary *body = [model getCMDMessageExtData];
                    
                    if(model.messageType == ZFMessageTypeWithdrawn){
                        NSDictionary *body = [model getCMDMessageBody];
                        if(body && [body isKindOfClass:[NSDictionary class]]){
                            NSString *msgid = body[@"msgId"];
                            NSString *opid = body[@"opId"];
                            NSString *opNick = body[@"opNick"];
                            
                            if(msgid && [msgid isKindOfClass:[NSString class]] && opid && [opid isKindOfClass:[NSString class]]){
                                NSInteger isHas = [self searchDataUserMessageId:msgid];
                                
                                ZFChatConfigure *chat = [self getChatDataWithIndex:isHas];
                                
                                if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
                                    HTMessage *chatMsg = chat.msg;
                                    if(chatMsg && [chatMsg isKindOfClass:[HTMessage class]]){
                                        HTMessage *new =  [ZFChatMessageHelper translateCommonMessageToWithDrawnMessageForReceive:chatMsg opid:opid opNick:opNick userInfo:[chat getMessageBodyExt]];
                                        
                                        if(new && [new isKindOfClass:[HTMessage class]]){
                                            chat.msg = new;

                                              [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:new];
                                        }
                                    }
                                }
                            }
                        }
                    }else if (model.messageType == ZFMessageTypeGroupNotice){
                        NSString *groupID = [NSString stringWithFormat:@"%@",body[@"groupId"]];
                        if ([self.chatId isEqualToString:groupID]) {
                            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                NSString *key = [NSString stringWithFormat:@"%ld%@",model.userId,groupID];
                                NSString *msg = [NSString stringWithFormat:@"%@%@",body[@"id"],groupID];
                                [[NSUserDefaults standardUserDefaults] setObject:msg forKey:key];
                                [[NSUserDefaults standardUserDefaults] synchronize];
                            }];
                            
                            NSString *content = [NSString stringWithFormat:@"%@",body[@"content"]];
                            NSString *title = [NSString stringWithFormat:@"%@",body[@"title"]];
                            UIAlertController *aletr = [UIAlertController alertControllerWithTitle:@"群公告" message:content preferredStyle:UIAlertControllerStyleAlert];
                            UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleCancel handler:nil];
                            [aletr addAction:cancel];
                            [self.controlVC presentViewController:aletr animated:YES completion:nil];
                        }
                    }
                    else if(model.messageType == ZFMessageTypeGroupMemberSilence || model.messageType == ZFMessageTypeCancelGroupMemberSilence){
                        [tmp addObject:model];
                    }
                    else if(model.messageType == ZFMessageTypeGroupCancelSilence || model.messageType == ZFMessageTypeGroupSilence ||model.messageType ==  ZFMessageTypeGroupCancelSetManager || model.messageType == ZFMessageTypeGroupSetManager){
                        
                        if(body && [body isKindOfClass:[NSDictionary class]]){
                            NSString *groupId = body[@"groupId"];
                            if(groupId && [groupId isKindOfClass:[NSString class]]){
                                if([groupId isEqualToString:self.chatId]){
                                    
                                    [tmp addObject:model];
                                    
                                    if(model.messageType == ZFMessageTypeGroupSilence){
                                        
                                        self.groupSilenceState = 1;
                                        [self dealGroupSilenceState];
                                        
                                        [ProjectHelper helper_getGlobalThread:^{
                                            if(self.chatGroupInfoModel && [self.chatGroupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                                                self.chatGroupInfoModel.groupSilentStatus = 1;
                                                
                                                [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.chatGroupInfoModel invocation:^(BOOL isSuccess) {
                                                }];
                                            }
                                        }];
                                        
                                    }
                                    else if(model.messageType == ZFMessageTypeGroupCancelSilence){
                                        
                                        WS(weakSelf);
                                        [ProjectHelper helper_getGlobalThread:^{
                                            if(self.chatGroupInfoModel && [self.chatGroupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                                                
                                                self.chatGroupInfoModel.groupSilentStatus = 0;
                                                
                                                [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.chatGroupInfoModel invocation:^(BOOL isSuccess) {
                                                }];
                                            }
                                        }];
                                        
                                        weakSelf.groupSilenceState = 0;
                                        
                                        [self dealGroupSilenceState];
                                        
                                    }
                                    else if(model.messageType == ZFMessageTypeGroupSetManager){
                                        NSString *userId = body[@"userId"];
                                        if(userId && [userId isKindOfClass:[NSString class]]){
                                            if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
                                                self.groupPower = 1;
                                                
                                                [ProjectHelper helper_getGlobalThread:^{
                                                    if(self.chatGroupInfoModel && [self.chatGroupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                                                        self.chatGroupInfoModel.roleType = 1;
                                                        
                                                        [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.chatGroupInfoModel invocation:^(BOOL isSuccess) {
                                                        }];
                                                    }
                                                }];
                                            }
                                        }
                                    }
                                    else if(model.messageType == ZFMessageTypeGroupCancelSetManager){
                                        NSString *userId = body[@"userId"];
                                        if(userId && [userId isKindOfClass:[NSString class]]){
                                            if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
                                                self.groupPower = 0;
                                                
                                                [ProjectHelper helper_getGlobalThread:^{
                                                    if(self.chatGroupInfoModel && [self.chatGroupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
                                                        self.chatGroupInfoModel.roleType = 0;
                                                        
                                                        [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:self.chatGroupInfoModel invocation:^(BOOL isSuccess) {
                                                        }];
                                                    }
                                                }];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            [self dealAddMSGTime:tmp];
            [self addDataToChatMessageData:tmp];
            
            if(self.chatMessageDataArr.count != 0){
                self.nearestChatMessageTimeUnix = [self.chatMessageDataArr.firstObject getMessageTime];
            }
            
            [ProjectHelper helper_getMainThread:^{
                [self reloadData:isNeedScrollToDown];
            }];
        }
    }
}

- (BOOL)dealGroupSilenceState{
    
    BOOL isNeedSilence = NO;
    
    
    if(self.getChatType == ZFChatTypeGroup){
        if(self.singleSilenceState == 0 && self.groupSilenceState == 0){
            [self.chatToolBar cancelForbiddenInputText];
            isNeedSilence = NO;
        }
        else{
            if(_groupPower >= 1){
                [self.chatToolBar cancelForbiddenInputText];
                isNeedSilence =NO;
            }
            else{
                [self.chatToolBar forbiddenInputText];
                isNeedSilence = YES;
            }
        }
    }
    
    return isNeedSilence;
}

- (void)setGroupPower:(NSInteger)groupPower{
    _groupPower = groupPower;
    
    [self dealGroupSilenceState];
}

- (NSInteger)searchDataUserMessageId:(NSString *)messageId{
    if(messageId && [messageId isKindOfClass:[NSString class]]){
        __block NSInteger isHas = -1;
        dispatch_apply(self.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t num) {
            NSString *tmpMsgId = [[self getChatDataWithIndex:num] getMsgId];
            if(tmpMsgId && [tmpMsgId isKindOfClass:[NSString class]]){
                if([tmpMsgId isEqualToString:messageId]){
                    isHas = num;
                }
            }
        });
        return isHas;
        
    }
    return -1;
}

- (void)dealMSGTime{
    
    dispatch_apply(self.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
        ZFChatConfigure *msg = [self getChatDataWithIndex:i];
        if(i != 0){
            ZFChatConfigure *last = [self getChatDataWithIndex:i - 1];
            msg.lastMessageTime = [last getMessageTime];
        }
        else{
            msg.lastMessageTime = - 1;
        }
    });
}

- (void)dealAddMSGTime:(NSArray *)tmp{
    if(tmp && [tmp isKindOfClass:[NSArray class]]){
        NSInteger time = - 1;
        if(self.chatMessageDataArr){
            if(self.chatMessageDataArr.count != 0){
                time = [[self getChatDataWithIndex:self.chatMessageDataArr.count - 1] getMessageTime];
            }
        }
        
        dispatch_apply(tmp.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
            if(i <= tmp.count - 1){
                ZFChatConfigure *msg = tmp[i];
                if(msg && [msg isKindOfClass:[ZFChatConfigure class]]){
                    if(i != 0){
                        ZFChatConfigure *last = tmp[i - 1];
                        msg.lastMessageTime = [last getMessageTime];
                    }
                    else{
                        msg.lastMessageTime = time;
                    }
                }
            }
        });
    }
}

- (void)updateNextMSGTime{
    
}


- (ZFChatConfigure *)getChatDataWithIndex:(NSInteger)index{
    if(self.chatMessageDataArr && [self.chatMessageDataArr isKindOfClass:[NSArray class]]){
        if(self.chatMessageDataArr.count - 1 >= index && self.chatMessageDataArr.count > 0){
            return self.chatMessageDataArr[index];
        }
    }
    return nil;
}

- (void)reloadData:(BOOL)isScrollToDown{
    [self reloadData:isScrollToDown completion:^{
        
    }];
}

- (void)reloadData:(BOOL)isScrollToDown completion:(void(^)(void))completion{
    
    [ProjectHelper helper_getGlobalThread:^{
        
        dispatch_semaphore_wait(self.dealReloadDataLock, DISPATCH_TIME_FOREVER);
        
        [ProjectHelper helper_getMainThread:^{
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
                [arr addObject:[NSNumber numberWithInteger:1]];
            }
            
            self.controlVC.sectionsRowsNumSet = arr;;
            
            [self.controlVC.cTable reloadData];
            
            if(isScrollToDown){
                [self controlVCTableScrollToDown];
            }
            
            dispatch_semaphore_signal(self.dealReloadDataLock);
            completion();
        }];
        
    }];
}

- (void)reloadDataWithoutThread:(BOOL)isScrollToDown completion:(void(^)(void))completion{
    [ProjectHelper helper_getMainThread:^{
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
            [arr addObject:[NSNumber numberWithInteger:1]];
        }
        
        self.controlVC.sectionsRowsNumSet = arr;;
        
        [self.controlVC.cTable reloadData];
        
        if(isScrollToDown){
            [self controlVCTableScrollToDown];
        }
        
        completion();
    }];
}

- (void)controlVCTableScrollToDown{
    NSInteger sections =  self.controlVC.cTable.numberOfSections;
    
    if(sections > 0){
        NSIndexPath *index = [NSIndexPath indexPathForRow:0 inSection:sections - 1];
        
        [self.controlVC.cTable scrollToRowAtIndexPath:index atScrollPosition:UITableViewScrollPositionBottom animated:NO];
    }
}

- (void)addDataToChatMessageData:(NSArray *)chatDataArr{
    if(chatDataArr && [chatDataArr isKindOfClass:[NSArray class]]){
        if(chatDataArr.count >0){
            [self.chatMessageDataArr addObjectsFromArray:chatDataArr];
        }
    }
}

- (void)insertDataToChatMessageData:(NSArray *)chatDataArr{
    if(chatDataArr && [chatDataArr isKindOfClass:[NSArray class]]){
        if(chatDataArr.count >= 0){
            for (NSInteger i = chatDataArr.count - 1; i >= 0; i --) {
                if(i <= chatDataArr.count - 1){
                    id obj = chatDataArr[i];
                    if(obj && [obj isKindOfClass:[ZFChatConfigure class]]){
                        if(self.chatMessageDataArr.count > 0){
                           
                            if(obj){
                                [self.chatMessageDataArr insertObject:obj atIndex:0];
                            }
                        }
                        else{
                            if(obj){
                                [self.chatMessageDataArr addObject:obj];
                            }
                        }
                    }
                }
            }
        }
    }
}

- (void)removeChatMessageDataWithMsgId:(NSString *)msgId isNeedLoadData:(BOOL)isLoadData isNeedScrollToDone:(BOOL)isNeedScrollToDone{
    
    [ProjectHelper helper_getGlobalThread:^{
        dispatch_apply(self.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
            if((self.chatMessageDataArr.count - 1) >= i){
                ZFChatConfigure *data = self.chatMessageDataArr[i];
                NSString *dataMSGId = [data getMsgId];
                if(dataMSGId && [dataMSGId isKindOfClass:[NSString class]]){
                    if([dataMSGId isEqualToString:msgId]){
                        if((self.chatMessageDataArr.count - 1) >= i){
                            [self.chatMessageDataArr removeObjectAtIndex:i];
                        }
                    }
                }
            }
        });
        
        if(isLoadData){
            
            [self reloadData:isNeedScrollToDone];
        }
    }];
}

- (NSArray *)sortMessageDataArrWithTime:(NSArray *)messageDataArr{
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    [tmp addObjectsFromArray:messageDataArr];
    
    //排序
    NSArray *sortedArr = [tmp sortedArrayUsingComparator:^NSComparisonResult(HTMessage*  _Nonnull obj1, HTMessage*  _Nonnull obj2) {
        
        NSInteger timeBegin = obj1.timestamp;
        NSInteger timeEnd = obj2.timestamp;
        
        
        if(timeBegin > timeEnd){
            return NSOrderedDescending;
        }
        else{
            return NSOrderedAscending;
        }
    }];
    
    return sortedArr;
}

- (void)deleteRepeatDataForDataArr:(NSArray *)dataArr{
    
}

- (BOOL)judgeReceiveMsg:(id)msg{
    if([msg isKindOfClass:[HTMessage class]]){
        HTMessage *message = msg;
        if(message.to && [message.to isKindOfClass:[NSString class]] && self.chatId && [self.chatId isKindOfClass:[NSString class]] && message.chatType && [message.chatType isKindOfClass:[NSString class]]){
            
            if([message.chatType isEqualToString:@"1"]){
                NSString *user = [ZFChatHelper zfChatHelper_getCurrentUser];
                
                if(user && [user isKindOfClass:[NSString class]]){
                    if([message.to isEqualToString:user] && [message.from isEqualToString:self.chatId]){
                        return YES;
                    }
                }
            }
            else if([message.chatType isEqualToString:@"2"]){
                if([message.to isEqualToString:self.chatId]){
                    return YES;
                }
            }
        }
    }
    else if([msg isKindOfClass:[HTCmdMessage class]]){
        HTCmdMessage *message = msg;
        
        if(message.to && [message.to isKindOfClass:[NSString class]] && self.chatId && [self.chatId isKindOfClass:[NSString class]] && message.chatType && [message.chatType isKindOfClass:[NSString class]]){
            
            if([message.chatType isEqualToString:@"1"]){
                NSString *user = [ZFChatHelper zfChatHelper_getCurrentUser];
                
                if(user && [user isKindOfClass:[NSString class]]  && [message.from isEqualToString:self.chatId]){
                    if([message.to isEqualToString:user]){
                        return YES;
                    }
                }
            }
            else if([message.chatType isEqualToString:@"2"]){
                if([message.to isEqualToString:self.chatId]){
                    return YES;
                }
            }
        }
    }
    return NO;
}

- (void)didReceiveMsg:(NSNotification *)obj{
    
    NSArray *objArr = [obj object];
    if(objArr && [objArr isKindOfClass:[NSArray class]]){
        NSArray *messageArr = [obj object];
        
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
        
        for (int i = 0; i < messageArr.count; i ++) {
            NSDictionary *dic = messageArr[i];
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                HTMessage *msg = [[ZFChatManage defaultManager] getContentMessageDicMSGEntity:dic];
                if([self judgeReceiveMsg:msg]){
                    [tmp addObject:msg];
                }
            }
        }
        
        if(tmp && [tmp isKindOfClass:[NSArray class]]){
            [ProjectHelper helper_getMainThread:^{
             
                BOOL isNeedScrollDone = NO;
                NSLog(@"%f %f %f",self.controlVC.cTable.contentSize.height,self.controlVC.cTable.frame.size.height,self.controlVC.cTable.contentOffset.y);
                
                if(self.controlVC.cTable){
                    if(self.controlVC.cTable.contentSize.height > self.controlVC.cTable.frame.size.height){
                        CGFloat h = self.controlVC.cTable.contentSize.height - self.controlVC.cTable.frame.size.height;
                        if(self.controlVC.cTable.contentOffset.y >= (h - (self.controlVC.cTable.frame.size.height))){
                            isNeedScrollDone = YES;
                        }
                    }
                }
                [ProjectHelper helper_getGlobalThread:^{
                   [self dealRequestDataWithMessageListArr:tmp isNeedScrollToDown:isNeedScrollDone isAdd:YES];
                }];
            }];
        }
    }
}

- (void)didReceiveCMDMsg:(NSNotification *)obj{
    [ProjectHelper helper_getGlobalThread:^{
        NSArray *objArr = [obj object];
        if(objArr && [objArr isKindOfClass:[NSArray class]]){
            NSArray *messageArr = [obj object];
            
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            
            for (int i = 0; i < messageArr.count; i ++) {
                HTCmdMessage *msg = messageArr[i];
                
                NSInteger action = [ZFChatHelper getCMDMessageAction:msg];
                
                if(action == 30004 || action == 30005){
                    
                    //单个禁言
                    NSDictionary *body = [ZFChatHelper getCMDMessageBody:msg];
                    
                    NSString *groupid =  body[@"data"];;
                    
                    if(action == 30004){
                        
                        if(self.getChatType == ZFChatTypeGroup){
                            
                            if(groupid && [groupid isKindOfClass:[NSString class]]){
                                YiChatUserManager *manager = [YiChatUserManager defaultManagaer];
                                
                                YiChatUserModel *model = manager.userModel;
                                
                                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                    [[YiChatUserManager defaultManagaer] addLocalGroupMemberShutUpWithGroupId:groupid userId:[model getOriginDic] groupInfo:self.chatGroupInfoModel];
                                    
                                }
                                
                                if([self.chatId isEqualToString:groupid]){
                                   
                                    self.singleSilenceState = 1;
                                    
                                    [self dealGroupSilenceState];
                                    
                                    if(self.groupPower == 0){
                                        [self dealCMDDataWithMessageListArr:@[msg] isNeedScrollToDown:YES];
                                    }
                                }
                            }
                        }
                    }
                    else{
                        if(self.getChatType == ZFChatTypeGroup){
                            
                            if(groupid && [groupid isKindOfClass:[NSString class]]){
                                if([self.chatId isEqualToString:groupid]){
                                    
                                    [[YiChatUserManager defaultManagaer] removeLocalGroupMemberShutUpWithGroupId:groupid userId:YiChatUserInfo_UserIdStr groupInfo:self.chatGroupInfoModel];
                                    
                                    self.singleSilenceState = 0;
                                    
                                     [self dealGroupSilenceState];
                                    
                                    if(self.groupPower == 0){
                                        [self dealCMDDataWithMessageListArr:@[msg] isNeedScrollToDown:YES];
                                    }
                                }
                            }
                            
                           
                        }
                    }
                }
                else{
                    if([self judgeReceiveMsg:msg]){
                        [tmp addObject:msg];
                    }
                }
            }
            
            if(tmp && [tmp isKindOfClass:[NSArray class]]){
                [self dealCMDDataWithMessageListArr:tmp isNeedScrollToDown:NO];
            }
        }
    }];
}

- (void)didReceiveSendTimeMsg:(NSNotification *)obj{
    [ProjectHelper helper_getGlobalThread:^{
        NSArray *messageArr = [obj object];
        
        if(messageArr && [messageArr isKindOfClass:[NSArray class]]){
            for (HTMessage *message in messageArr) {
                
                if(message && [message isKindOfClass:[HTMessage class]]){
                    
                    for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
                        ZFChatConfigure *obj = self.chatMessageDataArr[i];
                        
                        if(obj && [obj isKindOfClass:[ZFChatConfigure class]]){
                            NSString *msgId = [obj getMsgId];
                            if(msgId && [msgId isKindOfClass:[NSString class]]){
                                if([msgId isEqualToString:message.msgId]){
                                    
                                    if(message.msgType == 2002){
                                        dispatch_semaphore_signal(self.sendImagesLock);
                                    }
                                    
                                    [obj setMsgTime:message.timestamp];
                                
//                                    [self uploadMessage:obj.msg];
                                    
                                    [self refreshDataSource:obj];
                                    
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

- (void)appDidEndBackgraound:(NSNotification *)notfy{
    
}

- (void)appDidBeginActive:(NSNotification *)notfy{
    if(self.getChatType == ZFChatTypeGroup){
        [self updateGroupChatMessgae];
    }
}

- (void)refreshDataSource:(ZFChatConfigure *)configure
{
    if(configure && [configure isKindOfClass:[ZFChatConfigure class]])
    if ([self.chatMessageDataArr count] > 0) {
        
        __block BOOL ishas = NO;
        dispatch_apply(self.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
            if((self.chatMessageDataArr.count - 1) >= i){
                NSString *msgid = [self.chatMessageDataArr[i] getMsgId];
                NSString *configureMsgId = [configure getMsgId];
                if(msgid && [msgid isKindOfClass:[NSString class]] && configureMsgId && [configureMsgId isKindOfClass:[NSString class]]){
                    if([msgid isEqualToString:configureMsgId]){
                        ishas = YES;
                        if(configure && (self.chatMessageDataArr.count - 1 >= i)){
                            [self.chatMessageDataArr replaceObjectAtIndex:i withObject:configure];
                        }
                    }
                }
            }
        });
        
        if(ishas == YES){
            [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:configure.msg];
            [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:configure.msg chatId:self.chatId isReadAllMessage:YES];
            
            //  self.currentIndexTimestmap = aMessage.timestamp;
            self.nearestChatMessageTimeUnix = [configure getMessageTime];
            
            [self reloadDataWithoutThread:NO completion:^{
                
            }];
        }
        return;
    }
}

- (void)addMessageAlertInfo:(NSDictionary *)info{
    if(info && [info isKindOfClass:[NSDictionary class]]){
        NSString *userIdInfo = info[@"user"];
        
       
        if(userIdInfo && [userIdInfo isKindOfClass:[NSString class]]){
            
            BOOL ishas = NO;
            
            for (int i = 0; i < self.messageAlertDicArr.count; i ++) {
                NSDictionary *dic = self.messageAlertDicArr[i];
                
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    NSString *userId = dic[@"user"];
                    if(userId && [userId isKindOfClass:[NSString class]]){
                        if([userId isEqualToString:userIdInfo]){
                            ishas = YES;
                        }
                    }
                }
            }
            
            if(ishas == NO){
                [self.messageAlertDicArr addObject:info];
            }
        }
    }
}

- (void)repeatSendMessahe:(HTMessage *)message isScrollToDone:(BOOL)isScrollToDown{
    [ProjectHelper helper_getGlobalThread:^{
       
        if(message && [message isKindOfClass:[HTMessage class]]){
            for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
                if(self.chatMessageDataArr.count - 1 >= i){
                    ZFChatConfigure *configure = self.chatMessageDataArr[i];
                    if(configure && [configure isKindOfClass:[ZFChatConfigure class]]){
                        NSString *msgId = [configure getMsgId];
                        if([msgId isEqualToString:message.msgId]){
                            
                            [self.chatMessageDataArr removeObjectAtIndex:i];
                            
                            message.timestamp = [[NSDate date] timeIntervalSince1970] * 1000;
                            
                            [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:message];
                             [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:configure.msg chatId:self.chatId isReadAllMessage:YES];
                        }
                    }
                }
            }
            [self reloadData:NO];
            [self sendMessage:message isScrollToDone:YES];
        }
        
    }];
}

- (void)withDrawMessageWithIndexPath:(NSIndexPath *)index{
    [ProjectHelper helper_getGlobalThread:^{
        if(index && [index isKindOfClass:[NSIndexPath class]]){
            ZFChatConfigure *model = [self getChatDataWithIndex:index.section];
            if(model && [model isKindOfClass:[ZFChatConfigure class]]){
                
                [ZFChatHelper getCurrentUserDicInvocation:^(NSDictionary *dic) {
                    NSDictionary * userInfoDic = dic;
                    
                    if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
                        
                        HTMessage *msg = [ZFChatMessageHelper translateCommonMessageToWithDrawnMessage:model.msg opid:YiChatUserInfo_UserIdStr  opNick:YiChatUserInfo_Nick userInfo:userInfoDic];
                        
                        if(msg && [msg isKindOfClass:[HTMessage class]]){
                            model.msg = msg;
                            
                            [self reloadData:NO];
                            
                            [ProjectHelper helper_getGlobalThread:^{
                                if([self getChatType] == ZFChatTypeChat){
                                    
                                    [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:msg];
                                    [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:msg chatId:self.chatId isReadAllMessage:YES];
                                    
                                    [self requestSendWithDrawnCMDMessageWithMsg:model index:index];
                                }
                                else if([self getChatType] == ZFChatTypeGroup){
                                    [self requestSendWithDrawnMessageWithMsg:model  index:index];
                                }
                            }];
                        }
                        else{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
                        }
                        
                    }
                }];
            }
        }
    }];
}

- (void)copyMessageWithIndexPath:(NSIndexPath *)index{
    if(index && [index isKindOfClass:[NSIndexPath class]]){
        ZFChatConfigure *model = [self getChatDataWithIndex:index.section];
        if(model && [model isKindOfClass:[ZFChatConfigure class]]){
            if(model.messageType == ZFMessageTypeText){
                UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
                pasteboard.string = [model getTextMessageContent];
            }
        }
    }
}

- (void)deleteMessageWithIndexPath:(NSIndexPath *)index{
    if(index && [index isKindOfClass:[NSIndexPath class]]){
        
        ZFChatConfigure *model = [self getChatDataWithIndex:index.section];
        
        HTMessage *msg = model.msg;
        
        [self.chatMessageDataArr removeObject:model];
        [self reloadData:NO];
        
        if(msg && [msg isKindOfClass:[HTMessage class]]){
            [ZFChatHelper zfChatHeler_deleteLocalMessageWithMessage:msg];
        }
    }
}

- (void)requestSendWithDrawnMessageWithMsg:(ZFChatConfigure *)model index:(NSIndexPath *)index{
    HTMessage *msg = model.msg;
    
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        NSString *content = [msg modelToJSONString];
        
        if(content && [content isKindOfClass:[NSString class]]){
            
            //    string = [string stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            NSDictionary *dic = [ProjectTranslateHelper helper_dictionaryWithJsonString:content];
            
            dic = [ZFChatHelper zfchatFeltSendUploadDic:dic];
            
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                
                NSDictionary *requestDic = @{@"type":@2000,@"data":dic};
                NSString *requestString = [ProjectTranslateHelper helper_convertJsonObjToJsonData:requestDic];
                NSString *base64MessageString = [requestString specialURLDecodedString];
                
                NSDictionary *param = [ProjectRequestParameterModel updateMessageWithMessageId:[model getMsgId] content:base64MessageString];
                
                [ZFChatHelper zfCahtHelper_updateLocalMessageWithMsg:msg];
                [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:msg chatId:self.chatId isReadAllMessage:YES];
                
                if(model && [model isKindOfClass:[ZFChatConfigure class]]){
                    [self requestSendWithDrawnCMDMessageWithMsg:model  index:index];
                }
                
                [ProjectRequestHelper updateMessageWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:nil successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
                    [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                        if(obj && [obj isKindOfClass:[NSDictionary class]]){
                            
                            return ;
                            
                        }
                        else if(obj && [obj isKindOfClass:[NSString class]]){
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                            return;
                        }
                         [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
                    }];
                    
                } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
                }];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
            }
        }
        else{
             [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
        }
    }
    else{
         [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
    }
   
}

- (void)requestSendWithDrawnCMDMessageWithMsg:(ZFChatConfigure *)model index:(NSIndexPath *)index{
    HTMessage *msg = model.msg;
    
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        NSString *userId = self.chatId;
        NSInteger power = [self getPower];
        
        [ZFChatMessageHelper sendWithDrawnMessageWithUserId:userId groupRole:power message:msg completion:^(HTMessage * _Nonnull message, NSError * _Nonnull error) {
            
        }];
        
    }
    else{
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息撤回出错"];
    }
}


- (void)transPortMessageWithIndexPath:(NSIndexPath *)index{
    
}

- (NSInteger)getSendDateLocal{
    return [[NSDate date] timeIntervalSince1970];
}

- (void)sendMessage:(HTMessage *)message isScrollToDone:(BOOL)isScrollToDown{
    
    WS(weakSelf);
    [ProjectHelper helper_getGlobalThread:^{
        if(message && [message isKindOfClass:[HTMessage class]]){
            
            dispatch_semaphore_wait(self.dealMessageSendLock, DISPATCH_TIME_FOREVER);
            
            [ZFChatHelper getCurrentUserDicInvocation:^(NSDictionary *dic) {
                NSDictionary * userInfoDic = dic;
                
                if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
                    
                    [ProjectHelper helper_getGlobalThread:^{
                        
                        [ZFChatHelper addMoreEXTToMessage:message withExt:userInfoDic];
                        
                        
                        ZFChatConfigure *configure = [[ZFChatConfigure alloc] initWithHTMsg:message];
                        
                        if(configure.messageType == ZFMessageTypeText){
                            
                            NSString *content = [configure getTextMessageContent];
                            
                            if(content && [content isKindOfClass:[NSString class]]){
                                
                                if(self.messageAlertDicArr.count != 0){
                                    NSMutableString *userIds = [NSMutableString stringWithCapacity:0];
                                    for (int i = 0; i < self.messageAlertDicArr.count; i ++) {
                                        NSDictionary *alertDic = self.messageAlertDicArr[i];
                                        if(alertDic && [alertDic isKindOfClass:[NSDictionary class]]){
                                            NSString *userId = alertDic[@"user"];
                                            NSString *nick = alertDic[@"nick"];
                                            
                                            if(nick && [nick isKindOfClass:[NSString class]] && userId && [userId isKindOfClass:[NSString class]]){
                                                if([content rangeOfString:nick].location != NSNotFound){
                                                    if(userIds.length != 0){
                                                        [userIds appendString:[NSString stringWithFormat:@"%@%@",@",",userId]];
                                                    }
                                                    else{
                                                        [userIds appendString:userId];
                                                    }
                                                }
                                            }
                                            
                                        }
                                    }
                                    if(userIds.length != 0){
                                        [ZFChatHelper addMoreEXTToMessage:message withExt:@{@"atUser":userIds}];
                                    }
                                }
                                [self.messageAlertDicArr removeAllObjects];
                            }
                            
                        }
                        
                        [self dealAddMSGTime:@[configure]];
                        [self addDataToChatMessageData:@[configure]];
                        
                        
                        [ZFChatHelper zfChatHeler_insertMessage:configure.msg];
                        [ZFChatHelper zfCahtHelper_updateLocalConcersationWithMsg:configure.msg chatId:self.chatId isReadAllMessage:YES];
                        
                        [ZFChatHelper zfChatHelper_sendMessage:message completion:^(HTMessage * _Nonnull message, NSError * _Nonnull error) {
                            
                            
                            if(message.msgType == 2002){
                                dispatch_semaphore_signal(weakSelf.sendImagesLock);
                            }
                            if(error){
                                if(message && [message isKindOfClass:[HTMessage class]]){
                                    NSString *msgid = message.msgId;
                                    if(msgid && [msgid isKindOfClass:[NSString class]]){
                                        dispatch_apply(self.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
                                            if((self.chatMessageDataArr.count - 1) >= i){
                                                
                                                ZFChatConfigure *config = self.chatMessageDataArr[i];
                                                if(config && [config isKindOfClass:[ZFChatConfigure class]]){
                                                    NSString *configMSGid = [config getMsgId];
                                                    if(configMSGid && [configMSGid isKindOfClass:[NSString class]]){
                                                        if([configMSGid isEqualToString:msgid]){
                                                            [config changeMSGSendStatus:SendStateFail];
                                                            [ProjectHelper helper_getMainThread:^{
                                                                [self reloadData:NO];
                                                            }];
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                             
                            }
                            
                        }];
                        
                         
                        [ProjectHelper helper_getMainThread:^{
                            
                            [self reloadData:isScrollToDown];
                            dispatch_semaphore_signal(self.dealMessageSendLock);
                        }];
                        
                    }];
                    
                }
            }];
        }
    }];
}

- (void)sendTextMessage:(NSString *)text withExt:(NSDictionary *)ext{
    
    HTMessage *message = [ZFChatMessageHelper sendTextMessage:text to:self.chatId messageType:self.type messageExt:ext];
    [self sendMessage:message isScrollToDone:YES];
}
    
- (void)sendSingleCradWithUserId:(NSString *)userid  userNick:(NSString *)userNick userAvtar:(NSString *)avtar{
    
    if(userid && [userid isKindOfClass:[NSString class]] && userNick && [userNick isKindOfClass:[NSString class]] && avtar && [avtar isKindOfClass:[NSString class]]){
        HTMessage *message = [ZFChatMessageHelper sendTextMessage:@"个人名片" to:self.chatId messageType:@"1" messageExt:@{@"action":@"10007",@"cardUserId":userid,@"cardUserNick":userNick,@"cardUserAvatar":avtar}];
        [self sendMessage:message isScrollToDone:YES];
    }
}

- (void)sendRedPackgeMessage:(NSString *)title redPackeId:(NSString *)redPackgeId   redPackeDes:(NSString *)redPackgeDes redPackgeName:(NSString *)redPackgeName{
    if(!(title && [title isKindOfClass:[NSString class]])){
        title = @"";
    }
    if(!(redPackgeId && [redPackgeId isKindOfClass:[NSString class]])){
        redPackgeId = @"";
    }
    if(!(redPackgeDes && [redPackgeDes isKindOfClass:[NSString class]])){
        redPackgeDes = @"";
    }
    if(!(redPackgeName && [redPackgeName isKindOfClass:[NSString class]])){
        redPackgeName = @"";
    }
    
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
             [self sendTextMessage:@"红包消息" withExt:@{@"action":@"10001",@"envId":redPackgeId,@"envName":[NSString stringWithFormat:@"%@红包",PROJECT_TEXT_APPNAME],@"envMsg":redPackgeDes,@"nick":[model nickName]}];
        }
    }];
}

- (void)getRedPackge:(NSString *)redPackgeSenderNick sendUserId:(NSString *)senderUserId redPackgeGetNick:(NSString *)redPackgeGetNick{
    if(self.chatId && [self.chatId isKindOfClass:[NSString class]]){
        if ([self getChatType] == ZFChatTypeGroup) {
            if(redPackgeGetNick && [redPackgeGetNick isKindOfClass:[NSString class]] && redPackgeSenderNick && [redPackgeSenderNick isKindOfClass:[NSString class]] && senderUserId && [senderUserId isKindOfClass:[NSString class]]){
                
                NSString *showContent = [NSString stringWithFormat:@"%@领取了%@的红包",redPackgeGetNick,redPackgeSenderNick];
                
                HTMessage *message = [ZFChatMessageHelper sendTextMessage:showContent to:self.chatId messageType:@"2" messageExt:nil];
                NSString *userId = senderUserId;
                NSString *messagId = message.msgId;
                if(!(userId && [userId isKindOfClass:[NSString class]])){
                    userId = @"";
                }
                if(!(messagId && [messagId isKindOfClass:[NSString class]])){
                    messagId = @"";
                }
                message.ext = @{@"action":@"10004",@"msgFrom":userId,@"msgId":messagId,@"msgFromNick":redPackgeSenderNick};
                [self sendMessage:message isScrollToDone:NO];
            }
            
        }else{
            NSString *showContent = [NSString stringWithFormat:@"红包已被领取"];
            HTMessage *message = [ZFChatMessageHelper sendTextMessage:showContent to:self.chatId messageType:@"1" messageExt:@{@"action":@"10004"}];
            [self sendMessage:message isScrollToDone:NO];
        }
    }
}

- (void)sendImageMessage:(UIImage *)image withExt:(NSDictionary *)ext isScrollToDone:(BOOL)isScrollToDown{
  
    [ProjectHelper helper_getGlobalThread:^{
        dispatch_semaphore_wait(self.sendImagesLock, DISPATCH_TIME_FOREVER);
        
        HTMessage *message = [ZFChatMessageHelper sendImageMessageWithImage:image to:self.chatId messageType:self.type messageExt:ext];
        [self sendMessage:message isScrollToDone:isScrollToDown];
    }];
}

- (void)sendAudioMessage:(NSString *)filePath andAudioTime:(CGFloat)audioTime withExt:(NSDictionary *)ext {
    
    HTMessage *message = [ZFChatMessageHelper sendAudioMessageWithLocalPath:filePath duration:audioTime to:self.chatId messageType:self.type messageExt:ext];
    [self sendMessage:message isScrollToDone:YES];
}

- (void)sendVideoMessage:(NSURL *)filePath withExt:(NSDictionary *)ext withTime:(CGFloat)time andThumbnailImage:(UIImage *)image {
    HTMessage * message = [ZFChatMessageHelper sendVideoMessageWithURL:filePath to:self.chatId messageType:self.type andSSImage:image andVideoTime:time messageExt:ext];
    [self sendMessage:message isScrollToDone:YES];
}

-(void)sendLocationMessage:(double)latitude longitude:(double)longitude andAddress:(NSString *)address andSSImage:(UIImage *)mapImage withExt:(NSDictionary *)ext {
    HTMessage * message = [ZFChatMessageHelper sendLocationMessageWithLatitude:latitude longitude:longitude address:address to:self.chatId messageType:self.type andSSImage:mapImage messageExt:ext];
    [self sendMessage:message isScrollToDone:YES];
}
-(void)sendFileMessage:(NSString *)filePath withFileName:(NSString *)fileName withExt:(NSDictionary *)ext {
    HTMessage * message = [ZFChatMessageHelper sendFileMessageWithFilePath:filePath to:self.chatId messageType:self.type fileName:fileName messageExt:ext];
    [self sendMessage:message isScrollToDone:YES];
}

- (ZFChatType)getChatType{
    if(_type && [_type isKindOfClass:[NSString class]]){
        if([_type isEqualToString:@"1"]){
            return ZFChatTypeChat;
        }
        else if([_type isEqualToString:@"2"]){
            return ZFChatTypeGroup;
        }
    }
    return ZFChatTypeUnknown;
}
    
- (NSDictionary *)getAllOfPhotoMessageUrls:(NSString *)messageId{
    
    NSInteger index = 0;
    if(messageId && [messageId isKindOfClass:[NSString class]]){
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
            if(self.chatMessageDataArr.count - 1 >= i){
                ZFChatConfigure *configure = self.chatMessageDataArr[i];
                
                if(configure.messageType == ZFMessageTypePhoto){
                    NSString *url = [configure getPhotoOriginUrl];
                    
                    if(!(url && [url isKindOfClass:[NSString class]])){
                        url = @"";
                    }
                    NSString *tmpMessageId = [configure getMsgId];
                    if(tmpMessageId && [tmpMessageId isKindOfClass:[NSString class]]){
                        if([tmpMessageId isEqualToString:messageId]){
                            index = arr.count;
                        }
                    }
                    [arr addObject:url];
                }
            }
        }
        
        return @{@"urls":arr,@"index":[NSNumber numberWithInteger:index]};
    }
    
    return nil;
}

- (NSDictionary *)getAllOfVideoMessageUrls:(NSString *)messageId{
    
    NSInteger index = 0;
    if(messageId && [messageId isKindOfClass:[NSString class]]){
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        for (int i = 0; i < self.chatMessageDataArr.count; i ++) {
            if(self.chatMessageDataArr.count - 1 >= i){
                ZFChatConfigure *configure = self.chatMessageDataArr[i];
                
                if(configure.messageType == ZFMessageTypeVideo){
                    NSString *url = [configure getVideoPlayUrl];
                    
                    if(!(url && [url isKindOfClass:[NSString class]])){
                        url = @"";
                    }
                    NSString *tmpMessageId = [configure getMsgId];
                    if(tmpMessageId && [tmpMessageId isKindOfClass:[NSString class]]){
                        if([tmpMessageId isEqualToString:messageId]){
                            index = arr.count;
                        }
                    }
                    [arr addObject:url];
                }
            }
        }
        
        return @{@"urls":arr,@"index":[NSNumber numberWithInteger:index]};
    }
    
    return nil;
}
@end
