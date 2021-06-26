//
//  YRGeneralApiDef.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/3.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#ifndef YRGeneralApiDef_h
#define YRGeneralApiDef_h

typedef void(^YRGeneralApisMovieTypeTranslateHandle) (BOOL success,NSString *path,NSString *errorStr);

typedef  void (^YRGeneralApisSaveImageToAlumnHandle) (NSString *path ,NSString *errorStr);

typedef NS_ENUM(NSUInteger,YRCurrentDeviceInch) {
    YRCurrentDeviceInch35,YRCurrentDeviceInch40, YRCurrentDeviceInch47,YRCurrentDeviceInch55,YRCurrentDeviceInchError
};

typedef  BOOL(^YRGeneralDealJudge)(id judgeObject,id judgedObject);

typedef NS_ENUM(NSUInteger,YRPickerControllerType) {
    YRPickerControllerTypePhoto,
    YRPickerControllerTypeVideo,
    YRPickerControllerTypeCamera
};


#define KGENERAL_IPHONE4S_W 320.0f
#define KGENERAL_IPHONE4S_H 480.0f

#define KGENERAL_IPHONE5_W 320.0f
#define KGENERAL_IPHONE5_H 568.0f

#define KGENERAL_IPHONE6_W 375.0f
#define KGENERAL_IPHONE6_H 667.0f

#define KGENERAL_IPHONE6P_W 414.0f
#define KGENERAL_IPHONE6P_H 736.0f

#define kGeneral_Size_W    [UIScreen mainScreen].bounds.size.width
#define kGeneral_Size_H    [UIScreen mainScreen].bounds.size.height

#define kGeneral_Size_Design_W 375.0
#define kGeneral_Size_Design_H 667.0

#define kGeneral_Size_X 15.0f
#define kGeneral_Size_ArrowW 7.0f
#define kGeneral_Size_ArrowH 13.0f
/**
 * 圆角的弧度
 */
#define kGeneral_Size_CircleValue 5.0f

#define kCommonFontSize 15

#define kRGBColor(r,g,b,a)  [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:a]

#define kGeneral_Method_GetGlobalQueue dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)

#define kGeneral_Name_NavNum @"kGeneral_Name_NavNum"
#define kGeneral_Name_StatusNum @"kGeneral_Name_StatusNum"
#define kGeneral_Name_TabNum @"kGeneral_Name_TabNum"

#endif /* YRGeneralApiDef_h */
