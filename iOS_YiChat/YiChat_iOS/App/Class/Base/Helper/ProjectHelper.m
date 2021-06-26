//
//  ProjectHelper.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectHelper.h"

#import "ProjectDef.h"
#import "YRGeneralApis.h"
#import <objc/message.h>
#import "NSMutableAttributedString+AddFullUrl.h"
#import "AppDelegate.h"
#import "NSError+DefaultError.h"

#define helper_Name_TabVcsArr @"helper_Name_TabVcsArr"

@implementation ProjectHelper

#pragma mark system

+ (NSString *)helper_getDeviceId{
    return [YRGeneralApis yrGeneralApis_getDeviceId];
}

+ (NSString *)helper_getAppVersionCode{
    return [YRGeneralApis yrGeneralApis_getAppVersionCode];
}

+ (NSString *)helper_getDeviceVersion{
    return [YRGeneralApis yrGeneralApis_getDeviceVersion];
}

+ (NSString *)helper_getDeviceModel{
    return [YRGeneralApis yrGeneralApis_getDeviceModel];
}

+ (AppDelegate *)helper_getAppDelegate{
    return  (AppDelegate *)[[UIApplication sharedApplication] delegate];
}

+ (id)helper_getVCWithName:(NSString *)name initialMethod:(SEL)initialMethod{
    Class vcClass = NSClassFromString(name);
    
    if([vcClass respondsToSelector:initialMethod]){
        return  ((id(*)(id, SEL))objc_msgSend)(vcClass, initialMethod);
    }
    else{
        return nil;
    }
}

+ (id)helper_getVCWithName:(NSString *)name initialMethod:(SEL)initialMethod flags:(NSArray *)flags{
    return [self helper_performClassSelectorWithClass:name initialMethod:initialMethod flags:flags];
}

+ (id)helper_performInstanceSelectorWithTarget:(id)target initialMethod:(SEL)initialMethod flags:(NSArray *)flags{
    Class class = [target class];
    NSMethodSignature *sigOfPrintStr = [class instanceMethodSignatureForSelector:initialMethod];
    if (!sigOfPrintStr) {
        return nil;
    }
    return [self buildInvocationWithTarget:target selector:initialMethod flags:flags sign:sigOfPrintStr];
}

+ (id)helper_performClassSelectorWithClass:(NSString *)name initialMethod:(SEL)initialMethod flags:(NSArray *)flags{
    Class class = NSClassFromString(name);
    NSMethodSignature *sigOfPrintStr = [class methodSignatureForSelector:initialMethod];
    if (!sigOfPrintStr) {
        return nil;
    }
    return [self buildInvocationWithTarget:class selector:initialMethod flags:flags sign:sigOfPrintStr];
}

