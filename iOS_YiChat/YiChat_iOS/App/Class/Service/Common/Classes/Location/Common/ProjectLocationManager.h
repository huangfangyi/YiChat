//
//  ProjectLocationManager.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AMapLocationKit/AMapLocationCommonObj.h>
#import <AMapLocationKit/AMapLocationRegionObj.h>
#import <AMapLocationKit/AMapLocationManager.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <MAMapKit/MAMapKit.h>
#import <AMapSearchKit/AMapSearchKit.h>
#import "ProjectLocationTranslateApis.h"
NS_ASSUME_NONNULL_BEGIN

typedef void (^ProjectPOISearchResonseInvocation)(NSArray<AMapPOI *>*POI,NSString *error);
@interface ProjectLocationManager : NSObject


+ (id)defualtLocationManager;

- (void)initialMapConfigure;

- (MAMapView *)getMap;

- (MAPointAnnotation *)getPointAnnotation;

- (void)projectLocationManagerSearchLocation:(CLLocationCoordinate2D)cordinate invocation:(ProjectPOISearchResonseInvocation)invocation;
/**
 *  定位用户当前地址
 */
- (void)projectlocationManaer_getCurrentLocationCompletionHandle:(AMapLocatingCompletionBlock)completionBlock;

@end

NS_ASSUME_NONNULL_END
