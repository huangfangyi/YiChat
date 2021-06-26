//
//  YiChatQRCodeScanVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/1.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatQRCodeScanVC.h"
#import "ServiceGlobalDef.h"
@interface YiChatQRCodeScanVC ()

@property (nonatomic,strong) NSString *url;
@property (nonatomic,strong) UIWebView *web;
@property (nonatomic,strong) UITextView *text;
    
@property (nonatomic,strong) UIButton *webLogin;

@end

@implementation YiChatQRCodeScanVC

+ (id)initialVC{
    YiChatQRCodeScanVC *qrcode = [YiChatQRCodeScanVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"scanComplete") leftItem:nil rightItem:nil];
    return qrcode;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    BOOL isLoadWeb = NO;
    if(_decodeScanString && [_decodeScanString isKindOfClass:[NSString class]]){
        if(_decodeScanString.length > 0){
            if([_decodeScanString hasPrefix:@"http"]){
                
                NSString *url = [NSString stringWithFormat:@"%@%@",YiChatProject_NetWork_BaseUrl,@"/api/web/api/webLogin"];
                
                if([_decodeScanString containsString:url]){
                    //web 二维码
                    
                    NSArray * arr = [_decodeScanString componentsSeparatedByString:YiChatProject_NetWork_BaseUrl];
                    if(arr.count > 0){
                        _url = arr.lastObject;
                        
                        [self makeScanWebQRCodeUI];
                    }
                   
                    return;
                }
                
                _url = _decodeScanString;
                
                [self makeWebView];
                isLoadWeb = YES;
            }
        }
    }
    
    if(isLoadWeb == NO){
        [self makeTextView];
    }
    // Do any additional setup after loading the view.
}
    
- (void)makeScanWebQRCodeUI{
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_WIDTH / 2 - 120.0 / 2, PROJECT_SIZE_HEIGHT - 40.0 - 100.0, 120.0, 40.0)];
    [self.view addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    UIButton *webLogin = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    webLogin.frame = back.bounds;
    [webLogin setTitle:@"登录" forState:UIControlStateNormal];
    [webLogin setTitleColor:PROJECT_COLOR_APPMAINCOLOR forState:UIControlStateNormal];
    [back addSubview:webLogin];
    webLogin.tintColor = [UIColor clearColor];
    [webLogin addTarget:self action:@selector(webLoginMethod) forControlEvents:UIControlEventTouchUpInside];
    _webLogin = webLogin;
}

- (void)webLoginMethod{
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@"登录中"];
    
    NSDictionary *token = [ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token];
    
    [ProjectRequestHelper webLoginWithInterface:_url headerParameters:token progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        if([progress respondsToSelector:@selector(hidden)]){
            [progress performSelector:@selector(hidden)];
        }
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if(obj && [obj isKindOfClass:[NSDictionary class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"登录成功"];
                [ProjectHelper helper_getMainThread:^{
                    [self.navigationController popViewControllerAnimated:YES];
                }];
            }
            else if(obj && [obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"网路故障"];
            }
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
        if([progress respondsToSelector:@selector(hidden)]){
            [progress performSelector:@selector(hidden)];
        }
    }];
    
}
    
- (void)webOpen{
    if(_url){
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:_url]];
    }
}

- (void)makeWebView{
    NSString *URL = _url;
    
    UIWebView *web = [[UIWebView alloc] initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH)];
    [self.view addSubview:web];
    web.scalesPageToFit = YES;
    [web loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:URL]]];
    _web = web;
    
}

- (void)makeTextView{
    _text = [ProjectHelper helper_factoryMakeTextViewWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH) fontSize:PROJECT_TEXT_FONT_COMMON(18) keybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
    _text.editable = NO;
    _text.scrollEnabled = YES;
    _text.text = _decodeScanString;
    [self.view addSubview:_text];
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
