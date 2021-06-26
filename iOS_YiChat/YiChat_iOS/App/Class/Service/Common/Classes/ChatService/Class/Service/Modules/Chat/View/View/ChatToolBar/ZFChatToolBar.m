//
//  ZFChatToolBar.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatToolBar.h"

#import "ZFChatEmojiView.h"
#import "ZFChatAddView.h"
#import "ZFVoiceInputAlertView.h"

#import "ZFChatGlobal.h"
#import "ZFChatResourceHelper.h"
#import "ZFChatRecorder.h"
#import "ZFChatStorageHelper.h"



typedef BOOL(^RunloopBlock)(void);

typedef NS_ENUM(NSUInteger,XYChatToolBarStyle){
    XYChatToolBarStyleOrigin = 0, //左侧 textVoiceIcon显示voice emojiIcon显示emoji addIcon显示add
    XYChatToolBarStyleTextInput, //textInputView 为first responder 键盘弹起
    XYChatToolBarStyleVoiceInput,
    XYChatToolBarStyleEmojiInput,
    XYChatToolBarStyleAddInput
};

@interface ZFChatToolBar ()

@property (nonatomic) CGPoint originBottomPosition;
@property (nonatomic) CGSize originSize;

@property (nonatomic) XYChatToolBarStyle style;

@property (nonatomic,strong) ZFChatTextInputView *textInputView;
@property (nonatomic,strong) ZFChatVoiceAlertView *alertView;


@property (nonatomic,strong) ZFChatIconClickView *textVoiceClick;
@property (nonatomic,strong) ZFChatIconClickView *emojiClick;
@property (nonatomic,strong) ZFChatIconClickView *addIconClick;

@property (nonatomic,assign) UIView *keyboardView;

@property (nonatomic,strong) ZFChatEmojiView *emojiView;
@property (nonatomic,strong) ZFChatAddView *addView;
@property (nonatomic,strong) ZFVoiceInputAlertView *voiceInputAppearView;

/** 数组  */
@property(nonatomic,strong)  NSMutableArray * tasks;

@property (nonatomic,assign) BOOL isCanInputText;

@end

#define XYChatToolBar_EmojiViewH 200.0f
#define XYChatToolBar_AddViewH 200.0f
#define XYChatToolBar_VoiceInputAppearView 150.0f
@implementation ZFChatToolBar


- (void)dealloc{
    [self removeKeyboardAppearNotify];
    [self removeKeyboardDisAppearNotify];
}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        self.originBottomPosition = CGPointMake(frame.origin.x, frame.origin.y + frame.size.height);
        self.originSize = CGSizeMake(frame.size.width, frame.size.height);
        
        self.backgroundColor = [UIColor whiteColor];
        _tasks  = [NSMutableArray arrayWithCapacity:0];
        _isCanInputText = YES;
        
        [self addRunloopObserver];
        
        [self registeKeyboardAppearNotify];
        [self registeKeyboardDisAppearNotify];
    }
    return self;
}

- (UIView *)emojiView{
    WS(weakSelf);
    
    if(!_emojiView){
        _emojiView = (ZFChatEmojiView *)[ZFChatResourceHelper ZFResourceHelperGetLoadEmojiView];
        _emojiView.originRect = CGRectMake(self.frame.origin.x, self.frame.origin.y + self.frame.size.height - XYChatToolBar_EmojiViewH, self.frame.size.width, XYChatToolBar_EmojiViewH);
        
        HelperObjFlagInvocation selecteDefaultEmoji = ^void(NSString *emojiStr){
            if(weakSelf.isCanInputText){
                if([emojiStr containsString:@"gemoji_"]){
                    NSMutableString *str = [NSMutableString stringWithCapacity:0];
                    [str appendString:@"["];
                    [str appendString:emojiStr];
                    [str appendString:@"]"];
                    weakSelf.zfChatToolBarSendMessage(str);
                }
                else{
                    [weakSelf.textInputView appendInputTextView:emojiStr];
                }
            }
        };
        HelperInvocation sendDefaultEmoji = ^{
            if(weakSelf.zfChatToolBarSendMessage){
                if(weakSelf.textInputView && [weakSelf.textInputView isKindOfClass:[ZFChatTextInputView class]]){
                    if(weakSelf.textInputView.text && [weakSelf.textInputView.text isKindOfClass:[UITextView class]] && weakSelf.isCanInputText){
                        weakSelf.zfChatToolBarSendMessage(weakSelf.textInputView.text.text);
                        weakSelf.textInputView.text.text = @"";
                    }
                }
            }
        };
        
        [_emojiView addSelecteDefaultEmojiInvocation:@{@"click":selecteDefaultEmoji}];
        [_emojiView addSendDefaultEmojiInvocation:@{@"click":sendDefaultEmoji}];
    }
    return _emojiView;
}

