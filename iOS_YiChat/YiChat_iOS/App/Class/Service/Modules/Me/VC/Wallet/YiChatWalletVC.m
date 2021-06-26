//
//  YiChatWalletVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatWalletVC.h"
#import "YiChatWalletViewCell.h"
#import "YiChatGrabRedPacketView.h"
#import <KLCPopup.h>
#import "YiChatWalletHeaderView.h"


@interface YiChatWalletVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) YiChatWalletHeaderView *walletHeaderView;
@property (nonatomic,strong) NSArray *imgArr;
@property (nonatomic,strong) NSArray *titleArr;
@property (nonatomic,strong) KLCPopup *popView;
@end

@implementation YiChatWalletVC

+ (id)initialVC{
     if(YiChatProject_IsNeedQianDao){
           YiChatWalletVC *walletVC = [YiChatWalletVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"WalletVC") leftItem:nil rightItem:@"签到"];
           walletVC.hidesBottomBarWhenPushed = YES;
           return walletVC;
       }
       else{
           YiChatWalletVC *walletVC = [YiChatWalletVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_1 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"WalletVC") leftItem:nil rightItem:nil];
           walletVC.hidesBottomBarWhenPushed = YES;
           return walletVC;
       }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"我的钱包";
    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
        self.imgArr = @[@"pic_packet",@"pic_list",@"pic_safe"];
        self.titleArr = @[@"我的红包",@"交易记录",@"安全设置"];
    }else{
        self.imgArr = @[@"pic_packet",@"pic_list",@"pic_safe",@"pic_card"];
        self.titleArr = @[@"我的红包",@"交易记录",@"安全设置",@"银行卡设置"];
    }
    [self setUI];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self reloadBalance];
}

//签到
- (void)navBarButtonRightItemMethod:(UIButton *)btn{
     if(YiChatProject_IsNeedQianDao){
          [self pushVCWithName:@"YiChatSignInVC"];
      }
}

-(void)setUI{
    __weak typeof(self) weakSelf = self;
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.walletHeaderView = [[YiChatWalletHeaderView alloc]initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 250)];
    self.walletHeaderView.walletHeaderBlock = ^(BOOL isWithdrawal) {
        NSString *name = @"";
        if (isWithdrawal) {
            name = @"YiChatWithdrawalVC";
        }else{
            name = @"YiChatRechargeVC";
        }
        [weakSelf pushVCWithName:name];
    };
    self.tableView.tableHeaderView = self.walletHeaderView;
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.scrollEnabled = NO;
    [self.view addSubview:self.tableView];
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self.navigationController popViewControllerAnimated:YES];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.titleArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatWalletViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatWalletViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    cell.imgString = self.imgArr[indexPath.row];
    cell.titleString = self.titleArr[indexPath.row];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSString *title = self.titleArr[indexPath.row];
    NSString *name = @"";
    if ([title isEqualToString:@"我的红包"]) {
        name = @"YiChatRedPacketInfoVC";
    }
 
    if ([title isEqualToString:@"账户信息"]) {
        
    }
    
    if ([title isEqualToString:@"交易记录"]) {
        name = @"YiChatRecordsListVC";
    }
    
    if ([title isEqualToString:@"安全设置"]) {
        name = @"YiChatModifyPayPwdVC";
    }
    
    if ([title isEqualToString:@"银行卡设置"]) {
        name = @"YiChatBankCardListVC";
    }
    [self pushVCWithName:name];
}

-(void)reloadBalance{
    [YiChatRedPacketHelper searchBalance:^(NSString * _Nonnull balance) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.walletHeaderView.balance = [NSString stringWithFormat:@"%@",balance];
        });
    }];
}

- (void)pushVCWithName:(NSString *)name{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:@selector(initialVC)];
            vc.hidesBottomBarWhenPushed = YES;
            if(vc){
                [self.navigationController pushViewController:vc animated:YES];
            }
        }
    }
}

@end
