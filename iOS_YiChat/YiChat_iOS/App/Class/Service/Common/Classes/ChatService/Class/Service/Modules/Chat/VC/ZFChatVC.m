//
//  ZFChatVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatVC.h"
#import "ZFChatPresenter.h"
#import "ZFChatConfigure.h"
#import "ZFChatUIConfigure.h"
#import "ZFChatCell.h"
#import "ZFChatHelper.h"
#import "ZFChatUIHelper.h"
#import "YiChatGrabRedPacketView.h"
#import "ZFMessageTransportVC.h"
#import <KLCPopup.h>
#import "YiChatRedPacketDetailVC.h"
#import "ProjectUIHelper.h"
#import "YiChatStorageManager.h"

@interface ZFChatVC ()<UIGestureRecognizerDelegate,UIScrollViewDelegate>

@property (nonatomic,strong) ZFChatPresenter *presenter;

@property (nonatomic,strong) UIMenuController *menuVC;

@property (nonatomic, strong) UIMenuItem * deleteMenuItem;

@property (nonatomic, strong) UIMenuItem * cpMenuItem;

@property (nonatomic, strong) UIMenuItem *backMenuItem;
    
@property (nonatomic, strong) UIMenuItem *voicePlayMenuItem;

@property (nonatomic, strong) UIMenuItem *transpondMenuItem;
    
@property (nonatomic, strong) UIMenuItem *shutUpMenuItem;
    
@property (nonatomic, strong) UIMenuItem *searchInfoMenuItem;

@property (nonatomic, strong) NSIndexPath *menuIndex;

@property (nonatomic,strong) KLCPopup *popView;
    
@property (nonatomic,assign) BOOL isFirstLoad;

@property (nonatomic,assign) NSInteger lastReloadUnix;

@end

@implementation ZFChatVC

+ (id)initialVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType{
    
    UIImage *icon = nil;
    
    if([chatType isEqualToString:@"1"]){
        icon = [UIImage imageNamed:@"tabbar_contacts@3x.png"];
    }
    else if([chatType isEqualToString:@"2"]){
        icon = [UIImage imageNamed:@"tabbar_contacts@3x.png"];
    }
    
    ZFChatVC *chat = [ZFChatVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:@"" leftItem:nil rightItem:icon];
    chat.presenter = [ZFChatPresenter initialVCWithChatId:chatId chatType:chatType];
    chat.presenter.controlVC = chat;
    return chat;
}

+ (id)initialVCWithGroupModel:(YiChatGroupInfoModel *)model{
    if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
        NSString *groupId = [model getGroupId];
        if(groupId && [groupId isKindOfClass:[NSString class]]){
            ZFChatVC *chat =  [self initialVCWithChatId:groupId chatType:@"2"];
            chat.presenter.chatGroupInfoModel = model;
            return chat;
        }
    }
    return nil;
}

- (void)dealloc{
    [_presenter clean];
    
    _presenter = nil;
}

- (void)getChatTitle:(void(^)(NSString *title))handle{
    WS(weakSelf);
    if(self.presenter.getChatType == ZFChatTypeChat){
        if(self.presenter.chatUserInfo && [self.presenter.chatUserInfo isKindOfClass:[NSDictionary class]]){
            handle([weakSelf.presenter.chatUserInfo appearName]);
            
            [self.presenter loadChatInfoInvocation:^(id  _Nonnull model, NSString * _Nonnull error) {
                
            } isUpdate:YES];
            return;
        }
        else{
            [self.presenter loadChatInfoInvocation:^(id  _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    handle([weakSelf.presenter.chatUserInfo appearName]);
                    return ;
                }
            } isUpdate:YES];
        }
    }
    else if(self.presenter.getChatType == ZFChatTypeGroup){
        if(self.presenter.chatGroupInfoModel && [self.presenter.chatGroupInfoModel isKindOfClass:[NSDictionary class]]){
            
            NSString *name = [NSString stringWithFormat:@"%@(%ld)",weakSelf.presenter.chatGroupInfoModel.groupName,weakSelf.presenter.chatGroupInfoModel.memberCount];
            handle(name);
            
            [self.presenter loadChatInfoInvocation:^(id  _Nonnull model, NSString * _Nonnull error) {
                
            } isUpdate:YES];
            return;
        }
        else{
            BOOL isUpdate = NO;
            
            if(_isFirstLoad == 0){
                isUpdate = YES;
            }
            
            [self.presenter loadChatInfoInvocation:^(id  _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                    
                     NSString *name = [NSString stringWithFormat:@"%@(%ld)",weakSelf.presenter.chatGroupInfoModel.groupName,weakSelf.presenter.chatGroupInfoModel.memberCount];
                    
                    handle(name);
                    return;
                }
            } isUpdate:isUpdate];
        }
    }
    handle(nil);
    return;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _isFirstLoad = NO;
    
    [_presenter addNotify];
    
    [self makeTable];
    
    if(YiChatProject_IsNeedRefreshChatListBtn){
          if(YiChatProject_IsNeedRefreshGroupChatListBtn){
              if(self.presenter.getChatType == ZFChatTypeGroup){
                  [self makeRefreshChatClick];
              }
          }
          if(YiChatProject_IsNeedRefreshSingleChatListBtn){
              if(self.presenter.getChatType == ZFChatTypeChat){
                  [self makeRefreshChatClick];
              }
          }
      }
    
    if(self.presenter.chatId && [self.presenter.chatId isKindOfClass:[NSString class]]){
        [ProjectHelper helper_getGlobalThread:^{
            [[YiChatStorageManager sharedManager] removeStorageMessageAlertWithKey:self.presenter.chatId];
        }];
    }
    
    WS(weakSelf);
    
    if([self.presenter getChatType] == ZFChatTypeGroup){
        [ZFGroupHelper judgeGroupIsExsit:self.presenter.chatId invocation:^(BOOL isExist) {
            if(!isExist){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群组不存在"];
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.navigationController popViewControllerAnimated:YES];
                }];
            }
        }];
    }
    
    [self.presenter makeChartBarWithFrame:CGRectMake(0, self.view.frame.size.height - PROJECT_SIZE_TABH - PROJECT_SIZE_SafeAreaInset.bottom, self.view.frame.size.width, PROJECT_SIZE_TABH) bgview:self.view];
    
    
    [self.presenter addRefresh];

    [self.presenter beginRefresh];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.presenter endRefresh];
    });
    
    [self.presenter zfchatAddSendReachbility];

    // Do any additional setup after loading the view.
}