- (UIView *)addView{
    if(!_addView){
        _addView = [[ZFChatAddView alloc] initWithFrame:CGRectMake(0,_originBottomPosition.y - XYChatToolBar_AddViewH, self.frame.size.width, XYChatToolBar_AddViewH)];
        [_addView createUI];
        [_addView makeUI];
        
        WS(weakSelf);
        HelperintergerObjFlagInvocation invocation = ^void(NSInteger row,id obj){
            if(weakSelf.zfChatToolBarAddViewSelecte){
                weakSelf.zfChatToolBarAddViewSelecte(row,obj);
            }
        };
        [_addView addInvocation:@{@"click":invocation}];
    }
    return _addView;
}

- (void)changeAddViewWithPower:(NSInteger)power{
    [self.addView changeAddViewWithPower:power];
}

- (void)changeAddViewWithSingleChat{
    [self.addView changeAddViewWithSingleChat];
}

- (UIView *)voiceInputAppearView{
    if(!_voiceInputAppearView){
        _voiceInputAppearView  = [[ZFVoiceInputAlertView alloc] initWithFrame:CGRectMake(_originBackView.frame.size.width / 2 - XYChatToolBar_VoiceInputAppearView / 2, _originBackView.frame.size.height / 2 - XYChatToolBar_VoiceInputAppearView / 2, XYChatToolBar_VoiceInputAppearView, XYChatToolBar_VoiceInputAppearView)];
        [_voiceInputAppearView makeUI];
    }
    return _voiceInputAppearView;
}

- (void)keyboardDidShow:(NSNotification *)notify{
    _keyboardView = [ProjectHelper helper_GetKeyboardView];
    
    NSDictionary* info = [notify userInfo];
    NSValue* aValue = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGSize keyboardSize= [aValue CGRectValue].size;
    
    if(keyboardSize.height == 0){
        keyboardSize = CGSizeMake(0, 0);
        return;
    }
    
    self.frame = CGRectMake(_originBottomPosition.x, PROJECT_SIZE_HEIGHT - keyboardSize.height - self.frame.size.height , self.frame.size.width, self.frame.size.height);
    
    [self.textVoiceClick changeIcon:[self getTextVoiceIconWithType:0] withState:0];
    [self.emojiClick changeIcon:[self getTextVoiceIconWithType:2] withState:0];
    [self.addIconClick changeIcon:[self getTextVoiceIconWithType:3] withState:0];
    
    [self emojiViewDidHidden];
    [self addviewDidHidden];
}

- (void)keyboardDidHidden:(NSNotification *)notify{
    
    self.frame = CGRectMake(_originBottomPosition.x, _originBottomPosition.y - self.frame.size.height, self.frame.size.width, self.frame.size.height);
}

- (void)emojiViewDidAppear{
    WS(weakSelf);
    
    [self.emojiView XYChatEmojiView_appearWithAnimate:^{
        weakSelf.frame = CGRectMake(weakSelf.originBottomPosition.x, weakSelf.originBottomPosition.y - weakSelf.emojiView.frame.size.height - weakSelf.frame.size.height, weakSelf.frame.size.width, weakSelf.frame.size.height);
    }];
}

- (void)emojiViewDidHidden{
    WS(weakSelf);
    [self.emojiView XYChatEmojiView_disappearWithAnimate:^{
        
    }];
}

- (void)addViewDidAppear{
    WS(weakSelf);
    
    [self.addView XYChatAddView_appearWithAnimate:^{
        weakSelf.frame = CGRectMake(weakSelf.originBottomPosition.x, weakSelf.originBottomPosition.y- weakSelf.addView.frame.size.height - weakSelf.frame.size.height, weakSelf.frame.size.width, weakSelf.frame.size.height);
    }];
}

