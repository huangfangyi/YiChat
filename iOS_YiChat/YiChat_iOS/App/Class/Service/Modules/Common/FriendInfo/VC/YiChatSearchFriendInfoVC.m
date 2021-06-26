//
//  YiChatSearchFriendInfoVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSearchFriendInfoVC.h"
#import "ServiceGlobalDef.h"

#import "ProjectClickView.h"
#import "YiChatFriendInfoView.h"
#import "YiChatUserManager.h"

#import "ProjectRequestHelper.h"
#import "ZFChatUIHelper.h"
#import "YiChatSetFriendsRemarkNameVC.h"

#import "ZFChatFriendHelper.h"

#import "YiChatAddFriendInputReason.h"
#import "ZFChatManage.h"
#import "YiChatDynamicVC.h"
#import "ZFChatMessageHelper.h"
#import "YiChatSearchGroupMsgVC.h"
@interface YiChatSearchFriendInfoVC ()

@property (nonatomic,strong) YiChatFriendInfoView *info;

@property (nonatomic,strong) ProjectClickView *click;

@property (nonatomic,strong) NSString *friendStatus;

@property (nonatomic,strong) UISwitch *switchControl;

@property (nonatomic,assign) BOOL shutState;

@property (nonatomic,assign) BOOL isFirst;

@end

@implementation YiChatSearchFriendInfoVC

+ (id)initialVC{

    return [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"friendCard") leftItem:nil rightItem:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _isFirst = YES;
    
    if(self.infoDic && [self.infoDic isKindOfClass:[NSDictionary class]]){
        
        YiChatUserModel *model = [[YiChatUserModel alloc] initWithDic:self.infoDic];
        _model = model;
        _userId = [NSString stringWithFormat:@"%ld",model.userId];
    }
    if(self.model && [self.model isKindOfClass:[YiChatUserModel class]]){
        _userId = [NSString stringWithFormat:@"%ld",self.model.userId];
    }
    
    [self makeUI];
    
    [self loadData];
    
    [self loadFriendStatus];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if(!_isFirst){
        NSString *userId = nil;
        if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
            userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
        }
        if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
            userId = [NSString stringWithFormat:@"%ld",_model.userId];
        }
        if(_userId && [_userId isKindOfClass:[NSString class]]){
            userId = _userId;
        }
        
        if(userId && [userId isKindOfClass:[NSString class]]){
            WS(weakSelf);
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                if(model && [model isKindOfClass:[YiChatUserModel class]]){
                    weakSelf.model = model;
                    [ProjectHelper helper_getMainThread:^{
                        [weakSelf xySroll_reloadData];
                    }];
                }
            }];
        }
    }
    _isFirst = NO;
}

- (void)loadData{
    NSString *userId = nil;
    if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [NSString stringWithFormat:@"%ld",_model.userId];
    }
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        WS(weakSelf);
        [[YiChatUserManager defaultManagaer] updateUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                weakSelf.model = model;
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf xySroll_reloadData];
                }];
            }
        }];
    }
}

- (void)loadFriendStatus{
    
    if(self.userId && [self.userId isKindOfClass:[NSString class]]){
        
        if([self.userId isEqualToString:YiChatUserInfo_UserIdStr]){
            _click.hidden = YES;
        }
        else{
            [[YiChatUserManager defaultManagaer] judgeFriendshipWithFriendId:self.userId invocation:^(NSString * _Nonnull frinedShip) {
                [ProjectHelper helper_getMainThread:^{
                    if(frinedShip && [frinedShip isKindOfClass:[NSString class]]){
                        
                        _friendStatus = frinedShip;
                        if(_click){
                            _click.hidden = NO;
                            if([frinedShip integerValue]){
                                _click.lab.text = @"发送消息";
                                self.sectionNum = 6;
                                
                                [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:self.userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                    if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                        NSDictionary *userInfoDic = [model getOriginDic];
                                        if(userInfoDic && [userInfoDic isKindOfClass:[NSDictionary class]]){
                                            [self addConnectionFriendWithFriendDic:userInfoDic];
                                        }
                                    }
                                }];
                                
                            }
                            else{
                                _click.lab.text = @"添加好友";
                                self.sectionNum = 2;
                                
                                [self deleteConnectionFriendWithFriendId:self.userId];
                                
                            }
                        }
                        if(self){
                            
                            NSMutableArray *secRows = [NSMutableArray arrayWithCapacity:0];
                            for (int i = 0; i < self.sectionNum; i ++) {
                                if(i == 0){
                                    [secRows addObject:[NSNumber numberWithInteger:1]];
                                }
                                else{
                                    [secRows addObject:[NSNumber numberWithInteger:1]];
                                }
                            }
                            
                            self.rowsNumSet = secRows;
                            
                            self.cScroll.contentSize = self.reallyContentSize;
                            
                            [self xySroll_reloadData];
                            
                            [self changeScrollFrame:CGRectMake(self.scrollFrame.origin.x, self.scrollFrame.origin.y, self.scrollFrame.size.width, self.reallyContentSize.height)];
                            
                            _click.frame = CGRectMake(_click.frame.origin.x,self.cScroll.frame.origin.y + self.reallyContentSize.height + PROJECT_SIZE_NAV_BLANK , _click.frame.size.width, _click.frame.size.height);
                        }
                    }
                }];
            }];
        }
    }
}

