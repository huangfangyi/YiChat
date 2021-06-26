//
//  YRGeneralApis.m
//  BussinessManager
//
//  Created by yunlian on 2017/1/13.
//  Copyright © 2017年 yunlian. All rights reserved.
//

#import "YRGeneralApis.h"

@implementation YRGeneralApis

+ (id)yrGeneralApis_getArrFirstEntityWithData:(NSArray *)dataSource{
    if(dataSource.count != 0){
        return dataSource[0];
    }
    else{
        return nil;
    }
}

+ (NSString *)yrGeneralApis_getPhoneAfterFourNumberWithPhone:(NSString *)phone{
    NSString *str=[phone substringWithRange:NSMakeRange(phone.length - 4, 4)];
    
    NSMutableString *returnStr=[NSMutableString stringWithCapacity:0];
    for (int i=0; i<str.length; i++) {
        [returnStr appendString:[str substringWithRange:NSMakeRange(i, 1)]];
        if(i != str.length - 1)
        {
            [returnStr appendString:@" "];
        }
    }
    
    return returnStr;
}

+ (NSString *)yrGeneralApis_dealStringObject:(NSString *)object;{
    if(object != nil){
        return object;
    }
    return @"";
}

+ (BOOL)yrGeneralApis_JudgeArrayItems:(NSArray *)array isAllExistInArray:(NSArray *)tempArray{
    if(array.count == 0 || tempArray.count == 0){
        return NO;
    }
    
    for (int i = 0; i<array.count; i++) {
        BOOL isHas = NO;
        for (int j = 0 ; j<tempArray.count; j++) {
            if([array[i] isEqualToString:tempArray[j]]){
                isHas = YES;
                break;
            }
        }
        if(isHas == NO){
            return NO;
        }
    }
    return YES;
    
}

+ (YRCurrentDeviceInch)yrGeneralApis_JudgeDeviceInch{
    if(kGeneral_Size_W == KGENERAL_IPHONE4S_W && kGeneral_Size_H == KGENERAL_IPHONE4S_H){
        return YRCurrentDeviceInch35;
    }
    else if(kGeneral_Size_W == KGENERAL_IPHONE5_W && kGeneral_Size_H == KGENERAL_IPHONE5_H){
        return YRCurrentDeviceInch40;
    }
    else if(kGeneral_Size_W == KGENERAL_IPHONE6_W && kGeneral_Size_H == KGENERAL_IPHONE6_H){
        return YRCurrentDeviceInch47;
    }
    else if(kGeneral_Size_W == KGENERAL_IPHONE6P_W && kGeneral_Size_H == KGENERAL_IPHONE6P_H){
        return YRCurrentDeviceInch55;
    }
    else{
        return YRCurrentDeviceInchError;
    }
}

+ (AppDelegate *)yrGeneral_ApisGetAppDelegate{
    return (AppDelegate *)[[UIApplication sharedApplication] delegate];
}

+ (NSString *)yrGeneral_ApisTranslateUnixDateToHourMinuteSecondWithUnix:(NSInteger)unixTime{
    NSInteger hour=unixTime / 3600;
    NSInteger minute=unixTime % 3600 / 60;
    
    if(hour == 0){
        if(minute == 0 ){
            return @"0小时0分钟";
        }
        else{
            return [NSString stringWithFormat:@"%ld%@",minute,@"分钟"];
        }
    }
    else{
        if(minute == 0 ){
            return [NSString stringWithFormat:@"%ld%@",hour,@"小时"];
        }
        else{
            return [NSString stringWithFormat:@"%ld%@%ld%@",hour,@"小时",minute,@"分钟"];
        }
    }
}

+ (NSString *)yrGeneral_ApisTranslateUnixDateToYearMonthDayHourMinuteWithUnix:(NSInteger)unixTime{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd hh:mm"];
    NSString *destDateString = [dateFormatter stringFromDate:[[NSDate alloc] initWithTimeIntervalSince1970:unixTime]];
    return destDateString;
}

+ (NSString *)yrGeneral_ApisTranslateDateToYearMonthDayWithDate:(NSDate *)date{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy.MM.dd"];
    NSString *destDateString = [dateFormatter stringFromDate:date];
    return destDateString;
}

+ (NSString *)yrGeneral_ApisTranslateDateToFormatTimeStringWithDate:(NSDate *)date{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy.MM.dd hh:mm:ss"];
    NSString *destDateString = [dateFormatter stringFromDate:date];
    return destDateString;
}

+ (void)yrGeneralApis_getMainThread:(YRThreadInvocation)invocation{
    dispatch_async(dispatch_get_main_queue(), ^{
        invocation();
    });
}

+ (void)yrGeneralApis_getGlobalThread:(YRThreadInvocation)invocation{
    dispatch_queue_t queue=dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        invocation();
    });
}

