//
//  YiChatChangeUserInfoInputView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeUserInfoInputView.h"
#import "ServiceGlobalDef.h"

@interface YiChatChangeUserInfoInputView ()

@property (nonatomic,strong) NSString *placeHolder;

@property (nonatomic,strong) NSString *headerText;

@property (nonatomic,strong) NSString *footerText;

@property (nonatomic,strong) UIView *headerView;
@property (nonatomic,strong) UILabel *headerLab;

@property (nonatomic,strong) UIView *inputView;
@property (nonatomic,strong) UITextField *input;
@property (nonatomic,strong) UITextView *inputTextView;

@property (nonatomic,strong) UIView *footerView;
@property (nonatomic,strong) UILabel *footerLab;

@property (nonatomic,assign) BOOL isTextView;

@end

@implementation YiChatChangeUserInfoInputView

- (id)initWithFrame:(CGRect)frame
        placeHolder:(NSString *)placeHolder
         headerText:(NSString *)headerText
         footerText:(NSString *)footerText{
    self = [super initWithFrame:frame];
    if(self){
        _placeHolder = placeHolder;
        _headerText = headerText;
        _footerText = footerText;
        
        [self addSubview:self.headerView];
        [self addSubview:self.inputView];
        [self addSubview:self.footerView];
        
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
        placeHolder:(NSString *)placeHolder
         headerText:(NSString *)headerText
         footerText:(NSString *)footerText isTextView:(BOOL)isTextView{
    self = [super initWithFrame:frame];
    if(self){
        _placeHolder = placeHolder;
        _headerText = headerText;
        _footerText = footerText;
        _isTextView = isTextView;
        
        [self addSubview:self.headerView];
        [self addSubview:self.inputView];
        [self addSubview:self.footerView];
        
    }
    return self;
}

- (UIView *)headerView{
    if(!_headerView){
        _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 20.0)];
        
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, _headerView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, _headerView.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(12.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        if([_headerText isKindOfClass:[NSString class]]){
             lab.text = _headerText;
        }
        [_headerView addSubview:lab];
        _headerLab = lab;
        _headerView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    return _headerView;
}

- (UIView *)footerView{
    if(!_footerView){
        _footerView = [[UIView alloc] initWithFrame:CGRectMake(0, self.frame.size.height - 20.0, self.frame.size.width, 20.0)];
        
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, _footerView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, _footerView.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(12.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        if([_footerText isKindOfClass:[NSString class]]){
            lab.text = _footerText;
        }
        [_footerView addSubview:lab];
        _footerLab = lab;
        
        _footerView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    return _footerView;
}

- (UIView *)inputView{
    if(!_inputView){
        _inputView = [[UIView alloc] initWithFrame:CGRectMake(0,self.headerView.frame.size.height + self.headerView.frame.origin.y + 5.0, self.frame.size.width, self.frame.size.height - (self.headerView.frame.size.height + self.headerView.frame.origin.y + self.footerView.frame.size.height + 10.0))];
        
        if(!_isTextView){
            _input = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, _inputView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, _inputView.frame.size.height) withPlaceholder:_placeHolder fontSize:PROJECT_TEXT_FONT_COMMON(14.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
            [_inputView addSubview:_input];
        }
        else{
            _inputTextView = [ProjectHelper helper_factoryMakeTextViewWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, _inputView.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, _inputView.frame.size.height) fontSize:PROJECT_TEXT_FONT_COMMON(14.0) keybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
            _inputTextView.text = _placeHolder;
            [_inputView addSubview:_inputTextView];
        }
        
        _inputView.backgroundColor = [UIColor whiteColor];
        
        
    }
    return _inputView;
}

- (NSString *)getInputText{
    if(!_isTextView){
        return _input.text;
    }
    else{
        return _inputTextView.text;
    }
}

- (void)changeInputText:(NSString *)text{
    if([text isKindOfClass:[NSString class]]){
        if(text.length !=0 && text != nil){
            if(!_isTextView){
                _input.text = text;
            }
            else{
                _inputTextView.text = text;
            }
        }
    }
}

- (void)resignKeyBoard{
    if(!_isTextView){
        [_input resignFirstResponder];
    }
    else{
        [_inputTextView resignFirstResponder];
    }
}
    
- (UITextField *)getInputTextControl{
    return _input;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
