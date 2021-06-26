//
//  YiChatChangeSexVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeSexVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatChangeSexCell.h"
#import "ProjectCommonCellModel.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"

@interface YiChatChangeSexVC ()

@property (nonatomic,strong) NSArray *dataArr;

@end

@implementation YiChatChangeSexVC

+ (id)initialVC{
    YiChatChangeSexVC *info = [YiChatChangeSexVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"changeSex") leftItem:nil rightItem:nil];
    return info;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)makeTable{
    NSArray *title = @[@"男",@"女"];
    NSMutableArray *add = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < 2; i ++) {
        ProjectCommonCellModel *cell = [[ProjectCommonCellModel alloc] init];
        cell.titleStr = title[i];
        cell.isSelecte = NO;
        
        if(YiChatUserInfo_Gender == 0 && i == 1){
            cell.isSelecte = YES;
        }
        else if(YiChatUserInfo_Gender == 1 && i == 0){
            cell.isSelecte = YES;
        }
        
        if(cell){
            [add addObject:cell];
        }
    }
    
    if(add){
        _dataArr = add;
    }
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    [tmp addObject:[NSNumber numberWithInteger:_dataArr.count]];
    self.sectionsRowsNumSet = [tmp copy];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y + 10.0, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y - 10.0);
    
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return 50.0;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 0.0001f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YiChatChangeSexCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    static NSString *str = @"YiChatChangeSexCell";
    cell =  [tableView dequeueReusableCellWithIdentifier:str];
    if(!cell){
        cell = [YiChatChangeSexCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES]];
    }
    
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
    
    cell.cellModel = [self getCellModelWithIndex:indexPath];
    
    return cell;
}

- (ProjectCommonCellModel *)getCellModelWithIndex:(NSIndexPath *)indexPath{
    if((self.dataArr.count - 1) >= indexPath.row){
        ProjectCommonCellModel *model = self.dataArr[indexPath.row];
        if([model isKindOfClass:[ProjectCommonCellModel class]]){
            return model;
        }
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
     ProjectCommonCellModel *model = [self getCellModelWithIndex:indexPath];
    if(model.isSelecte != YES){
        for (int i = 0; i <self.dataArr.count; i ++) {
            ProjectCommonCellModel *tmp = [self getCellModelWithIndex:[NSIndexPath indexPathForRow:i inSection:0]];
            tmp.isSelecte = NO;
        }
        model.isSelecte = YES;
    }
    
    [self change];
}


- (void)change{
    NSString *sex = @"1";
    for (int i = 0; i <self.dataArr.count; i ++) {
        ProjectCommonCellModel *tmp = [self getCellModelWithIndex:[NSIndexPath indexPathForRow:i inSection:0]];
        if(i == 1 && tmp.isSelecte){
            sex = @"0";
        }
    }
    
    WS(weakSelf);
    
    NSDictionary *param = [ProjectRequestParameterModel getUpdateUserInfoParamWithUserId:YiChatUserInfo_UserIdStr nick:nil gender:sex avatar:nil appId:nil mobile:nil password:nil];
    [ProjectRequestHelper getUpdateInfoWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:[ProjectUIHelper ProjectUIHelper_getProgressWithText:@""] isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.navigationController popViewControllerAnimated:YES];
                }];
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
    }];
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