- (void)makeRefreshChatClick{
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [btn setTitle:@"刷新聊天记录" forState:UIControlStateNormal];
    btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    btn.layer.cornerRadius = 10.0;
    btn.clipsToBounds = YES;
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.frame = CGRectMake(self.view.frame.size.width - 120, self.cTable.frame.origin.y, 120, 40);
    [btn addTarget:self action:@selector(refreshChatlist:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
}

- (void)refreshChatlist:(UIButton *)btn{
    [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"将主动从服务器拉取最新准确的聊天记录，将刷新本地的最新20条消息！" clickBtns:@[@"确定",@"取消"] invocation:^(NSInteger row) {
             if(row == 0){
                 [self.presenter updateGroupChatMessgae];
             }
         }];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(self.presenter.getChatType == ZFChatTypeChat){
        NSString *userId = [NSString stringWithFormat:@"%ld",[self.presenter.chatId integerValue]];
        
        [self.navigationController pushViewController:[ZFChatUIHelper getUserInfoVCWithUserId:userId] animated:YES];
    }
    else if(self.presenter.getChatType == ZFChatTypeGroup){
        NSString *groupId = [NSString stringWithFormat:@"%ld",[self.presenter.chatId integerValue]];
         [self.navigationController pushViewController:[ZFChatUIHelper getGroupInfoVCWithGroupId:groupId] animated:YES];
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    WS(weakSelf);
    
    [ProjectHelper helper_getGlobalThread:^{

        [self.presenter loadNotice];

        [self getChatTitle:^(NSString *title) {
            [ProjectHelper helper_getMainThread:^{
                [weakSelf changeNavTittle:title];
                
                if(self.presenter.getChatType == ZFChatTypeGroup){
                    
                     [self.presenter updateChatUI];
                   
                }
            }];
        }];
        
        
        _isFirstLoad = YES;
        
    }];
    
    
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    [ProjectHelper helper_getGlobalThread:^{
        dispatch_apply(self.presenter.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
            if((self.presenter.chatMessageDataArr.count - 1) >= i){
                ZFChatConfigure *data = self.presenter.chatMessageDataArr[i];
                
                if(data.isPlayVoice == YES){
                    data.isPlayVoice = NO;
                    [ProjectHelper helper_getMainThread:^{
                        ZFChatCell *cell = [self.cTable cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:i]];
                        if(cell && [cell isKindOfClass:[ZFChatCell class]]){
                            [cell changeVoicePlayUIWithState:NO];
                        }
                    }];
                }
            }
        });
        [self stopPlayVoice];
    }];
    [self.presenter resignChatTool];
}

- (void)makeTable{
    self.sectionsRowsNumSet = @[];
    self.tableStyle = 1;
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH )  - PROJECT_SIZE_SafeAreaInset.bottom - PROJECT_SIZE_TABH);
    self.cTable.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:index.section];
    if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
        return [chat getCellH];
    }
    return 0.00001f;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:section];
    if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
        return [chat getHeaderH];
    }
    return 0.00001f;
}

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section{
    ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:section];
    if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
        return [chat getFooterH];
    }
    return 0.00001f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:section];
    
    if(chat.isShowHeaderTime){
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(back.frame.size.width / 2 - chat.headerTextSize.width / 2, 0, chat.headerTextSize.width,chat.headerTextSize.height) andfont:chat.uiConfigure.headerTextFont textColor:chat.uiConfigure.timeAppearColor textAlignment:NSTextAlignmentCenter];
        [back addSubview:lab];
        lab.text = chat.timeText;
    }

    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ZFChatConfigure *model = [self.presenter getChatDataWithIndex:indexPath.section];
    ZFChatCell *cell = nil;
    UITableViewCellStyle style = UITableViewCellStyleDefault;
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    CGFloat cellW = self.cTable.frame.size.width;
    
    if(model.messageType == ZFMessageTypeText){
        static NSString *reuserIdentifier = @"ZFChat_ZFMessageTypeText";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeText chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    else if(model.messageType == ZFMessageTypePhoto){
        static NSString *reuserIdentifier = @"ZFChat_ZFMessageTypePhoto";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypePhoto chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    else if(model.messageType == ZFMessageTypeVoice){
        static NSString *reuserIdentifier = @"ZFChat_ZFMessageTypeVoice";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeVoice chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        
        __weak ZFChatCell *tmpCell = cell;
        WS(weakSelf);
       
        cell.zfChatCellClickVoiceAction = ^void(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            
            if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
                
                dispatch_apply(weakSelf.presenter.chatMessageDataArr.count, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(size_t i) {
                    if((weakSelf.presenter.chatMessageDataArr.count - 1) >= i){
                        ZFChatConfigure *data = weakSelf.presenter.chatMessageDataArr[i];
                        if([[data getMsgId] isEqualToString:[chatModel getMsgId]]){
                           
                        }
                        else{
                             data.isPlayVoice = NO;
                        }
                    }
                });
                
                if(chatModel.isPlayVoice){
                    
                    [chatModel getRemoteVoiceResourceWavLoadUrlInvocation:^(NSString * _Nonnull url) {
                        
                        if(url && [url isKindOfClass:[NSString class]]){
                            
                            [ZFChatHelper zfChatHelper_playVoiceWithUrl:url progress:^(CGFloat progress, CGFloat duration) {
                                
                            } completion:^(NSString * _Nonnull url) {
                                if(tmpCell){
                                    [tmpCell.chatModel changeVoicePlayState:NO];
                                    [tmpCell changeVoicePlayUIWithState:NO];
                                    [weakSelf stopPlayVoice];
                                }
                                
                            } failure:^(NSString * _Nonnull url, NSString * _Nonnull error) {
                                if(tmpCell){
                                    [tmpCell.chatModel changeVoicePlayState:NO];
                                    [tmpCell changeVoicePlayUIWithState:NO];
                                    [weakSelf stopPlayVoice];
                                }
                            }];
                        }
                        else{
                            if(tmpCell){
                                [tmpCell.chatModel changeVoicePlayState:NO];
                                [tmpCell changeVoicePlayUIWithState:NO];
                                [weakSelf stopPlayVoice];
                            }
                        }
                    }];
                }
                else{
                    [weakSelf stopPlayVoice];
                }
                [weakSelf.presenter reloadData:NO];
            }
            
        };
        cell.chatModel = model;
    }
    
    else if(model.messageType == ZFMessageTypeVideo){
        static NSString *reuserIdentifier = @"ZFChat_ZFMessageTypeVideo";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeVideo chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    else if(model.messageType == ZFMessageTypeLocation){
        static NSString *reuserIdentifier = @"ZFChat_ZFMessageTypeLocation";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeLocation chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    else if(model.messageType == ZFMessageTypeRedPackageReceiveOrSend){
        static NSString *reuserIdentifier = @"ZFChat_RedPackageReceiveOrSend";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeRedPackgeSendOrReceive chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    else if(model.messageType == ZFMessageTypePersonCard){
        static NSString *reuserIdentifier = @"ZFChat_ZFChatCellTypePersonCard";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypePersonCard chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    
    else if(model.messageType == ZFMessageTypeGroupMsgNotify || model.messageType == ZFMessageTypeWithdrawn || model.messageType == ZFMessageTypeGroupSetManager || model.messageType == ZFMessageTypeGroupCancelSetManager  || model.messageType == ZFMessageTypeGroupCancelSilence || model.messageType == ZFMessageTypeGroupSilence || model.messageType == ZFMessageTypeRedPackageGet || model.messageType == ZFMessageTypeGroupMemberSilence || model.messageType == ZFMessageTypeCancelGroupMemberSilence){
        static NSString *reuserIdentifier = @"ZFChatCellTypeCommonCMDMessage";
        cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:ZFChatCellTypeCommonCMDMessage chattype:model.chatType];
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
        
        cell.chatModel = model;
    }
    
    if(cell){
        WS(weakSelf);
        cell.zfChatCellClickSendFailAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"是否重新发送？" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
                if(row == 0 && weakSelf){
                    [weakSelf.presenter repeatSendMessahe:chatModel.msg isScrollToDone:NO];
                    
                }
            }];
        };
        
        cell.zfChatCellClickIconAction = ^(NSString * _Nonnull userId, NSIndexPath * _Nonnull index) {
            if(userId && [userId isKindOfClass:[NSString class]]){
                
                if(weakSelf.presenter.getChatType == ZFChatTypeGroup){
                    if(weakSelf.presenter.getPower > 0)
                    {
                        
                        ZFChatCell *tmpCell = [tableView cellForRowAtIndexPath:index];
                        UIView *back = [tmpCell getIconBack];
                        
                        if(tmpCell && [tmpCell isKindOfClass:[ZFChatCell class]] && back && [back isKindOfClass:[UIView class]] && userId && [userId isKindOfClass:[NSString class]]){
                            
                            [weakSelf showIconClickMenu:back frame:back.bounds andIndexPath:index userId:userId];
                        }
                    }
                }
                else{
                    [weakSelf.navigationController pushViewController:[ZFChatUIHelper getUserInfoVCWithUserId:userId] animated:YES];
                }
            }
        };
        cell.zfChatCellLongpressClickAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            ZFChatCell *tmpCell = [tableView cellForRowAtIndexPath:index];
            UIView *back = [tmpCell getMessageBack];
            UIView *cellBack = [tmpCell getCellBack];
            
            if(tmpCell && [tmpCell isKindOfClass:[ZFChatCell class]] && back && [back isKindOfClass:[UIView class]] && chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]] && cellBack && [cellBack isKindOfClass:[UIView class]]){
                
                
                [weakSelf showMenuViewController:back frame:back.bounds andIndexPath:index model:chatModel];
            }
        };
        cell.zfChatCellUserIconLongPressAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
                if(chatModel.chatType == ZFChatTypeGroup){
                    
                    NSString *from = [chatModel getMsgFrom];
                    
                    if(from && [from isKindOfClass:[NSString class]]){
                        if(![from isEqualToString:YiChatUserInfo_UserIdStr]){
                            [ZFChatHelper fetchUserInfoWithUserId:from invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                    
                                    NSString *nick = [[@"@" stringByAppendingString:[model nickName]] stringByAppendingString:@" "];
                                    
                                    [ProjectHelper helper_getMainThread:^{
                                        [weakSelf.presenter addMessageAlertInfo:@{@"user":[model getUserIdStr],@"nick":nick}];
                                       [weakSelf.presenter changeChatToolBarTextInputContent:nick];
                                    }];
                                }
                            }];
                        }
                    }
                    
                }
            }
        };
        cell.zfChatCellReviewPhotoMessageAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index, UIView * _Nonnull backView) {
            
            if(chatModel.isGEmojiText){
                return ;
            }
            
            if(!(backView && [backView isKindOfClass:[UIView class]])){
                backView = weakSelf.view;
            }
            NSDictionary *urlsDic = [weakSelf.presenter getAllOfPhotoMessageUrls:chatModel.getMsgId];
            
            if(urlsDic && [urlsDic isKindOfClass:[NSDictionary class]]){
                NSArray *urls = urlsDic[@"urls"];
                NSInteger index = [urlsDic[@"index"] integerValue];
                
                if(urls && [urls isKindOfClass:[NSArray class]]){
                    [ProjectUIHelper helper_showImageBrowseWithDataSouce:urls withSourceObjs:@[backView] currentIndex:index];
                }
            }
            
        };
        cell.zfChatCellReviewVideoMessageAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index, UIView * _Nonnull backView) {
            
            if(!(backView && [backView isKindOfClass:[UIView class]])){
                backView = weakSelf.view;
            }
            NSDictionary *urlsDic = [weakSelf.presenter getAllOfVideoMessageUrls:chatModel.getMsgId];
            
            if(urlsDic && [urlsDic isKindOfClass:[NSDictionary class]]){
                NSArray *urls = urlsDic[@"urls"];
                NSInteger index = [urlsDic[@"index"] integerValue];
                
                if(urls && [urls isKindOfClass:[NSArray class]]){
                    [ProjectUIHelper helper_showVideoBrowseWithDataSouce:urls withSourceObjs:@[backView] currentIndex:index];
                }
            }
            
        };
        cell.zfChatCellPersonCardClickAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
                NSString *userId = [chatModel showPersonCardUserId];
                if(userId && [userId isKindOfClass:[NSString class]]){
                     [weakSelf.navigationController pushViewController:[ZFChatUIHelper getUserInfoVCWithUserId:userId] animated:YES];
                }
            }
        };
        
        //抢红包
        cell.zfChatCellRedPackgeClickAction = ^(ZFChatConfigure * _Nonnull chatModel, NSIndexPath * _Nonnull index) {
            [weakSelf.view endEditing:YES];
            BOOL isGroup = NO;
            if (weakSelf.presenter.getChatType == ZFChatTypeChat) {
                isGroup = NO;
            }else{
                isGroup = YES;
            }
            [YiChatRedPacketHelper receiveRedPacketDetailPacketID:chatModel.packageModel.redPackageId redBlock:^(YiChatRedPacketDetailModel * _Nonnull redPacketModel, NSDictionary * _Nonnull redDic) {
                if (redPacketModel.data.status == 0) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if (weakSelf.presenter.getChatType == ZFChatTypeChat) {
                            if ([redPacketModel.data.userId isEqualToString:[[YiChatUserManager defaultManagaer] getUserIdStr]]) {
                                YiChatRedPacketDetailVC *vc = [YiChatRedPacketDetailVC initialVC];
                                vc.isGroup = isGroup;
                                vc.redModel = redPacketModel;
                                UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:vc];
                                [weakSelf presentViewController:nav animated:YES completion:nil];
                                return ;
                            }
                        }
                        
                        CGFloat h = (PROJECT_SIZE_WIDTH - 60)/310 * 410;
                        YiChatGrabRedPacketView *payView = [[YiChatGrabRedPacketView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 60, h)];
                        payView.model = redPacketModel.data;
                        payView.redPacketBlock = ^(YiChatRedPacketDetailModel * _Nonnull model, BOOL isJump) {
                            [weakSelf.popView dismiss:YES];
                            
                            NSString *senderId = [NSString stringWithFormat:@"%ld",[model.data.userId integerValue]];
                            if (isJump) {
                                [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                    
                                    if (model) {
                                        [weakSelf.presenter getRedPackge:chatModel.packageModel.sendPersonNickName sendUserId:senderId redPackgeGetNick:model.nickName];
                                    }
                                }];
                                dispatch_async(dispatch_get_main_queue(), ^{
                                    YiChatRedPacketDetailVC *vc = [YiChatRedPacketDetailVC initialVC];
                                    vc.isGroup = isGroup;
                                    vc.redModel = model;
                                    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:vc];
                                    [weakSelf presentViewController:nav animated:YES completion:nil];
                                });
                            }
                        };
                        weakSelf.popView = [KLCPopup popupWithContentView:payView showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
                        [weakSelf.popView show];
                    });
                }
            
                else{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        YiChatRedPacketDetailVC *vc = [YiChatRedPacketDetailVC initialVC];
                        vc.isGroup = isGroup;
                        vc.redModel = redPacketModel;
                        UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:vc];
                        [weakSelf presentViewController:nav animated:YES completion:nil];
                    });
                }
            }];
        };
        if(self.presenter.getChatType == ZFChatTypeGroup){
            
            if(model.isSender == NO){
                NSString *from = [model getMsgFrom];
                
                [cell changeGroupRoleIcon:0];
                                             
                if(from && [from isKindOfClass:[NSString class]]){
                     __block BOOL isSet = NO;
                     if(![from isEqualToString:YiChatUserInfo_UserIdStr]){
                         NSString *owner =  [NSString stringWithFormat:@"%ld",[self.presenter.chatGroupInfoModel.owner integerValue]];
                         
                         
                         if(owner && [owner isKindOfClass:[NSString class]]){
                                    if([owner isEqualToString:from]){
                                            [cell changeGroupRoleIcon:2];
                                            isSet = YES;
                                    }
                         }
                         
                         if(!isSet){
                             
                            [ProjectHelper helper_getGlobalThread:^{
                                NSArray *managerList = self.presenter.chatGroupInfoModel.adminList;
                                                                 
                                if(managerList && [managerList isKindOfClass:[NSArray class]]){
                                    for (int i = 0; i < managerList.count; i ++) {
                                       
                                        if(managerList.count - 1 >= i){
                                            NSDictionary *dic = managerList[i];
                                            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                                YiChatUserModel *usermodel = [[YiChatUserModel alloc] initWithDic:dic];
                                                if(usermodel && [usermodel isKindOfClass:[YiChatUserModel class]]){
                                                    if(from){
                                                        if([from isEqualToString:usermodel.getUserIdStr]){
                                                            [ProjectHelper helper_getMainThread:^{
                                                                 [cell changeGroupRoleIcon:1];
                                                            }];
                                                           
                                                            isSet = YES;
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
                         
                    }
                }
                                        
            }
           
        }
        return cell;
    }
    
    return [UITableViewCell new];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self removeMenu];
}

- (void)playVoiceWithUrl:(NSString *)url progress:(zfChatVoicePlayProgressInvocation)progress completion:(zfChatVoicePlayCompleteHandel)success failure:(zfChatVoicePlayErrorHandel)failure{
    [ZFChatHelper zfChatHelper_playVoiceWithUrl:url progress:progress completion:success failure:failure];
}
    
- (ZFChatVoicePlayMode)getVoicePlayMode{
    return [ZFChatHelper zfChatHelper_getPlayVoiceMode];
}

- (void)changePlayMode{
    return [ZFChatHelper zfChatHelper_ChangePlayVoiceMode];
}
    
- (void)stopPlayVoice{
    [ZFChatHelper zfChatHelper_stopPlayVoice];
}

- (ZFChatCell *)getCellWithTable:(UITableView *)table style:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuse index:(NSIndexPath *)index cellH:(CGFloat)cellH cellW:(CGFloat)cellW type:(ZFChatCellType)cellType chattype:(ZFChatType)chatType{
    ZFChatCell *cell = [table dequeueReusableCellWithIdentifier:reuse];
    if(!cell){
        cell = [ZFChatCell initialWithStyle:style reuseIdentifier:reuse indexPath:index cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] type:cellType chatType:chatType];
    }
    return cell;
}

- (void)changeNavTittle:(NSString *)title{
    if(title && [title isKindOfClass:[NSString class]]){
        UILabel *lab = [self navBarGetCenterBarItem];
        
        if(lab && [lab isKindOfClass:[UILabel class]]){
            lab.text = title;
        }
    }
}

- (UIMenuController *)menuVC{
    if(!_menuVC){
        _menuVC = [UIMenuController sharedMenuController];
    }
    return _menuVC;
}

- (UIMenuItem *)deleteMenuItem{
    if(!_deleteMenuItem){
        _deleteMenuItem = [[UIMenuItem alloc] initWithTitle:@"删除" action:@selector(deleteMenuAction:)];
    }
    return _deleteMenuItem;
}

- (UIMenuItem *)cpMenuItem{
    if(!_cpMenuItem){
        _cpMenuItem = [[UIMenuItem alloc] initWithTitle:@"复制" action:@selector(copyMenuAction:)];
    }
    return _cpMenuItem;
}

- (UIMenuItem *)backMenuItem{
    if(!_backMenuItem){
        _backMenuItem = [[UIMenuItem alloc] initWithTitle:@"撤回" action:@selector(backMessageMenuAction:)];
        
    }
    return _backMenuItem;
}

- (UIMenuItem *)transpondMenuItem{
    if(!_transpondMenuItem){
        _transpondMenuItem = [[UIMenuItem alloc] initWithTitle:@"转发" action:@selector(transpondMenuAction:)];
        
    }
    return _transpondMenuItem;
}
    
- (UIMenuItem *)shutUpMenuItem{
    if(!_shutUpMenuItem){
        _shutUpMenuItem = [[UIMenuItem alloc] initWithTitle:@"禁言" action:@selector(shutUpMethodAction:)];
        
    }
    return _shutUpMenuItem;
}

- (UIMenuItem *)searchInfoMenuItem{
    if(!_searchInfoMenuItem){
        _searchInfoMenuItem = [[UIMenuItem alloc] initWithTitle:@"查看资料" action:@selector(searchInfoMethodAction:)];
        
    }
    return _searchInfoMenuItem;
}
    
- (UIMenuItem *)voicePlayMenuItem{
    ZFChatVoicePlayMode mode = [self getVoicePlayMode];
    NSString *name = @"";
    if(mode == ZFChatVoicePlayModeBack){
        name = @"听筒播放";
    }
    else{
        name = @"扬声器播放";
    }
    if(!_voicePlayMenuItem){
        _voicePlayMenuItem = [[UIMenuItem alloc] initWithTitle:name action:@selector(voicePlayMenuItemAction:)];
    }
    else{
        [_voicePlayMenuItem setTitle:name];
    }
    return _voicePlayMenuItem;
}
    
- (void)showMenuViewController:(UIView *)showInView
                         frame:(CGRect)frame
                  andIndexPath:(NSIndexPath *)indexPath
                         model:(ZFChatConfigure *)model{
    
    
    if([self.menuVC isMenuVisible]){
        [self.menuVC setMenuVisible:NO animated:NO];
    }
    
    [self.menuVC setMenuItems:nil];
    
    long long localTime = [model getMessageTime];
    long long currentTime = [[NSDate date] timeIntervalSince1970] * 1000 ;

    long long subTime = 0;
    
    if(localTime != 0 && currentTime != 0){
        if(currentTime >= localTime){
            subTime = (currentTime - localTime) / 1000;
        }
    }
    NSInteger expirteTime = 60 * 60 * 24 * 30;
    NSMutableArray *appearItem = [NSMutableArray arrayWithCapacity:0];
    
    if(model.messageType == ZFMessageTypeText){
        if(model.isSender == YES){
            if(!(subTime <= expirteTime)){
                //,self.deleteMenuItem
                if([model chatType] == ZFChatTypeChat){
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                else{
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
            }
            else{
                
                if([model chatType] == ZFChatTypeChat){
                   
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                else{
                    
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                
                //,self.deleteMenuItem
               
            }
        }
        else{
            if([model chatType] == ZFChatTypeGroup){
                if([self.presenter getPower] >= 1){
                    //,self.deleteMenuItem
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                else{
                    //,self.deleteMenuItem
                    
                    [appearItem addObject:self.cpMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
            }
            else{
                //,self.deleteMenuItem
                [appearItem addObject:self.cpMenuItem];
                [appearItem addObject:self.deleteMenuItem];
                [appearItem addObject:self.transpondMenuItem];
            }
            
        }
    }
    else if(model.messageType == ZFMessageTypeVoice){
        if(model.isSender == YES){
            if(!(subTime <= expirteTime)){
                //self.deleteMenuItem,
                if([model chatType] == ZFChatTypeChat){
                    
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.voicePlayMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    
                }
                else{
                    
                    [appearItem addObject:self.voicePlayMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    
                }
            }
            else{
                //self.deleteMenuItem,
                
                if([model chatType] == ZFChatTypeChat){
                    
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    [appearItem addObject:self.voicePlayMenuItem];
                }
                else{
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    [appearItem addObject:self.voicePlayMenuItem];
                    
                }
            }
        }
        else{
            if([model chatType] == ZFChatTypeGroup){
                //self.deleteMenuItem,
                if([self.presenter getPower]  >= 1){
                    
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    [appearItem addObject:self.voicePlayMenuItem];
                }
                else{
                    //self.deleteMenuItem,
                    [appearItem addObject:self.transpondMenuItem];
                    [appearItem addObject:self.voicePlayMenuItem];
                }
            }
            else{
                //self.deleteMenuItem,
                [appearItem addObject:self.deleteMenuItem];
                [appearItem addObject:self.transpondMenuItem];
                [appearItem addObject:self.voicePlayMenuItem];
                
            }
        }
    }
    else if(model.messageType == ZFMessageTypePhoto || model.messageType == ZFMessageTypeVideo){
        if(model.isSender == YES){
            if(!(subTime <= expirteTime)){
                //self.deleteMenuItem,
                if([model chatType] == ZFChatTypeChat){
                    
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    
                }
                else{
                    
                    [appearItem addObject:self.transpondMenuItem];
                }
            }
            else{
                //self.deleteMenuItem,
                
                if([model chatType] == ZFChatTypeChat){
                    
                    [appearItem addObject:self.deleteMenuItem];
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                else{
                    
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                    
                }
            }
        }
        else{
            if([model chatType] == ZFChatTypeGroup){
                //self.deleteMenuItem,
                if([self.presenter getPower]  >= 1){
                    
                    [appearItem addObject:self.backMenuItem];
                    [appearItem addObject:self.transpondMenuItem];
                }
                else{
                    
                    [appearItem addObject:self.transpondMenuItem];
                    
                }
            }
            else{
                //self.deleteMenuItem,
                
                [appearItem addObject:self.deleteMenuItem];
                [appearItem addObject:self.transpondMenuItem];
            }
        }
    }else if(model.messageType == ZFMessageTypeRedPackageReceiveOrSend){
        if (YiChatProject_IsBackRedPackge == 1) {
            if(model.isSender == YES){
                if(!(subTime <= expirteTime)){
                    if([model chatType] == ZFChatTypeChat){
                        [appearItem addObject:self.deleteMenuItem];
                    }
                }else{
                    if([model chatType] == ZFChatTypeChat){
                        [appearItem addObject:self.deleteMenuItem];
                    }
                    else{
                        [appearItem addObject:self.backMenuItem];
                    }
                }
            }
            else{
                if([model chatType] == ZFChatTypeGroup){
                    if([self.presenter getPower] >= 1){
                        [appearItem addObject:self.backMenuItem];
                    }
                    else{
                    }
                }
                else{
                    [appearItem addObject:self.deleteMenuItem];
                }
            }
        }
    }
    
    [self.menuVC setMenuItems:appearItem];
    
    self.menuIndex = indexPath;
    
    [self.menuVC setTargetRect:frame inView:showInView];
    [self.menuVC update];
    [self.menuVC setMenuVisible:YES animated:YES];
}
    
- (void)showIconClickMenu:(UIView *)showInView
                    frame:(CGRect)frame
             andIndexPath:(NSIndexPath *)indexPath userId:(NSString *)userId{
   
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        if([self.menuVC isMenuVisible]){
            [self.menuVC setMenuVisible:NO animated:NO];
        }
        
        [self.menuVC setMenuItems:nil];
        
        self.menuIndex = indexPath;
        
        if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
             [self.menuVC setMenuItems:@[self.searchInfoMenuItem]];
        }
        else{
             [self.menuVC setMenuItems:@[self.shutUpMenuItem,self.searchInfoMenuItem]];
        }
       
        [self.menuVC setTargetRect:frame inView:showInView];
        [self.menuVC update];
        [self.menuVC setMenuVisible:YES animated:YES];
    }
    
    
  
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if(action ==  @selector(deleteMenuAction:) || action == @selector(copyMenuAction:) || action == @selector(backMessageMenuAction:) || action == @selector(transpondMenuAction:) || action == @selector(shutUpMethodAction:) || action == @selector(searchInfoMethodAction:) || action == @selector(voicePlayMenuItemAction:)){
        return YES;
    }
    else{
        return NO;
    }
}

- (void)deleteMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        [self.presenter deleteMessageWithIndexPath:selecte];
    }
}

- (void)copyMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        [self.presenter copyMessageWithIndexPath:selecte];
    }
}

- (void)backMessageMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        [self.presenter withDrawMessageWithIndexPath:selecte];
    }
}
    


- (void)transpondMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:selecte.section];
        if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
            
            if(chat.messageType == ZFMessageTypePhoto || chat.messageType == ZFMessageTypeVoice || chat.messageType == ZFMessageTypeVideo){
                
                NSString *remotePath = [chat getRemoteFilePath];
                if(remotePath && [remotePath isKindOfClass:[NSString class]]){
                    if([remotePath hasPrefix:@"http://"]){
                        ZFMessageTransportVC *transport = [ZFMessageTransportVC initialVC];
                        transport.chat = chat;
                        [self.navigationController pushViewController:transport animated:YES];
                        return;
                    }
                }
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"消息获取失败 暂不支持转发"];
                return;
            
            }
            else{
                ZFMessageTransportVC *transport = [ZFMessageTransportVC initialVC];
                transport.chat = chat;
                [self.navigationController pushViewController:transport animated:YES];
            }
           
        }
    }
}
    
- (void)shutUpMethodAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:selecte.section];
        if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
            NSString *from = chat.getMsgFrom;
            if(from && [from isKindOfClass:[NSString class]]){
                
                [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:from invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                   
                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                        [[YiChatUserManager defaultManagaer] addLocalGroupMemberShutUpWithGroupId:self.presenter.chatId userId:[model getOriginDic] groupInfo:self.presenter.chatGroupInfoModel];
                        
                        [ZFGroupHelper setGroupMemberShutUpWithGroupId:self.presenter.chatId userId:from status:YES invocation:^(BOOL isSuccess, NSString * _Nonnull des) {
                           
                            
                        }];
                    }
                    
                }];
            }
        }
    }
    
    
}
    
