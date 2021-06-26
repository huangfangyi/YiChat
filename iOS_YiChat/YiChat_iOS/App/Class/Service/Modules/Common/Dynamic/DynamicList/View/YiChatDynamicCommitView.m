//
//  YiChatDynamicCommitView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicCommitView.h"
#import "ServiceGlobalDef.h"
@interface YiChatDynamicCommitView ()<UITextViewDelegate>

@property (nonatomic,strong) UITextView *text;

@property (nonatomic,assign) CGSize originSize;

@property (nonatomic,weak) UIView *keyboardView;

@end

#define YiChatDynamicCommitView_textY 8.0f
#define YiChatDynamicCommitView_textX 10.0f
@implementation YiChatDynamicCommitView

- (void)dealloc{
    [self removeNotify];
}

+ (id)create{
    
    return [[self alloc] init];
}

- (id)init{
    self = [super init];
    if(self){
        self.originSize = CGSizeMake(PROJECT_SIZE_WIDTH, 50.0);
        self.frame = CGRectMake(0, PROJECT_SIZE_HEIGHT, self.originSize.width, self.originSize.height);
        self.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        [self makeUI];
        
        [self addNotify];
    }
    return self;
}

- (void)addNotify{
    [self registeKeyboardAppearNotify];
    [self registeKeyboardDisAppearNotify];
}

- (void)removeNotify{
    [self removeKeyboardAppearNotify];
    [self removeKeyboardDisAppearNotify];
}

- (void)registeKeyboardAppearNotify{
    PROJECT_Method_KeyboardAddObserver;
}

- (void)removeKeyboardAppearNotify{
    PROJECT_Method_KeyboardRemoveObserver;
}

- (void)registeKeyboardDisAppearNotify{
    PROJECT_Method_KeyBoardDisappearObserver;
}

- (void)removeKeyboardDisAppearNotify{
    PROJECT_Method_KeyBoardDisappearRemoveObserver;
}

- (void)keyboardDidShow:(NSNotification *)notify{
    _keyboardView = [ProjectHelper helper_GetKeyboardView];
    
    NSDictionary* info = [notify userInfo];
    NSValue* aValue = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGSize keyboardSize= [aValue CGRectValue].size;
    
    _keyBoardSize = keyboardSize;
    
    if(keyboardSize.height == 0){
        keyboardSize = CGSizeMake(0, 0);
        return;
    }
    
    self.frame = CGRectMake(0, PROJECT_SIZE_HEIGHT - keyboardSize.height - self.frame.size.height , self.frame.size.width, self.frame.size.height);
    [self changeTextViewFrame];
    
    if(self.YiChatDynamicCommitViewKeyBoardShow){
        self.YiChatDynamicCommitViewKeyBoardShow(keyboardSize);
    }
}

- (void)keyboardDidHidden:(NSNotification *)notify{
    
    self.frame = CGRectMake(0, PROJECT_SIZE_HEIGHT, self.originSize.width, self.originSize.height);
    
    [self changeTextViewFrame];
    
    self.hidden = YES;
}

- (void)makeUI{
    _text = [ProjectHelper helper_factoryMakeTextViewWithFrame:CGRectMake(YiChatDynamicCommitView_textX, YiChatDynamicCommitView_textY, self.frame.size.width - YiChatDynamicCommitView_textX * 2, self.frame.size.height - YiChatDynamicCommitView_textY * 2) fontSize:PROJECT_TEXT_FONT_COMMON(15) keybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
    [self addSubview:_text];
    _text.keyboardType = UIKeyboardTypeDefault;
    _text.returnKeyType = UIReturnKeySend;
    _text.delegate = self;
}

- (void)beginActive:(NSString *)trendId{
    self.hidden = NO;
    _trendId = trendId;
    [_text becomeFirstResponder];
}

- (void)removeActive{
    [_text resignFirstResponder];
}

- (void)textViewDidChange:(UITextView *)textView{
    NSString *text = textView.text;
    CGRect rect = [ProjectHelper helper_getFontSizeWithString:text useSetFont:_text.font withWidth:_text.frame.size.width andHeight:MAXFLOAT];
    
    if(rect.size.height > (_originSize.height - YiChatDynamicCommitView_textY * 2)){
        CGFloat h = rect.size.height + YiChatDynamicCommitView_textY * 2;
        if(h > 100.0){
            h = 100.0;
            self.frame = CGRectMake(0, PROJECT_SIZE_HEIGHT - _keyboardView.frame.size.height - h,self.frame.size.width , h);
            [self changeTextViewFrame];
        }
        else{
            self.frame = CGRectMake(0, PROJECT_SIZE_HEIGHT - _keyboardView.frame.size.height - h,self.frame.size.width , h);
            [self changeTextViewFrame];
        }
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    
    if([text isEqualToString:@"\n"]){

        if(self.YiChatDynamicCommitViewSend){
            self.YiChatDynamicCommitViewSend(_text.text,self.trendId);
        }
        [_text resignFirstResponder];
        _text.text = @"";
        
        self.hidden = YES;
        _text.selectedRange = NSMakeRange(0, 0);
        
        return NO;
        
    }
    return YES;
}



- (void)changeTextViewFrame{
    _text.frame = CGRectMake(YiChatDynamicCommitView_textX, YiChatDynamicCommitView_textY, self.frame.size.width - YiChatDynamicCommitView_textX * 2, self.frame.size.height - YiChatDynamicCommitView_textY * 2);
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
