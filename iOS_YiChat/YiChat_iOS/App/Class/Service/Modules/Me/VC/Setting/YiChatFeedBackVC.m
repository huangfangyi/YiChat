//
//  YiChatFeedBackVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/9/4.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatFeedBackVC.h"

@interface YiChatFeedBackVC ()
@property (nonatomic,strong) UITextView *textView;
@end

@implementation YiChatFeedBackVC

+ (id)initialVC{
    YiChatFeedBackVC *walletVC = [YiChatFeedBackVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"意见反馈") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self setUI];
    // Do any additional setup after loading the view.
}

-(void)setUI{
    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(20, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 10, PROJECT_SIZE_WIDTH - 40, 200)];
    self.textView.layer.masksToBounds = YES;
    self.textView.layer.cornerRadius = 5;
    [self.view addSubview:self.textView];
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectZero];
    [btn setTitle:@"提交" forState:UIControlStateNormal];
    btn.backgroundColor = PROJECT_COLOR_BlLUE;
    btn.layer.masksToBounds = YES;
    btn.layer.cornerRadius = 5;
    [btn addTarget:self action:@selector(submit) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    [btn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.textView.mas_bottom).offset(30);
        make.left.mas_equalTo(20);
        make.right.mas_equalTo(-20);
        make.height.mas_equalTo(45);
    }];
}

-(void)submit{
    [self.view endEditing:YES];
    if (self.textView.text.length == 0 || self.textView.text == nil) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入有效的意见"];
        return;
    }
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel feedbackParametersWithContent:self.textView.text];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    [ProjectRequestHelper feedbackWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:dataDic];
                if (model.code == 0) {
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"意见提交成功"];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.navigationController popViewControllerAnimated:YES];
                    });
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
