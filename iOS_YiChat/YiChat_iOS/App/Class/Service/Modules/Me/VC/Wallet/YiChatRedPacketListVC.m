//
//  YiChatRedPacketListVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  我的红包

#import "YiChatRedPacketListVC.h"
#import "YiChatRedPacketListCell.h"
#import "YiChatWalletRecordModel.h"
#import "YiChatRedPacketMoneyView.h"
#import "YiChatRedPacketModel.h"
#import "YiChatRedPacketSendCell.h"
#import "YiChatRedPacketDetailVC.h"

@interface YiChatRedPacketListVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArr;
@property (nonatomic ,assign) NSInteger page;


@property (nonatomic,strong) UILabel *moneyLabel;

@property (nonatomic,strong) UILabel *countLabel;

@property (nonatomic,strong) YiChatRedPacketMoneyView *countView;
@property (nonatomic,strong) YiChatRedPacketMoneyView *luckView;

@property (nonatomic,assign) BOOL isNoData;
@end

@implementation YiChatRedPacketListVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.page = 1;
    self.view.backgroundColor = [UIColor whiteColor];
    self.dataArr = [NSMutableArray new];
    [self setUI];
    [self reloadData];
    [self reloadHeader];
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.rowHeight = 60;
    self.tableView.tableHeaderView = [self setTableHeader];
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(tableViewDidTriggerHeaderRefresh)];
    self.tableView.mj_footer = [MJRefreshAutoNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(tableViewDidTriggerFooterRefresh)];
    [self.view addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 0, 0, 0));
    }];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (self.isSend) {
        YiChatRedPacketSendCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
        if (cell == nil) {
            cell = [[YiChatRedPacketSendCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        cell.model = self.dataArr[indexPath.row];
        return cell;
    }else{
        YiChatRedPacketListCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
        if (cell == nil) {
            cell = [[YiChatRedPacketListCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        cell.model = self.dataArr[indexPath.row];
        return cell;
    }
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (self.isSend) {
        YiChatWalletRecordListModel *model = self.dataArr[indexPath.row];
        BOOL isGroup = NO;
        if ([model.type isEqualToString:@"0"]) {
            isGroup = NO;
        }else{
            isGroup = YES;
        }
        WS(weakSelf);
        [YiChatRedPacketHelper receiveRedPacketDetailPacketID:model.packetId redBlock:^(YiChatRedPacketDetailModel * _Nonnull redPacketModel, NSDictionary * _Nonnull redDic) {
            dispatch_async(dispatch_get_main_queue(), ^{
            YiChatRedPacketDetailVC *vc = [YiChatRedPacketDetailVC initialVC];
            vc.isGroup = isGroup;
            vc.redModel = redPacketModel;
            UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:vc];
            [weakSelf presentViewController:nav animated:YES completion:nil];
            });
        }];
    }
}

-(UIView *)setTableHeader{
    __weak typeof(self) weakSelf = self;
    UIView *bgV = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, 300)];
    UIImageView *avatarImg = [[UIImageView alloc]initWithFrame:CGRectZero];
    avatarImg.layer.masksToBounds = YES;
    avatarImg.layer.cornerRadius = 40;
    [bgV addSubview:avatarImg];
    [avatarImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(80, 80));
    }];
    
    UILabel *nick = [[UILabel alloc]initWithFrame:CGRectZero];
    nick.textAlignment = NSTextAlignmentCenter;
    nick.font = [UIFont systemFontOfSize:18];
    nick.textColor = [UIColor lightGrayColor];
    [bgV addSubview:nick];
    [nick mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(avatarImg.mas_bottom).offset(10);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(30);
    }];
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        NSString *t = @"";
        if (weakSelf.isSend) {
            t = @"共发出";
        }else{
            t = @"共收到";
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [avatarImg sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
            NSMutableAttributedString *nameStr = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"%@%@",model.nick,t]];
            [nameStr addAttribute:NSForegroundColorAttributeName value:[UIColor blackColor] range:NSMakeRange(0, nameStr.length - 3)];
            nick.attributedText = nameStr.copy;
        });
    }];
    
    self.moneyLabel = [[UILabel alloc]initWithFrame:CGRectZero];
    self.moneyLabel.textAlignment = NSTextAlignmentCenter;
    self.moneyLabel.font = [UIFont systemFontOfSize:18];
    self.moneyLabel.textColor = [UIColor lightGrayColor];
    [bgV addSubview:self.moneyLabel];
    [self.moneyLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(nick.mas_bottom).offset(10);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(30);
    }];
    
    if (self.isSend) {
        self.countLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        self.countLabel.textColor = [UIColor lightGrayColor];
        self.countLabel.textAlignment = NSTextAlignmentCenter;
        [bgV addSubview:self.countLabel];
        [self.countLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(weakSelf.moneyLabel.mas_bottom).offset(10);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
    }else{
        self.countView = [[YiChatRedPacketMoneyView alloc] initWithFrame:CGRectZero];
        self.countView.title = @"收到的红包";
        [bgV addSubview:self.countView];
        [self.countView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(weakSelf.moneyLabel.mas_bottom).offset(10);
            make.left.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH / 2, 40));
        }];
        
        self.luckView = [[YiChatRedPacketMoneyView alloc] initWithFrame:CGRectZero];
        self.luckView.title = @"手气最佳";
        [bgV addSubview:self.luckView];
        [self.luckView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(weakSelf.moneyLabel.mas_bottom).offset(10);
            make.right.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH / 2, 40));
        }];
        
    }
    
    return bgV;
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
    WS(weakSelf);
    if (self.isSend) {
        NSDictionary *param = [ProjectRequestParameterModel getSendRedPacketListParametersWithType:@"" pageSize:@"20" pageNo:[NSString stringWithFormat:@"%ld",(long)self.page]];
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        [ProjectRequestHelper sendRedPackageListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    YiChatWalletRecordModel *model = [YiChatWalletRecordModel mj_objectWithKeyValues:dataDic];
                    if (model.data.count > 0 && weakSelf.page <= model.pageNo) {
                        [weakSelf.dataArr addObjectsFromArray:model.data];
                    }
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.tableView.mj_footer endRefreshing];
                        if (weakSelf.dataArr.count == model.count) {
                            weakSelf.isNoData = YES;
                            weakSelf.tableView.mj_footer.hidden = YES;
                        }
                        [weakSelf.tableView.mj_header endRefreshingCompletionBlock];
                        [weakSelf.tableView reloadData];
                    });
                }else if([obj isKindOfClass:[NSString class]]){
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
                [ProjectRequestHelper progressHidden:progress];
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }else{
        NSDictionary *param = [ProjectRequestParameterModel getReceiveRedPacketListParametersWithType:@"" pageSize:@"20" pageNo:[NSString stringWithFormat:@"%ld",(long)self.page]];
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        [ProjectRequestHelper receiveRedPackageListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    YiChatWalletRecordModel *model = [YiChatWalletRecordModel mj_objectWithKeyValues:dataDic];
    
                    if (model.data.count > 0 && weakSelf.page <= model.pageNo) {
                        [weakSelf.dataArr addObjectsFromArray:model.data];
                    }
//                    else{
//                        weakSelf.isNoData = YES;
//                        weakSelf.page = model.pageNo;
//                    }
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.tableView.mj_footer endRefreshing];
                        if (weakSelf.dataArr.count == model.count) {
                            weakSelf.isNoData = YES;
                            weakSelf.tableView.mj_footer.hidden = YES;
                        }
                        [weakSelf.tableView.mj_header endRefreshing];
                        [weakSelf.tableView reloadData];
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
    
}