- (void)searchInfoMethodAction:(id)sender{
    
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:selecte.section];
        if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){
            NSString *from = chat.getMsgFrom;
            if(from && [from isKindOfClass:[NSString class]]){
                
                NSString *userId = from;
                [self.navigationController pushViewController:[ZFChatUIHelper getUserInfoVCWithUserId:userId] animated:YES];
            }
        }
    }
}
    
- (void)voicePlayMenuItemAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        ZFChatConfigure *chat = [self.presenter getChatDataWithIndex:selecte.section];
        if(chat && [chat isKindOfClass:[ZFChatConfigure class]]){

            BOOL isPlaying = [ZFChatHelper zfChatHelper_getPlayVoicePlayingState];
            if(isPlaying){
                [ZFChatHelper zfChatHelper_ChangePlayVoiceMode];
            }
            else{
                ZFChatCell *cell =  [self.cTable cellForRowAtIndexPath:selecte];
                [cell voiceClickMethod:nil];
                
                [ZFChatHelper zfChatHelper_ChangePlayVoiceMode];
            }
            
            
        }
    }
}

- (NSIndexPath *)selecteMenu{
    if(self.menuIndex && [self.menuIndex isKindOfClass:[NSIndexPath class]]){
        NSIndexPath *selecte = [NSIndexPath indexPathForRow:self.menuIndex.row inSection:self.menuIndex.section];
        [self removeMenu];
        if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
            return selecte;
        }
    }
    return nil;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
   
    
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self.view];
    if(point.y >= self.cTable.frame.origin.y && point.y <= self.cTable.frame.size.height + self.cTable.frame.origin.y){
        
        if(self.presenter){
            [self.presenter resignChatTool];
        }
    }
    
    [self removeMenu];
}

- (void)removeMenu{
    if([self.menuVC isMenuVisible]){
        [self.menuVC setMenuVisible:NO animated:NO];
    }
    self.menuIndex = nil;
}

- (NSString *)getChatId{
    return self.presenter.chatId;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