+ (id)buildInvocationWithTarget:(id)target selector:(SEL)selector flags:(NSArray *)flags sign:(NSMethodSignature *)sigOfPrintStr{
    //获取方法签名对应的invocation
    NSInvocation *invocationOfPrintStr = [NSInvocation invocationWithMethodSignature:sigOfPrintStr];
    
    [invocationOfPrintStr setTarget:target];
    
    [invocationOfPrintStr setSelector:selector];
    
    // 参数个数signature.numberOfArguments 默认有一个_cmd 一个target 所以要-2
    NSInteger paramsCount = sigOfPrintStr.numberOfArguments - 2;
    
    // 当objects的个数多于函数的参数的时候,取前面的参数
    // 当objects的个数少于函数的参数的时候,不需要设置,默认为nil
    paramsCount = MIN(paramsCount, flags.count);
    
    for (int i = 0; i<paramsCount; i++) {
        id object = flags[i];
        // 对参数为nil的处理
        if ([object isKindOfClass:[NSNull class]]) {
            continue;
        }
        [invocationOfPrintStr setArgument:&object atIndex:i + 2];
    }
    [invocationOfPrintStr retainArguments];
    
    [invocationOfPrintStr invoke];
    
    //获得返回值类型
    const char *returnType = sigOfPrintStr.methodReturnType;
    //声明返回值变量
    // id __unsafe_unretained returnValue = nil;
    __autoreleasing id returnValue = nil;
    //如果没有返回值，也就是消息声明为void，那么returnValue=nil
    if( !strcmp(returnType, @encode(void)) ){
        returnValue =  nil;
    }
    //如果返回值为对象，那么为变量赋值
    else if( !strcmp(returnType, @encode(id)) ){
        [invocationOfPrintStr getReturnValue:&returnValue];
    }
    else{
        //如果返回值为普通类型NSInteger  BOOL
        
        //返回值长度
        NSUInteger length = [sigOfPrintStr methodReturnLength];
        //根据长度申请内存
        void *buffer = (void *)malloc(length);
        //为变量赋值
        [invocationOfPrintStr getReturnValue:buffer];
        
        if( !strcmp(returnType, @encode(BOOL)) ) {
            returnValue = [NSNumber numberWithBool:*((BOOL*)buffer)];
        }
        else if( !strcmp(returnType, @encode(NSInteger)) ){
            returnValue = [NSNumber numberWithInteger:*((NSInteger*)buffer)];
        }
        else if( !strcmp(returnType, @encode(float)) ){
            returnValue = [NSNumber numberWithFloat:*((float*)buffer)];
        }
        else if( !strcmp(returnType, @encode(double)) ){
            returnValue = [NSNumber numberWithDouble:*((double*)buffer)];
        }
        
    }
    
    return returnValue;
}

/**
 *  从一张图片中截取中心区域 生成正方形图片
 */
+ (UIImage *)helper_getSquareIconFromImage:(UIImage *)image{
    CGFloat w = 0;
    CGFloat h = 0;
    if(image.size.width > image.size.height){
        h = image.size.height;
        w = h;
        
    }
    else{
        w = image.size.width;
        h = w;
    }
    
    CGImageRef cgRef = image.CGImage;
    CGImageRef imageRef = CGImageCreateWithImageInRect(cgRef, CGRectMake(image.size.width / 2 - w / 2,image.size.height / 2 - h/ 2, w, h));
    UIImage *thumbScale = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);
    return thumbScale;
}


+ (CGSize)helper_getChatTextSizeWithChatText:(NSAttributedString *)string limiteW:(CGFloat)w font:(UIFont *)font{
    NSInteger maxW = w;
    UILabel *lab = [[UILabel alloc] init];
    lab.font = font;
    lab.attributedText = string;
    lab.numberOfLines = 0;
    CGSize size = [lab sizeThatFits:CGSizeMake(maxW,MAXFLOAT)];
    
    CGRect rect = CGRectZero;
    
    if(size.width < maxW){
        rect =  CGRectMake(0, 0, size.width, size.height);
    }
    else{
        rect =  CGRectMake(0, 0, maxW, size.height);
    }
    return rect.size;
}

/**
 *  根据图片比例(宽/高) 给出高或者宽 返回成比例的高或者宽
 */
+ (CGFloat)helper_GetWidthOrHeightIntoScale:(CGFloat)scale width:(CGFloat)width height:(CGFloat)height{
    return [YRGeneralApis yrGenral_ApisGetWidthOrHeightIntoScale:scale width:width height:height];
}

/**
 *  按比例适配 获取适配后的h
 */
+ (CGFloat)helper_getScreenSuitable_H:(CGFloat)h{
    return[YRGeneralApis yrGeneralApisGetScreenSuitable_H:h];
}

/**
 *  按比例适配 获取适配后的w
 */
+ (CGFloat)helper_getScreenSuitable_W:(CGFloat)w{
    return [YRGeneralApis yrGeneralApisGetScreenSuitable_W:w];
}

/**
 * 根据显示类容计算行高 factory
 */
+ (CGRect)helper_getFontSizeWithString:(NSString *)string useFont:(int )font withWidth:(CGFloat)width andHeight:(CGFloat)height{
    return [YRGeneralApis yrGeneralApis_FactoryGetFontSizeWithString:string useFont:font withWidth:width andHeight:height];
}