+ (NSString *)yrGeneral_ApisTranslateDateToUnixStrWithDate:(NSDate *)date{
    return [NSString stringWithFormat:@"%ld",(long)[date timeIntervalSince1970]];
}

+ (NSString *)yrGeneral_ApisTranslateDateToString:(NSDate *)date withDateFormat:(NSString *)format
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:format];
    NSString *strDate = [dateFormatter stringFromDate:date];
    return strDate;
}

+ (NSString *)yrGeneral_ApisReplaceUnicode:(NSString*)TransformUnicodeString{
    
    NSString*tepStr1 = [TransformUnicodeString stringByReplacingOccurrencesOfString:@"\\u"withString:@"\\U"];
    
    NSString*tepStr2 = [tepStr1 stringByReplacingOccurrencesOfString:@"\""withString:@"\\\""];
    
    NSString*tepStr3 = [[@"\"" stringByAppendingString:tepStr2]stringByAppendingString:@"\""];
    
    NSData*tepData = [tepStr3 dataUsingEncoding:NSUTF8StringEncoding];
    
    NSString*axiba = [NSPropertyListSerialization propertyListWithData:tepData options:NSPropertyListMutableContainers format:NULL error:NULL];
    
    return  [axiba stringByReplacingOccurrencesOfString:@"\\r\\n" withString:@"\n"];
    
}
//字符串转日期格式
+ (NSDate *)yrGeneral_ApisTranslateStringToDate:(NSString *)dateString withDateFormat:(NSString *)format
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:format];
    
    NSDate *date = [dateFormatter dateFromString:dateString];
    return [self yrGeneral_ApisTranslateWorldTimeToChinaTime:date];
}

//将世界时间转化为中国区时间
+ (NSDate *)yrGeneral_ApisTranslateWorldTimeToChinaTime:(NSDate *)date
{
    NSTimeZone *timeZone = [NSTimeZone systemTimeZone];
    NSInteger interval = [timeZone secondsFromGMTForDate:date];
    NSDate *localeDate = [date  dateByAddingTimeInterval:interval];
    return localeDate;
}

+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithThreeDiffereentTextWithRange:(NSRange)range centerRange:(NSRange)centerRange rightRange:(NSRange)rightRange font:(CGFloat)textLeftFont font:(CGFloat)textCenterFont andFont:(CGFloat)textRightFont color:(UIColor *)leftColor andColor:(UIColor *)centerColor color:(UIColor *)rightColor  withText:(NSString *)text{
    
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    
    NSRange range1=range;
    NSRange range2=centerRange;
    NSRange range3=rightRange;
    
    
    [str addAttribute:NSForegroundColorAttributeName value:leftColor range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:centerColor range:range2];
    [str addAttribute:NSForegroundColorAttributeName value:rightColor range:range3];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textLeftFont] range:range1];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textCenterFont] range:range2];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textRightFont] range:range3];
    return str;
}


+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithTwoDiffirrentTextWhileSpecialInLeftWithRange:(NSRange)range font:(CGFloat)specialFont andFont:(CGFloat)generalFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text{
    
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    NSRange range1=range;
    NSRange range2=NSMakeRange(range.length, text.length-range.length);
    
    
    [str addAttribute:NSForegroundColorAttributeName value:special range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:generalColor range:range2];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:specialFont] range:range1];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:generalFont] range:range2];
    return str;
}


+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithTwoDiffirrentTextWhileSpecialInRightWithRange:(NSRange)range font:(CGFloat)specialFont andFont:(CGFloat)generalFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text{
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    NSRange range1=range;
    NSRange range2=NSMakeRange(0, text.length- range1.length);
    
    
    [str addAttribute:NSForegroundColorAttributeName value:generalColor range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:special range:range2];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:generalFont] range:range1];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:specialFont] range:range2];
    return str;
}


+ (NSMutableAttributedString *)yrGeneralApis_MakeAttributedStringWithFourSpecialTextWithRange:(NSRange)range otherRange:(NSRange)otherRange font:(CGFloat)textFont color:(UIColor *)special andColor:(UIColor *)generalColor  withText:(NSString *)text{
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:text];
    NSRange range1=NSMakeRange(0, range.location);
    NSRange range2=range;
    NSRange range3=NSMakeRange(range.location+range.length, otherRange.location-range2.location-range2.length);
    NSRange range4=otherRange;
    
    [str addAttribute:NSForegroundColorAttributeName value:generalColor range:range1];
    [str addAttribute:NSForegroundColorAttributeName value:special range:range2];
    [str addAttribute:NSForegroundColorAttributeName value:generalColor range:range3];
    [str addAttribute:NSForegroundColorAttributeName value:special range:range4];
    
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textFont] range:range1];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textFont] range:range2];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textFont] range:range3];
    [str addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:textFont] range:range4];
    
    return str;
}


#pragma mark 适配

