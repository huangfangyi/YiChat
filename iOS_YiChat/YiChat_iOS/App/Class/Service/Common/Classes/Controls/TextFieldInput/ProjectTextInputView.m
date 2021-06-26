//
//  ProjectTextInputView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/21.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectTextInputView.h"
#import <objc/message.h>
#import "ServiceGlobalDef.h"

@interface ProjectTextInputView ()

@property (nonatomic,strong) UIView *certifyUI;

@property (nonatomic,copy) HelperReturnBOOLInvocation recturnClick;

@end

@implementation ProjectTextInputView

- (void)createUI{
    self.backgroundColor = [UIColor clearColor];
    
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat w = self.frame.size.width;
    CGFloat h = self.frame.size.height;
    
    if(_inputStyle == ProjectInputViewStylePhoneInput){
        
        UITextField *phoneInput = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(x, y,w , h) withPlaceholder:@"手机号码" fontSize:PROJECT_TEXT_FONT_COMMON(15.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTGRAY];
        [self addSubview:phoneInput];
        phoneInput.textAlignment = NSTextAlignmentLeft;
        [phoneInput addTarget:self action:@selector(textInputValueChange:) forControlEvents:UIControlEventEditingChanged];
        _textInput = phoneInput;
        
    }
    else if(_inputStyle == ProjectInputViewPasswordInput){
        
        w = self.frame.size.width * 0.9;
        
        UITextField *passwordInput = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(x, y,w , h) withPlaceholder:@"密码" fontSize:PROJECT_TEXT_FONT_COMMON(15.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTGRAY];
        [self addSubview:passwordInput];
        passwordInput.secureTextEntry = YES;
        passwordInput.textAlignment = NSTextAlignmentLeft;
        [passwordInput addTarget:self action:@selector(textInputValueChange:) forControlEvents:UIControlEventEditingChanged];
        _textInput = passwordInput;
        
//        UIButton *btn =  [self makeSeeBtn];
//        btn.selected = YES;
        
    }
    else if (_inputStyle == ProjectInputViewStyleSetPasswordInput){
        
        w = self.frame.size.width * 0.9;
        
        UITextField *passwordInput = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(x, y,w , h) withPlaceholder:@"请设置6-20位（不能全为数字）" fontSize:PROJECT_TEXT_FONT_COMMON(15.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTGRAY];
        [self addSubview:passwordInput];
        passwordInput.secureTextEntry = YES;
        passwordInput.textAlignment = NSTextAlignmentLeft;
        _textInput = passwordInput;
        [passwordInput addTarget:self action:@selector(textInputValueChange:) forControlEvents:UIControlEventEditingChanged];
        
        UIButton *btn =  [self makeSeeBtn];
        btn.selected = YES;
        
    }
    else if(_inputStyle == ProjectInputViewStyleInputCertify){
        w = self.frame.size.width / 2;
        
        UITextField *certify = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(x, y,w , h) withPlaceholder:@"短信验证码" fontSize:PROJECT_TEXT_FONT_COMMON(15.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTGRAY];
        [self addSubview:certify];
        certify.textAlignment = NSTextAlignmentLeft;
        _textInput = certify;
        [certify addTarget:self action:@selector(textInputValueChange:) forControlEvents:UIControlEventEditingChanged];
        
        CGFloat certifyW = [ProjectHelper helper_getFontSizeWithString:@"获取验证码获取验证码" useFont:12.0 withWidth:self.frame.size.width / 2 andHeight:PROJECT_SIZE_SUTEBLE_H(29.0)].size.width;
        CGFloat certifyH = PROJECT_SIZE_SUTEBLE_H(29.0);
        
        NSValue *certifyRectValue = [NSValue valueWithCGRect:CGRectMake(self.frame.size.width - certifyW, self.frame.size.height / 2 - certifyH / 2, certifyW, certifyH)];
        
        
        id certifyUI =  ((id(*)(id, SEL,NSValue *))objc_msgSend)(NSClassFromString(@"ProjectSendCertifyView"), @selector(buildObjWithFrame:), certifyRectValue);
        
        if(self.recturnClick){
            ((void(*)(id, SEL,NSDictionary *))objc_msgSend)(certifyUI, @selector(addInvocation:),@{@"click":self.recturnClick});
        }
        
        ((void(*)(id, SEL))objc_msgSend)(certifyUI, @selector(createUI));
        
        _certifyUI = certifyUI;
        [self addSubview:certifyUI];
        
        UIView *line = [ProjectHelper helper_factoryMakeVerticalLineWithPoint:CGPointMake(certifyRectValue.CGRectValue.origin.x, certifyRectValue.CGRectValue.origin.y + 5.0) height:certifyRectValue.CGRectValue.size.height - 10.0];
        [self addSubview:line];
    }
    else if(_inputStyle == ProjectInputViewStyleSelecteAreaCountry){
        w = self.frame.size.width * 0.9;
//
        UILabel *areaCountrySelecte = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x, y,w , h) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
        [self addSubview:areaCountrySelecte];
        areaCountrySelecte.text = @"中国(China)(+86)";
        _selecteAppearLab = areaCountrySelecte;
//
        
        UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        clearBtn.frame = self.bounds;
        [clearBtn addTarget:self action:@selector(clickSelecteRows:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:clearBtn];
    }
    
    if(_isShowHorizontalLine.boolValue){
        UIView *line=[ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, self.frame.size.height - 0.8, self.frame.size.width, 0.8) backGroundColor:PROJECT_COLOR_TEXTGRAY];
        line.alpha=0.3;
        [self addSubview:line];
    }
    
    if(_isShowArrow.boolValue){
        UIImage *arrowImg = [UIImage imageNamed:Project_Icon_rightGrayArrow];
        
        if(arrowImg && [arrowImg isKindOfClass:[UIImage class]]){
            CGSize size=arrowImg.size;
            
            CGFloat h=[ProjectHelper helper_getScreenSuitable_H:10.0];
            
            CGFloat w=[ProjectHelper helper_GetWidthOrHeightIntoScale:size.width / size.height width:0 height:h];
            
            UIImageView *img=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width - 10.0  - w, self.frame.size.height / 2 - h / 2, w, h) andImg:arrowImg];
            [self addSubview:img];
        }
    }
}

