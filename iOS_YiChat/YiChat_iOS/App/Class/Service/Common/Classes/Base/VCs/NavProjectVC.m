//
//  NavProjectVC.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "NavProjectVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectHelper.h"
@interface NavProjectVC ()
{
    ProjectNavBarStyle _dNavBarStyle;
    UIView *_cBackView;
    UIView *_cStatusView;
    UIView *_cProgressView;
    UIButton *_currentBtn;
    
    
    id _cRightItem;
    id _cLeftItem;
    id _cCenterItem;
    UIView *_cNetStatusView;
    UILabel *_cNetStatusLab;
    UIActivityIndicatorView *_cNetStatusProgress;
    
    id _dLeftItem;
    id _dRightItem;
    id _dCenterItem;
}
@end

#define APPAGE_FONTSIZE_DEFAULT PROJECT_TEXT_FONT_COMMON(18.0)

#define APPAGE_FONTSIZE_LEFTRIGHT PROJECT_TEXT_FONT_COMMON(14.0)

#define APPAGE_COMMONICON_SIZE_H 20.0f

#define APPAGE_COMMONLABEL_SIZE_H 25.0f

#define APPAGE_FONT APPAGE_FONTSIZE_DEFAULT
#define APPAGE_FONT_DEFAULT APPAGE_FONTSIZE_DEFAULT
#define APPAGE_FONT_LEFTRIGHT APPAGE_FONTSIZE_LEFTRIGHT

#define APPPAGE_BACKCOLOR  PROJECT_COLOR_NAVBACKCOLOR
#define APPPAGE_LEFTBACK_ARROW @"back@3x"

#define APPPAGE_INTERFACE_DEBUG YES

@implementation NavProjectVC

- (id)initWithNavBarStyle:(ProjectNavBarStyle)navBarStyle centeritem:(id)centerItem leftItem:(id)leftItem rightItem:(id)rightItem{
    self=[super init];
    if(self){
        
        
        _dNavBarStyle=navBarStyle;
        _dLeftItem=leftItem;
        _dRightItem=rightItem;
        _dCenterItem=centerItem;
        
        _statusH = PROJECT_SIZE_STATUSH;
        
        _navH = PROJECT_SIZE_NAVH;
        _totalNavH = _statusH + _navH;
        _isNeedBackText = NO;
        
    }
    return self;
}

+ (id)initialVCWithNavBarStyle:(ProjectNavBarStyle)navBarStyle  centeritem:(id)centerItem leftItem:(id)leftItem rightItem:(id)rightItem{
    return [[self alloc] initWithNavBarStyle:navBarStyle centeritem:centerItem leftItem:leftItem rightItem:rightItem];
}

- (void)setNavH:(CGFloat)navH{
    _navH = navH;
    _statusH = [[UIApplication sharedApplication] statusBarFrame].size.height;
    _totalNavH = _statusH + _navH;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setPersonalNav];
    
    [self setUp];
    // Do any additional setup after loading the view.
}


- (void)removeNetStatusAppearanceSub{
    if(_cNetStatusView){
        for (id tmp in _cNetStatusView.subviews) {
            [tmp removeFromSuperview];
        }
    }
    [_cNetStatusView removeFromSuperview];
    _cNetStatusView = nil;
}

- (void)makeUIForNetBack{
    if(!_cNetStatusView){
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(_cBackView.frame.size.width / 2 - 100.0 / 2, 0, 100.0, _cBackView.frame.size.height)];
        [_cBackView addSubview:back];
        back.backgroundColor = [UIColor whiteColor];
        _cNetStatusView =back;
    }
}

- (void)makeUIForConnecting{
    [self removeNetStatusAppearanceSub];
    if(!_cNetStatusView){
        [self makeUIForNetBack];
    }
    
    UIView *back = _cNetStatusView;
    CGRect frame = back.frame;
    
    UIFont *textFont = APPAGE_FONTSIZE_DEFAULT;
    NSString *str = @"正在连接...";
    CGRect tectRect = [ProjectHelper helper_getFontSizeWithString:str useSetFont:textFont withWidth:frame.size.width andHeight:frame.size.height];
    CGFloat w = 30.0;
    
    UIActivityIndicatorView *progress = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(frame.size.width / 2 - (w + tectRect.size.width + 5.0) / 2, frame.size.height / 2 - w / 2 , w, w)];
    [back addSubview:progress];
    progress.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    [progress startAnimating];
    
    UILabel *text = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(progress.frame.origin.x + progress.frame.size.width + 5.0, 0, tectRect.size.width, frame.size.height) andfont:textFont textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [back addSubview:text];
    text.text = str;
}

