//
//  YiChatAdvertisementMain.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/10/17.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatAdvertisementMain.h"

@interface YiChatAdvertisementMain ()

@property (nonatomic,strong) UIWebView *web;

@property (nonatomic,assign) BOOL isLoadSuccess;

@end

@implementation YiChatAdvertisementMain

+ (id)initialVC{
    return  [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_5 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"advertisementMain") leftItem:nil rightItem:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeWebView];
    
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if(!_isLoadSuccess){
        [self loadData];
    }
    
}

- (void)makeWebView{
    UIWebView *web = [[UIWebView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + PROJECT_SIZE_TABH) - PROJECT_SIZE_SafeAreaInset.bottom)];
    [self.view addSubview:web];
    web.scalesPageToFit = YES;
    _web = web;
}

- (void)loadData{
    NSDictionary *token = [ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token];
    
    [ProjectRequestHelper advertisementMainWithParameters:@{} headerParameters:token progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj){
                if([obj isKindOfClass:[NSDictionary class]]){
                    NSString *url = obj[@"data"];
                    if(url && [url isKindOfClass:[NSString class]]){
                        [ProjectHelper helper_getMainThread:^{
                            _isLoadSuccess = YES;
                            [self loadUrl:url];
                        }];
                    }
                }
                else if([obj isKindOfClass:[NSString class]]){
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (void)loadUrl:(NSString *)url{
     [_web loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
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