+ (void)yrGeneralApis_initialNav{
    UIViewController *vc = [[UIViewController alloc] init];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
    
    //获取状态栏的rect
    CGRect statusRect = [[UIApplication sharedApplication] statusBarFrame];
    //获取导航栏的rect 44
    CGRect navRect = nav.navigationBar.frame;
    
    NSNumber *statusNum = [NSNumber numberWithFloat:statusRect.size.height];
    NSNumber *navNum = [NSNumber numberWithFloat:navRect.size.height];
    
    [[NSUserDefaults standardUserDefaults] setObject:navNum forKey:kGeneral_Name_NavNum];
    [[NSUserDefaults standardUserDefaults] setObject:statusNum forKey:kGeneral_Name_StatusNum];
    
}

+ (void)yrGeneralApis_initialTabBar:(UITabBarController *)tab{
    CGRect tabBarRect = tab.tabBar.bounds;
    NSNumber *tabNum = [NSNumber numberWithFloat:tabBarRect.size.height];
    
    [[NSUserDefaults standardUserDefaults] setObject:tabNum forKey:kGeneral_Name_TabNum];
}

+ (CGFloat)yrGeneralApis_getNavH{
    NSNumber *num = [[NSUserDefaults standardUserDefaults] objectForKey:kGeneral_Name_NavNum];
    
    return num.floatValue;
}

+ (CGFloat)yrGeneralApis_getStatusH{
    NSNumber *num = [[NSUserDefaults standardUserDefaults] objectForKey:kGeneral_Name_StatusNum];
    
    return num.floatValue;
}

+ (CGFloat)yrGeneralApis_getTabH{
    NSNumber *num = [[NSUserDefaults standardUserDefaults] objectForKey:kGeneral_Name_TabNum];
    
    return num.floatValue;
}


/**
 *  根据图片比例(宽/高) 给出高或者宽 返回成比例的高或者宽
 */
+ (CGFloat)yrGenral_ApisGetWidthOrHeightIntoScale:(CGFloat)scale width:(CGFloat)width height:(CGFloat)height{
    if(width==0){
    
        return scale * height;
    }
    else if(height==0 && scale != 0){
        return width / scale;
    }
    return 0;
}
/**
 *  按比例适配 获取适配后的h
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_H:(CGFloat)h{
    return h/kGeneral_Size_Design_H*kGeneral_Size_H;
}

/**
 *  按比例适配 获取适配后的w
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_W:(CGFloat)w{
    return w/kGeneral_Size_Design_W*kGeneral_Size_W;
}

/**
 *  适配font
 */
+ (CGFloat)yrGeneralApisGetScreenSuitable_font:(CGFloat)w{
    if(kGeneral_Size_W==320.0 && kGeneral_Size_H==480.0){
        return w-1;
    }else if(kGeneral_Size_W==320.0 && kGeneral_Size_H==568.0){
        return w;
    }else if(kGeneral_Size_W==375.0){
        return w+1;
    }else{
        return w+1;
    }
}

/**
 *  将unix时间戳转换成月日时分秒str
 */
+ (NSString *)yrGeneralApisTranslateUnixTimeToRealTimeStr:(NSInteger)unixValue{
    NSDateFormatter *formatter=[[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"MM月dd日 HH时mm分"];
    NSString *fromTime=[formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:unixValue]];
    return fromTime;
}

/**
 *  生成当前时间时间戳
 */
+ (NSString*)yrGeneralApisGetCurrentTimeString{
    NSDateFormatter *dateformat = [[NSDateFormatter  alloc]init];
    [dateformat setDateFormat:@"yyyyMMddHHmmss"];
    return [dateformat stringFromDate:[NSDate date]];
}

+ (NSString*)yrGeneralApisGetCurrentTimeStringForFileNameWithDate:(NSDate *)date{
    NSDateFormatter *dateformat = [[NSDateFormatter  alloc]init];
    [dateformat setDateFormat:@"yyyyMMddHHmmss"];
    return [dateformat stringFromDate:[NSDate date]];
}

