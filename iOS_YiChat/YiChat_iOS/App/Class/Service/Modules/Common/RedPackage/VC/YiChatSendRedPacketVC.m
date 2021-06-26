//
//  YiChatSendRedPacketVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSendRedPacketVC.h"
#import "YiChatRedPacketInPutView.h"
#import <KLCPopup/KLCPopup.h>
#import "YiChatRedPacketChoosePayView.h"
#import "YiChatGrabRedPacketView.h"
#import "YiChatRedPacketDetailVC.h"
#import "YiChatRedPacketDetailModel.h"
#import "YiChatUserManager.h"
#import "ZFChatNotifyEntity.h"
#import "ZFChatHelper.h"
@interface YiChatSendRedPacketVC ()<UITableViewDelegate,UITableViewDataSource,UITextViewDelegate,YiChatRedPacketInPutViewDelegate>{
    UILabel *groupMembersNumLa;
}
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) UILabel *moneyLa;
@property (nonatomic,strong) UIButton *sendButton;
@property (nonatomic,strong) UILabel *placeholderLa;

@property (nonatomic,copy) NSString *redNum;//红包数
@property (nonatomic,copy) NSString *redMoney;//红包金额

@property (nonatomic,strong) KLCPopup *popView;

@property (nonatomic,strong) NSString *redContent;

@property (nonatomic,assign) CGFloat balance;

@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeActive;

@property (nonatomic,assign) BOOL isPay;

@property (nonatomic,strong) NSString *out_trade_no;
@end

@implementation YiChatSendRedPacketVC

+ (id)initialVC{
    YiChatSendRedPacketVC *walletVC = [YiChatSendRedPacketVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"SendRedPacket") leftItem:@"返回" rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    [self setUI];
    self.isPay = NO;
    if (self.isGroup) {
        [[YiChatUserManager defaultManagaer] updateGroupInfoWithGroupId:self.chatId invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            self.groupMembersNum = [NSString stringWithFormat:@"%ld",model.memberCount];
            dispatch_async(dispatch_get_main_queue(), ^{
                self->groupMembersNumLa.text = [NSString stringWithFormat:@"本群共%@人",self.groupMembersNum == nil? @"0" : self.groupMembersNum];
            });
        }];
    }
    
    _notify_app_becomeActive = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeActive target:self sel:@selector(appDidBeginActive:)];
    [_notify_app_becomeActive addNotify];
}

-(void)dealloc{
    [_notify_app_becomeActive removeSuperNotify];
}

//从后台回到
- (void)appDidBeginActive:(NSNotification *)notfy{
    WS(weakSelf);
    if (self.isPay) {
        if (self.out_trade_no && self.out_trade_no.length > 0) {
            [YiChatRedPacketHelper queryTradeNo:weakSelf.out_trade_no status:^(BOOL status) {
                weakSelf.out_trade_no = @"";
                weakSelf.isPay = NO;
                [weakSelf payRedPacket:@"" type:@"2"];
            }];
        }
    }
    
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self.view addSubview:self.tableView];
    self.tableView.rowHeight = 60;
    self.tableView.tableHeaderView = [self tabHeaderView];
    self.tableView.tableFooterView = [self tabFootView];
}