- (void)deleteConnectionFriendWithFriendId:(NSString *)friend{
    if(friend && [friend isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] deleteConnectionFriends:@[friend] invocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull des) {
            
        }];
    }
}

- (void)addConnectionFriendWithFriendDic:(NSDictionary *)dic{
    if(dic && [dic isKindOfClass:[NSDictionary class]]){
        [[YiChatUserManager defaultManagaer] addConnectionFriends:@[dic] invocation:^(YiChatConnectionModel * _Nonnull model, NSString * _Nonnull error) {
            
        }];
    }
}


- (YiChatFriendInfoView *)info{
    if(!_info){
        _info = [[YiChatFriendInfoView alloc] initWithFrame:CGRectMake(0, 0, self.cScroll.frame.size.width, PROJECT_SIZE_FRIENDCARD_INFO_H)];
    }
    return _info;
}

- (void)makeUI{
    self.scrollFrame = CGRectMake(0,PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, self.view.frame.size.height - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH);
    [self.view addSubview:self.cScroll];
    
    self.sectionNum = 2;
    
    NSMutableArray *secRows = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.sectionNum; i ++) {
        if(i == 0){
            [secRows addObject:[NSNumber numberWithInteger:1]];
        }
        else{
            [secRows addObject:[NSNumber numberWithInteger:1]];
        }
    }
    
    self.rowsNumSet = secRows;
    
    self.cScroll.contentSize = self.reallyContentSize;
    
    self.cScroll.backgroundColor = [UIColor whiteColor];
    
    [self xySroll_reloadData];
    
    [self changeScrollFrame:CGRectMake(self.scrollFrame.origin.x, self.scrollFrame.origin.y, self.scrollFrame.size.width, self.reallyContentSize.height)];
    
    [self makeClickBtn];
}

- (void)makeClickBtn{
    WS(weakSelf);
    
    
    ProjectClickView *certifyLog = [ProjectClickView createClickViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.cScroll.frame.origin.y + self.reallyContentSize.height + PROJECT_SIZE_NAV_BLANK, self.cScroll.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_CLICKBTN_H) title:@"" type:0];
    certifyLog.userInteractionEnabled = YES;
    
    certifyLog.clickInvocation = ^(NSString * _Nonnull identify) {
        if([weakSelf isFriendStatus] == 0){
            [weakSelf addFriends];
        }
        else{
            UIViewController *vc = [ZFChatUIHelper getChatVCWithChatId:[NSString stringWithFormat:@"%ld",_model.userId] chatType:@"1"];
            [self.navigationController pushViewController:vc animated:YES];
        }
    };
    _click = certifyLog;
    _click.hidden = YES;
    [self.view addSubview:certifyLog];
}

- (CGFloat)XYScrollController_CellHWithIndex:(NSIndexPath *)index{
    if(index.section == 0){
        return PROJECT_SIZE_FRIENDCARD_INFO_H;
    }
    return PROJECT_SIZE_FRIENDCARD_COMMON_CELLH;
}

- (CGFloat)XYScrollController_SectionHWithIndex:(NSInteger)section{
    if(section == 1){
        return 15.0;
    }
    return 10;
}

