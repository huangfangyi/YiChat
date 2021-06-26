//
//  YiChatSendDynamicVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSendDynamicVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatSendDynamicToolBar.h"

@interface YiChatSendDynamicVC ()<UITextViewDelegate,TZImagePickerControllerDelegate,UINavigationControllerDelegate,UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatSendDynamicToolBar *sendToolBar;

@property (nonatomic,strong) UIView *backView;
@property (nonatomic,strong) UITextView *textView;
@property (nonatomic,strong) UILabel *alertLab;


@property (nonatomic,strong) NSMutableArray *uploadResourceList;
@end

@implementation YiChatSendDynamicVC

+ (id)initialVC{
    YiChatSendDynamicVC *dynamic = [YiChatSendDynamicVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"discoverMain.sendDynamic") leftItem:nil rightItem:@"发布"];
    return dynamic;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    _uploadResourceList = [NSMutableArray arrayWithCapacity:0];
    
    [self makeUIForType_0];

    [_backView addSubview:self.sendToolBar];
    // Do any additional setup after loading the view.
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    [_textView resignFirstResponder];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(_textView.text.length > 0){
        NSString *content = _textView.text;
        
        [_textView resignFirstResponder];
        
        NSMutableArray *str = [NSMutableArray arrayWithCapacity:0];
        NSString *uploadStr = nil;
        NSInteger type = 0;
        for (int i = 0; i < _uploadResourceList.count; i ++) {
            YiChatSendDynamicBarModel *model = _uploadResourceList[i];
            if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
                
                NSString *url = model.remoteUrl;
                if(url && [url isKindOfClass:[NSString class]]){
                    [str addObject:url];
                }
                if(i == 0){
                    if(model.type == YiChatSendDynamicBarModelTypeImage){
                        type = 0;
                    }
                    else if(model.type == YiChatSendDynamicBarModelTypeVideo){
                        type = 1;
                    }
                }
            }
        }
        if(str.count > 0){
            if(str.count == 1){
                uploadStr = str.firstObject;
            }
            else{
                uploadStr = [str componentsJoinedByString:@","];
            }
        }
        
        [self sendDynamicsWithResourceStr:uploadStr content:content location:nil type:type];
        
    }
    else{
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"请输入内容"];
    }
}

- (void)sendDynamicsWithResourceStr:(NSString *)resource content:(NSString *)content location:(NSString *)location type:(NSInteger)type{
    NSString *videoUrl = nil;
    NSString *imgsUrl = nil;
    if(type == 0){
        imgsUrl = resource;
    }
    else{
        videoUrl = resource;
    }
    NSDictionary *param = [ProjectRequestParameterModel sendDynamicWithimgs:imgsUrl videos:videoUrl content:content location:location];
    
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper sendDynamicWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]] && obj){
                [ProjectHelper helper_getMainThread:^{
                    
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"动态发布成功"];
                    
                    [self dismissViewControllerAnimated:YES completion:^{
                        
                    }];
                }];
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"发布动态出错"];
            }
        }];
        
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"发布动态出错"];
    }];
    
}

- (void)makeUIForType_0{
    CGFloat scrollW = PROJECT_SIZE_WIDTH;
    CGFloat scroolH = (PROJECT_SIZE_HEIGHT - PROJECT_SIZE_NAVH - PROJECT_SIZE_STATUSH - PROJECT_SIZE_SafeAreaInset.bottom);
    
    UIScrollView *scroll = [ProjectHelper helper_factoryMakeScrollViewWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, scrollW, scroolH) contentSize:CGSizeMake(scrollW, scroolH + 10.0) pagingEnabled:YES showsHorizontalScrollIndicator:NO showsVerticalScrollIndicator:NO scrollEnabled:YES];
    [self.view addSubview:scroll];
    scroll.delegate = self;
    
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, scroll.frame.size.width, scroll.frame.size.height + 10.0) backGroundColor:[UIColor whiteColor]];
    [scroll addSubview:back];
    _backView = back;
    
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = 10.0;
    CGFloat w = back.frame.size.width - x * 2;
    CGFloat h = back.frame.size.height / 3 - y;
    
    _textView = [ProjectHelper helper_factoryMakeTextViewWithFrame:CGRectMake(x, y, w, h) fontSize:PROJECT_TEXT_FONT_COMMON(16.0)  keybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
    [back addSubview:_textView];
    _textView.delegate = self;
    
    
    _alertLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x, y, w, 20.0) andfont:PROJECT_TEXT_FONT_COMMON(16) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [back addSubview:_alertLab];
    _alertLab.text = @"请输入内容";
}