#pragma tableView代理相关方法
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        YiChatRedPacketInPutView *whiteBG = [[YiChatRedPacketInPutView alloc]initWithFrame:CGRectZero];
        whiteBG.backgroundColor = [UIColor whiteColor];
        whiteBG.layer.masksToBounds = YES;
        whiteBG.layer.cornerRadius = 4.0;
        whiteBG.isGroup = self.isGroup;
        whiteBG.redPacketTitle = self.isGroup? @"总金额" : @"金额";
        whiteBG.placeholder = @"0.00";
        whiteBG.unit = @"元";
        whiteBG.textFieldTag = 1;
        whiteBG.delegate = self;
        [cell.contentView addSubview:whiteBG];
        [whiteBG mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(0);
            make.left.mas_equalTo(15);
            make.width.mas_equalTo(PROJECT_SIZE_WIDTH-25);
            make.height.mas_equalTo(60);
        }];
    }
    cell.backgroundColor = [UIColor groupTableViewBackgroundColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

#pragma 群红包 顶部红包数量view创建
-(UIView *)tabHeaderView{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, self.isGroup? 100 : 10)];
    bg.backgroundColor = [UIColor clearColor];
    if (self.isGroup) {
        YiChatRedPacketInPutView *whiteBG = [[YiChatRedPacketInPutView alloc]init];
        whiteBG.backgroundColor = [UIColor whiteColor];
        whiteBG.layer.masksToBounds = YES;
        whiteBG.layer.cornerRadius = 4.0;
        whiteBG.redPacketTitle = @"红包个数";
        whiteBG.placeholder = @"0";
        whiteBG.unit = @"个";
        whiteBG.isGroup = self.isGroup;
        whiteBG.textFieldTag = 0;
        whiteBG.delegate = self;
        [bg addSubview:whiteBG];
        [whiteBG mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(10);
            make.left.mas_equalTo(15);
            make.width.mas_equalTo(PROJECT_SIZE_WIDTH-25);
            make.height.mas_equalTo(60);
        }];
        
        groupMembersNumLa = [[UILabel alloc]initWithFrame:CGRectZero];
        //groupMembersNumLa.text = [NSString stringWithFormat:@"本群共%@人",self.groupMembersNum == nil? @"0" : self.groupMembersNum];
        groupMembersNumLa.font = [UIFont systemFontOfSize:12];
        [bg addSubview:groupMembersNumLa];
        [groupMembersNumLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(15);
            make.bottom.mas_equalTo(-5);
            make.height.mas_equalTo(20);
        }];
    }
    return bg;
}

#pragma YiChatRedPacketInPutView代理方法
-(void)textFieldInPutChangeText:(NSString *)string tag:(NSInteger)tag{
    if (tag == 0) {
        self.redNum = string;
    }else{
        if (string.length == 0) {
            self.moneyLa.text = @"￥0.00";
        }else{
            self.moneyLa.text = [NSString stringWithFormat:@"￥%.2f",string.floatValue];
        }
        self.redMoney = string;
    }
    
    if (self.isGroup) {
        if (self.redNum.length > 0 && self.redMoney.length > 0) {
            self.sendButton.enabled = YES;
        }else{
            self.sendButton.enabled = NO;
        }
    }else{
        if (self.redMoney.length > 0) {
            self.sendButton.enabled = YES;
        }else{
            self.sendButton.enabled = NO;
        }
    }
    
}

#pragma 创建发送按钮、红包金额label、提示文字textView

-(UIView *)tabFootView{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 300)];
    bg.backgroundColor = [UIColor clearColor];
    UITextView *textView = [[UITextView alloc]initWithFrame:CGRectZero];
    textView.backgroundColor = [UIColor whiteColor];
    textView.delegate = self;
    textView.layer.masksToBounds = YES;
    textView.font = [UIFont systemFontOfSize:14];
    textView.layer.cornerRadius = 4.0;
    [bg addSubview:textView];
    [textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(15);
        make.left.mas_equalTo(15);
        make.height.mas_equalTo(80);
        make.width.mas_equalTo(PROJECT_SIZE_WIDTH - 25);
    }];
    
    self.placeholderLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.placeholderLa.text = @"恭喜发财，大吉大利";
    self.placeholderLa.font = [UIFont systemFontOfSize:14];
    self.placeholderLa.textColor = [UIColor grayColor];
    self.placeholderLa.userInteractionEnabled = NO;
    [textView addSubview:self.placeholderLa];
    [self.placeholderLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(5);
        make.top.mas_equalTo(6);
        make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 40, 20));
    }];
    
    self.moneyLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.moneyLa.font = [UIFont fontWithName:@"Helvetica-Bold" size:23];
    self.moneyLa.text = @"￥0.00";
    self.moneyLa.textAlignment = NSTextAlignmentCenter;
    [bg addSubview:self.moneyLa];
    [self.moneyLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH, 25));
        make.top.equalTo(textView.mas_bottom).offset(10);
    }];

    self.sendButton = [[UIButton alloc]initWithFrame:CGRectZero];
    self.sendButton.layer.masksToBounds = YES;
    self.sendButton.layer.cornerRadius = 4;
    [self.sendButton setTitle:@"塞钱进红包" forState:UIControlStateNormal];
    [self.sendButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.sendButton addTarget:self action:@selector(sendRedPacket) forControlEvents:UIControlEventTouchUpInside];
    [self.sendButton setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:247/255.0 green:68/255.0 blue:77/255.0 alpha:1] size:CGSizeMake(1, 1)] forState:UIControlStateNormal];
    self.sendButton.enabled = NO;
    [bg addSubview:self.sendButton];
    [self.sendButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.moneyLa.mas_bottom).offset(20);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 40, 50));
    }];
    return bg;
}

