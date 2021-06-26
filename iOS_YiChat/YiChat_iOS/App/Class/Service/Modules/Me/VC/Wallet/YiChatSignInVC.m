//
//  YiChatSignInVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/14.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSignInVC.h"
#import "YiChatSignInModel.h"

@interface YiChatSignInVC ()
@property (nonatomic,strong) NSMutableArray *signArr;

@property (nonatomic,strong) UIButton *signBtn;

@property (nonatomic,strong) UILabel *integralLa;

@property (nonatomic,strong) UILabel *signLa;

@property (nonatomic,strong) UITextView *textView;
@end

@implementation YiChatSignInVC

+ (id)initialVC{
    YiChatSignInVC *walletVC = [YiChatSignInVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"SignIn") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.signArr = [NSMutableArray new];
    CGFloat y_view = PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH;
    
    UIImageView *signImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, y_view, self.view.frame.size.width, self.view.frame.size.width / 2)];
    signImage.image = [UIImage imageNamed:@"Signin"];
    [self.view addSubview:signImage];

    self.integralLa = [[UILabel alloc]initWithFrame:CGRectMake(0, self.view.frame.size.width / 2 + 20 + y_view, self.view.frame.size.width / 2, 20)];
    self.integralLa.text = @"";
    self.integralLa.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.integralLa];
    
    self.signLa = [[UILabel alloc]initWithFrame:CGRectMake(self.view.frame.size.width / 2, self.view.frame.size.width / 2 + 20 + y_view, self.view.frame.size.width / 2, 20)];
    self.signLa.text = @"已经签到天";
    self.signLa.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.signLa];
    
    CGFloat btn_h = self.view.frame.size.width / 7;
    CGFloat x = 0;
    NSArray *week = @[@"周一",@"周二",@"周三",@"周四",@"周五",@"周六",@"周日"];
    for (NSInteger i = 0; i < 7; i ++) {
        UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(x, self.view.frame.size.width / 2 + 50 + y_view, btn_h, btn_h)];
        [self.view addSubview:bg];
        
        UIImageView *im = [[UIImageView alloc] initWithFrame:CGRectZero];
        im.image = [UIImage imageNamed:@"noSign"];
        [bg addSubview:im];
        [im mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(30, 30));
        }];
        [self.signArr addObject:im];
        UILabel *la = [[UILabel alloc]initWithFrame:CGRectMake(x, self.view.frame.size.width / 2 + 50 + btn_h + y_view, btn_h, 20)];
        la.text = week[i];
        la.textAlignment = NSTextAlignmentCenter;
        [self.view addSubview:la];
        x = x + btn_h;
    }
    
    self.signBtn = [[UIButton alloc] initWithFrame:CGRectZero];
    [self.signBtn setTitle:@"马上签到" forState:UIControlStateNormal];
    [self.signBtn addTarget:self action:@selector(sign) forControlEvents:UIControlEventTouchUpInside];
    [self.signBtn setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:247/255.0 green:68/255.0 blue:77/255.0 alpha:1] size:CGSizeMake(1, 1)] forState:UIControlStateNormal];
    self.signBtn.layer.masksToBounds = YES;
    self.signBtn.layer.cornerRadius = 5;
    [self.view addSubview:self.signBtn];
    
    [self.signBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.view.frame.size.width / 2 + 50 + btn_h + 100 + y_view);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(self.view.frame.size.width - 50, 45));
    }];
    
    UILabel *la = [[UILabel alloc]initWithFrame:CGRectZero];
    la.text = @"---- 签到规则 ----";
    la.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:la];
    [la mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.signBtn.mas_bottom).offset(20);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(20);
    }];
    
    self.textView = [[UITextView alloc]initWithFrame:CGRectZero];
    self.textView.editable = NO;
    self.textView.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:self.textView];
    [self.textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(la.mas_bottom).offset(10);
        make.bottom.mas_equalTo(-15);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
    }];
    [self sign:@"0"];
}

-(void)sign{
    [self sign:@"1"];
}

//签到 不传默认为0 0无签到操作 1签到操作
-(void)sign:(NSString *)type{
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel getSignParametersWithSignType:type];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper signWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatSignInModel *model = [YiChatSignInModel mj_objectWithKeyValues:dataDic];
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSInteger day = 0;
                    for (NSInteger i = 0; i < weakSelf.signArr.count; i++) {
                        UIImageView *im = weakSelf.signArr[i];
                        YiChatSignInInfoModel *m = model.data.list[i];
                        if (m.isToday && m.signStatus) {
                            weakSelf.signBtn.enabled = NO;
                            [weakSelf.signBtn setTitle:@"今日已签到" forState:UIControlStateNormal];
                        }
                        if (m.signStatus) {
                            day++;
                            im.image = [UIImage imageNamed:@"Sign"];
                        }else{
                            im.image = [UIImage imageNamed:@"noSign"];
                        }
                    }
                    weakSelf.signLa.text = [NSString stringWithFormat:@"已签到%ld天",day];
                    weakSelf.textView.text = model.data.content;
                });
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

- (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size {
    if (!color || size.width <= 0 || size.height <= 0)
        return nil;
    CGRect rect = CGRectMake(0.0f, 0.0f, size.width, size.height);
    UIGraphicsBeginImageContextWithOptions(rect.size, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

@end