+ (NSString*)yrGeneralApisGetCurrentTimeStringWithDate:(NSDate *)date{
    NSDateFormatter *dateformat = [[NSDateFormatter  alloc]init];
    [dateformat setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    return [dateformat stringFromDate:[NSDate date]];
}


#pragma mark factory

+ (UIImagePickerController *)yrGeneralApis_GetPickerControllerWithType:(YRPickerControllerType)type{
    UIImagePickerController *picker=[[UIImagePickerController alloc] init];
    if(type == YRPickerControllerTypePhoto){
        picker.sourceType=UIImagePickerControllerSourceTypePhotoLibrary;
        picker.allowsEditing=YES;
        picker.accessibilityLanguage =NSCalendarIdentifierChinese;
        
    }
    else if(type == YRPickerControllerTypeCamera){
        picker.sourceType=UIImagePickerControllerSourceTypeCamera;
        picker.cameraCaptureMode=UIImagePickerControllerCameraCaptureModePhoto;
        picker.allowsEditing=YES;
        picker.accessibilityLanguage =NSCalendarIdentifierChinese;
        picker.cameraDevice=UIImagePickerControllerCameraDeviceRear;
    }
    else if(type == YRPickerControllerTypeVideo){
        
        picker.sourceType=UIImagePickerControllerSourceTypeCamera;
        picker.mediaTypes = @[ (NSString *)kUTTypeMovie];
        picker.cameraDevice=UIImagePickerControllerCameraDeviceRear;
        picker.videoMaximumDuration = 20;
        picker.accessibilityLanguage =NSCalendarIdentifierChinese;
        picker.videoQuality = UIImagePickerControllerQualityTypeMedium;
        picker.allowsEditing=YES;
    }
    return picker;
}

/**
 *  UIWindow factory
 */
+ (UIWindow *)yrGeneralApis_FactoryMakeWindow{
    UIWindow *window=[[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    window.backgroundColor=[UIColor whiteColor];
    [window makeKeyAndVisible];
    return window;
}

/**
 * UIView factory
 */
+ (UIView *)yrGeneralApis_FactoryMakeViewWithFrame:(CGRect)rect backGroundColor:(UIColor *)color{
    UIView *view=[[UIView alloc] initWithFrame:rect];
    view.backgroundColor=color;
    return view;
}

/**
 * UILabel factory
 */
+ (UILabel *)yrGeneralApis_FactoryMakeLabelWithFrame:(CGRect)rect andfont:(UIFont *)fontSize textColor:(UIColor *)color textAlignment:(NSTextAlignment)textAlignment{
    UILabel *lab=[[UILabel alloc] initWithFrame:rect];
    lab.textColor=color;
    lab.tintColor=[UIColor clearColor];
    lab.textAlignment=textAlignment;
    lab.font = fontSize;
    return lab;
}

/**
 * UIButton factory
 */
+ (UIButton *)yrGeneralApis_FactoryMakeButtonWithFrame:(CGRect)rect andBtnType:(UIButtonType)type{
    UIButton *btn=[UIButton buttonWithType:type];
    btn.frame=rect;
    return btn;
}

/**
 * clear btn
 */
+ (UIButton *)yrGeneralApis_FactoryMakeClearButtonWithFrame:(CGRect)rect target:(id)object method:(SEL)method{
    UIButton *btn=[UIButton buttonWithType:UIButtonTypeCustom];
    btn.backgroundColor=[UIColor clearColor];
    btn.frame=rect;
    [btn addTarget:object action:method forControlEvents:UIControlEventTouchUpInside];
    return btn;
}


/**
 * UIButton 圆角按钮factory
 */
+  (UIButton *)yrGeneralApis_FactoryMakeCircleBtnWithFrame:(CGRect)rect andBtnType:(UIButtonType)type title:(NSString *)title titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)backgroundColor{
    UIButton *btn=[UIButton buttonWithType:type];
    btn.frame=rect;
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setTitleColor:titleColor forState:UIControlStateNormal];
    btn.backgroundColor=backgroundColor;
    btn.layer.cornerRadius=kGeneral_Size_CircleValue;
    btn.clipsToBounds=YES;
    
    return btn;
}

/**
 * UIImageView factory
 */
+ (UIImageView *)yrGeneralApis_FactoryMakeImageViewWithFrame:(CGRect)rect andImg:(UIImage *)img{
    UIImageView *myImage=[[UIImageView alloc] initWithFrame:rect];
    if(img != nil){
        myImage.image=img;
    }
    else{
        myImage.image = [UIImage imageNamed:@""];
    }
    return myImage;
}

/**
 * UITextFiled factory
 */
+ (UITextField *)yrGeneralApis_FactoryMakeTextFieldWithFrame:(CGRect)rect withPlaceholder:(NSString *)holder fontSize:(UIFont *)size isClearButtonMode:(UITextFieldViewMode)mode andKeybordType:(UIKeyboardType)type textColor:(UIColor *)color{
    UITextField *text=[[UITextField alloc] initWithFrame:rect];
    text.placeholder=holder;
    text.font = size;
    text.clearButtonMode=mode;
    text.keyboardType=type;
    text.textColor=color;
    return text;
    
}


/**
 * UITextView factory
 */
+ (UITextView *)yrGeneralApis_FactoryMakeTextViewWithFrame:(CGRect)rect  fontSize:(UIFont *)size keybordType:(UIKeyboardType)type textColor:(UIColor *)color{
    UITextView *text=[[UITextView alloc] initWithFrame:rect];
    text.font = size;
    text.keyboardType=type;
    text.textColor=color;
    return text;
    
}

/**
 * UIScrollView factory
 */
+ (UIScrollView *)yrGeneralApis_FactoryMakeScrollViewWithFrame:(CGRect)frame contentSize:(CGSize)size pagingEnabled:(BOOL)enabled showsHorizontalScrollIndicator:(BOOL)horizontall showsVerticalScrollIndicator:(BOOL)vertical scrollEnabled:(BOOL)scrollEnabled{
    UIScrollView *scroll=[[UIScrollView alloc] initWithFrame:frame];
    scroll.contentSize=size;
    scroll.pagingEnabled=enabled;
    scroll.showsVerticalScrollIndicator=vertical;
    scroll.showsHorizontalScrollIndicator=horizontall;
    scroll.scrollEnabled=scrollEnabled;
    return scroll;
}

/**
 * UITableView factory
 */
+ (UITableView *)yrGeneralApis_FactoryMakeTableViewWithFrame:(CGRect)rect backgroundColor:(UIColor *)backgroundColor style:(UITableViewStyle)style  bounces:(BOOL)bounce pageEnabled:(BOOL)enabled superView:(UIView *)view object:(id)object{
    UITableView *table=[[UITableView alloc] initWithFrame:rect style:style];
    table.bounces=bounce;
    table.backgroundColor=backgroundColor;
    table.pagingEnabled=enabled;
    table.showsHorizontalScrollIndicator=NO;
    table.showsVerticalScrollIndicator=YES;
    table.separatorStyle=UITableViewCellSeparatorStyleNone;
    table.delegate=object;
    table.dataSource=object;
    
    [view addSubview:table];
    
    return table;
}

/**
 * UIImage largeImage factory
 */
+ (UIImage *)yrGeneralApis_FactoryGetImageIntoContentFileWithResource:(NSString *)resource andType:(NSString *)type{
    NSString *path=[[NSBundle mainBundle] pathForResource:resource ofType:@"png"];
    UIImage *img=[[UIImage alloc] initWithContentsOfFile:path];
    return img;
}

/**
 * UIImage smallImage factory
 */
+ (UIImage *)yrGeneralApis_FactoryGetImageIntoNamedWithResource:(NSString *)resource andType:(NSString *)type{
    UIImage *img=[UIImage imageNamed:[NSString stringWithFormat:@"%@%@",resource,@".png"]];
    return img;
}

/**
 * 根据显示类容计算行高 factory
 */
+ (CGRect)yrGeneralApis_FactoryGetFontSizeWithString:(NSString *)string useFont:(int )font withWidth:(CGFloat)width andHeight:(CGFloat)height{
    NSDictionary *dic=@{NSFontAttributeName:[UIFont systemFontOfSize:font]};
    CGRect rect=[string boundingRectWithSize:CGSizeMake(width, height) options:NSStringDrawingUsesLineFragmentOrigin attributes:dic context:nil];
    return rect;
}

/**
 * 横线
 * 提供的position y 为横线目标所在位置的y
 */
+ (UIView *)yrGeneralApis_FactoryMakeHorizontalLineWithPoint:(CGPoint)positionPoint width:(CGFloat)width{
    UIView *line=[[UIView alloc] initWithFrame:CGRectMake(positionPoint.x, positionPoint.y-1, width, 1)];
    line.alpha=0.2;
    line.backgroundColor=[UIColor grayColor];
    return line;
}

/**
 * 竖线
 * 提供的position x y 为竖线目标所在位置的 x y
 */
+ (UIView *)yrGeneralApis_FactoryMakeVerticalLineWithPoint:(CGPoint)positionPoint height:(CGFloat)height{
    UIView *line=[[UIView alloc] initWithFrame:CGRectMake(positionPoint.x-1, positionPoint.y, 1, height)];
    line.alpha=0.6;
    line.backgroundColor=[UIColor grayColor];
    return line;
}

+ (NSDictionary *)feltNotifyobjectWithNotify:(NSNotification *)notify{
    id object = [notify object];
    if([object isKindOfClass:[NSDictionary class]]){
        return object;
    }
    else{
        return nil;
    }
}

+ (NSString *)yrGeneralApis_FactoryGetStringFirstCharacter:(NSString *)firstStr{
    
    if([firstStr isEqualToString:@""]){
        return @"*";
    }
    //转成了可变字符串
    NSMutableString *str = [NSMutableString stringWithString:firstStr];
    //先转换为带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformMandarinLatin,NO);
    //再转换为不带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformStripDiacritics,NO);
    //转化为大写拼音
    NSString *pinYin = [str capitalizedString];
    //获取并返回首字母
    return [pinYin substringToIndex:1];
}

+ (NSString *)yrGeneralApis_FeltStringDataWithString:(NSString *)string{
    if(string == nil){
        return @"";
    }
    else{
        return string;
    }
}

+ (NSString *)yrGeneralApis_FactoryTranslateTimeStrIntoTimeStr:(long long)time{
    NSInteger num = time / 1000;
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"MM-dd HH:mm:ss"];
    
    NSDate*confromTimesp = [NSDate dateWithTimeIntervalSince1970:num];
    NSString*confromTimespStr = [formatter stringFromDate:confromTimesp];
    return confromTimespStr;
    
}