#pragma textView代理
- (void)textViewDidChange:(UITextView *)textView{
    if (!textView.text.length) {
        self.placeholderLa.hidden = NO;
    } else {
        self.placeholderLa.hidden = YES;
    }
}

-(void)textViewDidEndEditing:(UITextView *)textView{
    self.redContent = textView.text;
}

#pragma 发送红包
-(void)sendRedPacket{
    [self.view endEditing:YES];
    self.sendButton.enabled = NO;
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        if (model.payPasswordStatus.integerValue == 0) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请前往钱包界面设置支付密码"];
            return;
            
        }else{
            if (self.redContent.length == 0 || self.redContent == nil || [self.redContent isEqualToString:@""]) {
                self.redContent = @"恭喜发财，大吉大利";
            }
    
            if (self.isGroup) {
                
                if (self.redNum.integerValue > self.groupMembersNum.integerValue) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"红包个数不能多于%@个",self.groupMembersNum]];
                    return;
                }
                
                if ((self.redMoney.floatValue / self.redNum.floatValue) > 200.0) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"单个红包金额不能超过200元"]];
                    return;
                }
                
                if (self.redMoney.floatValue < self.redNum.floatValue * 0.00999999) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"单个红包金额不能少于0.01元"]];
                    return;
                }
            }else{
                if (self.redMoney.floatValue < 0.009999) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"单个红包金额不能少于0.01元"]];
                    return;
                }
            }
            
            [self reloadBalance];
        }
    }];
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
}

- (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size {
    if (!color || size.width <= 0 || size.height <= 0)
        return nil;
    CGRect rect = CGRectMake(0.0f, 0.0f, size.width, size.height);
    UIGraphicsBeginImageContextWithOptions(rect.size, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

-(void)payRedPacket:(NSString *)passWord type:(NSString *)type{
    WS(weakSelf);
    NSDictionary *param = [NSDictionary new];
    if (self.isGroup) {
        param = [ProjectRequestParameterModel getSendGroupRedPacketParametersWithGroupId:self.chatId money:self.redMoney content:self.redContent nun:self.redNum password:passWord type:type];
        
    }else{
        param = [ProjectRequestParameterModel getSendSingleRedPacketParametersWithReceiveUserId:self.chatId money:self.redMoney content:self.redContent password:passWord type:type];
        
    }
    
    [YiChatRedPacketHelper sendRedPacketGroup:self.isGroup param:param successDic:^(NSDictionary * _Nonnull successDic) {
        weakSelf.sendRedPacketBlock(successDic, weakSelf.isGroup);
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf dismissViewControllerAnimated:YES completion:nil];
        });
    }];
}

