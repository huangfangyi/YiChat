//
//  YiChatTransactionRecordsVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  交易记录列表

#import "YiChatTransactionRecordsVC.h"
#import "YiChatTransactionRecordsCell.h"
#import "YiChatWalletRecordModel.h"

@interface YiChatTransactionRecordsVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArr;
@property (nonatomic,strong) NSMutableArray *sectionArr;
@property (nonatomic ,assign) NSInteger page;

@property (nonatomic,assign) BOOL isNoData;
@end

@implementation YiChatTransactionRecordsVC

-(NSMutableArray *)dataArr{
    
    if (!_dataArr) {
        _dataArr = [NSMutableArray new];
    }
    return _dataArr;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.sectionArr = [NSMutableArray new];
    self.view.backgroundColor = [UIColor whiteColor];
    self.page = 1;
    [self setUI];
    [self reloadData];
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStyleGrouped];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.rowHeight = 70;
    self.tableView.sectionHeaderHeight = 30;
    self.tableView.sectionFooterHeight = CGFLOAT_MIN;
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(tableViewDidTriggerHeaderRefresh)];
    self.tableView.mj_footer = [MJRefreshAutoFooter footerWithRefreshingTarget:self refreshingAction:@selector(tableViewDidTriggerFooterRefresh)];
    [self.view addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
    }];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    NSArray *arr = self.dataArr[section];
    return arr.count;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return self.dataArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatTransactionRecordsCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatTransactionRecordsCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.model = self.dataArr[indexPath.section][indexPath.row];
    return cell;
}

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 30)];
    bg.backgroundColor = [UIColor groupTableViewBackgroundColor];
    UILabel *sectionTitle = [[UILabel alloc]initWithFrame:CGRectMake(15, 0, 200, 30)];
    sectionTitle.text = self.sectionArr[section];
    sectionTitle.font = [UIFont systemFontOfSize:14];
    sectionTitle.textColor = [UIColor grayColor];
    [bg addSubview:sectionTitle];
    return bg;
}

-(UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    return [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, CGFLOAT_MIN)];
}

- (void)tableViewDidTriggerFooterRefresh {
    if (self.isNoData) {
        return;
    }
    self.page++;
    [self reloadData];
}

-(void)tableViewDidTriggerHeaderRefresh{
    self.page = 1;
    [self.dataArr removeAllObjects];
    [self reloadData];
}

-(void)reloadData{
    NSString *type = @"";
    if (self.recordType == RecordTypeAll) {
        type = @"";
    }else if (self.recordType == RecordTypeIncome){
        type = @"1";
    }else{
        type = @"0";
    }
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel getBalanceListParametersWithType:type pageSize:@"20" pageNo:[NSString stringWithFormat:@"%ld",(long)self.page]];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [ProjectRequestHelper setBalanceListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatWalletRecordModel *model = [YiChatWalletRecordModel mj_objectWithKeyValues:dataDic];
                if (model.data.count > 0 && self.page <= model.pageNo) {
                    for (YiChatWalletRecordListModel *m in model.data) {
                        if (![self.sectionArr containsObject:m.dateDesc]) {
                            [self.sectionArr addObject:m.dateDesc];
                        }
                    }
                    
                    NSMutableArray *centerArr = [NSMutableArray new];
                    if (self.dataArr.count > 0) {
                        NSArray *a = self.dataArr.lastObject;
                        YiChatWalletRecordListModel *m = a.lastObject;
                        YiChatWalletRecordListModel *m2 = model.data.firstObject;
                        if ([m.dateDesc isEqualToString:m2.dateDesc]) {
                            [centerArr addObjectsFromArray:self.dataArr.lastObject];
                            [centerArr addObjectsFromArray:model.data];
                            [self.dataArr removeObjectAtIndex:self.dataArr.count - 1];
                        }
                    }else{
                        [centerArr addObjectsFromArray:model.data];
                    }
                    
                    for (NSString *str in self.sectionArr) {
                        NSMutableArray *arr = [NSMutableArray new];
                        for (YiChatWalletRecordListModel *m in centerArr) {
                            if ([str isEqualToString:m.dateDesc]) {
                                [arr addObject:m];
                            }
                        }
                        if (arr.count > 0) {
                            [self.dataArr addObject:arr];
                        }
                    }
                }
                
                NSInteger num = 0;
                for (NSArray *arr in weakSelf.dataArr) {
                    num = num + arr.count;
                }
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.tableView.mj_footer endRefreshing];
                    if (num == model.count) {
                        weakSelf.isNoData = YES;
                        weakSelf.tableView.mj_footer.hidden = YES;
                    }
                    [self.tableView.mj_header endRefreshing];
                    
                    [self.tableView reloadData];
                });
            }else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}
@end
