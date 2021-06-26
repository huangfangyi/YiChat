//
//  YiChatSetGroupNoticeVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSetGroupNoticeVC.h"
#import "YiChatGroupNoticeModel.h"
#import "ZFChatMessageHelper.h"

@interface YiChatSetGroupNoticeVC ()<UITextViewDelegate>{
    UITextField *integral;
}
@property (nonatomic,strong) UIButton *sumitButton;
@property (nonatomic,strong) UITextView *textView;
@property (nonatomic,strong) UILabel *placeholderLa;
@end

@implementation YiChatSetGroupNoticeVC

+ (id)initialVC{
    YiChatSetGroupNoticeVC *groupList = [YiChatSetGroupNoticeVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"SetGroupNotice") leftItem:nil rightItem:nil];
    return groupList;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setUI];
    // Do any additional setup after loading the view.
}

-(void)setUI{
    UIView *bg = [[UIView alloc] initWithFrame:CGRectMake(15, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 10, PROJECT_SIZE_WIDTH - 30, 40)];
    bg.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:bg];
    integral = [[UITextField alloc]initWithFrame:CGRectMake(18, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH + 15, PROJECT_SIZE_WIDTH - 30, 30)];
    integral.placeholder = @"请输入群公告标题";
    integral.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:integral];
    
    self.textView = [[UITextView alloc]initWithFrame:CGRectZero];
    self.textView.font = [UIFont systemFontOfSize:14];
    self.textView.delegate = self;
    [self.view addSubview:self.textView];
    [self.textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(bg.mas_bottom).offset(10);
        make.height.mas_equalTo(150);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
    }];
    
    self.placeholderLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.placeholderLa.text = @"在此输入公告内容";
    self.placeholderLa.font = [UIFont systemFontOfSize:14];
    self.placeholderLa.textColor = [UIColor grayColor];
    self.placeholderLa.userInteractionEnabled = NO;
    [self.textView addSubview:self.placeholderLa];
    [self.placeholderLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(5);
        make.top.mas_equalTo(6);
        make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 40, 20));
    }];
    
    self.sumitButton = [[UIButton alloc]initWithFrame:CGRectZero];
    self.sumitButton.layer.masksToBounds = YES;
    self.sumitButton.layer.cornerRadius = 4;
    [self.sumitButton setTitle:@"提交" forState:UIControlStateNormal];
    [self.sumitButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.sumitButton addTarget:self action:@selector(setGroupNotice) forControlEvents:UIControlEventTouchUpInside];
    [self.sumitButton setBackgroundImage:[self imageWithColor:[UIColor colorWithRed:247/255.0 green:68/255.0 blue:77/255.0 alpha:1] size:CGSizeMake(1, 1)] forState:UIControlStateNormal];
    [self.view addSubview:self.sumitButton];
    [self.sumitButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.textView.mas_bottom).offset(50);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 40, 50));
    }];
}

- (void)textViewDidChange:(UITextView *)textView{
    if (!textView.text.length) {
        self.placeholderLa.hidden = NO;
    } else {
        self.placeholderLa.hidden = YES;
    }
}

-(void)setGroupNotice{
    if (integral.text.length == 0 || integral.text == nil || [integral.text isEqualToString:@""]) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"标题不能为空"];
        return;
    }
    
    if (self.textView.text.length == 0 || self.textView.text == nil || [self.textView.text isEqualToString:@""]) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"群公告内容不能为空"];
        return;
    }
    
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel setGroupNoticePublishWithTitle:integral.text content:self.textView.text groupId:self.groupID];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper groupNoticePublishWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatGroupNoticeModel *model1 = [YiChatGroupNoticeModel mj_objectWithKeyValues:dataDic];
                if (model1.code == 0) {
                    [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                        YiChatGroupNoticeInfoModel *m = model1.data.lastObject;
                        NSString *key = [NSString stringWithFormat:@"%ld%@",model.userId,weakSelf.groupID];
                        NSString *msg = [NSString stringWithFormat:@"%@%@",m.noticeId,self.groupID];
                        [[NSUserDefaults standardUserDefaults] setObject:msg forKey:key];
                        [[NSUserDefaults standardUserDefaults] synchronize];
                        
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [ZFChatMessageHelper groupNoticeWithGroupId:self.groupID content:self.textView.text title:self->integral.text msgId:m.noticeId completion:^(HTCmdMessage * _Nonnull cmd, NSError * _Nonnull error) {
                            }];
                            [weakSelf.navigationController popViewControllerAnimated:YES];
                        });
                    }];
                    
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model1.msg];
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

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
