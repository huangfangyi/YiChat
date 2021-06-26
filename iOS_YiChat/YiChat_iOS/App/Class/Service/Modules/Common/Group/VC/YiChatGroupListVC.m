//
//  YiChatGroupListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupListVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatServiceClient.h"
#import "YiChatGroupInfoModel.h"
#import "YiChatGroupListCell.h"
#import "NSError+DefaultError.h"
#import "ZFChatUIHelper.h"

@interface YiChatGroupListVC ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong) NSMutableArray *groupListArr;

@end

@implementation YiChatGroupListVC

+ (id)initialVC{
    YiChatGroupListVC *groupList = [YiChatGroupListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"savedGroup") leftItem:nil rightItem:[UIImage imageNamed:@"copy2@3x.png"]];
    return groupList;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _groupListArr = [NSMutableArray arrayWithCapacity:0];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
 
    WS(weakSelf);
    
    [[YiChatUserManager defaultManagaer] fetchUserCreateGroupAuthInvocation:^(BOOL isHasAuth, NSString * _Nonnull des) {
        if(isHasAuth){
            
            [ProjectHelper helper_getMainThread:^{
                UIViewController *vc = [ProjectHelper helper_getVCWithName:@"YiChatCreateGroupVC" initialMethod:@selector(initialVC)];
                if([vc respondsToSelector:@selector(createEndStyle)]){
                    [vc setValue:[NSNumber numberWithInteger:0] forKey:@"createEndStyle"];
                }
                vc.hidesBottomBarWhenPushed = YES;
                if(vc){
                    [weakSelf.navigationController pushViewController:vc animated:YES];
                }
            }];
            
        }
        else{
            UIAlertController *alt = [UIAlertController alertControllerWithTitle:@"提示" message:des preferredStyle:UIAlertControllerStyleAlert];
            
            UIAlertAction *destructive = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDestructive handler:nil];
            [alt addAction:destructive];
            [weakSelf presentViewController:alt animated:YES completion:nil];
        }
    }];
    
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self loadGroupListData];
}

- (void)loadGroupListData{
    
    WS(weakSelf);
    
    [ZFGroupHelper getSelfGroups:^(NSArray * _Nonnull aGroups) {
        
        [ProjectHelper helper_getGlobalThread:^{
            if(aGroups.count){
                
                [weakSelf.groupListArr removeAllObjects];
                
                for (int i = 0; i < aGroups.count; i ++) {
                    NSDictionary *info = [YiChatGroupInfoModel translateObjPropertyToDic:aGroups[i]];
                    
                    if([info isKindOfClass:[NSDictionary class]] && info){
                        YiChatGroupInfoModel *model = [[YiChatGroupInfoModel alloc] initWithGroupListInfoDic:info];
                        if(model && [model isKindOfClass:[YiChatGroupInfoModel class]]){
                            [weakSelf.groupListArr addObject:model];
                            
                            [[YiChatUserManager defaultManagaer] updateGroupInfoWithModel:model invocation:^(BOOL isSuccess) {
                                
                            }];
                        }
                    }
                }
                if(weakSelf.groupListArr){
                    
                    [ProjectHelper helper_getMainThread:^{
                        weakSelf.sectionsRowsNumSet = @[[NSNumber numberWithInteger:weakSelf.groupListArr.count]];
                        [weakSelf.cTable reloadData];
                    }];
                }
            }
        }];
       
        
    } failure:^(NSError * _Nonnull error) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error.localizedDescription];
    }];
}


- (void)makeTable{
     self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:0]];
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH)  - PROJECT_SIZE_SafeAreaInset.bottom );
    self.cTable.backgroundColor = [UIColor whiteColor];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 0.0001f;
}

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section{
    return 30.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_FooterHWithIndex:section])];
    back.backgroundColor = [UIColor whiteColor];
    
    UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 5.0, back.frame.size.width, back.frame.size.height - 5.0) andfont:PROJECT_TEXT_FONT_COMMON(13) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [back addSubview:lab];
    if(self.groupListArr){
        lab.text = [NSString stringWithFormat:@"%ld%@",self.groupListArr.count,@"个群聊"];
    }
    else{
        lab.text = @"暂无群聊";
    }
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YiChatGroupListCell *cell = nil;
    
    static NSString *str = @"YiChatGroupListCell_group";
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    cell = [tableView dequeueReusableCellWithIdentifier:str];
    
    if(!cell){
        cell = [YiChatGroupListCell  initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.cTable.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
    }
    
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:cellH]];
    
    cell.infoModel = [self getUserModelWithIndex:indexPath];
    
    
    return cell;
}

- (YiChatGroupInfoModel *)getUserModelWithIndex:(NSIndexPath *)indexPath{
    if(indexPath && [indexPath isKindOfClass:[NSIndexPath class]]){
        if(indexPath.section == 0){
            NSInteger row = indexPath.row;
            
            if(_groupListArr && [_groupListArr isKindOfClass:[NSArray class]]){
                NSArray *arr = _groupListArr;
                
                if([arr isKindOfClass:[NSArray class]] && arr){
                    if(row <= (arr.count - 1)){
                        id obj = arr[row];
                        if([obj isKindOfClass:[YiChatGroupInfoModel class]] && obj){
                            return obj;
                        }
                    }
                }
            }
         
        }
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if((_groupListArr.count - 1) >= indexPath.row){
        YiChatGroupInfoModel *obj = [self getUserModelWithIndex:indexPath];
        if([obj isKindOfClass:[YiChatGroupInfoModel class]] && obj){
            
            UIViewController *chat = [ZFChatUIHelper getGroupChatVCWithGroupModel:obj];
            
            chat.hidesBottomBarWhenPushed = YES;
            if(chat){
                [self.navigationController pushViewController:chat animated:YES];
            }
           
            
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