- (void)addTextViewObserevr{
    [_textView addObserver:self forKeyPath:@"text" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)removeTextViewObserver{
    [_textView removeObserver:self forKeyPath:@"text"];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context{
    //    if(object == _textView && [keyPath isEqualToString:@"text"]){
    //        NSString *text = change[@"new"];
    //        if(text.length < 10){
    //            _textView.text = text;
    //        }
    //        else{
    //            _textView.text = [text substringWithRange:NSMakeRange(0, 10)];
    //        }
    //    }
}

- (YiChatSendDynamicToolBar *)sendToolBar{
    
    if(!_sendToolBar){
        
        WS(weakSelf);
        
        CGFloat y = _textView.frame.origin.y + _textView.frame.size.height + 20.0;
        
        _sendToolBar = [[YiChatSendDynamicToolBar alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, y, PROJECT_SIZE_WIDTH -  PROJECT_SIZE_NAV_BLANK * 2, _backView.frame.size.height - y)];
        _sendToolBar.bgVC = self;
        
        _sendToolBar.yiChatSendDynamicToolBarDidSelelcteResource = ^(NSArray * _Nonnull resource) {
            
            if(resource && [resource isKindOfClass:[NSArray class]]){
                
                [ProjectHelper helper_getMainThread:^{
                    if(resource.count > 0){
                        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
                        [ProjectHelper helper_getGlobalThread:^{
                            [weakSelf dealToolBarSelecteResourceWithResources:resource];
                            
                            [ProjectHelper helper_getMainThread:^{
                                [ProjectHelper helper_performInstanceSelectorWithTarget:progress initialMethod:@selector(hidden) flags:nil];
                            }];
                        }];
                    }
                }];
            }
        };
        _sendToolBar.yiChatSendDynamicToolBarDidDeleteResource = ^(NSArray * _Nonnull resource) {
            if(resource && [resource isKindOfClass:[NSArray class]]){
                
                if(resource.count > 0){
                    [ProjectHelper helper_getGlobalThread:^{
                        for (int j = 0; j < resource.count; j ++) {
                            YiChatSendDynamicBarModel *model = resource[j];
                            
                            if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
                                if(model.type != YiChatSendDynamicBarModelTypeAdd){
                                    
                                    for (int i = 0; i < weakSelf.uploadResourceList.count; i ++) {
                                        YiChatSendDynamicBarModel *dic = weakSelf.uploadResourceList[i];
                                        
                                        if(dic && [dic isKindOfClass:[YiChatSendDynamicBarModel class]]){
                                            NSString *indexUrl = dic.remoteUrl;
                                            NSString *tmpUrl = model.remoteUrl;
                                            if(indexUrl && [indexUrl isKindOfClass:[NSString class]] && tmpUrl && [tmpUrl isKindOfClass:[NSString class]]){
                                                
                                                
                                                if([indexUrl isEqualToString:tmpUrl]){
                                                    [weakSelf.uploadResourceList removeObjectAtIndex:i];
                                                }
                                                
                                            }
                                            
                                        }
                                    }
                                }
                            }
                        }
                        
                    }];
                }
            }
        };
    }
    return _sendToolBar;
}

- (void)dealToolBarSelecteResourceWithResources:(NSArray *)resource{
    
    __block NSInteger num = 0;
    WS(weakSelf);
    NSMutableArray *uploadSuccessModelArr =[NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < resource.count; i ++) {
        
        YiChatSendDynamicBarModel *model =  resource[i];
        
        if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
            if(model.type == YiChatSendDynamicBarModelTypeImage){
                
                [self uploadImageWithModel:model invocation:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
                    num ++;
                    if(isSuccess){
                        if(model){
                            if(remotePath && [remotePath isKindOfClass:[NSString class]]){
                                [uploadSuccessModelArr addObject:model];
                                model.remoteUrl = remotePath;
                            }
                        }
                      
                        
                    }
                }];
            }
            else if(model.type == YiChatSendDynamicBarModelTypeVideo){
                [self uploadVideoWithModel:model invocation:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
                    num ++;
                    if(isSuccess){
                        if(model){
                            if(remotePath && [remotePath isKindOfClass:[NSString class]]){
                                model.remoteUrl = remotePath;
                                [uploadSuccessModelArr addObject:model];
                            }
                        }
                    }
                }];
            }
        }
    }
    
    do {
        
    } while (num <= (resource.count - 1));
    
    [ProjectHelper helper_getMainThread:^{
        if(uploadSuccessModelArr.count > 0){
            
            
            [weakSelf.uploadResourceList addObjectsFromArray:uploadSuccessModelArr];
            [self.sendToolBar addResourceForRefreshUI:uploadSuccessModelArr];
        }
    }];
    
}