-(void)reloadBalance{
    WS(weakSelf);
    [YiChatRedPacketHelper searchBalance:^(NSString * _Nonnull balance) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.balance = balance.floatValue;
            dispatch_async(dispatch_get_main_queue(), ^{
                if (weakSelf.balance < weakSelf.redMoney.floatValue) {
                    
                    NSString *str = @"";
                    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
                        str = @"积分不足";
                    }else{
                        str = @"请选择微信或者支付宝支付";
                    }
                    UIAlertController *aletr = [UIAlertController alertControllerWithTitle:@"余额不足" message:str preferredStyle:UIAlertControllerStyleAlert];
                    UIAlertAction *aliPay = [UIAlertAction actionWithTitle:@"支付宝支付" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                        [YiChatRedPacketHelper aliPayType:@"1" money:weakSelf.redMoney payBlock:^(BOOL isInstallation, NSString * _Nonnull out_trade_no) {
                            if (isInstallation){
                                weakSelf.isPay = YES;
                                weakSelf.out_trade_no = out_trade_no;
                                
                            }
                        }];
                    }];
                    
                    UIAlertAction *wechatPay = [UIAlertAction actionWithTitle:@"微信支付" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                        [YiChatRedPacketHelper weChatPayType:@"1" money:weakSelf.redMoney payBlock:^(BOOL isInstallation, NSString * _Nonnull out_trade_no) {
                            if (isInstallation){
                                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(wePayResult:) name:WXPayonResp object:nil];
                            }
                        }];
                    }];
                    
                    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
                    if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 1) {
                        [aletr addAction:aliPay];
                        [aletr addAction:wechatPay];
                    }
                    
                    if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 0) {
                        [aletr addAction:aliPay];
                    }
                    
                    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 1) {
                        [aletr addAction:wechatPay];
                    }
                    
                    [aletr addAction:cancel];
                    [weakSelf presentViewController:aletr animated:YES completion:nil];
                    return;
                }
                [weakSelf showChossesPayView:[NSString stringWithFormat:@"%.2f",balance.floatValue]];
            });
        });
    }];
}

-(void)showChossesPayView:(NSString *)balance{
    WS(weakSelf);
    YiChatRedPacketChoosePayView *payView = [[YiChatRedPacketChoosePayView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 60, 320)];
    payView.layer.cornerRadius = 5;
    payView.layer.masksToBounds = YES;
    payView.redMoney = weakSelf.redMoney;
    payView.balance = balance;
    payView.vc = weakSelf;
    payView.dissmiss = ^{
        [self.popView dismiss:YES];
        self.sendButton.enabled = YES;
    };
    payView.payType = ^(RedPacketPayType payType, NSString * _Nonnull password) {
        [weakSelf.popView dismiss:YES];
        weakSelf.sendButton.enabled = YES;
        if (payType == RedPacketPayBalance) {
            [weakSelf payRedPacket:password type:@"0"];
        }
        
        if (payType == RedPacketPayWeChat) {
            [YiChatRedPacketHelper weChatPayType:@"1" money:weakSelf.redMoney payBlock:^(BOOL isInstallation, NSString * _Nonnull out_trade_no) {
                if (isInstallation){
                    [[NSNotificationCenter defaultCenter] addObserver:weakSelf selector:@selector(wePayResult:) name:WXPayonResp object:nil];
                }
            }];
        }
        
        if (payType == RedPacketPayAli) {
            [YiChatRedPacketHelper aliPayType:@"1" money:weakSelf.redMoney payBlock:^(BOOL isInstallation,  NSString * _Nonnull out_trade_no) {
                if (isInstallation){
                    weakSelf.isPay = YES;
                    weakSelf.out_trade_no = out_trade_no;
                }
            }];
        }
    };
    
    payView.backgroundColor = [UIColor whiteColor];
    weakSelf.popView = [KLCPopup popupWithContentView:payView showType:KLCPopupShowTypeBounceIn dismissType:KLCPopupDismissTypeGrowOut maskType:KLCPopupMaskTypeDimmed dismissOnBackgroundTouch:NO dismissOnContentTouch:NO];
    [weakSelf.popView showAtCenter:CGPointMake(weakSelf.view.frame.size.width / 2, weakSelf.view.frame.size.height/ 2 - 100) inView:weakSelf.view];
}

-(void)alipayRusurt{
    [self payRedPacket:@"" type:@"2"];
}

-(void)wePayResult:(NSNotification *)noti{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSDictionary *dic = noti.userInfo;
        if ([dic[@"errCode"] integerValue] == 0) {
            [self payRedPacket:@"" type:@"1"];
        }else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"红包发送失败"];
        }
    });
}
@end
