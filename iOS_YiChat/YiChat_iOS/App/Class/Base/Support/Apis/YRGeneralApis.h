//
//  YRGeneralApis.h
//  BussinessManager
//
//  Created by yunlian on 2017/1/13.
//  Copyright © 2017年 yunlian. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "AppDelegate.h"
#import "YRGeneralApiDef.h"
#import <AVFoundation/AVFoundation.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import <Photos/PHPhotoLibrary.h>

typedef void(^YRThreadInvocation) (void);


@class YRTableView;
@interface YRGeneralApis : NSObject

#pragma mark system

+ (YRCurrentDeviceInch)yrGeneralApis_JudgeDeviceInch;

+ (NSString *)yrGeneralApis_getDeviceId;

+ (NSString *)yrGeneralApis_getAppVersionCode;

+ (NSString *)yrGeneralApis_getDeviceVersion;

+ (NSString *)yrGeneralApis_getDeviceModel;

+ (UIView *)yrGeneralApis_FactoryGetKeyboardView;

+ (AppDelegate *)yrGeneral_ApisGetAppDelegate;

#pragma mark 适配

+ (void)yrGeneralApis_initialNav;

+ (void)yrGeneralApis_initialTabBar:(UITabBarController *)tab;

+ (CGFloat)yrGeneralApis_getNavH;

+ (CGFloat)yrGeneralApis_getStatusH;

+ (CGFloat)yrGeneralApis_getTabH;

/**
 *  根据图片比例(宽/高) 给出高或者宽 返回成比例的高或者宽
 */
+ (CGFloat)yrGenral_ApisGetWidthOrHeightIntoScale:(CGFloat)scale width:(CGFloat)width height:(CGFloat)height;
/**
 *  按比例适配 获取适配后的h
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_H:(CGFloat)h;

/**
 *  按比例适配 获取适配后的w
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_W:(CGFloat)w;

/**
 *  适配font
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_font:(CGFloat)w;


#pragma mark

+ (id)yrGeneralApis_getArrFirstEntityWithData:(NSArray *)dataSource;

+ (NSString *)yrGeneralApis_getPhoneAfterFourNumberWithPhone:(NSString *)phone;

+ (NSString *)yrGeneralApis_dealStringObject:(NSString *)object;

+ (BOOL)yrGeneralApis_JudgeArrayItems:(NSArray *)array isAllExistInArray:(NSArray *)tempArray;

#pragma mark thread

+ (void)yrGeneralApis_getMainThread:(YRThreadInvocation)invocation;

+ (void)yrGeneralApis_getGlobalThread:(YRThreadInvocation)invocation;


#pragma mark NSMutableAttributedString

/**
 *  label color operation
 */
+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithThreeDiffereentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(CGFloat)textLeftFont font:(CGFloat)textCenterFont andFont:(CGFloat)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text;

+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithTwoDiffirrentTextWhileSpecialInLeftWithRange:(NSRange)range font:(CGFloat)specialFont andFont:(CGFloat)generalFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text;

+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithTwoDiffirrentTextWhileSpecialInRightWithRange:(NSRange)range font:(CGFloat)specialFont andFont:(CGFloat)generalFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text;

+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithTwoSpecialTextInCenterWithRange:(NSRange)range otherRange:(NSRange)otherRange font:(CGFloat)textFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text;


#pragma mark factory

+ (UIImagePickerController *)yrGeneralApis_GetPickerControllerWithType:(YRPickerControllerType)type;

/**
 *  UIWindow factory
 */
+ (UIWindow *)yrGeneralApis_FactoryMakeWindow;

/**
 * UIView factory
 */
+ (UIView *)yrGeneralApis_FactoryMakeViewWithFrame:(CGRect)rect backGroundColor:(UIColor *)color;

/**
 * UILabel factory
 */
+ (UILabel *)yrGeneralApis_FactoryMakeLabelWithFrame:(CGRect)rect andfont:(UIFont *)fontSize textColor:(UIColor *)color textAlignment:(NSTextAlignment)textAlignment;

/**
 * UIButton factory
 */
+ (UIButton *)yrGeneralApis_FactoryMakeButtonWithFrame:(CGRect)rect andBtnType:(UIButtonType)type;

/**
 * clear btn
 */
+ (UIButton *)yrGeneralApis_FactoryMakeClearButtonWithFrame:(CGRect)rect target:(id)object method:(SEL)method;

/**
 * UIButton 圆角按钮factory
 */
+  (UIButton *)yrGeneralApis_FactoryMakeCircleBtnWithFrame:(CGRect)rect andBtnType:(UIButtonType)type title:(NSString *)title titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)backgroundColor;

/**
 * UIImageView factory
 */
+ (UIImageView *)yrGeneralApis_FactoryMakeImageViewWithFrame:(CGRect)rect andImg:(UIImage *)img;

/**
 * UITextView factory
 */
+ (UITextView *)yrGeneralApis_FactoryMakeTextViewWithFrame:(CGRect)rect  fontSize:(UIFont *)size keybordType:(UIKeyboardType)type textColor:(UIColor *)color;

/**
 * UITextFiled factory
 */
+ (UITextField *)yrGeneralApis_FactoryMakeTextFieldWithFrame:(CGRect)rect withPlaceholder:(NSString *)holder fontSize:(UIFont *)size isClearButtonMode:(UITextFieldViewMode)mode andKeybordType:(UIKeyboardType)type textColor:(UIColor *)color;

/**
 * UIScrollView factory
 */
+ (UIScrollView *)yrGeneralApis_FactoryMakeScrollViewWithFrame:(CGRect)frame contentSize:(CGSize)size pagingEnabled:(BOOL)enabled showsHorizontalScrollIndicator:(BOOL)horizontall showsVerticalScrollIndicator:(BOOL)vertical scrollEnabled:(BOOL)scrollEnabled;

