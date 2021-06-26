//
//  ProjectTableVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"
#import "ServiceGlobalDef.h"
@interface ProjectTableVC ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation ProjectTableVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (UITableView *)cTable{
    if(!_cTable){
        
        UITableViewStyle style;
        if(_tableStyle == 0){
            style = UITableViewStylePlain;
        }
        else{
            style = UITableViewStyleGrouped;
        }
        
        _cTable = [[UITableView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH) - PROJECT_SIZE_SafeAreaInset.bottom) style:style];
        if (@available(iOS 11.0, *)) {
            _cTable.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
        } else {
            // Fallback on earlier versions
            self.automaticallyAdjustsScrollViewInsets = NO;
        }
        _cTable.estimatedRowHeight = 0;
        _cTable.estimatedSectionFooterHeight = 0;
        _cTable.estimatedSectionHeaderHeight = 0;
        _cTable.separatorStyle = UITableViewCellSeparatorStyleNone;
        _cTable.delegate = self;
        _cTable.dataSource = self;
        _cTable.backgroundColor = [UIColor groupTableViewBackgroundColor];
    }
    return _cTable;
}

#pragma mark delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    if([self.sectionsRowsNumSet isKindOfClass:[NSArray class]]){
        return self.sectionsRowsNumSet.count;
    }
    else{
        return 0;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if(self.sectionsRowsNumSet.count - 1 >= section){
        return self.sectionsRowsNumSet[section].integerValue;
    }
    else{
        return 0;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return [self projectTableViewController_CellHWithIndex:indexPath];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return [self projectTableViewController_SectionHWithIndex:section];
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return [self projectTableViewController_FooterHWithIndex:section];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return 0.00001f;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 0.00001f;
}

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section{
    return 0.00001f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    return [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, tableView.frame.size.width, [self projectTableViewController_SectionHWithIndex:section]) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    return [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, tableView.frame.size.width, [self projectTableViewController_FooterHWithIndex:section]) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
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
