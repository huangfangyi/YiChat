//
//  ProjectBaseTableView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseTableView.h"
#import "ServiceGlobalDef.h"

@interface ProjectBaseTableView ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation ProjectBaseTableView

- (UITableView *)cTable{
    if(!_cTable){
        
        UITableViewStyle style;
        if(_tableStyle == 0){
            style = UITableViewStylePlain;
        }
        else{
            style = UITableViewStyleGrouped;
        }
        
        _cTable = [[UITableView alloc] initWithFrame:CGRectMake(0,0, self.frame.size.width, self.frame.size.height) style:style];
        if (@available(iOS 11.0, *)) {
            _cTable.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
        } else {
            // Fallback on earlier versions
        }
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
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
