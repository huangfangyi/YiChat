//
//  YiChatMsgInfoVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/2.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  聊天记录详情

#import "YiChatMsgInfoVC.h"
#import "ProjectSearchMsgCell.h"
@interface YiChatMsgInfoVC ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation YiChatMsgInfoVC

+ (id)initialVC{
    YiChatMsgInfoVC *conversation = [YiChatMsgInfoVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"messageInfo") leftItem:nil rightItem:nil];
    conversation.hidesBottomBarWhenPushed = YES;
    return conversation;
}



- (void)viewDidLoad {
    [super viewDidLoad];
    [self makeTable];
}

- (void)makeTable{
    self.tableStyle = 1;
    self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:self.dataArr.count]];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH) - PROJECT_SIZE_SafeAreaInset.bottom);
    self.cTable.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    [self.view addSubview:self.cTable];
    
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_CONVERSATION_CELLH;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectSearchMsgCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[ProjectSearchMsgCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.message = self.dataArr[indexPath.row];
    return cell;
    
}
@end
