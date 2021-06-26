//
//  YiChatModifyPayPwdVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  修改支付密码 设置支付密码

#import "YiChatModifyPayPwdVC.h"
#import <Masonry/Masonry.h>
#import "ServiceGlobalDef.h"
#import "YiChatUserManager.h"
#import "ProjectRequestHelper.h"
#import "YiChatRedPacketDetailVC.h"
#import "YiChatRedPacketHelper.h"
#import "YiChatWalletComCell.h"
#import "YiChatUserManager.h"
#import "YiChatChangePhoneNumVC.h"

@interface YiChatModifyPayPwdVC ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,copy) NSString *passWord1;
@property (nonatomic,copy) NSString *passWord2;
@property (nonatomic,copy) NSString *passWordOld;
@property (nonatomic,strong) UIButton *codeBtn;
@property (nonatomic,strong) NSMutableArray *titleArr;
@property (nonatomic,strong) NSMutableArray *placeholderArr;
@property (nonatomic,strong) NSString *codeStr;
@property (nonatomic,strong) NSString *code;
@property (nonatomic,strong) YiChatUserModel *userModel;

@property (nonatomic,assign) BOOL isSetPay;
@end

@implementation YiChatModifyPayPwdVC

+ (id)initialVC{
    YiChatModifyPayPwdVC *walletVC = [YiChatModifyPayPwdVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"ModifyPay") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.titleArr = [NSMutableArray new];
    self.placeholderArr = [NSMutableArray new];
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    WS(weakSelf);
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        weakSelf.userModel = model;
        if (weakSelf.userModel.payPasswordStatus.integerValue == 0) {
            weakSelf.isSetPay = NO;
        }else{
            weakSelf.isSetPay = YES;
        }
        
        if (YiChatProjext_CertifyPower == 0) {
            [weakSelf.placeholderArr addObjectsFromArray:@[@"请输入新的支付密码",@"确认新支付密码"]];
            [weakSelf.titleArr addObjectsFromArray:@[@"支付密码",@"确认支付密码"]];
        }else{
            [weakSelf.placeholderArr addObjectsFromArray:@[@"请输入新的支付密码",@"确认新支付密码",@"请输入验证码"]];
            [weakSelf.titleArr addObjectsFromArray:@[@"支付密码",@"确认支付密码",@"验证码"]];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self setUI];
        });
    }];
}

-(void)setUI{
    UIView *header = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 30)];
    UILabel *la = [[UILabel alloc]initWithFrame:CGRectMake(15, 0, 110, 30)];
    la.font = [UIFont systemFontOfSize:13];
    la.text = self.isSetPay? @"修改支付密码" : @"设置支付密码";
    [header addSubview:la];

    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.rowHeight = 50;
    self.tableView.tableHeaderView = header;
    self.tableView.tableFooterView = [self setTabFootView];
    self.tableView.scrollEnabled = NO;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self.view addSubview:self.tableView];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.titleArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatWalletComCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatWalletComCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    if (indexPath.row == 2) {
        [cell.codeBtn addTarget:self action:@selector(clickCodeBtn) forControlEvents:UIControlEventTouchUpInside];
        self.codeBtn = cell.codeBtn;
        cell.codeBtn.hidden = NO;
    }else{
        cell.codeBtn.hidden = YES;
    }
    
    cell.backgroundColor = [UIColor whiteColor];
    cell.titleLa.text = self.titleArr[indexPath.row];
    cell.textField.placeholder = self.placeholderArr[indexPath.row];
    cell.textField.tag = indexPath.row;
    cell.textField.keyboardType = UIKeyboardTypeNumberPad;
    cell.textField.secureTextEntry = YES;
    cell.textField.delegate = self;
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (self.placeholderArr.count > 0) {
        return;
    }
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    if (textField.tag == 0){
        self.passWord1 = textField.text;
    }else if (textField.tag == 1){
        self.passWord2 = textField.text;
    }else{
        self.code = textField.text;
    }
}

-(UIView *)setTabFootView{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 200)];
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
    [btn setTitle:@"确认提交" forState:UIControlStateNormal];
    btn.layer.masksToBounds = YES;
    btn.layer.cornerRadius = 4;
    btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    [btn addTarget:self action:@selector(settingPassWord) forControlEvents:UIControlEventTouchUpInside];
    [bg addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(bg);
        make.width.mas_equalTo(PROJECT_SIZE_WIDTH - 30);
        make.height.mas_equalTo(45);
    }];
    return bg;
}

- (BOOL) deptNumInputShouldNumber:(NSString *)str{
    if (str.length == 0) {
        return NO;
    }
    NSString *regex = @"[0-9]*";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    if ([pred evaluateWithObject:str]) {
        return YES;
    }
    return NO;
}

-(void)settingPassWord{
    [self.view endEditing:YES];
    if (YiChatProjext_CertifyPower == 1) {
        if (![self.code isEqualToString:self.codeStr]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"验证码有误，请重新输入"];
            return;
        }
    }
    
    if (self.passWord1.length == 0 || self.passWord1 == nil) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入密码"];
        return;
    }
    
    if (![self.passWord1 isEqualToString:self.passWord2]) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"两次输入密码不一致，请重新输入！"];
        return;
    }
    
    NSString *str = [self.passWord1 stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    if (str.length != 6) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"密码必须是六位数字！"];
        return;
    }
    
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel getSetPayPassWord:self.passWord1];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [ProjectRequestHelper setPayPassWord:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"支付密码设置成功"];
                [[YiChatUserManager defaultManagaer] updateUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                    
                }];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakSelf.navigationController popViewControllerAnimated:YES];
                });
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

-(void)clickCodeBtn{
    [self.view endEditing:YES];
    
    if (self.userModel.mobile) {
        [YiChatRedPacketHelper sendSMSCode:self.userModel.mobile smsCode:^(NSString * _Nonnull code) {
            self.codeStr = code;
        }];
        [self openCountdown];
    }else{
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"温馨提示" message:@"未绑定手机，请先绑定手机号" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *banding = [UIAlertAction actionWithTitle:@"绑定手机" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [self.navigationController pushViewController:[YiChatChangePhoneNumVC initialVC] animated:YES];
        }];
        [alert addAction:banding];
        [self presentViewController:alert animated:YES completion:nil];
    }
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
}

-(void)openCountdown{
    __block NSInteger time = 59; //倒计时时间
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_source_t _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue);
    
    dispatch_source_set_timer(_timer,dispatch_walltime(NULL, 0),1.0*NSEC_PER_SEC, 0); //每秒执行
    
    dispatch_source_set_event_handler(_timer, ^{
        
        if(time <= 0){ //倒计时结束，关闭
            
            dispatch_source_cancel(_timer);
            dispatch_async(dispatch_get_main_queue(), ^{
                
                //设置按钮的样式
                [self.codeBtn setTitle:@"重新发送" forState:UIControlStateNormal];
                self.codeBtn.enabled = YES;
            });
        }else{
            int seconds = time % 60;
            dispatch_async(dispatch_get_main_queue(), ^{
                
                //设置按钮显示读秒效果
                [self.codeBtn setTitle:[NSString stringWithFormat:@"%@(%.2d)",@"重新发送", seconds] forState:UIControlStateNormal];
                self.codeBtn.enabled = NO;
            });
            time--;
        }
    });
    dispatch_resume(_timer);
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