- (CGFloat)XYScrollController_FooterHWithIndex:(NSInteger)section{
    return 0;
}

- (void)XYScroll_makeUIForHeaderWithSection:(NSInteger)section{
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, [self XYScroll_getHeaderBeginPositionWithHeader:section],self.cScroll.frame.size.width, [self XYScrollController_SectionHWithIndex:section])];
    [self.cScroll addSubview:back];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
}

- (void)XYScroll_makeUIForRow:(NSIndexPath *)row{
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, [self XYScroll_getRowBeginPositionWithIndex:row], self.cScroll.frame.size.width, [self XYScrollController_CellHWithIndex:row])];
    [self.cScroll addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    if(row.section == 0){
        [back addSubview:self.info];
        if([_model isKindOfClass:[YiChatUserModel class]]){
            self.info.userModel = _model;
        }
    }
    else if(row.section == 1){
        
        
        if([self isFriendStatus] == 1){
            UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
            [back addSubview:fromTitle];
            fromTitle.text = @"设置备注";
            
            UILabel *remarkContent = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(fromTitle.frame.size.width + fromTitle.frame.origin.x, 0,fromTitle.frame.size.width, fromTitle.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentRight];
            if(self.model && [self.model isKindOfClass:[YiChatUserModel class]]){
                remarkContent.text = [self.model remarkName];
            }
            [back addSubview:remarkContent];
            
            UIButton *changeRemark = [UIButton buttonWithType:UIButtonTypeCustom];
            changeRemark.frame = CGRectMake(0, 0, back.frame.size.width, back.frame.size.height);
            [back addSubview:changeRemark];
            [changeRemark addTarget:self action:@selector(changeRemark:) forControlEvents:UIControlEventTouchUpInside];
        }
        else{
            UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
            [back addSubview:fromTitle];
            fromTitle.text = @"来自";
            
            
            UILabel *fromContent = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(fromTitle.frame.size.width + fromTitle.frame.origin.x, 0,fromTitle.frame.size.width, fromTitle.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentRight];
            [back addSubview:fromContent];
            if([_fromDes isKindOfClass:[NSString class]] && _fromDes){
                fromContent.text = _fromDes;
            }
            NSString *userId = nil;
            if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
                userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
            }
            if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
                userId = [NSString stringWithFormat:@"%ld",_model.userId];
            }
            if(_userId && [_userId isKindOfClass:[_userId class]]){
                userId = _userId;
            }
            if(userId && [userId isKindOfClass:[NSString class]]){
                if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
                    fromContent.text = @"自己";
                }
            }
        }
    }
    else if(row.section == 2){
        UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
        [back addSubview:fromTitle];
        fromTitle.text = @"个人相册";
        
        UIButton *albumn = [UIButton buttonWithType:UIButtonTypeCustom];
        albumn.frame = CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width, back.frame.size.height);
        [back addSubview:albumn];
        [albumn addTarget:self action:@selector(goToAlumn:) forControlEvents:UIControlEventTouchUpInside];
        
        
    }
    else if(row.section == 3){
        UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
        [back addSubview:fromTitle];
        fromTitle.text = @"消息免打扰";
        
        CGFloat w = 50.0;
        CGFloat x = back.frame.size.width - w - PROJECT_SIZE_NAV_BLANK;
        CGFloat h = 30.0;
        
        _switchControl = [[UISwitch alloc] initWithFrame:CGRectMake(x, back.frame.size.height / 2 - h / 2, w,  h )];
        [back addSubview:_switchControl];
        [_switchControl addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
        
        NSString *userId = nil;
        if(_userId && [_userId isKindOfClass:[NSString class]]){
            userId = _userId;
        }
        else if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
            userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
        }
        else if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
            userId = [self.model getUserIdStr];
        }
        
        if(userId && [userId isKindOfClass:[NSString class]]){
            [[YiChatUserManager defaultManagaer] getMessageShutUpStateWithChatId:userId invocation:^(NSString * _Nonnull state) {
                if(state && [state isKindOfClass:[NSString class]]){
                    [ProjectHelper helper_getMainThread:^{
                        
                        if(self){
                            BOOL shutState = [state boolValue];
                            
                            _shutState = shutState;
                            
                            [_switchControl setOn:shutState animated:YES];
                        }
                        
                    }];
                    
                }
            }];
        }
    }
    else if(row.section == 4){
          UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
          [back addSubview:fromTitle];
          fromTitle.text = @"清空聊天记录";

          UIButton *albumn = [UIButton buttonWithType:UIButtonTypeCustom];
          albumn.frame = CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width, back.frame.size.height);
          [back addSubview:albumn];
          [albumn addTarget:self action:@selector(cleanChatRecorder:) forControlEvents:UIControlEventTouchUpInside];
          
          
      }
    else if(row.section == 5){
           UILabel *fromTitle = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0,(back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2) / 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
           [back addSubview:fromTitle];
           fromTitle.text = @"查找聊天记录";

           UIButton *albumn = [UIButton buttonWithType:UIButtonTypeCustom];
           albumn.frame = CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width, back.frame.size.height);
           [back addSubview:albumn];
           [albumn addTarget:self action:@selector(searchChatRecorder) forControlEvents:UIControlEventTouchUpInside];
           
           
       }
}

