//
//  YiChatBankCardListVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/29.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBankCardListVC.h"
#import "YiChatAddBankCardVC.h"
#import "YiChatBankCardListCell.h"
#import "YiChatBankCardListModel.h"

@interface YiChatBankCardListVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArray;

@end

@implementation YiChatBankCardListVC

+ (id)initialVC{
    YiChatBankCardListVC *walletVC = [YiChatBankCardListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"BankCardList") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"添加银行卡";
    self.view.backgroundColor = [UIColor whiteColor];
    self.dataArray = [NSMutableArray new];
    [self setUI];
    
    // Do any additional setup after loading the view.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self reloadata];
}

-(void)reloadata{
//    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper bankCardListWithParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                [self.dataArray removeAllObjects];
                YiChatBankCardListModel *model = [YiChatBankCardListModel mj_objectWithKeyValues:dataDic];
                [self.dataArray addObjectsFromArray:model.data];
                if (model.code == 0) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData];
                    });
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

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.rowHeight = 90;
    self.tableView.tableHeaderView = [self tabHeaderView];
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    [self.view addSubview:self.tableView];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArray.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatBankCardListCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatBankCardListCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.model = self.dataArray[indexPath.row];
    return cell;
}

-(UIView *)tabHeaderView{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 80)];
    bg.backgroundColor = [UIColor groupTableViewBackgroundColor];
    UILabel *la = [[UILabel alloc]init];
    la.text = @"+添加银行卡";
    la.textColor = PROJECT_COLOR_APPMAINCOLOR;
    la.textAlignment = NSTextAlignmentCenter;
    la.font = [UIFont systemFontOfSize:14];
    [bg addSubview:la];
    [la mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(200, 20));
    }];
    
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectZero];
    [btn addTarget:self action:@selector(addBank) forControlEvents:UIControlEventTouchUpInside];
    [bg addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
    }];
    return bg;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return self.isWithdrawal? NO : YES;
    
}
// 定义编辑样式
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
    
}
// 进入编辑模式，按下出现的编辑按钮后,进行删除操作
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        YiChatBankCardInfoModel *model = self.dataArray[indexPath.row];
        NSLog(@"这里做删除操作这里做删除操作这里做删除操作这里做删除操作这里做删除操作这里做删除操作这里做删除操作这里做删除操作");
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        NSDictionary *param = [ProjectRequestParameterModel getDeleteBankCardParametersWithCardId:model.cardID];
        [ProjectRequestHelper deleteBankCardWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    [self.dataArray removeAllObjects];
                    YiChatBankCardListModel *model = [YiChatBankCardListModel mj_objectWithKeyValues:dataDic];
                    [self.dataArray addObjectsFromArray:model.data];
                    if (model.code == 0) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self.tableView reloadData];
                        });
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
    
}
// 修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath {
    return @"删除";
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (self.isWithdrawal) {
        YiChatBankCardInfoModel *model = self.dataArray[indexPath.row];
        self.chooseBank(model.bankName, model.bankNumber);
        [self.navigationController popViewControllerAnimated:YES];
    }
}

-(void)addBank{
    YiChatAddBankCardVC *vc = [YiChatAddBankCardVC initialVC];
    vc.type = 1;
    [self.navigationController pushViewController:vc animated:YES];
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