- (void)addviewDidHidden{
    WS(weakSelf);
    [self.addView XYChatAddView_disappearWithAnimate:^{
        
    }];
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

- (void)makeUI{
    
    WS(weakSelf);
    
    [self makeToolBarUI];
    
    [self addTask:^BOOL{
        [ProjectHelper helper_getMainThread:^{
            [weakSelf.originBackView addSubview:weakSelf.emojiView];
            weakSelf.emojiView.hidden = YES;
        }];
       
        return YES;
    }];
    
    [self addTask:^BOOL{
        [ProjectHelper helper_getMainThread:^{
            [weakSelf.originBackView addSubview:weakSelf.addView];
            weakSelf.addView.hidden = YES;
        }];
        
        return YES;
    }];
    
    [self addTask:^BOOL{
        [ProjectHelper helper_getMainThread:^{
            [weakSelf.originBackView addSubview:weakSelf.voiceInputAppearView];
            weakSelf.voiceInputAppearView.hidden = YES;
        }];
        return YES;
    }];
    
}

- (void)makeToolBarUI{
    
    UIView *back = self;
    
    CGFloat blank = 8.0;
    CGFloat textInputW = 235.0 / 375 * self.frame.size.width;
    CGFloat iconW = (self.frame.size.width - textInputW - blank * 5) / 3;
    
    CGFloat x = blank;
    CGFloat h = iconW;
    CGFloat w = h;
    CGFloat y = back.frame.size.height / 2 - h / 2;
    
    WS(weakSelf);
    
    HelperIntergeFlagInvocation textVoiceClick = ^void(NSInteger state){
        state = !state;
        if(state == 0){
            //显示语音图标
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:0] withState:state];
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:2] withState:0];
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:0];
            [weakSelf emojiViewDidHidden];
            [weakSelf addviewDidHidden];
            [weakSelf.textInputView.text becomeFirstResponder];
            
            weakSelf.alertView.hidden = YES;
            weakSelf.textInputView.hidden = NO;
        }
        else{
            //显示文本图标
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:1] withState:state];
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:2] withState:0];
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:0];
            
            [weakSelf.textInputView.text resignFirstResponder];
            [weakSelf emojiViewDidHidden];
            [weakSelf addviewDidHidden];
            weakSelf.frame = CGRectMake(weakSelf.originBottomPosition.x, weakSelf.originBottomPosition.y - weakSelf.frame.size.height, weakSelf.frame.size.width, weakSelf.frame.size.height);
            
            weakSelf.alertView.hidden = NO;
            weakSelf.textInputView.hidden = YES;
        }
    };
    
    _textVoiceClick = [[ZFChatIconClickView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    _textVoiceClick.icon = [self getTextVoiceIconWithType:0];
    _textVoiceClick.clickInvocation = @{@"click":textVoiceClick};
    [_textVoiceClick creatateUI];
    [back addSubview:_textVoiceClick];
    
    x = _textVoiceClick.frame.origin.x + _textVoiceClick.frame.size.width + blank;
    h = 40.0;
    y = back.frame.size.height / 2 - h / 2;
    w = textInputW;
    
    _textInputView = [[ZFChatTextInputView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    [_textInputView makeUI];
    _textInputView.ZFChatTextInputViewTextChanged = ^(NSString * _Nonnull text) {
        if(text && [text isKindOfClass:[NSString class]]){
            if(weakSelf.zfChatToolBarTextChanged){
                weakSelf.zfChatToolBarTextChanged(text);
            }
        }
    };
    HelperObjFlagInvocation sendTextInvocation = ^void(NSString *sendText){
        
        if(weakSelf.zfChatToolBarSendMessage){
            weakSelf.zfChatToolBarSendMessage(sendText);
        }
        if(!weakSelf.isCanInputText){
            [weakSelf forbiddenInputText];
        }
    };
    
    [_textInputView addSendInvocation:@{@"click":sendTextInvocation}];
    [back addSubview:_textInputView];
    
    
    HelperIntergeObjDoubleFlagInvocation stateInvocation = ^void(NSInteger state,NSString *filePath,NSString *error){
        //type == 0 沉默状态
        //type == 1 录音状态
        //type == 2 录音中 滑动手指
        //type == 3 录音结束
        if(state == 0){
            weakSelf.voiceInputAppearView.hidden = YES;
        }
        else if(state == 1){
            weakSelf.voiceInputAppearView.hidden = NO;
            [weakSelf.voiceInputAppearView changeUIWithState:1];
        }
        else if(state == 2){
            [weakSelf.voiceInputAppearView changeUIWithState:0];
        }
        else if(state == 3){
            [weakSelf.voiceInputAppearView changeUIWithState:1];
            weakSelf.voiceInputAppearView.hidden = YES;
        }
    };
    
    HelperFloatFlagInvocation volumnClick = ^void(CGFloat volumn){
        [weakSelf.voiceInputAppearView  changeVolumn:volumn];
    };
    
    _alertView = [[ZFChatVoiceAlertView alloc] initWithFrame:_textInputView.frame];
    [_alertView creatateUI];
    _alertView.zfChatVoiceInvocation = ^(NSString * _Nonnull filePath, NSError * _Nonnull error) {
        if(weakSelf.zfChatToolBarSendVoice && !error){
            weakSelf.zfChatToolBarSendVoice(filePath);
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[error getErrorDes]];
        }
    };
    [_alertView addRecorderVolumnInvocation:@{@"click":volumnClick}];
    [_alertView addRecorderStateInvocation:@{@"click":stateInvocation}];

    [back addSubview:_alertView];
    _alertView.hidden = YES;
    
    x = _textInputView.frame.origin.x + _textInputView.frame.size.width + blank;
    h = iconW;
    w = iconW;
    y = back.frame.size.height / 2 - h / 2;
    
    HelperIntergeFlagInvocation emojiClick = ^void(NSInteger state){
        state = !state;
        if(state == 0){
            //显示表情
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:2] withState:state];
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:0] withState:0];
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:0];
            
            [weakSelf emojiViewDidHidden];
            [weakSelf addviewDidHidden];
            if(weakSelf.isCanInputText){
                [weakSelf.textInputView.text becomeFirstResponder];
            }
            else{
                [weakSelf resignToolBar];
            }
            weakSelf.alertView.hidden = YES;
            weakSelf.textInputView.hidden = NO;
        }
        else{
            //显示文本
            
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:1] withState:state];
            
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:0] withState:0];
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:0];
            [weakSelf.textInputView.text resignFirstResponder];
            [weakSelf addviewDidHidden];
            [weakSelf emojiViewDidAppear];
            weakSelf.alertView.hidden = YES;
            weakSelf.textInputView.hidden = NO;
        }
    };
    
    _emojiClick = [[ZFChatIconClickView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    _emojiClick.icon = [self getTextVoiceIconWithType:2];
    _emojiClick.clickInvocation = @{@"click":emojiClick};
    [_emojiClick creatateUI];
    [back addSubview:_emojiClick];
    
    x = _emojiClick.frame.origin.x + _emojiClick.frame.size.width + blank;
    h = iconW;
    w = iconW;
    y = back.frame.size.height / 2 - h / 2;
    
    HelperIntergeFlagInvocation addClick = ^void(NSInteger state){
        state = !state;
        if(state == 0){
            //显示add图标
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:state];
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:0] withState:0];
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:2] withState:0];
            [weakSelf emojiViewDidHidden];
            [weakSelf addviewDidHidden];
            if(weakSelf.isCanInputText){
                [weakSelf.textInputView.text becomeFirstResponder];
            }
            else{
                [weakSelf resignToolBar];
            }
            weakSelf.alertView.hidden = YES;
            weakSelf.textInputView.hidden = NO;
        }
        else{
            //显示文本
            [weakSelf.addIconClick changeIcon:[weakSelf getTextVoiceIconWithType:3] withState:state];
            [weakSelf.textVoiceClick changeIcon:[weakSelf getTextVoiceIconWithType:0] withState:0];
            [weakSelf.emojiClick changeIcon:[weakSelf getTextVoiceIconWithType:2] withState:0];
            
            [weakSelf.textInputView.text resignFirstResponder];
            
            [weakSelf emojiViewDidHidden];
            [weakSelf addViewDidAppear];
            weakSelf.alertView.hidden = YES;
            weakSelf.textInputView.hidden = NO;
            
        }
    };
    
    _addIconClick = [[ZFChatIconClickView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    _addIconClick.icon = [self getTextVoiceIconWithType:3];
    _addIconClick.clickInvocation = @{@"click":addClick};
    [_addIconClick creatateUI];
    [back addSubview:_addIconClick];
}

- (void)resignToolBar{
    
    if(self.frame.origin.y != (self.originBottomPosition.y - self.frame.size.height)){
        [self emojiViewDidHidden];
        [self addviewDidHidden];
        [self.textInputView.text resignFirstResponder];
        self.alertView.hidden = YES;
        self.frame = CGRectMake(_originBottomPosition.x, _originBottomPosition.y - self.frame.size.height, self.frame.size.width, self.frame.size.height);
    }
}

- (void)forbiddenInputText{
    [ProjectHelper helper_getMainThread:^{
        if(_textInputView){
            _textInputView.text.text = @"已禁言";
            _textInputView.text.editable = NO;
            self.isCanInputText = NO;
            
            [self resignToolBar];
        }
    }];
}

- (void)cancelForbiddenInputText{
    [ProjectHelper helper_getMainThread:^{
        if(_textInputView){
            NSString *sub = _textInputView.text.text;
            if(sub && [sub isKindOfClass:[NSString class]]){
                if(sub.length > 0){
                    NSArray *arr =  [sub componentsSeparatedByString:@"已禁言"];
                    if(arr.count > 0){
                        sub = [arr componentsJoinedByString:@""];
                    }
                }
                
                if(sub){
                    _textInputView.text.text = sub;
                }
            }
            //_textInputView.text.text = @"";
            self.isCanInputText = YES;
            _textInputView.text.editable = YES;
        }
    }];
}

- (void)apendTextToInputText:(NSString *)inputText{
    if(self.isCanInputText){
        if(inputText && [inputText isKindOfClass:[NSString class]]){
            NSString *current = self.textInputView.text.text;
            if(!(current && [current isKindOfClass:[NSString class]])){
                current = @"";
            }
            if([inputText rangeOfString:@"@"].location != NSNotFound){
                if([self.textInputView.text.text rangeOfString:inputText].location != NSNotFound){
                    return;
                }
            }
            NSString *total = [NSString stringWithFormat:@"%@%@",current,inputText];
            self.textInputView.text.text = total;
        }
    }
}

- (void)changeTextInputText:(NSString *)inputText{
    if(self.isCanInputText){
        if(inputText && [inputText isKindOfClass:[NSString class]]){
            self.textInputView.text.text = inputText;
        }
    }
}

- (void)setIsCanInputText:(BOOL)isCanInputText{
    _isCanInputText = isCanInputText;
}

- (void)adjustCharBarBackH{
    
}

#pragma mark - <关于RunLoop的方法>
//添加新的任务的方法!
-(void)addTask:(RunloopBlock)unit {
    
    [self.tasks addObject:unit];
    
    //判断一下 保证没有来得及显示的cell不会绘制图片!!
    if (self.tasks.count > 5) {
        [self.tasks removeObjectAtIndex:0];
    }
}

//回调函数
static void Callback(CFRunLoopObserverRef observer, CFRunLoopActivity activity, void *info){
    
    //从数组里面取代码!! info 就是 self
    ZFChatToolBar * vc = (__bridge ZFChatToolBar *)info;
    if (vc.tasks.count == 0) {
        CFRunLoopRemoveObserver(CFRunLoopGetCurrent(), observer, kCFRunLoopCommonModes);
        return;
    }
    BOOL result = NO;
    while (result == NO && vc.tasks.count) {
        //取出任务
        RunloopBlock unit = vc.tasks.firstObject;
        //执行任务
        result = unit();
        //干掉第一个任务
        [vc.tasks removeObjectAtIndex:0];
    }
}

//这里面都是c语言的代码
-(void)addRunloopObserver{
    //获取当前RunLoop
    CFRunLoopRef runloop = CFRunLoopGetCurrent();
    //定义一个上下文
    CFRunLoopObserverContext context = {
        0,
        (__bridge void *)(self),
        &CFRetain,
        &CFRelease,
        NULL,
    };
    //定义一个观察者
    static CFRunLoopObserverRef defaultModeObserver;
    //创建观察者
    defaultModeObserver = CFRunLoopObserverCreate(NULL, kCFRunLoopBeforeWaiting, YES, NSIntegerMax - 999, &Callback, &context);
    
    //添加当前RunLoop的观察者
    CFRunLoopAddObserver(runloop, defaultModeObserver, kCFRunLoopCommonModes);
    //C语言里面有Creat\new\copy 就需要 释放 ARC 管不了!!
    CFRelease(defaultModeObserver);
}

/**
 *  type == 0 语音输入框图标 type == 1 文本输入框图标 type == 2 表情输入图标 type == 3 更多功能图标
 */
- (UIImage *)getTextVoiceIconWithType:(NSInteger)type{
    if(type == 0){
        return [UIImage imageNamed:@"news_chat_voice@3x.png"];
    }
    else if(type == 1){
        return [UIImage imageNamed:@"news_chat_keyboard@3x.png"];
    }
    else if(type == 2){
        return [UIImage imageNamed:@"news_chat_emoticon@3x.png"];
    }
    else if(type == 3){
        return [UIImage imageNamed:@"news_chat_more@3x.png"];
    }
    return nil;
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

@interface ZFChatTextInputView ()<UITextViewDelegate>

@property (nonatomic,copy) HelperObjFlagInvocation sendInvocation;

@end

@implementation ZFChatTextInputView

- (void)makeUI{
    self.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    self.layer.cornerRadius = 8.0;
    
    _text = [ProjectHelper helper_factoryMakeTextViewWithFrame:CGRectMake(3.0, 2.0, self.frame.size.width - 6.0, self.frame.size.height - 4.0) fontSize:PROJECT_TEXT_FONT_COMMON(15.0) keybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTGRAY];
    _text.returnKeyType = UIReturnKeySend;
    _text.delegate = self;
    [self addSubview:_text];
    _text.backgroundColor = [UIColor clearColor];
}

- (void)addSendInvocation:(NSDictionary *)sendInvocation{
    if(sendInvocation[@"click"]){
        self.sendInvocation = sendInvocation[@"click"];
    }
}

- (void)appendInputTextView:(NSString *)text{
    self.text.text = [self.text.text stringByAppendingString:text];
    self.text.selectedRange = NSMakeRange(self.text.text.length - 1 , 1);
    if(self.text.contentSize.height >= self.text.frame.size.height){
        self.text.contentOffset = CGPointMake(0, self.text.contentSize.height - self.text.frame.size.height);
    }
}

- (void)textViewDidChange:(UITextView *)textView{
    if(self.ZFChatTextInputViewTextChanged){
        self.ZFChatTextInputViewTextChanged(textView.text);
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    
    if([text isEqualToString:@"\n"]){
        
        if(self.sendInvocation){
            self.sendInvocation(_text.text);
        }
        _text.text = @"";
        _text.selectedRange = NSMakeRange(0, 0);
        
        return NO;
        
    }
    return YES;
}
@end

@interface ZFChatIconClickView ()

@property (nonatomic,copy) HelperIntergeFlagInvocation click;

@property (nonatomic,strong) UIImageView *iconImg;

@property (nonatomic,strong) UIButton *clickBtn;

//textVoiceClick state == 0 显示voice icon state == 1 显示键盘 icon
//emojiClick state == 0 显示emoji icon state == 1 显示键盘 icon
//addClick state == 0 显示add icon state == 1
@property (nonatomic) NSInteger state;

@end

@implementation ZFChatIconClickView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _state = 0;
    }
    return self;
}

- (void)setClickInvocation:(NSDictionary *)clickInvocation{
    if(clickInvocation[@"click"]){
        self.click = clickInvocation[@"click"];
    }
}

- (void)creatateUI{
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat w = self.frame.size.width;
    CGFloat h = self.frame.size.height;
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(x, y, w, h) andImg:_icon];
    [self addSubview:icon];
    _iconImg = icon;
    
    UIButton *iconBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:icon.frame target:self method:@selector(clearBtnClickMethod:)];
    [self addSubview:iconBtn];
    _clickBtn = iconBtn;
}