/**
 * UITableView factory
 */
+ (UITableView *)yrGeneralApis_FactoryMakeTableViewWithFrame:(CGRect)rect backgroundColor:(UIColor *)backgroundColor style:(UITableViewStyle)style  bounces:(BOOL)bounce pageEnabled:(BOOL)enabled superView:(UIView *)view object:(id)object;

/**
 * UIImage largeImage factory
 */
+ (UIImage *)yrGeneralApis_FactoryGetImageIntoContentFileWithResource:(NSString *)resource andType:(NSString *)type;

/**
 * UIImage smallImage factory
 */
+ (UIImage *)yrGeneralApis_FactoryGetImageIntoNamedWithResource:(NSString *)resource andType:(NSString *)type;

/**
 * 根据显示类容计算行高 factory
 */
+ (CGRect)yrGeneralApis_FactoryGetFontSizeWithString:(NSString *)string useFont:(int )font withWidth:(CGFloat)width andHeight:(CGFloat)height;

/**
 * 横线
 * 提供的position y 为横线目标所在位置的y
 */
+ (UIView *)yrGeneralApis_FactoryMakeHorizontalLineWithPoint:(CGPoint)positionPoint width:(CGFloat)width;

/**
 * 竖线
 * 提供的position x y 为竖线目标所在位置的 x y
 */
+ (UIView *)yrGeneralApis_FactoryMakeVerticalLineWithPoint:(CGPoint)positionPoint height:(CGFloat)height;

#pragma mark tool

+ (NSDictionary *)feltNotifyobjectWithNotify:(NSNotification *)notify;

/**
 *  获取首字母
 */
+ (NSString *)yrGeneralApis_FactoryGetStringFirstCharacter:(NSString *)firstStr;

+ (NSString *)yrGeneralApis_FeltStringDataWithString:(NSString *)string;

+ (BOOL)yrGeneralApis_SearchObject:(id)object isContainInArr:(NSArray *)arr judge:(YRGeneralDealJudge)dealJudge;

+ (id)yrGeneralApis_dictionaryWithJsonString:(NSString *)jsonString;

+ (NSString *)yrGeneralApis_JsonStringWithDictionary:(NSDictionary *)dic;

+ (NSString *)yrGeneralApis_getPhoneAfterNumberWithPhoneNumber:(NSString *)phoneNumber;
#pragma mark date

+ (NSString *)yrGeneralApis_FactoryTranslateTimeStrIntoTimeStr:(long long)time;

+ (NSString *)yrGeneral_ApisTranslateUnixDateToHourMinuteSecondWithUnix:(NSInteger)unixTime;

+ (NSString *)yrGeneral_ApisTranslateUnixDateToYearMonthDayHourMinuteWithUnix:(NSInteger)unixTime;

+ (NSString *)yrGeneral_ApisTranslateDateToYearMonthDayWithDate:(NSDate *)date;

+ (NSString *)yrGeneral_ApisTranslateDateToFormatTimeStringWithDate:(NSDate *)date;

+ (NSString *)yrGeneral_ApisTranslateDateToUnixStrWithDate:(NSDate *)date;

+ (NSString *)yrGeneral_ApisTranslateDateToString:(NSDate *)date withDateFormat:(NSString *)format;

+ (NSString *)yrGeneral_ApisReplaceUnicode:(NSString*)TransformUnicodeString;

//字符串转日期格式  [self stringToDate:@"2016-01-18 15:13:12" withDateFormat:@"yyyy-MM-dd HH:mm:ss"];
+ (NSDate *)yrGeneral_ApisTranslateStringToDate:(NSString *)dateString withDateFormat:(NSString *)format;

//将世界时间转化为中国区时间
+ (NSDate *)yrGeneral_ApisTranslateWorldTimeToChinaTime:(NSDate *)date;

/**
 *  将unix时间戳转换成月日时分秒str
 */
+ (NSString *)yrGeneralApisTranslateUnixTimeToRealTimeStr:(NSInteger)unixValue;

/**
 *  生成当前时间时间戳
 */
+ (NSString*)yrGeneralApisGetCurrentTimeString;

+ (NSString*)yrGeneralApisGetCurrentTimeStringForFileNameWithDate:(NSDate *)date;

+ (NSString*)yrGeneralApisGetCurrentTimeStringWithDate:(NSDate *)date;


#pragma mark file 

+ (NSString *)yrGeneralApis_getHomeDirectoryPath;

+ (NSString *)yrGeneralApis_getTempDirectoryPath;

+ (NSString *)yrGeneralApis_getDocumentDirecoryPath;

+ (NSString *)yrGeneralApis_getPeferenceDirecoryPath;

+ (NSString *)yrGeneralApis_getLibraryDirectoryPath;

+ (NSString *)yrGeneralApis_getCacheDirectoryPath;

/**
 *  创建文件夹
 */
+ (BOOL)yrGeneralApisCreateItemWithPath:(NSString *)path;

/**
 *  获取目录下所有文件
 */
+ (NSArray *)YRGeneralApis_GetAllFilesNameIntoItem:(NSString *)path;

+ (void)yrGeneralApisTranlateMovToMP4WithPath:(NSURL *)path savePath:(NSString *)savaPath hanlde:(YRGeneralApisMovieTypeTranslateHandle)handle;

+ (void)yrGeneralApis_SaveImageToSystemAlbumWithImage:(UIImage *)img handle:(YRGeneralApisSaveImageToAlumnHandle)handle;

+ (NSArray *)yrGeneralApis_DeleteRepeatDataInArray:(NSArray *)array;

+ (NSArray *)yrGeneralApis_DeleteRepeatDataInArray:(NSArray *)array keyString:(NSString *)keyString;
@end
