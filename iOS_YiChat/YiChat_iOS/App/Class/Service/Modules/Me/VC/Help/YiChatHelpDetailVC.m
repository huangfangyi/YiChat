//
//  YiChatHelpDetailVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatHelpDetailVC.h"
#import "ServiceGlobalDef.h"
@interface YiChatHelpDetailVC ()

@end

@implementation YiChatHelpDetailVC

+ (id)initialVC{
    YiChatHelpDetailVC *detail = [YiChatHelpDetailVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"helpDetail") leftItem:nil rightItem:nil];
    return detail;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeUI];
    // Do any additional setup after loading the view.
}

- (void)makeUI{
    self.scrollFrame = CGRectMake(0, 10 + PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, self.view.frame.size.height - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH - 10.0);
    [self.view addSubview:self.cScroll];
    
    self.sectionNum = 1;
    
    NSMutableArray *secRows = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.sectionNum; i ++) {
        [secRows addObject:[NSNumber numberWithInteger:1]];
    }
    
    self.rowsNumSet = secRows;
    
    self.cScroll.contentSize = self.contentSize;
    
    self.cScroll.backgroundColor = [UIColor whiteColor];
    
    [self xySroll_reloadData];
}

- (CGFloat)XYScrollController_CellHWithIndex:(NSIndexPath *)index{
    return self.view.frame.size.height - 64.0;
}

- (CGFloat)XYScrollController_SectionHWithIndex:(NSInteger)section{
    return 64.0;
}

- (CGFloat)XYScrollController_FooterHWithIndex:(NSInteger)section{
    return 0;
}

- (void)XYScroll_makeUIForHeaderWithSection:(NSInteger)section{
    
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(10, [self XYScroll_getHeaderBeginPositionWithHeader:section],self.cScroll.frame.size.width - 20.0, [self XYScrollController_SectionHWithIndex:section])];
    [self.cScroll addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    UIFont *font = [UIFont boldSystemFontOfSize:18.0];
    
    
    UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 10, back.frame.size.width - 20.0, back.frame.size.height - 20.0)];
    lab.text = self.contentTitle;
    lab.font = font;
    lab.numberOfLines = 0;
    lab.textAlignment = NSTextAlignmentLeft;
    [back addSubview:lab];
    
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(10, back.frame.size.height - 0.3, back.frame.size.width - 20.0, 0.3)];
    [back addSubview:line];
    line.alpha = 0.3;
    line.backgroundColor = [UIColor blackColor];
}

- (void)XYScroll_makeUIForRow:(NSIndexPath *)row{
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(10, [self XYScroll_getRowBeginPositionWithIndex:row], self.cScroll.frame.size.width - 20.0, [self XYScrollController_CellHWithIndex:row])];
    [self.cScroll addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    UIFont *font = [UIFont systemFontOfSize:16.0];
    CGFloat w = back.frame.size.width - 20.0;
    CGFloat h = back.frame.size.height - 40.0;
    
    NSDictionary *dic=@{NSFontAttributeName:font};
    CGRect rect=[self.content boundingRectWithSize:CGSizeMake(w, h) options:NSStringDrawingUsesLineFragmentOrigin attributes:dic context:nil];
    
    UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(10, 30.0, rect.size.width, rect.size.height)];
    [back addSubview:lab];
    lab.numberOfLines = 0;
    lab.textAlignment = NSTextAlignmentLeft;
    lab.text = self.content;
    lab.font = font;
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
