//
//  YiChatPasswordTextView.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/8.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPasswordTextView.h"

@interface YiChatPasswordTextView ()
@property(nonatomic, weak) UITextField *textField;
@property (nonatomic, strong) NSMutableArray<UITextField *> *dataSource;
@end

@implementation YiChatPasswordTextView

#pragma mark - lazy
- (NSMutableArray *)dataSource {
    if (_dataSource == nil) {
        _dataSource = [NSMutableArray array];
    }
    return _dataSource;
}


#pragma mark - initialization
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        UITextField *textField = [[UITextField alloc] initWithFrame:self.bounds];
        textField.hidden = YES;
        textField.keyboardType = UIKeyboardTypeNumberPad;
        [textField addTarget:self action:@selector(textChange:) forControlEvents:UIControlEventEditingChanged];
        [self addSubview:textField];
        self.textField = textField;
        self.autoHideKeyboard = YES;
        self.elementBorderColor = [UIColor blackColor];
        self.backgroundColor = [UIColor whiteColor];
        self.elementBorderWidth = 1;
    }
    return self;
}

- (void)setElementCount:(NSInteger)elementCount {
    _elementCount = elementCount;
    if (elementCount <= 0) {
        return;
    }
    
    if (self.dataSource.count > 0) {
        [self.dataSource enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            [NSObject cancelPreviousPerformRequestsWithTarget:obj selector:@selector(removeFromSuperview) object:nil];
        }];
        
        [self.dataSource makeObjectsPerformSelector:@selector(removeFromSuperview)];
        
        [self.dataSource removeAllObjects];
    }
    
    for (int i = 0; i < self.elementCount; i++)
    {
        UITextField *pwdTextField = [[UITextField alloc] init];
        pwdTextField.enabled = NO;
        pwdTextField.textAlignment = NSTextAlignmentCenter;//居中
        pwdTextField.secureTextEntry = YES;//设置密码模式
        pwdTextField.userInteractionEnabled = NO;
        [self insertSubview:pwdTextField belowSubview:self.textField];
        [self.dataSource addObject:pwdTextField];
    }
}

- (void)setElementMargin:(CGFloat)elementMargin {
    _elementMargin = elementMargin;
    [self setNeedsLayout];
    [self setNeedsDisplay];
}

#pragma mark - publick method
- (void)clearPassword {
    self.textField.text = nil;
    [self textChange:self.textField];
}

- (void)showKeyboard {
    [self.textField becomeFirstResponder];
}

- (void)hideKeyboard {
    [self.textField resignFirstResponder];
}

#pragma mark - 文本框内容改变
- (void)textChange:(UITextField *)textField {
    NSString *password = textField.text;
    if (password.length > self.elementCount) {
        return;
    }
    
    for (int i = 0; i < self.dataSource.count; i++)
    {
        UITextField *pwdTextField= [self.dataSource objectAtIndex:i];
        if (i < password.length) {
            NSString *pwd = [password substringWithRange:NSMakeRange(i, 1)];
            pwdTextField.text = pwd;
        } else {
            pwdTextField.text = nil;
        }
        
    }
    
    if (password.length == self.dataSource.count)
    {
        if (self.autoHideKeyboard) {
            [self hideKeyboard];//隐藏键盘
        }
    }
    
    !self.passwordDidChangeBlock ? : self.passwordDidChangeBlock(textField.text);
    
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat w = (self.bounds.size.width - (self.elementCount - 1) * self.elementMargin) / self.elementCount;
    CGFloat h = self.bounds.size.height;
    for (NSUInteger i = 0; i < self.dataSource.count; i++) {
        UITextField *pwdTextField = [self.dataSource objectAtIndex:i];
        x = i * (w + self.elementMargin);
        pwdTextField.frame = CGRectMake(x, y, w, h);
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self showKeyboard];
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    [super drawRect:rect];
    // Drawing code
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    [self.backgroundColor set];
    CGContextFillRect(context, rect);
    
    CGContextSetLineCap(context, kCGLineCapSquare);
    
    CGContextSetLineWidth(context, self.elementBorderWidth);
    
    CGContextSetStrokeColorWithColor(context, self.elementBorderColor.CGColor);
    CGContextSetFillColorWithColor(context, [UIColor clearColor].CGColor);
    
    CGContextBeginPath(context);
    if (self.elementMargin != 0) {
        for (UITextField *textField in self.dataSource) {
            CGRect rect = CGRectInset(textField.frame, self.elementBorderWidth, self.elementBorderWidth);
            CGFloat left = rect.origin.x;
            CGFloat right = rect.origin.x + rect.size.width;
            CGFloat top = rect.origin.y;
            CGFloat bottom = rect.origin.y + rect.size.height;
            CGContextMoveToPoint(context, left, top);
            CGContextAddLineToPoint(context, right, top);
            CGContextAddLineToPoint(context, right, bottom);
            CGContextAddLineToPoint(context, left, bottom);
            CGContextClosePath(context);
        }
    }else {
        CGPoint leftTopPoint, rightTopPoint, leftBottomPoint, rightBottomPoint;
        for (NSUInteger i = 0; i < self.dataSource.count; i++) {
            UITextField *textField = [self.dataSource objectAtIndex:i];
            CGRect rect = CGRectInset(textField.frame, self.elementBorderWidth, self.elementBorderWidth);
            CGFloat left = rect.origin.x;
            CGFloat right = rect.origin.x + rect.size.width;
            CGFloat top = rect.origin.y;
            CGFloat bottom = rect.origin.y + rect.size.height;
            
            CGContextMoveToPoint(context, left, top);
            CGContextAddLineToPoint(context, left, bottom);
            CGContextClosePath(context);
            if (self.dataSource.count - 1 == i) {
                CGContextMoveToPoint(context, right, top);
                CGContextAddLineToPoint(context, right, bottom);
                CGContextClosePath(context);
                rightTopPoint = CGPointMake(right, top);
                rightBottomPoint = CGPointMake(right, bottom);
            }else if (0 == i) {
                leftTopPoint = CGPointMake(left, top);
                leftBottomPoint = CGPointMake(left, bottom);
            }
        }
        
        CGContextMoveToPoint(context, leftTopPoint.x, leftTopPoint.y);
        CGContextAddLineToPoint(context, rightTopPoint.x, rightTopPoint.y);
        CGContextClosePath(context);
        
        CGContextMoveToPoint(context, leftBottomPoint.x, leftBottomPoint.y);
        CGContextAddLineToPoint(context, rightBottomPoint.x, rightBottomPoint.y);
        CGContextClosePath(context);
    }
    
    CGContextStrokePath(context);
}
@end