- (void)changeIcon:(UIImage *)icon withState:(NSInteger)state{
    
    _iconImg.image = icon;
    _state = state;
}

- (void)clearBtnClickMethod:(UIButton *)btn{
    WS(weakSelf);
    if(self.click){
        self.click(weakSelf.state);
    }
}

@end

#import <AVFoundation/AVFoundation.h>

@interface ZFChatVoiceAlertView ()<UIGestureRecognizerDelegate,AVAudioRecorderDelegate>

//state voideLum error,text
@property (nonatomic,copy) HelperIntergeObjDoubleFlagInvocation stateClick;
@property (nonatomic,copy) HelperFloatFlagInvocation volumnClick;

@property (nonatomic,strong) UILabel *alertLab;

//type == 0 沉默状态
//type == 1 录音状态
//type == 2 录音中 滑动手指
//type == 3 录音结束
//type == 4 录音失败
@property (nonatomic) NSInteger state;
@property(nonatomic,strong) ZFChatRecorder *recorder;
@property(nonatomic,assign) NSTimer *timer;
@property(nonatomic,assign) NSInteger timerNum;
@end



#define VoiceLevel_Timeterval 0.15
@implementation ZFChatVoiceAlertView

- (void)dealloc{
    [_timer invalidate];
    _timer = nil;
}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        self.layer.cornerRadius = 8.0;
        self.layer.borderColor = PROJECT_COLOR_TEXTCOLOR_BLACK.CGColor;
        self.layer.borderWidth = 0.5;
        _state = 0;
        
        _timerNum = 0;
    }
    return self;
}