+ (CGRect)helper_getFontSizeWithString:(NSString *)string useSetFont:(UIFont *)font withWidth:(CGFloat)width andHeight:(CGFloat)height{
    if(string && [string isKindOfClass:[NSString class]]){
        if(font == nil){
            font = [UIFont systemFontOfSize:12.0];
        }
        NSDictionary *dic=@{NSFontAttributeName:font};
        CGRect rect=[string boundingRectWithSize:CGSizeMake(width, height) options:NSStringDrawingUsesLineFragmentOrigin attributes:dic context:nil];
        return rect;
    }
    return CGRectZero;
   
}

+ (UIView *)helper_GetKeyboardView{
    return [YRGeneralApis yrGeneralApis_FactoryGetKeyboardView];
}

+ (id)helper_CreateErrorWithDes:(NSString *)des{
    NSError *error = [NSError errorWithDes:des];
    
    return error;
}

/**
 *  UIWindow factory
 */
+ (UIWindow *)helper_factoryMakeWindow{
    return [YRGeneralApis yrGeneralApis_FactoryMakeWindow];
}

/**
 * UIView factory
 */
+ (UIView *)helper_factoryMakeViewWithFrame:(CGRect)rect backGroundColor:(UIColor *)color{
    return [YRGeneralApis yrGeneralApis_FactoryMakeViewWithFrame:rect backGroundColor:color];
}

/**
 * UILabel factory
 */
+ (UILabel *)helper_factoryMakeLabelWithFrame:(CGRect)rect andfont:(UIFont *)fontSize textColor:(UIColor *)color textAlignment:(NSTextAlignment)textAlignment{
    UILabel *lab = [YRGeneralApis yrGeneralApis_FactoryMakeLabelWithFrame:rect andfont:fontSize textColor:color textAlignment:textAlignment];
    return lab;
}

/**
 * UIButton factory
 */
+ (UIButton *)helper_factoryMakeButtonWithFrame:(CGRect)rect andBtnType:(UIButtonType)type{
    return [YRGeneralApis yrGeneralApis_FactoryMakeButtonWithFrame:rect andBtnType:type];
}

/**
 * clear btn
 */
+ (UIButton *)helper_factoryMakeClearButtonWithFrame:(CGRect)rect target:(id)object method:(SEL)method{
    return [YRGeneralApis yrGeneralApis_FactoryMakeClearButtonWithFrame:rect target:object method:method];
}

/**
 * UIButton 圆角按钮factory
 */
+  (UIButton *)helper_factoryMakeCircleBtnWithFrame:(CGRect)rect andBtnType:(UIButtonType)type title:(NSString *)title titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)backgroundColor{
    return [YRGeneralApis yrGeneralApis_FactoryMakeCircleBtnWithFrame:rect andBtnType:type title:title titleColor:titleColor backgroundColor:backgroundColor];
}

/**
 * UIImageView factory
 */
+ (UIImageView *)helper_factoryMakeImageViewWithFrame:(CGRect)rect andImg:(UIImage *)img{
    return [YRGeneralApis yrGeneralApis_FactoryMakeImageViewWithFrame:rect andImg:img];
}

/**
 * UITextView factory
 */
+ (UITextView *)helper_factoryMakeTextViewWithFrame:(CGRect)rect  fontSize:(UIFont *)size keybordType:(UIKeyboardType)type textColor:(UIColor *)color{
    return [YRGeneralApis yrGeneralApis_FactoryMakeTextViewWithFrame:rect fontSize:size  keybordType:type textColor:color];
}
/**
 * UITextFiled factory
 */
+ (UITextField *)helper_factoryMakeTextFieldWithFrame:(CGRect)rect withPlaceholder:(NSString *)holder fontSize:(UIFont *)size isClearButtonMode:(UITextFieldViewMode)mode andKeybordType:(UIKeyboardType)type textColor:(UIColor *)color{
    return [YRGeneralApis yrGeneralApis_FactoryMakeTextFieldWithFrame:rect withPlaceholder:holder fontSize:size isClearButtonMode:mode andKeybordType:type textColor:color];
}