+ (BOOL)yrGeneralApis_SearchObject:(id)object isContainInArr:(NSArray *)arr judge:(YRGeneralDealJudge)dealJudge{
    if(dealJudge == nil){
        if ([object isKindOfClass:[NSString class]]) {
            for (int i=0;i<arr.count; i++)
            {
                if([object isEqualToString:arr[i]]){
                    return YES;
                }
            }
        }
    }
    else{
        for (int i=0; i<arr.count;i++)
        {
            if(dealJudge(object,arr[i]) == YES)
            {
                return YES;
            }
            else{
                return NO;
            }
        }
    }
    return NO;
}

+ (id)yrGeneralApis_dictionaryWithJsonString:(NSString *)jsonString {
    if (jsonString == nil) {
        return nil;
    }
   
    if([jsonString isKindOfClass:[NSString class]]){
        NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        NSError *err;
        id object = [NSJSONSerialization JSONObjectWithData:jsonData
                                                            options:NSJSONReadingMutableContainers
                                                              error:&err];
       
        if(err) {
            NSLog(@"json解析失败：%@",err);
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\r\n" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\t" withString:@""];
            jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\\" withString:@""];
            
            NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
            NSError *err;
            id object = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
            
            if(err) {
                NSLog(@"json解析失败：%@",err);
                return nil;
            }
            
            return object;
        }
        return object;
    }
    else if([jsonString isKindOfClass:[NSDictionary class]]){
        return (NSDictionary *)jsonString;
    }
    else{
        return nil;
    }
   
}

