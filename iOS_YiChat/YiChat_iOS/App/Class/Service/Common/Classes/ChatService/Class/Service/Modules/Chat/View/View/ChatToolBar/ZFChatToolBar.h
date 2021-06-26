//
//  ZFChatToolBar.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatToolBar : UIView

@property (nonatomic,assign) UIView *originBackView;

@property (nonatomic,copy) void(^zfChatToolBarSendMessage)(id messageText);
@property (nonatomic,copy) void(^zfChatToolBarSendVoice)(id messageVoice);
@property (nonatomic,copy) void(^zfChatToolBarAddViewSelecte)(NSInteger row,NSString *title);
@property (nonatomic,copy) void(^zfChatToolBarTextChanged)(NSString *text);

- (id)initWithFrame:(CGRect)frame;

- (void)makeUI;

- (void)changeAddViewWithPower:(NSInteger)power;
    
- (void)changeAddViewWithSingleChat;

- (void)resignToolBar;

- (void)forbiddenInputText;

- (void)cancelForbiddenInputText;

- (void)apendTextToInputText:(NSString *)inputText;

- (void)changeTextInputText:(NSString *)inputText;

@end

@interface ZFChatTextInputView : UIView

@property (nonatomic,strong) UITextView *text;

@property (nonatomic,copy) void(^ZFChatTextInputViewTextChanged)(NSString *text);


- (void)addSendInvocation:(NSDictionary *)sendInvocation;

- (void)makeUI;

- (void)appendInputTextView:(NSString *)text;


@end

@interface ZFChatIconClickView : UIView

@property (nonatomic,strong) UIImage *icon;
@property (nonatomic,strong) NSDictionary *clickInvocation;

- (void)creatateUI;

- (void)changeIcon:(UIImage *)icon withState:(NSInteger)state;

@end

@interface ZFChatVoiceAlertView : UIView

@property (nonatomic,copy) void(^zfChatVoiceInvocation)(NSString *filePath,NSError *error);

- (void)creatateUI;

- (void)addRecorderStateInvocation:(NSDictionary *)invocation;

- (void)addRecorderVolumnInvocation:(NSDictionary *)invocation;
@end

NS_ASSUME_NONNULL_END