- (void)uploadImageWithModel:(YiChatSendDynamicBarModel *)model invocation:(void(^)(BOOL isSuccess,NSString * _Nonnull remotePath))invocation{
    if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
        UIImage *icon = model.icon;
        if(icon && [icon isKindOfClass:[UIImage class]]){
            
            [ProjectRequestHelper commonUpLoadImageWithoutCrop:icon progressBlock:^(CGFloat progress) {
                
            } sendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
                invocation(isSuccess,remotePath);
            }];
        }
    }
}

- (void)uploadVideoWithModel:(YiChatSendDynamicBarModel *)model invocation:(void(^)(BOOL isSuccess,NSString * _Nonnull remotePath))invocation{
    if(model && [model isKindOfClass:[YiChatSendDynamicBarModel class]]){
        NSString *localPath = model.localUrl;
        if(localPath && [localPath isKindOfClass:[NSString class]]){
            [ProjectRequestHelper commonUpLoadVideo:localPath progressBlock:^(CGFloat progress) {
                
            } sendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
                
                if(isSuccess){
                    NSString *url = [NSString stringWithFormat:@"%@%@",YiChatProject_NetWork_ChatFileHost,remotePath];
                     invocation(YES,url);
                    return ;
                }
                invocation(isSuccess,remotePath);
            }];
        }
    }
}

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
}

- (void)pushVCWithName:(NSString *)name{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:@selector(initialVC)];
            vc.hidesBottomBarWhenPushed = YES;
            if(vc){
                [self.navigationController pushViewController:vc animated:YES];
            }
        }
    }
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
    if(textView.text.length == 0){
        _alertLab.hidden = NO;
        
        CGFloat x = [textView caretRectForPosition:textView.selectedTextRange.start].origin.x + textView.frame.origin.x;
        CGFloat y = [textView caretRectForPosition:textView.selectedTextRange.start].origin.y + textView.frame.origin.y;
        
        _alertLab.frame = CGRectMake(x, y, _alertLab.frame.size.width, _alertLab.frame.size.height);
        
    }
    else{
        _alertLab.hidden = YES;
    }
    
}

- (void)textViewDidChangeSelection:(UITextView *)textView{
    CGFloat x = [textView caretRectForPosition:textView.selectedTextRange.start].origin.x + textView.frame.origin.x;
    CGFloat y = [textView caretRectForPosition:textView.selectedTextRange.start].origin.y + textView.frame.origin.y;
    
    _alertLab.frame = CGRectMake(x, y, _alertLab.frame.size.width, _alertLab.frame.size.height);
}

