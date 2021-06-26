//
//  ProjectAlertView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/21.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectAlertView.h"
#import "ServiceGlobalDef.h"
#import "UIButton+BtnCategory.h"

static ProjectAlertView *alert= nil;
static ProjectAlertView *clickAlert = nil;

/**
 *  默认是title content加一个确认btn YRAlertViewStyleAutoDisappear 仅有title content 3秒自动消失
 */
typedef NS_ENUM(NSUInteger,ProjectAlertViewStyle) {
    ProjectAlertViewStyleDefault=0,
    ProjectAlertViewStyleTitleOneButton,
    ProjectAlertViewStyleTitleTwoButton,
    ProjectAlertViewStyleAutoDisappear,
    ProjectAlertViewStyleAutoDisappearNoBackShadow = 10
};

#define TITLE_FONT PROJECT_TEXT_FONT_COMMON(18.0)
#define CONTENT_FONT PROJECT_TEXT_FONT_COMMON(16.0)
#define BTNS_FONT PROJECT_TEXT_FONT_COMMON(16.0)

#define TEXT_BLANK_W PROJECT_SIZE_SUTEBLE_W(5.0)
#define TEXT_BLANK_H PROJECT_SIZE_SUTEBLE_H(5.0)

@interface ProjectAlertView ()
{
    ProjectAlertViewStyle _dStyle;
    NSString *_dTitleText;
    NSString *_dContentText;
    NSArray *_dOtherBtnDataSource;
    
    
    UILabel *_cTitleLab;
    UILabel *_cContentLab;
    
}

@property (nonatomic,strong)  UIView *cBack;
@property (nonatomic,strong)  UIView *cControlBack;
@property (nonatomic,copy) HelperIntergeFlagInvocation clickInvocation;
@end

@implementation ProjectAlertView

+ (id)appearAlertWithStyle:(ProjectAlertViewStyle)style title:(NSString *)title content:(NSString *)content buttonsDataSource:(NSArray *)btnsResource clickInvocation:(NSDictionary *)clickInvocation{
    return [[self alloc] initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_HEIGHT) style:style title:title content:content buttonsDataSource:btnsResource clickInvocation:clickInvocation];
}

- (id)initWithFrame:(CGRect)frame style:(ProjectAlertViewStyle)style title:(NSString *)title content:(NSString *)content buttonsDataSource:(NSArray *)btnsResource clickInvocation:(NSDictionary *)clickInvocation{
    self=[super initWithFrame:frame];
    if(self){
        [self systemInitialWithStyle:style title:title content:content buttonsDataSource:btnsResource clickInvocation:clickInvocation];
        
        [self makeUI];
    }
    return self;
}

- (void)systemInitialWithStyle:(ProjectAlertViewStyle)style title:(NSString *)title content:(NSString *)content buttonsDataSource:(NSArray *)btnsResource clickInvocation:(NSDictionary *)clickInvocation{
    _dStyle=style;
    _dTitleText=title;
    _dContentText=content;
    _dOtherBtnDataSource=btnsResource;
    if(clickInvocation[@"click"]){
        self.clickInvocation=clickInvocation[@"click"];
    }
    
    
    [self configure];
}

- (void)configure{
    if(_dStyle == ProjectAlertViewStyleDefault){
        _dOtherBtnDataSource=@[@"确认",@"取消"];
        _dTitleText=@"";
    }
    if(_dStyle == ProjectAlertViewStyleAutoDisappear || _dStyle == ProjectAlertViewStyleAutoDisappearNoBackShadow){
        _dOtherBtnDataSource=nil;
        _dTitleText=@"";
    }
    
    self.backgroundColor=[UIColor clearColor];
    self.frame=CGRectMake(0, 0, PROJECT_SIZE_WIDTH, PROJECT_SIZE_HEIGHT);
}

#pragma mark

- (void)makeUI{
    [self makeBackView];
    
    [self makeControlBack];
}

