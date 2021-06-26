//
//  YiChatPersonalVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPersonalVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "YiChatPersonalCell.h"
#import "XYQRCodeScan.h"
#import "YiChatPersonalInfoVC.h"
#import "YiChatUserManager.h"
#import "YiChatDynamicVC.h"
#import "YiChatWalletVC.h"
#import "YiChatMyQRCodeVC.h"
#import <CommonCrypto/CommonDigest.h>
#include <fcntl.h>
#include <unistd.h>
#define YiChatPersonalVC_PersonInfoCellH 80.0f
#define YiChatPersonalVC_CommonCellH PROJECT_SIZE_COMMON_CELLH
@interface YiChatPersonalVC ()

@property (nonatomic,strong) NSArray *toolCellData;

@end

@implementation YiChatPersonalVC

+ (id)initialVC{
    YiChatPersonalVC *me = [YiChatPersonalVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_5 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"meMain") leftItem:nil rightItem:nil];
    return me;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self loadUserData];
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

- (void)loadUserData{
    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull modelUser, NSString * _Nonnull error) {
        [ProjectHelper helper_getGlobalThread:^{
            if(modelUser && [modelUser isKindOfClass:[YiChatUserModel class]]){
                ProjectCommonCellModel *model = [self getModelWithIndex:[NSIndexPath indexPathForRow:0 inSection:0]];
                model.iconUrl = [modelUser userIcon];
                model.titleStr = [modelUser appearName];
                if([YiChatUserInfo_AppId isKindOfClass:[NSString class]])
                {
                    model.contentStr = [NSString stringWithFormat:@"%@号:%@",PROJECT_TEXT_APPNAME,YiChatUserInfo_AppId];
                }
                else{
                    model.contentStr = [NSString stringWithFormat:@"%@号:",PROJECT_TEXT_APPNAME];
                }
                [ProjectHelper helper_getMainThread:^{
                    [self.cTable reloadData];
                }];
            }
           
        }];
    }];
}

- (void)InitialOlderWalletWithState{
    
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray *num = [NSMutableArray arrayWithCapacity:0];
    NSArray *iconArr = nil;
    NSArray *textArr = nil;
    
    if(YiChatProject_IsNeedRedPackge){
        iconArr = @[@[@"icon_scan.png"],@[@"me_albumn.png"],@[@"me_dynamic.png"],@[@"me__change.png"],@[@"me_setting.png"]];
        textArr = @[@[@"扫一扫"],@[@"我的相册"],@[@"好友圈"],@[@"钱包"],@[@"设置"]];
    }
    else{
        iconArr = @[@[@"icon_scan.png"],@[@"me_albumn.png"],@[@"me_dynamic.png"],@[@"me_setting.png"]];
        textArr = @[@[@"扫一扫"],@[@"我的相册"],@[@"好友圈"],@[@"设置"]];
    }
    
    for (int i = 0; i < textArr.count; i ++) {
        
        if([textArr[i] isKindOfClass:[NSArray class]]){
            NSArray *value = textArr[i];
            if([value isKindOfClass:[NSArray class]]){
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                
                for (int j = 0; j < value.count; j ++) {
                    ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                    model.titleStr = value[j];
                    if((iconArr.count - 1) >= i ){
                        NSArray *iconValue = iconArr[i];
                        if([iconValue isKindOfClass:[NSArray class]]){
                            if((iconValue.count - 1) >= j){
                                model.iconUrl = iconValue[j];
                            }
                        }
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
    ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
    
    [tool insertObject:@[model] atIndex:0];
    [num insertObject:[NSNumber numberWithInteger:1] atIndex:0];
    
    _toolCellData = tool;
    self.sectionsRowsNumSet = [num copy];
}

- (void)makeTable{
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_async(group, queue, ^{
        [self InitialOlderWalletWithState];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        
        [self.view addSubview:self.cTable];
        self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - self.cTable.frame.origin.y - PROJECT_SIZE_TABH);
    });

}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    if(index.section == 0){
        return YiChatPersonalVC_PersonInfoCellH;
    }
    else{
        return YiChatPersonalVC_CommonCellH;
    }
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
    YiChatPersonalCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    WS(weakSelf);
    if(indexPath.section == 0){
        
        static NSString *str = @"YiChatPersonal_UserInfo";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPersonalCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:YES] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:0];
        
        cell.YiChatPersonalCellClickQrcode = ^{
            YiChatMyQRCodeVC *qrcode = [YiChatMyQRCodeVC initialVC];
            qrcode.hidesBottomBarWhenPushed = YES;
            [weakSelf.navigationController pushViewController:qrcode animated:YES];
            
        };
    }
    else{
        static NSString *str = @"YiChatPersonal_Tool";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPersonalCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:YES] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:1];
    }
    
    cell.cellModel = [self getModelWithIndex:indexPath];
    
    return cell;
}

- (NSString *)md5StringForString:(NSString *)string {
    const char *str = [string UTF8String];
    unsigned char r[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, (uint32_t)strlen(str), r);
    return [NSString stringWithFormat:@"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9], r[10], r[11], r[12], r[13], r[14], r[15]];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
    if([model isKindOfClass:[ProjectCommonCellModel class]]){
        if([model.titleStr isEqualToString:@"扫一扫"]){
            //扫一扫
            XYQRCodeScan *scan = [[XYQRCodeScan alloc] init];
            scan.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:scan animated:YES];
        }
        else if([model.titleStr isEqualToString:@"钱包"]){
            [self pushVCWithName:@"YiChatWalletVC"];
        }
        else if([model.titleStr isEqualToString:@"我的相册"]){
            
            YiChatDynamicVC *vc = [YiChatDynamicVC initialVC];
            vc.hidesBottomBarWhenPushed = YES;
            vc.userId = YiChatUserInfo_UserIdStr;
            [self.navigationController pushViewController:vc animated:YES];
        }
        else if([model.titleStr isEqualToString:@"好友圈"]){
            
            YiChatDynamicVC *vc = [YiChatDynamicVC initialVC];
            vc.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:vc animated:YES];
        }else if([model.titleStr isEqualToString:@"收藏"]){
            
            [self pushVCWithName:@"YiChatCollectionListVC"];
        }
        else if([model.titleStr isEqualToString:@"设置"]){
            [self pushVCWithName:@"YiChatSettingVC"];
        }
        else if([model.titleStr isEqualToString:@"帮助"]){
            
            [self pushVCWithName:@"YiChatHelperListVC"];
        }
        else if(indexPath.section == 0){
            
            [self pushVCWithName:@"YiChatPersonalInfoVC"];
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
