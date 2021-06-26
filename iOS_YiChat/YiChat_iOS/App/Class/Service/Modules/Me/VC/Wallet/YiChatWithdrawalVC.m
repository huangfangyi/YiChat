//
//  YiChatWithdrawalVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/25.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  提现

#import "YiChatWithdrawalVC.h"
#import "YiChatWithdrawalListVC.h"
#import "YiChatBankCardListVC.h"

@interface YiChatWithdrawalVC ()
@property (nonatomic,strong) UITextField *texxtField;
@property (nonatomic,strong) UITextField *noteTexxtField;
@property (nonatomic,copy) NSString *balance;
@property (nonatomic,strong) UILabel *bankName;
@property (nonatomic,strong) NSString *bankNum;

@property (nonatomic,strong) UITextView *textView;

@property (nonatomic,assign) CGFloat minLimit;

@property (nonatomic,assign) CGFloat rate;
@end

@implementation YiChatWithdrawalVC

+ (id)initialVC{
    YiChatWithdrawalVC *walletVC = [YiChatWithdrawalVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"Withdrawal") leftItem:nil rightItem:@"提现记录"];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"提现";
    self.bankNum = @"";
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [YiChatRedPacketHelper searchBalance:^(NSString * _Nonnull balance) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.balance = balance;
        });
    }];
    [self setUI];
    [self reloadwithdrawConfig];
}

-(void)setUI{
    UIView *addBankView = [[UIView alloc]initWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, PROJECT_SIZE_WIDTH, 80)];
    addBankView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:addBankView];
    
    self.bankName = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 80)];
    self.bankName.text = @"+添加银行卡";
    self.bankName.textAlignment = NSTextAlignmentCenter;
    self.bankName.numberOfLines = 2;
    self.bankName.font = [UIFont systemFontOfSize:14];
    self.bankName.textColor = PROJECT_COLOR_APPMAINCOLOR;
    [addBankView addSubview:self.bankName];
    
    UIButton *addBankBtn = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 80)];
    [addBankBtn addTarget:self action:@selector(addBankCard) forControlEvents:UIControlEventTouchUpInside];
    [addBankView addSubview:addBankBtn];
    
    CGFloat y = PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 10;
    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
        addBankView.hidden = YES;
    }else{
        y = PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 90;
    }
    
    UIView *withdrawalView = [[UIView alloc] initWithFrame:CGRectMake(0, y, PROJECT_SIZE_WIDTH, 50)];
    withdrawalView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:withdrawalView];
    
    UILabel *withdrawalLa = [[UILabel alloc] initWithFrame:CGRectZero];
    withdrawalLa.text = @"提现金额";
    withdrawalLa.font = [UIFont systemFontOfSize:13];
    [withdrawalView addSubview:withdrawalLa];
    [withdrawalLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.mas_equalTo(15);
        make.size.mas_equalTo(CGSizeMake(60, 20));
    }];
    
    self.texxtField = [[UITextField alloc]initWithFrame:CGRectZero];
    self.texxtField.placeholder = @"请输入提现金额";
    self.texxtField.font = [UIFont systemFontOfSize:13];
    self.texxtField.keyboardType = UIKeyboardTypeDecimalPad;
    [self.view addSubview:self.texxtField];
    [self.texxtField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(withdrawalLa.mas_centerY).offset(0);
        make.left.equalTo(withdrawalLa.mas_right).offset(0);
        make.right.mas_equalTo(-10);
        make.height.mas_equalTo(30);
    }];
    
    UIButton *allWithdrawal = [[UIButton alloc]initWithFrame:CGRectZero];
    [allWithdrawal setTitle:@"全部提现" forState:UIControlStateNormal];
    allWithdrawal.layer.masksToBounds = YES;
    allWithdrawal.layer.cornerRadius = 3;
    allWithdrawal.layer.borderColor = PROJECT_COLOR_APPMAINCOLOR.CGColor;
    allWithdrawal.layer.borderWidth = 1.0f;
    allWithdrawal.titleLabel.font = [UIFont systemFontOfSize:13];
    [allWithdrawal setTitleColor:PROJECT_COLOR_APPMAINCOLOR forState:UIControlStateNormal];
    [allWithdrawal addTarget:self action:@selector(allWithdrawalAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:allWithdrawal];
    [allWithdrawal mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(withdrawalLa.mas_centerY).offset(0);
        make.right.mas_equalTo(-10);
        make.size.mas_equalTo(CGSizeMake(90, 30));
    }];
    
    UIView *noteView = [[UIView alloc] initWithFrame:CGRectZero];
    noteView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:noteView];
    [noteView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(withdrawalView.mas_bottom).offset(10);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(50);
    }];
    
    UILabel *noteLa = [[UILabel alloc] initWithFrame:CGRectZero];
    noteLa.text = @"备注";
    noteLa.font = [UIFont systemFontOfSize:13];
    [noteView addSubview:noteLa];
    [noteLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.mas_equalTo(15);
        make.size.mas_equalTo(CGSizeMake(60, 20));
    }];
    
    self.noteTexxtField = [[UITextField alloc]initWithFrame:CGRectZero];
    self.noteTexxtField.placeholder = @"请填写备注";
    self.noteTexxtField.font = [UIFont systemFontOfSize:13];
    self.noteTexxtField.keyboardType = UIKeyboardTypeDefault;
    [noteView addSubview:self.noteTexxtField];
    [self.noteTexxtField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(noteLa.mas_centerY).offset(0);
        make.left.equalTo(noteLa.mas_right).offset(0);
        make.right.mas_equalTo(-10);
        make.height.mas_equalTo(30);
    }];
    
    UIButton *withdrawalBtn = [[UIButton alloc]initWithFrame:CGRectZero];
    [withdrawalBtn setTitle:@"提现" forState:UIControlStateNormal];
    withdrawalBtn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    [withdrawalBtn addTarget:self action:@selector(withdrawalAction) forControlEvents:UIControlEventTouchUpInside];
    withdrawalBtn.layer.masksToBounds = YES;
    withdrawalBtn.layer.cornerRadius = 5;
    [self.view addSubview:withdrawalBtn];
    [withdrawalBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(withdrawalView.mas_bottom).offset(100);
        make.left.mas_equalTo(20);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(45);
    }];
    
    self.textView = [[UITextView alloc] initWithFrame:CGRectZero];
    self.textView.font = [UIFont systemFontOfSize:14];
    self.textView.userInteractionEnabled = NO;
    [self.view addSubview:self.textView];
    [self.textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(withdrawalBtn.mas_bottom).offset(30);
        make.bottom.mas_equalTo(-20);
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
    }];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [self.navigationController pushViewController:[YiChatWithdrawalListVC initialVC] animated:YES];
}