- (ZFChatRecorder *)recorder{
    if(!_recorder){
        ZFChatRecorder *recorder = [[ZFChatRecorder alloc] init];
        _recorder = recorder;
    }
    return _recorder;
}

- (void)creatateUI{
    _alertLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    _alertLab.text = [self getTextWithState:_state];
    _alertLab.userInteractionEnabled = NO;
    [self addSubview:_alertLab];
    
}

- (void)addRecorderStateInvocation:(NSDictionary *)invocation{
    if(invocation[@"click"]){
        self.stateClick = invocation[@"click"];
    }
}

- (void)addRecorderVolumnInvocation:(NSDictionary *)invocation{
    if(invocation[@"click"]){
        self.volumnClick = invocation[@"click"];
    }
}

- (void)startTimer{
    [_timer invalidate];
    _timer = nil;
    
    _timer = [NSTimer scheduledTimerWithTimeInterval:VoiceLevel_Timeterval target:self selector:@selector(timerMethod:) userInfo:nil repeats:YES];

}

- (void)timerMethod:(NSTimer *)timer{
    
    _timerNum ++;
    
    if(self.volumnClick){
        self.volumnClick([self getVoiceluman].floatValue);
    }
    
    if(_recorder){
        if(_recorder.recorder){
            if(_recorder.recorder.currentTime >= 59){
                //到时间了 自动发送
                [_recorder.recorder stop];
                [_timer invalidate];
                _timer = nil;
                
            }
        }
    }
}

