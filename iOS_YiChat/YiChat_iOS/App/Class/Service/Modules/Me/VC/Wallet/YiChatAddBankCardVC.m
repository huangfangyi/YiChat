//
//  YiChatAddBankCardVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/24.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//添加银行卡 银行卡设置

#import "YiChatAddBankCardVC.h"
#import "YiChatAddBankCardCell.h"
#import "YiChatWalletVC.h"
#import "YiChatBankCardListVC.h"
@interface YiChatAddBankCardVC ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArr;
@property (nonatomic,strong) NSMutableDictionary *placeholderDic;
@property (nonatomic,strong) UIButton *codeBtn;
@property (nonatomic,strong) NSMutableDictionary *requestDic;
@property (nonatomic,strong) NSString *codeStr;
@property (nonatomic,strong) UILabel *bankNameLa;

@end

@implementation YiChatAddBankCardVC

+ (id)initialVC{
    YiChatAddBankCardVC *walletVC = [YiChatAddBankCardVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"AddBankCard") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"添加银行卡";
    self.view.backgroundColor = [UIColor whiteColor];
    self.dataArr = [NSMutableArray new];
    self.placeholderDic = [NSMutableDictionary new];
    self.requestDic = [NSMutableDictionary new];
    if (self.type == 1) {
        [self.dataArr addObject:@"银行卡号"];
        self.placeholderDic[@"银行卡号"] = @"请输入银行卡号";
    }
    
    if (self.type == 2) {
        [self.dataArr addObject:@"银行卡号"];
        [self.dataArr addObject:@"姓名"];
        [self.dataArr addObject:@"身份证号"];
        [self.dataArr addObject:@"手机号"];
        [self.dataArr addObject:@"验证码"];
        
        NSString *s = [self.bankcard substringFromIndex:self.bankcard.length - 4];
        
        self.requestDic[@"银行卡号"] = [NSString stringWithFormat:@"%@(%@)",self.bank,s];
        self.requestDic[@"bank"] = self.bank;
        self.placeholderDic[@"姓名"] = @"请填写姓名";
        self.placeholderDic[@"身份证号"] = @"请输入身份证号";
        self.placeholderDic[@"手机号"] = @"请输入银手机号";
        self.placeholderDic[@"验证码"] = @"请输入验证码";
    }
    
    [self setUI];
}

- (NSString *)returnBankName:(NSString *)cardName {
    NSString *filePath = [[NSBundle mainBundle]pathForResource:@"bank" ofType:@"plist"];
    NSDictionary *resultDic = [NSDictionary dictionaryWithContentsOfFile:filePath];
    NSArray *bankBin = resultDic.allKeys;
    if (cardName.length < 6) {
        return @"";
    }
    NSString *cardbin_6 ;
    if (cardName.length >= 6) {
        cardbin_6 = [cardName substringWithRange:NSMakeRange(0, 6)];
    }
    NSString *cardbin_8 = nil;
    if (cardName.length >= 8) {
        //8位
        cardbin_8 = [cardName substringWithRange:NSMakeRange(0, 8)];
    }
    if ([bankBin containsObject:cardbin_6]) {
        return [resultDic objectForKey:cardbin_6];
    } else if ([bankBin containsObject:cardbin_8]){
        return [resultDic objectForKey:cardbin_8];
    } else {
        return @"";
    }
    return @"";
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.rowHeight = 50;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    [self.view addSubview:self.tableView];
//    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
//    }];
    
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 200)];
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
    [btn addTarget:self action:@selector(clickBtn) forControlEvents:UIControlEventTouchUpInside];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    btn.layer.masksToBounds = YES;
    btn.layer.cornerRadius = 3;
    [bg addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(0);
        make.width.mas_equalTo(PROJECT_SIZE_WIDTH - 40);
        make.height.mas_equalTo(45);
    }];
    
    self.tableView.tableFooterView = bg;