+ (NSString *)yrGeneralApis_JsonStringWithDictionary:(NSDictionary *)dic{
    if(dic.allKeys.count == 0){
        return @"{}";
    }
    else{
        NSError *error = nil;
        NSData *data = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&error];
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        
    }
}


+ (NSString *)yrGeneralApis_getPhoneAfterNumberWithPhoneNumber:(NSString *)phoneNumber{
    if(phoneNumber.length < 4){
        return nil;
    }
    else{
        return [phoneNumber substringWithRange:NSMakeRange(phoneNumber.length  - 4, 4)];
    }
}

+ (UIView *)yrGeneralApis_FactoryGetKeyboardView{
    UIView *keyBoardView = nil;
    
    NSArray *windows = [[UIApplication sharedApplication] windows];
    
    for (UIWindow*window in [windows reverseObjectEnumerator])
    {
        keyBoardView = [[self alloc] getKeyBoardInView:window];
        if (keyBoardView)
        {
            return keyBoardView;
        }
    }
    return nil;
}

- (UIView *)getKeyBoardInView:(UIView *)view
{
    
    for(UIView *subView in [view subviews])
    {
        if (strstr(object_getClassName(subView), "UIKeyboard"))
        {
            return subView;
        }else{
            
            UIView *tempView = [self getKeyBoardInView:subView];
            
            if (tempView)
            {
                return tempView;
            }
        }
    }
    
    return nil;
    
}


+ (NSString *)yrGeneralApis_getDeviceId{
    return [[[UIDevice currentDevice] identifierForVendor] UUIDString];
}

+ (NSString *)yrGeneralApis_getAppVersionCode{
    return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"];
}

+ (NSString *)yrGeneralApis_getDeviceVersion{
    return [NSString stringWithFormat:@"iOS Version: %@%@",[UIDevice currentDevice].systemName,[UIDevice currentDevice].systemVersion];
}

+ (NSString *)yrGeneralApis_getDeviceModel{
    return [UIDevice currentDevice].model;
}

#pragma mark file

+ (NSString *)yrGeneralApis_getHomeDirectoryPath{
    return NSHomeDirectory();
}

+ (NSString *)yrGeneralApis_getTempDirectoryPath{
    return NSTemporaryDirectory();
}

+ (NSString *)yrGeneralApis_getDocumentDirecoryPath{
    return NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).lastObject;
}

+ (NSString *)yrGeneralApis_getPeferenceDirecoryPath{
    NSArray * paths4 = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    NSString * preferencePath = [[paths4 lastObject] stringByAppendingPathComponent:@"Preferences"];
    return preferencePath;
}

+ (NSString *)yrGeneralApis_getLibraryDirectoryPath{
    return NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES).lastObject;
}

+ (NSString *)yrGeneralApis_getCacheDirectoryPath{
    return NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).lastObject;
}

+ (NSArray *)YRGeneralApis_GetAllFilesNameIntoItem:(NSString *)path{
    return  [[NSFileManager defaultManager] contentsOfDirectoryAtPath:path error:nil];
}