-(void)searchChatRecorder{
    NSString *userId = nil;
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    else if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    else if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [self.model getUserIdStr];
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        YiChatSearchGroupMsgVC *vc = [YiChatSearchGroupMsgVC initialVC];
        vc.chatId = userId;
        vc.chatType = @"1";
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)goToAlumn:(UIButton *)btn{
    NSString *userId = nil;
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    else if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    else if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [self.model getUserIdStr];
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        YiChatDynamicVC *vc = [YiChatDynamicVC initialVC];
        vc.hidesBottomBarWhenPushed = YES;
        vc.userId = userId;
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)cleanChatRecorder:(UIButton *)btn{
    NSString *userId = nil;
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    else if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    else if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [self.model getUserIdStr];
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        HTConversationManager *manager = [HTClient sharedInstance].conversationManager;
        [manager deleteOneChatterAllMessagesByChatterId:userId];
        [manager deleteOneConversationWithChatterId:userId isCleanAllHistoryMessage:YES];

        [[NSNotificationCenter defaultCenter] postNotificationName:@"clearMsg" object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:@"clearMsg" object:nil];
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [ZFChatMessageHelper upDateMsgType:@"1" to:userId];
            if([progress respondsToSelector:@selector(hidden)]){
                [progress performSelector:@selector(hidden)];
            }
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"清除完成"];
        });
    }
}

- (void)switchAction:(id)sender{
    _shutState = !_shutState;
    [_switchControl setOn:_shutState animated:YES];
    
    NSString *userId = nil;
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    else if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    else if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [self.model getUserIdStr];
    }
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        [[YiChatUserManager defaultManagaer] storageMessageShutUpStateWithChatId:userId state:[NSString stringWithFormat:@"%d",_shutState]];
    }
}

// status == 0 好友 == 1非好友
- (NSInteger)isFriendStatus{
    if(_friendStatus && [_friendStatus isKindOfClass:[NSString class]]){
        if([_friendStatus integerValue] == 1){
            return 1;
        }
        else if([_friendStatus integerValue] == 0){
            return 0;
        }
    }
    return -1;
}

- (void)changeRemark:(UIButton *)btn{
    NSString *userId = nil;
    if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [NSString stringWithFormat:@"%ld",_model.userId];
    }
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    
    YiChatSetFriendsRemarkNameVC *setRemark = [YiChatSetFriendsRemarkNameVC initialVC];
    setRemark.userModel = self.model;
    setRemark.userId = userId;
    [self.navigationController pushViewController:setRemark animated:YES];
}

- (void)addFriends{
    WS(weakSelf);
    
    NSString *userId = nil;
    if(_infoDic && [_infoDic isKindOfClass:[NSDictionary class]]){
        userId = [NSString stringWithFormat:@"%ld",[_infoDic[@"userId"] integerValue]];
    }
    if(_model && [_model isKindOfClass:[YiChatUserModel class]]){
        userId = [NSString stringWithFormat:@"%ld",_model.userId];
    }
    if(_userId && [_userId isKindOfClass:[NSString class]]){
        userId = _userId;
    }
    
    YiChatAddFriendInputReason *reason = [YiChatAddFriendInputReason initialVC];
    reason.friendId = userId;
    [self.navigationController pushViewController:reason animated:YES];
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