- (void)makeUIForDisconnect{
    [self removeNetStatusAppearanceSub];
    
    if(!_cNetStatusView){
        [self makeUIForNetBack];
    }
    
    UIFont *textFont = APPAGE_FONTSIZE_DEFAULT;
    UILabel *text = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0, _cNetStatusView.frame.size.width, _cNetStatusView.frame.size.height) andfont:textFont textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [_cNetStatusView addSubview:text];
    text.text = @"未连接";
}

- (void)makeUIForConnect{
    [self removeNetStatusAppearanceSub];
    if(_cNetStatusView){
        [_cNetStatusView removeFromSuperview];
        _cNetStatusView = nil;
    }
}


- (void)setPersonalNav{
    self.navigationController.navigationBar.hidden=YES;
    if (@available(iOS 13.0, *)) {
      //  self.overrideUserInterfaceStyle = UIUserInterfaceStyleLight;
    } else {
        // Fallback on earlier versions
    }

    self.view.backgroundColor=PROJECT_COLOR_APPBACKCOLOR;
}

- (void)setUp{
    
    /*       push                               Present
     Vc1 view will disappear            Vc2 load view
     Vc2  Load view                     Vc2 view did load
     Vc2. View did load                 Vc1 view will disappear
     Vc2. View will appear              Vc2 view will appear
     Vc1. View did disappear            Vc2 view did appear
     Vc2. View did appear               vc1 view did disappear
     
     Pop                                         Dismiss
     Vc2 view will disappear            Vc2 view will disappear
     Vc1 view will appear               Vc1 view will appear
     Vc2 view did disappear             Vc1 view did appear
     Vc1 view did appear                Vc2 view did disappear
     Dealloc                            Dealloc
     */
    
    /**
     *  导航条样式 分为logo 和普通
     *    LOGO 样式
     *      1.为左右图片按钮 中间图片样式
     *    普通 样式
     *      1.左边图片返回按钮 中间文字title
     *      2.左边图片返回按钮 右边图片按钮 中间文字title
     *      3.左边图片返回按钮 右边文字按钮 中间文字title
     *      4.左边图片按钮 右边图片按钮 中间文件title
     *      5.中间文字title
     */
    
    [self makeBackView];
    
    if(_dNavBarStyle==ProjectNavBarStyleLOGO_1){
        
    }
    if(_dNavBarStyle>=10)
    {
        [self makeCenterLab];
        
        if(_dNavBarStyle==ProjectNavBarStyleCommon_1){
            [self makeNavBarUIStyle_Common1];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_2){
            [self makeNavBarUIStyle_Common2];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_3){
            [self makeNavBarUIStyle_Common3];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_4){
            [self makeNavBarUIStyle_Common4];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_5){
            [self makeNavBarUIStyle_Common5];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_6){
            [self makeNavBarUIStyle_Common6];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_7){
            for (id temp in _cBackView.subviews) {
                [temp removeFromSuperview];
            }
            [_cBackView removeFromSuperview];
            _cBackView=nil;
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_8){
            [self makeNavBarUIStyle_Common8];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_9){
            [self makeNavBarUIStyle_Common9];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_10){
            [self makeNavBarUIStyle_Common10];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_11){
            [self makeNavBarUIStyle_Common11];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_12){
            [self makeNavBarUIStyle_Common12];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_13){
            for (id temp in _cBackView.subviews) {
                [temp removeFromSuperview];
            }
            [self makeNavBarUIStyle_Common13];
        }
        else if(_dNavBarStyle==ProjectNavBarStyleCommon_14){
            for (id temp in _cBackView.subviews) {
                [temp removeFromSuperview];
            }
            [self makeNavBarUIStyle_Common14];
        }
    }
}


