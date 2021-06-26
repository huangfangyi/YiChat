//
//  YiChatDiscoverVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDiscoverVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import "YiChatUserManager.h"
#import "YiChatDiscoverCell.h"
@interface YiChatDiscoverVC ()

@property (nonatomic,strong) NSArray *toolCellData;

@end

@implementation YiChatDiscoverVC

+ (id)initialVC{
    YiChatDiscoverVC *discover = [YiChatDiscoverVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_5 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"discoverMain") leftItem:nil rightItem:nil];
    return discover;
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
    [ProjectHelper helper_getGlobalThread:^{
       
        [ProjectHelper helper_getMainThread:^{
            [self.cTable reloadData];
        }];
    }];
}

- (void)loadSystemData{
    
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray *num = [NSMutableArray arrayWithCapacity:0];
    NSArray *iconArr = @[@[@"discover_icon_circle.png"]];
    //,@[@"钱包"],@[@"收藏"],@[@"设置"],@[@"帮助"]
    NSArray *textArr = @[@[@"朋友圈"]];
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
    
    _toolCellData = tool;
    self.sectionsRowsNumSet = [num copy];
}

- (void)makeTable{
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_async(group, queue, ^{
        [self loadSystemData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        
        [self.view addSubview:self.cTable];
        self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - self.cTable.frame.origin.y - PROJECT_SIZE_TABH);
    });
    
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
    YiChatDiscoverCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    static NSString *str = @"YiChatDiscoverCell_Tool";
    cell =  [tableView dequeueReusableCellWithIdentifier:str];
    if(!cell){
        cell = [YiChatDiscoverCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
    }
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:YES] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
    
    [cell updateType:1];

    cell.cellModel = [self getModelWithIndex:indexPath];

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *model = [self getModelWithIndex:indexPath];
    if([model isKindOfClass:[ProjectCommonCellModel class]]){
        if(indexPath.section == 0){
            
            [self pushVCWithName:@"YiChatDynamicVC"];
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