/**
 * UIScrollView factory
 */
+ (UIScrollView *)helper_factoryMakeScrollViewWithFrame:(CGRect)frame contentSize:(CGSize)size pagingEnabled:(BOOL)enabled showsHorizontalScrollIndicator:(BOOL)horizontall showsVerticalScrollIndicator:(BOOL)vertical scrollEnabled:(BOOL)scrollEnabled{
    return [YRGeneralApis yrGeneralApis_FactoryMakeScrollViewWithFrame:frame contentSize:size pagingEnabled:enabled showsHorizontalScrollIndicator:horizontall showsVerticalScrollIndicator:vertical scrollEnabled:scrollEnabled];
}

/**
 * UITableView factory
 */
+ (UITableView *)helper_factoryMakeTableViewWithFrame:(CGRect)rect backgroundColor:(UIColor *)backgroundColor style:(UITableViewStyle)style  bounces:(BOOL)bounce pageEnabled:(BOOL)enabled superView:(UIView *)view object:(id)object{
    return [YRGeneralApis yrGeneralApis_FactoryMakeTableViewWithFrame:rect backgroundColor:backgroundColor style:style bounces:bounce pageEnabled:enabled superView:view object:object];
}

/**
 * UIImage largeImage factory
 */
+ (UIImage *)helper_factoryGetImageIntoContentFileWithResource:(NSString *)resource andType:(NSString *)type{
    return [YRGeneralApis yrGeneralApis_FactoryGetImageIntoContentFileWithResource:resource andType:type];
}

/**
 * UIImage smallImage factory
 */
+ (UIImage *)helper_factoryGetImageIntoNamedWithResource:(NSString *)resource andType:(NSString *)type{
    return [YRGeneralApis yrGeneralApis_FactoryGetImageIntoNamedWithResource:resource andType:type];
}

/**
 * 横线
 * 提供的position y 为横线目标所在位置的y
 */
+ (UIView *)helper_factoryMakeHorizontalLineWithPoint:(CGPoint)positionPoint width:(CGFloat)width{
    return [YRGeneralApis yrGeneralApis_FactoryMakeHorizontalLineWithPoint:positionPoint width:width];
}

/**
 * 竖线
 * 提供的position x y 为竖线目标所在位置的 x y
 */
+ (UIView *)helper_factoryMakeVerticalLineWithPoint:(CGPoint)positionPoint height:(CGFloat)height{
    return [YRGeneralApis yrGeneralApis_FactoryMakeVerticalLineWithPoint:positionPoint height:height];
}


/**
 *  label color operation
 */
+ (NSMutableAttributedString *)helper_factoryMakeAttributedStringWithThreeDifferentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(CGFloat)textLeftFont font:(CGFloat)textCenterFont andFont:(CGFloat)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text{
    return [YRGeneralApis yrGeneralApis_MakeAttributedStringWithThreeDiffereentTextWithRange:range centerRange:centerRange rightRange:rightRange font:textLeftFont font:textCenterFont andFont:textRightFont color:leftColor andColor:centerColor color:rightColor withText:text];
}

/**
 *  label color operation
 */
+ (NSMutableAttributedString *)helper_factoryMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:(NSRange)range font:(CGFloat)textLeftFont  andFont:(CGFloat)textRightFont color:(UIColor *)leftColor  color:(UIColor *)rightColor  withText:(NSString *)text{
    return [YRGeneralApis yrGeneralApis_MakeAttributedStringWithTwoDiffirrentTextWhileSpecialInLeftWithRange:range font:textLeftFont andFont:textRightFont color:leftColor andColor:rightColor withText:text];
}

/**
 *  two
 */