- (void)makeBackView{
    
    UIView *status = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, _statusH)];
    status.backgroundColor = PROJECT_COLOR_STATUSBACKCOLOR;
    [self.view addSubview:status];
    _cStatusView = status;
    
    UIView *back=[[UIView alloc] initWithFrame:CGRectMake(0, status.frame.origin.y + status.frame.size.height, self.view.frame.size.width, _navH)];
    back.backgroundColor=PROJECT_COLOR_NAVBACKCOLOR;
    [self.view addSubview:back];
    _cBackView=back;
    back.userInteractionEnabled=YES;
}

/* 左边图片返回按钮 中间文字title */

- (void)makeNavBarUIStyle_Common1{
    [self makeGeneralImgViewWith:0];
}

/* 左边图片返回按钮 右边图片按钮 中间文字title */

- (void)makeNavBarUIStyle_Common2{
    for (int i=0; i<2; i++) {
        [self makeGeneralImgViewWith:i];
    }
}

/* 左边图片返回按钮 右边文字按钮 中间文字title */

- (void)makeNavBarUIStyle_Common3{
    [self makeGeneralImgViewWith:0];
    
    [self makeLabelWithType:1];
    
}

/* 左边图片按钮 右边图片按钮 中间文件title */

- (void)makeNavBarUIStyle_Common4{
    for (int i=0; i<2; i++) {
        [self makeGeneralImgViewWith:i];
    }
}

- (void)makeNavBarUIStyle_Common5{
    
}

- (void)makeNavBarUIStyle_Common6{
    [self makeLabelWithType:1];
}

- (void)makeNavBarUIStyle_Common8{
    [self makeGeneralImgViewWith:0];
    
    [self makeLabelWithType:1];
}

- (void)makeNavBarUIStyle_Common9{
    [self makeGeneralImgViewWith:1];
}

- (void)makeNavBarUIStyle_Common10{
    [self makeGeneralImgViewWith:0];
    
    UILabel *lab =  (UILabel *)_cCenterItem;
    UIImageView *left = (UIImageView *)_cLeftItem;
    lab.frame = CGRectMake(left.frame.origin.x * 2 + left.frame.size.width, _statusH, (self.view.frame.size.width - left.frame.origin.x * 2) / 2 - left.frame.origin.x * 2 + left.frame.size.width, _navH);
    lab.textAlignment = NSTextAlignmentLeft;
    
    
    CGFloat itemH = left.frame.size.height + 10.0;
    NSArray *rightItemTitleArr = (NSArray *)_dRightItem;
    
    CGFloat w = 0;
    CGFloat blank = left.frame.origin.x;
    for (NSInteger i = rightItemTitleArr.count - 1; i >= 0; i--) {
        CGRect rightItemRect = [ProjectHelper helper_getFontSizeWithString:rightItemTitleArr[i] useFont:APPAGE_FONTSIZE_LEFTRIGHT withWidth:MAXFLOAT andHeight:itemH];
        
        w += (rightItemRect.size.width + 10.0);
        blank += blank;
        
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(5.0, 5.0, rightItemRect.size.width, itemH - 10.0) andfont:APPAGE_FONTSIZE_LEFTRIGHT textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
        [_cBackView addSubview:lab];
        lab.text = rightItemTitleArr[i];
        
    }
}

- (void)makeNavBarUIStyle_Common11{
   
    [self makeLabelWithType:0];
    [self makeLabelWithType:1];
    
}

- (void)makeNavBarUIStyle_Common12{
    [self makeLabelWithType:0];
    [self makeGeneralImgViewWith:1];
}

- (void)makeNavBarUIStyle_Common13{
    UIImageView *icon =   [self makeGeneralImgViewWith:0];
    UILabel *lab =  [self makeLabelWithType:0];
    lab.frame = CGRectMake(icon.frame.origin.x + icon.frame.size.width,0, lab.frame.size.width, _cBackView.frame.size.height);
    lab.text = @"返回";
    lab.textColor = [UIColor blackColor];
    
    _cLeftItem = lab;
    
    [self makeCenterLab];
    
    [self makeLabelWithType:1];

}

