//
//  YiChatXcxVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/22.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatXcxVC.h"

@interface YiChatXcxVC ()

@end

@implementation YiChatXcxVC

+ (id)initialVCName:(NSString *)name{
    YiChatXcxVC *walletVC = [YiChatXcxVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(name) leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)makeWebView{
    NSString *URL = _url;
    UIWebView *web = [[UIWebView alloc] initWithFrame:TableViewRectMake];
    [self.view addSubview:web];
    web.scalesPageToFit = YES;
    [web loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:URL]]];
    
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self makeWebView];
    // Do any additional setup after loading the view.
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