+ (NSMutableAttributedString *)helper_factoryFontMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:(NSRange)range font:(UIFont *)textLeftFont  andFont:(UIFont *)textRightFont color:(UIColor *)leftColor  color:(UIColor *)rightColor  withText:(NSString *)text{
    
    
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    NSRange range1=range;
    NSRange range2=NSMakeRange(range.length, text.length-range.length);
    
    [str addAttribute:NSForegroundColorAttributeName value:leftColor range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:rightColor range:range2];
    
    [str addAttribute:NSFontAttributeName value:textLeftFont range:range1];
    [str addAttribute:NSFontAttributeName value:textRightFont range:range2];
    return str;
}

/**
 * three
 */
+ (NSMutableAttributedString *)helper_factoryFontMakeAttributedStringWithThreeDifferentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(UIFont *)textLeftFont font:(UIFont *)textCenterFont andFont:(UIFont *)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text{
    
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    
    NSRange range1=range;
    NSRange range2=centerRange;
    NSRange range3=rightRange;
    
    [str addAttribute:NSForegroundColorAttributeName value:leftColor range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:centerColor range:range2];
    [str addAttribute:NSForegroundColorAttributeName value:rightColor range:range3];
    [str addAttribute:NSFontAttributeName value:textLeftFont range:range1];
    [str addAttribute:NSFontAttributeName value:textCenterFont range:range2];
    [str addAttribute:NSFontAttributeName value:textRightFont range:range3];
    return str;
    
}

+ (NSMutableAttributedString *)helper_createFullUrlStrWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color{
    return [NSMutableAttributedString createFullUrlStrWithString:string font:font color:color];
}

+ (void)helper_addFullUrlWithAttributeStr:(NSMutableAttributedString *)attribute color:(UIColor *)color range:(NSRange)range url:(NSString *)url{
    [attribute addFullUrlWithWithColor:color range:range url:url];
}


+ (void)helper_getMainThread:(HelperInvocation)invocation{
    NSThread *thread = [NSThread currentThread];
    if(thread.isMainThread == NO){
        [YRGeneralApis yrGeneralApis_getMainThread:invocation];
    }
    else{
        invocation();
    }
}

+ (void)helper_getGlobalThread:(HelperInvocation)invocation{
    [YRGeneralApis yrGeneralApis_getGlobalThread:invocation];
}

/**
 *  将unix时间戳转换成月日时分秒str
 */
+ (NSString *)helper_getUnixTimeToRealTimeStr:(NSInteger)unixValue{
    return [YRGeneralApis yrGeneralApisTranslateUnixTimeToRealTimeStr:unixValue];
}

/**
 *  生成当前时间时间戳
 */
+ (NSString*)helper_GetCurrentTimeString{
    return [YRGeneralApis yrGeneralApisGetCurrentTimeString];
}

+ (NSTimeInterval)helper_GetCurrentTimeUnixTime{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init] ;
    
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    
    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"]; // ----------设置你想要的格式,hh与HH的区别:分别表示12小时制,24小时制
    
    //设置时区,这个对于时间的处理有时很重要
    
    //北京时间在iOS的timeZone对照表里面并没有，中国标准时间在里面存的是上海时间，也就是上面的Asia/Shanghai
    NSTimeZone* timeZone = [NSTimeZone timeZoneWithName:@"Asia/Shanghai"];
    
    [formatter setTimeZone:timeZone];
    
    NSDate *datenow = [NSDate date];//现在时间,你可以输出来看下是什么格式
    
    return [datenow timeIntervalSince1970];
}

+ (NSInteger)helper_GetRandowNum{
    return arc4random() % 100000;
}

+ (NSInteger)helper_GetSomeMonthDaysNumWithYear:(NSString *)year month:(NSString *)month{
    NSDateFormatter * formatter = [[NSDateFormatter alloc] init];
    
    [formatter setDateFormat:@"yyyy-MM"]; // 年-月
    
    NSString * dateStr = [NSString stringWithFormat:@"%@-%@",year,month];
    
    NSDate * date = [formatter dateFromString:dateStr];
    
    //
    NSCalendar * calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    
    NSRange range = [calendar rangeOfUnit:NSDayCalendarUnit
                                   inUnit: NSMonthCalendarUnit
                                  forDate:date];
    return range.length;
}