+ (void)yrGeneralApisTranlateMovToMP4WithPath:(NSURL *)path savePath:(NSString *)savaPath hanlde:(YRGeneralApisMovieTypeTranslateHandle)handle{
    AVURLAsset *avAsset = [AVURLAsset URLAssetWithURL:path options:nil];
    NSArray *compatiblePresets = [AVAssetExportSession exportPresetsCompatibleWithAsset:avAsset];
    
    if ([compatiblePresets containsObject:AVAssetExportPresetMediumQuality])
        
    {
        
        AVAssetExportSession *exportSession = [[AVAssetExportSession alloc]initWithAsset:avAsset presetName:AVAssetExportPresetPassthrough];
        
        
        NSString *url = savaPath;
        
        NSString *exportPath = url;
        
        exportSession.outputURL = [NSURL fileURLWithPath:exportPath];
        NSLog(@"mov--->%@", exportPath);
        exportSession.outputFileType = AVFileTypeMPEG4;
        exportSession.shouldOptimizeForNetworkUse = YES;
        exportSession.videoComposition = [self getVideoComposition:avAsset];
        [exportSession exportAsynchronouslyWithCompletionHandler:^{
            switch ([exportSession status]) {
                case AVAssetExportSessionStatusFailed:
                {
                    NSLog(@"Export failed: %@", [[exportSession error] localizedDescription]);
                    handle(NO,savaPath,[[exportSession error] localizedDescription]);
                    break;
                }
                case AVAssetExportSessionStatusCancelled:
                {
                    NSLog(@"Export canceled");
                    handle(NO,savaPath,@"cancle");
                    break;
                }
                case AVAssetExportSessionStatusCompleted:
                {
                    NSLog(@"转换成功");
                    handle(YES,exportPath,@"cancle");
                    break;
                }
            }
        }];
    }
}

+ (AVMutableVideoComposition *)getVideoComposition:(AVAsset *)asset {
    
    NSArray *arr = [asset tracksWithMediaType:AVMediaTypeVideo];

    AVAssetTrack *videoTrack = nil;
    if(arr && [arr isKindOfClass:[NSArray class]]){
        if(arr.count > 0){
            videoTrack = [arr objectAtIndex:0];
        }
    }
    AVMutableComposition *composition = [AVMutableComposition composition];
    AVMutableVideoComposition *videoComposition = [AVMutableVideoComposition videoComposition];
    
    CGSize videoSize = videoTrack.naturalSize;
    
    NSArray *tracks = [asset tracksWithMediaType:AVMediaTypeVideo];
    
    CGAffineTransform transform ;
    
    
    if([tracks count] > 0) {
        AVAssetTrack *videoTrack = [tracks objectAtIndex:0];
        
        CGFloat renderW = MIN(videoSize.width, videoSize.height);
        
        CGFloat rate;
        
        rate = renderW / MIN(videoTrack.naturalSize.width, videoTrack.naturalSize.height);
        
        CGAffineTransform translateToCenter;
        CGAffineTransform mixedTransform;
        
        NSUInteger videoDegrees = [self degressFromVideoFileWithAsset:asset];
        
        if (videoDegrees == 0) {
            
            //                AVAssetExportSession *session = [[AVAssetExportSession alloc] initWithAsset:asset     presetName:AVAssetExportPresetMediumQuality];
            //                session.outputURL = outputURL;
            //                session.outputFileType = AVFileTypeQuickTimeMovie;
            
        }else{
            if(videoDegrees == 90){
                //顺时针旋转90°
                NSLog(@"视频旋转90度,home按键在左");
                
                translateToCenter = CGAffineTransformMakeTranslation(videoTrack.naturalSize.height, 0.0);
                //                    mixedTransform = CGAffineTransformRotate(translateToCenter,0);
                mixedTransform = CGAffineTransformRotate(translateToCenter,M_PI_2);
                //                    videoTrack.renderSize = CGSizeMake(assetTrack.naturalSize.height,assetTrack.naturalSize.width);
                transform = mixedTransform;
            }else if(videoDegrees == 180){
                //顺时针旋转180°
                NSLog(@"视频旋转180度，home按键在上");
                translateToCenter = CGAffineTransformMakeTranslation(videoTrack.naturalSize.width, videoTrack.naturalSize.height);
                mixedTransform = CGAffineTransformRotate(translateToCenter,M_PI);
                //                    waterMarkVideoComposition.renderSize = CGSizeMake(assetTrack.naturalSize.width,assetTrack.naturalSize.height);
            }else if(videoDegrees == 270){
                //顺时针旋转270°
                NSLog(@"视频旋转270度，home按键在右");
                translateToCenter = CGAffineTransformMakeTranslation(0.0, videoTrack.naturalSize.width);
                mixedTransform = CGAffineTransformRotate(translateToCenter,M_PI_2*3.0);
                //                    waterMarkVideoComposition.renderSize = CGSizeMake(assetTrack.naturalSize.height,assetTrack.naturalSize.width);
            }
        }
        
        CGAffineTransform preferredTransform = videoTrack.preferredTransform;
        
        CGAffineTransform trans = CGAffineTransformTranslate(preferredTransform, 0.0, -videoTrack.naturalSize.height);
        
        CGAffineTransform transNew = CGAffineTransformRotate(preferredTransform,M_PI_2*3);
        
        transNew = CGAffineTransformTranslate(transNew, 0, -(videoTrack.naturalSize.width - videoTrack.naturalSize.height) / 2.0);
        
        transNew = CGAffineTransformConcat(trans, transNew);
        
        transNew = CGAffineTransformScale(transNew, rate, rate);
        
        transform = transNew;
    }
    
    
    composition.naturalSize    = videoSize;
    videoComposition.renderSize = videoSize;
    videoComposition.frameDuration = CMTimeMakeWithSeconds( 1 / videoTrack.nominalFrameRate, 600);
    
    AVMutableCompositionTrack *compositionVideoTrack;
    compositionVideoTrack = [composition addMutableTrackWithMediaType:AVMediaTypeVideo preferredTrackID:kCMPersistentTrackID_Invalid];
    [compositionVideoTrack insertTimeRange:CMTimeRangeMake(kCMTimeZero, asset.duration) ofTrack:videoTrack atTime:kCMTimeZero error:nil];
    
    AVMutableVideoCompositionLayerInstruction *layerInst;
    layerInst = [AVMutableVideoCompositionLayerInstruction videoCompositionLayerInstructionWithAssetTrack:videoTrack];
    [layerInst setTransform:transform atTime:kCMTimeZero];
    AVMutableVideoCompositionInstruction *inst = [AVMutableVideoCompositionInstruction videoCompositionInstruction];
    inst.timeRange = CMTimeRangeMake(kCMTimeZero, asset.duration);
    inst.layerInstructions = [NSArray arrayWithObject:layerInst];
    videoComposition.instructions = [NSArray arrayWithObject:inst];
    return videoComposition;
}

