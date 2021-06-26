//
//  YiChatSearchGroupMsgVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/11/21.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSearchGroupMsgVC.h"
#import "ProjectSearchMsgCell.h"
#import "ZFChatManage.h"
#import "ZFChatVC.h"
@interface YiChatSearchGroupMsgVC ()<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArray;

@end

@implementation YiChatSearchGroupMsgVC

+ (id)initialVC{
    YiChatSearchGroupMsgVC *walletVC = [YiChatSearchGroupMsgVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"查询群聊天消息") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.dataArray = [NSMutableArray new];
    [self setUI];
    
    // Do any additional setup after loading the view.
}

-(void)searchContent:(NSString *)content{
    WS(weakSelf);
    [self.dataArray removeAllObjects];
    NSMutableArray *array = [NSMutableArray new];
    if ([self.chatType isEqualToString:@"2"]) {
        [[HTClient sharedInstance].messageManager getSingleChatMessagesWithContent:content from:nil to:self.chatId timestamp:-1 completion:^(NSArray<HTMessage *> *resArr) {
            [array addObjectsFromArray:resArr];
            for (HTMessage *msg in array) {
                if ([msg.chatType isEqualToString:weakSelf.chatType]) {
                    
                    NSDictionary *ext = msg.ext;
                    NSString *action = [NSString stringWithFormat:@"%@",ext[@"action"]];
                    if ([action isEqualToString:@"6000"] || [action isEqualToString:@"6001"]) {
                        
                    }else{
                        if ([msg.to isEqualToString:weakSelf.chatId] || [msg.from isEqualToString:weakSelf.chatId]) {
                            [weakSelf.dataArray addObject:msg];
                        }
                    }
                }
            }

            if (weakSelf.dataArray.count == 0) {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"本地没有查到相关记录"];
            }else{
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.tableView reloadData];
                }];
            }
        }];
    }else{
        YiChatUserModel *user = [YiChatUserModel mj_objectWithKeyValues:[[YiChatUserManager defaultManagaer] getCashUserDicInfo]];
        [[HTClient sharedInstance].messageManager getSingleChatMessagesWithContent:content from:[NSString stringWithFormat:@"%ld",user.userId] to:nil timestamp:-1 completion:^(NSArray<HTMessage *> *resArr) {
            [array addObjectsFromArray:resArr];
            [[HTClient sharedInstance].messageManager getSingleChatMessagesWithContent:content from:nil to:[NSString stringWithFormat:@"%ld",user.userId] timestamp:-1 completion:^(NSArray<HTMessage *> *resArr) {
                [array addObjectsFromArray:resArr];
                for (HTMessage *msg in array) {
                    if ([msg.chatType isEqualToString:weakSelf.chatType]) {
                        
                        NSDictionary *ext = msg.ext;
                        NSString *action = [NSString stringWithFormat:@"%@",ext[@"action"]];
                        if ([action isEqualToString:@"6000"] || [action isEqualToString:@"6001"]) {
                            
                        }else{
                            if ([msg.to isEqualToString:weakSelf.chatId] || [msg.from isEqualToString:weakSelf.chatId]) {
                                [weakSelf.dataArray addObject:msg];
                            }
                        }
                    }
                }

                if (weakSelf.dataArray.count == 0) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"本地没有查到相关记录"];
                }else{
                    [ProjectHelper helper_getMainThread:^{
                        [weakSelf.tableView reloadData];
                    }];
                }
                
            }];
        }];
    }
}

-(void)setUI{
    UISearchBar *bar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, 40)];
    bar.placeholder = @"请输入需要查询的内容";
    bar.delegate = self;
    bar.showsCancelButton = YES;
    UIButton *cancelBtn = [bar valueForKeyPath:@"cancelButton"];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    cancelBtn.enabled = YES;
    [self.view addSubview:bar];
    
    self.tableView = [[UITableView alloc]initWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 50, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH) - PROJECT_SIZE_SafeAreaInset.bottom - 50) style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.backgroundColor = [UIColor groupTableViewBackgroundColor];
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    self.tableView.rowHeight = 90;
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    [self.view addSubview:self.tableView];
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    [self searchContent:searchBar.text];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [self.view endEditing:YES];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArray.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectSearchMsgCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[ProjectSearchMsgCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.message = self.dataArray[indexPath.row];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    HTMessage *msg = self.dataArray[indexPath.row];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"SearchLocal_msg" object:nil userInfo:@{@"data":msg}];
    
    for (UIViewController *vc in self.navigationController.viewControllers) {
        if ([vc isKindOfClass:[ZFChatVC class]]) {
            [self.navigationController popToViewController:vc animated:YES];
        }
    }
}

-(void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"SearchLocal_msg" object:nil];
    
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