- (void)makeBackView{
    
    _cBack=[ProjectHelper helper_factoryMakeViewWithFrame:self.bounds backGroundColor:[UIColor blackColor]];
    _cBack.alpha=0.6;
    if(_dStyle == ProjectAlertViewStyleAutoDisappearNoBackShadow){
        _cBack.hidden = YES;
    }
    [self addSubview:_cBack];
    
    UIButton *backClearBtn=[ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(0, 0, _cBack.frame.size.width, _cBack.frame.size.height) andBtnType:UIButtonTypeCustom];
    [backClearBtn addTarget:self action:@selector(backClearBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:backClearBtn];
}

- (void)makeControlBack{
    CGSize size=[self getAlertViewSize];
    
    _cControlBack=[ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(self.frame.size.width / 2 -size.width / 2, self.frame.size.height, size.width, size.height) backGroundColor:[UIColor whiteColor]];
    _cControlBack.layer.cornerRadius = 10;
    [self addSubview:_cControlBack];
    
    [self makeControlBackSubviews];
}

- (void)makeControlBackSubviews{
    NSString *titlte=_dTitleText;
    NSString *conetent=_dContentText;
    NSArray *btnDataSource=_dOtherBtnDataSource;
    
    if(titlte == nil || titlte.length == 0){
        
    }
    else{
        CGSize titleSize=[self getTitleSize];
        
        UILabel *lab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0, _cControlBack.frame.size.width, titleSize.height) andfont:TITLE_FONT textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
        [_cControlBack addSubview:lab];
        lab.numberOfLines=0;
        lab.text=_dTitleText;
        _cTitleLab=lab;
        
        UIView *line=[ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(lab.frame.origin.x, lab.frame.size.height) width:lab.frame.size.width];
        [_cControlBack addSubview:line];
    }
    
    if(conetent == nil || conetent.length == 0){
        
    }
    else{
        
        CGSize contentSize=[self getContentSize];
        
        UILabel *lab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0,_cTitleLab.frame.size.height + _cTitleLab.frame.origin.y, _cControlBack.frame.size.width, contentSize.height) andfont:CONTENT_FONT textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
        [_cControlBack addSubview:lab];
        lab.numberOfLines=0;
        lab.text=_dContentText;
        _cContentLab=lab;
        
        UIView *line=[ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(lab.frame.origin.x, lab.frame.size.height + lab.frame.origin.y) width:lab.frame.size.width];
        [_cControlBack addSubview:line];
    }
    
    if(_dOtherBtnDataSource.count == 0){
        
    }
    else{
        CGSize btnSize=[self getBtnSize];
        
        if(btnSize.width < _cControlBack.frame.size.width){
            btnSize=CGSizeMake(_cControlBack.frame.size.width, btnSize.height);
        }
        CGFloat w=btnSize.width / btnDataSource.count;
        
        CGFloat y=0;
        if(_cTitleLab != nil  && _cContentLab == nil){
            y=_cTitleLab.frame.origin.y + _cTitleLab.frame.size.height;
        }
        else if(_cTitleLab != nil && _cContentLab != nil){
            y=_cContentLab.frame.origin.y + _cContentLab.frame.size.height;
        }
        else if(_cTitleLab == nil && _cContentLab != nil){
            y=_cContentLab.frame.origin.y + _cContentLab.frame.size.height;
        }
        
        for (int i=0; i<_dOtherBtnDataSource.count; i++) {
            
            UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(i * (w), y, w, btnSize.height) backGroundColor:[UIColor whiteColor]];
            [_cControlBack addSubview:back];
            
            
            UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0, back.frame.size.width, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
            [back addSubview:lab];
            lab.text = btnDataSource[i];
            
            UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:back.frame target:self method:@selector(clearBtnMethod:)];
            btn.btnIdentifier = [NSString stringWithFormat:@"%ld",i];
            [_cControlBack addSubview:btn];
            
            UIView *vertivalLine=[ProjectHelper helper_factoryMakeVerticalLineWithPoint:CGPointMake(back.frame.origin.x, back.frame.origin.y) height:back.frame.size.height];
            [_cControlBack addSubview:vertivalLine];
            
            back.layer.cornerRadius = 10.0;
            
        }
    }
}

- (void)clearBtnMethod:(UIButton *)btn{
    [self clean];
    self.clickInvocation(btn.btnIdentifier.integerValue);
}

- (void)beginAnimate{
    WS(weakSelf);
    weakSelf.cControlBack.frame=CGRectMake(self.frame.size.width / 2 - weakSelf.cControlBack.frame.size.width / 2, self.frame.size.height / 2  - weakSelf.cControlBack.frame.size.height / 2 - 100.0, weakSelf.cControlBack.frame.size.width, weakSelf.cControlBack.frame.size.height);
    weakSelf.cBack.alpha = 0.3;
}

- (void)endAnimate{
    WS(weakSelf);
    weakSelf.cControlBack.frame=CGRectMake(self.frame.size.width / 2 - weakSelf.cControlBack.frame.size.width / 2, self.frame.size.height, weakSelf.cControlBack.frame.size.width, weakSelf.cControlBack.frame.size.height);
    weakSelf.cBack.alpha=1;
}

