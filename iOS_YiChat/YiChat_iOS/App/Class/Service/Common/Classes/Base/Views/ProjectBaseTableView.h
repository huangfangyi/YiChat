//
//  ProjectBaseTableView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectBaseTableView : UIView

@property (nonatomic,strong) UITableView *cTable;
@property (nonatomic,strong) NSArray <NSNumber *>*sectionsRowsNumSet;
@property (nonatomic,assign) NSInteger tableStyle;


- (id)projectTableViewController_getRefreshHeaderWithTarget:(id)target method:(SEL)method;

- (id)projectTableViewController_getRefreshFooterWithTarget:(id)target method:(SEL)method;

- (void)projectTableViewController_SuiteMj;

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index;

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section;

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section;

- (UITableView *)cTable;

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView;

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section;

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath;

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section;

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section;

@end

NS_ASSUME_NONNULL_END