+ (NSString *)helper_getPropertyType:(id)obj key:(NSString *)propertyName{
    objc_property_t p = class_getProperty([obj class], propertyName.UTF8String);
    
    // 2.成员类型
    NSString *attrs = @(property_getAttributes(p));
    
    return attrs;
}

+ (UIImage *)helper_productQRCodeWithContent:(NSString *)content
                               codeImageSize:(CGFloat)size{
//        return  [XYQRCodeProduct qrCodeImageWithContent:content codeImageSize:size logo:[UIImage imageNamed:@"logo@3x.png"] logoFrame:CGRectMake(size / 2 - 30.0 / 2, size / 2 -30.0 / 2, 30, 30) red:100 / 255.0 green:100.0 / 255.0 blue:50.0 / 255.0];
    return nil;
}

+ (NSData *)helper_zipImage:(UIImage *)sourceImage limiteSize:(NSInteger)size{
    //进行图像尺寸的压缩
    CGSize imageSize = sourceImage.size;//取出要压缩的image尺寸
    CGFloat width = imageSize.width;    //图片宽度
    CGFloat height = imageSize.height;  //图片高度
    //1.宽高大于1280(宽高比不按照2来算，按照1来算)
    if (width>1280||height>1280) {
        if (width>height) {
            CGFloat scale = height/width;
            width = 1280;
            height = width*scale;
        }else{
            CGFloat scale = width/height;
            height = 1280;
            width = height*scale;
        }
        //2.宽大于1280高小于1280
    }else if(width>1280||height<1280){
        CGFloat scale = height/width;
        width = 1280;
        height = width*scale;
        //3.宽小于1280高大于1280
    }else if(width<1280||height>1280){
        CGFloat scale = width/height;
        height = 1280;
        width = height*scale;
        //4.宽高都小于1280
    }else{
    }
    UIGraphicsBeginImageContext(CGSizeMake(width, height));
    [sourceImage drawInRect:CGRectMake(0,0,width,height)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    //进行图像的画面质量压缩
    NSData *data=UIImageJPEGRepresentation(newImage, 1.0);
    if (data.length>100*1024) {
        if (data.length>1024*1024) {//1M以及以上
            data=UIImageJPEGRepresentation(newImage, 0.7);
        }else if (data.length>512*1024) {//0.5M-1M
            data=UIImageJPEGRepresentation(newImage, 0.8);
        }else if (data.length>200*1024) {
            //0.25M-0.5M
            data=UIImageJPEGRepresentation(newImage, 0.9);
        }
    }
    return data;
}

+ (NSString *)helper_getSDWebImageLoadUrlWithUrl:(NSString *)url{
    if(url && [url isKindOfClass:[NSString class]]){
        NSString * str1 = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        
        return str1;
    }
    else{
        return @"";
    }
}

+ (void)projectHelper_asyncLoadNetImage:(NSString *)url imageView:(UIImageView *)icon placeHolder:(UIImage *)placeholder invocation:(NSString *(^)(void))invocation{
    
    if(icon && [icon isKindOfClass:[UIImageView class]]){
        [ProjectHelper helper_getMainThread:^{
            icon.image = placeholder;
            if(url && [url isKindOfClass:[NSString class]]){
                if(url.length > 0 && [url hasPrefix:@"http"]){
                    
                    [icon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:placeholder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                        if(image && !error){
                            if(imageURL.absoluteString && [imageURL.absoluteString isKindOfClass:[NSString class]]){
                                
                                if(invocation){
                                    NSString *imageurl = invocation();
                                    if(imageurl && [imageurl isKindOfClass:[NSString class]]){
                                        if([imageURL.absoluteString isEqualToString:imageurl]){
                                            icon.image = image;
                                            return ;
                                        }
                                    }
                                }
                                
                                
                            }
                            
                            icon.image = placeholder;
                        }
                        else{
                            icon.image = placeholder;
                        }
                    }];
                }
            }
        }];
        
    }
    
    
    
}
@end