- (void)show{
    
    UIWindow *window=[[ProjectHelper helper_getAppDelegate] window];
    [window addSubview:self];
    
    [self beginAnimate];
    
    [window bringSubviewToFront:self];
    
    if(_dStyle == ProjectAlertViewStyleAutoDisappear || _dStyle == ProjectAlertViewStyleAutoDisappearNoBackShadow){
        [self performSelector:@selector(clean) withObject:nil afterDelay:1];
    }
}


- (void)showNoAnimate{
    UIWindow *window=[ProjectHelper helper_getAppDelegate].window;
    [window addSubview:self];
    
    _cControlBack.frame=CGRectMake(self.frame.size.width / 2 - _cControlBack.frame.size.width / 2, self.frame.size.height / 2  - _cControlBack.frame.size.height / 2 - 60.0, _cControlBack.frame.size.width, _cControlBack.frame.size.height);
    _cBack.alpha=0.3;
    
    [window bringSubviewToFront:self];
    
    if(_dStyle == ProjectAlertViewStyleAutoDisappear || _dStyle == ProjectAlertViewStyleAutoDisappearNoBackShadow){
        [self performSelector:@selector(clean) withObject:nil afterDelay:1];
    }
}

- (void)hidden{
    [self endAnimate];
}

- (void)clean{
    [self hidden];
    
    for (id temp in _cControlBack.subviews) {
        [temp removeFromSuperview];
    }
    
    for (id temp in self.subviews) {
        [temp removeFromSuperview];
    }
    _cBack=nil;
    _cControlBack=nil;
    [self removeFromSuperview];
    _dOtherBtnDataSource=nil;
    _dTitleText=nil;
    _dContentText=nil;
}

- (void)cleanNoAnimate{
    for (id temp in _cControlBack.subviews) {
        [temp removeFromSuperview];
    }
    
    for (id temp in self.subviews) {
        [temp removeFromSuperview];
    }
    _cBack=nil;
    _cControlBack=nil;
    [self removeFromSuperview];
    _dOtherBtnDataSource=nil;
    _dTitleText=nil;
    _dContentText=nil;
}

- (void)backClearBtnMethod:(UIButton *)btn{
    [self clean];
}

#pragma mark private

- (CGSize)getAlertViewSize{
    
    CGSize titleSize=[self getTitleSize];
    CGSize contentSize = [self getContentSize];
    CGSize btnsSize = [self getBtnSize];
    
    CGFloat w1=titleSize.width;
    CGFloat w2=contentSize.width;
    CGFloat w3=btnsSize.width;
    
    CGFloat maxW=0;
    
    if(w1 > w2){
        if(w1 > w3){
            maxW=w1;
        }
        else{
            maxW=w3;
        }
    }
    else{
        if(w2 > w3){
            maxW=w2;
        }
        else{
            maxW=w3;
        }
    }
    
    return CGSizeMake(maxW, titleSize.height + contentSize.height + btnsSize.height);
}

- (CGSize)getTitleSize{
    NSString *title=_dTitleText;
    CGSize titleSize = CGSizeMake(0, 0);
    if(title == nil || title.length == 0){
        
    }
    else{
        CGFloat w=[self getTitleTextMinWidth];
        CGFloat h=[self getTitleMinH];
        
        
        CGRect titleRect=[ProjectHelper helper_getFontSizeWithString:_dTitleText useSetFont:TITLE_FONT withWidth:[self getTitleTextMinWidth] andHeight:PROJECT_SIZE_HEIGHT];
        
        if(titleRect.size.width >= w){
            w=titleRect.size.width + TEXT_BLANK_W * 2;
        }
        
        if(titleRect.size.height >= h ){
            h=titleRect.size.height + TEXT_BLANK_H * 2;
        }
        
        
        if(w >= PROJECT_SIZE_WIDTH){
            w=PROJECT_SIZE_WIDTH - TEXT_BLANK_W * 2;
        }
        
        titleSize=CGSizeMake(w, h);
    }
    return titleSize;
    
}

- (CGSize)getContentSize{
    NSString *content=_dContentText;
    CGSize contentSize = CGSizeMake(0, 0);
    if(content == nil || content.length == 0){
        
    }
    else{
        CGFloat w=[self getContentTextMinWidth];
        CGFloat h=[self getContentMinH];
        
        CGRect contentRect=[ProjectHelper helper_getFontSizeWithString:_dContentText useSetFont:CONTENT_FONT withWidth:[self getContentTextMinWidth] andHeight:PROJECT_SIZE_HEIGHT];
        
        if(contentRect.size.width >= w){
            w=contentRect.size.width + TEXT_BLANK_W * 2;
        }
        
        if(contentRect.size.height >= h){
            h=contentRect.size.height + TEXT_BLANK_H * 2;
        }
        
        if(w >= PROJECT_SIZE_WIDTH){
            w=PROJECT_SIZE_WIDTH - TEXT_BLANK_W * 2;
        }
        
        contentSize=CGSizeMake(w, h);
        
    }
    return contentSize;
}

