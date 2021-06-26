//
//  YiChatContactMatchVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatContactMatchVC.h"
#import "ServiceGlobalDef.h"

@interface YiChatContactMatchVC ()

@property (nonatomic,strong) NSArray *connectPersonEntityArr;

@end

@implementation YiChatContactMatchVC

+ (id)initialVC{
    
    return [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"matchPhoneContact") leftItem:nil rightItem:nil];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)makeTable{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.connectPersonEntityArr.count; i ++) {
        NSDictionary *dic = self.connectPersonEntityArr[i];
        
        if([dic isKindOfClass:[NSDictionary class]]){
            NSArray *arr = dic[dic.allKeys.lastObject];
            [tmp addObject:[NSNumber numberWithInteger:arr.count]];
        }
    }
    self.tableStyle = 0;
    self.sectionsRowsNumSet = [tmp copy];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y);
    
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