- (void)textViewDidEndEditing:(UITextView *)textView{
    if(textView.text.length == 0){
        _alertLab.hidden = NO;
    }
    else{
        _alertLab.hidden = YES;
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    //不支持系统表情的输入
    if ([[textView textInputMode] primaryLanguage] == nil || [[[textView textInputMode] primaryLanguage]isEqualToString:@"emoji"]) {
        return NO;
    }
    UITextRange *selectedRange = [textView markedTextRange];
    //获取高亮部分
    UITextPosition *pos = [textView positionFromPosition:selectedRange.start offset:0];
    //获取高亮部分内容
    //NSString * selectedtext = [textView textInRange:selectedRange];
    //如果有高亮且当前字数开始位置小于最大限制时允许输入
    if (selectedRange && pos) {
        NSInteger startOffset = [textView offsetFromPosition:textView.beginningOfDocument toPosition:selectedRange.start];
        NSInteger endOffset = [textView offsetFromPosition:textView.beginningOfDocument toPosition:selectedRange.end];
        NSRange offsetRange =NSMakeRange(startOffset, endOffset - startOffset);
        if (offsetRange.location <120) {
            return YES;
        }else{
            return NO;
        }
    }
    NSString *comcatstr = [textView.text stringByReplacingCharactersInRange:range withString:text];
    NSInteger caninputlen =120 - comcatstr.length;
    if (caninputlen >=0){
        return YES;
    }else{
        NSInteger len = text.length + caninputlen;
        //防止当text.length + caninputlen < 0时，使得rg.length为一个非法最大正数出错
        NSRange rg = {0,MAX(len,0)};
        if (rg.length >0){
            NSString *s =@"";
            //判断是否只普通的字符或asc码(对于中文和表情返回NO)
            BOOL asc = [text canBeConvertedToEncoding:NSASCIIStringEncoding];
            if (asc) {
                s = [text substringWithRange:rg];//因为是ascii码直接取就可以了不会错
            }else{
                __block NSInteger idx =0;
                __block NSString  *trimString =@"";//截取出的字串
                //使用字符串遍历，这个方法能准确知道每个emoji是占一个unicode还是两个
                [text enumerateSubstringsInRange:NSMakeRange(0, [text length])
                                         options:NSStringEnumerationByComposedCharacterSequences
                                      usingBlock: ^(NSString* substring,NSRange substringRange,NSRange enclosingRange,BOOL* stop) {
                                          if (idx >= rg.length) {
                                              *stop =YES;//取出所需要就break，提高效率
                                              return ;
                                          }
                                          trimString = [trimString stringByAppendingString:substring];
                                          idx++;
                                      }];
                s = trimString;
            }
            //rang是指从当前光标处进行替换处理(注意如果执行此句后面返回的是YES会触发didchange事件)
            [textView setText:[textView.text stringByReplacingCharactersInRange:range withString:s]];
            //既然是超出部分截取了，哪一定是最大限制了。
            _textView.text = [NSString stringWithFormat:@"%d/%ld",0,(long)120];
        }
        return NO;
    }
}

// 计算转换后字符的个数

- (NSUInteger) lenghtWithString:(NSString *)string
{
    NSUInteger len = string.length;
    // 汉字字符集
    NSString * pattern  = @"[\u4e00-\u9fa5]";
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
    // 计算中文字符的个数
    NSInteger numMatch = [regex numberOfMatchesInString:string options:NSMatchingReportProgress range:NSMakeRange(0, len)];
    
    return len + numMatch;
}

- (void)textViewDidChange:(UITextView *)textView{
    if(textView.text.length > 0){
        _alertLab.hidden = YES;
    }
    else{
        _alertLab.hidden = NO;
    }
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
     [_textView resignFirstResponder];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_textView resignFirstResponder];
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