- (void)changeUIWithState:(NSInteger)state{
    _state = state;
    _alertLab.text = [self getTextWithState:state];
    self.backgroundColor = [self getBackColor:state];
}

- (void)didBeginRecorder{
    if(self.stateClick){
        self.stateClick(1,nil,nil);
    }
    
    [self changeUIWithState:1];
    
    [self.recorder changeRecorderPath:[ZFChatStorageHelper zfChatStorageHelper_getChatVoiceRecorderPath]];
    self.recorder.recorder.delegate = self;
    
    [self.recorder.recorder prepareToRecord];
    BOOL sucess = [self.recorder.recorder record];
    if(sucess){
        [self startTimer];
    }
    else{
        [_timer invalidate];
        _timer = nil;
        
        [_recorder.recorder stop];
        
        [self changeUIWithState:4];
    }
    
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
   
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    if(point.y >= 0){
       // [self didBeginRecorder];
    }
}

- (UIView*)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    
    // 如果交互未打开，或者透明度小于0.05 或者 视图被隐藏
    if (self.userInteractionEnabled == NO || self.alpha < 0.05 || self.hidden == YES)
    {
        return nil;
    }
    
    // 如果 touch 的point 在 self 的bounds 内
    if ([self pointInside:point withEvent:event])
    {
        
         [self didBeginRecorder];
        for (UIView *subView in self.subviews)
        {

            //进行坐标转化
            CGPoint coverPoint = [subView convertPoint:point fromView:self];

            // 调用子视图的 hitTest 重复上面的步骤。找到了，返回hitTest view ,没找到返回有自身处理
            UIView *hitTestView = [subView hitTest:coverPoint withEvent:event];

            if (hitTestView)
            {
                return hitTestView;
            }
        }
        
        return self;
    }
    
    return nil;
}