- (void)makeNavBarUIStyle_Common14{
    UIImageView *icon =   [self makeGeneralImgViewWith:0];
    UILabel *lab =  [self makeLabelWithType:0];
    
    lab.frame = CGRectMake(icon.frame.origin.x + icon.frame.size.width,0, lab.frame.size.width, _cBackView.frame.size.height);
    lab.text = @"返回";
    lab.textColor = [UIColor blackColor];
    
    _cLeftItem = lab;
    
    
    [self makeCenterLab];
    
    [self makeGeneralImgViewWith:1];
}

#pragma mark ---  General

- (UILabel *)makeCenterLab{
    UILabel *lab=[[UILabel alloc] initWithFrame:CGRectMake(80.0, 0, self.view.frame.size.width - 160.0, _navH)];
    lab.backgroundColor=[UIColor clearColor];
    lab.textColor=PROJECT_COLOR_TEXTCOLOR_BLACK;
    lab.textAlignment=NSTextAlignmentCenter;
    lab.font=APPAGE_FONT;
    [_cBackView addSubview:lab];
    _cCenterItem=lab;
    
    NSString *text = (NSString *)_dCenterItem;
    
    if(text.length > 0 && text != nil){
        lab.text = text;
    }
    
    return lab;
}

- (void)changeCenterLabWithStr:(NSString *)str{
    UILabel *lab = _cCenterItem;
    lab.text = str;
}

/**
 * 0 左边 1右边
 */
