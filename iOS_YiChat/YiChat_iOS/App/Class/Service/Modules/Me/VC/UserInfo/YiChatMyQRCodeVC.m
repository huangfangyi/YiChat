//
//  YiChatMyQRCodeVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatMyQRCodeVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatQrcodeView.h"

@interface YiChatMyQRCodeVC ()

@property (nonatomic,strong) YiChatQrcodeView *visitCard;


@end

@implementation YiChatMyQRCodeVC

+ (id)initialVC{
    YiChatMyQRCodeVC *qrcode = [YiChatMyQRCodeVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"myQRCode") leftItem:nil rightItem:[UIImage imageNamed:@"more@3x.png"]];
    return qrcode;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view addSubview:self.visitCard];
    // Do any additional setup after loading the view.
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    
}

- (YiChatQrcodeView *)visitCard{
    if(!_visitCard){
        
        CGFloat h = self.view.frame.size.height / 3 * 2;
        CGFloat downH = (self.view.frame.size.height - h - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH) / 3 * 2;
        
        _visitCard = [[YiChatQrcodeView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.view.frame.size.height - downH - h, self.view.frame.size.width -  PROJECT_SIZE_NAV_BLANK * 2, h)];
        _visitCard.backgroundColor = [UIColor whiteColor];
        _visitCard.layer.cornerRadius = 10.0;
        _visitCard.clipsToBounds = YES;
    }
    return _visitCard;
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