- (void)clickSelecteRows:(UIButton *)btn{
    if(self.clickRowsInvocation){
        self.clickRowsInvocation(self.row);
    }
}

- (void)clickSendCertify{
    
    if([_certifyUI respondsToSelector:@selector(sendCertify)]){
        [_certifyUI performSelector:@selector(sendCertify)];
    }
}

- (void)addCertifyInvocation:(NSDictionary *)dic{
    if(dic[@"click"]){
        self.recturnClick = dic[@"click"];
    }
}

- (UIButton *)makeSeeBtn{
    UITextField *passwordInput = _textInput;
    
    UIImage *seeIcon = [UIImage imageNamed:@"login_password_display@3x.png"];
    UIImage *unseeIcon = [UIImage imageNamed:@"login_password_hide@3x.png"];
    
    CGFloat seeIconH = 20.0;
    CGFloat seeIconW = [ProjectHelper helper_GetWidthOrHeightIntoScale:seeIcon.size.width / seeIcon.size.height width:0 height:seeIconH];
    
    UIButton *btn = [ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(self.frame.size.width - seeIconW, passwordInput.frame.origin.y + passwordInput.frame.size.height / 2 - seeIconH / 2, seeIconW, seeIconH) andBtnType:UIButtonTypeCustom];
    btn.selected = NO;
    [self addSubview:btn];
    [btn addTarget:self action:@selector(seeBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    [btn setImage:seeIcon forState:UIControlStateNormal];
    [btn setImage:unseeIcon forState:UIControlStateSelected];
    btn.tintColor = [UIColor clearColor];
    
    return btn;
}

- (void)seeBtnMethod:(UIButton *)btn{
    btn.selected = !btn.selected;
    if(btn.selected == YES){
        _textInput.secureTextEntry = YES;
    }
    else{
        _textInput.secureTextEntry = NO;
    }
}

- (void)textInputValueChange:(UITextField *)text{
    
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