-(NSNumber *)getVoiceluman{
    if (_recorder.recorder.isRecording)
    {
        [_recorder.recorder updateMeters];
        float peakPower = [_recorder.recorder peakPowerForChannel:0];
        NSNumber *num = [NSNumber numberWithFloat: - peakPower];
        return num;
        //-160 – 0
    }
    return [NSNumber numberWithFloat:0];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    
    if(point.y >= 5){
        [self changeUIWithState:1];
        if(self.stateClick){
            self.stateClick(1,nil,nil);
        }
    }
    else{
        [self changeUIWithState:2];
        if(self.stateClick){
            self.stateClick(2,nil,nil);
        }
    }
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    
    [self changeUIWithState:3];
    
    [ProjectHelper helper_getGlobalThread:^{
        [_timer invalidate];
        _timer = nil;
        [_recorder.recorder stop];
    }];
    
    if(point.y < 5){
        _timerNum = 0;
    }
    
    if(self.stateClick){
        self.stateClick(3,nil,nil);
    }
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self changeUIWithState:0];
    
    if(self.stateClick){
        self.stateClick(0,nil,nil);
    }
    
    [ProjectHelper helper_getGlobalThread:^{
        [_timer invalidate];
        _timer = nil;
        [_recorder.recorder stop];
    }];
}