- (CGSize)getBtnSize{
    NSArray *btnsDataSource=_dOtherBtnDataSource;
    CGSize btnsSize = CGSizeMake(0, 0);
    
    if(btnsDataSource.count != 0){
        CGFloat w=0;
        CGFloat h=0;
        
        for (int i=0; i<btnsDataSource.count; i++) {
            NSString *str=btnsDataSource[i];
            if(str.length == 0){
                str=@"好的";
            }
            CGRect btnRect=[ProjectHelper helper_getFontSizeWithString:str useSetFont:TITLE_FONT withWidth:PROJECT_SIZE_WIDTH andHeight:PROJECT_SIZE_HEIGHT];
            
            CGFloat tempW=[self getBtnMinWidth];
            CGFloat tempH=[self getBtnsMinH];
            
            if(btnRect.size.width >= tempW){
                tempW=btnRect.size.width + TEXT_BLANK_W * 2;
            }
            
            if(btnRect.size.height >= tempH){
                tempH=btnRect.size.height + TEXT_BLANK_H * 2;
            }
            
            
            if(tempW > w){
                w=tempW;
            }
            
            
            if(tempH > h){
                h=tempH;
            }
        }
        
        if(w * btnsDataSource.count >= PROJECT_SIZE_WIDTH){
            btnsSize=CGSizeMake((PROJECT_SIZE_WIDTH - TEXT_BLANK_W * 2), h);
        }
        else{
            btnsSize=CGSizeMake(w * btnsDataSource.count, h);
        }
    }
    return btnsSize;
}


- (CGFloat)getTitleTextMinWidth{
    return PROJECT_SIZE_WIDTH * 0.8;
}

- (CGFloat)getContentTextMinWidth{
    return PROJECT_SIZE_WIDTH * 0.8;
}

- (CGFloat)getBtnMinWidth{
    return [ProjectHelper helper_getScreenSuitable_W:40.0];
}

- (CGFloat)getTitleMinH{
    return [ProjectHelper helper_getScreenSuitable_H:50.0];
}

- (CGFloat)getContentMinH{
    return [ProjectHelper helper_getScreenSuitable_H:60.0];
}

- (CGFloat)getBtnsMinH{
    return [ProjectHelper helper_getScreenSuitable_H:40.0];
}

- (CGFloat)getGeneralX{
    
    return [ProjectHelper helper_getScreenSuitable_W:PROJECT_SIZE_NAV_BLANK];
}

- (CGFloat)getGeneralY{
    return [ProjectHelper helper_getScreenSuitable_H:PROJECT_SIZE_NAV_BLANK];
}

+ (void)yrAlertViewAlertMessgae:(NSString *)msg{
    [ProjectHelper helper_getMainThread:^{
        if(!alert){
            
            alert =  [ProjectAlertView appearAlertWithStyle:ProjectAlertViewStyleAutoDisappear title:nil content:msg buttonsDataSource:nil clickInvocation:nil];
            [alert show];
        }
        else{
            [alert cleanNoAnimate];
            
            alert =  [ProjectAlertView appearAlertWithStyle:ProjectAlertViewStyleAutoDisappear title:nil content:msg buttonsDataSource:nil clickInvocation:nil];
            [alert show];
        }
    }];
}

+ (void)yrAlertWithAlertMessage:(NSString *)message clickBtns:(NSArray *)arr invocation:(void(^)(NSInteger row))click{
    ProjectAlertViewStyle style ;
    if(arr.count == 0){
        style = ProjectAlertViewStyleDefault;
    }
    else if(arr.count == 1){
        style = ProjectAlertViewStyleTitleOneButton;
    }
    else{
        style = ProjectAlertViewStyleTitleTwoButton;
    }
    
    NSDictionary *clickInvocation = @{@"click":click};
    
    if(!clickAlert){
        clickAlert = [ProjectAlertView appearAlertWithStyle:style title:nil content:message buttonsDataSource:arr clickInvocation:clickInvocation];
        [clickAlert show];
    }
    else{
        [clickAlert removeFromSuperview];
        clickAlert = nil;
        
        clickAlert = [ProjectAlertView appearAlertWithStyle:style title:nil content:message buttonsDataSource:arr clickInvocation:clickInvocation];
        [clickAlert showNoAnimate];
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
