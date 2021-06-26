//
//  YiChatRedPacketDetailVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketDetailVC.h"
#import "YiChatRedPacketDetailCell.h"
#import "YiChatRedPacketDetailModel.h"

@interface YiChatRedPacketDetailVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSArray *dataArr;

@property (nonatomic,strong) UIImageView *avterIm;
@property (nonatomic,strong) UILabel *nickNameLa;
@property (nonatomic,strong) UILabel *contentLa;
@property (nonatomic,strong) UILabel *moneyLa;

@property (nonatomic,strong) YiChatRedPacketListModel *redPacketModel;
@end

@implementation YiChatRedPacketDetailVC

+ (id)initialVC{
    YiChatRedPacketDetailVC *walletVC = [YiChatRedPacketDetailVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"RedPacketDetail") leftItem:[UIImage imageNamed:@"back"] rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)setNavBar{
    UIView *back = [self navBarGetNavBar];
    back.backgroundColor =[UIColor clearColor];
    
    [self.view bringSubviewToFront:back];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTranslucent:YES];
    self.redPacketModel = self.redModel.data;
    self.dataArr = self.redPacketModel.list;
    [self setUI];
    [self setNavBar];
}

-(void)setUI{
    self.tableStyle = 1;
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, 0, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT  - PROJECT_SIZE_SafeAreaInset.bottom);
    self.cTable.backgroundColor = [UIColor whiteColor];
    self.cTable.tableHeaderView = [self setHeaderView];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return 65;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatRedPacketDetailCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatRedPacketDetailCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    YiChatRedPacketInfoModel *model = self.dataArr[indexPath.row];
    if (self.isGroup) {
        cell.isLuck = model.maxStatus;
    }else{
        cell.isLuck = NO;
    }
    cell.model = model;
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 30;
}

-(CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return CGFLOAT_MIN;
}

-(UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    return [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, CGFLOAT_MIN)];
}

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *bgView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 30)];
    UILabel *la = [[UILabel alloc]initWithFrame:CGRectMake(15, 0, self.view.frame.size.width - 30, 30)];
    la.font = [UIFont systemFontOfSize:14];
    la.textColor = [UIColor grayColor];
    if (self.isGroup) {
        la.text = [NSString stringWithFormat:@"一共%@个红包,共%@元,已领取%@个。",self.redPacketModel.num,self.redPacketModel.money,self.redPacketModel.receiveNum];
    }else{
        la.text = @"";
        if (self.redPacketModel.status == 0){
            la.text = [NSString stringWithFormat:@"红包共%@元,等待对方领取。",self.redPacketModel.money];;
        }
    }
    [bgView addSubview:la];
    return bgView;
    
}

-(UIView *)setHeaderView{
    CGFloat avter_H = 64.0;
    UIView *bgView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 225 + PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)];
    bgView.backgroundColor = [UIColor whiteColor];
    UIView *redV = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)];
    redV.backgroundColor = [UIColor colorWithRed:229/255.0 green:65/255.0 blue:65/255.0 alpha:1];
    [bgView addSubview:redV];
    UIImageView *im = [[UIImageView alloc]initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.view.frame.size.width, 64)];
    im.image = [UIImage imageNamed:@"RedPacket_header"];
    [bgView addSubview:im];
    
    self.avterIm = [[UIImageView alloc]initWithFrame:CGRectZero];
    self.avterIm.layer.masksToBounds = YES;
    self.avterIm.layer.cornerRadius = avter_H / 2;
    [self.avterIm sd_setImageWithURL:[NSURL URLWithString:self.redPacketModel.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    [bgView addSubview:self.avterIm];
    [self.avterIm mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.top.equalTo(im.mas_top).offset(avter_H / 2);
        make.size.mas_equalTo(CGSizeMake(avter_H, avter_H));
    }];
    
    self.nickNameLa = [[UILabel alloc] initWithFrame:CGRectZero];
    self.nickNameLa.text = self.redPacketModel.nick;
    self.nickNameLa.textAlignment = NSTextAlignmentCenter;
    [bgView addSubview:self.nickNameLa];
    [self.nickNameLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.top.equalTo(self.avterIm.mas_bottom).offset(10);
        make.size.mas_equalTo(CGSizeMake(self.view.frame.size.width - 20, 20));
    }];
    
    self.contentLa = [[UILabel alloc] initWithFrame:CGRectZero];
    self.contentLa.text = self.redPacketModel.content;
    self.contentLa.textAlignment = NSTextAlignmentCenter;
    self.contentLa.textColor = [UIColor grayColor];
    self.contentLa.alpha = 0.6;
    self.contentLa.font = [UIFont systemFontOfSize:15];
    [bgView addSubview:self.contentLa];
    [self.contentLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.top.equalTo(self.nickNameLa.mas_bottom).offset(10);
        make.size.mas_equalTo(CGSizeMake(self.view.frame.size.width - 20, 20));
    }];
    
    self.moneyLa = [[UILabel alloc] initWithFrame:CGRectZero];
    self.moneyLa.textAlignment = NSTextAlignmentCenter;
    [bgView addSubview:self.moneyLa];
    [self.moneyLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.bottom.mas_equalTo(-10);
        make.size.mas_equalTo(CGSizeMake(self.view.frame.size.width - 20, 30));
    }];
    
    //    0已创建 1有领取 2领取完 3已超时
    if (self.redPacketModel.status == 3) {
        self.moneyLa.text = self.isGroup? @"红包已过期" : @"红包已过期，已退回";
    }else{
        if (self.isGroup) {
            if (self.redPacketModel.status == 2) {
                if (self.redPacketModel.receiveMoney == 0.0) {
                    self.moneyLa.text = @"手速慢了，红包已经抢完了";
                }else{
                    self.moneyLa.attributedText = [self getreceiveMoney:self.redPacketModel.receiveMoney];
                }
            }
            else{
                if (self.redPacketModel.receiveMoney) {
                    self.moneyLa.attributedText = [self getreceiveMoney:self.redPacketModel.receiveMoney];
                }else{
                    
                }
            }
        }else{
            if (self.redPacketModel.status == 2) {
                if ([self.redPacketModel.userId isEqualToString:YiChatUserInfo_UserIdStr]) {
                    self.moneyLa.text = @"对方已领取";
                }else{
                    self.moneyLa.attributedText = [self getreceiveMoney:self.redPacketModel.receiveMoney];
                }
            }else{
                if (self.redPacketModel.receiveMoney) {
                    self.moneyLa.attributedText = [self getreceiveMoney:self.redPacketModel.receiveMoney];
                }
            }
        }
    }
    return bgView;
}

-(void)refreshTableHeader{
    [self.avterIm sd_setImageWithURL:[NSURL URLWithString:self.redPacketModel.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    self.nickNameLa.text = self.redPacketModel.nick;
    self.contentLa.text = self.redPacketModel.content;
//    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"%.2f元",self.redPacketModel.receiveMoney]];
//    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:40] range:NSMakeRange(0, str.length - 1)];
    self.moneyLa.attributedText = [self getreceiveMoney:self.redPacketModel.receiveMoney];;
}

#pragma 返回按钮点击方法
-(void)dismissAction{
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(NSMutableAttributedString *)getreceiveMoney:(CGFloat)receiveMoney{
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"%.2f元",receiveMoney]];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:40] range:NSMakeRange(0, str.length - 1)];
    return str;
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
