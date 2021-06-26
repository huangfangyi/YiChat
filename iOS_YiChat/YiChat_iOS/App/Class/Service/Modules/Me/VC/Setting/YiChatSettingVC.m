//
//  YiChatSettingVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSettingVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "YiChatSettingMainCell.h"
#import "ProjectClickView.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "YiChatServiceClient.h"
#import <JPUSHService.h>

@interface YiChatSettingVC ()


@property (nonatomic,strong) NSArray *toolCellData;

@property (nonatomic,strong) ProjectClickView *outLogin;

@end

@implementation YiChatSettingVC

+ (id)initialVC{
    YiChatSettingVC *set = [YiChatSettingVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"setting") leftItem:nil rightItem:nil];
    return set;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)loadSystemData{
    
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray *num = [NSMutableArray arrayWithCapacity:0];
    NSArray *textArr = @[@[@"重置密码",@"版本号",@"意见反馈"],@[@"消息免打扰"]];
    for (int i = 0; i < textArr.count; i ++) {
        
        if([textArr[i] isKindOfClass:[NSArray class]]){
            NSArray *value = textArr[i];
            if([value isKindOfClass:[NSArray class]]){
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                
                for (int j = 0; j < value.count; j ++) {
                    ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                    model.titleStr = value[j];
                    
                    if([model.titleStr isEqualToString:@"版本号"]){
                        NSNumber *version = [NSNumber numberWithInteger:[[ProjectHelper helper_getAppVersionCode] integerValue]];
                        model.contentStr = [NSString stringWithFormat:@"V%ld",version.integerValue];
                    }
                    
                    if(model){
                        [arr addObject:model];
                    }
                }
                
                if(arr.count != 0){
                    [tool addObject:arr];
                    [num addObject:[NSNumber numberWithInteger:arr.count]];
                }
            }
        }
    }
    
    _toolCellData = tool;
    self.sectionsRowsNumSet = [num copy];
}

- (ProjectCommonCellModel *)getModelWithIndex:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *dataModel = nil;
    if(indexPath.section <= (_toolCellData.count - 1)){
        NSArray *tmp = _toolCellData[indexPath.section];
        if([tmp isKindOfClass:[NSArray class]]){
            if((tmp.count - 1) >= indexPath.row){
                ProjectCommonCellModel *model = tmp[indexPath.row];
                if(model){
                    dataModel = model;
                }
            }
        }
    }
    return dataModel;
}

- (void)makeTable{
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_async(group, queue, ^{
        [self loadSystemData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        
        [self.view addSubview:self.cTable];
        self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - PROJECT_SIZE_SafeAreaInset.bottom  - self.cTable.frame.origin.y);
        
        UIView *footer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, PROJECT_SIZE_CLICKBTN_H + 30.0)];
        self.cTable.tableFooterView = footer;
        
        [footer addSubview:self.outLogin];
        
        if ([PROJECT_TEXT_APPNAME isEqualToString:@"YiChat"]) {
            UILabel *la = [[UILabel alloc] initWithFrame:CGRectZero];
            la.text = @"掌峰科技 版权所有";
            la.textColor = [UIColor lightGrayColor];
            la.textAlignment = NSTextAlignmentCenter;
            la.font = [UIFont systemFontOfSize:12];
            [self.view addSubview:la];
            [la mas_makeConstraints:^(MASConstraintMaker *make) {
                make.left.right.mas_equalTo(0);
                make.bottom.mas_equalTo(-20);
                make.height.mas_equalTo(20);
            }];
        }
       
    });
    
}


- (ProjectClickView *)outLogin{
    if(!_outLogin){
        WS(weakSelf);
        
        ProjectClickView *outLogin = [ProjectClickView createClickViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 20, self.cTable.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, PROJECT_SIZE_CLICKBTN_H) title:@"退出登录" type:0];
        outLogin.clickInvocation = ^(NSString * _Nonnull identify) {
            [weakSelf loginOutAlert];
        };
        outLogin.userInteractionEnabled = YES;
        _outLogin = outLogin;
    }
    return _outLogin;
}

- (void)loginOutAlert{
    [JPUSHService cleanTags:nil seq:1];
    [JPUSHService deleteAlias:nil seq:1];
    [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"确认退出？" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
        if(row == 0){
            [[YiChatServiceClient defaultChatClient] loginOut];
        }
    }];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 10.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatSettingMainCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
    
    
    if(indexPath.section == 0){
        
        static NSString *str = @"YiChatSettingVC_content";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatSettingMainCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:0];
    }
    else{
        static NSString *str = @"YiChatSettingVC_switch";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatSettingMainCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:1];
        [cell setSwitch];
    }
    
    cell.cellModel = [self getModelWithIndex:indexPath];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
    if([model isKindOfClass:[ProjectCommonCellModel class]]){
        if([model.titleStr isEqualToString:@"重置密码"]){
            NSString *phone = YiChatUserInfo_Mobile;
            
            if(phone && [phone isKindOfClass:[NSString class]]){
                if(phone.length > 0){
                    [self pushVCWithName:@"YiChatResetPasswordVC"];
                    return;
                }
            }
            
            [self pushVCWithName:@"YiChatChangePhoneNumVC"];
        }
        
        if([model.titleStr isEqualToString:@"意见反馈"]){
            [self pushVCWithName:@"YiChatFeedBackVC"];
        }
        
        if([model.titleStr isEqualToString:@"版本号"]){
            YiChatServiceClient *client = [YiChatServiceClient defaultChatClient];
            [client checkVersionUpdateState:^(BOOL state) {
                if (!state) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"没有版本可以更新"];
                }
            }];
        }
    }
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