+ (NSUInteger)degressFromVideoFileWithAsset:(AVAsset *)asset {
    NSUInteger degress = 0;
    
    NSArray *tracks = [asset tracksWithMediaType:AVMediaTypeVideo];
    if([tracks count] > 0) {
        AVAssetTrack *videoTrack = [tracks objectAtIndex:0];
        CGAffineTransform t = videoTrack.preferredTransform;
        
        if(t.a == 0 && t.b == 1.0 && t.c == -1.0 && t.d == 0){
            // Portrait
            degress = 90;
        }else if(t.a == 0 && t.b == -1.0 && t.c == 1.0 && t.d == 0){
            // PortraitUpsideDown
            degress = 270;
        }else if(t.a == 1.0 && t.b == 0 && t.c == 0 && t.d == 1.0){
            // LandscapeRight
            degress = 0;
        }else if(t.a == -1.0 && t.b == 0 && t.c == 0 && t.d == -1.0){
            // LandscapeLeft
            degress = 180;
        }
    }
    return degress;
}

/**
 *  创建文件夹
 */
+ (BOOL)yrGeneralApisCreateItemWithPath:(NSString *)path{
    NSFileManager *fileManager=[NSFileManager defaultManager];
    NSError *error=nil;
    if(![fileManager fileExistsAtPath:path])
    {
        BOOL listCreate= [fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
        if(listCreate){
            NSLog(@"文件夹创建成功");
            return YES;
        }
        else{
            NSLog(@"%@",error.description);
            return NO;
        }
    }
    NSLog(@"%@",path);
    return YES;
}


+ (void)yrGeneralApis_SaveImageToSystemAlbumWithImage:(UIImage *)img handle:(YRGeneralApisSaveImageToAlumnHandle)handle{
    __block ALAssetsLibrary *lib = [[ALAssetsLibrary alloc] init];
    
    [lib writeImageToSavedPhotosAlbum:img.CGImage metadata:nil completionBlock:^(NSURL *assetURL, NSError *error) {
        
        NSLog(@"assetURL = %@, error = %@", assetURL, error);
        lib = nil;
        if(!error && assetURL != nil){
            handle(assetURL.path,nil);
        }
        else{
            handle(nil,error.localizedDescription);
        }
    }];
}

+ (NSArray *)yrGeneralApis_DeleteRepeatDataInArray:(NSArray *)array{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    for (int i = 0; i<array.count; i++) {
        [dic setObject:array[i] forKey:array[i]];
    }
    
    return dic.allKeys;
}

+ (NSArray *)yrGeneralApis_DeleteRepeatDataInArray:(NSArray *)array keyString:(NSString *)keyString{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    for (int i = 0; i<array.count; i++) {
        [dic setObject:array[i] forKey:array[i][keyString]];
    }
    
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i<dic.allKeys.count; i++) {
        [arr addObject:dic[dic.allKeys[i]]];
    }
    
    return arr;
}




@end