- (UIImageView *)makeGeneralImgViewWith:(NSInteger)type{
    CGFloat x=0;
    CGFloat y=0;
    CGFloat w=0;
    CGFloat h=APPAGE_COMMONICON_SIZE_H;
    
    if(_dNavBarStyle==ProjectNavBarStyleCommon_1 || _dNavBarStyle==ProjectNavBarStyleCommon_2 || _dNavBarStyle==ProjectNavBarStyleCommon_3 || _dNavBarStyle==ProjectNavBarStyleCommon_10 || _dNavBarStyle==ProjectNavBarStyleCommon_13 || _dNavBarStyle==ProjectNavBarStyleCommon_14){
        _dLeftItem=[UIImage imageNamed:APPPAGE_LEFTBACK_ARROW];
    }
    
    if(_dLeftItem==nil && type==0){
        return nil;
    }
    
    if(_dRightItem==nil && type==1){
        return nil;
    }
    
    UIImage *img=nil;
    if(type==0){
        img=(UIImage *)_dLeftItem;
        x=PROJECT_SIZE_NAV_BLANK;
        y=(_navH - h) / 2;
        w=[self getImageSizeWithWidth:0 height:h scale:img.size.width / img.size.height];
        
    }
    else if(type==1){
        img=(UIImage *)_dRightItem;
        y=(_navH - h) / 2;
        if([_dRightItem isKindOfClass:[UIImage class]] && _dRightItem != nil){
            w=[self getImageSizeWithWidth:0 height:h scale:img.size.width / img.size.height];
        }
        else{
            w = h ;
        }
        x=self.view.frame.size.width - PROJECT_SIZE_NAV_BLANK - w;
    }
    else{
        return nil;
    }
    
    UIImageView *barImage=[[UIImageView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    if([img isKindOfClass:[UIImage class]]){
        barImage.image=img;
    }
    [_cBackView addSubview:barImage];
    
    if(type == 0){
        _cLeftItem = barImage;
    }
    else{
        _cRightItem = barImage;
    }
    
    type==0 ?
    
    [[self makeButtonWithFrame:CGRectMake(barImage.frame.origin.x - 10.0, 0.0, barImage.frame.size.width + 20.0,_navH)] addTarget:self action:@selector(navBarButtonLeftItemMethod:) forControlEvents:UIControlEventTouchUpInside]
    
    :
    
    [[self makeButtonWithFrame:CGRectMake(barImage.frame.origin.x - 10.0, 0.0, barImage.frame.size.width + 20.0, _navH)] addTarget:self action:@selector(navBarButtonRightItemMethod:) forControlEvents:UIControlEventTouchUpInside] ;
    
    return barImage;
    
}

- (UILabel *)makeLabelWithType:(NSInteger)type{
    CGFloat x=0;
    CGFloat y=0;
    CGFloat w=0;
    CGFloat h=APPAGE_COMMONLABEL_SIZE_H;
    UIFont *font = APPAGE_FONT;
    
    NSString *str=nil;
    
    if(type==0){
        str=(NSString *)_dLeftItem;
        x=PROJECT_SIZE_NAV_BLANK;
        y= (_navH - h) / 2;
        w=80.0;
        font = APPAGE_FONT_LEFTRIGHT;
    }
    else if(type==1){
        str=(NSString *)_dRightItem;
        y=(_navH - h) / 2;
        w=80.0;
        x=self.view.frame.size.width - PROJECT_SIZE_NAV_BLANK - w;
        font = APPAGE_FONT_LEFTRIGHT;
    }
    else{
        return nil;
    }
    
    UILabel *lab=[[UILabel alloc] initWithFrame:CGRectMake(x, y, w, h)];
    lab.backgroundColor=[UIColor clearColor];
    lab.textColor=PROJECT_COLOR_NAVTEXTCOLOR;
    lab.textAlignment=NSTextAlignmentCenter;
    lab.font=font;
    [_cBackView addSubview:lab];
    
    NSString *text = @"";
    if([str isKindOfClass:[NSString class]]){
        text = str;
    }
    if(text.length > 0 && text != nil){
        NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:text];
        if(font != nil){
            [attributedString addAttribute:NSFontAttributeName value:font range:NSMakeRange(0, text.length)];
        }
        else{
            [attributedString addAttribute:NSFontAttributeName value:APPAGE_FONT_DEFAULT range:NSMakeRange(0, text.length)];
        }
        [attributedString addAttribute:NSForegroundColorAttributeName value:PROJECT_COLOR_TEXTCOLOR_BLACK range:NSMakeRange(0, text.length)];
        
        lab.attributedText = attributedString;
    }
    
    
    if(type == 0){
        _cLeftItem=lab;
    }
    
    if(type == 1){
        _cRightItem=lab;
    }
    
    
    type==0 ? (lab.textAlignment=NSTextAlignmentLeft) : (lab.textAlignment=NSTextAlignmentRight);
    
    
    type==0 ?
    
    [[self makeButtonWithFrame:CGRectMake(lab.frame.origin.x - 10.0,0 , lab.frame.size.width + 20.0, _navH)] addTarget:self action:@selector(navBarButtonLeftItemMethod:) forControlEvents:UIControlEventTouchUpInside]
    
    :
    
    [[self makeButtonWithFrame:CGRectMake(lab.frame.origin.x - 10.0,0 , lab.frame.size.width + 20.0, _navH)] addTarget:self action:@selector(navBarButtonRightItemMethod:) forControlEvents:UIControlEventTouchUpInside] ;
    
    return lab;
}

- (UIButton *)makeButtonWithFrame:(CGRect)frame{
    UIButton *clearBtn=[UIButton buttonWithType:UIButtonTypeCustom];
    clearBtn.frame=frame;
    clearBtn.backgroundColor=[UIColor clearColor];
    [_cBackView addSubview:clearBtn];
    
    return clearBtn;
}

#pragma mark invocation methods



#pragma mark ---  touches

- (void)navBarButtonLeftItemMethod:(UIButton *)btn{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    
}

- (void)navBarButtonRightItem_1Method:(UIButton *)btn{
    
}

- (UIView *)navBarGetNavBar{
    return _cBackView;
}

- (UIView *)navBarGetNavBarStatusView{
    return _cStatusView;
}

- (id)navBarGetLeftBarItem{
    return _cLeftItem;
}

- (id)navBarGetCenterBarItem{
    
    return _cCenterItem;
}

- (id)navBarGetRightBarItem{
    return _cRightItem;
}

#pragma mark ---  helps

- (CGFloat)getImageScaleWithImg:(UIImage *)img{
    return img.size.width / img.size.height;
}

- (CGFloat)getImageSizeWithWidth:(CGFloat)width height:(CGFloat)height scale:(CGFloat)scale{
    if(width==0){
        return height * scale;
    }
    else if(height==0){
        return width / scale;
    }
    return 0;
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
