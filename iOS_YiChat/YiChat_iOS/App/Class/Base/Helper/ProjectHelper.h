//
//  ProjectHelper.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <objc/message.h>

NS_ASSUME_NONNULL_BEGIN

static inline UIEdgeInsets xy_safeAreaInset(UIView *view) {
    if (@available(iOS 11.0, *)) {
        return view.safeAreaInsets;
    }
    return UIEdgeInsetsZero;
}

typedef void(^HelperInvocation) (void);
typedef BOOL(^HelperReturnBOOLInvocation) (void);
typedef id(^HelperReturnInvocation)(void);
typedef id(^HelperReturnInvocationWithFlag)(id);
typedef void(^HelperBoolFlagInvocation)(BOOL);
typedef void(^HelperintergerObjFlagInvocation)(NSInteger, id);
typedef void(^HelperIntergeFlagInvocation)(NSInteger);
typedef void(^HelperFloatFlagInvocation)(CGFloat);
typedef void(^HelperIntergeObjDoubleFlagInvocation)(NSInteger, id, id);
typedef void(^HelperIntergeBoolFlagInvocation)(NSInteger,BOOL);
typedef void(^HelperObjDoubleFlagInvocation)(id, id);
typedef void(^HelperObjFlagInvocation)(id);


#define Helper_ImageSizeLimit 1024 * 1024 * 0.3

@class AppDelegate;

@interface ProjectHelper : NSObject

#pragma mark system

+ (NSString *)helper_getDeviceId;

+ (NSString *)helper_getAppVersionCode;

+ (NSString *)helper_getDeviceVersion;

+ (NSString *)helper_getDeviceModel;

+ (AppDelegate *)helper_getAppDelegate;

+ (id)helper_getVCWithName:(NSString *)name initialMethod:(SEL)initialMethod;

+ (id)helper_getVCWithName:(NSString *)name initialMethod:(SEL)initialMethod flags:(NSArray *)flags;


+ (CGSize)helper_getChatTextSizeWithChatText:(NSAttributedString *)string limiteW:(CGFloat)w font:(UIFont *)font;

+ (id)helper_performInstanceSelectorWithTarget:(id)target initialMethod:(SEL)initialMethod flags:(NSArray *)flags;

/**
 *  从一张图片中截取中心区域 生成正方形图片
 */
+ (UIImage *)helper_getSquareIconFromImage:(UIImage *)image;

+ (id)helper_performClassSelectorWithClass:(NSString *)name initialMethod:(SEL)initialMethod flags:(NSArray *)flags;

/**
 *  根据图片比例(宽/高) 给出高或者宽 返回成比例的高或者宽
 */
+ (CGFloat)helper_GetWidthOrHeightIntoScale:(CGFloat)scale width:(CGFloat)width height:(CGFloat)height;

/**
 *  按比例适配 获取适配后的h
 */
+ (CGFloat)helper_getScreenSuitable_H:(CGFloat)h;

/**
 *  按比例适配 获取适配后的w
 */
+ (CGFloat)helper_getScreenSuitable_W:(CGFloat)w;

/**
 * 根据显示类容计算行高 factory
 */
+ (CGRect)helper_getFontSizeWithString:(NSString *)string useFont:(int )font withWidth:(CGFloat)width andHeight:(CGFloat)height;

+ (CGRect)helper_getFontSizeWithString:(NSString *)string useSetFont:(UIFont *)font withWidth:(CGFloat)width andHeight:(CGFloat)height;

+ (UIView *)helper_GetKeyboardView;

#pragma mark factory

+ (id)helper_CreateErrorWithDes:(NSString *)des;

/**
 *  UIWindow factory
 */
+ (UIWindow *)helper_factoryMakeWindow;

/**
 * UIView factory
 */
+ (UIView *)helper_factoryMakeViewWithFrame:(CGRect)rect backGroundColor:(UIColor *)color;

/**
 * UILabel factory
 */
+ (UILabel *)helper_factoryMakeLabelWithFrame:(CGRect)rect andfont:(UIFont *)fontSize textColor:(UIColor *)color textAlignment:(NSTextAlignment)textAlignment;

/**
 * UIButton factory
 */
+ (UIButton *)helper_factoryMakeButtonWithFrame:(CGRect)rect andBtnType:(UIButtonType)type;

/**
 * clear btn
 */
+ (UIButton *)helper_factoryMakeClearButtonWithFrame:(CGRect)rect target:(id)object method:(SEL)method;

/**
 * UIButton 圆角按钮factory
 */
+  (UIButton *)helper_factoryMakeCircleBtnWithFrame:(CGRect)rect andBtnType:(UIButtonType)type title:(NSString *)title titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)backgroundColor;

/**
 * UIImageView factory
 */
