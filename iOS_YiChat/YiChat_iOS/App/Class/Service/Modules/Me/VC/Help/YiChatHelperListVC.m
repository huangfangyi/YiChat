//
//  YiChatHelperListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatHelperListVC.h"
#import "YiChatHelpDetailVC.h"
#import "UIButton+BtnCategory.h"
#import "YiChatHelpModel.h"
#import "ServiceGlobalDef.h"
@interface YiChatHelperListVC ()

@end

@implementation YiChatHelperListVC

+ (id)initialVC{
    YiChatHelperListVC *helplist = [YiChatHelperListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"helpList") leftItem:nil rightItem:nil];
    return helplist;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if(_helperList == nil){
        
        _helperList = [YiChatHelpModel createModel];
    }
    
    [self makeUI];
    // Do any additional setup after loading the view.
}

- (void)makeUI{
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    self.scrollFrame = CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, self.view.frame.size.height - PROJECT_SIZE_STATUSH - PROJECT_SIZE_NAVH);
    [self.view addSubview:self.cScroll];
    
    self.sectionNum = _helperList.count;
    
    NSMutableArray *secRows = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.sectionNum; i ++) {
        id obj = _helperList[i];
        if([obj isKindOfClass:[YiChatHelpModel class]]){
            YiChatHelpModel *model = obj;
            [secRows addObject:[NSNumber numberWithInteger:model.contentList.count]];
        }
    }
    self.rowsNumSet = secRows;
    
    self.cScroll.contentSize = CGSizeMake(self.contentSize.width, self.contentSize.height);
    
    [self xySroll_reloadData];
}

- (CGFloat)XYScrollController_CellHWithIndex:(NSIndexPath *)index{
    return PROJECT_SIZE_COMMON_CELLH;
}

- (CGFloat)XYScrollController_SectionHWithIndex:(NSInteger)section{
    return 30.0;
}

- (CGFloat)XYScrollController_FooterHWithIndex:(NSInteger)section{
    return 0;
}

- (void)XYScroll_makeUIForHeaderWithSection:(NSInteger)section{
    
    YiChatHelpModel *model = _helperList[section];
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, [self XYScroll_getHeaderBeginPositionWithHeader:section],self.cScroll.frame.size.width, [self XYScrollController_SectionHWithIndex:section])];
    [self.cScroll addSubview:back];
    back.backgroundColor = [UIColor groupTableViewBackgroundColor];
    
    UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0, self.cScroll.frame.size.width - 20.0, back.frame.size.height)];
    lab.text = model.sectionTitle;
    lab.font = [UIFont systemFontOfSize:13.0];
    lab.textAlignment = NSTextAlignmentLeft;
    [back addSubview:lab];
}

- (void)XYScroll_makeUIForRow:(NSIndexPath *)row{
    YiChatHelpModel *model = _helperList[row.section];
    YiChatHelpContentModel *content =  model.contentList[row.row];
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, [self XYScroll_getRowBeginPositionWithIndex:row], self.cScroll.frame.size.width, [self XYScrollController_CellHWithIndex:row])];
    [self.cScroll addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, back.frame.size.width - 20.0 - 20.0, back.frame.size.height)];
    [back addSubview:lab];
    lab.textAlignment = NSTextAlignmentLeft;
    lab.text = content.contentTitle;
    lab.font = [UIFont systemFontOfSize:14.0];
    
    UIImage *arrow = [UIImage imageNamed:@"setting_next@2x.png"];
    
    CGFloat w = 10.0;
    CGFloat h = 0;
    if(arrow != nil){
        h = w / (arrow.size.width / arrow.size.height);
    }
    
    UIImageView *arrowIcon = [[UIImageView alloc] initWithFrame:CGRectMake(back.frame.size.width - 10.0 - w, lab.frame.size.height / 2 + lab.frame.origin.y - h / 2, w, h)];
    [back addSubview:arrowIcon];
    arrowIcon.image = arrow;
    
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(10, back.frame.size.height - 0.3, back.frame.size.width - 20.0, 0.3)];
    [back addSubview:line];
    line.alpha = 0.3;
    line.backgroundColor = [UIColor blackColor];
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(0, 0, back.frame.size.width, back.frame.size.height);
    [back addSubview:btn];
    
    btn.btnIdentifier = [NSString stringWithFormat:@"%ld%@%ld",row.section,@",",row.row];
    [btn addTarget:self action:@selector(btnMethod:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)btnMethod:(UIButton *)btn{
    NSArray *arrr = [btn.btnIdentifier componentsSeparatedByString:@","];
    if(arrr.count == 2){
        NSInteger sec = [arrr.firstObject integerValue];
        NSInteger row = [arrr.lastObject integerValue];
        
        YiChatHelpModel *model = _helperList[sec];
        
        
        YiChatHelpContentModel *contentModel = model.contentList[row];
        
        YiChatHelpDetailVC *detail = [YiChatHelpDetailVC initialVC];
        detail.contentTitle = contentModel.contentTitle;
        detail.content = contentModel.contentText;
        [self.navigationController pushViewController:detail animated:YES];
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