- (NSString *)getTextWithState:(NSInteger)state{
    //type == 0 沉默状态
    //type == 1 录音状态
    //type == 2 录音中 滑动手指
    //type == 3 录音结束
    //type == 4 录音失败
    if(state == 0){
        return @"按住 说话";
    }
    else if(state == 1){
        return @"松开 结束";
    }
    else if(state == 2){
        return @"松开 结束";
    }
    else if(state == 3){
        return @"按住 说话";
    }
    else if(state == 4){
        return @"按住 说话";
    }
    return @"按住 说话";
}

- (UIColor *)getBackColor:(NSInteger)state{
    if(state == 0){
        return [UIColor whiteColor];
    }
    else if(state == 1){
        return PROJECT_COLOR_TEXTGRAY;
    }
    else if(state == 2){
        return PROJECT_COLOR_TEXTGRAY;
    }
    else if(state == 3){
        return [UIColor whiteColor];
    }
    else if(state == 4){
        return [UIColor whiteColor];
    }
    return [UIColor whiteColor];
}

- (void)changeAlertText:(NSString *)changeAlertText{
    _alertLab.text = changeAlertText;
}


- (void)audioRecorderDidFinishRecording:(AVAudioRecorder *)recorder successfully:(BOOL)flag{
   
    if(flag){
        if(self.zfChatVoiceInvocation && _timerNum > 1){
            self.zfChatVoiceInvocation(_recorder.filePath, nil);
        }
        else{
             self.zfChatVoiceInvocation(nil, nil);
        }
    }
    else{
        if(self.zfChatVoiceInvocation && _timerNum > 1){
            self.zfChatVoiceInvocation(nil, [ProjectHelper helper_CreateErrorWithDes:@"录音出错"]);
        }
    }
}

- (void)audioRecorderEncodeErrorDidOccur:(AVAudioRecorder *)recorder error:(NSError * __nullable)error{
    if(self.zfChatVoiceInvocation && _timerNum > 1){
        self.zfChatVoiceInvocation(nil, error);
    }
}
@end