//    if (self.type == 0) {
//        btn.hidden = YES;
////        self.tableView.tableHeaderView = [self tabHeaderView];
//    }else
    
    if (self.type == 1){
        self.bankNameLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.bankNameLa.textAlignment = NSTextAlignmentCenter;
        [bg addSubview:self.bankNameLa];
        [self.bankNameLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(25);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        [btn setTitle:@"下一步" forState:UIControlStateNormal];
    }else{
        [btn setTitle:@"确认提交" forState:UIControlStateNormal];
    }
}


- (BOOL) IsBankCard:(NSString *)cardNumber{
    if(cardNumber.length==0){
        return NO;
    }
    NSString *digitsOnly = @"";
    char c;
    for (int i = 0; i < cardNumber.length; i++){
        c = [cardNumber characterAtIndex:i];
        if (isdigit(c)){
            digitsOnly =[digitsOnly stringByAppendingFormat:@"%c",c];
        }
    }
    int sum = 0;
    int digit = 0;
    int addend = 0;
    BOOL timesTwo = false;
    for (NSInteger i = digitsOnly.length - 1; i >= 0; i--){
        digit = [digitsOnly characterAtIndex:i] - '0';
        if (timesTwo)
        {
            addend = digit * 2;
            if (addend > 9) {
                addend -= 9;
            }
        }
        else {
            addend = digit;
        }
        sum += addend;
        timesTwo = !timesTwo;
    }
    int modulus = sum % 10;
    return modulus == 0;
}

-(void)clickBtn{
    [self.view endEditing:YES];
    if (self.type == 1) {
        YiChatAddBankCardVC *vc = [YiChatAddBankCardVC initialVC];
        vc.type = 2;
        vc.bankcard = self.bankcard;
        NSString *text = self.bankcard;
        //返回一个字符集,指定字符串中包含的字符
        NSCharacterSet *characterSet = [NSCharacterSet characterSetWithCharactersInString:@"0123456789\b"];
        if ([text rangeOfCharacterFromSet:[characterSet invertedSet]].location != NSNotFound) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"银行卡号不合规范请重新输入"];
            return ;
        }
        
        if (![self IsBankCard:text]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"银行卡号不合规范请重新输入"];
            return;
        }
        //不能写nil
        NSString *newString = @"";
        while (text.length > 0) {
            //每4位截取/不够4位有多少截取多少
            NSString *subString = [text substringToIndex:MIN(text.length, 4)];
            newString = [newString stringByAppendingString:subString];
            //加空格
            if (subString.length == 4) {
                newString = [newString stringByAppendingString:@" "];
            }
            text = [text substringFromIndex:MIN(text.length, 4)];
        }
        newString = [newString stringByTrimmingCharactersInSet:[characterSet invertedSet]];
        //限制长度
        if (newString.length >= 24) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"银行卡号不合规范请重新输入"];
            return ;
        }
        
        //        NSLog(@"%@",);
        NSString *originalStr = [newString stringByReplacingOccurrencesOfString:@" " withString:@""];
        //判断实哪家银行,并赋值
        //        if ([self returnBankName:originalStr].length > 0) {
        self.bank = [self returnBankName:originalStr];
        //        }
        
        
        vc.bank = self.bank;
        [self.navigationController pushViewController:vc animated:YES];
        
    }else{
        if (!self.requestDic[@"code"]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入验证码"];
            return;
        }
        
        NSString *code = [NSString stringWithFormat:@"%@",self.requestDic[@"code"]];

        if (![code isEqual:self.codeStr]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入正确的验证码"];
            return;
        }
        //银行卡
        if ([self.bankcard isEqualToString:@""] || [self.bank isEqualToString:@""] || [self.requestDic[@"mobile"] isEqualToString:@""] || [self.requestDic[@"idcard"] isEqualToString:@""] || [self.requestDic[@"realname"] isEqualToString:@""]) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请填写完整的银行卡信息"];
            return;
        }
        
        NSDictionary *param = [ProjectRequestParameterModel getAddBankCardParametersWithName:self.requestDic[@"realname"] mobile:self.requestDic[@"mobile"] idNumber:self.requestDic[@"idcard"] bankName:self.bank bankNumber:self.bankcard];
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        [ProjectRequestHelper addBankCardWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:dataDic];
                    if (model.code == 0) {
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"银行卡添加成功"];
                        NSArray *arr = self.navigationController.viewControllers;
                        dispatch_async(dispatch_get_main_queue(), ^{
                            for (UIViewController *vc in arr) {
                                if ([vc isKindOfClass:[YiChatBankCardListVC class]]) {
                                    [self.navigationController popToViewController:vc animated:YES];
                                }
                            }
                        });
                    }else{
                        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                    }
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
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.type == 0? 0 : self.dataArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatAddBankCardCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatAddBankCardCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    if (indexPath.row == 4) {
        [cell.codeBtn addTarget:self action:@selector(clickCodeBtn) forControlEvents:UIControlEventTouchUpInside];
        self.codeBtn = cell.codeBtn;
        cell.codeBtn.hidden = NO;
    }else{
        cell.codeBtn.hidden = YES;
    }
    
    if (self.type == 2 && indexPath.row == 0) {
        cell.textField.userInteractionEnabled = NO;
    }else{
        cell.textField.userInteractionEnabled = YES;
    }
    
    cell.titleLa.text = self.dataArr[indexPath.row];
    cell.textField.placeholder = self.placeholderDic[cell.titleLa.text];
    cell.textField.tag = indexPath.row;
    cell.textField.delegate = self;
    cell.textField.text = self.requestDic[cell.titleLa.text];
    return cell;
}

//- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
//    self.bankNameLa.text = textField.text;
//
//    return YES;
//}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    NSString *str = [NSString stringWithFormat:@"%@%@",textField.text,string];
    self.bankNameLa.text = [self returnBankName:str];
    
    return YES;
}

-(void)textFieldDidEndEditing:(UITextField *)textField{
    if (textField.tag == 0) {
        if (self.type == 1) {
            self.bankcard = textField.text;
        }
    }
    
    if (textField.tag == 1) {
        self.requestDic[@"realname"] = textField.text;
    }
    
    if (textField.tag == 2) {
        self.requestDic[@"idcard"] = textField.text;
    }
    
    if (textField.tag == 3) {
        self.requestDic[@"mobile"] = textField.text;
    }
    
    if (textField.tag == 4) {
        self.requestDic[@"code"] = textField.text;
    }
}

//-(UIView *)tabHeaderView{
//    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 80)];
//    bg.backgroundColor = [UIColor whiteColor];
//    UIImageView *imageView = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"add_black"]];
//    [bg addSubview:imageView];
//    [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerY.mas_equalTo(0);
//        make.centerX.mas_equalTo(-30);
//        make.size.mas_equalTo(CGSizeMake(25, 25));
//    }];
//
//    UILabel *la = [[UILabel alloc]init];
//    la.text = @"添加银行卡";
//    la.font = [UIFont systemFontOfSize:14];
//    [bg addSubview:la];
//    [la mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerY.mas_equalTo(0);
//        make.left.equalTo(imageView.mas_right).offset(10);
//        make.height.mas_equalTo(20);
//    }];
//
//    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
//    [btn addTarget:self action:@selector(addBank) forControlEvents:UIControlEventTouchUpInside];
//    [bg addSubview:btn];
//    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
//    }];
//    return bg;
//}

-(void)addBank{
    [self.view endEditing:YES];
    YiChatAddBankCardVC *vc = [YiChatAddBankCardVC initialVC];
    vc.type = 1;
    [self.navigationController pushViewController:vc animated:YES];
}

-(void)clickCodeBtn{
    [self.view endEditing:YES];
    NSString *mobile = self.requestDic[@"mobile"];
    if ([mobile isEqualToString:@""] || mobile.length == 0 || mobile.length != 11 || mobile == nil) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"手机号不合规范，请重新输入"];
        return;
    }
    
    [YiChatRedPacketHelper sendSMSCode:self.requestDic[@"mobile"] smsCode:^(NSString * _Nonnull code) {
        self.codeStr = code;
    }];
    [self openCountdown];
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
@end
