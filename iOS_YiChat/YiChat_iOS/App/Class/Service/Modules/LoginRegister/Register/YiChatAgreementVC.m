//
//  YiChatAgreementVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatAgreementVC.h"

@interface YiChatAgreementVC ()

@end

@implementation YiChatAgreementVC
    
+ (id)initialVCName:(NSString *)name{
    YiChatAgreementVC *agree = [YiChatAgreementVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:name leftItem:nil rightItem:nil];
    return agree;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeWebView];
    // Do any additional setup after loading the view.
}

- (void)makeWebView{
    NSString *URL = _url;
    UIWebView *web = [[UIWebView alloc] initWithFrame:TableViewRectMake];
    [self.view addSubview:web];
    web.scalesPageToFit = YES;
    [web loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:URL]]];
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