-(void)allWithdrawalAction{
    self.texxtField.text = [NSString stringWithFormat:@"%.2f",self.balance.floatValue];
}

-(void)withdrawalAction{
    [self.view endEditing:YES];
    if (self.texxtField.text.length == 0 || self.texxtField.text == nil || [self.texxtField.text isEqualToString:@""]) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"提现金额不能为空"];
        return;
    }
    
    if (self.texxtField.text.floatValue < self.minLimit) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"提现金额不能低于%.2f元",self.minLimit]];
        return;
    }

    if (YiChatProjext_IsNeedAliPay == 0 && YiChatProjext_IsNeedWeChat == 0) {
        if ([self.texxtField.text floatValue] < 50.0000000001) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"提现不能少于50元"];
            return;
        }
    }else{
        if (self.bankNum.length == 0 || self.bankNum == nil || [self.bankNum isEqualToString:@""]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请选择银行卡"];
            return;
        }
    }
    
    if ([self.texxtField.text floatValue] > [self.balance floatValue]) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"余额不足"];
        return;
    }
    
    NSDictionary *param = [ProjectRequestParameterModel getWithdrawApplyParametersWithMoney:self.texxtField.text bankNumber:self.bankNum memo:self.noteTexxtField.text];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [ProjectRequestHelper withdrawApplyWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:dataDic];
                if (model.code == 0) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"提现提交成功"];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.navigationController popViewControllerAnimated:YES];
                    });
                }
                
                if (model.code == 250) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
      
            }else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

-(void)addBankCard{
    YiChatBankCardListVC *vc = [YiChatBankCardListVC initialVC];
    vc.isWithdrawal = YES;
    vc.chooseBank = ^(NSString * _Nonnull bankName, NSString * _Nonnull bankNum) {
        self.bankNum = bankNum;
        self.bankName.text = [NSString stringWithFormat:@"(%@)%@\n(点击重选银行卡)",bankName,[self getNewStarBankNumWitOldNum:bankNum]];
    };
    [self.navigationController pushViewController:vc animated:YES];
}

-(NSString *)getNewStarBankNumWitOldNum:(NSString *)bankCardNum{
    NSString *bankNum = bankCardNum;
    NSMutableString *mutableStr;
    if (bankNum.length) {
        mutableStr = [NSMutableString stringWithString:bankNum];
        for (int i = 0 ; i < mutableStr.length; i ++) {
            if (i < mutableStr.length - 4) {
                [mutableStr replaceCharactersInRange:NSMakeRange(i, 1) withString:@"*"];
            }
        }
        NSString *text = mutableStr;
        //        NSCharacterSet *characterSet = [NSCharacterSet characterSetWithCharactersInString:@"0123456789\b"];
        text = [text stringByReplacingOccurrencesOfString:@" " withString:@""];
//        NSString *newString = @"";
//        while (text.length > 0) {
//            NSString *subString = [text substringToIndex:MIN(text.length, 4)];
//            newString = [newString stringByAppendingString:subString];
//            if (subString.length == 4) {
//                newString = [newString stringByAppendingString:@" "];
//            }
//            text = [text substringFromIndex:MIN(text.length, 4)];
//        }
        //        newString = [newString stringByTrimmingCharactersInSet:[characterSet invertedSet]];
        return text;
    }
    return bankNum;
}

-(void)reloadwithdrawConfig{
    WS(weakSelf);
    [ProjectRequestHelper withdrawConfigParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:dataDic];
                if (model.code == 0) {
                    NSDictionary *dic = dataDic[@"data"];
                    NSString *rateStr = [NSString stringWithFormat:@"%@",dic[@"rate"]];
                    NSString *minLimitStr = [NSString stringWithFormat:@"%@",dic[@"minLimit"]];
                    NSString *text = [NSString stringWithFormat:@"%@",dic[@"text"]];
                    weakSelf.rate = rateStr.floatValue;
                    weakSelf.minLimit = minLimitStr.floatValue;
                    dispatch_async(dispatch_get_main_queue(), ^{
                        weakSelf.textView.text = text;
                    });
                }
                
                if (model.code == 250) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
                
            }else if([obj isKindOfClass:[NSString class]]){
//                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
//            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
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
