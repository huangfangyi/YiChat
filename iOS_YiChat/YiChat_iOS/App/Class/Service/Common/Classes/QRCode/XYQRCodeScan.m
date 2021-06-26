//
//  XYQRCodeScan.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/20.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "XYQRCodeScan.h"
#import "XYQRCodeScanView.h"
#import "XYQRCodeTool.h"
#import "ServiceGlobalDef.h"
#import "YiChatUserManager.h"
#import "YiChatQRCodeScanVC.h"
#import "YiChatFriendInfoVC.h"

#define iPhoneX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)
#define StatusBarAndNavigationBarHeight (iPhoneX ? 88.f : 64.f)

@interface XYQRCodeScan ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (nonatomic, strong)  XYQRCodeTool * scanTool;
@property (nonatomic, strong)  XYQRCodeScanView * scanView;

@end

@implementation XYQRCodeScan

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = @"二维码/条码";
    
    //输出流视图
    UIView *preview  = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height - 0)];
    [self.view addSubview:preview];
    
    __weak typeof(self) weakSelf = self;
    
    //构建扫描样式视图
    _scanView = [[XYQRCodeScanView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height - 0)];
    
    CGFloat blank = PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH;
    
    _scanView.scanRetangleRect = CGRectMake(blank, blank * 2, (self.view.frame.size.width - 2 * blank),  (self.view.frame.size.width - 2 * blank));
    _scanView.colorAngle = [UIColor greenColor];
    _scanView.photoframeAngleW = 20;
    _scanView.photoframeAngleH = 20;
    _scanView.photoframeLineW = 2;
    _scanView.isNeedShowRetangle = YES;
    _scanView.colorRetangleLine = [UIColor whiteColor];
    _scanView.notRecoginitonArea = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
    _scanView.animationImage = [UIImage imageNamed:@"scanLine"];
    _scanView.myQRCodeBlock = ^{
        
    };
    _scanView.flashSwitchBlock = ^(BOOL open) {
        [weakSelf.scanTool openFlashSwitch:open];
    };
    [self.view addSubview:_scanView];
    
    //初始化扫描工具
    _scanTool = [[XYQRCodeTool alloc] initWithPreview:preview andScanFrame:_scanView.scanRetangleRect];
    
    _scanTool.scanFinishedBlock = ^(NSString *scanString) {
        NSLog(@"扫描结果 %@",scanString);
        
        BOOL isAppString = [[YiChatUserManager defaultManagaer] judgeQRCodeStringIsAppString:scanString];
        BOOL ispush = NO;
        
        UINavigationController *nav = weakSelf.navigationController;
        if(isAppString == YES){
            NSDictionary *dic = [[YiChatUserManager defaultManagaer] decodeQRCodeImageStringIntoJsonDic:scanString];
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                NSInteger type = [[YiChatUserManager defaultManagaer] getQRCodeStringType:dic];
                if(type == 2){
                    NSString *userId = [[YiChatUserManager defaultManagaer] getQRCodeStringUserId:dic];
                    
                    if(userId && [userId isKindOfClass:[NSString class]]){
                        YiChatFriendInfoVC *friend = [YiChatFriendInfoVC initialVC];
                        friend.userId = userId;
                        friend.hidesBottomBarWhenPushed = YES;
                        [nav popViewControllerAnimated:NO];
                        [nav pushViewController:friend animated:YES];
                        ispush = YES;
                    }
                }
            }
        }
        
        if(ispush == NO){
            YiChatQRCodeScanVC *scan = [YiChatQRCodeScanVC initialVC];
            scan.decodeScanString = scanString;
            scan.hidesBottomBarWhenPushed = YES;
            [nav popViewControllerAnimated:NO];
            [nav pushViewController:scan animated:YES];
        }
        
        if(weakSelf.XYQRCodeScanInvocation){
            weakSelf.XYQRCodeScanInvocation(scanString);
        }
        
        [weakSelf.scanView handlingResultsOfScan];
        [weakSelf.scanTool sessionStopRunning];
        [weakSelf.scanTool openFlashSwitch:NO];
    };
    _scanTool.monitorLightBlock = ^(float brightness) {
        if (brightness < 0) {
            // 环境太暗，显示闪光灯开关按钮
            [weakSelf.scanView showFlashSwitch:YES];
        }else if(brightness > 0){
            // 环境亮度可以,且闪光灯处于关闭状态时，隐藏闪光灯开关
            if(!weakSelf.scanTool.flashOpen){
                [weakSelf.scanView showFlashSwitch:NO];
            }
        }
    };
    
    [_scanTool sessionStartRunning];
    [_scanView startScanAnimation];
    
    UIButton * photoBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.frame.size.width - 80, StatusBarAndNavigationBarHeight / 2, 64, StatusBarAndNavigationBarHeight / 2)];
    [photoBtn setTitle:@"相册" forState:UIControlStateNormal];
    photoBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
    [photoBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [photoBtn addTarget:self action:@selector(photoBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:photoBtn];
    
    UIButton * backBtn = [[UIButton alloc] initWithFrame:CGRectMake(80.0 - 64.0, StatusBarAndNavigationBarHeight / 2, 64, StatusBarAndNavigationBarHeight / 2)];
    [backBtn setTitle:@"返回" forState:UIControlStateNormal];
    backBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
    [backBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(backBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];

}

- (void)backBtnClick:(UIButton *)btn{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [_scanView startScanAnimation];
    [_scanTool sessionStartRunning];
}
- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [_scanView stopScanAnimation];
    [_scanView finishedHandle];
    [_scanView showFlashSwitch:NO];
    [_scanTool sessionStopRunning];
}
#pragma mark -- Events Handle
- (void)photoBtnClicked{
  if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]){
        UIImagePickerController * _imagePickerController = [[UIImagePickerController alloc] init];
        _imagePickerController.delegate = self;
        _imagePickerController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
        _imagePickerController.allowsEditing = YES;
        _imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:_imagePickerController animated:YES completion:nil];
    }else{
        NSLog(@"不支持访问相册");
    }
}
- (void)showAlertWithTitle:(NSString *)title message:(NSString *)message handler:(void (^) (UIAlertAction *action))handler{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:handler];
    [alert addAction:action];
    [self presentViewController:alert animated:YES completion:nil];
}
#pragma mark UIImagePickerControllerDelegate
//该代理方法仅适用于只选取图片时
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingImage:(UIImage *)image editingInfo:(nullable NSDictionary<NSString *,id> *)editingInfo {
    //    NSLog(@"选择完毕----image:%@-----info:%@",image,editingInfo);
    [self dismissViewControllerAnimated:YES completion:nil];
    [_scanTool scanImageQRCode:image];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
