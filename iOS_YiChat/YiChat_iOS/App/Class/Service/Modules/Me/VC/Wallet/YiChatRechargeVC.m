//
//  YiChatRechargeVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/25.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  充值

#import "YiChatRechargeVC.h"
#import "YiChatAddBankCardCell.h"
#import "YiChatAddBankCardVC.h"
#import "WXApi.h"
#import "ZFChatNotifyEntity.h"

@interface YiChatRechargeVC ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSString *money;
@property (nonatomic,strong) NSString *tradeNo;
@property (nonatomic,strong) ZFChatNotifyEntity *notify_app_becomeActive;
@end

@implementation YiChatRechargeVC

+ (id)initialVC{
    YiChatRechargeVC *RechargeVC = [YiChatRechargeVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"Recharge") leftItem:nil rightItem:nil];
//    RechargeVC.hidesBottomBarWhenPushed = YES;
    return RechargeVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"充值";
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self setUI];
    _notify_app_becomeActive = [self getNotifyEntitfyWithStyle:ZFChatNotifyStyleAppBecomeActive superNotifyName:UIApplicationDidBecomeActiveNotification target:self sel:@selector(appBecomeActive)];
    [_notify_app_becomeActive addSuperNotify];
}

- (ZFChatNotifyEntity *)getNotifyEntitfyWithStyle:(NSInteger)style superNotifyName:(NSString *)superNotifyName target:(id)target sel:(SEL)selector{
    return [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:style superNotifyName:superNotifyName target:target sel:selector];
}

-(void)appBecomeActive{
    WS(weakSelf);
    if (self.tradeNo && self.tradeNo.length > 0) {
        [YiChatRedPacketHelper queryTradeNo:self.tradeNo status:^(BOOL status) {
            weakSelf.tradeNo = nil;
            [ProjectHelper helper_getMainThread:^{
                [weakSelf.navigationController popViewControllerAnimated:YES];
            }];
        }];
    }
}

-(void)dealloc{
    [_notify_app_becomeActive removeSuperNotify];
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.rowHeight = 50;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.scrollEnabled = NO;
    [self.view addSubview:self.tableView];
    
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 200)];
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
    [btn addTarget:self action:@selector(clickBtn) forControlEvents:UIControlEventTouchUpInside];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    btn.layer.masksToBounds = YES;
    btn.layer.cornerRadius = 3;
    [btn setTitle:@"下一步" forState:UIControlStateNormal];
    [bg addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(0);
        make.width.mas_equalTo(PROJECT_SIZE_WIDTH - 40);
        make.height.mas_equalTo(45);
    }];
    
    self.tableView.tableFooterView = bg;
}

-(void)clickBtn{
    [self.view endEditing:YES];
    WS(weakSelf);
    NSString *moneyStr = self.money;
    if (!moneyStr || moneyStr.length == 0) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入充值金额"];
        return;
    }
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"选择充值方式" message:@"" preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction *ali = [UIAlertAction actionWithTitle:@"支付宝支付" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [YiChatRedPacketHelper aliPayType:@"0" money:weakSelf.money payBlock:^(BOOL isInstallation, NSString * _Nonnull out_trade_no) {
            if (isInstallation){
                weakSelf.tradeNo = out_trade_no;
                NSLog(@"=====   成功了  %@",out_trade_no);
//                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(alipayRusurt) name:ALIPayResp object:nil];
            }
        }];
    }];
    
    UIAlertAction *wechat = [UIAlertAction actionWithTitle:@"微信支付" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [YiChatRedPacketHelper weChatPayType:@"0" money:self.money payBlock:^(BOOL isInstallation, NSString * _Nonnull out_trade_no) {
            if (isInstallation){
                NSLog(@"=====   成功了");
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(wePayResult:) name:WXPayonResp object:nil];
            }
        }];
    }];
    
    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 1) {
        [alert addAction:ali];
        [alert addAction:wechat];
    }
    
    if (YiChatProjext_IsNeedAliPay == 1 && YiChatProjext_IsNeedWeChat == 0) {
        [alert addAction:ali];
    }
    
    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 1) {
        [alert addAction:wechat];
    }

    [alert addAction:cancel];
    [self presentViewController:alert animated:YES completion:nil];
}

-(void)alipayRusurt{
    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"充值成功"];
    [self.navigationController popViewControllerAnimated:YES];
}

-(void)wePayResult:(NSNotification *)noti{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSDictionary *dic = noti.userInfo;
        if ([dic[@"errCode"] integerValue] == 0) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"充值成功"];
            [self.navigationController popViewControllerAnimated:YES];
        }else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"支付失败"];
        }
    });
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatAddBankCardCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatAddBankCardCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    cell.codeBtn.hidden = YES;
    cell.titleLa.text = @"金额(元)";
    cell.textField.placeholder = @"请输入充值金额";
    cell.textField.keyboardType = UIKeyboardTypeDecimalPad;
    cell.textField.tag = indexPath.row;
    cell.textField.delegate = self;
    return cell;
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    self.money = textField.text;
}

-(UIView *)tabHeaderView{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 80)];
    bg.backgroundColor = [UIColor groupTableViewBackgroundColor];
    UIImageView *imageView = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"add_black"]];
    [bg addSubview:imageView];
    [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.centerX.mas_equalTo(-30);
        make.size.mas_equalTo(CGSizeMake(25, 25));
    }];
    
    UILabel *la = [[UILabel alloc]init];
    la.text = @"添加银行卡";
    la.font = [UIFont systemFontOfSize:14];
    [bg addSubview:la];
    [la mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.equalTo(imageView.mas_right).offset(10);
        make.height.mas_equalTo(20);
    }];
    
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
    [btn addTarget:self action:@selector(addBank) forControlEvents:UIControlEventTouchUpInside];
    [bg addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
    }];
    return bg;
}

-(void)addBank{
    [self.view endEditing:YES];
    YiChatAddBankCardVC *vc = [YiChatAddBankCardVC new];
    vc.type = 1;
    [self.navigationController pushViewController:vc animated:YES];
}

@end