+ (UIImageView *)helper_factoryMakeImageViewWithFrame:(CGRect)rect andImg:(UIImage *)img;

/**
 * UITextView factory
 */
+ (UITextView *)helper_factoryMakeTextViewWithFrame:(CGRect)rect  fontSize:(UIFont *)size keybordType:(UIKeyboardType)type textColor:(UIColor *)color;

/**
 * UITextFiled factory
 */
+ (UITextField *)helper_factoryMakeTextFieldWithFrame:(CGRect)rect withPlaceholder:(NSString *)holder fontSize:(UIFont *)size isClearButtonMode:(UITextFieldViewMode)mode andKeybordType:(UIKeyboardType)type textColor:(UIColor *)color;

/**
 * UIScrollView factory
 */
+ (UIScrollView *)helper_factoryMakeScrollViewWithFrame:(CGRect)frame contentSize:(CGSize)size pagingEnabled:(BOOL)enabled showsHorizontalScrollIndicator:(BOOL)horizontall showsVerticalScrollIndicator:(BOOL)vertical scrollEnabled:(BOOL)scrollEnabled;

/**
 * UITableView factory
 */
+ (UITableView *)helper_factoryMakeTableViewWithFrame:(CGRect)rect backgroundColor:(UIColor *)backgroundColor style:(UITableViewStyle)style  bounces:(BOOL)bounce pageEnabled:(BOOL)enabled superView:(UIView *)view object:(id)object;

/**
 * UIImage largeImage factory
 */
+ (UIImage *)helper_factoryGetImageIntoContentFileWithResource:(NSString *)resource andType:(NSString *)type;

/**
 * UIImage smallImage factory
 */
+ (UIImage *)helper_factoryGetImageIntoNamedWithResource:(NSString *)resource andType:(NSString *)type;

/**
 * 横线
 * 提供的position y 为横线目标所在位置的y
 */
+ (UIView *)helper_factoryMakeHorizontalLineWithPoint:(CGPoint)positionPoint width:(CGFloat)width;

/**
 * 竖线
 * 提供的position x y 为竖线目标所在位置的 x y
 */
+ (UIView *)helper_factoryMakeVerticalLineWithPoint:(CGPoint)positionPoint height:(CGFloat)height;

/**
 * three
 */
+ (NSMutableAttributedString *)helper_factoryMakeAttributedStringWithThreeDifferentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(CGFloat)textLeftFont font:(CGFloat)textCenterFont andFont:(CGFloat)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text;

/**
 *  two
 */
+ (NSMutableAttributedString *)helper_factoryMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:(NSRange)range font:(CGFloat)textLeftFont  andFont:(CGFloat)textRightFont color:(UIColor *)leftColor  color:(UIColor *)rightColor  withText:(NSString *)text;


+ (NSMutableAttributedString *)helper_factoryFontMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:(NSRange)range font:(UIFont *)textLeftFont  andFont:(UIFont *)textRightFont color:(UIColor *)leftColor  color:(UIColor *)rightColor  withText:(NSString *)text;

/**
 * three
 */
+ (NSMutableAttributedString *)helper_factoryFontMakeAttributedStringWithThreeDifferentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(UIFont *)textLeftFont font:(UIFont *)textCenterFont andFont:(UIFont *)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text;

+ (NSMutableAttributedString *)helper_createFullUrlStrWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color;

+ (void)helper_addFullUrlWithAttributeStr:(NSMutableAttributedString *)attribute color:(UIColor *)color range:(NSRange)range url:(NSString *)url;

+ (void)helper_getMainThread:(HelperInvocation)invocation;

+ (void)helper_getGlobalThread:(HelperInvocation)invocation;

/**
 *  将unix时间戳转换成月日时分秒str
 */
+ (NSString *)helper_getUnixTimeToRealTimeStr:(NSInteger)unixValue;

/**
 *  生成当前时间时间戳
 */
+ (NSString*)helper_GetCurrentTimeString;

+ (NSTimeInterval)helper_GetCurrentTimeUnixTime;

+ (NSInteger)helper_GetRandowNum;

+ (NSInteger)helper_GetSomeMonthDaysNumWithYear:(NSString *)year month:(NSString *)month;

+ (NSString *)helper_getPropertyType:(id)obj key:(NSString *)propertyName;

+ (UIImage *)helper_productQRCodeWithContent:(NSString *)content
                               codeImageSize:(CGFloat)size;

+ (NSString *)helper_getSDWebImageLoadUrlWithUrl:(NSString *)url;
    
+ (void)projectHelper_asyncLoadNetImage:(NSString *)url imageView:(UIImageView *)icon placeHolder:(UIImage *)placeholder invocation:(NSString *(^)(void))invocation;

#pragma mark  json

@end

NS_ASSUME_NONNULL_END