-(void)reloadHeader{
    if (self.isSend) {
        NSDictionary *param = [ProjectRequestParameterModel getSendRedPacketInfoParametersWithType:@""];
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        [ProjectRequestHelper sendRedPackageInfoWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    YiChatRedPacketModel *model = [YiChatRedPacketModel mj_objectWithKeyValues:dataDic];
                    NSString *moneyStr = [NSString stringWithFormat:@"%.2f元",model.data.money];

                    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:moneyStr];
                    [str addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:NSMakeRange(0, moneyStr.length - 1)];

                    NSMutableAttributedString *sendNumStr = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"发出%ld个",model.data.count]];
                    [sendNumStr addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:NSMakeRange(2, sendNumStr.length - 3)];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        self.moneyLabel.attributedText = str;
                        self.countLabel.attributedText = sendNumStr;
                    });
                }else if([obj isKindOfClass:[NSString class]]){
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
                [ProjectRequestHelper progressHidden:progress];
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
            
        }];
    }else{
        NSDictionary *param = [ProjectRequestParameterModel getReceiveRedPacketInfoParametersWithType:@""];
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        [ProjectRequestHelper receiveRedPackageInfoWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
            
        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSDictionary *dataDic = (NSDictionary *)obj;
                    YiChatRedPacketModel *model = [YiChatRedPacketModel mj_objectWithKeyValues:dataDic];
                    NSString *moneyStr = [NSString stringWithFormat:@"%.2f元",model.data.money];
                    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:moneyStr];
                    [str addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:NSMakeRange(0, moneyStr.length - 1)];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        self.moneyLabel.attributedText = str;
                        self.luckView.count = [NSString stringWithFormat:@"%ld",model.data.luckCount];
                        self.countView.count = [NSString stringWithFormat:@"%ld",model.data.count];
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
